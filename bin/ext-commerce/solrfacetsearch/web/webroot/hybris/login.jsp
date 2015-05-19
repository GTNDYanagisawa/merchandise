<!DOCTYPE html PUBLIC "-//thestyleworks.de//DTD XHTML 1.0 Custom//EN" "../dtd/xhtml1-custom.dtd">

<%@page import="de.hybris.platform.util.JspContext"%>
<%@page import="de.hybris.platform.core.*"%>

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
	
	boolean failed = request.getParameter("login_error")!=null;
	
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
	<link rel="stylesheet" href="login.css">
	<title> hybris Search &amp; Navigation - Embedded Apache Solr - tenant [<%=Registry.getCurrentTenant().getTenantID()%>]</title>
	<link rel="shortcut icon" href="favicon.ico">
</head>
<body>
	
<div class="login_grid">
<form method="POST" name="login" action="../j_spring_security_check">

		<% if( failed ){ %>
			<div class="wrong_credentials">Wrong credentials!</div>
		<% }%>
		<div class="form-group">
			<label>Login:</label>
			<input type="text" name="j_username" id="j_username" value="" onkeypress="return checkForEnter(event);" onfocus="this.select();">
		</div>
		<div class="form-group">
		<label>Password:</label>
		<input type="password" name="j_password" id="j_password" value=""  onkeypress="return checkForEnter(event);" onfocus="this.select();" autocomplete="off">
		</div>
		<div class="form-group">
		<input type="submit" title="Login" value="Login" onclick="document.login.submit()" name="login" id="j_login" hidefocus="true"/>
		</div>
		</form>
</div>


</body>
</html>

<script type="text/javascript">
document.login.j_username.focus();
</script>

