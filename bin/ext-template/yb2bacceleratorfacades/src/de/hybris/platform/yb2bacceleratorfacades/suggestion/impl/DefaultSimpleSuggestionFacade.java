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
package de.hybris.platform.yb2bacceleratorfacades.suggestion.impl;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.yb2bacceleratorcore.suggestion.SimpleSuggestionService;
import de.hybris.platform.yb2bacceleratorfacades.suggestion.SimpleSuggestionFacade;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SimpleSuggestionFacade}.
 */
public class DefaultSimpleSuggestionFacade implements SimpleSuggestionFacade
{
	private UserService userService;
	private CategoryService categoryService;
	private Populator<ProductModel, ProductData> productPrimaryImagePopulator;
	private Populator<ProductModel, ProductData> productPricePopulator;
	private Converter<ProductModel, ProductData> productConverter;
	private SimpleSuggestionService b2bSimpleSuggestionService;


	@Override
	public List<ProductData> getReferencesForPurchasedInCategory(final String categoryCode,
			final List<ProductReferenceTypeEnum> referenceTypes, final boolean excludePurchased, final Integer limit)
	{
		final UserModel user = getUserService().getCurrentUser();
		final CategoryModel category = getCategoryService().getCategoryForCode(categoryCode);

		final List<ProductModel> products = getB2bSimpleSuggestionService().getReferencesForPurchasedInCategory(category, user,
				referenceTypes, excludePurchased, limit);

		final List<ProductData> result = new LinkedList<ProductData>();
		for (final ProductModel productModel : products)
		{
			final ProductData productData = getProductConverter().convert(productModel);

			getProductPricePopulator().populate(productModel, productData);
			getProductPrimaryImagePopulator().populate(productModel, productData);

			result.add(productData);
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public List<ProductData> getReferencesForPurchasedInCategory(final String categoryCode,
			final ProductReferenceTypeEnum referenceType, final boolean excludePurchased, final Integer limit)
	{
		final UserModel user = getUserService().getCurrentUser();
		final CategoryModel category = getCategoryService().getCategoryForCode(categoryCode);

		final List<ProductModel> products = getB2bSimpleSuggestionService().getReferencesForPurchasedInCategory(category, user,
				referenceType, excludePurchased, limit);

		final List<ProductData> result = new LinkedList<ProductData>();
		for (final ProductModel productModel : products)
		{
			final ProductData productData = getProductConverter().convert(productModel);

			getProductPricePopulator().populate(productModel, productData);
			getProductPrimaryImagePopulator().populate(productModel, productData);

			result.add(productData);
		}
		return result;
	}

	/**
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	/**
	 * @return the productPrimaryImagePopulator
	 */
	protected Populator<ProductModel, ProductData> getProductPrimaryImagePopulator()
	{
		return productPrimaryImagePopulator;
	}

	/**
	 * @param productPrimaryImagePopulator
	 *           the productPrimaryImagePopulator to set
	 */
	@Required
	public void setProductPrimaryImagePopulator(final Populator<ProductModel, ProductData> productPrimaryImagePopulator)
	{
		this.productPrimaryImagePopulator = productPrimaryImagePopulator;
	}

	/**
	 * @return the productPricePopulator
	 */
	protected Populator<ProductModel, ProductData> getProductPricePopulator()
	{
		return productPricePopulator;
	}

	/**
	 * @param productPricePopulator
	 *           the productPricePopulator to set
	 */
	@Required
	public void setProductPricePopulator(final Populator<ProductModel, ProductData> productPricePopulator)
	{
		this.productPricePopulator = productPricePopulator;
	}

	/**
	 * @return the productConverter
	 */
	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * @param productConverter
	 *           the productConverter to set
	 */
	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	/**
	 * @return the simpleSuggestionService
	 */
	protected SimpleSuggestionService getB2bSimpleSuggestionService()
	{
		return b2bSimpleSuggestionService;
	}

	/**
	 * @param simpleSuggestionService
	 *           the simpleSuggestionService to set
	 */
	@Required
	public void setB2bSimpleSuggestionService(final SimpleSuggestionService simpleSuggestionService)
	{
		this.b2bSimpleSuggestionService = simpleSuggestionService;
	}

}
