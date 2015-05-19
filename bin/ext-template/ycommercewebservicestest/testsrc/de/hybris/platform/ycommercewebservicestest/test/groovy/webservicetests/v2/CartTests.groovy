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

	//covered
	@Test
	void testFailGetCartsForAnonymousUser() {
		def access_token = testUtil.getTrustedClientCredentialsToken()

		def con = testUtil.getSecureConnection("/users/anonymous/carts", 'GET', 'XML', HttpURLConnection.HTTP_UNAUTHORIZED, null, null, access_token)
		def body = con.errorStream.text;
		def response = new XmlSlurper().parseText(body);

		assert response.errors[0].type == 'AccessDeniedError'
	}

	//covered
	@Test
	void testGetCartsForCustomerUser() {
		def usersTest = new UsersTest()
		def uid = usersTest.registerUser();
		def access_token = testUtil.getAccessToken(uid, PASSWORD, config.TRUSTED_CLIENT_ID, config.TRUSTED_CLIENT_SECRET)
		def cartCode = createRegularUsersCart(uid, access_token)

		def con = testUtil.getSecureConnection("/users/${uid}/carts?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con)

		assert response.carts[0].code == cartCode
	}

	//covered
	@Test
	void testCreateAnonymousUsersCart() {
		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
	}

	//covered
	@Test
	void testGetCartJSON() {
		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;

		def con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con)
		assert response.totalItems == 0
		assert response.totalUnitCount == 0
		assert response.net == false
		//assert response.calculated == false
		assert response.totalPrice.currencyIso == 'USD'
		assert response.totalPrice.priceType == 'BUY'
		assert response.totalPrice.value == 0
		assert response.totalPrice.formattedValue == '$0.00'
		assert response.subTotal.currencyIso == 'USD'
		assert response.subTotal.priceType == 'BUY'
		assert response.subTotal.value == 0
		assert response.subTotal.formattedValue == '$0.00'
	}

	//covered
	@Test
	void testGetCartXML() {
		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;

		def con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name() == 'cart'
		assert response.totalItems == 0
		assert response.totalUnitCount == 0
		assert response.net == 'false'
		assert response.totalPrice.currencyIso == 'USD'
		assert response.totalPrice.priceType == 'BUY'
		assert response.totalPrice.value == 0.0
		assert response.totalPrice.formattedValue == '$0.00'
		assert response.subTotal.currencyIso == 'USD'
		assert response.subTotal.priceType == 'BUY'
		assert response.subTotal.value == 0.0
		assert response.subTotal.formattedValue == '$0.00'
	}

	//covered
	@Test
	void testGetCartAfterCurrencyChangeXML() {
		def con, body, response

		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)

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

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?curr=JPY&fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)

		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		println response.totalPrice.value;
		assert response.totalPrice.value == 940.0
		assert response.totalPrice.formattedValue == '¥940'
		assert response.totalPrice.currencyIso == 'JPY'

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?curr=USD&fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)

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

	//covered
	@Test
	void testAddToCart() {
		def con, response

		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.store == 'wsTest'
		assert response.net == false
		assert response.calculated == true
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
		assert response.totalUnitCount == 1
	}

	//covered <-- out of stock online != out of stock in store
	@Test
	void testAddPickupItemOutOfStockOnlineToCart() {
		def con, response

		// add pickup item which is out of stock in the online store
		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
		addProductToAnonymousUsersCart(cartGuid, 2006139, 1, STORE_NAME)

		// get the cart
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.entries[0].product.availableForPickup == true
		assert response.entries[0].deliveryPointOfService.name == STORE_NAME
	}

	//covered <- add product to cart for pickup in non-existing store
	@Test
	void testAddPickupItemToCartWithWrongStoreNameFail() {
		def con, response

		// add pickup item which is out of stock in the online store
		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;

		def postBody = "code=2006139&qty=1&pickupStore=WrongStoreName"
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody)

		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error);
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].subject == 'pickupStore'
		assert response.errors[0].reason == 'invalid'
		assert response.errors[0].message == "Store with given name doesn't exist"
	}

	//covered
	@Test
	void testFailAddPickupItemOutOfStockToCart() {
		def con, response

		// add pickup item which is out of stock in the online store
		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;

		// add pickup item which is out of stock in the STORE_NAME
		def postBody = "code=816780&qty=1&pickupStore=${STORE_NAME}"
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody)

		def error = con.errorStream.text
		response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'InsufficientStockError'
		assert response.errors[0].subjectType == 'product'
		assert response.errors[0].subject == '816780'
		assert response.errors[0].reason == 'noStock'
		assert response.errors[0].message == 'Product [816780] is currently out of stock'
	}

	//covered
	@Test
	void testUpdateCartEntry() {
		def con, body, response

		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		// get the cart
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 11.12

		// update cart entry
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries/0?qty=3&fields=${FULL_SET}", 'PATCH', 'XML', HttpURLConnection.HTTP_OK)
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
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 33.36
	}

	//covered
	@Test
	void testRemoveCartEntry() {
		def con, response

		// create anonymous cart
		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
		addProductToAnonymousUsersCart(cartGuid, 3429337, 2)
		addProductToAnonymousUsersCart(cartGuid, 1225694, 1)

		// get the cart
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 869.98

		// remove cart entry
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries/1", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK)

		// check the cart
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 22.24
	}

	//covered
	@Test
	void testPickupInStoreMode() {
		def con, response
		def productCode = 1934793

		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
		addProductToAnonymousUsersCart(cartGuid, productCode)

		// set cart entry as pickup in store
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries/0?pickupStore=WS-Nakano&fields=${FULL_SET}", 'PATCH', 'XML', HttpURLConnection.HTTP_OK)
		def body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.name() == 'cartModification'
		assert response.statusCode == 'success'
		assert response.entry[0].product.code == productCode
		assert response.entry[0].deliveryPointOfService.name == 'WS-Nakano'

		// get updated cart
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.pickupItemsQuantity == 1
		assert response.deliveryItemsQuantity == 0
		assert response.entries[0].deliveryPointOfService.name == 'WS-Nakano'
		assert response.pickupOrderGroups[0].entries[0].deliveryPointOfService.name == 'WS-Nakano'

		//set cart entry as shipping
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries/0?qty=1&fields=${FULL_SET}", 'PUT', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.name() == 'cartModification'
		assert response.statusCode == 'success'
		assert response.entry[0].product.code == productCode

		// get updated cart
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.pickupItemsQuantity == 0
		assert response.deliveryItemsQuantity == 1
	}

	//covered
	@Test
	void testPickupInStoreWithWrongStoreName() {
		def con, response

		def cartGuid = createAnonymousUsersCart()
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		// set cart entry as pickup in store
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries/0?pickupStore=wrongStoreName", 'PATCH', 'XML', HttpURLConnection.HTTP_BAD_REQUEST)
		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subject == 'pickupStore'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].reason == 'invalid'
		assert response.errors[0].message == "Store with given name doesn't exist"
	}

	//covered <- update non existing entry
	@Test
	void testPickupInStoreWithWrongEntryNumber() {
		def con, response

		// create anonymous cart
		def cartGuid = createAnonymousUsersCart()
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		// set cart entry as pickup in store
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries/1?pickupStore=WS-Nakano", 'PATCH', 'XML', HttpURLConnection.HTTP_BAD_REQUEST)
		def error = con.errorStream.text
		response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'CartEntryError'
		assert response.errors[0].message == "Entry not found"
		assert response.errors[0].subject == '1'
		assert response.errors[0].subjectType == 'entry'
		assert response.errors[0].reason == 'notFound'
	}

	//covered <- replace not existing entry
	@Test
	void testSetCartEntryWithoutChangingShippingMode() {
		def con, body, response

		// create anonymous cart
		def cartGuid = createAnonymousUsersCart()
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		// try to set cart entry as shipping  when it is already in shipping mode
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries/0?qty=1", 'PUT', 'XML', HttpURLConnection.HTTP_OK)

		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.name() == 'cartModification'
		assert response.statusCode == 'success'
	}

	//covered
	@Test
	void testDeletePickupInStoreWithWrongEntryNumber() {
		def con, response

		// create anonymous cart
		def cartGuid = createAnonymousUsersCart()
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		//remove pickup in store mode
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries/1?qty=1", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST)

		response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.errors[0].type == 'CartEntryError'
		assert response.errors[0].message == "Entry not found"
		assert response.errors[0].subject == '1'
		assert response.errors[0].subjectType == 'entry'
		assert response.errors[0].reason == 'notFound'

	}

	//covered <- change entry to shipping while out of stock
	@Test
	void testDeletePickupInStoreForItemOutOfStockOnline() {
		def con, response

		// add pickup item which is out of stock in the online store
		def cartGuid = createAnonymousUsersCart()
		addProductToAnonymousUsersCart(cartGuid, 2006139, 1, STORE_NAME)

		//set cart entry as shipping
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries/0?qty=1", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST)
		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error);
		assert response.errors[0].type == 'InsufficientStockError'
		assert response.errors[0].subjectType == 'entry'
		assert response.errors[0].subject == '0'
		assert response.errors[0].reason == 'noStock'
		assert response.errors[0].message == 'Product [2006139] cannot be shipped - out of stock online'
	}

	//covered <- change entry to pickup while out of stock
	@Test
	void testFailPickupInStoreWhenOutOfStock() {
		def con, response

		// add item which is out of stock in the STORE_NAME
		def cartGuid = createAnonymousUsersCart()
		addProductToAnonymousUsersCart(cartGuid, 1934795)

		// try to pickup in this store
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries/0?pickupStore=${STORE_NAME}", 'PATCH', 'XML', HttpURLConnection.HTTP_BAD_REQUEST)

		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error);
		assert response.errors[0].type == 'InsufficientStockError'
		assert response.errors[0].subjectType == 'entry'
		assert response.errors[0].subject == '0'
		assert response.errors[0].reason == 'noStock'
		assert response.errors[0].message == 'Product [1934795] is currently out of stock'
	}

	//covered <-- not really "by client" as trusted client credentials are retrieved in the body
	@Test
	void testEnableOrderPromotionByClient() {
		def con, body, response
		def access_token = testUtil.getTrustedClientCredentialsToken()

		def cartGuid = createAnonymousUsersCart()
		addProductToAnonymousUsersCart(cartGuid, 1225694, 1)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		// enable
		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/promotions?promotionId=${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions[0].promotion.code == RESTRICTED_PROMOTION_CODE
		assert response.appliedOrderPromotions[0].promotion.firedMessages == RESTRICTED_PROMOTION_FIRED_MESSAGE
		assert response.appliedOrderPromotions[0].promotion.promotionType == RESTRICTED_PROMOTION_TYPE
		assert response.appliedOrderPromotions[0].promotion.description == RESTRICTED_PROMOTION_DESCRIPTION
		assert response.appliedOrderPromotions[0].promotion.endDate.toString().startsWith(RESTRICTED_PROMOTION_END_DATE)
		assert response.appliedOrderPromotions[0].description == RESTRICTED_PROMOTION_FIRED_MESSAGE
	}

	//covered <-- not really "by client" as trusted client credentials are retrieved in the body
	@Test
	void testDisableOrderPromotionByClient() {
		def con, body, response
		def access_token = testUtil.getTrustedClientCredentialsToken();

		def cartGuid = createAnonymousUsersCart()
		addProductToAnonymousUsersCart(cartGuid, 1225694, 1)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		// enable
		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/promotions?promotionId=${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		// disable
		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/promotions/${RESTRICTED_PROMOTION_CODE}", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0
	}

	//covered
	@Test
	void testEnablePotentialOrderPromotionByClient() {
		def con, body, response
		def access_token = testUtil.getTrustedClientCredentialsToken()

		def cartGuid = createAnonymousUsersCart()

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/promotions?promotionId=${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions[0].promotion.code == RESTRICTED_PROMOTION_CODE
		assert response.potentialOrderPromotions[0].promotion.promotionType == RESTRICTED_PROMOTION_TYPE
		assert response.potentialOrderPromotions[0].promotion.description == RESTRICTED_PROMOTION_DESCRIPTION
		assert response.potentialOrderPromotions[0].promotion.endDate.toString().startsWith(RESTRICTED_PROMOTION_END_DATE)
		assert response.potentialOrderPromotions[0].promotion.couldFireMessages == RESTRICTED_PROMOTION_COULD_FIRE_MESSAGE
		assert response.potentialOrderPromotions[0].description == RESTRICTED_PROMOTION_COULD_FIRE_MESSAGE
	}

	//covered, same thing as removing applied promotion
	@Test
	void testDisablePotentialOrderPromotionByClient() {
		def con, body, response
		def access_token = testUtil.getTrustedClientCredentialsToken()

		def cartGuid = createAnonymousUsersCart()

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		// enable
		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/promotions?promotionId=${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		// disable
		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/promotions/${RESTRICTED_PROMOTION_CODE}", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0
	}

	//covered, same thing as before, tests above also use trusted client
	@Test
	void testEnableOrderPromotionForCustomerByTrustedClient() {
		def con, body, response
		def usersTest = new UsersTest()
		def uid = usersTest.registerUser();
		def access_token = testUtil.getTrustedClientCredentialsToken()

		def cartCode = createRegularUsersCart(uid, access_token)
		addProductToRegularUsersCart(uid, cartCode, 1225694, 1, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		// enable
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/promotions?promotionId=${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions[0].promotion.code == RESTRICTED_PROMOTION_CODE
		assert response.appliedOrderPromotions[0].promotion.firedMessages == RESTRICTED_PROMOTION_FIRED_MESSAGE
		assert response.appliedOrderPromotions[0].promotion.promotionType == RESTRICTED_PROMOTION_TYPE
		assert response.appliedOrderPromotions[0].promotion.description == RESTRICTED_PROMOTION_DESCRIPTION
		assert response.appliedOrderPromotions[0].promotion.endDate.toString().startsWith(RESTRICTED_PROMOTION_END_DATE)
		assert response.appliedOrderPromotions[0].description == RESTRICTED_PROMOTION_FIRED_MESSAGE
	}

	//covered, same thing as before, tests above also use trusted client
	@Test
	void testDisableOrderPromotionForCustomerByTrustedClient() {
		def con, body, response
		def usersTest = new UsersTest()
		def uid = usersTest.registerUser();
		def access_token = testUtil.getTrustedClientCredentialsToken()

		def cartCode = createRegularUsersCart(uid, access_token)
		addProductToRegularUsersCart(uid, cartCode, 1225694, 1, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		// enable
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/promotions?promotionId=${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		// disable
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/promotions/${RESTRICTED_PROMOTION_CODE}", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0
	}

	//covered
	@Test
	void testDisableOrderPromotionForCustomerByCustomerShouldFail() {
		def con, body, response
		def usersTest = new UsersTest()
		def uid = usersTest.registerUser();
		def access_token = testUtil.getAccessToken(uid, PASSWORD, config.CLIENT_ID, config.CLIENT_SECRET)

		def cartCode = createRegularUsersCart(uid, access_token)
		addProductToRegularUsersCart(uid, cartCode, 1225694, 1, null, access_token)

		// disable
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/promotions/${RESTRICTED_PROMOTION_CODE}", 'DELETE', 'XML', HttpURLConnection.HTTP_UNAUTHORIZED, null, null, access_token)
	}

	//covered
	@Test
	void testEnableOrderPromotionForCustomerByCustomerShouldFail() {
		def con, body, response
		def usersTest = new UsersTest()
		def uid = usersTest.registerUser();
		def access_token = testUtil.getAccessToken(uid, PASSWORD, config.CLIENT_ID, config.CLIENT_SECRET)

		def cartCode = createRegularUsersCart(uid, access_token)
		addProductToRegularUsersCart(uid, cartCode, 1225694, 1, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.appliedOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/promotions?promotionId=${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_UNAUTHORIZED, null, null, access_token)
	}

	//covered, test 'ByClient' uses trusted client credentials
	@Test
	void testEnablePotentialOrderPromotionByTrustedClient() {
		def con, body, response
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken()
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser();
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		// enable
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/promotions?promotionId=${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, null, trusted_client_access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions[0].promotion.code == RESTRICTED_PROMOTION_CODE
		assert response.potentialOrderPromotions[0].promotion.promotionType == RESTRICTED_PROMOTION_TYPE
		assert response.potentialOrderPromotions[0].promotion.description == RESTRICTED_PROMOTION_DESCRIPTION
		assert response.potentialOrderPromotions[0].promotion.endDate.toString().startsWith(RESTRICTED_PROMOTION_END_DATE)
		assert response.potentialOrderPromotions[0].promotion.couldFireMessages == RESTRICTED_PROMOTION_COULD_FIRE_MESSAGE
		assert response.potentialOrderPromotions[0].description == RESTRICTED_PROMOTION_COULD_FIRE_MESSAGE
	}

	//covered, test 'ByClient' uses trusted client credentials and removing potential promotion is the same as removing applied promotion
	@Test
	void testDisablePotentialOrderPromotionByTrustedClient() {
		def con, body, response
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken()
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser();
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0

		// enable
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/promotions?promotionId=${RESTRICTED_PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, null, trusted_client_access_token)

		// disable
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/promotions/${RESTRICTED_PROMOTION_CODE}", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, null, trusted_client_access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.potentialOrderPromotions.findAll { it.code = RESTRICTED_PROMOTION_CODE }.size() == 0
	}

	//covered
	@Test
	void testEnableUnestrictedPromotionShouldFail() {
		def con, body, response
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken()
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser();
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/promotions?promotionId=${PROMOTION_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, trusted_client_access_token)
		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'CommercePromotionRestrictionError'
	}

	//covered
	@Test
	void testDisableUnestrictedPromotionShouldFail() {
		def con, body, response
		def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken()
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser();
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/promotions/${PROMOTION_CODE}", 'DELETE', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, trusted_client_access_token)
		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'CommercePromotionRestrictionError'
	}

	//covered in user tests
	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testCartCreatePaymentInfo() {
		def con, response, postBody
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def aid = usersTest.createAddressJSON(uid, access_token)

		def cartCode = createRegularUsersCart(uid, access_token)
		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)

		//set the delivery address
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=deliveryAddress(FULL)", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryAddress.id
		assert response.deliveryAddress.firstName == 'John'
		assert response.deliveryAddress.lastName == 'Doe'
		assert response.deliveryAddress.titleCode == 'dr'
		assert response.deliveryAddress.title == 'Dr.'
		assert response.deliveryAddress.postalCode == '80331'
		assert response.deliveryAddress.town == 'Muenchen'
		assert response.deliveryAddress.line1 == 'Nymphenburger Str. 86 - Maillingerstrasse'
		assert response.deliveryAddress.country.name == 'Germany'
		assert response.deliveryAddress.country.isocode == 'DE'
		assert response.deliveryAddress.formattedAddress == 'Nymphenburger Str. 86 - Maillingerstrasse, 80331, Muenchen, Germany'

		//set a delivery mode
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/deliverymode?deliveryModeId=standard-gross", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/deliverymode?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryCost.currencyIso == 'USD'
		assert response.deliveryCost.priceType == 'BUY'
		assert response.deliveryCost.value == 8.99
		assert response.deliveryCost.formattedValue == '$8.99'

		//create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/paymentdetails?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.id
		assert response.subscriptionId == 'MockedSubscriptionID'
		assert response.saved == true
		assert response.expiryMonth == '01'
		assert response.expiryYear == '2113'
		assert response.cardType.name == 'Visa'
		assert response.cardType.code == 'visa'
		assert response.accountHolderName == 'Sven Haiges'
		assert response.cardNumber == '************1111'
		assert response.billingAddress.id
		assert response.billingAddress.lastName == 'haiges'
		assert response.billingAddress.firstName == 'sven'
		assert response.billingAddress.titleCode == 'mr'
		assert response.billingAddress.title == "Mr"
		assert response.billingAddress.country.name == 'Germany'
		assert response.billingAddress.country.isocode == 'DE'
		assert response.billingAddress.postalCode == '12345'
		assert response.billingAddress.email == uid
		assert response.billingAddress.formattedAddress == 'test1, test2, 12345, somecity, Germany'
		assert response.billingAddress.town == 'somecity'
		assert response.billingAddress.line1 == 'test1'
		assert response.billingAddress.line2 == 'test2'
	}

	//covered in users resource
	@Test
	void testCreateDefaultPaymentWithoutSavingIt() {
		def con, response, postBody
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)
		addProductToRegularUsersCart(uid, cartCode, 1934795, 2, null, access_token)

		//create a paymentinfo for this cart
		def saved = false
		def defaultPaymentInfo = true
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=$saved&defaultPaymentInfo=$defaultPaymentInfo&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/paymentdetails?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);

		assert response.defaultPayment == false // payment cannot be default if it's not saved
	}

	//covered
	@Test
	void testRemoveDeliveryAddress() {
		def con, response, postBody
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)
		def aid = usersTest.createAddressJSON(uid, access_token)

		// put something in a new cart
		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)

		// add delivery address
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryAddress != null

		// delete delivery address
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/addresses/delivery", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryAddress == null
	}

	//covered, split to two
	@Test
	void testRemoveDeliveryMode() {
		def con, response, postBody
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)
		def aid = usersTest.createAddressJSON(uid, access_token)

		// put something in a new cart
		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)

		// add delivery address
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		// set delivery mode
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/deliverymode?deliveryModeId=standard-gross", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryCost != null

		// reset delivery mode
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/deliverymode", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryCost == null
	}

	//covered
	@Test
	void testInvalidPlaceOrder() {
		def con, response, postBody
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)

		// put something in a new cart'
		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartCode}&securityCode=123", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
		def error = con.errorStream.text;
		println error;
		response = new XmlSlurper().parseText(error);
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subject == 'sessionCart'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].reason == 'invalid'
		assert response.errors[0].message == 'Delivery mode is not set'

		assert response.errors[1].type == 'ValidationError'
		assert response.errors[1].subject == 'sessionCart'
		assert response.errors[1].subjectType == 'parameter'
		assert response.errors[1].reason == 'invalid'
		assert response.errors[1].message == 'Payment info is not set'
	}

	//covered
	@Test
	void testAuthorizedValidPlaceOrder() {
		def con, response, postBody
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)
		def aid = usersTest.createAddressJSON(uid, access_token)

		// put something in a new cart'
		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)

		// set the delivery address
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		// set delivery mode
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/deliverymode?deliveryModeId=standard-gross", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		// create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/paymentdetails?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.id != null

		con = testUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartCode}&securityCode=123&fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == false
	}

	//covered
	@Test
	void testPlaceOrderForPickupInStore() {
		def con, response, postBody
		def String productId = "1934793"
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)
		def aid = usersTest.createAddressJSON(uid, access_token)

		// put something in a new cart'
		addProductToRegularUsersCart(uid, cartCode, productId, 1, STORE_NAME, access_token)

		// set a delivery mode as pickup
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/deliverymode?deliveryModeId=pickup", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.deliveryMode.code == 'pickup'

		// create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/paymentdetails", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartCode}&securityCode=123&fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, true, false);
		assert response.created
		assert response.guestCustomer == false
	}

	//covered
	@Test
	void testPlaceOrderWithProductOutOfStock() {
		def con, response, postBody
		def String productId = "1687508"
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)
		def aid = usersTest.createAddressJSON(uid, access_token)

		// put something in a new cart'
		addProductToRegularUsersCart(uid, cartCode, productId, 1, null, access_token)

		// set the delivery address
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		// set delivery mode
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/deliverymode?deliveryModeId=standard-gross", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		// create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/paymentdetails", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)

		try {
			setStockStatus("forceOutOfStock", productId)
			con = testUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartCode}&securityCode=123", 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
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

	//covered
	@Test
	void testGetSupportedDeliveryModes() {
		def con, response, postBody
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)
		def aid = usersTest.createAddressJSON(uid, access_token)

		// put something in a new cart'
		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)

		// set the delivery address
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		// get supported delivery modes
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/deliverymodes?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, true, false);
		assert response.deliveryModes
		assert response.deliveryModes.size() == 2

		def standardDeliveryMode = response.deliveryModes.find { it.code == 'standard-gross' };
		assert standardDeliveryMode.code == 'standard-gross'
		assert standardDeliveryMode.deliveryCost.value == 8.99
		assert standardDeliveryMode.deliveryCost.formattedValue == '$8.99'
	}

	//covered <-- promotions
	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyVoucherForCartByCustomer() {
		def con, response, postBody
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)

		// put something in a new cart'
		addProductToRegularUsersCart(uid, cartCode, 1934795, 2, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/vouchers?voucherId=${PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers
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

	//covered
	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyVoucherForCartByClient() {
		def con, response, postBody
		def access_token = testUtil.getClientCredentialsToken()

		def cartGuid = createAnonymousUsersCart()

		//put something in a new cart'
		addProductToAnonymousUsersCart(cartGuid, 1934795, 2, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/vouchers?voucherId=${PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, true, false);
		assert response.appliedVouchers
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

	//covered, split to 2, customer applies restricted voucher
	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyRestrictedVoucherForCartByCustomer() {
		def con, response, postBody
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)

		// put something in a new cart
		addProductToRegularUsersCart(uid, cartCode, 1934795, 2, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value < 250

		// Should fail:
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/vouchers?voucherId=${RESTRICTED_PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
		def error = con.errorStream.text
		response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'VoucherOperationError'
		assert response.errors[0].message == 'Voucher cannot be redeemed: abr-D7S5-K14A-51Y5'

		// put one more product to meet the restriction (totalPrice >= 250)
		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 3
		assert response.totalPrice.value >= 250

		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/vouchers?voucherId=${RESTRICTED_PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers
		assert response.appliedVouchers.size() == 1
		assert response.appliedVouchers[0].code == "abr"
		assert response.appliedVouchers[0].freeShipping == false
		assert response.appliedVouchers[0].voucherCode == RESTRICTED_PROMOTION_VOUCHER_CODE
		assert response.appliedVouchers[0].value == 10
		assert response.totalItems == 1
		assert response.totalUnitCount == 3
	}

	//covered
	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyAbsoluteVoucherForCartByCustomer() {
		def con, response, postBody
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)

		// put something in a new cart
		addProductToRegularUsersCart(uid, cartCode, 1934795, 2, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/vouchers?voucherId=${ABSOLUTE_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers
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

	//not transfered, adding voucher to anonymous cart already covered
	//different types of vouchers are handled the same way
	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyAbsoluteVoucherForCartByClient() {
		def con, response
		def access_token = testUtil.getClientCredentialsToken()

		def cartGuid = createAnonymousUsersCart();

		//put something in a new cart'
		addProductToAnonymousUsersCart(cartGuid, 1934795, 2, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/vouchers?voucherId=${ABSOLUTE_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers
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

	//covered
	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyNotExistingVoucherForCartByCustomer() {
		def con, response
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)

		// put something in a new cart
		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/vouchers?voucherId=${NOT_EXISTING_VOUCHER_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
		def error = con.errorStream.text;
		response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'VoucherOperationError'
		assert response.errors[0].message == 'Voucher not found: notExistingVoucher'
	}

	//pointless to transfer, test above already covers it
	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testApplyNotExistingVoucherForCartByClient() {
		def con, response
		def access_token = testUtil.getClientCredentialsToken()

		def cartGuid = createAnonymousUsersCart();

		//put something in a new cart'
		addProductToAnonymousUsersCart(cartGuid, 1934795, 1, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/vouchers?voucherId=${NOT_EXISTING_VOUCHER_CODE}", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)

		response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.errors[0].type == 'VoucherOperationError'
		assert response.errors[0].message == 'Voucher not found: notExistingVoucher'
	}

	//covered
	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testReleaseVoucherForCartByCustomer() {
		def con, response
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode = createRegularUsersCart(uid, access_token)

		//put something in a new cart'
		addProductToRegularUsersCart(uid, cartCode, 1934795, 2, null, access_token)

		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/vouchers?voucherId=${PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers
		assert response.appliedVouchers.size() == 1
		assert response.appliedVouchers[0].code == "abc"
		assert response.appliedVouchers[0].voucherCode == PROMOTION_VOUCHER_CODE
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 201.31

		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/vouchers/${PROMOTION_VOUCHER_CODE}", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert !response.appliedVouchers
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
	}

	//pointless to transfer, test for anonymous, untrusted client already shows that such operation is possible
	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testReleaseVoucherForCartByClient() {
		def con, response
		def access_token = testUtil.getClientCredentialsToken()

		def cartGuid = createAnonymousUsersCart();

		//put something in a new cart'
		addProductToAnonymousUsersCart(cartGuid, 1934795, 2, null, access_token)

		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/vouchers?voucherId=${PROMOTION_VOUCHER_CODE}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.appliedVouchers
		assert response.appliedVouchers.size() == 1
		assert response.appliedVouchers[0].code == "abc"
		assert response.appliedVouchers[0].voucherCode == PROMOTION_VOUCHER_CODE
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 201.31

		testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/vouchers/${PROMOTION_VOUCHER_CODE}", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert !response.appliedVouchers
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
	}

	//covered
	@Test
	void testCreateMultipleCartsAsAnonymous() {
		def con, body, response
		def access_token = testUtil.getClientCredentialsToken()

		def cartGuid1 = createAnonymousUsersCart()
		def cartGuid2 = createAnonymousUsersCart()

		addProductToAnonymousUsersCart(cartGuid1, 3429337, 1, null, access_token)
		addProductToAnonymousUsersCart(cartGuid2, 1225694, 1, null, access_token)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid1}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 11.12

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid2}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 847.74
	}

	//covered
	@Test
	void testCreateMultipleCartsAsCustomer() {
		def con, body, response
		def usersTest = new UsersTest()
		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode1 = createRegularUsersCart(uid, access_token)
		def cartCode2 = createRegularUsersCart(uid, access_token)

		addProductToRegularUsersCart(uid, cartCode1, 3429337, 1, null, access_token)
		addProductToRegularUsersCart(uid, cartCode2, 1225694, 1, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode1}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 11.12

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode2}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		body = con.inputStream.text
		println body
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 847.74
	}

	//covered
	@Test
	void testAuthorizedValidPlaceOrderWithMultipleCarts() {
		def con, response, postBody
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		def cartCode1 = createRegularUsersCart(uid, access_token)
		def cartCode2 = createRegularUsersCart(uid, access_token)
		def aid = usersTest.createAddressJSON(uid, access_token)

		// put something in a new cart'
		addProductToRegularUsersCart(uid, cartCode1, 1934795, 1, null, access_token)
		addProductToRegularUsersCart(uid, cartCode2, 1225694, 1, null, access_token)

		// set the delivery address
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode1}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode2}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		// set delivery mode
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode1}/deliverymode?deliveryModeId=standard-gross", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		testUtil.getSecureConnection("/users/${uid}/carts/${cartCode2}/deliverymode?deliveryModeId=standard-gross", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		// create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode1}/paymentdetails?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.id != null
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode2}/paymentdetails?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.id != null

		con = testUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartCode1}&securityCode=123&fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == false
		con = testUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartCode2}&securityCode=123&fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false);
		assert response.created
		assert response.guestCustomer == false
	}

	//covered
	@Test
	void testRestoreAnonymousCartAsCustomer() {
		def con, body, response
		def usersTest = new UsersTest()

		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		// get the cart
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 11.12

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		//copy cart
		con = testUtil.getSecureConnection("/users/${uid}/carts?oldCartId=${cartGuid}&fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		println body
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 11.12
	}

	//covered
	@Test
	void testRestoreSomeoneOthersCartShouldFail() {
		def con, body, response
		def usersTest = new UsersTest()

		def uid1 = usersTest.registerUser()
		def access_token1 = testUtil.getAccessToken(uid1, PASSWORD)

		def uid2 = usersTest.registerUser()
		def access_token2 = testUtil.getAccessToken(uid2, PASSWORD)

		def cartGuid = createAnonymousUsersCart()
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		//copy cart
		con = testUtil.getSecureConnection("/users/${uid1}/carts?oldCartId=${cartGuid}&fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, null, null, access_token1)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		println body
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 11.12

		//copy cart again
		con = testUtil.getSecureConnection("/users/${uid2}/carts?oldCartId=${cartGuid}&fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token2)
		String error = con.errorStream.text;
		response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'CartError'
		assert response.errors[0].reason == 'cannotRestore'
		assert response.errors[0].subject == cartGuid
	}

	//covered
	@Test
	void testRestoreAlreadyRestoredCartShouldFail() {
		def con, body, response
		def usersTest = new UsersTest()

		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		// get the cart
		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 11.12

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		//copy cart
		con = testUtil.getSecureConnection("/users/${uid}/carts?oldCartId=${cartGuid}&fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, null, null, access_token)
		body = con.inputStream.text
		testUtil.verifyXML(body)
		response = new XmlSlurper().parseText(body)
		println body
		assert response.totalItems == 1
		assert response.totalUnitCount == 1
		assert response.totalPrice.value == 11.12

		//copy cart again
		con = testUtil.getSecureConnection("/users/${uid}/carts?oldCartId=${cartGuid}&fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
		String error = con.errorStream.text;
		response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'CartError'
		assert response.errors[0].reason == 'cannotRestore'
		assert response.errors[0].subject == cartGuid
	}

	//covered
	@Test
	void testAddInvalidDeliveryAddress() {
		def con, body, response
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)
		def cartCode = createRegularUsersCart(uid, access_token)
		def aid = "Definetely_wrong_address"

		// put something in a new cart
		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)

		// add delivery address
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
		def error = con.errorStream.text
		response = new JsonSlurper().parseText(error)
		assert response.errors[0].type == 'CartAddressError'
		assert response.errors[0].reason == 'notValid'
		assert response.errors[0].subjectType == 'address'
		assert response.errors[0].subject == aid
		assert response.errors[0].message == "Address given by id ${aid} is not valid"
	}

	//	@Test
	//	void testPlaceOrderDeliveryWithoutAddressOneStepCheckout() {
	//		def con, response, postBody
	//
	//		def uid = UsersTest.registerUser()
	//		def access_token = testUtil.getAccessToken(uid, PASSWORD)
	//
	//		def cartCode = createRegularUsersCart(uid, access_token)
	//		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)
	//
	//		postBody = 'deliveryMode=standard-gross&securityCode=123'
	//		con = testUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartCode}", 'POST', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)
	//		def error = con.errorStream.text
	//		response = new JsonSlurper().parseText(error)
	//		assert response.errors[0].type == 'UnsupportedDeliveryModeError'
	//		assert response.errors[0].message == 'Delivery Mode [standard-gross] is not supported for the current cart'
	//	}

	//	@Test
	//	void testAuthorizedValidPlaceOrderCreateAddressAndPaymentInfoOneStepCheckout() {
	//		def con, response, postBody
	//
	//		def uid = UsersTest.registerUser()
	//		def access_token = testUtil.getAccessToken(uid, PASSWORD)
	//
	//		def cartCode = createRegularUsersCart(uid, access_token)
	//		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)
	//
	//		postBody = "titleCode=dr&firstName=Sven&lastName=Haiges&line1=Nymphenburger Str. 86 - Maillingerstrasse&town=Muenchen&postalCode=80331&country.isocode=DE&deliveryMode=standard-gross&securityCode=123&accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
	//		con = testUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartCode}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	//		response = testUtil.verifiedJSONSlurper(con, false, false);
	//		assert response.created
	//		assert response.guestCustomer == false
	//	}

	//	@Test
	//	void testAuthorizedValidPlaceOrderAddressIdOneStepCheckout() {
	//		def con, response, postBody
	//
	//		def uid = UsersTest.registerUser()
	//		def access_token = testUtil.getAccessToken(uid, PASSWORD)
	//
	//		def cartCode = createRegularUsersCart(uid, access_token)
	//		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)
	//
	//		def aid = UsersTest.createAddressJSON(uid, access_token)
	//
	//		postBody = "addressId=${aid}&deliveryMode=standard-gross&securityCode=123&accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
	//		con = testUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartCode}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	//		response = testUtil.verifiedJSONSlurper(con, false, false);
	//		assert response.created
	//		assert response.guestCustomer == false
	//	}
	//
	//	@Test
	//	void testAuthorizedValidPlaceOrderPickupOneStepCheckout() {
	//		def con, response, postBody
	//
	//		def uid = UsersTest.registerUser()
	//		def access_token = TestUtil.getAccessToken(uid, PASSWORD)
	//
	//		def cartCode = createRegularUsersCart(uid, access_token)
	//		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, "WS-Nakano", access_token)
	//
	//		postBody = "deliveryMode=pickup&securityCode=123&accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2114&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
	//		con = TestUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartCode}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	//		response = TestUtil.verifiedJSONSlurper(con, false, false);
	//		assert response.created
	//		assert response.guestCustomer == false
	//	}
	//
	//	@Test
	//	void testAuthorizedValidPlaceOrderPaymentInfoIdOneStepCheckout() {
	//		def con, response, postBody
	//
	//		def uid = UsersTest.registerUser()
	//		def access_token = TestUtil.getAccessToken(uid, PASSWORD)
	//
	//		def cartCode = createRegularUsersCart(uid, access_token)
	//		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)
	//
	//		def aid = UsersTest.createAddressJSON(uid, access_token)
	//
	//		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
	//		con = TestUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/paymentdetails", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	//		response = TestUtil.verifiedJSONSlurper(con, false, false);
	//		def pid = response.paymentInfo.id
	//
	//		postBody = "addressId=${aid}&deliveryMode=standard-gross&securityCode=123&paymentInfoId=${pid}"
	//		con = TestUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartCode}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	//		response = TestUtil.verifiedJSONSlurper(con, false, false);
	//		assert response.created
	//		assert response.guestCustomer == false
	//	}

	//	@Test
	//	void testAuthorizedValidPlaceOrderGuestOneStepCheckout() {
	//		def con, response, postBody
	//
	//		def guestUid = GuestCheckoutTests.getGuestUid();
	//		def access_token = testUtil.getClientCredentialsToken();
	//
	//		def cartGuid = createAnonymousUsersCart();
	//		addProductToAnonymousUsersCart(cartGuid, 1934795, 1, null, access_token)
	//
	//		postBody = "email=${guestUid}";
	//		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/email", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	//
	//		postBody = "titleCode=dr&firstName=John&lastName=Doe&line1=Nymphenburger Str. 86 - Maillingerstrasse&town=Muenchen&postalCode=80331&country.isocode=DE"
	//		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/addresses/delivery", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	//		response = testUtil.verifiedJSONSlurper(con)
	//		assert response.postalCode == "80331"
	//		assert response.country.isocode == "DE"
	//
	//		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
	//		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/paymentdetails", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	//		response = testUtil.verifiedJSONSlurper(con, false, false);
	//		def pid = response.paymentInfo.id
	//		postBody = "deliveryMode=standard-gross&securityCode=123&paymentInfoId=${pid}"
	//		con = testUtil.getSecureConnection("/users/anonymous/orders?cartId=${cartGuid}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
	//		response = testUtil.verifiedJSONSlurper(con, false, false);
	//		assert response.created
	//		assert response.guestCustomer == true
	//	}

	//UTIL METHODS

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

	public String createAnonymousUsersCart() {
		def con = testUtil.getSecureConnection('/users/anonymous/carts?fields=guid', 'POST', 'XML', HttpURLConnection.HTTP_CREATED, null);

		def body = con.inputStream.text
		testUtil.verifyXML(body);

		def response = new XmlSlurper().parseText(body)
		assert response.guid;
		return response.guid;
	}

	public String createRegularUsersCart(String userId, auth) {
		def con = testUtil.getSecureConnection("/users/${userId}/carts?fields=code", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, null, null, auth);

		def body = con.inputStream.text
		testUtil.verifyXML(body);

		def response = new XmlSlurper().parseText(body)
		assert response.code;
		println('code: ' + response.code)
		return response.code;
	}

	public void addProductToAnonymousUsersCart(String cartGuid, productCode, int quantity = 1, String pickupStore = null, auth = null) {
		def postBody = "code=${productCode}&qty=${quantity}"
		if (pickupStore != null) {
			postBody += "&pickupStore=${pickupStore}"
		}

		def con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}/entries?fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, null, auth);

		def body = con.inputStream.text
		testUtil.verifyXML(body);

		def response = new XmlSlurper().parseText(body)
		assert response.quantityAdded == quantity;
		assert response.statusCode == 'success'
	}

	public void addProductToRegularUsersCart(String userId, String cartCode, productCode, int quantity = 1, String pickupStore = null, auth = null) {
		def postBody = "code=${productCode}&qty=${quantity}"
		if (pickupStore != null) {
			postBody += "&pickupStore=${pickupStore}"
		}

		def con = testUtil.getSecureConnection("/users/${userId}/carts/${cartCode}/entries?fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, null, auth);

		def body = con.inputStream.text
		testUtil.verifyXML(body);

		def response = new XmlSlurper().parseText(body)
		assert response.quantityAdded == quantity;
		assert response.statusCode == 'success'
	}

	@Test
	void testRestoreAndMergeCarts() {
		def con, response, response2
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		//create users cart and add products
		def cartCode = createRegularUsersCart(uid, access_token)
		addProductToRegularUsersCart(uid, cartCode, 1934795, 2, null, access_token)
		addProductToRegularUsersCart(uid, cartCode, 3429337, 2, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)
		assert response.totalItems == 2

		cartCode = createRegularUsersCart(uid, access_token)
		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)
		addProductToRegularUsersCart(uid, cartCode, 3429337, 1, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response2 = testUtil.verifiedJSONSlurper(con)
		def userCartGuid = response2.guid

		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
		addProductToAnonymousUsersCart(cartGuid, 1934795)
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response2 = testUtil.verifiedJSONSlurper(con)

		assert response.totalItems == 2

		//copy cart
		con = testUtil.getSecureConnection("/users/${uid}/carts?oldCartId=${cartGuid}&toMergeCartGuid=${userCartGuid}&fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, null, null, access_token)
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response2 = testUtil.verifiedJSONSlurper(con)

		assert response.totalItems == response2.totalItems
		assert response.totalPrice.currencyIso == response2.totalPrice.currencyIso
		assert response.totalPrice.value == response2.totalPrice.value
		assert response.totalUnitCount == response2.totalUnitCount
		assert response.entries[0].product.code == response2.entries[0].product.code
		assert response.entries[0].quantity == response2.entries[0].quantity
		assert response.entries[0].totalPrice.value == response2.entries[0].totalPrice.value

	}

	@Test
	void testRestoreAndMergeCartsOverStock() {
		def con, response, stock
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		con = testUtil.getSecureConnection("/products/3429337?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK) //some SD CARD
		response = testUtil.verifiedJSONSlurper(con)
		stock = response.stock.stockLevel.toInteger()

		//create users cart and add products
		def cartCode = createRegularUsersCart(uid, access_token)
		addProductToRegularUsersCart(uid, cartCode, 3429337, stock-1, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)
		assert response.totalItems == 1
		def userCartGuid = response.guid

		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
		addProductToAnonymousUsersCart(cartGuid, 3429337, stock)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response = testUtil.verifiedJSONSlurper(con)
		assert response.totalItems == 1

		//copy cart
		con = testUtil.getSecureConnection("/users/${uid}/carts?oldCartId=${cartGuid}&toMergeCartGuid=${userCartGuid}&fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.totalItems == 1
		assert response.entries[0].quantity == stock
	}

	@Test
	void testRestoreAndMergeCartsAndRemoveOld() {
		def con, response
		def usersTest = new UsersTest()

		def uid = usersTest.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		//create users cart and add products
		def cartCode = createRegularUsersCart(uid, access_token)
		addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)
		assert response.totalItems == 1
		def userCartGuid = response.guid

		def cartGuid = createAnonymousUsersCart()
		assert cartGuid;
		addProductToAnonymousUsersCart(cartGuid, 3429337)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response = testUtil.verifiedJSONSlurper(con, true)

		//copy cart
		con = testUtil.getSecureConnection("/users/${uid}/carts?oldCartId=${cartGuid}&toMergeCartGuid=${userCartGuid}&fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
	}


	//test for outside validity period scenario, this is not default configuration in spring so test is disabled by default
	/*@Test
	 void testRestoreAndMergeCartsOutsideValidity() {
	 def con, response, response2
	 def usersTest = new UsersTest()
	 def uid = usersTest.registerUser()
	 def access_token = testUtil.getAccessToken(uid, PASSWORD)
	 //create users cart and add products
	 def cartCode = createRegularUsersCart(uid, access_token)
	 addProductToRegularUsersCart(uid, cartCode, 1934795, 2, null, access_token)
	 addProductToRegularUsersCart(uid, cartCode, 3429337, 2, null, access_token)
	 con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
	 response = testUtil.verifiedJSONSlurper(con)
	 assert response.totalItems == 2
	 cartCode = createRegularUsersCart(uid, access_token)
	 addProductToRegularUsersCart(uid, cartCode, 1934795, 1, null, access_token)
	 addProductToRegularUsersCart(uid, cartCode, 3429337, 1, null, access_token)
	 con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
	 response2 = testUtil.verifiedJSONSlurper(con)
	 def userCartGuid = response2.guid
	 def cartGuid = createAnonymousUsersCart()
	 assert cartGuid;
	 addProductToAnonymousUsersCart(cartGuid, 1934795)
	 addProductToAnonymousUsersCart(cartGuid, 3429337)
	 con = testUtil.getSecureConnection("/users/anonymous/carts/${cartGuid}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
	 response2 = testUtil.verifiedJSONSlurper(con)
	 assert response.totalItems == 2
	 //copy cart
	 con = testUtil.getSecureConnection("/users/${uid}/carts?oldCartId=${cartGuid}&toMergeCartGuid=${userCartGuid}&fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, null, null, access_token)
	 response2 = testUtil.verifiedJSONSlurper(con)
	 def newCartCode = response2.code
	 con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, null, access_token)
	 con = testUtil.getSecureConnection("/users/${uid}/carts/${newCartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
	 response2 = testUtil.verifiedJSONSlurper(con)
	 assert response.totalItems == response2.totalItems
	 assert response.totalPrice.currencyIso == response2.totalPrice.currencyIso
	 assert response.totalPrice.value == response2.totalPrice.value
	 assert response.totalUnitCount == response2.totalUnitCount
	 assert response.entries[0].product.code == response2.entries[0].product.code
	 assert response.entries[0].quantity == response2.entries[0].quantity
	 assert response.entries[0].totalPrice.value == response2.entries[0].totalPrice.value
	 }*/

}
