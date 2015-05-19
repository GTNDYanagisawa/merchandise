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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl;

import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesTransactionsFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;


/**
 * Default implementation of the factory creating some simple sales related objects. <br>
 * 
 */
public class SalesTransactionsFactoryImpl implements SalesTransactionsFactory
{

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
	public ShipTo createShipTo()
	{
		final ShipTo shipTo = (ShipTo) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SHIP_TO);
		return shipTo;
	}

	@Override
	public BillTo createBillTo()
	{
		final BillTo billTo = (BillTo) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BILL_TO);
		return billTo;
	}

	@Override
	public Item createSalesDocumentItem()
	{
		final Item item = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		return item;
	}

}
