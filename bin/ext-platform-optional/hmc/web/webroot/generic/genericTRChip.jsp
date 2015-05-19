<%@include file="../head.inc"%>
<%
	GenericTRChip theChip= (GenericTRChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<tr <%=(theChip.getHeight()!=null?" height=\""+theChip.getHeight()+"\"":"")%><%=(theChip.getWidth()!=null?" width=\""+theChip.getWidth()+"\"":"")%>>
<%
		for (final Iterator it = theChip.getChildren().iterator(); it.hasNext(); )
		{
			final GenericTDChip chip = (GenericTDChip)it.next();
			chip.render(pageContext);
		}
%>
</tr>
