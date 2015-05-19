<%@ page import="de.hybris.platform.util.WebSessionFunctions"%>
<%@ page import="de.hybris.platform.jalo.JaloSession"%>
<%@ page import="de.hybris.platform.hmc.jalo.HMCManager"%>
<%@ page import="de.hybris.platform.hmc.webchips.DisplayState"%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<title>hybris e-business software</title>
	</head>
	
	<%	
		if( JaloSession.hasCurrentSession() )
		{
			JaloSession.getCurrentSession().close();
			JaloSession.deactivate();
		} 
		DisplayState.unsetCurrent();
		session.invalidate();
	%>
	
	<body>
		<center>
		<h1>Selenium vs. HMC</h1><br>
		This page is for testing purposes only and does nothing more than clean the Jalo session and set the language to DE.<br><br>
		<br/>
		<br/>
		<a href="../index.jsp?lang=de">HMCLogin</a>
		</center>
	</body>
</html>
