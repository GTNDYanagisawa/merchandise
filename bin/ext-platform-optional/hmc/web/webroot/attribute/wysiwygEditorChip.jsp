<%@include file="../head.inc"%>

<%
	WysiwygEditorChip theChip = (WysiwygEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	final String valueEventID = theChip.getEventID(StringLayoutChip.SET_VALUE);
	final String isChangedEventID = theChip.getEventID(WysiwygEditorChip.IS_CHANGED);
	final boolean hasClipboardItem = theChip.getClipboardItem() != null;
	final String itemURL = theChip.createLink();
	final boolean hasMediaLink = theChip.clipboardContainsMedia();
	final String mediaURL = theChip.getClipboardMediaUrl();
	final String styleSheet = theChip.getStyleSheet();
	String language = DisplayState.getCurrent().getLocale().getLanguage();
	final String conf = theChip.getConfig();
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
									cleanup : true,
									convert_fonts_to_spans : false,
									forced_root_block : false,
									fix_nesting : true,
									inline_styles : false,
									elements : "<%= valueEventID %>",
									content_css : "<%=styleSheet%>",									
									<%= conf %> 
									hybris_anyitemlink_enabled: <%= hasClipboardItem %>,
									hybris_anyitemlink: "<%= itemURL %>",
									hybris_medialink_enabled: <%= hasMediaLink %>,
									hybris_medialink: "<%= mediaURL %>",
									language : "<%= language %>",
									onchange_callback : "mce_myOnChangeHandler",
									execcommand_callback : "mce_myExecCommandHandler"
							});
		
				tinyMCE.addI18n({<%= language %>:{
					hybris:{
						anyitemlink_title : "<%= localized("anyitemlink.title") %>",
						medialink_title : "<%= localized("medialink.title") %>"
					}}});
											
				mce_addIsChangedElement('<%= valueEventID %>', $('<%= isChangedEventID %>_id'));
				
			</script>
<%
		}
		else
		{	 
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
