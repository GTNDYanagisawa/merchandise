<%@include file="../head.inc"%>
<%
	NoAccessChip theChip = (NoAccessChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<h3><%=localized("norightstoviewobjects")%></h3>
<%=localized("type")%>:<%=theChip.getTypeCode()%>
