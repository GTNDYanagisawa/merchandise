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

import de.hybris.platform.b2b.model.VariantValueCategoryModel;

import java.util.Comparator;


/**
 * Comparator for type {@link VariantValueCategoryModel}.
 */
public class VariantValueCategoryModelSequenceComparator implements Comparator<VariantValueCategoryModel>
{

	@Override
	public int compare(final VariantValueCategoryModel variantValueCategory1, final VariantValueCategoryModel variantValueCategory2)
	{
		if (variantValueCategory1 != null && variantValueCategory1.getSequence() != null && variantValueCategory2 != null
				&& variantValueCategory2.getSequence() != null)
		{
			return Integer.compare(variantValueCategory1.getSequence(), variantValueCategory2.getSequence());
		}
		else
		{
			throw new IllegalArgumentException("Cannot compare variants that are null or without sequence.");
		}
	}
}
