<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.sap.core.configuration.hmc.chip.MyChip"%>
<%@page import="de.hybris.platform.hmc.webchips.AbstractChip"%>
 
<%
   MyChip theChip = (MyChip) request.getAttribute(AbstractChip.CHIP_KEY);
   out.println( theChip.getModuleIds() );
%>