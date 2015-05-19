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
package de.hybris.platform.acceleratorfacades.order.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAcceleratorCheckoutFacadeTest
{
	@Mock
	private UserService userService;
	@Mock
	private CartService cartService;
	@Mock
	private AbstractPopulatingConverter<CartModel, CartData> cartConverter;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseStoreModel baseStoreModel;
	@Mock
	private CartModel cart;

	@Mock
	private CustomerModel expressCustomerModel;
	@Mock
	private CreditCardPaymentInfoModel paymentInfoModel;

	private DefaultAcceleratorCheckoutFacade acceleratorCheckoutFacade;

	private AddressModel addressModel;
	@Mock
	private CartData cartData;

	private List<OrderEntryData> entriesList;

	private OrderEntryData entry1;

	protected static class MockAddressModel extends AddressModel
	{
		@Override
		public PK getPk()
		{
			return de.hybris.platform.core.PK.fromLong(9999l);
		}
	}

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		acceleratorCheckoutFacade = new DefaultAcceleratorCheckoutFacade();
		final DefaultCartFacade defaultCartFacade = new DefaultCartFacade();
		acceleratorCheckoutFacade.setCartService(cartService);
		acceleratorCheckoutFacade.setUserService(userService);
		acceleratorCheckoutFacade.setCartFacade(defaultCartFacade);
		acceleratorCheckoutFacade.setBaseStoreService(baseStoreService);
		defaultCartFacade.setCartService(cartService);
		defaultCartFacade.setCartConverter(cartConverter);

		entriesList = new ArrayList<OrderEntryData>();

		entry1 = Mockito.mock(OrderEntryData.class);
		entriesList.add(entry1);

		addressModel = new MockAddressModel();
		final CountryModel countryModel = new CountryModel();
		final CountryData countryData = new CountryData();
		countryData.setIsocode("PL");

		addressModel.setCountry(countryModel);

		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(baseStoreModel.getExpressCheckoutEnabled()).willReturn(Boolean.TRUE);
		given(cartData.getEntries()).willReturn(entriesList);
		given(userService.getCurrentUser()).willReturn(expressCustomerModel);
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cart);
		given(expressCustomerModel.getDefaultPaymentInfo()).willReturn(paymentInfoModel);
		given(Boolean.valueOf(paymentInfoModel.isSaved())).willReturn(Boolean.TRUE);
		given(cartConverter.convert(cart)).willReturn(cartData);

	}

	@Test
	public void testIsExpressCheckoutEnabledForStore()
	{
		Assert.assertTrue(acceleratorCheckoutFacade.isExpressCheckoutEnabledForStore());
	}

	@Test
	public void testIsExpressCheckoutDisabledForStore()
	{
		given(baseStoreModel.getExpressCheckoutEnabled()).willReturn(Boolean.FALSE);
		Assert.assertFalse(acceleratorCheckoutFacade.isExpressCheckoutEnabledForStore());
	}

	@Test
	public void testIsExpressCheckoutDisabledIsNullForStore()
	{
		given(baseStoreModel.getExpressCheckoutEnabled()).willReturn(null);
		Assert.assertFalse(acceleratorCheckoutFacade.isExpressCheckoutEnabledForStore());
	}

	@Test
	public void testCheckIfExpressCheckoutAllowedForShipping()
	{
		given(expressCustomerModel.getDefaultShipmentAddress()).willReturn(addressModel);

		acceleratorCheckoutFacade.setBaseStoreService(baseStoreService);
		Assert.assertTrue(acceleratorCheckoutFacade.isExpressCheckoutAllowedForCart());
	}

	@Test
	public void testIsExpressCheckoutNotAllowedForShipping()
	{
		final CartEntryModel entry = Mockito.mock(CartEntryModel.class);
		given(cart.getEntries()).willReturn(Arrays.asList((AbstractOrderEntryModel) entry));
		acceleratorCheckoutFacade.setBaseStoreService(baseStoreService);
		Assert.assertFalse(acceleratorCheckoutFacade.isExpressCheckoutAllowedForCart());
	}

	@Test
	public void testIsExpressCheckoutAllowedForPickupOnlyCart()
	{
		//final PointOfServiceData pos1 = Mockito.mock(PointOfServiceData.class);
		//given(pos1.getDisplayName()).willReturn("pos1");
		//given(entry1.getDeliveryPointOfService()).willReturn(pos1);

		final PointOfServiceModel pos1 = Mockito.mock(PointOfServiceModel.class);
		given(pos1.getDisplayName()).willReturn("pos1");

		final CartEntryModel entry1 = Mockito.mock(CartEntryModel.class);
		given(entry1.getDeliveryPointOfService()).willReturn(pos1);

		final CartEntryModel entry2 = Mockito.mock(CartEntryModel.class);

		given(cart.getEntries()).willReturn(Arrays.asList((AbstractOrderEntryModel) entry1));
		Assert.assertTrue(acceleratorCheckoutFacade.isExpressCheckoutAllowedForCart());

		given(cart.getEntries()).willReturn(Arrays.asList((AbstractOrderEntryModel) entry1, entry2));
		Assert.assertFalse(acceleratorCheckoutFacade.isExpressCheckoutAllowedForCart());
	}

	@Test
	public void testIsExpressCheckoutNotAllowedForPickupOnlyCart()
	{
		given(expressCustomerModel.getDefaultPaymentInfo()).willReturn(null);
		Assert.assertFalse(acceleratorCheckoutFacade.isExpressCheckoutAllowedPickupOnlyCart());
	}

	@Test
	public void shouldReturnTaxEstimationEnabled()
	{
		given(baseStoreModel.getTaxEstimationEnabled()).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(baseStoreModel.isNet())).willReturn(Boolean.TRUE);
		Assert.assertTrue(acceleratorCheckoutFacade.isTaxEstimationEnabledForCart());
	}

	@Test
	public void shouldNotReturnTaxEstimationEnabledForStoreGross()
	{
		given(baseStoreModel.getTaxEstimationEnabled()).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(baseStoreModel.isNet())).willReturn(Boolean.FALSE);
		Assert.assertFalse(acceleratorCheckoutFacade.isTaxEstimationEnabledForCart());
	}

	@Test
	public void shouldNotReturnTaxEstimationEnabled()
	{
		given(baseStoreModel.getTaxEstimationEnabled()).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(baseStoreModel.isNet())).willReturn(Boolean.TRUE);
		Assert.assertFalse(acceleratorCheckoutFacade.isTaxEstimationEnabledForCart());
	}

}
