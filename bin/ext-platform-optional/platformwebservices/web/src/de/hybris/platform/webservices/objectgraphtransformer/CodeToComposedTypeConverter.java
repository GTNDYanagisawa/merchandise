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

import de.hybris.platform.core.dto.type.ComposedTypeDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;


public class CodeToComposedTypeConverter implements PropertyInterceptor<String, ComposedTypeDTO>
{
	@Override
	public ComposedTypeDTO intercept(final PropertyContext ctx, final String source)
	{
		// that interceptor was used in the past to provide a virtual mapping between ComposedTypeModel and String
		// now as we have a DTo for each type, it may be not needed any more
		// however, it's still there to have a String representation for a DTO
		final ComposedTypeDTO result = new ComposedTypeDTO();
		result.setCode(source);
		return result;
	}

}
