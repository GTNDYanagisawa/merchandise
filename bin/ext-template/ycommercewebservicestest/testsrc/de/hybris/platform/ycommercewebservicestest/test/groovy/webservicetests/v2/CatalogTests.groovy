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
class CatalogTests extends BaseWSTest {

	@Test
	void testGetCatalogsXML() {
		def con = testUtil.getSecureConnection('/catalogs?fields=catalogs(FULL)', 'GET', 'XML')
		assert con.responseCode == HttpURLConnection.HTTP_OK : testUtil.messageResponseCode(con.responseCode, HttpURLConnection.HTTP_OK)


		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name() == 'catalogList'
		assert response.catalogs.size() == 1
		assert response.catalogs[0].id == 'wsTestProductCatalog'
		assert response.catalogs[0].name == 'wsTest Product Catalog'
		assert response.catalogs[0].url == '/wsTestProductCatalog'
		assert response.catalogs[0].catalogVersions.size() == 2
		def catVersions = response.catalogs[0].catalogVersions.collect { it.id.toString() }
		assert catVersions.containsAll(['Staged', 'Online'])
	}

	@Test
	void testGetCatalogsWithFieldsParameterXML() {
		//catalogVersion with categories
		def response = testUtil.verifiedXMLSlurper(testUtil.getSecureConnection('/catalogs?fields=catalogs(catalogVersions(id,categories))', 'GET', 'XML'))
		assert ['Staged', 'Online'].contains(response.catalogs[0].catalogVersions[1].id)
		def onlineCatalog = response.catalogs[0].catalogVersions.find({it.id == 'Online'})
		assert onlineCatalog != null
		assert onlineCatalog.categories.size() == 2

		//categories with subcategories
		response = testUtil.verifiedXMLSlurper(testUtil.getSecureConnection('/catalogs?fields=catalogs(catalogVersions(id,categories(subcategories)))', 'GET', 'XML'))
		onlineCatalog = onlineCatalog = response.catalogs[0].catalogVersions.find({it.id == 'Online'})
		assert onlineCatalog != null
		assert onlineCatalog.categories[0].subcategories.size() > 0
	}

	@Test
	void testGetCatalogXML() {
		def con = testUtil.getSecureConnection('/catalogs/wsTestProductCatalog')

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name() == 'catalog'
		assert response.id == 'wsTestProductCatalog'
		assert response.name == 'wsTest Product Catalog'
		assert response.url == '/wsTestProductCatalog'

		assert response.catalogVersions.size() == 2
		def catVersions = response.catalogVersions.collect { it.id.toString() }
		assert catVersions.containsAll(['Staged', 'Online'])
	}

	@Test
	void testGetCatalogVersionXML() {
		def con = testUtil.getSecureConnection('/catalogs/wsTestProductCatalog/Online')

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name() == 'catalogVersion'
		assert response.id == 'Online'
		assert response.url == '/wsTestProductCatalog/Online'
	}

	@Test
	void testGetCategoriesXML() {
		def con = testUtil.getSecureConnection('/catalogs/wsTestProductCatalog/Online/categories/brands?fields=id,name,url')

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name() == 'categoryHierarchy'
		assert response.id == 'brands'
		assert response.name == 'Brands'
		assert response.url == "/wsTest/catalogs/wsTestProductCatalog/Online/categories/brands"
	}

	//JSON JSON JSON
	@Test
	void testGetCatalogsJSON() {
		def con = testUtil.getSecureConnection('/catalogs?fields=catalogs(FULL)', 'GET', 'JSON')
		assert con.responseCode == HttpURLConnection.HTTP_OK : testUtil.messageResponseCode(con.responseCode, HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con)
		assert response.catalogs.size() == 1
		assert response.catalogs[0].id == 'wsTestProductCatalog'
		assert response.catalogs[0].name == 'wsTest Product Catalog'
		assert response.catalogs[0].url == '/wsTestProductCatalog'
		//TODO change to catalogversion - https://jira.hybris.com/browse/COMWS-34
		assert response.catalogs[0].catalogVersions.size() == 2
		def catVersions = response.catalogs[0].catalogVersions.collect { it.id.toString() }
		assert catVersions.containsAll(['Staged', 'Online'])
	}

	@Test
	void testGetCatalogsWithFieldsParameterJSON() {
		//catalogVersion with categories
		def response = testUtil.verifiedJSONSlurper(testUtil.getSecureConnection('/catalogs?fields=catalogs(catalogVersions(id,categories))', 'GET', 'JSON'))
		assert ['Staged', 'Online'].contains(response.catalogs[0].catalogVersions[1].id)
		def onlineCatalog = response.catalogs[0].catalogVersions.find{ version -> version.id == 'Online'}
		assert onlineCatalog != null
		assert onlineCatalog.categories.category.size() == 2

		//categories with subcategories
		response = testUtil.verifiedJSONSlurper(testUtil.getSecureConnection('/catalogs?fields=catalogs(catalogVersions(id,categories(subcategories)))', 'GET', 'JSON'))
		onlineCatalog = response.catalogs[0].catalogVersions.find{ version -> version.id == 'Online'}
		assert onlineCatalog != null
		assert onlineCatalog.categories[0].subcategories.size() > 0
	}

	@Test
	void testGetCatalogJSON() {
		def con = testUtil.getSecureConnection('/catalogs/wsTestProductCatalog', 'GET', 'JSON')

		def response = testUtil.verifiedJSONSlurper(con)
		assert response.id == 'wsTestProductCatalog'
		assert response.name == 'wsTest Product Catalog'
		assert response.url == '/wsTestProductCatalog'
		//TODO change to catalogVVVersion - https://jira.hybris.com/browse/COMWS-34
		assert response.catalogVersions.size() == 2
		def catVersions = response.catalogVersions.collect { it.id.toString() }
		assert catVersions.containsAll(['Staged', 'Online'])
	}

	@Test
	void testGetCatalogVersionJSON() {
		def con = testUtil.getSecureConnection('/catalogs/wsTestProductCatalog/Online', 'GET', 'JSON')

		def response = testUtil.verifiedJSONSlurper(con)
		assert response.id == 'Online'
		assert response.url == '/wsTestProductCatalog/Online'
	}

	@Test
	void testGetCategoriesJSON() {
		def con = testUtil.getSecureConnection('/catalogs/wsTestProductCatalog/Online/categories/brands?fields=id,name', 'GET', 'JSON')

		def response = testUtil.verifiedJSONSlurper(con)
		assert response.id == 'brands'
		assert response.name == 'Brands'
		assert !response.subcategories
		assert !response.products
	}


}