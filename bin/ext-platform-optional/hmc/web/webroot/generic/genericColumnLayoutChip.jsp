<%@include file="../head.inc"%>
<%
	GenericColumnLayoutChip theChip= (GenericColumnLayoutChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<table cellpadding="0" cellspacing="0"
		 <%= (theChip.getHeight() != null ? "height=\"" + theChip.getHeight() + "\"" : "" ) %>
		 <%= (theChip.getWidth() != null ? "width=\"" + theChip.getWidth() + "\"" : "") %>>
<%
		int i = 0;
		for (final Iterator it = theChip.getChildren().iterator(); it.hasNext(); i++ )
		{
			final Chip chip = (Chip)it.next();
			chip.render(pageContext);
		}
%>
</table>
