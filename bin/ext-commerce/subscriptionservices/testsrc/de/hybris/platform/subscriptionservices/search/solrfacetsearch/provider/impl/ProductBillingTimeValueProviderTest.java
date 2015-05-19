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
package de.hybris.platform.subscriptionservices.search.solrfacetsearch.provider.impl;

import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PropertyFieldValueProviderTestBase;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.Mock;


/**
 * Test to check that billing frequency attribute is returned by ProudctBillingTimeValueProvider
 */
@UnitTest
public class ProductBillingTimeValueProviderTest extends PropertyFieldValueProviderTestBase
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final String TEST_PRODUCT_KEYWORD_PROP_VAL = "propVal";
	private static final String TEST_PRODUCT_KEYWORD_PROP_EN_FIELD_NAME = "prop_en_string";
	@Mock
	private SessionService sessionService;
	@Mock
	private VariantProductModel model;
	@Mock
	private ProductModel baseProduct;

	@Mock
	private IndexedProperty indexedProperty;

	private final BillingTimeModel value = new BillingTimeModel();

	@Before
	public void setUp() throws Exception
	{
		configure();
	}

	@Override
	protected String getPropertyName()
	{
		return TEST_PRODUCT_KEYWORD_PROP_VAL;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new ProductBillingTimeValueProvider());
		configureBase();
		((ProductBillingTimeValueProvider) getPropertyFieldValueProvider()).setCommonI18NService(commonI18NService);
		((ProductBillingTimeValueProvider) getPropertyFieldValueProvider()).setSessionService(sessionService);
		((ProductBillingTimeValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);

		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);

		when(Boolean.valueOf(indexedProperty.isLocalized())).thenReturn(Boolean.FALSE);
		when(model.getBaseProduct()).thenReturn(baseProduct);



		when(sessionService.executeInLocalView((SessionExecutionBody) Matchers.any())).thenReturn(value);
	}

	@Test
	public void testInvalidArgs() throws FieldValueProviderException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("model can not be null");

		verify(((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, null));
	}

	@Test
	public void testWhenIndexPropertyIsLocalized() throws FieldValueProviderException
	{
		Collection<FieldValue> result;
		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.TRUE);

		given(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), Matchers.<String> any())).willReturn(
				singletonList(TEST_PRODUCT_KEYWORD_PROP_EN_FIELD_NAME));

		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertTrue("Did not receive expected result size.", result.size() == 2);
		for (final FieldValue val : result)
		{
			Assert.assertTrue(val.getValue().equals(value));
		}
	}
}
