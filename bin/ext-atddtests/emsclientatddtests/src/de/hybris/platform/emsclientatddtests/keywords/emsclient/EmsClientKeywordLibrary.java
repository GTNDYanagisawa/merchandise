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
package de.hybris.platform.emsclientatddtests.keywords.emsclient;

import de.hybris.platform.atddengine.keywords.AbstractKeywordLibrary;
import de.hybris.platform.entitlementservices.facades.EntitlementFacadeDecorator;

import org.springframework.beans.factory.annotation.Autowired;


public class EmsClientKeywordLibrary extends AbstractKeywordLibrary
{

	@Autowired
	private EntitlementFacadeDecorator entitlementFacadeDecorator;

}
