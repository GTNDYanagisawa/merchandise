<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="bindResult" required="true"
	type="org.springframework.validation.BindingResult"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>

<%@ taglib prefix="conf" uri="sapproductconfig.tld"%>

<c:if test="${bindResult.fieldErrorCount gt 0}">
	<c:set var="validationErrors"
		value="${conf:csticValidationError(bindResult, path)}" />
	<c:set var="warnings"
		value="${conf:csticWarnings(bindResult, path)}" />

	<c:if test="${fn:length(validationErrors) gt 0}">
		<span id="Errors" class="product-config-error"> <c:forEach
				var="message" items="${validationErrors}" varStatus="status">
				<span id="" class=product-config-error-msg> <spring:message
						code="${message.code}" arguments="${message.args}"
						text="${message.message}" />
				</span>
			</c:forEach>
		</span>
	</c:if>

	<c:if test="${fn:length(warnings) gt 0}">
		<span id="Warnings" class="product-config-warning"> <c:forEach
				var="message" items="${warnings}" varStatus="status">
				<span id="" class="product-config-warning-msg"> <spring:message
						code="${message.code}" arguments="${message.args}"
						text="${message.message}" />
				</span>
			</c:forEach>
		</span>
	</c:if>



	<c:set var="browser" value="${header['User-Agent']}" scope="session"/>
	
	<c:choose>
		<c:when test="${fn:contains(header['User-Agent'],'Chrome')}">
			<div style="width: 0;height:0;overflow: hidden;">
				<input id="${path}.errors" style="opacity:0;filter:alpha(opacity=0);" tabindex="-1"/>
			</div>
		</c:when>
		<c:otherwise>
			<span id="${path}.errors" tabindex="-1"/>
		</c:otherwise>
	</c:choose>
</c:if>