<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.commons.hmc.DocumentEditorChip"%>
<%
	DocumentEditorChip theChip = (DocumentEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);	
	
	final String labelWidth = theChip.getLabelWidth() + "px";
	final String formatsWidth = theChip.getFormatsWidth() + "px";
	final String documentsWidth = theChip.getDocumentsWidth() + "px";

	final boolean editable = theChip.isEditable();
%>
<table class="documentEditorChip" cellpadding="0" cellspacing="0">
	<tr>
		<td class="arrowButton">
			<div style="arrow-button">&nbsp;</div>
		</td>

 		<td class="label" style="width:<%= labelWidth %>">
 			<div <%= (editable ? "" : "class=\"disabled\"") %> style="width:<%= labelWidth %>;">
 				<%= localized("tab.documents.label.formats") %>:
 			</div>
 		</td>
 		
		<td class="noLocalizationFlag">
			<div style="noLocalizationFlag">&nbsp;</div>
		</td>

 		<td class="isChangedSign">
			<div style="isChangedSign">&nbsp;</div>
 		</td>
 		
		<td style="width:<%= formatsWidth %>">
<%
			if( theChip.getFormatSelectionEditorChip() != null )
			{
				theChip.getFormatSelectionEditorChip().render(pageContext);
			}
			else
			{
%>
				<%= localized("notdefined") %>
<%
			}
%>
		</td>
		
		<td>
			<div class="buttons">
<%
			if( theChip.isCreateButtonActive() )
			{
%>
				<div class="xp-button chip-event">
					<a class="createButton" href="#" title="<%= localized("button.create.document.tooltip") %>" name="<%= theChip.getCommandID(DocumentEditorChip.CREATE_DOCUMENT) %>" hidefocus="true">
						<span class="label">
							<%= localized("button.create.document") %>
						</span>
					</a>
				</div>
<%
			}
			else
			{
%>
				<div class="xp-button-disabled">
					<a class="createButton" href="#" title="<%= localized("button.create.document.tooltip") %>">
						<span class="label">
							<%= localized("button.create.document") %>
						</span>
					</a>
				</div>
<%
			}
%>
			</div>
		</td>
	</tr>

	<tr>
		<td class="arrowButton">
			<div class="arrow-button">&nbsp;</div>
		</td>
		
		<td class="label" style="width:<%= labelWidth %>;">
			<div style="width:<%= labelWidth %>;">
				<%= localized("tab.documents.label.documents") %>:
			</div>
		</td>
		
		<td class="noLocalizationFlag">
			<div style="noLocalizationFlag">&nbsp;</div>
		</td>

 		<td class="isChangedSign">
			<div class="isChangedSign">
				<%= (theChip.isChanged() ? "*" : "&nbsp;") %>
			</div>
 		</td>
		<td colspan="2" style="width:<%= documentsWidth %>">
<%
			if( theChip.getDocumentListChip() != null )
			{
				theChip.getDocumentListChip().render(pageContext);
			}
			else
			{
%>
				<%= localized("notdefined") %>
<%
			}
%>
		</td>
	</tr>
</table>
