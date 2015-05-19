<%@include file="/head.inc"%>
<%
	DraggableReferenceListEditorChip theChip= (DraggableReferenceListEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<div id="<%= theChip.getID() %>">
<%
	theChip.getListChip().render(pageContext);
%>
</div>
