/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v1
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.AvoidCollectingOutputFromTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest

import org.junit.Test
import org.junit.experimental.categories.Category

import groovy.json.JsonSlurper

@Category(CollectOutputFromTest.class)
@ManualTest
class GuestCheckoutTests extends BaseWSTest {
	static final STORE_NAME = "WS-Shinbashi"

	def getGuestUid() {
		def randomUID = System.currentTimeMillis()
		def guestUid = "${randomUID}@test.com"
		return guestUid;
	}

	def guestPlaceOrder(guestUid, access_token, cookieNoPath = null) {
		def con, response, postBody
		def customerTests = new CustomerTests()

		postBody = "email=${guestUid}";
		con = testUtil.getSecureConnection('/customers/current/guestlogin', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)

		def aid = customerTests.createAddressJSON(access_token, null, cookieNoPath);

		// set the delivery address
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryAddress != null

		// set a delivery mode
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'standard-gross', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryCost != null

		// create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.paymentInfo != null

		postBody = 'securityCode=123'
		con = testUtil.getSecureConnection('/cart/authorize', 'POST', 'JSON', HttpURLConnection.HTTP_ACCEPTED, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);

		con = testUtil.getSecureConnection('/cart/placeorder', 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == true

		return response
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testGuestPlaceOrder() {
		def cartTests = new CartTests();
		def response, cookieNoPath
		def access_token = testUtil.getClientCredentialsToken()
		def guestUid = getGuestUid();

		cookieNoPath = cartTests.addToCart(1934795, 1, null, access_token)

		response = guestPlaceOrder(guestUid, access_token, cookieNoPath)
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testBOPISWithGuestPlaceOrder() {
		def cartTests = new CartTests();
		def response, cookieNoPath, postBody, con
		def access_token = testUtil.getClientCredentialsToken()
		def guestUid = getGuestUid();

		cookieNoPath = cartTests.addToCart(2006139, 1, null, access_token, STORE_NAME)

		postBody = "email=${guestUid}";
		con = testUtil.getSecureConnection('/customers/current/guestlogin', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)

		// set a delivery mode
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'pickup', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);

		// create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.paymentInfo != null

		postBody = 'securityCode=123'
		con = testUtil.getSecureConnection('/cart/authorize', 'POST', 'JSON', HttpURLConnection.HTTP_ACCEPTED, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);

		con = testUtil.getSecureConnection('/cart/placeorder', 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == true
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testCreateFullAccountAfterGuestPlaceOrder() {
		def final PASSWORD = '1234'
		def cartTests = new CartTests();
		def con, response, cookieNoPath, postBody
		def access_token = testUtil.getClientCredentialsToken()
		def guestUid = getGuestUid();

		cookieNoPath = cartTests.addToCart(1934795, 1, null, access_token)

		response = guestPlaceOrder(guestUid, access_token, cookieNoPath)

		postBody="password=${PASSWORD}"
		con = testUtil.getSecureConnection('/customers/current/convert', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)

		access_token = testUtil.getAccessToken(guestUid, PASSWORD)
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testGetGuestOrderInfo() {
		def cartTests = new CartTests();
		def con, response, cookieNoPath, postBody
		def access_token = testUtil.getClientCredentialsToken()
		def guestUid = getGuestUid();

		cookieNoPath = cartTests.addToCart(1934795, 1, null, access_token)

		response = guestPlaceOrder(guestUid, access_token, cookieNoPath)
		String orderGuid = response.guid;

		def access_token2 = testUtil.getClientCredentialsToken()
		con = testUtil.getSecureConnection('/orders/byGuid/' + orderGuid, 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token2)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.guid == orderGuid;
		assert response.entries.size() == 1;
		assert response.entries.product[0].code == '1934795'
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testIfCreateFullAccountWillFailIfUserExists() {
		def final PASSWORD = '1234'
		def customerTests = new CustomerTests();
		def cartTests = new CartTests();
		def con, response, cookieNoPath, postBody
		def access_token = testUtil.getClientCredentialsToken()
		def uid = customerTests.registerUser()

		cookieNoPath = cartTests.addToCart(1934795, 1, null, access_token)

		response = guestPlaceOrder(uid, access_token, cookieNoPath)

		postBody="password=${PASSWORD}"
		con = testUtil.getSecureConnection('/customers/current/convert', 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, cookieNoPath, access_token)
		def error = con.errorStream.text
		println error
		response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'DuplicateUidError'
		assert response.errors[0].message == uid;
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testGetOrderByGuidShouldWorkEvenIfUserHadCreatedFullAccountBefore() {
		def final PASSWORD = '1234'
		def cartTests = new CartTests();
		def con, response, cookieNoPath, postBody
		def access_token = testUtil.getClientCredentialsToken()
		def guestUid = getGuestUid();

		cookieNoPath = cartTests.addToCart(1934795, 1, null, access_token)

		response = guestPlaceOrder(guestUid, access_token, cookieNoPath)
		String orderGuid = response.guid;

		postBody="password=${PASSWORD}"
		con = testUtil.getSecureConnection('/customers/current/convert', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)

		def access_token2 = testUtil.getClientCredentialsToken()
		con = testUtil.getSecureConnection('/orders/byGuid/' + orderGuid, 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token2)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.guid == orderGuid;
		assert response.entries.size() == 1;
		assert response.entries.product[0].code == '1934795'
	}
}
