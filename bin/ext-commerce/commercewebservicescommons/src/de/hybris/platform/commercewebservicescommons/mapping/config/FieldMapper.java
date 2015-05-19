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
package de.hybris.platform.commercewebservicescommons.mapping.config;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


/**
 * Class storing information needed for creation {@link ma.glasnost.orika.Mapper}<br/>
 * Spring Bean of that type will cause registering {@link ma.glasnost.orika.Mapper} in
 * {@link ma.glasnost.orika.MapperFactory}
 */
public class FieldMapper
{
	/**
	 * Class of the source object
	 */
	private Class sourceClass;
	/**
	 * Class of the destination object
	 */
	private Class destClass;
	/**
	 * Arguments of source type<br/>
	 * e.g. for type class ProductSearchPageData<SearchStateData, ProductData> it is
	 * {SearchStateData.class,ProductData.class}
	 */
	private List<Class> sourceClassArguments;

	/**
	 * Arguments of destination type<br\>
	 * e.g. for type class like ProductSearchPageData<SearchStateData, ProductData> it is
	 * {SearchStateData.class,ProductData.class}
	 */
	private List<Class> destClassArguments;

	/**
	 * Mapping between fields of source class and destination class
	 */
	private Map<String, String> fieldMapping;


	public Class getSourceClass()
	{
		return sourceClass;
	}

	public void setSourceClass(final Class sourceClass)
	{
		this.sourceClass = sourceClass;
	}

	public Class getDestClass()
	{
		return destClass;
	}

	public void setDestClass(final Class destClass)
	{
		this.destClass = destClass;
	}

	public Map<String, String> getFieldMapping()
	{
		return fieldMapping;
	}

	public void setFieldMapping(final Map<String, String> fieldMapping)
	{
		this.fieldMapping = fieldMapping;
	}

	public List<Class> getSourceClassArguments()
	{
		return sourceClassArguments;
	}

	public void setSourceClassArguments(final List<Class> sourceClassArguments)
	{
		this.sourceClassArguments = sourceClassArguments;
	}

	public List<Class> getDestClassArguments()
	{
		return destClassArguments;
	}

	public void setDestClassArguments(final List<Class> destClassArguments)
	{
		this.destClassArguments = destClassArguments;
	}

	public Type[] getSourceActualTypeArguments()
	{
		if (this.sourceClassArguments != null)
		{
			final Type[] arguments = this.sourceClassArguments.toArray(new Type[0]);
			return arguments;
		}
		return null;
	}

	public Type[] getDestActualTypeArguments()
	{
		if (this.sourceClassArguments != null)
		{
			final Type[] arguments = this.destClassArguments.toArray(new Type[0]);
			return arguments;
		}
		return null;
	}

}
