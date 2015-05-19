<%@include file="../head.inc"%>
<%
	GenericRowChip theChip= (GenericRowChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	Chip first = theChip.getFirstEntry();
	Chip second = theChip.getSecondEntry();
	
	String leftWidth = "";
	String rightWidth = "";

	if( theChip.getParent() instanceof GenericColumnLayoutChip )
	{
		GenericColumnLayoutChip columnLayoutChip = (GenericColumnLayoutChip) theChip.getParent();
		
		leftWidth = columnLayoutChip.getLeftWidth() != 0 ? " style=\"width:" + columnLayoutChip.getLeftWidth() + "px;\" " : "";
		rightWidth = columnLayoutChip.getRightWidth() != 0 ?  " style=\" width:" + columnLayoutChip.getRightWidth() + "px;\" " : "";
	}
%>
<tr class="genericRowChip"
	 <%= (theChip.getHeight() != null ? "height=\"" + theChip.getHeight() + "\"px" : "") %> >
<%
	// first entry
	if( first != null )
	{
%>
		<td <%= (second == null) ? " colspan=\"2\"" : leftWidth %>>
			<div class="grcDIV" <%= (second == null) ? "" : leftWidth %>>
<%
				first.render(pageContext);
%>
			</div>
		</td>
<%
		// second entry
		if( second != null )
		{
%>
		<td <%= rightWidth %>>
			<div class="grcDIV" <%= rightWidth %>>
<%
				second.render(pageContext);
%>
			</div>
		</td>
<%
		}
	}
	// fallback
	else {
%>
	<td colspan="2"><div class="grcDIV">&nbsp;</div></td>
<%
	}
%>
</tr>
