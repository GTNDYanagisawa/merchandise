<%@ include file="./inc/head.jspf"%>
<%@ taglib prefix="c" uri="/WEB-INF/tlds/c-rt.tld"%>

<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@ page import="org.xml.sax.InputSource"%>

<%@page import="de.hybris.platform.jalo.media.Media"%>
<%@page import="de.hybris.platform.impex.jalo.*"%>
<%@page import="de.hybris.platform.impex.constants.*"%>
<%@page import="de.hybris.platform.impex.jalo.media.*"%>
<%@page import="de.hybris.platform.impex.jalo.ImpExManager"%>
<%@page import="de.hybris.platform.bmecat.parser.BMECatParser"%>
<%@page import="de.hybris.platform.bmecat.jalo.bmecat2csv.BMECat2CSVObjectProcessor"%>

<div class="absatz"><a href="<%=response.encodeURL("/bmecat")%>">[Back
to BMECat import]</a></div>
<div class="absatz">&nbsp;</div>
<div class="absatz">


<h1>BMECat transforming</h1>
<c:set var="fileUploaded" scope="session" value="0"/>
<%
	String encoding = "windows-1252"; //request.getParameter("encoding");
	String script = "";
	String mediaPK = "";
	String resourceMediaPK = "";
	Map<String, String> bmecatHeader = new HashMap<String, String>();
	String scriptConstants = "";

	JspContext jspc = new JspContext( out, request, response );

	System.out.println( "Content Type =" + request.getContentType() );

	ServletFileUpload upload = new ServletFileUpload();
	//fu.setSizeMax(1000000);
	FileItemIterator iter = upload.getItemIterator( request );

	//only one xml file and one zip file are allowed.
	FileItemStream[] fis = new FileItemStream[2];
	int i = 0;
	while( iter.hasNext() )
	{
		User u = null;
		
		if(i == 0)
		{
			fis[0] = iter.next();
			i++;
			if( !fis[0].isFormField() )
			{
				if( !jaloSession.getUser().isAdmin() )
				{
					u = jaloSession.getUser();
					jaloSession.setUser( UserManager.getInstance().getAdminEmployee() );
				}
				MediaDataTranslator.setMediaDataHandler( new DefaultMediaDataHandler() );
				String importFileName = fis[0].getName();

				//only *.xml files can be transformed to .csv files
				if(importFileName.toLowerCase().endsWith(".xml"))
				{
					//parsing BMECat.xml
					BMECat2CSVObjectProcessor proc = new BMECat2CSVObjectProcessor();
					BMECatParser bmecatParser = new BMECatParser(proc);
					try
					{
						bmecatParser.parse(new InputSource(fis[0].openStream()));
						proc.finish();
						Media media = proc.getResultZipMedia();
						if(media == null)
						{
							out.print("media is null.");
						}
						else
						{
							out.print("<a href=\"" + media.getDownloadURL()+ "\">zip_file </a><br/>");
						}
						out.print("<br/>Info of transformed catalog:<br/>");
						out.print("<br/>" + proc.getGeneralCatalogInfo() + "<br/>");
						bmecatHeader = proc.getBmecatHeader();
						
						//dealing with the language
						String language = "";
						if("eng".equals(bmecatHeader.get("catalog.language").trim()))
						{
							language = "en";
							out.print("Language: [eng] --> [en]<br/><p/>");
						}
						else if("deu".equals(bmecatHeader.get("catalog.language").trim()))
						{
							language = "de";
							out.print("Language: [deu] --> [de]<br/><p/>");
						}
						else
							language = bmecatHeader.get("catalog.language").trim();
			
						//get the header information from bmecat.xml
						if(bmecatHeader.containsKey("catalog.id"))
						{
							out.print("*************constants starts*************<br/>");
							scriptConstants += "$catalog_id=" + bmecatHeader.get("catalog.id") + "\n";
							out.print("$catalog_id=" + bmecatHeader.get("catalog.id") + "<br/>");
						}
						if(bmecatHeader.containsKey("catalog.version"))
						{
							scriptConstants += "$catalog_version=" + bmecatHeader.get("catalog.version") + "\n";
							out.print("$catalog_version=" + bmecatHeader.get("catalog.version") + "<br/>");
						}
						if(bmecatHeader.containsKey("catalog.name"))
						{
							scriptConstants += "$catalog_name=" + bmecatHeader.get("catalog.name") + "\n";
							out.print("$catalog_name=" + bmecatHeader.get("catalog.name") + "<br/>");
						}
						if(bmecatHeader.containsKey("catalog.language"))
						{
							scriptConstants += "$lang_iso=" + language + "\n";
							out.print("$lang_iso=" + language + "<br/>");
						}
						if(bmecatHeader.containsKey("catalog.currency"))
						{
							scriptConstants += "$cur_iso=" + bmecatHeader.get("catalog.currency") + "\n";
							out.print("$cur_iso=" + bmecatHeader.get("catalog.currency") + "<br/>");
							out.print("*************constants ends*************<br/>");
						}					
						scriptConstants += "#% impex.setLocale( Locale.GERMAN );\n";
						out.print("<br/>Locale is set to German.<br/>");
						mediaPK = media.getPK().toString();
					}
					catch (Exception e) 
					{
						System.out.println("import exception in fileUpload.jsp -->  " + e.getMessage());
						e.printStackTrace();
					}
					out.print("<br/>File [" + importFileName + "] is transformed to csv files successfully.<br/>");
				}
				else
				{
					out.print("<br/><font color='red'>The first file [" + importFileName + "] is not valid.</font><br/>");
					out.print("<font color='red'>Only the bmecat xml file can be imported.</font><br/>");
					break;
				}
			}
		}
		else if(i == 1)
		{
			fis[1] = iter.next();
			i++;
			if( !fis[1].isFormField() )
			{
				if( !jaloSession.getUser().isAdmin() )
				{
					u = jaloSession.getUser();
					jaloSession.setUser( UserManager.getInstance().getAdminEmployee() );
				}
				MediaDataTranslator.setMediaDataHandler( new DefaultMediaDataHandler() );
				String importFileName = fis[1].getName();

				//only .zip files can be uploaded
				if(importFileName.toLowerCase().endsWith(".zip"))
				{
					ImpExMedia resourceMedia=ImpExManager.getInstance().createImpExMedia("resources.zip");
					resourceMedia.setData( new DataInputStream(fis[1].openStream()), "resources.zip", ImpExConstants.File.MIME_TYPE_ZIP );
					resourceMedia.setRemoveOnSuccess( true );
					resourceMediaPK = resourceMedia.getPK().toString();
					%><c:set var="fileUploaded" value="1"/><%
					break;
				}
				else
				{
					out.print("<br/><font color='red'>The second file [" + importFileName + "] is not valid.</font><br/>");
					out.print("<font color='red'>Only the bmecat zip file can be imported.</font><br/>");
					break;
				}
			}
		}

		MediaDataTranslator.unsetMediaDataHandler( );	
		if( u != null )
		{
			jaloSession.setUser( u );
		}

	}
%>
<p><br />
<b>Note: Please also check the application server output if errors occured.</b>
<p>

<c:choose>
<c:when test="${fileUploaded == 1}">
<table style="text-align: left; width: 100%;" border="0" cellpadding="0" cellspacing="0">
	<tbody>
		<tr>
			<td>
				Scriptarea:
				<form name="bmecatImportForm" action="validation.jsp" method="post">
					<input type="hidden" name="mediaPK" value="<%= mediaPK %>"></input>
					<input type="hidden" name="scriptConstants" value="<%= scriptConstants %>"></input>
					<input type="hidden" name="resourceMediaPK" value="<%= resourceMediaPK %>"></input>
					<textarea name="script" cols="100" rows="20" wrap="off"><%=script%></textarea>
					<p/>
					<input type="submit" name="Submit" value="Go!"/>
				</form>
			</td>
		</tr>
	</tbody>
</table>
</c:when>
</c:choose>

<%@ include file="./inc/tail.jspf"%>
