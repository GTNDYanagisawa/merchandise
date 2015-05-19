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

import java.util.ArrayList;


/**
 * In case of ECC 605, we also request payment service provider information from the EPR backend
 * 
 * 
 */
public class LrdActionsStrategyERP605 extends LrdActionsStrategyERP
{

	/**
	 * Default constructor
	 */
	public LrdActionsStrategyERP605()
	{
		super();
		setActiveFieldsListCreateChange605(activeFieldsListCreateChange);
	}


	private static void setActiveFieldsListCreateChange605(final ArrayList<SetActiveFieldsListEntry> activeFieldsListCreateChange)
	{
		// HEAD
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "SPPAYM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("LPAYSP", "PS_PROVIDER"));
	}

}
