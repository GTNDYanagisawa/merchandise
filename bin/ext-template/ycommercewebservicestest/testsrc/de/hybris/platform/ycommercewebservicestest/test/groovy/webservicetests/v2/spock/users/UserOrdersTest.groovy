/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users

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

import spock.lang.Ignore
import spock.lang.Unroll
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

/**
 *
 *
 */
@ManualTest
@Unroll
class UserOrdersTest extends AbstractUserTest {

	static final String username_with_orders = "orderhistoryuser@test.com"
	static final String ORDER_CODE = "testOrder1"
	static final String password_with_orders = "test"
	static final customer_with_orders = ["id": username_with_orders, "password": password_with_orders]
	static final CREATED_ORDERS = 10
	static final ALL_ORDERS = 13
	static final PAGE_SIZE = 5

	def setup() {
		authorizeCustomer(restClient, customer_with_orders)
	}

	def "Customer retrieves all of his orders : #format"() {

		when: "customer retrieves his orders without specifying pagination"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer_with_orders.id + "/orders",
				contentType: format,
				requestContentType: URLENC)

		then: "he receives his orders"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			data.pagination.pageSize == 20
			data.pagination.totalPages == 1
			data.pagination.currentPage == 0
			data.pagination.totalResults == ALL_ORDERS
			response.getFirstHeader(HEADER_TOTAL_COUNT).getValue() == ALL_ORDERS.toString()
		}

		where:
		format << [XML, JSON]
	}

	def "Customer gets number of all his orders : #format"() {

		when: "customer gets number of orders"
		HttpResponseDecorator response = restClient.head(
				path: getBasePathWithSite() + '/users/' + customer_with_orders.id + "/orders",
				contentType: format,
				requestContentType: URLENC)

		then: "he receives proper header"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			response.getFirstHeader(HEADER_TOTAL_COUNT).getValue() == ALL_ORDERS.toString()
		}

		where:
		format << [XML, JSON]
	}

	def "Customer retrieves his orders paged : #format"() {

		when: "customer retrieves his orders specifying pagination"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer_with_orders.id + "/orders",
				contentType: format,
				query: ["pageSize": PAGE_SIZE],
				requestContentType: URLENC)

		then: "he receives first page of his orders with specified size"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			data.orders.size() == PAGE_SIZE
			data.pagination.pageSize == PAGE_SIZE
			data.pagination.totalPages == 3
			data.pagination.currentPage == 0
			data.pagination.totalResults == ALL_ORDERS
			response.getFirstHeader(HEADER_TOTAL_COUNT).getValue() == ALL_ORDERS.toString()
		}

		where:
		format << [XML, JSON]
	}

	def "Customers gets his orders in a given status : #format"() {

		when: "customer retrieves his orders specifying order status"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer_with_orders.id + "/orders",
				contentType: format,
				query: ['statuses': "CREATED"],
				requestContentType: URLENC)

		then: "only orders in requested status are returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			data.pagination.totalResults == CREATED_ORDERS
			response.getFirstHeader(HEADER_TOTAL_COUNT).getValue() == CREATED_ORDERS.toString()
			for (order in data.orders) {
				order.status == "CREATED"
			}
		}
		//check expected resutls
		where:
		format << [XML, JSON]
	}

	@Ignore
	def "Customers requests his orders in not existing status : #format"() {

		when: "customer retrieves his orders specifying order status"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer_with_orders.id + "/orders",
				contentType: format,
				query: ['statuses': "NO_SUCH_STATUS"],
				requestContentType: URLENC)

		then: "only orders in requested status are returned"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors.size() > 0
		}
		and: "error is properly wrapped"
		with(response) {
			data.errors[0].type != "IllegalStateError" //this error should be wrapped, we do not want to expose such a thing. UsersTest, line 1742
		}

		where:
		format << [XML, JSON]
	}

	def "Customers requests his order by code : #format"() {

		given: "authorized customer"
		authorizeCustomer(restClient, customer_with_orders)

		when: "customer retrieves his order specifying order code"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer_with_orders.id + "/orders/" + ORDER_CODE,
				contentType: format,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC)

		then: "he receives requested order"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			data.store == "wsTest"
			data.net == false
			isNotEmpty(data.totalDiscounts)
			isNotEmpty(data.productDiscounts)
			isNotEmpty(data.created)
			isNotEmpty(data.subTotal)
			isNotEmpty(data.orderDiscounts)
			isNotEmpty(data.entries)
			data.entries.size() == 2
			isNotEmpty(data.totalPrice)
			data.site == "wsTest"
			data.status == "CREATED"

			"created".equalsIgnoreCase(data.statusDisplay.toString())
			isNotEmpty(data.deliveryMode)
			data.code == ORDER_CODE
			data.totalItems == 2
			isNotEmpty(data.totalPriceWithTax)
			data.guestCustomer == false
			data.deliveryItemsQuantity == 7
			isNotEmpty(data.totalTax)
			data.user.uid == customer_with_orders.id
			data.user.name == "orders test user"
			isNotEmpty(data.deliveryCost)
		}

		where:
		format << [XML, JSON]
	}

	def "Customer requests not existing order by code or guid : #format"() {
		when: "customer retrieves his order specifying order code"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer_with_orders.id + "/orders/notExistingOrder",
				contentType: format,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC)

		then: "he receives requested order"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == "UnknownIdentifierError"
		}

		where:
		format << [XML, JSON]
	}
}