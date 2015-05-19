<%@include file="../../head.inc"%>
<%@include file="../../xp_button.inc"%>
<%
	OrganizerCreateChip theChip = (OrganizerCreateChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<table class="organizerCreateChip" cellspacing="8" cellpadding="0">
	<tr>
		<td><%= localized("organizer.create.info") %>:</td>
	</tr>	
	<tr>
<% 
		Collection types = theChip.getTypeEntries();
		if( types.size()>1 )
		{
%>
			<td class="head" align="left"><span style="white-space:nowrap"><%=localized("type")%>:</span></td>
			<td align="left" colspan="2">
				<select name="<%=theChip.getEventID(OrganizerCreateChip.TYPECODE)%>" onchange="setScrollAndSubmit();">
<%
					String currentCode = theChip.getCurrentTypeCode();
					for (final Iterator it = types.iterator(); it.hasNext(); )
					{
						GenericHelper.TypeEntry entry = (GenericHelper.TypeEntry)it.next();
%>
						<option 
							value="<%= entry.getCode() %>" <%= entry.getCode().equals(currentCode) ? " selected" : "" %>
							<%= entry.isAbstract() ? " class=\"disabled\"" : "" %> 
						>
							<%= entry.getDisplayName() %>
						</option>
<%
					}
%>
				</select>
			</td>
<%
		}
%>
		<td>
<% 
			if( (theChip.getSelectedType() != null) && theChip.getSelectedType().isAbstract() )
			{
%>
			<%= xpButtonDisabled(localized("create"), localized("create") ) %>				
<%
			}
			else
			{
%>
			<%= xpButton( localized("create"), theChip.getCommandID(OrganizerCreateChip.CREATE), localized("create") ) %>
<%
			}
%>
		</td>
	</tr>
</table>
