<%@include file="../head.inc"%>
<%
	AllInstancesSelectEditorChip theChip = (AllInstancesSelectEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	final String contextMenu = theChip.hasVisibleContextMenuEntries()
									? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
									: "return false;";

	theChip.resetEntries();

	String onKeyPressString = "";
	if( theChip.getParent() instanceof FlexibleConditionChip )
	{
		onKeyPressString = "onkeypress=\"return checkForEnter(event);\"";
	}
	else
	{
		onKeyPressString = "onkeypress=\"return true;\"";
	}
	
	String style = "style=\"width:" +  theChip.getWidth() + "px;\"";
	
%>
	<div <%= style %> oncontextmenu="<%= contextMenu %>">
	<table class="allInstancesSelectEditorChip" <%= style %> cellspacing="0" cellpadding="0">
		<tr>
			<td>
				<select
					class="<%= (theChip.isEditable() ? "enabled" : "disabled") %>"
					oncontextmenu="<%= contextMenu %>"
					size="<%= theChip.getSize() %>"
					name="<%= theChip.getEventID(AllInstancesSelectEditorChip.SET_VALUE) %>"
					id="<%= theChip.getUniqueName() %>_select"
					<%= onKeyPressString %>
					<%= style %>
					onchange="setScrollAndSubmit();"
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
					if( theChip.isOptional() || ( theChip.getValue() == null && !theChip.isHideNull() ) )
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
