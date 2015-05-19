<%@include file="../head.inc"%>
<%
	final DateOnlyDateRangeEditorChip theChip = (DateOnlyDateRangeEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	String clearButton = theChip.isEditable() ? 
								getSimpleImageLink(theChip.getEventID(DateRangeEditorChip.CLEAR), localized("clicktoclear"), "images/icons/valueeditor_clear.gif", "images/icons/valueeditor_clear_hover.gif")
								: "<img src=\"images/icons/valueeditor_clear_inactive.gif\">";
	final boolean wrapValues = theChip.isWrapValues();
%>
<%
	if( wrapValues )
	{
%>
	<table class="dateOnlyDateRangeEditorChip" cellspacing="0" cellpadding="0">
		<tr>
			<td class="start">
				<%= localized("daterange.start")%>:
			</td>
			<td><% theChip.getStartDateEditor().render( pageContext );%></td>
		</tr>
		<tr height="5px"><td> </td></tr>	
		<tr>
			<td class="end">
				<%= localized("daterange.end") %>:
			</td>
			<td><% theChip.getEndDateEditor().render( pageContext );%></td>
		</tr>
	</table>
<%
	}
	else
	{
%>
	<table class="dateOnlyDateRangeEditorChip">
		<tr>
			<td><% theChip.getStartDateEditor().render( pageContext );%></td>
			<td></td>
			<td class="nowrap">
				<%= localized("to")%>
			</td>
			<td><% theChip.getEndDateEditor().render( pageContext );%></td>
		</tr>
	</table>
<%
	}
%>
