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
package de.hybris.platform.spring.properties.beans;

import de.hybris.platform.servicelayer.config.impl.HybrisPropertiesFactory;

import java.util.Map;
import java.util.Properties;


/**
 *
 */
public class TestHybrisPropertiesFactory extends HybrisPropertiesFactory
{
	private Map<String, String> allProps;


	public void setAllProps(final Map<String, String> allProps)
	{
		this.allProps = allProps;
	}


	@Override
	public Properties getObject()
	{
		final Properties props = new Properties();
		props.putAll(allProps);
		return props;
	}

}
