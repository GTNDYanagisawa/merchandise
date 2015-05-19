<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="product-config-title-summary">
	<div class="product-config-title-text">
	   	<spring:message text="Configuration of ${product.name}"
				code="configure.product.header.message"
				arguments="${product.name}" />	
	</div>
	
	<c:if test="${showSummary}">
	<div class="product-config-summary-text">
		<c:out value="${product.summary}"/>	
	</div>
	</c:if>
</div>