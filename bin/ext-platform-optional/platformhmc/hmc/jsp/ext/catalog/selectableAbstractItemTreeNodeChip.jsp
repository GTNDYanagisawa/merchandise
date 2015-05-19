<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.catalog.hmc.SelectableAbstractItemTreeNodeChip" %>

<%
	final SelectableAbstractItemTreeNodeChip theChip = (SelectableAbstractItemTreeNodeChip)request.getAttribute(AbstractChip.CHIP_KEY);
%>
<input type="hidden" name="<%=theChip.getEventID(AbstractTreeNodeChip.EDIT)%>" value="<%=AbstractChip.FALSE%>" />
<input type="hidden" name="<%=theChip.getEventID( SelectableAbstractItemTreeNodeChip.SELECTED )%>" value="<%= AbstractChip.FALSE %>" />
<table class="selectableAbstractItemTreeNodeChip" cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<input type="checkbox" <%= theChip.isSelected() ? "checked=\"checked\"" : "" %> <%= theChip.isReadOnly() ? "readonly=\"readonly\" disabled=\"disabled\"" : "" %> onclick="document.editorForm.elements['<%=theChip.getEventID( SelectableAbstractItemTreeNodeChip.SELECTED )%>'].value='<%= AbstractChip.TRUE %>';setScrollAndSubmit();"/>
		</td>
		<td class="satncIcon">
			<div onclick="document.editorForm.elements['<%=theChip.getEventID(AbstractTreeNodeChip.EDIT)%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();">
				<img src="<%=theChip.getIcon()%>">
			</div>
		</td>
		<td class="satncName">
			<div onclick="document.editorForm.elements['<%=theChip.getEventID(AbstractTreeNodeChip.EDIT)%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();">
				<%=theChip.getName()%>
			</div>
		</td>
	</tr>
	<%
		if( theChip.isExpanded() )
		{
			for( final Iterator it = theChip.getAllChildren().iterator(); it.hasNext(); )
			{
				final AbstractTreeNodeChip child = (AbstractTreeNodeChip)it.next();
				%>
					<tr>
						<td class="satncTreeIMG" background="<%=it.hasNext() ? "images/vert.gif" : ""%>">
							<%
								if( child.hasChildren() )
								{
									if( child.isExpanded() )
									{
										%><input type="hidden" name="<%=child.getEventID( AbstractTreeNodeChip.COLLAPSE )%>" value="<%=AbstractChip.FALSE%>" /><div onclick="document.editorForm.elements['<%=child.getEventID(AbstractTreeNodeChip.COLLAPSE )%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();"><%
										if (it.hasNext())
										{
											%><img src="images/minus.gif"></td><%
										}
										else
										{
											%><img src="images/minusend.gif"></td><%
										}
										%></div><%
									}
									else
									{
										%><input type="hidden" name="<%=child.getEventID(AbstractTreeNodeChip.EXPAND)%>" value="<%=AbstractChip.FALSE%>" /><div onclick="document.editorForm.elements['<%=child.getEventID(AbstractTreeNodeChip.EXPAND)%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();"><%
										if (it.hasNext())
										{
											%><img src="images/plus.gif"></td><%
										}
										else
										{
											%><img src="images/plusend.gif"></td><%
										}
										%></div><%
									}
								}
								else
								{
									if (it.hasNext())
									{
										%><img src="images/horiz.gif"></td><%
									}
									else
									{
										%><img src="images/end.gif"></td><%
									}
								}
							%>
						</td>
						<td>&nbsp;</td>
						<td class="satncContext"><% child.render(pageContext); %></td>
					</tr>
				<%
			}
		}
	%>
</table>
