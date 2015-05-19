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


/**
 * {@link PropertyInterceptor} for converting HybrisEnumValue to String.
 * 
 * @see StringToHybrisEnumValueConverter
 */
public class HybrisEnumValueToStringConverter implements PropertyInterceptor<HybrisEnumValue, String>
{

	@Override
	public String intercept(final PropertyContext ctx, final HybrisEnumValue source)
	{
		String result = null;
		if (source != null)
		{
			// There are two two different type of "Hybris enums" which are available
			// 1a) plain java-enum (isEnum=true): 
			//     'code' and enum 'name' are not equal (but seems to be after toUpperCase)
			//     convert back with Enum.valueOf(name)
			// 1b) old-fashioned style; just a class with generated constants
			//     enum name is not available
			//     convert back invokation of static method valueOf(code) from class holding the constants  
			//result = source.getClass().isEnum() ? ((Enum) source).name() : source.getCode();

			result = source.getCode();
		}
		return result;
	}

}
