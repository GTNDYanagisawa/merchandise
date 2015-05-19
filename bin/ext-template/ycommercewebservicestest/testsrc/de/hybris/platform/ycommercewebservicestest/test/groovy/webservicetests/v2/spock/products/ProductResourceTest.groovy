/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.products


import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_ACCEPTED
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest

import spock.lang.Unroll
import groovyx.net.http.HttpResponseDecorator

/**
 *
 *
 */
@ManualTest
@Unroll
class ProductResourceTest extends AbstractSpockFlowTest {
	static final PRODUCT_ID_FLEXI_TRIPOD = '3429337'
	static final NUMBER_OF_ALL_PRODUCTS = 28
	static final STORE_NAME = 'WS-Shinbashi'

	def "Search for product: #format"() {

		when: "user search for specified product attributes"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				query: ['fields': 'products,sorts,pagination'],
				requestContentType: URLENC
		)

		then: "he gets all the requested fields"
		with(response) {
			status == SC_OK
			data.products.size() > 0
			data.sorts.size() > 0
			data.pagination
			data.pagination.pageSize.toString() == '20'
			data.pagination.totalPages.toString() == '2'
			data.pagination.currentPage.toString() == '0'
			data.pagination.totalResults.toString() == '28'
			response.containsHeader(HEADER_TOTAL_COUNT)
			response.getFirstHeader(HEADER_TOTAL_COUNT).getValue() == '28'
		}

		where:
		format << [XML, JSON]
	}

	def "Get number of all products: #format"() {

		when: "user search for any products (empty query)"
		HttpResponseDecorator response = restClient.head(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				requestContentType: URLENC
		)

		then: "he gets proper header"
		with(response) {
			status == SC_OK
			response.containsHeader(HEADER_TOTAL_COUNT)
			response.getFirstHeader(HEADER_TOTAL_COUNT).getValue() == '28'
		}

		where:
		format << [XML, JSON]
	}

	def "Check spelling suggestion: #format"() {

		when: "search input contains a typo"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				query: ['query': 'somy'],
				requestContentType: URLENC
		)

		then: "correct spelling suggestion will be returned"
		with(response) {
			status == SC_OK;
			data.currentQuery.query.value == 'somy:relevance'
			data.spellingSuggestion
			data.spellingSuggestion.suggestion == 'sony'
			data.spellingSuggestion.query == 'sony:relevance' || data.spellingSuggestion.query == 'sony:topRated'
		}
		where:
		format << [XML, JSON]
	}

	def "Check autosuggestion: #format"() {

		when: "search text is incomplete"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/suggestions',
				contentType: format,
				query: ['term': 'ta'],
				requestContentType: URLENC
		)
		then: "set of possible autosuggested fields will be returned"
		with(response) {
			status == SC_OK;
			isNotEmpty(data.suggestions)
			data.suggestions.size() == 2
			data.suggestions.value[0] == 'tape'
			data.suggestions.value[1] == 'targus'
		}
		where:
		format << [XML, JSON]
	}

	def "Check autosuggest with limit: #format"() {

		when: "search text is incomplete and the expected outcome is limmited"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/suggestions',
				contentType: format,
				query: ['max': 1,
						'term': 'ta'
				],
				requestContentType: URLENC
		)
		then: "limited set of possible autosuggested fields will be returned"
		with(response) {
			status == SC_OK;
			isNotEmpty(data.suggestions)
			data.suggestions.size() == 1
			data.suggestions.value[0] == 'tape'
		}
		where:
		format << [XML, JSON]
	}

	def "Check product sorting: #format"() {

		expect: "all camaras sorted by default sort order"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				query: ['query': 'camera'],
				requestContentType: URLENC
		)

		with(response) {
			status == SC_OK
			data.pagination.sort == 'relevance'
			isNotEmpty(data.sorts)
			data.sorts.find { it.code == response.data.pagination.sort }.selected == true
		}

		def allSorts = response.data.sorts

		and: "check all possible sort orders"

		for (def sortOrder : allSorts) {
			HttpResponseDecorator sortResponse = restClient.get(
					path: getBasePathWithSite() + '/products/search',
					contentType: format,
					query: ['sort': sortOrder.code],
					requestContentType: URLENC
			)

			with(sortResponse) {
				status == SC_OK
				data.pagination.sort == sortOrder.code
			}
		}

		where:
		format << [XML, JSON]
	}

	def "Check product details: #format"() {

		expect: "all camaras sorted by default sort order"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				contentType: format,
				query: ['query': 'camera'],
				requestContentType: URLENC
		)

		with(response) {
			status == SC_OK
			isNotEmpty(data.products)
		}

		def productsList = response.data.products

		and: "check products details for all camaras"

		for (def product : productsList) {
			HttpResponseDecorator productResponse = restClient.get(
					path: getBasePathWithSite() + '/products/' + product.code,
					contentType: format,
					requestContentType: URLENC
			)

			with(productResponse) {
				status == SC_OK
				data.code == product.code
				isNotEmpty(data.name)
				isNotEmpty(data.url)
			}
		}
		where:
		format << [XML, JSON]
	}
}







