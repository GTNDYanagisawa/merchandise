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


@GraphNode(target = CycleTwoDTO.class)
public class CycleTwoDTO
{
	private CycleOneDTO cycleOneDTO;

	private int value;

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

	public CycleTwoDTO()
	{
		this(0);
	}

	public CycleTwoDTO(final int value)
	{
		this.value = value;
	}


	/**
	 * @return the cycleOneDTO
	 */
	public CycleOneDTO getCycleOneDTO()
	{
		return cycleOneDTO;
	}

	/**
	 * @param cycleOneDTO
	 *           the cycleOneDTO to set
	 */
	public void setCycleOneDTO(final CycleOneDTO cycleOneDTO)
	{
		this.cycleOneDTO = cycleOneDTO;
	}

}
