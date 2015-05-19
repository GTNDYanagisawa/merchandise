<%@include file="../head.inc"%>
<%
	GenericItemListChip theChip = (GenericItemListChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final boolean prohibitOpening = theChip.isProhibitOpening();
	final String contextMenu = theChip.hasVisibleContextMenuEntries()
						? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
						: "return false;";

	final boolean showScrollbars = ConfigConstants.getInstance().ENABLE_SCROLLBAR;
%>
		<table id="resultTable_<%= theChip.getID() %>" class="genericItemListChip" cellspacing="0" cellpadding="0" oncontextmenu="<%= contextMenu %>" >
			<tr>
				<td>
					<div id="resultlist_<%= theChip.getUniqueName() %>">
<%
							if( showScrollbars && theChip.getListEntryCount() > 12 && theChip.getMaxCount() > 12 )
							{
%>
						<script language="JavaScript1.2">
							document.getElementById( "resultlist_<%= theChip.getUniqueName() %>" ).style.height=260;
						</script>
<%
							}
%>
						<table id="table_<%= theChip.getID() %>_table" class="listtable selecttable<%= theChip.isMultipleSelectionAllowed() ? "" : " singleselect" %>" cellpadding="0" cellspacing="0">
						<thead>
							<tr>
								<!-- gilcHeaderCheckbox -->
								<th class="checkbox gilcCheckbox">
									<div>
										<input name="SELECTOR" class="header" <%= theChip.isAllSelected() ? "checked" : "" %> onclick="document.editorForm.elements['<%=theChip.getEventID(GenericItemListChip.SELECT_VISIBLE)%>'].value='<%= !theChip.isAllSelected()%>';setScrollAndSubmit();" type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>" name="" value="ALL" />
										<input type="hidden" name="<%=theChip.getEventID(GenericItemListChip.SELECT_VISIBLE)%>" value=""/>
										<input type="hidden" name="<%=theChip.getEventID(SearchListChip.SELECT)%>" value="" />   <!-- dummy event to allow lists to be completely de-selected -->
									</div>
								</th>
								<!-- gilcHeaderIcon -->
								<th class="gilcIcon" style="border-left:0px;">
									<div class="gilcIcon">&nbsp;</div>
								</th>
<%
	for( final Iterator iter = theChip.getChildNodes().iterator(); iter.hasNext(); )
	{
		final Node childNode = (Node) iter.next();
		final boolean editable = theChip.isEditable();
		
		if( childNode instanceof AttributeNode )
		{
			final AttributeNode attributeNode = (AttributeNode) childNode;
			final String qualifier = attributeNode.getAttributeQualifier();
			final int attributeWidth = attributeNode.getActualColumnWidth();
%>
								<!-- gilcHeaderAttribte -->
								<th>
									<div class="gilcEntry<%= theChip.isMarkedAsMandatory(qualifier) ? "-mandatory" : "" %>" style="<%= editable ? "" : "color:#999999;"%><%= attributeWidth > 0 ? "width:" + attributeWidth + "px;" : "" %>" title="<%= theChip.getTitle(qualifier) %>"><%= theChip.getTitle(qualifier) %></div>
								</th>
<%
		}
		else if( childNode instanceof ItemNode )
		{
			final int itemWidth = ((ItemNode) childNode).getWidth();
			final String itemTitle = ((ItemNode) childNode).getTitle();
%>
								<!-- gilcHeaderItem -->
								<th>
									<div class="gilcEntry" style="<%= editable ? "" : "color:#999999;"%><%= itemWidth > 0 ? "width:" + itemWidth + "px;" : "" %>"><%= localized(itemTitle) %></div>
								</th>
<%
		}
	}
%>
							</tr>
							</thead>
<tbody id="<%= theChip.getUniqueName()%>_tbody">
<%
	if( theChip.getListEntries().isEmpty() && theChip.getNewItemEntry() == null )
	{
%>
							<!-- gilcEmptylist -->
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
								<tr id="<%= chip.getItem().getPK() %>" class="draggable <%= prohibitOpening ? "" : "doubleclick-event " + chip.getCommandID(GenericItemListEntryChip.OPEN_EDITOR) %>" onclick="">	<%-- mr: empty onclick is necessary for selenium test recording! --%>
									<!-- gilcCheckbox -->
									<td class="checkbox gilcCheckbox" id="<%= theChip.getUniqueName() %>_td">
										<input type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>" 
												 name="<%=theChip.getEventID(SearchListChip.SELECT)%>" 
												 value="<%=theChip.getPosition(chip)%>" <%=theChip.isListEntrySelected( chip )?" checked":""%>/>
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
			<td class="gilcNewEntry"><div>&nbsp;</div></td>
<%
				chip.render(pageContext);
%>
		</tr>
<%
	}
	if(theChip.getSearchEditor()!=null)
	{
		final ReferenceAttributeEditorChip chip = theChip.getSearchEditor();
		int colspan = theChip.getChildNodes().size();
		if( theChip.getAttributeNodes().size() > 0 )
		{
			AttributeNode firstAtt = theChip.getAttributeNodes().get( 0 );
			if( firstAtt.getActualColumnWidth() >= 150 )
			{
				chip.setWidth( firstAtt.getActualColumnWidth() - 6 );
				colspan = 1;
			}
		}
%>
		<tr>
			<td>
			</td>
			<td>
				<div style="padding-left:8px;">
					<img class="icon" src="images/icons/list_menu_add.gif" id="<%= chip.getUniqueName() %>_img"/>
				</div>
			</td>
			<td style="padding-left:3px;" colspan="<%= colspan %>">
<%
		chip.render(pageContext);
%>
			</td>
		</tr>
<%
	}
%>
</tbody>
						</table>
					<script language="JavaScript1.2">
						Sortable.create("<%= theChip.getUniqueName() %>_tbody",{onUpdate:y_dragListItem,tag:'tr',only:'draggable',ghosting:false,format:/(.*)/});
					</script>
					</div>
				</td>
			</tr>
<%
			int maxCount = theChip.getMaxCount();
			int totalCount = theChip.getListEntryCount();
			boolean showRange = (maxCount != 0) && (maxCount < totalCount);
%>

		</table>
