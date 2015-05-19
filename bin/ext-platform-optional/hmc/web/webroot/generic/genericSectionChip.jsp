<%@include file="../head.inc"%>
<%
	GenericSectionChip theChip= (GenericSectionChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<%
	if( !theChip.containsNoVisibleChips() )
	{
%>
		<table cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td class="sectionheader"><div class="sh"><%=localized(theChip.getName())%></div></td>
			</tr>
<%
		for( final Iterator it=theChip.getChildren().iterator(); it.hasNext(); )
		{
						%>
			<tr>
				<td width="100%" style="padding:2px 0px;">
<%
			((Chip)it.next()).render( pageContext );
%>
				</td>
			</tr>
<%
		}
%>
		</table>
		<br>
<%
	}
%>
