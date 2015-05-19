<%@include file="../../head.inc"%>
<%
	OrganizerListEntryChip theChip= (OrganizerListEntryChip) request.getAttribute(AbstractChip.CHIP_KEY);

	if( !theChip.isElementReadable() )
	{
%>
		<td class="<%=( theChip.isSelected() ? "selected " : "" )%>olectEntry" colspan="2"><%= localized("elementnotreadable") %></td>
<%
	}
	else
	{
%>
		<td class="<%=( theChip.isSelected() ? "selected " : "" )%>olectIsChangedSign">
			<div <%= theChip.isChanged() ? ("title=\"" + localized("item.ischanged.tooltip") + "\"") : "" %>>
				<%= theChip.isChanged() ? "*" : "&nbsp;" %>
				<input type="hidden" name="<%= theChip.getEventID(GenericItemListEntryChip.OPEN_EDITOR) %>" value="<%= AbstractChip.FALSE %>" />
			</div>
		</td>
		<td <%=( theChip.isSelected() ? " class=\"selected\"" : "" )%> colspan="<%= theChip.getAttributeCount() %>">
			<div class="olecEntry">
				<% theChip.getItemTreeViewChip().render( pageContext ); %>
			</div>
		</td>
<%
	}
%>
