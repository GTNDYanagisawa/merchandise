<%@include file="../head.inc"%>
<%
	EnumerationValueSelectEditorChip theChip = (EnumerationValueSelectEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String contextMenu = theChip.hasVisibleContextMenuEntries()
									? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
									: "return false;";

	String onKeyPressString = "";
	if( theChip.getParent() instanceof FlexibleConditionChip )
	{
		onKeyPressString = "onkeypress=\"return checkForEnter(event);\"";
	}
	else
	{
		onKeyPressString = "onkeypress=\"return true;\"";
	}

	String style = theChip.getWidth() != 0 ? ("style=\"width:" + theChip.getWidth() + "px;\"") : "";

%>


<div <%= style %> oncontextmenu="<%= contextMenu %>">
	<table class="enumerationValueSelectEditorChip" <%= style %> cellspacing="0" cellpadding="0">
		<tr>
			<td>
				<select
					oncontextmenu="<%= contextMenu %>"
					class="<%= (theChip.isEditable() ? "enabled" : "disabled") %>"
					size="<%= theChip.getSize() %>"
					name="<%= theChip.getEventID(SelectLayoutChip.SET_VALUE) %>"
					<%= onKeyPressString %>
					id="<%= theChip.getUniqueName() %>_select"
<% 
						if( theChip.submitForm() ) 
						{ 
%>
							onchange="setScrollAndSubmit();"
<% 
						} 
%>
					<%= (theChip.isEditable() ? "" : "disabled") %>>
<%
						if( theChip.isOptional() || ((theChip.getValue() == null) && !theChip.isHideNull()) )
						{
%>
							<option value="-1"><%= theChip.getNoSelectionText() %></option>
<%
						}
%>
<%
						for( int i=0; i<theChip.getCount(); i++ )
						{
%>
							<option value="<%= Integer.toString(i) %>"<%= (theChip.isSelected(i) ? " selected" : "") %>>
								<%= theChip.getStringValue(i) %>
							</option>
<%
						}
%>
				</select>
			</td>
		</tr>
	</table>
</div>
