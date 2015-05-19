<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="pageCss" required="false" fragment="true"%>
<%@ attribute name="pageScripts" required="false" fragment="true"%>

<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="spheader" tagdir="/WEB-INF/tags/addons/secureportaladdon/desktop/common/spheader"%>
<%@ taglib prefix="spfooter" tagdir="/WEB-INF/tags/addons/secureportaladdon/desktop/common/spfooter"%>


<template:master pageTitle="${pageTitle}">

	<jsp:attribute name="pageCss">
		<jsp:invoke fragment="pageCss" />
	</jsp:attribute>

	<jsp:attribute name="pageScripts">
		<jsp:invoke fragment="pageScripts" />
	</jsp:attribute>

	<jsp:body>
		<div id="wrapper" background="world_map.png">
			<div id="page">
				<spring:theme code="text.skipToContent" var="skipToContent" />
				<a href="#skip-to-content" class="skiptocontent">${skipToContent}</a>
				<spring:theme code="text.skipToNavigation" var="skipToNavigation" />
				<a href="#skiptonavigation" class="skiptonavigation">${skipToNavigation}</a>
				<spheader:header />
				<a id="skiptonavigation"></a>
				<div id="content">
					<a id="skip-to-content"></a>
					<jsp:doBody />
				</div>
				<spfooter:footer />
			</div>
		</div>

	</jsp:body>

</template:master>
