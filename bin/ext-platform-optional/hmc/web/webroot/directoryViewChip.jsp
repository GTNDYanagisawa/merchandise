<%@include file="head.inc"%>
<%@page import="de.hybris.platform.hmc.DirectoryViewChip"%>
<%@page import="de.hybris.platform.hmc.IconViewChip"%>

<%
	final DirectoryViewChip theChip = (DirectoryViewChip)request.getAttribute(AbstractChip.CHIP_KEY);
	final IconViewChip view = theChip.getIconViewChip();
	
	theChip.getIconViewChip().render(pageContext);
%>
