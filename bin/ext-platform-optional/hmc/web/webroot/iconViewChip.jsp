<%@include file="head.inc" %>

<%@page import="de.hybris.platform.hmc.ajax.*" %>

<%
	final IconViewChip theChip = (IconViewChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final DisplayState ds = theChip.getDisplayState();

	final IconViewModel model = theChip.getModel();
	
	final int[] box = model.getBoxSize();
	final int boxWidth = box != null && box[0] > 0 ? box[0] : -1;
	final boolean useWidth = boxWidth > 0;
	final int boxHeight = box != null && box[1] > 0 ? box[1] : -1;
	final boolean useHeight = boxHeight > 0;
	final String border = ""; //"border-style:dotted; border-width:1px; border-color:#ff0000; ";

	final List menuEntries = theChip.getMenuEntries();
	final String contextMenu = theChip.hasVisibleContextMenuEntries()
				? "(new Menu(" + theChip.createMenuEntriesForJS(menuEntries) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
				: "return false;";
%>

<table width="100%" style="height:100%;">
	<tr>
		<td>
			<table width="100%">
				<tr>
					<td>
						<table><tr><td style="vertical-align:middle">
<%
						if( !theChip.getPath().isEmpty() )
						{
							out.println( 
								getSimpleImageLink(
									theChip.getCommandID( IconViewChip.UP ), 
									localized( "up" ),
									"images/icons/folder_up.gif",
									"images/icons/folder_up_hover.gif"
								) 
							);
						}
						else
						{
%>
						<img src="images/icons/folder_up_disabled.gif"/>
<%							
						}
%>					
						</td>
						<td style="vertical-align:middle">
							<input id="<%=IconViewChip.AJAX_INPUT%>"
									 name="<%=theChip.getCommandID( IconViewChip.PATH )%>"
									 class="editorform"
									 autocomplete="off"
									 type="text"
									 value="<%=theChip.getPathString()%>"
									 size="130"
									 maxlenth="1000" />
							<div id="<%=IconViewChip.AJAX_RESULT%>" class="autocomplete"/>
							<script language='JavaScript1.2'>
								document.getElementById('<%=IconViewChip.AJAX_INPUT%>').focus();
							</script>
							<%= theChip.getAutoCompleter().render() %>
						</td>
						<td width="100%" style="vertical-align:middle">
							<%= getSimpleImageLink(
										theChip.getCommandID( IconViewChip.GOTO ), 
										localized( "open" ),
										"images/icons/valueeditor_valueeditor.gif",
										"images/icons/valueeditor_valueeditor_hover.gif"
								)
							%>
						</td></tr></table>
			   	</td>
				</tr>
				<tr oncontextmenu="<%= contextMenu %>" >
					<td style="float:left">
<%
						if( !theChip.getPath().isEmpty() )
						{
%>						
							<div style="margin-top:5px; <%= border %> <%= (useHeight?"height:"+boxHeight+"px;":"") %> <%= (useWidth?"width:"+boxWidth+"px;":"") %> overflow:hidden; <%= border %> float:left;">
								<% theChip.getUpFolderIconChip().render(pageContext); %>
							</div>
<%
						}
						for( Iterator it = theChip.getElementChips().iterator(); it.hasNext(); )
						{
							final IconChip ic = (IconChip)it.next();
%>
							<div style="margin-top:5px; <%= border %> <%= (useHeight?"height:"+boxHeight+"px;":"") %> <%= (useWidth?"width:"+boxWidth+"px;":"") %> overflow:hidden; <%= border %> float:left;">
								<% ic.render(pageContext); %>
							</div>
<%
						}
%>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr><td style="height:100%; vertical-align:bottom;">&nbsp;</td></tr>
</table>
