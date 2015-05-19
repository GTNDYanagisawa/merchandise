<%@include file="head.inc"%>
<%
	AbstractTreeNodeChip theChip = (AbstractTreeNodeChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>

<table class="explorer tree" cellspacing="0" cellpadding="0" border="0" style="height:16px; overflow:hidden;">
<%@include file="treeNodeBody.inc"%>
</table>
