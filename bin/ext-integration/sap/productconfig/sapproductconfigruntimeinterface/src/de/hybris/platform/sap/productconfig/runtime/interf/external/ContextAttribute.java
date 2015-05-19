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
package de.hybris.platform.sap.productconfig.runtime.interf.external;

/**
 * Context attribute
 */
public interface ContextAttribute
{

	/**
	 * Set value
	 * 
	 * @param value
	 */
	public abstract void setValue(String value);

	/**
	 * @return Value
	 */
	public abstract String getValue();

	/**
	 * Set Name
	 * 
	 * @param name
	 */
	public abstract void setName(String name);

	/**
	 * @return Name
	 */
	public abstract String getName();

}
