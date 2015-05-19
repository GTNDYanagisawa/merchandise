<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="bindResult" required="true"
	type="org.springframework.validation.BindingResult"%>

<%@ attribute name="groupPath" required="false"
	type="java.lang.String"%>

<%@ taglib prefix="conf" uri="sapproductconfig.tld"%>

<c:if test="${not empty bindResult}">
	<c:choose>
		<c:when test="${empty groupPath}">
			<c:set var="errors" value="${conf:validationError(bindResult)}" />
			<c:set var="warnings" value="${conf:warnings(bindResult)}" />
		</c:when>
		<c:otherwise>
			<c:set var="errors" value="${conf:groupValidationError(bindResult, groupPath)}" />
			<c:set var="warnings" value="${conf:groupWarnings(bindResult, groupPath)}" />
		</c:otherwise>
	</c:choose>
	
	<c:if test="${fn:length(errors) gt 0 || fn:length(warnings) gt 0}">
		<div class="product-config-head-errormessages">
			<c:if test="${fn:length(errors) gt 0}">
				<div class="product-config-head-error">
					<span class="product-config-head-error-title">
						<spring:message
							code="sapproductconfig.head.error.title"
							text="Please check the errors below" />
					</span>
					<c:forEach var="error" items="${errors}" varStatus="status">
						<span class="product-config-head-error-msg">
							<a href="javascript:focusOnInput('${error.path}');">
								<spring:message code="${error.code}" arguments="${error.args}" text="${error.message}" />
							</a>
						</span>
						<br />
					</c:forEach>
				</div>
			</c:if>
		
			<c:if test="${fn:length(warnings) gt 0}">
				<div class="product-config-head-warning">
					<span class="product-config-head-warning-title">
						<spring:message
							code="sapproductconfig.head.warning.title"
							text="Please prove the following Conflict messages below" />
					</span>
					<c:forEach var="warning" items="${warnings}" varStatus="status">
						<span class="product-config-head-warning-msg">
							<a href="javascript:focusOnInput('${warning.path}');">
								<spring:message code="${warning.code}"  arguments="${warning.args}" text="${warning.message}" />
							</a>
						</span>
						<br />
					</c:forEach>
				</div>
			</c:if>
		</div>
	</c:if>
</c:if>
