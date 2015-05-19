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

import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;


public abstract class AbstractPropertyMapping implements PropertyMapping
{
	public static final int COMPLIANCE_LEVEL_LOW = 0;
	public static final int COMPLIANCE_LEVEL_MEDIUM = 1;
	public static final int COMPLIANCE_LEVEL_HIGH = 2;

	protected boolean _isInitialized = false;
	protected NodeMapping nodeMapping = null;

	public AbstractPropertyMapping(final NodeMapping parentNode)
	{
		this.nodeMapping = parentNode;
	}

	protected abstract boolean initialize(final int complianceLevel);

	protected boolean isInitialized()
	{
		return _isInitialized;
	}

	protected void setInitialized(final boolean initialized)
	{
		this._isInitialized = initialized;
	}

	@Override
	public NodeMapping getParentMapping()
	{
		return this.nodeMapping;
	}
}
