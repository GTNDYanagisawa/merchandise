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

/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v1
import de.hybris.bootstrap.annotations.ManualTest
import org.junit.Test

@ManualTest
class AddressTest extends BaseWSTest {


	final firstName = "John"
	final lastName = "Doe"
	final titleCode = "dr"
	final title = "dr"
	final password = "test"
	final line1 = "Zygmunta Starego 11"
	final line2 = "2nd floor"
	final town = "Gliwice"
	final postalCode = "44-100"
	final plIsoCode = "PL"
	final jpIsoCode = "JP"
	final cnIsoCode = "CN"
	final phone = "+4855488755296"
	final companyName = "hybris"
	final email = "email@email.dot.com"

	final japanRegonCode = "JP-23" //Aiti [Aichi]
	final chinaRegonCode = "CN-34" //Anhui


	@Test
	void shouldReturnBadRequestDueToMissingCountryCode() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "titleCode=${title}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&town=${town}&postalCode=${postalCode}"
		def con = testUtil.getSecureConnection("/customers/current/addresses", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.error.size()==1;
		assert response.error[0].type == 'ValidationError'
		assert response.error[0].subject == 'country.isocode'
		assert response.error[0].subjectType == 'parameter'
		assert response.error[0].reason == 'missing'
		assert response.error[0].message == 'This field is required and must to be between 1 and 2 characters long.'
	}

	@Test
	void shouldReturnValidationErrorsDueToMissingFields() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "country.isocode=${plIsoCode}"
		def con = testUtil.getSecureConnection("/customers/current/addresses", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.error.size()==6;
		assert response.error[0].type == 'ValidationError'
		assert response.error[0].subject == 'firstName'
		assert response.error[0].subjectType == 'parameter'
		assert response.error[0].reason == 'missing'
		assert response.error[0].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[1].type == 'ValidationError'
		assert response.error[1].subject == 'lastName'
		assert response.error[1].subjectType == 'parameter'
		assert response.error[1].reason == 'missing'
		assert response.error[1].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[2].type == 'ValidationError'
		assert response.error[2].subject == 'line1'
		assert response.error[2].subjectType == 'parameter'
		assert response.error[2].reason == 'missing'
		assert response.error[2].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[3].type == 'ValidationError'
		assert response.error[3].subject == 'town'
		assert response.error[3].subjectType == 'parameter'
		assert response.error[3].reason == 'missing'
		assert response.error[3].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[4].type == 'ValidationError'
		assert response.error[4].subject == 'postalCode'
		assert response.error[4].subjectType == 'parameter'
		assert response.error[4].reason == 'missing'
		assert response.error[4].message == 'This field is required and must to be between 1 and 10 characters long.'
		assert response.error[5].type == 'ValidationError'
		assert response.error[5].subject == 'titleCode'
		assert response.error[5].subjectType == 'parameter'
		assert response.error[5].reason == 'missing'
		assert response.error[5].message == 'This field is required and must to be between 1 and 255 characters long.'
	}

	@Test
	void shouldReturnValidationErrorsDueToMissingFields_JAPAN() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "country.isocode=${jpIsoCode}"
		def con = testUtil.getSecureConnection("/customers/current/addresses", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.error.size()==7;
		assert response.error[0].type == 'ValidationError'
		assert response.error[0].subject == 'firstName'
		assert response.error[0].subjectType == 'parameter'
		assert response.error[0].reason == 'missing'
		assert response.error[0].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[1].type == 'ValidationError'
		assert response.error[1].subject == 'lastName'
		assert response.error[1].subjectType == 'parameter'
		assert response.error[1].reason == 'missing'
		assert response.error[1].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[2].type == 'ValidationError'
		assert response.error[2].subject == 'line1'
		assert response.error[2].subjectType == 'parameter'
		assert response.error[2].reason == 'missing'
		assert response.error[2].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[3].type == 'ValidationError'
		assert response.error[3].subject == 'town'
		assert response.error[3].subjectType == 'parameter'
		assert response.error[3].reason == 'missing'
		assert response.error[3].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[4].type == 'ValidationError'
		assert response.error[4].subject == 'postalCode'
		assert response.error[4].subjectType == 'parameter'
		assert response.error[4].reason == 'missing'
		assert response.error[4].message == 'This field is required and must to be between 1 and 10 characters long.'
		assert response.error[5].type == 'ValidationError'
		assert response.error[5].subject == 'line2'
		assert response.error[5].subjectType == 'parameter'
		assert response.error[5].reason == 'missing'
		assert response.error[5].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[6].type == 'ValidationError'
		assert response.error[6].subject == 'region.isocode'
		assert response.error[6].subjectType == 'parameter'
		assert response.error[6].reason == 'missing'
		assert response.error[6].message == 'This field is required and must to be between 1 and 7 characters long.'
	}

	@Test
	void shouldReturnValidationErrorsDueToMissingFields_CHINA() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "country.isocode=${cnIsoCode}"
		def con = testUtil.getSecureConnection("/customers/current/addresses", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)

		def error = con.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.error.size()==7;
		assert response.error[0].type == 'ValidationError'
		assert response.error[0].subject == 'firstName'
		assert response.error[0].subjectType == 'parameter'
		assert response.error[0].reason == 'missing'
		assert response.error[0].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[1].type == 'ValidationError'
		assert response.error[1].subject == 'lastName'
		assert response.error[1].subjectType == 'parameter'
		assert response.error[1].reason == 'missing'
		assert response.error[1].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[2].type == 'ValidationError'
		assert response.error[2].subject == 'line1'
		assert response.error[2].subjectType == 'parameter'
		assert response.error[2].reason == 'missing'
		assert response.error[2].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[3].type == 'ValidationError'
		assert response.error[3].subject == 'town'
		assert response.error[3].subjectType == 'parameter'
		assert response.error[3].reason == 'missing'
		assert response.error[3].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[4].type == 'ValidationError'
		assert response.error[4].subject == 'postalCode'
		assert response.error[4].subjectType == 'parameter'
		assert response.error[4].reason == 'missing'
		assert response.error[4].message == 'This field is required and must to be between 1 and 10 characters long.'
		assert response.error[5].type == 'ValidationError'
		assert response.error[5].subject == 'titleCode'
		assert response.error[5].subjectType == 'parameter'
		assert response.error[5].reason == 'missing'
		assert response.error[5].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[6].type == 'ValidationError'
		assert response.error[6].subject == 'region.isocode'
		assert response.error[6].subjectType == 'parameter'
		assert response.error[6].reason == 'missing'
		assert response.error[6].message == 'This field is required and must to be between 1 and 7 characters long.'

	}

	@Test
	void shouldSaveNewAddress() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "titleCode=${title}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&town=${town}&country.isocode=${plIsoCode}&postalCode=${postalCode}&phone=${phone}&companyName=${companyName}&email=${email}&defaultAddress=true"
		def con = testUtil.getSecureConnection("/customers/current/addresses", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		def response = testUtil.verifiedXMLSlurper(con,true)

		assert response.firstName == "${firstName}"
		assert response.lastName == "${lastName}"
		assert response.titleCode == "${titleCode}"
		assert response.line1 == "${line1}"
		assert response.town == "${town}"
		assert response.postalCode == "${postalCode}"
		assert response.country.isocode == "${plIsoCode}"
	}

	@Test
	void shouldSaveJapaneseAddress() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "firstName=${firstName}&lastName=${lastName}&line1=${line1}&line2=${line2}&town=${town}&country.isocode=${jpIsoCode}&postalCode=${postalCode}&region.isocode=${japanRegonCode}"
		def con = testUtil.getSecureConnection("/customers/current/addresses", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		def response = testUtil.verifiedXMLSlurper(con)

		assert response.firstName == "${firstName}"
		assert response.lastName == "${lastName}"
		assert response.line1 == "${line1}"
		assert response.line2 == "${line2}"
		assert response.town == "${town}"
		assert response.postalCode == "${postalCode}"
		assert response.country.isocode == "${jpIsoCode}"
		assert response.region.isocode == "${japanRegonCode}"
	}

	@Test
	void shouldSaveChineseAddress() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		def postBody = "titleCode=${title}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&town=${town}&country.isocode=${cnIsoCode}&postalCode=${postalCode}&region.isocode=${chinaRegonCode}"
		def con = testUtil.getSecureConnection("/customers/current/addresses", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		def response = testUtil.verifiedXMLSlurper(con)

		assert response.titleCode == "${titleCode}"
		assert response.firstName == "${firstName}"
		assert response.lastName == "${lastName}"
		assert response.line1 == "${line1}"
		assert response.town == "${town}"
		assert response.postalCode == "${postalCode}"
		assert response.country.isocode == "${cnIsoCode}"
		assert response.region.isocode == "${chinaRegonCode}"
	}

	@Test
	void shouldNotAllowToEditAddressDueToValidationError() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, password);

		//create address
		def createAddressPostBody = "titleCode=${title}&firstName=${firstName}&lastName=${lastName}&line1=${line1}&town=${town}&country.isocode=${plIsoCode}&postalCode=${postalCode}&phone=${phone}&companyName=${companyName}&email=${email}&defaultAddress=true"
		def createAddressCon = testUtil.getSecureConnection("/customers/current/addresses", 'POST', 'XML', HttpURLConnection.HTTP_OK, createAddressPostBody, null, access_token)

		def createAddressResponse = testUtil.verifiedXMLSlurper(createAddressCon,true)

		assert createAddressResponse.firstName == "${firstName}"
		assert createAddressResponse.lastName == "${lastName}"
		assert createAddressResponse.titleCode == "${titleCode}"
		assert createAddressResponse.line1 == "${line1}"
		assert createAddressResponse.town == "${town}"
		assert createAddressResponse.postalCode == "${postalCode}"
		assert createAddressResponse.country.isocode == "${plIsoCode}"

		def aid = createAddressResponse.id

		//edit address
		def editAddressPostBody = "country.isocode=JP"
		def editAddressCon = testUtil.getSecureConnection("/customers/current/addresses/" + aid, 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, editAddressPostBody, null, access_token)

		def error = editAddressCon.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.error.size()==2;
		assert response.error[0].type == 'ValidationError'
		assert response.error[0].subject == 'line2'
		assert response.error[0].subjectType == 'parameter'
		assert response.error[0].reason == 'missing'
		assert response.error[0].message == 'This field is required and must to be between 1 and 255 characters long.'
		assert response.error[1].type == 'ValidationError'
		assert response.error[1].subject == 'region.isocode'
		assert response.error[1].subjectType == 'parameter'
		assert response.error[1].reason == 'missing'
		assert response.error[1].message == 'This field is required and must to be between 1 and 7 characters long.'
	}
}
