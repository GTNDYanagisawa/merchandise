/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.core;

import java.util.Collections;
import java.util.List;
import java.util.Properties;


/**
 *
 */
public class TestSlaveTenantStub extends SlaveTenant
{

	/**
	 * 
	 */
	public TestSlaveTenantStub(final String systemName)
	{
		super(systemName, new Properties());
		// YTODO Auto-generated constructor stub
	}


	@Override
	List<TenantListener> getTenantListeners()
	{

		return Collections.EMPTY_LIST;
	}

}
