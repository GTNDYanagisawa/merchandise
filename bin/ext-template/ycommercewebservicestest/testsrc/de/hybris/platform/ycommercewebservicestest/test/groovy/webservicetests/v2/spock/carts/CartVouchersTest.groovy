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
import groovyx.net.http.RESTClient
/**
 *
 *
 */
@Unroll
@ManualTest
class CartVouchersTest extends AbstractCartTest {

	def "Customer applies voucher to cart: #format"(){
		given: "a logged in customer with a cart and some items in the cart"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480, 2, format)
		cart = getCart(restClient, customer, cart.code, format)
		def oldPrice = cart.totalPrice.value.toDouble()

		when: "customer applies a voucher to the cart"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/'+cart.code + '/vouchers',
				query : ['voucherId' : PROMOTION_VOUCHER_CODE],
				contentType : format,
				requestContentType : URLENC
				)

		and: "retrieves carth with applied voucher"
		def cartWithVoucher=getCart(restClient, customer, cart.code, format)
		def newPrice = cartWithVoucher.totalPrice.value.toDouble()

		then: "voucher application was successfull"
		with (response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}

		and: "total price is reduced"
		newPrice < oldPrice

		and: "expected values are visible on the cart"
		with (cartWithVoucher){
			isNotEmpty(appliedVouchers)
			appliedVouchers.size() == 1
			appliedVouchers[0].code == "abc"
			appliedVouchers[0].freeShipping == false
			appliedVouchers[0].voucherCode == PROMOTION_VOUCHER_CODE
			appliedVouchers[0].name == "New Promotional Voucher"
			appliedVouchers[0].description == "Promotion Voucher Description"
			appliedVouchers[0].value.toDouble() == 10.0
			appliedVouchers[0].valueString == '10.0%'
			appliedVouchers[0].valueFormatted == '10.0%'

			appliedVouchers[0].appliedValue.priceType == 'BUY'
			appliedVouchers[0].appliedValue.currencyIso == 'USD'
			appliedVouchers[0].appliedValue.formattedValue == '$22.37'
			((appliedVouchers[0].appliedValue.value.toDouble() * 100.0).round() / 100) == 22.37
			totalItems == 1
			totalUnitCount == 2
			totalPrice.value == 201.31
		}
		where:
		format << [XML, JSON]
	}

	def "Anonymous customer applies voucher to cart: #format"(){
		given: "not logged in customer using client with anonymous cart and some items in the cart"
		authorizeClient(restClient)
		def cart = createAnonymousCart(restClient, format)
		def anonymous = ['id':'anonymous']
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_POWER_SHOT_A480, 2, format)
		cart = getCart(restClient, anonymous, cart.guid, format)
		def oldPrice = cart.totalPrice.value.toDouble()

		when: "customer applies a voucher to the cart"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/anonymous/carts/'+cart.guid + '/vouchers',
				query : ['voucherId' : PROMOTION_VOUCHER_CODE],
				contentType : format,
				requestContentType : URLENC
				)

		and: "retrieves carth with applied voucher"
		def cartWithVoucher=getCart(restClient, anonymous, cart.guid, format)
		def newPrice = cartWithVoucher.totalPrice.value.toDouble()

		then: "voucher application was successfull"
		with (response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}

		and: "total price is reduced"
		newPrice < oldPrice

		where:
		format << [XML, JSON]
	}

	def "Customer applies restricted voucher to cart not meeting the requirements: #format"(){
		given: "a logged in customer with a cart and some items in the cart"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480, 2, format)
		cart = getCart(restClient, customer, cart.code, format)
		def price = cart.totalPrice.value.toDouble()

		when: "customer attempts to apply a voucher to the cart"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/'+cart.code + '/vouchers',
				query : ['voucherId' : RESTRICTED_PROMOTION_VOUCHER_CODE],
				contentType : format,
				requestContentType : URLENC
				)

		then: "voucher application was not successfull"
		price < 250 //this is the limit at which voucher is applicable
		with (response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'VoucherOperationError'
			data.errors[0].message == 'Voucher cannot be redeemed: ' + RESTRICTED_PROMOTION_VOUCHER_CODE
		}

		where:
		format << [XML, JSON]
	}

	def "Customer applies restricted voucher to cart meeting the requirements: #format"(){
		given: "a logged in customer with a cart and some items in the cart"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480, 3, format)
		cart = getCart(restClient, customer, cart.code, format)
		def price = cart.totalPrice.value.toDouble()

		when: "customer attempts to apply a voucher to the cart"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/'+cart.code + '/vouchers',
				query : ['voucherId' : RESTRICTED_PROMOTION_VOUCHER_CODE],
				contentType : format,
				requestContentType : URLENC
				)

		and: "retrieves carth with applied voucher"
		def cartWithVoucher=getCart(restClient, customer, cart.code, format)
		def newPrice = cartWithVoucher.totalPrice.value.toDouble()

		then: "voucher application was successfull"
		price >= 250 //this is the limit at which voucher is applicable
		with (response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}

		and: "voucher is visble on the cart"
		with(cartWithVoucher){
			isNotEmpty(appliedVouchers)
			appliedVouchers.size() == 1
			appliedVouchers[0].code == "abr"
			appliedVouchers[0].freeShipping == false
			appliedVouchers[0].voucherCode == RESTRICTED_PROMOTION_VOUCHER_CODE
			appliedVouchers[0].value.toDouble() == 10
			totalItems == 1
			totalUnitCount == 3
		}

		where:
		format << [XML, JSON]
	}

	def "Customer applies absolute voucher to cart: #format"(){
		given: "a logged in customer with a cart and some items in the cart"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480, 2, format)
		cart = getCart(restClient, customer, cart.code, format)
		def price = cart.totalPrice.value.toDouble()

		when: "customer attempts to apply a voucher to the cart"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/'+cart.code + '/vouchers',
				query : ['voucherId' : ABSOLUTE_VOUCHER_CODE],
				contentType : format,
				requestContentType : URLENC
				)

		and: "retrieves carth with applied voucher"
		def cartWithVoucher=getCart(restClient, customer, cart.code, format)
		def newPrice = cartWithVoucher.totalPrice.value.toDouble()

		then: "voucher application was successfull"
		with (response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}

		and: "voucher is visble on the cart"
		with(cartWithVoucher){
			isNotEmpty(appliedVouchers)
			appliedVouchers.size() == 1
			appliedVouchers.size() == 1
			appliedVouchers[0].code == "xyz"
			appliedVouchers[0].freeShipping == true
			appliedVouchers[0].voucherCode == ABSOLUTE_VOUCHER_CODE
			appliedVouchers[0].name == "New Voucher"
			appliedVouchers[0].description == "Voucher Description"
			appliedVouchers[0].value.toDouble() == 15
			appliedVouchers[0].valueString == '15.0 USD'
			appliedVouchers[0].valueFormatted == '15.0 USD'
			appliedVouchers[0].currency.isocode == "USD"
			appliedVouchers[0].currency.name == 'US Dollar'
			appliedVouchers[0].currency.symbol == '$'
			appliedVouchers[0].appliedValue.value.toDouble() == 15.0
			appliedVouchers[0].appliedValue.priceType == 'BUY'
			appliedVouchers[0].appliedValue.currencyIso == 'USD'
			appliedVouchers[0].appliedValue.formattedValue == '$15.00'
			totalItems == 1
			totalUnitCount == 2
			totalPrice.value.toDouble() == 208.68
		}

		where:
		format << [XML, JSON]
	}

	def "Customer applies invalid voucher to cart: #format"(){
		given: "a logged in customer with a cart and some items in the cart"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480, 2, format)
		cart = getCart(restClient, customer, cart.code, format)
		def price = cart.totalPrice.value.toDouble()

		when: "customer attempts to apply invalid voucher to the cart"
		HttpResponseDecorator response = restClient.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/'+cart.code + '/vouchers',
				query : ['voucherId' : 'invalidVoucher'],
				contentType : format,
				requestContentType : URLENC
				)

		then: "voucher application fails"
		with (response){
			status == SC_BAD_REQUEST
			data.errors[0].type == 'VoucherOperationError'
			data.errors[0].message == 'Voucher not found: invalidVoucher'
		}

		where:
		format << [XML, JSON]
	}

	def "Customer removes voucher from cart: #format"(){
		given: "a logged in customer with a cart and some items in the cart and voucher applied to the cart"
		def val = createAndAuthorizeCustomerWithCart(restClient, format)
		def customer = val[0]
		def cart = val[1]
		addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480, 2, format)
		applyVoucherToCart(restClient, customer, cart.code, PROMOTION_VOUCHER_CODE, format)
		cart = getCart(restClient, customer, cart.code, format)
		def price = cart.totalPrice.value.toDouble()


		when: "customer attempts to remove voucher from cart"
		HttpResponseDecorator response = restClient.delete(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/'+cart.code + '/vouchers/'+PROMOTION_VOUCHER_CODE,
				contentType : format,
				requestContentType : URLENC
				)

		and: "retrieves updated cart"
		def cart2 = getCart(restClient, customer, cart.code, format)
		def price2 = cart2.totalPrice.value.toDouble()

		then: "voucher is removed from cart"
		with (response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}

		and: "price is adjusted to reflect voucher removal"
		price2 > price

		where:
		format << [XML, JSON]
	}

}
