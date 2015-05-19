<%@include file="head.inc" %>
<%
	final SimpleMessageDialogChip theChip = (SimpleMessageDialogChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<div class="simpleMessageDialogChip">
	<%= theChip.getMessage() %>
</div>
