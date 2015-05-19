<%@include file="../head.inc"%>
<%
	final SavedValueEntryEditorChip theChip = (SavedValueEntryEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	if( theChip.getSavedValueEntry() != null )
	{
%>
		<table class="savedValueEntryEditorChip" cellspacing="0" cellpadding="0" width="100%">
			<tr class="header">
				<td class="lspacer"><div class="lspacer">&nbsp;</div></td>
<%
				if( theChip.isLocalized() ) 
				{
%>
					<td class="langcode"><div class="langcode">&nbsp;</div></td>
<%
				}
%>
 				<td class="<%= theChip.isEditable() ? "enabled" : "disabled" %>">
 					<div class="column"><%= theChip.getOldValueLabel() %>:</div>
		 		</td>
 				
 				<td class="cspacer"><div class="cspacer">&nbsp;</div></td>
 				
 				<td class="<%= theChip.isEditable() ? "enabled" : "disabled" %>">
 					<div class="column"><%= theChip.getNewValueLabel() %>:</div>
		 		</td>
			</tr>
<%
			if( !theChip.isLocalized() )
			{
				// single editors
%>
				<tr class="singlerow">
					<td class="lspacer"><div class="lspacer"/></td>
	 				
	 				<td class="column">
		 				<div class="column">
<% 
							theChip.getOldValueEditorChip().render(pageContext); 
%>
						</div>
	 				</td>
	 				
	 				<td class="cspacer"><div class="cspacer">&nbsp;</div></td>
	 				
	 				<td class="column">
		 				<div class="column">
<% 
							theChip.getNewValueEditorChip().render(pageContext); 
%>
						</div>
			 		</td>
				</tr>
<%
			}
			else
			{
				// editors for every language
				final Map oldValueEditorChips = theChip.getLocalizedOldValueEditorChips();
				final Map newValueEditorChips = theChip.getLocalizedNewValueEditorChips();
		
				for( Iterator iter = oldValueEditorChips.keySet().iterator(); iter.hasNext(); )
				{
					Language lang = (Language) iter.next();
%>
					<tr class="langrow">
						<td class="lspacer"><div class="lspacer">&nbsp;</div></td>
						
						<td class="langcode"><div class="langcode"><%= lang.getIsoCode() %></div></td>
				 		<td>
<% 
							((Chip) oldValueEditorChips.get(lang)).render(pageContext);
%>
				 		</td>
		 				
		 				<td class="cspacer"><div class="cspacer">&nbsp;</div></td>
				 		
				 		<td>
<% 
							((Chip) newValueEditorChips.get(lang)).render(pageContext); 
%>
				 		</td>
					</tr>		
<%
				}
			}
%>
		</table>
<%
	}
	else
	{
%>
		This type may not be instantiated manually!!
<%
	}
%>
