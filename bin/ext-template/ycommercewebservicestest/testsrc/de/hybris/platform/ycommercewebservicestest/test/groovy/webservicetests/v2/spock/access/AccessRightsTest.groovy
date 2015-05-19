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

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.access

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.DELETE
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.PATCH
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.access.AbstractSpockAccessRightsTest.Roles

import spock.lang.Shared
import groovyx.net.http.RESTClient

@ManualTest
class AccessRightsTest extends AbstractSpockAccessRightsTest {

	@Shared
	private customer

	def setupSpec() {
		RESTClient restClient = createRestClient()
		authorizeTrustedClient(restClient)

		customer = registerCustomer(restClient)

		restClient.shutdown()
	}

	def createCustomerCart(RESTClient client, customer, format = JSON) {
		def cart = returningWith(client.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts',
				contentType: format),
				{ assert status == SC_CREATED }).data

		with(client.post(
				path: getBasePathWithSite() + '/users/anonymous/carts/' + cart.guid + '/entries',
				body: [
						'code': '3429337'
				],
				contentType: format,
				requestContentType: URLENC),
				{ assert status == SC_OK })

		return cart
	}

	def createAnonymousCart(RESTClient client, format = JSON) {
		def cart = returningWith(client.post(
				path: getBasePathWithSite() + '/users/anonymous/carts',
				contentType: format),
				{ assert status == SC_CREATED }).data

		with(client.post(
				path: getBasePathWithSite() + '/users/anonymous/carts/' + cart.guid + '/entries',
				body: [
						'code': '3429337'
				],
				contentType: format,
				requestContentType: URLENC),
				{ assert status == SC_OK })

		return cart
	}

	@Override
	protected void authorizeCustomer(RESTClient client) {
		super.authorizeCustomer(client, customer)
	}

	@Override
	protected void authorizeGuest(RESTClient client) {
		authorizeClient(client)

		def cart = returningWith(client.post(
				path: getBasePathWithSite() + '/users/anonymous/carts',
				contentType: JSON),
				{ assert status == SC_CREATED }).data

		with(client.post(
				path: getBasePathWithSite() + '/users/anonymous/carts/' + cart.guid + '/entries',
				body: [
						'code': '3429337'
				],
				contentType: JSON,
				requestContentType: URLENC),
				{ assert status == SC_OK })

		with(client.put(
				path: getBasePathWithSite() + '/users/anonymous/carts/' + cart.guid + '/email',
				body: [
						'email': 'email@example.com'
				],
				contentType: JSON,
				requestContentType: URLENC),
				{ assert status == SC_OK })
	}

	@Override
	protected getRequests() {
		[
				[
						setup: { println 'SETUP' },
						cleanup: { println 'CLEANUP' },
						rolesAllowed: [
								Roles.TRUSTED_CLIENT,
								Roles.CUSTOMERGROUP,
								Roles.CUSTOMERMANAGERGROUP
						],
						methods: [GET],
						requestArgs: [
								path: getBasePathWithSite() + '/users/' + customer.id + '/carts',
						]
				],
				[
						rolesAllowed: [],
						methods: [GET],
						requestArgs: [
								path: getBasePathWithSite() + '/users/anonymous/carts',
						]
				]
		]
	}
}
