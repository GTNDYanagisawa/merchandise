<%@include file="head.inc"%>
<% if( DEBUG_COMMENTS ) { %><!-- menuTabChip.jsp start --><% } %>
<%
	MenuTabChip theChip = (MenuTabChip) request.getAttribute(AbstractChip.CHIP_KEY);
	if (theChip.isActive())
	{
		%>
			<td class="menuTabChipActive"
             background="images/karte_horiz_active.gif">
						&nbsp;&nbsp;&nbsp; <%=localized(theChip.getName())%>
			</td>
		<%
	}
	else
	{
		if (theChip.isEnabled())
		{
			%>
				<td class="menuTabChipEnabled"
					 background="images/karte_horiz.gif">
							&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="submit" name="<%=theChip.getCommandID(MenuTabChip.SELECT)%>" value="<%=localized(theChip.getName())%>">
				</td>
			<%
			
		}
		else
		{
			%>
				<td class="menuTabChipNotEnabled"
					 background="images/karte_horiz.gif">
						&nbsp;&nbsp;&nbsp; <%=localized(theChip.getName())%>
				</td>
			<%
		}
	}
%>
<% if( DEBUG_COMMENTS ) { %><!-- menuTabChip.jsp end --><% } %>
