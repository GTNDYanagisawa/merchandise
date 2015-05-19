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
package de.hybris.platform.acceleratorservices.config;

/**
 * Host config service is used to lookup a hostname specific configuration property.
 * Certain configurations are specific the the hostname that the site is accessed on,
 * these include analytics tracking packages or maps integrations.
 */
public interface HostConfigService
{
	String getProperty(String property, String hostname);

	ConfigLookup getConfigForHost(String hostname);
}
