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

import org.junit.BeforeClass
import org.junit.Test
import org.junit.experimental.categories.Category

@Category(CollectOutputFromTest.class)
@ManualTest
class CartMatchingFilterTests extends BaseWSTest {
	static final PASSWORD = "test"
	static uid1
	static uid2

	@BeforeClass
	static void setUpBeforeClass() {
		def usersTest = new UsersTest()
		uid1 = usersTest.registerUser();
		uid2 = usersTest.registerUser();
	}

	@Test
	void testExistingAnonymousCart() {
		CartTests cartTests = new CartTests();
		def cartGuid = cartTests.createAnonymousUsersCart();

		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
	}

	@Test
	void testNonExistingAnonymousCart() {
		def cartGuid = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
		def con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST)
		def error = con.errorStream.text
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'CartError'
		assert response.errors[0].subject == 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx'
		assert response.errors[0].subjectType == 'cart'
		assert response.errors[0].reason == 'notFound'
		assert response.errors[0].message == 'Cart not found.'
	}

	@Test
	void testAnonymousUserOtherCart() {
		def access_token1 = testUtil.getAccessToken(uid1, PASSWORD,config.TRUSTED_CLIENT_ID,config.TRUSTED_CLIENT_SECRET)
		CartTests cartTests = new CartTests();
		def cartGuid = cartTests.createRegularUsersCart(uid1, access_token1)

		def con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST)
		def error = con.errorStream.text
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'CartError'
		assert response.errors[0].subject == cartGuid
		assert response.errors[0].subjectType == 'cart'
		assert response.errors[0].reason == 'notFound'
		assert response.errors[0].message == 'Cart not found.'
	}

	@Test
	void testAnonymousUserCurrentCart() {
		def access_token1 = testUtil.getAccessToken(uid1, PASSWORD,config.TRUSTED_CLIENT_ID,config.TRUSTED_CLIENT_SECRET)
		CartTests cartTests = new CartTests();
		def cartGuid = cartTests.createRegularUsersCart(uid1, access_token1)

		def con = testUtil.getSecureConnection("/users/anonymous/carts/current", 'GET', 'XML', HttpURLConnection.HTTP_UNAUTHORIZED)
		def error = con.errorStream.text
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'UnauthorizedError'
		assert response.errors[0].message == 'Full authentication is required to access this resource'
	}

	@Test
	void testExistingCustomerCart() {
		def access_token = testUtil.getAccessToken(uid1, PASSWORD,config.TRUSTED_CLIENT_ID,config.TRUSTED_CLIENT_SECRET)
		CartTests cartTests = new CartTests();
		def cartGuid = cartTests.createRegularUsersCart(uid1, access_token)
		testUtil.getSecureConnection("/users/${uid1}/carts/${cartGuid}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
	}

	@Test
	void testNonExistingCustomerCart() {
		def cartGuid = "xxxxxxxx"
		def access_token = testUtil.getAccessToken(uid1, PASSWORD,config.TRUSTED_CLIENT_ID,config.TRUSTED_CLIENT_SECRET)
		def con = testUtil.getSecureConnection("/users/${uid1}/carts/${cartGuid}", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
		def error = con.errorStream.text
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'CartError'
		assert response.errors[0].subject == 'xxxxxxxx'
		assert response.errors[0].subjectType == 'cart'
		assert response.errors[0].reason == 'notFound'
		assert response.errors[0].message == 'Cart not found.'
	}

	@Test
	void testOtherUsersCart() {
		def access_token1 = testUtil.getAccessToken(uid1, PASSWORD,config.TRUSTED_CLIENT_ID,config.TRUSTED_CLIENT_SECRET)
		CartTests cartTests = new CartTests();
		def cartGuid = cartTests.createRegularUsersCart(uid1, access_token1)

		def access_token2 = testUtil.getAccessToken(uid2, PASSWORD,config.TRUSTED_CLIENT_ID,config.TRUSTED_CLIENT_SECRET)
		def con = testUtil.getSecureConnection("/users/${uid2}/carts/${cartGuid}", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token2)
		def error = con.errorStream.text
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'CartError'
		assert response.errors[0].subject == cartGuid
		assert response.errors[0].subjectType == 'cart'
		assert response.errors[0].reason == 'notFound'
		assert response.errors[0].message == 'Cart not found.'
	}

	@Test
	void testNonCustomerExistingAnonymousCart() {
		CartTests cartTests = new CartTests();
		def cartGuid = cartTests.createAnonymousUsersCart();
		def customermanager_access_token = testUtil.getAccessToken("customermanager", "1234")

		testUtil.getSecureConnection("/users/customermanager/carts/${cartGuid}", 'GET', 'XML', HttpURLConnection.HTTP_FORBIDDEN, null, null, customermanager_access_token)
	}

	@Test
	void testNonCustomerUserExistingOtherCart() {
		def access_token1 = testUtil.getAccessToken(uid1, PASSWORD,config.TRUSTED_CLIENT_ID,config.TRUSTED_CLIENT_SECRET)
		CartTests cartTests = new CartTests();
		def cartGuid = cartTests.createRegularUsersCart(uid1, access_token1)
		def customermanager_access_token = testUtil.getAccessToken("customermanager", "1234")

		testUtil.getSecureConnection("/users/customermanager/carts/${cartGuid}", 'GET', 'XML', HttpURLConnection.HTTP_FORBIDDEN, null, null, customermanager_access_token)
	}

	@Test
	void testNonCustomerUserExistingCurrentCart() {
		def access_token1 = testUtil.getAccessToken(uid1, PASSWORD,config.TRUSTED_CLIENT_ID,config.TRUSTED_CLIENT_SECRET)
		CartTests cartTests = new CartTests();
		def cartGuid = cartTests.createRegularUsersCart(uid1, access_token1)
		def customermanager_access_token = testUtil.getAccessToken("customermanager", "1234")

		testUtil.getSecureConnection("/users/customermanager/carts/current", 'GET', 'XML', HttpURLConnection.HTTP_FORBIDDEN, null, null, customermanager_access_token)
	}
}