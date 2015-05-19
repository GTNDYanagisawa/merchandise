<%@include file="../head.inc"%>
<%
	TimePeriodEditorChip theChip = (TimePeriodEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final boolean showPattern = theChip.isShowPattern();
	String eventID = theChip.getEventID(TimePeriodEditorChip.SET_TIME_VALUE);
	String onKeyPressString = "";
	if( theChip.getParent() instanceof FlexibleConditionChip )
	{
		onKeyPressString = "onkeypress=\"return checkForEnter(event);\"";
	}
	else
	{
		onKeyPressString = "onkeypress=\"return true;\"";
	}
	
	String style = "style=\"width:" + theChip.getWidth() + "px;\"";
	
%>

<div <%= style %>>
	<table class="timePeriodEditorChip" style="width:<%= theChip.getWidth() %>px;" cellspacing="0" cellpadding="0">
		<tr>
			<!-- time input field -->
			<td class="timeinput" style="width:<%= theChip.getTimeWidth() %>px;">
				<input type="input" name="<%=theChip.getEventID(TimePeriodEditorChip.SET_TIME_VALUE)%>"
					class="<%=(theChip.isEditable()?"enabled":"disabled")%>"
					value="<%=theChip.getStringValue()%>"
					<%= style %>
					<%= onKeyPressString %>
					<%=(theChip.isEditable()?"":"readonly=\"readonly\"")%>/>
			</td>
<%
			if( showPattern )
			{
%>			
				<!-- time format string -->
				<td/>
				<td class="timeformat">
					<div>
						(<%=theChip.getScriptTimeFormat()%>)
					</div>
				</td>
<%
			}
%>
			<td class="spacer"/>
		</tr>
	</table>
</div>
