/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices.util.objectgraphtransformer.nodefactory;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;


@GraphNode(target = TfTarget2.class, factory = TfTarget2Factory.class)
public class TfTarget2
{
	private int value;

	public TfTarget2()
	{ //
	}

	public TfTarget2(final int value)
	{
		super();
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *           the value to set
	 */
	public void setValue(final int value)
	{
		this.value = value;
	}
}