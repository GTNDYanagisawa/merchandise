<%--
fileUpload.jsp
HowTo:
This jsp uploads a file and stores it. It than redirects the browser to a location you can specify.
It requires four parameters:
	"target"        - the location the browser will be directed to
	"code"          - a code which will not be processed but instead forwarded to you
	"codeEvent"     - the parameter name which will be used to forward the code
	"fileEvent"     - the parameter name which will be used to forward the absolute filename of the stored file.

There are two additional commands, which can be used to build a modal dialog:
	"saveCommand"   - the parameter name which will be used to forward the save command
	"cancelCommand" - the parameter name which will be used to forward the cancel command

As all file upload handlers this uses a multipart request. You have to create a special form like this:

	<form action="fileUpload.jsp" method="post" target="main" enctype="multipart/form-data">
		<table>
			<tr>
				<td class="head">
					Code:
				</td>
				<td class="item">
					<input type="text" name="code">
				</td>
			</tr>
			<tr>
				<td class="head">
					File:
				</td>
				<td class="item">
					<input type="file" name="file">
				</td>
			</tr>
			<input type="hidden" name="target" value="<%=getRequestURL("main")%>">
			<input type="hidden" name="fileevent" value="<%=theChip.getEventID(MediaCreatorChip.SET_FILENAME)%>">
			<input type="hidden" name="orignalfileevent" value="<%=theChip.getEventID(MediaCreatorChip.SET_ORIGINALFILENAME)%>">
			<input type="hidden" name="mimeevent" value="<%=theChip.getEventID(MediaCreatorChip.SET_MIMETYPE)%>">
			<input type="hidden" name="codeevent" value="<%=theChip.getEventID(MediaCreatorChip.SET_CODE)%>">
			<input type="hidden" name="savecommand" value="<%=theChip.getCommandID(MediaCreatorChip.SAVE)%>">
			<input type="hidden" name="cancelcommand" value="<%=theChip.getCommandID(MediaCreatorChip.CANCEL)%>">
			<input type="hidden" name="tenantID" value="<%=Registry.getCurrentTenant().getTenantID()%>">
			<tr>
				<td colspan="2" class="head">
					<input type="image" src="save.gif" name="save">
					<input type="image" src="cancel.gif" name="cancel">
				</td>
			</tr>
		</table>
	</form>

--%>

<%@include file="head.inc"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.commons.fileupload.*,
					 org.apache.commons.io.FilenameUtils"%>

<HTML>
	<BODY BGCOLOR="white">
<%
			String target = "";
			String fileNameEvent = "";
			String originalFileNameEvent = "";
			String mimeEvent = "";
			boolean save = true;
			String saveCommand = "";
			String cancelCommand = "";
			FileItem uploadFileItem = null;

			final DiskFileUpload upload = new DiskFileUpload();

			List fileItems = null;
			
			upload.setHeaderEncoding("UTF-8");
			
			try
			{
				fileItems = upload.parseRequest(request);
			}
			catch( FileUploadBase.SizeLimitExceededException slee )
			{
%>
				<script language="javascript">
					alert("Upload size exeeds allowed size of <%= upload.getSizeMax() %> bytes!");	// TODO i18n
					window.close();
				</script>
<%
			}
			catch( FileUploadException fue )
			{
%>
				<script language="javascript">
					alert("Upload failed:\n<%= fue.getLocalizedMessage() %>");									// TODO i18n
					window.close();
				</script>
<%
			}
			
			Map<String,String> additionalParams=new HashMap<String,String>();
			if( fileItems != null )
			{
				for( final Iterator iter = fileItems.iterator(); iter.hasNext(); )
				{
					final FileItem fi = (FileItem) iter.next();
	
					final String key = fi.getFieldName();
	
					if( key.equals("file") )
					{
						uploadFileItem = fi;		
					}
					else if( key.equals("target") )
					{
						target = fi.getString();
					}
					else if( key.equals("fileevent") )
					{
						fileNameEvent = fi.getString();
					}
					else if( key.equals("savecommand") )
					{
						saveCommand = fi.getString();
					}
					else if( key.equals("cancelcommand") )
					{
						cancelCommand = fi.getString();
					}
					else if( key.equals("originalfileevent") )
					{
						originalFileNameEvent = fi.getString();
					}
					else if( key.equals("mimeevent") )
					{
						mimeEvent = fi.getString();
					}
					else if( key.startsWith("save") && fi.getString().equalsIgnoreCase("true") )
					{
						save=true;
					}					
					else if( key.startsWith("cancel") && fi.getString().equalsIgnoreCase("true") )
					{
						save=false;
					}
					else
					{
						additionalParams.put(key,fi.getString());
					}
				}
	
				if( uploadFileItem != null && uploadFileItem.getSize() > 0 )
				{
					
					String originalFileName = uploadFileItem.getName();
					if( originalFileName != null )
					{
						originalFileName = FilenameUtils.getName(originalFileName);
					}
					
		  			final File file = File.createTempFile("hmc_upload_file", null);
		  			uploadFileItem.write(file);
		  			
		  			final String realFileName = file.getAbsolutePath();
		  			
					target += "&" + fileNameEvent + "=" + URLEncoder.encode(realFileName,request.getCharacterEncoding());
					target += "&" + originalFileNameEvent + "=" + URLEncoder.encode(originalFileName,request.getCharacterEncoding());
					//target += "&" + mimeEvent + "=" + uploadFileItem.getContentType(); // removed so the mime guessing works
					
					for(Map.Entry<String,String> entry: additionalParams.entrySet())
					{
						target += "&" + entry.getKey() + "=" + entry.getValue();
					}
				}
	
				if( save )
				{
					target += "&" + saveCommand + "=true";
				}
				else
				{
					target += "&" + cancelCommand + "=true";
				}
				
				// insert tenant ID if necessary
				Tenant t = Registry.getCurrentTenant();
				if( t instanceof SlaveTenant )
				{
					int pos = target.indexOf("?");
					if( pos >= 0 )
					{
						target = 
							target.substring(0,pos) +
							";tenantID="+t.getTenantID()+
							target.substring(pos);
					}
					else
					{
						System.err.println("could not insert tenantID into target URL '"+target+"'" );
					}
				}
				response.sendRedirect(request.getScheme() + "://" +request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/" + target );							
			}	
		%>
	</BODY>
</HTML>
