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

import de.hybris.platform.util.Config;
import de.hybris.platform.webservices.AbstractSecurityStrategy;
import de.hybris.platform.webservices.SecurityStrategy;
import de.hybris.platform.webservices.paging.PagingStrategy;
import de.hybris.platform.webservices.paging.PageInfoCtx;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.CollectionNodeProcessor;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.NodeContextImpl;

import java.util.Collection;



/**
 * This class processes Collection nodes with security attribute checking of the elements of collections.
 */
public class YCollectionNodeProcessor extends CollectionNodeProcessor
{
	@Override
	public <T> T process(final NodeContextImpl nodeCtx, final Object source, final T target)
	{
		// TODO: optimize
		if (((YObjectGraphContext) nodeCtx.getGraphContext()).isDtoToModelTransformation())
		{
			return super.process(nodeCtx, source, target);
		}

		T result = null;
		final YObjectGraphContext graphCtx = (YObjectGraphContext) nodeCtx.getGraphContext();
		final PagingStrategy wsPaging = graphCtx.getRequestResource().getPagingStrategy();

		//achieving targets
		result = super.process(nodeCtx, source, target);

		//get collection element name from (root or property)collection
		final String collectionPropertyName = nodeCtx.getParentContext() == null ? graphCtx.getUriInfo().getPath() : nodeCtx
				.getParentContext().getPropertyMapping().getId();

		final PageInfoCtx wsPageCtx = wsPaging.findPageContext(collectionPropertyName, graphCtx.getUriInfo().getQueryParameters());

		// This if statement specifies paging behavior.
		// description - don't execute security at the collection level if:
		// 1) paging is in use and 
		// 2) complete_size_per_page is required.
		if (wsPageCtx == null || !Config.getBoolean("webservices.paging.complete_size_per_page", true))
		{
			final SecurityStrategy securityStrategy = graphCtx.getRequestResource().getSecurityStrategy();
			//type based attribute security level(for collection nodes)
			result = (T) ((securityStrategy == null) ? result : ((AbstractSecurityStrategy) securityStrategy)
					.performDtoReadOperationAllowed((Collection<T>) result));
		}

		return result;
	}
}
