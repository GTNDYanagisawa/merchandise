<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.variants.hmc.*" %>
<%
	VariantAttributesEditorChip theChip= (VariantAttributesEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

	for( final Iterator iter = theChip.getChildren().iterator(); iter.hasNext(); )
	{
%>
		<div>
<%
			((AttributeChip) iter.next()).render(pageContext);
%>
		</div>
<%
		
	}
%>
