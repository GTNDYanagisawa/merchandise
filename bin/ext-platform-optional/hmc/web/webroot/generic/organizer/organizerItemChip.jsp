<%@include file="../../head.inc"%>
<%
	OrganizerItemChip theChip = (OrganizerItemChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<input type="hidden" name="<%= theChip.getCommandID(theChip.PING) %>" value="true"/>
<table class="organizerItemChip" cellspacing="0" cellpadding="0">
	<tr>
		<td class="oicToolbar"><% theChip.getToolbar().render(pageContext); %></td>
	</tr>
	<tr>
		<td class="oicContext"><% theChip.getEditor().render(pageContext); %></td>
	</tr>
<%
	if( theChip.hasFooter() )
	{
%>
	<tr>
		<td class="oicContext"><% theChip.getFooter().render(pageContext); %></td>
	</tr>
<%
	}
%>
</table>

