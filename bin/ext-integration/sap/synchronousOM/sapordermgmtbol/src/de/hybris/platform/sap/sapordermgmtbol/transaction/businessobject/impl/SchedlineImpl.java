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
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Schedule line of the Sales Document
 * 
 */

public class SchedlineImpl extends BusinessObjectBase implements Schedline
{

	private Date committedDate;
	private BigDecimal committedQuantity = null;
	private String unit;

	/**
	 * Retrieves the committed date of the schedule line
	 */
	@Override
	public Date getCommittedDate()
	{
		return committedDate;
	}

	/**
	 * Retrieves the committed quantity of the schedule line
	 */
	@Override
	public BigDecimal getCommittedQuantity()
	{
		return committedQuantity;
	}

	/**
	 * Sets the committed date of the schedule line
	 */
	@Override
	public void setCommittedDate(final Date committedDate)
	{
		this.committedDate = committedDate;

	}

	/**
	 * Sets the committed quantity of the schedule line
	 */
	@Override
	public void setCommittedQuantity(final BigDecimal committedQuantity)
	{
		this.committedQuantity = committedQuantity;

	}

	/**
	 * Performs a deep-copy of the object rather than a shallow copy.
	 * 
	 * @return returns a deep copy
	 */
	@Override
	public Object clone()
	{
		SchedlineImpl myClone;
		try
		{
			// immutable fields will be handled by object clone.
			myClone = (SchedlineImpl) super.clone();
		}
		catch (final CloneNotSupportedException ex)
		{
			// should not happen, because we are clone able
			throw new ApplicationBaseRuntimeException(
					"Failed to clone Object, check whether Cloneable Interface is still implemented", ex);
		}
		// clone mutable fields
		if (committedDate != null)
		{
			myClone.committedDate = (Date) committedDate.clone();
		}

		return myClone;

	}

	@Override
	public void setUnit(final String unit)
	{
		this.unit = unit;

	}


	@Override
	public String getUnit()
	{
		return unit;
	}

}