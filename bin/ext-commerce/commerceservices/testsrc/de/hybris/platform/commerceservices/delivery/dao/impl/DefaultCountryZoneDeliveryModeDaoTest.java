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
package de.hybris.platform.commerceservices.delivery.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.delivery.dao.CountryZoneDeliveryModeDao;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCountryZoneDeliveryModeDaoTest extends ServicelayerTransactionalTest
{
	@Resource
	private CountryZoneDeliveryModeDao countryZoneDeliveryModeDao;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private DeliveryModeService deliveryModeService;
	private CurrencyModel usd;
	private CountryModel countryModelUS;
	private DeliveryModeModel premium_gross_dm;
	private DeliveryModeModel standard_gross_dm;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/commerceservices/test/defaultCountryZoneDeliveryModeDaoTest.impex", "utf-8");
		countryModelUS = commonI18NService.getCountry("US");
		usd = commonI18NService.getCurrency("USD");
		premium_gross_dm = deliveryModeService.getDeliveryModeForCode("premium-gross");
		standard_gross_dm = deliveryModeService.getDeliveryModeForCode("standard-gross");
	}

	@Test
	public void testFindDeliveryModes()
	{
		final Collection<DeliveryModeModel> deliveryModes = countryZoneDeliveryModeDao.findDeliveryModesByCountryAndCurrency(
				countryModelUS, usd, Boolean.FALSE);

		assertNotNull(deliveryModes);
		assertEquals(2, deliveryModes.size());
		assertThat(deliveryModes, hasItems(premium_gross_dm, standard_gross_dm));
	}
}
