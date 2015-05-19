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
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.external.PartOfRelation;


/**
 * Default partOf relation implementation
 *
 */
public class PartOfRelationImpl implements PartOfRelation
{
	private String instId;
	private String parentInstId;
	private String posNr;
	private String objectType;
	private String objectKey;
	private String classType;
	private String author;


	@Override
	public String getInstId()
	{
		return instId;
	}

	@Override
	public void setInstId(final String instId)
	{
		this.instId = instId;
	}

	@Override
	public String getParentInstId()
	{
		return parentInstId;
	}

	@Override
	public void setParentInstId(final String parentInstId)
	{
		this.parentInstId = parentInstId;
	}

	@Override
	public String getPosNr()
	{
		return posNr;
	}

	@Override
	public void setPosNr(final String posNr)
	{
		this.posNr = posNr;
	}

	@Override
	public String getObjectType()
	{
		return objectType;
	}

	@Override
	public void setObjectType(final String objectType)
	{
		this.objectType = objectType;
	}

	@Override
	public String getObjectKey()
	{
		return objectKey;
	}

	@Override
	public void setObjectKey(final String objectKey)
	{
		this.objectKey = objectKey;
	}

	@Override
	public String getClassType()
	{
		return classType;
	}

	@Override
	public void setClassType(final String classType)
	{
		this.classType = classType;
	}

	@Override
	public String getAuthor()
	{
		return author;
	}

	@Override
	public void setAuthor(final String author)
	{
		this.author = author;
	}

}
