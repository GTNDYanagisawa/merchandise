<%@include file="../head.inc"%>
<%@page import="de.hybris.platform.jalo.Item"%>
<%@page import="de.hybris.platform.jalo.type.*"%>
<%@page import="de.hybris.platform.jalo.type.AttributeDescriptor"%>
<%
	final CollectionTreeViewChip theChip = (CollectionTreeViewChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>	
<% 
	if( theChip.isExpanded() ) 
	{
		for( Iterator it = theChip.getChildren().iterator(); it.hasNext(); )
		{
			final TreeViewChip node = (TreeViewChip)it.next();

			
/*
			String image;
			String url;
			String bg = "";
			if( node.getChildren().isEmpty() )
			{
				url = null;
				if( it.hasNext() )
				{
					image = "<img src=\"images/horiz.gif\">";
				}
				else
				{
					image = "<img src=\"images/end.gif\">";
				}
			}		
			else
			{
				if( node.isExpanded() )
				{
					url = getRequestURL() + "&" + node.getCommandID(TreeViewChip.COLLAPSE) + "=" + AbstractChip.TRUE;
					if( it.hasNext() )
					{
						image = "<img src=\"images/minus.gif\">";
					}
					else
					{
						image = "<img src=\"images/minusend.gif\">";
					}
				}
				else
				{
					url = getRequestURL() + "&" + node.getCommandID(TreeViewChip.EXPAND) + "=" + AbstractChip.TRUE;
					if( it.hasNext() )
					{
						image = "<img src=\"images/plus.gif\">";
					}
					else
					{
						image = "<img src=\"images/plusend.gif\">";
					}
				}
			}
			if( it.hasNext() )
			{
				bg = "background=\"images/vert.gif\"";
			}

*/			
%>	
			<table class="collectionTreeViewChip" cellspacing="0" cellpadding="0">
				<tr>
					<td <%= it.hasNext() ? "class=\"vertical\"" : "" %>>
<%
						if( node.hasChildren() )
						{
			
							final String cssClass = (node.isExpanded() ? "minus" : "plus") + (node.isLast() ? "-end" : "");
							final String tenantIDStr = Registry.getCurrentTenant() instanceof SlaveTenant ?
								";tenantID="+Registry.getCurrentTenant().getTenantID() : "";
%>
								<div class="tree-toggle">
									<div class="chip-id"><%= node.getID() %></div>
									<div class="tenantIDStr"><%=tenantIDStr%></div>
									<div class="icon <%= cssClass %>"></div>				
								</div>
<%
						}
						else
						{
%>
								<div class="<%= node.isLast() ? "end" : "horizontal" %>"></div>
<%
						}
%>
					</td>
<%-- 					<td <%= bg %>><%= getLinkedLabel( url, image ) %></td>
--%>
					<td>
						<% node.render( pageContext ); %>
					</td>
				</tr>
			</table>
<%		
		}
	}
%>							
