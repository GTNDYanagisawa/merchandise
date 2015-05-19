<%@include file="../head.inc"%>
<%
	GenericLocalizableConditionChip theChip= (GenericLocalizableConditionChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<td class="attribute">
	<div class="attribute"><%=theChip.getName()%></div>
</td>
<td class="locale">
	<div class="locale"><%theChip.getLanguageEditor().render(pageContext);%></div>
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
