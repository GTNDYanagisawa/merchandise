<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<template:page pageTitle="${pageTitle}">

	<div class="global-alerts">
		<div class="alert alert-info" role="alert">
			<spring:theme code="text.page.message.underconstruction" text="Information: Page Under Construction - Not Completely Functional"/>
		</div>
	</div>


		<div class="checkout-login">
			<div class="row">
				<div class="col-sm-6">
					<div class="headline">Returning Customer</div>
					<form role="form">

						<div class="form-group">
							<label for="beispielFeldEmail1">Email-Adress</label> <input
								type="email" class="form-control" id="beispielFeldEmail1"
								placeholder="Email-Adress">
						</div>
						<div class="form-group">
							<label for="beispielFeldPasswort1">Password</label> <input
								type="password" class="form-control" id="beispielFeldPasswort1"
								placeholder="Password">
						</div>
						<a href="checkout.registered.step1.html"
							class="btn btn-primary btn-block">Login and Checkout</a> <a
							href="checkout.registered.step4.html"
							class="btn btn-default btn-block">Express Checkout</a>
					</form>

				</div>
				<div class="col-sm-6">
					<div class="headline">New Customer?</div>

					<div class="form-group">
						<label for="beispielFeldEmail1">Email-Adress</label> <input
							type="email" class="form-control" id="beispielFeldEmail1"
							placeholder="Email-Adress">
					</div>

					<div class="form-group">
						<label for="beispielFeldEmail1">Confirm Email Address</label> <input
							type="email" class="form-control" id="beispielFeldEmail1"
							placeholder="Email-Adress">
					</div>

					<button type="button" class="btn btn-default btn-block">Checkout
						as a Guest</button>

				</div>

			</div>

		</div>


</template:page>