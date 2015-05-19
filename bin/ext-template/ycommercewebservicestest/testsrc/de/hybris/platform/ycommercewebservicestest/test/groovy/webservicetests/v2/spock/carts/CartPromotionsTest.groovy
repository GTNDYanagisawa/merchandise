/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import de.hybris.bootstrap.annotations.ManualTest

import spock.lang.Unroll
import groovyx.net.http.HttpResponseDecorator
/**
 *
 *
 */
@Unroll
@ManualTest
class CartPromotionsTest extends AbstractCartTest {

	def "Restricted promotion is applied to anonymous cart: #format"(){
		given: "an anonymous cart with a product in it"
		authorizeTrustedClient(restClient)
		def cart = createAnonymousCart(restClient, format)
		def anonymous = ['id':'anonymous']
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_EOS_40D_BODY, 1, format)
		cart = getCart(restClient, anonymous, cart.guid, format)
		def price = cart.totalPrice.value.toDouble()

		when: "a restricted promotion is applied to the cart"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/anonymous/carts/' + cart.guid + '/promotions',
				query : ['promotionId' : RESTRICTED_PROMOTION_CODE],
				contentType : format,
				requestContentType : URLENC
				)

		and: "a cart is retrieved"
		cart = getCart(restClient, anonymous, cart.guid, format)
		def price2 = cart.totalPrice.value.toDouble()

		then: "promotion was successfully applied"
		price > price2
		with (response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}
		with(cart){
			isNotEmpty(appliedOrderPromotions)
			appliedOrderPromotions[0].promotion.code == RESTRICTED_PROMOTION_CODE
		}

		where:
		format << [XML, JSON]
	}

	def "Restricted promotion is removed from anonymous cart: #format"(){
		given: "an anonymous cart with a product in it and promotion applied"
		authorizeTrustedClient(restClient)
		def cart = createAnonymousCart(restClient, format)
		def anonymous = ['id':'anonymous']
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_EOS_40D_BODY, 1, format)
		applyPromotionToCart(restClient, anonymous, cart.guid, RESTRICTED_PROMOTION_CODE, format)
		cart = getCart(restClient, anonymous, cart.guid, format)
		def price = cart.totalPrice.value.toDouble()

		when: "a restricted promotion is removed from the cart"
		HttpResponseDecorator response = restClient.delete(
				path : getBasePathWithSite() + '/users/anonymous/carts/' + cart.guid + '/promotions/' + RESTRICTED_PROMOTION_CODE,
				contentType : format,
				requestContentType : URLENC
				)

		and: "a cart is retrieved"
		cart = getCart(restClient, anonymous, cart.guid, format)
		def price2 = cart.totalPrice.value.toDouble()

		then: "promotion was successfully removed"
		price < price2
		with (response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}
		with(cart){ !isNotEmpty(appliedOrderPromotions) }

		where:
		format << [XML, JSON]
	}

	def "Restricted promotion is applied to anonymous cart as potential promotion: #format"(){
		given: "an empty anonymous cart"
		authorizeTrustedClient(restClient)
		def cart = createAnonymousCart(restClient, format)
		def anonymous = ['id':'anonymous']

		when: "a restricted promotion is applied to the cart"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/anonymous/carts/' + cart.guid + '/promotions',
				query : ['promotionId' : RESTRICTED_PROMOTION_CODE],
				contentType : format,
				requestContentType : URLENC
				)

		and: "a cart is retrieved"
		cart = getCart(restClient, anonymous, cart.guid, format)

		then: "promotion was successfully applied"
		with (response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}
		with(cart){
			isNotEmpty(potentialOrderPromotions)
			potentialOrderPromotions[0].promotion.code == RESTRICTED_PROMOTION_CODE
		}

		where:
		format << [XML, JSON]
	}

	def "Attempt to enable unrestricted promotion fails: #format"(){
		//unrestricted promotion is one that is automatically handled by the system and cannot be enabled or disabled
		given: "an anonymous cart and trusted client"
		authorizeTrustedClient(restClient)
		def cart = createAnonymousCart(restClient, format)

		when: "attempt to enable unrestricted promotion is made"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/anonymous/carts/' + cart.guid + '/promotions',
				query : ['promotionId' : PROMOTION_CODE],
				contentType : format,
				requestContentType : URLENC
				)

		then: "this attempt fails"
		with(response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CommercePromotionRestrictionError'
		}

		where:
		format << [XML, JSON]
	}

	def "Attempt to disable unrestricted promotion fails: #format"(){
		given: "an anonymous cart and trusted client"
		authorizeTrustedClient(restClient)
		def cart = createAnonymousCart(restClient, format)

		when: "attempt to enable unrestricted promotion is made"
		HttpResponseDecorator response = restClient.delete(
				path : getBasePathWithSite() + '/users/anonymous/carts/' + cart.guid + '/promotions/'+PROMOTION_CODE,
				contentType : format,
				requestContentType : URLENC
				)

		then: "this attempt fails"
		with(response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'CommercePromotionRestrictionError'
		}

		where:
		format << [XML, JSON]
	}

	def "Customer should not be allowed to apply promotions: #format"(){
		given: "an authorized customer with cart"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]

		when: "attempt to enable unrestricted promotion is made"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/promotions',
				query : ['promotionId' : RESTRICTED_PROMOTION_CODE],
				contentType : format,
				requestContentType : URLENC
				)

		then: "this attempt fails"
		with(response){ status == SC_UNAUTHORIZED }

		where:
		format << [XML, JSON]
	}

	def "Customer should not be allowed to disable promotions: #format"(){
		given: "an authorized customer with cart"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]

		when: "attempt to enable unrestricted promotion is made"
		HttpResponseDecorator response = restClient.delete(
				path : getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/promotions/' + RESTRICTED_PROMOTION_CODE,
				query : ['promotionId' : RESTRICTED_PROMOTION_CODE],
				contentType : format,
				requestContentType : URLENC
				)

		then: "this attempt fails"
		with(response){ status == SC_UNAUTHORIZED }

		where:
		format << [XML, JSON]
	}
}
