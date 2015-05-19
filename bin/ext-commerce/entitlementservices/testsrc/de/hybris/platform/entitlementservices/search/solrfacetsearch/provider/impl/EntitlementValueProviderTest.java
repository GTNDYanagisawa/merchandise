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
 */

package de.hybris.platform.entitlementservices.search.solrfacetsearch.provider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PropertyFieldValueProviderTestBase;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.entitlementservices.model.EntitlementModel;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.entitlementservices.model.UsageUnitModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;

public class EntitlementValueProviderTest extends PropertyFieldValueProviderTestBase
{
	public static final String ENTITLEMENT_ID = "SMS";
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private SessionService sessionService;

	@Mock
	private IndexedProperty indexedProperty;

	@Mock
	private UsageUnitModel usageUnit;

	@Mock
	private EntitlementModel entitlement;

	private final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();

	final private ProductModel product = new ProductModel();

	@Mock
	private L10NService l10NService;

	@Before
	public void setUp()
	{
		configure();
	}

	@Override
	protected String getPropertyName()
	{
		return "";
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new EntitlementValueProvider());
		configureBase();

		final EntitlementValueProvider provider = (EntitlementValueProvider) getPropertyFieldValueProvider();
		provider.setCommonI18NService(commonI18NService);
		provider.setSessionService(sessionService);
		provider.setFieldNameProvider(fieldNameProvider);
		provider.setL10NService(l10NService);
		provider.setEntitlementId(ENTITLEMENT_ID);

		when(indexedProperty.isLocalized()).thenReturn(Boolean.FALSE);
		doReturn("unlimited").when(l10NService).getLocalizedString(Mockito.anyString(), Mockito.any(Object[].class));

		doReturn("pcs").when(usageUnit).getNamePlural();
		when(entitlement.getId()).thenReturn(ENTITLEMENT_ID);
		when(entitlement.getName()).thenReturn("entitlement name");
		when(entitlement.getUsageUnit()).thenReturn(usageUnit);
		when(entitlement.getProductEntitlements()).thenReturn(Arrays.asList(productEntitlement));
		productEntitlement.setEntitlement(entitlement);
		product.setProductEntitlements(Arrays.asList(productEntitlement));
	}

	@Test
	public void shouldReturnUnlimitedQuantity() throws FieldValueProviderException
	{
		productEntitlement.setQuantity(-1);
		final EntitlementValueProvider provider = (EntitlementValueProvider) getPropertyFieldValueProvider();
		List<Object> values = provider.getPropertyValue(product);
		assertNotNull(values);
		assertEquals(2, values.size());
		assertTrue(values.get(0).equals(entitlement.getName()) || values.get(1).equals(entitlement.getName()));
		assertTrue(values.get(0).equals("unlimited") || values.get(1).equals("unlimited"));
	}

	@Test
	public void shouldReturnFormattedQuantity() throws FieldValueProviderException
	{
		productEntitlement.setQuantity(5);
		final EntitlementValueProvider provider = (EntitlementValueProvider) getPropertyFieldValueProvider();
		List<Object> values = provider.getPropertyValue(product);
		assertNotNull(values);
		assertEquals(2, values.size());
		assertTrue(values.get(0).equals(entitlement.getName()) || values.get(1).equals(entitlement.getName()));
		assertTrue(values.get(0).equals("5 pcs") || values.get(1).equals("5 pcs"));
	}

	@Test
	public void testInvalidArgs() throws FieldValueProviderException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("model can not be null");

		verify(((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, null));
	}

	@Test
	public void shouldSkipForeignEntitlements()
	{
		try
		{
			when(entitlement.getId()).thenReturn(ENTITLEMENT_ID+"1");
			final EntitlementValueProvider provider = (EntitlementValueProvider) getPropertyFieldValueProvider();
			List<Object> values = provider.getPropertyValue(product);
			assertTrue(CollectionUtils.isEmpty(values));
		}
		finally
		{
			when(entitlement.getId()).thenReturn(ENTITLEMENT_ID);
		}
	}
}
