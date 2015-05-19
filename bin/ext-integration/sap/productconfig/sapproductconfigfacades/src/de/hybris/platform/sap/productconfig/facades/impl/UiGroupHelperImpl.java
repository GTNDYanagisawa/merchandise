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

package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;


public class UiGroupHelperImpl
{

	public static String generateGroupIdForInstance(final InstanceModel instance)
	{
		final String uiGroupId = instance.getId() + "-" + instance.getName();
		return uiGroupId;
	}

	public static String generateGroupIdForGroup(final InstanceModel instance, final CsticGroupModel group)
	{
		final String prefix = generateGroupIdForInstance(instance);
		final String uiGroupId = prefix + "." + group.getName();
		return uiGroupId;
	}


	public static String retrieveInstanceId(final String uiGroupId)
	{
		final String instanceId = uiGroupId.substring(0, uiGroupId.indexOf("-"));
		return instanceId;
	}

}
