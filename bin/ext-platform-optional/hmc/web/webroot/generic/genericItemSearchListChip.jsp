<%@include file="../head.inc"%>
<%@page import="de.hybris.platform.hmc.Constants"%>

<%
	GenericItemSearchListChip theChip= (GenericItemSearchListChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String contextMenu = theChip.hasVisibleContextMenuEntries()
								? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
								: "return false;";
%>
<table class="genericItemSearchListChip" cellpadding="0" cellspacing="0" oncontextmenu="<%= contextMenu %>">
	<col width="100%"/>
	<tr align="left" valign="top">
		<td><%
			theChip.getToolbar().render( pageContext );
		%></td>
	</tr>
<%
	if( theChip.isExpanded() )
	{
%>
	<tr>
		<td>
			<table class="listtable" cellpadding="0px" cellspacing="0px">
				<tr>
					<th class="checkbox gislcCheckbox">

<%
	if( theChip.isMultipleSelectionAllowed() )
				{
%>
							<input name="SELECTOR" class="header" <%= theChip.isAllSelected() ? "checked" : "" %> 
									 onclick="document.editorForm.elements['<%=theChip.getEventID(GenericItemListChip.SELECT_VISIBLE)%>'].value='<%= !theChip.isAllSelected()%>';setScrollAndSubmit();" 
									 type="checkbox" name="" value="ALL" title="<%= localized("select.all.tooltip") %>"
									 />
							<input type="hidden" name="<%=theChip.getEventID(SearchListChip.SELECT)%>" value="" />   <!-- dummy event to allow lists to be completely de-selected -->
<%
	}
				if( theChip.getAdditionalColumnProvider()!=null )
				{
%>					
							<th style="width:<%=theChip.getAdditionalColumnProvider().getAdditionalColumnWidth()-2%>px;">
								<div class="gislcEntry" style="width:<%=theChip.getAdditionalColumnProvider().getAdditionalColumnWidth()-2%>px;">					
									<%=theChip.getAdditionalColumnProvider().getAdditionalColumnHeader()%>
								</div>
							</th>
<%
	}
%>
						</th>
						<th class="gislcIcon">
							<div class="gislcIcon">&nbsp;</div>
						</th>
<%
	for(final Iterator i = theChip.getChildNodes().iterator(); i.hasNext(); )
	{
		final Node childNode = (Node) i.next();
		
		if( childNode instanceof AttributeNode )
		{
	final String headerQualifier = ((AttributeNode) childNode).getAttributeQualifier();
	String header = theChip.getTitle(headerQualifier);
	String width = null;
	if( i.hasNext() && (theChip.getAttributeWidths().get(headerQualifier) != null) )
	{
		width = theChip.getAttributeWidths().get(headerQualifier).toString();
	}
%>
				<th<%= (width != null) ? (" style=\"width:" + width + "px\"") : "" %>>
<%
	if (theChip.isSortable(headerQualifier))
			{
%>
						<div class="gislcEntry" style="<%= (width != null) ? ("width:" + width + "px") : "" %>">
							<a class="sort" hidefocus="true" href="<%=getRequestURL()%>&#38;<%=theChip.getEventID(OrganizerListChip.SORT)%>=<%=headerQualifier%>" id="<%= theChip.getUniqueName() + "_" + headerQualifier %>_sort">
								<%=theChip.getTitle(headerQualifier)%>
<%
	if( headerQualifier.equals(theChip.getSortAttributeQualifier() ) )
						{
%>
									&nbsp;<img src="images/icons/arrow_<%= theChip.getSortOrderIdentifier() %>.gif" id="<%= theChip.getUniqueName() + "_" + headerQualifier %>_sortimg"/>
<%
	}
%>
							</a>
						</div>
<%
	}
			else
			{
%>
						<div class="gislcEntry" style="<%= (width != null) ? ("width:" + width + "px;") : "" %>"><%=theChip.getTitle(headerQualifier)%></div>
<%
	}
%>
				</th>
<%
	}
		else if( childNode instanceof ItemNode )
		{
	final ItemNode itemNode = (ItemNode) childNode;
		
	String width = (itemNode.getWidth() > 0 && i.hasNext()) ? ("width:" + itemNode.getWidth() + "px;") : "";
%>
				<th style="<%= width %>">
					<div class="gislcEntry" style="<%= width %>">
						<%=localized(itemNode.getTitle())%>
					</div>
				</th>
<%
	}
	}
%>
				</tr>
			</table>
			
			<div class="gislcResultList" id="resultlist_<%= theChip.getUniqueName() %>">
				<table id="table_<%= theChip.getID() %>" class="listtable selecttable<%= theChip.isMultipleSelectionAllowed() ? "" : " singleselect" %>" cellpadding="0" cellspacing="0">
				<tr style="display:none;">
					<td>
						<input name="<%=theChip.getEventID(GenericItemListChip.SELECT_VISIBLE)%>" value=""/>
					</td>
				</tr>
<%
	if( theChip.getListEntries().size()>0)
			{
				List entries=theChip.getListEntries();
				for (int i=0;i<entries.size();i++)
				{
					Chip chip = (Chip) entries.get(i);
%>
							<tr id="<%= chip.getCommandID(GenericItemListEntryChip.OPEN_EDITOR) %>" 
								 class="doubleclick-event <%= theChip.getParent() instanceof ModalGenericSearchChip ? theChip.getParent().getCommandID(ModalGenericSearchChip.USE) : chip.getCommandID(GenericItemListEntryChip.OPEN_EDITOR) %>"
								 onclick="">	<%-- mr: empty onclick is necessary for selenium test recording! --%>
								<td class="checkbox gislcCheckbox">
									<div>
										<input type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>" 
												 name="<%=theChip.getEventID(SearchListChip.SELECT)%>" 
												 id="<%= theChip.getEventID(SearchListChip.SELECT) + theChip.getPosition(chip) %>" 
												 value="<%= theChip.getPosition(chip) %>" 
												 <%=theChip.isListEntrySelected( chip )?" checked":""%>/>
									</div>
								</td>
								
<%
									if( theChip.getAdditionalColumnProvider()!=null)
														{
								%>
									<td style="width:<%=theChip.getAdditionalColumnProvider().getAdditionalColumnWidth()%>px; height:25px;" class="listtable">
										<div style="width:<%=theChip.getAdditionalColumnProvider().getAdditionalColumnWidth()%>px;">
											<%=theChip.getAdditionalColumnProvider().getAdditionalColumnValue(i)%>
										</div>
									</td>
<%
	}
						chip.render(pageContext);
%>
							</tr>
<%
	}
			}
			else
			{
%>
						<tr>
							<td colspan="<%= theChip.getHeaderCount() + 3 %>" >
<%
	if (theChip.isNewSearch())
						{
%>
									&nbsp;
<%
	}
						else
						{
%>
									<%=localized("searchresultempty")%>
<%
	}
%>
							</td>
						</tr>
<%
	}
%>
				</table>
			</div>
<%
	final boolean showScrollbars = ConfigConstants.getInstance().ENABLE_SCROLLBAR;
			if( showScrollbars )
			{
				int diffSize = theChip.getHeightDiff();
				int minheight = theChip.getMinListHeight();
				int maxheight = theChip.getMaxListHeight() + 16;
%>
						<script language="JavaScript1.2">
							document.getElementById( "resultlist_<%= theChip.getUniqueName() %>" ).style.height = getResultListHeightAbsolute( <%= diffSize %>, <%= minheight %>, <%= maxheight %> );
						</script>
<%
					}
%>
		</td>
	</tr>
	<tr>
		<td>
			<%@include file="../searchListChipRange.inc"%>
		</td>
	</tr>
<%
		if (theChip instanceof ModalGenericItemSearchListChip)
		{
%>
	<tr>
		<td class="padder">
			<table>
				<tr>
					<td>
						<div class="xp-button chip-event">
							<a href="#" title="<%= localized("button.use") %>" name="<%= theChip.getParent().getCommandID(ModalGenericSearchChip.USE) %>" hidefocus="true">
								<span class="label" id="<%= theChip.getUniqueName() %>_use">
									<%= localized("button.use") %>
								</span>
							</a>
						</div>
					</td>
					<td>
						<div class="xp-button chip-event">
							<a href="#" title="<%= localized("button.cancel") %>" name="<%= theChip.getParent().getCommandID(ModalGenericSearchChip.CANCEL) %>" hidefocus="true">
								<span class="label" id="<%= theChip.getUniqueName() %>_cancel">
									<%= localized("button.cancel") %>
								</span>
							</a>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
<%
		}
	}
%>
</table>
