<%@include file="head.inc"%>
<%
	TreeChip.TreeNodeChip theChip = (TreeChip.TreeNodeChip) request.getAttribute( AbstractChip.CHIP_KEY );
	
	TreeChip tree = theChip.getTree();
	boolean showRoot = tree.showRoot();
	boolean isRoot = (theChip.getParentNode() == null);
	boolean isRootChild = !isRoot && theChip.getParentNode().getParentNode() == null;
	boolean isFirstNode = theChip.isFirstNode();
	boolean isFirstNodeInTree = (isRoot && showRoot) || (!showRoot && isRootChild && isFirstNode);
	boolean isLastNode = theChip.isLastNode();
	boolean isLeaf = theChip.isLeaf();
	boolean showHeader = tree.showHeader() && tree.getCellRenderer().hasHeader();
	boolean isExpanded = theChip.isExpanded();
	boolean isSelectionEnabled = tree.isSelectionEnabled();
	boolean isMultipleSelectionEnabled = tree.isMultipleSelectionEnabled();
	boolean isSelected = isSelectionEnabled && theChip.isSelected();
	boolean isCurrent = theChip.isCurrentPath();
	boolean highlightCurrent = tree.highlightCurrentPath();
	
	int maxLevel = tree.getMaxLevel();
	if( !showRoot ) maxLevel--;
	int level = theChip.getLevel();
	if( !showRoot ) level--;
	
	int rowCount = tree.getCellRenderer().getRowCount();
	int treeRow = tree.getCellRenderer().getTreeRow();
	if( treeRow < 0 )
		treeRow = 0;
	else if( treeRow >= rowCount )
		treeRow = rowCount - 1;

	String CELLSTYLE_LASTROW_HEADER = "border-bottom: 1px solid #d5d5d5;";
	String CELLSTYLE_HEADER = CELLSTYLE_LASTROW_HEADER + (tree.showLines() ? " border-right: 1px solid #d5d5d5;" : "");
	String CELLSTYLE_LASTROW = ((!tree.showLines() || isFirstNodeInTree) ? "" : "border-top: 1px solid #e5e5e5;");
	String CELLSTYLE = CELLSTYLE_LASTROW + (tree.showLines() ? " border-right: 1px solid #e5e5e5;" : "");

	// header
	if( isFirstNodeInTree && showHeader )
	{
%>
<tr>
<%
	if( isSelectionEnabled )
	{
		boolean isLastRow = (rowCount == 0);
		
		%>
			<td align="left" valign="top" style="<%= CELLSTYLE_LASTROW_HEADER %>">
				<%
					Chip rendererChip = tree.getSelectionHeaderRendererChip();
					if( rendererChip != null )
					{
						rendererChip.render( pageContext );
					}
					else
					{
						%>&nbsp;<%
					}
				%>
			</td>
		<%
	}

	for( int row = 0; row < rowCount; row++ )
	{
		boolean isLastRow = (row == rowCount - 1);
		int colspan = (row == treeRow ? maxLevel + 1 : 1);
		
		%>
			<td <%= colspan > 1 ? "colspan=\"" + colspan + "\"" : "" %> align="left" valign="top" style="<%= isLastRow ? CELLSTYLE_LASTROW_HEADER : CELLSTYLE_HEADER %>">
				<%
					Chip rendererChip = tree.getHeaderRendererChip( row );
					if( rendererChip != null )
					{
						rendererChip.render( pageContext );
					}
					else
					{
						%>&nbsp;<%
					}
				%>
			</td>
		<%
	}
%>
</tr>
<%
	}
%>
<tr style="<%= isCurrent && highlightCurrent? " background-color: " + tree.getCurrentPathBackground() + ";" : "" %>">
<%
	// select box
	if( isSelectionEnabled )
	{
		%>
			<td align="left" valign="top" style="<%= CELLSTYLE_LASTROW %>">
				<div>
					<input type="checkbox" name="<%= theChip.getEventID( TreeChip.TreeNodeChip.SELECT ) %>" value="1" <%= isSelected ? " checked" : "" %> />
				</div>
			</td>
		<%
	}
	
	// rows
	for( int row = 0; row < rowCount; row++ )
	{
		boolean isLastRow = (row == rowCount - 1);
		
		if( row == treeRow )
		{
			for( int l = level - 1; l > 0; l-- )
			{
				TreeChip.TreeNodeChip parent = theChip.getParentNode( l );
				String strBackground = !parent.isLastNode() ? "background=\"images/vert.gif\"" : "";
				%>
					<td <%= strBackground %> align="left" valign="top" style="<%= CELLSTYLE_LASTROW %>">&nbsp;</td>
				<%
			}
			
			String strBackground = !isLastNode ? "background=\"images/vert.gif\"" : "";
%>
			<td <%= strBackground %> align="left" valign="top" style="<%= CELLSTYLE_LASTROW %>">
				<%
					if( !isLeaf )
					{
						if( isExpanded )
						{
							%>		
								<input type="hidden" name="<%= theChip.getEventID( TreeChip.TreeNodeChip.COLLAPSE )%>" value="<%= AbstractChip.FALSE %>" />
								<div onclick="document.editorForm.elements['<%= theChip.getEventID( TreeChip.TreeNodeChip.COLLAPSE )%>'].value='<%= AbstractChip.TRUE %>';setScrollAndSubmit();">
									<%
										if( isFirstNodeInTree )
										{
											%>
												<img src="images/minusbegin.gif" border="0" alt=""/>
											<%
										}
										else if( !isLastNode )
										{
											%>
												<img src="images/minus.gif" border="0" alt=""/>
											<%
										}
										else
										{
											%>
												<img src="images/minusend.gif" border="0" alt=""/>
											<%
										}
									%>
								</div>
							<%
						}
						else
						{
							%>
								<input type="hidden" name="<%= theChip.getEventID( TreeChip.TreeNodeChip.EXPAND )%>" value="<%= AbstractChip.FALSE %>" />
								<div onclick="document.editorForm.elements['<%= theChip.getEventID( TreeChip.TreeNodeChip.EXPAND )%>'].value='<%= AbstractChip.TRUE %>';setScrollAndSubmit();">
									<%
										if( isFirstNodeInTree )
										{
											%>
												<img src="images/plusbegin.gif" border="0" alt=""/>
											<%
										}
										else if( !isLastNode )
										{
											%>
												<img src="images/plus.gif" border="0" alt=""/>
											<%
										}
										else
										{
											%>
												<img src="images/plusend.gif" border="0" alt=""/>
											<%
										}
									%>
								</div>
							<%
						}
					}
					else if( !isRoot )
					{
						%>
							<div>
							<%
								if( !isLastNode )
								{
									%>
										<img src="images/horiz.gif" border="0" alt=""/>
									<%
								}
								else
								{
									%>
										<img src="images/end.gif" border="0" alt=""/>
									<%
								}
							%>
							</div>
						<%
					}
				%>
			</td>
			<td colspan="<%= maxLevel - level + 1 %>" align="left" valign="top" style="<%= isLastRow ? CELLSTYLE_LASTROW : CELLSTYLE %>; height:100%; width:100%;">
				<%
					Chip rendererChip = theChip.getRendererChip( row );
					if( rendererChip != null )
					{
						rendererChip.render( pageContext );
					}
					else
					{
						%>&nbsp;<%
					}
				%>
			</td>
<%
		}
		else
		{
			%>
				<td align="left" valign="top" style="<%= isLastRow ? CELLSTYLE_LASTROW : CELLSTYLE %>; height:100%;">
					<%
						Chip rendererChip = theChip.getRendererChip( row );
						if( rendererChip != null )
						{
							rendererChip.render( pageContext );
						}
						else
						{
							%>&nbsp;<%
						}
					%>
				</td>
			<%
		}
	}
%>
</tr>
<%
	if( !isLeaf && isExpanded )
	{
	
		List children = theChip.getChildren();	
		for( Iterator it = children.iterator(); it.hasNext(); )
		{
			TreeChip.TreeNodeChip childNode = (TreeChip.TreeNodeChip)it.next();
			childNode.render( pageContext );	
		}
	}
%>

