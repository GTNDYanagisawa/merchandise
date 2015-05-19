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

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.addons
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v1.BaseWSTest

import org.junit.Test

import groovy.json.JsonSlurper

@org.junit.experimental.categories.Category(CollectOutputFromTest.class)
@ManualTest
class ExtendedCustomersTests extends BaseWSTest{

	final firstName = "Sven"
	final lastName = "Haiges"
	final titleCode = "dr"
	final public static password = "test"

	/**
	 * helper method to register user
	 * @return generated userId
	 */
	def registerUser(useSecureConnection = true, status = HttpURLConnection.HTTP_CREATED) {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def randomUID = System.currentTimeMillis()
		def body = "login=${randomUID}@sven.de&password=${password}&firstName=${firstName}&lastName=${lastName}&titleCode=${titleCode}"

		def con = null
		if (useSecureConnection) {
			con = testUtil.getSecureConnection("/customers", 'POST', 'XML', status, body, null, client_credentials_token)
		} else {
			con = testUtil.getConnection("/customers", 'POST', 'XML', status, body, null, client_credentials_token)
		}

		return status == HttpURLConnection.HTTP_CREATED ? "${randomUID}@sven.de" : null
	}


	def registerUserJSON() {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def randomUID = System.currentTimeMillis()
		def body = "login=${randomUID}@sven.de&password=${password}&firstName=${firstName}&lastName=${lastName}&titleCode=${titleCode}"
		def con = testUtil.getSecureConnection("/customers", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)
		return "${randomUID}@sven.de"
	}

	@Test
	void testSetUserLocationByGeolocationJSON() {
		def uid = registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password);
		def postBody = "latitude=35.65&longitude=139.69"

		def con = testUtil.getSecureConnection("/customers/current/locationLatLong", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def cookie = con.getHeaderField('Set-Cookie')

		assert cookie: 'No cookie present, cannot keep session'

		def cookieNoPath = cookie.split(';')[0]

		con = testUtil.getSecureConnection("/customers/current/location", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		def response = testUtil.verifiedJSONSlurper(con)

		assert response.point.longitude == 139.69
		assert response.point.latitude == 35.65
		assert response.searchTerm == ""
	}

	@Test
	void testSetUserLocationByGeolocationXML() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);
		def postBody = "latitude=35.65&longitude=139.69"

		def con = testUtil.getSecureConnection("/customers/current/locationLatLong", 'PUT', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def cookie = con.getHeaderField('Set-Cookie')

		assert cookie: 'No cookie present, cannot keep session'

		def cookieNoPath = cookie.split(';')[0]

		con = testUtil.getSecureConnection("/customers/current/location", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		def response = testUtil.verifiedXMLSlurper(con)

		assert response.point.longitude == 139.69
		assert response.point.latitude == 35.65
		assert response.searchTerm == ""
	}

	@Test
	void testSetUserLocationByValidTermJSON() {
		def uid = registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password);
		def postBody = "location=tokio"

		def con = testUtil.getSecureConnection("/customers/current/location", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def cookie = con.getHeaderField('Set-Cookie')

		assert cookie: 'No cookie present, cannot keep session'

		def cookieNoPath = cookie.split(';')[0]

		con = testUtil.getSecureConnection("/customers/current/location", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		def response = testUtil.verifiedJSONSlurper(con)

		assert response.searchTerm == "tokio"
		assert response.point.latitude == 35.6894875
		assert response.point.longitude == 139.6917064
	}

	@Test
	void testSetUserLocationByValidTermXML() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);
		def postBody = "location=tokio"

		def con = testUtil.getSecureConnection("/customers/current/location", 'PUT', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def cookie = con.getHeaderField('Set-Cookie')

		assert cookie: 'No cookie present, cannot keep session'

		def cookieNoPath = cookie.split(';')[0]

		con = testUtil.getSecureConnection("/customers/current/location", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		def response = testUtil.verifiedXMLSlurper(con)

		assert response.searchTerm == "tokio"
		assert response.point.latitude == 35.6894875
		assert response.point.longitude == 139.6917064
	}

	@Test
	void testSetUserLocationByInValidTermJSON() {
		def uid = registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password);
		def location="457231589321y5sdhkguisdgh"
		def postBody = "location=${location}"

		def con = testUtil.getSecureConnection("/customers/current/location", 'PUT', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)
		def error = con.errorStream.text;
		def response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'NoLocationFoundError'
		assert response.errors[0].message == "Location: ${location} could not be found"
	}

	@Test
	void testSetUserLocationByInValidTermXML() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);
		def location="457231589321y5sdhkguisdgh"
		def postBody = "location=${location}"

		def con = testUtil.getSecureConnection("/customers/current/location", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)
		def response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.error.type == 'NoLocationFoundError'
		assert response.error.message == "Location: ${location} could not be found"
	}
}
