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
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest

import org.junit.Test
import org.junit.experimental.categories.Category

@Category(CollectOutputFromTest.class)
@ManualTest
class MiscTests extends BaseWSTest {
	@Test
	void testInvalidResourceException() {
		def url =  config.DEFAULT_HTTPS_URI + config.BASE_PATH + "/wrongBaseSite"
		def con = testUtil.getSecureConnection(url, 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST)

		def error = con.errorStream.text;
		println error;
		def response = new XmlSlurper().parseText(error)
		assert response.error[0].type == 'InvalidResourceError'
		assert response.error[0].message == 'Base site wrongBaseSite doesn\'t exist'
	}

	@Test
	void testGetTitleCodesJSON() {
		def con = testUtil.getConnection("/titles", 'GET', 'JSON')

		def response = testUtil.verifiedJSONSlurper(con)
		assert response.titles.size() > 0
	}

	@Test
	void testGetTitleCodesXML() {
		def con = testUtil.getConnection("/titles", 'GET', 'XML')
		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name() == "titles" : 'Root element is not <titles>'
		assert response.title.size() > 0
	}

	@Test
	void testGetCardTypesJSON() {
		def con = testUtil.getConnection("/cardtypes", 'GET', 'JSON')
		def response = testUtil.verifiedJSONSlurper(con)
		def codes = ['maestro', 'switch', 'mastercard_eurocard', 'amex', 'diners', 'visa', 'master']
		assert response.cardTypes.size() == codes.size()
		assert response.cardTypes.findAll { card -> card.code in codes }.size() == codes.size()
	}

	@Test
	void testGetCardTypesXML() {
		def con = testUtil.getConnection("/cardtypes", 'GET', 'XML')
		def response = testUtil.verifiedXMLSlurper(con)
		def codes = ['maestro', 'switch', 'mastercard_eurocard', 'amex', 'diners', 'visa', 'master']
		assert response.name() == "cardTypes" : 'Root element is not <cardTypes>'
		assert response.cardType.size() == codes.size()
		assert response.cardType.findAll { card -> card.code in codes }.size() == codes.size()
	}

	@Test
	void testGetDeliveryCountriesJSON() {
		def con = testUtil.getConnection("/deliverycountries", 'GET', 'JSON')
		def response = testUtil.verifiedJSONSlurper(con)
		assert response.countries.size() > 0
	}

	@Test
	void testGetDeliveryCountriesXML() {
		def con = testUtil.getConnection("/deliverycountries", 'GET', 'XML')
		def response = testUtil.verifiedXMLSlurper(con);
		assert response.name() == "countries" : 'Root element is not <countries>'
		assert response.country.size() > 0
	}

	@Test
	void testGetCurrenciesJSON() {
		def con = testUtil.getConnection("/currencies", 'GET', 'JSON')
		def response = testUtil.verifiedJSONSlurper(con)
		def currencies = ['USD', 'JPY']
		assert response.currencies.size() == currencies.size()
		assert response.currencies.findAll { currency -> currency.isocode in currencies }.size() == currencies.size()
	}

	@Test
	void testGetCurrenciesXML() {
		def con = testUtil.getConnection("/currencies", 'GET', 'XML')
		def response = testUtil.verifiedXMLSlurper(con);
		def currencies = ['USD', 'JPY']
		assert response.name() == "currencies" : 'Root element is not <currencies>'
		assert response.currency.size() == currencies.size()
		assert response.currency.findAll { currency -> currency.isocode in currencies }.size() == currencies.size()
	}

	@Test
	void testGetLanguagesJSON() {
		def con = testUtil.getConnection("/languages", 'GET', 'JSON')
		def response = testUtil.verifiedJSONSlurper(con)
		def languages = ['ja', 'en', 'de', 'zh']
		assert response.languages.size() == languages.size()
		assert response.languages.findAll { language -> language.isocode in languages }.size() == languages.size()
	}

	@Test
	void testGetLanguagesXML() {
		def con = testUtil.getConnection("/languages", 'GET', 'XML')
		def response = testUtil.verifiedXMLSlurper(con)
		def languages = ['ja', 'en', 'de', 'zh']
		assert response.name() == 'languages'
		assert response.language.size() == languages.size()
		assert response.language.findAll { language -> language.isocode in languages }.size() == languages.size()
	}
}