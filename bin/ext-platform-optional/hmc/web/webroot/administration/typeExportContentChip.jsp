<%@include file="../head.inc"%>
<%
	TypeExportContentChip theChip = (TypeExportContentChip)request.getAttribute( AbstractChip.CHIP_KEY );
%>

<table class="typeExportContentChip">
	<tr>
		<td class="sectionheader" colspan="2" > 
			<%= theChip.getTitle() %>
		</td>
	</tr>
	<tr class="header">
		<td colspan="2">
			<%=localized("typeexport.header")%>
		</td>
	</tr>
	<tr class="list">
		<td>
			<% theChip.getListEditor().render(pageContext); %>
		</td>
		<td class="list">
			<table>
				<tr>
					<td>
						<div class="xp-button chip-event">
							<a href="#" class="typeDefinition" 	title="<%= localized("typeexport.export.tooltip") %>" name="<%= theChip.getCommandID(TypeExportContentChip.EXPORT_TYPES) %>" hidefocus="true">
								<span class="label">
									<%= localized("typeexport.export.button") %>
								</span>
							</a>
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<i><%= localized("typeexport.export.info") %></i>
					</td>
				</tr>
				<tr>
					<td class="typeLocalisation">
						<div class="xp-button chip-event">
							<a href="#" title="<%= localized("typeexport.localization.tooltip") %>" name="<%= theChip.getCommandID(TypeExportContentChip.EXPORT_TYPE_LOCALIZATION) %>" hidefocus="true">
								<span class="label">
									<%= localized("typeexport.localization.button") %>
								</span>
							</a>
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<i><%= localized("typeexport.localization.info") %></i>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
