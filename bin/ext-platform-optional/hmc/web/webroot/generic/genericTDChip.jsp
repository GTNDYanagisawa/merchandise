<%@include file="../head.inc"%>
<%
	GenericTDChip theChip= (GenericTDChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String widthStyle = theChip.getWidth() != null ? "width:" + theChip.getWidth() + ";" : "";
	final String heightStyle = theChip.getHeight() != null ? "height:" + theChip.getHeight() + ";" : "";

	String style = "style=\"" + widthStyle + " " + heightStyle + "\"";
	
%>
<td class="genericTDChip" <%= style %> colspan="<%=theChip.getColSpan()%>" rowspan="<%=theChip.getRowSpan()%>">
	<div class="gTDc">
<%
	final Iterator it = theChip.getChildren().iterator();	
	if( !it.hasNext() ) { %>&nbsp;<% }
	else {
		while ( it.hasNext() )
		{
			final Chip chip = (Chip)it.next();
			chip.render(pageContext);
		}
	}
%>
	</div>
</td>
