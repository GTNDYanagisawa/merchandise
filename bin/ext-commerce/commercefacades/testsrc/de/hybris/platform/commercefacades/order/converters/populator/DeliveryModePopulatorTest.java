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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@UnitTest
public class DeliveryModePopulatorTest
{

	private AbstractPopulatingConverter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter =
				new ConverterFactory<DeliveryModeModel, DeliveryModeData, DeliveryModePopulator>().create(DeliveryModeData.class, new DeliveryModePopulator());

	@Before
	public void setUp()
	{
		//Do Nothing
	}

	@Test
	public void testConvert()
	{
		final DeliveryModeModel deliveryModeModel = mock(DeliveryModeModel.class);
		given(deliveryModeModel.getCode()).willReturn("code");
		given(deliveryModeModel.getName()).willReturn("name");
		given(deliveryModeModel.getDescription()).willReturn("desc");
		final DeliveryModeData deliveryModeData = deliveryModeConverter.convert(deliveryModeModel);
		Assert.assertEquals("code", deliveryModeData.getCode());
		Assert.assertEquals("name", deliveryModeData.getName());
		Assert.assertEquals("desc", deliveryModeData.getDescription());
	}
}
