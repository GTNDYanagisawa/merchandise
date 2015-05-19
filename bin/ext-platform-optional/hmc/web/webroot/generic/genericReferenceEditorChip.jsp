<%@include file="../head.inc"%>
<%
	GenericReferenceEditorChip theChip = (GenericReferenceEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String contextMenu = theChip.hasVisibleContextMenuEntries()
								? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
								: "return false;";

	boolean itemIsAlive = theChip.hasItem() && ((Item) theChip.getValue()).isAlive();
	boolean isEditable = theChip.isEditable() && theChip.isAttributeTypeReadable();
%>
<div style="width:<%= theChip.getWidth() %>px;" oncontextmenu="<%= contextMenu %>">
	<table cellpadding="0" cellspacing="0" style="width:<%= theChip.getWidth() %>px;">
		<tr>
			<td>
<%
			if( theChip.hasItem() && !itemIsAlive )
			{
%>
				<%= localized("notdefined") %>
<%		
			}
			else if( !theChip.isElementReadable() )
			{
%>
					<%= localized("elementnotreadable") %>
<%
			}
			else if (theChip.hasItem())
			{
%>
				<div <%= isEditable ? "" : " class=\"disabled\"" %> id="<%= theChip.getUniqueName() %>_div">
					<%= theChip.getSelectedItemAsString() %>
				</div>
<%
			}
			else
			{
%>
					<%= theChip.getSelectedItemAsString() %>
<%
			}
%>
			</td>
<%
			if( isEditable && !theChip.isPartOf() && theChip.isAttributeTypeReadable() )
			{
%>
				<td class="grecSearchIcon">
					<div class="button-on-white chip-event">
						<a href="#" style="width:23px" title="<%= localized("clicktoopensearch") %>" name="<%= theChip.getEventID( ReferenceAttributeEditorChip.OPEN_SEARCH ) %>" hidefocus="true">
							<span>
								<img class="icon" src="images/icons/valueeditor_search.gif" id="<%= theChip.getUniqueName() %>_img"/>
							</span>
						</a>
					</div>
				</td>
<%
			}
%>
		</tr>
	</table>
</div>
