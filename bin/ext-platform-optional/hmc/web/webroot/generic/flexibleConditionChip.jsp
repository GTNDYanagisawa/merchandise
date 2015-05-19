<%@include file="../head.inc"%>
<%
	FlexibleConditionChip theChip = (FlexibleConditionChip) request.getAttribute(AbstractChip.CHIP_KEY);

	// surrounding css class is "abstractGenericConditionalSearchChip tr.row"
%>
<td class="attribute">
	<div class="attribute"><%=theChip.getName()%></div>
</td>
<td class="condition">
	<div class="condition"><% if (theChip.getValueEditor() != null) { theChip.getValueEditor().render(pageContext); }%></div>
</td>
