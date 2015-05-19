<%@include file="../head.inc"%>
<%
	GenericConditionChip theChip= (GenericConditionChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<td class="attribute">
	<div class="attribute"><%=theChip.getName()%></div>
</td>
<td class="locale">
	<div class="locale">&nbsp;</div>
</td>
<td class="comparator">
	<div class="comparator"><%theChip.getOperatorEditor().render(pageContext);%></div>
</td>
<td class="condition">
	<div class="condition">
<% 
		if( theChip.getValueEditor() != null )
		{
			theChip.getValueEditor().render(pageContext);
		}
		else
		{
%>
			&nbsp;
<%
		}
%>
	</div>
</td>
