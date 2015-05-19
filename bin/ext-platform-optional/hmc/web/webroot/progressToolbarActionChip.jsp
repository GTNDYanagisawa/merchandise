<%@include file="head.inc"%>
<%
	ProgressToolbarActionChip theChip = (ProgressToolbarActionChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final String tooltip = theChip.getTooltip() != null ? " title=\"" + localized(theChip.getTooltip()) + "\"" : "";
%>
<td class="ptacFirst"<%= tooltip %>">
	<div class="outerDiv">
		<div style="width:<%= 2 * theChip.getPercentage() %>px;">&nbsp;</div>
	</div>
</td>
<td class="ptacSecond"<%= tooltip %>>
	<%= theChip.getPercentage() %>%&nbsp;
</td>
<td class="ptacThirs" id="refreshtimestatus"<%= tooltip %>>&nbsp;</td>
