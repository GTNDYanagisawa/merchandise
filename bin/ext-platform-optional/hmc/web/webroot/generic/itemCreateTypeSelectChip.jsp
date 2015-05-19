<%@include file="../head.inc"%>
<%@include file="../xp_button.inc"%>
<%
	ItemCreateTypeSelectChip theChip = (ItemCreateTypeSelectChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<div class="itemCreateTypeSelectChip">
	<table cellspacing="0" cellpadding="0" class="ictsc">
		<tr>
			<td colspan="4" class="ictscInfo">
				<%= localized("organizer.create.info") %>:
			</td>
		</tr>
		<tr>
<% 
			Collection types = theChip.getTypeEntries();
			if( types.size() > 1 )
			{
%>
				<td class="ictscType">
					<div>
						<%=localized("type")%>:
					</div>
				</td>
				<td style="ictscDescr">
					<select name="<%=theChip.getEventID(ItemCreateTypeSelectChip.TYPECODE)%>" onchange="setScrollAndSubmit();" size="<%= types.size() > 10 ? 10 : types.size() %>">
<%
						String currentCode = theChip.getCurrentTypeCode();
						for (final Iterator it = types.iterator(); it.hasNext(); )
						{
							GenericHelper.TypeEntry entry = (GenericHelper.TypeEntry)it.next();
							final String description = entry.getType().getDescription() != null && entry.getType().getDescription().length() != 0 ? " (" + entry.getType().getDescription() + ")" : "";
%>
							<option 
								value="<%= entry.getCode() %>" <%= entry.getCode().equals(currentCode) ? " selected" : "" %>
								<%= (entry.isAbstract() || entry.getType() instanceof ViewType) ? " class=\"disabled\"" : "" %> 
							>
								<%= entry.getDisplayName() + description %>
							</option>
<%
						}
%>
					</select>
				</td>
<%
			}
%>
			</tr>
			<tr>
			   <td>&nbsp;</td><td>&nbsp;</td>
			</tr>
			<tr>
			   <td>&nbsp;</td>
			<td style="ictscButtons">
<% 
				if( (theChip.getSelectedType() != null) && (theChip.getSelectedType().isAbstract() || theChip.getSelectedType() instanceof ViewType) )
				{
%>
					<%= xpButtonDisabled(localized("create"), localized("create") ) %>				
<%
				}
				else
				{
%>
					<%= xpButton( localized("create"), theChip.getCommandID(ItemCreateTypeSelectChip.CREATE), localized("create") ) %>
<%
				}
%>
			</td>
			<td style="width:100%">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="4" style="height:100%"/>
		</tr>
	</table>
</div>

<script language="Javascript">
	var optimizeWindowSize=true;
</script>

