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
package de.hybris.platform.webservices;

import de.hybris.platform.webservices.paging.PageInfoCtx;


public abstract class AbstractCollectionResource<RESOURCE> extends AbstractYResource<RESOURCE>
{
	public AbstractCollectionResource(final String composedTypeName)
	{
		super(composedTypeName);
	}


	/**
	 * Convenience method which just checks, whether this resources parent is not null. A resource has a parent resource,
	 * whenever it was called as child by another resource.
	 */
	protected boolean isRoot()
	{
		return getParentResource() == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.AbstractResource#readResource(java.lang.String)
	 */
	@Override
	protected RESOURCE readResource(final String resourceId) throws Exception
	{
		super.prepareJaloSession();
		if (isRoot())
		{
			return readResourcesByType(getResourceType());
		}
		else
		{
			return getResourceValue();
		}
	}

	protected RESOURCE readResourcesByType(final String typeName)
	{
		RESOURCE result = null;

		final PageInfoCtx pageCtx = getPagingStrategy().findPageContext(uriInfo.getPath(), uriInfo.getQueryParameters());
		result = (RESOURCE) getPagingStrategy().executeRootCollectionPaging(pageCtx, typeName);

		return result;
	}
}
