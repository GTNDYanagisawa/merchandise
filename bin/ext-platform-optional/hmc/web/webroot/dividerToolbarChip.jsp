<%@include file="head.inc"%>
<%
	DividerToolbarChip theChip = (DividerToolbarChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<td style="text-align:center; vertical-align:middle;<%= theChip.getWidth() != null ? " width:" + theChip.getWidth() + ";" : "" %>"><img src="<%=theChip.getImageURL()%>"/></td>
