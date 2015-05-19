<%@include file="../head.inc"%>
<%
	GenericTabEditorChip theChip= (GenericTabEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<table width="100%" height="100%" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top">
			<%
				for (final Iterator it = theChip.getChildren().iterator(); it.hasNext(); )
				{
					GenericSectionChip chip = (GenericSectionChip)it.next();
					chip.render(pageContext);
				}
			%>
		</td>
	</tr>
</table>
