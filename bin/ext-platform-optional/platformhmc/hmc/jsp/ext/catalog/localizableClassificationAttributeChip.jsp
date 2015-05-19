
<%@include file="../../head.inc" %>
<%@page import="de.hybris.platform.catalog.hmc.ClassificationAttributeChip,
					 de.hybris.platform.catalog.hmc.AbstractClassificationAttributeEditorChip" %>
<%
	ClassificationAttributeChip theChip = (ClassificationAttributeChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String label = escapeHTML(theChip.getLabel());
	final int labelWidth = theChip.getLabelWidth();
	final boolean mandatory = theChip.isMandatory();
	final boolean hasError = theChip.hasError();
	final String errorMessage = theChip.getErrorMessage();
	final Map<Language, AbstractClassificationAttributeEditorChip> editorChips = theChip.getEditorChips();
	final String tooltip = theChip.getTooltip();
	boolean editable = theChip.isEditable();
	final boolean invalidValue = !theChip.containsValidValue();
	final boolean expanded = theChip.isExpanded();
	final String contextMenu = theChip.hasVisibleContextMenuEntries()
										? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
										: "return false;";

	if( hasError )
	{
%>
		<div class="error">
			<%= errorMessage %>
		</div>
<%
	}
	else
	{
%>
		<table class="attribute" cellpadding="0" cellspacing="0" <%= (tooltip != null ? " title=\"" + tooltip + "\"" : "") %>>
			<tr>
				
		<!-- language expand/collapse button -->
				<td style="width:16px; vertical-align:top; padding-left:4px;">
					<div style="width:16px;">
<%
						if( expanded )
						{
							out.println( getSimpleImageLink(theChip.getCommandID(ClassificationAttributeChip.COLLAPSE),
										localized("collapse"),
										"images/icons/arrow_opened.gif",
										"images/icons/arrow_opened_hover.gif"));
						}
						else
						{
							out.println( getSimpleImageLink(theChip.getCommandID(ClassificationAttributeChip.EXPAND),
										localized("expand"),
										"images/icons/arrow_closed.gif",
										"images/icons/arrow_closed_hover.gif"));
						}
%>
					</div>
				</td>

		<!-- label -->
		 		<td style="width:<%= labelWidth %>px;">
		 			<div <%= (editable ? "" : "class=\"disabled\"") %> 
		 				  style="width:<%= labelWidth %>px;<%= invalidValue ? "color:red;" : "" %>white-space:normal;<%= mandatory ? "font-weight:bold;" : "" %>"
		 				  class="chip-event" oncontextmenu="<%= contextMenu %>" >
						<a href="#" 
							title="<%= localized("classification.editor.open.attribute") %>" 
							name="<%= theChip.getCommandID(ClassificationAttributeChip.OPEN_ATTRIBUTE_EXTERNAL) %>"
							style="color:#333333;">
			 				<%= label.length() > 0 ? (label + ":") : "" %>
			 			</a>
		 			</div>
		 		</td>

				<td style="vertical-align:top; text-align:right;">
					<table cellspacing="0" cellpadding="0">
<%
						AbstractClassificationAttributeEditorChip editorChip;
	
						// show entry for the current session language first
						Language sessionLanguage = theDisplayState.getJaloSession().getSessionContext().getLanguage();
						editorChip = theChip.getEditorChips().get(sessionLanguage);
						if(editorChip==null)
						{
							Map.Entry<Language, AbstractClassificationAttributeEditorChip> first=theChip.getEditorChips().entrySet().iterator().next();
							sessionLanguage=first.getKey();
							editorChip = first.getValue();
						}
						editable = editorChip.isEditable();
%>
						<tr align="left">
<%
							if( !expanded )
							{
%>
					<!-- general language symbol -->
								<td class="langCode">
									<div class="button-on-white chip-event">
										<a href="#" title="<%= localized("expand") %>" 
											name="<%= theChip.getCommandID(ClassificationAttributeChip.EXPAND) %>"
										>
											<span>
												<img class="icon" src="images/icons/button_language.gif"/>
											</span>
										</a>
									</div>
								</td>
<%
							}
							else
							{
%>

					<!-- language code -->
								<td style="width:17px; height:20px; text-align:left;">
									<div style="white-space: nowrap; width:17px; " <%= (editable ? "" : "class=\"disabled\"") %>>
										<%= sessionLanguage.getIsoCode() %>
									</div>
								</td>
<%
							}
%>
				<!-- is changed sign -->
					 		<td style="width:10px; text-align:center;">
					 			<div style="width:10px;">
									<%= (editorChip.isChanged() ? "*" : "&nbsp;") %>
								</div>
					 		</td>

				<!-- attribute editor -->
							<td>
								<% editorChip.render(pageContext); %>
							</td>
						</tr>
<%
						if( expanded )
						{
							// now the entries for the remaining (non-session) languages
							for( Map.Entry<Language, AbstractClassificationAttributeEditorChip> entry : theChip.getEditorChips().entrySet() )
							{
								if( !sessionLanguage.equals(entry.getKey()) )
								{
									editorChip = entry.getValue();
									editable = editorChip.isEditable();
%>
									<tr align="left">

						<!-- language code -->
										<td style="width:17px; height:20px; text-align:left;">
											<div style="width:17px; white-space: nowrap;" <%= editable ? "" : "class=\"disabled\"" %>>
												<%= entry.getKey().getIsoCode() %>
											</div>
										</td>
										
						<!-- is changed sign -->
								 		<td style="width:10px; text-align:center;">
								 			<div style="width:10px;">
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
%>
					</table>
				</td>
			</tr>
		</table>
<% 
	}
%>


<%--
		}
		if( !"".equals(theChip.getDescriptionAttributeValue()) )
		{
%>
	 		<td style="padding-top:6px">
				<span style="white-space:nowrap;">
			 		&nbsp;<%= theChip.getDescriptionAttributeValue() %>
				</span>
			</td>
<%
		}
		if( !"".equals(theChip.getDescription()) )
		{
%>
	 		<td style="padding-top:6px">
				<span style="white-space:nowrap;">
			 		&nbsp;<%= theChip.getDescription() %>
				</span>
			</td>
<%
		}
--%>
