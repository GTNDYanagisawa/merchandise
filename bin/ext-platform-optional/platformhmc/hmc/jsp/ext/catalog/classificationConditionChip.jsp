<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.catalog.hmc.ClassificationConditionChip" %>
<%
	ClassificationConditionChip theChip = (ClassificationConditionChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<td class="attribute">
	<div class="attribute"><%=theChip.getName()%></div>
</td>
<td class="locale">
	<div class="locale">&nbsp;</div>
</td>
<td class="comparator">
	<div class="comparator">
<%
		theChip.getOperatorEditor().render(pageContext);
%>
	</div>
</td>
<td class="condition">
	<div class="condition">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
<% 
					theChip.getValueEditor().render(pageContext);
%>
				</td>
<%
				if( theChip.supportUnits() )
				{
%>
					<td class="unit">
						<div class="unit">
<% 
							theChip.getUnitEditor().render(pageContext); 
%>
						</div>
					</td>
<%
				}
%>
			</tr>
		</table>
	</div>
</td>
