<%@include file="head.inc" %>

<%@page import="de.hybris.platform.hmc.ajax.*" %>

<%
	final IconViewChip theChip = (IconViewChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final IconViewModel model = theChip.getModel();
	final List path = theChip.getPath();
	
	// how many columns in which the icons will be displayed
	final int columns = model.getNumberOfColumns() > 0 ? model.getNumberOfColumns() : 3;	// defaults to 3 columns

	// the max. width of the organizer window is assumed to be 720; this is related to a total window size of 1024x768
	// this value also can be devided by 2, 3, 4, 5 and 6; so you can use it up to 6 columns
	final int boxWidth = 720 / columns;
%>
<table style="width:100%; height:100%;">
	<tr>
		<td>
			<table width="100%">
			<!--
				<tr>
					<td class="sectionheader" colspan="<%= columns %>"> 
						&nbsp;Home&nbsp;/

<%						for( int i = 0, s = path.size() ; i < s ; i++ )
						{ 
%>
							<%= model.getIconTitle(path.get(i)) %>
<%						} 
%>
			   	</td>
				</tr>
				-->
				<tr>
					<td class="sectionheader" colspan="<%= columns %>"> 
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
							<%= theChip.getAutoCompleter().render() %>
						</td>
						<td width="100%" style="vertical-align:middle">
							<div class="button-on-white chip-event">
								<a href="#"
									name="<%= theChip.getCommandID(IconViewChip.GOTO) %>" 
									title="<%= localized("open") %>"
									hidefocus="true"
								>
									<span>
										<img class="icon" src="images/icons/valueeditor_valueeditor.gif" id="<%= theChip.getUniqueName() %>_img"/>
									</span>
								</a>
							</div>

							<%--getSimpleImageLink(
										theChip.getCommandID( IconViewChip.GOTO ), 
										localized( "open" ),
										"images/icons/valueeditor_valueeditor.gif",
										"images/icons/valueeditor_valueeditor_hover.gif"
								)
							--%>
						</td></tr></table>
					</td>
				</tr>
				
<%				int i = 0;
				for( Iterator it = theChip.getElementChips().iterator(); it.hasNext() ; i++ )
				{
					if ( i % columns == 0 )  out.println("<tr>");
					((IconChip)it.next()).render(pageContext);
					// check, if the row is full. if so, start a new row
					if( i % columns == columns - 1 ) out.println("</tr>");
				}
%>
			</table>
		</td>
	</tr>
	<tr>
		<td style="height:100%; vertical-align:bottom;">&nbsp;</td>
	</tr>
</table>
