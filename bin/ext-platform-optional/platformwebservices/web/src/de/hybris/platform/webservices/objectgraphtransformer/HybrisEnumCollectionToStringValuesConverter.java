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

import java.util.ArrayList;
import java.util.Collection;


/**
 * {@link PropertyInterceptor} for converting a Collection&lt;HybrisEnumValue&gt; to Collection&lt;String&gt;.
 * 
 * @see StringCollectionToHybrisEnumValuesConverter
 */
public class HybrisEnumCollectionToStringValuesConverter extends
		AbstractEnumToStringCollectionsConverter<Collection<HybrisEnumValue>, Collection<String>>
{
	@Override
	public Collection<String> intercept(final PropertyContext propertyCtx, final Collection<HybrisEnumValue> source)
	{
		final Collection<String> result;
		if (source == null)
		{
			result = null;
		}
		else
		{
			result = new ArrayList<String>();
			super.intercept(propertyCtx, source, result);
		}
		return result;
	}
}
