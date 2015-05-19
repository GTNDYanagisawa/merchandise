<%@include file="../../head.inc"%>

<%@page import="de.hybris.platform.catalog.hmc.NoAccessClassificationAttributeEditorChip" %>

<%
	final NoAccessClassificationAttributeEditorChip theChip = (NoAccessClassificationAttributeEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<%= localized(theChip.getMessage()) %>
