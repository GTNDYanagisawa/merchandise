<%@include file="../head.inc"%>
<%
	DateTimeEditorChip theChip = (DateTimeEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final boolean showPattern = theChip.isShowPattern();
	final boolean isEditable = theChip.isEditable();
	String eventID = theChip.getEventID(DateTimeEditorChip.SET_VALUE);
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


<div style="width:<%= theChip.getWidth() %>px;">
	<table class="dateTimeEditorChip" style="width:<%= theChip.getWidth() %>px;" cellspacing="0" cellpadding="0" border="0">
		<tr>
			<!-- date input field -->
			<td style="width:<%= theChip.getDateWidth() %>px;"
				 class="dateInputField"
				 title="<%=theChip.getScriptDateFormat()%>">
				<input type="input" name="<%= eventID %>"
					class="<%= isEditable ? "enabled" : "disabled" %>"
					value="<%=theChip.getStringDateValue()%>"
					style="width:<%= theChip.getDateWidth() %>px;"
					id="<%= theChip.getUniqueName() %>_date"
					<%= onKeyPressString %>
					<%= isEditable ? "" : "readonly=\"readonly\"" %>/>
			</td>
			
			<!-- date picker symbol -->
			<td class="datePickerSymbol">
<%
				if( isEditable )
				{
%>
					<div class="button-on-white datePickerSymbol">
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
					<div class="datePickerSymbol">
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
				<td class="dateFormatString">
					<div class="dateFormatString">
						(<%=theChip.getScriptDateFormat()%>)
					</div>
				</td>
<%
			}
			if( theChip.isWrapValues() )
			{
%>
			<td class="spacer"/>
		</tr>
		<tr>
<%
			}
%>
			<!-- time input field -->
			<td style="width:<%= theChip.getTimeWidth() %>px;"
				 class="timeInputField"
				 title="<%=theChip.getScriptTimeFormat()%>">
				<input type="input" name="<%=theChip.getEventID(DateTimeEditorChip.SET_TIME_VALUE)%>"
					class="<%= isEditable ? "enabled" : "disabled" %>"
					value="<%=theChip.getStringTimeValue()%>"
					style="width:<%= theChip.getTimeWidth() %>px"
					id="<%= theChip.getUniqueName() %>_time"
					<%= onKeyPressString %>
					<%= isEditable ? "" : "readonly=\"readonly\"" %>/>
			</td>
<%
			if( showPattern )
			{
%>			
				<!-- time format string -->
				<td/>
				<td class="timeFormatString">
					<div class="timeFormatString">
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
