<%@include file="../head.inc"%>
<%
	MapTypeEditorChip theChip = (MapTypeEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	theChip.getListChip().render( pageContext );
%>	
