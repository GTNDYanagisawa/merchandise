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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.yb2bacceleratorcore.product.data.SolrFirstVariantCategoryEntryData;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.entity.VariantValueCategoryModelSequenceComparator;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Tests for {@link SolrFirstVariantCategoryManager}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SolrFirstVariantCategoryManagerTest
{
	@Mock
	private L10NService l10NService;

	private Comparator<VariantValueCategoryModel> comparator;

	private SolrFirstVariantCategoryManager manager;

	@Before
	public void setup()
	{
		comparator = new VariantValueCategoryModelSequenceComparator();
		manager = new SolrFirstVariantCategoryManager();
		manager.setL10NService(l10NService);
	}

	@Test
	public void testPropertyBuildToSolrShouldRebuildToData()
	{
		final String[] categoryNames = new String[]
		{ "cat1", "cat2" };
		final String[] variantCodes = new String[]
		{ "var1", "var2" };
		final int[] sequences = new int[]
		{ 1, 2 };
		final SortedMap<VariantValueCategoryModel, GenericVariantProductModel> categoryVariantPairs = buildCategoryVariantPairs(
				categoryNames, variantCodes, sequences);
		// build Solr property
		final String solrProperty = manager.buildSolrPropertyFromCategoryVariantPairs(categoryVariantPairs);
		Assert.assertNotNull(solrProperty);
		// build SolrFirstVariantCategoryEntryData from Solr property
		final List<SolrFirstVariantCategoryEntryData> datas = manager.buildFirstVariantCategoryListFromSolrProperty(solrProperty);
		Assert.assertNotNull(datas);
		// checks with what was on original input data was rebuild correctly
		for (final Entry<VariantValueCategoryModel, GenericVariantProductModel> entry : categoryVariantPairs.entrySet())
		{
			boolean exists = false;
			for (final SolrFirstVariantCategoryEntryData data : datas)
			{
				if (entry.getKey().getName().equals(data.getCategoryName())
						&& entry.getValue().getCode().equals(data.getVariantCode()))
				{
					exists = true;
					break;
				}
			}
			Assert.assertTrue("Entry [" + entry.getKey().getCode() + "," + entry.getValue() + "] does not have match in datas",
					exists);
		}
	}

	@Test
	public void testEmptyMapShouldBuildEmptyString()
	{
		final SortedMap<VariantValueCategoryModel, GenericVariantProductModel> categoryVariantPairs = new TreeMap<>(comparator);
		final String solrProperty = manager.buildSolrPropertyFromCategoryVariantPairs(categoryVariantPairs);
		Assert.assertNotNull(solrProperty);
		Assert.assertTrue(solrProperty.isEmpty());
	}

	@Test
	public void testEmptyPropertyShouldBuildEmptyList()
	{
		final List<SolrFirstVariantCategoryEntryData> entries = manager.buildFirstVariantCategoryListFromSolrProperty("");
		Assert.assertNotNull(entries);
		Assert.assertTrue(entries.isEmpty());
	}

	private SortedMap<VariantValueCategoryModel, GenericVariantProductModel> buildCategoryVariantPairs(
			final String[] categoryNames, final String[] variantCodes, final int[] sequences)
	{
		final SortedMap<VariantValueCategoryModel, GenericVariantProductModel> categoryVariantPairs = new TreeMap<>(comparator);
		for (int i = 0; i < variantCodes.length; i++)
		{
			final String categoryName = categoryNames[i];
			Mockito.when(l10NService.getLocalizedString(Mockito.eq(categoryName))).thenReturn(categoryName);

			final VariantValueCategoryModel category = Mockito.mock(VariantValueCategoryModel.class);
			Mockito.when(category.getName()).thenReturn(categoryName);
			Mockito.when(category.getSequence()).thenReturn(new Integer(sequences[i]));
			final GenericVariantProductModel variant = Mockito.mock(GenericVariantProductModel.class);
			Mockito.when(variant.getCode()).thenReturn(variantCodes[i]);
			categoryVariantPairs.put(category, variant);
		}
		return categoryVariantPairs;
	}


}
