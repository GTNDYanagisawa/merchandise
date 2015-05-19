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
		def con = testUtil.getSecureConnection("/products/search?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.products.size() > 0
		assert response.sorts.size() > 0
		assert response.pagination
		assert response.currentQuery
		assert response.facets.size() > 0
	}

	@Test
	void testSearchProductsBasicXML() {
		def con = testUtil.getSecureConnection("/products/search?fields=${FULL_SET}", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'productCategorySearchPage'
		assert response.products.size() > 0
		assert response.sorts.size() > 0
		assert response.pagination
		assert response.currentQuery
		assert response.facets.size() > 0
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsSpellingJSON() {
		def con = testUtil.getSecureConnection("/products/search?query=somy", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.spellingSuggestion
		assert response.spellingSuggestion.suggestion == 'sony'
		assert (response.spellingSuggestion.query == 'sony:topRated') || (response.spellingSuggestion.query == 'sony:relevance')
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsSpellingXML() {
		def con = testUtil.getSecureConnection("/products/search?query=somy", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.spellingSuggestion
		assert response.spellingSuggestion.suggestion == 'sony'
		assert (response.spellingSuggestion.query == 'sony:topRated') || (response.spellingSuggestion.query == 'sony:relevance')
	}

	@Test
	void testSearchProductsAutoSuggestXML() {
		def con = testUtil.getSecureConnection("/products/suggestions?term=ta", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.name() == 'suggestionList'
		assert response.suggestions
		assert response.suggestions.size() == 2
		assert response.suggestions[0] == 'tape'
		assert response.suggestions[1] == 'targus'
	}

	@Test
	void testSearchProductsAutoSuggestWithLimitXML() {
		def con = testUtil.getSecureConnection("/products/suggestions?term=ta&max=1", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)

		assert response.suggestions.size() == 1
		assert response.suggestions[0] == 'tape'
	}

	@Test
	void testSearchProductsAutoSuggestJSON() {
		def con = testUtil.getSecureConnection("/products/suggestions?term=ta", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response
		assert response.suggestions
		assert response.suggestions.size() == 2
		assert response.suggestions[0].value == 'tape'
		assert response.suggestions[1].value == 'targus'
	}

	@Test
	void testSearchProductsAutoSuggestWithLmitJSON() {

		def con = testUtil.getSecureConnection("/products/suggestions?term=ta&max=1", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)

		assert response
		assert response.suggestions
		assert response.suggestions.size() == 1
		assert response.suggestions[0].value == 'tape'
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsPaginationXML() {
		def con = testUtil.getSecureConnection("/products/search", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.pagination
		assert response.pagination.pageSize == 20
		assert response.pagination.currentPage == 0
		assert response.pagination.totalResults == NUMBER_OF_ALL_PRODUCTS
		assert response.pagination.totalPages == 2


		assert Math.ceil(response.pagination.totalResults.toBigInteger() / response.pagination.pageSize.toBigInteger()) == response.pagination.totalPages.toBigInteger()

		(0..(response.pagination.totalPages.toInteger())).each { pageNumber ->
			//step through each page
			con = testUtil.getSecureConnection("/products/search?currentPage=${pageNumber}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
			response = testUtil.verifiedJSONSlurper(con, false, false)
			assert response.pagination.currentPage == pageNumber
		}
	}


	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsPaginationJSON() {
		def con = testUtil.getSecureConnection("/products/search", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.pagination
		assert response.pagination.pageSize == 20
		assert response.pagination.currentPage == 0
		assert response.pagination.totalResults == NUMBER_OF_ALL_PRODUCTS
		assert response.pagination.totalPages == 2


		assert Math.ceil(response.pagination.totalResults / response.pagination.pageSize) == response.pagination.totalPages

		(0..(response.pagination.totalPages)).each { pageNumber ->
			//step through each page
			con = testUtil.getSecureConnection("/products/search?currentPage=${pageNumber}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
			response = testUtil.verifiedJSONSlurper(con, false, false)
			assert response.pagination.currentPage == pageNumber
		}
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsSortXML() {
		def con = testUtil.getSecureConnection("/products/search?query=camera&sort=topRated", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.sorts
		assert response.sorts.find { it.code == 'topRated' }.selected == true: 'topRated is not default selected'

		response.sorts.each { sort ->
			con = testUtil.getSecureConnection("/products/search?query=camera:${sort.code}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
			response = testUtil.verifiedXMLSlurper(con, false, false)
			assert response.sorts.find { it.code == sort.code }.selected == true: "Expected ${sort.code} to be selected"
		}
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testSearchProductsSortJSON() {
		def con = testUtil.getSecureConnection("/products/search?query=camera&sort=topRated", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.sorts
		assert response.sorts.find { it.code == 'topRated' }.selected == true: 'topRated is not default selected'

		response.sorts.each { sort ->
			con = testUtil.getSecureConnection("/products/search?query=camera:${sort.code}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
			response = testUtil.verifiedJSONSlurper(con, false, false)
			assert response.sorts.find { it.code == sort.code }.selected == true: "Expected ${sort.code} to be selected"
		}
	}

	@Test
	@Category(AvoidCollectingOutputFromTest.class)
	void testProductDetailsXML() {
		def con = testUtil.getSecureConnection("/products/search?query=camera", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.products.size() == 20
		response.products.each { product ->
			//details request for each
			con = testUtil.getSecureConnection("/products/${product.code}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
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
		def con = testUtil.getSecureConnection("/products/search?query=camera", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.products.size() == 20
		response.products.each { product ->
			//details request for each
			con = testUtil.getSecureConnection("/products/${product.code}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
			response = testUtil.verifiedJSONSlurper(con, false, false)
			assert response.code == product.code
			assert response.name
			assert response.url
		}
	}

	@Test
	void testProductDetailsSpecificXML() {
		def con = testUtil.getSecureConnection("/products/872912a?fields=name,code,purchasable,manufacturer,images", 'GET', 'XML', HttpURLConnection.HTTP_OK) //some SD CARD
		def response = testUtil.verifiedXMLSlurper(con, true, false)

		assert response.name() == 'product'
		assert response.code == '872912a'
		assert response.name == 'Secure Digital Card 2GB'
		assert response.purchasable
		//	assert response.averageRating == 3.25
		assert response.manufacturer == 'ICIDU'
		assert response.images.size() == 13
	}

	@Test
	void testProductDetailsSpecificJSON() {
		def con = testUtil.getSecureConnection("/products/872912a?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK) //some SD CARD
		def response = testUtil.verifiedJSONSlurper(con, false, false)

		assert response.code == '872912a'
		assert response.name == 'Secure Digital Card 2GB'
		assert response.purchasable
		//	assert response.averageRating == 3.25
		assert response.manufacturer == 'ICIDU'
		assert response.images.size() == 13

	}


	@Test
	void testProductExtendedDetailsSpecificJSON() {
		def con = testUtil.getSecureConnection("/products/872912a?fields=${FULL_SET}", 'GET', 'JSON', HttpURLConnection.HTTP_OK) //some SD CARD
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
		assert response.stock.stockLevelStatus == 'inStock'
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
	void testProductsExportReferencesJSON() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def code = '2053226';  // electronicsstore extension, products-relations.impex, INSERT_UPDATE ProductReference
		def con = testUtil.getSecureConnection("/products/${code}/references?referenceType=SIMILAR", "GET", "JSON", HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedJSONSlurper(con)
		assert response != null
	}

	@Test
	void testProductsExportReferencesXML() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def code = '2053226'; // electronicsstore extension, products-relations.impex, INSERT_UPDATE ProductReference
		def con = testUtil.getSecureConnection("/products/${code}/references?referenceType=SIMILAR", "GET", "XML", HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedXMLSlurper(con)
		assert response != null
	}

	@Test
	void testGetProductByCodeWithReferencesXML() {
		def trustedClientCredentialsToken = testUtil.getTrustedClientCredentialsToken();
		def code = '2053226'; // electronicsstore extension, products-relations.impex, INSERT_UPDATE ProductReference
		def con = testUtil.getSecureConnection("/products/${code}?fields=${FULL_SET}", "GET", "XML", HttpURLConnection.HTTP_OK, null, null, trustedClientCredentialsToken)
		def response = testUtil.verifiedXMLSlurper(con, true, false)
		assert response != null
		assert response.productReferences != null
		assert response.productReferences.size() > 0
	}

	@Test
	void testGetReviews() {
		def code = '280916';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "GET", "JSON", HttpURLConnection.HTTP_OK, null);
		def response = testUtil.verifiedJSONSlurper(con)
		assert response.reviews.size() == 2

		def review1 = response.reviews.find {it['headline'] == 'A good solid product, worthy of a purchase.'}
		assert review1
		assert review1.rating == 4.0
		def review2 = response.reviews.find {it['headline'] == 'This is a fantastic product, did everything I wanted it to do.'}
		assert review2
		assert review2.rating == 5.0
	}

	@Test
	void testGetReviewsWithMaxCount() {
		def code = '280916';
		def con = testUtil.getSecureConnection("/products/${code}/reviews?maxCount=1", "GET", "JSON", HttpURLConnection.HTTP_OK, null);
		def response = testUtil.verifiedJSONSlurper(con)
		assert response.reviews.size() == 1
		def review = response.reviews[0]

		if (review.headline == 'A good solid product, worthy of a purchase.') {
			assert review.rating == 4.0
		}
		else if (review.headline == 'This is a fantastic product, did everything I wanted it to do.') {
			assert review.rating == 5.0
		}
		else {
			assert false
		}
	}


	@Test
	void testCreateReview() {
		def postBody = "alias=krzys&rating=4&comment=perfect&headline=samplereview"
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "JSON", HttpURLConnection.HTTP_CREATED, postBody);
		def response = testUtil.verifiedJSONSlurper(con)
		assert response != null
		assert response.alias == "krzys"
		assert response.rating == 4.0
		assert response.comment == "perfect"
		assert response.headline == "samplereview"
		assert response.principal.uid == "anonymous"
	}

	@Test
	void testCreateReviewWithDTO() {
		def postBody = "{\"alias\" : \"krzys\",\"comment\" : \"perfect\",\"date\" : \"2014-07-01T13:11:58+0200\",\"headline\" : \"samplereview\",\"principal\" : {\"name\" : \"Anonymous\",\"uid\" : \"anonymous\"},\"rating\" : 4.0}";
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "JSON", HttpURLConnection.HTTP_CREATED, postBody,null,null,"application/json");
		def response = testUtil.verifiedJSONSlurper(con,true)
		assert response != null
		assert response.alias == "krzys"
		assert response.rating == 4.0
		assert response.comment == "perfect"
		assert response.headline == "samplereview"
		assert response.principal.uid == "anonymous"
	}

	@Test
	void testCreateReviewWithDTOWithoutMandatoryFields() {
		def postBody = "{}";
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_BAD_REQUEST, postBody,null,null,"application/json");
		def response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].subject == 'headline'
		assert response.errors[0].reason == 'missing'
		assert response.errors[0].message == 'This field is required.'

		assert response.errors[1].type == 'ValidationError'
		assert response.errors[1].subjectType == 'parameter'
		assert response.errors[1].subject == 'comment'
		assert response.errors[1].reason == 'missing'
		assert response.errors[1].message == 'This field is required.'

		assert response.errors[2].type == 'ValidationError'
		assert response.errors[2].subjectType == 'parameter'
		assert response.errors[2].subject == 'rating'
		assert response.errors[2].reason == 'missing'
		assert response.errors[2].message == 'This field is required.'
	}

	@Test
	void testCreateReviewWithDTOAndFields() {
		def postBody = "{\"alias\" : \"krzys\",\"comment\" : \"perfect\",\"date\" : \"2014-07-01T13:11:58+0200\",\"headline\" : \"samplereview\",\"principal\" : {\"name\" : \"Anonymous\",\"uid\" : \"anonymous\"},\"rating\" : 4.0}";
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews?fields=alias", "POST", "JSON", HttpURLConnection.HTTP_CREATED, postBody,null,null,"application/json");
		def response = testUtil.verifiedJSONSlurper(con,true)
		assert response != null
		assert response.alias == "krzys"
		assert !response.rating
		assert !response.comment
		assert !response.headline
		assert !response.principal
	}

	@Test
	void testCreateReviewXML() {
		def postBody = "alias=krzys&rating=4&comment=perfect&headline=samplereview"
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_CREATED, postBody);
		def response = testUtil.verifiedXMLSlurper(con,true)
		assert response != null
		assert response.alias == "krzys"
		assert response.rating == "4.0"
		assert response.comment == "perfect"
		assert response.headline == "samplereview"
		assert response.principal.uid == "anonymous"
	}

	@Test
	void testCreateReviewWithDTOXML() {
		def postBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><review><alias>krzys</alias><comment>perfect</comment><date>2014-07-01T13:41:22+0200</date><headline>samplereview</headline><principal><name>Anonymous</name><uid>anonymous</uid></principal><rating>4.0</rating></review>"
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_CREATED, postBody,null,null,"application/xml");
		def response = testUtil.verifiedXMLSlurper(con)
		assert response != null
		assert response.alias == "krzys"
		assert response.rating == "4.0"
		assert response.comment == "perfect"
		assert response.headline == "samplereview"
		assert response.principal.uid == "anonymous"
	}

	@Test
	void testCreateReviewWithDTOXMLAndFields() {
		def postBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><review><alias>krzys</alias><comment>perfect</comment><date>2014-07-01T13:41:22+0200</date><headline>samplereview</headline><principal><name>Anonymous</name><uid>anonymous</uid></principal><rating>4.0</rating></review>"
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews?fields=alias,rating", "POST", "XML", HttpURLConnection.HTTP_CREATED, postBody,null,null,"application/xml");
		def response = testUtil.verifiedXMLSlurper(con)

		assert response != null
		assert response.alias == "krzys"
		assert response.rating == "4.0"
		assert response.comment.size() == 0
		assert response.headline.size() == 0
		assert response.principal.size() == 0
	}

	@Test
	void testCreateReviewWithoutMandatoryFields() {
		def postBody = ""
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, null);
		def response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].subject == 'headline'
		assert response.errors[0].reason == 'missing'
		assert response.errors[0].message == 'This field is required.'

		assert response.errors[1].type == 'ValidationError'
		assert response.errors[1].subjectType == 'parameter'
		assert response.errors[1].subject == 'comment'
		assert response.errors[1].reason == 'missing'
		assert response.errors[1].message == 'This field is required.'

		assert response.errors[2].type == 'ValidationError'
		assert response.errors[2].subjectType == 'parameter'
		assert response.errors[2].subject == 'rating'
		assert response.errors[2].reason == 'missing'
		assert response.errors[2].message == 'This field is required.'
	}

	@Test
	void testCreateReviewWithRatingOverMaxValue() {
		def postBody = "alias=krzys&rating=5.1&comment=perfect&headline=samplereview"
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, null);
		def response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].subject == 'rating'
		assert response.errors[0].reason == 'invalid'
		assert response.errors[0].message == 'Value should be between 1 and 5'
	}

	@Test
	void testCreateReviewWithRatingUnderMinValue() {
		def postBody = "alias=krzys&rating=0.9&comment=perfect&headline=samplereview"
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, null);
		def response = new XmlSlurper().parseText(con.errorStream.text)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].subject == 'rating'
		assert response.errors[0].reason == 'invalid'
		assert response.errors[0].message == 'Value should be between 1 and 5'
	}

	@Test
	void testCreateReviewWithoutMandatoryFieldsForGermanLang() {
		def postBody = "lang=de"
		def code = '816780';
		def con = testUtil.getSecureConnection("/products/${code}/reviews", "POST", "XML", HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, null);
		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error);
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].subject == 'headline'
		assert response.errors[0].reason == 'missing'
		assert response.errors[0].message == 'Dieses Feld ist erforderlich.'

		assert response.errors[1].type == 'ValidationError'
		assert response.errors[1].subjectType == 'parameter'
		assert response.errors[1].subject == 'comment'
		assert response.errors[1].reason == 'missing'
		assert response.errors[1].message == 'Dieses Feld ist erforderlich.'

		assert response.errors[2].type == 'ValidationError'
		assert response.errors[2].subjectType == 'parameter'
		assert response.errors[2].subject == 'rating'
		assert response.errors[2].reason == 'missing'
		assert response.errors[2].message == 'Dieses Feld ist erforderlich.'
	}

	@Test
	void testSearchProductStockByLocation() {
		def urlParams = '?location=wsTestLocation&pageSize=5&currentPage=0';
		def code = '872912a';
		def con = testUtil.getSecureConnection("/products/${code}/stock${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.product.code == code;
		assert response.product.name == 'Secure Digital Card 2GB';
		assert response.stores.size() > 0
		assert response.stores[0].stockInfo != null && response.stores[0].stockInfo.size() > 0
	}

	@Test
	void testSearchProductStockByLocationXML() {
		def urlParams = '?location=wsTestLocation&pageSize=5&currentPage=0';
		def code = '872912a';
		def con = testUtil.getSecureConnection("/products/${code}/stock${urlParams}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedXMLSlurper(con, false, false)
		assert response.product.code == code;
		assert response.product.name == 'Secure Digital Card 2GB';
		assert response.stores.children().size() > 0
		assert response.stores[0].stockInfo != null && response.stores[0].stockInfo.size() > 0
	}

	@Test
	void testSearchProductStockByLocationPagination() {
		def urlParams = '?location=wsTestLocation&pageSize=5&currentPage=1';
		def code = '872912a';
		def con = testUtil.getSecureConnection("/products/${code}/stock${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.pagination.pageSize == 5;
		assert response.pagination.currentPage == 1;
	}

	@Test
	void testSearchProductStockByLocationDistance() {
		def urlParams = "?location=wsTestLocation&pageSize=5&currentPage=1&fields=${FULL_SET}";
		def code = '872912a';
		def con = testUtil.getSecureConnection("/products/${code}/stock${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)

		for (int i = 1; i < response.stores.size(); i++) {
			assert Double.parseDouble(response.stores[i - 1].formattedDistance.replace(" km", "")) <= Double.parseDouble(response.stores[i].formattedDistance.replace(" km", ""));
		}
	}

	@Test
	void testSearchProductStockByLocationGeoCode() {
		def urlParams = '?latitude=35.6816951&longitude=139.7650482&pageSize=5&currentPage=0';
		def code = '872912a';
		def con = testUtil.getSecureConnection("/products/${code}/stock${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.product.code == code;
		assert response.product.name == 'Secure Digital Card 2GB';
		assert response.stores.size() > 0
		assert response.stores[0].stockInfo != null && response.stores[0].stockInfo.size() > 0
	}

	@Test
	void testSearchProductStockByLocationGeoCodeXML() {
		def urlParams = '?latitude=35.6816951&longitude=139.7650482&pageSize=5&currentPage=0';
		def code = '872912a';
		def con = testUtil.getSecureConnection("/products/${code}/stock${urlParams}", 'GET', 'XML', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedXMLSlurper(con, true, false)
		assert response.product.code == code;
		assert response.product.name == 'Secure Digital Card 2GB';
		assert response.stores.size() > 0
		assert response.stores[0].stockInfo != null && response.stores[0].stockInfo.size() > 0
	}

	@Test
	void testSearchProductStockByLocationGeoCodePagination() {
		def urlParams = '?latitude=35.6816951&longitude=139.7650482&pageSize=5&currentPage=1';
		def code = '872912a';
		def con = testUtil.getSecureConnection("/products/${code}/stock${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.pagination.pageSize == 5;
		assert response.pagination.currentPage == 1;
	}

	@Test
	void testSearchProductStockByLocationGeoCodeDistanceAscSort() {
		def urlParams = "?latitude=35.6816951&longitude=139.7650482&pageSize=5&currentPage=0&fields=${FULL_SET}";
		def code = '872912a';
		def con = testUtil.getSecureConnection("/products/${code}/stock${urlParams}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)

		for (int i = 1; i < response.stores.size(); i++) {
			assert Double.parseDouble(response.stores[i - 1].formattedDistance.replace(" km", "")) <= Double.parseDouble(response.stores[i].formattedDistance.replace(" km", ""));
		}
	}

	@Test
	void testGetStockLevelForStore() {
		def access_token = testUtil.getClientCredentialsToken()
		def code = 2006139
		def con = testUtil.getSecureConnection("/products/${code}/stock/${STORE_NAME}", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stockLevelStatus == "inStock"
		assert response.stockLevel == 10
	}

	@Test
	void testFailGetStockLevelWhenWrongStoreName() {
		def access_token = testUtil.getClientCredentialsToken()
		def code = 2006139
		def con = testUtil.getSecureConnection("/products/${code}/stock/WrongStoreName", 'GET', 'XML', HttpURLConnection.HTTP_BAD_REQUEST)
		def error = con.errorStream.text;
		def response = new XmlSlurper().parseText(error)
		assert response.errors[0].type == 'ValidationError'
		assert response.errors[0].subjectType == 'parameter'
		assert response.errors[0].subject == 'storeName'
		assert response.errors[0].reason == 'invalid'
		assert response.errors[0].message == "Store with given name doesn't exist"
	}
}