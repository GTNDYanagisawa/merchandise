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

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter;


/**
 * 
 */
public class SearchFilterImpl implements SearchFilter
{

	private String shippingStatus;
	private String product;
	private String soldToId;




	@Override
	public String getShippingStatus()
	{
		return shippingStatus;
	}

	@Override
	public void setShippingStatus(final String shippingStatus)
	{
		this.shippingStatus = shippingStatus;
	}


	@Override
	public String getProductID()
	{
		return product;
	}

	@Override
	public void setProductID(final String product)
	{
		this.product = product;

	}


	@Override
	public String getSoldToId()
	{
		return soldToId;
	}

	@Override
	public void setSoldToId(final String soldToId)
	{
		this.soldToId = soldToId;
	}


}
