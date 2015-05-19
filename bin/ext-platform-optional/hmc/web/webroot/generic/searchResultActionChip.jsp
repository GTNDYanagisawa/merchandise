<%@include file="../head.inc"%>
<%@include file="../xp_button.inc"%>

<%
	SearchResultActionChip theChip = (SearchResultActionChip)request.getAttribute( AbstractChip.CHIP_KEY );
	if( theChip.isActive() )
	{
		String buttonContent = "<img style=\"vertical-align:bottom;\" src=\"" + SearchResultActionChip.DEFAULT_ACTION_ICON + "\">&nbsp;&nbsp;" + localized(theChip.getName());
		if( theChip.needsConfirmation() )
		{
			%>
				<%= xpButton( buttonContent, theChip.getPerformCommandID(), "", false, theChip.getConfirmationMessage() ) %>
			<%
		}
		else
		{
			%>
				<%= xpButton( buttonContent, theChip.getPerformCommandID() ) %>
			<%
		}
	}
	else
	{
		String buttonContent = "<img style=\"vertical-align:bottom;\" src=\"" + SearchResultActionChip.DEFAULT_ACTION_DISABLED_ICON + "\">&nbsp;&nbsp;" + localized(theChip.getName());
		%>
			<%= xpButtonDisabled( buttonContent, localized("actionnotactive") ) %>
		<%
	}
%>
