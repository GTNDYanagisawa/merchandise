<%@include file="../head.inc"%>
<%
	final SelectionOfCollectionReferenceEditorChip theChip = (SelectionOfCollectionReferenceEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	theChip.getListEditor().render(pageContext);
%>
