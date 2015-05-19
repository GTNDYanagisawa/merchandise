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

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
/**
 *
 *
 *
 */
abstract class AbstractOrderTest extends AbstractCartTest {
	static final String username_with_orders = "orderhistoryuser@test.com"
	static final String ORDER_CODE = "testOrder1"
	static final String password_with_orders = "test"
	static final customer_with_orders = ["id" : username_with_orders, "password" : password_with_orders]
	static final CREATED_ORDERS = 10
	static final ALL_ORDERS = 13
	static final PAGE_SIZE = 5


	/**
	 * This method places order with provided cart. It expects that cart is ready to be ordered, i.e. it has delivery address, delivery method and payment info set
	 * @param client REST client to be used
	 * @param customer customer for whom order will be placed
	 * @param cartID ID (code or guid) of a cart
	 * @param securityCode security code for payment
	 * @param format format to be used
	 * @return placed order
	 */
	protected placeOrder(RESTClient client, customer, cartID, securityCode, format){

		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/orders',
				query : ['cartId' : cartID,
					'securityCode' : securityCode,
					'fields' : FIELD_SET_LEVEL_FULL
				],
				contentType : format,
				requestContentType : URLENC
				)

		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_CREATED
		}
		return response.data
	}
}
