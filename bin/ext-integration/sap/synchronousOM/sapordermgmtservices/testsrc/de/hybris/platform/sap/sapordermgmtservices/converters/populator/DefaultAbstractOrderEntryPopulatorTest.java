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

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.math.BigDecimal;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class DefaultAbstractOrderEntryPopulatorTest
{

	final Item item = new ItemSalesDoc();
	final String productId = "1";
	final String internalProductId = "01";

	/**
	 * 
	 */
	private static final String USD = "USD";
	/**
	 * 
	 */
	private static final String TEST_PRODUCT_ID = "TestProductID";
	/**
	 * 
	 */
	private static final int NUMBER_INT = 1;
	/**
	 * 
	 */
	private static final BigDecimal QUANTITY = new BigDecimal(15);
	/**
	 * 
	 */
	private static final BigDecimal GROSS_VALUE = new BigDecimal(8);
	/**
	 * 
	 */
	private static final BigDecimal NET_VALUE_WO_FREIGHT = new BigDecimal(5);

	private final DefaultAbstractOrderEntryPopulator classUnderTest = new DefaultAbstractOrderEntryPopulator();

	@Before
	public void setUp()
	{
		final Converter converter = EasyMock.createMock(Converter.class);
		try
		{
			EasyMock.expect(converter.getCurrencyScale(USD)).andReturn(new Integer(5)).anyTimes();
		}
		catch (final BusinessObjectException e)
		{
			e.printStackTrace();
		}
		EasyMock.replay(converter);


		final PriceDataFactory priceFactory = new DefaultPriceDataFactoryForTest();

		classUnderTest.setPriceFactory(priceFactory);
	}

	@Test
	public void testBeanInstanciation()
	{
		Assert.assertNotNull(classUnderTest);
	}

	@Test
	public void testPopulateAbstractOrderEntryPopulator()
	{
		final OrderEntryData target = new OrderEntryData();

		final Item item = new ItemSalesDoc();

		item.setNetValueWOFreight(NET_VALUE_WO_FREIGHT);
		item.setGrossValue(GROSS_VALUE);
		item.setQuantity(QUANTITY);
		item.setNumberInt(NUMBER_INT);
		item.setProductId(TEST_PRODUCT_ID);
		item.setCurrency(USD);


		classUnderTest.populate(item, target);

		final OrderEntryData orderEntry = target;
		Assert.assertEquals(orderEntry.getBasePrice().getValue(), NET_VALUE_WO_FREIGHT);
		Assert.assertEquals(orderEntry.getTotalPrice().getValue(), GROSS_VALUE);
		Assert.assertEquals(orderEntry.getQuantity(), new java.lang.Long(15));
		Assert.assertEquals(orderEntry.getEntryNumber(), new Integer(NUMBER_INT));
		Assert.assertEquals(orderEntry.getProduct().getCode(), TEST_PRODUCT_ID);
	}

	@Test
	public void testCreateProductItemFromERP()
	{
		item.setProductId(productId);
		item.setProductGuid(new TechKey(internalProductId));
		final ProductData productData = classUnderTest.createProductFromItem(item);
		assertEquals(productId, productData.getName());
		assertEquals(productId, productData.getCode());
	}


	@Test
	public void testCreateProductItemDoesNotExistInERP()
	{
		item.setProductId(productId);
		item.setProductGuid(TechKey.EMPTY_KEY);
		final ProductData productData = classUnderTest.createProductFromItem(item);
		checkIdIsSet(productData);
	}


	@Test
	public void testCreateProductItemWOTechKey()
	{
		item.setProductId(productId);
		item.setProductGuid(null);
		final ProductData productData = classUnderTest.createProductFromItem(item);
		checkIdIsSet(productData);
	}

	void checkIdIsSet(final ProductData productData)
	{
		assertEquals(productId, productData.getName());
		assertEquals(productId, productData.getCode());
	}

	@Test
	public void testFormatProductId()
	{
		String productId = "000000000000000001";
		assertEquals("1", classUnderTest.formatProductIdForHybris(productId));
		productId = "01";
		assertEquals("1", classUnderTest.formatProductIdForHybris(productId));
	}

	@Test
	public void testFormatProductIdAlphaNum()
	{
		final String productId = "KD990KAP";
		assertEquals(productId, classUnderTest.formatProductIdForHybris(productId));
	}

}
