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
package de.hybris.platform.b2bacceleratorservices.strategies.impl;

import de.hybris.platform.b2bacceleratorservices.strategies.B2BApprovalProcessLookUpStrategy;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

public class DefaultB2BApprovalProcessLookUpStrategy implements B2BApprovalProcessLookUpStrategy
{
	private Map<String, String> processes;

	@Override
	public Map<String, String> getProcesses(final BaseStoreModel store)
	{
		//TODO: pull the list from the baseStore.
		return processes;
	}

	@Required
	public void setProcesses(final Map<String, String> processes)
	{
		this.processes = processes;
	}
}
