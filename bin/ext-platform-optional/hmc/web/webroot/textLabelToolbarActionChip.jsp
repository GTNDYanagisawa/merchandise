<%@include file="head.inc"%>
<%@page import="de.hybris.platform.hmc.TextLabelToolbarActionChip"%>
<%
	TextLabelToolbarActionChip theChip = (TextLabelToolbarActionChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	final String label = (theChip.getLabel() == null ? "" : theChip.getLabel()) ;
	final String color = theChip.isPartOf() ? "#ffffff" : "#333333";
%>
<td style="text-align:right; vertical-align:middle; padding-left:3px; padding-right:3px; color:<%= color %>;"><%= label %></td>
