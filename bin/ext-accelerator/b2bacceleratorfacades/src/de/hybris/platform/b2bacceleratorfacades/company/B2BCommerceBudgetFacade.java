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
package de.hybris.platform.b2bacceleratorfacades.company;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BBudgetData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;


/**
 * A facade for Budget management within b2b commerce
 */
public interface B2BCommerceBudgetFacade
{
	/**
	 * @param b2bBudgetData
	 * @throws DuplicateUidException
	 */
	void addBudget(B2BBudgetData b2bBudgetData) throws DuplicateUidException;

	/**
	 * Enable or disable a budget
	 * 
	 * @param b2BudgetCode
	 * @param active
	 * @throws DuplicateUidException
	 */
	void enableDisableBudget(String b2BudgetCode, boolean active) throws DuplicateUidException;


	/**
	 * Update budget details for edit operation
	 * 
	 * @param b2BBudgetData
	 * @throws DuplicateUidException
	 */
	void updateBudgetDetails(B2BBudgetData b2BBudgetData) throws DuplicateUidException;
}
