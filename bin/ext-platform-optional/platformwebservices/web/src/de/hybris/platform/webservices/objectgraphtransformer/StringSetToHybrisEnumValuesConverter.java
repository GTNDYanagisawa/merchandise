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

import java.util.HashSet;
import java.util.Set;


/**
 * {@link PropertyInterceptor} for converting a Set&lt;String&gt; to Set&lt;HybrisEnumValue&gt;.
 * 
 * @see HybrisEnumSetToStringValuesConverter
 */
public class StringSetToHybrisEnumValuesConverter extends AbstractStringToEnumCollectionsConverter<Set<String>, Set<HybrisEnumValue>>
{
	@Override
	public Set<HybrisEnumValue> intercept(final PropertyContext propertyCtx, final Set<String> source)
	{
		final Set<HybrisEnumValue> result;
		if (source == null)
		{
			result = null;
		}
		else
		{
			result = new HashSet<HybrisEnumValue>();
			super.intercept(propertyCtx, source, result);
		}
		return result;
	}
}
