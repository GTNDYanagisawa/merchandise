<%@include file="head.inc"%>

<%
	UserManager.getInstance().deleteLoginTokenCookie( request, response );
	
	LogOutChip theChip = (LogOutChip) request.getAttribute(AbstractChip.CHIP_KEY);
	de.hybris.platform.util.WebSessionFunctions.invalidateSession( request.getSession() );
	response.sendRedirect(response.encodeRedirectURL(HMCMasterServlet.MASTERSERVLET));
	
	if(true) return;
%>
