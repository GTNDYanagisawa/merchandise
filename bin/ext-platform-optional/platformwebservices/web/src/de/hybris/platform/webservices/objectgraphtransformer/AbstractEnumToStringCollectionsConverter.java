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

import java.util.Collection;


/**
 * Base class for converters used to convert from Collection&lt;HybrisEnumValue&gt; to Collection&lt;String&gt;. Uses
 * {@link HybrisEnumValueToStringConverter} for converting single elements.
 */
public abstract class AbstractEnumToStringCollectionsConverter<S extends Collection<HybrisEnumValue>, T extends Collection<String>>
		implements PropertyInterceptor<S, T>
{
	private final HybrisEnumValueToStringConverter elementConverter = new HybrisEnumValueToStringConverter();

	public void intercept(final PropertyContext propertyCtx, final Collection<HybrisEnumValue> source, final T result)
	{
		for (final HybrisEnumValue enumValue : source)
		{
			result.add(elementConverter.intercept(propertyCtx, enumValue));
		}
	}
}
