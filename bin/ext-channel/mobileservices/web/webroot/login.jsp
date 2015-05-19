<!DOCTYPE html PUBLIC "-//thestyleworks.de//DTD XHTML 1.0 Custom//EN" "../dtd/xhtml1-custom.dtd">

<%@page import="de.hybris.platform.util.JspContext"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.io.*"%>
<%@page import="java.net.*"%>
<%@page import="java.sql.*"%>
<%@page import="javax.sql.*"%>
<%@page import="java.lang.reflect.*"%>
<%@page import="javax.naming.*"%>
<%@page import="javax.transaction.*"%>

<%@page import="de.hybris.platform.core.*"%>
<%@page import="de.hybris.platform.cache.*"%>
<%@page import="de.hybris.platform.jalo.*"%>
<%@page import="de.hybris.platform.jalo.c2l.Currency" %>
<%@page import="de.hybris.platform.jalo.c2l.*"%>
<%@page import="de.hybris.platform.jalo.product.*"%>
<%@page import="de.hybris.platform.jalo.user.*"%>
<%@page import="de.hybris.platform.jdbcwrapper.*"%>
<%@page import="de.hybris.platform.jalo.type.*"%>
<%@page import="de.hybris.platform.jalo.order.*"%>
<%@page import="de.hybris.platform.jalo.meta.*"%>
<%@page import="de.hybris.platform.jalo.extension.*"%>
<%@page import="de.hybris.platform.jalo.flexiblesearch.*"%>
<%@page import="de.hybris.platform.persistence.flexiblesearch.*"%>
<%@page import="de.hybris.platform.util.*"%>
<%@page import="de.hybris.platform.licence.*"%>
<%@page import="de.hybris.platform.util.collections.*"%>

<script type="text/javascript">
function checkForEnter(event)
{
	if( (event ? event : window.event).keyCode == 13 )
	{
		document.login.submit();
	}
	return true;
}
</script>


<%
	if( request.getParameter("setmaster")!=null )
	{
		Registry.activateMasterTenant();
		response.sendRedirect("");
		return;
	}

	JaloSession jaloSession = null;
	try
	{
		jaloSession = WebSessionFunctions.getSession( request );
		if(jaloSession != null && jaloSession.getUser().getUID().equals("anonymous"))
		{
			jaloSession.setUser(UserManager.getInstance().getAdminEmployee());
		}
	}
	catch( Exception e )
	{
		System.err.println(e.getMessage());
		//e.printStackTrace( System.err );
	}
	response.setContentType("text/html; charset=utf-8");
	
	boolean initialzed = true;
	final Tenant tenant = Registry.getMasterTenant();
	if (tenant.getJaloConnection().isSystemInitialized())
	{
		try
		{
			jaloSession = WebSessionFunctions.getSession( request );
		}
		catch(Exception e)
		{
			initialzed = false;
		}
	}
	else
	{
		initialzed=false;
	}
	
	boolean failed = request.getParameter("login_error")!=null;
	
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
	<link rel="stylesheet" href="styles/hybris_main.css">
	<title> [<%=Registry.getCurrentTenant().getTenantID()%>] - Mobile Test Pages - hybris platform </title>
	<link rel="shortcut icon" href="favicon.ico">
</head>
<body>


<table width="100%" height="99%" cellspacing="0" cellpadding="0" border="0" style="height: 99%; width: 100%; white-space: nowrap;">
	<tr style="vertical-align: top;">
		<td style="white-space: nowrap;" colspan="2" id="outerTD" width="100%" height="100%">
			<table height="100%" width="100%" class="login_screen">
				<tr>
					<td align="center" valign="middle" width="100%" height="100%">
						<div class="login_screen_hmc">
							<form method="POST" name="login" action="j_spring_security_check">
								<table align="right">
									<tr>
										<td align="left">
											Login:
										</td>
										<td >
											<input type="text" name="j_username"
													 id="j_username"
													 value="" style="width:150px"
													 onkeypress="return checkForEnter(event);"
													 onfocus="this.select();">
										</td>
									</tr>
									<tr>
										<td align="left">
											Password:
										</td>
										<td align="right">
											<input type="password" name="j_password"
													 id="j_password"
													 value="" style="width:150px"
													 onkeypress="return checkForEnter(event);"
													 onfocus="this.select();">
										</td>
									</tr>
									<tr>
										<td align="left">
											Remember me?:
										</td>
										<td align="left">
											<input type="checkbox" name="_spring_security_remember_me" class="checkbox"
												 id="rememberme"
											 onkeypress="return checkForEnter(event);"
											 onfocus="this.select();" onblur="this.value=this.value;">
										</td>
									</tr>
								<%
									if( failed )
									{
								%>
									<tr>
										<td colspan="2" style="color: red">Wrong credentials!<br/><br/></td>
									</tr>
									
								<%
									}
								%>	
									<tr>
										<td style="width:73px;">
										</td>
										<td>
											<div class="z-button chip-event">
												<a href="#" title="Login" onclick="document.login.submit()" name="login" id="j_login" hidefocus="true">
													Login
												</a>
											</div>		
										</td>
									</tr>
								</table>
							</form>
						</div>
					</td>
				</tr>
			</table>				
		</td>
	</tr>
</table>

</body>
</html>

<script type="text/javascript">
document.login.j_username.focus();
</script>


<%
JaloSession.deactivate();
%>
