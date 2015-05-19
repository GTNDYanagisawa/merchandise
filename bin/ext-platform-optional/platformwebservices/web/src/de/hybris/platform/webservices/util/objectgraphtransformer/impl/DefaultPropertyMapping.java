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


import de.hybris.platform.webservices.util.objectgraphtransformer.GraphException;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyFilter;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyProcessor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


public class DefaultPropertyMapping extends AbstractPropertyMapping
{
	private static final Logger log = Logger.getLogger(DefaultPropertyMapping.class);

	private static final PropertyProcessor DEFAULT_PROPERTY_PROCESSOR = new PropertyProcessorImpl();

	private String id = null;

	private DefaultPropertyConfig readPropertyConfig = null;
	private DefaultPropertyConfig writePropertyConfig = null;

	private List<PropertyFilter> filters = Collections.EMPTY_LIST;
	private List<NodeMapping> nodeMappingList = Collections.EMPTY_LIST;
	private boolean _isNode = false;
	private boolean _isVirtual = false;
	private boolean _isTypeCheckEnabled = true;

	private PropertyProcessor propertyProc = null;
	private boolean isPropertyCfgInitialized = false;

	/**
	 * Constructor.
	 * 
	 * @param property
	 */
	public DefaultPropertyMapping(final NodeMapping nodeMapping, final String property)
	{
		this(nodeMapping, property, property, null, null);
	}

	/**
	 * @param readProp
	 * @param writeProp
	 */
	public DefaultPropertyMapping(final NodeMapping nodeMapping, final String readProp, final String writeProp)
	{
		this(nodeMapping, readProp, writeProp, null, null);
	}


	/**
	 * Constructor.
	 * <p/>
	 * Maps a read-property to a write-property. Properties are given independently from any concrete class and must
	 * follow java bean naming scheme. A {@link PropertyInterceptor} for both, read-value and write value, can be passed.
	 * <p/>
	 * A configuration must be compiled before it can be used. Compiling binds the configuration to a specific
	 * {@link GraphNode} and a {@link GraphTransformer} and verifies whether configuration is valid (e.g. property names)
	 * 
	 * @param readProp
	 *           name of read property from source node
	 * @param writeProp
	 *           name of write property from target node
	 * @param readInterceptor
	 *           {@link PropertyInterceptor} for read
	 * 
	 * @param writeInterceptor
	 *           {@link PropertyInterceptor} for write
	 */
	public DefaultPropertyMapping(final NodeMapping nodeMapping, final String readProp, String writeProp,
			final PropertyInterceptor readInterceptor, final PropertyInterceptor writeInterceptor)
	{
		super(nodeMapping);
		if (readProp == null)
		{
			throw new GraphException("No read property specified");
		}

		if (writeProp == null)
		{
			writeProp = readProp;
		}

		this.id = writeProp;
		this.readPropertyConfig = new DefaultPropertyConfig(readProp, null, null);
		this.readPropertyConfig.setReadInterceptor(readInterceptor);
		this.writePropertyConfig = new DefaultPropertyConfig(writeProp, null, null);
		this.writePropertyConfig.setWriteInterceptor(writeInterceptor);
		this.propertyProc = DEFAULT_PROPERTY_PROCESSOR;
	}


	/**
	 * Constructs a new instance using the two passed arguments as read- and write property.
	 * 
	 * @param readProperty
	 *           {@link DefaultPropertyConfig}
	 * @param writeProperty
	 *           {@link DefaultPropertyConfig}
	 */
	public DefaultPropertyMapping(final NodeMapping nodeMapping, final DefaultPropertyConfig readProperty,
			final DefaultPropertyConfig writeProperty)
	{
		super(nodeMapping);
		if (readProperty == null)
		{
			throw new GraphException("No read property specified");
		}

		if (writeProperty == null)
		{
			throw new GraphException("No write property specified");
		}

		this.id = readProperty.getName();
		this.readPropertyConfig = readProperty;
		this.writePropertyConfig = writeProperty;
		this.propertyProc = DEFAULT_PROPERTY_PROCESSOR;
		this.isPropertyCfgInitialized = true;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public List<PropertyFilter> getPropertyFilters()
	{
		return filters;
	}

	@Override
	public boolean isNode()
	{
		return this._isNode;
	}

	protected void setNode(final boolean isNode)
	{
		this._isNode = isNode;
	}

	@Override
	public boolean isVirtual()
	{
		return _isVirtual;
	}

	protected void setVirtual(final boolean isVirtual)
	{
		_isVirtual = isVirtual;
	}

	public void setFilters(final List<PropertyFilter> filters)
	{
		this.filters = filters;
	}

	@Override
	public List<NodeMapping> getNewNodeMappings()
	{
		return nodeMappingList;
	}

	public void setNewNodeMappings(final List<NodeMapping> nodeConfig)
	{
		this.nodeMappingList = nodeConfig;
	}

	@Override
	public PropertyProcessor getProcessor()
	{
		return this.propertyProc;
	}

	@Override
	public DefaultPropertyConfig getSourceConfig()
	{
		return this.readPropertyConfig;
	}

	@Override
	public DefaultPropertyConfig getTargetConfig()
	{
		return this.writePropertyConfig;
	}

	/**
	 * Compiles configuration settings by assuming this property belongs to passed node which itself belongs to passed
	 * graph.
	 * 
	 * @param complianceLevel
	 *           various levels for error handling
	 * @return true when compiling was successful
	 */
	@Override
	protected boolean initialize(final int complianceLevel)
	{
		// read-write PropertyConfig must be compiled in case this PropertyMapping was created with
		// read/write property names (and no concrete read/write methods)
		if (!this.isPropertyCfgInitialized)
		{
			final PropertyConfig pRead = nodeMapping.getSourceConfig().getProperties().get(this.readPropertyConfig.getName());
			final PropertyConfig pWrite = nodeMapping.getTargetConfig().getProperties().get(this.writePropertyConfig.getName());
			this.readPropertyConfig.mergeWith(pRead);
			this.writePropertyConfig.mergeWith(pWrite);
			this.isPropertyCfgInitialized = true;
		}

		if (this.writePropertyConfig.getWriteMethod() != null)
		{
			this.readAnnotationConfiguration();
			this._isInitialized = this.isVirtual();

			// read-property of source node type must provide read-method access
			// write-property of target node type must provide write-method access
			if (!_isInitialized && readPropertyConfig.getReadMethod() != null)
			{
				// type check (source read-type vs. target write type) is only enabled when
				// both, read- and write type check is enabled
				this._isTypeCheckEnabled = this.readPropertyConfig.isReadTypeCheckEnabled()
						&& this.writePropertyConfig.isWriteTypeCheckEnabled();

				// ... having a NodeConfig for read-method return type: success
				final NodeMapping nodeCfg = nodeMapping.getParentMapping().getNodeMapping(readPropertyConfig.getReadType());
				if (nodeCfg != null)
				{
					// XXX: special handling for collections
					// no type check here because it's valid to have mismatches like List<->Set (CollectionNodeProcessor converts automatically)
					if (Collection.class.isAssignableFrom(writePropertyConfig.getWriteType()))
					{
						this._isNode = true;
						this._isInitialized = true;
					}
					else
					{
						final Class readType = nodeCfg.getTargetConfig().getType();
						final Class writeType = writePropertyConfig.getWriteType();

						// compiled successfully if read and write type are compatible
						// (including possible  read/write interceptors)
						this._isInitialized = writeType.isAssignableFrom(readType);

						if (!this._isInitialized && !this._isTypeCheckEnabled)
						{
							this._isInitialized = readType.isAssignableFrom(writeType);
						}
						this._isNode = true;
					}
				}
				else
				{
					final Class readType = readPropertyConfig.getReadType();
					final Class writeType = writePropertyConfig.getWriteType();

					// compiled successfully if read and write type are compatible
					// (including possible  read/write interceptors)
					this._isInitialized = writeType.isAssignableFrom(readType);

					if (!this._isInitialized && !this._isTypeCheckEnabled)
					{
						this._isInitialized = readType.isAssignableFrom(writeType);
					}
				}
			}
		}


		// debug
		if (log.isDebugEnabled() && ((AbstractNodeMapping) nodeMapping).isDebugEnabled())
		{
			final String action = this._isInitialized ? "Take " : "Skip ";
			final String logMsg = action + toExtString();
			log.debug(logMsg);
		}

		// error handling in case compilation fails
		if (!_isInitialized)
		{
			final String logMsg = toExtString();
			if (complianceLevel == COMPLIANCE_LEVEL_HIGH)
			{
				throw new GraphException("Skip " + logMsg);
			}
			if (complianceLevel == COMPLIANCE_LEVEL_MEDIUM)
			{
				log.error(" Invalid " + logMsg);
			}
		}

		return this._isInitialized;
	}

	private void readAnnotationConfiguration()
	{
		final Method write = this.writePropertyConfig.getWriteMethod();
		if (write != null && write.isAnnotationPresent(GraphProperty.class))
		{
			final GraphProperty writeAnno = write.getAnnotation(GraphProperty.class);
			this._isVirtual = writeAnno.virtual();
		}

	}


	protected boolean isTypeCheckEnabled()
	{
		return this._isTypeCheckEnabled;
	}

	@Override
	public String toString()
	{
		return this.getSourceConfig() + "->" + this.getTargetConfig();
	}


	/**
	 * Enhanced toString representation. Use carefully as this method has an performance impact.
	 * 
	 * @return String representation
	 */
	public String toExtString()
	{
		final DefaultPropertyConfig readPropertyConfig = getSourceConfig();
		final DefaultPropertyConfig writePropertyConfig = getTargetConfig();

		// read-information
		final String readPropName = readPropertyConfig.getName();
		final Class readType = readPropertyConfig.getReadType();
		final Method readMethod = readPropertyConfig.getReadMethod();

		// write-information
		final String writePropName = writePropertyConfig.getName();
		final Class writeType = writePropertyConfig.getWriteType();
		final Method writeMethod = writePropertyConfig.getWriteMethod();


		// create read-information part for final log message
		String read = "";
		if (isVirtual())
		{
			read = nodeMapping.getSourceConfig().getType().getSimpleName() + "#[virtual]";
		}
		else
		{
			read = nodeMapping.getSourceConfig().getType().getSimpleName() + "#" + readPropName;
			if (readMethod != null)
			{
				read = read + ":" + readMethod.getReturnType().getSimpleName();
			}
			read = read + " -> ";
		}
		final PropertyInterceptor readInterceptor = readPropertyConfig.getReadInterceptor();
		if (readInterceptor != null)
		{
			read = read + readInterceptor.getClass().getSimpleName() + ":" + readType.getSimpleName() + " -> ";
		}

		// create write information part for final log message
		String write = "";
		final PropertyInterceptor writeInterceptor = writePropertyConfig.getWriteInterceptor();
		if (writeInterceptor != null)
		{
			final Class writeConvReturnType = writePropertyConfig.getInterceptMethod(writeInterceptor).getReturnType();
			final Class writeConvParamtype = writePropertyConfig.getInterceptMethod(writeInterceptor).getParameterTypes()[1];
			write = " -> " + writeInterceptor.getClass().getSimpleName() + "(" + writeConvParamtype.getSimpleName() + ")" + ":"
					+ writeConvReturnType.getSimpleName();
		}

		write = write + " -> " + nodeMapping.getTargetConfig().getType().getSimpleName() + "#" + writePropName;
		if (writeMethod != null)
		{
			write = write + "(" + writeMethod.getParameterTypes()[0].getSimpleName() + ")";
		}



		final NodeMapping nodeCfg = (readType != null) ? nodeMapping.getParentMapping().getNodeMapping(readType) : null;
		final String transformed = (nodeCfg != null) ? "[" + nodeCfg.getTargetConfig().getType().getSimpleName() + "]" : "[]";


		// add enabled/disabled flags ...
		String flags = "";
		// ... typecheck
		if (!isTypeCheckEnabled())
		{
			flags = flags + " typecheck off";
		}

		if (flags.length() > 0)
		{
			flags = " (" + flags + ")";
		}


		// add conflicts 
		String conflicts = "";
		if (!isInitialized())
		{
			// ... no read method (getter)?
			if (readMethod == null)
			{
				conflicts = conflicts + "no read method ";
			}

			// ... no write method (setter)?
			if (writeMethod == null)
			{
				conflicts = conflicts + "no write method";
			}


			if (readMethod != null && writeMethod != null)
			{
				final String fromType = nodeCfg == null ? readType.getSimpleName() : nodeCfg.getTargetConfig().getType()
						.getSimpleName();
				// ... read/write type not compatible (no node)
				conflicts = conflicts + "read<->write type mismatch (" + fromType + "<->" + writeType.getSimpleName();
			}

			if (conflicts.length() > 0)
			{
				conflicts = " (" + conflicts + ")";
			}
		}

		// start creating final log message 
		final String logMsg = read + transformed + write + flags + conflicts;


		return logMsg;
	}

}
