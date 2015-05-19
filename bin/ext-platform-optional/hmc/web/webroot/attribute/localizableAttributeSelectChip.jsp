<%@include file="../head.inc"%>
<%
	final LocalizableAttributeSelectChip theChip = (LocalizableAttributeSelectChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String label = escapeHTML(theChip.getLabel());
	final String labelWidth = theChip.getLabelWidth() > 0 || theChip.isHideLabel() ? (theChip.getLabelWidth() - 18 + "px") : "82px";
%>
<table class="localAttributeChip" title="<%= theChip.getToolTip() %>" cellspacing="0" cellpadding="0">
	<tr>
		<!-- label -->
 		<td style="width:20px;">
 			<div style="width:20px;padding-left:10px;" <%= theChip.isChanged() ? "title=\"" + localized("attribute.ischanged.tooltip") + "\"" : "" %>>
				<%= (theChip.isChanged() ? "*" : "&nbsp;") %>
			</div>
 		</td>
		<td class="label" style="width:<%= labelWidth  %>;">
			<div <%= theChip.isEditable() ? "" : "class=\"disabled\"" %> style="width:<%= labelWidth %>;<%= theChip.isRequired() ? "font-weight:bold;" : "" %>" >
				<%= label.length() > 0 ? (label + ":") : "" %>
			</div>
		</td>
<%
		if( theChip.hasError() )
		{
%>
			<td class="errorspace">
				<div class="errorspace">
				</div>
			</td>
			<td class="error">
				<%= "&nbsp;" + localized("attributeerror") +
					 ":&nbsp;" + HMCHelper.getCoreExceptionMessage( theChip.getDisplayState(), theChip.getError() )
					 %>
			</td>
<%
		}
		else
		{
%>
			<td style="width:40px;padding-top:1px;">
<%
				theChip.getLanguageSelectEditorChip().render(pageContext);
%>
			</td>
<%
			final AbstractAttributeEditorChip editorChip = theChip.getEditor(theChip.getSelectedLanguage());
%>
			<!-- is changed sign -->
	 		<td class="isChangedSign">
	 			<div <%= editorChip.isChanged() ? "title=\"" + localized("attribute.ischanged.tooltip") + "\"" : "" %>>
					<%= (editorChip.isChanged() ? "*" : "&nbsp;") %>
				</div>
	 		</td>
			<td style="padding-left:2px;">
<%	
				editorChip.render(pageContext); 
%>
			</td>
<%
		}
		if( !"".equals(theChip.getDescriptionAttributeValue()) )
		{
%>
	 		<td class="description">
				<span class="description">
			 		&nbsp;<%= theChip.getDescriptionAttributeValue() %>
				</span>
			</td>
<%
		}
		if( !"".equals(theChip.getDescription()) )
		{
%>
	 		<td class="description">
				<span class="description">
			 		&nbsp;<%= theChip.getDescription() %>
				</span>
			</td>
<%
		}
%>
	</tr>
</table>
