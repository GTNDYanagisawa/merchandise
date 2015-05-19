<%@page import="de.hybris.platform.hmc.HMCMasterServlet"%>

<%
 String backurl = request.getParameter("backurl");
%>

<!DOCTYPE html PUBLIC "-//thestyleworks.de//DTD XHTML 1.0 Custom//EN" "../dtd/xhtml1-custom.dtd">

<%@page import="de.hybris.platform.core.Tenant"%>
<%@page import="de.hybris.platform.core.Registry"%>
<%@page import="de.hybris.platform.core.SlaveTenant"%><html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="hybris_frontpage.css">
<title>[<%=Registry.getCurrentTenant().getTenantID()%>] - Management Console - hybris e-business software</title>
</head>
	
	<script language = "JavaScript1.2">
		x=(screen.width/2)+(screen.width/4)+(screen.width/8);
		y=(screen.height/2)+(screen.height/4);
		newWindow = window.open("<%=HMCMasterServlet.MASTERSERVLET%>","hmc<%=(int)(Math.random()*10000)%>", "height="+y+",width="+x+",screenX=100,screenY=50,status=yes,dependent=yes,scrollbars=yes,resizable=yes,directories=no,location=no,left=80,top=50");
		newWindow.focus();
	</script>

<body>
<div id="head">
	&nbsp;
</div><div id="rightmargin">&nbsp;</div>
		<div id="headsystem" class="header">
<%
		Tenant hs = Registry.getCurrentTenant();
		if( hs instanceof SlaveTenant )
		{
%> 
		&lt;&lt;<%=hs.getTenantID()%>&gt;&gt; <br/>
		<a href="/?setmaster=true" style="color:white;font-size:10px;"> [Back to master tenant] </a>
<%
		}
%>
		</div>
<div id="headtop">
	<img name ="head_E-Business_Software" src="images/HEAD_e-business_platform.gif" />
	<br /> 
 	<img src="images/transp.gif" height="20" /> 
 	<br /> 
 	<div class="header">Management Console</div>
</div>
<div id="MainNav">
</div><div id="Scaleback">&nbsp;</div>
<div id="main">

	<div id="left">
	</div>
	

	<div id="right">
	</div>
	

	<div id="content">
<div class="absatz">
<%
String hacContextPath = de.hybris.platform.util.Utilities.getExtensionInfo("hac").getWebModule().getWebRoot();
if (hacContextPath == null || hacContextPath.length() == 0)
{
  hacContextPath = "/";
}
%>
<a href="<%=response.encodeURL(hacContextPath)%>">[Back to main page]</a><p/>
</div>
<div class="absatz">
&nbsp;
</div>
		<p/>
				Note: If the Management Console window does not open, this may be due to a pop-up window blocker 
				function of your web browser. Please disable pop-up blocking for this browser session and refresh this page.
	</div>
</div>
</body>
</html>

<%
try{session.invalidate();}catch(Exception e ){/**/}
%>

