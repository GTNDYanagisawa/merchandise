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
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.AbstractUserTest

import spock.lang.Unroll
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

@Unroll
@ManualTest
/**
 *
 *
 */
abstract class AbstractCartTest extends AbstractUserTest {

	protected static final PRODUCT_FLEXI_TRIPOD = 3429337
	protected static final PRODUCT_EOS_40D_BODY = 1225694
	protected static final PRODUCT_M340 = 2006139 // <-- out of stock online
	protected static final PRODUCT_POWER_SHOT_A480 = 1934795 // <-- out of stock in STORE_SHINBASHI
	protected static final PRODUCT_POWER_SHOT_A480_2 = 1934793
	protected static final PRODUCT_REMOTE_CONTROL_TRIPOD = 1687508 // <-- to be used for stock modifications
	protected static final STORE_SHINBASHI = 'WS-Shinbashi'
	protected static final STORE_NAKANO = 'WS-Nakano'
	protected static final PROMOTION_VOUCHER_CODE = "abc-9PSW-EDH2-RXKA"
	protected static final RESTRICTED_PROMOTION_VOUCHER_CODE = "abr-D7S5-K14A-51Y5"
	protected static final ABSOLUTE_VOUCHER_CODE = "xyz-MHE2-B8L5-LPHE"

	protected static final PROMOTION_CODE = "WS_OrderThreshold15Discount"
	protected static final RESTRICTED_PROMOTION_CODE = "WS_RestrictedOrderThreshold15Discount"
	protected static final DELIVERY_STANDARD = 'standard-gross'
	protected static final DELIVERY_PICKUP = 'pickup'

	protected static final ANONYMOUS_USER = ['id':'anonymous']

	/**
	 * @param client REST client to be used
	 * @param format format to be used
	 * @return new anonymous cart
	 */
	protected createAnonymousCart(RESTClient client, format) {
		return createCart(client, ANONYMOUS_USER, format)
	}

	/**
	 * This method adds product to cart without specifying pickup store.
	 * It has no return value, but will check if the operation was successful.
	 * @param client REST client to be used
	 * @param customer customer represented as a map containing at least id
	 * @param cartCode ID of a cart (code or guid)
	 * @param productCode product to be added
	 * @param quantity quantity of products to be added
	 * @param format format to be used
	 */
	protected void addProductToCartOnline (RESTClient client, customer, cartCode, productCode, int quantity = 1, format=JSON){

		HttpResponseDecorator response = client.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/'+cartCode + '/entries',
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				body : ['code' : productCode,
					'qty' : quantity],
				contentType : format,
				requestContentType : URLENC
				)

		with(response){
			status == SC_OK
			data.statusCode == 'success'
			data.quantityAdded == quantity;
		}
	}

	/**
	 * This method adds specified product for pickup in specified store to the cart.
	 * It has no return value, but will check if the operation was successful.
	 * @param client REST client to be used
	 * @param customer customer to whom cart belongs
	 * @param cartCode Id (code or guid for anonymous) of a cart
	 * @param productCode Id of a product to be added to cart
	 * @param pickupStore Id of a store for pickup
	 */
	protected void addProductToCartPickup (RESTClient client, customer, cartCode, productCode, pickupStore, int quantity = 1, format=JSON){

		HttpResponseDecorator response = client.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/'+cartCode + '/entries',
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				body : ['code' : productCode,
					'qty' : quantity,
					'pickupStore' : pickupStore],
				contentType : format,
				requestContentType : URLENC
				)

		with(response){
			data.quantityAdded == quantity;
			data.statusCode == 'success' }
	}

	/**
	 * This method retrieves customer's cart
	 * @param client REST client to be used
	 * @param customer customer to whom cart belongs
	 * @param cartID Id (code or guid for anonymous) of a cart
	 * @param format format to be used
	 * @return cart
	 */
	protected getCart(RESTClient client, customer, cartID, format){
		HttpResponseDecorator response = client.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cartID,
				contentType: format,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC
				)

		with(response){ status == SC_OK }

		return response.data
	}

	/**
	 * This is service method that prepares customer and cart that belongs to the customer and has specified item in it.
	 * Similar to createAndAuthorizeCustomerWithCart(), but additionally puts product in the cart
	 * @param client REST client to be used
	 * @param productID Id of a product to be added
	 * @param quantity quantity to be added, defaults to 1
	 * @param format format to be used
	 * @return an array holding customer at position 0 and cart at position 1
	 */
	protected createCustomerWithProductsInCart(RESTClient client, productID, quantity=1, format){
		def val = createAndAuthorizeCustomerWithCart(client, format)
		def customer = val[0]
		def cart = val[1]
		addProductToCartOnline(client, customer, cart.code, productID, quantity, format)
		cart = getCart(client, customer, cart.code, format)
		return [customer, cart]
	}

	/**
	 * This method applies specified voucher to the cart.
	 * It has no return value, but will check if the operation was successful.
	 * @param client REST client to be used
	 * @param customer customer to whom the cart belongs
	 * @param cartID Id of a cart for which voucher should be applied (code or guid)
	 * @param voucherID Id of a voucher to be applied
	 * @param format format to be used
	 */
	protected void applyVoucherToCart(RESTClient client, customer, cartID, voucherID, format){
		HttpResponseDecorator response = client.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/'+cartID + '/vouchers',
				query : ['voucherId' : voucherID],
				contentType : format,
				requestContentType : URLENC
				)

		with (response){ status == SC_OK }
	}

	/**
	 * This method applies specified promotion to the cart.
	 * It has no return value, but will check if the operation was successful.
	 * @param client REST client to be used
	 * @param customer customer to whom the cart belongs
	 * @param cartID Id of cart for which promotion should be applied (code or guid)
	 * @param promotionCode Id of a promotion to be applied to the cart
	 * @param format format to be used
	 */
	protected void applyPromotionToCart(RESTClient client, customer, cartID, promotionCode, format){
		HttpResponseDecorator response = client.post(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/' + cartID + '/promotions',
				query : ['promotionId' : promotionCode],
				contentType : format,
				requestContentType : URLENC
				)
		with (response){ status == SC_OK }
	}

	/**
	 * This method sets specified address for the cart.
	 * It has no return value, but will check if the operation was successful.
	 * @param client REST client to be used
	 * @param customer customer to whom the cart belongs
	 * @param cartID Id of cart for which address should be set (code or guid)
	 * @param addressID Id of an address
	 * @param format format to be used
	 */
	protected void setDeliveryAddressForCart(RESTClient client, customer, cartID, addressID, format){
		def response = client.put(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/' + cartID + '/addresses/delivery',
				query : [ 'addressId' : addressID ],
				contentType : format,
				requestContentType : URLENC
				)
		with (response){ status == SC_OK }
	}

	/**
	 * This method sets specified delivery mode for cart.
	 * It has no return value, but will check if the operation was successful.
	 * @param client REST client to be used
	 * @param customer customer to whom the cart belongs
	 * @param cartID Id of cart for which delivery mode should be set (code or guid)
	 * @param deliveryModeId Id of a delivery mode
	 * @param format format to be used
	 */
	protected void setDeliveryModeForCart(RESTClient client, customer, cartID, deliveryModeId, format){
		HttpResponseDecorator response = client.put(
				path : getBasePathWithSite() + '/users/'+ customer.id +'/carts/' + cartID + '/deliverymode',
				query : [ 'deliveryModeId' : deliveryModeId ],
				contentType : format,
				requestContentType : URLENC
				)
		if(isNotEmpty(response.data)) println(response.data)
		with (response){ status == SC_OK }
	}

	/**
	 * This method uses platformwebservices to update stock of a given product.
	 * It is a simple implementation that will update first entry in stock list (online store)
	 * @param stockStatus stock status that should be set
	 * @param productId product for which stock status should be set
	 */
	protected void setStockStatus(RESTClient client, String stockStatus, productId) {

		HttpResponseDecorator response = client.get(
				path : '/ws410/rest/catalogs/wsTestProductCatalog/catalogversions/Online/products/' + PRODUCT_REMOTE_CONTROL_TRIPOD,
				contentType : XML,
				requestContentType : XML,
				headers : ['Authorization' : 'Basic '+ "admin:nimda".bytes.encodeBase64().toString()]
				)
		String stockUri = response.data.stockLevels.stockLevel[0].@uri
		stockUri = stockUri.substring(stockUri.indexOf("/ws410"))

		HttpResponseDecorator response2 = client.put(
				path : stockUri,
				contentType : XML,
				requestContentType : XML,
				headers : ['Authorization' : 'Basic '+ "admin:nimda".bytes.encodeBase64().toString()],
				body :  "<stocklevel><inStockStatus>"+stockStatus+"</inStockStatus></stocklevel>"
				)
		with(response2){ status == SC_OK}
	}

	/**
	 * This method sets provided delivery address for anonymous cart
	 * @param address address to be set (a map); will be used as post body
	 * @param cartGuid GUID of an anonymous cart
	 * @param format format to be used
	 */
	protected void setAddressForAnonymousCart(RESTClient client,address, cartGuid, format){
		HttpResponseDecorator response = client.post(
				path : getBasePathWithSite() + '/users/anonymous/carts/'+ cartGuid + "/addresses/delivery",
				body : address,
				query : ['fields' : FIELD_SET_LEVEL_FULL],
				contentType : format,
				requestContentType : URLENC
				)
		with(response){ status == SC_CREATED}
	}

	protected void addEmailToAnonymousCart(RESTClient client,cartGuid, emailAddress, format){
		HttpResponseDecorator response = client.put(
				path : getBasePathWithSite() + '/users/anonymous/carts/'+ cartGuid + "/email",
				query : ['email' : emailAddress],
				contentType : format,
				requestContentType : URLENC
				)
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}
	}

}