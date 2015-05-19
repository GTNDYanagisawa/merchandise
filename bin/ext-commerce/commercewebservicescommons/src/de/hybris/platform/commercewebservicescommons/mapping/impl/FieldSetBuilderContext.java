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
 */
package de.hybris.platform.commercewebservicescommons.mapping.impl;

import java.util.HashMap;
import java.util.Map;


/**
 * Class storing additional information needed by
 * {@link de.hybris.platform.commercewebservicescommons.mapping.FieldSetBuilder}
 * 
 */
public class FieldSetBuilderContext
{
	private Map<String, Class> typeVariableMap;
	private int recurrencyLevel = 4;
	private Map<Class, Integer> recurrencyMap = new HashMap<>();
	private int fieldCounter = 0;
	private int maxFieldSetSize = 50000;

	public void addToRecurrencyMap(final Class clazz)
	{
		if (recurrencyMap != null)
		{
			if (recurrencyMap.containsKey(clazz))
			{
				Integer value = recurrencyMap.get(clazz);
				value = Integer.valueOf(value.intValue() + 1);
				recurrencyMap.put(clazz, value);
			}
			else
			{
				recurrencyMap.put(clazz, Integer.valueOf(1));
			}
		}
	}

	public void removeFromRecurrencyMap(final Class clazz)
	{
		if (recurrencyMap != null)
		{
			if (recurrencyMap.containsKey(clazz))
			{
				Integer value = recurrencyMap.get(clazz);
				value = Integer.valueOf(value.intValue() - 1);
				recurrencyMap.put(clazz, value);
			}
		}
	}

	public boolean isRecurencyLevelExceeded(final Class clazz)
	{
		if (recurrencyMap != null)
		{
			final Integer value = recurrencyMap.get(clazz);
			if (value != null)
			{
				return (value.intValue() > recurrencyLevel);
			}
		}

		return false;
	}

	public void resetRecurrencyMap()
	{
		recurrencyMap = new HashMap<>();
	}

	public Map<String, Class> getTypeVariableMap()
	{
		return typeVariableMap;
	}

	public void setTypeVariableMap(final Map<String, Class> typeVariableMap)
	{
		this.typeVariableMap = typeVariableMap;
	}

	public int getRecurrencyLevel()
	{
		return recurrencyLevel;
	}

	public void setRecurrencyLevel(final int recurrencyLevel)
	{
		this.recurrencyLevel = recurrencyLevel;
	}

	public void resetFieldCounter()
	{
		fieldCounter = 0;
	}

	public void incrementFieldCounter()
	{
		fieldCounter++;
	}

	public boolean isMaxFieldSetSizeExceeded()
	{
		return fieldCounter > maxFieldSetSize;
	}

	public int getMaxFieldSetSize()
	{
		return maxFieldSetSize;
	}

	public void setMaxFieldSetSize(final int maxFieldSetSize)
	{
		this.maxFieldSetSize = maxFieldSetSize;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final FieldSetBuilderContext that = (FieldSetBuilderContext) o;

		if (recurrencyLevel != that.recurrencyLevel)
		{
			return false;
		}
		if (typeVariableMap != null ? !typeVariableMap.equals(that.typeVariableMap) : that.typeVariableMap != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = typeVariableMap != null ? typeVariableMap.hashCode() : 0;
		result = 31 * result + recurrencyLevel;
		return result;
	}
}
