<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<div class="headline">
	<spring:theme code="text.account.order.orderTotals" />
</div>

<div class="orderList">

	<table class="orderListTable">
		
		<thead>
			<tr>
				<th id="header2"><span class="hidden"><spring:theme code="text.productDetails" text="Product Details" /></span></th>
				<th id="header4"><spring:theme code="text.quantity" text="Quantity" /></th>
				<th id="header5"><spring:theme code="text.itemPrice" text="Item Price" /></th>
				<th id="header6"><spring:theme code="text.total" text="Total" /></th>
			</tr>
		</thead>
		
		<c:forEach items="${orderData.orderEntries}" var="orderEntry">
			<tr class="item">
				<td headers="header2" class="product_details">
				    <ycommerce:testId code="orderDetails_productName_link">${orderEntry.product.name}</ycommerce:testId>
				</td>
				<td headers="header4" class="quantity">
				   <ycommerce:testId code="orderDetails_productQuantity_label">${orderEntry.quantity}</ycommerce:testId>
				</td>
				<td headers="header5">
				   <ycommerce:testId code="orderDetails_productItemPrice_label">						
						<c:choose>
							<c:when test="${orderData.net}">
							   <format:price priceData="${orderEntry.basePrice}" displayFreeForZero="false" />
							</c:when>
							<c:otherwise>
							    <format:price priceData="${orderEntry.basePriceWithTax}" displayFreeForZero="true" />
							</c:otherwise>
						</c:choose>
				   </ycommerce:testId>
				</td>
				<td headers="header6" class="total">
				   <ycommerce:testId code="orderDetails_productTotalPrice_label">
					   <c:choose>
							<c:when test="${orderData.net}">
							   <format:price priceData="${orderEntry.totalPrice}" displayFreeForZero="true" />
							</c:when>
							<c:otherwise>
							    <format:price priceData="${orderEntry.totalPriceWithTax}" displayFreeForZero="true" />
							</c:otherwise>
						</c:choose>
				   </ycommerce:testId>
				</td>
			</tr>
		</c:forEach>	
	</table>
</div>