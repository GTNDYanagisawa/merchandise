<%@include file="head.inc"%>
<%
	AbstractToolbarChip theChip = (AbstractToolbarChip) request.getAttribute(AbstractChip.CHIP_KEY);
	List actionChips = theChip.getToolbarActionChips();
	List leftActionChips = AbstractToolbarChip.filterToolbarActionChips( actionChips, AbstractToolbarActionChip.ALIGN_LEFT );
	List rightActionChips = AbstractToolbarChip.filterToolbarActionChips( actionChips, AbstractToolbarActionChip.ALIGN_RIGHT );
	boolean noActionChips = actionChips.isEmpty();
	boolean smallToolbar = noActionChips;
	String bgColor = theChip.hasBackgroundColor() ? "background-color:"+theChip.getBackgroundColor()+";" : "background-color:#3566F0;";
%>

<table class="toolbar" cellpadding="0" cellspacing="0">
	<tr>
<% // only show blue corners if no custom background color ist set
	if( theChip.hasCorners() )
	{
		String corner = null;
		if( smallToolbar )
		{
			corner = theChip.hasBackgroundColor() ? "images/toolbar_corner_small_ul_inverted.gif" : "images/bluebar_corner_small_ul.gif";
		}
		else
		{
			corner = theChip.hasBackgroundColor() ? "images/toolbar_corner_ul_inverted.gif" : "images/bluebar_corner_ul.gif";
		}
%>
		<td class="leftCorners" style="<%=(theChip.hasBackgroundColor()?bgColor:"")%>"><img src="<%= corner %>"></td>
<%	
	}
	if( noActionChips )
	{
		%>
			<td class="noActionChips" style="<%=bgColor%>">&nbsp;</td>
		<%
	}
	else
	{
		if( !leftActionChips.isEmpty() )
		{
			%>
				<td class="leftActionChips">
					<table cellspacing="0" cellpadding="0">
						<tr>
			<%
			for( Iterator it = leftActionChips.iterator(); it.hasNext(); )
			{
				AbstractToolbarActionChip aChip = (AbstractToolbarActionChip)it.next();
				aChip.render( pageContext );
			}
			%>
						</tr>
					</table>
				</td>
			<%
		}
		if( !rightActionChips.isEmpty() )
		{
			%>
				<td class="rightActionChips">
					<table cellspacing="0" cellpadding="0" align="right">
						<tr>
			<%
			for( Iterator it = rightActionChips.iterator(); it.hasNext(); )
			{
				AbstractToolbarActionChip aChip = (AbstractToolbarActionChip)it.next();
				aChip.render( pageContext );
			}
			%>
						</tr>
					</table>
				</td>
			<%
		}
	}
 	// only show blue corners if no custom background color ist set
	if( theChip.hasCorners() )
	{
		String corner = null;
		if( smallToolbar )
		{
			corner = theChip.hasBackgroundColor() ? "images/toolbar_corner_small_ur_inverted.gif" : "images/bluebar_corner_small_ur.gif";
		}
		else
		{
			corner = theChip.hasBackgroundColor() ? "images/toolbar_corner_ur_inverted.gif" : "images/bluebar_corner_ur.gif";
		}
%>
		<td class="rightCorners" style="<%=(theChip.hasBackgroundColor()?bgColor:"")%>"><img src="<%= corner %>"/></td>
<%
	}
%>
	</tr>
</table>
