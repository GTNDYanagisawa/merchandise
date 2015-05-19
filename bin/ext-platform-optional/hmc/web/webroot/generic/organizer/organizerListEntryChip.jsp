<%@include file="../../head.inc"%>
<%
	OrganizerListEntryChip theChip= (OrganizerListEntryChip) request.getAttribute(AbstractChip.CHIP_KEY);
	String itemChangedTooltip = " title=\"" + localized("item.ischanged.tooltip") + "\" ";
	
	if( !theChip.isElementReadable() )
	{
%>
			<td colspan="<%= theChip.getAttributeCount()+2 %>"><%= localized("elementnotreadable") %></td>
<%
	}
	else
	{
%>
		<!-- isChangedSign -->
		<td class="olecIsChangedSign" <%= (theChip.isChanged() ? itemChangedTooltip : "") %> id="<%= theChip.getUniqueName() %>_td1">
			<div id="<%= theChip.getUniqueName() %>_div1">
				<%= (theChip.isChanged() ? "*" : "&nbsp;") %>
			</div>
		</td>
		
		<!-- icon -->
		<td class="olecIcon" id="<%= theChip.getUniqueName() %>_td2">
			<div class="olecIcon" >
				<div class="button-on-white chip-event">
					<a href="#" title="<%= localized("open_editor") %>" hidefocus="true"
						name="<%= theChip.getCommandID(GenericItemListEntryChip.OPEN_EDITOR) %>">
						<span id="<%= theChip.getUniqueName() %>_span">
							<img class="icon" src="<%= theChip.getIcon() %>" id="<%= theChip.getUniqueName() %>_img"/>
						</span>
					</a>
				</div>
			</div>
		</td>
<%
		String sortQualifier = ((OrganizerListChip) theChip.getParent()).getSortAttributeQualifier();
		
		for (final Iterator it = theChip.getChildNodes().iterator(); it.hasNext(); )
		{
			final Node childNode = (Node) it.next();
			
			if( childNode instanceof AttributeNode )
			{
				AttributeNode attributeNode = (AttributeNode) childNode;
				AbstractAttributeDisplayChip displayChip = (AbstractAttributeDisplayChip) theChip.getAttributeDisplayChips().get(attributeNode.getAttributeQualifier());
				
				String width = ( attributeNode.getWidth() == null || !it.hasNext() ) ? "" : " style=\"width: " + ( Integer.parseInt( attributeNode.getWidth() ) + 1 ) + "px; overflow: hidden;\" ";
				
				String tdClass = "";
				tdClass = tdClass + ( attributeNode.getAttributeQualifier().equals(sortQualifier) ? "sorted" : "" );
				tdClass = tdClass != null ? " class=\"" + tdClass + "\"" : "";
				
				String linkStyle = displayChip != null ? displayChip.getLinkStyle() : null;
				if( (linkStyle == null) || linkStyle.equals("") )
				{
					linkStyle = "class = \"normallink\" ";
				}
%>
				<td<%= tdClass %><%= width %> id="<%= displayChip != null ? displayChip.getUniqueName() : theChip.getUniqueName() + "_item" %>_td3">
					<div class="olecEntry" <%= width %> id="<%= displayChip != null ? displayChip.getUniqueName() : theChip.getUniqueName() + "_item" %>_div2">
<%
					if( displayChip != null && !(theChip.getListItemType() instanceof ViewType) && !displayChip.isDefaultLink() )
					{
						final String displayChipTarget = displayChip.getLinkTarget();
						final String target;
						if( displayChipTarget != null )
						{
						target = "target=\"" + displayChipTarget + "\"";
						}
						else
						{
						target = "";
						}
%>
						<a href="<%= displayChip.getLinkUrl() %>"
						onMouseover="window.status='<%= localized( displayChip.getLinkTitle() ) %>'; return true;"
						onMouseout="window.status='';return true;"
						title="<%= localized( displayChip.getLinkTitle() ) %>"
						hidefocus="true" style="white-space: nowrap;" <%= linkStyle %> <%= target %>
						id="<%= displayChip.getUniqueName() %>_a">
<%
					}
					if( displayChip != null )
					{
						displayChip.render( pageContext ); 
					}
					else
					{
%>
						<%= localized("notdefined") %>
<%
					}
					if( displayChip != null && !(theChip.getListItemType() instanceof ViewType) && !displayChip.isDefaultLink() )
					{
%>
						</a>
<%
					}
%>
					</div>
				</td>
<%
			}
			else if( childNode instanceof ItemNode )
			{
				final String width = (it.hasNext() && ((ItemNode) childNode).getWidth() > 0) ? "width:" + (((ItemNode) childNode).getWidth()+1) + "px;" : "";
				final String itemTitle = ((ItemNode) childNode).getTitle();

				String tdClass = "";
				tdClass = tdClass != null ? " class=\"" + tdClass + "\"" : "";				
%>
				<td<%= tdClass %> style="<%= width %>">
					<div class="olecEntry" style="<%= width %>;">
						<%= theChip.getItemAsString() %>
					</div>
				</td>
<%
			}
		}
	}
%>
