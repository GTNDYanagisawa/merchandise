<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.ToggleTooblbarActionChip"%>
<%
	ToggleTooblbarActionChip theChip = (ToggleTooblbarActionChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	final boolean isExpanded = theChip.isExpanded();
%>
<td>
	<div class="arrow-button chip-event" title="<%= isExpanded ? localized("collapse") : localized("expand") %>">
		<a href="#" class="<%= isExpanded ? "collapse" : "expand" %>" hidefocus="true" id="<%= theChip.getUniqueName() %>_togglearrow"
			name="<%= theChip.getCommandID(theChip.getEvent()) %>" hidefocus="true"></a>
	</div>
</td>
