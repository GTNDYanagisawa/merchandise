
<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.catalog.hmc.ClassificationAttributeChip" %>

<%
	final ClassificationAttributeChip theChip = (ClassificationAttributeChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String contextMenu = theChip.hasVisibleContextMenuEntries()
										? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
										: "return false;";

	final String label = escapeHTML(theChip.getLabel());
	final int labelWidth = theChip.getLabelWidth();
	final String tooltip = theChip.getTooltip();

	if( theChip.hasError() )
	{
%>
		<div class="error">
			<%= theChip.getErrorMessage() %>
		</div>
<%
	}
	else
	{
%>
		<table class="attribute" cellspacing="0" cellpadding="0" <%= (tooltip != null ? " title=\"" + tooltip + "\"" : "") %>>
			<tr>
				<td style="width:20px">
					<div style="width:20px;"></div>
				</td>

		<!-- label -->
		 		<td style="width:<%= labelWidth %>px;">
		 			<div <%= (theChip.isEditable() ? "" : "class=\"disabled\"") %> 
		 				  style="width:<%= labelWidth %>px;overflow:hidden;<%= !theChip.containsValidValue() ? "color:red;" : "" %>white-space:normal;<%= theChip.isMandatory() ? "font-weight:bold;" : "" %>"
		 				  class="chip-event" oncontextmenu="<%= contextMenu %>">
						<a href="#" 
							title="<%= localized("classification.editor.open.attribute") %>" 
							name="<%= theChip.getCommandID(ClassificationAttributeChip.OPEN_ATTRIBUTE_EXTERNAL) %>"
							style="color:#333333;">
			 				<%= label.length() > 0 ? (label + ":") : "" %>
			 			</a>
		 			</div>
		 		</td>
				<td style="width:17px">
					<div style="width:17px">
					</div>
				</td>

		<!-- 'is changed' sign -->
		 		<td style="width:10px; text-align:center;">
					<div style="width:10px">
						<%= theChip.isChanged() ? "*" : "&nbsp;" %>
					</div>
		 		</td>

			<!-- attribute editor -->
 				<td style="vertical-align:top;">
 					<% theChip.getEditorChip().render(pageContext); %>
 				</td>
			</tr>
		</table>		
<%
	}
%>
