
<%@include file="../head.inc"%>
<%
	ObjectEditorChip theChip = (ObjectEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);		
	if( theChip.getValue() != null)
	{
%>
	<%= theChip.getValue().toString() %>
<%
	}
	else if( theChip.getMessage() != null )
	{
%>
		<%= localized(theChip.getMessage()) %>
<%	
	}
	else
	{
%>
	<%= localized("notdefined") %>
<%	
	}
%>
