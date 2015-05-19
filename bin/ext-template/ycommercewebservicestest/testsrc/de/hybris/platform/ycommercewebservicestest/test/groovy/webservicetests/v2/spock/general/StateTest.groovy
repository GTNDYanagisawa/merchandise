/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.general;

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
import groovyx.net.http.HttpResponseDecorator


@Unroll
@ManualTest
class StateTest extends AbstractSpockFlowTest {
	def "Checking JSESSIONID cookie in V2: #format"() {

		when: "a request to any v2 controller is made"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/titles',
				contentType: format,
				requestContentType: URLENC
		)

		then: "no JSESSION id is set in 'Set-Cookie' header"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			!containsHeader("Set-Cookie") || !getFirstHeader("Set-Cookie").getValue().contains("JSESSIONID")
		}

		where:
		format << [JSON, XML]
	}
}
