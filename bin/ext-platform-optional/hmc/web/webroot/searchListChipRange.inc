<%
	int rangeCount = theChip.getMaxItemsPerPage();
	int totalCount = theChip.getItemCount();
	
	int ranges = (int)Math.ceil( (double)totalCount / rangeCount );
	int currentRange = (int)Math.ceil( (double)theChip.getRangeStart()/ rangeCount );
	currentRange++;
	
	int startRange = Math.max( Math.min( currentRange - 5, ranges - 10 ), 1 );
	
	boolean isEditing = theChip instanceof OrganizerListChip && ((OrganizerListChip) theChip).isEditing();
%>
<table class="footer" cellspacing="0" cellpadding="0">
	<tr>
		<td align="left" valign="bottom" width="7px"><img src="images/editortab_corner_bl.gif"/></td>
		<td style="white-space:nowrap">
			<%= (totalCount == 0 ? 0 : (theChip.getRangeStart() + 1)) %> - <%= theChip.getRangeStart()+theChip.getListEntryCount() %>
			<%= localized("searchlist.of") %>
			<%= totalCount %>&nbsp;&nbsp;
		</td>
		<td width="100%">&nbsp;</td>

		<td>
<%
			boolean enabled = theChip.getRangeStart() > 0  && !isEditing;
%>		
			<%= getFooterButton(theChip.getEventID(GenericItemSearchListChip.FIRST_PAGE),
												  localized("searchlist.first"), localized("searchlist.first.tooltip"),
												  "images/icons/footer_first" + (enabled ? "" : "_inactive") + ".gif",
												  null,
												  false, 
												  enabled) %>
<%
			if( enabled )
			{
%>
				<script language="JavaScript1.2">
						addKeyEvent("F", "<%= theChip.getEventID(GenericItemSearchListChip.FIRST_PAGE) %>");
				</script>
<%
			}
%>
		</td>
		<td>
			<%= getFooterButton(theChip.getEventID(GenericItemSearchListChip.PREVIOUS_PAGE),
												  localized("searchlist.previous"), localized("searchlist.previous.tooltip"),
												  "images/icons/footer_previous" + (enabled ? "" : "_inactive") + ".gif",
												  null, 
												  false, 
												  enabled) %>
<%
			if( enabled )
			{
%>
				<script language="JavaScript1.2">
						addKeyEvent(String.fromCharCode(33), "<%= theChip.getEventID(GenericItemSearchListChip.PREVIOUS_PAGE) %>");
				</script>
<%
			}
%>
		</td>
<%
	if( ranges > 0 )
	{
		final String event = theChip.getEventID(GenericItemSearchListChip.RANGESTART);

		String tooltip;
		String imageID;
		int value;
%>
		<td><input type="hidden" name="<%= event %>" value="" /></td>

<%		
		for( int i = startRange; i <= ranges && i <= (startRange + 10); i++ )
		{
%>
			<td>
<%
					if( currentRange == i )
					{
%>
						<div style="font-weight:bold; font-size:9pt; width:20px; text-align:center; vertical-align:middle; <%= isEditing ? "color:#888888;" : "" %>"><%= i %></div>
<%
					}
					else if( !isEditing )
					{
						tooltip = localized("searchlist.goto") + " " + i;	// i18n
						
						
						imageID = event + i + "_img";
						value = rangeCount * (i - 1);
%>			
							<a href="#" hidefocus="true" style="text-decoration:none;" alt="<%= tooltip %>"
								onMouseover="window.status='<%= tooltip %>'; document.getElementById('<%= imageID + "_bg_left" %>').style.backgroundImage = 'url(images/icons/footer_background_hover_l.gif)'; document.getElementById('<%= imageID + "_bg_middle" %>').style.backgroundImage = 'url(images/icons/footer_background_hover_m.gif)'; document.getElementById('<%= imageID + "_bg_right" %>').style.backgroundImage = 'url(images/icons/footer_background_hover_r.gif)'; return true;"
								onMouseout="window.status='<%= tooltip %>'; document.getElementById('<%= imageID + "_bg_left" %>').style.backgroundImage = 'url(images/icons/footer_background_l.gif)'; document.getElementById('<%= imageID + "_bg_middle" %>').style.backgroundImage = 'url(images/icons/footer_background_m.gif)'; document.getElementById('<%= imageID + "_bg_right" %>').style.backgroundImage = 'url(images/icons/footer_background_r.gif)'; return true;"
								onFocus="window.status='<%= tooltip %>'; document.getElementById('<%= imageID + "_bg_left" %>').style.backgroundImage = 'url(images/icons/footer_background_hover_l.gif)'; document.getElementById('<%= imageID + "_bg_middle" %>').style.backgroundImage = 'url(images/icons/footer_background_hover_m.gif)'; document.getElementById('<%= imageID + "_bg_right" %>').style.backgroundImage = 'url(images/icons/footer_background_hover_r.gif)'; return true;"
								onBlur="window.status='<%= tooltip %>'; document.getElementById('<%= imageID + "_bg_left" %>').style.backgroundImage = 'url(images/icons/footer_background_l.gif)'; document.getElementById('<%= imageID + "_bg_middle" %>').style.backgroundImage = 'url(images/icons/footer_background_m.gif)'; document.getElementById('<%= imageID + "_bg_right" %>').style.backgroundImage = 'url(images/icons/footer_background_r.gif)'; return true;"
								onclick="document.editorForm.elements['<%= event %>'].value = <%= value %>; setScrollAndSubmit(); return false;"
								>
					
								<table style="vertical-align:middle; height:23px;" cellpadding="0" cellspacing="0" border="0">
									<tr>
										<td id="<%= imageID + "_bg_left" %>" style="width:3px;vertical-align:middle;" background="images/icons/footer_background_l.gif">&nbsp;</td>
										<td id="<%= imageID + "_bg_middle" %>" style="white-space:nowrap; vertical-align:middle; text-align:center;" background="images/icons/footer_background_m.gif">		
											<div style="font-size:8pt; text-align:center; margin-top: 1px; width:<%= i<100 ? "10" : "18" %>px;" title="<%= tooltip %>"><%= i %></div>
										</td>
										<td id="<%= imageID + "_bg_right" %>" style="width:3px;vertical-align:middle;" background="images/icons/footer_background_r.gif">&nbsp;</td>
									</tr>
								</table>
							</a>
						</td>
<%
					}
					else
					{
%>
						<div class="disabled" style="height:23px; width:15px;"><%= i %></div>
<%
					}
%>
			</td>
<%
		}
	}
	enabled = (theChip.getRangeStart() + theChip.getListEntryCount()) < totalCount && !isEditing;
%>
		<td>
			<%= getFooterButton(theChip.getEventID(GenericItemSearchListChip.NEXT_PAGE),
												  localized("searchlist.next"), localized("searchlist.next.tooltip"),
												  "images/icons/footer_next" + (enabled ? "" : "_inactive") + ".gif",
												  null, 
												  false, 
												  enabled) %>
<%
			if( enabled )
			{
%>
				<script language="JavaScript1.2">
						addKeyEvent(String.fromCharCode(34), "<%= theChip.getEventID(GenericItemSearchListChip.NEXT_PAGE) %>");
				</script>
<%
			}
%>
		</td>
		<td>&nbsp;</td>
		<td>
			<%= getFooterButton(theChip.getEventID(GenericItemSearchListChip.LAST_PAGE),
												  localized("searchlist.last"), localized("searchlist.last.tooltip"),
												  "images/icons/footer_last" + (enabled ? "" : "_inactive") + ".gif",
												  null, 
												  false, 
												  enabled) %>
<%
			if( enabled )
			{
%>
				<script language="JavaScript1.2">
						addKeyEvent("L", "<%= theChip.getEventID(GenericItemSearchListChip.LAST_PAGE) %>");
				</script>
<%
			}
%>
		</td>
		<td align="right" valign="bottom" width="7px"><img src="images/editortab_corner_br.gif"/></td>
	</tr>
</table>

<% if( DEBUG_COMMENTS ) { %><!-- end: searchListChipRange.inc --><% } %>
