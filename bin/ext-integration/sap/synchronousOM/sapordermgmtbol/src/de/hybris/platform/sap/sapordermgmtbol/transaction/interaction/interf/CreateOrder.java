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
package de.hybris.platform.sap.sapordermgmtbol.transaction.interaction.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;


/**
 * This interaction object is used to create an order out of an existing document.
 * 
 */
public interface CreateOrder
{

	/**
	 * Create an order with the data of a basket.
	 * 
	 * @param source
	 *           Basket which an order should be created of
	 * @param destination
	 *           Order to create
	 * @param isBasketLinkedToOrder
	 *           is basket linked to order?
	 * @param config
	 *           Configuration to use this creation
	 * @throws CommunicationException
	 *            the communication with the back end failed
	 * @throws BusinessObjectException
	 */
	public void createOrderFromBasket(Basket source, Order destination, boolean isBasketLinkedToOrder,
			TransactionConfiguration config) throws CommunicationException, BusinessObjectException;


}
