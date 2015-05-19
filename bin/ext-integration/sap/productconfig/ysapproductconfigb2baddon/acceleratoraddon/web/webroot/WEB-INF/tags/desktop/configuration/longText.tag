<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ attribute name="cstic"
	type="de.hybris.platform.sap.productconfig.facades.CsticData"%>

<%@ attribute name="pathPrefix" required="false"
	type="java.lang.String"%>

<c:set var="text" value="${cstic.longText}"/>

<c:if test="${not empty text}">
	<div id="${cstic.key}.longText" class="product-config-cstic-long-text">
		<c:set var="moreLength" value="280"/>
		<c:set var="textLength" value="${fn:length(text)}"/>
	
		<c:choose>
			<c:when test="${textLength > moreLength and not cstic.longTextHTMLFormat}">
				<c:set var="shortText" value="${fn:substring(text, 0, moreLength)}"/> 
				<c:set var="moreText" value="${fn:substring(text, moreLength, textLength)}"/>
				${shortText} <a <c:if test="${cstic.showFullLongText}">style="display:none"</c:if> class="product-config-cstic-morelink"><spring:message code="sapproductconfig.cstic.more.link"
				text="> more" /></a><div <c:if test="${not (cstic.showFullLongText)}">style="display:none"</c:if> class="product-config-cstic-moretext">${moreText}<a class="product-config-cstic-lesslink"><spring:message code="sapproductconfig.cstic.less.link"
				text="< less" /></a></div>
			</c:when>
			<c:otherwise>
				${text}
			</c:otherwise>
		</c:choose>
		<form:input type="hidden"  id="${cstic.key}.showFullLongText"
 			path="${pathPrefix}showFullLongText" />			 
		
	</div>
</c:if>