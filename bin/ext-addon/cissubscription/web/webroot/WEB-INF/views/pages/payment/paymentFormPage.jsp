<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
	<head>
		<title>Payment details</title>
	</head>
	<body>
		<!-- Form posts to mock event handler which should not be used in productive systems. -->
		<c:url value="/index.zul?events=sop-mock&sop-mock-error=false" context="/cscockpit" var="sopMockUrl"/>
		<form:form method="post" commandName="paymentForm" action="${sopMockUrl}">
			<form:hidden path="sessionToken" value="${sessionToken}"/>
			<div>
				<table>
					<tr>
						<td><form:label path="cardTypeCode"><spring:message code="creditCard.type.label"/></form:label></td>
						<td><form:select path="cardTypeCode" items="${cardTypes}"/></td>
					</tr>
					<tr>
						<td><form:label path="nameOnCard"><spring:message code="creditCard.accountHolderName.label"/></form:label></td>
						<td><form:input path="nameOnCard"/></td> 
					</tr>
					<tr>
						<td><form:label path="cardNumber"><spring:message code="creditCard.number.label"/></form:label></td>
						<td><form:input path="cardNumber"/></td>
					</tr>
					<tr>
						<td><form:label path="startMonth"><spring:message code="creditCard.startMonth.label"/></form:label></td>
						<td><form:select path="startMonth" items="${months}"/></td>
					</tr>
					<tr>
						<td><form:label path="startYear"><spring:message code="creditCard.startYear.label"/></form:label></td>
						<td><form:select path="startYear" items="${startYears}"/></td>
					</tr>
					<tr>
						<td><form:label path="issueNumber"><spring:message code="creditCard.issueNumber.label"/></form:label></td>
						<td><form:input path="issueNumber"/></td>
					</tr>
					<tr>
						<td><form:label path="expiryMonth"><spring:message code="creditCard.expiryMonth.label"/></form:label></td>
						<td><form:select path="expiryMonth" items="${months}"/></td>
					</tr>
					<tr>
						<td><form:label path="expiryYear"><spring:message code="creditCard.expiryYear.label"/></form:label></td>
						<td><form:select path="expiryYear" items="${expiryYears}"/></td>
					</tr>
					<tr>
						<td><form:label path="cvn"><spring:message code="creditCard.cv2.label"/></form:label></td>
						<td><form:input path="cvn"/></td>
					</tr>
					<tr>
						<td><form:label path="billingAddress.titleCode"><spring:message code="billingAddress.title.label"/></form:label></td>
						<td><form:select path="billingAddress.titleCode" items="${titles}"/></td>
					</tr>
					<tr>
						<td><form:label path="billingAddress.firstName"><spring:message code="billingAddress.firstName.label"/></form:label></td>
						<td><form:input path="billingAddress.firstName"/></td> 
					</tr>
					<tr>
						<td><form:label path="billingAddress.lastName"><spring:message code="billingAddress.lastName.label"/></form:label></td>
						<td><form:input path="billingAddress.lastName"/></td> 
					</tr>
					<tr>
						<td><form:label path="billingAddress.line1"><spring:message code="billingAddress.address1.label"/></form:label></td>
						<td><form:input path="billingAddress.line1"/></td>
					</tr>
					<tr>
						<td><form:label path="billingAddress.line2"><spring:message code="billingAddress.address2.label"/></form:label></td>
						<td><form:input path="billingAddress.line2"/></td>
					</tr>
					<tr>
						<td><form:label path="billingAddress.townCity"><spring:message code="billingAddress.city.label"/></form:label></td>
						<td><form:input path="billingAddress.townCity"/></td>
					</tr>
					<tr>
						<td><form:label path="billingAddress.postcode"><spring:message code="billingAddress.postalCode.label"/></form:label></td>
						<td><form:input path="billingAddress.postcode"/></td>
					</tr>
					<tr>
						<td><form:label path="billingAddress.countryIso"><spring:message code="billingAddress.country.label"/></form:label></td>
						<td><form:select path="billingAddress.countryIso" items="${countries}"/></td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="submit" value="Add Payment Method"/>
						</td>
					</tr>
				</table>
			</div>
		</form:form>
	</body>
</html>