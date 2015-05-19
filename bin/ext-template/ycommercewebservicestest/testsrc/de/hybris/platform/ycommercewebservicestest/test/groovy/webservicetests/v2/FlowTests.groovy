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
class FlowTests extends BaseWSTest {

	final password = "test"

	@Test
	void testCartFlowJSON() {
		//create customer and cart
		def userTests = new UsersTest()
		def cartTests = new CartTests()
		def uid = userTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)
		def cartId = cartTests.createRegularUsersCart(uid, access_token)
		def aid = userTests.createAddressJSON(uid, access_token)

		//add something to a cart
		def postBody = "code=3429337"
		def con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		def response = testUtil.verifiedJSONSlurper(con)

		assert response.statusCode == 'success'
		assert response.quantityAdded == 1
		assert response.entry.entryNumber == 0

		//test response.entry is an array and has size one
		//test that product with id 3429337 is in there

		//add another product, keep session
		postBody = "code=1934795&qty=2"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		response = testUtil.verifiedJSONSlurper(con)
		assert response.statusCode == 'success'
		assert response.quantityAdded == 2
		assert response.entry.entryNumber == 1

		//get cart, 2 entries in?
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		response = testUtil.verifiedJSONSlurper(con)
		//println body

		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 234.8

		//update the quantity for one item
		def updateBody = "qty=3"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries/0", 'PATCH', 'JSON', HttpURLConnection.HTTP_OK, updateBody, null, access_token)

		response = testUtil.verifiedJSONSlurper(con)
		//println body

		assert response.statusCode == "success"
		assert response.quantityAdded == 2
		assert response.quantity == 3

		//check cart again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		//println body
		assert response.totalItems == 2
		assert response.totalUnitCount == 5
		assert response.totalPrice.value == 257.04

		//remove one item
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries/0", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		//get cart again, only one item should be in
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		//println body
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		//HTTPS!!!
		//set delivery address  /{site}/cart/address/delivery/{id}
		//verify we need user/pass etc
		//		con = testUtil.getConnection("/users/${uid}/carts/${cartId}/addresses/delivery/" + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_UNAUTHORIZED, null, null, access_token)

		//second try, now with oauth
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		//remove the delivery address again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/addresses/delivery", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert !response.deliveryAddress
		//println body

		//check deliverymodes
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/deliverymodes", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert !response.deliveryModes: "If there is no delivery address, there should be no deliverymodes"

		//set address again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		//get cart just for fun and check that delivery address is in response
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"

		//again get deliverymodes
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/deliverymodes", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.deliveryModes
		assert response.deliveryModes.size() == 2
		assert response.deliveryModes[0].code == "standard-gross"
		assert response.deliveryModes[1].code == "premium-gross"

		//set a delivery mode
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/deliverymode?deliveryModeId=standard-gross", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		//delete deliverymode again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/deliverymode?deliveryModeId=standard-gross", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		//add deliverymode again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/deliverymode?deliveryModeId=standard-gross", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		//create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/paymentdetails", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		//con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

		response = testUtil.verifiedJSONSlurper(con, true)
		//response is cart now
		//println body
		assert response.id
		assert response.accountHolderName == 'Sven Haiges'
		assert response.cardType.code == 'visa'
		assert response.cardType.name == 'Visa'
		//assert response.defaultPaymentInfo == true BUG???

		//get all payment infos of current user, should be 1
		con = testUtil.getSecureConnection("/users/${uid}/paymentdetails", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.payments
		assert response.payments.size() > 0


		def paymentInfoID = response.payments[0].id

		//place order
		con = testUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartId}&securityCode=123", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.code: "No cart id (code)!"
		def orderNumber = response.code

		//get orders, no cookie required
		con = testUtil.getSecureConnection("/users/${uid}/orders", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.orders
		assert response.orders.size() == 1
		assert response.orders[0].code == orderNumber

		//access this specific order
		con = testUtil.getSecureConnection("/users/${uid}/orders/" + orderNumber, 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.code == orderNumber
		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 1
		assert response.totalPrice.value == 232.67 // changed, due to delivery cost +8.99
	}

	@Test
	void testPostPaymentInfoWithoutDeliveryModeSet() {

		//create customer and cart
		def userTests = new UsersTest()
		def cartTests = new CartTests()
		def uid = userTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)
		def cartId = cartTests.createRegularUsersCart(uid, access_token)
		def aid = userTests.createAddressJSON(uid, access_token)

		//add something to a cart
		def postBody = "code=3429337"
		def con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		def response = testUtil.verifiedJSONSlurper(con);

		assert response.statusCode == 'success'
		assert response.quantityAdded == 1
		assert response.entry.entryNumber == 0

		//test response.entry is an array and has size one
		//test that product with id 3429337 is in there

		//add another product, keep session
		postBody = "code=1934795&qty=2"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries?fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
		response = testUtil.verifiedJSONSlurper(con);

		assert response.statusCode == 'success'
		assert response.quantityAdded == 2
		assert response.entry.entryNumber == 1

		//get cart, 2 entries in?
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con);
		//println body

		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 234.8

		//update the quantity for one item
		def updateBody = "qty=3"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries/0", 'PATCH', 'JSON', HttpURLConnection.HTTP_OK, updateBody, null, access_token)
		response = testUtil.verifiedJSONSlurper(con);

		assert response.statusCode == "success"
		assert response.quantityAdded == 2
		assert response.quantity == 3

		//check cart again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con);
		//println body
		assert response.totalItems == 2
		assert response.totalUnitCount == 5
		assert response.totalPrice.value == 257.04

		//remove one item
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries/0", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		//get cart again, only one item should be in
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con);

		//println body
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		//second try, now with oauth
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		//remove the delivery address again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/addresses/delivery", 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con);

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert !response.deliveryAddress
		//println body

		/*		//check deliverymodes
		 con = testUtil.getSecureConnection('/cart/deliverymodes', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		 body = con.inputStream.text
		 verifyJSON(body)
		 response = new JsonSlurper().parseText(body)
		 assert response.deliveryModes.size() == 0 : "If there is no delivery address, there should be no deliverymodes"*/

		//set address again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/addresses/delivery?addressId=${aid}", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)

		//get cart just for fun and check that delivery address is in response
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con);

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"

		//create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/paymentdetails", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		//con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
		response = testUtil.verifiedJSONSlurper(con);
		//response is cart now
		//println body
		assert response.id
		assert response.accountHolderName == 'Sven Haiges'
		assert response.cardType.code == 'visa'
		assert response.cardType.name == 'Visa'
	}

	@Test
	void testCartFlowXML() {
		//create customer and cart
		def userTests = new UsersTest()
		def cartTests = new CartTests()
		def uid = userTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, password)
		def cartId = cartTests.createRegularUsersCart(uid, access_token)
		def aid = userTests.createAddress(uid, access_token)

		//add something to a cart
		def postBody = "code=3429337"
		def con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries?fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		def response = testUtil.verifiedXMLSlurper(con)

		assert response.statusCode == 'success'
		assert response.quantityAdded == 1
		assert response.entry.entryNumber == 0

		//test response.entry is an array and has size one
		//test that product with id 3429337 is in there

		//add another product, keep session
		postBody = "code=1934795&qty=2"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries?fields=${FULL_SET}", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)

		response = testUtil.verifiedXMLSlurper(con)
		assert response.statusCode == 'success'
		assert response.quantityAdded == 2
		assert response.entry.entryNumber == 1

		//get cart, 2 entries in?
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		response = testUtil.verifiedXMLSlurper(con)
		//println body

		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 234.8

		//update the quantity for one item
		def updateBody = "qty=3"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries/0", 'PATCH', 'XML', HttpURLConnection.HTTP_OK, updateBody, null, access_token)

		response = testUtil.verifiedXMLSlurper(con)
		//println body

		assert response.statusCode == "success"
		assert response.quantityAdded == 2
		assert response.quantity == 3

		//check cart again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		//println body
		assert response.totalItems == 2
		assert response.totalUnitCount == 5
		assert response.totalPrice.value == 257.04

		//remove one item
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/entries/0", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		//get cart again, only one item should be in
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		//println body
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		//HTTPS!!!
		//set delivery address  /{site}/cart/address/delivery/{id}
		//verify we need user/pass etc
		//		con = testUtil.getConnection("/users/${uid}/carts/${cartId}/addresses/delivery/" + aid, 'PUT', 'XML', HttpURLConnection.HTTP_UNAUTHORIZED, null, null, access_token)

		//second try, now with oauth
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/addresses/delivery?addressId=${aid}", 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		//remove the delivery address again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/addresses/delivery", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.size() == 0
		//println body

		//check deliverymodes
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/deliverymodes", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.deliveryModes.size() == 0: "If there is no delivery address, there should be no deliverymodes"

		//set address again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/addresses/delivery?addressId=${aid}", 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		//get cart just for fun and check that delivery address is in response
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"

		//again get deliverymodes
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/deliverymodes", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.deliveryModes
		assert response.deliveryModes.size() == 2
		assert response.deliveryModes[0].code == "standard-gross"
		assert response.deliveryModes[1].code == "premium-gross"

		//set a delivery mode
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/deliverymode?deliveryModeId=standard-gross", 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		//delete deliverymode again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/deliverymode?deliveryModeId=standard-gross", 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		//add deliverymode again
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/deliverymode?deliveryModeId=standard-gross", 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		//create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2113&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartId}/paymentdetails", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, postBody, null, access_token)
		//con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

		response = testUtil.verifiedXMLSlurper(con, true)
		//response is cart now
		//println body
		assert response.id
		assert response.accountHolderName == 'Sven Haiges'
		assert response.cardType.code == 'visa'
		assert response.cardType.name == 'Visa'
		//assert response.defaultPaymentInfo == true BUG???

		//get all payment infos of current user, should be 1
		con = testUtil.getSecureConnection("/users/${uid}/paymentdetails", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.payments
		assert response.payments.size() > 0


		def paymentInfoID = response.payments[0].id

		//place order
		con = testUtil.getSecureConnection("/users/${uid}/orders?cartId=${cartId}&securityCode=123", 'POST', 'XML', HttpURLConnection.HTTP_CREATED, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.code: "No cart id (code)!"
		def orderNumber = response.code

		//get orders, no cookie required
		con = testUtil.getSecureConnection("/users/${uid}/orders", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.orders
		assert response.orders.size() == 1
		assert response.orders[0].code == orderNumber

		//access this specific order
		con = testUtil.getSecureConnection("/users/${uid}/orders/" + orderNumber, 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.code == orderNumber
		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 1
		assert response.totalPrice.value == 232.67 // changed, due to delivery cost +8.99
	}
}