<%@include file="head.inc"%>
<%
	final ItemHistoryChip theChip = (ItemHistoryChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String chipID = theChip.getID();
	final String event = theChip.getCommandID(ItemHistoryChip.OPEN_ITEM_INTERNAL);	
%>
<div class="itemhistory " style="position:relative;top:-145px;left:3;" id="<%= chipID %>_drag">
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<th><div class="title"><%= localized("item.history.title") %></div></th>
		</tr>
<%
		for( final Map.Entry<String, ItemHistoryChip.Entry> itemEntry : theChip.getItemEntries().entrySet() )
		{
			final String pk = itemEntry.getKey();
			final String label = itemEntry.getValue().getLabel();
			final String icon = itemEntry.getValue().getIcon();
			final String contextMenu = theChip.getContextMenu(pk);
%>
			<tr oncontextmenu="<%= contextMenu %>">
				<td onMouseover="this.className = 'highlight';" onMouseout="this.className='';">
					<div class="chip-event entry">
						<a href="#"	name="<%= event %>" style="width:100%;" id="<%= theChip.getUniqueName() + "[" + label + "]"%>_a" hidefocus="true">
							<span class="event-value"><%= pk %></span>
							<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td style="width:23px;" id="<%= theChip.getUniqueName() + "[" + label + "]"%>_td1">
										<img src="<%= icon %>" id="<%= theChip.getUniqueName() + "[" + label + "]"%>_img"/>
									</td>
									<td id="<%= theChip.getUniqueName() + "[" + label + "]"%>_td2">
										<%= label %>
									</td>
								</tr>
							</table>
						</a>
					</div>
				</td>
			</tr>
<%
		}
%>
	</table>
</div>
<!-- script language="JavaScript1.2">

		$('<%= chipID %>_drag').style.top = 0;
		$('<%= chipID %>_drag').style.left = 0;

<%
	final String tenantIDStr = Registry.getCurrentTenant() instanceof SlaveTenant ? ";tenantID="+Registry.getCurrentTenant().getTenantID() : "";
%>
	new Draggable('<%= chipID %>_drag', 
					 	{ starteffect: '', 
						  endeffect: '', 
						  handle: 'title',
						  revert: function() { y_ajaxNotifyPosition($('<%= chipID %>_drag'), '<%= chipID %>', '<%=tenantIDStr%>'); } });
	
	if( <%= theChip.getTop() %> > -10000 )
	{
		// there are valid coordinates
		var dragElement = $('<%= chipID %>_drag');
		dragElement.style.top = <%= theChip.getTop() %>;
		dragElement.style.left = <%= theChip.getLeft() %>;
		dragElement.removeClassName("hidden");
	}
	else
	{
		// history chip is shown for the first time, try to position it at the bottom of the explorer tree (after the page was loaded) and send these coordinates back to the server
		Behaviour.addLoadEvent(
			function() { y_repositionElement($('<%= chipID %>_drag'), $('end_of_explorertree'), 3, -138); y_ajaxNotifyPosition($('<%= chipID %>_drag'), '<%= chipID %>', '<%=tenantIDStr%>'); }
		);
	}
	
</script>
-->
