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
import org.junit.experimental.categories.Category


@Category(CollectOutputFromTest.class)
@ManualTest
class MiscTests extends BaseWSTest {

	@Test
	void testInvalidResourceException() {
		def url = config.DEFAULT_HTTPS_URI + config.BASE_PATH + "/wrongBaseSite"
		def con = testUtil.getSecureConnection(url, 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST)

		def error = con.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'InvalidResourceError'
		assert response.errors[0].message == 'Base site wrongBaseSite doesn\'t exist'
	}

	@Test
	void testJSessionIdInV2CallsShouldNotBeReturned() {
		def con = testUtil.getSecureConnection("/titles", 'GET', 'JSON')
		def cookie = con.getHeaderField("Set-Cookie");
		if (cookie != null) {
			assert !cookie.contains("JSESSIONID");
		}
	}

	@Test
	void testGetTitleCodesJSON() {
		def con = testUtil.getSecureConnection("/titles", 'GET', 'JSON')

		def response = testUtil.verifiedJSONSlurper(con)
		assert response.titles
		assert response.titles.size() > 0
	}

	@Test
	void testGetTitleCodesXML() {
		def con = testUtil.getSecureConnection("/titles", 'GET', 'XML')
		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name() == "titleList": 'Root element is not <titles>'
		assert response.titles
		assert response.titles.size() > 0
	}

	@Test
	void testGetCardTypesJSON() {
		def con = testUtil.getSecureConnection("/cardtypes", 'GET', 'JSON')
		def response = testUtil.verifiedJSONSlurper(con)
		def codes = ['maestro', 'switch', 'mastercard_eurocard', 'amex', 'diners', 'visa', 'master']
		assert response.cardTypes
		assert response.cardTypes.size() == codes.size()
		assert response.cardTypes.findAll { card -> card.code in codes }.size() == codes.size()
	}

	@Test
	void testGetCardTypesXML() {
		def con = testUtil.getSecureConnection("/cardtypes", 'GET', 'XML')
		def response = testUtil.verifiedXMLSlurper(con)
		def codes = ['maestro', 'switch', 'mastercard_eurocard', 'amex', 'diners', 'visa', 'master']
		assert response.name() == "cardTypeList": 'Root element is not <cardTypes>'
		assert response.cardTypes
		assert response.cardTypes.size() == codes.size()
		assert response.cardTypes.findAll { card -> card.code in codes }.size() == codes.size()
	}

	@Test
	void testGetDeliveryCountriesJSON() {
		def con = testUtil.getSecureConnection("/deliverycountries", 'GET', 'JSON')
		def response = testUtil.verifiedJSONSlurper(con)
		assert response.countries
		assert response.countries.size() > 0
	}

	@Test
	void testGetDeliveryCountriesXML() {
		def con = testUtil.getSecureConnection("/deliverycountries", 'GET', 'XML')
		def response = testUtil.verifiedXMLSlurper(con);
		assert response.name() == "countryList": 'Root element is not <countries>'
		assert response.countries
		assert response.countries.size() > 0
	}

	@Test
	void testGetCurrenciesJSON() {
		def con = testUtil.getSecureConnection("/currencies", 'GET', 'JSON')
		def response = testUtil.verifiedJSONSlurper(con)
		def currencies = ['USD', 'JPY']
		assert response.currencies
		assert response.currencies.size() == currencies.size()
		assert response.currencies.findAll { currency -> currency.isocode in currencies }.size() == currencies.size()
	}

	@Test
	void testGetCurrenciesXML() {
		def con = testUtil.getSecureConnection("/currencies", 'GET', 'XML')
		def response = testUtil.verifiedXMLSlurper(con);
		def currencies = ['USD', 'JPY']
		assert response.name() == "currencyList": 'Root element is not <currencies>'
		assert response.currencies
		assert response.currencies.size() == currencies.size()
		assert response.currencies.findAll { currency -> currency.isocode in currencies }.size() == currencies.size()
	}

	@Test
	void testGetLanguagesJSON() {
		def con = testUtil.getSecureConnection("/languages", 'GET', 'JSON')
		def response = testUtil.verifiedJSONSlurper(con)
		def languages = ['ja', 'en', 'de', 'zh']
		assert response.languages
		assert response.languages.size() == languages.size()
		assert response.languages.findAll { language -> language.isocode in languages }.size() == languages.size()
	}

	@Test
	void testGetLanguagesXML() {
		def con = testUtil.getSecureConnection("/languages", 'GET', 'XML')
		def response = testUtil.verifiedXMLSlurper(con)
		def languages = ['ja', 'en', 'de', 'zh']
		assert response.name() == 'languageList'
		assert response.languages
		assert response.languages.size() == languages.size()
		assert response.languages.findAll { language -> language.isocode in languages }.size() == languages.size()
	}
}