<%@include file="../head.inc"%>
<%
	SubTypeSelectEditorChip theChip = (SubTypeSelectEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

	String onKeyPressString = "";
	if( theChip.getParent() instanceof FlexibleConditionChip )
	{
		onKeyPressString = "onkeypress=\"return checkForEnter(event);\"";
	}
	else
	{
		onKeyPressString = "onkeypress=\"return true;\"";
	}
%>
<div class="subTypeSelectEditorChip">
<select
		class="<%= (theChip.isEditable() ? "enabled" : "disabled") %>"
	size="1"
	name="<%=theChip.getEventID(SubTypeSelectEditorChip.SET_VALUE)%>"
	style="width: <%=theChip.getWidth()%>px"
	<%= onKeyPressString %>
	onchange="setScrollAndSubmit();"
	<%=(theChip.isEditable()?"":"disabled")%>>
	<%
		if (theChip.isOptional())
		{
			%>
				<option value="<%=SubTypeSelectEditorChip.CLEAR%>">&nbsp;</option>
			<%
		}
		String currentCode = theChip.getCurrentTypeCode();
		for (final Iterator it = theChip.getTypeEntries().iterator(); it.hasNext(); )
		{
			GenericHelper.TypeEntry entry = (GenericHelper.TypeEntry)it.next();
			%>
				<option value="<%=entry.getCode()%>"<%=(entry.getCode().equals(currentCode)?" selected":"")%>><%=entry.getDisplayName()%></option>
			<%
		}
	%>
</select>
</div>
<%
	if (theChip.hasItem())
	{
		%>
			<input type="hidden" name="<%=theChip.getEventID(SubTypeSelectEditorChip.OPEN_EDITOR)%>" value="<%=AbstractChip.FALSE%>">
			<input 
				type="image"
				src="images/icons/e_openeditor.gif"
				onclick="document.editorForm.elements['<%=theChip.getEventID(SubTypeSelectEditorChip.OPEN_EDITOR)%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();return false;"
				title="<%=localized("clicktoopeneditor")%>">
		<%
	}
%>
