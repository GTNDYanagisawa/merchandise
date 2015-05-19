<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="cart" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<c:url value="/cart" var="cartUrl"/>

<c:if test="${isInspectOperation}">
	<c:url value="" var="cartUrl"/>
</c:if>

<li class="yCmsComponent miniCart">
	<a href="${cartUrl}" class="minicart">
		<spring:theme code="punchout.minicart.title" />
		
		<ycommerce:testId code="miniCart_items_label">
			<span class="count"><c:out value="${fn:length(cart.entries)}" /></span>	
			<span class="price">
				<format:price priceData="${cart.totalPrice}"/>
			</span>
		</ycommerce:testId>
	</a>
</li>
