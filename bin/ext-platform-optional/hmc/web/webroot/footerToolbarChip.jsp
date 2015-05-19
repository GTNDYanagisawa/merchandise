<%@include file="head.inc"%>
<%
	AbstractToolbarChip theChip = (AbstractToolbarChip) request.getAttribute(AbstractChip.CHIP_KEY);

	List actionChips = theChip.getToolbarActionChips();
	List leftActionChips = AbstractToolbarChip.filterToolbarActionChips(actionChips, AbstractToolbarActionChip.ALIGN_LEFT);
	List rightActionChips = AbstractToolbarChip.filterToolbarActionChips(actionChips, AbstractToolbarActionChip.ALIGN_RIGHT);
%>
<table class="footer" width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td align="left" valign="bottom" width="7px"><img src="images/editortab_corner_bl.gif"/></td>
<%
			if( actionChips.isEmpty() )
			{
%>
				<td width=\"100%\">&nbsp;</td>
<%
			}
			else
			{
%>
				<td style="text-align:left; padding-left:3px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr height="23px">
<%
							for( Iterator it = leftActionChips.iterator(); it.hasNext(); )
							{
								((AbstractToolbarActionChip) it.next()).render( pageContext );
							}
%>
						</tr>
					</table>
				</td>
				<td width=\"100%\">&nbsp;</td>
				<td style="text-align:right; padding-right:3px;">
					<table cellspacing="0" cellpadding="0" border="0">
						<tr height="23px">
<%
							for( Iterator it = rightActionChips.iterator(); it.hasNext(); )
							{
								((AbstractToolbarActionChip) it.next()).render( pageContext );
							}
%>
						</tr>
					</table>
				</td>
<%
			}
%>
		<td align="right" valign="bottom" width="7px"><img src="images/editortab_corner_br.gif"/></td>
	</tr>
</table>
