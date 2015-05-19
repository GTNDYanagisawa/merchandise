<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html> 
<head> 
	<meta http-equiv="content-type" content="text/html; charset=UTF-8"> 
	<link rel="stylesheet" href="<c:url value='/styles/hybris_main.css'/>" /> 
</head> 
<body> 
<c:if test="${empty param['main']}">
	<c:set var="main" value="active"/>
</c:if>
<c:if test="${empty param['onewlink']}">
	<c:set var="onewlink" value="active"/>
</c:if>
<c:if test="${empty param['onewtext']}">
	<c:set var="onewtext" value="active"/>
</c:if>
<c:if test="${empty param['twowdedicated']}">
	<c:set var="twowdedicated" value="active"/>
</c:if>

<div id="header"> 
	<div id="headtop"> 
		<img src="<c:url value='/images/y_logo.gif'/>"> 
	</div> 
	<div class="mainNavBorder"> 
 	</div> 
 	<div id="MainNav"> 	
 		<ul> 
			<li class="${main}"> 
 				<a href="<c:url value='/'/>"> 
 					<span class="mainNavItem"><img src="<c:url value='/images/home.gif'/>">&nbsp;home</span> 
 				</a> 
 			</li> 
 			<li class="${onewlink}"> 
 				<a href="<c:url value='/view/page/link.html'/>"> 
 					<span class="mainNavItem">1Way link Message</span> 
 				</a> 
 			</li> 
 			<li  class="${twowdedicated}"> 
 				<a href="<c:url value='/view/page/mo.html'/>"> 
 					<span class="mainNavItem">2Ways Messaging</span> 
 				</a> 
 			</li> 
 			<li class="${onewtext}"> 
 				<a href="<c:url value='/view/page/text.html'/>"> 
 					<span class="mainNavItem">1Way text Message</span> 
 				</a> 
 			</li> 
 		</ul> 
 	</div> 
</div> 
 
<div id="main"> 
 
<!-- start mobile content --> 
