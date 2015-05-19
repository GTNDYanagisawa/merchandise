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


@GraphNode(target = CycleOneDTO.class)
public class CycleOneDTO
{
	private CycleTwoDTO cycleTwoDTO;
	private int value;

	public CycleOneDTO()
	{
		this(0);
	}

	public CycleOneDTO(final int value)
	{
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

	/**
	 * @return the cycleTwoDTO
	 */
	public CycleTwoDTO getCycleTwoDTO()
	{
		return cycleTwoDTO;
	}

	/**
	 * @param cycleTwoDTO
	 *           the cycleTwoDTO to set
	 */
	public void setCycleTwoDTO(final CycleTwoDTO cycleTwoDTO)
	{
		this.cycleTwoDTO = cycleTwoDTO;
	}

}
