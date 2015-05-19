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
package de.hybris.platform.sap.orderexchange.mocks;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.CancelDecision;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;

import java.util.Map;


/**
 * Mock to be used for spring tests
 */
public class MockOrderCancelService implements OrderCancelService
{

	@Override
	public Map<AbstractOrderEntryModel, Long> getAllCancelableEntries(final OrderModel arg0, final PrincipalModel arg1)
	{
		return null;
	}

	@Override
	public OrderCancelRecordModel getCancelRecordForOrder(final OrderModel arg0) throws OrderCancelException
	{
		return null;
	}

	@Override
	public OrderCancelConfigModel getConfiguration()
	{
		return null;
	}

	@Override
	public OrderCancelRecordEntryModel getPendingCancelRecordEntry(final OrderModel arg0) throws OrderCancelException
	{
		return null;
	}

	@Override
	public CancelDecision isCancelPossible(final OrderModel arg0, final PrincipalModel arg1, final boolean arg2, final boolean arg3)
	{
		return null;
	}

	@Override
	public OrderCancelRecordEntryModel requestOrderCancel(final OrderCancelRequest arg0, final PrincipalModel arg1)
			throws OrderCancelException
	{
		return null;
	}

}
