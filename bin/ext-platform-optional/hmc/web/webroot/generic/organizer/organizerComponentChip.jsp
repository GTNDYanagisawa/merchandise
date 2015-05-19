<%@include file="../../head.inc"%>
<%
	OrganizerComponentChip theChip = (OrganizerComponentChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final boolean isExpanded = theChip.isExpanded();
	final boolean isEnabledAndSet = theChip.isEnabledAndSet();
	final String localizedName = theChip.getLocalizedName();
	final String tooltip = theChip.getTooltip();	
%>
<table class="component">
	<tr>
		<!-- expand/collapse arrow icon -->
		<th class="arrowIcon">
<%
				if( isEnabledAndSet )
				{
%>
					<div class="arrow-button chip-event" title="<%= isExpanded ? localized("collapse") : localized("expand") %>">
						<a href="#" class="<%= isExpanded ? "collapse" : "expand" %>" hidefocus="true" id="<%= theChip.getUniqueName() %>_togglearrow" hidefocus="true"
							name="<%= theChip.getCommandID(isExpanded ? OrganizerComponentChip.COLLAPSE_ICO : OrganizerComponentChip.EXPAND_ICO) %>"></a>
					</div>
<%
				}
				else
				{
%>
					<div class="arrow-button">
						<img src="images/icons/arrow_closed.gif"/>
					</div>
<%
				}
%>
		</th>
		
		<!-- component name (e.g. 'Editor' or 'Search') -->
		<th<%= isEnabledAndSet ? "" : " class=\"disabled\"" %>>
<% 
			if( isEnabledAndSet )
			{
%>
				<div class="chip-event">
					<a href="#" hidefocus="true" name="<%= theChip.getCommandID(isExpanded ? OrganizerComponentChip.COLLAPSE_TXT : OrganizerComponentChip.EXPAND_TXT) %>" id="<%= theChip.getUniqueName() %>_togglelabel" title="<%= tooltip %>"><%= localizedName %></a>
				</div>
<%
			}
			else
			{
				out.println(localizedName);
			}
%>	
		</th>
		
		<!-- pin icon -->
<%
		if( theChip.showPin() )
		{
%>
			<th class="pinIcon">
			<div>
<% 
				if( isEnabledAndSet )
				{
%>
					<div class="pin-button chip-event" title="<%= theChip.getPinStatusText() %>">
						<a href="#" class="<%= theChip.isPinLocked() ? "locked" : "unlocked" %>" hidefocus="true" id="<%= theChip.getUniqueName() %>_pin" hidefocus="true"
							name="<%= theChip.getCommandID(OrganizerComponentChip.PIN_TOGGLE_EVENT) %>"></a>
					</div>
<%
//out.println(getSimpleImageLink(pinEvent, pinStatusText, pinIcon, pinHoverIcon));
				}
				else
				{
%>
					<img src="images/icons/pin_unlocked_inactive.gif"/>
<%
				}
%>	
			</div>
			</th>
<%
		}
%>
	</tr>
</table>
<%
	if( isEnabledAndSet && isExpanded )
	{
%>
	<div class="occEnabledAndSetExpanded">
		<% theChip.getPlugin().render(pageContext); %>
	</div>
	<div class="occSpacer">&nbsp;</div>
<%
	}
%>
