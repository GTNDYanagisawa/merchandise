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

import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ProcessingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;


/**
 * Represents the ShippingStatus object.
 * 
 * @see ShippingStatus
 */
public class ProcessingStatusImpl extends BusinessStatusImpl implements ProcessingStatus
{

	/**
	 * Only for unit tests. Other callers need to instantiate via {@link GenericFactory} and call init method}.
	 * 
	 * @param key
	 *           status key
	 */
	public ProcessingStatusImpl(final EStatus key)
	{
		super(key);
	}

	/**
	 * Default constructor
	 */
	public ProcessingStatusImpl()
	{
		super();
	}

	@Override
	public Object clone()
	{
		// class only contains immutable objects, so super clone is sufficient
		return super.clone();
	}

	@Override
	public boolean isNotProcessed()
	{
		return this.wecStatus == EStatus.NOT_PROCESSED;
	}

	@Override
	public boolean isNotRelevant()
	{
		return this.wecStatus == EStatus.NOT_RELEVANT;
	}

	@Override
	public boolean isPartiallyProcessed()
	{
		return this.wecStatus == EStatus.PARTIALLY_PROCESSED;
	}

	@Override
	public boolean isProcessed()
	{
		return this.wecStatus == EStatus.PROCESSED;
	}

}
