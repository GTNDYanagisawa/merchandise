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
package de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.interf.BasketBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.interaction.interf.CreateOrder;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.OrderBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.OrderImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.SalesDocumentBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.SalesDocumentType;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class CreateOrderImplTest extends SapordermanagmentBolSpringJunitTest
{
	private CreateOrder classUnderTest;
	private BasketImpl source;
	private TransactionConfiguration transConf;
	private OrderImpl target;

	private OrderBackend mockedOrderBackend;
	private BasketBackend mockedBasketBackend;
	private SalesDocumentBackend mockedSalesDocumentBackend;
	private final TechKey basketTechKey = new TechKey("Basket");

	@Override
	@Before
	public void setUp()
	{
		transConf = (TransactionConfiguration) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_TRANSACTION_CONFIGURATION);
		source = (BasketImpl) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);
		classUnderTest = (CreateOrder) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_INT_CREATE_ORDER);
		target = (OrderImpl) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_ORDER);
		mockedOrderBackend = EasyMock.createMock(OrderBackend.class);
		mockedBasketBackend = EasyMock.createMock(BasketBackend.class);
		mockedSalesDocumentBackend = EasyMock.createMock(SalesDocumentBackend.class);

		EasyMock.expect(mockedOrderBackend.getSalesDocumentType()).andReturn(SalesDocumentType.ORDER).anyTimes();
		mockedOrderBackend.setLoadStateCreate();
		EasyMock.expectLastCall().anyTimes();

		mockedOrderBackend.clearItemBuffer();
		EasyMock.expectLastCall().anyTimes();

		target.setBackendService(mockedOrderBackend);
		source.setBackendService(mockedBasketBackend);

		source.setTransactionConfiguration(transConf);
		target.setTransactionConfiguration(transConf);

		source.setTechKey(basketTechKey);
		target.setUpdateMissing(false);
	}

	@Override
	public void tearDown()
	{
		EasyMock.verify(mockedOrderBackend);

		// The instances must not be copied
		assertFalse(source.getHeader() == target.getHeader());
		assertFalse(source.getItemList() == target.getItemList());

	}

	private void replay()
	{
		EasyMock.replay(mockedOrderBackend);
		EasyMock.replay(mockedBasketBackend);
		EasyMock.replay(mockedSalesDocumentBackend);

	}

	@Test
	public void testInternalToOrder() throws BusinessObjectException, BackendException
	{
		prepareBasketToBeUpToDate();
		prepareBasketAsBackendBasket();
		replay();

		classUnderTest.createOrderFromBasket(source, target, false, transConf);

		assertFalse("Backend basket and order are in synch, no update needed", target.isUpdateMissing());
		assertEquals("Order must be linked to basket", basketTechKey, target.getBasketId());
	}


	private void prepareBasketAsBackendBasket()
	{
		source.setExternalToOrder(false);
	}

	private void prepareBasketToBeUpToDate()
	{
		source.setDirty(false);
	}





}
