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

package de.hybris.platform.omsorders.interceptors;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.integration.oms.order.model.OmsZoneDeliveryModeValueModel;
import de.hybris.platform.omsorders.services.query.daos.OmsZoneDeliveryModeValueDao;
import de.hybris.platform.order.ZoneDeliveryModeService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.services.BaseStoreService;

import javax.annotation.Resource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Test for {@link OmsZoneDeliveryModeValueValidator}.
 */
@IntegrationTest
public class OmsZoneDeliveryModeValueValidatorTest extends ServicelayerTransactionalTest
{
	@Resource
	private OmsZoneDeliveryModeValueValidator omsZoneDeliveryModeValueValidator;
	@Resource
	private ModelService modelService;
	@Resource
	private ZoneDeliveryModeService zoneDeliveryModeService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private OmsZoneDeliveryModeValueDao omsZoneDeliveryModeValueDao;
	@Resource
	private BaseStoreService baseStoreService;

	/**
	 * Creates the core data, and necessary data for delivery modes.
	 */
	@Before
	public void setUp() throws Exception
	{
		importCsv("/omsorders/test/testOmsZoneDeliveryModeValueValidator.csv", "utf-8");
	}

	@Test
	public void testZoneDeliveryModeValueValidatorForZoneInconsistency()
	{
		final ZoneDeliveryModeModel stdNetDeliveryMode = (ZoneDeliveryModeModel) zoneDeliveryModeService
				.getDeliveryModeForCode("standard-net");

		final Double min = Double.valueOf(0);
		final Double value = Double.valueOf(8.99);
		final CurrencyModel currency = commonI18NService.getCurrency("USD");
		final ZoneModel duplicateZone = zoneDeliveryModeService.getZoneForCode("duplicate_zone");
		final OmsZoneDeliveryModeValueModel omsZoneDeliveryModeValueModel =
				createNewOmsZoneDeliveryModeValueModel(currency, min, value, duplicateZone, stdNetDeliveryMode);
		try
		{
			omsZoneDeliveryModeValueValidator.onValidate(omsZoneDeliveryModeValueModel, null);
		}
		catch (InterceptorException e)
		{
			//Expected
		}
		catch (Exception e)
		{
			fail("Zone Consistency was not checked " + e.getMessage());
		}
	}

	@Test
	public void testZoneDeliveryModeValueValidatorForUpdate()
	{
		final AbstractOrderModel cart = modelService.create(CartModel.class);
		final AddressModel addressModel = modelService.create(AddressModel.class);
		addressModel.setCountry(commonI18NService.getCountry("US"));
		cart.setDeliveryFromAddress(addressModel);
		cart.setDeliveryAddress(addressModel);
		cart.setCurrency(commonI18NService.getCurrency("USD"));
		cart.setNet(Boolean.FALSE);
		cart.setStore(baseStoreService.getBaseStoreForUid("testStore"));

		final OmsZoneDeliveryModeValueModel omsZoneDeliveryModeValueModel = omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cart, zoneDeliveryModeService
				.getDeliveryModeForCode("standard-gross"));

		omsZoneDeliveryModeValueModel.setDeliveryMode((ZoneDeliveryModeModel) zoneDeliveryModeService
				.getDeliveryModeForCode("standard-net"));
		omsZoneDeliveryModeValueModel.setValue(Double.valueOf(8.99));
		try
		{
			omsZoneDeliveryModeValueValidator.onValidate(omsZoneDeliveryModeValueModel, null);
		}
		catch (InterceptorException e)
		{
			//Expected
		}
		catch (Exception e)
		{
			fail("OmsZoneDeliveryModeValue should  not be updated: " + e.getMessage());
		}

	}

	@Test
	public void testZoneDeliveryModeValueValidator()
	{
		final ZoneDeliveryModeModel stdNetDeliveryMode = (ZoneDeliveryModeModel) zoneDeliveryModeService
				.getDeliveryModeForCode("standard-net");

		final Double min = Double.valueOf(0);
		final Double value = Double.valueOf(8.99);
		final CurrencyModel currency = commonI18NService.getCurrency("USD");
		final ZoneModel usZone = zoneDeliveryModeService.getZoneForCode("usa");
		testZoneDeliveryModeValueValidator(createNewOmsZoneDeliveryModeValueModel(currency, min, value, usZone, stdNetDeliveryMode), true);
	}

	private void testZoneDeliveryModeValueValidator(final OmsZoneDeliveryModeValueModel zoneDeliveryModeValue, final boolean expectException)
	{
		try
		{
			omsZoneDeliveryModeValueValidator.onValidate(zoneDeliveryModeValue, null);
			if (expectException)
			{
				fail("InterceptorException must be thrown.");
			}
		}
		catch (InterceptorException e)
		{
			//Expected
		}
		catch (Exception e)
		{
			fail("OmsZoneDeliveryModeValue was not created: " + e.getMessage());
		}
	}

	private OmsZoneDeliveryModeValueModel createNewOmsZoneDeliveryModeValueModel(final CurrencyModel currency, final Double min, final Double value,
	                                                                             final ZoneModel zone, final ZoneDeliveryModeModel zoneDeliveryMode)
	{
		final OmsZoneDeliveryModeValueModel omsZoneDeliveryModeValueModel = modelService.create(OmsZoneDeliveryModeValueModel.class);
		omsZoneDeliveryModeValueModel.setCurrency(currency);
		omsZoneDeliveryModeValueModel.setMinimum(min);
		omsZoneDeliveryModeValueModel.setValue(value);
		omsZoneDeliveryModeValueModel.setFromZone(zone);
		omsZoneDeliveryModeValueModel.setSpecificCarrierShippingMethod("02");
		omsZoneDeliveryModeValueModel.setZone(zone);
		omsZoneDeliveryModeValueModel.setDeliveryMode(zoneDeliveryMode);

		return omsZoneDeliveryModeValueModel;
	}

}

