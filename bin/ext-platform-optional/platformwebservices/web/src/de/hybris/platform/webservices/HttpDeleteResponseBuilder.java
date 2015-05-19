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



public class HttpDeleteResponseBuilder<RESOURCE, RESPONSE> extends AbstractYResponseBuilder<RESOURCE, Object, RESPONSE>
{
	public HttpDeleteResponseBuilder()
	{
		super(Operation.DELETE);
	}

	public HttpDeleteResponseBuilder(final AbstractYResource resource)
	{
		super(resource, Operation.DELETE);
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.sandbox.AbstractResponseProcessor#createResponseEntity(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	protected RESPONSE createResponseEntity(final RESOURCE resourceEntity, final Object requestEntity) throws Exception
	{
		this.beforeProcessing(resourceEntity);
		try
		{
			this.deleteResource(resourceEntity);
		}
		catch (final Exception e)
		{
			processException("Error deleting resource", e);
		}
		this.afterProcessing(resourceEntity);
		return null;
	}

	public void beforeProcessing(final RESOURCE resrcEntity)
	{
		getResource().beforeDelete(resrcEntity);
	}

	public void deleteResource(final RESOURCE resrcEntity) throws Exception
	{
		getResource().deleteResource(resrcEntity);
	}

	public void afterProcessing(final RESOURCE resrcEntity)
	{
		getResource().afterDelete(resrcEntity);
	}



}
