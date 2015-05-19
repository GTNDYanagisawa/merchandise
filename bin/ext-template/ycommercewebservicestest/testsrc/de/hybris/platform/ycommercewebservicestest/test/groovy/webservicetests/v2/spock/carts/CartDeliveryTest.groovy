/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import de.hybris.bootstrap.annotations.ManualTest

import spock.lang.Unroll
import groovyx.net.http.HttpResponseDecorator
/**
 *
 *`
 */
@Unroll
@ManualTest
class CartDeliveryTest extends AbstractCartTest {


	def "Customer tries to set invalid delivery address for cart: #format"(){
		given: "a customer with cart and product in the cart"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_FLEXI_TRIPOD)

		when: "a customer tries to use invalid address as delivery address"
		HttpResponseDecorator response = restClient.put(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/' + cart.code + '/addresses/delivery',
				query : ['addressId' : 'Definetely_wrong_address'],
				contentType : format,
				requestContentType : URLENC
				)

		then: "he receives an error"
		with(response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CartAddressError'
			data.errors[0].reason == 'notValid'
			data.errors[0].subjectType == 'address'
		}

		where:
		format << [XML, JSON]
	}

	def "Customer sets delivery address for a cart: #format"(){
		given: "a customer with cart and an address"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		def address = createAddress(restClient, customer)

		when: "a customer tries to set delivery address"
		HttpResponseDecorator response = restClient.put(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/' + cart.code + '/addresses/delivery',
				query : [ 'addressId' : address.id ],
				contentType : format,
				requestContentType : URLENC
				)

		then: "address is successfully set"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}

		where:
		format << [XML, JSON]
	}

	def "Customer create delivery address for a cart when request: #requestFormat and response: #responseFormat"(){
		given: "anonymous customer with cart, product in the cart and email"
		authorizeClient(restClient)
		def anonymous = ['id' : 'anonymous']
		def userGuid = System.currentTimeMillis() + '@test.com'
		def cart = createAnonymousCart(restClient, responseFormat)
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_POWER_SHOT_A480)
		addProductToCartPickup(restClient, anonymous, cart.guid, PRODUCT_M340, STORE_SHINBASHI)
		addEmailToAnonymousCart(restClient, cart.guid, userGuid, responseFormat) //setting email address on cart makes it recognized as guest cart

		when: "quest user create address"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/anonymous/carts/'+ cart.guid + "/addresses/delivery",
				body : postBody,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				contentType : responseFormat,
				requestContentType : requestFormat
				)

		then: "delivery address is created"
		println response
		with(response){
			status == SC_CREATED
			isNotEmpty(data.id)
			data.titleCode == 'mrs'
			data.firstName == 'Jane'
			data.lastName == 'Smith'
			data.line1 == 'Nymphenburger Str. 86 - Maillingerstrasse'
			data.line2 == 'pietro 2'
			data.postalCode == '80331'
			data.town == 'Muenchen'
			data.country.isocode == 'DE'
		}

		where:
		requestFormat | responseFormat | postBody
		URLENC | XML | GOOD_ADDRESS_DE
		URLENC | JSON | GOOD_ADDRESS_DE
		JSON | JSON | GOOD_ADDRESS_DE_JSON
		XML | XML | GOOD_ADDRESS_DE_XML
	}

	def "Customer removes delivery address from a cart: #format"(){
		given: "a customer with cart and set delivery address"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		def address = createAddress(restClient, customer)
		setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)

		when: "a customer tries to remove delivery address"
		HttpResponseDecorator response = restClient.delete(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/' + cart.code + '/addresses/delivery',
				contentType : format,
				requestContentType : URLENC
				)

		cart = getCart(restClient, customer, cart.code, format)

		then: "address is successfully set"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}
		with(cart){!isNotEmpty(deliveryAddress) }

		where:
		format << [XML, JSON]
	}

	def "Delivery address cannot be set  by anonymous user when request: #requestFormat and response: #responseFormat"(){
		given: "anonymous customer with cart and product in the cart"
		authorizeClient(restClient)
		def anonymous = ['id' : 'anonymous']
		def cart = createAnonymousCart(restClient, responseFormat)
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_POWER_SHOT_A480)
		addProductToCartPickup(restClient, anonymous, cart.guid, PRODUCT_M340, STORE_SHINBASHI)

		when: "anonymous tries to set delivery mode"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/anonymous/carts/'+ cart.guid + "/addresses/delivery",
				body : postBody,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				contentType : responseFormat,
				requestContentType : requestFormat
				)

		then: "he receives an error"
		with(response){ status == SC_UNAUTHORIZED }

		where:
		requestFormat | responseFormat | postBody
		URLENC | XML | GOOD_ADDRESS_DE
		URLENC | JSON | GOOD_ADDRESS_DE
		JSON | JSON | GOOD_ADDRESS_DE_JSON
		XML | XML | GOOD_ADDRESS_DE_XML
	}

	def "Customer tries to create delivery address without country field when request: #requestFormat and response: #responseFormat"(){
		given: "anonymous customer with cart, product in the cart and email"
		authorizeClient(restClient)
		def anonymous = ['id' : 'anonymous']
		def userGuid = System.currentTimeMillis() + '@test.com'
		def cart = createAnonymousCart(restClient, responseFormat)
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_POWER_SHOT_A480)
		addProductToCartPickup(restClient, anonymous, cart.guid, PRODUCT_M340, STORE_SHINBASHI)
		addEmailToAnonymousCart(restClient, cart.guid, userGuid, responseFormat) //setting email address on cart makes it recognized as guest cart

		when: "quest user tries to create address without country"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/anonymous/carts/'+ cart.guid + "/addresses/delivery",
				body : postBody,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				contentType : responseFormat,
				requestContentType : requestFormat
				)

		then: "ValidationError is returned"
		with(response){
			println data
			status == SC_BAD_REQUEST
			data.errors[0].type == 'ValidationError'
			data.errors[0].subjectType == 'parameter'
			data.errors[0].subject == 'country.isocode'
			data.errors[0].reason == 'missing'
			data.errors[0].message == 'This field is required and must to be between 1 and 2 characters long.'
		}

		where:
		requestFormat | responseFormat | postBody
		URLENC | XML | ['town' : 'Muenchen']
		URLENC | JSON | ['town' : 'Muenchen']
		JSON | JSON | "{ \"town\" : \"Muenchen\"}"
		XML | XML | "<?xml version=\"1.0\" encoding=\"UTF-8\"?><address><town>Muenchen</town></address>"
	}

	def "Customer tries to create delivery address without required fields when request: #requestFormat and response: #responseFormat"(){
		given: "anonymous customer with cart, product in the cart and email"
		authorizeClient(restClient)
		def anonymous = ['id' : 'anonymous']
		def userGuid = System.currentTimeMillis() + '@test.com'
		def cart = createAnonymousCart(restClient, responseFormat)
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_POWER_SHOT_A480)
		addProductToCartPickup(restClient, anonymous, cart.guid, PRODUCT_M340, STORE_SHINBASHI)
		addEmailToAnonymousCart(restClient, cart.guid, userGuid, responseFormat) //setting email address on cart makes it recognized as guest cart

		when: "quest user tries to create address without required fields"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/anonymous/carts/'+ cart.guid + "/addresses/delivery",
				body : postBody,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				contentType : responseFormat,
				requestContentType : requestFormat
				)

		then: "ValidationError is returned"
		with(response){
			println data
			status == SC_BAD_REQUEST
			data.errors[0].type == 'ValidationError'
			data.errors[0].subjectType == 'parameter'
			data.errors[0].subject == 'firstName'
			data.errors[0].reason == 'missing'
			data.errors[0].message == 'This field is required and must to be between 1 and 255 characters long.'

			data.errors[1].type == 'ValidationError'
			data.errors[1].subjectType == 'parameter'
			data.errors[1].subject == 'lastName'
			data.errors[1].reason == 'missing'
			data.errors[1].message == 'This field is required and must to be between 1 and 255 characters long.'

			data.errors[2].type == 'ValidationError'
			data.errors[2].subjectType == 'parameter'
			data.errors[2].subject == 'line1'
			data.errors[2].reason == 'missing'
			data.errors[2].message == 'This field is required and must to be between 1 and 255 characters long.'

			data.errors[3].type == 'ValidationError'
			data.errors[3].subjectType == 'parameter'
			data.errors[3].subject == 'postalCode'
			data.errors[3].reason == 'missing'
			data.errors[3].message == 'This field is required and must to be between 1 and 10 characters long.'

			data.errors[4].type == 'ValidationError'
			data.errors[4].subjectType == 'parameter'
			data.errors[4].subject == 'titleCode'
			data.errors[4].reason == 'missing'
			data.errors[4].message == 'This field is required and must to be between 1 and 255 characters long.'
		}

		where:
		requestFormat | responseFormat | postBody
		URLENC | XML | BAD_ADDRESS_DE
		URLENC | JSON | BAD_ADDRESS_DE
		JSON | JSON | BAD_ADDRESS_DE_JSON
		XML | XML | BAD_ADDRESS_DE_XML
	}

	def "Customer retrieves supported delivery modes: #format"(){
		given: "a customer with cart with product in it and delivery address set"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		def address = createAddress(restClient, customer)
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_FLEXI_TRIPOD)
		setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)
		cart = getCart(restClient, customer, cart.code, format)

		when: "a customer retrieves available delivery modes"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/' + cart.code + '/deliverymodes',
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				contentType : format,
				requestContentType : URLENC
				)

		then: "he receives an error"
		cart.deliveryAddress.id == address.id
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			isNotEmpty(data.deliveryModes)
			data.deliveryModes.size() == 2
		}
		def standardDeliveryMode = response.data.deliveryModes.find { it.code == 'standard-gross' }
		with(standardDeliveryMode){
			deliveryCost.value == 8.99
			deliveryCost.formattedValue == '$8.99'
		}

		where:
		format << [XML, JSON]
	}

	def "Delivery mode cannot be set  by anonymous user: #format"(){
		given: "anonymous customer with cart and product in the cart"
		authorizeClient(restClient)
		def anonymous = ['id' : 'anonymous']
		def cart = createAnonymousCart(restClient, format)
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_POWER_SHOT_A480)
		addProductToCartPickup(restClient, anonymous, cart.guid, PRODUCT_M340, STORE_SHINBASHI)

		when: "anonymous tries to set delivery mode"
		HttpResponseDecorator response = restClient.put(
				path : getBasePathWithSite() + '/users/anonymous/carts/' + cart.guid + '/deliverymode',
				query : [ 'deliveryModeId' : DELIVERY_STANDARD ],
				contentType : format,
				requestContentType : URLENC
				)
		then: "he receives an error"
		with(response){ status == SC_UNAUTHORIZED }

		where:
		format << [XML, JSON]
	}


	def "Customer sets delivery mode for the cart: #format"(){
		given: "a customer with cart that has a product and address set"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		def address = createAddress(restClient, customer)
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_FLEXI_TRIPOD)
		setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)

		when: "customer selects a delivery mode for cart"
		HttpResponseDecorator response = restClient.put(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/' + cart.code + '/deliverymode',
				query : [ 'deliveryModeId' : DELIVERY_STANDARD ],
				contentType : format,
				requestContentType : URLENC
				)
		cart = getCart(restClient, customer, cart.code, format)

		then: "his selection should be stored"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}
		with(cart){ deliveryMode.code == DELIVERY_STANDARD }

		where:
		format << [XML, JSON]
	}

	def "Customer clears delivery mode for the cart: #format"(){
		given: "a customer with cart that has delivery mode set"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		def address = createAddress(restClient, customer)
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_FLEXI_TRIPOD)
		setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)
		setDeliveryModeForCart(restClient, customer, cart.code, DELIVERY_STANDARD, format)

		when: "customer clears delivery mode from cart"
		HttpResponseDecorator response = restClient.delete(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/' + cart.code + '/deliverymode',
				contentType : format,
				requestContentType : URLENC
				)
		cart = getCart(restClient, customer, cart.code, format)

		then: "delivery mode should be removed"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}
		with(cart){ !isNotEmpty(deliveryMode) }

		where:
		format << [XML, JSON]
	}
}
