<%@include file="../head.inc"%>
<%
	PartOfReferenceEditorChip theChip = (PartOfReferenceEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

	String onKeyPressString = "";
	if( theChip.getParent() instanceof FlexibleConditionChip )
	{
		onKeyPressString = "onkeypress=\"return checkForEnter(event);\"";
	}
	else
	{
		onKeyPressString = "onkeypress=\"return true;\"";
	}

	String style = "style=\"width:" + theChip.getWidth() + "px;\" ";
%>
<div class="partOfReferenceEditorChip">
<select
		class="<%= (theChip.isEditable() ? "enabled" : "disabled") %>"
	size="1"
	name="<%=theChip.getEventID(PartOfReferenceEditorChip.SELECT)%>"
	<%= style %>
	<%= onKeyPressString %>
	<%=(theChip.isEditable()?"":"disabled")%>>
	<%
		for(int i = 0; i<theChip.getEntryCount(); i++)
		{
			final String value = theChip.getItemAsString(i);
			%>
				<option value="<%=Integer.toString(i)%>" <%=(theChip.getValueAsString().equals(value)) ? "selected" : "" %>>
					<%=value%>
				</option>
			<%
		}
	%>
</select>
</div>
