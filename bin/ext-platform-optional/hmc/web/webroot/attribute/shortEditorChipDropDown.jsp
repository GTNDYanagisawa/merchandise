<%@include file="../head.inc"%>
<%
	ShortEditorChip theChip = (ShortEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

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
<div class="shortEditorChipDropDown">
	<select
		class="<%= (theChip.isEditable() ? "enabled" : "disabled") %>"
	name="<%= theChip.getEventID(StringLayoutChip.SET_VALUE) %>"
	<%= style %>
	<%= onKeyPressString %>
	<%= (theChip.isEditable() ? "" : "disabled") %>>
<%
		if( theChip.isOptional() )
		{
%>
			<option value="" <%= (theChip.getValue() == null ? "selected" : "") %>><%= localized("noselection") %></option>
<%
		}
		if( theChip.getDefaultValue() < theChip.getMinValue() || theChip.getDefaultValue() > theChip.getMaxValue() )
		{
			// explicitly include default value even if it is not within min/max boundaries
%>
				<option value="<%= theChip.getDefaultValue() %>"<%= (theChip.getValue() != null && theChip.getDefaultValue() == theChip.getShortValue().shortValue() ? "selected" : "") %>><%= theChip.getDefaultValue() %></option>
<%
		}
		if( theChip.getValue() != null && (theChip.getShortValue().shortValue() != theChip.getDefaultValue()) 
			 && (theChip.getShortValue().shortValue() < theChip.getMinValue() || theChip.getShortValue().shortValue() > theChip.getMaxValue()) )
		{
			// explicitly include current value even if it is not within min/max boundaries
%>
				<option value="<%= theChip.getShortValue().shortValue() %>" selected><%= theChip.getShortValue().shortValue() %></option>
<%
		}
		for( short i = theChip.getMinValue(); i < theChip.getMaxValue() + 1; i++)
		{
%>
			<option value="<%= i %>" <%= (theChip.getValue() != null && i == ((Short) theChip.getValue()).shortValue() ? "selected" : "") %>><%= i %></option>
<%
		}
%>
</select>
</div>
