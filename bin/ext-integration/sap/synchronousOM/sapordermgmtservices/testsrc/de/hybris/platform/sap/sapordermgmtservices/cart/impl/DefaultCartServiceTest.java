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
package de.hybris.platform.sap.sapordermgmtservices.cart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.OrderMgmtMessage;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartFacade;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultAbstractOrderEntryPopulator;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultCartPopulator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class DefaultCartServiceTest extends SapordermanagmentBolSpringJunitTest
{
	private final DefaultCartService classUnderTest = new DefaultCartService();
	private Basket cart = null;
	BolCartFacade bolCartFacade = null;
	private String productId;
	private final Item newItem = new ItemSalesDoc();
	final long quantity = 1;
	private final ConfigModel configModel = new ConfigModelImpl();
	private final String itemKey = "A";
	private final MessageList messageList = new MessageList();

	@Before
	public void init()
	{
		cart = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);
		productId = "A";
		newItem.setQuantity(new BigDecimal(quantity));
		newItem.setProductId(productId);
		newItem.setTechKey(new TechKey(itemKey));



		cart.setHeader(new HeaderSalesDocument());
		final ItemListImpl itemList = new ItemListImpl();
		itemList.add(newItem);
		cart.setItemList(itemList);

		bolCartFacade = EasyMock.createMock(BolCartFacade.class);

		EasyMock.expect(bolCartFacade.addToCart(productId, quantity)).andReturn(newItem);
		EasyMock.expect(bolCartFacade.validateCart()).andReturn(messageList);
		EasyMock.expect(bolCartFacade.getCart()).andReturn(cart).anyTimes();
		EasyMock.expect(bolCartFacade.hasCart()).andReturn(new Boolean(false));
		EasyMock.expect(bolCartFacade.createCart()).andReturn(cart);
		EasyMock.expect(bolCartFacade.addConfigurationToCart(configModel)).andReturn(itemKey);
		bolCartFacade.updateConfigurationInCart("A", configModel);
		final Map<String, String> deliveryTypes = new HashMap<>();
		EasyMock.expect(bolCartFacade.getAllowedDeliveryTypes()).andReturn(deliveryTypes);
		bolCartFacade.releaseCart();
		EasyMock.replay(bolCartFacade);
		classUnderTest.setBolCartFacade(bolCartFacade);

		classUnderTest.setCartItemConverter(createCartItemConverter());
		classUnderTest.setCartConverter(createCartConverter());

	}


	private Converter<Basket, CartData> createCartConverter()
	{
		final Converter<Basket, CartData> converter = new AbstractConverter<Basket, CartData>()
		{


			@Override
			protected CartData createTarget()
			{
				return new CartData();
			}


			@Override
			public void populate(final Basket source, final CartData target)
			{

				final DefaultCartPopulator<Basket, CartData> populator = new DefaultCartPopulator<>();
				populator.setBolCartFacade(bolCartFacade);
				populator.setPriceFactory(createPriceFactory());
				populator.populate(source, target);
			}


		};
		return converter;
	}


	private Converter<Item, OrderEntryData> createCartItemConverter()
	{
		final Converter<Item, OrderEntryData> converter = new AbstractConverter<Item, OrderEntryData>()
		{


			@Override
			protected OrderEntryData createTarget()
			{
				return new OrderEntryData();
			}

			@Override
			public void populate(final Item source, final OrderEntryData target)
			{
				final DefaultAbstractOrderEntryPopulator populator = new DefaultAbstractOrderEntryPopulator();
				populator.setPriceFactory(createPriceFactory());
				populator.populate(source, target);

			}


		};
		return converter;
	}

	private PriceDataFactory createPriceFactory()
	{
		return new PriceDataFactory()
		{

			@Override
			public PriceData create(final PriceDataType priceType, final BigDecimal value, final CurrencyModel currency)
			{
				return new PriceData();
			}

			@Override
			public PriceData create(final PriceDataType priceType, final BigDecimal value, final String currencyIso)
			{
				return new PriceData();
			}
		};
	}

	@Test
	public void testSessionCart()
	{
		assertNotNull(classUnderTest.getSessionCart());
	}

	@Test
	public void testSessionCartInvertSorting()
	{
		final boolean recentlyAddedFirst = true;
		assertNotNull(classUnderTest.getSessionCart(recentlyAddedFirst));
	}

	@Test
	public void testForDeletionQty1()
	{

		final Item itemToUpdate = createItem();
		classUnderTest.checkForDeletion(BigDecimal.ONE, itemToUpdate);
		assertEquals(itemToUpdate.getProductId(), productId);
	}

	@Test
	public void testForDeletionQty0()
	{

		final Item itemToUpdate = createItem();
		classUnderTest.checkForDeletion(BigDecimal.ZERO, itemToUpdate);
		assertEquals(itemToUpdate.getProductId(), "");
	}

	protected Item createItem()
	{
		final Item itemToUpdate = new ItemSalesDoc();

		itemToUpdate.setProductId(productId);
		return itemToUpdate;
	}

	@Test
	public void testConvertToPositiveInt()
	{
		final long quantity = 0;
		final int positiveInt = classUnderTest.convertToPositiveInt(quantity);
		assertTrue(positiveInt == 0);
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testConvertToPositiveIntNegativeQty()
	{
		final long quantity = -1;
		classUnderTest.convertToPositiveInt(quantity);
	}

	@Test(expected = ArithmeticException.class)
	public void testConvertToPositiveIntMaxLong()
	{
		final long quantity = Long.MAX_VALUE;
		classUnderTest.convertToPositiveInt(quantity);
	}

	@Test
	public void testConvertQty()
	{
		final long quantity = 3;
		final BigDecimal qty = classUnderTest.convertQuantity(quantity);
		assertEquals(qty, new BigDecimal(3));
	}

	@Test
	public void testConvertQtyNegative()
	{
		final long quantity = -3;
		final BigDecimal qty = classUnderTest.convertQuantity(quantity);
		assertEquals(qty, BigDecimal.ZERO);
	}

	@Test
	public void testConvertQtyZero()
	{
		final long quantity = 0;
		final BigDecimal qty = classUnderTest.convertQuantity(quantity);
		assertEquals(qty, BigDecimal.ZERO);
	}

	@Test
	public void testValidateCart()
	{
		final List<CartModificationData> cartModification = classUnderTest.validateCartData();
		assertNotNull(cartModification);
	}

	@Test
	public void testValidateCartStatusWithNoMessages()
	{
		cart.clearMessages();
		final List<CartModificationData> cartModification = classUnderTest.validateCartData();
		assertNotNull(cartModification);
		assertTrue(cartModification.size() == 0);
	}

	@Test
	public void testValidateCartStatusWithMessages()
	{
		final Message message = new Message(Message.ERROR);
		cart.addMessage(message);
		final List<CartModificationData> cartModification = classUnderTest.validateCartData();
		assertNotNull(cartModification);

		assertTrue(cartModification.size() == 0);
	}

	@Test
	public void testValidateCartStatusWithCheckoutMessage()
	{
		final OrderMgmtMessage message = new OrderMgmtMessage(Message.ERROR);
		message.setProcessStep("CH");
		messageList.add(message);

		final List<CartModificationData> cartModification = classUnderTest.validateCartData();
		assertNotNull(cartModification);

		assertTrue(cartModification.size() == 0);

		messageList.clear();
	}

	@Test
	public void testAddToCart()
	{
		final CartModificationData modification = classUnderTest.addToCart(productId, quantity);
		assertNotNull(modification);
		assertTrue(modification.getQuantity() == quantity);
		assertTrue(modification.getQuantityAdded() == quantity);
	}

	@Test
	public void testAddToCartEntry()
	{
		final CartModificationData modification = classUnderTest.addToCart(productId, quantity);
		assertNotNull(modification);
		final OrderEntryData entry = modification.getEntry();
		assertNotNull(entry);
		assertEquals(productId, entry.getProduct().getCode());
		assertEquals(new Long(quantity), entry.getQuantity());
	}

	@Test
	public void testRemoveSessionCart()
	{
		classUnderTest.removeSessionCart();
		assertFalse(classUnderTest.getBolCartFacade().getCart().isInitialized());
	}

	@Test
	public void testEmptyCart()
	{
		final CartData emptyCart = classUnderTest.createEmptyCart();
		assertNotNull(emptyCart);
		final List<OrderEntryData> entries = emptyCart.getEntries();
		assertNotNull(entries);
		assertEquals(0, entries.size());
	}

	@Test
	public void testAddConfigurationToCart()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final String itemKey = classUnderTest.addConfigurationToCart(configModel);
		assertNotNull(itemKey);
	}

	@Test
	public void testUpdateConfigurationInCart()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final String key = "A";
		classUnderTest.updateConfigurationInCart(key, configModel);
	}

	@Test
	public void testIsItemAvailable()
	{
		final boolean available = classUnderTest.isItemAvailable(itemKey);
		assertTrue(available);
	}

	@Test
	public void testIsItemAvailableUnknownKey()
	{
		final boolean available = classUnderTest.isItemAvailable("X");
		assertFalse(available);
	}


}
