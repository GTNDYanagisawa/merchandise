<%@page import="de.hybris.platform.hmc.PrintContainerChip"%>
<%@include file="head.inc"%>
<%
	PrintContainerChip theChip = (PrintContainerChip) request.getAttribute(AbstractChip.CHIP_KEY);
	theChip.getContentChip().render( pageContext ); 
%>
