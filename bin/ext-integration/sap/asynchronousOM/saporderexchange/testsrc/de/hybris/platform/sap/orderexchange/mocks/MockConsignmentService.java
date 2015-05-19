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
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.ConsignmentCreationException;
import de.hybris.platform.ordersplitting.ConsignmentService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import java.util.List;


/**
 * Mock to be used for spring tests
 */
public class MockConsignmentService implements ConsignmentService
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.ordersplitting.ConsignmentService#createConsignment(de.hybris.platform.core.model.order.
	 * AbstractOrderModel, java.lang.String, java.util.List)
	 */
	public ConsignmentModel createConsignment(final AbstractOrderModel arg0, final String arg1,
			final List<AbstractOrderEntryModel> arg2) throws ConsignmentCreationException
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.ordersplitting.ConsignmentService#getWarehouse(java.util.List)
	 */
	public WarehouseModel getWarehouse(final List<AbstractOrderEntryModel> arg0)
	{
		return null;
	}

}
