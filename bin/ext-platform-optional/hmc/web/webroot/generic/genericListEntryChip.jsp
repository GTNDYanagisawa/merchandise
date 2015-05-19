<%@include file="../head.inc"%>
<%
	GenericListEntryChip theChip= (GenericListEntryChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<%=theChip.getValueAsString()%>
