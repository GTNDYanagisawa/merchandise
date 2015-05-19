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
 *
 *  
 */
package de.hybris.platform.commercefacades.product;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Collection;
import java.util.List;


/**
 * Product facade interface. Its main purpose is to retrieve product related DTOs using existing services.
 */
public interface ProductFacade
{
	/**
	 * Gets the product data. The current session data (catalog versions, user) are used, so the valid product for the
	 * current session parameters will be returned. Use
	 * {@link ProductFacade#getProductForCodeAndOptions(String, Collection)} if you only have the code.
	 * 
	 * @param productModel
	 *           the productModel
	 * @param options
	 *           options set that determines amount of information that will be attached to the returned product. If
	 *           empty or null default BASIC option is assumed
	 * @return the {@link ProductData}
	 */
	ProductData getProductForOptions(ProductModel productModel, Collection<ProductOption> options);

	/**
	 * Gets the product by code. The current session data (catalog versions, user) are used, so the valid product for the
	 * current session parameters will be returned. Use
	 * {@link ProductFacade#getProductForOptions(ProductModel, Collection)} if you have the model already.
	 * 
	 * @param code
	 *           the code of the product to be found
	 * @param options
	 *           options set that determines amount of information that will be attached to the returned product. If
	 *           empty or null default BASIC option is assumed
	 * @return the {@link ProductData}
	 * @throws IllegalArgumentException
	 *            when given product code is null
	 * @throws UnknownIdentifierException
	 *            when product with the given code is not found
	 */
	ProductData getProductForCodeAndOptions(String code, Collection<ProductOption> options) throws UnknownIdentifierException,
			IllegalArgumentException;


	/**
	 * Post review for specified product code. Current session catalog version is used to retrieve proper product. New
	 * {@link CustomerReviewModel} entry will be created.
	 * 
	 * @param productCode
	 *           the product code that given review will be assigned to
	 * @param reviewData
	 *           the review data to be created in the system
	 * @return the created review converted into {@link ReviewData} object
	 * @throws IllegalArgumentException
	 *            when given review data is null
	 * @throws UnknownIdentifierException
	 *            when product with the given code is not found
	 */
	ReviewData postReview(String productCode, ReviewData reviewData) throws UnknownIdentifierException, IllegalArgumentException;

	/**
	 * Gets the reviews for specified product. Current session catalog version is used to retrieve reviews from proper
	 * product.
	 * 
	 * @param productCode
	 *           the product code
	 * @return the reviews that are assigned to specified product
	 * @throws UnknownIdentifierException
	 *            when product with the given code is not found
	 */
	List<ReviewData> getReviews(String productCode) throws UnknownIdentifierException;

	/**
	 * Gets the first X reviews for specified product. Current session catalog version is used to retrieve reviews from proper
	 * product.
	 *
	 * @param productCode
	 *           the product code
	 * @param numberOfReviews
	 *           the number of reviews to show, if null shows all reviews, if exceeds the total number of reviews,
	 *           shows all available reviews
	 * @return the first X reviews that are assigned to specified product
	 * @throws UnknownIdentifierException
	 *            when product with the given code is not found
	 * @throws IllegalArgumentException
	 *            when the quantity of numberOfReviews is negative
	 */
	List<ReviewData> getReviews(String productCode, Integer numberOfReviews) throws UnknownIdentifierException, IllegalArgumentException;

	/**
	 * @deprecated use getProductReferencesForCode(String code, List<ProductReferenceTypeEnum> referenceTypes,
	 *             List<ProductOption> options, Integer limit) instead.
	 */
	@Deprecated
	List<ProductReferenceData> getProductReferencesForCode(String code, ProductReferenceTypeEnum referenceType,
			List<ProductOption> options, Integer limit);

	/**
	 * Retrieves product references of a product given its code
	 * 
	 * @param code
	 *           the product code
	 * @param referenceTypes
	 *           the product reference types to return.
	 * @param options
	 *           options set that determines amount of information that will be attached to the returned product.
	 * @param limit
	 *           maximum number of references to retrieve. If null, all available references will be retrieved.
	 * @return the product references
	 */
	List<ProductReferenceData> getProductReferencesForCode(String code, List<ProductReferenceTypeEnum> referenceTypes,
			List<ProductOption> options, Integer limit);

}
