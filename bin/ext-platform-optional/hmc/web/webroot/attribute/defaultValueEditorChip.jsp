<%@include file="../head.inc"%>
<%
	DefaultValueEditorChip theChip = (DefaultValueEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	AbstractAttributeEditorChip embeddedEditor = theChip.getEmbeddedEditor();
	if( embeddedEditor != null )
	{
		embeddedEditor.render( pageContext );
	}
	else
	{
%>
		<%= localized("notdefined") %>
<%
	}
%>
