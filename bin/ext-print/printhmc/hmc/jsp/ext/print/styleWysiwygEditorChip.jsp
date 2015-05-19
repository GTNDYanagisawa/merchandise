<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.jalo.Item"%>
<%@page import="de.hybris.platform.print.jalo.ContentBlock"%>
<%@page import="de.hybris.platform.print.hmc.attribute.StyleWysiwygEditorChip"%>

<%
	final StyleWysiwygEditorChip theChip = (StyleWysiwygEditorChip)request.getAttribute(AbstractChip.CHIP_KEY);
	
	final String valueEventID = theChip.getEventID(StringLayoutChip.SET_VALUE);
	final String isChangedEventID = theChip.getEventID(WysiwygEditorChip.IS_CHANGED);
	final boolean hasClipboardItem = theChip.getClipboardItem() != null;
	final String itemURL = theChip.createLink();
	final boolean hasMediaLink = theChip.clipboardContainsMedia();
	final String mediaURL = theChip.getClipboardMediaUrl();
	final String styleSheet = theChip.getStyleSheet();
	String language = DisplayState.getCurrent().getLocale().getLanguage();
	if( language == null || language.length() == 0 )
	{
		// fallback to english 
		language = "en";
	}
	final boolean isEditable = theChip.isEditable();
%>
	<div class="wysiwygEditorChip" style="border:1px solid #bbbbbb; margin-bottom: 10px;">
		<input name="<%= isChangedEventID %>" 
				id="<%= isChangedEventID %>_id"
				 value="false" 
<%
					if( !isEditable )
					{
						// focus trap
%>
					 	onfocus="this.blur();"
<%
					}
%>
		/>
		
<%
		if( isEditable )
		{
			// load style and block formats
			String cStyles = theChip.getCharacterStyles( ); 
			String pStyles = theChip.getParagraphStyles( );
			
			Item item = ((GenericItemChip)GenericHelper.getItemChip(theChip)).getItem();
			
%>
			<textarea id="<%= valueEventID %>" 
					name="<%= valueEventID %>" style="width:<%= theChip.getWidth() %>px; height:<%= theChip.getHeight() %>px">
							<%= theChip.getValue() == null ? "" : theChip.getValue() %></textarea>
			<script language="JavaScript1.2">
				if( !tinymceIsLoaded )
				{
					document.write('<sc'+'ript language="javascript" type="text/javascript" src="js/tinymce/jscripts/tiny_mce/tiny_mce.js"></scr' + 'ipt>');
					tinymceIsLoaded = true;
				}
			</script>
			<script language="javascript" type="text/javascript">
				tinyMCE.init({ 
					mode : "exact",
					apply_source_formatting : false,
					entity_encoding : "raw",
					forced_root_block : false,
					elements : "<%= valueEventID %>",
					content_css : "<%=styleSheet%>",
					theme : "advanced",
					convert_urls : false,
					plugins : "table,save,insertdatetime,searchreplace,print,contextmenu,safari,charcount,hybris,hybrisstyles",
					theme_advanced_buttons1 : "bold,italic,underline,strikethrough,sub,sup,separator,bullist,numlist,separator,charmap,insertdate,inserttime,separator,hybrisprintcharacterstyle,hybrisprintparagraphstyle,separator,separator,removeformat",
					theme_advanced_buttons2 : "cut,copy,paste,separator,search,replace,separator,undo,redo,separator,visualaid,charcount,code,separator,separator,tablecontrols,separator,separator,print",
					theme_advanced_buttons3 : "medialink,anyitemlink",
					theme_advanced_toolbar_location : "top",
					theme_advanced_toolbar_align : "left",
					plugin_insertdate_dateFormat : "%Y-%m-%d",
					plugin_insertdate_timeFormat : "%H:%M:%S",
					valid_elements: "+a[id|style|rel|rev|charset|hreflang|dir|lang|tabindex|accesskey|type|name|href|target|title|class|onfocus|onblur|onclick|ondblclick|onmousedown|onmouseup|onmouseover|onmousemove|onmouseout|onkeypress|onkeydown|onkeyup],-strong[class|style],-b[class|style],-em[class|style],-i[class|style],-strike[class|style],-u[class|style],#p[id|style|dir|class|align],-ol[class|style],-ul[class|style],-li[class|style],br,img[id|dir|lang|longdesc|usemap|style|class|src|onmouseover|onmouseout|border|alt=|title|hspace|vspace|width|height|align],-sub[style|class],-sup[style|class],-blockquote[dir|style],-table[border=0|cellspacing|cellpadding|width|height|class|align|summary|style|dir|id|lang|bgcolor|background|bordercolor],-tr[id|lang|dir|class|rowspan|width|height|align|valign|style|bgcolor|background|bordercolor],tbody[id|class],thead[id|class],tfoot[id|class],-td[id|lang|dir|class|colspan|rowspan|width|height|align|valign|style|bgcolor|background|bordercolor|scope],-th[id|lang|dir|class|colspan|rowspan|width|height|align|valign|style|scope],caption[id|lang|dir|class|style],-div[id|dir|class|align|style],-span[style|class|align],-pre[class|align|style],address[class|align|style],-h1[id|style|dir|class|align],-h2[id|style|dir|class|align],-h3[id|style|dir|class|align],-h4[id|style|dir|class|align],-h5[id|style|dir|class|align],-h6[id|style|dir|class|align],hr[class|style],-font[face|size|style|id|class|dir|color],dd[id|class|title|style|dir|lang],dl[id|class|title|style|dir|lang],dt[id|class|title|style|dir|lang]",
					extended_valid_elements : "a[name|href|target|title|onclick],img[class|src|border=0|alt|title|hspace|vspace|width|height|align|onmouseover|onmouseout|name],hr[class|width|size|noshade],font[face|size|color|style],span[class|align|style],style[*]",
					hybris_anyitemlink_enabled: <%= hasClipboardItem %>,
					hybris_anyitemlink: "<%= itemURL %>",
					hybris_medialink_enabled: <%= hasMediaLink %>,
					hybris_medialink: "<%= mediaURL %>",
					hybris_print_characterstyles : "<%= cStyles %>", 
					hybris_print_paragraphstyles : "<%= pStyles %>",
					language : "<%= language %>",
					onchange_callback : "mce_myOnChangeHandler",
					execcommand_callback : "mce_myExecCommandHandler",
			        table_styles : "<%=theChip.getTableStyles() %>",
			    	table_cell_styles : "<%=theChip.getTableCellStyles() %>"
					
				});

				tinyMCE.addI18n({<%= language %>:{
					hybris:{
						anyitemlink_title : '<%= localized("anyitemlink.title") %>',
						medialink_title : '<%= localized("medialink.title") %>'
				}}});
				tinyMCE.addI18n({<%= language %>:{
					hybrisstyles:{
						emptycharacterstyle : '<%= localized("text.wysiwygeditor.emptycharacterstyle") %>',
						emptyparagraphstyle : '<%= localized("text.wysiwygeditor.emptyparagraphstyle") %>'
				}}});

				mce_addIsChangedElement('<%= valueEventID %>', $('<%= isChangedEventID %>_id'));
				
			</script>
<%
		}
		else
		{
			// show plain html (which is not editable)	
%>
			<textarea id="<%= theChip.getID() %>_textarea"><%= theChip.getDisplayContent() %></textarea>
			<iframe style="width:<%= theChip.getWidth() %>px; height:<%= theChip.getHeight() %>px;" class="disablediframe" id="<%= theChip.getID() %>_iframe" src="js/empty_iframe.html"></iframe>
			<script type="text/javascript">
				y_initIFrame($('<%= theChip.getID() %>_textarea'), $('<%= theChip.getID() %>_iframe'));
			</script>
<%
		}
%>
	</div>
