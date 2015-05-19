<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="visible" required="true" type="java.lang.Boolean"%>

<c:if test="${visible}">
	<div class="product-config-legend-required">
		<spring:message code="com.sap.hybris.productconfig.legend.mandatory"
			var="requiredText"  text="Fields marked with an asterisk are mandatory" />
		<c:out value="${requiredText}" />
	</div>
</c:if>