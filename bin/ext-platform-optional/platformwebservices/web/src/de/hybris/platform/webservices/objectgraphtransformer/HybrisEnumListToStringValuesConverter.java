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
import java.util.List;


/**
 * {@link PropertyInterceptor} for converting a List&lt;HybrisEnumValue&gt; to List&lt;String&gt;.
 * 
 * @see StringListToHybrisEnumValuesConverter
 */
public class HybrisEnumListToStringValuesConverter extends
		AbstractEnumToStringCollectionsConverter<List<HybrisEnumValue>, List<String>>
{
	@Override
	public List<String> intercept(final PropertyContext propertyCtx, final List<HybrisEnumValue> source)
	{
		final List<String> result;
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
