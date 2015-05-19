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

import org.apache.log4j.Logger;

import de.hybris.platform.webservices.objectgraphtransformer.YObjectGraphContext;
import de.hybris.platform.webservices.objectgraphtransformer.YObjectGraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.basic.BasicNodeFilter;
import de.hybris.platform.webservices.util.objectgraphtransformer.basic.ModifiedPropertyFilter;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.DefaultNodeMapping;


/**
 * Abstract base implementation for {@link YResponseBuilder}
 * 
 * @param <RESOURCE>
 *           type of resource value which the response is generated for
 */
public abstract class AbstractYResponseBuilder<RESOURCE, REQUEST, RESPONSE> extends
		AbstractResponseBuilder<RESOURCE, REQUEST, RESPONSE>
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(AbstractYResponseBuilder.class);

	// the graph which defines this resource value and is used for transformations DTO<->Model. 
	private YObjectGraphTransformer objectGraph = null;
	// graphcontext for dto to model operations 
	private YObjectGraphContext dtoToModelCtx = null;
	// graphcontext for model to dto operations 
	private YObjectGraphContext modelToDtoCtx = null;

	protected AbstractYResponseBuilder(final Operation operation)
	{
		super(operation);
	}

	protected AbstractYResponseBuilder(final AbstractYResource resource, final Operation operation)
	{
		super(resource, operation);
	}

	@Override
	public void setResource(final AbstractResource resource)
	{
		this.objectGraph = ((AbstractYResource) resource).getObjectGraph();
		super.setResource(resource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.AbstractResponseBuilder#getResource()
	 */
	@Override
	public AbstractYResource getResource()
	{
		return (AbstractYResource) super.getResource();
	}

	public void setObjectGraph(final YObjectGraphTransformer objectGraph)
	{
		this.objectGraph = objectGraph;
	}
	
	@Override
	protected abstract RESPONSE createResponseEntity(final RESOURCE resourceEntity, final REQUEST requestEntity) throws Exception;



	public <T2> T2 modelToDto(final Object source)
	{
		return (T2) modelToDto(source, null, 1);
	}

	public <T2> T2 modelToDto(final Object source, final Object target)
	{
		return (T2) modelToDto(source, target, 1);
	}

	public <T2> T2 modelToDto(final Object source, final int depth)
	{
		return (T2) modelToDto(source, null, depth);
	}

	/**
	 * Transforms a model into a DTO.
	 * 
	 * @param <T2>
	 * @param source
	 * @param target
	 * @param depth
	 */
	public <T2> T2 modelToDto(final Object source, final Object target, final int depth)
	{
		final BasicNodeFilter filter = new BasicNodeFilter();
		filter.setDepth(depth);

		final GraphContext ctx = this.getModelToDtoContext();
		ctx.getNodeFilterList().add(filter);

		//((ModelToDtoGraphContext) ctx).setUriInfo(this.resource.getUriInfo());
		T2 result = null;

		try
		{
			result = (T2) objectGraph.transform(ctx, source, target);
		}
		catch (final Exception e)
		{
			this.processException("Error generating dto-graph from " + source.getClass().getSimpleName(), e);
		}

		this.modelToDtoCtx = null;

		return result;
	}


	/**
	 * Transforms a DTO into a model. Transforming includes any convert/create/merge operation which is needed.
	 * <p/>
	 * e.g. if passed model is null, an appropriate one gets created, if passed model is not null, dto gets merged into
	 * that model.
	 * 
	 * @param <MODEL>
	 *           type of result model
	 * @param dto
	 *           the DTO which has to be transformed into a model
	 * @param model
	 *           the target model or 'null'
	 */
	protected <MODEL> MODEL dtoToModel(final Object dto, final MODEL model)
	{
		MODEL result = null;
		YObjectGraphContext ctx = null;

		try
		{
			// prepare a graphcontext with ModifiedProperties filter
			ctx = getDtoToModelContext();
			ctx.getPropertyFilterList().add(new ModifiedPropertyFilter());
			ctx.setGraphWasNewlyCreated(Boolean.valueOf(model == null));

			// merge (or create) DTO into MODEL
			result = objectGraph.transform(ctx, dto, model);

			// set OPERATION flag when not already done before
			if (this.operation == Operation.CREATE_OR_UPDATE || this.operation == null)
			{
				this.operation = (ctx.getGraphWasNewlyCreated().booleanValue()) ? Operation.CREATE : Operation.UPDATE;
			}
		}
		catch (final Exception e)
		{
			throw new YWebservicesException("Error processing request (invalid or malformed content?)", e);
		}

		return result;
	}

	public YObjectGraphContext getDtoToModelContext()
	{
		if (this.dtoToModelCtx == null)
		{
			this.dtoToModelCtx = (YObjectGraphContext) objectGraph.createGraphContext();
			this.dtoToModelCtx.setRequestResource(getResource());
		}
		return this.dtoToModelCtx;
	}

	public YObjectGraphContext getModelToDtoContext()
	{
		if (this.modelToDtoCtx == null)
		{
			this.modelToDtoCtx = (YObjectGraphContext) objectGraph.createSecondGraphContext();
			this.modelToDtoCtx.setRequestResource(getResource());
		}
		return this.modelToDtoCtx;
	}

	/**
	 * Returns a {@link DefaultNodeMapping} instance which can be freely customized and is only valid for transformations
	 * within this resource.
	 * 
	 * @param nodeType
	 *           type of node
	 */
	public DefaultNodeMapping getLocalNodeConfig(final Class nodeType)
	{
		return getLocalNodeConfig(nodeType, 0);
	}

	/**
	 * Returns a {@link DefaultNodeMapping} instance which can be freely customized and is only valid for transformations
	 * within this resource.
	 * 
	 * @param nodeType
	 *           type of node
	 * @param distance
	 *           distance which this node is positioned (same node types can be used in different distances and with
	 *           different configurations)
	 */
	public DefaultNodeMapping getLocalNodeConfig(final Class nodeType, final int distance)
	{
		final GraphContext ctx = this.getModelToDtoContext();
		DefaultNodeMapping result = (DefaultNodeMapping) ctx.getConfiguration().getNodeMapping(distance, nodeType);
		if (result == null)
		{
			result = new DefaultNodeMapping(objectGraph, objectGraph.getNodeMapping(nodeType), false);
			ctx.getConfiguration().addNodeMapping(distance, result);
		}

		return result;

	}



}
