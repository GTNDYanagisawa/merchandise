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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocumentBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.HeaderBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemListBase;

import java.util.ArrayList;
import java.util.List;


/**
 * Base class for all sales documents, e.g. Basket, Order etc.<br>
 * The document consists in principle of a Header and a ItemList, which again contains Items. The type of all 3 objects
 * can be passed to this class via generics to enable type safe access.<br>
 * 
 * @param <L>
 *           The ItemList has to extend
 *           {@link de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemListBase}
 * @param <I>
 *           The Item has to extend
 *           {@link de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemBase} objects
 * @param <H>
 *           The Header has to extend
 *           {@link de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.HeaderBase}
 */
public abstract class SalesDocumentBaseImpl<L extends ItemListBase<I>, I extends ItemBase, H extends HeaderBase> extends
		SimpleDocumentImpl<L, I, H> implements SalesDocumentBase<L, I, H>
{

	// private static final WCFLocation LOCATION =
	// WCFLocation.getInstance(SalesDocumentBaseImpl.class.getName());

	protected boolean changeHeaderOnly = false;
	protected List<ShipTo> shipToList = new ArrayList<ShipTo>();
	protected List<BillTo> billToList = new ArrayList<BillTo>();


	protected boolean persistentInBackend = true;

	@Override
	public void clear()
	{
		super.clear();
		changeHeaderOnly = false;
	}

	/**
	 * Method will be called by the <code>BusinessObjectManager</code> when the end of the life cycle is reached, so that
	 * all used resources can be released.
	 */
	@Override
	public void destroy()
	{
		super.destroy();

	}

	/**
	 * Returns true if only the header should be changed
	 * 
	 * @return boolean true if only the header should be changed, false else
	 */
	@Override
	public boolean isChangeHeaderOnly()
	{
		return changeHeaderOnly;
	}



	@Override
	public List<BillTo> getAlternativeBillTos()
	{
		return this.billToList;
	}

	@Override
	public List<ShipTo> getAlternativeShipTos()
	{
		return this.shipToList;
	}


	@Override
	public boolean isPersistentInBackend()
	{
		return persistentInBackend;
	}

	@Override
	public void setPersistentInBackend(final boolean isPersistent)
	{
		persistentInBackend = isPersistent;
	}

}
