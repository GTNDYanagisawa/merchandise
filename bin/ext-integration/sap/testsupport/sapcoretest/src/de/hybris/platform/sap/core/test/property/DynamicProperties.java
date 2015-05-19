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
package de.hybris.platform.sap.core.test.property;

/**
 * Class for holding properties added during runtime.
 */
class DynamicProperties extends LinkedProperties
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Standard constructor.
	 * 
	 * @param parent
	 *           parent linked properties
	 */
	public DynamicProperties(final LinkedProperties parent)
	{
		super(parent);
	}

	@Override
	public String getInfo()
	{
		return "Dynamic properties";
	}

}
