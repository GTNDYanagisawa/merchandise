<%@include file="../head.inc"%>
<%
	DiscountValueEditorChip theChip = (DiscountValueEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	String onKeyPressString = "";
	if( theChip.getParent() instanceof FlexibleConditionChip )
	{
		onKeyPressString = "onkeypress=\"return checkForEnter(event);\"";
	}
	else
	{
		onKeyPressString = "onkeypress=\"return true;\"";
	}
%>
<table cellspacing="0" cellpadding="0">
	<tr><td><%= localized("discountvalue.code") %>:</td><td><% theChip.getCodeEditor().render(pageContext);%></td></tr>
	<tr><td><%= localized("discountvalue.value") %>:</td><td><% theChip.getValueEditor().render(pageContext);%></td></tr>
	<tr><td><%= localized("currency") %>:</td><td><% theChip.getCurrencyEditor().render(pageContext);%></td></tr>
</table>

