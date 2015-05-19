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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;


public class CollectionNodeMapping extends AbstractNodeMapping
{
	private static final AbstractNodeProcessor DEFAULT_NODE_PROCESSOR = new CollectionNodeProcessor();

	public CollectionNodeMapping(final AbstractGraphTransformer graph)
	{
		super(graph);
		setSourceConfig(new DefaultNodeConfig(Collection.class));
		setTargetConfig(new DefaultNodeConfig(Collection.class));
		setNodeProcessor(DEFAULT_NODE_PROCESSOR);
		setVirtual(true);
		setInitialized(true);
	}

	@Override
	public Map<String, PropertyMapping> getPropertyMappings()
	{
		return Collections.EMPTY_MAP;
	}



	@Override
	protected boolean initializeNode()
	{
		return true;
	}


}
