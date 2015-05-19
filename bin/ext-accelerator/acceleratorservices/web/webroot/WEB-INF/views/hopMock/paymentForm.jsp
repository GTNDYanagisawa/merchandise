<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="/_ui/hop-mock/css/common.css" var="stylesheetPath"/>
<c:url value="/_ui/hop-mock/images/favicon.ico" var="favIconPath"/>
<c:url value="/_ui/hop-mock/images/logo.png" var="imgLogoPath"/>

<c:url value="/_ui/hop-mock/images/visa.gif" var="imgCardVisaPath"/>
<c:url value="/_ui/hop-mock/images/mastercard.gif" var="imgCardMastercardPath"/>
<c:url value="/_ui/hop-mock/images/maestro.gif" var="imgCardMaestroPath"/>
<c:url value="/_ui/hop-mock/images/americanexpress.gif" var="imgCardAmericanExpressPath"/>
<c:url value="/_ui/hop-mock/images/dinersclub.gif" var="imgCardDinersClubPath"/>

<c:url value="/hop-mock/process" var="hopResponseActionUrl"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="${currentLanguageIso}" lang="${currentLanguageIso}">
<head>
	<title><spring:message code="text.title"/></title>
	<link rel="shortcut icon" href="${favIconPath}" type="image/x-icon"/>
	<link rel="stylesheet" type="text/css" media="screen" href="${stylesheetPath}"/>
	<meta name="HandheldFriendly" content="True">
	<meta name="MobileOptimized" content="320">
	<meta name="viewport" content="width=device-width, target-densitydpi=160dpi, maximum-scale=1">
</head>
<body>
	<div id="wrapper">
		<div id="page">
			<div id="header">
				<div class="logo">
					<img alt="logo" src="${imgLogoPath}"/>
				</div>
			</div>
			<div style="clear: both;"></div>
			<div id="item_container_holder">
				<div class="item_container">
					<div id="welcome">
						<h3>
							<spring:message code="text.header.welcome"/>
							<c:out value=" ${paymentDetailsForm.billingAddress.firstName} ${paymentDetailsForm.billingAddress.lastName}"/>
						</h3>
						<h4>
							<spring:message code="text.header.welcome.subscription.message"/>
						</h4>
					</div>
				</div>
				<div class="item_container">
					<form:form id="paymentDetailsForm" name="paymentDetailsForm" action="${hopResponseActionUrl}" method="post" commandName="paymentDetailsForm">
						<form:hidden path="originalParameters" />
						<spring:hasBindErrors name="paymentDetailsForm">
							<div id="form_errors">
								<h3>Errors</h3>
								<p>
									<spring:message code="text.error.header.message"/>
								</p>
								<ul>
									<c:forEach var="error" items="${errors.allErrors}">
										<li><spring:message code="${empty error.defaultMessage ? error.code : error.defaultMessage}" /></li>
									</c:forEach>
								</ul>
							</div>
						</spring:hasBindErrors>
					
						<!-- Payment Details -->
						<div class="section_break"><spring:message code="text.section.break.payment.details"/></div>
						<p class="bold"><spring:message code="text.required.fields"/></p>
						
						<!-- Credit Cards -->
						<div class="section_break"><spring:message code="text.section.break.creditcard"/></div>
						<div>
							<img alt="Visa" src="${imgCardVisaPath}"/>
							<img alt="MasterCard" src="${imgCardMastercardPath}"/>
							<img alt="Maestro (UK Domestic)" src="${imgCardMaestroPath}"/>
							<img alt="American Express" src="${imgCardAmericanExpressPath}"/>
							<img alt="Diners Club" src="${imgCardDinersClubPath}"/>
						</div>
						<div class="form">
							<dl>
								<dt><label for="payment.cardType" class="required"><spring:message code="payment.cardType"/></label></dt>
								<dd>
									<form:select id="payment.cardType" path="cardTypeCode" tabindex="1">
										<option value="" disabled="disabled"><spring:message code="payment.cardType.pleaseSelect"/></option>
										<form:options items="${cardTypes}"/>
									</form:select>
								</dd>
								
								<dt><label for="payment.cardNumber" class="required"><spring:message code="payment.cardNumber"/></label></dt>
								<dd><form:input id="payment.cardNumber" path="cardNumber" tabindex="2"/></dd>
								
								<dt><label for="payment.cvn" class="required"><spring:message code="payment.cvn"/></label></dt>
								<dd><form:input id="payment.cvn" path="verificationNumber" tabindex="3"/></dd>
								
								<dt><label for="payment.issueNumber" class="required"><spring:message code="payment.issueNumber"/></label></dt>
								<dd><form:input id="payment.issueNumber" path="issueNumber" tabindex="4"/></dd>
								
								<dt><label for="payment.startDate.month" class="required"><spring:message code="payment.startDate"/></label></dt>
								<dd>
									<div class="grid">
										<div class="grid_block_a">
											<form:select id="payment.startDate.month" path="startMonth" tabindex="5">
												<option value="" label=""/>
												<form:options items="${months}" itemValue="code" itemLabel="name"/>
											</form:select>
										</div>
										<div class="grid_block_b">
											<form:select id="payment.startDate.year" path="startYear" tabindex="6">
												<option value="" label=""/>
												<form:options items="${startYears}" itemValue="code" itemLabel="name"/>
											</form:select>
										</div>
									</div>
								</dd>
								
								<dt><label for="payment.endDate.month" class="required"><spring:message code="payment.endDate"/></label></dt>
								<dd>
									<div class="grid">
										<div class="grid_block_a">
											<form:select id="payment.endDate.month" path="expiryMonth" tabindex="7">
												<option value="" label=""/>
												<form:options items="${months}" itemValue="code" itemLabel="name"/>
											</form:select>
										</div>
										<div class="grid_block_b">
											<form:select id="payment.endDate.year" path="expiryYear" tabindex="8">
												<option value="" label=""/>
												<form:options items="${expiryYears}" itemValue="code" itemLabel="name"/>
											</form:select>
										</div>
									</div>
								</dd>
							</dl>
						</div>

						<!-- Billing Information -->
						<div class="section_break"><spring:message code="text.section.break.billing.info"/></div>
						<div class="form">
							<dl>
								<dt>
									<label for="address.firstName" class="required"><spring:message code="address.firstName"/></label>/
									<label for="address.lastName" class="required"><spring:message code="address.lastName"/></label>
								</dt>
								<dd>
									<div class="gridBreak">
										<div class="grid_block_a">
											<form:input id="address.firstName" path="billingAddress.firstName" tabindex="9"/>
										</div>
										<div class="grid_block_b">
											<form:input id="address.lastName" path="billingAddress.lastName" tabindex="10"/>
										</div>
									</div>
								</dd>
								
								<dt><label for="address.company"><spring:message code="address.company"/></label></dt>
								<dd><form:input id="address.company" path="billingAddress.company" tabindex="11"/></dd>
								
								<dt><label for="address.line1" class="required"><spring:message code="address.line1"/></label></dt>
								<dd><form:input id="address.line1" path="billingAddress.line1" tabindex="12"/></dd>
								
								<dt><label for="address.line2"><spring:message code="address.line2"/></label></dt>
								<dd><form:input id="address.line2" path="billingAddress.line2" tabindex="13"/></dd>
								
								<dt>
									<label for="address.city" class="required"><spring:message code="address.city"/></label>/
									<label for="address.state"><spring:message code="address.state"/></label>/
									<label for="address.postCode" class="required"><spring:message code="address.postCode"/></label>
								</dt>
								<dd>
									<form:input id="address.city" path="billingAddress.townCity" cssClass="cityInput" tabindex="14"/>
									<form:input id="address.state" path="billingAddress.state" cssClass="stateInput" tabindex="15"/>
									<form:input id="address.postCode" path="billingAddress.postcode" cssClass="postCodeInput" tabindex="16"/>
								</dd>

								<dt><label for="address.country" class="required"><spring:message code="address.country"/></label></dt>
								<dd>
									<form:select id="address.country" path="billingAddress.countryIso" tabindex="17">
										<option value="" disabled="disabled"><spring:message code="address.country.pleaseSelect"/></option>
										<form:options items="${billingCountries}" itemValue="code" itemLabel="name"/>
									</form:select>
								</dd>
								
								<dt><label for="address.phoneNumber"><spring:message code="address.phoneNumber"/></label></dt>
								<dd><form:input id="address.phoneNumber" path="billingAddress.phoneNumber" tabindex="18"/></dd>
								
								<dt><label for="address.emailAddress"><spring:message code="address.emailAddress"/></label></dt>
								<dd><form:input id="address.emailAddress" path="billingAddress.emailAddress" tabindex="19"/></dd>
							</dl>
						</div>
						
						<div class="section_break"><spring:message code="text.section.break.actions"/></div>
						
						<div id="content" class="optionsBlock">
							<p class="bold"><spring:message code="text.save.message"/></p>
							<dl>
								<dd>
									<div class="gridBreak">
										<div class="grid_block_a">
											<form:select id="mock.response.error" path="mockReasonCode" tabindex="20">
												<option value=""><spring:message code="mock.response.error.pleaseSelect"/></option>
												<form:options items="${mockErrorResponses}" itemValue="code" itemLabel="name"/>
											</form:select>
										</div>
										<div class="grid_block_b">
										</div>
									</div>
								</dd>
								<dd>
									<div class="gridBreak">
										<div class="grid_block_a">
											<input id="button.fail" name="button.fail" class="submitButtonText" type="submit" tabindex="21" title="<spring:message code="button.fail"/>" value="<spring:message code="button.fail"/>"/>
										</div>
										<div class="grid_block_b">
											<input id="button.succeed" name="button.succeed" class="submitButtonText" type="submit" tabindex="22" title="<spring:message code="button.succeed"/>" value="<spring:message code="button.succeed"/>"/>
										</div>
									</div>
								</dd>
								<dd>
									<div class="showDebugPage">
										<form:checkbox id="show_debug_page" name="show_debug_page" path="showDebugPage" />
										<label for="show_debug_page"><spring:message code="checkbox.show.debug.page"/></label>
									</div>
								</dd>
							</dl>
						</div>

					</form:form>
				</div>
			</div>
			<div style="clear: both;"></div>
			<div id="footer">
				<!--- Footer INFO GOES HERE: <img alt="logo" src="${imgLogoPath}"/> -->
			</div>
		</div>
	</div>
</body>
</html>
