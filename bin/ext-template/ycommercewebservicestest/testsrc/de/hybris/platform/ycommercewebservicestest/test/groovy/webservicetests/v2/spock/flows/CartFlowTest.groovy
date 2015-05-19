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

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.flows

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest

import spock.lang.Unroll

@ManualTest
@Unroll
class CartFlowTest extends AbstractSpockFlowTest {

	def "Cart flow : #format"() {
		given: "a trusted client"
		authorizeTrustedClient(restClient)

		and: "a new customer"
		def customer = registerCustomer(restClient, format)

		and: "a new address"
		def address = createAddress(restClient, customer, format);

		expect: "create a new cart"
		def cart = returningWith(restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts',
				body: [
					'code': '3429337'
				],
				contentType: format,
				requestContentType: URLENC), {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
				}
				).data

		and: "add a product to the cart"
		with(restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code +  '/entries',
				body: [
					'code': '3429337'
				],
				contentType: format,
				requestContentType: URLENC)) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					data.quantityAdded == 1
					isNotEmpty(data.entry)
					data.entry.entryNumber == 0
				}

		and: "add another product with quantity to the cart"
		with(restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code +  '/entries',
				body: [
					'code': '1934795',
					'qty': 2
				],
				contentType: format,
				requestContentType: URLENC)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					data.quantityAdded == 2
					isNotEmpty(data.entry)
					data.entry.entryNumber == 1
				}

		and: "get cart with 2 entries"
		with(restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code,
				query: [ 'fields': 'DEFAULT,totalUnitCount' ],
				contentType: format)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					data.totalItems == 2
					data.totalUnitCount == 3
					isNotEmpty(data.totalPrice)
					data.totalPrice.value == 234.8
				}

		and: "update quantity of first product"
		with(restClient.patch(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/entries/0',
				body: [
					'qty': 3
				],
				contentType: format,
				requestContentType: URLENC)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					data.quantityAdded == 2
					data.quantity == 3
				}

		and: "get cart again"
		with(restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code,
				query: [ 'fields': 'DEFAULT,totalUnitCount' ],
				contentType: format)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					data.totalItems == 2
					data.totalUnitCount == 5
					isNotEmpty(data.totalPrice)
					data.totalPrice.value == 257.04
				}

		and: "remove first product"
		with(restClient.delete(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/entries/0',
				contentType: format)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
				}

		and: "get cart with 1 entry"
		with(restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code,
				query: [ 'fields': 'DEFAULT,totalUnitCount' ],
				contentType: format)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					data.totalItems == 1
					data.totalUnitCount == 2
					isNotEmpty(data.totalPrice)
					data.totalPrice.value == 223.68
				}

		and: "authorize the customer"
		authorizeCustomer(restClient, customer)

		and: "set the delivery address"
		with(restClient.put(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/addresses/delivery',
				body: [
					'addressId': address.id,
				],
				contentType: format,
				requestContentType: URLENC)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
				}

		and: "remove the delivery address"
		with(restClient.delete(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/addresses/delivery',
				contentType: format)
				) { status == SC_OK }

		and: "get cart again"
		with(restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code,
				query: [ 'fields': 'DEFAULT,totalUnitCount' ],
				contentType: format)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					data.totalItems == 1
					data.totalUnitCount == 2
					data.totalPrice.value == 223.68
					!isNotEmpty(data.deliveryAddress)
				}

		and: "check deliverymodes (if there is no delivery address, there should be no deliverymodes)"
		with(restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/deliverymodes',
				contentType: format)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					!isNotEmpty(data.deliveryModes)
				}

		and: "set the delivery address again"
		with(restClient.put(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/addresses/delivery',
				body: [
					'addressId': address.id,
				],
				contentType: format,
				requestContentType: URLENC)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
				}

		and: "get cart again and check if the delivery address is in the response"
		with(restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code,
				query: [ 'fields': 'DEFAULT,totalUnitCount,deliveryAddress' ],
				contentType: format)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					data.totalItems == 1
					data.totalUnitCount == 2
					data.totalPrice.value == 223.68
					isNotEmpty(data.deliveryAddress)
				}

		and: "get the available delivery modes"
		with(restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/deliverymodes',
				contentType: format)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					isNotEmpty(data.deliveryModes)
					data.deliveryModes.collect{(String)it.code}.containsAll([
						'standard-gross',
						'premium-gross'
					])
				}

		and: "set the delivery mode"
		with(restClient.put(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/deliverymode',
				body: [
					'deliveryModeId': 'standard-gross',
				],
				contentType: format,
				requestContentType: URLENC)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
				}

		and: "remove the delivery mode"
		with(restClient.delete(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/deliverymode',
				contentType: format)
				) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
				}

		and: "set the delivery mode again"
		with(restClient.put(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/deliverymode',
				body: [
					'deliveryModeId': 'standard-gross',
				],
				contentType: format,
				requestContentType: URLENC)) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
				}

		and: "create payment details"
		def paymentDetails = returningWith(restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/paymentdetails',
				body: [
					'accountHolderName': 'Sven Haiges',
					'cardNumber': '4111111111111111',
					'cardType': 'visa',
					'expiryMonth': '01',
					'expiryYear': '2013',
					'saved': true,
					'defaultPaymentInfo': true,
					'billingAddress.titleCode': 'mr',
					'billingAddress.firstName': 'sven',
					'billingAddress.lastName': 'haiges',
					'billingAddress.line1': 'test1',
					'billingAddress.line2': 'test2',
					'billingAddress.postalCode': '12345',
					'billingAddress.town': 'somecity',
					'billingAddress.country.isocode': 'DE'
				],
				contentType: format,
				requestContentType: URLENC), {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_CREATED
					isNotEmpty(data.id)
					data.accountHolderName == 'Sven Haiges'
					isNotEmpty(data.cardType)
					data.cardType.code == 'visa'
					data.cardType.name == 'Visa'
				}).data

		and: "get all payment details of current customer"
		with(restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/paymentdetails',
				contentType: format)) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					isNotEmpty(data.payments)
					data.payments.size() == 1
				}

		and: "set the payment details"
		with(restClient.put(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/paymentdetails',
				body: [
					'paymentDetailsId': paymentDetails.id,
				],
				contentType: format,
				requestContentType: URLENC)) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
				}

		and: "place order"
		def order = returningWith(restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
				body: [
					'cartId': cart.code,
					'securityCode': '123'
				],
				contentType: format,
				requestContentType: URLENC), {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					isNotEmpty(data.code)
				}).data

		and: "get all the orders"
		with(restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
				contentType: format)) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					isNotEmpty(data.orders)
					data.orders.size() == 1
					data.orders[0].code == order.code
				}

		and: "get the specific order"
		with(restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/orders/' + order.code,
				contentType: format)) {
					if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
					status == SC_OK
					data.code == order.code
					data.totalItems == 1
					data.totalPrice.value == 232.67 // changed, due to delivery cost +8.99
					isNotEmpty(data.deliveryMode)
					data.deliveryMode.code == 'standard-gross'
				}

		where:
		format << [JSON, XML]
	}
}
