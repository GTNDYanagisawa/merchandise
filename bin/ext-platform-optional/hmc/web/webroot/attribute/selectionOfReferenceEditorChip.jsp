<%@include file="../head.inc"%>
<%
	SelectionOfReferenceEditorChip theChip = (SelectionOfReferenceEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	theChip.resetEntries();

	String style = "style=\"width:" + theChip.getWidth() + "px;\" ";
%>
<span class="selectionOfReferenceEditorChip">
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
		>
<%
		if( !theChip.isHideNull() )
		{
%>	
			<option value="-1"><%=localized("noselection")%></option>
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
