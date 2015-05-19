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

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.util.CSSClassResolver;

import org.springframework.validation.BindingResult;


/**

 * 
 */
public class CSSClassResolverFactory
{

	private static CSSClassResolver resolver;


	/**
	 * setter to inject a resolver for testing
	 * 
	 * @param resolver
	 */
	static void setResolver(final CSSClassResolver resolver)
	{
		CSSClassResolverFactory.resolver = resolver;
	}

	private static CSSClassResolver getCSSClassResolver()
	{
		if (resolver == null)
		{
			resolver = (CSSClassResolver) Registry.getApplicationContext().getBean("sapProductConfigDefaultCssClassResolver");
		}
		return resolver;
	}

	public static String getStyleClassForGroup(final UiGroupData group)
	{
		return getCSSClassResolver().getGroupStyleClass(group);
	}

	public static String getStyleClassForNode(final UiGroupData group, final Boolean isFirstLevelNode, final Boolean showNodeTitle)
	{
		return getCSSClassResolver().getNodeStyleClass(group, isFirstLevelNode.booleanValue(), showNodeTitle.booleanValue());
	}

	public static String getLabelStyleClassForCstic(final CsticData cstic)
	{
		return getCSSClassResolver().getLabelStyleClass(cstic);
	}

	public static String getValueStyleClassForCstic(final CsticData cstic)
	{
		return getCSSClassResolver().getValueStyleClass(cstic);
	}

	public static String getStyleClassForGroupTab(final UiGroupData group)
	{
		return getCSSClassResolver().getGroupTabStyleClass(group);
	}

	public static String getStyleClassForSideBarComponent(final Boolean collapsed, final BindingResult bindResult)
	{
		return getCSSClassResolver().getStyleClassForSideBarComponent(collapsed.booleanValue(), bindResult);
	}
}
