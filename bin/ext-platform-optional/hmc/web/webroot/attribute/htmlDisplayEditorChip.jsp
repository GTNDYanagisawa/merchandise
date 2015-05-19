<%@include file="../head.inc"%>
<%@page import="de.hybris.platform.hmc.attribute.HTMLDisplayEditorChip"%>
<%
	HTMLDisplayEditorChip theChip = (HTMLDisplayEditorChip)request.getAttribute(AbstractChip.CHIP_KEY);
%>
	<table style="<%= theChip.getCssStyle() %>">
		<tr>
			<td><%= theChip.getStringValue() %></td>
		</tr>
	</table>
