<%@page import="java.text.MessageFormat"%>
<%@page import="java.io.PrintWriter"%>

<%@page import="de.hybris.platform.util.Config"%>

<%@page import="de.hybris.platform.hmc.*"%>
<%@page import="de.hybris.platform.hmc.jalo.*"%>

<%
	boolean developerMode = "true".equals( session.getAttribute( "developermode" ) );
%>

<html>
	<head>
		<title>hybris Management Console (hMC)</title>
		<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="cache-control" content="no-cache">
		<link rel="stylesheet" type="text/css" media="all" href="css/hmc.css"/>
	</head>
	
	<body>

		<table style="height:100%; width: 100%; white-space:nowrap;" width="100%" height="100%" cellpadding="0" cellspacing="0" border="0" >
				<tr class="page-header">
					<td class="page-header-left"><span class="error_headline" style="color:white;font-size:24px;font-weight:bold;margin-left:10px">Error</span></td>
					<td align="right" valign="top" class="page-header-right"><img src="css/src/images/y_logo_platform.gif" border="0" name="logo"></td>
				</tr>
				<tr style="vertical-align: top;">
					<td style="vertical-align: top; white-space: nowrap;" colspan="2" id="outerTD">
						<table cellspacing="0" cellpadding="0" class="content_table" style="height: 100%; width: 100%;">
							<tbody><tr>
								<td align="top" class="toolbar_container" colspan="3">
									<table cellspacing="0" cellpadding="0" class="toolbar">
										<tbody><tr>
											<td class="leftActionChips">&nbsp;</td>
											<td class="rightActionChips">&nbsp;</td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
					</tbody>
				</table>
			</td>
	</tr>
	<tr height="100%">
		<td colspan="2" class="error_content" style="padding:20px;background-color:white" valign="top">
						<table width="600px">
							<tr>
								<td class="item">
									<%
										if( developerMode ) 
										{
											Exception exp = (Exception)session.getAttribute( "exception" );	
											PrintWriter pw = new PrintWriter( out, true );
									%>
																		<b>Unhandled Exception:</b><br/>
																		<pre>
									<%	
											exp.printStackTrace( pw ); 
											if( exp instanceof HMCSystemException && ((HMCSystemException)exp).getThrowable() != null )
											{
												((HMCSystemException)exp).getThrowable().printStackTrace( pw );
											}
									%>
																		</pre>
									<%	
										}
										else
										{
											String errormessage = (String) session.getAttribute("localizederrormessage");
											if( errormessage != null )
											{
												out.println( MessageFormat.format(errormessage, new Object[] { new java.util.Date().toString() }) );
											}
										}
									%>
									<p><a href="<%=HMCMasterServlet.MASTERSERVLET%>">Neu anmelden / Login again</a></p>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr style="width:100%; height:7px;">
					<td colspan="2"> </td>
			</tr>
		</table>

	</body>
</html>
