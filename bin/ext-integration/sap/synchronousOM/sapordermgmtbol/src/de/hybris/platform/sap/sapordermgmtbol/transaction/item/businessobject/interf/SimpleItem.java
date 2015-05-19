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
package de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.common.TechKey;

import java.math.BigDecimal;
import java.util.Map;


/**
 * this interface defines the most common attributes of an item (e.g. product, quantity, ...). <br>
 * 
 */
public interface SimpleItem extends BusinessObject, Cloneable, Comparable<SimpleItem>
{

	/**
	 * Returns the product (name) of this item.<br>
	 * 
	 * @return product name
	 */
	public String getProductId();

	/**
	 * Returns the id / TechKey / GUID of the product.<br>
	 * 
	 * @return TechKey / GUID of this product
	 */
	public TechKey getProductGuid();

	/**
	 * Sets the product for this item.<br>
	 * 
	 * @param productId
	 *           product name
	 */
	public void setProductId(String productId);

	/**
	 * Sets the product guid for this item.<br>
	 * 
	 * @param productGuid
	 *           TechKey/guid of the product
	 */
	public void setProductGuid(TechKey productGuid);

	/**
	 * If product of the item changes (e.g. was a notebook and is now a monitor), we reuse the item but we need to know
	 * whether the product changed
	 * 
	 * @return true if product was changed
	 */
	public boolean isProductChanged();

	/**
	 * If product of the item changes (e.g. was a notebook and is now a monitor), we reuse the item but we need to know
	 * whether the product changed
	 * 
	 * @param productChanged
	 *           if <code>true</code>, we conside the item to be changted
	 */
	public void setProductChanged(boolean productChanged);

	/**
	 * Sets parent Id for an item. For example for the sub item, free good item.<br>
	 * 
	 * @param parentId
	 *           TechKey or null/TechKey.EMPTY_KEY
	 */
	public void setParentId(TechKey parentId);

	/**
	 * Returns the TechKey (guid) of the parent item if this item is a sub item.<br>
	 * If there is no parent item, null or TechKey.EMPTY_KEY is returned.
	 * 
	 * @return TechKey of the parent item
	 */
	public TechKey getParentId();

	/**
	 * Get descriptions on the item level.
	 * 
	 * @return description
	 */
	public String getDescription();

	/**
	 * Returns the quantity of this item.<br>
	 * 
	 * @return quantity
	 */
	public BigDecimal getQuantity();

	/**
	 * Returns the quantity of this item.<br>
	 * 
	 * @return quantity
	 */
	public BigDecimal getLastQuantity();

	/**
	 * Returns the UOM (Unit of Measure) of this item.<br>
	 * This unit is not localised, e.g. ST
	 * 
	 * @return UOM
	 */
	public String getUnit();

	/**
	 * Sets Description.<br>
	 * 
	 * @param description
	 *           item description
	 */
	public void setDescription(String description);

	/**
	 * Sets an item quantity.<br>
	 * 
	 * @param quantity
	 *           value to set
	 */
	public void setQuantity(BigDecimal quantity);

	/**
	 * Set the unit (UOM) for this item.<br>
	 * The value is not localised, e.g. ST
	 * 
	 * @param unit
	 *           UOM
	 */
	public void setUnit(String unit);

	/**
	 * Return the position number of this item.<br>
	 * The position is determined in the back end.
	 * 
	 * @return numberInt/position
	 */
	public int getNumberInt();

	/**
	 * Sets the numberInt/position of the item.<br>
	 * The position is determined in the back end and set to the item. It cannot be changed.
	 * 
	 * @param numberInt
	 *           position
	 */
	public void setNumberInt(int numberInt);

	/**
	 * Type safe getter for the extension map<br>
	 * 
	 * @return extension map attached to this item
	 */
	public Map<String, Object> getTypedExtensionMap();


}
