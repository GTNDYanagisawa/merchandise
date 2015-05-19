/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts
import de.hybris.bootstrap.annotations.ManualTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.*
/**
 *
 *`
 */
@Unroll
@ManualTest
class CartResourceTest extends AbstractCartTest {

	def "Anonymous user creates a cart: #format"(){
		given: "a trusted client"
		authorizeTrustedClient(restClient)

		when: "anonymous (not logged in) user wants to create a cart for himself"
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + '/users/anonymous/carts',
				contentType: format,
				requestContentType: URLENC
				)

		then: "he can create such a cart and newly created cart is empty"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_CREATED
			data.totalPrice.value == 0.0
			!isNotEmpty(data.entries)
			isNotEmpty(data.guid)
			isNotEmpty(data.code)
		}

		where:
		format <<[XML, JSON]
	}

	def "Subsequent calls to cart creation for anonymous user do not return the same cart: #format"(){
		given: "not trusted client"
		authorizeClient(restClient)

		when: "two requests for cart creation are made"
		HttpResponseDecorator response1 = restClient.post(
				path: getBasePathWithSite() + '/users/anonymous/carts',
				contentType: format,
				requestContentType: URLENC
				)

		HttpResponseDecorator response2 = restClient.post(
				path: getBasePathWithSite() + '/users/anonymous/carts',
				contentType: format,
				requestContentType: URLENC
				)

		then:"two retuned carts are different"
		with(response1){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_CREATED
		}
		with(response2){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_CREATED
		}
		response1.data.code != response2.data.code

		where:
		format << [XML, JSON]
	}

	def "Anonymous user gets cart by guid: #format"(){
		given: "a trusted client and anonymous cart"
		authorizeTrustedClient(restClient)
		def cart = createAnonymousCart(restClient, format)

		when: "anonymous (not logged in) user wants to create a cart for himself"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/anonymous/carts/'+cart.guid,
				contentType: format,
				requestContentType: URLENC
				)

		then: "he can get such a cart and newly created cart is empty"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			data.guid == cart.guid
		}

		where:
		format <<[XML, JSON]
	}

	def "All anonymous carts cannot be retrieved by anonymous user : #format"(){
		given: "a trusted client"
		authorizeTrustedClient(restClient)

		when: "anonymous (not logged in) user wants to retrieve anonymous carts"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/anonymous/carts',
				contentType: format,
				requestContentType: URLENC
				)

		then: "his access id denied"
		with(response){
			status == SC_UNAUTHORIZED
			data.errors[0].type == 'AccessDeniedError'
		}

		where:
		format <<[XML, JSON]
	}

	def "Registered and authorized customer creates two carts : #format"(){
		given: "a registered customer with more than one cart"
		authorizeTrustedClient(restClient)
		def customer = registerCustomer(restClient)
		authorizeCustomer(restClient, customer)

		when: "authorized customer wants to retrieve anonymous carts"
		HttpResponseDecorator response1 = restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id +'/carts',
				contentType: format,
				requestContentType: URLENC
				)

		HttpResponseDecorator response2 = restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id +'/carts',
				contentType: format,
				requestContentType: URLENC
				)

		then: "he retrieves his carts"
		with(response1){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_CREATED
		}
		with(response2){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_CREATED
		}
		response1.data.code != response2.data.code

		where:
		format <<[XML, JSON]
	}

	def "Registered and authorized customer gets his carts : #format"(){
		given: "a registered customer with more than one cart"
		authorizeTrustedClient(restClient)
		def customer = registerCustomer(restClient)
		def cart1 = createCart( restClient, customer, format)
		def cart2 = createCart( restClient, customer, format)
		authorizeCustomer(restClient, customer)

		when: "authorized customer wants to retrieve anonymous carts"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id +'/carts',
				contentType: format,
				requestContentType: URLENC
				)

		then: "he retrieves his carts"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			(data.carts[0].code == cart1.code || data.carts[1].code == cart1.code)
			(data.carts[0].code == cart2.code || data.carts[1].code == cart2.code)
		}

		where:
		format <<[XML, JSON]
	}

	def "Customer changes his cart currency: #format"(){
		given: "a registerd and logged in customer with cart and at least one cart entry"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_FLEXI_TRIPOD, 1, format)
		def oldPrice = cart.totalPrice.value.toDouble()

		when: "customer decides to change his cart currency"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id +'/carts/' + cart.code,
				contentType: format,
				query : ['curr' : 'JPY',
					'fields' : FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC
				)

		cart = getCart(restClient, customer, cart.code, format)
		def newPrice = cart.totalPrice.value.toDouble()

		then: "cart currency is updated"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			data.totalPrice.currencyIso == 'JPY'
			oldPrice != newPrice
		}

		where:
		format <<[XML, JSON]
	}

	def "Customer restores anonymous cart: #format"(){
		given: "An anonymous cart with a product in it"
		def anonymousCart = createAnonymousCart(restClient, format)
		addProductToCartOnline(restClient, ['id':'anonymous'], anonymousCart.guid, PRODUCT_FLEXI_TRIPOD)
		anonymousCart = getCart(restClient, ['id':'anonymous'], anonymousCart.guid, format)

		and: "authorized customer"
		authorizeTrustedClient(restClient)
		def customer = registerCustomer(restClient)
		authorizeCustomer(restClient, customer)

		when: "customer tries to restore anonymous cart"
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id +'/carts/',
				contentType: format,
				query : ['oldCartId' : anonymousCart.guid,
					'fields' : FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC
				)

		then: "he is able to restore the cart and the product is transfered to his own cart"
		isNotEmpty(anonymousCart.entries)
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_CREATED
			isNotEmpty(data.entries)
			data.entries.size() == 1
		}

		where:
		format << [XML, JSON]
	}

	def "Customer restores already restored cart: #format"(){
		given: "An anonymous cart with a product in it"
		def anonymousCart = createAnonymousCart(restClient, format)
		addProductToCartOnline(restClient, ['id':'anonymous'], anonymousCart.guid, PRODUCT_FLEXI_TRIPOD)
		anonymousCart = getCart(restClient, ['id':'anonymous'], anonymousCart.guid, format)

		and: "authorized customer"
		authorizeTrustedClient(restClient)
		def customer = registerCustomer(restClient)
		authorizeCustomer(restClient, customer)

		when: "customer tries to restore anonymous cart"
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id +'/carts/',
				contentType: format,
				query : ['oldCartId' : anonymousCart.guid,
					'fields' : FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC
				)

		and: "then tries to restore the same cart again"
		HttpResponseDecorator response2 = restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id +'/carts/',
				contentType: format,
				query : ['oldCartId' : anonymousCart.guid,
					'fields' : FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC
				)

		then: "first attempt is successfull, but second fails"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_CREATED
		}
		with (response2){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CartError'
			data.errors[0].reason == 'cannotRestore'
			data.errors[0].subject == anonymousCart.guid
		}

		where:
		format << [XML, JSON]
	}

	def "Customer restores another customer's cart: #format"(){
		given: "a customer with cart and a product in it"
		authorizeTrustedClient(restClient)
		def customer1 = registerCustomer(restClient)
		def cart1 = createCart(restClient, customer1, format)
		addProductToCartOnline(restClient, customer1, cart1.code, PRODUCT_FLEXI_TRIPOD, 1, format)
		cart1 = getCart(restClient, customer1, cart1.code, format)

		and: "another, authorized customer"
		def customer2 = registerCustomer(restClient)
		authorizeCustomer(restClient, customer2)

		when: "authorized customer attempts to restore other customer's cart"
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + '/users/' + customer2.id +'/carts/',
				contentType: format,
				query : ['oldCartId' : cart1.code,
					'fields' : FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC
				)

		then: "this attempt fails"
		with(response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CartError'
			data.errors[0].reason == 'cannotRestore'
			data.errors[0].subject == cart1.code
		}

		where:
		format << [XML, JSON]
	}
}

