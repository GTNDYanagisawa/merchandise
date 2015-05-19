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
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.AvoidCollectingOutputFromTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest

import org.junit.Test
import org.junit.experimental.categories.Category

import groovy.json.JsonSlurper

@Category(CollectOutputFromTest.class)
@ManualTest
class CartTests extends BaseWSTest {

	static final PASSWORD = "test"
	static final PROMOTION_CODE = "WS_OrderThreshold15Discount"
	static final RESTRICTED_PROMOTION_CODE = "WS_RestrictedOrderThreshold15Discount"
	static final PROMOTION_VOUCHER_CODE = "abc-9PSW-EDH2-RXKA";
	static final RESTRICTED_PROMOTION_VOUCHER_CODE = "abr-D7S5-K14A-51Y5"
	static final ABSOLUTE_VOUCHER_CODE = "xyz-MHE2-B8L5-LPHE";
	static final NOT_EXISTING_VOUCHER_CODE = "notExistingVoucher";
	static final RESTRICTED_PROMOTION_TYPE = 'Order threshold fixed discount'
	static final RESTRICTED_PROMOTION_FIRED_MESSAGE = 'You saved $20.00 for spending over $200.00'
	static final RESTRICTED_PROMOTION_COULD_FIRE_MESSAGE = 'Spend $200.00 to get a discount of $20.00 - Spend another $200.00 to qualify'
	static final RESTRICTED_PROMOTION_DESCRIPTION = 'You saved bunch of bucks for spending quite much'
	static final RESTRICTED_PROMOTION_END_DATE = '2099-01-01T00:00:00'
	static final STORE_NAME = "WS-Shinbashi"

	final @Test
	void testGetCartJSON() {
		def con = testUtil.getConnection("/cart", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con)
		assert response.totalItems == 0
		assert response.totalUnitCount == 0
		assert response.net == false
		assert response.calculated == false
		assert response.totalPrice.currencyIso == 'USD'
		assert response.totalPrice.priceType == 'BUY'
		assert response.totalPrice.value == 0
		assert response.totalPrice.formattedValue == '$0.00'
		assert response.subTotal.currencyIso == 'USD'
		assert response.subTotal.priceType == 'BUY'
		assert response.subTotal.value == 0
		assert response.subTotal.formattedValue == '$0.00'
	}

	@Test
	void testGetCartXML() {
		def con = testUtil.getConnection("/cart", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name() == 'cart'
		assert response.totalItems == 0
		assert response.totalUnitCount == 0
		assert response.net == 'false'
		assert response.calculated == false
		assert response.totalPrice.currencyIso == 'USD'
		assert response.totalPrice.priceType == 'BUY'
		assert response.totalPrice.value == 0
		assert response.totalPrice.formattedValue == '$0.00'
		assert response.subTotal.currencyIso == 'USD'
		assert response.subTotal.priceType == 'BUY'
		assert response.subTotal.value == 0
		assert response.subTotal.formattedValue == '$0.00'
	}

	@Test
	//@Category(AvoidCollectingOutputFromTest.class)
	void testGetCartAfterCurrencyChangeXML() {
		def con, body, response, cookieNoPath

		//create customer and cart
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = customerTests.createAddress(access_token)

		//add something to a cart
		cookieNoPath = addToCart(3429337)
		//println cookieNoPath

		con = testUtil.getConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)

		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)

		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 11.12
		assert response.totalPrice.formattedValue == '$11.12'
		assert response.totalPrice.currencyIso == 'USD'
		assert response.totalPrice.priceType == 'BUY'

		//=============================

		con = testUtil.getConnection('/cart?curr=JPY', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)

		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)

		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 940.0
		assert response.totalPrice.formattedValue == '¥940'
		assert response.totalPrice.currencyIso == 'JPY'

		con = testUtil.getConnection('/cart?curr=USD', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)

		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)

		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 11.12
		assert response.totalPrice.formattedValue == '$11.12'
		assert response.totalPrice.currencyIso == 'USD'
	}

	@Test
	void testAddToCart() {
		def con, response, cookieNoPath

		// create anonymous cart
		cookieNoPath = addToCart(3429337)

		// get the cart
		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.store == 'wsTest'
		assert response.appliedOrderPromotions != null
		assert response.net == false
		assert response.calculated == true
		assert response.appliedVouchers != null
		assert response.productDiscounts.currencyIso == 'USD'
		assert response.productDiscounts.priceType == 'BUY'
		assert response.productDiscounts.value == 0.0
		assert response.productDiscounts.formattedValue == '$0.00'
		assert response.totalDiscounts.currencyIso == 'USD'
		assert response.totalDiscounts.priceType == 'BUY'
		assert response.totalDiscounts.value == 0.0
		assert response.totalDiscounts.formattedValue == '$0.00'
		assert response.subTotal.currencyIso == 'USD'
		assert response.subTotal.priceType == 'BUY'
		assert response.subTotal.value == 11.12
		assert response.subTotal.formattedValue == '$11.12'
		assert response.orderDiscounts.currencyIso == 'USD'
		assert response.orderDiscounts.priceType == 'BUY'
		assert response.orderDiscounts.value == 0.0
		assert response.orderDiscounts.formattedValue == '$0.00'
		assert response.entries.product[0].code == '3429337'
		assert response.entries[0].entryNumber == 0
		assert response.entries[0].quantity == 1
		assert response.appliedProductPromotions != null
		assert response.totalPrice.currencyIso == 'USD'
		assert response.totalPrice.priceType == 'BUY'
		assert response.totalPrice.value == 11.12
		assert response.totalPrice.formattedValue == '$11.12'
		assert response.site == 'wsTest'
		assert response.code
		assert response.guid
		assert response.totalItems == 1
		assert response.totalPriceWithTax.currencyIso == 'USD'
		assert response.totalPriceWithTax.priceType == 'BUY'
		assert response.totalPriceWithTax.value == 11.12
		assert response.totalPriceWithTax.formattedValue == '$11.12'
		assert response.totalTax.currencyIso == 'USD'
		assert response.totalTax.priceType == 'BUY'
		assert response.totalTax.value == 0.0
		assert response.totalTax.formattedValue == '$0.00'
		assert response.potentialProductPromotions != null
		assert response.potentialOrderPromotions != null
		assert response.totalUnitCount == 1
	}

	@Test
	void testAddPickupItemOutOfStockOnlineToCart() {
		def cookieNoPath, con, response
		def access_token = testUtil.getClientCredentialsToken();

		// add pickup item which is out of stock in the online store
		cookieNoPath = addToCart(2006139, 1, null, access_token, STORE_NAME)

		// get the cart
		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.entries[0].product.availableForPickup == true
		assert response.entries[0].deliveryPointOfService.name == STORE_NAME
	}

	@Test
	void testAddPickupItemToCartWithWrongStoreNameFail() {
		def con, response
		def access_token = testUtil.getClientCredentialsToken();

		// add item which is out of stock in the online store
		def postBody = "code=2006139&qty=1&storeName=WrongStoreName"
		con = testUtil.getSecureConnection('/cart/entry', 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)
		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error);
		assert response.error.type == 'ValidationError'
		assert response.error.subjectType == 'parameter'
		assert response.error.subject == 'storeName'
		assert response.error.reason == 'invalid'
		assert response.error.message == "Store with given name doesn't exist"
	}

	@Test
	void testFailAddPickupItemOutOfStockToCart() {
		def cookieNoPath, con, response
		def access_token = testUtil.getClientCredentialsToken();

		// add pickup item which is out of stock in the STORE_NAME
		def postBody = "code=816780&qty=1&storeName=${STORE_NAME}"
		con = testUtil.getSecureConnection('/cart/entry', 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)
		def error = con.errorStream.text
		response = new XmlSlurper().parseText(error)
		assert response.error.type == 'InsufficientStockError'
		assert response.error.subjectType == 'product'
		assert response.error.subject == '816780'
		assert response.error.reason == 'noStock'
		assert response.error.message == 'Product is currently out of stock'
	}

	@Test
	void testUpdateCartEntry() {
		def con, body, response, cookieNoPath

		// create anonymous cart
		cookieNoPath = addToCart(3429337)

		// get the cart
		con = testUtil.getConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 11.12

		// update cart entry
		con = testUtil.getConnection('/cart/entry/0?qty=3', 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.name() == 'cartModification'
		assert response.statusCode == 'success'
		assert response.entry[0].product.code == '3429337'
		assert response.entry[0].entryNumber == 0
		assert response.entry[0].updateable == true
		assert response.entry[0].quantity == 3
		assert response.entry[0].basePrice.currencyIso == 'USD'
		assert response.entry[0].basePrice.priceType == 'BUY'
		assert response.entry[0].basePrice.value == 11.12
		assert response.entry[0].basePrice.formattedValue == '$11.12'
		assert response.entry[0].totalPrice.currencyIso == 'USD'
		assert response.entry[0].totalPrice.priceType == 'BUY'
		assert response.entry[0].totalPrice.value == 33.36
		assert response.entry[0].totalPrice.formattedValue == '$33.36'
		assert response.quantity == 3
		assert response.quantityAdded == 2

		// get updated cart
		con = testUtil.getConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 33.36
	}

	@Test
	void testRemoveCartEntry() {
		def con, response, cookieNoPath

		// create anonymous cart
		cookieNoPath = addToCart(3429337, 2)
		cookieNoPath = addToCart(1225694, 1, cookieNoPath)

		// get the cart
		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 869.98

		// remove cart entry
		con = testUtil.getConnection('/cart/entry/1', 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.statusCode == 'success'
		assert response.quantity == 0
		assert response.quantityAdded == -1

		// check the cart
		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 22.24
	}

	@Test
	void testPickupInStoreMode() {
		def con, response, cookieNoPath
		def productCode = 1934793

		// create anonymous cart
		cookieNoPath = addToCart(productCode)

		// set cart entry as pickup in store
		con = testUtil.getConnection('/cart/entry/0/store?storeName=WS-Nakano', 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		def body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.name() == 'cartModification'
		assert response.statusCode == 'success'
		assert response.entry[0].product.code == productCode
		assert response.entry[0].deliveryPointOfService.name == 'WS-Nakano'

		// get updated cart
		con = testUtil.getConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.pickupItemsQuantity == 1
		assert response.deliveryItemsQuantity == 0
		assert response.entries[0].orderEntry[0].deliveryPointOfService.name == 'WS-Nakano'
		assert response.pickupOrderGroups.pickupOrderEntryGroup.entries[0].orderEntry[0].deliveryPointOfService.name == 'WS-Nakano'

		//set cart entry as shipping
		con = testUtil.getConnection('/cart/entry/0/store', 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.name() == 'cartModification'
		assert response.statusCode == 'success'
		assert response.entry[0].product.code == productCode

		// get updated cart
		con = testUtil.getConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.pickupItemsQuantity == 0
		assert response.deliveryItemsQuantity == 1
	}

	@Test
	void testPickupInStoreWithWrongStoreName() {
		def con, response, cookieNoPath

		// create anonymous cart
		cookieNoPath = addToCart(3429337)

		// set cart entry as pickup in store
		con = testUtil.getConnection('/cart/entry/0/store?storeName=wrongStoreName', 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath)
		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error)
		assert response.error.type == 'ValidationError'
		assert response.error.subject == 'storeName'
		assert response.error.subjectType == 'parameter'
		assert response.error.reason == 'invalid'
		assert response.error.message == "Store with given name doesn't exist"
	}

	@Test
	void testPickupInStoreWithWrongEntryNumber() {
		def con, response, cookieNoPath

		// create anonymous cart
		cookieNoPath = addToCart(3429337)

		// set cart entry as pickup in store
		con = testUtil.getConnection('/cart/entry/1/store?storeName=WS-Nakano', 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath)
		def error = con.errorStream.text
		response = new XmlSlurper().parseText(error)
		assert response.error.type == 'CommerceCartModificationError'
		assert response.error.message == "Unknown entry number"
	}

	@Test
	void testDeletePickupInStoreForWrongEntry() {
		def con, response, cookieNoPath

		// create anonymous cart
		cookieNoPath = addToCart(1934793)

		// try to set cart entry as shipping  when it is already in shipping mode
		con = testUtil.getConnection('/cart/entry/0/store', 'DELETE', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath)

		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error);
		assert response.error.type == 'CommerceCartModificationError'
		assert response.error.message == "Entry is already in shipping mode"
	}

	@Test
	void testDeletePickupInStoreWithWrongEntryNumber() {
		def con, response, cookieNoPath

		// create anonymous cart
		cookieNoPath = addToCart(3429337)

		//remove pickup in store mode
		con = testUtil.getConnection('/cart/entry/1/store', 'DELETE', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath)
		response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.error.type == 'CommerceCartModificationError'
		assert response.error.message == "Unknown entry number"
	}

	@Test
	void testDeletePickupInStoreForItemOutOfStockOnline() {
		def con, response, cookieNoPath

		// add item which is out of stock in the online store
		cookieNoPath = addToCart(2006139, 1, null, null, STORE_NAME)

		//set cart entry as shipping
		con = testUtil.getConnection('/cart/entry/0/store', 'DELETE', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath)
		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error);
		assert response.error.type == 'InsufficientStockError'
		assert response.error.subjectType == 'entry'
		assert response.error.subject == '0'
		assert response.error.reason == 'noStock'
		assert response.error.message == 'Product [2006139] cannot be shipped - out of stock online'
	}

	@Test
	void testFailPickupInStoreWhenOutOfStock() {
		def con, response, cookieNoPath

		// add item which is out of stock in the STORE_NAME
		cookieNoPath = addToCart(1934795, 1, null, null, null)

		// try to pickup in this store
		con = testUtil.getConnection("/cart/entry/0/store?storeName=${STORE_NAME}", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, null)
		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error);
		assert response.error.type == 'InsufficientStockError'
		assert response.error.subjectType == 'entry'
		assert response.error.subject == '0'
		assert response.error.reason == 'noStock'
		assert response.error.message == 'Product [1934795] is currently out of stock'
	}

	@Test
	void testClientCartRestorationByGuid() {
		def con, body, response, cookie, cookieNoPath

		// create anonymous cart
		cookieNoPath = addToCart(3429337, 2)

		// get the cart and save guid
		con = testUtil.getConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.guid: "No guid present, cannot restore cart"
		def guid = response.guid

		def access_token = testUtil.getClientCredentialsToken()

		// restore cart
		con = testUtil.getSecureConnection("/cart/restore?guid=${guid}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		// check for cookie, extract it for next request
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		// check cart restoration
		con = testUtil.getConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
	}

	@Test
	void testAutomaticallyCartRestoration() {
		def con, body, response, cookieNoPath

		//create customer and cart
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		cookieNoPath = addToCart(3429337, 2, null, access_token)

		//logout
		con = testUtil.getSecureConnection(config.DEFAULT_HTTPS_URI + config.BASE_PATH + "/customers/current/logout", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.logout.success

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
	}

	@Test
	void testDisabledAutomaticallyCartRestoration() {
		def con, body, response, cookieNoPath

		//create customer and cart
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		cookieNoPath = addToCart(3429337, 2, null, access_token)

		//logout
		con = testUtil.getSecureConnection(config.DEFAULT_HTTPS_URI + config.BASE_PATH + "/customers/current/logout", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.logout.success

		con = testUtil.getSecureConnection('/cart?restore=false', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)

		assert response.totalItems == 0
		assert response.totalUnitCount == 0
	}

	@Test
	void testEnableOrderPromotionByClient() {
		def con, body, response, cookie, cookieNoPath
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken();

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trusted_client_access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		cookie = con.getHeaderField('Set-Cookie')
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		addToCart(1225694, 1, cookieNoPath, trusted_client_access_token)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, trusted_client_access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, trusted_client_access_token)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.promotionResult[0].promotion.code == RESTRICTED_PROMOTION_CODE
		assert response.appliedOrderPromotions.promotionResult[0].promotion.firedMessages.string == RESTRICTED_PROMOTION_FIRED_MESSAGE
		assert response.appliedOrderPromotions.promotionResult[0].promotion.promotionType == RESTRICTED_PROMOTION_TYPE
		assert response.appliedOrderPromotions.promotionResult[0].promotion.description == RESTRICTED_PROMOTION_DESCRIPTION
		assert response.appliedOrderPromotions.promotionResult[0].promotion.endDate.toString().startsWith(RESTRICTED_PROMOTION_END_DATE)
		assert response.appliedOrderPromotions.promotionResult[0].description == RESTRICTED_PROMOTION_FIRED_MESSAGE
	}


	@Test
	void testDisableOrderPromotionByClient() {
		def con, body, response, cookie, cookieNoPath
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken()

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trusted_client_access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		cookie = con.getHeaderField('Set-Cookie')
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		addToCart(1225694, 1, cookieNoPath, trusted_client_access_token)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, trusted_client_access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, trusted_client_access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 1

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, trusted_client_access_token)
		body = con.inputStream.text
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0
	}

	@Test
	void testEnablePotentialOrderPromotionByClient() {
		def con, body, response, cookie, cookieNoPath
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken()

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trusted_client_access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, trusted_client_access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.promotionResult[0].promotion.code == RESTRICTED_PROMOTION_CODE
		assert response.potentialOrderPromotions.promotionResult[0].promotion.promotionType == RESTRICTED_PROMOTION_TYPE
		assert response.potentialOrderPromotions.promotionResult[0].promotion.description == RESTRICTED_PROMOTION_DESCRIPTION
		assert response.potentialOrderPromotions.promotionResult[0].promotion.endDate.toString().startsWith(RESTRICTED_PROMOTION_END_DATE)
		assert response.potentialOrderPromotions.promotionResult[0].promotion.couldFireMessages.string == RESTRICTED_PROMOTION_COULD_FIRE_MESSAGE
		assert response.potentialOrderPromotions.promotionResult[0].description == RESTRICTED_PROMOTION_COULD_FIRE_MESSAGE
	}

	@Test
	void testDisablePotentialOrderPromotionByClient() {
		def con, body, response, cookie, cookieNoPath
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken()

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trusted_client_access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, trusted_client_access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 1

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, trusted_client_access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0
	}

	@Test
	void testEnableOrderPromotionByCustomer() {
		def con, body, response, cookie, cookieNoPath
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD, config.TRUSTED_CLIENT_ID, config.TRUSTED_CLIENT_SECRET)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		addToCart(1225694, 1, cookieNoPath, access_token)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.promotionResult[0].promotion.code == RESTRICTED_PROMOTION_CODE
		assert response.appliedOrderPromotions.promotionResult[0].promotion.firedMessages.string == RESTRICTED_PROMOTION_FIRED_MESSAGE
		assert response.appliedOrderPromotions.promotionResult[0].promotion.promotionType == RESTRICTED_PROMOTION_TYPE
		assert response.appliedOrderPromotions.promotionResult[0].promotion.description == RESTRICTED_PROMOTION_DESCRIPTION
		assert response.appliedOrderPromotions.promotionResult[0].promotion.endDate.toString().startsWith(RESTRICTED_PROMOTION_END_DATE)
		assert response.appliedOrderPromotions.promotionResult[0].description == RESTRICTED_PROMOTION_FIRED_MESSAGE
	}

	@Test
	void testDisableOrderPromotionByCustomer() {
		def con, body, response, cookie, cookieNoPath
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD, config.TRUSTED_CLIENT_ID, config.TRUSTED_CLIENT_SECRET)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		addToCart(1225694, 1, cookieNoPath, access_token)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 1

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0
	}

	@Test
	void testEnableOrderPromotionByCustomerWithNonTrustedClientShouldFail() {
		def con, body, response, cookie, cookieNoPath
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD, config.CLIENT_ID, config.CLIENT_SECRET)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		addToCart(1225694, 1, cookieNoPath, access_token)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_FORBIDDEN, null, cookieNoPath, access_token)
	}


	@Test
	void testEnablePotentialOrderPromotionByCustomer() {
		def con, body, response, cookie, cookieNoPath
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD, config.TRUSTED_CLIENT_ID, config.TRUSTED_CLIENT_SECRET)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.promotionResult[0].promotion.code == RESTRICTED_PROMOTION_CODE
		assert response.potentialOrderPromotions.promotionResult[0].promotion.promotionType == RESTRICTED_PROMOTION_TYPE
		assert response.potentialOrderPromotions.promotionResult[0].promotion.description == RESTRICTED_PROMOTION_DESCRIPTION
		assert response.potentialOrderPromotions.promotionResult[0].promotion.endDate.toString().startsWith(RESTRICTED_PROMOTION_END_DATE)
		assert response.potentialOrderPromotions.promotionResult[0].promotion.couldFireMessages.string == RESTRICTED_PROMOTION_COULD_FIRE_MESSAGE
		assert response.potentialOrderPromotions.promotionResult[0].description == RESTRICTED_PROMOTION_COULD_FIRE_MESSAGE
	}

	@Test
	void testDisablePotentialOrderPromotionByCustomer() {
		def con, body, response, cookie, cookieNoPath
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD, config.TRUSTED_CLIENT_ID, config.TRUSTED_CLIENT_SECRET)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 1

		con = testUtil.getSecureConnection("/cart/promotion/${RESTRICTED_PROMOTION_CODE}", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.promotionResult.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0
	}

	@Test
	void testEnableUnestrictedPromotion() {
		def con, body, response, cookie, cookieNoPath
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD, config.TRUSTED_CLIENT_ID, config.TRUSTED_CLIENT_SECRET)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		con = testUtil.getSecureConnection("/cart/promotion/${PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, access_token)
		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error)
		assert response.error.type == 'CommercePromotionRestrictionError'
	}

	@Test
	void testDisableUnestrictedPromotion() {
		def con, body, response, cookie, cookieNoPath
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD, config.TRUSTED_CLIENT_ID, config.TRUSTED_CLIENT_SECRET)

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie: 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		con = testUtil.getSecureConnection("/cart/promotion/${PROMOTION_CODE}", 'DELETE', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, access_token)
		def error = con.errorStream.text
		response = new XmlSlurper().parseText(error)
		assert response.error.type == 'CommercePromotionRestrictionError'
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testCartCreatePaymentInfo() {
		def con, response, postBody, cookieNoPath

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = customerTests.createAddressJSON(access_token)

		//put something in a new cart
		cookieNoPath = addToCart(1934795, 2, null, access_token)

		//set the delivery address
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryAddress.id
		assert response.deliveryAddress.firstName == 'Sven'
		assert response.deliveryAddress.lastName == 'Haiges'
		assert response.deliveryAddress.titleCode == 'dr'
		assert response.deliveryAddress.title == 'Dr.'
		assert response.deliveryAddress.postalCode == '80331'
		assert response.deliveryAddress.town == 'Muenchen'
		assert response.deliveryAddress.line1 == 'Nymphenburger Str. 86 - Maillingerstrasse'
		assert response.deliveryAddress.country.name == 'Germany'
		assert response.deliveryAddress.country.isocode == 'DE'
		assert response.deliveryAddress.formattedAddress == 'Nymphenburger Str. 86 - Maillingerstrasse, 80331, Muenchen, Germany'

		//set a delivery mode
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'standard-gross', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryCost.currencyIso == 'USD'
		assert response.deliveryCost.priceType == 'BUY'
		assert response.deliveryCost.value == 8.99
		assert response.deliveryCost.formattedValue == '$8.99'

		//create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.paymentInfo.id
		assert response.paymentInfo.subscriptionId == 'MockedSubscriptionID'
		assert response.paymentInfo.saved == true
		assert response.paymentInfo.expiryMonth == '01'
		assert response.paymentInfo.expiryYear == '2113'
		assert response.paymentInfo.cardType.name == 'Visa'
		assert response.paymentInfo.cardType.code == 'visa'
		assert response.paymentInfo.accountHolderName == 'Sven Haiges'
		assert response.paymentInfo.cardNumber == '************1111'
		assert response.paymentInfo.billingAddress.id
		assert response.paymentInfo.billingAddress.lastName == 'haiges'
		assert response.paymentInfo.billingAddress.firstName == 'sven'
		assert response.paymentInfo.billingAddress.titleCode == 'mr'
		assert response.paymentInfo.billingAddress.title == "Mr"
		assert response.paymentInfo.billingAddress.country.name == 'Germany'
		assert response.paymentInfo.billingAddress.country.isocode == 'DE'
		assert response.paymentInfo.billingAddress.postalCode == '12345'
		assert response.paymentInfo.billingAddress.email == uid
		assert response.paymentInfo.billingAddress.formattedAddress == 'test1, test2, 12345, somecity, Germany'
		assert response.paymentInfo.billingAddress.town == 'somecity'
		assert response.paymentInfo.billingAddress.line1 == 'test1'
		assert response.paymentInfo.billingAddress.line2 == 'test2'
	}

	@Test
	void testCreateFirstPayment() {
		def con, response, postBody, cookieNoPath

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		//put something in a new cart
		cookieNoPath = addToCart(1934795, 2, null, access_token)

		//create a paymentinfo for this cart
		def saved = true
		def defaultPaymentInfo = false
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=$saved&defaultPaymentInfo=$defaultPaymentInfo&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);

		def paymentInfoId = response.paymentInfo.id;

		con = testUtil.getSecureConnection("/customers/current/paymentinfos/${paymentInfoId}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);

		assert response.defaultPaymentInfo == true // first payment is always default
	}

	@Test
	void testCreateDefaultPaymentWithoutSavingIt() {
		def con, response, postBody, cookieNoPath

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		//put something in a new cart
		cookieNoPath = addToCart(1934795, 2, null, access_token)

		//create a paymentinfo for this cart
		def saved = false
		def defaultPaymentInfo = true
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=$saved&defaultPaymentInfo=$defaultPaymentInfo&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		def paymentInfoId = response.paymentInfo.id;

		con = testUtil.getSecureConnection("/customers/current/paymentinfos/${paymentInfoId}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.defaultPaymentInfo == false // payment cannot be default if it's not saved
	}

	@Test
	void testRemoveDeliveryAddress() {
		def con, response, cookieNoPath
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = customerTests.createAddressJSON(access_token)

		// put something in a new cart
		cookieNoPath = addToCart(1934795, 1, null, access_token)

		// add delivery address
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryAddress != null

		// delete delivery address
		con = testUtil.getSecureConnection('/cart/address/delivery', 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryAddress == null
	}

	@Test
	void testRemoveDeliveryMode() {
		def con, response, cookieNoPath
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = customerTests.createAddressJSON(access_token)

		// put something in a new cart
		cookieNoPath = addToCart(1934795, 1, null, access_token)

		// add delivery address
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryAddress != null

		// set delivery mode
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'standard-gross', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryCost != null

		// delete delivery address
		con = testUtil.getSecureConnection('/cart/deliverymodes', 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryCost == null
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testCreatePaymentInfoWithoutCart() {
		def con, response, postBody, cookieNoPath

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postcode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, cookieNoPath, access_token)
		def error = con.errorStream.text;
		println error
		response = new XmlSlurper().parseText(error)
		assert response.error.type == 'NoCheckoutCartError'
		assert response.error[0].message == 'Cannot add PaymentInfo. There was no checkout cart created yet!'
	}

	@Test
	void testPlaceOrderWithoutCart() {
		def con, response, cookieNoPath

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		con = testUtil.getSecureConnection('/cart/placeorder', 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, access_token)
		def error = con.errorStream.text
		response = new XmlSlurper().parseText(error)
		println error
		assert response.error[0].type == 'NoCheckoutCartError'
		assert response.error[0].message == 'Cannot place order. There was no checkout cart created yet!'
	}

	@Test
	void testInvalidPlaceOrder() {
		def con, response, cookieNoPath

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		// put something in a new cart'
		cookieNoPath = addToCart(1934795, 1, null, access_token)

		con = testUtil.getSecureConnection('/cart/placeorder', 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, access_token)
		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error);
		assert response.error[0].type == 'ValidationError'
		assert response.error[0].subject == 'sessionCart'
		assert response.error[0].subjectType == 'parameter'
		assert response.error[0].reason == 'invalid'
		assert response.error[0].message == 'Delivery mode is not set'

		assert response.error[1].type == 'ValidationError'
		assert response.error[1].subject == 'sessionCart'
		assert response.error[1].subjectType == 'parameter'
		assert response.error[1].reason == 'invalid'
		assert response.error[1].message == 'Payment info is not set'
	}

	@Test
	void testAuthorizedValidPlaceOrder() {
		def con, response, cookieNoPath, postBody

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = customerTests.createAddressJSON(access_token)

		// put something in a new cart'
		cookieNoPath = addToCart(1934795, 1, null, access_token)

		// set the delivery address
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryAddress != null

		// set a delivery mode
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'standard-gross', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryCost != null

		// create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.paymentInfo != null

		postBody = 'securityCode=123'
		con = testUtil.getSecureConnection('/cart/authorize', 'POST', 'JSON', HttpURLConnection.HTTP_ACCEPTED, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);

		con = testUtil.getSecureConnection('/cart/placeorder', 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == false
	}

	@Test
	void testPlaceOrderForPickupInStore() {
		def con, response, cookieNoPath, postBody

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = customerTests.createAddressJSON(access_token)
		def String productId = "1934793"

		// put something in a new cart'
		cookieNoPath = addToCart(productId, 1, null, access_token, STORE_NAME)

		// set a delivery mode as pickup
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'pickup', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryMode.code == 'pickup'

		// create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.paymentInfo != null

		postBody = 'securityCode=123'
		con = testUtil.getSecureConnection('/cart/authorize', 'POST', 'JSON', HttpURLConnection.HTTP_ACCEPTED, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);

		con = testUtil.getSecureConnection('/cart/placeorder', 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, true, false);
		assert response.created
		assert response.guestCustomer == false

	}

	@Test
	void testPlaceOrderWithProductOutOfStock() {
		def con, response, cookieNoPath, postBody

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = customerTests.createAddressJSON(access_token)
		def String productId = "1687508"

		// put something in a new cart'
		cookieNoPath = addToCart(productId, 1, null, access_token)

		// set the delivery address
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryAddress != null

		// set a delivery mode
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'standard-gross', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryCost != null

		// create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.paymentInfo != null

		postBody = 'securityCode=123'
		con = testUtil.getSecureConnection('/cart/authorize', 'POST', 'JSON', HttpURLConnection.HTTP_ACCEPTED, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);

		try {
			setStockStatus("forceOutOfStock", productId)
			con = testUtil.getSecureConnection('/cart/placeorder', 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, access_token)
			String error = con.errorStream.text;
			response = new JsonSlurper().parseText(error)
			assert response.errors[0].type == 'InsufficientStockError'
			assert response.errors[0].reason == 'noStock'
			assert response.errors[0].subjectType == 'entry'
			assert response.errors[0].subject == '0'
			assert response.errors[0].message == "Product [${productId}] is currently out of stock"
		}
		finally {
			setStockStatus("notSpecified", productId)
		}
	}

	/**
	 * Method sets stock status for selected product using platformwebservices
	 */
	void setStockStatus(String status, String productId) {
		def con = testUtil.getSecureConnection(config.DEFAULT_HTTPS_URI + "/ws410/rest/catalogs/wsTestProductCatalog/catalogversions/Online/products/${productId}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, "admin:nimda")
		assert con.responseCode == HttpURLConnection.HTTP_OK
		def response = new XmlSlurper().parseText(con.inputStream.text)

		String stoclLevelUri = response.stockLevels.stockLevel[0].@'uri';
		//println stoclLevelUri
		//con = testUtil.getSecureConnection(stoclLevelUri,'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, "admin:nimda")
		//println con.inputStream.text;

		String body = "<stocklevel><inStockStatus>${status}</inStockStatus></stocklevel>"
		con = testUtil.getSecureConnection(stoclLevelUri, 'PUT', 'XML', HttpURLConnection.HTTP_OK, body, null, "admin:nimda", "application/xml")

		//con = testUtil.getSecureConnection(stoclLevelUri,'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, "admin:nimda")
		//println con.inputStream.text;
	}

	@Test
	void testGetSupportedDeliveryModes() {
		def con, response, cookieNoPath

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = customerTests.createAddressJSON(access_token)

		// put something in a new cart'
		cookieNoPath = addToCart(1934795, 1, null, access_token)

		// set the delivery address
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryAddress != null

		// get supported delivery modes
		con = testUtil.getSecureConnection('/cart/deliverymodes', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, true, false);
		assert response.deliveryModes.size() == 2

		def standardDeliveryMode = response.deliveryModes.find { it.code == 'standard-gross' };
		assert standardDeliveryMode.code == 'standard-gross'
		assert standardDeliveryMode.deliveryCost.value == 8.99
		assert standardDeliveryMode.deliveryCost.formattedValue == '$8.99'
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyVoucherForCartByCustomer() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		//put something in a new cart'
		def cookieNoPath = addToCart(1934795, 2, null, access_token)

		def con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		def response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		con = testUtil.getSecureConnection("/cart/voucher/${PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)

		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers.size() == 1
		assert response.appliedVouchers[0].code == "abc"
		assert response.appliedVouchers[0].freeShipping == false
		assert response.appliedVouchers[0].voucherCode == PROMOTION_VOUCHER_CODE
		assert response.appliedVouchers[0].name == "New Promotional Voucher"
		assert response.appliedVouchers[0].description == "Promotion Voucher Description"
		assert response.appliedVouchers[0].value == 10
		assert response.appliedVouchers[0].valueString == '10.0%'
		assert response.appliedVouchers[0].valueFormatted == '10.0%'
		assert Math.round(response.appliedVouchers[0].appliedValue.value * 100) / 100 == 22.37
		assert response.appliedVouchers[0].appliedValue.priceType == 'BUY'
		assert response.appliedVouchers[0].appliedValue.currencyIso == 'USD'
		assert response.appliedVouchers[0].appliedValue.formattedValue == '$22.37'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 201.31
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyVoucherForCartByClient() {
		def access_token = testUtil.getClientCredentialsToken()
		//put something in a new cart'
		def cookieNoPath = addToCart(1934795, 2, null, access_token)

		def con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		def response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		con = testUtil.getSecureConnection("/cart/voucher/${PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)

		response = testUtil.verifiedJSONSlurper(con, true, false);
		assert response.appliedVouchers.size() == 1
		assert response.appliedVouchers[0].code == "abc"
		assert response.appliedVouchers[0].freeShipping == false
		assert response.appliedVouchers[0].voucherCode == PROMOTION_VOUCHER_CODE
		assert response.appliedVouchers[0].name == "New Promotional Voucher"
		assert response.appliedVouchers[0].description == "Promotion Voucher Description"
		assert response.appliedVouchers[0].value == 10
		assert response.appliedVouchers[0].valueString == '10.0%'
		assert response.appliedVouchers[0].valueFormatted == '10.0%'
		assert Math.round(response.appliedVouchers[0].appliedValue.value * 100) / 100 == 22.37
		assert response.appliedVouchers[0].appliedValue.priceType == 'BUY'
		assert response.appliedVouchers[0].appliedValue.currencyIso == 'USD'
		assert response.appliedVouchers[0].appliedValue.formattedValue == '$22.37'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 201.31
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyRestrictedVoucherForCartByCustomer() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		// put something in a new cart
		def cookieNoPath = addToCart(1934795, 2, null, access_token)

		def con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		def response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value < 250

		// Should fail:
		con = testUtil.getSecureConnection("/cart/voucher/${RESTRICTED_PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, access_token)
		def error = con.errorStream.text
		response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'VoucherOperationError'
		assert response.errors[0].message == 'Voucher cannot be redeemed: abr-D7S5-K14A-51Y5'

		// put one more product to meet the restriction (totalPrice >= 250)
		cookieNoPath = addToCart(1934795, 1, cookieNoPath, access_token)

		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 3
		assert response.totalPrice.value >= 250

		con = testUtil.getSecureConnection("/cart/voucher/${RESTRICTED_PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)

		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers.size() == 1
		assert response.appliedVouchers[0].code == "abr"
		assert response.appliedVouchers[0].freeShipping == false
		assert response.appliedVouchers[0].voucherCode == RESTRICTED_PROMOTION_VOUCHER_CODE
		assert response.appliedVouchers[0].value == 10
		assert response.totalItems == 1
		assert response.totalUnitCount == 3
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyAbsoluteVoucherForCartByCustomer() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		//put something in a new cart'
		def cookieNoPath = addToCart(1934795, 2, null, access_token)

		def con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		def response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		con = testUtil.getSecureConnection("/cart/voucher/${ABSOLUTE_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)

		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers.size() == 1
		assert response.appliedVouchers[0].code == "xyz"
		assert response.appliedVouchers[0].freeShipping == true
		assert response.appliedVouchers[0].voucherCode == ABSOLUTE_VOUCHER_CODE
		assert response.appliedVouchers[0].name == "New Voucher"
		assert response.appliedVouchers[0].description == "Voucher Description"
		assert response.appliedVouchers[0].value == 15
		assert response.appliedVouchers[0].valueString == '15.0 USD'
		assert response.appliedVouchers[0].valueFormatted == '15.0 USD'
		assert response.appliedVouchers[0].currency.isocode == "USD"
		assert response.appliedVouchers[0].currency.name == 'US Dollar'
		assert response.appliedVouchers[0].currency.symbol == '$'
		assert response.appliedVouchers[0].appliedValue.value == 15.0
		assert response.appliedVouchers[0].appliedValue.priceType == 'BUY'
		assert response.appliedVouchers[0].appliedValue.currencyIso == 'USD'
		assert response.appliedVouchers[0].appliedValue.formattedValue == '$15.00'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 208.68
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyAbsoluteVoucherForCartByClient() {
		def access_token = testUtil.getClientCredentialsToken()

		//put something in a new cart'
		def cookieNoPath = addToCart(1934795, 2, null, access_token)

		def con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		def response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		con = testUtil.getSecureConnection("/cart/voucher/${ABSOLUTE_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)

		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers.size() == 1
		assert response.appliedVouchers[0].code == "xyz"
		assert response.appliedVouchers[0].freeShipping == true
		assert response.appliedVouchers[0].voucherCode == ABSOLUTE_VOUCHER_CODE
		assert response.appliedVouchers[0].name == "New Voucher"
		assert response.appliedVouchers[0].description == "Voucher Description"
		assert response.appliedVouchers[0].value == 15
		assert response.appliedVouchers[0].valueString == '15.0 USD'
		assert response.appliedVouchers[0].valueFormatted == '15.0 USD'
		assert response.appliedVouchers[0].currency.isocode == "USD"
		assert response.appliedVouchers[0].currency.name == 'US Dollar'
		assert response.appliedVouchers[0].currency.symbol == '$'
		assert response.appliedVouchers[0].appliedValue.value == 15.0
		assert response.appliedVouchers[0].appliedValue.priceType == 'BUY'
		assert response.appliedVouchers[0].appliedValue.currencyIso == 'USD'
		assert response.appliedVouchers[0].appliedValue.formattedValue == '$15.00'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 208.68
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyNotExistingVoucherForCartByCustomer() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		//put something in a new cart'
		def cookieNoPath = addToCart(1934795, 1, null, access_token)

		def con = testUtil.getSecureConnection("/cart/voucher/${NOT_EXISTING_VOUCHER_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, access_token)
		def error = con.errorStream.text;
		println error
		def response = new XmlSlurper().parseText(error)
		assert response.error.type == 'VoucherOperationError'
		assert response.error.message == 'Voucher not found: notExistingVoucher'
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyNotExistingVoucherForCartByClient() {
		def access_token = testUtil.getClientCredentialsToken()

		//put something in a new cart'
		def cookieNoPath = addToCart(1934795, 1, null, access_token)

		def con = testUtil.getSecureConnection("/cart/voucher/${NOT_EXISTING_VOUCHER_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, access_token)

		def response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.error.type == 'VoucherOperationError'
		assert response.error.message == 'Voucher not found: notExistingVoucher'
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyVoucherWithoutCartByCustomer() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cookieNoPath;

		def con = testUtil.getSecureConnection("/cart/voucher/${PROMOTION_VOUCHER_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, access_token)
		def error = con.errorStream.text
		def response = new XmlSlurper().parseText(error)
		assert response.error.type == 'NoCheckoutCartError'
		assert response.error.message == 'Cannot apply voucher. There was no checkout cart created yet!'
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyVoucherWithoutCartByClient() {
		def access_token = testUtil.getClientCredentialsToken()

		def cookieNoPath;

		def con = testUtil.getSecureConnection("/cart/voucher/${PROMOTION_VOUCHER_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, access_token)

		def response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.error.type == 'NoCheckoutCartError'
		assert response.error.message == 'Cannot apply voucher. There was no checkout cart created yet!'
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testReleaseVoucherForCartByCustomer() {
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		//put something in a new cart'
		def cookieNoPath = addToCart(1934795, 2, null, access_token)

		def con = testUtil.getSecureConnection("/cart/voucher/${PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		def response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers.size() == 1
		assert response.appliedVouchers[0].code == "abc"
		assert response.appliedVouchers[0].voucherCode == PROMOTION_VOUCHER_CODE
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 201.31

		con = testUtil.getSecureConnection("/cart/voucher/${PROMOTION_VOUCHER_CODE}", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers.size() == 0
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testReleaseVoucherForCartByClient() {
		def access_token = testUtil.getClientCredentialsToken()

		//put something in a new cart'
		def cookieNoPath = addToCart(1934795, 2, null, access_token)

		def con = testUtil.getSecureConnection("/cart/voucher/${PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		def response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers.size() == 1
		assert response.appliedVouchers[0].code == "abc"
		assert response.appliedVouchers[0].voucherCode == PROMOTION_VOUCHER_CODE
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 201.31

		con = testUtil.getSecureConnection("/cart/voucher/${PROMOTION_VOUCHER_CODE}", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers.size() == 0
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
	}

	/**
	 * Helper method for adding product to the cart
	 *
	 * @param code Product code
	 * @param qty Quantity of products
	 * @param cookie Cookie
	 * @param access_token Access token for secure connection
	 *
	 * @return cookie without path
	 */
	def addToCart(code, qty = 1, cookie = null, access_token = null, storeName = null) {
		//add something to a cart
		def postBody = "code=${code}&qty=${qty}"

		if (storeName != null) {
			postBody += "&storeName=${storeName}"
		}

		def con
		if (access_token != null)
			con = testUtil.getSecureConnection('/cart/entry', 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, cookie, access_token)
		else
			con = testUtil.getConnection('/cart/entry', 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, cookie)

		def body = con.inputStream.text
		testUtil.verifyXML(body)
		def response = new XmlSlurper().parseText(body)
		assert response.statusCode == 'success'
		assert response.quantityAdded == qty

		//check for cookie, extract it for next request
		def new_cookie = con.getHeaderField('Set-Cookie')
		println new_cookie
		def ret = cookie;
		if (new_cookie != null) {
			def cookie_split = new_cookie.split(';')
			if (cookie_split[0].split('=')[0] == 'JSESSIONID') {
				ret = cookie_split[0];
			}
		}
		assert ret: 'No cookie present, cannot keep session';

		return ret;
	}

	@Test
	void testAddInvalidDeliveryAddress() {
		def cookieNoPath
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = "Definetely_wrong_address"

		// put something in a new cart
		cookieNoPath = addToCart(1934795, 1, null, access_token)

		// add delivery address
		def con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, cookieNoPath, access_token)
		def error = con.errorStream.text
		def response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'UnsupportedDeliveryAddressError'
		assert response.errors[0].message == 'Address [Definetely_wrong_address] is not supported for the current cart'

	}

	@Test
	void testPlaceOrderWithoutCartOneStepCheckout() {
		def con, response, cookieNoPath, postBody

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = customerTests.createAddress(access_token)

		postBody = "deliveryMode=standard-gross&addressId=${aid}&securityCode=123"
		con = testUtil.getSecureConnection('/cart/checkout', 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, cookieNoPath, access_token)
		def error = con.errorStream.text
		response = new JsonSlurper().parseText(error)
		println error
		assert response.errors[0].type == 'NoCheckoutCartError'
		assert response.errors[0].message.toString().contains("There was no checkout cart created yet!")
	}

	@Test
	void testPlaceOrderDeliveryWithoutAddressOneStepCheckout() {
		def con, response, cookieNoPath, postBody

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		cookieNoPath = addToCart(1934795, 1, null, access_token)
		postBody = 'deliveryMode=standard-gross&securityCode=123'
		con = testUtil.getSecureConnection('/cart/checkout', 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, cookieNoPath, access_token)
		def error = con.errorStream.text
		response = new JsonSlurper().parseText(error)
		println error
		assert response.errors[0].type == 'UnsupportedDeliveryModeError'
		assert response.errors[0].message == 'Delivery Mode [standard-gross] is not supported for the current cart'
	}

	@Test
	void testAuthorizedValidPlaceOrderCreateAddressAndPaymentInfoOneStepCheckout() {
		def con, response, cookieNoPath, postBody

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		cookieNoPath = addToCart(1934795, 1, null, access_token)
		postBody = "titleCode=dr&firstName=Sven&lastName=Haiges&line1=Nymphenburger Str. 86 - Maillingerstrasse&town=Muenchen&postalCode=80331&country.isocode=DE&deliveryMode=standard-gross&securityCode=123&accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection('/cart/checkout', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == false
	}

	@Test
	void testAuthorizedValidPlaceOrderAddressIdOneStepCheckout() {
		def con, response, cookieNoPath, postBody

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = customerTests.createAddressJSON(access_token)

		cookieNoPath = addToCart(1934795, 1, null, access_token)
		postBody = "addressId=${aid}&deliveryMode=standard-gross&securityCode=123&accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection('/cart/checkout', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == false
	}

	@Test
	void testAuthorizedValidPlaceOrderPickupOneStepCheckout() {
		def con, response, cookieNoPath, postBody

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		cookieNoPath = addToCart(1934793, 1, null, access_token, "WS-Nakano")
		postBody = "deliveryMode=pickup&securityCode=123&accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection('/cart/checkout', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == false
	}

	@Test
	void testAuthorizedValidPlaceOrderPaymentInfoIdOneStepCheckout() {
		def con, response, cookieNoPath, postBody

		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = customerTests.createAddressJSON(access_token)

		cookieNoPath = addToCart(1934795, 1, null, access_token)

		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		def pid = response.paymentInfo.id
		postBody = "addressId=${aid}&deliveryMode=standard-gross&securityCode=123&paymentInfoId=${pid}"
		con = testUtil.getSecureConnection('/cart/checkout', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == false
	}

	@Test
	void testAuthorizedValidPlaceOrderGuestOneStepCheckout() {
		def con, response, cookieNoPath, postBody
		def guestCheckoutTests = new GuestCheckoutTests();
		def customerTests = new CustomerTests()
		def access_token = testUtil.getClientCredentialsToken()
		def guestUid = guestCheckoutTests.getGuestUid();

		cookieNoPath = addToCart(1934795, 1, null, access_token)
		postBody = "email=${guestUid}";
		con = testUtil.getSecureConnection('/customers/current/guestlogin', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		def aid = customerTests.createAddressJSON(access_token, null, cookieNoPath)
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		def pid = response.paymentInfo.id
		postBody = "addressId=${aid}&deliveryMode=standard-gross&securityCode=123&paymentInfoId=${pid}"
		con = testUtil.getSecureConnection('/cart/checkout', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == true
	}
}
