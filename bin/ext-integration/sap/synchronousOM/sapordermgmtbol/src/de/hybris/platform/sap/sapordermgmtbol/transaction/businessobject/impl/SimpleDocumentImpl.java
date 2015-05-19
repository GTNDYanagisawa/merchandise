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

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.PrettyPrinter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SimpleDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.SimpleHeader;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemListBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.SimpleItem;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @param <L>
 *           ItemList
 * @param <I>
 *           Item
 * @param <H>
 *           Header
 */
public abstract class SimpleDocumentImpl<L extends ItemListBase<I>, I extends SimpleItem, H extends SimpleHeader> extends
		BusinessObjectBase implements SimpleDocument<L, I, H>
{

	/**
	 * The atcual item list.
	 */
	private L itemList;

	private H header;

	private boolean dirty = true;

	private TechKey soldToGuid;

	private String applicationId;

	private long version;

	private Date changeDate;

	/**
	 * Returns an iterator for the items of the item list.<br>
	 * This method guarantees to always return an iterator, even if the underlying item list object is null. In this case
	 * an iterator over an empty list is returned.
	 * 
	 * @return iterator to iterate over the items
	 */
	@Override
	public Iterator<I> iterator()
	{
		if (itemList != null)
		{
			return itemList.iterator();
		}
		else
		{
			// return an empty iterator to prevent null pointer exception in
			// caller
			final List<I> list = Collections.emptyList();
			return list.iterator();
		}
	}

	@Override
	public void addItem(final I item)
	{
		if (item != null)
		{
			itemList.add(item);
		}
	}

	@Override
	public void clear()
	{
		header.clear();
		itemList.clear();
		dirty = true;

	}

	@Override
	public void clearHeader()
	{
		header.clear();
	}

	@Override
	public void clearItems()
	{
		if (itemList != null)
		{
			itemList.clear();
		}
		dirty = true;
	}

	/**
	 * clear all messages and set state of the Business Object to valid. By default all messages of sub objects will
	 * cleared also. Furthermore the document is set to dirty to force a back end call during next read
	 */
	@Override
	public void clearMessages()
	{
		super.clearMessages();
		dirty = true;
		header.setDirty(true);
	}

	@Override
	public H getHeader()
	{
		return header;
	}

	@Override
	public I getItem(final TechKey techKey)
	{
		return itemList.get(techKey);
	}

	@Override
	public L getItemList()
	{
		return itemList;
	}

	@Override
	public boolean isDirty()
	{
		return dirty;
	}

	@Override
	public void setDirty(final boolean isDirty)
	{
		this.dirty = isDirty;
		if (getHeader() != null)
		{
			getHeader().setDirty(isDirty);
		}
	}

	@Override
	public void setHeader(final H header)
	{
		this.header = header;
	}

	@Override
	public void setItemList(final L itemList)
	{
		this.itemList = itemList;

	}

	/**
	 * Sets the key for the document, as well as the document to dirty state.
	 * 
	 * @param techKey
	 *           Key to be set
	 */
	@Override
	public void setTechKey(final TechKey techKey)
	{
		header.setTechKey(techKey);
		super.setTechKey(techKey);
		setDirty(true);
		header.setDirty(true);
	}

	/**
	 * Returns a simplifies string representation of the object. Useful for debugging/logging purpose, but not for
	 * display on the User Interface.
	 * 
	 * @return object as string
	 */
	@Override
	public String toString()
	{
		final PrettyPrinter pp = new PrettyPrinter(super.toString());
		pp.add(techKey, "techKey");
		pp.add(applicationId, "applicationId");
		pp.add(soldToGuid, "soldToGuid");
		pp.add(header, "header");
		pp.add(itemList, "itemList");
		return pp.toString();
	}

	@Override
	public TechKey getSoldToGuid()
	{
		return soldToGuid;
	}

	@Override
	public void setSoldToGuid(final TechKey techKey)
	{
		final boolean soldToChanged = (null == techKey) || !techKey.equals(soldToGuid);

		if (soldToChanged)
		{
			soldToGuid = techKey;
			setDirty(true);
		}

	}

	@Override
	public void setApplicationId(final String applicationId)
	{
		this.applicationId = applicationId;

	}

	@Override
	public String getApplicationId()
	{
		return applicationId;
	}

	// we don't get the map type safe from the backend
	@Override
	public Map<String, Object> getTypedExtensionMap()
	{
		return getExtensionMap();
	}

	@Override
	public long getVersion()
	{
		return version;
	}

	@Override
	public void setVersion(final long version)
	{
		this.version = version;
	}

	/**
	 * @param changeDate
	 *           date this document was last changed
	 */
	@Override
	public void setChangeDate(final Date changeDate)
	{
		this.changeDate = changeDate;
	}

	/**
	 * 
	 * @return returns the date this document was last changed.
	 */
	@Override
	public Date getChangeDate()
	{
		return changeDate;
	}

}
