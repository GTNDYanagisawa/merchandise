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
package de.hybris.platform.sap.sapordermgmtb2bservices.partner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class DefaultSapPartnerServiceTest
{

	private DefaultSAPPartnerService classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new DefaultSAPPartnerService();
	}

	@Test
	public void testConstructor()
	{
		assertNotNull(classUnderTest);
	}

	@Test
	public void testGetCurrentSapCustomerId()
	{
		final B2BCustomerService b2bCustomerServiceMock = EasyMock.createNiceMock(B2BCustomerService.class);
		final B2BUnitService b2bUnitServiceMock = EasyMock.createNiceMock(B2BUnitService.class);
		final B2BUnitModel b2bUnitModelMock = EasyMock.createNiceMock(B2BUnitModel.class);
		final CompanyModel rootB2bUnitMock = EasyMock.createNiceMock(CompanyModel.class);
		final B2BCustomerModel b2bCustomerMock = EasyMock.createNiceMock(B2BCustomerModel.class);

		EasyMock.expect(b2bCustomerServiceMock.getCurrentB2BCustomer()).andReturn(b2bCustomerMock).anyTimes();
		EasyMock.expect(b2bUnitServiceMock.getParent(b2bCustomerMock)).andReturn(b2bUnitModelMock).anyTimes();
		EasyMock.expect(b2bUnitServiceMock.getRootUnit(b2bUnitModelMock)).andReturn(rootB2bUnitMock).anyTimes();


		EasyMock.expect(b2bUnitModelMock.getUid()).andReturn("4711").anyTimes();
		EasyMock.expect(rootB2bUnitMock.getUid()).andReturn("4711").anyTimes();

		EasyMock.replay(b2bCustomerServiceMock);
		EasyMock.replay(b2bUnitServiceMock);
		EasyMock.replay(b2bUnitModelMock);
		EasyMock.replay(rootB2bUnitMock);
		EasyMock.replay(b2bCustomerMock);

		classUnderTest.setB2bCustomerService(b2bCustomerServiceMock);
		classUnderTest.setB2bUnitService(b2bUnitServiceMock);


		assertNotNull(classUnderTest.getCurrentSapCustomerId());
		assertEquals("4711", classUnderTest.getCurrentSapCustomerId());
	}

	@Test
	public void testGetCurrentSapContactId()
	{
		final B2BCustomerService b2bCustomerServiceMock = EasyMock.createNiceMock(B2BCustomerService.class);
		final B2BCustomerModel b2bCustomerMock = EasyMock.createNiceMock(B2BCustomerModel.class);

		EasyMock.expect(b2bCustomerServiceMock.getCurrentB2BCustomer()).andReturn(b2bCustomerMock).anyTimes();
		EasyMock.expect(b2bCustomerMock.getCustomerID()).andReturn("4712").anyTimes();

		EasyMock.replay(b2bCustomerServiceMock);
		EasyMock.replay(b2bCustomerMock);

		classUnderTest.setB2bCustomerService(b2bCustomerServiceMock);


		assertNotNull(classUnderTest.getCurrentSapContactId());
		assertEquals("4712", classUnderTest.getCurrentSapContactId());
	}


	@Test
	public void testGetHybrisAddressForSAPCustomerId()
	{
		final B2BCustomerService b2bCustomerServiceMock = EasyMock.createNiceMock(B2BCustomerService.class);
		final B2BUnitService b2bUnitServiceMock = EasyMock.createNiceMock(B2BUnitService.class);
		final B2BUnitModel b2bUnitModelMock = EasyMock.createNiceMock(B2BUnitModel.class);
		final B2BCustomerModel b2bCustomerMock = EasyMock.createNiceMock(B2BCustomerModel.class);

		final Collection<AddressModel> addresses = new ArrayList<AddressModel>();
		final AddressModel address1 = new AddressModel();
		address1.setSapCustomerID("not4711");
		final AddressModel address2 = new AddressModel();
		address2.setSapCustomerID("4711");
		addresses.add(address1);
		addresses.add(address2);


		EasyMock.expect(b2bCustomerServiceMock.getCurrentB2BCustomer()).andReturn(b2bCustomerMock).anyTimes();
		EasyMock.expect(b2bUnitServiceMock.getParent(b2bCustomerMock)).andReturn(b2bUnitModelMock).anyTimes();
		EasyMock.expect(b2bUnitModelMock.getAddresses()).andReturn(addresses).anyTimes();


		EasyMock.replay(b2bCustomerServiceMock);
		EasyMock.replay(b2bUnitServiceMock);
		EasyMock.replay(b2bUnitModelMock);
		EasyMock.replay(b2bCustomerMock);

		classUnderTest.setB2bCustomerService(b2bCustomerServiceMock);
		classUnderTest.setB2bUnitService(b2bUnitServiceMock);


		assertNotNull(classUnderTest.getHybrisAddressForSAPCustomerId("4711"));
		assertSame(address2, classUnderTest.getHybrisAddressForSAPCustomerId("4711"));
	}


	@Test
	public void testGetAllowedDeliveryAddresses()
	{
		final B2BCustomerService b2bCustomerServiceMock = EasyMock.createNiceMock(B2BCustomerService.class);
		final B2BUnitService b2bUnitServiceMock = EasyMock.createNiceMock(B2BUnitService.class);
		final B2BUnitModel b2bUnitModelMock = EasyMock.createNiceMock(B2BUnitModel.class);
		final B2BCustomerModel b2bCustomerMock = EasyMock.createNiceMock(B2BCustomerModel.class);

		final Collection<AddressModel> addresses = new ArrayList<AddressModel>();
		final AddressModel address1 = new AddressModel();
		final AddressModel address2 = new AddressModel();
		addresses.add(address1);
		addresses.add(address2);


		EasyMock.expect(b2bCustomerServiceMock.getCurrentB2BCustomer()).andReturn(b2bCustomerMock).anyTimes();
		EasyMock.expect(b2bUnitServiceMock.getParent(b2bCustomerMock)).andReturn(b2bUnitModelMock).anyTimes();
		EasyMock.expect(b2bUnitModelMock.getShippingAddresses()).andReturn(addresses).anyTimes();


		EasyMock.replay(b2bCustomerServiceMock);
		EasyMock.replay(b2bUnitServiceMock);
		EasyMock.replay(b2bUnitModelMock);
		EasyMock.replay(b2bCustomerMock);

		classUnderTest.setB2bCustomerService(b2bCustomerServiceMock);
		classUnderTest.setB2bUnitService(b2bUnitServiceMock);


		assertNotNull(classUnderTest.getAllowedDeliveryAddresses());
		assertSame(addresses, classUnderTest.getAllowedDeliveryAddresses());
	}

	@Test
	public void testGetB2BCustomerForSapContactId_assertB2BReturned()
	{
		final B2BCustomerService b2bCustomerServiceMock = EasyMock.createNiceMock(B2BCustomerService.class);
		final B2BUnitService b2bUnitServiceMock = EasyMock.createNiceMock(B2BUnitService.class);
		final B2BUnitModel parentB2bUnitModelMock = EasyMock.createNiceMock(B2BUnitModel.class);
		final B2BUnitModel rootB2bUnitModelMock = EasyMock.createNiceMock(B2BUnitModel.class);
		final B2BCustomerModel b2bCustomerMock = EasyMock.createNiceMock(B2BCustomerModel.class);

		final Set b2bCustomers = new HashSet<Object>();

		final B2BCustomerModel b2bCustomer1 = new B2BCustomerModel();
		b2bCustomer1.setCustomerID("123");
		final B2BCustomerModel b2bCustomer2 = new B2BCustomerModel();
		b2bCustomer2.setCustomerID("not123");

		b2bCustomers.add(b2bCustomer1);
		b2bCustomers.add(b2bCustomer2);



		EasyMock.expect(b2bCustomerServiceMock.getCurrentB2BCustomer()).andReturn(b2bCustomerMock).anyTimes();

		EasyMock.expect(b2bUnitServiceMock.getParent(b2bCustomerMock)).andReturn(parentB2bUnitModelMock).anyTimes();
		EasyMock.expect(b2bUnitServiceMock.getRootUnit(parentB2bUnitModelMock)).andReturn(rootB2bUnitModelMock).anyTimes();
		EasyMock.expect(b2bUnitServiceMock.getB2BCustomers(parentB2bUnitModelMock)).andReturn(b2bCustomers).anyTimes();

		EasyMock.replay(b2bCustomerServiceMock);
		EasyMock.replay(b2bUnitServiceMock);
		EasyMock.replay(parentB2bUnitModelMock);
		EasyMock.replay(rootB2bUnitModelMock);

		classUnderTest.setB2bCustomerService(b2bCustomerServiceMock);
		classUnderTest.setB2bUnitService(b2bUnitServiceMock);

		final B2BCustomerModel returnedB2bCustomer = classUnderTest.getB2BCustomerForSapContactId("123");

		assertNotNull(returnedB2bCustomer);
		assertEquals("123", returnedB2bCustomer.getCustomerID());

	}

	@Test
	public void testGetB2BCustomerForSapContactId_assertNoB2BCustomerReturned()
	{
		final B2BCustomerService b2bCustomerServiceMock = EasyMock.createNiceMock(B2BCustomerService.class);
		final B2BUnitService b2bUnitServiceMock = EasyMock.createNiceMock(B2BUnitService.class);
		final B2BUnitModel parentB2bUnitModelMock = EasyMock.createNiceMock(B2BUnitModel.class);
		final B2BUnitModel rootB2bUnitModelMock = EasyMock.createNiceMock(B2BUnitModel.class);
		final B2BCustomerModel b2bCustomerMock = EasyMock.createNiceMock(B2BCustomerModel.class);

		final Set b2bCustomers = new HashSet<Object>();

		final B2BCustomerModel b2bCustomer1 = new B2BCustomerModel();
		b2bCustomer1.setCustomerID("not123");
		final B2BCustomerModel b2bCustomer2 = new B2BCustomerModel();
		b2bCustomer2.setCustomerID("not123");

		b2bCustomers.add(b2bCustomer1);
		b2bCustomers.add(b2bCustomer2);



		EasyMock.expect(b2bCustomerServiceMock.getCurrentB2BCustomer()).andReturn(b2bCustomerMock).anyTimes();

		EasyMock.expect(b2bUnitServiceMock.getParent(b2bCustomerMock)).andReturn(parentB2bUnitModelMock).anyTimes();
		EasyMock.expect(b2bUnitServiceMock.getRootUnit(parentB2bUnitModelMock)).andReturn(rootB2bUnitModelMock).anyTimes();
		EasyMock.expect(b2bUnitServiceMock.getB2BCustomers(parentB2bUnitModelMock)).andReturn(b2bCustomers).anyTimes();

		EasyMock.replay(b2bCustomerServiceMock);
		EasyMock.replay(b2bUnitServiceMock);
		EasyMock.replay(parentB2bUnitModelMock);
		EasyMock.replay(rootB2bUnitModelMock);

		classUnderTest.setB2bCustomerService(b2bCustomerServiceMock);
		classUnderTest.setB2bUnitService(b2bUnitServiceMock);

		final B2BCustomerModel returnedB2bCustomer = classUnderTest.getB2BCustomerForSapContactId("123");

		assertNull(returnedB2bCustomer);

	}

}
