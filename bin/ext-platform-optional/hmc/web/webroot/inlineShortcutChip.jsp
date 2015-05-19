<%@include file="head.inc"%>
<%
	InlineShortcutChip theChip = (InlineShortcutChip) request.getAttribute(InlineShortcutChip.CHIP_KEY);

	final String label = theChip.isHideLabel() ? "" : escapeHTML(theChip.getName());
	final int labelWidth = theChip.getLabelWidth() > 0 || theChip.isHideLabel() ? theChip.getLabelWidth() : 100;

	GenericItemListChip editor = theChip.getListChip();
	
	String widthStyle = "width:" + labelWidth + "px;";
	String labelTdStyle = "style=\"" + widthStyle + "overflow:hidden;\"";
	String labelDivStyle = "style=\"" + widthStyle + "overflow:hidden;";
	labelDivStyle += "\"";
	
%>
<table class="attributeChip" cellspacing="0" cellpadding="0">
	<tr>

		<!-- language expand/collapse button -->
		<td class="arrowButton">
			<div class="arrowButton">&nbsp;</div>
		</td>
		
		<!-- label -->
 		<td <%= labelTdStyle %>>
 			<div <%= labelDivStyle %>>
 				<%= label.length() > 0 ? (localized(label) + ":") : "" %>
 			</div>
 		</td>
<%
 			if( theChip.hasError() )
 			{
%>
				<!-- error -->
				<td class="errorspace">
					<div class="errorspace">&nbsp;</div>
				</td>
				<td class="error">
					<%= localized("attributeerror") + ":&nbsp;" + HMCHelper.getCoreExceptionMessage( theChip.getDisplayState(), theChip.getError() ) %>
				</td>
<%
 			}
 			else
 			{
%>
				<!-- no localization flag -->
				<td class="noLocalizationFlag">
					<div class="noLocalizationFlag">&nbsp;</div>
				</td>
						<!-- is changed sign -->
		 		<td class="isChangedSign">
		 		</td>
				
						<!-- attribute editor -->
				<td style="width:<%= editor.getWidth() %>px;">
					<% editor.render(pageContext); %>
				</td>
<%
 			}
%>
	</tr>
</table>
