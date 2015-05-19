<%@include file="../head.inc"%>
<%
	final MediaFileUploadEditorChip theChip = (MediaFileUploadEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
//	boolean readable = theChip.isURLReadable();
//	boolean editable = theChip.isURLEditable();

	final boolean showPreview = theChip.showPreview();
	final boolean isURLReadable = theChip.isURLReadable();
	final boolean isUploadEnabled = theChip.isUploadEnabled();
	final boolean isPreviewEnabled = theChip.isPreviewEnabled();
	final boolean isDownloadEnabled = theChip.isDownloadEnabled();
	
%>
<table class="mediaFileUploadEditorChip">
<%
	if( showPreview )
	{
%>
		<tr>
			<td class="arrowButton">&nbsp;</td>
			<td class="attrLabel"><%=localized("preview")%>:</td>
			<td class="noLocalizationFlag">&nbsp;</td>
			<td class="preview">
				<img src="<%= theChip.getURL() %>"/>
			</td>
		</tr>
<%
	}
%>
	<tr>
		<td class="arrowButton">&nbsp;</td>
		<td class="attrLabel"><%=localized("url")%>:</td>
		<!-- is changed sign -->
 		<td class="noLocalizationFlag">
			<div>
				<%= (theChip.isChanged() ? "*" : "&nbsp;") %>
			</div>
 		</td>
		<td class="preview">
		<%
			if( isURLReadable )
			{
				%><%= (theChip.getURL()==null ? "" : theChip.getURL()) %><%
			}
			else
			{
				%><% theChip.getNoAccessChip().render( pageContext ); %><%
			}
		%>
		</td>
	</tr>
	<tr>
		<td class="arrowButton">&nbsp;</td>
		<td class="attrLabel">&nbsp;</td>
		<td class="noLocalizationFlag">&nbsp;</td>
		
<%
%>
		<td colspan="2" class="preview">
			<table>
				<tr>
					<td>
						<div class="xp-button<%= isUploadEnabled ? " chip-event" : "-disabled" %>">
							 	<a href="#" title="<%= localized("upload") %>"   
								<%= isUploadEnabled ? "name=\"" + theChip.getCommandID(MediaFileUploadEditorChip.UPLOAD) + "\"" : "" %> 
								id="<%= theChip.getUniqueName() %>_upload_a"
								hidefocus="true">
								<span id="<%= theChip.getUniqueName() %>_upload_span">
									<img class="icon" src="images/icons/button_upload<%= isUploadEnabled ? "" : "_disabled" %>.gif" id="<%= theChip.getUniqueName() %>_upload_img"/>
									<div class="label" id="<%= theChip.getUniqueName() %>_upload_div">
										<%= localized("upload") %>
									</div>
								</span>
							</a>
						</div>
					</td>
					<td>
						<div class="xp-button<%= isPreviewEnabled ? " open-url" : "-disabled" %>">
							<a href="#" title="<%= localized("preview") %>" 
								<%= isPreviewEnabled ? "name=\"" + theChip.getURL() + "\"" : "" %> 
								id="<%= theChip.getUniqueName() %>_preview_a"
							>
								<span id="<%= theChip.getUniqueName() %>_preview_span">
									<img class="icon" src="images/icons/button_preview<%= isPreviewEnabled ? "" : "_disabled" %>.gif" id="<%= theChip.getUniqueName() %>_preview_img"/>
									<div class="label" id="<%= theChip.getUniqueName() %>_preview_div">
										<%= localized("preview") %>
									</div>
								</span>
							</a>
						</div>
					</td>
					<td>
						<div class="xp-button<%= isDownloadEnabled ? " open-url" : "-disabled" %>">
							<a href="#" title="<%= localized("download") %>" 
							<%= isDownloadEnabled ? "name=\"" + theChip.getDownloadURL() + "\"" : "" %> 
								id="<%= theChip.getUniqueName() %>_download_a"
							>
								<span id="<%= theChip.getUniqueName() %>_download_span">
									<img class="icon" src="images/icons/button_download<%= isDownloadEnabled ? "" : "_disabled" %>.gif" id="<%= theChip.getUniqueName() %>_download_img"/>
									<div class="label" id="<%= theChip.getUniqueName() %>_download_div">
										<%= localized("download") %>
									</div>
								</span>
							</a>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="arrowButton">&nbsp;</td>
		<td class="attrLabel"><%=localized("mime")%>:</td>
		<td class="noLocalizationFlag">&nbsp;</td>
		<td class="preview">
		<%
			if( theChip.isMimeReadable() )
			{
				%>
				<input
					class="<%=(theChip.isMimeEditable()?"enabled":"disabled")%>" type="text" 
					name="<%=theChip.getEventID(MediaFileUploadEditorChip.SET_MIME)%>" 
					value="<%=(theChip.getMime() != null ? theChip.getMime() : "")%>" 
					id="<%= theChip.getUniqueName() %>_mime"
					<%=(theChip.isMimeEditable()?"":"readonly")%>>
				<%
			}
			else
			{
				%><% theChip.getNoAccessChip().render( pageContext ); %><%
			}
		%>
		</td>
	</tr>
	<tr>
		<td class="arrowButton">&nbsp;</td>
		<td class="attrLabel"><%=localized("realfilename")%>:</td>
		<td class="noLocalizationFlag" >&nbsp;</td>
		<td>
		<%
			if( theChip.isRealFileNameReadable() )
			{
				%>
				<input class="<%=(theChip.isMimeEditable()?"enabled":"disabled")%>" type="text" 
					name="<%=theChip.getEventID(MediaFileUploadEditorChip.SET_REAL_FILENAME)%>" 
					value="<%=(theChip.getRealFileName() != null ? theChip.getRealFileName() : "")%>"
					id="<%= theChip.getUniqueName() %>_realfilename"
					<%=(theChip.isRealFileNameEditable()?"":"readonly")%>>
				<%
			}
			else
			{
				%><% theChip.getNoAccessChip().render( pageContext ); %><%
			}
		%>
		</td>
	</tr>
</table>
