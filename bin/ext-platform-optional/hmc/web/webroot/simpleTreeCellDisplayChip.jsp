<%@include file="head.inc"%>
<%
	SimpleTreeCellDisplayChip theChip = (SimpleTreeCellDisplayChip) request.getAttribute( AbstractChip.CHIP_KEY );

	TreeChip.TreeNodeChip nodeChip = theChip.getNodeChip();
	
	// context menu
	final List menuEntries = theChip.getMenuEntries();
	final String contextMenu = theChip.hasVisibleContextMenuEntries()
										? "(new Menu(" + theChip.createMenuEntriesForJS(menuEntries) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
										: "return false;";
	
	final String name = theChip.getName();
	final String tooltip = theChip.getTooltip();
	final String icon = theChip.getIcon();
%>
	<div class="simpleTreeCellDisplayChip"
	     oncontextmenu="<%= contextMenu %>"
        onclick="document.editorForm.elements['<%= nodeChip.getEventID( TreeChip.TreeNodeChip.SET_CURRENT_PATH ) %>'].value='<%= AbstractChip.TRUE %>'; setScrollAndSubmit();"
        onMouseOver="window.status='<%= name %>';" 
        onMouseOut="window.status='';"
        <%= tooltip != null ? "title=\"" + tooltip + "\"" : "" %>>
		<input type="hidden" name="<%= nodeChip.getEventID( TreeChip.TreeNodeChip.SET_CURRENT_PATH )%>" value="<%= AbstractChip.FALSE %>" />
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td class="stcdcIcon">
					<img src="<%= icon %>"/>
				</td>
				<td class="stcdcName">
					<%= name %>
				</td>
			</tr>
		</table>
	</div>
