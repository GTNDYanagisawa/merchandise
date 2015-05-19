<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.catalog.hmc.CatalogContentChip"%>
<%@page import="de.hybris.platform.hmc.IconViewChip"%>

<%
	final CatalogContentChip theChip = (CatalogContentChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final IconViewChip view = theChip.getIconViewChip();
	
	theChip.getIconViewChip().render(pageContext);
%>

