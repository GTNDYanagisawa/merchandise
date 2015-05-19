<%@include file="../head.inc"%>
<%
	ItemDisplayChip theChip = (ItemDisplayChip) request.getAttribute(AbstractChip.CHIP_KEY);

	String displayValue = "";
	
	if( (displayValue = theChip.getDisplayValue()) == null || displayValue.equals("") )
	{
		displayValue = localized("notdefined");
	}
	else if( theChip.allowHTMLEscaping() )
	{
		displayValue = escapeHTML(displayValue);
	}
	
	if( theChip.useItemLink() )
	{
%>
		<input type="hidden" name="<%= theChip.getEventID(ItemDisplayChip.OPEN_ITEM) %>" value="<%= AbstractChip.FALSE %>" />
		<a href="#" class="normallink"
			onMouseover="window.status='<%= localized("open_editor") %>'; return true;"
			onMouseout="window.status='';return true;"
			title="<%= localized("open_editor") %>"
			hidefocus="true" style="white-space: nowrap;"
			id="<%= theChip.getUniqueName() %>_a"
			onclick="document.editorForm.elements['<%= theChip.getEventID(ItemDisplayChip.OPEN_ITEM) %>'].value='<%= AbstractChip.TRUE %>'; setScrollAndSubmit(); return false;">
		<%= displayValue %>
		</a>
<%
	}
	else
	{
%>
		<%= displayValue %>
<%
	}
%>
