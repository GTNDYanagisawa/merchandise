<%@include file="../../head.inc"%>
<!-- Be aware to not include another submit button, because implementation
     requires that all submits will result in a SEARCH command -->

<%
	OrganizerChip theChip = (OrganizerChip) request.getAttribute(AbstractChip.CHIP_KEY);
	if( theChip.getToolbar() != null )
	{
%>
	<table class="ocNoToolbar" cellspacing="0" cellpadding="0">
		<tr>
			<td class="ocNoToolbar"><% theChip.getToolbar().render(pageContext); %></td>
		</tr>
	</table>
<%
	}
%>

<%
	if( ! theChip.isPartOf() )
	{
%>
<table class="ocNotPartOf" cellspacing="0" cellpadding="0">

	<!-- upper round corners -->
	<tr class="upperRow">
		<td class="leftCorner"> <img src="images/content_corner_ul.gif"> </td>
		<td> </td>
		<td class="rightCorner"> <img src="images/content_corner_ur.gif"> </td>
	</tr>
	
	<!-- page context -->
	<tr class="contextRow">
		<td> </td>
		<td class="contextField">
<%
	}
%>	
			<table class="ocPageContext" cellspacing="0" cellpadding="0">
				<tr class="ocPageContextTR">
					<td class="ocPageContextTD">
<%
						if( theChip.getSearchComponent().isEnabled() || theChip.getResultComponent().isEnabled() )
						{
							theChip.getSearchComponent().render(pageContext);
							theChip.getResultComponent().render(pageContext);
						}
						theChip.getEditorComponent().render(pageContext);
%>
					</td>
				</tr>
			</table>
<%
	if( ! theChip.isPartOf() )
	{
%>
		</td>
		<td> </td>
	</tr>


	<!-- lower round corners -->
	<tr class="lowerRow">
		<td class="leftCorner"> <img src="images/content_corner_ll.gif"> </td>
		<td> </td>
		<td class="rightCorner"> <img src="images/content_corner_lr.gif"> </td>
	</tr>

</table>
<%
	}
%>	
