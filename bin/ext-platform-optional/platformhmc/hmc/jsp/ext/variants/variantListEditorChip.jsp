<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.variants.hmc.*" %>
<%
	VariantListEditorChip theChip= (VariantListEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	theChip.getListChip().render(pageContext);
%>
