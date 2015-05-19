<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@include file="head.inc"%>
<%
	Frame theChip = (Frame)request.getAttribute(AbstractChip.CHIP_KEY);
	Chip mainChip = theChip.getMainChip();
	boolean developerMode = ConfigConstants.getInstance().DEVELOPERMODE;

	String title = "";
	String bodyBackground = "#d3d3d8";		// default background color

	if( theChip instanceof Window )
	{
		title = ((Window)theChip).getName();
		if( ((Window)theChip).getBackgroundColor() != null )
	bodyBackground = ((Window)theChip).getBackgroundColor();
	}
	if( mainChip instanceof ExplorerChip )
	{
		if( ((ExplorerChip)mainChip).getTitle() != null )
		{
	title = title + ": " + ((ExplorerChip)mainChip).getTitle();
		}
	}

	boolean chipIsModal = mainChip.getClass().getName().substring(mainChip.getClass().getName().lastIndexOf('.')+1).startsWith("Modal");
	boolean isModalMediaUpload = mainChip.getClass().getName().substring(mainChip.getClass().getName().lastIndexOf('.')+1).startsWith("ModalMedia");
%>




<html>
	<head>
		<title><%=title%> - hybris Management Console (hMC)</title>
		<link rel="shortcut icon" type="image/x-icon" href="/hmc/favicon.ico">
		<meta http-equiv="expires" content="-1"/>
		<meta http-equiv="cache-control" content="no-cache"/>
		<meta http-equiv="pragma" content="no-cache"/>

	<!-- hmc css styles -->
		<link rel="stylesheet" type="text/css" media="all" href="css/hmc.css"/>


	<!-- third party javascript libs -->

		<script type="text/javascript" language="JavaScript1.2" src="js/hmc.js"></script>

	<!-- date picker (third party, but heavily customized) -->
		<script type="text/javascript" language="JavaScript1.2" src="js/date.js"></script>
		<script type="text/javascript" language="JavaScript1.2" src="js/calendar.js"></script>

		<%@include file="js/eventHandler.inc"%>


	</head>
	<body style="cursor:default" bgcolor="<%= bodyBackground %>" leftmargin="0" topmargin="0"
			marginheight="0" marginwidth="0" style="height:100%"
			onkeydown="if(checkForF5(event)) { exit = false; }">

<% if( DEBUG_COMMENTS ) { %><!-- outer table start (containing the whole page) --><% } %>

		<form action="<%= getRequestURL() %>" method="post" onsubmit="" name="editorForm">
		<table style="height:100%; width: 100%; white-space:nowrap;" width="100%" height="100%" cellpadding="0" cellspacing="0" border="0" >
<%
			if( theChip.displayHeader() )
			{
%>
<% if( DEBUG_COMMENTS ) { %><!-- header (containing hybris platform logo) --><% } %>
				<tr class="page-header">
					<td class="page-header-left">&nbsp;&nbsp;<%=title%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td align="right" valign="top" class="page-header-right"><img src="css/src/images/y_logo_platform.gif" border="0" name="logo"></td>
				</tr>
<%
			}
%>

<% if( DEBUG_COMMENTS ) { %><!-- top border --><% } %>
<%
			String bgStyle = (chipIsModal ? (mainChip instanceof ItemChip ? "background-color:"+bodyBackground+";" : "background-color:#f2f2f5;") : "");
%>
			<tr style="vertical-align:top; <%= bgStyle %>">
<%
				if( theChip.displayHeader() )
				{
%>
					
<%
				}
%>
				<td id="outerTD" colspan="2" style="vertical-align:top; white-space:nowrap;">
<%
					if( theChip.hasSystemMessage() )
					{
%>
						<table cellpadding="0" cellspacing="0" border="0" style="width:100%;">
							<tr>
								<td style="padding: 2px; text-align:center;">
									<div style="border:2px solid #c00000; font-weight:bold;">
										<%= theChip.getSystemMessage() %>
									</div>
								</td>
							</tr>
							<tr>
								<td>
<%
					}

					if( chipIsModal )
					{
						// additional top red border
%>
						<table cellpadding="0" cellspacing="0" style="width:100%;">
							<tr style="height:7px; vertical-align:top; font-size:1pt;">
								<td> </td>
							</tr>
							<tr style="vertical-align:top;">
								<td style="padding:5px;">
								<%-- closing /tr and /table follow below --%>
<%
					}
					mainChip.render(pageContext);

					if( theChip.hasSystemMessage() )
					{
%>
								</td>
							</tr>
						</table>
<%
					}
%>
		<!-- script for adding invisible input elements for registered key events (to override internet explorer shortcut keys) -->
			<script language="JavaScript1.2">
				if( ie5 )
				{
					// overriding explorer access keys
					for( var i = 0; i < accessKeys.length; i++ )
					{
						document.write('<input name="keymapping_' + accessKeys[i] + '" accesskey="' + accessKeys[i] + '" style="width:0px; height:0px;"/>');
					}
				}
			</script>

			</td>
<%
			if( chipIsModal )
			{
%>
							<%-- continuing from above --%>
							</tr>
						</table>
<%
			}
			if( theChip.displayHeader() )
			{
%>
				
<%
			}
%>
			</tr>
<%
			if( !chipIsModal )
			{
%>

			<tr style="width:100%; height:7px;">
				<td colspan="2"> </td>
			</tr>
<%
			}
%>
		</table>
		</form>


<% if( DEBUG_COMMENTS ) { %><!-- outer table end --><% } %>

<!-- jsp & javascript code to show error and warning messages -->
		<script language="JavaScript1.2">
<%
			for (final Iterator it = theDisplayState.getErrorMessages().iterator(); it.hasNext(); )
			{
%>
				y_showErrorMessage("<%= (String) it.next() %>", "<%= localized("popup.error.title") %>", "<%= localized("popup.error.button") %>");
<%
			}
			theDisplayState.clearErrorMessages();
			for (final Iterator it = theDisplayState.getInfoMessages().iterator(); it.hasNext(); )
			{
%>
				y_showInfoMessage("<%= (String) it.next() %>", "<%= localized("popup.info.title") %>", "<%= localized("popup.info.button") %>");
<%
			}
			theDisplayState.clearInfoMessages();
%>
			window.scrollTo(<%= theChip.getScrollX() %>, <%= theChip.getScrollY() %>);
			
		</script>
<%

	class MyObjectInputStream extends ObjectInputStream
	{
		ClassLoader theClassLoader;
		
		public MyObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException
		{
			super(in);
			theClassLoader = classLoader;
		}
		
		protected Class resolveClass(ObjectStreamClass objectstreamclass) throws IOException, ClassNotFoundException
		{
			String s = objectstreamclass.getName();
			return Class.forName(s, false, theClassLoader);
		}
	}

	if( false && Config.getBoolean("core.sourceavailable",false) )
	{
		boolean exception = false;
		byte[] data = null;
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		
		ClassLoader myClassLoader = this.getClass().getClassLoader();
	
		try
		{
			// serialize jalosession		
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject( (Serializable)session.getAttribute("jalosession") );
			data = baos.toByteArray();
			oos.flush();
			oos.close();
			baos.close();
		}
		catch( Exception e )
		{
			exception = true;
			PrintWriter pw = new PrintWriter(out, true);
			out.println("Exception during jalosession serialization: <br>");
			out.println("<pre>");
			e.printStackTrace(pw); 
			out.println("</pre>");
		}
		
		
		
		if( !exception )
		{	
			try
			{
				// deserialize jalosession
				bais = new ByteArrayInputStream( data );
				ois = new MyObjectInputStream( bais, myClassLoader );
				ois.readObject();
				ois.close();
				bais.close();
				System.out.println( "JaloSession Size: "+data.length );
			}
			catch( Exception e )
			{
				exception = true;
				PrintWriter pw = new PrintWriter(out, true);
				out.println("Exception during jalosession de-serialization: <br>");
				out.println("<pre>");
				e.printStackTrace(pw); 
				out.println("</pre>");
			}
		}
			
		if( !exception )
		{	
			try
			{
				// serialize displaystate
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject( (Serializable)session.getAttribute("state") );
				data = baos.toByteArray();
				oos.flush();
				oos.close();
				baos.close();
			}
			catch( Exception e )
			{
				exception = true;
				PrintWriter pw = new PrintWriter(out, true);
				out.println("Exception during displaystate serialization: <br>");
				out.println("<pre>");
				e.printStackTrace(pw); 
				out.println("</pre>");
			}
		}
	
		if( !exception )
		{	
			try
			{
				// deserialize displaystate
				bais = new ByteArrayInputStream( data );
				ois = new MyObjectInputStream( bais, myClassLoader );
				ois.readObject();
				ois.close();
				bais.close();
				System.out.println( "DisplayState Size: "+data.length );
			}
			catch( Exception e )
			{
				exception = true;
				PrintWriter pw = new PrintWriter(out, true);
				out.println("Exception during displaystate de-serialization: <br>");
				out.println("<pre>");
				e.printStackTrace(pw); 
				out.println("</pre>");
			}
		} 
	}
%>
<%= StructureLoader.hasWarnings() ? "<!-- XYZ There are hmc type warnings!! XYZ -->" : "" %>
	</body>
</html>
