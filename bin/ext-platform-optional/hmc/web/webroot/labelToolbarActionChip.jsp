<%@include file="head.inc"%>
<%
	AbstractToolbarActionChip theChip = (AbstractToolbarActionChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String label = (theChip.getLabel() == null ? "" : theChip.getLabel());
	final String tooltip = (theChip.getTooltip() == null ? label : localized(theChip.getTooltip()));
	final String color = theChip.isPartOf() ? "#ffffff" : "#333333";
%>

<td class="labelToolbarActionChip" style="color:<%= color %>;<%= theChip.getWidth() != null ? " width:" + theChip.getWidth() + "px;" : "" %>"
	 title="<%= tooltip %>">
	 <%= label %>
</td>
