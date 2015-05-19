<%@include file="../head.inc"%>
<%
	GenericItemListEntryChip theChip= (GenericItemListEntryChip) request.getAttribute(AbstractChip.CHIP_KEY);
	boolean isEditable = true;
	boolean prohibitOpening = false;
	if( theChip.getParent() instanceof GenericItemListChip )
	{
		isEditable = ((GenericItemListChip) theChip.getParent()).isEditable();
		prohibitOpening = ((GenericItemListChip) theChip.getParent()).isProhibitOpening();
	}
	
	if( !theChip.isElementReadable() )
	{
%>		
		<td colspan="<%= theChip.getAttributeCount() + 2 %>">
			<%= localized("elementnotreadable") %>
		</td>
<%
	}
	else
	{
%>		
		<!-- gilecIcon -->
		<td class="gilcIcon" id="<%= theChip.getUniqueName() %>_td1">
			<div class="gilcIcon">
				<div <%= prohibitOpening ? "" : "class=\"button-on-white chip-event\"" %>>
<% 
					if( !prohibitOpening )
					{
%>
						<a href="#" title="<%= localized("open_editor") %>" hidefocus="true"
							name="<%= theChip.getCommandID(GenericItemListEntryChip.OPEN_EDITOR) %>">
<%
					}
%>
							<span id="<%= theChip.getUniqueName() %>_span">
								<img class="icon" src="<%= theChip.getIcon() %>" id="<%= theChip.getUniqueName() %>_img"/>
							</span>
<% 
					if( !prohibitOpening )
					{
%>
						</a>
<%
					}
%>
				</div>
			</div>
		</td>
<%
		boolean isInModalSearchList = (theChip.getParent() instanceof ModalGenericItemSearchListChip);

		String sortQualifier = null;
		if( theChip.getParent() instanceof GenericItemSearchListChip )
		{
			sortQualifier = ((GenericItemSearchListChip) theChip.getParent()).getSortAttributeQualifier();
		}

		for (final Iterator it = theChip.getChildNodes().iterator(); it.hasNext(); )
		{
			final Node childNode = (Node) it.next();
			String width = "";
			boolean sorted = false;
			AbstractAttributeDisplayChip displayChip = null;
			
			
			if( childNode instanceof AttributeNode )
			{
				displayChip = (AbstractAttributeDisplayChip) theChip.getAttributeDisplayChips().get(((AttributeNode) childNode).getAttributeQualifier());

				if( ((AttributeNode) childNode).getColumnWidth() != 0 && it.hasNext() )
				{
					width = " width:" + (((AttributeNode) childNode).getColumnWidth() + 1) + "px;";
				}
				sorted = ((AttributeNode) childNode).getAttributeQualifier().equals(sortQualifier);
			}
			else if( childNode instanceof ItemNode && ((ItemNode) childNode).getWidth() != 0 && it.hasNext())
			{
				width = " width:" + (((ItemNode) childNode).getWidth() + 1) + "px;";
			}
				
%>
			<!-- gilecEntry -->
			<td <%= sorted ? "class=\"sorted\"" : "" %> style="<%= width %><%= !it.hasNext() ? " border-right: 1px solid #bbbbbb;" : "" %>" >
				<div class="gilcEntry" style="<%= width %>" id="<%= displayChip != null ? displayChip.getUniqueName() : theChip.getUniqueName() + "_item" %>_div">
<%
					if( !isInModalSearchList && displayChip != null && !(theChip.getListItemType() instanceof ViewType) && !displayChip.isDefaultLink() )
					{
						final String linkStyle = displayChip.getLinkStyle();
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
							hidefocus="true" style="white-space: nowrap;" <%= linkStyle %> <%= target %>>
<%
							displayChip.render(pageContext);
%>
						</a>
<%
					}
					else
					{
						if( displayChip != null )
						{
							displayChip.render(pageContext);
						}
						else if( childNode instanceof ItemNode ) 
						{
%>
							<%= theChip.getItemAsString() %>
<%
						}
						else
						{
%>
							<%= localized("notdefined") %>
<%
						}
					}
%>
				</div>
			</td>
<%
		}
	}
%>
