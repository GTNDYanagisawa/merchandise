<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<%@ attribute name="containerCSS" required="false" type="java.lang.String" %>

<table id="orderTotals" class="${containerCSS}">
	
	<thead>
		<tr>
			<td><spring:theme code="text.account.salesDocument.orderTotals" text="Order Totals"/></td>
			<td></td>
		</tr>
	</thead>

	<tbody>
		
		<tr>
			<td><spring:theme code="text.account.order.subtotal" text="Subtotal:"/></td>
			<td><format:price priceData="${order.subTotal}"/></td>
		</tr>
		
		<c:if test="${order.net}" >
			<tr>
				<td><spring:theme code="text.account.order.netTax " text="Tax:"/></td>
				<td><format:price priceData="${order.totalTax}"/></td>
			</tr>
		</c:if>

	</tbody>
	
	<tfoot>
		<tr>
			<td><spring:theme code="text.account.order.total" text="Total:"/></td>
			<td><format:price priceData="${order.totalPriceWithTax}"/></td>			
		</tr>
	</tfoot>
	
</table>
