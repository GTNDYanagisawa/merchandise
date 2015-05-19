<%@include file="./inc/head.jspf"%>

<%@page import="java.util.zip.*"%>

<%@page import="de.hybris.platform.jalo.media.*"%>
<%@page import="de.hybris.platform.impex.constants.*"%>
<%@page import="de.hybris.platform.impex.jalo.*"%>
<%@page import="de.hybris.platform.impex.jalo.cronjob.*"%>

<%@page import="de.hybris.platform.cronjob.jalo.CronJobToJspLogListener"%>
<%@page import="de.hybris.platform.util.logging.HybrisLogger"%>
<div class="absatz">
<a href="<%=response.encodeURL("/")%>">[Back to Administration web]</a>
</div>
<div class="absatz">
&nbsp;
</div>
<div class="absatz">

<h1>BMECat import result</h1>
<% 
	JspContext jspc = new JspContext(out, request, response);
	String script = request.getParameter("script") != null ? request.getParameter("script") : "";
	String mediaPK = request.getParameter("mediaPK") != null ? request.getParameter("mediaPK") : "";
	String resourceMediaPK = request.getParameter("resourceMediaPK") != null ? request.getParameter("resourceMediaPK") : "";
	String scriptConstants = request.getParameter("scriptConstants") != null ? request.getParameter("scriptConstants") : "";
	out.write("script: " + scriptConstants + script + "<br/>");

	// create import media	
	ImpExMedia importMedia=ImpExManager.getInstance().createImpExMedia("importscript.impex");
	importMedia.setData( new DataInputStream(new ByteArrayInputStream((scriptConstants + script).getBytes())), "importscript.impex", ImpExConstants.File.MIME_TYPE_IMPEX );
	importMedia.setRemoveOnSuccess( true );
	
	// create exteneral data
	Collection<ImpExMedia> externalData = new ArrayList<ImpExMedia>();
	//TODO check currency
	try
	{
		Media zip=(Media) JaloSession.getCurrentSession().getItem( PK.parse( mediaPK ) );
		ZipInputStream csvZipStream=new ZipInputStream(zip.getDataFromStreamSure());
		ZipEntry entry;
		while( ( entry = csvZipStream.getNextEntry() ) != null )
		{
			// XXX: avoid temp file by setting data directly to media. Problem at the moment is, that the stream will be closed at each Media.setDate
			File temp = File.createTempFile( "zipentry", "impex" );
			MediaUtil.copy( csvZipStream, new FileOutputStream( temp ) );
			ImpExMedia externalMedia=ImpExManager.getInstance().createImpExMedia(entry.getName());
			externalMedia.setData( new DataInputStream(new FileInputStream(temp)), entry.getName(), ImpExConstants.File.MIME_TYPE_CSV );
			externalMedia.setRemoveOnSuccess( true );
			externalMedia.setLinesToSkip(1);
			externalData.add( externalMedia );
			if( !temp.delete() )
			{
				System.err.println( "Can not delete temporary file " + temp.getAbsolutePath() );
			}
		}
		csvZipStream.close();
		
		// get media with images
		ImpExMedia resourceMedia=(ImpExMedia) JaloSession.getCurrentSession().getItem( PK.parse( resourceMediaPK ) );

		// create impex cronjob and start it		
		ImpExImportCronJob cronJob = ImpExManager.getInstance().createDefaultImpExImportCronJob();
		cronJob.setEnableCodeExecution( true );
		cronJob.setJobMedia( importMedia );
		cronJob.setExternalDataCollection( externalData );
		cronJob.setMediasMedia(resourceMedia);
		String cronJobString = "cronjob with PK=" + cronJob.getPK() + " and name=" + cronJob.getCode();
		out.print( "<br>Starting import synchronous using " + cronJobString + "<br>" );
		CronJobToJspLogListener listener = null;
		try
		{
			listener = new CronJobToJspLogListener( cronJob, jspc );
			HybrisLogger.addListener( listener );
			cronJob = ImpExManager.getInstance().importData( cronJob, true, true );
		}
		finally
		{
			// remove log listener
			if( listener != null && !HybrisLogger.removeListener( listener ) )
			{
				out.print("WARNING: Can not remove log listener for cronjob " + cronJobString );
			}
		}
		if( cronJob==null )
		{
			out.print( "<br><font color='green'>Import was successful (using " + cronJobString + ")</font><br>" );
		}
		else
		{
			out.print( "<br><font color='red'>The content to be imported has caused an error, please refer to the CronJob <a href=\"/hmc/hybris?open=" + cronJob.getPK() + "\">" + cronJob.getCode() + "</a> " + (cronJob.getUnresolvedDataStore() == null ? " (no lines were dumped) " : " as well as the CronJob's dump file <a href=\"/hmc/hybris?open=" + cronJob.getUnresolvedDataStore().getPK() + "\">" + cronJob.getUnresolvedDataStore().getCode() + "</a>") + " for details.</font><br>" );
		}
	}
	catch( ImpExException hve)
	{
		//XXX unknown type, exception caught in console, but not in program?
		out.write("unknown type on screen: " + hve.getMessage());
		System.out.println("unknown type in console: " + hve.getMessage());
		hve.printStackTrace();
	}
	catch( JaloItemNotFoundException e )
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	catch( JaloBusinessException e )
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	catch( IOException e )
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

%>


<p />
TODO: if the csv files in the zip file is not correct, some errors should be displayed.
<p>

<%@include file="./inc/tail.jspf"%>
