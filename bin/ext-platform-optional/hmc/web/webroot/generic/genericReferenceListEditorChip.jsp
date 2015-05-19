<%@include file="../head.inc"%>
<%
	GenericReferenceListEditorChip theChip= (GenericReferenceListEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	theChip.getListChip().render(pageContext);
%>
