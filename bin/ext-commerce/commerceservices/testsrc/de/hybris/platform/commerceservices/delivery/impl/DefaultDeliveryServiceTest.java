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
package de.hybris.platform.commerceservices.delivery.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.strategies.DeliveryAddressesLookupStrategy;
import de.hybris.platform.commerceservices.strategies.DeliveryModeLookupStrategy;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.order.daos.DeliveryModeDao;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 */
@UnitTest
public class DefaultDeliveryServiceTest
{
	private DefaultDeliveryService deliveryService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;

	@Mock
	private DeliveryModeDao deliveryModeDao;

	@Mock
	private AbstractOrderModel order;

	@Mock
	private DeliveryModeLookupStrategy deliveryModeLookupStrategy;

	@Mock
	private DeliveryAddressesLookupStrategy deliveryAddressesLookupStrategy;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		deliveryService = new DefaultDeliveryService();
		deliveryService.setCommonI18NService(commonI18NService);
		deliveryService.setCommerceCommonI18NService(commerceCommonI18NService);
		deliveryService.setDeliveryModeDao(deliveryModeDao);
		deliveryService.setDeliveryModeLookupStrategy(deliveryModeLookupStrategy);
		deliveryService.setDeliveryAddressesLookupStrategy(deliveryAddressesLookupStrategy);
	}

	@Test
	public void testGetDeliveryCountriesEmpty()
	{
		final Collection<CountryModel> countryModels = deliveryService.getDeliveryCountriesForOrder(order);

		verify(commerceCommonI18NService, times(1)).getAllCountries();
		verify(commonI18NService, times(1)).getAllCountries();
		Assert.assertEquals(0, countryModels.size());
	}

	@Test
	public void testGetDeliveryCountriesWithCommerceCommonI18NService()
	{
		final CountryModel countryModel = mock(CountryModel.class);

		given(commerceCommonI18NService.getAllCountries()).willReturn(Collections.singletonList(countryModel));
		given(commonI18NService.getAllCountries()).willReturn(Collections.EMPTY_LIST);

		final Collection<CountryModel> countryModels = deliveryService.getDeliveryCountriesForOrder(order);

		verify(commerceCommonI18NService, times(1)).getAllCountries();
		verify(commonI18NService, times(0)).getAllCountries();
		Assert.assertEquals(countryModel, countryModels.iterator().next());
	}

	@Test
	public void testGetDeliveryCountriesWithCommonI18NService()
	{
		final CountryModel countryModel = mock(CountryModel.class);

		given(commerceCommonI18NService.getAllCountries()).willReturn(Collections.EMPTY_LIST);
		given(commonI18NService.getAllCountries()).willReturn(Collections.singletonList(countryModel));

		final Collection<CountryModel> countryModels = deliveryService.getDeliveryCountriesForOrder(order);

		verify(commerceCommonI18NService, times(1)).getAllCountries();
		verify(commonI18NService, times(1)).getAllCountries();
		Assert.assertEquals(countryModel, countryModels.iterator().next());
	}

	@Test
	public void testGetCountry()
	{
		final CountryModel countryModel = mock(CountryModel.class);
		countryModel.setIsocode("DE");

		given(commonI18NService.getCountry("DE")).willReturn(countryModel);

		final CountryModel country = deliveryService.getCountryForCode("DE");

		Assert.assertEquals(countryModel.getIsocode(), country.getIsocode());
	}

	@Test
	public void testGetSupportedDeliveryModesEmpty()
	{
		final CartModel cartModel = mock(CartModel.class);

		final Collection<DeliveryModeModel> deliveryModes = deliveryService.getSupportedDeliveryModesForOrder(cartModel);

		Assert.assertEquals(0, deliveryModes.size());
	}

	@Test
	public void testGetSupportedDeliveryModes()
	{
		final CartModel cartModel = mock(CartModel.class);
		cartModel.setCurrency(mock(CurrencyModel.class));
		final AddressModel address = mock(AddressModel.class);
		final CountryModel country = mock(CountryModel.class);
		final CurrencyModel currency = mock(CurrencyModel.class);
		final DeliveryModeModel deliveryModeModel = mock(ZoneDeliveryModeModel.class);

		given(cartModel.getCurrency()).willReturn(currency);
		given(cartModel.getDeliveryAddress()).willReturn(address);
		given(address.getCountry()).willReturn(country);
		given(deliveryModeLookupStrategy.getSelectableDeliveryModesForOrder(cartModel)).willReturn(
				new ArrayList<DeliveryModeModel>(Collections.singletonList(deliveryModeModel)));

		final Collection<DeliveryModeModel> deliveryModes = deliveryService.getSupportedDeliveryModesForOrder(cartModel);

		verify(deliveryModeLookupStrategy, times(1)).getSelectableDeliveryModesForOrder(cartModel);

		Assert.assertEquals(deliveryModeModel, deliveryModes.iterator().next());
	}

	@Test
	public void testFindDeliveryModeByCode()
	{
		final DeliveryModeModel deliveryModeModel = mock(DeliveryModeModel.class);
		deliveryModeModel.setCode("premium-net");
		given(deliveryModeDao.findDeliveryModesByCode("premium-net")).willReturn(Collections.singletonList(deliveryModeModel));

		final DeliveryModeModel deliveryMode = deliveryService.getDeliveryModeForCode("premium-net");
		Assert.assertEquals(deliveryModeModel.getCode(), deliveryMode.getCode());
	}

	@Test
	public void testGetZoneDeliveryModeValueForAbstractOrder()
	{
		final CartModel cartModel = mock(CartModel.class);
		cartModel.setCurrency(mock(CurrencyModel.class));
		final AddressModel address = mock(AddressModel.class);
		final CountryModel country = mock(CountryModel.class);
		final CurrencyModel currency = mock(CurrencyModel.class);
		final ZoneModel zone = mock(ZoneModel.class);

		given(cartModel.getCurrency()).willReturn(currency);
		given(cartModel.getDeliveryAddress()).willReturn(address);
		given(address.getCountry()).willReturn(country);

		final ZoneDeliveryModeModel deliveryModeModel = mock(ZoneDeliveryModeModel.class);
		final ZoneDeliveryModeValueModel deliveryModeValueModel = mock(ZoneDeliveryModeValueModel.class);
		given(deliveryModeModel.getValues()).willReturn(Collections.singleton(deliveryModeValueModel));
		given(deliveryModeValueModel.getCurrency()).willReturn(currency);
		given(deliveryModeValueModel.getZone()).willReturn(zone);
		given(zone.getCountries()).willReturn(Collections.singleton(country));

		final ZoneDeliveryModeValueModel modeValue = deliveryService.getZoneDeliveryModeValueForAbstractOrder(deliveryModeModel,
				cartModel);
		verify(cartModel, times(1)).getDeliveryAddress();
		verify(cartModel, times(1)).getCurrency();
		verify(deliveryModeModel, times(1)).getValues();
		verify(zone, times(1)).getCountries();
		Assert.assertEquals(deliveryModeValueModel, modeValue);
	}

}
