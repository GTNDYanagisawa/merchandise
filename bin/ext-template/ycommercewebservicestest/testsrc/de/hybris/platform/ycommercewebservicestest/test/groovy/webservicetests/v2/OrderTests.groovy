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

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest

import org.junit.Test

import groovy.json.JsonSlurper

@org.junit.experimental.categories.Category(CollectOutputFromTest.class)
@ManualTest
class OrderTests extends BaseWSTest {
	static final String USERNAME = "orderhistoryuser@test.com"
	static final String PASSWORD = "test"
	static final String ORDER_CODE = "testOrder1"

	//when cart controller will be finished
	//covered
	@Test
	void testGetOrderByGUID() {
		//add entry to cart should be changed...
		def cartTests = new CartTests();
		def con, response, postBody

		def userTests = new UsersTest()
		def uid = userTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def String productId = "1934793"

		def cartCode = cartTests.createRegularUsersCart(uid, access_token)
		// put something in a new cart'

		cartTests.addProductToRegularUsersCart(uid, cartCode, productId, 1, "WS-Shinbashi", access_token)

		// set a delivery mode as pickup
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/deliverymode?deliveryModeId=pickup", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		// create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/paymentdetails", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)

		postBody = "cartId=${cartCode}"
		con = testUtil.getSecureConnection("/users/${uid}/orders?securityCode=123&fields=FULL", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == false

		def code = response.code
		def guid = response.guid

		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken();
		con = testUtil.getSecureConnection("/orders/" + guid + "?fields=FULL", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, trusted_client_access_token)
		response = testUtil.verifiedJSONSlurper(con, true, false);
		assert response.guid == guid
		assert response.code == code
	}

	//covered
	@Test
	void testGetOrderByCode() {
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken();

		def con = testUtil.getSecureConnection("/orders/" + ORDER_CODE, 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, trusted_client_access_token)
		def response = testUtil.verifiedJSONSlurper(con, true, false)
		assert response.store == "wsTest"
		assert response.net == false
		assert !response.appliedVouchers
		assert response.totalDiscounts != null
		assert response.productDiscounts != null
		assert response.created != null
		assert response.subTotal != null
		assert response.orderDiscounts != null
		assert response.entries
		assert response.entries.size() == 2
		assert !response.appliedProductPromotions
		assert response.totalPrice != null
		assert response.site == "wsTest"
		assert response.status == "CREATED"
		assert response.statusDisplay.toLowerCase() == "created"
		assert response.deliveryMode != null
		assert response.code == ORDER_CODE
		assert response.totalItems == 2
		assert response.totalPriceWithTax != null
		assert response.guestCustomer == false
		//assert response.deliveryItemsQuantity == 7
		assert response.totalTax != null
		assert response.user.uid == USERNAME
		assert response.user.name == "orders test user"
		assert response.deliveryCost != null
	}

	//covered
	@Test
	void testGetOrderByWrongGUIDWrongCode() {
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken();
		def con = testUtil.getSecureConnection("/orders/WRONG_CODE", 'GET', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, null, trusted_client_access_token)
		def error = con.errorStream.text;
		def response = new JsonSlurper().parseText(error);
		assert response.errors[0].type == "UnknownIdentifierError"
	}

	//covered
	@Test
	void testGetOrderByWrongGUID() {
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken();
		def con = testUtil.getSecureConnection("/orders/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 'GET', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, null, trusted_client_access_token)
		def error = con.errorStream.text;
		def response = new JsonSlurper().parseText(error);
		assert response.errors[0].type == "UnknownIdentifierError"
	}
}
