<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/desktop/template" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="addoncart" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/desktop/cart" %>

<spring:theme text="Your Shopping Cart" var="title" code="cart.page.title"/>
<c:url value="/cxml/requisition" context="/yb2bacceleratorstorefront/punchout" var="checkoutUrl"/>
<c:url value="/cxml/cancel" context="/yb2bacceleratorstorefront/punchout" var="cancelUrl"/>

<template:page pageTitle="${pageTitle}">

	<spring:theme code="basket.add.to.cart" var="basketAddToCart"/>
	<spring:theme code="cart.page.checkout" var="checkoutText"/>
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<common:globalMessages/>
	<cart:cartRestoration/>
	<cart:cartValidation/>
	
	<c:if test="${not empty cartData.entries}">
		<spring:url value="${continueUrl}" var="continueShoppingUrl" htmlEscape="true"/>

			<addoncart:checkoutButton url="${checkoutUrl}" />
			<addoncart:cancelButton url="${cancelUrl}" title="${cancelText}" />
			
			<cart:cartItems cartData="${cartData}"/>

				<div class="clearfix">
					<div class="span-16">
						<cart:cartPromotions cartData="${cartData}"/>
							<cart:cartPotentialPromotions cartData="${cartData}"/>
					
					</div>
					<div class="span-8 last">
						<cart:cartTotals cartData="${cartData}" showTaxEstimate="true"/>
					</div>
				</div>
		
			<a class="button" href="${continueShoppingUrl}"><spring:theme text="Continue Shopping" code="cart.page.continue"/></a>
			<addoncart:checkoutButton url="${checkoutUrl}" />
			<addoncart:cancelButton url="${cancelUrl}" title="${cancelText}" />
		
	</c:if>
	
	<c:if test="${empty cartData.entries}">
		<div class="span-24">
			<div class="span-24 wide-content-slot cms_disp-img_slot">
				<cms:pageSlot position="MiddleContent" var="feature" element="div">
					<cms:component component="${feature}"/>
				</cms:pageSlot>

				<cms:pageSlot position="BottomContent" var="feature" element="div">
					<cms:component component="${feature}"/>
				</cms:pageSlot>
			</div>
		</div>
	</c:if>

	<c:if test="${not empty cartData.entries}" >
		<cms:pageSlot position="Suggestions" var="feature" element="div" class="span-24">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</c:if>

	<cms:pageSlot position="BottomContent" var="feature" element="div" class="span-24">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

</template:page>
