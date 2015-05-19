<%@include file="../head.inc"%>
<%
	final ItemActionChip theChip = (ItemActionChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	final boolean isActive = theChip.isActive();
	final String tooltip = isActive ? (theChip.getTooltip() != null ? localized(theChip.getTooltip()) : localized(theChip.getName())) 
															: localized("itemactionsnotactive");
	final String icon = isActive ? theChip.getIcon() : theChip.getDisabledIcon();
	
	
	if( !theChip.isHideButton() )
	{
%>
		<div class="xp-button<%= isActive ? " chip-event" : "-disabled" %>">
			<a href="#" title="<%= tooltip %>" name="<%= theChip.getPerformCommandID() %>" style="width:100%;" hidefocus="true">
<%
				if( theChip.needsConfirmation() )
				{
%>
					<span class="confirm-message"><%= theChip.getConfirmationMessage() %></span>
<%
				}
%>
				<span>
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<img class="icon" src="<%= icon %>" id="<%= theChip.getUniqueName() %>_img"/>
							</td>
							<td>
								<div class="label" id="<%= theChip.getUniqueName() %>_div" ><%= localized(theChip.getName()) %></div>
							</td>
						</tr>
					</table>
				</span>
			</a>
		</div>
<%
	}
%>
