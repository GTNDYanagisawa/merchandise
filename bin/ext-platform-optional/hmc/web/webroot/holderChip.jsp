<%@include file="head.inc"%>
<%
	HolderChip theChip = (HolderChip) request.getAttribute(AbstractChip.CHIP_KEY);
	if (theChip.getPlugin() != null)
	{
		theChip.getPlugin().render(pageContext);
	}
%>
