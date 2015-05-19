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

import java.util.Properties;


/**
 * Single-linked list of Properties definitions.
 * 
 */
abstract class LinkedProperties extends Properties
{
	private static final long serialVersionUID = 1L;
	protected LinkedProperties parent = null; // NOPMD

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *           parent linked properties
	 */
	public LinkedProperties(final LinkedProperties parent)
	{
		super(parent);
		this.parent = parent;
	}

	/**
	 * Returns the parent linked properties.
	 * 
	 * @return parent parent linked properties
	 */
	public LinkedProperties getParent()
	{
		return parent;
	}

	/**
	 * Returns information about the source.
	 * 
	 * @return information
	 */
	public abstract String getInfo();

}
