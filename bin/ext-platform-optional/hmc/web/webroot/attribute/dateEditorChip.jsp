<%@include file="../head.inc"%>
<%
	DateEditorChip theChip = (DateEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	String eventID = theChip.getEventID(DateEditorChip.SET_VALUE);
	final boolean showPattern = theChip.isShowPattern();

	String onKeyPressString = "";
	if( theChip.getParent() instanceof FlexibleConditionChip )
	{
		onKeyPressString = "onkeypress=\"return checkForEnter(event);\"";
	}
	else
	{
		onKeyPressString = "onkeypress=\"return true;\"";
	}
	
	String styleWidth = "style=\"width:" + theChip.getWidth() + "px;\"";
	String styleDateWidth = "style=\"width:" + theChip.getDateWidth() + "px;\""; 
%>

<div <%= styleWidth %>>
	<table class="dateEditorChip" <%= styleDateWidth %> cellspacing="0" cellpadding="0">
		<tr>

			<!-- date input field -->
			<td class="inputField" <%= styleDateWidth %>>
				<input type="input" name="<%= eventID %>"
					class="<%=(theChip.isEditable() ? "enabled" : "disabled") %>"
					value="<%=theChip.getStringValue()%>"
					id="<%= theChip.getUniqueName() %>_date"
					<%= onKeyPressString %>
	 				<%= styleDateWidth %>
					<%=(theChip.isEditable()?"":"readonly=\"readonly\"")%>/>
			</td>
	
			<!-- date picker symbol -->
			<td class="pickerSymbol">
<%
				if (theChip.isEditable())
				{
%>
					<div class="button-on-white pickerSymbol">
 						<a href="#"
 							onclick="(new calendar(document.editorForm.elements['<%= eventID %>'], '<%= localized("datepicker") %>', '<%= localized("datepicker.header") %>', '<%= theChip.getDatePattern() %>', <%= HMCHelper.getLocalizedMonths() %>, <%= HMCHelper.getLocalizedWeekdays() %>)).popup();"
							hidefocus="true" title="<%= localized("datepicker") %>">
						   <span>
								<img class="icon" id="<%= theChip.getUniqueName() %>_img" src="images/icons/calendar.gif"/>
							</span>
						</a>
					</div>
<%
				}
				else
				{
%>
					<div class="pickerSymbol">
						<img src="images/icons/calendar_inactive.gif"/>
					</div>
<%
				}
%>
			</td>			
<%
			if( showPattern )
			{
%>			
				<!-- date format string -->
				<td class="formatString">
					<div class="formatString">
						(<%=theChip.getScriptDateFormat()%>)
					</div>
				</td>
<%
			}
%>
 			<td class="spacer"/>
		</tr>
	</table>
</div>
