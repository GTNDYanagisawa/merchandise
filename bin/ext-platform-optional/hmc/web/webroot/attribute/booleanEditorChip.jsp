<%@include file="../head.inc"%>
<%
	final BooleanEditorChip theChip = (BooleanEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final String name = theChip.getEventID(BooleanEditorChip.SET_VALUE);
	final boolean checked = Boolean.TRUE.equals(theChip.getBooleanValue());
	final boolean not_checked = Boolean.FALSE.equals(theChip.getBooleanValue());
	final boolean editable = theChip.isEditable();
	final String trueName = localized(theChip.getTrueName());
	final String falseName = localized(theChip.getFalseName());
	final String nullName = localized(theChip.getNullName());
	final boolean useSelectBox = theChip instanceof AdvanceBooleanEditorChip ? ((AdvanceBooleanEditorChip) theChip).useSelectBox() : false;

	String onKeyPressString = "";
	
	
	String style = "style=\"width:" + theChip.getWidth() + ";\"";
	
	
	if( theChip.getParent() instanceof FlexibleConditionChip )
	{
		onKeyPressString = "onkeypress=\"return checkForEnter(event);\"";
	}
	else
	{
		onKeyPressString = "onkeypress=\"return true;\"";
	}
%>
	<div class="booleanEditorChip" <%= style %>>
<%
		if( useSelectBox )
		{
%>
			<select class="<%= editable ? "enabled" : "disabled" %>" name="<%= name %>" <%= onKeyPressString %> <%= editable ? "" : "disabled" %> id="<%= theChip.getUniqueName() %>_select">
<%
				if( theChip.isOptional() || theChip.getValue() == null )
				{
%>
					<option value="" <%= theChip.getValue() == null ? "selected" : "" %>>
						<%= nullName %>
					</option>
<%
				}
%>
				<option value="<%= Boolean.TRUE.toString() %>" <%= checked ? "selected" : "" %>>
					<%= trueName %>
				</option>
				<option value="<%= Boolean.FALSE.toString() %>" <%= not_checked ? "selected" : "" %>>
					<%= falseName %>
				</option>
			</select>
<%
		}
		else
		{
			if( !theChip.isOptional() && BooleanEditorChip.CHECKBOX.equals(theChip.getMandatoryView()) )
			{
%>
				<input type="hidden" name="<%= name %>" value="<%= checked ? Boolean.TRUE.toString() : Boolean.FALSE.toString() %>"/>
				<input 
					class="<%= editable ? "enabled" : "disabled" %>"
					type="checkbox" 
					value="" 
					id="<%= theChip.getUniqueName() %>_checkbox"
					<%= onKeyPressString %> 
					onclick="document.editorForm.elements['<%= name %>'].value=this.checked;" 
					<%= checked ? " checked" : "" %> 
					<%= editable ? "" : "disabled" %> />
<%
			}
			else
			{
%>
				<input
					type="radio"
					name="<%= name %>"
					id="<%= theChip.getUniqueName() %>_true"
					value="<%= Boolean.TRUE.toString() %>"
					<%= checked ? " checked" : "" %>
					<%= onKeyPressString %>
					onkeydown="return moveFocus(event);"
					<%= editable ? "" : "disabled"%>/>
				<span <%= editable ? "onclick=\"document.editorForm.elements['" + theChip.getUniqueName() +"_true'].checked='true';\"" : "" %> id="<%= theChip.getUniqueName() %>_spantrue">
					<%= trueName %>
				</span>
				<%= theChip.isWrapValues() ? "<br>" : "&nbsp;" %>
				<input 
					type="radio" 
					name="<%= name %>"
					id="<%= theChip.getUniqueName() %>_false"
					value="<%= Boolean.FALSE.toString() %>"
					<%= not_checked ? " checked" : "" %>
					<%= onKeyPressString %>
					<%= editable ? "" : "disabled" %>/> 
				<span <%= editable ? "onclick=\"document.editorForm.elements['" + theChip.getUniqueName() + "_false'].checked='true';\"" : "" %> id="<%= theChip.getUniqueName() %>_spanfalse">
					<%= falseName %>
				</span>
<%
				if( theChip.isOptional() )
				{
%>
					<%= theChip.isWrapValues() ? "<br>" : "&nbsp;" %>
					<input 
						type="radio"
						name="<%= name %>"
						id="<%= theChip.getUniqueName() %>_null"
						value=""
						<%= theChip.getValue() == null ? "checked" : "" %>
						<%= onKeyPressString %>
						<%= editable ? "" : "disabled" %>/>
					<span <%= editable ? "onclick=\"document.editorForm.elements['" + theChip.getUniqueName() + "_null'].checked='true';\"" : "" %> id="<%= theChip.getUniqueName() %>_spannull">
						<%= nullName %>
					</span>
<%
				}
			}
		}
%>
	</div>
