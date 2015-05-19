package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest

import org.junit.Test

@org.junit.experimental.categories.Category(CollectOutputFromTest.class)
@ManualTest
class ExportTests extends BaseWSTest{

	static final NUMBER_OF_ALL_PRODUCTS = 28

	//covered
	@Test
	void testExportProductsFullXML() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def con = testUtil.getSecureConnection("/export/products", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'productList'
		assert response.products
		assert response.products.size() == NUMBER_OF_ALL_PRODUCTS
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 1
		assert response.currentPage == 0

		//change pageSize to 20
		con = testUtil.getSecureConnection("/export/products?pageSize=20", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'productList'
		assert response.products
		assert response.products.size() == 20
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 2
		assert response.currentPage == 0
	}

	//covered
	@Test
	void testExportProductsFullJSON() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def con = testUtil.getSecureConnection("/export/products", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedJSONSlurper(con, false, false)

		assert response.products.size() == NUMBER_OF_ALL_PRODUCTS
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 1
		assert response.currentPage == 0

		//change pageSize to 20
		con = testUtil.getSecureConnection("/export/products?pageSize=20", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.products.size() == 20
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 2
		assert response.currentPage == 0
	}

	//covered
	@Test
	void testExportProductsIncrementalXML() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		//def con = testUtil.getSecureConnection("/export/products", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, trustedClientCredentialsToken)
		//2007-08-31T16:47+00:00
		def con = testUtil.getSecureConnection("/export/products?timestamp=2012-03-28T07:50:49%2B00:00", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'productList'
		assert response.products
		assert response.products.size() == NUMBER_OF_ALL_PRODUCTS
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 1
		assert response.currentPage == 0

		con = testUtil.getSecureConnection("/export/products?timestamp=2113-06-28T07:50:49%2B00:00", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'productList'
		assert response.products
		assert response.products.size() == 0
		assert response.totalProductCount == 0
		assert response.totalPageCount == 0
		assert response.currentPage == 0
	}

	//covered
	@Test
	void testExportProductsIncrementalJSON() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		//def con = testUtil.getSecureConnection("/export/products", 'GET', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, null, trustedClientCredentialsToken)
		//2007-08-31T16:47+00:00
		def con = testUtil.getSecureConnection("/export/products?timestamp=2012-03-28T07:50:49%2B00:00", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.products
		assert response.products.size() == NUMBER_OF_ALL_PRODUCTS
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 1
		assert response.currentPage == 0

		con = testUtil.getSecureConnection("/export/products?timestamp=2113-06-28T07:50:49%2B00:00", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert !response.products
		assert response.totalProductCount == 0
		assert response.totalPageCount == 0
		assert response.currentPage == 0
	}
}
