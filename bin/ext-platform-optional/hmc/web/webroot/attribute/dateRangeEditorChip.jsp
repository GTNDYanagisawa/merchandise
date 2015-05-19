<%@include file="../head.inc"%>
<%
	DateRangeEditorChip theChip = (DateRangeEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	String clearButton = theChip.isEditable() ? 
								getSimpleImageLink(theChip.getCommandID(DateRangeEditorChip.CLEAR), localized("clicktoclear"), "images/icons/valueeditor_clear.gif", "images/icons/valueeditor_clear_hover.gif")
								: "<img src=\"images/icons/valueeditor_clear_inactive.gif\">";
	final boolean wrapValues = theChip.isWrapValues();
%>
<div style="width:<%= theChip.getWidth() %>px;">
	<table class="dateRangeEditorChip" cellspacing="0" cellpadding="0">
		<tr>
			<td class="start">
				<%= localized("daterange.start") %>:
			</td>
<%
			if( wrapValues )
			{
%>
				<td class="wrap">
					<%= clearButton %>
				</td>
			</tr>
			<tr>
<%
			}
%>
			<td colspan="2">
				<% theChip.getStartDateEditor().render(pageContext); %>
			</td>
<%
			if( wrapValues )
			{
%>
			</tr>
			<tr>
<%
			}
%>
			<td colspan="2" class="end">
				<%= localized("daterange.end") %>:
<%
			if( wrapValues )
			{
%>
			</tr>
			<tr>
<%
			}
%>
			<td colspan="2">
				<% theChip.getEndDateEditor().render(pageContext); %>
			</td>
<%
			if( !wrapValues )
			{
%>
				<td class="nowrap">
					<%= clearButton %>
				</td>
<%
			}
%>
		</tr>
	</table>
</div>
