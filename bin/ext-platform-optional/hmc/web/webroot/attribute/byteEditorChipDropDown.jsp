<%@include file="../head.inc"%>
<%
	ByteEditorChip theChip = (ByteEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
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
<div class="byteEditorChipDropDown">
<select
	class="<%=(theChip.isEditable() ? "enabled" : "disabled") %>"
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
				<option value="<%= theChip.getDefaultValue() %>"<%= (theChip.getValue() != null && theChip.getDefaultValue() == ((Byte) theChip.getValue()).byteValue() ? "selected" : "") %>><%= theChip.getDefaultValue() %></option>
<%
		}
		if( theChip.getValue() != null && (theChip.getByteValue().byteValue() != theChip.getDefaultValue()) 
			 && (theChip.getByteValue().byteValue() < theChip.getMinValue() || theChip.getByteValue().byteValue() > theChip.getMaxValue()) )
		{
			// explicitly include current value even if it is not within min/max boundaries
%>
				<option value="<%= theChip.getByteValue().byteValue() %>" selected><%= theChip.getByteValue().byteValue() %></option>
<%
		}
		for( byte i = theChip.getMinValue(); i < theChip.getMaxValue() + 1; i++)
		{
%>
			<option value="<%= i %>" <%= (theChip.getValue() != null && i == ((Byte) theChip.getValue()).byteValue() ? "selected" : "") %>><%= i %></option>
<%
		}
%>
</select>
</div>
