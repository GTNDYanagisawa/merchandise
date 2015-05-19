/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.configurablebundleservices.search.solrfacetsearch.provider.impl;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PropertyFieldValueProviderTestBase;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import com.google.common.collect.Lists;


/**
 * Test to check whether the lowest price value is returned in LowestBundlePriceValueProvider
 */
@UnitTest
public class LowestBundlePriceValueProviderTest extends PropertyFieldValueProviderTestBase
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final String PROPERTY_NAME = "lowestBundlePriceValue";
	@Mock
	private BundleRuleService bundleRuleService;

	@Mock
	private ProductModel product;

	@Mock
	private IndexedProperty indexedProperty;

	private final Collection<CurrencyModel> currencies = new ArrayList<CurrencyModel>();
	private CurrencyModel usd;

	@Before
	public void setUp() throws Exception
	{
		configure();
	}

	@Override
	protected String getPropertyName()
	{
		return PROPERTY_NAME;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new LowestBundlePriceValueProvider());
		configureBase();
		((LowestBundlePriceValueProvider) getPropertyFieldValueProvider()).setBundleRuleService(bundleRuleService);

		((LowestBundlePriceValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);
		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);

		when(Boolean.valueOf(indexedProperty.isLocalized())).thenReturn(Boolean.TRUE);
		createCurrencies();
		when(fieldNameProvider.getFieldNames(indexedProperty, usd.getIsocode().toLowerCase())).thenReturn(
				Lists.newArrayList(getPropertyName()));

		when(indexConfig.getCurrencies()).thenReturn(currencies);
		final ChangeProductPriceBundleRuleModel priceRule = new ChangeProductPriceBundleRuleModel();
		priceRule.setPrice(BigDecimal.valueOf(10.00));
		priceRule.setCurrency(usd);

		when(bundleRuleService.getChangePriceBundleRuleWithLowestPrice(product, usd)).thenReturn(priceRule);


	}

	private void createCurrencies()
	{
		usd = new CurrencyModel();
		usd.setName("USD", Locale.US);
		usd.setIsocode("USD");
		currencies.add(usd);

	}

	@Test
	public void testGetFiledValues() throws FieldValueProviderException
	{
		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, product);

		Assert.assertTrue(!result.isEmpty());
		Assert.assertEquals(1, result.size());
		final Double value = (Double) result.iterator().next().getValue();
		Assert.assertEquals(10.0, value.doubleValue(), 0.1);
	}

	@Test
	public void testInvalidArgs() throws FieldValueProviderException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("model can not be null");

		verify(((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, null));
	}

}
