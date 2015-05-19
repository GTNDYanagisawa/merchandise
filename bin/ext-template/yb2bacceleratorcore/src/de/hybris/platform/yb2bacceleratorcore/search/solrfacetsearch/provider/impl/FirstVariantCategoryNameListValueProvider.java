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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.AbstractMultidimensionalProductFieldValueProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Value Provider for first level of categories for multidimensional products.
 */
public class FirstVariantCategoryNameListValueProvider extends AbstractMultidimensionalProductFieldValueProvider
{
	private Comparator<VariantValueCategoryModel> variantValueCategoryModelPriorityComparator;
	private Comparator<VariantValueCategoryModel> variantValueCategoryModelSequenceComparator;
	private SolrFirstVariantCategoryManager categoryManager;

	@Override
	public Object getFieldValue(final ProductModel product)
	{
		String categoryNameList = null;
		if (isVariantBaseProduct(product))
		{
			final Collection<VariantProductModel> variants = product.getVariants();
			if (CollectionUtils.isNotEmpty(variants))
			{
				final SortedMap<VariantValueCategoryModel, GenericVariantProductModel> categoryValues = new TreeMap<>(
						variantValueCategoryModelSequenceComparator);
				for (final VariantProductModel variant : variants)
				{
					if (variant instanceof GenericVariantProductModel)
					{
						addCategories(categoryValues, (GenericVariantProductModel) variant);
					}
				}
				if (!categoryValues.isEmpty())
				{
					categoryNameList = categoryManager.buildSolrPropertyFromCategoryVariantPairs(categoryValues);
				}
			}

		}
		return categoryNameList;
	}

	/**
	 * Add first level category from {@code variant} to {@code categoryValues}. If there is already a first level
	 * category with higher precedence, nothing is added.
	 * 
	 * @param categoryValues
	 *           The map to be populated.
	 * @param variant
	 *           The variant that contains the categories.
	 */
	protected void addCategories(final SortedMap<VariantValueCategoryModel, GenericVariantProductModel> categoryValues,
			final GenericVariantProductModel variant)
	{
		final List<VariantValueCategoryModel> currentCategoryValues = getVariantList(variant);
		if (!currentCategoryValues.isEmpty())
		{
			boolean addCurrent = false;
			// if there is already a similar entry, check if current variant has higher priority
			final GenericVariantProductModel oldVariant = categoryValues.get(currentCategoryValues.get(0));
			if (oldVariant != null)
			{
				final List<VariantValueCategoryModel> oldCategoryValues = getVariantList(oldVariant);
				if (!oldCategoryValues.isEmpty())
				{
					addCurrent = isCurrentListPrecedent(currentCategoryValues, oldCategoryValues);
				}
			}
			else
			{
				addCurrent = true;
			}
			if (addCurrent)
			{
				categoryValues.put(currentCategoryValues.get(0), variant);
			}
		}
	}

	/**
	 * Get the list of {@link VariantValueCategoryModel} for a given {@link GenericVariantProductModel}.
	 * 
	 * @param variant
	 *           The variant that contains the categories.
	 * @return A list of categories sorted by priority.
	 */
	protected List<VariantValueCategoryModel> getVariantList(final GenericVariantProductModel variant)
	{
		final Collection<CategoryModel> categories = variant.getSupercategories();
		final List<VariantValueCategoryModel> currentCategoryValues = new ArrayList<>();
		for (final CategoryModel category : categories)
		{
			if (category instanceof VariantValueCategoryModel)
			{
				currentCategoryValues.add((VariantValueCategoryModel) category);
			}
		}
		if (!currentCategoryValues.isEmpty())
		{
			Collections.sort(currentCategoryValues, variantValueCategoryModelPriorityComparator);
		}
		return currentCategoryValues;
	}

	/**
	 * Compare two category lists (previously sorted by priority) to check if the categories in the current one are
	 * previous to the old one.<br>
	 * e.g.: If {@code currentCategoryValues} has sequences [3,4,5] and {@code oldCategoryValues} has sequences [3,5,4],
	 * then {@code currentCategoryValues} is previous (on first level, 3 == 3, but on second level 4 < 5).
	 * 
	 * @param currentCategoryValues
	 *           Current category list.
	 * @param oldCategoryValues
	 *           Existent category list to compare with.
	 * @return {@code true} if {@code currentCategoryValues} categories are in lower sequence than
	 *         {@code oldCategoryValues}.
	 */
	protected boolean isCurrentListPrecedent(final List<VariantValueCategoryModel> currentCategoryValues,
			final List<VariantValueCategoryModel> oldCategoryValues)
	{
		boolean isPrecendent = false;
		if (currentCategoryValues.size() == oldCategoryValues.size())
		{
			for (int i = 0; i < currentCategoryValues.size(); i++)
			{
				if (variantValueCategoryModelSequenceComparator.compare(currentCategoryValues.get(i), oldCategoryValues.get(i)) < 0)
				{
					isPrecendent = true;
					break;
				}
			}
		}
		return isPrecendent;
	}

	public Comparator<VariantValueCategoryModel> getVariantValueCategoryModelPriorityComparator()
	{
		return variantValueCategoryModelPriorityComparator;
	}

	@Required
	public void setVariantValueCategoryModelPriorityComparator(
			final Comparator<VariantValueCategoryModel> variantValueCategoryModelPriorityComparator)
	{
		this.variantValueCategoryModelPriorityComparator = variantValueCategoryModelPriorityComparator;
	}

	public Comparator<VariantValueCategoryModel> getVariantValueCategoryModelSequenceComparator()
	{
		return variantValueCategoryModelSequenceComparator;
	}

	@Required
	public void setVariantValueCategoryModelSequenceComparator(
			final Comparator<VariantValueCategoryModel> variantValueCategoryModelSequenceComparator)
	{
		this.variantValueCategoryModelSequenceComparator = variantValueCategoryModelSequenceComparator;
	}

	public SolrFirstVariantCategoryManager getCategoryManager()
	{
		return categoryManager;
	}

	@Required
	public void setCategoryManager(final SolrFirstVariantCategoryManager categoryManager)
	{
		this.categoryManager = categoryManager;
	}

}
