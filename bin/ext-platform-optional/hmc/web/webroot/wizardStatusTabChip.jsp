<%@include file="head.inc"%>
<%
	WizardStatusTabChip theChip= (WizardStatusTabChip)request.getAttribute(AbstractChip.CHIP_KEY);
	String status = theChip.getStatus();
	if( status == null ) status = localized("wizard.status.not.available");
	String style = theChip.showError()? "style=\"color:#ff0000;\"" : "";
%>
<table class="wizardStatusTabChip">
	<tr>
		<!-- left space like attribute label -->
		<td class="leftSpace">&nbsp;</td>
 		<td class="wizLabel">
 			<span class="disabled">
 				<%= localized("wizard.status.label") + ":" %>
 			</span>
 		</td>
		<td class="spacer">&nbsp;</td>
		<td class="status">
			<div <%= style %>> <%= status %> </div>
		</td>
	</tr>
</table>
