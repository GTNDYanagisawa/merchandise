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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.SimpleHeader;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemListBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.SimpleItem;

import java.util.Date;
import java.util.Map;


/**
 * This is a base class to encapsulate the most common features of a document (e.g. Header and Items). <br>
 * 
 * @param <L>
 *           ItemList to be used
 * @param <I>
 *           Item to be used
 * @param <H>
 *           Header to be used
 */
public interface SimpleDocument<L extends ItemListBase<I>, I extends SimpleItem, H extends SimpleHeader> extends Iterable<I>,
		BusinessObject
{

	/**
	 * Adds a <code>Item</code> to the sales document. This item must be uniquely identified by a technical key. A
	 * <code>null</code> reference passed to this function will be ignored, in this case nothing will happen.
	 * 
	 * @param item
	 *           Item to be added to the sales doc
	 */
	public void addItem(I item);

	/**
	 * Clear the whole sales document including header and itemlist.
	 */
	public void clear();

	/**
	 * Convenience method to releases state of the header. Same as <code>getHeader().clear()</code>
	 */
	public void clearHeader();

	/**
	 * Convenience method to releases state of the ItemList. Same as <code>getItemList().clear()</code>
	 */
	public void clearItems();

	/**
	 * Returns the header associated with the sales document
	 * 
	 * @return Header
	 */
	public H getHeader();

	/**
	 * Returns the item of the document specified by the given technical key.<br>
	 * <br>
	 * <b>Note -</b> This is a convenience method operating on the <code>getItem</code> method of the internal
	 * <code>ItemList</code>.
	 * 
	 * @param techKey
	 *           the technical key of the item that should be retrieved
	 * @return the item with the given techical key or <code>null</code> if no item for that key was found.
	 */
	public I getItem(TechKey techKey);

	/**
	 * Returns a list of the items currently stored for this sales document.
	 * 
	 * @return list of items
	 */
	public L getItemList();

	/**
	 * Sets the header information. The header information is data common to all items of the basket.
	 * 
	 * @param header
	 *           Header data to set
	 */
	public void setHeader(H header);

	/**
	 * Sets the list of the items for this sales document.
	 * 
	 * @param itemList
	 *           list of items to be stored
	 */
	public void setItemList(L itemList);

	/**
	 * Get the dirty flag
	 * 
	 * @return isDirty will the document be read from the backend when the a read method is called true/false
	 */
	public boolean isDirty();

	/**
	 * Set the dirty flag
	 * 
	 * @param isDirty
	 *           if set to true, the document will be read from the backend when a read method is called if set to false,
	 *           the next call to a read method won't fill the object from the backend
	 */
	public void setDirty(boolean isDirty);

	/**
	 * Creates a <code>Item</code> for the basket. This item must be uniquely identified by a technical key.
	 * 
	 * @return Item which can be added to the basket
	 */
	public I createItem();

	/**
	 * @param techKey
	 *           GUID of the soldTo
	 */
	public void setSoldToGuid(TechKey techKey);

	/**
	 * @return techKey GUID of the soldTo
	 */
	public TechKey getSoldToGuid();

	/**
	 * @param applicationId
	 *           shop id
	 */
	public void setApplicationId(String applicationId);

	/**
	 * @return ID of application
	 */
	public String getApplicationId();

	/**
	 * Type safe getter for the extension map<br>
	 * 
	 * @return extension map attached to this document
	 */
	public Map<String, Object> getTypedExtensionMap();

	/**
	 * @return version field to support jpa optimistic locking
	 */
	public long getVersion();

	/**
	 * @param version
	 *           to support jpa optimistic locking
	 */
	public void setVersion(long version);

	/**
	 * @param changeDate
	 *           The date when the product list entity was changed
	 */
	public void setChangeDate(Date changeDate);

	/**
	 * @return changeDate The date when the product list entity was changed
	 */
	public Date getChangeDate();

}
