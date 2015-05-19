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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.PlatformConfig;
import de.hybris.platform.core.AbstractTenant;
import de.hybris.platform.core.DataSourceFactory;
import de.hybris.platform.core.DataSourceImplFactory;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.servicelayer.tenant.MockTenant;
import de.hybris.platform.util.Utilities;
import de.hybris.platform.util.config.ConfigIntf;
import de.hybris.platform.util.config.HybrisConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;


@Ignore
public class MockitoTenant extends MockTenant
{
	// This mock is required for setting up the datasource


	private HybrisDataSource dataSource;
	private ConfigIntf configIntf;
	private Map<String, String> rawConfig;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(MockitoTenant.class);

	public MockitoTenant(final String tenantId)
	{
		super(tenantId);
		try
		{
			final AbstractTenant abstractTenant = mock(AbstractTenant.class);
			rawConfig = loadRawConfigFile();
			configIntf = new HybrisConfig(MapUtils.toProperties(rawConfig), true, -1);
			// set up AbstractTenant to set up HybrisDatasource
			when(abstractTenant.getConfig()).thenReturn(configIntf);
			dataSource = createAlternativeDataSource("junit", rawConfig, false, abstractTenant);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public HybrisDataSource getDataSource()
	{
		return this.dataSource;
	}

	@Override
	public ConfigIntf getConfig()
	{
		return this.configIntf;
	}

	private HybrisDataSource createAlternativeDataSource(final String id, final Map<String, String> params,
			final boolean readOnly, final AbstractTenant abstractTenant)
	{
		final Map<String, String> connectionParams = new HashMap<String, String>(5);
		connectionParams.put(SystemSpecificParams.DB_USERNAME, params.get(SystemSpecificParams.DB_USERNAME));
		connectionParams.put(SystemSpecificParams.DB_PASSWORD, params.get(SystemSpecificParams.DB_PASSWORD));
		connectionParams.put(SystemSpecificParams.DB_URL, params.get(SystemSpecificParams.DB_URL));
		connectionParams.put(SystemSpecificParams.DB_DRIVER, params.get(SystemSpecificParams.DB_DRIVER));
		connectionParams.put(SystemSpecificParams.DB_TABLEPREFIX, params.get(SystemSpecificParams.DB_TABLEPREFIX));
		return createDataSourceFactory().createDataSource(id, abstractTenant, connectionParams, readOnly);
	}

	private DataSourceFactory createDataSourceFactory()
	{
		return new DataSourceImplFactory();
	}

	private static interface SystemSpecificParams
	{
		final String DB_USERNAME = "db.username".intern();
		final String DB_PASSWORD = "db.password".intern();
		final String DB_URL = "db.url".intern();
		final String DB_DRIVER = "db.driver".intern();
		final String DB_TABLEPREFIX = "db.tableprefix".intern();
		final String DB_POOL_FROMJNDI = "db.pool.fromJNDI".intern();
		final String EXTENSIONS = "allowed.extensions".intern();
		final String LOCALE = "locale".intern();
		final String TIME_ZONE = "timezone".intern();
		final String DB_FACTORY = "db.factory".intern();
	}

	private static Map<String, String> loadRawConfigFile() throws IOException
	{
		final Properties tempProps = new Properties();
		// 1. first, try to load the following files
		final PlatformConfig config = Utilities.getPlatformConfig();
		ConfigUtil.loadRuntimeProperties(tempProps, config);
		// 3. all system properties have the highest priority
		for (final Map.Entry entry : System.getProperties().entrySet())
		{
			tempProps.put(entry.getKey(), ((String) entry.getValue()).trim());
		}
		// do trimming etc.
		final Map<String, String> props = new HashMap<String, String>();
		for (final Iterator<?> it = tempProps.keySet().iterator(); it.hasNext();)
		{
			final String key = (String) it.next();
			String value = tempProps.getProperty(key);
			int idx = value.indexOf('#');
			if (idx > 0)
			{
				if (value.charAt(idx - 1) == ('\\'))
				{
					idx = -1; // wenn escaped, dann nicht!
				}
			}
			if (idx > 0)
			{
				value = value.substring(0, idx);
			}
			value = value.trim();
			value = value.replace("\\#", "#");
			value = value.replace("{tab}", "\t");
			// if( value!=null && value.length()>1) value=value.trim();
			// value = Config.trim( value, "\t".toCharArray() );
			props.put(key, value);
		}
		return props;
	}
}
