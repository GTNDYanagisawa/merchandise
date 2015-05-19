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
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PropertyFieldValueProviderTestBase;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Tests for {@link ProductPriceRangeValueProvider}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductPriceRangeValueProviderTest extends PropertyFieldValueProviderTestBase
{

	private static final String SOLR_PROPERTY = "priceRange";
	private static final String FIELD_NAME_INDEXING = SOLR_PROPERTY + "_string";
	private static final String FIELD_NAME_SORTING = SOLR_PROPERTY + "_sortable_string";

	@Mock
	private IndexedProperty indexedProperty;

	private ProductPriceRangeValueProvider valueProvider;

	@Before
	public void setUp() throws Exception
	{
		valueProvider = new ProductPriceRangeValueProvider();
		configure();
	}

	@Override
	protected String getPropertyName()
	{
		return SOLR_PROPERTY;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(valueProvider);
		configureBase();

		((ProductPriceRangeValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);

		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);
	}

	@Test
	public void testProductWithVariantGeneratesFields() throws FieldValueProviderException
	{
		final ProductModel model = new ProductModel();
		final List<VariantProductModel> variants = new ArrayList<>();
		final GenericVariantProductModel variant = new GenericVariantProductModel();
		final PriceRowModel lower = buildPriceRow(1.1);
		final PriceRowModel higher = buildPriceRow(2.2);
		final List<PriceRowModel> europe1 = Arrays.asList(new PriceRowModel[]
		{ higher, lower });
		variant.setEurope1Prices(europe1);
		variants.add(variant);
		model.setVariants(variants);

		final List fieldNames = Arrays.asList(new String[]
		{ FIELD_NAME_INDEXING, FIELD_NAME_SORTING });

		Mockito.when(fieldNameProvider.getFieldNames(Mockito.eq(indexedProperty), Mockito.anyString())).thenReturn(fieldNames);

		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, model);
		// result should not be empty
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
	}

	private PriceRowModel buildPriceRow(final double value)
	{
		final PriceRowModel price = new PriceRowModel();
		price.setPrice(value);
		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("CAN");
		price.setCurrency(currency);
		return price;
	}


}
