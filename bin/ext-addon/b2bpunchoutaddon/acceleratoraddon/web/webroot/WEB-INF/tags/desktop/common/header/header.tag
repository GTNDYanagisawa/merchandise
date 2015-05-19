<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="hideHeaderLinks" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header"  %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="addoncart" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/desktop/cart" %>

<c:url value="/" context="/yb2bacceleratorstorefront" var="homePageURL"/>

<c:if test="${isInspectOperation}">
	<c:url value="" var="homePageURL"/>
</c:if>

<%-- Test if the UiExperience is currently overriden and we should show the UiExperience prompt --%>
<c:if test="${uiExperienceOverride and not sessionScope.hideUiExperienceLevelOverridePrompt}">
	<c:url value="/_s/ui-experience?level=" var="clearUiExperienceLevelOverrideUrl"/>
	<c:url value="/_s/ui-experience-level-prompt?hide=true" var="stayOnDesktopStoreUrl"/>
	<div class="backToMobileStore">
		<a href="${clearUiExperienceLevelOverrideUrl}"><span class="greyDot">&lt;</span><spring:theme code="text.swithToMobileStore" /></a>
		<span class="greyDot closeDot"><a href="${stayOnDesktopStoreUrl}">x</a></span>
	</div>
</c:if>

<div id="header" class="clearfix">
	<div class="headerContent ">
		<ul class="nav clearfix">
			<sec:authorize ifAnyGranted="ROLE_CUSTOMERGROUP">
				<li class="logged_in"><ycommerce:testId code="header_LoggedUser"><spring:theme code="header.welcome" arguments="${user.firstName},${user.lastName}" htmlEscape="true"/></ycommerce:testId></li>
			</sec:authorize>
						
			<c:choose>
				<c:when test="${isInspectOperation}">
					<addoncart:miniCart cart="${cartData}" />
				</c:when>
				<c:otherwise>
					<cms:pageSlot position="MiniCart" var="cart" limit="1">
						<cms:component component="${cart}" element="li" class="miniCart" />
					</cms:pageSlot>
				</c:otherwise>
			</c:choose>
		</ul>
	</div>

	<div class="headerContent secondRow">
		<c:if test="${not isInspectOperation}">
			<cms:pageSlot position="SearchBox" var="component">
				<cms:component component="${component}"/>
			</cms:pageSlot>
		</c:if>
	</div>
	<div class="yCmsComponent siteLogo">
		<div class="simple_disp-img">
			<a href="${homePageURL}">
				<spring:theme code="punchout.img.logo.alt" var="altLogo"/>
				<theme:image code="img.punchout.logo" alt="${altLogo}" title="${altLogo}"/>
			</a>
		</div>
	</div>
</div>
