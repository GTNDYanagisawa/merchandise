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
package de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.entity;

import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.category.model.CategoryModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Comparator for type {@link GenericVariantProductModel}.
 */
public class GenericVariantProductModelComparator implements Comparator<GenericVariantProductModel>
{
	private Comparator<VariantValueCategoryModel> variantValueCategoryModelPriorityComparator;

	/**
	 * Considers variant1 greater then other if, for the same variant category level, the value of the
	 * {@link VariantValueCategoryModel} belonging to variant1 has a bigger sequence then the
	 * {@link VariantValueCategoryModel} belonging to variant2.
	 */
	@Override
	public int compare(final GenericVariantProductModel variant1, final GenericVariantProductModel variant2)
	{
		if ((variant1 != null) && (variant2 != null))
		{
			// get sorted list of VariantValueCategories
			final List<VariantValueCategoryModel> variantValueCategories1 = getVariantValuesCategories(variant1);
			final List<VariantValueCategoryModel> variantValueCategories2 = getVariantValuesCategories(variant2);
			if (variantValueCategories1.size() == variantValueCategories2.size())
			{
				int compare = 0;
				for (int i = 0; i < variantValueCategories1.size(); i++)
				{
					final VariantValueCategoryModel valueCategory1 = variantValueCategories1.get(i);
					final VariantValueCategoryModel valueCategory2 = variantValueCategories2.get(i);
					compare = Integer.compare(valueCategory1.getSequence(), valueCategory2.getSequence());
					if (compare != 0)
					{
						break;
					}
				}
				return compare;
			}
			else
			{
				throw new IllegalArgumentException("Variants should have the same number of category levels to be compared!");
			}
		}
		else
		{
			throw new IllegalArgumentException("Cannot compare null variants!");
		}
	}

	/**
	 * Get the list of {@link VariantValueCategoryModel} related to a specific {@link GenericVariantProductModel}.
	 * 
	 * @param productModel
	 *           the variant product.
	 * @return The variant value categories, ordered by variant category priority.
	 */
	protected List<VariantValueCategoryModel> getVariantValuesCategories(final GenericVariantProductModel productModel)
	{
		final List<VariantValueCategoryModel> variantValueCategories = new ArrayList<>();
		for (final CategoryModel categoryProductModel : productModel.getSupercategories())
		{
			if (categoryProductModel instanceof VariantValueCategoryModel)
			{
				variantValueCategories.add((VariantValueCategoryModel) categoryProductModel);
			}
		}
		Collections.sort(variantValueCategories, variantValueCategoryModelPriorityComparator);

		return variantValueCategories;
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
}
