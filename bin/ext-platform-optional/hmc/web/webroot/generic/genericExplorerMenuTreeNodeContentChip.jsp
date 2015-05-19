<%@include file="../head.inc"%>

<%
	final GenericExplorerMenuTreeNodeContentChip theChip = (GenericExplorerMenuTreeNodeContentChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final IconViewChip view = theChip.getIconViewChip();
	
%>
	<table class="genericExplorerMenuTreeNodeContentChip">
		<tr>
			<td>
<%
	view.render(pageContext);
%>
			</td>
		</tr>
<%
	if( theChip.showDefaultContent())
	{
%>
		<tr>
			<td class="defaultContent">
				<div class="dc">
<%
					theChip.getDefaultContentChip().render(pageContext);
%>
				</div>
			</td>
		</tr>
<%
	}
%>
	</table>
