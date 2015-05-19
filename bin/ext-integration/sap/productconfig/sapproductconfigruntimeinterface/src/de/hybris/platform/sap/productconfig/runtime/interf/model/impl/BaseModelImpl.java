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

package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.BaseModel;

import java.util.HashMap;
import java.util.Map;


public class BaseModelImpl implements BaseModel
{
	private Map<String, String> extensionMap = new HashMap<String, String>();

	@Override
	public Map<String, String> getExtensionMap()
	{
		return extensionMap;
	}

	@Override
	public void setExtensionMap(final Map<String, String> extensionMap)
	{
		this.extensionMap = extensionMap;
	}

	@Override
	public void putExtensionData(final String key, final String value)
	{
		extensionMap.put(key, value);
	}

	@Override
	public String getExtensionData(final String key)
	{
		String value = null;
		if (extensionMap != null)
		{
			value = extensionMap.get(key);
		}
		return value;
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((extensionMap == null) ? 0 : extensionMap.hashCode());
		return result;
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final BaseModelImpl other = (BaseModelImpl) obj;
		if (extensionMap == null)
		{
			if (other.extensionMap != null)
			{
				return false;
			}
		}
		else if (!extensionMap.equals(other.extensionMap))
		{
			return false;
		}
		return true;
	}

	@Override
	public BaseModel clone()
	{
		BaseModel clonedBaseModel;
		try
		{
			clonedBaseModel = (BaseModel) super.clone();
		}
		catch (final CloneNotSupportedException ex)
		{
			throw new IllegalArgumentException("Could not clone base model", ex);
		}

		final HashMap<String, String> hashMap = (HashMap<String, String>) extensionMap;
		final Map<String, String> clonedExtensionMap = (Map<String, String>) hashMap.clone();

		clonedBaseModel.setExtensionMap(clonedExtensionMap);

		return clonedBaseModel;
	}
}
