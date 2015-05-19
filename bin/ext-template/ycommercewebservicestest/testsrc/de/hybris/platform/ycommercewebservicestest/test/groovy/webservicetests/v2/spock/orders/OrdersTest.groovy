/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.orders

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_ACCEPTED
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_NOT_FOUND
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import de.hybris.bootstrap.annotations.ManualTest

import spock.lang.Unroll
import groovyx.net.http.HttpResponseDecorator
/**
 *
 *
 */
@ManualTest
@Unroll
class OrdersTest extends AbstractOrderTest {


	def "Trusted client requests order by code: #format"(){
		given: "trusted client"
		authorizeTrustedClient(restClient)

		when: "trusted client retrieves order by code"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/orders/" + ORDER_CODE,
				contentType : format,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				requestContentType : URLENC)

		then:"he receives requested order"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			data.store == "wsTest"
			data.net == false
		}

		where:
		format << [XML, JSON]
	}

	def "Trusted client requests order by wrong id: #format"(){
		given: "trusted client"
		authorizeTrustedClient(restClient)

		when: "trusted client retrieves order by code"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/orders/wrongOrderGuidOrCode" ,
				contentType : format,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				requestContentType : URLENC)

		then:"he receives requested order"
		with(response){
			status == SC_BAD_REQUEST
			data.errors[0].type == "UnknownIdentifierError"
		}

		where:
		format << [XML, JSON]
	}

	def "Trusted client retrieves order by guid: #format"(){
		given: "an order with guid and trusted client"
		//this preparation steps are necessary because sample data does not contain orders with guid
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		def address = createAddress(restClient, customer)
		setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480)
		setDeliveryModeForCart(restClient, customer, cart.code, DELIVERY_STANDARD, format)
		createPaymentInfo(restClient, customer, cart.code)
		def order = placeOrder(restClient, customer, cart.code, "123", format)

		authorizeTrustedClient(restClient)

		when: "trusted client retrieves order by GUID"
		HttpResponseDecorator response = restClient.get(
				path : getBasePathWithSite() + "/orders/"+order.guid ,
				contentType : format,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				requestContentType : URLENC)

		then: "order is retrieved"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			data.guid == order.guid
			data.code == order.code
		}

		where:
		format << [XML, JSON]
	}
}
