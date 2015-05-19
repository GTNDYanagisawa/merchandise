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
package de.hybris.platform.yb2bacceleratorcore.suggestion.dao;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;


/**
 * Dao to retrieve product related data for
 * {@link de.hybris.platform.yb2bacceleratorcore.suggestion.SimpleSuggestionService}
 */
public interface SimpleSuggestionDao extends Dao
{
	/**
	 * @deprecated use findProductsRelatedToPurchasedProductsByCategory(CategoryModel category, UserModel user,
	 *             List<ProductReferenceTypeEnum> referenceTypes, boolean excludePurchased, Integer limit) instead.
	 */
	@Deprecated
	List<ProductModel> findProductsRelatedToPurchasedProductsByCategory(CategoryModel category, UserModel user,
			ProductReferenceTypeEnum referenceType, boolean excludePurchased, Integer limit);

	/**
	 * Returns a list of referenced products for a product purchased in a category identified by categoryCode.
	 * 
	 * @param category
	 *           the category that the returned products must belong to
	 * @param user
	 *           the user that has placed the orders
	 * @param referenceTypes
	 *           optional referenceTypes
	 * @param excludePurchased
	 *           if true, only retrieve products that have not been purchased by the user
	 * @param limit
	 *           if not null: limit the amount of returned products to the given number
	 * @return a list with referenced products
	 */
	List<ProductModel> findProductsRelatedToPurchasedProductsByCategory(CategoryModel category, UserModel user,
			List<ProductReferenceTypeEnum> referenceTypes, boolean excludePurchased, Integer limit);
}
