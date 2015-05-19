<%@include file="../../head.inc"%>
<%
	final AbstractItemTreeNodeChip theChip = (AbstractItemTreeNodeChip)request.getAttribute(AbstractChip.CHIP_KEY);
%>
<input type="hidden" name="<%=theChip.getEventID(AbstractTreeNodeChip.EDIT)%>" value="<%=AbstractChip.FALSE%>" />
<table class="abstractItemTreeNodeChip" cellspacing="0" cellpadding="0">
	<tr>
		<td class="aitncIcon">
			<div onclick="document.editorForm.elements['<%=theChip.getEventID(AbstractTreeNodeChip.EDIT)%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();">
				<img src="<%=theChip.getIcon()%>" border="0">
			</div>
		</td>
		<td class="aitncName">
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
						<td class="aitncTreeIMG" background="<%=it.hasNext() ? "images/vert.gif" : ""%>">
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
						<td class="aitncContext"><% child.render(pageContext); %></td>
					</tr>
				<%
			}
		}
	%>
</table>
