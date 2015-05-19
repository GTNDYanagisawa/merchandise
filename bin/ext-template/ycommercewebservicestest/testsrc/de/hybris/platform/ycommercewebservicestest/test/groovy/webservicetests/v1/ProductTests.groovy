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
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.AvoidCollectingOutputFromTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest

import org.junit.Test
import org.junit.experimental.categories.Category

@Category(CollectOutputFromTest.class)
@ManualTest
class ProductTests extends BaseWSTest {

	static final PRODUCT_ID_FLEXI_TRIPOD = "3429337"
	static final NUMBER_OF_ALL_PRODUCTS = 28
	static final STORE_NAME = "WS-Shinbashi"

	@Test
	void testSearchProductsBasicJSON() {
		def con = testUtil.getConnection("/products", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.products.size() > 0
		assert response.sorts.size() > 0
		assert response.pagination
		assert response.currentQuery
		assert response.facets.size() > 0
	}

	@Test
	void testSearchProductsBasicXML() {
		def con = testUtil.getConnection("/products", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'searchResult'
		assert response.products.product.size() > 0
		assert response.sorts.sort.size() > 0
		assert response.pagination
		assert response.currentQuery
		assert response.facets.facet.size() > 0
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsSpellingJSON() {
		def con = testUtil.getConnection("/products?query=somy", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.spellingSuggestion
		assert response.spellingSuggestion.suggestion == 'sony'
		assert (response.spellingSuggestion.query == 'sony:topRated') || (response.spellingSuggestion.query == 'sony:relevance')
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsSpellingXML() {
		def con = testUtil.getConnection("/products?query=somy", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.spellingSuggestion
		assert response.spellingSuggestion.suggestion == 'sony'
		assert (response.spellingSuggestion.query == 'sony:topRated') || (response.spellingSuggestion.query == 'sony:relevance')
	}

	@Test
	void testSearchProductsAutoSuggestXML() {
		def con = testUtil.getConnection("/products/suggest?term=ta", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'suggestions'
		assert response.suggestion
		assert response.suggestion.size() == 2
		assert response.suggestion[0] == 'tape'
		assert response.suggestion[1] == 'targus'
	}

	@Test
	void testSearchProductsAutoSuggestWithLimitXML() {
		def con = testUtil.getConnection("/products/suggest?term=ta&max=1", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)

		assert response.suggestion.size() == 1
		assert response.suggestion[0] == 'tape'
	}

	@Test
	void testSearchProductsAutoSuggestJSON() {
		def con = testUtil.getConnection("/products/suggest?term=ta", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response
		assert response.suggestions.size() == 2
		assert response.suggestions[0].value == 'tape'
		assert response.suggestions[1].value == 'targus'
	}

	@Test
	void testSearchProductsAutoSuggestWithLmitJSON() {

		def con = testUtil.getConnection("/products/suggest?term=ta&max=1", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)

		assert response
		assert response.suggestions.size() == 1
		assert response.suggestions[0].value == 'tape'
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsPaginationXML() {
		def con = testUtil.getConnection("/products", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.pagination
		assert response.pagination.pageSize == 20
		assert response.pagination.currentPage == 0
		assert response.pagination.totalResults == NUMBER_OF_ALL_PRODUCTS
		assert response.pagination.totalPages == 2


		assert Math.ceil(response.pagination.totalResults.toBigInteger() / response.pagination.pageSize.toBigInteger()) == response.pagination.totalPages.toBigInteger()

		(0..(response.pagination.totalPages.toInteger())).each { pageNumber ->
			//step through each page
			con = testUtil.getConnection("/products?currentPage=${pageNumber}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
			response = testUtil.verifiedJSONSlurper(con, false, false)
			assert response.pagination.currentPage == pageNumber
		}
	}


	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsPaginationJSON() {
		def con = testUtil.getConnection("/products", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.pagination
		assert response.pagination.pageSize == 20
		assert response.pagination.currentPage == 0
		assert response.pagination.totalResults == NUMBER_OF_ALL_PRODUCTS
		assert response.pagination.totalPages == 2


		assert Math.ceil(response.pagination.totalResults / response.pagination.pageSize) == response.pagination.totalPages

		(0..(response.pagination.totalPages)).each { pageNumber ->
			//step through each page
			con = testUtil.getConnection("/products?currentPage=${pageNumber}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
			response = testUtil.verifiedJSONSlurper(con, false, false)
			assert response.pagination.currentPage == pageNumber
		}
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsSortXML() {
		def con = testUtil.getConnection("/products?query=camera&sort=topRated", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.sorts
		assert response.sorts.sort.find { it.code == 'topRated' }.selected == true: 'topRated is not default selected'

		response.sorts.sort.each { sort ->
			con = testUtil.getConnection("/products?query=camera:${sort.code}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
			response = testUtil.verifiedXMLSlurper(con, false, false)
			assert response.sorts.sort.find { it.code == sort.code }.selected == true: "Expected ${sort.code} to be selected"
		}
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsSortJSON() {
		def con = testUtil.getConnection("/products?query=camera&sort=topRated", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.sorts
		assert response.sorts.find { it.code == 'topRated' }.selected == true: 'topRated is not default selected'

		response.sorts.each { sort ->
			con = testUtil.getConnection("/products?query=camera:${sort.code}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
			response = testUtil.verifiedJSONSlurper(con, false, false)
			assert response.sorts.find { it.code == sort.code }.selected == true: "Expected ${sort.code} to be selected"
		}
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testProductDetailsXML() {
		def con = testUtil.getConnection("/products?query=camera", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.products.product.size() == 20
		response.products.product.each { product ->
			//details request for each
			con = testUtil.getConnection("/products/${product.code}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
			response = testUtil.verifiedXMLSlurper(con, false, false)
			assert response.name() == 'product'
			assert response.code == product.code
			assert response.name
			assert response.url
			assert response.averageRating
		}
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testProductDetailsJSON() {
		def con = testUtil.getConnection("/products?query=camera", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.products.size() == 20
		response.products.each { product ->
			//details request for each
			con = testUtil.getConnection("/products/${product.code}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
			response = testUtil.verifiedJSONSlurper(con, false, false)
			assert response.code == product.code
			assert response.name
			assert response.url
		}
	}

	@Test
	void testProductDetailsSpecificXML() {
		def con = testUtil.getConnection("/products/872912a", 'GET', 'XML', HttpURLConnection.HTTP_OK) //some SD CARD
		def response = testUtil.verifiedXMLSlurper(con, false, false)

		assert response.name() == 'product'
		assert response.code == '872912a'
		assert response.name == 'Secure Digital Card 2GB'
		assert response.purchasable
		//	assert response.averageRating == 3.25
		assert response.manufacturer == 'ICIDU'
		assert response.images.image.size() == 4
	}

	@Test
	void testProductDetailsSpecificSJON() {
		def con = testUtil.getConnection("/products/872912a", 'GET', 'JSON', HttpURLConnection.HTTP_OK) //some SD CARD
		def response = testUtil.verifiedJSONSlurper(con, false, false)

		assert response.code == '872912a'
		assert response.name == 'Secure Digital Card 2GB'
		assert response.purchasable
		//	assert response.averageRating == 3.25
		assert response.manufacturer == 'ICIDU'
		assert response.images.size() == 4

	}


	@Test
	void testProductExtendedDetailsSpecificSJON() {
		def con = testUtil.getConnection("/products/872912a?options=BASIC,DESCRIPTION,GALLERY,CATEGORIES,PROMOTIONS,STOCK,REVIEW,CLASSIFICATION,REFERENCES,PRICE", 'GET', 'JSON', HttpURLConnection.HTTP_OK) //some SD CARD
		def response = testUtil.verifiedJSONSlurper(con, false, false)

		assert response.code == '872912a'
		assert response.name == 'Secure Digital Card 2GB'
		assert response.purchasable
		assert response.manufacturer == 'ICIDU'
		assert response.images.size() == 13

		assert response.categories.size() == 2
		response.categories.each { category ->
			assert (category.code == '902') || (category.code == 'brand_2171')
		}

		assert response.classifications.size() == 6
		def classification = response.classifications[0]
		assert classification.name == 'Technical details'
		assert classification.features.size == 1
		assert classification.code == '834'

		def feature = classification.features[0]
		assert feature.name == 'Source data-sheet'
		assert feature.comparable
		assert feature.code == 'wsTestClassification/1.0/834.source data-sheet, 6617'
		assert feature.featureUnit.unitType == '300'
		assert feature.featureUnit.symbol == '.'
		assert feature.featureUnit.name == '.'

		assert feature.featureValues.size() == 1
		assert feature.featureValues[0].value == 'ICEcat.biz'

		assert response.purchasable
		assert response.stock.stockLevelStatus.code == 'inStock'
		assert response.stock.stockLevelStatus.codeLowerCase == 'instock'
		assert response.stock.stockLevel == 11

		assert response.description == 'Create it… Store it… Share it, with an ICIDU SD Card. Save image, sound and data files on compatible devices such as digital cameras, camcorders and MP3-players.'
		assert response.name == 'Secure Digital Card 2GB'
		assert response.url == '/wsTest/products/872912a'

		assert response.price.currencyIso == 'USD'
		assert response.price.priceType == 'BUY'
		assert response.price.value == 10.0
		assert response.price.formattedValue == '$10.00'

		assert response.numberOfReviews == 0
		assert response.manufacturer == 'ICIDU'

		def image = response.images[0]
		assert image.imageType == 'PRIMARY'
		assert image.format == 'zoom'
		assert image.altText == 'Secure Digital Card 2GB'
	}

	@Test
	void testExportProductsFullXML() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def con = testUtil.getSecureConnection("/products/export/full", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'export'
		assert response.products.product.size() == NUMBER_OF_ALL_PRODUCTS
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 1
		assert response.currentPage == 0

		//change pageSize to 20
		con = testUtil.getSecureConnection("/products/export/full?pageSize=20", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'export'
		assert response.products.product.size() == 20
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 2
		assert response.currentPage == 0
	}

	@Test
	void testExportProductsFullJSON() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def con = testUtil.getSecureConnection("/products/export/full", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedJSONSlurper(con, false, false)

		assert response.products.size() == NUMBER_OF_ALL_PRODUCTS
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 1
		assert response.currentPage == 0

		//change pageSize to 20
		con = testUtil.getSecureConnection("/products/export/full?pageSize=20", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.products.size() == 20
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 2
		assert response.currentPage == 0
	}

	@Test
	void testExportProductsIncrementalXML() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def con = testUtil.getSecureConnection("/products/export/incremental", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, null, null, trustedClientCredentialsToken)
		//2007-08-31T16:47+00:00
		con = testUtil.getSecureConnection("/products/export/incremental?timestamp=2012-03-28T07:50:49%2B00:00", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'export'
		assert response.products.product.size() == NUMBER_OF_ALL_PRODUCTS
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 1
		assert response.currentPage == 0

		con = testUtil.getSecureConnection("/products/export/incremental?timestamp=2113-06-28T07:50:49%2B00:00", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'export'
		assert response.products.product.size() == 0
		assert response.totalProductCount == 0
		assert response.totalPageCount == 0
		assert response.currentPage == 0
	}

	@Test
	void testExportProductsIncrementalJSON() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def con = testUtil.getSecureConnection("/products/export/incremental", 'GET', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, null, trustedClientCredentialsToken)
		//2007-08-31T16:47+00:00
		con = testUtil.getSecureConnection("/products/export/incremental?timestamp=2012-03-28T07:50:49%2B00:00", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.products.size() == NUMBER_OF_ALL_PRODUCTS
		assert response.totalProductCount == NUMBER_OF_ALL_PRODUCTS
		assert response.totalPageCount == 1
		assert response.currentPage == 0

		con = testUtil.getSecureConnection("/products/export/incremental?timestamp=2113-06-28T07:50:49%2B00:00", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.products.size() == 0
		assert response.totalProductCount == 0
		assert response.totalPageCount == 0
		assert response.currentPage == 0
	}

	@Test
	void testProductsExportReferencesJSON() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def code = '2053226';  // electronicsstore extension, products-relations.impex, INSERT_UPDATE ProductReference
		def con = testUtil.getSecureConnection("/products/export/references/${code}?referenceType=SIMILAR", "GET", "JSON", HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedJSONSlurper(con)
		assert response != null
	}

	@Test
	void testProductsExportReferencesXML() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def code = '2053226'; // electronicsstore extension, products-relations.impex, INSERT_UPDATE ProductReference
		def con = testUtil.getSecureConnection("/products/export/references/${code}?referenceType=SIMILAR", "GET", "XML", HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedXMLSlurper(con)
		assert response != null
	}

	@Test
	void testGetProductByCodeWithReferencesXML() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def code = '2053226'; // electronicsstore extension, products-relations.impex, INSERT_UPDATE ProductReference
		def con = testUtil.getConnection("/products/${code}?options=REFERENCES", "GET", "XML", HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedXMLSlurper(con)
		assert response != null
		assert response.productReferences != null
		assert response.productReferences.reference.size() > 0
	}

	@Test
	void testCreateReview() {
		def postBody = "alias=krzys&rating=4&comment=perfect&headline=samplereview"
		def code = '816780';
		def con = testUtil.getConnection("/products/${code}/reviews", "POST", "JSON", HttpURLConnection.HTTP_OK, postBody);
		def response = testUtil.verifiedJSONSlurper(con)
		assert response != null
		assert response.alias == "krzys"
		assert response.rating == 4.0
		assert response.comment == "perfect"
		assert response.headline == "samplereview"
		assert response.principal.uid == "anonymous"
	}

	@Test
	void testCreateReviewXML() {
		def postBody = "alias=krzys&rating=4&comment=perfect&headline=samplereview"
		def code = '816780';
		def con = testUtil.getConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_OK, postBody);
		def response = testUtil.verifiedXMLSlurper(con)
		assert response != null
		assert response.alias == "krzys"
		assert response.rating == "4.0"
		assert response.comment == "perfect"
		assert response.headline == "samplereview"
		assert response.principal.uid == "anonymous"
	}

	@Test
	void testCreateReviewWithoutMandatoryFields() {
		def postBody = ""
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, null);
		def response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.error[0].type == 'ValidationError'
		assert response.error[0].subjectType == 'parameter'
		assert response.error[0].subject == 'headline'
		assert response.error[0].reason == 'missing'
		assert response.error[0].message == 'This field is required.'

		assert response.error[1].type == 'ValidationError'
		assert response.error[1].subjectType == 'parameter'
		assert response.error[1].subject == 'comment'
		assert response.error[1].reason == 'missing'
		assert response.error[1].message == 'This field is required.'

		assert response.error[2].type == 'ValidationError'
		assert response.error[2].subjectType == 'parameter'
		assert response.error[2].subject == 'rating'
		assert response.error[2].reason == 'missing'
		assert response.error[2].message == 'This field is required.'
	}

	@Test
	void testCreateReviewWithRatingOverMaxValue() {
		def postBody = "alias=krzys&rating=5.1&comment=perfect&headline=samplereview"
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, null);
		def response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.error[0].type == 'ValidationError'
		assert response.error[0].subjectType == 'parameter'
		assert response.error[0].subject == 'rating'
		assert response.error[0].reason == 'invalid'
		assert response.error[0].message == 'Value should be between 1 and 5'
	}

	@Test
	void testCreateReviewWithRatingUnderMinValue() {
		def postBody = "alias=krzys&rating=0.9&comment=perfect&headline=samplereview"
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, null);
		def response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.error[0].type == 'ValidationError'
		assert response.error[0].subjectType == 'parameter'
		assert response.error[0].subject == 'rating'
		assert response.error[0].reason == 'invalid'
		assert response.error[0].message == 'Value should be between 1 and 5'
	}

	@Test
	void testCreateReviewWithoutMandatoryFieldsForGermanLang() {
		def postBody = "lang=de"
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, null);
		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error);
		assert response.error[0].type == 'ValidationError'
		assert response.error[0].subjectType == 'parameter'
		assert response.error[0].subject == 'headline'
		assert response.error[0].reason == 'missing'
		assert response.error[0].message == 'Dieses Feld ist erforderlich.'

		assert response.error[1].type == 'ValidationError'
		assert response.error[1].subjectType == 'parameter'
		assert response.error[1].subject == 'comment'
		assert response.error[1].reason == 'missing'
		assert response.error[1].message == 'Dieses Feld ist erforderlich.'

		assert response.error[2].type == 'ValidationError'
		assert response.error[2].subjectType == 'parameter'
		assert response.error[2].subject == 'rating'
		assert response.error[2].reason == 'missing'
		assert response.error[2].message == 'Dieses Feld ist erforderlich.'
	}

	@Test
	void testSearchProductStockByLocation() {
		def urlParams = '?location=wsTestLocation&pageSize=5&currentPage=0';
		def code = '872912a';
		def con = testUtil.getConnection("/products/${code}/nearLocation${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.product.code == code;
		assert response.product.name == 'Secure Digital Card 2GB';
		assert response.stores.size() > 0
	}

	@Test
	void testSearchProductStockByLocationXML() {
		def urlParams = '?location=wsTestLocation&pageSize=5&currentPage=0';
		def code = '872912a';
		def con = testUtil.getConnection("/products/${code}/nearLocation${urlParams}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.product.code == code;
		assert response.product.name == 'Secure Digital Card 2GB';
		assert response.stores.children().size() > 0
	}

	@Test
	void testSearchProductStockByLocationPagination() {
		def urlParams = '?location=wsTestLocation&pageSize=5&currentPage=1';
		def code = '872912a';
		def con = testUtil.getConnection("/products/${code}/nearLocation${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.pagination.pageSize == 5;
		assert response.pagination.currentPage == 1;
	}

	@Test
	void testSearchProductStockByLocationDistanceAscSort() {
		def urlParams = '?location=wsTestLocation&pageSize=5&currentPage=1';
		def code = '872912a';
		def con = testUtil.getConnection("/products/${code}/nearLocation${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)

		for (int i = 1; i < response.stores.size(); i++) {
			assert Double.parseDouble(response.stores[i - 1].formattedDistance.replace(" km", "")) <= Double.parseDouble(response.stores[i].formattedDistance.replace(" km", ""));
		}
	}

	@Test
	void testSearchProductStockByLocationGeoCode() {
		def urlParams = '?latitude=35.6816951&longitude=139.7650482&pageSize=5&currentPage=0';
		def code = '872912a';
		def con = testUtil.getConnection("/products/${code}/nearLatLong${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.product.code == code;
		assert response.product.name == 'Secure Digital Card 2GB';
		assert response.stores.size() > 0
	}

	@Test
	void testSearchProductStockByLocationGeoCodeXML() {
		def urlParams = '?latitude=35.6816951&longitude=139.7650482&pageSize=5&currentPage=0';
		def code = '872912a';
		def con = testUtil.getConnection("/products/${code}/nearLatLong${urlParams}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedXMLSlurper(con)
		assert response.product.code == code;
		assert response.product.name == 'Secure Digital Card 2GB';
		assert response.stores.size() > 0
	}

	@Test
	void testSearchProductStockByLocationGeoCodePagination() {
		def urlParams = '?latitude=35.6816951&longitude=139.7650482&pageSize=5&currentPage=1';
		def code = '872912a';
		def con = testUtil.getConnection("/products/${code}/nearLatLong${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.pagination.pageSize == 5;
		assert response.pagination.currentPage == 1;
	}

	@Test
	void testSearchProductStockByLocationGeoCodeDistanceAscSort() {
		def urlParams = '?latitude=35.6816951&longitude=139.7650482&pageSize=5&currentPage=0';
		def code = '872912a';
		def con = testUtil.getConnection("/products/${code}/nearLatLong${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)

		for (int i = 1; i < response.stores.size(); i++) {
			assert Double.parseDouble(response.stores[i - 1].formattedDistance.replace(" km", "")) <= Double.parseDouble(response.stores[i].formattedDistance.replace(" km", ""));
		}
	}

	@Test
	void testGetStockLevelForStore() {
		def access_token = testUtil.getClientCredentialsToken()
		def code = 2006139
		def con = testUtil.getConnection("/products/${code}/stock?storeName=${STORE_NAME}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stockLevelStatus.code == "inStock"
		assert response.stockLevelStatus.codeLowerCase == "instock"
		assert response.stockLevel == 10
	}

	@Test
	void testFailGetStockLevelWhenWrongStoreName() {
		def access_token = testUtil.getClientCredentialsToken()
		def code = 2006139
		def con = testUtil.getConnection("/products/${code}/stock?storeName=WrongStoreName", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST)
		def error =con.errorStream.text;
		def response = new XmlSlurper().parseText(error)
		assert response.error.type == 'ValidationError'
		assert response.error.subjectType == 'parameter'
		assert response.error.subject == 'storeName'
		assert response.error.reason == 'invalid'
		assert response.error.message == "Store with given name doesn't exist"
	}
}