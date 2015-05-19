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

import de.hybris.platform.core.PK;
import de.hybris.platform.webservices.InternalServerErrorException;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;


public class PkToLongConverter implements PropertyInterceptor
{

	@Override
	public Long intercept(final PropertyContext ctx, final Object source)
	{
		Long result = null;
		if (source instanceof PK)
		{
			result = Long.valueOf(((PK) source).getLongValue());
		}
		else
		{
			if (source != null)
			{
				throw new InternalServerErrorException("Converter expected " + PK.class.getSimpleName() + " but got "
						+ source.getClass().getSimpleName());
			}
		}
		return result;
	}

}
