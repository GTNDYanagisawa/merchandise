<%@include file="../../head.inc"%>

<%@page import="de.hybris.platform.catalog.hmc.ClassificationVariantListChip" %>

<%
	final ClassificationVariantListChip theChip = (ClassificationVariantListChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	
	final boolean showScrollbars = Config.getBoolean("hmc.enable.scrollbar",true);
%>
		<table class="classificationVariantListChip" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<div class="cvlc" id="resultlist_<%= theChip.getUniqueName() %>">
					<%
							if( showScrollbars && theChip.getListEntryCount()>12 )
							{
					%>
						<script language="JavaScript1.2">
							document.getElementById( "resultlist_<%= theChip.getUniqueName() %>" ).style.height=260;
						</script>
					<%
							}
					%>
						<table class="listtable" cellpadding="0" cellspacing="0">
							<tr>
								<th class="cvlcCheckbox">
									<div class="cvlcCheckbox">
										<input name="SELECTOR" class="header" <%= theChip.isAllSelected() ? "checked" : "" %> onclick="document.editorForm.elements['<%=theChip.getCommandID(GenericItemListChip.SELECT_VISIBLE)%>'].value='<%= !theChip.isAllSelected()%>';setScrollAndSubmit();" type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>" name="" value="ALL" />
										<input type="hidden" name="<%=theChip.getCommandID(GenericItemListChip.SELECT_VISIBLE)%>" value=""/>
									</div>
								</th>
								<th class="cvlcIcon" style="border-left:0px;">
									<div class="cvlcIcon">&nbsp;</div>
								</th>
<%
	// normal attributes
	for( final Iterator iter = theChip.getAttributeNodes().iterator(); iter.hasNext(); )
	{
		final AttributeNode attributeNode = (AttributeNode) iter.next();
		final String qualifier = attributeNode.getAttributeQualifier();
		final int attributeWidth = attributeNode.getActualColumnWidth();
%>
								<th>
									<div class="cvlcEntry" style="<%= attributeWidth > 0 ? "width:" + attributeWidth + "px;" : "" %>"><%= theChip.getTitle(qualifier) %></div>
								</th>
<%
	}
	
	// classification attributes
	if( theChip.getClassificationAttributeNames() != null )
	{
		for( final Iterator iter = theChip.getClassificationAttributeNames().iterator(); iter.hasNext(); )
		{
%>
								<th>
									<div class="cvlcEntry"><%= (String) iter.next() %></div>
								</th>
<%
		}
	}
%>
							</tr>
<%		
	if( theChip.getListEntries().isEmpty() && theChip.getNewItemEntry() == null )
	{
%>
							<tr>
								<td class="disabled" colspan="<%= theChip.getHeaderCount() + 3 %>">
									<div><%= localized( "listisempty" ) %></div>
								</td>
							</tr>
<%
	}
	else
	{
		List removedEntries = new ArrayList();

		for (final Iterator theChips = theChip.getRestrictedListEntries().iterator(); theChips.hasNext();)
		{
			GenericItemListEntryChip chip = (GenericItemListEntryChip) theChips.next();
			if( (chip.getItem() == null) ||chip.getItem().isAlive() )
			{
%>
								<tr>
									<td class="cvlcCheckbox">
										<input onclick="document.editorForm.elements['SELECTOR'].checked=false;" type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>" name="<%=theChip.getEventID(SearchListChip.SELECT)%>" value="<%=theChip.getPosition(chip)%>" <%=theChip.isListEntrySelected( chip )?" checked":""%>/>
									</td>
<%
				chip.render(pageContext);
%>
								</tr>
<%
			}
			else
			{
				removedEntries.add(chip);
			}
		}

		for( final Iterator removedIter = removedEntries.iterator(); removedIter.hasNext(); )
		{
			Chip chipToRemove = (Chip) removedIter.next();
			theChip.removeListEntry(chipToRemove);
		}
	}
	if( theChip.getNewItemEntry() != null )
	{
		final CreateItemListEntryChip chip = theChip.getNewItemEntry();
		// there is a new entry
%>
		<tr>
			<td style="cvlcNewEntry"><div>&nbsp;</div></td>
<%
				chip.render(pageContext);
%>
		</tr>
<%
	}
%>
						</table>
					</div>
				</td>
			</tr>
<%
			int maxCount = theChip.getMaxCount();
			int totalCount = theChip.getListEntryCount();
			boolean showRange = (maxCount != 0) && (maxCount < totalCount);
%>

		</table>
