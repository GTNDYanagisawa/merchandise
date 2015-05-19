<%@include file="../../head.inc"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="de.hybris.platform.hmc.Constants"%>
<%
	OrganizerListChip theChip= (OrganizerListChip) request.getAttribute(AbstractChip.CHIP_KEY);

	boolean isScrollHeader = theChip.isScrollHeader();
	boolean treemode = theChip.getMode() == Constants.LISTMODES.TREE;
	int entryCount = theChip.getListEntryCount();
	boolean showScrollbars = theChip.showScrollbar();
%>
<script language="JavaScript1.2">
<%
	if( theChip.hasVisibleContextMenuEntries() )
	{
%>
		var organizerListContextMenu = new Menu(<%= theChip.createMenuEntriesForJS(theChip.getMenuEntries()) %>, null, null, null, { uniqueName: '<%= theChip.getUniqueName() %>'} );
<%
	}
	else
	{
%>
		var organizerListContextMenu = null;
<%
	}
%>
</script>
<table class="organizerListChip" id="<%= theChip.getUniqueName() %>_outertable" cellpadding="0" cellspacing="0" oncontextmenu="if( organizerListContextMenu != null ) { organizerListContextMenu.show(event); } return false;">
	<col width="100%"/>
	<tr align="left" valign="top">
		<td><% theChip.getToolbar().render( pageContext ); %></td>
	</tr>
	<tr>
		<td>
<%
			if( isScrollHeader )
			{
%>
				<div class="olcResultList" id="resultlist_<%= theChip.getUniqueName() %>">
<%
			}
			
			if( !treemode )
			{
				if( isScrollHeader )
				{
%>
					<table id="<%= theChip.getUniqueName() %>_innertable" class="<%= treemode ? "treetable" : "listtable selecttable" + (theChip.isMultipleSelectionAllowed() ? "" : " singleselect") %>" cellpadding="0px" cellspacing="0px">

<%
				}
				else
				{
%>
					<table class="listtable" cellpadding="0" cellspacing="0">
<%
				}
%>
				<tr>
					<th class="checkbox olcCheckbox">
							<input name="SELECTOR" <%= theChip.isAllSelected() ? "checked" : "" %> 
									 type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>" 
									 name="" value="ALL" title="<%= localized("select.all.tooltip") %>"
									 />
							<input type="hidden" name="<%=theChip.getEventID(ListChip.SELECT)%>" value="" />   <!-- dummy event to allow lists to be completely de-selected -->
					</th>
					<th class="olcIcon" <%= isScrollHeader ? "colspan=\"2\"" : "" %>>
						<div class="olcIcon">&nbsp;</div>
					</th>
<%
						for( final Iterator i = theChip.getChildNodes().iterator(); i.hasNext(); )
						{
							final Node childNode = (Node) i.next();
							
							if( childNode instanceof AttributeNode )
							{
								final String headerQualifier = ((AttributeNode) childNode).getAttributeQualifier();
								
								String width = null;
								if( (isScrollHeader || i.hasNext()) && (theChip.getAttributeWidths().get(headerQualifier) != null) )
								{
									width = theChip.getAttributeWidths().get(headerQualifier).toString();
								}
%>
								<th<%= (width != null) ? (" style=\"width:" + width + "px\"") : "" %>>
<%
										if( theChip.isSortable(headerQualifier) )
										{
%>
											<div class="olcEntry" style="<%= (width != null) ? ("width:" + width + "px") : "" %>">
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
												<div class="olcEntry" style="<%= (width != null) ? ("width:" + width + "px;") : "" %>"><%=theChip.getTitle(headerQualifier)%></div>
<%
										}
%>
								</th>
<%
							}
							else if( childNode instanceof ItemNode )
							{
								final int itemWidth = i.hasNext() ? ((ItemNode) childNode).getWidth() : 0;
								final String itemTitle = ((ItemNode) childNode).getTitle();
%>
									<th style="<%= itemWidth > 0 ? "width:" + itemWidth + "px;" : "" %>">
										<div class="olcEntry" style="<%= itemWidth > 0 ? "width:" + itemWidth + "px;" : "" %>"><%= localized(itemTitle) %></div>
									</th>
<%
							}
						}
%>
				</tr>
<%
				if( !isScrollHeader )
				{
%>
 			</table>
<%
				}
			}
			else
			{
%>
			<table class="treetable" cellpadding="0" cellspacing="0">
				<tr>
					<th class="olcCheckbox">
							<input name="SELECTOR" class="header" <%= theChip.isAllSelected() ? "checked" : "" %> onclick="document.editorForm.elements['<%=theChip.getEventID(GenericItemListChip.SELECT_VISIBLE)%>'].value='<%= !theChip.isAllSelected()%>';setScrollAndSubmit();" type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>" name="" value="ALL" />
							<input type="hidden" name="<%=theChip.getEventID(GenericItemListChip.SELECT_VISIBLE)%>" value=""/>
							<input type="hidden" name="<%=theChip.getEventID(ListChip.SELECT)%>" value="" />   <!-- dummy event to allow lists to be completely de-selected -->
					</th>
					<th class="olcIcon">
						<div class="olcIcon">&nbsp;</div>
					</th>
					<th>
						<div class="olcEntry"><%= localized("items") %></div>
					</th>
				</tr>
<%
				if( !isScrollHeader )
				{
%>
			</table>
<%
				}
			}
			if( !isScrollHeader )
			{
				
%>
				<div class="olcResultList" id="resultlist_<%= theChip.getUniqueName() %>">
<%		
			}
			int diffSize = 300;

			if( theChip.getParent().getParent() instanceof OrganizerChip )
			{
				OrganizerChip organizer = (OrganizerChip) theChip.getParent().getParent();
				OrganizerComponentChip component = organizer.getSearchComponent();
				
				if( (component != null) && component.isExpanded() && (organizer.getSearchChip() != null) )
				{
					diffSize = (organizer.getSearchChip().getListEntryCount() * 25) - (isScrollHeader ? 25 : 0);
					if( theChip.isEditing() )
					{
						diffSize += 520;
					}
					else
					{
						diffSize += 430;
					}
				}
			}
			
			if( treemode )
			{
				String height = (entryCount == 0) ? "21" : ("getResultListHeightAbsolute(" + diffSize + ", 100, 10000)");
				if( showScrollbars )
				{
%>
					<script language="JavaScript1.2">
						document.getElementById( "resultlist_<%= theChip.getUniqueName() %>" ).style.height = <%= height %>;
					</script>
<%
				}
			}
			else
			{
				int minheight = theChip.getMinListHeight() + (isScrollHeader ? 25 : 0);
				int maxheight = theChip.getMaxListHeight() + 16 + (isScrollHeader ? 25 : 0);
				
				if( showScrollbars )
				{
%>
					<script language="JavaScript1.2">
						document.getElementById( "resultlist_<%= theChip.getUniqueName() %>" ).style.height=getResultListHeightAbsolute(<%= diffSize %>, <%= minheight %>, <%= maxheight %>);
					</script>
<%
				}
			}

			if( !isScrollHeader )
			{
%>
	 			<table id="<%= theChip.getUniqueName() %>_innertable" class="<%= treemode ? "treetable" : "listtable selecttable" + (theChip.isMultipleSelectionAllowed() ? "" : " singleselect") %>" cellpadding="0px" cellspacing="0px">
<%
			}
%>
					<tr style="display:none;">
						<td>
							<input name="<%=theChip.getEventID(GenericItemListChip.SELECT_VISIBLE)%>" value=""/>
						</td>
					</tr>
				
<%
						if( !theChip.getListEntries().isEmpty() )
						{
							for (final Iterator theChips = theChip.getListEntries().iterator(); theChips.hasNext();)
							{
								final OrganizerListEntryChip chip = (OrganizerListEntryChip)theChips.next();
								chip.setLast( !theChips.hasNext() );
%>
									<tr id="<%= chip.getUniqueName() %>_tr" class="doubleclick-event <%= chip.getCommandID(GenericItemListEntryChip.OPEN_EDITOR) %> <%= theChip.isListEntrySelected(chip) ? "selected" : "" %>" onclick="" ondblclick="">	<%-- mr: empty onclick is necessary for selenium test recording! --%>
										<td class="checkbox olcCheckbox">
											<div>
												<input class="header"
														 type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>"
														 name="<%=theChip.getEventID(ListChip.SELECT)%>"
														 value="<%=chip.getPosition()%>"
														 <%=theChip.isListEntrySelected( chip )?" checked":""%>/>
											</div>
										</td>
										<% chip.render(pageContext); %>
									</tr>
<%
							}
						}
						else
						{
%>
								<tr>
									<td class="olcEmpty" colspan="<%= theChip.getHeaderCount() + 3 %>" >
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
<%@include file="../../searchListChipRange.inc"%></th>

<%
				if (theChip.isEditing() && theChip.getSearchableColumns().size()>0)
				{
%>
						<div class="isEditing" align="center">
							<table class="isEditing" cellspacing="0" cellpadding="0">
								<tr class="rowToolbar">
									<td>
										<table cellspacing="0" cellpadding="0">
											<tr>
												<td colspan="20">
													<table class="toolbar" cellspacing="0" cellpadding="0">
														<tr>
															<td class="topCorner"><img src="images/bluebar_corner_small_ul.gif" border="0"/></td>
															<td class="middleBlue"></td>
															<td class="topCorner"><img src="images/bluebar_corner_small_ur.gif" border="0"/></td>
														</tr>
													</table>
												</td>
											</tr>
											<tr class="tabRow">
<%
												for (int i=0; i<theChip.getOperationChips().length; i++)
												{
													NamedChip chip = theChip.getOperationChips()[i];
													boolean active = chip.equals(theChip.getActiveOperationChip());
													if (active)
													{
%>
														<span class="editortab_active">
															<td class="topCorner"><img src="images/editortab_left.gif"></td>
															<td class="topMiddle"><%= localized(chip.getName()) %></td>
															<td class="topCorner"><img src="images/editortab_right.gif"></td>
														</span>
<%
													}
													else
													{
%>
														<span class="editortab">
															<td class="topCorner"><img src="images/editortab_left.gif"></td>
															<td class="topMiddle">
																<div class="chip-event">
																	<a href="#" title="<%= localized(chip.getName()) %>" hidefocus="true" class="editortab"
																		name="<%= chip.getCommandID(ListOperationChip.SELECT) %>" id="<%= chip.getUniqueName() %>_tab">
																		<%= localized(chip.getName()) %>
																	</a>
																</div>
															</td>
															<td class="topCorner"><img src="images/editortab_right.gif"></td>
														</span>
<%
													}
												}
%>
												<td class="tabRowRest">&nbsp;</td>
											</tr>
										</table>
									</td>
								</tr>

								<tr>
									<td class="rowContent">
<%
											theChip.getActiveOperationChip().render(pageContext);
%>
									</td>
								</tr>

								<tr class="rowFooter">
									<td colspan="20">
										<table class="footer" cellspacing="0" cellpadding="0">
											<tr>
												<td class="bottomCorner"><img src="images/editortab_corner_bl.gif"/></td>
												<td class="middle"></td>
												<td class="bottomCorner"><img src="images/editortab_corner_br.gif"/></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</div>
<%
				}
%>
		</td>
	</tr>
</table>
<script language="JavaScript1.2">
	addKeyFunction("A", function() { setEvent("<%= theChip.getEventID(GenericItemListChip.SELECT_VISIBLE) %>"); setScrollAndSubmit(); return false;});
</script>
