/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package de.hybris.platform.print.util;

import de.hybris.platform.core.Registry;
import de.hybris.platform.util.Config;

import org.apache.log4j.Logger;

/**
 * ValueUtils implementation that add additional utility and convenience methods
 *
 * @author  rri
 */
public class AdditionalValueUtils extends ValueUtilsImpl
{
	private static final Logger log = Logger.getLogger(AdditionalValueUtils.class.getName());


	public Object getBean( final String beanid )
	{
		return Registry.getApplicationContext().getBean(beanid);
	}


	public Config getConfig()
	{
		try
		{
			return Config.class.newInstance();
		}
		catch (final Exception e)
		{
			log.error("Could not forward instance of Config: " + e.getMessage());
			return null;
		}
	}
}
