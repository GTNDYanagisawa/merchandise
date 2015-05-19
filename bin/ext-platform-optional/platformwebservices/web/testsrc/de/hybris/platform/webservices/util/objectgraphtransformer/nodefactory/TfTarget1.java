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



public class TfTarget1
{
	private int value;
	private TfTarget2 dto2;
	private TfSource3 dto3;

	/**
	 * @return the dto3
	 */
	public TfSource3 getDto3()
	{
		return dto3;
	}

	/**
	 * @param dto3
	 *           the dto3 to set
	 */
	public void setDto3(final TfSource3 dto3)
	{
		this.dto3 = dto3;
	}

	/**
	 * @return the dto2
	 */
	public TfTarget2 getDto2()
	{
		return dto2;
	}

	/**
	 * @param dto2
	 *           the dto2 to set
	 */
	public void setDto2(final TfTarget2 dto2)
	{
		this.dto2 = dto2;
	}

	public TfTarget1()
	{ //
	}

	public TfTarget1(final int value)
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