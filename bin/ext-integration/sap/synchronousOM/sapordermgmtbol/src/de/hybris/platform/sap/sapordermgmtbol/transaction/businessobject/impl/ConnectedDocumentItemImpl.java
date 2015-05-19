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

import java.math.BigDecimal;
import java.util.Date;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocumentItem;


/**
 * Standard implementation for the ConnectedDocumentItem interface.<br>
 * The ConnectedDocumentItem represents an entry in the document flow on item level.
 * 
 */
public class ConnectedDocumentItemImpl extends ConnectedObjectImpl implements ConnectedDocumentItem
{

	/**
	 * position number of the item
	 */
	protected String posNumber = "";
	/**
	 * technical key for the document
	 */
	protected TechKey documentKey = TechKey.EMPTY_KEY;
	/**
	 * date as attribute for the doc flow e.g. delivery date in the item doc flow entry for a order-delivery relation
	 */
	protected Date date;
	/**
	 * quantity as attribute for the doc flow e.g. delivered quantity in the item doc flow entry for a order-delivery
	 * relation
	 */
	protected BigDecimal quantity;
	/**
	 * quantity unit as attribute for the doc flow see the reference attribute 'quantity'
	 */
	protected String unitOfMesure = "";
	/**
	 * origin of the connected item
	 */
	protected String docOrigin = "";
	/**
	 * application type
	 */
	protected String appTyp = "";
	/**
	 * Reference guid
	 */
	protected String refGuid = "";
	/**
	 * tracking URL for Delivery connected documents
	 */
	protected String trackingURL = "";

	/**
	 * Returns the key of the document.
	 * 
	 * @return TechKey
	 */
	@Override
	public TechKey getDocumentKey()
	{
		return documentKey;
	}

	/**
	 * Sets the document key.
	 * 
	 * @param documentKey
	 *           The document key to set
	 */
	@Override
	public void setDocumentKey(final TechKey documentKey)
	{
		this.documentKey = documentKey;
	}

	/**
	 * Set the Position Number of the connected Item
	 * 
	 * @param posNumber
	 *           position number
	 */
	@Override
	public void setPosNumber(final String posNumber)
	{
		this.posNumber = posNumber;
	}

	/**
	 * Returns the Position Number of the connected Item
	 * 
	 * @return position number
	 */
	@Override
	public String getPosNumber()
	{
		return posNumber;
	}

	/**
	 * Returns the date of the relation item e.g. delivery date
	 */
	@Override
	public Date getDate()
	{
		return date;
	}

	/**
	 * Sets the date for the relation item e.g. delivery date, provided by the doc flow
	 */

	@Override
	public void setDate(final Date date)
	{

		this.date = date;
	}

	/**
	 * returns the quantity for the relation item e.g. delivered quantity
	 */
	@Override
	public BigDecimal getQuantity()
	{
		return quantity;
	}

	@Override
	public void setQuantity(final BigDecimal quant)
	{
		quantity = quant;
	}

	/**
	 * returns the quantity unit see the reference attribute 'quantity'
	 */
	@Override
	public String getUnit()
	{
		return unitOfMesure;
	}

	/**
	 * sets the quantity unit provided by the doc flow as attribute of the item relation
	 */
	@Override
	public void setUnit(final String quantUnit)
	{
		unitOfMesure = quantUnit;
	}

	/**
	 * returns the applicaion type
	 */
	@Override
	public String getAppTyp()
	{
		return appTyp;
	}

	/**
	 * sets the application type of reference document
	 */
	@Override
	public void setAppTyp(final String string)
	{
		appTyp = string;
	}

	/**
	 * returns the reference guid
	 */
	@Override
	public String getRefGuid()
	{
		return refGuid;
	}

	/**
	 * sets the reference guid
	 */
	@Override
	public void setRefGuid(final String refGuid)
	{
		this.refGuid = refGuid;
	}

	/**
	 * returns the tracking URL for delivery doc flow entry
	 */
	@Override
	public String getTrackingURL()
	{
		return trackingURL;
	}

	/**
	 * sets the tracking URL for delivery doc flow entry
	 */
	@Override
	public void setTrackingURL(final String string)
	{
		trackingURL = string;
	}

	/**
	 * Set the origin (e.g RFC destination) of a document
	 * 
	 * @param docOrigin
	 *           contains a documents origin
	 */
	@Override
	public void setDocumentOrigin(final String docOrigin)
	{
		this.docOrigin = docOrigin;
	}

	/**
	 * Returns the origin (e.g RFC destination) of a document
	 * 
	 * @return String containg a documents origin
	 */
	@Override
	public String getDocumentOrigin()
	{
		return docOrigin;
	}

	@Override
	public String getBusObjectType()
	{
		return busObjectType;
	}

	@Override
	public void setBusObjectType(final String busObjectType)
	{
		this.busObjectType = busObjectType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.wec.app.common.module.transaction.businessobject.impl. ConnectedObjectImpl#clone()
	 */
	@Override
	public Object clone()
	{
		// we only contain immutable fields so super clone is fine.
		return super.clone();
	}

}
