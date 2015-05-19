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
 * Site config service is used to lookup a site specific configuration property.
 * The configuration property is looked up relative to the current site.
 */
public interface SiteConfigService extends ConfigLookup
{
	String getProperty(String property);
}
