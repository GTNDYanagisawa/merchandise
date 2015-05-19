<%@include file="head.inc"%>
<%
	TextboxToolbarActionChip theChip = (TextboxToolbarActionChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final String tooltip = theChip.getTooltip() != null ? localized(theChip.getTooltip()) : "";
%>
<td class="textboxToolbarActionChip" title="<%= tooltip %>">
	<input type="text" class="editorform" size="<%=theChip.getSize()%>" value="<%=theChip.getValue()%>"/>
</td>
