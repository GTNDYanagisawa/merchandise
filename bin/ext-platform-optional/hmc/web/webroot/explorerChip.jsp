<%@include file="head.inc"%>
<%
	ExplorerChip theChip = (ExplorerChip)request.getAttribute(AbstractChip.CHIP_KEY);
%>
<script language = "JavaScript1.2">
	window.name="<%= theChip.getFrameID()%>";
</script>	
<% if( DEBUG_COMMENTS ) { %><!-- explorerChip.jsp start --><% } %>
<% if( DEBUG_COMMENTS ) { %><!-- table containing the whole explorer start (toolbar, tree, content...) --><% } %>
<table style="height:100%; width:100%;" cellpadding="0" cellspacing="0" class="content_table">
<%--	<tr>
		<td colspan="2" height="27px" align="top">
			<table cellpadding="0" cellspacing="0">
				<tr>
					<td background="images/toolbar_left.gif" width="11px">&nbsp;&nbsp;</td>
					<td height="27px" width="100%" align="top" background="images/toolbar-back.gif">&nbsp;File&nbsp;&nbsp;&nbsp;Administration&nbsp;&nbsp;&nbsp;Info</td>
					<td width="1px" height="1%" border="0" valign="top"><img src="images/toolbar_right.gif" border="0"></td>
				</tr>
			</table>
		</td>
	</tr>
--%>
	<tr>
		<td colspan="3" align="top" class="toolbar_container">

<% if( DEBUG_COMMENTS ) { %><!-- render the explorer toolbar start --><% } %>
			<%
				theChip.getToolbar().render(pageContext);
			%>
<% if( DEBUG_COMMENTS ) { %><!-- render the explorer toolbar end --><% } %>

		</td>
	</tr>
	<tr height="100%">
		<td style="height:100%;background-color:#F2F7FC; padding:0px;">
			<table border="0" cellspacing="0" cellpadding="0" style="height:100%;">

<% if( DEBUG_COMMENTS ) { %><!-- upper round corners in explorer tree --><% } %>
				<tr valign="top" class="explorer_top">
					<td align="left" valign="top"> <img src="images/tree_corner_ul.gif"/> </td>
					<td>&nbsp;</td>
					<td align="right" valign="top"> <img src="images/tree_corner_ur.gif"/> </td>
				</tr>
<% if( DEBUG_COMMENTS ) { %><!-- upper round corners in explorer tree end --><% } %>
				
				<tr valign="top">
					<td> </td>
					<td>

<% if( DEBUG_COMMENTS ) { %><!-- render the explorer tree start --><% } %>
				<%
					theChip.getTree().render(pageContext);
				%>
<% if( DEBUG_COMMENTS ) { %><!-- render the explorer tree end --><% } %>
<% if( request.getHeader("User-Agent").contains("Chrome") ) { %>
						<div style="height:145px;"/>
<% } %>						
					</td>
					<td> </td>
				</tr>

<% if( DEBUG_COMMENTS ) { %><!-- lower round corners in explorer tree --><% } %>
				<tr> <td colspan="3" height="100%"> &nbsp; </td></tr>
				
				<tr valign="bottom">
					<td id="end_of_explorertree" align="left" valign="bottom" style="font-size:1pt;height:6px;"> <img src="images/tree_corner_ll.gif"/> </td>
					<td> </td>
					<td align="right" valign="bottom" style="font-size:1pt;height:6px;"> <img src="images/tree_corner_lr.gif"/> </td>
				</tr>
<% if( DEBUG_COMMENTS ) { %><!-- lower round corners in explorer tree end --><% } %>
				
			</table>
		</td>

<% if( DEBUG_COMMENTS ) { %><!-- spacer between explorer tree and content --><% } %>
		<td style="padding-left:5px;" class="divider_td"> </td>

		<td style="background-color:#fff;padding:0px" valign="top" width="100%" class="content_td">

			<table style="height:100%;width:100%;" border="0" cellspacing="0" cellpadding="0">

<% if( DEBUG_COMMENTS ) { %><!-- upper round corners in explorer content --><% } %>
				<tr valign="top">
					<td align="left" valign="top" style="font-size:1pt;height:6px;"> <img src="images/content_corner_ul.gif"/> </td>
					<td> </td>
					<td align="right" valign="top" style="font-size:1pt;height:6px;"> <img src="images/content_corner_ur.gif"/> </td>
				</tr>
<% if( DEBUG_COMMENTS ) { %><!-- upper round corners in explorer tree content end --><% } %>

				<tr valign="top" style="height:100%">
					<td style="height:100%"> </td>
					<td style="height:100%;width:100%">
<% if( DEBUG_COMMENTS ) { %><!-- render the explorer content start --><% } %>
			<%
				theChip.getContent().render(pageContext);
			%>
<% if( DEBUG_COMMENTS ) { %><!-- render the explorer content end --><% } %>
					</td>
					<td>&nbsp;</td>
				</tr>

<% if( DEBUG_COMMENTS ) { %><!-- lower round corners in explorer content --><% } %>
				<tr valign="bottom">
					<td align="left" valign="bottom" style="font-size:1pt;height:6px;"> <img src="images/content_corner_ll.gif"/> </td>
					<td> </td>
					<td align="right" valign="bottom" style="font-size:1pt;height:6px;"> <img src="images/content_corner_lr.gif"/> </td>
				</tr>
<% if( DEBUG_COMMENTS ) { %><!-- lower round corners in explorer content end --><% } %>
				
			</table>
		</td>
	</tr>
</table>
<% if( DEBUG_COMMENTS ) { %><!-- table containing the whole explorer end --><% } %>
<% if( DEBUG_COMMENTS ) { %><!-- explorerChip.jsp end --><% } %>
<%
	if( theChip.showItemHistoryChip() )
	{
				theChip.getItemHistoryChip().render(pageContext);
	}
%>
