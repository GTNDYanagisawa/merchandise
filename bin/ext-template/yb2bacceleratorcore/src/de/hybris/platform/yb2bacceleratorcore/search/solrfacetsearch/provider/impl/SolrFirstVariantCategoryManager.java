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
import de.hybris.platform.yb2bacceleratorcore.product.data.SolrFirstVariantCategoryEntryData;
import de.hybris.platform.servicelayer.i18n.L10NService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.springframework.beans.factory.annotation.Required;


/**
 * Class to process {@link SolrFirstVariantCategoryEntryData} operations.
 */
public class SolrFirstVariantCategoryManager
{

	protected static final String BEFORE_BEAN = "_SFVC_";
	protected static final String FIELD_SEPARATOR = "_:_";
	protected static final int TOTAL_FIELDS = 2;

	private L10NService l10NService;

	/**
	 * Builds a String to be used in first category name list Solr property.
	 * 
	 * @param categoryVariantPairs
	 *           Sorted pairs of {@link VariantValueCategoryModel} and {@link GenericVariantProductModel}.code.
	 * @return String to be used in Solr property.
	 */
	public String buildSolrPropertyFromCategoryVariantPairs(
			final SortedMap<VariantValueCategoryModel, GenericVariantProductModel> categoryVariantPairs)
	{
		final StringBuilder builder = new StringBuilder();
		if (categoryVariantPairs != null)
		{
			for (final Entry<VariantValueCategoryModel, GenericVariantProductModel> entry : categoryVariantPairs.entrySet())
			{
				builder.append(BEFORE_BEAN + localizeForKey(entry.getKey().getName()) + FIELD_SEPARATOR + entry.getValue().getCode());
			}
		}
		return builder.toString();
	}

	/**
	 * Generate a list of {@link SolrFirstVariantCategoryEntryData} based on a Solr property String that holds the first
	 * category name list.
	 * 
	 * @param solrProperty
	 *           The first category name list.
	 * @return List of {@link SolrFirstVariantCategoryEntryData};
	 */
	public List<SolrFirstVariantCategoryEntryData> buildFirstVariantCategoryListFromSolrProperty(final String solrProperty)
	{
		final List<SolrFirstVariantCategoryEntryData> entries = new ArrayList<>();
		// Split by beans. Discard first entry in array, as it will be empty
		final String[] original = solrProperty.split(BEFORE_BEAN);
		if (original.length > 1)
		{
			final String[] propertyEntries = Arrays.copyOfRange(original, 1, original.length);
			for (final String propertyEntry : propertyEntries)
			{
				final String[] tokens = propertyEntry.split(FIELD_SEPARATOR);
				if (tokens != null && tokens.length == TOTAL_FIELDS)
				{
					for (int i = 0; i < tokens.length; i += TOTAL_FIELDS)
					{
						final SolrFirstVariantCategoryEntryData entry = new SolrFirstVariantCategoryEntryData();
						entry.setCategoryName(tokens[i]);
						entry.setVariantCode(tokens[i + 1]);
						entries.add(entry);
					}
				}
				else
				{
					throw new IllegalArgumentException("The solrProperty [" + solrProperty + "] should have " + TOTAL_FIELDS
							+ " fields separated by '" + FIELD_SEPARATOR + "'");
				}

			}
		}
		return entries;
	}

	public String localizeForKey(final String key)
	{
		return getL10NService().getLocalizedString(key);
	}

	public L10NService getL10NService()
	{
		return l10NService;
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}
}
