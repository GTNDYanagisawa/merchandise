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


@GraphNode(target = Tf2Target1.class, uidProperties = "id")
public class Tf2Source1
{
	private String id = "0";
	private Tf2Source2 dto2 = null;

	public Tf2Source1()
	{
		//
	}

	public Tf2Source1(final String id)
	{
		super();
		this.id = id;
	}



	/**
	 * @return the dto2
	 */
	public Tf2Source2 getDto2()
	{
		return dto2;
	}

	/**
	 * @param dto2
	 *           the dto2 to set
	 */
	public void setDto2(final Tf2Source2 dto2)
	{
		this.dto2 = dto2;
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

}
