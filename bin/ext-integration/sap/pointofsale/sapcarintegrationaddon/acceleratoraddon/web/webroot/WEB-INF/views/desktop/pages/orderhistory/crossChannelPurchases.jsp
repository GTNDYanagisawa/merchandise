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
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/addons/sapcarintegrationaddon/desktop/action" %>
<div class="headline">
	<spring:theme code="text.account.orderHistory" text="Order History"/>
</div>
<c:if test="${not empty searchPageData.results}">
	<div class="description">
		<spring:theme code="text.account.orderHistory.viewOrders" text="View your Orders"/>
	</div>

	<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" 
	searchPageData="${searchPageData}" searchUrl="/my-account/purchases?sort=${searchPageData.pagination.sort}" 
	msgKey="text.account.orderHistory.page" numberPagesShown="${numberPagesShown}"/>
	<div class="orderList">
		<table class="orderListTable">
			<thead>
			<tr>
				<th id="header1">
					<spring:theme code="text.account.orderHistory.orderNumber" text="Order Number"/>
				</th>
				<th id="header2">
					<spring:theme code="text.account.orderHistory.datePlaced" text="Date Placed"/>
				</th>
				<th id="header3">
					<spring:theme code="text.account.carorderhistory.orderChannel" text="Order Channel"/>
				</th>
				<th id="header4">
					<spring:theme code="text.account.orderHistory.orderStatus" text="Order Status"/>
				</th>
				<th id="header5">
					<spring:theme code="text.account.orderHistory.total" text="Total"/>
				</th>
				<th id="header6">
					<spring:theme code="text.account.orderHistory.actions" text="Actions"/>
				</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${searchPageData.results}" var="order">
				<tr>
					<td headers="header1">
						<ycommerce:testId code="carHistory_posTrnsNumber_link">
							 ${order.purchaseOrderNumber}	
						</ycommerce:testId>
					</td>
					<td headers="header2">						
						<ycommerce:testId code="carHistory_posTrnsDate_label">
							<p>
								<fmt:formatDate value="${order.transactionDate}" dateStyle="long" timeStyle="short" type="both"/>
							</p>
						</ycommerce:testId>
					</td>
					<td headers="header3">
					   <ycommerce:testId code="carHistory_ordChannel_label">
						     ${order.orderChannelName}	
						</ycommerce:testId>						
					</td>
					<td headers="header4">
						<ycommerce:testId code="carHistory_posTrnsTotal_links">
						
					        ${order.overallOrderProcessStatusDesc}				
						</ycommerce:testId>
					</td>
					<td headers="header5">
						<ycommerce:testId code="carHistory_posTrnsTotal_links">
						
					        ${order.totalPriceWithTax.formattedValue}				
						</ycommerce:testId>
					</td>
					<td headers="header6">
													
						<c:if test="${order.orderChannelTypeEnum == 'SD'}" >
							<c:set var="orderCode" scope="request" value="${order.purchaseOrderNumber}" />						
							<action:caractions element="div" parentComponent="${component}" carAction="ViewSDTransactionAction"/>
						</c:if>
						
						<c:if test="${order.orderChannelTypeEnum == 'WEB'}" >
							<c:set var="orderCode" scope="request" value="${order.purchaseOrderNumber}" />
							<action:caractions element="div" parentComponent="${component}" carAction="ViewWEBTransactionAction"/>						
						</c:if>
						
						<c:if test="${order.orderChannelTypeEnum == 'POS'}" >										
							 <c:set var="orderCode" scope="request" value="${order.purchaseOrderNumber}?storeId=${order.store.storeId}&businessDayDate=${order.businessDayDate}&transactionIndex=${order.transactionIndex}" /> 
							<action:caractions element="div" parentComponent="${component}" carAction="ViewMCPOSTransactionAction"/>					
						</c:if>	
										    					
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" 
	                searchPageData="${searchPageData}" searchUrl="/my-account/purchases?sort=${searchPageData.pagination.sort}" 
	                msgKey="text.account.orderHistory.page" numberPagesShown="${numberPagesShown}"/>

</c:if>
<c:if test="${empty searchPageData.results}">
	<p>
		<spring:theme code="text.account.orderHistory.noOrders" text="You have no orders"/>
	</p>
</c:if>



