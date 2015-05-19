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
package de.hybris.platform.webservices.util.objectgraphtransformer.misc;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;


@GraphNode(target = GraphPropSource.class)
public class GraphPropTarget
{
	private String value1;
	private Integer value2;
	private Integer value3;
	private Integer value4;

	/**
	 * @return the value
	 */
	public String getValue1()
	{
		return value1;
	}

	/**
	 * @param value
	 *           the value to set
	 */
	public void setValue1(final String value)
	{
		this.value1 = value;
	}

	/**
	 * @return the value2
	 */
	public Integer getValue2()
	{
		return value2;
	}

	/**
	 * @param value2
	 *           the value2 to set
	 */
	public void setValue2(final Integer value2)
	{
		this.value2 = value2;
	}

	/**
	 * @return the value3
	 */
	public Integer getValue3()
	{
		return value3;
	}

	/**
	 * @param value3
	 *           the value3 to set
	 */
	public void setValue3(final Integer value3)
	{
		this.value3 = value3;
	}

	/**
	 * @return the value4
	 */
	public Integer getValue4()
	{
		return value4;
	}

	/**
	 * @param value4
	 *           the value4 to set
	 */
	public void setValue4(final Integer value4)
	{
		this.value4 = value4;
	}




}
