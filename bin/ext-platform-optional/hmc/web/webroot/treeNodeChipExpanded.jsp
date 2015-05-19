<%@include file="head.inc"%>
<%
	final AbstractTreeNodeChip theChip = (AbstractTreeNodeChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<table class="explorer tree" cellspacing="0" cellpadding="0" border="0">
<%@include file="treeNodeBody.inc"%>
<%
	for( final Iterator it = theChip.getAllChildren().iterator(); it.hasNext(); )	
	{
		AbstractTreeNodeChip child = (AbstractTreeNodeChip) it.next();
%>
		<tr>
			<td <%= it.hasNext() ? "class=\"vertical\"" : ( child.hasChildren() ? "class=\"toggle-top\"" : "") %>>
<%
				if( child.hasChildren() )
				{
					final String cssClass = (child.isExpanded() ? "minus" : "plus") + (it.hasNext() ? "" : "-end");
					final String tenantIDStr = Registry.getCurrentTenant() instanceof SlaveTenant ?
						";tenantID="+Registry.getCurrentTenant().getTenantID() : "";
%>
						<div class="tree-toggle">
							<div class="chip-id"><%= child.getID() %></div>
							<div class="tenantIDStr"><%=tenantIDStr%></div>
							<div class="icon <%= cssClass %>" id="<%= child.getUniqueName() %>_treeicon" onclick=""></div>	<%-- mr: empty onclick handler is necessary for selenium recordings!!!! --%>
						</div>
<%
				}
				else
				{
%>
					<div class="<%= it.hasNext() ? "horizontal" : "end" %>"></div>
<%
				}
%>
			</td>
			<td>
				<div id="<%= child.getID() %>_updater">
<%
					child.render(pageContext);
%>
				</div>
			</td>
		</tr>
<%
	}
%>
</table>
