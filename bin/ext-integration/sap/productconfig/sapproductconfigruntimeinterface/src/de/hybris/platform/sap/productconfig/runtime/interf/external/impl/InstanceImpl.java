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

import de.hybris.platform.sap.productconfig.runtime.interf.external.Instance;


/**
 * External representation of an instance.
 * 
 */
public class InstanceImpl implements Instance
{

	private String id;
	private String objectType;
	private String objectKey;
	private String objectText;
	private String classType;
	private String author;
	private String quantity;
	private String quantityUnit;
	private boolean consistent;
	private boolean complete;

	@Override
	public void setId(final String id)
	{
		this.id = id;

	}


	@Override
	public String getId()
	{
		return this.id;
	}


	@Override
	public void setObjectType(final String objectType)
	{
		this.objectType = objectType;

	}


	@Override
	public String getObjectType()
	{
		return objectType;
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
	public String getObjectText()
	{
		return objectText;
	}


	@Override
	public void setObjectText(final String objectText)
	{
		this.objectText = objectText;
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


	@Override
	public String getQuantity()
	{
		return quantity;
	}


	@Override
	public void setQuantity(final String quantity)
	{
		this.quantity = quantity;
	}


	@Override
	public String getQuantityUnit()
	{
		return quantityUnit;
	}


	@Override
	public void setQuantityUnit(final String quantityUnit)
	{
		this.quantityUnit = quantityUnit;
	}


	@Override
	public boolean isConsistent()
	{
		return consistent;
	}


	@Override
	public void setConsistent(final boolean consistent)
	{
		this.consistent = consistent;
	}


	@Override
	public boolean isComplete()
	{
		return complete;
	}


	@Override
	public void setComplete(final boolean complete)
	{
		this.complete = complete;
	}

}
