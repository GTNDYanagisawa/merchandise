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

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest

import org.junit.Test
import org.junit.experimental.categories.Category

@Category(CollectOutputFromTest.class)
@ManualTest
class StoresTests extends BaseWSTest {

	//covered
	@Test
	void testGetStoresInMunichJSON() {
		def con = testUtil.getSecureConnection("/stores?query=munich", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert !response.stores
		assert response.pagination.totalResults == 0
		assert response.pagination.totalPages == 0
	}

	//covered
	@Test
	void testGetStoresInChoshiJSON() {
		def con = testUtil.getSecureConnection("/stores?query=choshi&radius=500&fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.pagination.pageSize == 20
		assert response.pagination.totalResults == 1
		assert response.pagination.totalPages == 1

		def store = response.stores[0]
		assert store.formattedDistance == '0 km'
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

	//covered
	@Test
	void testGetStoresInTokioWithRadiusJSON() {
		def con = testUtil.getSecureConnection("/stores?query=tokyo&radius=1000", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 11
		assert response.pagination.pageSize == 20
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 1
	}

	//covered
	@Test
	void testGetStoresInTokioAndThenChangeAPageJSON() {

		def con = testUtil.getSecureConnection("/stores?query=tokyo&radius=1000&pageSize=10", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.pagination.pageSize == 10
		assert response.pagination.currentPage == 0
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2

		con = testUtil.getSecureConnection("/stores?query=tokyo&radius=1000&pageSize=10&currentPage=1", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.pagination.currentPage == 1
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	//covered
	@Test
	void testGetStoresInChoshiWithFieldsJSON() {
		def con = testUtil.getSecureConnection("/stores?query=choshi&radius=500&fields=stores(formattedDistance,openingHours),pagination", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.stores[0].formattedDistance == '0 km'
		assert response.stores[0].openingHours != null
		assert response.pagination.pageSize == 20
		assert response.pagination.totalResults == 1
		assert response.pagination.totalPages == 1
	}

	//covered
	@Test
	void testGetStoresByLatAndLongitudeJSON() {
		def con = testUtil.getSecureConnection("/stores?longitude=139.69&latitude=35.65&radius=4500&fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 11
		assert response.stores[0].formattedDistance == '4.4 km'
		assert response.stores[1].formattedDistance == '4.4 km'
		assert response.pagination.pageSize == 20
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 1
	}

	// covered
	@Test
	void testGetStoresByLatAndLongitudeAndThenChangeAPageJSON() {

		def con = testUtil.getSecureConnection("/stores?longitude=139.69&latitude=35.65&radius=4500&pageSize=10", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, true, false)
		assert response.stores.size == 10
		assert response.pagination.pageSize == 10
		assert response.pagination.currentPage == 0
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2

		con = testUtil.getSecureConnection("/stores?longitude=139.69&latitude=35.65&radius=4500&pageSize=10&currentPage=1", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.pagination.currentPage == 1
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	//covered
	@Test
	void testGetStoresByLatAndLongitudeWithFieldsJSON() {
		def con = testUtil.getSecureConnection("/stores?longitude=139.691706&latitude=35.689488&radius=500&fields=stores(formattedDistance,openingHours),pagination", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 11
		assert response.stores[0].formattedDistance == '0 km'
		assert response.stores[0].openingHours != null
		assert response.pagination.pageSize == 20
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 1
	}

	//covered
	@Test
	void testGetStoresByLatAndLongitudeWithAccuracyJSON() {
		def con = testUtil.getSecureConnection("/stores?longitude=139.69&latitude=35.65&radius=4000&accuracy=500&fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 11
		assert response.stores[0].formattedDistance == '4.4 km'
		assert response.stores[1].formattedDistance == '4.4 km'
		assert response.pagination.pageSize == 20
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 1
	}

	//covered
	@Test
	void testGetAllStoresJSON() {
		def con = testUtil.getSecureConnection("/stores?pageSize=10", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 49
		assert response.pagination.totalPages == 5
	}

	//covered
	@Test
	void testGetSpecificStoreJSON() {
		def con = testUtil.getSecureConnection("/stores/WS-Nakano", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.name == 'WS-Nakano'
		assert response.geoPoint.latitude == 35.6894875
		assert response.geoPoint.longitude == 139.6917064
	}

	//covered
	@Test
	void testGetSpecificStoreXML() {
		def con = testUtil.getSecureConnection("/stores/WS-Nakano", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name == 'WS-Nakano'
		assert response.geoPoint.latitude == 35.6894875
		assert response.geoPoint.longitude == 139.6917064
	}
}