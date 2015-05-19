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
package de.hybris.platform.servicelayer.tenant.impl;

import de.hybris.platform.core.Tenant;
import de.hybris.platform.servicelayer.tenant.TenantService;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class TestTenantService implements TenantService
{
	private Tenant tenant;

	@Required
	public void setCurrentTenant(final Tenant tenant)
	{
		this.tenant = tenant;
	}

	@Override
	public String getCurrentTenantId()
	{
		return tenant.getTenantID();
	}

}
