/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts
import de.hybris.bootstrap.annotations.ManualTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED

@Unroll
@ManualTest
class CartMergeTest extends AbstractCartTest {

	protected static final int STOCK_LEVEL_FLEXI_TRIPOD = 4

	void "Customer merges carts after logon: #format"(){
		given: "a customer with two carts (one created in the past and one created anonymously) "
		final def (customer, customerCart) = createCustomerWithProductsInCart(restClient, PRODUCT_REMOTE_CONTROL_TRIPOD, 1, format)
		removeAuthorization(restClient)
		def anonymousCart = createAnonymousCart(restClient, format)
		addProductToCartOnline(restClient, ANONYMOUS_USER, anonymousCart.guid, PRODUCT_EOS_40D_BODY, 1, format)
		authorizeCustomer(restClient, customer)

		when: "customer decides to restore the cart and provides two guids"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/',
				query : ['fields' : FIELD_SET_LEVEL_FULL, 'oldCartId' : anonymousCart.guid, 'toMergeCartGuid' : customerCart.guid],
				contentType : format,
		)

		then: "carts are successfully merged"
		with(response){
			status == SC_CREATED
			data.entries.size()==2
			data.entries.find{ it2 -> it2.product.code.toString() == PRODUCT_REMOTE_CONTROL_TRIPOD.toString() }.quantity == 1
			data.entries.find{ it2 -> it2.product.code.toString() == PRODUCT_EOS_40D_BODY.toString() }.quantity == 1
		}

		where:
		format <<[XML, JSON]
	}


	void "Customer merges carts after logon with some product which is over stock when summed up: #format"(){
		given: "a customer with two carts (one created in the past and one created anonymously) with a product which is over stock when summed up "
		final def (customer, customerCart) = createCustomerWithProductsInCart(restClient, PRODUCT_FLEXI_TRIPOD, STOCK_LEVEL_FLEXI_TRIPOD-1, format)
		removeAuthorization(restClient)
		def anonymousCart = createAnonymousCart(restClient, format)
		addProductToCartOnline(restClient, ANONYMOUS_USER, anonymousCart.guid, PRODUCT_FLEXI_TRIPOD, STOCK_LEVEL_FLEXI_TRIPOD-1, format)
		authorizeCustomer(restClient, customer)

		when: "customer decides to restore the cart and provides two guids"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/',
				query : ['fields' : FIELD_SET_LEVEL_FULL, 'oldCartId' : anonymousCart.guid, 'toMergeCartGuid' : customerCart.guid],
				contentType : format,
		)

		then: "carts are successfully merged and products has proper quantity (max = equal or below stock level)"
		with(response){
			status == SC_CREATED
			data.entries.size()==1
			data.entries[0].product.code.toString() == PRODUCT_FLEXI_TRIPOD.toString()
			data.entries[0].quantity == STOCK_LEVEL_FLEXI_TRIPOD;
		}

		where:
		format <<[XML, JSON]
	}


	void "Customer merges carts after logon and ensure that source cart is removed: #format"(){
		given: "a customer with two carts (one created in the past and one created anonymously) "
		final def (customer, customerCart) = createCustomerWithProductsInCart(restClient, PRODUCT_REMOTE_CONTROL_TRIPOD, 1, format)
		removeAuthorization(restClient)
		def anonymousCart = createAnonymousCart(restClient, format)
		addProductToCartOnline(restClient, ANONYMOUS_USER, anonymousCart.guid, PRODUCT_EOS_40D_BODY, 1, format)
		authorizeCustomer(restClient, customer)

		when: "customer decides to restore the cart and provides two guids"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/',
				query : ['fields' : FIELD_SET_LEVEL_FULL, 'oldCartId' : anonymousCart.guid, 'toMergeCartGuid' : customerCart.guid],
				contentType : format,
		)

		then: "carts are successfully merged and source cart is deleted"
		with (response) {
			status == SC_CREATED
			data.entries.size()==2
		}
		and:
		removeAuthorization(restClient)
		HttpResponseDecorator response2 = restClient.post(
				path : getBasePathWithSite() + '/users/anonymous/carts/' + anonymousCart.guid,
				contentType : format,
		)
		response2.status == SC_BAD_REQUEST

		where:
		format <<[XML, JSON]
	}

	void "Anonymous cart merge with someone others cart must fail: #format"(){
		given: "a customer and someone others cart"
		final def (customer, customerCart) = createCustomerWithProductsInCart(restClient, PRODUCT_REMOTE_CONTROL_TRIPOD, 1, format)
		removeAuthorization(restClient)
		def anonymousCart = createAnonymousCart(restClient, format)
		addProductToCartOnline(restClient, ANONYMOUS_USER, anonymousCart.guid, PRODUCT_EOS_40D_BODY, 1, format)
		def otherCustomer = registerAndAuthorizeCustomer(restClient, format)

		when: "customer decides to restore the cart and provides two guids"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ otherCustomer.id +'/carts/',
				query : ['fields' : FIELD_SET_LEVEL_FULL, 'oldCartId' : anonymousCart.guid, 'toMergeCartGuid' : customerCart.guid],
				contentType : format,
		)

		then: "an exception is thrown"
		with (response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CartError'
			data.errors[0].reason == 'cannotRestore'
			data.errors[0].subject == customerCart.guid
		}

		where:
		format <<[XML, JSON]
	}

	void "Merging two carts should be possible only when cart provided by oldCartId is created anonymously: #format"(){
		given: "a customer with two carts (both created as logged customer) "
		final def customer = registerAndAuthorizeCustomer(restClient, format);
		final def customerCart1 = createCart(restClient, customer, format);
		final def customerCart2 = createCart(restClient, customer, format);
		addProductToCartOnline(restClient, customer, customerCart1.code, PRODUCT_REMOTE_CONTROL_TRIPOD, 1, format);
		addProductToCartOnline(restClient, customer, customerCart2.code, PRODUCT_EOS_40D_BODY, 1, format);

		when: "customer decides to merge two not anonymously created carts"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/',
				query : ['fields' : FIELD_SET_LEVEL_FULL, 'oldCartId' : customerCart1.guid, 'toMergeCartGuid' : customerCart2.guid],
				contentType : format,
		)

		then: "exception is thrown"
		with (response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CartError'
			data.errors[0].reason == 'cannotRestore'
			data.errors[0].subject == customerCart1.guid
		}

		where:
		format <<[XML, JSON]
	}

	void "Merging two carts should be possible only when cart given by toMergeCartGuid is created by logged user: #format"(){
		given: "a customer and two anonymously created carts "
		final def anonymousCart1 = createCart(restClient, ANONYMOUS_USER, format);
		addProductToCartOnline(restClient, ANONYMOUS_USER, anonymousCart1.guid, PRODUCT_EOS_40D_BODY, 1, format);
		final def anonymousCart2 = createCart(restClient, ANONYMOUS_USER, format);
		addProductToCartOnline(restClient, ANONYMOUS_USER, anonymousCart2.guid, PRODUCT_EOS_40D_BODY, 1, format);
		final def customer = registerAndAuthorizeCustomer(restClient, format);

		when: "customer decides to merge two not anonymously created carts"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/',
				query : ['fields' : FIELD_SET_LEVEL_FULL, 'oldCartId' : anonymousCart1.guid, 'toMergeCartGuid' : anonymousCart2.guid],
				contentType : format,
		)

		then: "exception is thrown"
		with (response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CartError'
			data.errors[0].reason == 'cannotRestore'
			data.errors[0].subject == anonymousCart2.guid
		}

		where:
		format <<[XML, JSON]
	}

	void "Anonymous cart should be restored if no TO MERGE cart is provided: #format"(){
		given: "customer with anonymous cart"
		def anonymousCart = createAnonymousCart(restClient, format)
		addProductToCartOnline(restClient, ANONYMOUS_USER, anonymousCart.guid, PRODUCT_FLEXI_TRIPOD, 1, format)
		final def customer = registerAndAuthorizeCustomer(restClient, format);

		when: "customer decides to restore the cart and provides only anonymous cart guid"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/',
				query : ['fields' : FIELD_SET_LEVEL_FULL, 'oldCartId' : anonymousCart.guid],
				contentType : format,
		)

		then: "anonymous cart is restored"
		with(response){
			status == SC_CREATED
			data.entries.size()==1
			data.entries[0].product.code.toString() == PRODUCT_FLEXI_TRIPOD.toString()
			data.entries[0].quantity == 1;
		}

		where:
		format <<[XML, JSON]
	}

	void "Customer cart should be restored if no FROM MERGE cart is provided: #format"(){
		given: "customer with cart"
		final def (customer, customerCart) = createCustomerWithProductsInCart(restClient, PRODUCT_REMOTE_CONTROL_TRIPOD, 1, format)
		removeAuthorization(restClient)
		authorizeCustomer(restClient, customer)

		when: "customer decides to restore the cart and provides only his cart guid"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/',
				query : ['fields' : FIELD_SET_LEVEL_FULL, 'toMergeCartGuid' : customerCart.guid],
				contentType : format,
		)

		then: "user cart is restored"
		with(response){
			status == SC_CREATED
			data.entries.size()==1
			data.entries[0].product.code.toString() == PRODUCT_REMOTE_CONTROL_TRIPOD.toString()
			data.entries[0].quantity == 1;
		}

		where:
		format <<[XML, JSON]
	}
}
