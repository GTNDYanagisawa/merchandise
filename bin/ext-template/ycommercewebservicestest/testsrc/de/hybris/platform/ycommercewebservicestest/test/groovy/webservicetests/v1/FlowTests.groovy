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
class FlowTests extends BaseWSTest {

	final password = "test"

	@Test
	void testCartFlowJSON() {
		//create customer and cart
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)
		def aid = customerTests.createAddressJSON(access_token)

		//add something to a cart
		def postBody = "code=3429337"
		def con = testUtil.getConnection('/cart/entry', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody)

		def response = testUtil.verifiedJSONSlurper(con)

		assert response.statusCode == 'success'
		assert response.quantityAdded == 1
		assert response.entry.entryNumber == 0

		//test response.entry is an array and has size one
		//test that product with id 3429337 is in there

		//check for cookie, extract it for next request
		def cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie : 'No cookie present, cannot keep session'
		def cookieNoPath = cookie.split(';')[0]
		//println cookieNoPath


		//add another product, keep session
		postBody = "code=1934795&qty=2"
		con = testUtil.getConnection('/cart/entry', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con)
		assert response.statusCode == 'success'
		assert response.quantityAdded == 2
		assert response.entry.entryNumber == 1


		//get cart, 2 entries in?
		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)

		response = testUtil.verifiedJSONSlurper(con)
		//println body

		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 234.8

		//update the quantity for one item
		def updateBody="qty=3"
		con = testUtil.getConnection('/cart/entry/0', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, updateBody, cookieNoPath)

		response = testUtil.verifiedJSONSlurper(con)
		//println body

		assert response.statusCode == "success"
		assert response.quantityAdded == 2
		assert response.quantity == 3

		//check cart again
		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con)

		//println body
		assert response.totalItems == 2
		assert response.totalUnitCount == 5
		assert response.totalPrice.value == 257.04


		//remove one item
		con = testUtil.getConnection('/cart/entry/0', 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.statusCode == 'success'
		assert response.quantityAdded == -3
		assert response.quantity == 0

		//get cart again, only one item should be in
		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con)

		//println body
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		//HTTPS!!!
		//set delivery address  /{site}/cart/address/delivery/{id}
		//verify we need user/pass etc
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_UNAUTHORIZED, null, cookieNoPath)


		//second try, now with oauth
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"

		//update of cookie required!!
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie : 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		//remove the delivery address again
		con = testUtil.getSecureConnection('/cart/address/delivery', 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.totalItems == 1
		assert response.totalPrice.value == 223.68




		con = testUtil.getSecureConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert !response.deliveryAddress
		//println body

		//check deliverymodes
		con = testUtil.getSecureConnection('/cart/deliverymodes', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.deliveryModes.size() == 0 : "If there is no delivery address, there should be no deliverymodes"

		//set address again
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"

		//get cart just for fun and check that delivery address is in response
		con = testUtil.getSecureConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"

		//again get deliverymodes
		con = testUtil.getSecureConnection('/cart/deliverymodes', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.deliveryModes.size() == 2
		assert response.deliveryModes[0].code == "standard-gross"
		assert response.deliveryModes[1].code == "premium-gross"

		//set a delivery mode
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'standard-gross', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		//println body
		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 232.67 // changed, due to delivery cost +8.99
		assert response.deliveryAddress.id == "$aid"

		//delete deliverymode again
		con = testUtil.getSecureConnection('/cart/deliverymodes/', 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert !response.deliveryMode : 'Delivery mode should have been deleted'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68 // changed, back to price withput delivery cost
		assert response.deliveryAddress.id == "$aid"


		//add deliverymode again
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'standard-gross', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		//println body
		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 232.67 // changed, due to delivery cost +8.99
		assert response.deliveryAddress.id == "$aid"


		//create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2013&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		//con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

		response = testUtil.verifiedJSONSlurper(con,true)
		//response is cart now
		//println body
		assert response.paymentInfo.id
		assert response.paymentInfo.accountHolderName == 'Sven Haiges'
		assert response.paymentInfo.cardType.code == 'visa'
		assert response.paymentInfo.cardType.name == 'Visa'
		//assert response.defaultPaymentInfo == true BUG???

		//get all payment infos of current user, should be 1
		con = testUtil.getSecureConnection("/customers/current/paymentinfos", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.paymentInfos.size() > 0


		def paymentInfoID = response.paymentInfos[0].id

		//assign payment info to cart
		con = testUtil.getSecureConnection("/cart/paymentinfo/" + paymentInfoID, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)
		assert response.paymentInfo.id == paymentInfoID

		//authorize cart
		postBody= "securityCode=123"
		con = testUtil.getSecureConnection("/cart/authorize", 'POST', 'JSON', HttpURLConnection.HTTP_ACCEPTED, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 232.67 // changed, due to delivery cost +8.99
		assert response.deliveryAddress.id == "$aid"


		//place order
		con = testUtil.getSecureConnection("/cart/placeorder", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.code : "No cart id (code)!"
		def orderNumber = response.code

		//get orders, no cookie required
		con = testUtil.getSecureConnection("/orders", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.orders.size() == 1
		assert response.orders[0].code == orderNumber


		//access this specific order
		con = testUtil.getSecureConnection("/orders/" + orderNumber, 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
		response = testUtil.verifiedJSONSlurper(con)

		assert response.code == orderNumber
		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 1
		assert response.totalPrice.value == 232.67 // changed, due to delivery cost +8.99

		//logout //no site element!!
		con = testUtil.getSecureURLConnection(config.DEFAULT_HTTPS_URI + config.BASE_PATH + "/customers/current/logout", 'POST')
		testUtil.acceptJSON(con)
		testUtil.cookieString(con, cookieNoPath)
		testUtil.basicAuth(con, "${uid}", "${password}")
		assert con.responseCode == HttpURLConnection.HTTP_OK : testUtil.messageResponseCode(con.responseCode, HttpURLConnection.HTTP_OK)

		//def body = con.inputStream.text
		//verifyJSON(body)
		response = testUtil.verifiedJSONSlurper(con)
	}

	@Test
	void testPostPaymentInfoWithoutDeliveryModeSet() {

		//create customer and cart
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUserJSON()
		def access_token = testUtil.getAccessToken(uid, password)
		def aid = customerTests.createAddressJSON(access_token)

		//add something to a cart
		def postBody = "code=3429337"
		def con = testUtil.getConnection('/cart/entry', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody)
		def response = testUtil.verifiedJSONSlurper(con);

		assert response.statusCode == 'success'
		assert response.quantityAdded == 1
		assert response.entry.entryNumber == 0

		//test response.entry is an array and has size one
		//test that product with id 3429337 is in there

		//check for cookie, extract it for next request
		def cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie : 'No cookie present, cannot keep session'
		def cookieNoPath = cookie.split(';')[0]
		//println cookieNoPath


		//add another product, keep session
		postBody = "code=1934795&qty=2"
		con = testUtil.getConnection('/cart/entry', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con);

		assert response.statusCode == 'success'
		assert response.quantityAdded == 2
		assert response.entry.entryNumber == 1

		//get cart, 2 entries in?
		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con);
		//println body

		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 234.8

		//update the quantity for one item
		def updateBody="qty=3"
		con = testUtil.getConnection('/cart/entry/0', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, updateBody, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con);

		assert response.statusCode == "success"
		assert response.quantityAdded == 2
		assert response.quantity == 3

		//check cart again
		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con);
		//println body
		assert response.totalItems == 2
		assert response.totalUnitCount == 5
		assert response.totalPrice.value == 257.04

		//remove one item
		con = testUtil.getConnection('/cart/entry/0', 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con);

		assert response.statusCode == 'success'
		assert response.quantityAdded == -3
		assert response.quantity == 0

		//get cart again, only one item should be in
		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedJSONSlurper(con);

		//println body
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		//HTTPS!!!
		//set delivery address  /{site}/cart/address/delivery/{id}
		//verify we need user/pass etc
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_UNAUTHORIZED, null, cookieNoPath)


		//second try, now with oauth
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con);

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"

		//update of cookie required!!
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie : 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		//remove the delivery address again
		con = testUtil.getSecureConnection('/cart/address/delivery', 'DELETE', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con);

		assert response.totalItems == 1
		assert response.totalPrice.value == 223.68

		con = testUtil.getSecureConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
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
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con);

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"

		//get cart just for fun and check that delivery address is in response
		con = testUtil.getSecureConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedJSONSlurper(con);

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"

		//create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2013&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		//con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
		response = testUtil.verifiedJSONSlurper(con);
		//response is cart now
		//println body
		assert response.paymentInfo.id
		assert response.paymentInfo.accountHolderName == 'Sven Haiges'
		assert response.paymentInfo.cardType.code == 'visa'
		assert response.paymentInfo.cardType.name == 'Visa'
	}

	@Test
	void testCartFlowXML() {
		//create customer and cart
		def  customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, password)
		def aid = customerTests.createAddress(access_token)

		//add something to a cart
		def postBody = "code=3429337"
		def con = testUtil.getConnection('/cart/entry', 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody)
		def response = testUtil.verifiedXMLSlurper(con)

		assert response.statusCode == 'success'
		assert response.quantityAdded == 1
		assert response.entry.entryNumber == 0


		//check for cookie, extract it for next request
		def cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie : 'No cookie present, cannot keep session'
		def cookieNoPath = cookie.split(';')[0]
		//println cookieNoPath


		//add another product, keep session
		postBody = "code=1934795&qty=2"
		con = testUtil.getConnection('/cart/entry', 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, cookieNoPath)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.statusCode == 'success'
		assert response.quantityAdded == 2
		assert response.entry.entryNumber == 1


		//get cart, 2 entries in?
		con = testUtil.getConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 234.8

		//update the quantity for one item
		con = testUtil.getConnection('/cart/entry/0?qty=3', 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.statusCode == "success"
		assert response.quantityAdded == 2
		assert response.quantity == 3

		//check cart again
		con = testUtil.getConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedXMLSlurper(con)

		//println body
		assert response.totalItems == 2
		assert response.totalUnitCount == 5
		assert response.totalPrice.value == 257.04


		//remove one item
		con = testUtil.getConnection('/cart/entry/0', 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.statusCode == 'success'
		assert response.quantityAdded == -3
		assert response.quantity == 0

		//get cart again, only one item should be in
		con = testUtil.getConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath)
		response = testUtil.verifiedXMLSlurper(con)

		//println body
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		//HTTPS!!!
		//set delivery address  /{site}/cart/address/delivery/{id}
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'XML', HttpURLConnection.HTTP_UNAUTHORIZED, null, cookieNoPath)

		//second try, now with basic auth
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"

		//update of cookie required!!
		cookie = con.getHeaderField('Set-Cookie')
		println cookie
		assert cookie : 'No cookie present, cannot keep session'
		cookieNoPath = cookie.split(';')[0]

		//remove the delivery address again
		con = testUtil.getSecureConnection('/cart/address/delivery', 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		//cart is here
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68

		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		//println body
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress == "" //a bit weird, but seems xml slurper for tag not present


		//check deliverymodes
		con = testUtil.getSecureConnection('/cart/deliverymodes', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.deliveryModes == "" : "If there is no delivery address, there should be no deliverymodes"


		//set address again
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"

		//get cart just for fun and check that delivery address is in response
		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68
		assert response.deliveryAddress.id == "$aid"


		//again get deliverymodes
		con = testUtil.getSecureConnection('/cart/deliverymodes', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.deliveryMode.size() == 2
		assert response.deliveryMode[0].code == "standard-gross"
		assert response.deliveryMode[1].code == "premium-gross"

		//set a delivery mode
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'standard-gross', 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		//println body
		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 232.67 // changed, due to delivery cost +8.99
		assert response.deliveryAddress.id == "$aid"

		//delete deliverymode again
		con = testUtil.getSecureConnection('/cart/deliverymodes/', 'DELETE', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.deliveryMode == "" : 'Delivery mode should have been deleted'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 223.68 // changed, back to price withput delivery cost
		assert response.deliveryAddress.id == "$aid"


		//add deliverymode again
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'standard-gross', 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath,  access_token)
		response = testUtil.verifiedXMLSlurper(con)

		//println body
		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 232.67 // changed, due to delivery cost +8.99
		assert response.deliveryAddress.id == "$aid"


		//create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2013&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'XML', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		//response is now cart
		assert response.paymentInfo.id
		assert response.paymentInfo.accountHolderName == 'Sven Haiges'
		assert response.paymentInfo.cardType.code == 'visa'
		assert response.paymentInfo.cardType.name == 'Visa'

		//assert response.defaultPaymentInfo == true BUG???


		//get cart just for fun and check that payment info is in the response
		con = testUtil.getSecureConnection('/cart', 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 232.67
		assert response.deliveryAddress.id == "$aid"
		assert response.paymentInfo.id
		assert response.paymentInfo.accountHolderName == 'Sven Haiges'
		assert response.paymentInfo.cardType.code == 'visa'
		assert response.paymentInfo.cardType.name == 'Visa'


		//get all payment infos of current user, should be 1
		con = testUtil.getSecureConnection("/customers/current/paymentinfos", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.creditCardPaymentInfo.size() > 0


		def paymentInfoID = response.creditCardPaymentInfo[0].id

		//assign payment info to cart
		con = testUtil.getSecureConnection("/cart/paymentinfo/" + paymentInfoID, 'PUT', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		response = testUtil.verifiedXMLSlurper(con)

		assert response.paymentInfo.id == paymentInfoID

		//authorize cart
		postBody = "securityCode=123"
		con = testUtil.getSecureConnection("/cart/authorize", 'POST', 'XML', HttpURLConnection.HTTP_ACCEPTED, postBody, cookieNoPath, access_token)

		response = testUtil.verifiedXMLSlurper(con)

		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 1
		assert response.totalUnitCount == 2
		assert response.totalPrice.value == 232.67 // changed, due to delivery cost +8.99
		assert response.deliveryAddress.id == "$aid"



		//place order
		con = testUtil.getSecureConnection("/cart/placeorder", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
		//	println con.inputStream.text

		response = testUtil.verifiedXMLSlurper(con)
		assert response.code : "No cart id (code)!"
		def orderNumber = response.code

		//get orders
		con = testUtil.getSecureConnection("/orders", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		response = testUtil.verifiedXMLSlurper(con)

		assert response.order.size() == 1
		assert response.order[0].code == orderNumber

		//access this specific order
		con = testUtil.getSecureConnection("/orders/" + orderNumber, 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)

		response = testUtil.verifiedXMLSlurper(con)

		assert response.code == orderNumber
		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 1
		assert response.totalPrice.value == 232.67 // changed, due to delivery cost +8.99

		//logout //no site element!!
		con = testUtil.getSecureURLConnection(config.DEFAULT_HTTPS_URI + config.BASE_PATH + "/customers/current/logout", 'POST')
		testUtil.acceptXML(con)
		testUtil.cookieString(con, cookieNoPath)
		testUtil.basicAuth(con, "${uid}", "${password}")
		assert con.responseCode == HttpURLConnection.HTTP_OK : testUtil.messageResponseCode(con.responseCode, HttpURLConnection.HTTP_OK)

		//def body = con.inputStream.text
		//println body
		//verifyXML(body)

		testUtil.verifiedXMLSlurper(con)

		//response = new JsonSlurper().parseText(body)
	}
}