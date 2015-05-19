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
package de.hybris.platform.yb2bacceleratorcore.suggestion.impl;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.yb2bacceleratorcore.suggestion.SimpleSuggestionService;
import de.hybris.platform.yb2bacceleratorcore.suggestion.dao.SimpleSuggestionDao;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SimpleSuggestionService}.
 */
public class DefaultSimpleSuggestionService implements SimpleSuggestionService
{
	private SimpleSuggestionDao simpleSuggestionDao;

	@Override
	public List<ProductModel> getReferencesForPurchasedInCategory(final CategoryModel category, final UserModel user,
			final List<ProductReferenceTypeEnum> referenceTypes, final boolean excludePurchased, final Integer limit)
	{
		return getSimpleSuggestionDao().findProductsRelatedToPurchasedProductsByCategory(category, user, referenceTypes,
				excludePurchased, limit);
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public List<ProductModel> getReferencesForPurchasedInCategory(final CategoryModel category, final UserModel user,
			final ProductReferenceTypeEnum referenceType, final boolean excludePurchased, final Integer limit)
	{
		return getSimpleSuggestionDao().findProductsRelatedToPurchasedProductsByCategory(category, user, referenceType,
				excludePurchased, limit);
	}

	protected SimpleSuggestionDao getSimpleSuggestionDao()
	{
		return simpleSuggestionDao;
	}

	@Required
	public void setSimpleSuggestionDao(final SimpleSuggestionDao simpleSuggestionDao)
	{
		this.simpleSuggestionDao = simpleSuggestionDao;
	}
}
