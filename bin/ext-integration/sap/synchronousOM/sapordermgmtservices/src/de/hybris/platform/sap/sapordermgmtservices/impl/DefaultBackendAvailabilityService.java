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
package de.hybris.platform.sap.sapordermgmtservices.impl;

import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartFacade;


/**
 * Allows to access availability of the backend
 * 
 */
public class DefaultBackendAvailabilityService implements BackendAvailabilityService
{

	BolCartFacade bolCartFacade;
	private boolean backendDownForUnitTests;
	private boolean backendStateSetFromOutside;

	@Override
	public boolean isBackendDown()
	{
		if (backendStateSetFromOutside)
		{
			return backendDownForUnitTests;
		}
		return bolCartFacade.isBackendDown();
	}

	/**
	 * @return the bolCartFacade
	 */
	protected BolCartFacade getBolCartFacade()
	{
		return bolCartFacade;
	}

	/**
	 * @param bolCartFacade
	 *           the bolCartFacade to set
	 */
	public void setBolCartFacade(final BolCartFacade bolCartFacade)
	{
		this.bolCartFacade = bolCartFacade;
	}

	/**
	 * Just for unit test purposes
	 * 
	 * @param b
	 */
	public void setBackendDown(final boolean b)
	{
		this.backendStateSetFromOutside = true;
		this.backendDownForUnitTests = b;

	}


}
