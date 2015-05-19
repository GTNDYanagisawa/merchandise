<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<table id="document_payment_history">
<caption><h2 id="document_payment_history_title" /></caption>
	<tbody>

		<tr>

			<th><spring:theme
					code="text.company.accountsummary.documentPaymentInfo.date.label"
					text="Date" /></th>

			<th><spring:theme
					code="text.company.accountsummary.documentPaymentInfo.paymentmethod.label"
					text="Payment Method" /></th>

			<th><spring:theme
					code="text.company.accountsummary.documentPaymentInfo.amount.label"
					text="Amount" /></th>

			<th><spring:theme
					code="text.company.accountsummary.documentPaymentInfo.referencenumber.label"
					text="Reference Number" /></th>

		</tr>

		<c:forEach items="${payments}" var="it">
			<tr>

				<td><fmt:formatDate value="${it.date }" dateStyle="long"
						timeStyle="short" type="both" /></td>

				<td>${it.paymentMethod } </td>
				<td>${it.formattedAmount }</td>

				<td> ${it.referenceNumber } </td>
			</tr>
		</c:forEach>
	</tbody>
</table>

