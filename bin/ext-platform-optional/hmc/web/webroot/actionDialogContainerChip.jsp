<%@include file="head.inc" %>
<%
	final ActionDialogContainerChip theChip = (ActionDialogContainerChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<div class="actionDialogContainerChip">
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td class="searchResultAction">
				<div>
					<%= localized("searchresultaction") %>: <%= localized(theChip.getActionChip().getName()) %>
				</div>
			</td>
		</tr>
		<tr>
			<td class="dialogChip">
<%
				theChip.getDialogChip().render(pageContext);
%>
			</td>
		</tr>
		<tr>
			<td class="buttons">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<div class="xp-button chip-event">
								<a href="#" style="width:120px" title="<%= localized("performaction") %>" name="<%= theChip.getCommandID(ActionDialogContainerChip.PERFORM) %>" hidefocus="true">
									<span class="label">
										<%= localized("performaction") %>
									</span>
								</a>
							</div>		
						</td>
						<td style="padding-left:5px;">
							<div class="xp-button chip-event">
								<a href="#" style="width:120px" title="<%= localized("cancel") %>" name="<%= theChip.getCommandID(ActionDialogContainerChip.CANCEL) %>" hidefocus="true">
									<span class="label">
										<%= localized("cancel") %>
									</span>
								</a>
							</div>
						</td>
					</tr>
				</table>
			</td>
		<tr>
			<td colspan="4" class="spacer">&nbsp;</td>
		</tr>
	</table>
</div>
