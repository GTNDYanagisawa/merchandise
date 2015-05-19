<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.SearchGroupChip"%>

<%
	SearchGroupChip theChip = (SearchGroupChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>

<input type="checkbox" name="<%=theChip.getEventID(theChip.SELECTED)%>" value="v"<%=theChip.isStillAvailable()?"":" disabled"%><%=theChip.isSelected()?" checked":""%>><%=theChip.getGroupName()%><br>
<input type="hidden" name="<%=theChip.getEventID(theChip.HIDDEN)%>" value="v">
