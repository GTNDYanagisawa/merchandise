<%@include file="head.inc" %>
<%
	final DialogContainerChip theChip = (DialogContainerChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<div class="dialogContainerChip">
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td class="dialogChip">
<%
				theChip.getDialogChip().render(pageContext);
%>
			</td>
		</tr>
		<tr>
			<td class="closeButton">
				<div class="xp-button chip-event">
					<a href="#" title="<%= localized(theChip.getCloseButtonName()) %>" name="<%= theChip.getCommandID(DialogContainerChip.CLOSE) %>" hidefocus="true">
						<span class="label">
							<%= localized(theChip.getCloseButtonName()) %>
						</span>
					</a>
				</div>
			</td>
		<tr>
			<td colspan="4" class="spacer"/>
		</tr>
	</table>
</div>
