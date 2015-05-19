<%@include file="../../head.inc"%>
<%@include file="../../xp_button.inc"%>
<%@page import="de.hybris.platform.impex.hmc.ModalImpExMediaFileUploadChip"%>
<%
	final ModalImpExMediaFileUploadChip theChip = (ModalImpExMediaFileUploadChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
</form>
<form name="editorForm" action="<%=response.encodeURL("fileUpload.jsp")%>" method="post" enctype="multipart/form-data">
	<input type="hidden" name="target" value="<%=HMCMasterServlet.MASTERSERVLET%><%=getRequestURL()%>">
	<input type="hidden" name="fileevent" value="<%=theChip.getEventID(ModalImpExMediaFileUploadChip.SET_FILENAME)%>">
	<input type="hidden" name="tempfilename" value="upload_<%= theChip.getID() %>.tmp">
<!--	<input type="hidden" name="codeevent" value="code">	-->
	<input type="hidden" name="originalfileevent" value="<%=theChip.getEventID(ModalImpExMediaFileUploadChip.SET_ORIGINALFILENAME)%>">
	<input type="hidden" name="mimeevent" value="<%=theChip.getEventID(ModalImpExMediaFileUploadChip.SET_MIMETYPE)%>">
	<input type="hidden" name="savecommand" value="<%=theChip.getCommandID(ModalImpExMediaFileUploadChip.SAVE)%>">
	<input type="hidden" name="cancelcommand" value="<%=theChip.getCommandID(ModalImpExMediaFileUploadChip.CANCEL)%>">
	<table>
		<tr>
		 	<td>
				<table>
					<!-- Headline row -->
					<tr>
						<td class="modalMediaFileUploadChip"><%=localized("createmedia")%></td>
					</tr>
					<tr>
						<td>
							<table>
								<tr>
									<td><%=localized("file")%>:</td>
									<td><input class="modalMediaFileUploadChip" type="file" name="file"/></td>
								</tr>
								<tr>
									<td><%=localized("folder")%>:</td>
									<td>
										<% theChip.getFolderEditor().render(pageContext); %>									
									</td>
								</tr>
								<tr>
									<td>Encoding:</td>
									<td>
										<select
											class="enabled"
											size="1"
											name="<%=theChip.getEventID(ModalImpExMediaFileUploadChip.SET_ENCODING)%>"
											id="<%= theChip.getUniqueName() %>_select">
<%
											for( int i=0; i<theChip.getEncodingCount(); i++ )
											{
%>
												<option value="<%= Integer.toString(i) %>"<%= (theChip.isEncodingSelected(i) ? " selected" : "") %>>
													<%= theChip.getEncodingStringValue(i) %>
												</option>
<%
											}
%>
										</select>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table>
					<tr>
						<td width="100%">&nbsp;</td>
						<td>
							<%= xpButton( localized("upload"), "save" ) %>
						</td>
						<td>
							<%= xpButton( localized("cancel"), "cancel" ) %>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="100%">&nbsp;</td>
		</tr>
	</table>
</form>
