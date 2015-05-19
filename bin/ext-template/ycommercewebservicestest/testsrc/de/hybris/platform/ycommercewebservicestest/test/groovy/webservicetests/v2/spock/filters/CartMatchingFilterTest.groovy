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

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.filters

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest

import spock.lang.Unroll
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
/**
 *
 *`
 */
@Unroll
@ManualTest
class CartMatchingFilterTest extends AbstractCartTest {

	def "Anonymous retrieves existing anonymous cart: #format"(){
		given: "anonymous cart"
		def cart = createAnonymousCart(restClient, format)

		when: "anonymous customer requests anonymous cart"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + '/users/anonymous/carts/' + cart.guid,
				contentType : format,
				requestContentType : URLENC
				)

		then: "he can retrieve it"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}

		where:
		format << [XML, JSON]
	}

	def "Anonymous retrieves not existing anonymous cart: #format"(){
		given:
		def cartGuid = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"

		when: "anonymous customer requests non existing cart"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/users/anonymous/carts/${cartGuid}",
				contentType : format,
				requestContentType : URLENC
				)

		then: "he gets an error"
		with(response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CartError'
			data.errors[0].subject == 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx'
			data.errors[0].subjectType == 'cart'
			data.errors[0].reason == 'notFound'
			data.errors[0].message == 'Cart not found.'
		}

		where:
		format << [XML, JSON]
	}

	def "Anonymous user getting other users cart: #format"(){
		given: "user with cart"
		authorizeTrustedClient(restClient)
		def customer = registerCustomer(restClient)
		def cart = createCart(restClient, customer, format)
		authorizeClient(restClient)

		when: "anonymous customer requests users cart"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/users/anonymous/carts/" + cart.guid,
				contentType : format,
				requestContentType : URLENC
				)

		then: "request is refused"
		with(response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CartError'
			data.errors[0].subject == cart.guid
			data.errors[0].subjectType == 'cart'
			data.errors[0].reason == 'notFound'
			data.errors[0].message == 'Cart not found.'
		}

		where:
		format << [XML, JSON]
	}

	def "Anonymous user getting current cart: #format"(){
		when: "anonynous customer requestst current cart"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/users/anonymous/carts/current",
				contentType : format,
				requestContentType : URLENC
				)

		then: "his request is refused"
		with(response){
			status == SC_UNAUTHORIZED
			data.errors[0].type == 'UnauthorizedError'
			data.errors[0].message == 'Full authentication is required to access this resource'
		}

		where:
		format << [XML, JSON]
	}

	def "User getting his cart: #format"(){
		given: "authorized customer with cart"
		authorizeTrustedClient(restClient)
		def customer = registerCustomer(restClient)
		def cart = createCart(restClient, customer, format)
		authorizeCustomer(restClient, customer)

		when: "customer requests his own cart"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/users/"+ customer.id +"/carts/" + cart.code,
				contentType : format,
				requestContentType : URLENC
				)

		then: "he can retrieve it"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}

		where:
		format << [XML, JSON]
	}

	def "User getting non existing cart: #format"(){
		given: "authorized customer"
		authorizeTrustedClient(restClient)
		def customer = registerCustomer(restClient)
		def cartID = 'xxxxxxxx'
		authorizeCustomer(restClient, customer)

		when: "customer requests not existing cart"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/users/"+ customer.id +"/carts/${cartID}",
				contentType : format,
				requestContentType : URLENC
				)

		then: "he can retrieve it"
		with(response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CartError'
			data.errors[0].subject == cartID
			data.errors[0].subjectType == 'cart'
			data.errors[0].reason == 'notFound'
			data.errors[0].message == 'Cart not found.'
		}

		where:
		format << [XML, JSON]
	}

	def "User attempts to get other users cart: #format"(){
		given: "two customers, one with cart"
		authorizeTrustedClient(restClient)
		def customer1 = registerCustomer(restClient)
		def cart = createCart(restClient, customer1, format)
		def customer2 = registerCustomer(restClient)
		authorizeCustomer(restClient, customer2)

		when: "customer requests not existing cart"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/users/"+ customer2.id +"/carts/" + cart.code,
				contentType : format,
				requestContentType : URLENC
				)

		then: "he can retrieve it"
		with(response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CartError'
			data.errors[0].subject == cart.code
			data.errors[0].subjectType == 'cart'
			data.errors[0].reason == 'notFound'
			data.errors[0].message == 'Cart not found.'
		}

		where:
		format << [XML, JSON]
	}

	def "Non-customer user attempts to get anonymous cart: #format"(){
		given: "anonymous cart"
		def cart = createAnonymousCart(restClient, format)
		authorizeCustomerManager(restClient)

		when: "non-customer requests not existing cart"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/users/customermanager/carts/" + cart.guid,
				contentType : format,
				requestContentType : URLENC
				)

		then: "he can not retrieve it"
		with(response){ status == SC_FORBIDDEN }

		where:
		format << [XML, JSON]
	}

	def "Non-customer user attempts to get customer cart: #format"(){
		given: "customer with cart"
		authorizeTrustedClient(restClient)
		def customer = registerCustomer(restClient)
		def cart = createCart(restClient, customer, format)
		authorizeCustomerManager(restClient)

		when: "non-customer requests user cart"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/users/customermanager/carts/" + cart.code,
				contentType : format,
				requestContentType : URLENC
				)

		then: "he can not retrieve it"
		with(response){ status == SC_FORBIDDEN }

		where:
		format << [XML, JSON]
	}

	def "Non-customer user attempts to get current cart: #format"(){
		given: "non-customer manager"
		authorizeCustomerManager(restClient)

		when: "non-customer requests not existing cart"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/users/customermanager/carts/current",
				contentType : format,
				requestContentType : URLENC
				)

		then: "he can not retrieve it"
		with(response){ status == SC_FORBIDDEN }

		where:
		format << [XML, JSON]
	}
}