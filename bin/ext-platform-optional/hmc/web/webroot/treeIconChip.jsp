<%@include file="head.inc" %>
<%
	final IconChip theChip = (IconChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final Object element = theChip.getElement();
	final IconViewModel model = theChip.getModel();
	final boolean explorable = model.isExplorable(element);
	final String iconURI = model.getIconURI(element);
	final String iconLabel = model.getIconTitle(element);
	final String iconTooltip = model.getIconTooltip(element);
	final String description = model.getDescription(element) != null ? model.getDescription(element) : "";
	final String titleStr = iconTooltip != null ? " title=\""+iconTooltip+"\" alt=\""+iconTooltip+"\" " : null;

	// how many columns in which the icons will be displayed
	final int columns = model.getNumberOfColumns() > 0 ? model.getNumberOfColumns() : 3;	// defaults to 3 columns

	// the max. width of the organizer window is assumed to be 720; this is related to a total window size of 1024x768
	// this value also can be devided by 2, 3, 4, 5 and 6; so you can use it up to 6 columns
	final int boxWidth = 720 / columns;
%>
<td class="treeIconChip">
	<table cellspacing="0" cellpadding="0"
<%	if( explorable )
	{ %>
		onclick="setEvent('<%=theChip.getCommandID(IconChip.CLICK)%>','true');setScrollAndSubmit();return false;"
		onMouseover="this.style.backgroundColor = '#f2f2f5'; window.status='open'; return true;" 
		onMouseout="this.style.backgroundColor = '#ffffff'; window.status=''; return true;"
<% }
%>
	>
		<tr>
			<td class="middle">
				<div style="width:<%= boxWidth %>">
					<table class="ticContent" cellpadding="0" cellspacing="0">
						<tr>
							<td class="ticIcon">
		<!-- icon -->
								<div>
									<img src="<%= iconURI %>" <%= titleStr %> id="<%= theChip.getUniqueName() %>_img"/>
								</div>
							</td>
							<td class="ticTitle" id="<%= theChip.getUniqueName() %>_td">
								<div id="<%= theChip.getUniqueName() %>_div">
		<!-- title -->
									<%= iconLabel %>
								</div>
		<!-- description -->
								<%= description %>
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
</td>
