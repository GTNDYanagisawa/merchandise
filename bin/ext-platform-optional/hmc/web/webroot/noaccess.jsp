<%@include file="head.inc"%>

<%
	NoAccessAttributeEditorChip theChip = (NoAccessAttributeEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);		

	if( theChip.getMessage() != null )
	{
%>
		<%= localized(theChip.getMessage()) %>
<%	
	}
	else
	{
%>
		<%= localized("attributenotreadable") %>
<%
	}
%>
