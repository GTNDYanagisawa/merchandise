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

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemListBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.SimpleItem;

import java.util.ArrayList;


/**
 * Standard implementation of ItemListBase interface based on the ArrayList class.
 * 
 * @param <T>
 *           The actual item type
 * @stereotype collection
 */
public class ItemListBaseImpl<T extends SimpleItem> extends ArrayList<T> implements ItemListBase<T>
{

	private static final long serialVersionUID = 627217022374215955L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.
	 * ItemListBase#contains(java.lang.String)
	 */
	@Override
	public boolean contains(final String handle)
	{
		return indexOf(handle) != -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.
	 * ItemListBase#contains(com.sap.wec.tc.core.common.TechKey)
	 */
	@Override
	public boolean contains(final TechKey techKey)
	{
		return indexOf(techKey) != -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.
	 * ItemListBase#get(java.lang.String)
	 */
	@Override
	public T get(final String handle)
	{
		final int index = indexOf(handle);

		if (index != -1)
		{
			return super.get(index);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.
	 * ItemListBase#get(com.sap.wec.tc.core.common.TechKey)
	 */
	@Override
	public T get(final TechKey techKey)
	{
		final int index = indexOf(techKey);

		if (index != -1)
		{
			return super.get(index);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.
	 * ItemListBase#indexOf(java.lang.String)
	 */
	@Override
	public int indexOf(final String handle)
	{
		final int size = size();

		for (int i = 0; i < size; i++)
		{
			if (super.get(i).getHandle().equals(handle))
			{
				return i;
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.
	 * ItemListBase#indexOf(com.sap.wec.tc.core.common.TechKey)
	 */
	@Override
	public int indexOf(final TechKey techKey)
	{
		final int size = size();

		for (int i = 0; i < size; i++)
		{
			if (super.get(i).getTechKey().equals(techKey))
			{
				return i;
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.
	 * ItemListBase#remove(java.lang.String)
	 */
	@Override
	public boolean remove(final String handle)
	{
		final int index = indexOf(handle);

		if (index != -1)
		{
			super.remove(index);
			return true;
		}
		else
		{
			return false;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.
	 * ItemListBase#remove(com.sap.wec.tc.core.common.TechKey)
	 */
	@Override
	public boolean remove(final TechKey techKey)
	{

		final int index = indexOf(techKey);

		if (index != -1)
		{
			super.remove(index);
			return true;
		}
		else
		{
			return false;
		}
	}

}
