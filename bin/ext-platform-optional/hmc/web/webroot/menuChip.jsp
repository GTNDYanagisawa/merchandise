<%@include file="head.inc"%>
<%
	final MenuChip theChip = (MenuChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<div style="width:<%= StructureLoader.getExplorerTreeWidth() %>px; overflow:hidden;">
	<table class="explorer tree" cellspacing="0" cellpadding="0" border="0">
		<tr>
			<td valign="top"><img src="images/icons/n_session.gif"></td>
			<td style="text-indent:5px;">
				<a href="#" <%= theChip.isHomeSelected() ? "class=\"selected\"" : "" %>
					onclick="setEvent('<%= theChip.getEventID(MenuChip.HOME) %>'); setScrollAndSubmit(); return false;"
					onMouseOver="window.status='<%= localized("explorer.link.root") %>'; return true;" onMouseOut="window.status=''; return true;"
					hidefocus="true"
					style="width:30px; white-space:nowrap; padding-right:5px; vertical-align:middle;"
					id="<%= theChip.getUniqueName() %>_username">
					<%= theChip.getUserName() %>
				</a>
			</td>
		</tr>
<%
		for( Iterator it = theChip.getRootChips().iterator(); it.hasNext(); )
		{
			AbstractExplorerMenuTreeNodeChip rootChip = (AbstractExplorerMenuTreeNodeChip)it.next();
%>
			<tr>
				<td <%= it.hasNext() ? "class=\"vertical\"" : "" %>>
<%
					if( rootChip.hasChildren() )
					{
						final String cssClass = (rootChip.isExpanded() ? "minus" : "plus") + (it.hasNext() ? "" : "-end");
						final String tenantIDStr = Registry.getCurrentTenant() instanceof SlaveTenant ?
							";tenantID="+Registry.getCurrentTenant().getTenantID() : "";
%>
							<div class="tree-toggle" >
								<div class="chip-id"><%= rootChip.getID() %></div>
								<div class="tenantIDStr"><%=tenantIDStr%></div>
								<div class="icon <%= cssClass %>" id="<%= rootChip.getUniqueName() %>_treeicon" onclick=""></div>	<%-- mr: empty onclick handler is necessary for selenium recordings!!!! --%>
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
					<div id="<%= rootChip.getID() %>_updater">
<%
						rootChip.render(pageContext);
%>
					</div>
				</td>
			</tr>
<%
		}
%>
	</table>
</div>
