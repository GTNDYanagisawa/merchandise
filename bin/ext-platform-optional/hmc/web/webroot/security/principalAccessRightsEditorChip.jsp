<%@include file="../head.inc"%>
<%@page import="de.hybris.platform.hmc.jalo.AccessManager" %>
<%
	PrincipalAccessRightsEditorChip theChip = (PrincipalAccessRightsEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);

	String selectTypeEvent = theChip.getEventID(PrincipalAccessRightsEditorChip.SELECT_TYPE_EVENT);

	String eventQualifier = theChip.getEventID(PrincipalAccessRightsEditorChip.TYPEORFD_EVENT);
	String eventPermission = theChip.getEventID(PrincipalAccessRightsEditorChip.PERMISSION_EVENT);
	String eventRight = theChip.getEventID(PrincipalAccessRightsEditorChip.RIGHT_EVENT);
	String eventChangePermission = theChip.getEventID(PrincipalAccessRightsEditorChip.CHANGE_PERMISSION_EVENT);
	
	final boolean disabled = !theChip.canChangeRights();

	Set types = theChip.getTypes();
%>

<%!public String getQualifierLine(PrincipalAccessRightsEditorChip chip, String id, String right, boolean disabled)
	{
		if( disabled )
		{
			return "<img src='images/icons/checkbox_disabled.gif' />";
		}
		else
		{
			final String permission = chip.getPermission(id, right);
			return "<a href=\"#\" onclick=\"setEntityAndPermission('" + id + "', '" + right + "', '" + permission + "'); setScrollAndSubmit(); return false;\"><img id=\"" + id + "_" + right + "_" + permission +  "\" src='images/icons/checkbox_" + permission + ".gif'/></a>";
		}
	}%>

<script language="JavaScript1.2">
	function setEntityAndPermission( entity, right, permission )
	{
		document.editorForm.elements['<%= eventQualifier %>'].value=entity;
		document.editorForm.elements['<%= eventPermission %>'].value=permission;
		document.editorForm.elements['<%= eventRight %>'].value=right;
		document.editorForm.elements['<%= eventChangePermission %>'].value='true';
	}
</script>

<input type="hidden" name="<%= selectTypeEvent %>"/>
<table class="table.principalAccessRightsEditorChipHeader">
	<tr>
		<td>&nbsp;</td>
		<td class="sectionheader">
			<div class="sh"><%=localized("principal.security.section")%>&nbsp;<%=theChip.isChanged() ? "*" : ""%></div>
		</td>
	</tr>
</table>

<table class="principalAccessRightsEditorChip">	
	<tr><td class="spacer" colspan="4"><div>&nbsp;</div></td></tr>
	<tr>
		<td class="arrowButton">&nbsp;</td>
		<td class="description" colspan="3"><div><%=localized("principal.security.tab.composedtypes.description")%></div></td>
	</tr>
	<tr><td class="spacer" colspan="4"><div>&nbsp;</div></td></tr>
	
	<tr>
 		<td class="arrowButton">&nbsp;</td>
 		<td class="attrLabel"><div><%=localized("principal.security.tab.composedtypes")%>:</div></td>
		<td class="objects">
			<table>
				<tr class="headline">
					<td><%
						theChip.getToolbar().render(pageContext);
					%></td>
				</tr>
				<tr>
					<td>
					<!-- type list start -->
						<div style="overflow:auto" id="resultlist_<%= theChip.getUniqueName() %>">
<%
	if( ConfigConstants.getInstance().ENABLE_SCROLLBAR && types.size()>12 )
					{
%>
							<script language="JavaScript1.2">
								document.getElementById( "resultlist_<%= theChip.getUniqueName() %>" ).style.height=260;
							</script>
<%
							}
%>
							<table class="listtable" cellpadding="1px" cellspacing="0px">
								<tr>
									<th style="width:30px;"><div style="width:30px; padding-left:2px;"/></th>
									<th style="width:30px;"><div style="width:30px; padding-left:2px;"><%= localized("type") %></div></th>
									<th style="width:208px;"><div style="width:208px; padding-left:2px;"><%= localized("code") %></div></th>
									<th style="width:209px;"><div style="width:209px; padding-left:2px;"><%= localized("name") %></div></th>
								</tr>
<%
								if( types.isEmpty() )
								{
%>
								<tr>
									<td class="disabled" width="496px" colspan="4" style="border: 1px solid #bbbbbb"><div class="disabled"><%= localized( "listisempty" ) %></div></td>
								</tr>
<%
								}
								else
								{
									for( final Iterator it = types.iterator(); it.hasNext(); )
									{
										final ComposedType type = (ComposedType) it.next();
										final boolean selected = theChip.isSelected(type);
										final String typeCode = type.getCode();
										String typeName = type.getName();
										if( typeName == null )
										{
											typeName = localized("notdefined");
										}
%>
								<tr>
									<td valign="top" <%= (selected ? "class=\"selected\"" : "" ) %> style="border-left: 1px solid #bbbbbb; width:30px; text-align:center;">
										<input class="header"
												 onclick="document.editorForm.elements['<%= selectTypeEvent %>'].value='<%= typeCode %>';setScrollAndSubmit();"
												 type="radio"
												 name="1"
												 value="2"
												 id="access_<%= typeCode %>_radio"
												 <%= selected?" checked" : "" %>/>
									</td>
									<td <%= (selected ? "class=\"selected\"" : "" ) %> style="text-align:center;">
										<img src="<%= theChip.getIcon(type) %>" 
											  border="0" 
											  onclick="document.editorForm.elements['<%= selectTypeEvent %>'].value='<%= typeCode %>';setScrollAndSubmit();return false;" 
											  onMouseover="window.status='<%= localized("select.list.entry") %>'; return true;"  
											  onMouseout="window.status='';return true;" 
											  id="access_<%= typeCode %>_img"
											  title="<%= localized("select.list.entry") %>" />
									</td>
									<td <%= selected ? "class=\"selected\"" : "" %> style="padding-left:2px;">
										<a href="#"
											class="normallink"
											onMouseover="window.status='<%= localized("select.list.entry") %>'; return true;" 
											onMouseout="window.status='';return true;"
											hidefocus="true" 
											style="white-space: nowrap;"
											onclick="document.editorForm.elements['<%= selectTypeEvent %>'].value='<%= typeCode %>';setScrollAndSubmit();return false;"
											id="access_<%= typeCode %>_code"
											title="<%= localized("select.list.entry") %>">
											<%= typeCode %>
										</a>
									</td>
									<td <%= selected ? "class=\"selected\"" : "" %> style="border-right: 1px solid #bbbbbb; padding-left:2px;">
										<a href="#"
											class="normallink"
											onMouseover="window.status='<%= localized("select.list.entry") %>'; return true;" 
											onMouseout="window.status='';return true;"
											hidefocus="true" 
											style="white-space: nowrap;"
											onclick="document.editorForm.elements['<%= selectTypeEvent %>'].value='<%= typeCode %>';setScrollAndSubmit();return false;"
											id="access_<%= typeCode %>_name"
											title="<%= localized("select.list.entry") %>">
											<%= typeName %>
										</a>
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
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="spacer" colspan="4"><div>&nbsp;</div></td>
	</tr>
	<tr>
		<td class="arrowButton">&nbsp;</td>
		<td class="description" colspan="3"><div><%= localized("typeandattributedescriptors.description") %></div></td>
	</tr>
	<tr>
		<td class="spacer" colspan="4"><div>&nbsp;</div></td>
	</tr>
	<tr>
 		<td class="arrowButton">&nbsp;</td>
 		<td class="attrLabel"><div><%= localized("typeandattributedescriptors") %>:</div></td>
		<td style="vertical-align:top;">

			<table cellspacing="0" cellpadding="0" style="width:500px;">
<%
				if( !theChip.isTypeSelected() || !theChip.hasItem() )
				{
%>
					<tr>
						<td class="disabled">
							<%= localized( "listisempty" ) %>
						</td>
					</tr>
<%
				}
				else
				{
%>
					<tr>
						<td>
<%
							final String typeCode = theChip.getSelectedType().getCode();
							final String typeName = theChip.getSelectedType().getName() != null ? theChip.getSelectedType().getName() : localized("notdefined");
							final String style = disabled ? "color: silver;" : "";
%>
							<input type="hidden" name="<%= eventQualifier %>"/>
							<input type="hidden" name="<%= eventPermission %>"/>
							<input type="hidden" name="<%= eventRight %>"/>
							<input type="hidden" name="<%= eventChangePermission %>"/>
							<table width="100%" class="listtable">
								<tr>
									<th style="width: 150px;"><div style="padding-left:2px;"><%= localized("code") %></div></th>
									<th style="width: 150px;"><div style="padding-left:2px;"><%= localized("name") %></div></th>
									<th style="width: 55px; text-align:center; overflow:hidden;"><div style="padding-left:2px;"><%= localized("security.read") %></div></th>
									<th style="width: 55px; text-align:center; overflow:hidden;"><div style="padding-left:2px;"><%= localized("security.change") %></div></th>
									<th style="width: 55px; text-align:center; overflow:hidden;"><div style="padding-left:2px;"><%= localized("security.create") %></div></th>
									<th style="width: 55px; text-align:center; overflow:hidden;"><div style="padding-left:2px;"><%= localized("security.remove") %></div></th>
								</tr>
								<tr>
									<td style="border-left: 1px solid #bbbbbb; width:150px;<%= style %>"><div style="overflow:hidden; white-space:nowrap; padding-left:2px;"><%= typeCode %></div></td>
									<td style="<%= style %> width:150px;"><div style="overflow:hidden; white-space:nowrap; padding-left:2px;"><%= typeName %></div></td>
									<td style="text-align:center; width:55px;"><div style="padding-left:2px;"><%= getQualifierLine(theChip, PrincipalAccessRightsEditorChip.TYPE, AccessManager.READ, disabled) %></div></td>
									<td style="text-align:center; width:55px;"><div style="padding-left:2px;"><%= getQualifierLine(theChip, PrincipalAccessRightsEditorChip.TYPE, AccessManager.CHANGE, disabled) %></div></td>
									<td style="text-align:center; width:55px;"><div style="padding-left:2px;"><%= getQualifierLine(theChip, PrincipalAccessRightsEditorChip.TYPE, AccessManager.CREATE, disabled) %></div></td>
									<td style="text-align:center; width:55px; border-right: 1px solid #bbbbbb;"><div style="padding-left:2px;"><%= getQualifierLine(theChip, PrincipalAccessRightsEditorChip.TYPE, AccessManager.REMOVE, disabled) %></div></td>
								</tr>
<%
								for( final Iterator it = theChip.getAttributeDescriptors().iterator(); it.hasNext(); )
								{
									final AttributeDescriptor descr = (AttributeDescriptor) it.next();
									final String descriptorID = theChip.getAttributeDescriptorID(descr);
									final String descriptorQualifier = descr.getQualifier();
									final String descriptorName = descr.getName() != null ? descr.getName() : localized("notdefined");
%>
									<tr>
										<td style="border-left: 1px solid #bbbbbb; white-space:nowrap;<%= style %>"><div style="white-space:nowrap; width:150px; overflow:hidden;padding-left:2px;">-<%= descriptorQualifier %></div></td>
										<td style="<%= style %>; white-space:nowrap;"><div style="white-space:nowrap; width:150px; overflow:hidden;"><%= descriptorName %></div></td>
										<td style="text-align:center;width: 55px;"><div style="padding-left:2px;"><%= getQualifierLine(theChip, descriptorID, AccessManager.READ, disabled) %></div></td>
										<td style="text-align:center;width: 55px;"><div style="padding-left:2px;"><%= getQualifierLine(theChip, descriptorID, AccessManager.CHANGE, disabled) %></div></td>
										<td style="width: 55px;">&nbsp;</td>
										<td style="border-right: 1px solid #bbbbbb;width: 55px;">&nbsp;</td>
									</tr>
<%
								}
%>
							</table>
					</td>
				</tr>
				<tr>
					<td><%@include file="../emptyFooter.inc"%></td>
				</tr>
<%
							}
%>
			</table>

		</td>
	</tr>
	<tr>
		<td class="spacer" colspan="4"><div>&nbsp;</div></td>
	</tr>


 </table>
