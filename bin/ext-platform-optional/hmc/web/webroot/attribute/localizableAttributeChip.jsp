<%@include file="../head.inc"%>
<%
	final LocalizableAttributeChip theChip = (LocalizableAttributeChip) request.getAttribute(AbstractChip.CHIP_KEY);
	theChip.refreshEditors();
	boolean editable = theChip.isEditable();
	String tooltip = theChip.getToolTip();
	tooltip = editable ? tooltip : tooltip + " " + localized( "noteditable" );
	final boolean required = theChip.isRequired();
	final int labelWidth = theChip.getLabelWidth() > 0 || theChip.isHideLabel() ? theChip.getLabelWidth() : 100;
	final String label = theChip.isHideLabel() ? "" : escapeHTML(theChip.getLabel());
	final Map<Language, AbstractAttributeEditorChip> editors=theChip.getEditors();
	final Language curLanguage = theChip.getCurrentLanguage();
	final boolean expandable = theChip.isExpandable();
	final boolean isExpanded = theChip.isExpanded() && expandable;
%>

<table class="localAttributeChip" <%=(tooltip!=null?" title=\""+tooltip+"\"":"")%> cellspacing="0" cellpadding="0" 
<%
	if( theChip.isAjaxUpdatable() )
	{
%>
		id="<%= theChip.getID() %>_updater"
<%
	}
%>
>
	<tr>

		<!-- language expand/collapse button -->
		<td class="arrowButton" align="center">
<%
		if( expandable )
		{
			final String tenantIDStr = Registry.getCurrentTenant() instanceof SlaveTenant ?
				";tenantID="+Registry.getCurrentTenant().getTenantID() : "";
%>
			<div class="arrow-button <%= theChip.isAjaxUpdatable() ? "language-toggle" : "chip-event" %>" title="<%= isExpanded ? localized("collapse") : localized("expand") %>">
				<a href="#"
					class="<%= isExpanded ? "collapse" : "expand" %>" 
					hidefocus="true" 
					id="<%= theChip.getUniqueName() %>_languagetoggle"
					hidefocus="true"
					name="<%= theChip.isAjaxUpdatable() ? theChip.getID() : theChip.getCommandID(isExpanded ? LocalizableAttributeChip.COLLAPSE_LOCALIZE : LocalizableAttributeChip.EXPAND_LOCALIZE) %>">
					<%= theChip.isAjaxUpdatable() ?"<div class=\"tenantIDStr\">"+tenantIDStr+"</div>":""%>
				</a>
			</div>
<%
		}
%>
		</td>

		<!-- label -->
		<td class="label" style="width:<%= labelWidth + "px" %>">
			<div <%=(editable ? "" : "class=\"disabled\"") %> style="width:<%= labelWidth + "px" %>;<%= required ? "font-weight:bold;" : "" %>" >
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
			AbstractAttributeEditorChip editorChip;
%>
			<!-- language(s) and "required" symbol(s) -->
			<td class="localized">
				<table>
<%
					
					if( isExpanded )
					{
						
						// show entry for the current session language first
						if( editors.containsKey(curLanguage) )
						{
							editorChip = editors.get(curLanguage);
							editable = editorChip.isEditable();
%>
							<tr>
								<!-- language code -->
								<td class="langCode">
									<div class="<%= (editable ? "edit" : "noedit") %>">
										<%= curLanguage.getIsoCode() %>&nbsp;
									</div>
								</td>
								<!-- is changed sign -->
						 		<td class="isChangedSign">
						 			<div <%= editorChip.isChanged() ? "title=\""+localized("attribute.ischanged.tooltip")+"\"" : "" %>>
										<%= (editorChip.isChanged() ? "*" : "&nbsp;") %>
									</div>
						 		</td>
								<!-- attribute editor -->
								<td>
									<% editorChip.render(pageContext); %>
								</td>
							</tr>
<%
						}
						else
						{
%>
							<tr>
								<!-- language code -->
								<td class="langCode">
									<div class="<%= (editable ? "edit" : "noedit") %>">
										<%= curLanguage.getIsoCode() %>&nbsp;
									</div>
								</td>
								<!-- is changed sign -->
						 		<td class="isChangedSign">
						 		</td>
								<!-- attribute editor -->
								<td class="disabled">
									<%=theChip.getLocalizedString("attribute.localizable.notvisible",curLanguage.getIsoCode()) %>
								</td>
							</tr>
							<%
						}

						// now the entries for the remaining (non-session) languages
						for( Map.Entry<Language, AbstractAttributeEditorChip> entry: editors.entrySet() )
						{
							final Language language = entry.getKey();
							if( !curLanguage.equals(language) )
							{
								editorChip = entry.getValue();
								editable = editorChip.isEditable();
%>
								<tr>
									<!-- language code -->
									<td class="langCode">
										<div class="<%= (editable ? "edit" : "noedit") %>">
											<%= language.getIsoCode() %>
										</div>
									</td>
									<!-- is changed sign -->
							 		<td class="isChangedSign">
							 			<div <%= editorChip.isChanged() ? "title=\""+localized("attribute.ischanged.tooltip")+"\"" : "" %>>
											<%= (editorChip.isChanged() ? "*" : "&nbsp;") %>
										</div>
							 		</td>
									<!-- attribute editor -->
									<td>
										<% editorChip.render(pageContext); %>
									</td>
								</tr>
<%
							}
						}
					}
					else
					{
%>
						<tr>
							<!-- general language symbol -->
							<td class="langCode">
								<div <%=expandable?"class=\"button-on-white chip-event\"":""%>>
									<a href="#" <%=expandable?"title=\""+localized("expand")+"\"":""%>
										name="<%= theChip.getCommandID(LocalizableAttributeChip.EXPAND_LOCALIZE) %>"
										hidefocus="true"
										tabindex="100000"
									>
										<span>
											<img class="icon" src="images/icons/button_language.gif" id="<%= theChip.getUniqueName() %>_img"/>
										</span>
									</a>
								</div>
							</td>
<%
							// language is not expanded, show only entry for current session language
							editorChip = editors.get(curLanguage);
							if(editorChip!=null)
							{
%>			
								<!-- is changed sign -->
						 		<td class="isChangedSign">
						 			<div <%= theChip.isChanged() ? "title=\""+localized("attribute.ischanged.tooltip")+"\"" : "" %>>
										<%= (theChip.isChanged() ? "*" : "&nbsp;") %>
									</div>
						 		</td>
								<!-- attribute editor -->
								<td>
<%
									editorChip.render(pageContext);
%>
								</td>
<%
							}
							else
							{
%>
								<!-- is changed sign -->
						 		<td class="isChangedSign">
						 		</td>
								<!-- attribute editor -->
								<td class="disabled">
									<%=theChip.getLocalizedString("attribute.localizable.notvisible",curLanguage.getIsoCode()) %>
								</td>
<%
							}
%>
						</tr>
<%
					}
%>
				</table>
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
