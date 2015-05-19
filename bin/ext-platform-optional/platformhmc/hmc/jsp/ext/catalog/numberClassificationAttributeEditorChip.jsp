<%@include file="../../head.inc"%>

<%@page import="de.hybris.platform.catalog.hmc.*" %>

<%
	final NumberClassificationAttributeEditorChip theChip = (NumberClassificationAttributeEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final boolean isRange = theChip.isRange();
	final String contextMenu = theChip.hasVisibleContextMenuEntries()
										? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
										: "return false;";
%>
	<table cellspacing="0" cellpadding="0" oncontextmenu="<%= contextMenu %>">
<%
		for( int i = 0; i < theChip.getValueEditorChips().size(); i++ )
		{
			if( !isRange || i == 0 )
			{
%>
				<tr>
<%
			}
%>
					<td title="<%= theChip.getTooltip(i) %>">
<%
						theChip.getValueEditorChips().get(i).render(pageContext);
%>
					</td>
<%
			if( theChip.getUnitEditorChips().get(i) != null )
			{
%>
					<td>
<%
						theChip.getUnitEditorChips().get(i).render(pageContext);
%>
					</td>
<%
			}
%>
					<td>			
						<div class="popup">			
							<div class="button-on-white" style="width:28px; padding-left:3px;" onclick="y_togglePopupEditor(this); return false;">
								<a href="#" title="<%= localized("classification.editor.edit.description") %>">
									<span>
										<img class="icon" src="images/icons/button_description.gif">
									</span>
								</a>
							</div>
							
							<span class="popup-element">
<%
								theChip.getDescriptionEditorChips().get(i).render(pageContext);
%>
							</span>
						</div>
					</td>
<%
					if( theChip.isMultiValued() && !isRange )
					{
%>
						<td>
							<div class="button-on-white chip-event" style="width:23px; padding-left:3px;">
								<a href="#" title="<%= localized(i == 0 ? "classification.editor.add" : "classification.editor.remove" ) %>" 
									name="<%= theChip.getCommandID(i == 0 ? AbstractClassificationAttributeEditorChip.ADD : AbstractClassificationAttributeEditorChip.REMOVE) %>">
									<span class="event-value"><%= i == 0 ? "true" : i %></span>
									<span style="padding-left:3px;">
										<img class="icon" src="images/icons/list_menu_<%= i == 0 ? "add" : "remove" %>.gif">
									</span>
								</a>
							</div>
						</td>
<%
					}
					
					if( isRange && i == 0 )
					{
%>
						<td style="padding:2px 7px 0 5px;">
							<%= localized("classification.editor.range.to") %>
						</td>
<%
					}

			if( !isRange || i > 0 )
			{
%>
				</tr>
<%
			}
		}
%>
	</table>

