<%@include file="../../head.inc"%>
<%@include file="../../xp_button.inc"%>
<%@page import="de.hybris.platform.cronjob.hmc.*" %>
<%
	TriggerTimeValuesEditorChip theChip = (TriggerTimeValuesEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

	TriggerValuesHolder triggerValues = theChip.getTriggerValues();
%>
<table class="triggerTimeValuesEditorChip" cellspacing="0" cellpadding="0">
	<tr>
		<td class="sectionheader" colspan="2">
			<div class="sh"><%= localized("section.trigger.interval") %></div>
		</td>
	</tr>

<!-- interval settings -->
<%
	String intervalID = theChip.getEventID(TriggerTimeValuesEditorChip.SET_INTERVAL);
%>
	<tr>
		<td><img width="10" height="1" src="images/transparent.gif" /></td>
		<td>
			<table cellspacing="0" cellpadding="0">
				<tr>
					<td class="arrowButton">&nbsp;</td>
			 		<td class="label"><%= localized("trigger.interval") %>:</td>
			 		<td class="localAndChangedSign">&nbsp;</td>
					<td>
						<table class="ttvecIntervals" cellpadding=0" cellspacing="0">
							<tr>
								<td>
									<input type="hidden" name="<%= intervalID %>" value="<%= triggerValues.getSelectedInterval() %>" />
									<input type="radio" name="<%= TriggerTimeValuesEditorChip.SET_INTERVAL %>"
											 <%= triggerValues.isSelectedInterval(TriggerTimeValuesEditorChip.INTERVAL_DAILY) ? "checked=\"checked\""
											 	  : "onclick=\"document.editorForm.elements['" + intervalID + "'].value='" + TriggerTimeValuesEditorChip.INTERVAL_DAILY + "';setScrollAndSubmit();\"" %> />
								</td>
								<td>
									<%= localized("trigger.interval.daily") %>
								</td>
							</tr>
							<tr>
								<td>
									<input type="radio" name="<%= TriggerTimeValuesEditorChip.SET_INTERVAL %>"
											 <%= triggerValues.isSelectedInterval(TriggerTimeValuesEditorChip.INTERVAL_WEEKLY) ? "checked=\"checked\""
											 	  : "onclick=\"document.editorForm.elements['" + intervalID + "'].value='" + TriggerTimeValuesEditorChip.INTERVAL_WEEKLY + "';setScrollAndSubmit();\"" %> />
								</td>
								<td>
									<%= localized("trigger.interval.weekly") %>
								</td>
							</tr>
							<tr>
								<td>
									<input type="radio" name="<%= TriggerTimeValuesEditorChip.SET_INTERVAL %>"
											 <%= triggerValues.isSelectedInterval(TriggerTimeValuesEditorChip.INTERVAL_MONTHLY) ? "checked=\"checked\""
											 	  : "onclick=\"document.editorForm.elements['" + intervalID + "'].value='" + TriggerTimeValuesEditorChip.INTERVAL_MONTHLY + "';setScrollAndSubmit();\"" %> />
								</td>
								<td>
									<%= localized("trigger.interval.monthly") %>
								</td>
							</tr>
							<tr>
								<td>
									<input type="radio" name="<%= TriggerTimeValuesEditorChip.SET_INTERVAL %>"
											 <%= triggerValues.isSelectedInterval(TriggerTimeValuesEditorChip.INTERVAL_ONCE) ? "checked=\"checked\""
											 	  : "onclick=\"document.editorForm.elements['" + intervalID + "'].value='" + TriggerTimeValuesEditorChip.INTERVAL_ONCE + "';setScrollAndSubmit();\"" %> />
								</td>
								<td>
									<%= localized("trigger.interval.once") %>
								</td>
							</tr>
							<tr>
								<td>
									<input type="radio" name="<%= TriggerTimeValuesEditorChip.SET_INTERVAL %>"
											 <%= triggerValues.isSelectedInterval(TriggerTimeValuesEditorChip.INTERVAL_FREE) ? "checked=\"checked\""
											 	  : "onclick=\"document.editorForm.elements['" + intervalID + "'].value='" + TriggerTimeValuesEditorChip.INTERVAL_FREE + "';setScrollAndSubmit();\"" %> />
								</td>
								<td>
									<%= localized("trigger.interval.free") %>
								</td>
							</tr>
							<tr>
								<td>
									<input type="radio" name="<%= TriggerTimeValuesEditorChip.CRON_EXPRESSION %>"
											 <%= triggerValues.isSelectedInterval(TriggerTimeValuesEditorChip.CRON_EXPRESSION) ? "checked=\"checked\""
											 	  : "onclick=\"document.editorForm.elements['" + intervalID + "'].value='" + TriggerTimeValuesEditorChip.CRON_EXPRESSION + "';setScrollAndSubmit();\"" %> />
								</td>
								<td>
									<%= localized("trigger.interval.cronexpression") %>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td class="sectionheader" colspan="2">
			<div class="sh"><%= localized("section.trigger.details") %></div>
		</td>
	</tr>


<!-- next activation time -->
	<tr class="spacerTR">
		<td><img width="10" height="1" src="images/transparent.gif" /></td>
		<td>
			<table cellspacing="0" cellpadding="0">
				<tr class="disabled">
					<td width="arrowButton">&nbsp;</td>
			 		<td class="label"><%= localized("trigger.activationtime") %>:</td>
			 		<td class="localAndChangedSign">&nbsp;</td>
					<td>
						<%= theChip.getActivationTimeAsString() %>
					</td>
				</tr>
			</table>
		</td>
	</tr>


<%
if( !triggerValues.getSelectedInterval().equals(TriggerTimeValuesEditorChip.CRON_EXPRESSION)) {
%>
<!-- start time & date-->
	<tr class="spacerTR">
		<td><img width="10" height="1" src="images/transparent.gif" /></td>
		<td>
			<table cellspacing="0" cellpadding="0">
				<tr>
					<td width="arrowButton">&nbsp;</td>
			 		<td class="label"><%= localized("trigger.starttime") %>:</td>
			 		<td class="localAndChangedSign">&nbsp;</td>
					<td class="padder">
						<% theChip.getStartTimeEditorChip().render(pageContext); %>
					</td>
					<td class="buttonPadder">
						<%= xpButton(localized("trigger.button.currenttime"),
										 theChip.getCommandID(TriggerTimeValuesEditorChip.SET_CURRENT_TIME),
										 localized("trigger.button.tooltip.currenttime")) %>
					</td>
				</tr>
			</table>
		</td>
	</tr>
<%
  }
	if( triggerValues.getSelectedInterval().equals(TriggerTimeValuesEditorChip.INTERVAL_DAILY) )
	{
		// daily interval selected
		String daySkipID = theChip.getEventID(TriggerTimeValuesEditorChip.SET_DAYSKIP);
%>
<!-- interval = daily -->
<!-- day skip -->
		<input type="hidden" name="<%= daySkipID %>" value="<%= triggerValues.getDaySkip() %>" />
		<tr>
			<td><img width="10" height="1" src="images/transparent.gif" /></td>
			<td>
				<table cellspacing="0" cellpadding="0">
					<tr>
						<td width="arrowButton">&nbsp;</td>
				 		<td class="label"><%= localized("trigger.frequency") %>:</td>
				 		<td class="localAndChangedSign">&nbsp;</td>
						<td>
							<table class="ttvecIntervals" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<input type="radio" name="<%= TriggerTimeValuesEditorChip.SET_DAYSKIP %>"
												 <%= triggerValues.getDaySkip() == 1 ? "checked=\"checked\""
												 	  : "onclick=\"document.editorForm.elements['" + daySkipID + "'].value='1'; setScrollAndSubmit();\"" %>/>
									</td>
									<td colspan="2">
										<%= localized("trigger.dayskip.daily") %>
									</td>
								</tr>
								<tr>
									<td>
										<input type="radio" name="<%= TriggerTimeValuesEditorChip.SET_DAYSKIP %>"
												 <%= triggerValues.getDaySkip() != 1 ? "checked=\"checked\""
												 	  : "onclick=\"document.editorForm.elements['" + daySkipID +"'].value=document.editorForm.elements['" + TriggerTimeValuesEditorChip.SET_DAYSKIP + "_select'].value;setScrollAndSubmit();\"" %> />
									</td>
									<td>
										<%= localized("trigger.dayskip.frequency.pre") %>&nbsp;&nbsp;
									</td>
									<td>

										<select name="<%= TriggerTimeValuesEditorChip.SET_DAYSKIP + "_select" %>" size="1"
												  onchange="document.editorForm.elements['<%= daySkipID %>'].value=this.value"
												  <%= triggerValues.getDaySkip() == 1 ? " disabled" : "" %> >
<%
											for( int i = 2; i < 100; i++ )
											{
%>
												<option value="<%= i %>" <%= (i == triggerValues.getDaySkip() ? " selected" : "") %>><%= i %></option>
<%
											}
%>
										</select>&nbsp;
									</td>
									<td>
										<%= localized("trigger.dayskip.frequency.post") %>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
<%
	}
	else if( triggerValues.getSelectedInterval().equals(TriggerTimeValuesEditorChip.INTERVAL_WEEKLY) )
	{
		// weekly interval selected
		String weekSkipID = theChip.getEventID(TriggerTimeValuesEditorChip.SET_WEEKSKIP);
%>
<!-- interval = weekly -->
<!-- week skip -->
		<input type="hidden" name="<%= theChip.getEventID(TriggerTimeValuesEditorChip.PING_WEEKLY_INTERVAL) %>" value="<%= AbstractChip.TRUE %>" />
		<input type="hidden" name="<%= weekSkipID %>" value="<%= triggerValues.getWeekSkip() %>" />
		<tr class="spacerTR">
			<td><img width="10" height="1" src="images/transparent.gif" /></td>
			<td>
				<table cellspacing="0" cellpadding="0">
					<tr>
						<td width="arrowButton">&nbsp;</td>
				 		<td class="label"><%= localized("trigger.frequency") %>:</td>
				 		<td class="localAndChangedSign">&nbsp;</td>
						<td>
							<table class="ttvecIntervals" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<input type="radio" name="<%= TriggerTimeValuesEditorChip.SET_WEEKSKIP + "_frequency" %>"
												 <%= triggerValues.getWeekSkip() == 1 ? "checked=\"checked\""
												 	  : "onclick=\"document.editorForm.elements['" + weekSkipID + "'].value='1'; setScrollAndSubmit();\"" %>/>
									</td>
									<td colspan="2">
										<%= localized("trigger.weekskip.weekly") %>
									</td>
								</tr>
								<tr>
									<td>
										<input type="radio" name="<%= TriggerTimeValuesEditorChip.SET_WEEKSKIP + "_frequency" %>"
												 <%= triggerValues.getWeekSkip() != 1 ? "checked=\"checked\""
												 	  : "onclick=\"document.editorForm.elements['" + weekSkipID +"'].value=document.editorForm.elements['" + TriggerTimeValuesEditorChip.SET_WEEKSKIP + "_select'].value;setScrollAndSubmit();\"" %> />
									</td>
									<td>
										<%= localized("trigger.weekskip.frequency.pre") %>&nbsp;&nbsp;
									</td>
									<td>
										<select name="<%= TriggerTimeValuesEditorChip.SET_WEEKSKIP + "_select" %>" size="1"
												  onchange="document.editorForm.elements['<%= weekSkipID %>'].value=this.value"
												  <%= triggerValues.getWeekSkip() == 1 ? " disabled" : "" %> >
<%
											for( int i = 2; i <= 10; i++ )
											{
%>
												<option value="<%= i %>" <%= (i == triggerValues.getWeekSkip() ? " selected" : "") %>><%= i %></option>
<%
											}
%>
										</select>&nbsp;
									</td>
									<td>
										<%= localized("trigger.weekskip.frequency.post") %>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>

<!-- days of week -->
<%
		String daysOfWeekID = theChip.getEventID(TriggerTimeValuesEditorChip.SET_DAYSOFWEEK);
%>
		<tr>
			<td><img width="10" height="1" src="images/transparent.gif" /></td>
			<td>
				<table cellspacing="0" cellpadding="0">
					<tr>
						<td width="arrowButton">&nbsp;</td>
				 		<td class="label"><%= localized("trigger.daysofweek") %>:</td>
				 		<td class="localAndChangedSign">&nbsp;</td>
						<td>
							<table class="ttvecIntervals" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<input type="checkbox" name="<%= daysOfWeekID %>" value="<%= theChip.MONDAY %>"
												 <%= triggerValues.isDaySelected(theChip.MONDAY) ? "checked=\"checked\"" : "" %> />
									</td>
									<td>
										<%= localized("trigger.dayofweek.monday") %>
									</td>
									<td>
										<input type="checkbox" name="<%= daysOfWeekID %>" value="<%= theChip.SATURDAY %>"
												 <%= triggerValues.isDaySelected(theChip.SATURDAY) ? "checked=\"checked\"" : "" %> />
									</td>
									<td>
										<%= localized("trigger.dayofweek.saturday") %>
									</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="<%= daysOfWeekID %>" value="<%= theChip.TUESDAY %>"
												 <%= triggerValues.isDaySelected(theChip.TUESDAY) ? "checked=\"checked\"" : "" %> />
									</td>
									<td>
										<%= localized("trigger.dayofweek.tuesday") %>
									</td>
									<td>
										<input type="checkbox" name="<%= daysOfWeekID %>" value="<%= theChip.SUNDAY %>"
												 <%= triggerValues.isDaySelected(theChip.SUNDAY) ? "checked=\"checked\"" : "" %> />
									</td>
									<td>
										<%= localized("trigger.dayofweek.sunday") %>
									</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="<%= daysOfWeekID %>" value="<%= theChip.WEDNESDAY %>"
												 <%= triggerValues.isDaySelected(theChip.WEDNESDAY) ? "checked=\"checked\"" : "" %> />
									</td>
									<td>
										<%= localized("trigger.dayofweek.wednesday") %>
									</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="<%= daysOfWeekID %>" value="<%= theChip.THURSDAY %>"
												 <%= triggerValues.isDaySelected(theChip.THURSDAY) ? "checked=\"checked\"" : "" %> />
									</td>
									<td>
										<%= localized("trigger.dayofweek.thursday") %>
									</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="<%= daysOfWeekID %>" value="<%= theChip.FRIDAY %>"
												 <%= triggerValues.isDaySelected(theChip.FRIDAY) ? "checked=\"checked\"" : "" %> />
									</td>
									<td>
										<%= localized("trigger.dayofweek.friday") %>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
<%
	}
	else if( triggerValues.getSelectedInterval().equals(TriggerTimeValuesEditorChip.INTERVAL_MONTHLY) )
	{
		// monthly interval selected
		String dayOfMonthID = theChip.getEventID(TriggerTimeValuesEditorChip.SET_DAYOFMONTH);
%>
<!-- interval = monthly -->
<!-- choose day of month -->
		<tr>
			<td><img width="10" height="1" src="images/transparent.gif" /></td>
			<td>
				<table cellspacing="0" cellpadding="0">
					<tr>
						<td width="arrowButton">&nbsp;</td>
				 		<td class="label"><%= localized("trigger.dayofmonth") %>:</td>
				 		<td class="localAndChangedSign">&nbsp;</td>
						<td>
							<table class="ttvecIntervals" cellspacing="0" cellpadding="0">
								<tr>
									<td class="padder">
										<%= localized("trigger.dayofmonth.frequency.pre") %>&nbsp;&nbsp;
									</td>
									<td>

										<select name="<%= dayOfMonthID %>" size="1">
<%
											for( int i = 1; i <= 31; i++ )
											{
%>
												<option value="<%= i %>" <%= (i == triggerValues.getDayOfMonth() ? " selected" : "") %>><%= i %></option>
<%
											}
%>
										</select>&nbsp;
									</td>
									<td>
										<%= localized("trigger.dayofmonth.frequency.post") %>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
<%
	}
	else if( triggerValues.getSelectedInterval().equals(TriggerTimeValuesEditorChip.INTERVAL_FREE) )
	{
		// free interval selected
%>

<!-- interval = free interval -->
<!-- choose interval values for days, months, years, hours, minutes, seconds -->
		<tr>
			<td><img width="10" height="1" src="images/transparent.gif" /></td>
			<td>
				<table cellspacing="0" cellpadding="0">
					<tr>
						<td width="arrowButton">&nbsp;</td>
				 		<td class="label"><%= localized("trigger.interval.range") %>:</td>
				 		<td class="localAndChangedSign">&nbsp;</td>
						<td>
							<table class="ttvecIntervals" cellspacing="0" cellpadding="0">
								<tr>
									<td class="padder">
										<select name="<%= theChip.getEventID(TriggerTimeValuesEditorChip.SET_INTERVAL_SECONDS) %>" size="1">
<%
											for( int i = 0; i < 60; i++ )
											{
%>
												<option value="<%= i %>" <%= (i == triggerValues.getIntervalSeconds() ? " selected" : "") %>><%= i %></option>
<%
											}
%>
										</select>&nbsp;
									</td>
									<td>
										<%= localized("trigger.interval.free.seconds") %>
									</td>
								</tr>
								<tr>
									<td class="padder">
										<select name="<%= theChip.getEventID(TriggerTimeValuesEditorChip.SET_INTERVAL_MINUTES) %>" size="1">
<%
											for( int i = 0; i < 60; i++ )
											{
%>
												<option value="<%= i %>" <%= (i == triggerValues.getIntervalMinutes() ? " selected" : "") %>><%= i %></option>
<%
											}
%>
										</select>&nbsp;
									</td>
									<td>
										<%= localized("trigger.interval.free.minutes") %>
									</td>
								</tr>
								<tr>
									<td class="padder">
										<select name="<%= theChip.getEventID(TriggerTimeValuesEditorChip.SET_INTERVAL_HOURS) %>" size="1">
<%
											for( int i = 0; i < 24; i++ )
											{
%>
												<option value="<%= i %>" <%= (i == triggerValues.getIntervalHours() ? " selected" : "") %>><%= i %></option>
<%
											}
%>
										</select>&nbsp;
									</td>
									<td>
										<%= localized("trigger.interval.free.hours") %>
									</td>
								</tr>
								<tr>
									<td class="padder">
										<select name="<%= theChip.getEventID(TriggerTimeValuesEditorChip.SET_INTERVAL_DAYS) %>" size="1">
<%
											for( int i = 0; i <= 31; i++ )
											{
%>
												<option value="<%= i %>" <%= (i == triggerValues.getIntervalDays() ? " selected" : "") %>><%= i %></option>
<%
											}
%>
										</select>&nbsp;
									</td>
									<td>
										<%= localized("trigger.interval.free.days") %>
									</td>
								</tr>
								<tr>
									<td class="padder">
										<select name="<%= theChip.getEventID(TriggerTimeValuesEditorChip.SET_INTERVAL_MONTHS) %>" size="1">
<%
											for( int i = 0; i <= 12; i++ )
											{
%>
												<option value="<%= i %>" <%= (i == triggerValues.getIntervalMonths() ? " selected" : "") %>><%= i %></option>
<%
											}
%>
										</select>&nbsp;
									</td>
									<td>
										<%= localized("trigger.interval.free.months") %>
									</td>
								</tr>
								<tr>
									<td class="padder">
										<select name="<%= theChip.getEventID(TriggerTimeValuesEditorChip.SET_INTERVAL_YEARS) %>" size="1">
<%
											for( int i = 0; i <= 30; i++ )
											{
%>
												<option value="<%= i %>" <%= (i == triggerValues.getIntervalYears() ? " selected" : "") %>><%= i %></option>
<%
											}
%>
										</select>&nbsp;
									</td>
									<td>
										<%= localized("trigger.interval.free.years") %>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
<%
	}
	else if( triggerValues.getSelectedInterval().equals(TriggerTimeValuesEditorChip.CRON_EXPRESSION) )
	{
		// monthly interval selected
		String expression = theChip.getEventID(TriggerTimeValuesEditorChip.CRON_EXPRESSION);
%>
		<tr>
			<td><img width="10" height="1" src="images/transparent.gif" /></td>
			<td>
				<table cellspacing="0" cellpadding="0">
					<tr>
						<td width="arrowButton">&nbsp;</td>
				 		<td class="label"><%= localized("trigger.cronexpression") %></td>
				 		<td class="localAndChangedSign">&nbsp;</td>
						<td>
							<table class="ttvecIntervals" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<% theChip.getCronExpressionEditorChip().render(pageContext); %>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
<%
	}
%>
</table>
