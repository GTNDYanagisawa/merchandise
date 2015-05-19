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

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest

import spock.lang.Unroll
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

abstract class AbstractSpockAccessRightsTest extends AbstractSpockFlowTest {

	protected enum Roles {
		ANONYMOUS,
		TRUSTED_CLIENT,
		CLIENT,
		CUSTOMERGROUP,
		CUSTOMERMANAGERGROUP,
		GUEST
	}

	protected Set getAllRoles() {
		return Roles.values() as Set
	}

	protected authorize(role) {
		switch (role) {
			case Roles.ANONYMOUS:
				break

			case Roles.TRUSTED_CLIENT:
				authorizeTrustedClient(restClient)
				break

			case Roles.CLIENT:
				authorizeClient(restClient)
				break

			case Roles.CUSTOMERGROUP:
				authorizeCustomer(restClient)
				break

			case Roles.CUSTOMERMANAGERGROUP:
				authorizeCustomerManager(restClient)
				break

			case Roles.GUEST:
				authorizeGuest(restClient)
				break

			default:
				throw new IllegalArgumentException("Role " + role + " not supported")
		}
	}

	abstract protected getRequests()

	@Unroll("Check allowed request : role=#params.role method=#params.method requestArgs=#params.requestArgs")
	def "Check allowed request"(params) {
		setup:
		if (params.setup != null) {
			(params.setup)()
		}
		authorize(params.role)

		when:
		def response = doRequest(restClient, params.method, params.requestArgs)

		then:
		!(response.status in [
			SC_UNAUTHORIZED,
			SC_FORBIDDEN
		])

		cleanup:
		if (params.cleanup != null) {
			(params.cleanup)()
		}

		where:
		params << buildAllowedCombinations()
	}

	@Unroll("Check not allowed request : role=#params.role method=#params.method requestArgs=#params.requestArgs")
	def "Check not allowed request"(params) {
		setup:
		if (params.setup != null) {
			(params.setup)()
		}
		authorize(params.role)

		when:
		def response = doRequest(restClient, params.method, params.requestArgs)

		then:
		response.status in [
			SC_UNAUTHORIZED,
			SC_FORBIDDEN
		]

		cleanup:
		if (params.cleanup != null) {
			(params.cleanup)()
		}

		where:
		params << buildNotAllowedCombinations()
	}

	protected buildAllowedCombinations() {
		return buildCombinations() { it }
	}

	protected buildNotAllowedCombinations() {
		return buildCombinations() {
			def combinations = []
			combinations.addAll(getAllRoles())
			combinations.removeAll(it)
			return combinations
		}
	}

	protected buildCombinations(rolesClosure) {
		def combinations = []

		for (def entry in requests) {
			def roles = rolesClosure(entry.rolesAllowed)
			def methods = entry.methods

			if (roles && methods) {
				for (role in roles) {
					for (method in methods) {
						combinations.add([
							setup: entry.setup,
							cleanup: entry.cleanup,
							role: role,
							method: method,
							requestArgs: entry.requestArgs
						])
					}
				}
			}
		}

		return combinations
	}

	protected doRequest(RESTClient client, Method method, Map<String,?> requestArgs) {
		// necessary because HTTPBuilder RequestConfigDelegate.setPropertiesFromMap(args) removes the properties from the map
		def newRequestArgs = requestArgs.clone()

		switch (method) {
			case Method.GET:
				return restClient.get(newRequestArgs)

			case Method.POST:
				return restClient.post(newRequestArgs)

			case Method.PUT:
				return restClient.put(newRequestArgs)

			case Method.PATCH:
				return restClient.patch(newRequestArgs)

			case Method.DELETE:
				return restClient.delete(newRequestArgs)

			default:
				throw new IllegalArgumentException("HTTP method " + method + " not supported")
		}
	}
}
