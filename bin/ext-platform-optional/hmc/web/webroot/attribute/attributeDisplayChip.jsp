<%@include file="../head.inc"%>
<%
	final AbstractAttributeDisplayChip theChip = (AbstractAttributeDisplayChip) request.getAttribute( AbstractChip.CHIP_KEY );

	String displayValue = "";
	String style = "";
	
	if( (displayValue = theChip.getDisplayValue()) == null || displayValue.equals("") )
	{
		displayValue = localized("notdefined");
	}
	else if( theChip.allowHTMLEscaping() )
	{
		displayValue = escapeHTML(displayValue);
	}
	
	if( theChip.isAlignRight() )
	{
		style = "class=\"attributeDisplayChip\"";
	}
%>
<%= "" %><div <%= style %> id="<%= theChip.getUniqueName() %>_span"><%= displayValue %></div>
