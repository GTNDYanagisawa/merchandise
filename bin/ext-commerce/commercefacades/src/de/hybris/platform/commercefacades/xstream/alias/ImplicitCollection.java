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
package de.hybris.platform.commercefacades.xstream.alias;

public class ImplicitCollection
{
	private String fieldName;
	private Class ownerType;
	private String itemFieldName;
	private Class itemType;

	public String getFieldName()
	{
		return fieldName;
	}

	public void setFieldName(final String fieldName)
	{
		this.fieldName = fieldName;
	}

	public Class getOwnerType()
	{
		return ownerType;
	}

	public void setOwnerType(final Class ownerType)
	{
		this.ownerType = ownerType;
	}

	public String getItemFieldName()
	{
		return itemFieldName;
	}

	public void setItemFieldName(final String itemFieldName)
	{
		this.itemFieldName = itemFieldName;
	}

	public Class getItemType()
	{
		return itemType;
	}

	public void setItemType(final Class itemType)
	{
		this.itemType = itemType;
	}



}
