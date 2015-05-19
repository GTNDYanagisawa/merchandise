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
package com.sap.hybris.reco.addon.bo;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;


/**
 *
 */
public class UserIdProvider
{

	public String getUserId(final UserModel user)
	{
		String userId = user.getUid();
		if (user instanceof B2BCustomerModel)
		{
			userId = ((B2BCustomerModel) user).getCustomerID();
		}
		else if (user instanceof CustomerModel)
		{
			userId = ((CustomerModel) user).getCustomerID();
		}
		if (userId != null && !userId.isEmpty() && !userId.equals("anonymous"))
		{
			return userId;
		}

		return null;
	}

}
