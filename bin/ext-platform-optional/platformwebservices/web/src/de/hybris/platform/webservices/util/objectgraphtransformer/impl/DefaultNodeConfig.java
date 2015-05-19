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

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class DefaultNodeConfig extends AbstractNodeConfig
{
	private Class type = null;
	private String[] uidPropnames = null;
	private PropertyConfig[] uidProperties = null;
	private Map<String, PropertyConfig> properties = null;

	public DefaultNodeConfig(final Class<?> type)
	{
		super();
		this.type = type;

		if (type.isAnnotationPresent(GraphNode.class))
		{
			final GraphNode cfg = type.getAnnotation(GraphNode.class);
			if (cfg.uidProperties().trim().length() > 0)
			{
				setUidPropertyNames(cfg.uidProperties());
			}
		}
	}

	public DefaultNodeConfig(final NodeConfig template)
	{
		super();
		this.type = template.getType();
		if (template.getUidProperties() != null)
		{
			this.uidProperties = Arrays.copyOf(template.getUidProperties(), template.getUidProperties().length);
		}

	}


	@Override
	public Class getType()
	{
		return this.type;
	}


	public void setUidPropertyNames(final String propNames)
	{
		this.uidPropnames = null;
		this.uidProperties = null;

		// split and remove whitespaces
		if (StringUtils.isNotBlank(propNames))
		{
			final String[] names = propNames.split("\\s*,\\s*");
			for (int i = 0; i < names.length; i++)
			{
				names[i] = normalizePropertyName(names[i]);
			}
			this.uidPropnames = names;
		}
	}

	public String[] getUidPropertyNames()
	{
		return this.uidPropnames;
	}


	/**
	 * @param uidProperties
	 *           the uidProperties to set
	 */
	public void setUidProperties(final PropertyConfig[] uidProperties)
	{
		this.uidProperties = uidProperties;
	}


	@Override
	public PropertyConfig[] getUidProperties()
	{
		if (this.uidProperties == null && uidPropnames != null && uidPropnames.length > 0)
		{
			this.uidProperties = new PropertyConfig[uidPropnames.length];
			final Map<String, PropertyConfig> cfgMap = this.getProperties();
			for (int i = 0; i < uidPropnames.length; i++)
			{
				uidProperties[i] = cfgMap.get(uidPropnames[i]);
			}
		}
		return this.uidProperties;
	}

	/*
	 * (non-Javadoc)
	 * @see de.hybris.platform.webservices.util.objectgraphtransformer.NodeConfig#getProperties()
	 */
	@Override
	public Map<String, PropertyConfig> getProperties()
	{
		if (this.properties == null)
		{
			this.properties = getPropertiesFor(this.type);
		}
		return this.properties;
	}


}
