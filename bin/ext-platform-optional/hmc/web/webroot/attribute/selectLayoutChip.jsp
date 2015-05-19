<%@include file="../head.inc"%>
<%
	SelectLayoutChip theChip = (SelectLayoutChip) request.getAttribute(AbstractChip.CHIP_KEY);

	String onKeyPressString = "";
	if( theChip.getParent() instanceof FlexibleConditionChip )
	{
		onKeyPressString = "onkeypress=\"return checkForEnter(event);\"";
	}
	else
	{
		onKeyPressString = "onkeypress=\"return true;\"";
	}

	String style = "style=\"width:" + theChip.getWidth() + "px;\" ";
%>

<%--
<script language="JavaScript1.2" type="text/javascript">
  markClipStart(1);
</script>
--%>

<div class="selectLayoutChip">
<select
	class="<%= (theChip.isEditable() ? "enabled" : "disabled") %>"
	size="<%= theChip.getSize() %>"
	name="<%= theChip.getEventID(SelectLayoutChip.SET_VALUE) %>"
	id="<%= theChip.getUniqueName() %>_select"
	<%= style %>
	<%= onKeyPressString %>
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
	for( int i=0; i < theChip.getCount(); i++ )
	{
%>
		<option value="<%= Integer.toString(i) %>"<%= (theChip.isSelected(i) ? " selected" : "") %>>
			<%= theChip.getStringValue(i) %>
		</option>
<%
	}
%>
</select>
</div>
<%--
<script language="JavaScript1.2" type="text/javascript">
  markClipEnd();
</script>
--%>
