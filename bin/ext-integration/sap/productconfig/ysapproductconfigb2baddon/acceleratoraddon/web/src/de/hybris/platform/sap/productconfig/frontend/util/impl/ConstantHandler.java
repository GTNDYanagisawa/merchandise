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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

public class ConstantHandler
{
	public static final String GENERAL_GROUP_NAME = "SAP_GENERAL_GROUP_NAME";

	public static String getGeneralGroupName()
	{
		return GENERAL_GROUP_NAME;
	}

	public static String getExpandeableNodeClass()
	{
		return CSSClassResolverImpl.STYLE_CLASS_NODE_EXPANDABLE;
	}
}
