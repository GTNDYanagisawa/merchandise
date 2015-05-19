<%@include file="../head.inc"%>
<%
	GenericTableChip theChip= (GenericTableChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<table cellspacing="0" cellpadding="0"<%=(theChip.getHeight()!=null?" height=\""+theChip.getHeight()+"\"":"")%><%=(theChip.getWidth()!=null?" width=\""+theChip.getWidth()+"\"":"")%>>
	<%
		for (final Iterator it = theChip.getChildren().iterator(); it.hasNext(); )
		{
			final GenericTRChip chip = (GenericTRChip)it.next();
			chip.render(pageContext);
		}
	%>
</table>
