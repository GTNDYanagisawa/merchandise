<%@include file="../head.inc"%>
<%
	final ItemTreeViewChip theChip = (ItemTreeViewChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final String tenantIDStr = Registry.getCurrentTenant() instanceof SlaveTenant ?
		";tenantID="+Registry.getCurrentTenant().getTenantID() : "";
%>
<div id="<%= theChip.getID() %>_updater" class="update-parent">
<table class="tree" cellspacing="0" cellpadding="0">
	<tr>
<%
		if( theChip.isRoot() )
		{
%>
			<td rowspan="2" <%= theChip.isLast() ? "" : "class=\"vertical\"" %>>
<%
				if( theChip.hasChildren() )
				{
					final String cssClass = (theChip.isExpanded() ? "minus" : "plus") + (theChip.isLast() ? "-end" : "");
%>
						<div class="tree-toggle">
							<div class="chip-id"><%= theChip.getID() %></div>
							<div class="tenantIDStr"><%=tenantIDStr%></div>
							<div class="icon <%= cssClass %>"></div>
						</div>
<%
				}
				else
				{
%>
					<div class="<%= theChip.isLast() ? "end" : "horizontal" %>"></div>
<%
				}
%>
			</td>
<%
		}
%>
		<td <%= !theChip.isRoot() ? "colspan=\"2\"" : "" %>>
			<a href="#" hidefocus="true" onclick="setEvent('<%= theChip.getCommandID(ItemTreeViewChip.OPEN) %>'); setScrollAndSubmit(); return false;">
				<img border="0" src="<%= theChip.getIcon() %>">&nbsp;<%= theChip.getLabel() %>
			</a>
		</td>
	</tr>
<%
	if( theChip.isExpanded() ) 
	{
%>
	<tr>
		<td>
			<table cellspacing="0" cellpadding="0">
<% 
				for( Iterator it = theChip.getChildren().iterator(); it.hasNext(); )
				{
					final TreeViewChip node = (TreeViewChip) it.next();
					
					if( node instanceof ItemTreeViewChip && !((ItemTreeViewChip) node).getItem().isAlive() )
					{
						it.remove();
						continue;
					}
					
					if( node instanceof CollectionTreeViewChip )
					{
%>
						<tr>
							<td <%= it.hasNext() ? "class=\"vertical\"" : "" %>>
<%
								if( node.hasChildren() )
								{
									final String cssClass = (node.isExpanded() ? "minus" : "plus") + (node.isLast() ? "-end" : "");
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
							<td style="vertical-align:middle; padding-left:3px;">
								<%= node.getAttributeLabel()%>
							</td>
						</tr>
						<tr>
							<td class="<%= it.hasNext() ? "vertical" : "" %>" style="height:0px;" >
							</td>
							<td  id="<%= node.getID() %>_updater" style="height:0px;">
								<% node.render( pageContext ); %>
							</td>
						</tr>
<%
					}
					else
					{
%>
						<tr>
							<td <%= it.hasNext() ? "class=\"vertical\"" : "" %>>
<%
								if( node.hasChildren() )
								{
									final String cssClass = (node.isExpanded() ? "minus" : "plus") + (node.isLast() ? "-end" : "");
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
							<td>
								<% node.render( pageContext ); %>
							</td>
						</tr>
<%		
			
					}
				}
%>
			</table>
		</td>
	</tr>
<%
	}
%>
</table>
</div>
