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
package de.hybris.platform.cockpit.test.mock;

import de.hybris.platform.cockpit.services.impl.SystemServiceImpl;
import de.hybris.platform.core.model.user.UserModel;


public class MockSystemService extends SystemServiceImpl
{

	@Override
	public boolean checkAttributePermissionOn(final String typeCode, final String attributeQualifier, final String permissionCode)
	{
		return true;
	}

	@Override
	public boolean checkPermissionOn(final UserModel user, final String typeCode, final String permissionCode)
	{
		return true;
	}

}
