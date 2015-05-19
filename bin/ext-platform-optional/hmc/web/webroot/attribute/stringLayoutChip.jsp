<%@include file="../head.inc"%>
<%
	StringLayoutChip theChip = (StringLayoutChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	String onKeyPressString = "";
	if( theChip.getParent() instanceof FlexibleConditionChip )
	{
		onKeyPressString = "onkeypress=\"return checkForEnter(event);\"";
	}
	else
	{
		onKeyPressString = "onkeypress=\"return true;\"";
	}
		
	String style = "style=\"width:" + theChip.getWidth() + "px; text-align:" + (theChip.isAlignRight() ? "right" : "left" ) + ";\" ";
%>
<div <%= style %>>
<table class="stringLayoutChip" cellspacing="0" cellpadding="0">
<tr>
<td>
<input class="<%=(theChip.isEditable()?"enabled":"disabled")%>"
		 type="<%=(theChip.isEncoded()?"password":"text")%>" 
		 name="<%=theChip.getEventID(StringLayoutChip.SET_VALUE)%>" 
		 id="<%= theChip.getUniqueName() %>_input"
		 value="<%=theChip.getQuotedStringValue()%>" 
		 style="width:<%= theChip.getWidth() %>px;<%= (theChip.isDBEncrypted() ? "color:red;" : "" ) %> text-align:<%= theChip.isAlignRight() ? "right" : "left" %>;" 
		 <%= onKeyPressString %> 
		 <%=(theChip.isEditable()?"":"readonly=\"readonly\"")%>
		 
		 <%=theChip.getMaxLength() != 0 ? ("maxlength=\""+ theChip.getMaxLength()+"\"") : ""%>
		 
		 />
</td>
</tr>
</table>
</div>
