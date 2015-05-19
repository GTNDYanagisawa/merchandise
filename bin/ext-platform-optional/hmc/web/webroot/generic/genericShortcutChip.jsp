<%@include file="../head.inc"%>
<%
	final GenericShortcutChip theChip= (GenericShortcutChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
	<div class="xp-button<%= theChip.isActive() ? " chip-event" : "-disabled" %>">
		<a href="#" name="<%= theChip.getCommandID(GenericShortcutChip.OPEN) %>" style="width:100%;" hidefocus="true">
			<span>
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>
							<img class="icon" src="images/icons/e_shortcut_<%= theChip.getUsage().equals("creator") ? "create" : "search" %>.gif" id="<%= theChip.getUniqueName() %>_img"/>
						</td>
						<td>
							<div class="label" id="<%= theChip.getUniqueName() %>_div" ><%= localized(theChip.getName()) %></div>
						</td>
					</tr>
				</table>
			</span>
		</a>
	</div>
