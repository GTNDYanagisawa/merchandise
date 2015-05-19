<%@include file="../head.inc"%>
<%
	TaxValueEditorChip theChip = (TaxValueEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
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
	<tr><td><%= localized("pricevalue.code") %>:</td><td><% theChip.getCodeEditor().render( pageContext );%></td></tr>
	<tr><td><%= localized("pricevalue.value") %>:</td><td><% theChip.getValueEditor().render( pageContext );%></td></tr>
	<tr><td><%= localized("currency") %>:</td><td><% theChip.getCurrencyEditor().render(pageContext);%></td></tr>
</table>

