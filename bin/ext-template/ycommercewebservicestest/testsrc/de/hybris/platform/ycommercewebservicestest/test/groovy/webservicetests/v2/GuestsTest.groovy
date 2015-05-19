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
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.AvoidCollectingOutputFromTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest

import org.junit.Test
import org.junit.experimental.categories.Category

import groovy.json.JsonSlurper


@Category(CollectOutputFromTest.class)
@ManualTest
class GuestsTest extends BaseWSTest {
	static final FIRST_NAME = "John"
	static final LAST_NAME = "Doe"
	static final TITLE_CODE = "dr"
	static final TITLE = "Dr."
	static final LINE_1 = "Nymphenburger Str. 86 - Maillingerstrasse"
	static final TOWN = "Muenchen"
	static final POSTAL_CODE = "80331"
	static final COUNTRY_ISOCODE = "DE"
	static final String STORE_NAME = "WS-Shinbashi"
	static final String PRODUCT_FOR_PICKUP = '2006139'
	static final String PICKUP_DELIVERY_MODE = 'pickup'
	static final String PRODUCT_ID = '1934795'
	static final String DELIVERY_MODE = 'standard-gross'

	protected CartTests cartTests = new CartTests();

	def getGuestUid() {
		def randomUID = System.currentTimeMillis()
		def guestUid = "${randomUID}@test.com"
		return guestUid;
	}

	def guestPlaceOrder(guestUid, access_token, String productId = PRODUCT_ID, String pickupStore = null, String deliveryMode = DELIVERY_MODE) {
		def con, response, postBody;
		def cartGuid = cartTests.createAnonymousUsersCart();
		cartTests.addProductToAnonymousUsersCart(cartGuid, productId, 1, pickupStore, access_token)

		postBody = "email=${guestUid}";
		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/email", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		postBody = "titleCode=dr&firstName=${FIRST_NAME}&lastName=${LAST_NAME}&line1=${LINE_1}&town=${TOWN}&postalCode=${POSTAL_CODE}&country.isocode=${COUNTRY_ISOCODE}"
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/addresses/delivery?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)
		assert response.postalCode == "80331"
		assert response.country.isocode == "DE"

		// set delivery mode
		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/deliverymode?deliveryModeId=${deliveryMode}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		// create a paymentinfo for this cart
		postBody = "accountHolderName=Joe+Doe&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=${TITLE_CODE}&billingAddress.firstName=${FIRST_NAME}&billingAddress.lastName=${LAST_NAME}&billingAddress.line1=${LINE_1}&billingAddress.postalCode=${POSTAL_CODE}&billingAddress.town=${TOWN}&billingAddress.country.isocode=${COUNTRY_ISOCODE}"
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/paymentdetails?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.id != null

		con = testUtil.getSecureConnection("/users/anonymous/orders?cartId=${cartGuid}&securityCode=123&fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == true

		return response
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testGuestLogin() {
		def cartGuid = cartTests.createAnonymousUsersCart()
		assert cartGuid;

		def access_token = testUtil.getClientCredentialsToken()

		def postBody = 'email=test@email.com'
		def con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/email", 'PUT', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testGuestPlaceOrder() {
		def access_token = testUtil.getTrustedClientCredentialsToken();
		def guestUid = getGuestUid();

		def response = guestPlaceOrder(guestUid, access_token)
		def guid = response.guid;

		def con = testUtil.getSecureConnection("/orders/${guid}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.guid == guid;
		assert response.entries
		assert response.entries.size() == 1;
		assert response.entries.product[0].code == PRODUCT_ID
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testBOPISWithGuestPlaceOrder() {
		def access_token = testUtil.getTrustedClientCredentialsToken();
		def guestUid = getGuestUid();

		def response = guestPlaceOrder(guestUid, access_token, PRODUCT_FOR_PICKUP, STORE_NAME, PICKUP_DELIVERY_MODE)
		def guid = response.guid;

		def con = testUtil.getSecureConnection("/orders/${guid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, true, false);
		assert response.guid == guid;
		assert response.entries
		assert response.entries.size() == 1;
		assert response.entries.product[0].code == PRODUCT_FOR_PICKUP;
		assert response.entries.deliveryPointOfService[0].name == STORE_NAME;
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testCreateFullAccountForGuest() {
		def con, response, postBody

		def guestUid = getGuestUid();
		def access_token = testUtil.getClientCredentialsToken();

		response = guestPlaceOrder(guestUid, access_token)
		def guid = response.guid;
		postBody = "guid=${guid}&password=${config.PASSWORD}"
		testUtil.getSecureConnection("/users", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)

		access_token = testUtil.getAccessToken(guestUid, config.PASSWORD)
		con = testUtil.getSecureConnection("/users/${guestUid}/orders", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		response = testUtil.verifiedJSONSlurper(con, true, false);
		assert response.orders[0].guid == guid;
	}

	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testIfCreateFullAccountWillFailIfUserExists() {
		def con, response, cookieNoPath, postBody
		def access_token = testUtil.getClientCredentialsToken()
		UsersTest usersTest = new UsersTest();
		def uid = usersTest.registerUser();

		response = guestPlaceOrder(uid, access_token)
		def guid = response.guid;

		postBody = "guid=${guid}&password=${config.PASSWORD}"
		con = testUtil.getSecureConnection("/users", 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text
		response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'DuplicateUidError'
		assert response.errors[0].message == uid;
	}
}
