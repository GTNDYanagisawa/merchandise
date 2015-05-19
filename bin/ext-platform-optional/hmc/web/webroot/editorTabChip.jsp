<%@include file="head.inc"%>
<%
	final EditorTabChip theChip = (EditorTabChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
%>
	<li <%= theChip.isSelected() ? "class=\"selected\"" : "" %>>
		<div class="chip-event">
			<a href="#" title="<%= theChip.getTooltip() %>" name="<%= theChip.getCommandID(EditorTabChip.SELECT) %>" id="<%= theChip.getUniqueName() %>_a" hidefocus="true">
				<span id="<%= theChip.getUniqueName() %>_span" <%= theChip.containsEmptyMandatoryAttributes() ? "class=\"mandatory-missing\"" : "" %>>
					<%= localized(theChip.getName()) %>
				</span>
			</a>
		</div>
	</li>
<%
	if( theChip.isNextTab() )
	{
%>
		<script language="JavaScript1.2">
			addKeyEvent("T", "<%= theChip.getCommandID(EditorTabChip.SELECT) %>");
		</script>
<%
	} 
	else if( theChip.isPreviousTab() )
	{
%>
		<script language="JavaScript1.2">
			addKeyEvent("B", "<%= theChip.getCommandID(EditorTabChip.SELECT) %>");
		</script>
<%
	}
%>
