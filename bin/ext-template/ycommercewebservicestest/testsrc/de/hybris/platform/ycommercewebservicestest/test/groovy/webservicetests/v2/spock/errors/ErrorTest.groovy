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

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.errors

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockTest

import spock.lang.Unroll
import groovyx.net.http.HttpResponseDecorator

@ManualTest
@Unroll
class ErrorTest extends AbstractSpockTest {

	def "Missing identifier : #format"() {
		given: "an authorized trusted client"
		authorizeTrustedClient(restClient)

		when:
		HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/stores/WRONG_ID', contentType: format)

		then:
		with(response) {
			status == SC_BAD_REQUEST
			isNotEmpty(data.errors)
			data.errors[0].type == 'UnknownIdentifierError'
		}

		where:
		format << [JSON, XML]
	}

	def "Missing required parameter : #format"() {
		given: "an authorized trusted client"
		authorizeTrustedClient(restClient)

		when:
		HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/promotions', contentType: format)

		then:
		with(response) {
			status == SC_BAD_REQUEST
			isNotEmpty(data.errors)
			data.errors[0].type == 'MissingServletRequestParameterError'
		}

		where:
		format << [JSON, XML]
	}

	def "Invalid base site : #format"() {

		when: "attempt to request incorrect base site is made"
		HttpResponseDecorator response = restClient.get(path: getBasePath() + '/wrongBaseSite', contentType: format)

		then:
		with(response) {
			status == SC_BAD_REQUEST
			isNotEmpty(data.errors)
			data.errors[0].type == 'InvalidResourceError'
			data.errors[0].message == 'Base site wrongBaseSite doesn\'t exist'
		}

		where:
		format << [JSON, XML]
	}
}
