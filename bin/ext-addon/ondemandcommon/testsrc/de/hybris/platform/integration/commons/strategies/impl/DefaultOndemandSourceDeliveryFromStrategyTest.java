/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package de.hybris.platform.integration.commons.strategies.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@IntegrationTest
public class DefaultOndemandSourceDeliveryFromStrategyTest extends ServicelayerTest
{

	private DefaultOndemandSourceDeliveryFromStrategy defaultOndemandSourceDeliveryFromStrategy;

	private AbstractOrderModel order;

	@Mock
	private ModelService modelService;

	@Resource
	private SessionService sessionService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		order = new AbstractOrderModel();
		final AddressModel address = mock(AddressModel.class);
		given(address.getStreetname()).willReturn("Test Street");
		final DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
		final List<AbstractOrderEntryModel> entries = Collections.emptyList();
		order.setEntries(entries);
		order.setDeliveryAddress(address);
		order.setDeliveryFromAddress(address);
		order.setDeliveryMode(deliveryMode);
		modelService = Mockito.mock(ModelService.class);
		given(modelService.getModelType(order)).willReturn("Cart");
		defaultOndemandSourceDeliveryFromStrategy = new DefaultOndemandSourceDeliveryFromStrategy();
		defaultOndemandSourceDeliveryFromStrategy.setModelService(modelService);
		defaultOndemandSourceDeliveryFromStrategy.setSessionService(sessionService);
	}

	@Test
	public void testShouldNotRecalculate()
	{
		defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order);
		Assert.assertEquals(false, defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order));
	}

	@Test
	public void testShouldRecalculateEmptyHash()
	{
		Assert.assertEquals(true, defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order));
	}

	@Test
	public void testShouldRecalculateAttributeChanged()
	{
		defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order);
		Assert.assertEquals(false, defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order));

		final AddressModel newAddress = mock(AddressModel.class);
		given(newAddress.getLine1()).willReturn("Test Street 1");
		order.setDeliveryAddress(newAddress);
		Assert.assertEquals(true, defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order));
	}

	@Test
	public void testShouldRecalculateNoDeliveryMode()
	{

		order.setDeliveryMode(null);
		Assert.assertEquals(true, defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order));
	}

	@Test
	public void testShouldNotRecalculateNoAddressShippingOrder()
	{
		defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order);
		Assert.assertEquals(false, defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order));

		order.setDeliveryAddress(null);
		Assert.assertEquals(false, defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order));
	}

	@Test
	public void testShouldRecalculateNoAddressPickupOrder()
	{
		defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order);
		Assert.assertEquals(false, defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order));

		final AbstractOrderEntryModel orderEntry = Mockito.mock(AbstractOrderEntryModel.class);
		final PointOfServiceModel dpos = Mockito.mock(PointOfServiceModel.class);
		given(orderEntry.getDeliveryPointOfService()).willReturn(dpos);
		order.setEntries(Collections.singletonList(orderEntry));
		order.setDeliveryAddress(null);

		Assert.assertEquals(true, defaultOndemandSourceDeliveryFromStrategy.shouldSourceDeliveryFromAddress(order));
	}

}
