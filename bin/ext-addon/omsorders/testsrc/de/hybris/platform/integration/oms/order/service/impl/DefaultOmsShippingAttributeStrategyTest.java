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
package de.hybris.platform.integration.oms.order.service.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integration.oms.order.constants.OmsordersConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.oms.domain.address.Address;


@UnitTest
public class DefaultOmsShippingAttributeStrategyTest
{
	private static final String SHIPPING_ATTRIBUTES = "non-applicable";

	private DefaultOmsShippingAttributeStrategy omsShippingAttributeStrategy;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		omsShippingAttributeStrategy = new DefaultOmsShippingAttributeStrategy();
		omsShippingAttributeStrategy.setConfigurationService(configurationService);
		given(configuration.getString(OmsordersConstants.SHIP_TO_COMPANY)).willReturn(SHIPPING_ATTRIBUTES);
		given(configuration.getString(OmsordersConstants.SHIP_TO_PHONE)).willReturn(SHIPPING_ATTRIBUTES);
		given(configurationService.getConfiguration()).willReturn(configuration);
	}

	@Test
	public void shouldSetAttributes()
	{
		final Address deliveryAddress = Mockito.mock(Address.class);
		omsShippingAttributeStrategy.setShippingAttributes(deliveryAddress);
		Mockito.verify(deliveryAddress).setName(SHIPPING_ATTRIBUTES);
		Mockito.verify(deliveryAddress).setPhoneNumber(SHIPPING_ATTRIBUTES);
	}
}
