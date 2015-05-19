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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.GetAllStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.LrdActionsStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.SetStrategy;


/**
 * Holder of access (interaction procedure) for a specific release of ERP. <br>
 * 
 * @version 1.0
 */
public abstract class StrategyFactoryERP
{

	/**
	 * Access (interaction procedure) for reading a sales document from ERP with all aspects..<br>
	 * 
	 * @return get all strategy
	 */
	public abstract GetAllStrategy createGetAllStrategy();


	/**
	 * Access (interaction procedure) for LORD API.<br>
	 * 
	 * @return LORD API strategy
	 */
	public LrdActionsStrategy createLrdActionsStrategy()
	{
		return new LrdActionsStrategyERP();
	}

	/**
	 * Access (interaction procedure) for ERP_LORD_SET.<br>
	 * 
	 * @return ERP_LORD_SET strategy
	 */
	public SetStrategy createSetStrategy()
	{
		return new SetStrategyERP();
	}


}
