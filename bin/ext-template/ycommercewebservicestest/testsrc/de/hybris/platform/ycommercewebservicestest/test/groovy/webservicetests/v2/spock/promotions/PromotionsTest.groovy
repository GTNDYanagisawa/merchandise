/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.promotions

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
@Unroll
@ManualTest
class PromotionsTest extends AbstractSpockFlowTest {

	protected String ALL_PROMOTIONS = 'all'
	protected String ORDER_PROMOTIONS = "order"
	protected String PRODUCT_PROMOTIONS = "product"
	protected String PROMOTION_GROUP = "wsTestPromoGrp"
	protected String PROMOTION_CODE = "WS_OrderThreshold15Discount"
	protected String PROMOTION_TYPE = "Order threshold fixed discount"
	protected String PROMOTION_DESCRIPTION = "You saved bunch of bucks for spending quite much"
	protected String PROMOTION_END_DATE = "2099-01-01T00:00:00"
	protected String PROMOTION_START_DATE = "2000-01-01T00:00:00"
	protected boolean PROMOTION_ENABLED = true
	protected String PROMOTION_TITLE = PROMOTION_DESCRIPTION
	protected PROMOTION_PRIORITY = 500

	def "Get all promotions as trusted client : #format"(){
		given: "authorized trusted client"
		authorizeTrustedClient(restClient)

		when: "client requests all promotions"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + '/promotions',
				query : ['type' : ALL_PROMOTIONS],
				contentType : format,
				requestContentType : URLENC)

		then: "a list of promotions is sent back"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status ==SC_OK
			data.promotions.size() > 0
		}

		where:
		format << [JSON, XML]
	}

	def "Get product promotions as trusted client : #format"(){
		given: "authorized trusted client"
		authorizeTrustedClient(restClient)

		when: "client requests all product promotions"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + '/promotions',
				query : ['type' : PRODUCT_PROMOTIONS],
				contentType : format,
				requestContentType : URLENC)

		then: "a list of promotions is sent back"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status ==SC_OK
			data.promotions.size() > 0
		}

		where:
		format << [JSON, XML]
	}

	def "Get order promotions as trusted client : #format"(){
		given: "authorized trusted client"
		authorizeTrustedClient(restClient)

		when: "client requests all order promotions"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + '/promotions',
				query : ['type' : ORDER_PROMOTIONS],
				contentType : format,
				requestContentType : URLENC)

		then: "a list of promotions is sent back"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status ==SC_OK
			data.promotions.size() > 0
		}

		where:
		format << [JSON, XML]
	}

	def "Get promotions of unexisting type as trusted client : #format"(){
		given: "authorized trusted client"
		authorizeTrustedClient(restClient)

		when: "client requests promotions of not existing type"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + '/promotions',
				query : ['type' : 'noSuchType'],
				contentType : format,
				requestContentType : URLENC)

		then: "a list of promotions is sent back"
		with(response){ status ==SC_BAD_REQUEST }

		where:
		format << [JSON, XML]
	}


	def "Get promotions without authorization : #format"(){
		when: "requests for all promotions without autorization is made"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + '/promotions',
				query : ['type' : ALL_PROMOTIONS],
				contentType : format,
				requestContentType : URLENC)

		then: "the request is denied"
		with(response){ status ==SC_UNAUTHORIZED }

		where:
		format << [JSON, XML]
	}

	def "Get promotions without required params : #format"(){
		given: "authorized trusted client"
		authorizeTrustedClient(restClient)

		when: "requests for all promotions without autorization is made"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + '/promotions',
				contentType : format,
				requestContentType : URLENC)

		then: "the request is denied"
		with(response){ status ==SC_BAD_REQUEST }

		where:
		format << [JSON, XML]
	}


	def "Get promotion by code as trusted client : #format"(){
		given: "authorized trusted client"
		authorizeTrustedClient(restClient)

		when: "requests specific promotion"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + '/promotions/' + PROMOTION_CODE,
				contentType : format,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				requestContentType : URLENC)

		then: "the promotion data is sent"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status ==SC_OK
			PROMOTION_TYPE == data.promotionType.toString()
			PROMOTION_DESCRIPTION == data.description.toString()
			data.code == PROMOTION_CODE
			data.promotionType == PROMOTION_TYPE
			data.description == PROMOTION_DESCRIPTION
			data.enabled == PROMOTION_ENABLED
			data.title == PROMOTION_TITLE
			data.priority == PROMOTION_PRIORITY
			data.promotionGroup == PROMOTION_GROUP
		}

		where:
		format << [JSON, XML]
	}

	def "Get promotions of given type from group : #format"(){
		given: "authorized trusted client"
		authorizeTrustedClient(restClient)

		when: "requests specific promotion"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + '/promotions/',
				contentType : format,
				query : [
					'fields' : FIELD_SET_LEVEL_FULL,
					'promotionGroup' : PROMOTION_GROUP,
					'type' : PRODUCT_PROMOTIONS
				],
				requestContentType : URLENC)

		then: "the promotion data is sent"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status ==SC_OK
			data.promotions.size() == 3
		}

		where:
		format << [JSON, XML]
	}
}