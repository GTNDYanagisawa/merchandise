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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.commons.strategies.impl.DefaultOndemandDeliveryAddressStrategy;
import de.hybris.platform.store.BaseStoreModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCisDetermineExternalTaxStrategyTest
{

	private DefaultCisDetermineExternalTaxStrategy defaultOmsDetermineExternalTaxStrategy;

	@Mock
	private DefaultOndemandDeliveryAddressStrategy defaultOndemandDeliveryAddressStrategy;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultOmsDetermineExternalTaxStrategy = new DefaultCisDetermineExternalTaxStrategy();
		defaultOmsDetermineExternalTaxStrategy.setOndemandDeliveryAddressStrategy(defaultOndemandDeliveryAddressStrategy);
	}

	@Test
	public void shouldNotCalculateTaxes()
	{
		final CartModel cart = mock(CartModel.class);
		given(cart.getNet()).willReturn(Boolean.FALSE);
		final boolean calculateTaxes = defaultOmsDetermineExternalTaxStrategy.shouldCalculateExternalTaxes(cart);
		Assert.assertEquals(calculateTaxes, false);
	}

	@Test
	public void shouldCalculateTaxes()
	{
		final AddressModel address = mock(AddressModel.class);

		final DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);

		final CartModel cart = mock(CartModel.class);
		final BaseStoreModel baseStore = mock(BaseStoreModel.class);
		given(baseStore.getExternalTaxEnabled()).willReturn(Boolean.TRUE);
		given(cart.getStore()).willReturn(baseStore);
		given(cart.getNet()).willReturn(Boolean.TRUE);
		given(cart.getDeliveryAddress()).willReturn(address);
		given(cart.getDeliveryMode()).willReturn(deliveryMode);
		given(defaultOndemandDeliveryAddressStrategy.getDeliveryAddressForOrder(cart)).willReturn(address);

		final boolean calculateTaxes = defaultOmsDetermineExternalTaxStrategy.shouldCalculateExternalTaxes(cart);
		Assert.assertEquals(calculateTaxes, true);
	}
}
