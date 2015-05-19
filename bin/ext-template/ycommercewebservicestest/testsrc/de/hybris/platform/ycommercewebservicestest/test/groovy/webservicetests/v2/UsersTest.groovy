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
class UsersTest extends BaseWSTest {

	static final firstName = "John"
	static final lastName = "Doe"
	static final titleCode = "dr"
	static final title = "Dr."
	static final public password = "test"
	static final line1 = "Nymphenburger Str. 86 - Maillingerstrasse"
	static final town = "Muenchen"
	static final town2 = "Hamburg"
	static final postalCode = "80331"
	static final countryIsoCode = "DE"
	static final String username = "orderhistoryuser@test.com"
	static final String order_code = "testOrder1"

	/**
	 * Helper method to register user
	 * @return generated userId
	 */
	def registerUser(useSecureConnection = true, status = HttpURLConnection.HTTP_CREATED) {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def randomUID = System.currentTimeMillis()
		def body = "login=${randomUID}@test.v2.com&password=${password}&firstName=${firstName}&lastName=${lastName}&titleCode=${titleCode}"

		def con = null
		if (useSecureConnection) {
			con = testUtil.getSecureConnection("/users", 'POST', 'XML', status, body, null, client_credentials_token)
		} else {
			con = testUtil.getConnection("/users", 'POST', 'XML', status, body, null, client_credentials_token)
		}

		return status == HttpURLConnection.HTTP_CREATED ? "${randomUID}@test.v2.com" : null
	}


	def registerUserJSON() {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def randomUID = System.currentTimeMillis()
		def body = "login=${randomUID}@test.v2.com&password=${password}&firstName=${firstName}&lastName=${lastName}&titleCode=${titleCode}"
		def con = testUtil.getSecureConnection("/users", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)
		return "${randomUID}@test.v2.com"
	}

	/**
	 * Helper method to create address
	 * @return generated addressId
	 */
	def createAddress(String userId, access_token, town = "Muenchen", cookieNoPath = null) {
		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&town=${town}&postalCode=${postalCode}&country.isocode=${countryIsoCode}"
		def con = testUtil.getSecureConnection("/users/${userId}/addresses?fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, postBody, cookieNoPath, access_token)

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.line1 == "${line1}"
		assert response.town == "${town}"
		assert response.country.isocode == "${countryIsoCode}"

		return response.id
	}

	def createAddressJSON(String userId, access_token, town = "Muenchen", cookieNoPath = null) {
		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&town=${town}&postalCode=${postalCode}&country.isocode=${countryIsoCode}"
		def con = testUtil.getSecureConnection("/users/${userId}/addresses?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, cookieNoPath, access_token)
		def response = testUtil.verifiedJSONSlurper(con)
		assert response.line1 == "${line1}"
		assert response.town == "${town}"
		assert response.country.isocode == "${countryIsoCode}"

		return response.id
	}

	def createCart(userId, access_token = null) {
		def con
		if (access_token != null) {
			con = testUtil.getSecureConnection("/users/${userId}/carts?fields=code", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, null, null, access_token)
			def response = testUtil.verifiedJSONSlurper(con)
			return response.code;
		} else {
			con = testUtil.getConnection("/users/anonymous/carts?fields=guid", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, null)
			def response = testUtil.verifiedJSONSlurper(con)
			return response.guid;
		}
	}

	def createPaymentInfo() {
		def customerTests = new UsersTest()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)
		def aid = customerTests.createAddressJSON(uid, access_token)
		//add something to a cart
		def cartId = createCart(uid, access_token)
		def postBody = "accountHolderName=John+Doe&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2013&saved=true&defaultPaymentInfo=false&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		def con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/paymentdetails?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)
		def paymentInfoId = response.id
		assert response.id != null
		assert response.accountHolderName == 'John Doe'
		assert response.cardType.code == 'visa'
		assert response.cardType.name == 'Visa'
		assert response.expiryMonth == '01'
		assert response.expiryYear == '2013'
		return [
			uid,
			access_token,
			paymentInfoId,
			cartId
		]
	}

	def addPaymentInfo(userId, cartId, access_token, defaultPaymentInfo = true, saved = true) {
		def postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2013&saved=$saved&defaultPaymentInfo=$defaultPaymentInfo&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		def con = testUtil.getSecureConnection("/users/${userId}/carts/${cartId}/paymentdetails?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)
		assert response.id
		return response
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testRegisterUserJSON() {
		def uid = registerUserJSON()
		assert uid
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testTryRegisterUserWithoutHttps() {
		def uid = registerUser(false, HttpURLConnection.HTTP_MOVED_TEMP)
		assert uid == null
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testTryRegisterUserWithHttps() {

		def uid = registerUser(true)
		println uid
		assert uid != null
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testRegisterDuplicateUID() {
		def uid = registerUser()

		//try to register another user with same UID
		def postBody = "login=${uid}&password=${password}&firstName=${firstName}&lastName=${lastName}&titleCode=${titleCode}"
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def con = testUtil.getSecureConnection("/users", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, client_credentials_token)

		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'DuplicateUidError'
		assert response.errors[0].message == "${uid}"
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testRegisterDuplicateUIDJSON() {
		def uid = registerUserJSON()

		//try to register another user with same UID
		def postBody = "login=${uid}&password=${password}&firstName=${firstName}&lastName=${lastName}&titleCode=${titleCode}"
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def con = testUtil.getSecureConnection("/users", 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, client_credentials_token)

		def error = con.errorStream.text;
		def response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'DuplicateUidError'
		assert response.errors[0].message == "${uid}"
	}
	//covered
	@Test
	void testGetUserProfile() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		//check customer profile
		def con = testUtil.getSecureConnection("/users/${uid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.uid == "${uid}"
		assert response.firstName == "${firstName}"
		assert response.lastName == "${lastName}"
		assert response.titleCode == "${titleCode}"
		assert response.title == "${title}"
	}

	//not transferred, gives no added value
	@Test
	void testGetUserProfileUsingParamAccessToken() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		//check customer profile
		def con = testUtil.getSecureConnection("/users/${uid}?fields=${FULL_SET}&access_token=${access_token}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, null)

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.uid == "${uid}"
		assert response.firstName == "${firstName}"
		assert response.lastName == "${lastName}"
		assert response.titleCode == "${titleCode}"
	}

	//covered
	@Test
	void testGetUserProfileJSON() {
		def uid = registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password);

		//check customer profile
		def con = testUtil.getSecureConnection("/users/${uid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)

		assert response.uid == "${uid}"
		assert response.firstName == "${firstName}"
		assert response.lastName == "${lastName}"
		assert response.titleCode == "${titleCode}"
		assert response.title == "${title}"
		assert response.name == "$firstName $lastName"

		assert response.currency.isocode == 'USD'
		assert response.language.isocode == 'en'

	}

	//covered
	@Test
	void testChangePassword() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		//change password
		def postBody = "old=${password}&new=newpassword"
		def con = testUtil.getSecureConnection("/users/${uid}/password", 'PUT', 'XML', HttpURLConnection.HTTP_ACCEPTED, postBody, null, access_token)

	}

	//covered
	@Test
	void testForceChangePassword() {
		def uid = registerUser()
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken();

		//change password
		def postBody = "new=newpassword"
		def con = testUtil.getSecureConnection("/users/${uid}/password", 'PUT', 'XML', HttpURLConnection.HTTP_ACCEPTED, postBody, null, trusted_client_access_token)

		// test if password correct
		def access_token = testUtil.getAccessToken(uid, 'newpassword');
	}

	//covered
	@Test
	void testForceChangePasswordFailWhenClientCredentialsProvided() {
		def uid = registerUser()
		def client_access_token = testUtil.getClientCredentialsToken();

		//change password
		def postBody = "new=newpassword"
		def con = testUtil.getSecureConnection("/users/${uid}/password", 'PUT', 'XML', HttpURLConnection.HTTP_FORBIDDEN, postBody, null, client_access_token)
	}

	//covered
	@Test
	void testForceChangePasswordFailWhenCustomerCredentialsProvided() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		//change password
		def postBody = "new=newpassword"
		def con = testUtil.getSecureConnection("/users/${uid}/password", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].reason == 'missing'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].subject == 'old'
		assert response.errors[0].message == 'Request parameter \'old\' is missing.'
	}

	//covered
	@Test
	void testForceChangePasswordFailIfCustomerNotExists() {
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken();

		//change password
		def postBody = "new=newpassword"
		def con = testUtil.getSecureConnection("/users/nonexisting@hybris.com/password", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, trusted_client_access_token)

		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'UnknownIdentifierError'
		assert response.errors[0].message == 'Cannot find user with uid \'nonexisting@hybris.com\''
	}

	//covered
	@Test
	void testChangePasswordJSON() {
		def uid = registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "old=${password}&new=newpassword"
		def con = testUtil.getSecureConnection("/users/${uid}/password", 'PUT', 'JSON', HttpURLConnection.HTTP_ACCEPTED, postBody, null, access_token)
	}

	//covered
	@Test
	void testAddressBook() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);
		def aid = createAddress(uid, access_token, town)

		//set default address to aid2
		testUtil.getSecureConnection("/users/${uid}/addresses/${aid}?defaultAddress=true", 'PATCH', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		//check if address is listed on address book
		def con = testUtil.getSecureConnection("/users/${uid}/addresses?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)
		assert response.addresses[0].line1 == "${line1}"
		assert response.addresses[0].town == "${town}"
		assert response.addresses[0].country.isocode == "${countryIsoCode}"
		//assert response.address[0].defaultAddress == true

		//add another address

		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=Bayerstr.+10a&town=${town2}&country.isocode=DE&postalCode=80335"
		testUtil.getSecureConnection("/users/${uid}/addresses", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)

		//check if both addresses are listed on address book
		con = testUtil.getSecureConnection("/users/${uid}/addresses?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		response = testUtil.verifiedXMLSlurper(con)
		assert response.addresses[0].line1 == "${line1}"
		assert response.addresses[0].town == "${town}"
		assert response.addresses[0].country.isocode == "${countryIsoCode}"
		assert response.addresses[1].town == "${town2}"
		assert response.addresses[1].line1 == "Bayerstr. 10a"
		assert response.addresses[1].postalCode == "80335"
		assert response.addresses[1].country.isocode == "DE"
	}

	//covered
	@Test
	void testAddressBookJSON() {
		def uid = registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password);
		def aid = createAddressJSON(uid, access_token, town)

		//check if address is listed on address book
		def con = testUtil.getSecureConnection("/users/${uid}/addresses?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def body = con.inputStream.text
		println body
		def response = new JsonSlurper().parseText(body)
		assert response.addresses[0].line1 == "${line1}"
		assert response.addresses[0].town == "${town}"
		assert response.addresses[0].country.isocode == "${countryIsoCode}"

		//add another address
		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=Bayerstr.+10a&town=${town2}&country.isocode=DE&postalCode=80335"
		testUtil.getSecureConnection("/users/${uid}/addresses", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)

		//check if both addresses are listed on address book
		con = testUtil.getSecureConnection("/users/${uid}/addresses?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		//println body
		response = new JsonSlurper().parseText(body)
		assert response.addresses[0].line1 == "${line1}"
		assert response.addresses[0].town == "${town}"
		assert response.addresses[0].country.isocode == "${countryIsoCode}"
		assert response.addresses[1].line1 == "Bayerstr. 10a"
		assert response.addresses[1].town == "${town2}"
		assert response.addresses[1].postalCode == "80335"
		assert response.addresses[1].country.isocode == "DE"
	}

	//covered
	@Test
	void testDeleteAddress() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password)
		def aid = createAddress(uid, access_token, town)

		//check one addresses in GET /addresses/
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name() == 'addressList'
		assert response.addresses
		assert response.addresses.size() == 1

		//delete address
		testUtil.getSecureConnection("/users/${uid}/addresses/" + aid, 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		//check no addresses in GET /addresses/
		con = testUtil.getSecureConnection("/users/${uid}/addresses/", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con)
		assert response.name() == 'addressList'
		assert response.addresses.size() == 0
	}

	//covered
	@Test
	void testDeleteNotExistingAddress() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password)

		//delete address
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/notExistingId", 'DELETE', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)

		def error = con.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].message == "Address with given id: 'notExistingId' doesn't exist or belong to another user"
	}

	//covered
	@Test
	void testDeleteAddressJSON() {
		def uid = registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password);
		def aid = createAddressJSON(uid, access_token, town)

		//check one addresses in GET /addresses/
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def body = con.inputStream.text
		def response = new JsonSlurper().parseText(body)
		assert response.addresses
		assert response.addresses.size() == 1

		//delete address
		con = testUtil.getSecureConnection("/users/${uid}/addresses/" + aid, 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		//check no addresses in GET /addresses/
		con = testUtil.getSecureConnection("/users/${uid}/addresses/", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		response = new JsonSlurper().parseText(body)
		assert !response.addresses
	}

	//covered
	@Test
	void testEditAddress() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password)
		def aid = createAddress(uid, access_token, town)

		//edit address
		def postBody = "town=Montreal&postalCode=80335"
		testUtil.getSecureConnection("/users/${uid}/addresses/${aid}", 'PATCH', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		//check address book
		def con = testUtil.getSecureConnection("/users/${uid}/addresses?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name() == 'addressList'
		assert response.addresses[0].line1 == "${line1}"
		assert response.addresses[0].town == "Montreal"
		assert response.addresses[0].postalCode == "80335"
		assert response.addresses[0].country.isocode == "${countryIsoCode}"
	}

	//covered
	@Test
	void testEditNotExistingAddress() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password)

		//edit address
		def postBody = "town=Montreal&postalCode=80335"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/notExistingId", 'PATCH', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].message == "Address with given id: 'notExistingId' doesn't exist or belong to another user"
	}

	//covered
	@Test
	void testEditAddressJSON() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);
		def aid = createAddressJSON(uid, access_token)

		//edit address
		def postBody = "town=Montreal&postalCode=80335"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/${aid}", 'PATCH', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		//check address book
		con = testUtil.getSecureConnection("/users/${uid}/addresses?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		def response = new JsonSlurper().parseText(con.inputStream.text)
		assert response.addresses[0].line1 == "${line1}"
		assert response.addresses[0].town == "Montreal"
		assert response.addresses[0].postalCode == "80335"
		assert response.addresses[0].country.isocode == "${countryIsoCode}"
	}

	//covered
	@Test
	void testPutAddressJSON() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);
		def aid = createAddressJSON(uid, access_token)

		//edit address
		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=Bayerstr.+10a&town=${town2}&country.isocode=DE&postalCode=80335"
		testUtil.getSecureConnection("/users/${uid}/addresses/${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		//check address book
		def con = testUtil.getSecureConnection("/users/${uid}/addresses?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		def response = new JsonSlurper().parseText(con.inputStream.text)
		assert response.addresses[0].line1 == "Bayerstr. 10a"
		assert response.addresses[0].town == "${town2}"
		assert response.addresses[0].postalCode == "80335"
		assert response.addresses[0].country.isocode == "DE"
	}

	//covered
	@Test
	void testPutAddressPartially() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);
		def aid = createAddressJSON(uid, access_token)

		//edit address
		def postBody = "firstName=newName&lastName=newLastName&line1=Zwyciestwa&town=Gliwice&country.isocode=PL&postalCode=44-100"
		testUtil.getSecureConnection("/users/${uid}/addresses/${aid}", 'PUT', '', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		def con = testUtil.getSecureConnection("/users/${uid}/addresses/${aid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def text = con.inputStream.text;
		//println text
		def response = new JsonSlurper().parseText(text)
		assert response.firstName == "newName"
		assert response.lastName == "newLastName"
		assert response.line1 == "Zwyciestwa"
		assert response.town == "Gliwice"
		assert response.postalCode == "44-100"
		assert response.country.isocode == "PL"
	}

	//covered
	@Test
	void testPutAddressWithoutNeededFields() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);
		def aid = createAddressJSON(uid, access_token)

		//edit address
		def postBody = "country.isocode=DE&postalCode=80335"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/${aid}", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subject == 'firstName'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].reason == 'missing'
		assert response.errors[0].message == "This field is required and must to be between 1 and 255 characters long."

		assert response.errors[1].type == 'ValidationError'
		assert response.errors[1].subject == 'lastName'
		assert response.errors[1].subjectType == 'parameter'
		assert response.errors[1].reason == 'missing'
		assert response.errors[1].message == "This field is required and must to be between 1 and 255 characters long."

		assert response.errors[2].type == 'ValidationError'
		assert response.errors[2].subject == 'line1'
		assert response.errors[2].subjectType == 'parameter'
		assert response.errors[2].reason == 'missing'
		assert response.errors[2].message == "This field is required and must to be between 1 and 255 characters long."

		assert response.errors[3].type == 'ValidationError'
		assert response.errors[3].subject == 'town'
		assert response.errors[3].subjectType == 'parameter'
		assert response.errors[3].reason == 'missing'
		assert response.errors[3].message == "This field is required and must to be between 1 and 255 characters long."
	}

	//covered
	@Test
	void testPutForNotExistingAddress() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password)

		//edit address
		def postBody = "lastName=${lastName}&line1=Bayerstr.+10a&town=${town2}&country.isocode=DE&postalCode=80335"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/notExistingId", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].message == "Address with given id: 'notExistingId' doesn't exist or belong to another user"
	}

	//covered
	@Test
	void testSetDefaultAddress() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);
		def aid = createAddress(uid, access_token)

		//add another address
		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=Bayerstr.+10a&town=${town2}&country.isocode=DE&postalCode=80335"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)
		def newAddressId = response.id

		//check if first address is set as default
		con = testUtil.getSecureConnection("/users/${uid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		def body = con.inputStream.text
		//println body
		response = new XmlSlurper().parseText(body)
		assert response.uid == "${uid}"
		assert response.defaultAddress.line1 == "${line1}"
		assert response.defaultAddress.town == "${town}"
		assert response.defaultAddress.country.isocode == "${countryIsoCode}"

		//set second address as default
		testUtil.getSecureConnection("/users/${uid}/addresses/${newAddressId}?defaultAddress=true", 'PATCH', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		//check if second address is set as customer's default
		con = testUtil.getSecureConnection("/users/${uid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = new XmlSlurper().parseText(con.inputStream.text)
		assert response.uid == "${uid}"
		assert response.defaultAddress.id == "${newAddressId}"
		assert response.defaultAddress.line1 == "Bayerstr. 10a"
		assert response.defaultAddress.town == "${town2}"
		assert response.defaultAddress.country.isocode == "${countryIsoCode}"
	}

	//covered
	@Test
	void testUpdateCustomerProfileJSON() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "firstName=Udo&lastName=Hubertus"
		testUtil.getSecureConnection("/users/${uid}", 'PATCH', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		def con = testUtil.getSecureConnection("/users/${uid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)
		assert response.uid == uid
		assert response.name == "Udo Hubertus"
		assert response.firstName == "Udo"
		assert response.lastName == "Hubertus"
		assert response.titleCode == 'dr'
		assert response.title == 'Dr.'
	}

	//covered
	@Test
	void testPutCustomerProfileJSON() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "titleCode=mr&firstName=Udo&lastName=Hubertus&language=zh&currency=EUR"
		testUtil.getSecureConnection("/users/${uid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		def con = testUtil.getSecureConnection("/users/${uid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)
		assert response.uid == uid
		assert response.name == "Udo Hubertus"
		assert response.firstName == "Udo"
		assert response.lastName == "Hubertus"
		assert response.currency.isocode == "EUR"
		assert response.language.isocode == "zh"
		assert response.titleCode == 'mr'
		assert response.title == 'Mr'
	}

	//covered
	@Test
	void testPutCustomerProfileXML() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "titleCode=mr&firstName=Udo&lastName=Hubertus&language=zh&currency=EUR"
		testUtil.getSecureConnection("/users/${uid}", 'PUT', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		def con = testUtil.getSecureConnection("/users/${uid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)
		assert response.uid == uid
		assert response.name == "Udo Hubertus"
		assert response.firstName == "Udo"
		assert response.lastName == "Hubertus"
		assert response.currency.isocode == "EUR"
		assert response.language.isocode == "zh"
	}

	//covered
	@Test
	void testUpdateCustomerProfile() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "titleCode=mr&firstName=Udo&lastName=Hubertus&language=zh&currency=EUR"
		testUtil.getSecureConnection("/users/${uid}", 'PATCH', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		def con = testUtil.getSecureConnection("/users/${uid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)
		assert response.uid == uid
		assert response.name == "Udo Hubertus"
		assert response.firstName == "Udo"
		assert response.lastName == "Hubertus"
		assert response.currency.isocode == "EUR"
		assert response.language.isocode == "zh"
		assert response.titleCode == 'mr'
		assert response.title == 'Mr'
	}

	//covered
	@Test
	void testPutCustomerProfilePartially() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "titleCode=mr&firstName=Udo&lastName=Hubertus"
		testUtil.getSecureConnection("/users/${uid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		def con = testUtil.getSecureConnection("/users/${uid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)
		assert response.uid == uid
		assert response.name == "Udo Hubertus"
		assert response.firstName == "Udo"
		assert response.lastName == "Hubertus"
		assert response.currency.isocode == "USD" //some default value is set
		assert response.language.isocode == "en" //some default value is set
		assert response.titleCode == 'mr'
		assert response.title == 'Mr'
	}
	//covered
	@Test
	void testPutCustomerProfileWithoutAllRequiredFields() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "firstName=Udo"
		def con = testUtil.getSecureConnection("/users/${uid}", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)
		def error = con.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'MissingServletRequestParameterError'
		assert response.errors[0].message == "Required String parameter 'lastName' is not present"
	}

	//covered
	@Test
	void testGetPaymentInfo() {

		/* given */
		def (uid, access_token, paymentInfoId) = createPaymentInfo()

		/* when */
		def con = testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)

		/* then */
		println response.expiryMonth
		println response.expiryMonth.getClass()
		assert response
		assert response.id == paymentInfoId
		assert response.accountHolderName == 'John Doe'
		assert response.cardType.code == 'visa'
		assert response.cardType.name == 'Visa'
		assert response.expiryYear == "2013"
		assert response.expiryMonth.text() == "01"
	}

	//covered
	@Test
	void testGetPaymentInfoJSON() {

		/* given */
		def (uid, access_token, paymentInfoId) = createPaymentInfo()

		/* when */
		def con = testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)

		/* then */
		assert response
		assert response.id == paymentInfoId
		assert response.accountHolderName == 'John Doe'
		assert response.cardType.code == 'visa'
		assert response.cardType.name == 'Visa'
		assert response.expiryYear == "2013"
		assert response.expiryMonth == "01"
	}

	//covered
	@Test
	void testDeletePaymentInfo() {

		/* given */
		def (uid, access_token, paymentInfoId) = createPaymentInfo()

		/* when */
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		/* then */
		def con = testUtil.getSecureConnection("/users/${uid}/paymentdetails", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)
		assert response.payments.size() == 0
	}

	//covered
	@Test
	void testDeletePaymentInfoJSON() {

		/* given */
		def (uid, access_token, paymentInfoId) = createPaymentInfo()

		/* when */
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		/* then */
		def con = testUtil.getSecureConnection("/users/${uid}/paymentdetails", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)

		assert !response.payments
	}

	//covered
	@Test
	void testUpdatePaymentInfo() {

		/* given */
		def (uid, access_token, paymentInfoId, cartId) = createPaymentInfo()

		/* when */
		def response = addPaymentInfo(uid, cartId, access_token, false, true)
		paymentInfoId = response.id

		/* then */
		def con = testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con, true)
		assert response.saved == true
		assert response.defaultPayment == false

		/* when */
		def postBody = "expiryMonth=02&defaultPaymentInfo=true"
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", 'PATCH', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		/* then */
		con = testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con, true)
		assert response.expiryMonth == "02"
		assert response.saved == true
		assert response.defaultPayment == true

		/* given */
		response = addPaymentInfo(uid, cartId, access_token, false, false)
		paymentInfoId = response.id

		/* when */
		postBody = "defaultPaymentInfo=true"
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", 'PATCH', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		/* then */
		con = testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con, true)
		assert response.saved == false
		assert response.defaultPayment == false // payment cannot be default when it's not 'saved'
	}

	//covered
	@Test
	void testUpdatePaymentInfoJSON() {

		/* given */
		def (uid, access_token, paymentInfoId, cartId) = createPaymentInfo()

		/* when */
		def response = addPaymentInfo(uid, cartId, access_token, false, true)
		paymentInfoId = response.id

		/* then */
		def con = testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, true)
		assert response.saved == true
		assert response.defaultPayment == false

		/* when */
		def postBody = "expiryMonth=02&defaultPaymentInfo=true"
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", 'PATCH', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		/* then */
		con = testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, true)
		assert response.expiryMonth == "02"
		assert response.saved == true
		assert response.defaultPayment == true

		/* given */
		response = addPaymentInfo(uid, cartId, access_token, false, false)
		paymentInfoId = response.id

		/* when */
		postBody = "defaultPaymentInfo=true"
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", 'PATCH', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		/* then */
		con = testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, true)
		assert response.saved == false
		assert response.defaultPayment == false // payment cannot be default when it's not 'saved'
	}

	//covered
	@Test
	void testUpdatePaymentAddress() {

		/* given */
		def (uid, access_token, paymentInfoId) = createPaymentInfo()

		/* when */
		def postBody = "billingAddress.titleCode=mr&billingAddress.firstName=ChangedFirstName&billingAddress.lastName=ChangedLastName&billingAddress.line1=ChangedLine1&billingAddress.line2=ChangedLine2&billingAddress.postalCode=44-100&billingAddress.town=ChangedTown&billingAddress.country.isocode=DE"
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", "PATCH", "XML", HttpURLConnection.HTTP_OK, postBody, null, access_token)

		/* then */
		def con = testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)

		assert response.id == paymentInfoId
		assert response.billingAddress != null
		assert response.billingAddress.title == "Mr"
		assert response.billingAddress.firstName == "ChangedFirstName"
		assert response.billingAddress.lastName == "ChangedLastName"
		assert response.billingAddress.line1 == "ChangedLine1"
		assert response.billingAddress.line2 == "ChangedLine2"
		assert response.billingAddress.postalCode == "44-100"
		assert response.billingAddress.town == "ChangedTown"
		assert response.billingAddress.country.isocode == "DE"
	}

	//covered
	@Test
	void testUpdatePaymentAddressJSON() {

		/* given */
		def (uid, access_token, paymentInfoId) = createPaymentInfo()

		/* when */
		def postBody = "billingAddress.titleCode=mr&billingAddress.firstName=ChangedFirstName&billingAddress.lastName=ChangedLastName&billingAddress.line1=ChangedLine1&billingAddress.line2=ChangedLine2&billingAddress.postalCode=44-100&billingAddress.town=ChangedTown&billingAddress.country.isocode=DE"
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", "PATCH", "JSON", HttpURLConnection.HTTP_OK, postBody, null, access_token)

		/* then */
		def con = testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)

		assert response.id == paymentInfoId
		assert response.billingAddress != null
		assert response.billingAddress.title == "Mr"
		assert response.billingAddress.firstName == "ChangedFirstName"
		assert response.billingAddress.lastName == "ChangedLastName"
		assert response.billingAddress.line1 == "ChangedLine1"
		assert response.billingAddress.line2 == "ChangedLine2"
		assert response.billingAddress.postalCode == "44-100"
		assert response.billingAddress.town == "ChangedTown"
		assert response.billingAddress.country.isocode == "DE"
	}

	//covered
	@Test
	void testUpdatePartiallyPaymentAddress() {

		/* given */
		def (uid, access_token, paymentInfoId) = createPaymentInfo()

		/* when */
		def postBody = "billingAddress.firstName=ChangedFirstName&billingAddress.lastName=ChangedLastName"
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", "PATCH", "XML", HttpURLConnection.HTTP_OK, postBody, null, access_token)

		/* then */
		def con = testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)

		assert response.id == paymentInfoId
		assert response.billingAddress != null
		assert response.billingAddress.title == "Mr"
		assert response.billingAddress.firstName == "ChangedFirstName"
		assert response.billingAddress.lastName == "ChangedLastName"
		assert response.billingAddress.line1 == "test1"
		assert response.billingAddress.line2 == "test2"
		assert response.billingAddress.postalCode == "12345"
		assert response.billingAddress.town == "somecity"
		assert response.billingAddress.country.isocode == "DE"
	}

	//covered
	@Test
	void testPaymentInfosFlowJSON() {
		def customerTests = new UsersTest()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)
		def aid = customerTests.createAddressJSON(uid, access_token)
		//add something to a cart
		def cartId = createCart(uid, access_token)
		def postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2013&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		def con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/paymentdetails?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con, true)
		assert response.id
		assert response.accountHolderName == 'Sven Haiges'
		assert response.cardType.code == 'visa'
		assert response.cardType.name == 'Visa'
		def paymentInfoId = response.id
		postBody = "startMonth=02&startYear=11&accountHolderName=Admin00&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2013&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		con = testUtil.getSecureConnection("/users/${uid}/paymentdetails?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)
		assert response.payments
		assert response.payments.size() == 1
		assert response.payments[0].id == paymentInfoId
		assert response.payments[0].accountHolderName == 'Admin00'
		assert response.payments[0].cardType.code == 'visa'
		assert response.payments[0].cardType.name == 'Visa'
		assert response.payments[0].expiryMonth == "01"
		assert response.payments[0].startMonth == "02"
		assert response.payments[0].startYear == "11"
		// changing only one attribute
		postBody = "expiryMonth=03"
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", 'PATCH', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		con = testUtil.getSecureConnection("/users/${uid}/paymentdetails?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)
		println response
		assert response.payments
		assert response.payments.size() == 1
		assert response.payments[0].id == paymentInfoId
		assert response.payments[0].accountHolderName == 'Admin00'
		assert response.payments[0].cardType.code == 'visa'
		assert response.payments[0].cardType.name == 'Visa'
		assert response.payments[0].expiryMonth == "03"
		assert response.payments[0].startMonth == "02"
		assert response.payments[0].startYear == "11"
		// you can change optional attributes like startMonth, startYear
		postBody = "startMonth=&expiryMonth=05&startYear="
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", 'PATCH', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		con = testUtil.getSecureConnection("/users/${uid}/paymentdetails?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)
		println response
		assert response.payments
		assert response.payments.size() == 1
		assert response.payments[0].id == paymentInfoId
		assert response.payments[0].accountHolderName == 'Admin00'
		assert response.payments[0].cardType.code == 'visa'
		assert response.payments[0].cardType.name == 'Visa'
		assert response.payments[0].expiryMonth == "05"
		assert !response.payments[0].startMonth
		assert !response.payments[0].startYear
		// it is not possible to set empty value for required attribute
		System.out.println("PAYMENbutT INFOS: " + response.payments[0]);
		postBody = "accountHolderName=&startYear=2012"
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", 'PATCH', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)
		// Updates billing address of existing customer's credit card payment info by payment info id.
		postBody = "billingAddress.titleCode=mr&billingAddress.firstName=ChangedFirstName&billingAddress.lastName=ChangedLastName&billingAddress.line1=ChangedLine1&billingAddress.line2=ChangedLine2&billingAddress.postalCode=44-100&billingAddress.town=ChangedTown&billingAddress.country.isocode=DE"
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", "PATCH", "JSON", HttpURLConnection.HTTP_OK, postBody, null, access_token)
		con = testUtil.getSecureConnection("/users/${uid}/paymentdetails?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)
		assert response.payments
		assert response.payments.size() == 1
		assert response.payments[0].id == paymentInfoId
		assert response.payments[0].billingAddress != null
		assert response.payments[0].billingAddress.title == "Mr"
		assert response.payments[0].billingAddress.firstName == "ChangedFirstName"
		assert response.payments[0].billingAddress.lastName == "ChangedLastName"
		assert response.payments[0].billingAddress.line1 == "ChangedLine1"
		assert response.payments[0].billingAddress.line2 == "ChangedLine2"
		assert response.payments[0].billingAddress.postalCode == "44-100"
		assert response.payments[0].billingAddress.town == "ChangedTown"
		assert response.payments[0].billingAddress.country.isocode == "DE"
		//delete payment infos
		testUtil.getSecureConnection("/users/${uid}/paymentdetails/${paymentInfoId}", "DELETE", "JSON", HttpURLConnection.HTTP_OK, null, null, access_token);
		con = testUtil.getSecureConnection("/users/${uid}/paymentdetails", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)
		assert !response.payments
	}

	//covered
	@Test
	public void testGetAllCustomerGroupsForCurrentCustomerXML() {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def access_token = testUtil.getAccessToken("customermanager", "1234")
		def userUid1 = "" + System.currentTimeMillis() + "@hybris.de"
		def customerGroup1 = "" + System.currentTimeMillis() + "_customerGroup"
		//add user

		def body = "login=${userUid1}&password=password&firstName=firstName&lastName=lastName&titleCode=dr"
		testUtil.getSecureConnection("/users", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)
		//add customer group
		body = "groupId=${customerGroup1}&localizedName=aaa"
		testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, body, null, access_token);
		//assign user to customer group1
		def putBody = "members=${userUid1}"
		testUtil.getSecureConnection("/customergroups/${customerGroup1}/members", "PUT", "XML", HttpURLConnection.HTTP_OK, putBody, null, access_token);
		def baseCustomerGroup = "customergroup"
		testUtil.getSecureConnection("/customergroups/${baseCustomerGroup}/members", "PATCH", "XML", HttpURLConnection.HTTP_OK, putBody, null, access_token);

		//check
		def customer_access_token = testUtil.getAccessToken(userUid1, "password")
		def con = testUtil.getSecureConnection("/users/${userUid1}/customergroups?fields=${FULL_SET}", "GET", "JSON", HttpURLConnection.HTTP_OK, null, null, customer_access_token);
		def response = testUtil.verifiedJSONSlurper(con)

		assert response.userGroups
		assert response.userGroups.size() == 2
		assert (response.userGroups[0].uid == baseCustomerGroup || response.userGroups[1].uid == baseCustomerGroup)
		assert (response.userGroups[0].uid == customerGroup1 || response.userGroups[1].uid == customerGroup1)

	}

	//covered
	@Test
	public void testGetAllCustomerGroupsForCurrentCustomerJSON() {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def access_token = testUtil.getAccessToken("customermanager", "1234")
		def userUid1 = "" + System.currentTimeMillis() + "@hybris.de"
		def customerGroup1 = "" + System.currentTimeMillis() + "_customerGroup"
		//add user

		def body = "login=${userUid1}&password=password&firstName=firstName&lastName=lastName&titleCode=dr"
		testUtil.getSecureConnection("/users", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)
		//add customer group
		body = "groupId=${customerGroup1}&localizedName=aaa"
		testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, body, null, access_token);
		//assign user to customer group1
		def putBody = "members=${userUid1}"
		testUtil.getSecureConnection("/customergroups/${customerGroup1}/members", "PUT", "XML", HttpURLConnection.HTTP_OK, putBody, null, access_token);
		def baseCustomerGroup = "customergroup"
		testUtil.getSecureConnection("/customergroups/${baseCustomerGroup}/members", "PATCH", "XML", HttpURLConnection.HTTP_OK, putBody, null, access_token);

		//check
		def customer_access_token = testUtil.getAccessToken(userUid1, "password")
		def con = testUtil.getSecureConnection("/users/${userUid1}/customergroups?fields=${FULL_SET}", "GET", "JSON", HttpURLConnection.HTTP_OK, null, null, customer_access_token);

		def response = testUtil.verifiedJSONSlurper(con, true)
		assert response.userGroups
		assert response.userGroups.size() == 2
		assert (response.userGroups[0].uid == baseCustomerGroup || response.userGroups[1].uid == baseCustomerGroup)
		assert (response.userGroups[0].uid == customerGroup1 || response.userGroups[1].uid == customerGroup1)
	}

	//covered
	@Test
	public void testGetAllCustomerGroupsWhenUserIsNotCustomer() {
		def access_token = testUtil.getAccessToken("admin", "nimda")
		def con = testUtil.getSecureConnection("/users/admin/customergroups", "GET", "JSON", HttpURLConnection.HTTP_FORBIDDEN, null, null, access_token);
	}

	//covered
	@Test
	public void testGetAllCustomerGroupsForCustomerXML() {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def access_token = testUtil.getAccessToken("customermanager", "1234");
		def userUid1 = "" + System.currentTimeMillis() + "@hybris.de"
		def customerGroup1 = "" + System.currentTimeMillis() + "_customerGroup"
		//add user

		def body = "login=${userUid1}&password=password&firstName=firstName&lastName=lastName&titleCode=dr"
		testUtil.getSecureConnection("/users", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)
		//add customer group
		body = "groupId=${customerGroup1}&localizedName=aaa"
		testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, body, null, access_token);
		//assign user to customer group1
		def putBody = "members=${userUid1}"
		testUtil.getSecureConnection("/customergroups/${customerGroup1}/members", "PUT", "XML", HttpURLConnection.HTTP_OK, putBody, null, access_token);
		def baseCustomerGroup = "customergroup"
		testUtil.getSecureConnection("/customergroups/${baseCustomerGroup}/members", "PATCH", "XML", HttpURLConnection.HTTP_OK, putBody, null, access_token);

		//check
		def con = testUtil.getSecureConnection("/users/${userUid1}/customergroups?fields=${FULL_SET}", "GET", "XML", HttpURLConnection.HTTP_OK, null, null, access_token);

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.userGroups
		assert response.userGroups.size() == 2
		assert (response.userGroups[0].uid == baseCustomerGroup || response.userGroups[1].uid == baseCustomerGroup)
		assert (response.userGroups[0].uid == customerGroup1 || response.userGroups[1].uid == customerGroup1)
	}

	//covered
	@Test
	public void testGetAllCustomerGroupsForCustomerJSON() {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def access_token = testUtil.getAccessToken("customermanager", "1234")
		def userUid1 = "" + System.currentTimeMillis() + "@hybris.de"
		def customerGroup1 = "" + System.currentTimeMillis() + "_customerGroup"
		//add user

		def body = "login=${userUid1}&password=password&firstName=firstName&lastName=lastName&titleCode=dr"
		testUtil.getSecureConnection("/users", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)
		//add customer group
		body = "groupId=${customerGroup1}&localizedName=aaa"
		testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, body, null, access_token);
		//assign user to customer group1
		def putBody = "members=${userUid1}"
		testUtil.getSecureConnection("/customergroups/${customerGroup1}/members", "PUT", "XML", HttpURLConnection.HTTP_OK, putBody, null, access_token);
		def baseCustomerGroup = "customergroup"
		testUtil.getSecureConnection("/customergroups/${baseCustomerGroup}/members", "PATCH", "XML", HttpURLConnection.HTTP_OK, putBody, null, access_token);

		//check
		def con = testUtil.getSecureConnection("/users/${userUid1}/customergroups?fields=${FULL_SET}", "GET", "JSON", HttpURLConnection.HTTP_OK, null, null, access_token);

		def response = testUtil.verifiedJSONSlurper(con, true)
		assert response.userGroups
		assert response.userGroups.size() == 2
		assert (response.userGroups[0].uid == baseCustomerGroup || response.userGroups[1].uid == baseCustomerGroup)
		assert (response.userGroups[0].uid == customerGroup1 || response.userGroups[1].uid == customerGroup1)
	}

	//covered
	@Test
	public void testGetAllCustomerGroupsForCustomerByCustomerManager() {
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def access_token = testUtil.getAccessToken("customermanager", "1234")
		def userUid1 = "" + System.currentTimeMillis() + "@hybris.de"
		def customerGroup1 = "" + System.currentTimeMillis() + "_customerGroup"

		//add user
		def body = "login=${userUid1}&password=password&firstName=firstName&lastName=lastName&titleCode=dr"
		testUtil.getSecureConnection("/users", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)

		//add customer group
		body = "groupId=${customerGroup1}&localizedName=aaa"
		testUtil.getSecureConnection("/customergroups", "POST", "XML", HttpURLConnection.HTTP_CREATED, body, null, access_token);

		//assign user to customer group1
		def putBody = "members=${userUid1}"
		testUtil.getSecureConnection("/customergroups/${customerGroup1}/members", "PUT", "XML", HttpURLConnection.HTTP_OK, putBody, null, access_token);
		def baseCustomerGroup = "customergroup"
		testUtil.getSecureConnection("/customergroups/${baseCustomerGroup}/members", "PATCH", "XML", HttpURLConnection.HTTP_OK, putBody, null, access_token);

		//check
		def customer_access_token = testUtil.getAccessToken(userUid1, "password")
		def con = testUtil.getSecureConnection("/users/${userUid1}/customergroups", "GET", "JSON", HttpURLConnection.HTTP_OK, null, null, customer_access_token);

		def response = testUtil.verifiedJSONSlurper(con, true)
		assert response.userGroups
		assert response.userGroups.size() == 2
		assert (response.userGroups[0].uid == baseCustomerGroup || response.userGroups[1].uid == baseCustomerGroup)
		assert (response.userGroups[0].uid == customerGroup1 || response.userGroups[1].uid == customerGroup1)

	}

	//covered
	@Test
	void testChangeLoginXML() {
		def uid = registerUser()
		def newUid = "AbC" + System.currentTimeMillis() + "@hybris.com"
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "newLogin=${newUid}&password=${password}"
		testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	}

	//covered
	@Test
	void testChangeLoginJSON() {
		def uid = registerUser()
		def newUid = "AbC" + System.currentTimeMillis() + "@hybris.com"
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "newLogin=${newUid}&password=${password}"
		testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testChangeLoginDuplicateUIDXML() {
		def existingUid = registerUser()
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		// try to change uid from ${uid} to already existing ${existingUid}
		def postBody = "newLogin=${existingUid}&password=${password}"
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'DuplicateUidError'
		assert response.errors[0].message == "User with email ${existingUid} already exists."
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testChangeLoginDuplicateUIDJSON() {
		def existingUid = registerUserJSON()
		def uid = registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password);

		// try to change uid from ${uid} to already existing ${existingUid}
		def postBody = "newLogin=${existingUid}&password=${password}"
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		def response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'DuplicateUidError'
		assert response.errors[0].message == "User with email ${existingUid} already exists."
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testChangeLoginInvalidEmailXML() {
		def newLogin = "notaValidEmail"
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		// try to change uid from ${uid} to ${newLogin} which is not a correct email
		def postBody = "newLogin=${newLogin}&password=${password}"
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subject == 'newLogin'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].reason == 'invalid'
		assert response.errors[0].message == 'Login [notaValidEmail] is not a valid e-mail address!'
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testUpdateCustomerProfileChangeLoginInvalidEmailJSON() {
		def newLogin = "notaValidEmail"
		def uid = registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password);

		// try to change uid from ${uid} to ${newLogin} which is not a correct email
		def postBody = "newLogin=${newLogin}&password=${password}"
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		def response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subject == 'newLogin'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].reason == 'invalid'
		assert response.errors[0].message == 'Login [notaValidEmail] is not a valid e-mail address!'
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testChangeLoginEmptyNewLoginXML() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		// try to change uid from ${uid} to a not set newLogin
		def postBody = "newLogin=&password=${password}"
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subject == 'newLogin'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].reason == 'invalid'
		assert response.errors[0].message == 'Login [] is not a valid e-mail address!'
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testChangeLoginEmptyNewLoginJSON() {
		def uid = registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		// try to change uid from ${uid} to a not set newLogin
		def postBody = "newLogin=&password=${password}"
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		def response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subject == 'newLogin'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].reason == 'invalid'
		assert response.errors[0].message == 'Login [] is not a valid e-mail address!'
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testChangeLoginEmptyPasswordXML() {
		def uid = registerUser()
		def newUid = "AbC" + System.currentTimeMillis() + "@hybris.com"
		def access_token = testUtil.getAccessToken(uid, password);

		// try to change uid without passed password
		def postBody = "newLogin=${newUid}&password="
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'IllegalArgumentError'
		assert response.errors[0].message == 'The field [currentPassword] cannot be empty'
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testChangeLoginEmptyPasswordJSON() {
		def uid = registerUser()
		def newUid = "AbC" + System.currentTimeMillis() + "@hybris.com"
		def access_token = testUtil.getAccessToken(uid, password);

		// try to change uid without passed password
		def postBody = "newLogin=${newUid}&password="
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		def response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'IllegalArgumentError'
		assert response.errors[0].message == 'The field [currentPassword] cannot be empty'
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testChangeLoginWrongPasswordXML() {
		def uid = registerUser()
		def newUid = "AbC" + System.currentTimeMillis() + "@hybris.com"
		def access_token = testUtil.getAccessToken(uid, password);

		// try to change uid with wrong password
		def postBody = "newLogin=${newUid}&password=paswd"
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'PasswordMismatchError'
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testChangeLoginWrongPasswordJSON() {
		def uid = registerUser()
		def newUid = "AbC" + System.currentTimeMillis() + "@hybris.com"
		def access_token = testUtil.getAccessToken(uid, password);

		// try to change uid with wrong password
		def postBody = "newLogin=${newUid}&password=paswd"
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		println error
		def response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'PasswordMismatchError'
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testChangeLoginAndGetToken4ValidUIDXML() {
		def uid = registerUser()
		def newUid = "abc" + System.currentTimeMillis() + "@hybris.com"
		def access_token = testUtil.getAccessToken(uid, password);
		def postBody = "newLogin=${newUid}&password=${password}"
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		// get access token with new UID
		def access_token2 = testUtil.getAccessToken(newUid, password);
		assert access_token2 != null
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testChangeLoginAndGetToken4InvalidUIDXML() {
		def uid = registerUser()
		def newUid = "abc" + System.currentTimeMillis() + "@hybris.com"
		def access_token = testUtil.getAccessToken(uid, password);
		def postBody = "newLogin=${newUid}&password=${password}"
		def con = testUtil.getSecureConnection("/users/${uid}/login", 'PUT', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		// get access token with old (non-existing) UID
		try {
			def access_token2 = testUtil.getAccessToken(uid, password);
		} catch (java.io.IOException ex) {
			return;
		}
		org.junit.Assert.fail("Failure expected for getting access token.");
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testRestorePasswordXML() {
		def uid = registerUser()
		def client_credentials_token = testUtil.getClientCredentialsToken()

		// restore password for existing uid
		def postBody = "userId=${uid}"
		def con = testUtil.getSecureConnection("/forgottenpasswordtokens", 'POST', 'XML', HttpURLConnection.HTTP_ACCEPTED, postBody, null, client_credentials_token)
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testRestorePasswordJSON() {
		def uid = registerUser()
		def client_credentials_token = testUtil.getClientCredentialsToken()

		// restore password for existing uid
		def postBody = "userId=${uid}"
		def con = testUtil.getSecureConnection("/forgottenpasswordtokens", 'POST', 'JSON', HttpURLConnection.HTTP_ACCEPTED, postBody, null, client_credentials_token)
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testRestorePasswordNonExistingLoginXML() {
		def uid = System.currentTimeMillis() + "@hybris.com"
		def client_credentials_token = testUtil.getClientCredentialsToken()

		// restore password passing non-existing uid
		def postBody = "userId=${uid}"
		def con = testUtil.getSecureConnection("/forgottenpasswordtokens", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, client_credentials_token)

		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'UnknownIdentifierError'
		assert response.errors[0].message == "Cannot find user with uid '${uid}'"
	}

	//covered
	@Category(AvoidCollectingOutputFromTest.class)
	@Test
	void testRestorePasswordNonExistingLoginJSON() {
		def uid = System.currentTimeMillis() + "@hybris.com"
		def client_credentials_token = testUtil.getClientCredentialsToken()

		// restore password passing non-existing uid
		def postBody = "userId=${uid}"
		def con = testUtil.getSecureConnection("/forgottenpasswordtokens", 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, client_credentials_token)

		def error = con.errorStream.text;
		def response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'UnknownIdentifierError'
		assert response.errors[0].message == "Cannot find user with uid '${uid}'"
	}

	//covered
	@Test
	void testVerifyValidAddressJSON() {
		def customerTests = new UsersTest()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)

		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&town=${town}&postalCode=${postalCode}&country.isocode=${countryIsoCode}"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/verification?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)

		assert response.decision == "ACCEPT"
		assert response.suggestedAddresses == null
	}

	//covered
	@Test
	void testVerifyRejectedByValidatorAddressJSON() {
		def customerTests = new UsersTest()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)

		def templine1 = "12345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452"
		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=${templine1}&town=${town}&postalCode=${postalCode}&country.isocode=${countryIsoCode}"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/verification?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con, true)

		assert response.decision == "REJECT"
		assert response.suggestedAddresses == null
	}

	//covered
	@Test
	void testVerifyRejectedByServiceAddressJSON() {
		def customerTests = new UsersTest()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)
		def line2 = "12345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452"
		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&line2=${line2}&town=${town}&postalCode=${postalCode}&country.isocode=${countryIsoCode}"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/verification?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)

		assert response.decision == "REJECT"
		assert response.suggestedAddresses == null
	}

	//covered
	@Test
	void testVerifyReviewAddressJSON() {
		def customerTests = new UsersTest()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)
		def town2 = "review"
		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&town=${town2}&postalCode=${postalCode}&country.isocode=${countryIsoCode}"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/verification?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)

		assert response.decision == "REVIEW"
		assert response.suggestedAddresses.size == 1
		assert response.suggestedAddresses[0].line1.contains('corrected')
	}

	//covered
	@Test
	void testVerifyValidAddressXML() {
		def customerTests = new UsersTest()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)

		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&town=${town}&postalCode=${postalCode}&country.isocode=${countryIsoCode}"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/verification?fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)

		assert response.decision == "ACCEPT"
	}

	//covered
	@Test
	void testVerifyRejectedByValidatorAddressXML() {
		def customerTests = new UsersTest()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)

		def templine1 = "12345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452"
		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=${templine1}&town=${town}&postalCode=${postalCode}&country.isocode=${countryIsoCode}"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/verification?fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)

		assert response.decision == "REJECT"
	}

	//covered
	@Test
	void testVerifyRejectedByServiceAddressXML() {
		def customerTests = new UsersTest()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)
		def line2 = "12345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452123456789112345678901234521234567891123456789012345212345678911234567890123452"
		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&line2=${line2}&town=${town}&postalCode=${postalCode}&country.isocode=${countryIsoCode}"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/verification?fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)

		assert response.decision == "REJECT"
	}

	//covered
	@Test
	void testVerifyReviewAddressXML() {
		def customerTests = new UsersTest()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)
		def town2 = "review"
		def postBody = "titleCode=${titleCode}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&town=${town2}&postalCode=${postalCode}&country.isocode=${countryIsoCode}"
		def con = testUtil.getSecureConnection("/users/${uid}/addresses/verification?fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def response = testUtil.verifiedXMLSlurper(con)

		assert response.suggestedAddresses
		assert response.suggestedAddresses.size() == 1
		assert response.suggestedAddresses[0].line1.toString().contains('corrected')
		assert response.decision == "REVIEW"
	}

	//covered
	@Test
	void testGetOrders() {
		def response, con
		def access_token = testUtil.getAccessToken(username, password)

		con = testUtil.getSecureConnection("/users/${username}/orders", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.orders
		assert response.orders.size() == 13

		con = testUtil.getSecureConnection("/users/${username}/orders/?pageSize=5", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.orders
		assert response.orders.size() == 5
	}

	//covered
	@Test
	void testGetOrdersWithStatuses() {
		def response, con
		def access_token = testUtil.getAccessToken(username, password)

		con = testUtil.getSecureConnection("/users/${username}/orders/?statuses=CREATED,CANCELLED", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.orders
		assert response.orders.size() == 13

		con = testUtil.getSecureConnection("/users/${username}/orders/?statuses=CREATED", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.orders
		assert response.orders.size() == 10

		con = testUtil.getSecureConnection("/users/${username}/orders/?statuses=CREATED&pageSize=5", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.orders
		assert response.orders.size() == 5
	}

	//covered
	@Test
	void testGetOrdersWithWrongStatus() {
		def access_token = testUtil.getAccessToken(username, password)

		def con = testUtil.getSecureConnection("/users/${username}/orders/?statuses=THIS_IS_WRONG", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
		def error = con.errorStream.text
		def response = new XmlSlurper().parseText(error)
		// TODO: this exception should be wrapped into some commercelayer exception - defect reported
		assert response.errors[0].type == "IllegalStateError"
		assert response.errors[0].message == "missing persistent item for enum value THIS_IS_WRONG"
	}

	//covered
	@Test
	void testGetUserOrderByCode() {
		def access_token = testUtil.getAccessToken(username, password)

		def con = testUtil.getSecureConnection("/users/${username}/orders/${order_code}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con, true, false)
		assert response.store == "wsTest"
		assert response.net == false
		assert response.totalDiscounts != null
		assert response.productDiscounts != null
		assert response.created != null
		assert response.subTotal != null
		assert response.orderDiscounts != null
		assert response.entries
		assert response.entries.size() == 2
		assert response.totalPrice != null
		assert response.site == "wsTest"
		assert response.status == "CREATED"
		assert response.statusDisplay.toLowerCase() == "created"
		assert response.deliveryMode != null
		assert response.code == order_code
		assert response.totalItems == 2
		assert response.totalPriceWithTax != null
		assert response.guestCustomer == false
		//assert response.deliveryItemsQuantity == 7
		assert response.totalTax != null
		assert response.user.uid == username
		assert response.user.name == "orders test user"
		assert response.deliveryCost != null
	}

	//covered
	@Test
	void testGetOrderByWrongCode() {
		def access_token = testUtil.getAccessToken(username, password)

		def con = testUtil.getSecureConnection("/users/${username}/orders/THIS_IS_WRONG", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error);
		assert response.errors.type == "UnknownIdentifierError"
		assert response.errors.message == "No result for the given query"
	}
}
