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
import groovy.json.JsonSlurper
import org.junit.Test

@ManualTest
class OCCDemo_Oauth2 extends BaseWSTest {


	/*
	 * 1 Register a new user: login=${login}&password=${password}&firstName=Sven&lastName=Haiges&titleCode=mr
	 * 2 Add 1x 3429337 to the cart (FLEXI-TRIPOD)
	 * 3 Add 2x 1934795 to the cart (POWERSHOT A480)
	 * 4 Get Cart, check total of 2 items and 3 units, price is $234.8
	 * 5 Create Delivery Address: line1=Some+Street+1&town=SomeTown&country.isocode=DE
	 * 6 Use the created delivery address for this cart
	 * 7 Choose standard-gross delivery mode - TOTAL is now 243.79 (+8.99)
	 * 8 Create PaymentInfo: accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2013&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE
	 * 9 Authorize Cart: 123
	 * 10 Place order
	 * 11 Retrieve orders (not supported by UI?)
	 */
	@Test
	void testSimpleFlow() {

		//1 - register a new user
		def client_credentials_token = testUtil.getClientCredentialsToken()
		def login = System.currentTimeMillis() + '@hybris.com'
		def password = 'test'
		def postBody = "login=${login}&password=${password}&firstName=Sven&lastName=Haiges&titleCode=mr"
		def con = testUtil.getSecureConnection("/customers", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, postBody, null, client_credentials_token)


		//2 - add something to a cart
		postBody = "code=3429337"
		con = testUtil.getConnection('/cart/entry', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody)
		println con.getHeaderField('Set-Cookie')

		def body = con.inputStream.text
		//println body
		def response = new JsonSlurper().parseText(body)
		assert response.statusCode == 'success'
		assert response.quantityAdded == 1
		assert response.entry.entryNumber == 0


		//check for cookie, extract it for next request
		def cookie = con.getHeaderField('Set-Cookie')
		assert cookie : 'No cookie present!'
		def cookieNoPath = cookie.split(';')[0]

		//3 - add another product, keep session for same cart
		postBody = "code=1934795&qty=2"
		con = testUtil.getConnection('/cart/entry', 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath)

		body = con.inputStream.text
		response = new JsonSlurper().parseText(body)
		assert response.statusCode == 'success'
		assert response.quantityAdded == 2
		assert response.entry.entryNumber == 1


		//4 - get cart again, just for fun
		con = testUtil.getConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)

		body = con.inputStream.text
		println body
		response = new JsonSlurper().parseText(body)


		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 234.8

		def access_token = testUtil.getAccessToken(login, password);

		//5 - create a new address for delivery
		postBody = "titleCode=dr&firstName=John&lastName=John&line1=Some+Street+1&town=SomeTown&country.isocode=DE&postalCode=44100"
		con = testUtil.getSecureConnection("/customers/current/addresses", 'POST', 'JSON', HttpURLConnection.HTTP_OK, postBody, cookieNoPath, access_token)
		println con.getHeaderField('Set-Cookie')

		response = new JsonSlurper().parseText(con.inputStream.text)
		assert response.line1 == "Some Street 1"
		assert response.town == "SomeTown"
		assert response.country.isocode == "DE"

		def aid = response.id

		//update of cookie required!! session fixation via Spring Security
		cookie = con.getHeaderField('Set-Cookie')
		assert cookie : 'No cookie present!'
		cookieNoPath = cookie.split(';')[0]

		//6 - set delivery address
		con = testUtil.getSecureConnection('/cart/address/delivery/' + aid, 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)

		body = con.inputStream.text
		response = new JsonSlurper().parseText(body)
		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 234.8
		assert response.deliveryAddress.id == "$aid"




		//7 - set a delivery mode
		con = testUtil.getSecureConnection('/cart/deliverymodes/' + 'standard-gross', 'PUT', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)

		body = con.inputStream.text
		response = new JsonSlurper().parseText(body)
		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 243.79 // changed, due to delivery cost +8.99
		assert response.deliveryAddress.id == "$aid"

		//8 - create a paymentinfo for this cart
		postBody = "accountHolderName=Sven+Haiges&cardNumber=4111111111111111&cardType=visa&expiryMonth=01&expiryYear=2013&saved=true&defaultPaymentInfo=true&billingAddress.titleCode=mr&billingAddress.firstName=sven&billingAddress.lastName=haiges&billingAddress.line1=test1&billingAddress.line2=test2&billingAddress.postalCode=12345&billingAddress.town=somecity&billingAddress.country.isocode=DE"
		con = testUtil.getSecureConnection("/cart/paymentinfo", 'POST', 'JSON', HttpURLConnection.HTTP_OK , postBody, cookieNoPath )

		body = con.inputStream.text
		response = new JsonSlurper().parseText(body)
		assert response.paymentInfo.id
		assert response.paymentInfo.accountHolderName == 'Sven Haiges'
		assert response.paymentInfo.cardType.name == 'Visa'


		//9 - authorize cart
		postBody = "securityCode=123"
		con = testUtil.getSecureConnection("/cart/authorize", 'POST', 'JSON', HttpURLConnection.HTTP_ACCEPTED, postBody, cookieNoPath)

		body = con.inputStream.text
		response = new JsonSlurper().parseText(body)
		assert response.deliveryMode.code == 'standard-gross'
		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.totalPrice.value == 243.79 // changed, due to delivery cost +8.99
		assert response.deliveryAddress.id == "$aid"


		//10 - place order
		con = testUtil.getSecureConnection("/cart/placeorder", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)

		body = con.inputStream.text
		response = new JsonSlurper().parseText(body)
		assert response.code : "No cart id (code)!"
		def orderNumber = response.code

		//11 - get orders
		//stateless
		con = testUtil.getSecureConnection("/orders", 'GET', 'JSON', HttpURLConnection.HTTP_OK , null, null, access_token)

		body = con.inputStream.text
		//println body
		response = new JsonSlurper().parseText(body)
		assert response.orders.size() == 1
		assert response.orders[0].code == orderNumber
	}
}