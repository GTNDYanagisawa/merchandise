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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl;

import java.util.Date;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;


/**
 * 
 */
public class SearchResultImpl implements SearchResult
{

	private TechKey key;
	private String purchaseOrderNumber;
	private String overallStatus;
	private String shippingStatus;
	private Date creationDate;



	@Override
	public Date getCreationDate()
	{
		return creationDate;
	}

	@Override
	public String getShippingStatus()
	{
		return shippingStatus;
	}

	@Override
	public String getPurchaseOrderNumber()
	{
		return purchaseOrderNumber;
	}

	@Override
	public TechKey getKey()
	{
		return key;
	}

	@Override
	public void setKey(final TechKey key)
	{
		this.key = key;
	}

	//


	@Override
	public void setPurchaseOrderNumber(final String poNr)
	{
		this.purchaseOrderNumber = poNr;

	}


	@Override
	public void setOverallStatus(final String overallStatus)
	{
		this.overallStatus = overallStatus;

	}

	@Override
	public String getOverallStatus()
	{
		return overallStatus;
	}

	@Override
	public void setShippingStatus(final String shippingStatus)
	{
		this.shippingStatus = shippingStatus;

	}

	@Override
	public void setCreationDate(final Date date)
	{
		this.creationDate = date;

	}
}
