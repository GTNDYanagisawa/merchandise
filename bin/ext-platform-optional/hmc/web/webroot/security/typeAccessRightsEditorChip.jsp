<%@include file="../head.inc"%>
<%@page import="de.hybris.platform.hmc.jalo.AccessManager" %>
<%
	TypeAccessRightsEditorChip theChip = (TypeAccessRightsEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	Set principals = theChip.getPrincipals();
	String eventSelectPrincipal = theChip.getEventID(TypeAccessRightsEditorChip.SELECT_PRINCIPAL_EVENT);
	String eventQualifier = theChip.getEventID(TypeAccessRightsEditorChip.TYPEORFD_EVENT);
	String eventPermission = theChip.getEventID(TypeAccessRightsEditorChip.PERMISSION_EVENT);
	String eventRight = theChip.getEventID(TypeAccessRightsEditorChip.RIGHT_EVENT);
	String eventChangePermission = theChip.getEventID(TypeAccessRightsEditorChip.CHANGE_PERMISSION_EVENT);
	boolean selected = false;
%>
<%!public String getQualifierLine(TypeAccessRightsEditorChip chip, String qualifier, String right, boolean disabled)
	{
		String permission = chip.getPermission(qualifier, right);
		if( disabled )
		{
			return "<img src='images/icons/checkbox_disabled.gif' />";
		}
		else
		{
			return "<a href=\"#\" onclick=\"setEntityAndPermission('" + qualifier + "', '" + right + "', '" + permission + "'); setScrollAndSubmit(); return false;\"><img id=\"" + qualifier + "_" + right + "_" + permission +  "\" src='images/icons/checkbox_" + permission + ".gif'/></a>";
		}
	}%>
<script language="JavaScript1.2">
	function setEntityAndPermission( entity, right, permission )
	{
		document.editorForm.elements['<%=theChip.getEventID(TypeAccessRightsEditorChip.TYPEORFD_EVENT)%>'].value=entity;
		document.editorForm.elements['<%=theChip.getEventID(TypeAccessRightsEditorChip.PERMISSION_EVENT)%>'].value=permission;
		document.editorForm.elements['<%=theChip.getEventID(TypeAccessRightsEditorChip.RIGHT_EVENT)%>'].value=right;
		document.editorForm.elements['<%=theChip.getEventID(TypeAccessRightsEditorChip.CHANGE_PERMISSION_EVENT)%>'].value='true';
	}
</script>

<input type="hidden" name="<%= eventSelectPrincipal %>"/>
<table class="typeAccessRightsEditorChip" cellspacing="0" cellpadding="0">
	<tr>
		<td class="sectionheader">
			<div class="sh"><%=localized("accessrights")%>&nbsp;<%=theChip.isChanged() ? "*" : ""%></div>
		</td>
	</tr>
	<tr>
		<td>
			<table width="742px" class="attribute" cellspacing="0" cellpadding="0">
				<tr>
					<td class="arrowButton">&nbsp;</td>
			 		<td class="attrLabel"><%=localized("principals")%>:</td>
			 		<td class="noLocalizationFlag">&nbsp;</td>
					<td>
						<table width="100%" cellspacing="0" cellpadding="0">
							<tr>
								<td><%
									theChip.getToolbar().render(pageContext);
								%></td>
							</tr>
							<tr>
								<td>
								<!-- principal list start -->
									<div style="width:100%;overflow:auto;" id="resultlist_<%= theChip.getUniqueName() %>">
<%
	if( ConfigConstants.getInstance().ENABLE_SCROLLBAR && principals.size()>12 )
								{
%>
										<script language="JavaScript1.2">
											document.getElementById( "resultlist_<%= theChip.getUniqueName() %>" ).style.height=260;
										</script>
<%
										}
%>
										<table width="100%" class="listtable" cellpadding="1px" cellspacing="0px">
											<tr>
												<th style="width:30px; text-align:center;">
													<div class="tarecEntry" style="width:30px;">&nbsp;&nbsp;</div>
												</th>
												<th style="width:30px;">
													<div class="tarecEntry" style="width:30px;"><%=localized( "type" )%></div>
												</th>
												<th style="width:30%;">
													<div class="tarecEntry" style="width:100%"><%=localized( "uid" )%></div>
												</th>
												<th style="width:70%;">
													<div class="tarecEntry" style="width:100%"><%=localized( "name" )%></div>
												</th>
											</tr>
<%
											if( principals.isEmpty() )
											{
%>
											<tr>
												<td class="disabled" width="100%" colspan="4" style="border: 1px solid #bbbbbb">
													<div class="tarecEntry" width="100%"><font class="disabled"><%= localized( "listisempty" ) %></font></div>
												</td>
											</tr>
<%
											}
											else
											{
												for( final Iterator it = principals.iterator(); it.hasNext(); )
												{
													Principal principal = (Principal) it.next();
													selected = theChip.isSelected(principal);
													String combinedID = theChip.getCombinedID(principal);
													String principalID = principal.getUID();
													String principalName = principal.getName();
													if( principalName == null )
													{
														principalName = localized("notdefined");
													}
%>
											<tr>
												<td valign="top" <%= (selected ? "class=\"selected\"" : "" ) %> style="border-left: 1px solid #bbbbbb; width:30px; text-align:center;">
													<div class="tarecEntry" style="width:30px;">
														<input class="header"
																 onclick="document.editorForm.elements['<%= eventSelectPrincipal %>'].value='<%= combinedID %>';setScrollAndSubmit();"
																 type="radio"
																 name="1"
																 value="2"
																 id="<%= combinedID %>_radio"
																 <%=selected?" checked":""%>/>
													</div>
												</td>
												<td <%= (selected ? "class=\"selected\"" : "" ) %>>
													<div class="tarecEntry" style="width:30px;">
														<img src="<%=theChip.getIcon( principal )%>" 
															  border="0" 
															  onclick="document.editorForm.elements['<%= eventSelectPrincipal %>'].value='<%= combinedID %>';setScrollAndSubmit();return false;"
															  onMouseover="window.status='<%= localized("select.list.entry") %>'; return true;"
															  onMouseout="window.status='';return true;"
															  id="<%= combinedID %>_img"
															  title="<%= localized("select.list.entry") %>" />
													</div>
												</td>
												<td width="30%" <%= selected ? "class=\"selected\"" : "" %>>
													<div class="tarecEntry" style="width:100%;">
														<a href="#"
															class="normallink"
															onMouseover="window.status='<%= localized("select.list.entry") %>'; return true;" 
															onMouseout="window.status='';return true;"
															hidefocus="true" 
															style="white-space: nowrap;"
															onclick="document.editorForm.elements['<%= eventSelectPrincipal %>'].value='<%= combinedID %>';setScrollAndSubmit();return false;"
														   id="<%= combinedID %>_id"
															title="<%= localized("select.list.entry") %>">
															<%= principalID %>
														</a>
													</div>
												</td>
												<td width="70%" <%= selected ? "class=\"selected\"" : "" %> style="border-right: 1px solid #bbbbbb">
													<div class="tarecEntry" style="width:100%;">
														<a href="#"
															class="normallink"
															onMouseover="window.status='<%= localized("select.list.entry") %>'; return true;" 
															onMouseout="window.status='';return true;"
															hidefocus="true" 
															style="white-space: nowrap;"
															onclick="document.editorForm.elements['<%= eventSelectPrincipal %>'].value='<%= combinedID %>';setScrollAndSubmit();return false;"
														   id="<%= combinedID %>_name"
															title="<%= localized("select.list.entry") %>">
															<%= principalName %>
														</a>
													</div>
												</td>
											</tr>
<%
												}
											}
%>
										</table>
									</div>
								</td>
							</tr>
							<tr>
								<td><%@include file="../emptyFooter.inc"%></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td height="10px;">&nbsp;</td>
	</tr>
	<tr>
		<td>
			<table width="742px" class="attribute" cellspacing="0" cellpadding="0">
				<tr>
					<td class="arrowButton">&nbsp;</td>
			 		<td class="attrLabel"><%= localized("typeandattributedescriptors") %>:</td>
			 		<td class="noLocalizationFlag">&nbsp;</td>
					<td>
						<table width="100%" cellspacing="0" cellpadding="0">
							<tr>
								<td>
<%
									boolean disabled = !theChip.hasItem() || !theChip.isPrincipalSelected();
									String typeCode = theChip.getComposedType() == null ? localized( "type" ) : theChip.getComposedType().getCode();
									String typeName = theChip.getComposedType() == null ? localized( "type" ) : theChip.getComposedType().getName();
									String style = disabled ? "color: silver;" : "";
									typeName = (typeName == null ? typeName = localized("notdefined") : typeName);
%>
									<input type="hidden" name="<%= eventQualifier %>"/>
									<input type="hidden" name="<%= eventPermission %>"/>
									<input type="hidden" name="<%= eventRight %>"/>
									<input type="hidden" name="<%= eventChangePermission %>"/>
									<table width="100%" class="listtable">
										<tr>
											<th style="width: 150px;">
												<div class="tarecEntry" style="width:150px;"><%= localized("code") %></div>
											</th>
											<th style="width: 150px;">
												<div class="tarecEntry" style="width:150px;"><%= localized("name") %></div>
											</th>
											<th style="width: 55px; overflow:hidden;">
												<div class="tarecEntry" style="width:55px;"><%= localized("security.read") %></div>
											</th>
											<th style="width: 55px; overflow:hidden;">
												<div class="tarecEntry" style="width:55px;"><%= localized("security.change") %></div>
											</th>
											<th style="width: 55px; overflow:hidden;">
												<div class="tarecEntry" style="width:55px;"><%= localized("security.create") %></div>
											</th>
											<th style="width: 55px; overflow:hidden;">
												<div class="tarecEntry" style="width:55px;"><%= localized("security.remove") %></div>
											</th>
										</tr>
										<tr>
											<td style="border-left: 1px solid #bbbbbb; width:150px;<%= style %>">
												<div class="tarecEntry" style="width:150px; overflow:hidden; white-space:nowrap;"><%= typeCode %></div>
											</td>
											<td style="<%= style %>; width:150px;">
												<div class="tarecEntry" style="width:150px; overflow:hidden; white-space:nowrap;"><%= typeName %></div>
											</td>
											<td style="text-align:center; width:55px;<%= style %>">
												<div class="tarecEntry" style="width:55px;"><%= getQualifierLine(theChip, TypeAccessRightsEditorChip.TYPE, AccessManager.READ, disabled) %></div>
											</td>
											<td style="text-align:center; width:55px;">
												<div class="tarecEntry" style="width:55px;"><%= getQualifierLine(theChip, TypeAccessRightsEditorChip.TYPE, AccessManager.CHANGE, disabled) %></div>
											</td>
											<td style="text-align:center; width:55px;">
												<div class="tarecEntry" style="width:55px;"><%= getQualifierLine(theChip, TypeAccessRightsEditorChip.TYPE, AccessManager.CREATE, disabled) %></div>
											</td>
											<td style="text-align:center; width:55px; border-right: 1px solid #bbbbbb;">
												<div class="tarecEntry" style="width:55px;"><%= getQualifierLine(theChip, TypeAccessRightsEditorChip.TYPE, AccessManager.REMOVE, disabled) %></div>
											</td>
										</tr>
<%
										if( theChip.getComposedType() instanceof ComposedType )
										{
											for( final Iterator it = theChip.getAttributeDescriptors().iterator(); it.hasNext(); )
											{
												AttributeDescriptor fd = (AttributeDescriptor) it.next();
												String descriptorID = theChip.getAttributeDescriptorID(fd);
%>
												<tr>
													<td style="border-left: 1px solid #bbbbbb; white-space:nowrap;<%= style%>">
														<div class="tarecEntry" style="white-space:nowrap; width:150px; overflow:hidden;">- <%= fd.getQualifier() %></div>
													</td>
													<td style="<%= style %>; white-space:nowrap;">
														<div class="tarecEntry" style="white-space:nowrap; width:150px; overflow:hidden;"><%= fd.getName() %></div>
													</td>
													<td style="text-align:center;width: 55px;">
														<div class="tarecEntry" style="width:55px;"><%= getQualifierLine(theChip, descriptorID, AccessManager.READ, disabled) %></div>
													</td>
													<td style="text-align:center;width: 55px;">
														<div class="tarecEntry" style="width:55px;"><%= getQualifierLine(theChip, descriptorID, AccessManager.CHANGE, disabled) %></div>
													</td>
													<td style="width: 55px;">
														<div class="tarecEntry" style="width:55px;">&nbsp;</div>
													</td>
													<td style="border-right: 1px solid #bbbbbb;width: 55px;">
														<div class="tarecEntry" style="width:55px;">&nbsp;</div>
													</td>
												</tr>
<%
											}
										}
%>
									</table>
								</td>
							</tr>
							<tr>
								<td><%@include file="../emptyFooter.inc"%></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td style="height:10px;">&nbsp;</td>
	</tr>
</table>
