<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="salesdocument" tagdir="/WEB-INF/tags/addons/sapcarintegrationaddon/desktop/salesdocument" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<div class="accountOrderDetailOrderTotals clearfix">
   
   <div class="span-7">
		<spring:theme code="text.account.order.orderNumber" arguments="${orderData.purchaseOrderNumber}"/>
		<br/>
		<spring:theme code="text.account.order.orderPlaced" arguments="${orderData.transactionDate}"/>
		<br/>
		<spring:theme code="text.account.order.orderStatus" arguments="${orderData.overallOrderProcessStatusDesc}"/>
		<br/>	
	</div>
	
	<div class="span-7">&nbsp;</div>

	<div class="span-6 last order-totals">
		<salesdocument:header order="${orderData}"/>
	</div>
	
</div>

