/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.addons.v2
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v1.CustomerTests
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.BaseWSTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.CartTests

import org.junit.Test

@ManualTest
class ExtendedCartV2Tests extends BaseWSTest {
	static final PASSWORD = "test"
	static final STORE_NAME = "WS-Shinbashi"
	static final ANOTHER_STORE_NAME = "WS-Tokio Hotel Metropolitan Tokyo"

	@Test
	void testGetConsolidatedPickupLocations() {
		def cartTests = new CartTests();
		def con, response
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		// add item which is out of stock in the online store
		def cartCode = cartTests.createRegularUsersCart(uid, access_token)
		cartTests.addProductToRegularUsersCart(uid, cartCode, 2006139, 1, STORE_NAME, access_token)


		// add pickup item to the cart
		cartTests.addProductToRegularUsersCart(uid, cartCode, 1934793, 2, ANOTHER_STORE_NAME, access_token)

		// get consolidated options
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/consolidate?fields=${FULL_SET}",'GET', 'JSON', HttpURLConnection.HTTP_OK, null,null,access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.pointOfServices.size() == 2
		assert response.pointOfServices[0].name == STORE_NAME
		assert response.pointOfServices[1].name == ANOTHER_STORE_NAME
	}

	@Test
	void testConsolidatePickupLocations() {
		def cartTests = new CartTests();
		def con, response
		def customerTests = new CustomerTests()
		def uid = customerTests.registerUser()
		def access_token = testUtil.getAccessToken(uid, PASSWORD)

		// add item which is out of stock in the online store
		def cartCode = cartTests.createRegularUsersCart(uid, access_token)
		cartTests.addProductToRegularUsersCart(uid, cartCode, 2006139, 1, STORE_NAME, access_token)

		// add pickup item to the cart
		cartTests.addProductToRegularUsersCart(uid, cartCode, 1934793, 2, ANOTHER_STORE_NAME, access_token)

		// consolidate pickup locations
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}/consolidate?storeName=${STORE_NAME}&fields=${FULL_SET}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, null,access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)

		// get the cart
		con = testUtil.getSecureConnection("/users/${uid}/carts/${cartCode}?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null,access_token)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.totalItems == 2
		assert response.totalUnitCount == 3
		assert response.entries[0].product.availableForPickup == true
		assert response.entries[0].deliveryPointOfService.name == STORE_NAME
		assert response.entries[1].product.availableForPickup == true
		assert response.entries[1].deliveryPointOfService.name == STORE_NAME
		assert response.pickupOrderGroups[0].entries[0].product.availableForPickup == true
		assert response.pickupOrderGroups[0].entries[0].deliveryPointOfService.name == STORE_NAME
		assert response.pickupOrderGroups[0].entries[1].product.availableForPickup == true
		assert response.pickupOrderGroups[0].entries[1].deliveryPointOfService.name == STORE_NAME
		assert response.pickupOrderGroups[0].deliveryPointOfService.name == STORE_NAME
	}
}
