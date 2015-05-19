<%@include file="../head.inc"%>

<%
	EditableItemListEntryChip theChip= (EditableItemListEntryChip) request.getAttribute(AbstractChip.CHIP_KEY);
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
		<td>
			<table class="gilcIcon" cellpadding="0" cellspacing="0">
				<tr>
					<td class="eilecIsChangedSign">
						<div>
							<%= theChip.isChanged() ? "*" : "" %>
						</div>
					</td>
					<td class="eilecIcon">
							<div <%= prohibitOpening ? "" : "class=\"button-on-white chip-event\"" %>>
<% 
								if( !prohibitOpening )
								{
%>
									<a href="#" title="<%= localized("open_editor") %>" hidefocus="true"
										name="<%= theChip.getEventID(GenericItemListEntryChip.OPEN_EDITOR) %>">
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


							<%-- getIconButton(theChip.getEventID(GenericItemListEntryChip.OPEN_EDITOR), 
														localized("open_editor"), 
														theChip.getIcon(), 
														null, 
														false,
														true) --%>
					</td>											
				</tr>
			</table>
		</td>
<%
		boolean isInModalSearchList = (theChip.getParent() instanceof ModalGenericItemSearchListChip);

		for (final Iterator it = theChip.getChildNodes().iterator(); it.hasNext(); )
		{
			final Node childNode = (Node) it.next();
			String width = "";
			
			if( childNode instanceof AttributeNode && ((AttributeNode) childNode).getActualColumnWidth() != 0 && it.hasNext() )
			{
				width = "width:" + ((AttributeNode) childNode).getActualColumnWidth() + "px; ";
			}
			else if( childNode instanceof ItemNode && ((ItemNode) childNode).getWidth() != 0 && it.hasNext())
			{
				width = " width:" + ((ItemNode) childNode).getWidth() + "px;";
			}
%>
			<td style="<%= width %><%= !it.hasNext() ? "border-right:1px solid #bbbbbb;" : "" %>">
				<div class="gilcEntry" style="<%= width %>">
<%
				if( childNode instanceof AttributeNode && theChip.getAttributeEditors().containsKey(((AttributeNode) childNode).getAttributeQualifier()) )
				{
					theChip.getEditorForCurrentLanguage(((AttributeNode) childNode).getAttributeQualifier()).render(pageContext);
				}
				else if( childNode instanceof ItemNode )
				{
%>
					<%= theChip.getItemAsString() %>
<%
				}
%>			
				</div>
			</td>
<%
		}
	}
%>
