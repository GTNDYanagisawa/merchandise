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

package de.hybris.platform.sap.productconfig.runtime.interf.model;

import java.util.Map;


/**
 * Common base interface for all configuration related models includes the extension map.
 */
public interface BaseModel extends Cloneable
{
	/**
	 * @return the extensionMap
	 */
	public Map<String, String> getExtensionMap();

	/**
	 * @param extensionMap
	 *           the extensionMap to set
	 */
	public void setExtensionMap(final Map<String, String> extensionMap);

	/**
	 * @param key
	 *           the key of the extension data
	 * @param value
	 *           the value of the extension data
	 */
	public void putExtensionData(final String key, final String value);

	/**
	 * @param key
	 *           the key of the extension data
	 * @return the value of the extension data
	 */
	public String getExtensionData(final String key);

	/**
	 * @return cloned BaseModel
	 */
	public BaseModel clone();
}
