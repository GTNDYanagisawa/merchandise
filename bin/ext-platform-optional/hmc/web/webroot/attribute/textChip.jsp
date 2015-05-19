<%@include file="../head.inc"%>
<%
	TextChip theChip = (TextChip) request.getAttribute(AbstractChip.CHIP_KEY);
	int width = theChip.getWidth();
	
	String style = width != 0 ? "style=\"width:" + width + "px;\"" : "";
	
%>
<table class="textChip" cellspacing="0" cellpadding="0">
	<tr>
		<td class="arrowButton"> </td>
		<td class="text" <%= style %>><%= theChip.getText() %></td>
	</tr>
</table>
