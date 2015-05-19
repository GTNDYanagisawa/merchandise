<%@include file="head.inc"%>
<%@include file="xp_button.inc"%>

<%
	ActionSelectChip theChip = (ActionSelectChip)request.getAttribute( AbstractChip.CHIP_KEY );
	
	List actions = theChip.getActions();
	String actionEventId = theChip.getEventID( theChip.ACTION );
	final String tooltip = theChip.getTooltip() != null ? localized(theChip.getTooltip()) : "";
%>

<script language="JavaScript" type="text/JavaScript">
	
	var actionConfirmationFlags_<%= actionEventId %> = new Array( <%= actions.size() %> );
	var actionConfirmationMessages_<%= actionEventId %> = new Array( <%= actions.size() %> );
	<%
		for( int i = 0; i < actions.size(); i++ )
		{
			AbstractActionChip action = (AbstractActionChip)actions.get( i );
			boolean needConfirmation = action.getAction().needConfirmation();
			String confirmationMessage = action.getAction().getConfirmationMessage();
			if( confirmationMessage == null || confirmationMessage.trim().length() == 0 )
				confirmationMessage = theChip.getDisplayState().getLocalizedString( "actionconfirmation" );
	
	%>actionConfirmationFlags_<%= actionEventId %>[<%= i %>] = <%= needConfirmation ? 1 : 0 %>;
	actionConfirmationMessages_<%= actionEventId %>[<%= i %>] = '<%= confirmationMessage %>';
	<%
		}
	%>
	var noActionSelectedMessage_<%= actionEventId %> = '<%= escapeHTML(theDisplayState.getLocalizedString( "noactionselected" )) %>';

	function getConfirmIfNeeded_<%= actionEventId %>( formElement, event )
	{
		var val = formElement.elements[event].value;
		if( val >= 0 && val < actionConfirmationFlags_<%= actionEventId %>.length )
		{
			var flag = actionConfirmationFlags_<%= actionEventId %>[ val ];
			if( flag == 1 )
			{
				return confirm( actionConfirmationMessages_<%= actionEventId %>[ val ] );
			}
		}
		else
		{
			alert( noActionSelectedMessage_<%= actionEventId %> );
			return false;
		}
		
		return true;
	}

</script>
<%
	if( !actions.isEmpty() )
	{
%>
		<td>
			<table border="0" cellspacing="0" cellpadding="0" title="<%= tooltip %>">
				<tr>
					<td style="color:#000000; padding:0 4px 0 3px;"><%= localized("toolbaractions") %>:</td>
					<td>
						<script language="JavaScript1.2" type="text/javascript">
						  markClipStart(2);
						</script>
						
						<select 
							onkeypress="return true;"
							onchange="return true;"
							name="<%= actionEventId %>">
							<option value="-1"><%= localized( "noselection" ) %></option>
						<%
							for( int i = 0; i < actions.size(); i++ )
							{
								AbstractActionChip action = (AbstractActionChip)actions.get( i );
								boolean selected = theChip.getSelectedAction() != null && theChip.getSelectedAction().equals( action );
								%>
									<option value="<%=i%>"<%=(selected ? " selected=\"selected\"" : "")%>><%= localized( action.getName() ) %></option>
								<%
							}
						%>
						</select>
						
						<script language="JavaScript1.2" type="text/javascript">
						  markClipEnd();
						</script>
					</td>
					<td>&nbsp;&nbsp;</td>
					<td>
						<%
							String buttonContent = "<img style=\"vertical-align:bottom;\" src=\"" + AbstractActionChip.DEFAULT_ACTION_ICON + "\" border=\"0\">&nbsp;&nbsp;" + localized("performaction");
							%><%= xpButtonWithCondition( buttonContent, theChip.getCommandID( ActionSelectChip.PERFORM_ACTION ), "", false, "getConfirmIfNeeded_" + actionEventId + "( currentForm, '" + actionEventId + "' )" ) %><%
						%>
					</td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				</tr>
			</table>
		</td>
<%
	}
%>
