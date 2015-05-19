<%@include file="../head.inc"%>
<%
	LongEditorChip theChip = (LongEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

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
<div class="longEditorChipDropDown">
	<select
		class="<%= (theChip.isEditable() ? "enabled" : "disabled") %>"
	name="<%=theChip.getEventID(StringLayoutChip.SET_VALUE)%>"
	<%= style %>
	<%= onKeyPressString %>
	<%=(theChip.isEditable()?"":"disabled")%>>
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
				<option value="<%= theChip.getDefaultValue() %>"<%= (theChip.getValue() != null && theChip.getDefaultValue() == theChip.getLongValue().longValue() ? "selected" : "") %>><%= theChip.getDefaultValue() %></option>
<%
		}
		if( theChip.getValue() != null && (theChip.getLongValue().longValue() != theChip.getDefaultValue()) 
			 && (theChip.getLongValue().longValue() < theChip.getMinValue() || theChip.getLongValue().longValue() > theChip.getMaxValue()) )
		{
			// explicitly include current value even if it is not within min/max boundaries
%>
				<option value="<%= theChip.getLongValue().longValue() %>" selected><%= theChip.getLongValue().longValue() %></option>
<%
		}
		for (long i=theChip.getMinValue(); i<theChip.getMaxValue()+1; i++)
		{
%>
				<option value="<%=i%>"<%=(theChip.getValue()!=null && i==((Long)theChip.getValue()).longValue()?"selected":"")%>><%=i%></option>
<%
		}
%>
</select>
</div>
