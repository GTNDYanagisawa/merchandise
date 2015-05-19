package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.products

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockTest
import groovyx.net.http.HttpResponseDecorator

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.SC_OK

/**
 *
 */
class ProductsStockTest extends AbstractSpockFlowTest {

	def "Get total number of product's stock levels : #format"() {

		when: "user search for product's stock levels"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/3429337/stock',
				contentType: format,
				query: ['location': 'tokio'],
				requestContentType: URLENC
		)

		then: "he gets all the requested fields"
		with(response) {
			status == SC_OK
			response.getFirstHeader(HEADER_TOTAL_COUNT).getValue() == '49'
		}

		where:
		format << [XML, JSON]
	}
}
