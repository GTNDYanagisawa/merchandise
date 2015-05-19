<%@include file="../../head.inc"%>
<%
	OrganizerListEntryChip theChip= (OrganizerListEntryChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<%
	if( !theChip.isElementReadable() )
	{
%>
			<td class="olecEntry" colspan="<%= theChip.getAttributeCount() + 3 %>"><%= localized("elementnotreadable") %></td>
<%
	}
	else
	{
%>
			<td class="<%=( theChip.isSelected() ? "selected " : "" )%>olecIsChangedSign" >
				<div <%= theChip.isChanged() ? ("title=\"" + localized("item.ischanged.tooltip") + "\"") : "" %>>
					<%= (theChip.isChanged() ? "*" : "&nbsp;") %>
				</div>
			</td>
			<td class="<%=( theChip.isSelected() ? "selected " : "" )%>olecIcon">
				<div class="button-on-white chip-event olecIcon">
					<a href="#" title="<%= localized("open_editor") %>" hidefocus="true"
						name="<%= theChip.getEventID(GenericItemListEntryChip.OPEN_EDITOR) %>">
						<span id="<%= theChip.getUniqueName() %>_span">
							<img class="icon" src="<%= theChip.getIcon() %>" id="<%= theChip.getUniqueName() %>_img"/>
						</span>
					</a>
				</div>
			</td>
<%
		for (final Iterator it = theChip.getChildNodes().iterator(); it.hasNext(); )
		{
			Node childNode = (Node) it.next();
			
			if( childNode instanceof AttributeNode )
			{
				String attributeName = ((AttributeNode) childNode).getAttributeQualifier();

				String width = "";
				if (theChip.getColumnWidths().get(attributeName) == null || !it.hasNext()) {
					// do nothing; 
				}
				else {
					String tmp = theChip.getColumnWidths().get(attributeName) + "";
					width = "style=\"width:" + (Integer.parseInt( tmp ) + 1 ) + "px;\"";
				}

				Chip editorChip = ((Chip) theChip.getEditorForCurrentLanguage(attributeName));
%>
					<td <%= width %>>
						<div class="olecEntry" <%= width %>>
<%						
							if( editorChip != null )
							{
								editorChip.render(pageContext);
							}
							else
							{
%>
								<%= localized("notdefined") %>
<%
							}
%>
						</div>
					</td>
<%
			}
			else if( childNode instanceof ItemNode )
			{
				final ItemNode itemNode = (ItemNode) childNode;

				final String width = (itemNode.getWidth() > 0 && it.hasNext()) ? " width:" + itemNode.getWidth() + "px;" : "";
%>
					<td style="<%= width %>">
						<div class="olecEntry" style="<%= width %>">
							<%= theChip.getItemAsString() %>
						</div>
					</td>
<%
			}
		}
	}
%>
