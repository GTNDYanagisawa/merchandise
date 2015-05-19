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
package de.hybris.platform.commerceservices.strategies.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.delivery.dao.CountryZoneDeliveryModeDao;
import de.hybris.platform.commerceservices.delivery.dao.PickupDeliveryModeDao;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultDeliveryModelLookupStrategyTest
{
	private DefaultDeliveryModeLookupStrategy strategy;

	@Mock
	private CountryZoneDeliveryModeDao countryZoneDeliveryModeDao;
	@Mock
	private AbstractOrderModel abstractOrderModel;
	@Mock
	private PickupDeliveryModeDao pickupDeliveryModeDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		strategy = new DefaultDeliveryModeLookupStrategy();
		strategy.setCountryZoneDeliveryModeDao(countryZoneDeliveryModeDao);
		strategy.setPickupDeliveryModeDao(pickupDeliveryModeDao);
	}


	@Test
	public void testGetDeliveryModesForOrder()
	{
		final Collection<DeliveryModeModel> deliveryModels = new ArrayList<DeliveryModeModel>();
		final AddressModel addressModel = mock(AddressModel.class);
		final CountryModel deliveryCountry = mock(CountryModel.class);
		final CurrencyModel currency = mock(CurrencyModel.class);
		final DeliveryModeModel deliveryModeModel1 = mock(DeliveryModeModel.class);
		final AbstractOrderEntryModel entryModel = mock(AbstractOrderEntryModel.class);

		deliveryModels.add(mock(DeliveryModeModel.class));
		deliveryModels.add(deliveryModeModel1);
		deliveryModels.add(mock(DeliveryModeModel.class));

		given(abstractOrderModel.getNet()).willReturn(Boolean.FALSE);
		given(abstractOrderModel.getDeliveryAddress()).willReturn(addressModel);
		given(addressModel.getCountry()).willReturn(deliveryCountry);
		given(abstractOrderModel.getCurrency()).willReturn(currency);
		given(abstractOrderModel.getEntries()).willReturn(Collections.singletonList(entryModel));
		given(entryModel.getDeliveryPointOfService()).willReturn(null);
		given(countryZoneDeliveryModeDao.findDeliveryModes((AbstractOrderModel) Mockito.anyObject())).willReturn(deliveryModels);

		final List<DeliveryModeModel> result = strategy.getSelectableDeliveryModesForOrder(abstractOrderModel);

		Assert.assertNotNull(result);
		Assert.assertEquals(3, result.size());
		Assert.assertEquals(deliveryModeModel1, result.get(1));
	}
}
