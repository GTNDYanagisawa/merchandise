<%@include file="../../head.inc"%>

<%@page import="de.hybris.platform.catalog.hmc.AttributeDescriptor2BooleanMapEditorChip" %>
<%
	final AttributeDescriptor2BooleanMapEditorChip theChip = (AttributeDescriptor2BooleanMapEditorChip)request.getAttribute(AbstractChip.CHIP_KEY);
%>
<%
	theChip.getRootTreeNodeChip().render( pageContext );
%>
