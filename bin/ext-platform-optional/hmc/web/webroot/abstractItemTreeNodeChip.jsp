<%@include file="head.inc"%>
<%
	AbstractItemTreeNodeChip theChip = (AbstractItemTreeNodeChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<input type="hidden" name="<%=theChip.getEventID(AbstractTreeNodeChip.EDIT)%>" value="<%=AbstractChip.FALSE%>" />
<table cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td valign="middle">
			<div 
			<%-- oncontextmenu="setMenu(<%=createMenuEntries(theChip)%>, event);return false;" --%>
				onclick="document.editorForm.elements['<%=theChip.getEventID(AbstractTreeNodeChip.EDIT)%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();">
				<img src="<%=theChip.getIcon()%>" border="0">
			</div>
		</td>
		<td class="item" valign="middle" align="left">
			<div 
					<%-- oncontextmenu="setMenu(<%=createMenuEntries(theChip)%>, event);return false;" --%>
				onclick="document.editorForm.elements['<%=theChip.getEventID(AbstractTreeNodeChip.EDIT)%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();"><%=theChip.getName()%>
			</div>

		</td>
	</tr>
	<%
		if( theChip.isExpanded() )
		{
			for (final Iterator it = theChip.getAllChildren().iterator(); it.hasNext(); )
			{
				final AbstractTreeNodeChip child = (AbstractTreeNodeChip)it.next();
				%>
					<tr>
						<td background="<%=it.hasNext() ? "images/vert.gif" : ""%>" valign="top" align="left">
							<%
								if (child.hasChildren())
								{
									if (child.isExpanded())
									{
										%><input type="hidden" name="<%=theChip.getEventID(AbstractTreeNodeChip.COLLAPSE )%>" value="<%=AbstractChip.FALSE%>" /><div onclick="document.editorForm.elements['<%=theChip.getEventID(AbstractTreeNodeChip.COLLAPSE )%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();"><%
										if (it.hasNext())
										{
											%><img src="images/minus.gif" border="0"></td><%
										}
										else
										{
											%><img src="images/minusend.gif" border="0"></td><%
										}
										%></div><%
									}
									else
									{
										%><input type="hidden" name="<%=theChip.getEventID(AbstractTreeNodeChip.EXPAND)%>" value="<%=AbstractChip.FALSE%>" /><div onclick="document.editorForm.elements['<%=theChip.getEventID(AbstractTreeNodeChip.EXPAND)%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();"><%
										if (it.hasNext())
										{
											%><img src="images/plus.gif" border="0"></td><%
										}
										else
										{
											%><img src="images/plusend.gif" border="0"></td><%
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
						<td valign="top" align="left"><% child.render(pageContext); %></td>
					</tr>
				<%
			}
		}
	%>
</table>
