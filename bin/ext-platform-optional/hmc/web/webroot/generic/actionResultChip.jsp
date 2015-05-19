<%@include file="../head.inc"%>

<%@page import="java.text.MessageFormat" %>
<%@page import="de.hybris.platform.hmc.util.action.ActionResult" %>

<%
	ActionResultChip theChip = (ActionResultChip)request.getAttribute( AbstractChip.CHIP_KEY );
	ActionResult actionResult = theChip.getActionResult();
%>

<table class="actionResultChip" cellspacing="5">
	<tr>
		<td class="arcMessage">
<%
	if( actionResult.getResult() == ActionResult.OK )
	{
		%><p class="titleOK"><%= theChip.getDisplayState().getLocalizedString("actionsuccess") %></p><%
	}
	else if( actionResult.getResult() == ActionResult.FAILED )
	{
		%><p class="titleERROR"><%= theChip.getDisplayState().getLocalizedString("actionfailed") %></p><%
	}
	else
	{
		%><p class="titleERROR"><%= theChip.getDisplayState().getLocalizedString("actioncancelled") %></p><%
	}
%>
		</td>
	</tr>
	<tr>
		<td class="arcTextArea">
			<textarea
				name="result_message"
				wrap="off"
				class="<%= actionResult.getResult() == ActionResult.OK ? "messageOK" : "messageERROR" %>"
				readonly="readonly"><%= HMCHelper.detab(DisplayState.encodeHTML(actionResult.getMessage()), 3) %></textarea>	
		</td>
	</tr>
	<tr>
		<td>
			<div class="xp-button chip-event">
				<a href="#" title="<%= localized("closewindow") %>" name="<%= theChip.getCommandID(ActionResultChip.CLOSE) %>" hidefocus="true">
					<span class="label">
						<%= localized("closewindow") %>
					</span>
				</a>
			</div>
		</td>
	</tr>
</table>
