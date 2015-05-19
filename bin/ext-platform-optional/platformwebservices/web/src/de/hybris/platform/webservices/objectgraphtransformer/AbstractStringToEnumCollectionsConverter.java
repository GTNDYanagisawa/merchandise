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
package de.hybris.platform.webservices.objectgraphtransformer;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;


/**
 * Base class for converters used to convert Collections&lt;String&gt; to Collections&lt;HybrisEnumValue&gt;. Uses
 * {@link StringToHybrisEnumValueConverter} for converting single elements.
 */
public abstract class AbstractStringToEnumCollectionsConverter<S extends Collection<String>, T extends Collection<HybrisEnumValue>>
		implements PropertyInterceptor<S, T>
{
	private final StringToHybrisEnumValueConverter elementConverter = new StringToHybrisEnumValueConverter();

	public void intercept(final PropertyContext propertyCtx, final Collection<String> source, final T result)
	{
		for (final String enumCode : source)
		{
			result.add(convertElement(propertyCtx, enumCode));
		}
	}

	protected Class getEnumType(final PropertyContext propertyCtx)
	{
		final PropertyMapping pCfg = propertyCtx.getPropertyMapping();

		final Method method = pCfg.getTargetConfig().getWriteMethod();

		final ParameterizedType methodArgumentType = (ParameterizedType) (method.getGenericParameterTypes()[0]);
		final Type[] collectionTypeArguments = methodArgumentType.getActualTypeArguments();

		return (Class<?>) collectionTypeArguments[0];
	}

	private HybrisEnumValue convertElement(final PropertyContext propertyCtx, final String enumCode)
	{
		final Class<?> enumType = getEnumType(propertyCtx);
		return elementConverter.getEnumValue(enumType, enumCode);
	}
}
