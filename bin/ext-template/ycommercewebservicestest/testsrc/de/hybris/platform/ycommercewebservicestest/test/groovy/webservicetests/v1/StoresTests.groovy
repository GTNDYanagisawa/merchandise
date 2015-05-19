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

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v1



import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.TestUtil
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest

import org.junit.Test
import org.junit.experimental.categories.Category

@Category(CollectOutputFromTest.class)
@ManualTest
class StoresTests extends BaseWSTest {

	@Test
	void testGetStoresInMunichJSON() {
		def con = testUtil.getConnection("/stores?query=munich", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size() == 0
		assert response.pagination.totalResults == 0
		assert response.pagination.totalPages == 0
	}

	@Test
	void testGetStoresInChoshiJSON() {
		def con = testUtil.getConnection("/stores?query=choshi&radius=500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 1
		assert response.pagination.totalPages == 1

		def store = response.stores[0]
		assert store.formattedDistance == '0 km'
		assert store.openingHours == null
		assert store.geoPoint != null
		assert store.name == "WS-Choshi"
		assert store.address.country.name == "Japan"
		assert store.address.country.isocode == "JP"
		assert store.address.town == "Choshi"
		assert store.address.line1 == "Chiba-ken Choshi-shi"

		def storeFeatures = store.features.collect { it.key.toString() }
		assert storeFeatures.containsAll([
			'sundayWorkshops',
			'creche',
			'buyOnlinePickupInStore'
		])

		assert response.locationText == "choshi"
	}

	@Test
	void testGetStoresInTokioWithRadiusJSON() {
		def con = testUtil.getConnection("/stores?query=tokyo&radius=1000", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	@Test
	void testGetStoresInTokioAndThenChangeAPageJSON() {

		def con = testUtil.getConnection("/stores?query=tokyo&radius=1000", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.pagination.pageSize == 10
		assert response.pagination.currentPage == 0
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2

		con = testUtil.getConnection("/stores?query=tokyo&radius=1000&currentPage=1", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.pagination.currentPage == 1
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	@Test
	void testGetStoresInChoshiWithOptionsJSON() {
		def con = testUtil.getConnection("/stores?query=Choshi&options=HOURS&radius=500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.stores[0].formattedDistance == '0 km'
		assert response.stores[0].openingHours != null
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 1
		assert response.pagination.totalPages == 1
	}

	@Test
	void testGetStoresByLatAndLongitudeJSON() {
		def con = testUtil.getConnection("/stores?longitude=139.69&latitude=35.65&radius=4500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.stores[0].formattedDistance == '4.4 km'
		assert response.stores[1].formattedDistance == '4.4 km'
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	@Test
	void testGetStoresByLatAndLongitudeAndThenChangeAPageJSON() {

		def con = testUtil.getConnection("/stores?longitude=139.69&latitude=35.65&radius=4500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.pagination.pageSize == 10
		assert response.pagination.currentPage == 0
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2

		con = testUtil.getConnection("/stores?longitude=139.69&latitude=35.65&radius=4500&currentPage=1", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.pagination.currentPage == 1
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	@Test
	void testGetStoresByLatAndLongitudeWithOptionsJSON() {
		def con = testUtil.getConnection("/stores?longitude=140.8064&latitude=35.7409&options=HOURS&radius=500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.stores[0].formattedDistance == '0 km'
		assert response.stores[0].openingHours != null
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 1
		assert response.pagination.totalPages == 1
	}

	@Test
	void testGetStoresByLatAndLongitudeWithAccuracyJSON() {
		def con = testUtil.getConnection("/stores?longitude=139.69&latitude=35.65&radius=4000&accuracy=500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.stores[0].formattedDistance == '4.4 km'
		assert response.stores[1].formattedDistance == '4.4 km'
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	@Test
	void testGetAllStoresJSON() {
		def con = testUtil.getConnection("/stores", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 49
		assert response.pagination.totalPages == 5
	}

	@Test
	void testGetSpecificStoreJSON() {
		def con = testUtil.getConnection("/stores/WS-Nakano", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.name == 'WS-Nakano'
		assert response.geoPoint.latitude == 35.6894875
		assert response.geoPoint.longitude == 139.6917064
	}

	@Test
	void testGetSpecificStoreXML() {
		def con = testUtil.getConnection("/stores/WS-Nakano", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name == 'WS-Nakano'
		assert response.geoPoint.latitude == 35.6894875
		assert response.geoPoint.longitude == 139.6917064
	}
}