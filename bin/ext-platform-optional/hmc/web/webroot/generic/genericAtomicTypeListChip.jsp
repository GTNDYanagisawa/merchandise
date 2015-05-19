<%@include file="../head.inc"%>
<%
	GenericAtomicTypeListChip theChip = (GenericAtomicTypeListChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String contextMenu = theChip.hasVisibleContextMenuEntries()
								? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
								: "return false;";
%>

<%
	if( (!theChip.isEditable()) && theChip.getListEntries().isEmpty() )
	{
		%>
			<div class="disabled"><%=localized("listisempty")%></div>
		<%
	}
	else
	{
%>
			<table class="genericAtomicTypeListChip" style="width:<%= theChip.getWidth() %>px;" cellspacing="0" cellpadding="0" oncontextmenu="<%= contextMenu %>">
				<tr>
					<td>
						<div class="gatlcResultList" id="resultlist_<%= theChip.getUniqueName() %>">
<%
		if( theChip.getListEntryCount()>12 && theChip.restrictHeight() )
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
<%
		if( theChip.isEditable() )
		{
%>
									<th class="gatlcCheckbox">
										<input name="SELECTOR" class="header" <%= theChip.isAllSelected() ? "checked" : "" %> onclick="document.editorForm.elements['<%=theChip.getCommandID(GenericListChip.SELECT_VISIBLE)%>'].value='<%= !theChip.isAllSelected()%>';setScrollAndSubmit();" type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>" name="" value="ALL" />
										<input type="hidden" name="<%=theChip.getCommandID(GenericListChip.SELECT_VISIBLE)%>" value=""/>
										<input type="hidden" name="<%=theChip.getEventID(SearchListChip.SELECT)%>" value="" />   <!-- dummy event to allow lists to be completely de-selected -->
									</th>
<%
		}

		if( theChip.isEntriesEditable() )
		{
%>
									<th class="gatlcIsChanged"><div class="gatlcIsChanged"> </div></th>
<%
		}
%>
									<th class="gatlcEntry" <%= !theChip.isEditable() ? "style=\"border-left:1px solid #bbbbbb;\"" : "" %>>
										<div class="gatlcEntry">
											<%= localized(theChip.getTitle()) %>
										</div>
									</th>
								</tr>
<%
		if( theChip.getListEntries().isEmpty() && theChip.getNewEntryChip() == null )
		{
%>
								<tr>
									<td class="disabled" colspan="3">
										<div class="disabled">
											<%= localized("listisempty") %>
										</div>
									</td>
								</tr>
<%
		}
		else
		{
			for (final Iterator theChips = theChip.getListEntries().iterator(); theChips.hasNext();)
			{
				Chip chip = (Chip)theChips.next();
%>
									<tr>
<%
				if (theChip.isEditable())
				{
%>
										<td class="gatlcCheckbox">
											<input onclick="document.editorForm.elements['SELECTOR'].checked=false;" type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>" name="<%=theChip.getEventID(SearchListChip.SELECT)%>" value="<%=theChip.getPosition(chip)%>" <%=theChip.isListEntrySelected( chip )?" checked":""%>/>
										</td>
<%
				}
				if( theChip.isEntriesEditable() )
				{
%>
										<td class="gatlcIsChanged">
											<div>
												<%= ((GenericEditableListEntryChip) chip).getEditorChip().isChanged() ? "*" : "" %>
											</div>
										</td>
<%
				}
%>
										<td style="width:<%= theChip.getWidth() - 50 %>px; border-right:1px solid #bbbbbb; <%= !theChip.isEditable() ? "border-left:1px solid #bbbbbb;" : "" %>">
											<div style="width:<%= theChip.getWidth() - 50 %>px; overflow:hidden;">
<%
				chip.render(pageContext);
%>
											</div>
										</td>
									</tr>
<%
			}
		}




		if( theChip.isEditable() && theChip.getNewEntryChip() != null )
		{
%>
					<tr>
						<td style="width:20px; border-left:1px solid #bbbbbb;" <%= theChip.isEntriesEditable() ? "colspan=\"2\"" : ""%>>
							<div style="width:15px;"/>
						</td>
						<td style="text-align:left; border-right:1px solid #bbbbbb;">
								<% theChip.getNewEntryChip().render(pageContext); %>
						</td>
					</tr>
<%
		}



%>
							</table>
						</div>
						</td>
					</tr>
				</table>
<%
	}
%>
