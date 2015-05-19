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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.OrderImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListEntryImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListImpl;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Date;
import java.util.Locale;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;




@UnitTest
@SuppressWarnings(
{ "javadoc", "rawtypes", "deprecation", "unchecked" })
public class DefaultOrderPopulatorTest
{

	DefaultOrderPopulator classUnderTest = new DefaultOrderPopulator();

	@Before
	public void setUp()
	{
		LocaleUtil.setLocale(Locale.US);
		DefaultAbstractOrderPopulatorTest.injectMockedBeans(classUnderTest);
	}

	@Test
	public void testPopulateHeaderAttributesFromSuper()
	{
		final Order source = new OrderImpl();
		DefaultAbstractOrderPopulatorTest.setupCart(source);

		final OrderData target = new OrderData();
		classUnderTest.populateHeader(source, target);
		Assert.assertEquals(DefaultAbstractOrderPopulatorTest.NET_VALUE_WO_FREIGHT_EXAMPLE, target.getSubTotal().getValue());
	}

	@Test
	public void testPopulateHeaderAttributesPO()
	{
		final Order source = new OrderImpl();
		DefaultAbstractOrderPopulatorTest.setupCart(source);

		final OrderData target = new OrderData();
		classUnderTest.populateHeader(source, target);
		assertNotNull(target.getPurchaseOrderNumber());
		assertFalse(target.getPurchaseOrderNumber().isEmpty());
	}

	@Test
	public void testPopulateHeaderAttributesCreated()
	{
		final Order source = new OrderImpl();
		DefaultAbstractOrderPopulatorTest.setupCart(source);

		final OrderData target = new OrderData();
		classUnderTest.populateHeader(source, target);
		assertNotNull(target.getCreated());
		assertEquals(DefaultAbstractOrderPopulatorTest.CREATED_AT, target.getCreated());
	}

	@Test
	public void testPopulateTechKey()
	{
		final Order source = new OrderImpl();
		DefaultAbstractOrderPopulatorTest.setupCart(source);
		final String code = "X";
		source.setTechKey(new TechKey(code));
		final OrderData target = new OrderData();
		classUnderTest.populate(source, target);
		assertEquals(target.getCode(), code);
	}

	@Test
	public void testRemoveLeadingZeros()
	{
		final String orderId = "0000192837";
		assertEquals("192837", classUnderTest.getExternalFormat(orderId));
	}

	@Test
	public void testRemoveLeadingZerosAlphanumeric()
	{
		final String orderId = "X";
		assertEquals(orderId, classUnderTest.getExternalFormat(orderId));
	}


	@Test
	public void testPopulatePartners()
	{
		final Order source = createOrder();

		final OrderData target = new OrderData();

		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		b2bCustomer.setCustomerID("123");
		b2bCustomer.setEmail("test@sap.com");

		final CustomerData b2bCustomerData = new CustomerData();
		b2bCustomerData.setEmail("test@sap.com");

		final SapPartnerService sapPartnerServiceMock = EasyMock.createNiceMock(SapPartnerService.class);
		EasyMock.expect(sapPartnerServiceMock.getB2BCustomerForSapContactId("123")).andReturn(b2bCustomer).anyTimes();
		EasyMock.replay(sapPartnerServiceMock);

		final Converter b2bCustomerConverterMock = EasyMock.createNiceMock(Converter.class);
		EasyMock.expect(b2bCustomerConverterMock.convert(b2bCustomer)).andReturn(b2bCustomerData).anyTimes();
		EasyMock.replay(b2bCustomerConverterMock);

		classUnderTest.setSapPartnerService(sapPartnerServiceMock);
		classUnderTest.setB2bCustomerConverter(b2bCustomerConverterMock);

		classUnderTest.populatePartners(source, target);

		assertEquals("test@sap.com", target.getB2bCustomerData().getEmail());

	}


	private Order createOrder()
	{
		final Order order = new OrderImpl();
		final Header header = new HeaderSalesDocument();
		final PartnerList partnerList = new PartnerListImpl();
		final PartnerListEntry partnerListEntrySoldTo = new PartnerListEntryImpl();
		partnerListEntrySoldTo.setPartnerId("4711");
		partnerList.setSoldToData(partnerListEntrySoldTo);

		final PartnerListEntry partnerListEntryContact = new PartnerListEntryImpl();
		partnerListEntryContact.setPartnerId("123");
		partnerList.setContactData(partnerListEntryContact);

		header.setPartnerList(partnerList);
		order.setHeader(header);

		return order;
	}



	@Test
	public void testConvertDate()
	{
		final Date date = new Date(113, 2, 13);
		final String dateAsString = classUnderTest.convertDateToLongDateString(date);
		assertEquals("03/13/2013", dateAsString);

	}

}
