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
package com.sap.wec.adtreco.bo.impl;

import java.util.Date;


/**
 *
 */
public class SAPInitiative
{
	protected String id;
	protected String name;
	protected String description;
	protected String status;
	protected String memberCount = "";
	protected Date startDate = null;
	protected Date endDate = null;



	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(final Date startDate)
	{
		this.startDate = startDate;
	}

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(final Date endDate)
	{
		this.endDate = endDate;
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
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *           the description to set
	 */
	public void setDescription(final String description)
	{
		this.description = description;
	}

	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status
	 *           the status to set
	 */
	public void setStatus(final String status)
	{
		this.status = status;
	}

	/**
	 * @return the memberCount
	 */
	public String getMemberCount()
	{
		return memberCount;
	}

	/**
	 * @param memberCount
	 *           the memberCount to set
	 */
	public void setMemberCount(final String memberCount)
	{
		this.memberCount = memberCount;
	}

	@Override
	public String toString()
	{
		return "id: " + id + " name: " + name + " description: " + description + " status: " + status + " Member Count: "
				+ memberCount;
	}
}
