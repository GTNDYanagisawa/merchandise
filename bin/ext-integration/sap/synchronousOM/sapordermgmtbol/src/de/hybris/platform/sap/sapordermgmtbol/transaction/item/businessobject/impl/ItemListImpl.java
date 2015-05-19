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
package de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;


/**
 * This class derives from the <code>ItemListBaseImpl</code>. It does not provide any functionality on top of the
 * <code>ItemListBaseImpl</code>. Instead It uses <code>Item</code> as list entry type, and can not by typed with a
 * different type using generics. So it represents a List of <code>Item</code> objects. This class can be used to
 * maintain a collection of such objects.
 * 
 * @stereotype collection
 */
public class ItemListImpl extends ItemListBaseImpl<Item> implements ItemList
{

	private static final long serialVersionUID = 4482891713343669138L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf. ItemList#toItemArray()
	 */
	@Override
	public Item[] toItemArray()
	{
		return super.toArray(new Item[super.size()]);
	}

}
