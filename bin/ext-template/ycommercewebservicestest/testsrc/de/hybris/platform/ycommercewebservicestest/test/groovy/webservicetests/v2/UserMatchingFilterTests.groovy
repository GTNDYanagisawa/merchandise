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
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.TestUtil
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest

import org.junit.BeforeClass
import org.junit.Test
import org.junit.experimental.categories.Category

@Category(CollectOutputFromTest.class)
@ManualTest
class UserMatchingFilterTests extends BaseWSTest {
	static final PASSWORD = "test"
	static uid

	@BeforeClass
	static void setUpBeforeClass() {
		def usersTest = new UsersTest()
		uid = usersTest.registerUser();
	}

	@Test
	void testNotMatchingPathForTrustedClient() {
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken();
		testUtil.getSecureConnection("/test/some/longer/path", 'GET', 'XML', HttpURLConnection.HTTP_NOT_FOUND, null, null, trusted_client_access_token)
	}

	@Test
	void testMatchingPathForUnknownCustomer() {
		def uid = "nonexistingcustomer@test.de"
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken();
		def con = testUtil.getSecureConnection("/users/${uid}", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, trusted_client_access_token)

		def error = con.errorStream.text
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'UnknownIdentifierError'
		assert response.errors[0].message == "Cannot find user with uid '" + uid + "'"
	}

	@Test
	void testMatchingPathForAuthenticatedCustomer() {
		def access_token = testUtil.getAccessToken(uid, "test", config.TRUSTED_CLIENT_ID, config.TRUSTED_CLIENT_SECRET)
		testUtil.getSecureConnection("/users/${uid}/addresses", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
	}

	@Test
	void testFailMatchingPathForUnauthenticatedCustomer() {
		testUtil.getSecureConnection("/users/${uid}/and/more", 'GET', 'XML', HttpURLConnection.HTTP_UNAUTHORIZED)
	}

	@Test
	void testMatchingFilterForAnonymousUser() {
		CartTests cartTests = new CartTests();
		def cartGuid = cartTests.createAnonymousUsersCart();
		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
	}

	@Test
	void testMatchingPathForCurrentCustomer() {
		def access_token = testUtil.getAccessToken(uid, "test", config.TRUSTED_CLIENT_ID, config.TRUSTED_CLIENT_SECRET)
		testUtil.getSecureConnection("/users/current/addresses", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
	}

	@Test
	void testNotMatchingPathForCustomermanager() {
		def access_token = testUtil.getAccessToken("customermanager", "1234")
		testUtil.getSecureConnection("/test/some/longer/path", 'GET', 'XML', HttpURLConnection.HTTP_NOT_FOUND, null, null, access_token)
	}

	@Test
	void testMatchingPathForCustomermanager() {
		def access_token = testUtil.getAccessToken("customermanager", "1234")
		testUtil.getSecureConnection("/customergroups", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
	}

	@Test
	void testNotMatchingPathForClient() {
		def client_access_token = testUtil.getClientCredentialsToken(config.CLIENT_ID, config.CLIENT_SECRET)
		testUtil.getSecureConnection("/test/some/longer/path", 'GET', 'XML', HttpURLConnection.HTTP_NOT_FOUND, null, null, client_access_token)
	}

	@Test
	void testMatchingPathForClient() {
		def client_access_token = testUtil.getClientCredentialsToken(config.CLIENT_ID, config.CLIENT_SECRET)
		def randomUID = System.currentTimeMillis()
		def body = "login=${randomUID}@test.v2.com&password=${PASSWORD}&firstName=fName&lastName=lName&titleCode=dr"
		testUtil.getSecureConnection("/users", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_access_token)
	}
}
