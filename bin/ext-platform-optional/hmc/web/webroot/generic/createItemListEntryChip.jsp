<%@include file="../head.inc"%>

<%
	CreateItemListEntryChip theChip= (CreateItemListEntryChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>	
	<td class="cilecIcon">
		<table cellpadding="0" cellspacing="0">
			<tr>
				<td style="first">
					<div>&nbsp;</div>
				</td>
				<td class="second">
					<div>
						<img src="<%= theChip.getIcon() %>">
					</div>
				</td>											
			</tr>
		</table>
	</td>
<%
	for( final Iterator it = theChip.getChildNodes().iterator(); it.hasNext(); )
	{
		final Node childNode = (Node) it.next();
		String width = "";
		
		if( childNode instanceof AttributeNode && ((AttributeNode) childNode).getActualColumnWidth() != 0 && it.hasNext() )
		{
			width = "width:" + ((AttributeNode) childNode).getColumnWidth() + "px;";
		}
		else if( childNode instanceof ItemNode && ((ItemNode) childNode).getWidth() != 0 && it.hasNext())
		{
			width = "width:" + ((ItemNode) childNode).getWidth() + "px;";
		}
%>
		<td class="cilecEntry" style="<%= width %> <%= !it.hasNext() ? "border-right:1px solid #bbbbbb;" : "" %>">
			<table cellspacing="0" cellpadding="0" style="border:0px;<%= width %>">
				<tr>
					<td>
						<div>
<%
							if( childNode instanceof AttributeNode && theChip.getAttributeEditors().containsKey(((AttributeNode) childNode).getAttributeQualifier()) )
							{
								theChip.getEditorForCurrentLanguage(((AttributeNode) childNode).getAttributeQualifier()).render(pageContext);
							}
							else if( childNode instanceof ItemNode )
							{
%>
								-
<%
							}
%>			
						</div>
					</td>
<%
					if( !it.hasNext() )
					{
%>
						<td class="lastEntry">
							<div>
								<%= getIconButton(theChip.getParent().getCommandID(GenericItemListChip.DELETE_NEW_ENTRY), 
															localized("list.new.entry.delete"), 
															GenericItemListChip.DELETE_ICON,
															null, 
															false,
															true) %>
							</div>
						</td>
<%
					}
%>
				</tr>
			</table>
		</td>
<%
	}
%>
