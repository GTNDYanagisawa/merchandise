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
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatus;


/**
 * Represents the OverallStatus object.
 * 
 * @see OverallStatus
 */
public abstract class OverallStatusImpl extends BusinessStatusImpl implements OverallStatus
{

	/**
	 * Default constructor
	 */
	public OverallStatusImpl()
	{
		super();
	}

	/**
	 * Only for unit tests. Other callers need to instantiate via {@link GenericFactory} and call init method}.
	 * 
	 * @param key
	 *           status key
	 */
	public OverallStatusImpl(final EStatus key)
	{
		super(key);
	}

	@Override
	public boolean isNotRelevant()
	{
		return this.wecStatus == EStatus.NOT_RELEVANT;
	}

	@Override
	public boolean isNotProcessed()
	{
		return this.wecStatus == EStatus.NOT_PROCESSED;
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
