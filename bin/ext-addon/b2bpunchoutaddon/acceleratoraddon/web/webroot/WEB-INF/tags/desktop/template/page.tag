<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true" %>
<%@ attribute name="pageCss" required="false" fragment="true" %>
<%@ attribute name="pageScripts" required="false" fragment="true" %>
<%@ attribute name="hideHeaderLinks" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/desktop/common/header" %>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/desktop/common/footer" %>


<template:master pageTitle="${pageTitle}">

	<jsp:attribute name="pageCss">
		<jsp:invoke fragment="pageCss"/>
	</jsp:attribute>
 
	<jsp:attribute name="pageScripts">
		<jsp:invoke fragment="pageScripts"/>
	</jsp:attribute>

	<jsp:body>

		<div id="page" data-currency-iso-code="${currentCurrency.isocode}">
			<spring:theme code="text.skipToContent" var="skipToContent"/>
			<a href="#skip-to-content" class="skiptocontent" data-role="none">${skipToContent}</a>
			<spring:theme code="text.skipToNavigation" var="skipToNavigation"/>
			<a href="#skiptonavigation" class="skiptonavigation" data-role="none">${skipToNavigation}</a>
			<header:header hideHeaderLinks="${hideHeaderLinks}"/>
			<a id="skiptonavigation"></a>
			<c:if test="${not isInspectOperation}">
				<nav:topNavigation/>
			</c:if>
			<div id="content" class="clearfix">
			<a id="skip-to-content"></a>
				<jsp:doBody/>
			</div>
			<footer:footer/>
		</div>

	</jsp:body>
	
</template:master>
