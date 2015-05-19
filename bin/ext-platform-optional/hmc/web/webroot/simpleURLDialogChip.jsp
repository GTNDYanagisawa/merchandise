<%@include file="head.inc" %>
<%
	final SimpleURLDialogChip theChip = (SimpleURLDialogChip) request.getAttribute(AbstractChip.CHIP_KEY);

	String onclick = "";	
	if( theChip.isAutoClose() && theChip.getParent() instanceof DialogContainerChip )
	{
		onclick = " onClick=\"document.editorForm.elements['" + theChip.getParent().getCommandID(DialogContainerChip.CLOSE) + "'].value=" + AbstractChip.TRUE + "; setScrollAndSubmit();\"";
	}
%>
<div class="simpleURLDialogChip">
	<table>
		<tr>
			<td>
				<%= theChip.getMessage() %>
			</td>
		</tr>
		<tr>
			<td>
				<a href="<%= theChip.getUrl() %>" target="_blank" <%= onclick %>><%= theChip.getUrlName() != null ? theChip.getUrlName() : theChip.getUrl() %></a>
			</td>
		</tr>
	</table>
</div>
