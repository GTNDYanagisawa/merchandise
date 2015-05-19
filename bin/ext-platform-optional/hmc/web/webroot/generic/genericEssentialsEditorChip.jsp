<%@include file="../head.inc"%>
<%
	GenericEssentialsEditorChip theChip= (GenericEssentialsEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<table class="geec" cellspacing="0" cellpadding="0">
	<tr>
		<td>
<%
	for(final Iterator childs=theChip.getChildren().iterator(); childs.hasNext(); )
	{
		((Chip) childs.next()).render(pageContext);
	}
%>
		</td>
	</tr>
</table>
