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

import de.hybris.platform.webservices.RestResource;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;

import java.lang.reflect.Method;


public class UriPropertyInterceptor implements PropertyInterceptor<Object, String>
{

	@Override
	public String intercept(final PropertyContext propertyCtx, final Object propertyValue)
	{
		String resultUri = null;
		final NodeContext nodeContext = propertyCtx.getParentContext();

		// get some values 
		final RestResource resource = ((YObjectGraphContext) nodeContext.getGraphContext()).getRequestResource();

		// from configured UID property names, take the first one
		final PropertyConfig[] uidProps = nodeContext.getNodeMapping().getTargetConfig().getUidProperties();
		final String property = uidProps[0].getName();

		// get UID property value ... 
		final Object modelValue = nodeContext.getSourceNodeValue();

		// ... by invoking method via reflection
		final Method readProp = nodeContext.getNodeMapping().getSourceConfig().getProperties().get(property).getReadMethod();
		try
		{
			final Object propValue = readProp.invoke(modelValue, (Object[]) null);

			// ... and finally build the result URI
			final YContextResourceResolver contextResourceResolver = new YContextResourceResolver(resource.getServiceLocator());
			final String baseUrl = resource.getUriInfo().getBaseUri().toString();
			contextResourceResolver.setBaseUrl(baseUrl);
			resultUri = contextResourceResolver.buildUri(modelValue, propValue);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		return resultUri;
	}

}
