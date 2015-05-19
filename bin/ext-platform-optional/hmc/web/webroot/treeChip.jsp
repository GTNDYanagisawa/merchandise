<%@include file="head.inc"%>
<%
	TreeChip theChip = (TreeChip) request.getAttribute( AbstractChip.CHIP_KEY );

	// update the tree structure
	// before rendering
	theChip.synchronize();

	TreeChip.TreeNodeChip rootChip = theChip.getRootNodeChip();
	if( rootChip != null )
	{
		boolean isSelectionEnabled = theChip.isSelectionEnabled();
		boolean isMultipleSelectionEnabled = theChip.isMultipleSelectionEnabled();
		boolean isSelected = isSelectionEnabled && rootChip.isSelected();
%>

<input type="hidden" name="<%=theChip.getEventID(TreeChip.TREE_EVENT)%>" value="1" />
<table cellspacing="0" cellpadding="0" border="0">
	<%
		if( theChip.showRoot() )
		{
			rootChip.render( pageContext );
		}
		else if( !rootChip.isLeaf() && rootChip.isExpanded() )
		{
		
			List children = rootChip.getChildren();	
			for( Iterator it = children.iterator(); it.hasNext(); )
			{
				TreeChip.TreeNodeChip childNode = (TreeChip.TreeNodeChip)it.next();
				childNode.render( pageContext );	
			}
		}
	%>
</table>

<%
	}
%>
