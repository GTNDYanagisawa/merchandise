/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.b2b.mock;

import de.hybris.platform.core.Tenant;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.service.AbstractService;
import de.hybris.platform.util.config.ConfigIntf;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Ignore;


@Ignore
public class MockConfigurationService extends AbstractService implements ConfigurationService
{
	private Configuration cfg;


	@Override
	public Configuration getConfiguration()
	{
		return this.cfg;
	}

	@Override
	public void afterPropertiesSet() throws Exception // NOPMD
	{
		super.afterPropertiesSet();
		this.cfg = getConfigurationInternal(this.getTenant().getConfig());
	}

	private Configuration getConfigurationInternal(final ConfigIntf hybrisconfig) throws IOException
	{
		return new AbstractConfiguration()
		{
			/**
			 * @see org.apache.commons.configuration.AbstractConfiguration#addPropertyDirect(String, Object)
			 */
			@Override
			protected void addPropertyDirect(final String key, final Object value)
			{
				hybrisconfig.setParameter(key, (String) value);
			}

			/**
			 * @see org.apache.commons.configuration.Configuration#containsKey(String)
			 */
			@Override
			public boolean containsKey(final String key)
			{
				return hybrisconfig.getParameter(key) != null;
			}

			/**
			 * @see org.apache.commons.configuration.Configuration#getKeys()
			 */
			@Override
			public Iterator<String> getKeys()
			{
				return hybrisconfig.getAllParameters().keySet().iterator();
			}

			/**
			 * @see org.apache.commons.configuration.Configuration#getProperty(String)
			 */
			@Override
			public Object getProperty(final String key)
			{
				return hybrisconfig.getParameter(key);
			}

			/**
			 * @see org.apache.commons.configuration.Configuration#isEmpty()
			 */
			@Override
			public boolean isEmpty()
			{
				// assume hybris config is never empty
				return false;
			}

			/**
			 * @see org.apache.commons.configuration.Configuration#getString(String)
			 */
			@Override
			public String getString(final String key)
			{
				return this.getString(key, "");
			}

			/**
			 * Default behavior gets overriden with that one from hybris. This disables the
			 * {@link org.apache.commons.configuration.interpol.ConfigurationInterpolator} which would be created/asked
			 * with every call for an appropriate {@link org.apache.commons.lang.text.StrLookup} which itself deals with an
			 * {@link org.apache.commons.lang.text.StrSubstitutor} and so on. (only pitfall is that variable replacement as
			 * commons defines it doesn't work anymore)
			 * 
			 * @see org.apache.commons.configuration.AbstractConfiguration#getString(String, String)
			 */
			@Override
			public String getString(final String key, final String defaultValue)
			{
				return hybrisconfig.getString(key, defaultValue);
			}

			@Override
			public Collection getErrorListeners()
			{
				return super.getErrorListeners(); // To change body of
				// overridden methods use
				// File | Settings | File
				// Templates.
			}
		};
	}


	public Tenant getTenant()
	{
		return getCurrentTenant();
	}


	/**
	 * 
	 * @deprecated since 5.0
	 */
	@Deprecated
	public void setTenant(final Tenant tenant)
	{
		//
	}
}
