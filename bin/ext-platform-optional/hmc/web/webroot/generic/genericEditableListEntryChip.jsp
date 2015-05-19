<%@include file="../head.inc"%>
<%
	GenericEditableListEntryChip theChip= (GenericEditableListEntryChip) request.getAttribute(AbstractChip.CHIP_KEY);

	theChip.getEditorChip().render(pageContext);
%>
