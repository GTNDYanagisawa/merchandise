<%@include file="../head.inc"%>
<%
	final SelectReferenceEditorChip theChip = (SelectReferenceEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

	theChip.resetEntries();

	final String contextMenu = theChip.hasVisibleContextMenuEntries()
			? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
			: "return false;";

	String style = "style=\"width:" + theChip.getWidth() + "px;\" ";
%>
<span class="selectReferenceEditorChip">
<%
	if( !theChip.isElementReadable() )
	{
%>
		<%=localized("elementnotreadable")%>
<%
	}
	else
	{
%>
		<select
			class="<%=(theChip.isEditable()?"enabled":"disabled")%>"
			name="<%= theChip.getEventID(SelectionOfReferenceEditorChip.SELECT) %>"
			<%= style %>
			<%=(theChip.isEditable()?"":"disabled=\"disabled\"")%>
			oncontextmenu="<%= contextMenu %>"
			id="<%= theChip.getUniqueName() %>_select"
			onchange=<%=theChip.isRefreshAfterSelect()?"setScrollAndSubmit();":"" %>
		>
<%
		if( !theChip.isHideNull() )
		{
%>	
			<option value="-1"><%=localized(theChip.getNoSelectionKey())%></option>
<%
		}
				int size = theChip.getPossibleValues().size();
				for( int i = 0; i < size; i++ )
				{
					boolean selected = theChip.isSelected(i);
%>
					<option value="<%= i %>"<%= (selected ? " selected=\"selected\"" : "") %>><%= Utilities.escapeHTML(theChip.getChoiceRepresentation(i)) %></option>
<%
				}
%>
		</select>
<%
	}
%>
</span>
