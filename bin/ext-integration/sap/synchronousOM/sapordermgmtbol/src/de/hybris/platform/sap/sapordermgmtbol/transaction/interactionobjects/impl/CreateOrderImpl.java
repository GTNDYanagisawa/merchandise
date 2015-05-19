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

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.interaction.interf.CreateOrder;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;

import java.util.ArrayList;


/**
 * This interaction object is used to create an order out of a another document (e.g. basket). <br>
 * 
 */
public class CreateOrderImpl implements CreateOrder
{

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(CreateOrderImpl.class.getName());

	private GenericFactory genericFactory;




	/**
	 * @param genericFactory
	 *           Factory to access SAP session beans
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}


	@Override
	public void createOrderFromBasket(final Basket basket, final Order order, final boolean isBasketLinkedToOrder,
			final TransactionConfiguration transConf) throws CommunicationException, BusinessObjectException
	{
		sapLogger.entering("createOrderFromBasket()");

		try
		{


			prepareBasket(basket);
			// this is a backend basket
			// fill order with basket data
			copyFromDocument(basket, order);


		}

		finally
		{
			sapLogger.exiting();
		}

	}


	/**
	 * Sets up the basket, so that is ready for copying.
	 * 
	 * @param basket
	 * @throws CommunicationException
	 */
	protected void prepareBasket(final Basket basket) throws CommunicationException
	{

		sapLogger.entering("setUpBasket()");

		try
		{
			if (basket.isUpdateMissing())
			{
				basket.update();
			}
			// if necessary, read items and shiptos from backend to
			// ensure consistent data for the order
			basket.read();

		}

		finally
		{
			sapLogger.exiting();
		}

	}

	/**
	 * Copies the basket's properties to the order object
	 * 
	 * @param salesDoc
	 *           SalesDocument (e.g. a basket) which is used as source
	 * @param order
	 *           copy destination
	 * @throws CommunicationException
	 *            an exception occured
	 * @throws BusinessObjectException
	 *            an exception occured
	 */
	protected void copyFromDocument(final SalesDocument salesDoc, final Order order) throws CommunicationException,
			BusinessObjectException
	{
		sapLogger.entering("copyFromDocument()");
		// copy of basket properties
		if (salesDoc.getTechKey() != null)
		{
			order.setTechKey(salesDoc.getTechKey());
		}
		order.setBasketId(salesDoc.getTechKey());
		order.setHeader((Header) salesDoc.getHeader().clone());
		order.getHeader().setDocumentType(DocumentType.ORDER);

		// ship-to list
		order.setShipToList(new ArrayList<ShipTo>(salesDoc.getAlternativeShipTos()));

		// bill-to list
		order.setBillToList(new ArrayList<BillTo>(salesDoc.getAlternativeBillTos()));

		// dont assign the very same itemlist, rather copy the items
		order.setItemList((ItemList) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_LIST));

		for (final Item item : salesDoc.getItemList())
		{
			order.addItem((Item) item.clone());
		}

		order.setLoadStateCreate();
		order.setInitialized(true);

		sapLogger.exiting();
	}


}
