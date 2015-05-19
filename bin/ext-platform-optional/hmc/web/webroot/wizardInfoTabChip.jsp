<%@include file="head.inc"%>
<%
	WizardInfoTabChip theChip= (WizardInfoTabChip) request.getAttribute(AbstractChip.CHIP_KEY);
	String message = theChip.getMessage();
%>
<table class="wizardInfoTabChip">
	<tr>
		<td class="spacer">&nbsp;</td>
 		<td class="message">
 			<div>
 				<%= message %>
 			</div>
 		</td>
	</tr>
</table>
