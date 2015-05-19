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
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@UnitTest
public class ZoneDeliveryModePopulatorTest
{
	private AbstractPopulatingConverter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter =
				new ConverterFactory<ZoneDeliveryModeModel, ZoneDeliveryModeData, ZoneDeliveryModePopulator>().create(ZoneDeliveryModeData.class, new ZoneDeliveryModePopulator());

	@Before
	public void setUp()
	{
		//Do Nothing
	}

	@Test
	public void testConvert()
	{
		final ZoneDeliveryModeModel zoneDeliveryModeModel = mock(ZoneDeliveryModeModel.class);
		given(zoneDeliveryModeModel.getCode()).willReturn("code");
		given(zoneDeliveryModeModel.getName()).willReturn("name");
		given(zoneDeliveryModeModel.getDescription()).willReturn("desc");
		final ZoneDeliveryModeData zoneDeliveryModeData = zoneDeliveryModeConverter.convert(zoneDeliveryModeModel);
		Assert.assertEquals("code", zoneDeliveryModeData.getCode());
		Assert.assertEquals("name", zoneDeliveryModeData.getName());
		Assert.assertEquals("desc", zoneDeliveryModeData.getDescription());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertNull()
	{
		zoneDeliveryModeConverter.convert(null);
	}
}
