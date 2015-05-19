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
package de.hybris.platform.sap.sapordermgmtb2bfacades.checkout.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.checkout.impl.DefaultCheckoutService;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
@UnitTest
public class DefaultB2BCheckoutFacadeTest
{

	private DefaultB2BCheckoutFacade classUnderTest;
	private final String addressId = "A";
	private final AddressData addressData = new AddressData();
	private BackendAvailabilityService backendAvailabilityService;

	@Before
	public void setUp()
	{
		classUnderTest = new DefaultB2BCheckoutFacade();
		final Converter<AddressModel, AddressData> addressConverter = EasyMock.createMock(Converter.class);
		final DefaultB2BCheckoutFacade b2bCheckoutFacade = EasyMock.createMock(DefaultB2BCheckoutFacade.class);
		EasyMock.expect(b2bCheckoutFacade.getAddressDataForId(addressId, true)).andReturn(addressData);

		EasyMock.expect(b2bCheckoutFacade.isBackendDown()).andReturn(false);

		backendAvailabilityService = EasyMock.createMock(BackendAvailabilityService.class);
		EasyMock.expect(backendAvailabilityService.isBackendDown()).andReturn(false);

		EasyMock.replay(addressConverter, b2bCheckoutFacade, backendAvailabilityService);
		classUnderTest.setAddressConverter(addressConverter);
		classUnderTest.setB2bCheckoutFacade(b2bCheckoutFacade);
		classUnderTest.setBackendAvailabilityService(backendAvailabilityService);
	}

	@Test
	public void testSetDeliveryAddress_returnFalse()
	{
		final AddressData usedAddress = new AddressData();
		usedAddress.setEmail("test@sap.com");

		final Collection<AddressModel> allowedDeliveryAddresses = new ArrayList<AddressModel>();

		final SapPartnerService sapPartnerServiceMock = EasyMock.createNiceMock(SapPartnerService.class);
		EasyMock.expect(sapPartnerServiceMock.getAllowedDeliveryAddresses()).andReturn(allowedDeliveryAddresses).anyTimes();
		EasyMock.replay(sapPartnerServiceMock);

		classUnderTest.setSapPartnerService(sapPartnerServiceMock);
		assertFalse(classUnderTest.setDeliveryAddress(usedAddress));
	}

	@Test
	public void testSetDeliveryAddress_returnTrue()
	{
		final AddressData usedAddress = new AddressData();
		usedAddress.setEmail("test@sap.com");


		final PK pk123 = PK.createFixedUUIDPK(0, 123);
		final PK pk124 = PK.createFixedUUIDPK(0, 124);

		usedAddress.setId(pk123.toString());


		final Collection<AddressModel> allowedDeliveryAddresses = new ArrayList<AddressModel>();
		final AddressModel address1mock = EasyMock.createNiceMock(AddressModel.class);
		EasyMock.expect(address1mock.getPk()).andReturn(pk123).anyTimes();
		EasyMock.expect(address1mock.getSapCustomerID()).andReturn("123").anyTimes();
		EasyMock.replay(address1mock);
		final AddressModel address2mock = EasyMock.createNiceMock(AddressModel.class);
		EasyMock.expect(address2mock.getPk()).andReturn(pk124).anyTimes();
		EasyMock.replay(address2mock);

		allowedDeliveryAddresses.add(address1mock);
		allowedDeliveryAddresses.add(address2mock);


		final SapPartnerService sapPartnerServiceMock = EasyMock.createNiceMock(SapPartnerService.class);
		EasyMock.expect(sapPartnerServiceMock.getAllowedDeliveryAddresses()).andReturn(allowedDeliveryAddresses).anyTimes();
		EasyMock.replay(sapPartnerServiceMock);

		final DefaultCheckoutService checkoutServiceMock = EasyMock.createNiceMock(DefaultCheckoutService.class);

		EasyMock.expect(checkoutServiceMock.setDeliveryAddress("123")).andReturn(true);
		EasyMock.replay(checkoutServiceMock);

		classUnderTest.setCheckoutService(checkoutServiceMock);
		classUnderTest.setSapPartnerService(sapPartnerServiceMock);
		assertTrue(classUnderTest.setDeliveryAddress(usedAddress));
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testAuthorizePayment()
	{
		final String securityCode = "code";
		classUnderTest.authorizePayment(securityCode);
	}

	@Test
	public void testContainsTaxValues()
	{
		final boolean containsTaxValues = classUnderTest.containsTaxValues();
		//prices from ERP always contain taxes
		assertTrue(containsTaxValues);
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testCreateCartFromOrder()
	{
		//this is not yet in scope
		classUnderTest.createCartFromOrder("A");
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testCreatePaymentSubscription()
	{
		final CCPaymentInfoData paymentInfoData = null;
		//this is not yet in scope
		classUnderTest.createPaymentSubscription(paymentInfoData);
	}

	@Test
	public void testActiveVisibleCostCenters()
	{
		assertEquals(Collections.EMPTY_LIST, classUnderTest.getActiveVisibleCostCenters());
	}

	@Test
	public void testAddressConverter()
	{
		assertNotNull(classUnderTest.getAddressConverter());
	}

	@Test
	public void testAddressDataForID()
	{
		//test proper delegation
		final boolean visibleAddressesOnly = true;
		final AddressData addressDataForId = classUnderTest.getAddressDataForId(addressId, visibleAddressesOnly);
		assertEquals(addressData, addressDataForId);
	}
}
