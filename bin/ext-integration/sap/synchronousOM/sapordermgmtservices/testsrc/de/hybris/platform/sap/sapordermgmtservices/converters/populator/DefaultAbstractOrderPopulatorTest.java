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
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.ShipToImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartFacade;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class DefaultAbstractOrderPopulatorTest
{
	/**
	 * 
	 */
	private static final BigDecimal GROSS_VALUE_EXAMPLE = new BigDecimal(900);
	/**
	 * 
	 */
	private static final BigDecimal FREIGHT_VALUE_EXAMPLE = new BigDecimal(20);
	/**
	 * 
	 */
	private static final String SHIPPING_CONDITION_EXAMPLE = "01";
	/**
	 * 
	 */
	private static final String SHIPPING_CONDITION_EXAMPLE_TEXT = "TEST";
	/**
	 * 
	 */
	private static final BigDecimal TAX_VALUE_EXAMPLE = FREIGHT_VALUE_EXAMPLE;
	/**
	 * 
	 */
	static final BigDecimal NET_VALUE_WO_FREIGHT_EXAMPLE = new BigDecimal(500);
	/**
	 * 
	 */
	private static final String PURCHASE_ORDER_EXAMPLE = "123";
	public static final Date CREATED_AT = new Date(System.currentTimeMillis());

	private final DefaultAbstractOrderPopulator<Basket, CartData> classUnderTest = new DefaultAbstractOrderPopulator<Basket, CartData>();

	@Before
	public void setUp()
	{
		injectMockedBeans(classUnderTest);
	}

	@SuppressWarnings("unchecked")
	public static void injectMockedBeans(final DefaultAbstractOrderPopulator<Basket, CartData> populator)
	{
		final BolCartFacade bolFactory = EasyMock.createMock(BolCartFacade.class);
		final Map<String, String> allowedDeliveryTypes = new HashMap<String, String>();
		allowedDeliveryTypes.put(SHIPPING_CONDITION_EXAMPLE, SHIPPING_CONDITION_EXAMPLE_TEXT);
		EasyMock.expect(bolFactory.getAllowedDeliveryTypes()).andReturn(allowedDeliveryTypes);
		EasyMock.replay(bolFactory);

		final BaseSiteModel baseSiteModel = EasyMock.createMock(BaseSiteModel.class);
		EasyMock.expect(baseSiteModel.getUid()).andReturn(" ");
		EasyMock.replay(baseSiteModel);

		final BaseSiteService baseSiteService = EasyMock.createMock(BaseSiteService.class);
		EasyMock.expect(baseSiteService.getCurrentBaseSite()).andReturn(baseSiteModel);
		EasyMock.replay(baseSiteService);

		final PriceDataFactory priceFactory = new DefaultPriceDataFactoryForTest();

		final Converter<Item, OrderEntryData> cartItemConverter = EasyMock.createMock(Converter.class);
		EasyMock.expect(cartItemConverter.convert((Item) EasyMock.anyObject())).andReturn(new OrderEntryData());
		EasyMock.replay(cartItemConverter);

		final SapPartnerService sapPartnerServiceMock = EasyMock.createNiceMock(SapPartnerService.class);
		EasyMock.expect(sapPartnerServiceMock.getHybrisAddressForSAPCustomerId("4711")).andReturn(null).anyTimes();
		EasyMock.replay(sapPartnerServiceMock);


		populator.setBolCartFacade(bolFactory);
		populator.setBaseSiteService(baseSiteService);
		populator.setPriceFactory(priceFactory);
		populator.setCartItemConverter(cartItemConverter);
		populator.setSapPartnerService(sapPartnerServiceMock);
	}

	@Test
	public void testBeanInstanciation()
	{
		Assert.assertNotNull(classUnderTest);
	}

	@Test
	public void testPopulateHeaderPrices()
	{
		final CartData target = new CartData();

		final Basket source = new BasketImpl();

		setupCart(source);

		classUnderTest.populate(source, target);

		Assert.assertEquals(NET_VALUE_WO_FREIGHT_EXAMPLE, target.getSubTotal().getValue());
		Assert.assertEquals(FREIGHT_VALUE_EXAMPLE, target.getDeliveryCost().getValue());
		Assert.assertEquals(TAX_VALUE_EXAMPLE, target.getTotalTax().getValue());
		Assert.assertEquals(GROSS_VALUE_EXAMPLE, target.getTotalPrice().getValue());
		Assert.assertEquals(SHIPPING_CONDITION_EXAMPLE, target.getDeliveryMode().getCode());
	}

	@Test
	public void testPopulateTotalItemsInitial()
	{
		final CartData target = new CartData();
		final Basket source = new BasketImpl();
		setupCart(source);

		classUnderTest.populate(source, target);
		assertEquals(new Integer(0), target.getTotalItems());
	}

	@Test
	public void testPopulateTotalItems()
	{
		final CartData target = new CartData();
		final Basket source = new BasketImpl();
		setupAndAddItem(source);
		classUnderTest.populate(source, target);
		assertEquals(new Integer(1), target.getTotalItems());
	}

	@Test
	public void testDeliveryItemsQuantity()
	{
		final CartData target = new CartData();
		final Basket source = new BasketImpl();
		setupAndAddItem(source);
		classUnderTest.populate(source, target);
		assertEquals(new Long(1), target.getDeliveryItemsQuantity());
	}


	private void setupAndAddItem(final Basket source)
	{
		setupCart(source);

		final ItemSalesDoc item = new ItemSalesDoc();
		item.setQuantity(new BigDecimal(1));

		source.getItemList().add(item);
	}

	@Test
	public void testGetBaseSiteService()
	{
		assertNotNull(classUnderTest.getBaseSiteService());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPopulateDeliveryAddress()
	{
		final CartData target = new CartData();
		final Basket source = new BasketImpl();
		setupCart(source);

		final SapPartnerService sapPartnerServiceMock = EasyMock.createNiceMock(SapPartnerService.class);
		final AddressModel addressModel = new AddressModel();
		EasyMock.expect(sapPartnerServiceMock.getHybrisAddressForSAPCustomerId("4711")).andReturn(addressModel).anyTimes();
		EasyMock.replay(sapPartnerServiceMock);

		final Converter<AddressModel, AddressData> addressConverterMock = EasyMock.createNiceMock(Converter.class);
		EasyMock.expect(addressConverterMock.convert(addressModel)).andReturn(new AddressData()).anyTimes();
		EasyMock.replay(addressConverterMock);


		classUnderTest.setSapPartnerService(sapPartnerServiceMock);
		classUnderTest.setAddressConverter(addressConverterMock);


		classUnderTest.populateDeliveryAddress(source, target);
		assertNotNull(target.getDeliveryAddress());

	}



	public static void setupCart(final SalesDocument basket)
	{
		final Header header = new HeaderSalesDocument();
		final ItemList itemList = new ItemListImpl();

		header.setShipTo(new ShipToImpl());
		header.getShipTo().setId("4711");


		basket.setHeader(header);
		basket.setItemList(itemList);
		header.setPurchaseOrderExt(PURCHASE_ORDER_EXAMPLE);
		header.setNetValueWOFreight(NET_VALUE_WO_FREIGHT_EXAMPLE);
		header.setFreightValue(FREIGHT_VALUE_EXAMPLE);
		header.setTaxValue(TAX_VALUE_EXAMPLE);
		header.setGrossValue(GROSS_VALUE_EXAMPLE);
		header.setShipCond(SHIPPING_CONDITION_EXAMPLE);
		header.setCreatedAt(CREATED_AT);

		basket.setHeader(header);
		basket.setItemList(itemList);
	}
}
