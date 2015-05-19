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
package de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.impl;

import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.DefaultCategorySource;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.ProductSource;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Find the categories for a variant product and all its variants' categories.
 */
public class VariantCategorySource extends DefaultCategorySource
{
	private ProductSource productSource;

	/**
	 * Get all the variant products of a base product
	 */
	@Override
	protected Set<ProductModel> getProducts(final Object model)
	{
		return getProductSource().getProducts(model);
	}

	protected ProductSource getProductSource()
	{
		return productSource;
	}

	@Required
	public void setProductSource(final ProductSource productSource)
	{
		this.productSource = productSource;
	}

	@Override
	public Collection<CategoryModel> getCategoriesForConfigAndProperty(final IndexConfig indexConfig,
			final IndexedProperty indexedProperty, final Object baseProduct)
	{
		final Set<CategoryModel> variantValueCategoriesForVariantCategory = createEmptyVariantValueCategoriesSet();

		final Set<ProductModel> variantProductsForBaseProduct = getProducts(baseProduct);

		for (final ProductModel product : variantProductsForBaseProduct)
		{
			if (product instanceof GenericVariantProductModel)
			{
				final GenericVariantProductModel genericVariantProduct = (GenericVariantProductModel) product;
				final Collection<CategoryModel> variantValueCategories = genericVariantProduct.getSupercategories();

				for (final CategoryModel variantValueCategory : variantValueCategories)
				{
					// VariantValueCategories have only one VariantCategory
					final CategoryModel parentVariantCategory = variantValueCategory.getSupercategories().get(0);
					final boolean isSameCategoryName = StringUtils.equalsIgnoreCase(parentVariantCategory.getName(),
							indexedProperty.getName());
					if (isSameCategoryName)
					{
						variantValueCategoriesForVariantCategory.add(variantValueCategory);
					}
				}
			}
		}
		return variantValueCategoriesForVariantCategory;
	}

	private Set<CategoryModel> createEmptyVariantValueCategoriesSet()
	{
		return new TreeSet<>(new Comparator<CategoryModel>()
		{
			@Override
			public int compare(final CategoryModel category1, final CategoryModel category2)
			{
				return ((VariantValueCategoryModel) category1).getSequence().compareTo(
						((VariantValueCategoryModel) category2).getSequence());
			}
		});
	}
}