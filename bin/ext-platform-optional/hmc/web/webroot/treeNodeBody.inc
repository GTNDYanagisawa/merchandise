<%
	final String contextMenu = theChip.hasVisibleContextMenuEntries() 
										? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
										: "return false;";
	final String name = theChip.getName();
											
	if( theChip.isActive() )
	{
%>
		<tr title="<%= name %>"
			 id="<%= theChip.getUniqueName() %>_tr"
			 oncontextmenu="<%= contextMenu %>" 
			 onclick="setEvent('<%= theChip.getEventID(AbstractTreeNodeChip.EDIT) %>'); setScrollAndSubmit(); return false;" 
		>
			<td>
				<img src="<%= theChip.getIcon() %>" border="0" alt="<%= name %>" id="<%= theChip.getUniqueName() %>_img">
			</td>
			<td>
				<a href="#" class="label<%= (theChip.isSelected() ? " selected" : "") %>" id="<%= theChip.getUniqueName() %>_label">
					<%= name %>
				</a>
			</td>
		</tr>
<%
	}
	else
	{
		out.println("SESSION TIMEOUT " + name);
	}
%>
