<%@page import="java.util.*,de.hybris.platform.print.jalo.*"%>
<%@include file="../../head.inc"%>
<table>
	<tr>
		<td valign="top"><b>Placeholder Qualifier</b></td>
		<td valign="top"><b>Placeholder Value</b></td>
	</tr>
<%
	de.hybris.platform.print.hmc.attribute.PlaceholderDisplayChip theChip = (de.hybris.platform.print.hmc.attribute.PlaceholderDisplayChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	for( Placeholder ph : theChip.getPlaceholders() )
	{
%>
	<tr>
		<td valign="top"><%= ph.getQualifier() %></td>
		<td valign="top"><%= theChip.getValue( ph )%><br/><small><%= theChip.getPlaceholderSource( ph )%></small></td>
	</tr>
<% 		
	}
%>	
</table>
