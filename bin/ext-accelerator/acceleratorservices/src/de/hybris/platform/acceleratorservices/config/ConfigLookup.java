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
 */
public interface ConfigLookup
{
	int getInt(String key, int defaultValue);

	long getLong(String key, long defaultValue);

	double getDouble(String key, double defaultValue);

	boolean getBoolean(String key, boolean defaultValue);

	String getString(String key, String defaultValue);
}
