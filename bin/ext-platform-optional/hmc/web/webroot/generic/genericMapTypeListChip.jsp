<%@include file="../head.inc"%>
<%
	GenericMapTypeListChip theChip = (GenericMapTypeListChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String contextMenu = theChip.hasVisibleContextMenuEntries()
						? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
						: "return false;";

	int keyWidth = theChip.getKeyWidth();
	int valueWidth = theChip.getValueWidth();
%>

<%
	if( (!theChip.isEditable()) && theChip.getListEntries().isEmpty() )
	{
%>
			<font class="disabled"><%=localized("listisempty")%></font>
		<%
			}
			else
			{
				final boolean showScrollbars = ConfigConstants.getInstance().ENABLE_SCROLLBAR;
		%>
			<table class="genericMapTypeListChip" style="width:<%= theChip.getWidth() %>px;" cellspacing="0" cellpadding="0" oncontextmenu="<%= contextMenu %>">
				<tr>
					<td>
						<div class="gmtlcResultList" <%=((showScrollbars && theChip.restrictHeight() && theChip.getListEntryCount()>12)?" style=\"height:300px\"":"")%>>
							<table class="listtable" cellpadding="0" cellspacing="0">
								<tr>
<%
		if( theChip.isEditable() )
		{
%>
									<th class="gmtlcCheckbox">
										<div class="gmtlcCheckbox">
											<input name="SELECTOR" class="header" <%= theChip.isAllSelected() ? "checked" : "" %> onclick="document.editorForm.elements['<%=theChip.getCommandID(GenericListChip.SELECT_VISIBLE)%>'].value='<%= !theChip.isAllSelected()%>';setScrollAndSubmit();" type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>" name="" value="ALL" />
											<input type="hidden" name="<%=theChip.getCommandID(GenericListChip.SELECT_VISIBLE)%>" value=""/>
											<input type="hidden" name="<%=theChip.getEventID(SearchListChip.SELECT)%>" value="" />   <!-- dummy event to allow lists to be completely de-selected -->
										</div>
									</th>
<%
		}
%>
									<th class="gmtlcEntry" colspan="2" style="width:<%= keyWidth %>px;">
										<div class="gmtlcEntry" style="width:<%= keyWidth %>px;">
											<%= localized(theChip.getKeyName()) %>
										</div>
									</th>
									<th class="gmtlcEntry2">
										<div class="gmtlcEntry">
											<%= localized(theChip.getValueName()) %>
										</div>
									</th>
								</tr>
<%
		if( theChip.getListEntries().isEmpty() && theChip.getKeyEditor() == null )
		{
%>
								<tr>
									<td colspan="4" class="disabled">
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
									<td class="gmtlcCheckbox">
										<div class="gmtlcCheckbox">
											<input onclick="document.editorForm.elements['SELECTOR'].checked=false;" type="<%= ( theChip.isMultipleSelectionAllowed() ? "checkbox" : "radio" ) %>" name="<%=theChip.getEventID(SearchListChip.SELECT)%>" value="<%=theChip.getPosition(chip)%>" <%=theChip.isListEntrySelected( chip )?" checked":""%>/>
										</div>
									</td>
<%
				}
				chip.render(pageContext);
%>
								</tr>
<%
			}
			if( theChip.isEditable() && theChip.getKeyEditor() != null)
			{
				// add empty list entry
%>
								<tr>
									<td class="gmtlcCheckbox">
										<div class="gmtlcCheckbox"> </div>
									</td>
									<td class="gmtlcEntry" colspan="2" style="width:<%= keyWidth %>px;">
										<div class="gmtlcEntry" style="width:<%= keyWidth %>px;">
											<% theChip.getKeyEditor().render(pageContext); %>
										</div>
									</td>
									<td class="gmtlcEntry2" style="width:<%= valueWidth %>px;">
										<div class="gmtlcEntry2" style="width:<%= valueWidth %>px;">
											<% theChip.getValueEditor().render(pageContext); %>
										</div>
									</td>
								</tr>
<%
			}
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
