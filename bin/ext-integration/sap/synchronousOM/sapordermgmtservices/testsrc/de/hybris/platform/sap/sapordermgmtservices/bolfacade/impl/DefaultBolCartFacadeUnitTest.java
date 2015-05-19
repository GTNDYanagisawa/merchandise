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
package de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;



@SuppressWarnings("javadoc")
public class DefaultBolCartFacadeUnitTest extends SapordermanagmentBolSpringJunitTest
{
	DefaultBolCartFacade classUnderTest = new DefaultBolCartFacade();
	Item item = new ItemSalesDoc();
	private final String productId = "KDAC";

	@Before
	public void init()
	{
		final SapPartnerService partnerService = EasyMock.createMock(SapPartnerService.class);
		EasyMock.expect(partnerService.getCurrentSapCustomerId()).andReturn("A");
		EasyMock.expect(partnerService.getCurrentSapContactId()).andReturn("A");
		EasyMock.replay(partnerService);
		classUnderTest.setSapPartnerService(partnerService);
		classUnderTest.setGenericFactory(genericFactory);
		final Basket cart = classUnderTest.getCart();
		assertNotNull(cart);
		item.setNumberInt(10);
		item.setProductId(productId);
		cart.addItem(item);

	}

	@Test
	public void testCreateAddress()
	{
		final Address address = classUnderTest.createAddress();
		assertNotNull(address);
	}

	@Test
	public void testGenericFactory()
	{
		assertNotNull(classUnderTest.getGenericFactory());
	}


	@Test
	public void testCart()
	{
		final Basket cart = classUnderTest.getCart();
		assertNotNull(cart);
	}

	@Test
	public void testOrder()
	{
		final Order order = classUnderTest.getOrder();
		assertNotNull(order);
	}



	@Test
	public void testOrderHasConfig()
	{
		final Order order = classUnderTest.getOrder();
		assertNotNull(order);
		assertNotNull(order.getTransactionConfiguration());
	}

	@Test
	public void testHasCart()
	{
		final boolean hasCart = classUnderTest.hasCart().booleanValue();
		assertFalse(hasCart);
	}

	@Test
	public void testHasCartCartExists()
	{
		final Basket cart = classUnderTest.getCart();
		cart.setInitialized(true);
		final boolean hasCart = classUnderTest.hasCart().booleanValue();
		assertTrue(hasCart);
	}



	@Test
	public void testConfiguration()
	{
		final TransactionConfiguration configuration = classUnderTest.getConfiguration();
		assertNotNull(configuration);
	}








	@Test
	public void testInteractionObjectCreateOrder()
	{
		assertNotNull(classUnderTest.getInteractionObjectCreateOrder());
	}

	@Test
	public void testInteractionObjectInitBasket()
	{
		assertNotNull(classUnderTest.getInteractionObjectInitBasket());
	}

	@Test
	public void testConverter()
	{
		assertNotNull(classUnderTest.getConverter());
	}



	@Test
	public void testGetCartErrorsInitialState()
	{
		final MessageList messages = classUnderTest.getCartErrors();
		assertNotNull(messages);
		assertTrue(messages.size() == 0);
	}

	@Test
	public void testGetCartErrorsWithErrorMessage()
	{
		final Basket cart = classUnderTest.getCart();
		cart.addMessage(new Message(Message.ERROR));
		final MessageList messages = classUnderTest.getCartErrors();
		assertNotNull(messages);
		assertTrue(messages.size() == 1);
	}

	@Test
	public void testGetCartErrorsWithWarningMessage()
	{
		final Basket cart = classUnderTest.getCart();
		cart.addMessage(new Message(Message.WARNING));
		final MessageList messages = classUnderTest.getCartErrors();
		assertNotNull(messages);
		assertTrue(messages.size() == 0);
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testUpdateCart()
	{
		//cart is not initialized yet, therefore we expect an exception
		classUnderTest.updateCart();
	}




	@Test
	public void testCreateNewItem()
	{
		final String code = "A";
		final long quantity = 1;
		//unit test: We expect an exception as item hasn't been written to backend
		final String handle = classUnderTest.createNewItem(code, quantity, null, classUnderTest.getCart(), null);
		assertNotNull(handle);
		assertFalse(handle.isEmpty());
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testCheckExists()
	{
		//cart is not initialized yet, therefore we expect an exception
		classUnderTest.checkCartExists();
	}

	public void testCheckExistsInitialized()
	{
		//cart is initialized, therefore no exception must happen
		classUnderTest.getCart().setInitialized(true);
		classUnderTest.checkCartExists();
	}

	@Test
	public void testGetItemWithNumber()
	{
		final int itemNumber = 10;
		final Item item = classUnderTest.getCartItem(itemNumber);
		assertNotNull(item);
	}

	@Test
	public void testGetItemWithNumberNotExists()
	{
		final Item item = classUnderTest.getCartItem(20);
		assertNull(item);
	}

	@Test
	public void testGetCartItemWithCode()
	{
		assertNotNull(classUnderTest.getCartItem(productId));
	}

	@Test
	public void testGetCartItemWithCodeNotExists()
	{
		assertNull(classUnderTest.getCartItem(""));
	}

	@Test
	public void testReleaseCart()
	{
		classUnderTest.releaseCart();
	}

	@Test
	public void testValidateCart()
	{
		final MessageList messageList = classUnderTest.validateCart();
		assertNotNull(messageList);
		assertEquals(0, messageList.size());
	}



	@Test
	public void testGetProductIdFromConfigModel()
	{
		final ConfigModel configModel = createCfgModel();
		final String productIdNewItem = classUnderTest.getProductIdFromConfigModel(configModel);
		assertEquals(productId, productIdNewItem);
	}

	/**
	 * @return
	 */
	ConfigModel createCfgModel()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final InstanceModel rootInstance = new InstanceModelImpl();

		rootInstance.setName(productId);
		configModel.setRootInstance(rootInstance);
		return configModel;
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testUpdateConfigurationInCartItemDoesNotExist() throws CommunicationException
	{
		final ConfigModel configModel = createCfgModel();
		final String handle = "A";
		classUnderTest.updateConfigurationInCart(handle, configModel);
	}

	@Test
	public void testAddLeadingZeros()
	{
		final String productId = "1";
		assertEquals("000000000000000001", classUnderTest.formatProductIdForBOL(productId));

	}

	@Test
	public void testAddLeadingZerosAlphanumeric()
	{
		final String productId = "A";
		assertEquals(productId, classUnderTest.formatProductIdForBOL(productId));

	}

	@Test(expected = BeanCreationException.class)
	public void testIsBackendDown()
	{
		assertFalse(classUnderTest.isBackendDown());
	}

}
