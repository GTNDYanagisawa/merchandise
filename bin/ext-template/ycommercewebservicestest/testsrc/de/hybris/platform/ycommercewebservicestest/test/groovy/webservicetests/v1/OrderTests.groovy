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
import org.junit.Test

@ManualTest
class OrderTests extends BaseWSTest{
	static final String USERNAME = "orderhistoryuser@test.com"
	static final String PASSWORD = "test"
	static final String ORDER_CODE = "testOrder1"

	@Test
	void testGetOrders() {
		def body, response, con, cookieNoPath
		def access_token = testUtil.getAccessToken(USERNAME, PASSWORD)

		con = testUtil.getSecureConnection('/orders', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.orders.size() == 13

		con = testUtil.getSecureConnection('/orders?pageSize=5', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.orders.size() == 5
	}

	@Test
	void testGetOrdersWithStatuses() {
		def body, response, con, cookieNoPath
		def access_token = testUtil.getAccessToken(USERNAME, PASSWORD)

		con = testUtil.getSecureConnection('/orders?statuses=CREATED,CANCELLED', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.orders.size() == 13

		con = testUtil.getSecureConnection('/orders?statuses=CREATED', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.orders.size() == 10

		con = testUtil.getSecureConnection('/orders?statuses=CREATED&pageSize=5', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.orders.size() == 5
	}

	@Test
	void testGetOrdersWithWrongStatus() {
		def access_token = testUtil.getAccessToken(USERNAME, PASSWORD)

		def con = testUtil.getSecureConnection('/orders?statuses=THIS_IS_WRONG', 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
		def error = con.errorStream.text
		def response = new XmlSlurper().parseText(error)
		// TODO: this exception should be wrapped into some commercelayer exception - defect reported
		assert response.error.type == "IllegalStateError"
		assert response.error.message == "missing persistent item for enum value THIS_IS_WRONG"
	}

	@Test
	void testGetOrderByCode() {
		def access_token = testUtil.getAccessToken(USERNAME, PASSWORD)

		def con = testUtil.getSecureConnection('/orders/' + ORDER_CODE, 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con, true, false)
		assert response.store == "wsTest"
		assert response.appliedOrderPromotions != null
		//assert response.pickupItemsQuantity == 0 // - attribute from acceleratorfacades
		assert response.net == false
		assert response.appliedVouchers.size() == 0
		assert response.totalDiscounts != null
		assert response.productDiscounts != null
		assert response.created != null
		assert response.subTotal != null
		assert response.orderDiscounts != null
		assert response.entries.size() == 2
		assert response.appliedProductPromotions.size() == 0
		assert response.totalPrice != null
		assert response.site == "wsTest"
		//assert response.unconsignedEntries.size() == 2
		assert response.status.code == "CREATED"
		assert response.status.codeLowerCase == "created"
		assert response.statusDisplay.toLowerCase() == "created"
		assert response.deliveryMode != null
		//assert response.consignments != null
		assert response.code == ORDER_CODE
		//assert response.deliveryOrderGroups != null
		//assert response.pickupOrderGroups != null
		assert response.totalItems == 2
		assert response.totalPriceWithTax != null
		assert response.guestCustomer == false
		//assert response.deliveryItemsQuantity == 7
		assert response.totalTax != null
		assert response.user.uid == USERNAME
		assert response.user.name == "orders test user"
		assert response.deliveryCost != null
	}

	@Test
	void testGetOrderByWrongCode() {
		def access_token = testUtil.getAccessToken(USERNAME, PASSWORD)

		def con = testUtil.getSecureConnection('/orders/THIS_IS_WRONG', 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error);
		assert response.error.type == "UnknownIdentifierError"
		assert response.error.message == "No result for the given query"
	}
}
