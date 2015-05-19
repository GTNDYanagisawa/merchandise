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

public class Tf2Target1
{
	private String id = null;
	private Tf2Target2 target2 = null;

	public Tf2Target1()
	{
		//
	}

	public Tf2Target1(final String id)
	{
		super();
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @return the target2
	 */
	public Tf2Target2 getTarget2()
	{
		return target2;
	}

	/**
	 * @param target2
	 *           the target2 to set
	 */
	public void setTarget2(final Tf2Target2 target2)
	{
		this.target2 = target2;
	}

}
