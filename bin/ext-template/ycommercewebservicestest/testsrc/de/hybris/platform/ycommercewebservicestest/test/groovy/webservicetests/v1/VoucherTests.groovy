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

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.TestUtil
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.AvoidCollectingOutputFromTest

import org.junit.Test
import org.junit.experimental.categories.Category


@Category(AvoidCollectingOutputFromTest.class)
@ManualTest
class VoucherTests extends BaseWSTest {
	def PROMOTION_V_CODE = "abc";
	def PROMOTION_VOUCHER_CODE = "abc-9PSW-EDH2-RXKA";
	def PROMOTION_VOUCHER_DESC = "Promotion Voucher Description";
	def PROMOTION_VOUCHER_NAME = "New Promotional Voucher";
	def PROMOTION_VOUCHER_VALUE = 10.0d;
	def PROMOTION_VOUCHER_VALUE_STRING = "10.0%"

	def ABSOLUTE_V_CODE = "xyz";
	def ABSOLUTE_VOUCHER_CODE = "xyz-MHE2-B8L5-LPHE";
	def ABSOLUTE_VOUCHER_DESC = "Voucher Description";
	def ABSOLUTE_VOUCHER_NAME = "New Voucher";
	def ABSOLUTE_VOUCHER_VALUE = 15.0d;
	def ABSOLUTE_VOUCHER_VALUE_STRING = "15.0 USD"
	def USD="USD"

	def getVoucher(code) {
		def client_credentials_token = testUtil.getTrustedClientCredentialsToken()
		def con = testUtil.getSecureConnection("/vouchers/${code}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, client_credentials_token)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		return response
	}


	@Test
	void testGetVoucher() {
		def response = getVoucher(PROMOTION_VOUCHER_CODE)
		assert response.code == PROMOTION_V_CODE
		assert response.voucherCode == PROMOTION_VOUCHER_CODE
		assert response.description == PROMOTION_VOUCHER_DESC
		assert response.name == PROMOTION_VOUCHER_NAME
		assert response.value == PROMOTION_VOUCHER_VALUE
		assert response.valueString == PROMOTION_VOUCHER_VALUE_STRING
		assert response.valueFormatted == PROMOTION_VOUCHER_VALUE_STRING
		assert response.freeShipping == false
	}

	@Test
	void testGetVoucherAbsolute() {
		def response = getVoucher(ABSOLUTE_VOUCHER_CODE)
		assert response.code == ABSOLUTE_V_CODE
		assert response.voucherCode == ABSOLUTE_VOUCHER_CODE
		assert response.description == ABSOLUTE_VOUCHER_DESC
		assert response.name == ABSOLUTE_VOUCHER_NAME
		assert response.value == ABSOLUTE_VOUCHER_VALUE
		assert response.valueString == ABSOLUTE_VOUCHER_VALUE_STRING
		assert response.valueFormatted == ABSOLUTE_VOUCHER_VALUE_STRING
		assert response.freeShipping == true
		assert response.currency.isocode == USD
	}

	@Test
	void testGetNotExistingVoucher() {
		def client_credentials_token = testUtil.getTrustedClientCredentialsToken()
		def con = testUtil.getSecureConnection("/vouchers/notExistingVoucher", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, client_credentials_token)
		def error = con.errorStream.text
		def response = new XmlSlurper().parseText(error)
		assert response.error.type == 'VoucherOperationError'
		assert response.error.message == 'Voucher not found: notExistingVoucher'
	}
}