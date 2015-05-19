<%@include file="../head.inc"%>
<%
	EnumerationValueUsageFinderChip theChip = (EnumerationValueUsageFinderChip)request.getAttribute(AbstractChip.CHIP_KEY);
%>
<table>
	<tr>
		<td class="propertyname">Aufzählungswert:</td><td class="propertyvalue"><%theChip.getEnumerationValueEditor().render(pageContext);%></td>
	</tr>
</table>

<div class="xp-button chip-event" style="padding: 5px 0 5px 10px;">
	<a href="#" title="<%= localized("search.button") %>" name="<%= theChip.getCommandID(EnumerationValueUsageFinderChip.PERFORM) %>" hidefocus="true">
		<span class="label">
			<%= localized("search.button") %>
		</span>
	</a>
</div>
<div style="clear:both;"/>

<!-- 
<input type="image" src="images/icons/<%=localized("btn_search")%>" name="<%=theChip.getCommandID(EnumerationValueUsageFinderChip.PERFORM)%>"/>
-->
<hr>
<%=theChip.getResult()%>
