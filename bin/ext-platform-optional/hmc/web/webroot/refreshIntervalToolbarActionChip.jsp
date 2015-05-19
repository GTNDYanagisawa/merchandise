<%@include file="head.inc"%>
<%
	RefreshIntervalToolbarActionChip theChip = (RefreshIntervalToolbarActionChip) request.getAttribute(AbstractChip.CHIP_KEY);
	String label = (theChip.getLabel() == null ? "" : localized(theChip.getLabel()));
	final String tooltip = (theChip.getTooltip() == null ? label : localized(theChip.getTooltip()));
	
	theChip.resetEntries();

	StringBuffer sb = new StringBuffer();
	String formName = theChip.isCommandAction() ? theChip.getCommandID(theChip.getEvent()) : theChip.getEventID(theChip.getEvent());
	sb.append( "<select name=\"" + formName + "\" " );
	sb.append( "class=\"componentformelement\"" );
	sb.append( " onMouseover=\"window.status='" + label + "';return true;\"" );
	sb.append( " onMouseOut=\"window.status='';return true;\"" );
	if( !theChip.isEnabled() )
	{
		sb.append( " disabled" );
	}
	
	if( theChip.getWidth() != null)
	{
		sb.append( " style=\"width:" + theChip.getWidth() + ";\"" );
	}
	
	if( theChip.getJavaScript()!=null )
	{
		sb.append( " onchange=\"" + theChip.getJavaScript() + "\"" );
	}
	sb.append( ">\n" );	
	
	for( Iterator it = theChip.getEntries().iterator(); it.hasNext(); )
	{ 
		AbstractSelectToolbarActionChip.Entry entry = (AbstractSelectToolbarActionChip.Entry) it.next();
		sb.append("   <option value=\"" + entry.getDisplayValue() + "\"" + (theChip.isSelected(entry) ? " selected" : "") + ">" 
					 + entry.getDisplayName() + "</option>\n");
	} 
	sb.append( "</select>\n" );
%>
<%= (label != "" ? ("<td style=\"vertical-align:middle;\" title=\"" + tooltip + "\">&nbsp;&nbsp;" + label + ":&nbsp;</td>") : "") %>
<td style="vertical-align:middle;" <%= theChip.getWidth() != null ? "style=\"width=" + theChip.getWidth() + ";\"" : "" %> align="center" title="<%= tooltip %>">

<input type="hidden" name="<%=theChip.getCommandID(RefreshIntervalToolbarActionChip.REFRESH)%>" value="true"/>
<script language="JavaScript1.2" type="text/javascript">
  markClipStart(2);
</script>
<%= sb.toString() %>
<script language="JavaScript1.2" type="text/javascript">
  markClipEnd();
</script>
<script language="JavaScript1.2" type="text/javascript">
	// countdown and refresh logic

	var activeInterval;
	var remainingSeconds = <%= theChip.getSelectedValue() %>;

	function showCountdown()
	{
		--remainingSeconds;
		
		// try to set 'time left' status
		var timeElement = document.getElementById("refreshtimestatus");

		if( timeElement )
		{
			timeElement.firstChild.data = "(<%= localized("refresh.interval.status") %>: " + remainingSeconds + "s)";
		}

		if( remainingSeconds == 0 )
		{
			window.clearInterval(activeInterval);
			//location.reload();
			setScrollAndSubmit();
			return;
		}
	}
	
	if( remainingSeconds > 0 )
	{
		activeInterval = window.setInterval("showCountdown()", 1000);
	}

</script>
</td>
