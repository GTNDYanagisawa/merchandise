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
package de.hybris.platform.webservices.util.objectgraphtransformer.impl;




import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.webservices.objectgraphtransformer.YObjectGraphContext;
import de.hybris.platform.webservices.paging.PagingStrategy;
import de.hybris.platform.webservices.paging.PageInfoCtx;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphException;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeProcessor;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyFilter;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


public class PropertyProcessorImpl implements PropertyProcessor
{
	private static final Logger log = Logger.getLogger(PropertyProcessorImpl.class);

	@Override
	public void process(final PropertyContext pCtx, final Object source, final Object target)
	{
		// TODO: type safety?
		final PropertyContextImpl pCtxImpl = (PropertyContextImpl) pCtx;
		final AbstractPropertyMapping propMapping = (AbstractPropertyMapping) pCtx.getPropertyMapping();


		final GraphContext graphCtx = pCtx.getGraphContext();

		// lazy compile when necessary
		if (!propMapping.isInitialized())
		{
			propMapping.initialize(DefaultPropertyMapping.COMPLIANCE_LEVEL_HIGH);
		}

		// DEBUG output
		if (log.isDebugEnabled() && ((AbstractNodeMapping) pCtxImpl.getPropertyMapping().getParentMapping()).isDebugEnabled())
		{
			final String logMsg = ((DefaultPropertyMapping) propMapping).toExtString();
			final String pre = "[" + pCtx.getParentContext().getRealDistance() + ":" + propMapping.getId() + "] config: ";
			log.debug(pre + logMsg);
		}

		// read property value from source node
		Object value = null;
		boolean isFiltered = false;
		if (!pCtx.getPropertyMapping().isVirtual())
		{
			value = this.readValueFromSource(pCtxImpl, source);

			// check filters
			// ... use global configured filters from GraphContext
			final List<PropertyFilter> globalFilters = graphCtx.getPropertyFilterList();
			// ... and local configured ones from current PropertyMapping
			final List<PropertyFilter> localFilters = propMapping.getPropertyFilters();
			// ... and apply
			isFiltered = this.isFilterd(pCtx, value, globalFilters, localFilters);

			// node transformation when necessary
			if (!isFiltered && value != null)
			{
				if (propMapping.isNode())
				{
					final AbstractNodeMapping nodeMapping = (AbstractNodeMapping) pCtxImpl.getChildNodeLookup().get(value.getClass());

					if (nodeMapping != null)
					{
						// check node filters
						isFiltered = this.isFilterd(pCtx, value, graphCtx.getNodeFilterList(), Collections.EMPTY_LIST);
						if (!isFiltered)
						{
							final NodeProcessor trans = nodeMapping.getProcessor();
							final NodeContext nodeCtx = pCtxImpl.createChildNodeContext(nodeMapping, value);
							value = trans.process(nodeCtx, value, null);
						}
					}
					else
					{
						// this should never happen
						// if yes an exception is not really necessary (log.error should do the job as well)
						throw new GraphException("Illegal graph config: need to transform a property but can't find a transformer");
					}
				}
			}
		}

		if (log.isDebugEnabled())
		{
			try
			{
				final String pre = "[" + pCtx.getParentContext().getRealDistance() + ":" + propMapping.getId() + "] actual: ";
				String read = "[virtual]";
				if (!propMapping.isVirtual())
				{
					final Method readMethod = propMapping.getSourceConfig().getReadMethod();
					read = source.getClass().getSimpleName() + "#" + readMethod.getName() + "():"
							+ source.getClass().getMethod(readMethod.getName()).getReturnType().getSimpleName();
				}
				final String write = target.getClass().getSimpleName() + "#???";
				log.debug(pre + read + " -> " + write + "; (value:" + ((value != null) ? value.getClass().getSimpleName() : "null")
						+ ")");
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}

		// can be filtered either by a PropertyFilter or, when node, additionally by a NodeFilter
		if (!isFiltered)
		{
			// target value is 'null' when any read-error occurs or conversion has failed
			// in any other case it's a converted source value or source value itself 
			this.writeValueToTarget(pCtxImpl, target, value);
		}

	}

	protected boolean isFilterd(final PropertyContext pCtx, final Object value, final List<PropertyFilter> globalFilters,
			final List<PropertyFilter> localFilters)
	{
		boolean isFiltered = false;

		PropertyFilter filter = null;

		// check first list of filters
		for (final Iterator<PropertyFilter> iter = globalFilters.iterator(); iter.hasNext() && !isFiltered;)
		{
			filter = iter.next();
			isFiltered = filter.isFiltered(pCtx, value);
		}

		// check second list of filters
		for (final Iterator<PropertyFilter> iter = localFilters.iterator(); iter.hasNext() && !isFiltered;)
		{
			filter = iter.next();
			isFiltered = filter.isFiltered(pCtx, value);
		}

		if (log.isDebugEnabled() && isFiltered)
		{
			log.debug("Filter matched: " + filter.getClass().getSimpleName());
		}

		return isFiltered;
	}

	/**
	 * Reads a property value from source node + (paging mechanism).
	 * 
	 * @param pCtx
	 *           {@link PropertyContext}
	 * @param source
	 *           source node
	 * @return value
	 */
	private Object readValueFromSource(final PropertyContextImpl pCtx, final Object source)
	{
		Object value = preparePaging(pCtx, source);

		final PropertyMapping pMap = pCtx.getPropertyMapping();
		// optionally convert (take interceptor from source graph property)
		if (pMap.getSourceConfig().getReadInterceptor() != null)
		{
			value = pMap.getSourceConfig().getReadInterceptor().intercept(pCtx, value);
		}

		return value;
	}

	/**
	 * Reads a property value from source node when paging mechanism is off. Otherwise, the paging strategy gets a
	 * property value by flexible search execution in order to improve performance.
	 * <p/>
	 * 
	 * @param pCtx
	 *           {@link PropertyContext}
	 * @param source
	 *           source node
	 * 
	 * @return reads a property value from source node when paging mechanism is off. Otherwise, the paging strategy gets
	 *         a property value by flexible search execution in order to improve performance.
	 */
	private Object preparePaging(final PropertyContextImpl pCtx, final Object source)
	{
		Object value = null;
		final PropertyMapping pMap = pCtx.getPropertyMapping();

		// read from source graph
		final Method readMethod = pMap.getSourceConfig().getReadMethod();
		try
		{
			if (pCtx.getGraphContext() instanceof YObjectGraphContext)
			{
				final YObjectGraphContext graphCtx = (YObjectGraphContext) pCtx.getGraphContext();

				//ModelToDto transformation and collection property required for preparing PAGING
				if (graphCtx.isModelToDtoTransformation() && Collection.class.isAssignableFrom(pMap.getSourceConfig().getReadType()))
				{
					final PagingStrategy wsPaging = graphCtx.getRequestResource().getPagingStrategy();
					final PageInfoCtx pageCtx = wsPaging.findPageContext(pMap.getId(), graphCtx.getUriInfo().getQueryParameters());
					//pageCtx required for preparing PAGING
					if (pageCtx != null)
					{
						// get attribute descriptor of that collection property
						final TypeService typeService = graphCtx.getServices().getTypeService();
						final AttributeDescriptorModel attrDescriptor = typeService.getAttributeDescriptor(typeService
								.getComposedType(source.getClass()), pMap.getId());

						// flexible style paging for RelationTypes
						if (attrDescriptor instanceof RelationDescriptorModel)
						{
							final RelationDescriptorModel relDesc = (RelationDescriptorModel) attrDescriptor;
							value = wsPaging.executeRelationTypePaging(pageCtx, relDesc, source);
						}
						// old style paging for CollectionTypes 
						// weak performance, collection elements are all loaded into memory before being paginated
						else
						{
							value = readMethod.invoke(source, (Object[]) null);
							value = wsPaging.executeCollectionTypePaging(pageCtx, value);
						}
					}
					else
					// pageCtx doesn't exist
					{
						value = readMethod.invoke(source, (Object[]) null);
					}
				}
				else
				// no model2dto transformation or no collection property
				{
					value = readMethod.invoke(source, (Object[]) null);
				}
			}
			else
			// no YObjectGraphContext 
			{
				value = readMethod.invoke(source, (Object[]) null);
			}
		}
		// only catch exceptions whose cause is method invocation
		catch (final InvocationTargetException e)
		{
			throw new GraphException("Error reading " + pCtx.createSourcePathString(), e);
		}
		catch (final IllegalAccessException e)
		{
			throw new GraphException("Error reading " + pCtx.createSourcePathString(), e);
		}

		return value;
	}

	private void writeValueToTarget(final PropertyContextImpl pCtx, final Object target, Object value)
	{
		final PropertyMapping pMap = pCtx.getPropertyMapping();

		final Method writeMethod = pMap.getTargetConfig().getWriteMethod();

		// write target value
		// target value is 'null' when any read-error occurs or conversion has failed
		// in any other case it's a converted source value or source value itself

		// invoke interceptor (if available) 
		if (pMap.getTargetConfig().getWriteInterceptor() != null)
		{
			try
			{
				// when propertymapping is of type 'node' we have to do an additional compatibility check here because
				// it may be, that a node was converted into an another type
				//				if (pCtx.getPropertyMapping().isNode())
				//				{
				//					final PropertyMapping propMap = pCtx.getPropertyMapping();
				//					final Class writeType = propMap.getTargetPropertyConfig().getWriteType();
				//					if (!writeType.isAssignableFrom(value.getClass()))
				//					{
				//						throw new GraphException("Property '" + propMap.getId() + "' was processed as node and transformed into "
				//								+ value.getClass().getSimpleName() + " but interceptor needs " + writeType.getSimpleName());
				//					}
				//
				//				}
				value = pMap.getTargetConfig().getWriteInterceptor().intercept(pCtx, value);
			}
			// any kind of exception gets caught, useful log output gets produced
			// property setter gets not invoked (method is left)
			catch (final Exception e)
			{
				final String name = pMap.getTargetConfig().getWriteInterceptor().getClass().getSimpleName();
				log.error("Error while processing write property interceptor '" + name + "' at property "
						+ pCtx.createTargetPathString(), e);
				return;
			}
		}

		try
		{
			writeMethod.invoke(target, value);
		}
		catch (final Exception e)
		{
			log.error("Error writing " + pCtx.createTargetPathString());
			if (writeMethod.getDeclaringClass().isAssignableFrom(target.getClass()))
			{
				final String actualType = (value != null) ? value.getClass().getName() : "null";
				final String expectedType = writeMethod.getParameterTypes()[0].getName();
				log.error("Error invoking method (used type '" + actualType + "' as parameter for '" + expectedType + "')", e);
			}
			else
			{
				log.error("Error invoking method '" + writeMethod.toString() + "' at class " + target.getClass().getSimpleName(), e);
			}
		}
	}

}
