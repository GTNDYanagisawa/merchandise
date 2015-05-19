<%@include file="../head.inc"%>
<%
	GenericListEditorChip theChip= (GenericListEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	theChip.getListChip().render(pageContext);
%>
