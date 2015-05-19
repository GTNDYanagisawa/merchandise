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
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action" %>

<div class="headline">
	<spring:theme code="text.account.posTransactionHistory" text="POS Transaction History"/>
</div>
<c:if test="${not empty searchPageData.results}">
	<div class="description">
		<spring:theme code="text.account.posTransactionHistory.viewPurchases" text="View your purchases"/>
	</div>

	<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" 
	searchPageData="${searchPageData}" searchUrl="/my-account/postransactions?sort=${searchPageData.pagination.sort}" 
	msgKey="text.account.posTransactionHistory.page" numberPagesShown="${numberPagesShown}"/>
	<div class="orderList">
		<table class="orderListTable">
			<thead>
			<tr>
				<th id="header1">
					<spring:theme code="text.account.posTransactionHistory.purchaseId" text="Purchase ID"/>
				</th>
				<th id="header2">
					<spring:theme code="text.account.posTransactionHistory.purchaseDate" text="Purchase Date"/>
				</th>
				<th id="header3">
					<spring:theme code="text.account.posTransactionHistory.storeName" text="Store Name"/>
				</th>
				<th id="header4">
					<spring:theme code="text.account.orderHistory.total" text="Total"/>
				</th>
				<th id="header5">
					<spring:theme code="text.account.orderHistory.actions" text="Actions"/>
				</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${searchPageData.results}" var="order">
				<tr>
					<td headers="header1">
						<ycommerce:testId code="posTrnsHistory_posTrnsNumber_link">
							${order.purchaseOrderNumber}
						</ycommerce:testId>
					</td>
					<td headers="header2">						
						<ycommerce:testId code="posTrnsHistory_posTrnsDate_label">
							<p>
								<fmt:formatDate value="${order.businessDayDate}" dateStyle="long" timeStyle="short" type="both"/>
							</p>
						</ycommerce:testId>
					</td>
					<td headers="header3">
					   <ycommerce:testId code="posTrnsHistory_posTrnsStoreName_label">
						     ${order.store.storeName}	
						</ycommerce:testId>						
					</td>
					<td headers="header4">
						<ycommerce:testId code="posTrnsHistory_posTrnsTotal_links">
						
					        ${order.totalPriceWithTax.formattedValue}				
						</ycommerce:testId>
					</td>
					<td headers="header5">
						<c:set var="orderCode" value="${order.purchaseOrderNumber}?storeId=${order.store.storeId}&businessDayDate=${order.businessDayDate}&transactionIndex=${order.transactionIndex}" scope="request"/>
						<action:actions element="div" parentComponent="${component}"/>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" 
	                searchPageData="${searchPageData}" searchUrl="/my-account/postransactions?sort=${searchPageData.pagination.sort}" 
	                msgKey="text.account.posTransactionHistory.page" numberPagesShown="${numberPagesShown}"/>

</c:if>
<c:if test="${empty searchPageData.results}">
	<p>
		<spring:theme code="text.account.posTransactionHistory.noPurchases" text="You have no purchases"/>
	</p>
</c:if>



