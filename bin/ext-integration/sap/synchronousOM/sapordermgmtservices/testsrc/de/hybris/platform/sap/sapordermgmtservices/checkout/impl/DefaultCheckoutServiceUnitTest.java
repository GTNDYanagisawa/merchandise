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
package de.hybris.platform.sap.sapordermgmtservices.checkout.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl.DefaultBolCartFacade;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultDeliveryModePopulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class DefaultCheckoutServiceUnitTest extends SapordermanagmentBolSpringJunitTest
{
	DefaultCheckoutService classUnderTest = new DefaultCheckoutService();

	@Before
	public void init()
	{
		final DefaultBolCartFacade bolCartFacade = new DefaultBolCartFacade();
		bolCartFacade.setGenericFactory(getGenericFactory());
		classUnderTest.setBolCartFacade(bolCartFacade);

		final AbstractPopulatingConverter<Entry<String, String>, DeliveryModeData> deliveryModeConverter = new AbstractPopulatingConverter<Map.Entry<String, String>, DeliveryModeData>()
		{



			@Override
			protected DeliveryModeData createTarget()
			{
				return new DeliveryModeData();
			}
		};
		final List<Populator<Entry<String, String>, DeliveryModeData>> populators = new ArrayList<>();
		populators.add(new DefaultDeliveryModePopulator());
		deliveryModeConverter.setPopulators(populators);
		classUnderTest.setDeliveryModeConverter(deliveryModeConverter);
	}


	@Test
	public void testBolFactory()
	{
		assertNotNull(classUnderTest.getBolCartFacade());
		assertNotNull(((DefaultBolCartFacade) classUnderTest.getBolCartFacade()).getGenericFactory());
	}

	@Test
	public void testDeliveryModeConverter()
	{
		assertNotNull(classUnderTest.getDeliveryModeConverter());
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testSetDeliveryMode()
	{
		final String deliveryModeCode = "A";
		classUnderTest.getBolCartFacade().getCart().setTechKey(TechKey.EMPTY_KEY);
		//cart is not initialized yet
		classUnderTest.setDeliveryMode(deliveryModeCode);

	}

	@Test
	public void testGetCurrentDeliveryMode()
	{
		final String deliveryMode = "A";
		classUnderTest.getBolCartFacade().getCart().getHeader().setShipCond(deliveryMode);

		final String currentDeliveryMode = classUnderTest.getCurrentDeliveryMode();
		assertNotNull(currentDeliveryMode);
		assertEquals(deliveryMode, currentDeliveryMode);
	}

	@Test
	public void testGetEntry()
	{
		final Map<String, String> map = new HashMap<>();
		final String key = "A";
		final String value = "B";
		map.put(key, value);
		final Entry<String, String> entry = classUnderTest.getEntry(key, map);
		assertNotNull(entry);
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testSetPurchaseOrderNumber()
	{
		final String poNr = "A";
		classUnderTest.getBolCartFacade().getCart().setTechKey(TechKey.EMPTY_KEY);
		//cart is not initialized yet -> exception
		classUnderTest.setPurchaseOrderNumber(poNr);
	}

}
