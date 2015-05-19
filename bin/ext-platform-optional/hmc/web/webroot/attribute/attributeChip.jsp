<%@include file="../head.inc"%>
<%
	AttributeChip theChip = (AttributeChip) request.getAttribute(AbstractChip.CHIP_KEY);
	boolean editable = theChip.isEditable();
	String tooltip = theChip.getToolTip();
	AbstractAttributeEditorChip editor = theChip.getEditor();
	boolean required = theChip.isRequired() && !(editor instanceof BooleanEditorChip);
	tooltip = editable ? tooltip : tooltip + " " + localized( "noteditable" );
	
	AttributeDescriptor ad = theChip.getAttributeDescriptor();
	final String label = theChip.isHideLabel() ? "" : escapeHTML(theChip.getLabel());
	final int labelWidth = theChip.getLabelWidth() > 0 || theChip.isHideLabel() ? theChip.getLabelWidth() : 100;
	final boolean errorLabel = theChip.hasError() || theChip.hasErrorText();
	
	String widthStyle = "width:" + labelWidth + "px;";
	
	String labelTdStyle = "style=\"" + widthStyle + "overflow:hidden;\"";

	String labelDivStyle = "style=\"" + widthStyle + "overflow:hidden;\"";

	final StringBuilder labelDivClass = new StringBuilder("class=\""); 
	if ( required ) labelDivClass.append("mandatory ");
	if ( !editable ) labelDivClass.append("disabled ");
	if ( errorLabel ) labelDivClass.append("error ");
	labelDivClass.append("\"");
	
	
%>
<table class="attributeChip" <%= (tooltip != null ? " title=\"" + tooltip + "\"" : "")%> cellspacing="0" cellpadding="0">
	<tr>

		<!-- language expand/collapse button -->
		<td class="arrowButton">
			<div class="arrowButton">&nbsp;</div>
		</td>
		
		<!-- label -->
 		<td <%= labelTdStyle %> class="label">
 			<div <%= labelDivStyle %> <%= labelDivClass.toString() %>>
 				<%= label.length() > 0 ? (label + ":") : "" %>
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
			<div class="isChangedSign" <%= theChip.isChanged() ? "title=\""+localized("attribute.ischanged.tooltip")+"\"" : "" %>>
						<%= (theChip.isChanged() ? "*" : "&nbsp;") %>
			</div>
 		</td>
		
				<!-- attribute editor -->
		<td style="width:<%= editor.getWidth() %>px;">
			<% editor.render(pageContext); %>
		</td>
<%
 			}
 		if( !"".equals(theChip.getDescriptionAttributeValue()) )
 		{
%>
		<!-- description -->
		<td style="description">
			<div style="description">
				<%= theChip.getDescriptionAttributeValue() %>
			</div>
		</td>
<%
		}
		if( !"".equals(theChip.getDescription()) )
		{
%>
		<!-- description -->
 		<td style="description">
			<div style="description">
				<%= theChip.getDescription() %>
			</div>
		</td>
<%
		}
%>
	</tr>
</table>
