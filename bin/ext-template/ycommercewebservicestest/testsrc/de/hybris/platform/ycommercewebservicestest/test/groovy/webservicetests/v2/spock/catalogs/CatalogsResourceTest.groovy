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

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.catalogs

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockTest

import spock.lang.Unroll
import groovyx.net.http.HttpResponseDecorator

@ManualTest
@Unroll
class CatalogsResourceTest extends AbstractSpockTest {

	def "Get catalogs : #format"() {
		when:
		HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/catalogs', contentType: format)

		then:
		with(response) {
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			isNotEmpty(data.catalogs)
			data.catalogs.size() == 1
		}

		where:
		format << [JSON, XML]
	}

	def "Get catalog : #format"() {
		when:
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/catalogs/wsTestProductCatalog',
				contentType: format
				)

		then:
		with(response) {
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			data.id == 'wsTestProductCatalog'
			data.name == 'wsTest Product Catalog'
			data.url == '/wsTestProductCatalog'
			isNotEmpty(data.catalogVersions)
			data.catalogVersions.size() == 2
			data.catalogVersions.collect{(String)it.id}.containsAll(['Staged', 'Online'])
		}

		where:
		format << [JSON, XML]
	}

	def "Get non existing catalog : #format"() {
		when:
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/catalogs/wsTestProductCatalogInvalid',
				contentType: format
				)

		then:
		with(response) {
			status == SC_BAD_REQUEST
			isNotEmpty(data.errors)
			data.errors[0].type == 'UnknownIdentifierError'
		}

		where:
		format << [JSON, XML]
	}

	def "Get catalog version : #format"() {
		when:
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/catalogs/wsTestProductCatalog/Online',
				contentType: format
				)

		then:
		with(response) {
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			data.id == 'Online'
			data.url == '/wsTestProductCatalog/Online'
		}

		where:
		format << [JSON, XML]
	}

	def "Get non existing catalog version : #format"() {
		when:
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/catalogs/wsTestProductCatalog/OnlineInvalid',
				contentType: format
				)

		then:
		with(response) {
			status == SC_BAD_REQUEST
			isNotEmpty(data.errors)
			data.errors[0].type == 'UnknownIdentifierError'
		}

		where:
		format << [JSON, XML]
	}

	def "Get category : #format"() {
		when:
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/catalogs/wsTestProductCatalog/Online/categories/brands',
				contentType: format
				)

		then:
		with(response) {
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			data.name == 'Brands'
		}

		where:
		format << [JSON, XML]
	}

	def "Get non existing category : #format"() {
		when:
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/catalogs/wsTestProductCatalog/Online/categories/brandsinvalid',
				contentType: format
				)

		then:
		with(response) {
			status == SC_BAD_REQUEST
			isNotEmpty(data.errors)
			data.errors[0].type == 'UnknownIdentifierError'
		}

		where:
		format << [JSON, XML]
	}
}
