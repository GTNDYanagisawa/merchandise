<%@include file="head.inc" %>

<%
	final IconChip theChip = (IconChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final boolean explorable = theChip.isExplorable();
	final String iconURI = explorable ? theChip.getIconURI() : theChip.getNonExplorableIconURI();
	final String iconLabel = theChip.getIconTitle();
	final String iconTooltip = theChip.getIconTooltip();
	final String altTxt = iconTooltip != null ? " alt=\""+iconTooltip.replace("\"", "''")+"\" " : "";
	final String toolTipTxt = iconTooltip != null ? " title=\""+iconTooltip.replace("\"", "''")+"\" " : "";

	final int[] box = theChip.getBoxSize();
	final int boxWidth = box != null && box[0] > 0 ? box[0] : -1;
	final boolean useWidth = boxWidth > 0;
	final int boxHeight = box != null && box[1] > 0 ? box[1] : -1;
	final boolean useHeight = boxHeight > 0;

	final List menuEntries = theChip.getMenuEntries();
	final String contextMenu = theChip.hasVisibleContextMenuEntries()
				? "(new Menu(" + theChip.createMenuEntriesForJS(menuEntries) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
				: "return false;";
				
	final String widthStyle = useWidth ? "width:" + boxWidth + "px;" : "";
	final String heightStyle = useHeight ? "height:" + (boxHeight/2) + "px;" : "";
%>
<table id="box_<%=theChip.getID()%>"
		 cellpadding="0"
		 cellspacing="0"
		 border="0"
		 <%=toolTipTxt%>
		 oncontextmenu="<%= contextMenu %>"
		 class="iconChip"
  		 onMouseover="<%= explorable ? "this.style.backgroundColor = '#ffffff'; return true;":""%>"
  		 onMouseout="<%= explorable ? "this.style.backgroundColor = '#f2f2f5'; return true;" : ""%>"
  		 >
	<tr>
		<td>
			<div style="<%= heightStyle %> <%= widthStyle %>">
				<table cellpadding="0" cellspacing="0">
					<tr>
						<td>
<%							if( explorable )
							{ 
%>
								<a href="#" hidefocus="true"
									onFocus="document.getElementById('box_<%=theChip.getID()%>').style.backgroundColor = '#ffffff'; return true;"
									onblur="document.getElementById('box_<%=theChip.getID()%>').style.backgroundColor = '#f2f2f5'; return true;"
									onclick="setEvent('<%=theChip.getCommandID( IconChip.CLICK )%>', 'true' ); setScrollAndSubmit();return false;"
									onmouseover="window.status='open'; return true;" 
									onMouseOut="window.status=''; return true;"
								>
									<img src="<%= iconURI %>" 
										  <%= altTxt %> 
										  <%= heightStyle %>
									/>
									<!--<img src="<%= iconURI %>" <%= altTxt %> />-->
								</a>
<%							}
							else
							{
%>
								<img src="<%= iconURI %>" 
									  <%= altTxt %> 
									  <%= heightStyle %>
								/>
								<!--<img src="<%= iconURI %>" <%= altTxt %> />-->
<%							}
%>
						</td>
					</tr>
				</table>
			</div>
		</td>
	</tr>
	<tr>
		<td class="smallText" style="<%= heightStyle %><%= widthStyle %>">
			<small><%= iconLabel %></small>
		</td>
	</tr>
</table>
