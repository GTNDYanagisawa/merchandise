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
import groovyx.net.http.RESTClient

/**
 *
 * `
 */
@Unroll
@ManualTest
class GuestsTest extends AbstractCartTest {

	private String getGuestUid() {
		def randomUID = System.currentTimeMillis()
		def guestUid = "${randomUID}@test.com"
		return guestUid;
	}


	private placeGuestOrder(cartGuid, securityCode, format) {
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + '/users/anonymous/orders',
				query: ['cartId': cartGuid,
						'securityCode': securityCode,
						'fields': FIELD_SET_LEVEL_FULL
				],
				contentType: format,
				requestContentType: URLENC
		)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_CREATED
		}
		return response.data
	}

	private prepareCartForGuestOrder(RESTClient client, userGuid, format) {
		def anonymous = ['id': 'anonymous']
		def cart = createAnonymousCart(client, format)
		addProductToCartOnline(client, anonymous, cart.guid, PRODUCT_POWER_SHOT_A480)
		addProductToCartPickup(client, anonymous, cart.guid, PRODUCT_M340, STORE_SHINBASHI)

		addEmailToAnonymousCart(client, cart.guid, userGuid, format) //setting email address on cart makes it recognized as guest cart

		setAddressForAnonymousCart(client, GOOD_ADDRESS_DE, cart.guid, format)
		setDeliveryModeForCart(client, anonymous, cart.guid, DELIVERY_STANDARD, format)
		createPaymentInfo(client, anonymous, cart.guid)

		return cart
	}

	def "Guest customer logs in: #format"() {
		given: "a guest customer and anonymous cart"
		authorizeClient(restClient)
		def customer = ['id': 'anonymous']
		def cart = createAnonymousCart(restClient, format)

		when: "customer logs in by providing email for anonymous cart"
		def response = restClient.put(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.guid + "/email",
				query: ['email': getGuestUid()],
				contentType: format,
				requestContentType: URLENC
		)

		then: "email is stored against the cart and guest customer is considered logged in"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
		}

		where:
		format << [XML, JSON]
	}

	def "Guest customer places oder: #format"() {
		given: "a customer with cart ready for ordering"
		authorizeClient(restClient)
		def userGuid = getGuestUid()
		def cart = prepareCartForGuestOrder(restClient, userGuid, format)

		and: "tries to place order"
		def response = restClient.post(
				path: getBasePathWithSite() + '/users/anonymous/orders',
				query: ['cartId': cart.guid,
						'securityCode': '123',
						'fields': FIELD_SET_LEVEL_FULL
				],
				contentType: format,
				requestContentType: URLENC
		)

		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_CREATED
			isNotEmpty(data.created)
			data.guestCustomer == true
			data.guid == cart.guid;
			isNotEmpty(data.entries)
			data.entries.size() == 2;
		}
		where:
		format << [XML, JSON]
	}

	def "Guest customer places pickup oder: #format"() {
		given: "a customer with cart ready for ordering as pickup"
		authorizeClient(restClient)
		def userGuid = getGuestUid()

		def anonymous = ['id': 'anonymous']
		def cart = createAnonymousCart(restClient, format)
		addProductToCartPickup(restClient, anonymous, cart.guid, PRODUCT_M340, STORE_SHINBASHI)

		addEmailToAnonymousCart(restClient, cart.guid, userGuid, format) //setting email address on cart makes it recognized as guest cart

		setAddressForAnonymousCart(restClient, GOOD_ADDRESS_DE, cart.guid, format)
		setDeliveryModeForCart(restClient, anonymous, cart.guid, DELIVERY_PICKUP, format)
		createPaymentInfo(restClient, anonymous, cart.guid)

		and: "tries to place order"
		def response = restClient.post(
				path: getBasePathWithSite() + '/users/anonymous/orders',
				query: ['cartId': cart.guid,
						'securityCode': '123',
						'fields': FIELD_SET_LEVEL_FULL
				],
				contentType: format,
				requestContentType: URLENC
		)

		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_CREATED
			isNotEmpty(data.created)
			data.guestCustomer == true
			data.guid == cart.guid;
			isNotEmpty(data.entries)
			data.entries.size() == 1;
		}
		where:
		format << [XML, JSON]
	}

	def "Guest customer creates full account after placing oder: #format"() {
		given: "a customer with cart ready for ordering"
		authorizeTrustedClient(restClient)
		def userGuid = getGuestUid()
		def cart = prepareCartForGuestOrder(restClient, userGuid, format)
		authorizeClient(restClient)
		def order = placeGuestOrder(cart.guid, '123', format)
		def customer = ['id': userGuid, 'password': PASSWORD]

		when: "customer wants to create full account"
		def response = restClient.post(
				path: getBasePathWithSite() + '/users/',
				query: [
						'guid': order.guid,
						'password': PASSWORD,
				],
				contentType: format,
				requestContentType: URLENC
		)

		and: "retrieves his orders after logging in"
		authorizeCustomer(restClient, customer)
		def ordersResponse = getOrders(restClient, customer, format)

		then: "account is created"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_CREATED
		}

		and: "customer can see his placed order"
		with(ordersResponse) {
			data.orders[0].guid == order.guid
		}

		where:
		format << [XML, JSON]
	}


	def "Guest customer fails to create full account with duplicate user ID: #format"() {
		given: "a customer with cart ready for ordering"
		authorizeTrustedClient(restClient)
		def existingCustomer = registerCustomer(restClient)
		def cart = prepareCartForGuestOrder(restClient, existingCustomer.id, format)
		authorizeClient(restClient)
		def order = placeGuestOrder(cart.guid, '123', format)
		def customer = ['id': existingCustomer.id, 'password': PASSWORD]

		when: "customer wants to create full account"
		def response = restClient.post(
				path: getBasePathWithSite() + '/users/',
				query: [
						'guid': order.guid,
						'password': PASSWORD,
				],
				contentType: format,
				requestContentType: URLENC
		)

		then: "account is created"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == 'DuplicateUidError'
			data.errors[0].message == existingCustomer.id;
		}

		where:
		format << [XML, JSON]
	}

	def "Process of converting guest user fails when guid doesn't belong to guest user: #format"() {

		given: "a customer with cart ready for ordering"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		addProductToCartPickup(restClient, customer, cart.code, PRODUCT_M340, STORE_SHINBASHI)
		def address = createAddress(restClient, customer)
		setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)
		setDeliveryModeForCart(restClient, customer, cart.code, DELIVERY_PICKUP, format)
		createPaymentInfo(restClient, customer, cart.code)

		and: "an anonymous user that knows about customer's order"
		HttpResponseDecorator orderResponse = restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
				query: ['cartId': cart.code,
						'securityCode': '123',
						'fields': FIELD_SET_LEVEL_BASIC
				],
				contentType: format,
				requestContentType: URLENC
		)
		with(orderResponse) { status == SC_CREATED }
		authorizeClient(restClient)

		when: "anonymous wants to create full account using customer's order"
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + '/users/',
				query: [
						'guid': orderResponse.data.guid,
						'password': PASSWORD,
				],
				contentType: format,
				requestContentType: URLENC
		)

		then: "he gets an error"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == 'ValidationError'
			// for security reasons unknown identifier error is returned
			data.errors[0].reason == 'unknownIdentifier'
			data.errors[0].subjectType == 'parameter'
			data.errors[0].subject == 'guid'
		}

		where:
		format << [XML, JSON]
	}

	def "User fails to convert into full account based on cart instead of order: #format"() {

		given: "an anonymous customer with cart"
		authorizeTrustedClient(restClient)
		def anonymous = ['id': 'anonymous']
		def cart = createAnonymousCart(restClient, format)
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_POWER_SHOT_A480)
		authorizeClient(restClient)

		when: "anonymous wants to create full account using cart instead of order"
		def response = restClient.post(
				path: getBasePathWithSite() + '/users/',
				query: [
						'guid': cart.guid,
						'password': PASSWORD,
				],
				contentType: format,
				requestContentType: URLENC
		)

		then: "he gets an error"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == 'ValidationError'
			data.errors[0].reason == 'unknownIdentifier'
			data.errors[0].subjectType == 'parameter'
			data.errors[0].subject == 'guid'
		}

		where:
		format << [XML, JSON]
	}
}