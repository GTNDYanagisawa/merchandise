<%@include file="../head.inc"%>
<%
	ToolbarActionChip theChip = (ToolbarActionChip)request.getAttribute( AbstractChip.CHIP_KEY );
%>
<%= theChip.getName() %>
