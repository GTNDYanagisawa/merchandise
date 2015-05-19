<%@include file="../head.inc"%>
<%
	StructureEditorChip theChip = (StructureEditorChip)request.getAttribute( AbstractChip.CHIP_KEY );
	
	final String confirmXSD = theChip.getConfirmXSDMessage();
	final String confirmLogout = localized("structure.editor.logout.confirm");
%>
<table class="structureEditorChip">
	<tr>
		<td class="sectionheader"> 
			<%= theChip.getTitle() %>
		</td>
	</tr>
	<tr class="spacer"><td>&nbsp;</td></tr>
	<tr><td><%= localized("text.structure.editor") %></td></tr>
	<tr class="spacer"><td>&nbsp;</td></tr>
	<tr>
		<td><textarea name="<%=theChip.getEventID(StructureEditorChip.TEXT)%>" rows="35" wrap="off" id="<%= theChip.getUniqueName() %>_textarea"><%=theChip.getStructureQuoted()%></textarea></td>
	</tr>
	<tr>
		<td>
			<table width="100%"><%
					if( !theChip.isUsingDBForStructure() )
					{
%>
						<tr>
							<td colspan="5" class="notusingdb"><%= localized("structure.editor.notusingdb.warning") %></td>
						</tr>
<%
					}
%>
			
				<tr>
					<td class="secButtons">
						<div class="xp-button chip-event">
							<a href="#" title="<%= localized("structure.editor.validate.tooltip") %>" name="<%= theChip.getCommandID(StructureEditorChip.VALIDATE) %>" hidefocus="true"><%
								if( confirmXSD != null && confirmXSD.length() > 0 )
								{
								%><span class="confirm-message"><%= confirmXSD %></span><%
								}
								%><span class="structure_editor_hack"></span>
								<span class="label">
									<%= localized("structure.editor.validate.button") %>
								</span>
							</a>
						</div>
						<div class="xp-button chip-event">
							<a href="#" title="<%= localized("structure.editor.uploadeditor.tooltip") %>" name="<%= theChip.getCommandID(StructureEditorChip.UPLOADEDITOR) %>" hidefocus="true"><%
								if( confirmXSD != null && confirmXSD.length() > 0 )
								{
							%>  <span class="confirm-message"><%= confirmXSD %></span><%
								}
							%>  <span class="confirm-message"><%= confirmLogout %></span><span class="structure_editor_hack"></span>
								<span class="label">
									<%= localized("structure.editor.uploadeditor.button") %>
								</span>
							</a>
						</div>
						
						<script language="JavaScript1.2">
							addKeyFunction("S", function() { if (confirm(decodeURI("<%= confirmLogout %>")) )
																		{ setEvent("<%= theChip.getCommandID(StructureEditorChip.UPLOADEDITOR) %>"); setScrollAndSubmit(true); } 
																	 });
						</script>
						
						<div class="xp-button chip-event">
							<a href="#" title="<%= localized("structure.editor.reset.tooltip") %>" name="<%= theChip.getCommandID(StructureEditorChip.RESET) %>" hidefocus="true">
								<span class="label">
									<%= localized("structure.editor.reset.button") %>
								</span>
							</a>
						</div>
						<div class="xp-button chip-event">
							<a href="#" style="width:140px;" title="<%= localized("structure.editor.uploadfile.tooltip") %>" name="<%= theChip.getCommandID(StructureEditorChip.UPLOADFILE) %>" hidefocus="true">
								<span class="confirm-message"><%= confirmLogout %></span>
								<span class="label">
									<%= localized("structure.editor.uploadfile.button") %>
								</span>
							</a>
						</div>
				</tr>
			</table>
		</td>
	</tr>
</table>
