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
package de.hybris.platform.cockpit.reports.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Config.SystemSpecificParams;
import de.hybris.platform.util.Utilities;

import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assume;
import org.junit.Ignore;



/**
 * Base class for reports VJDBC integration test. Uses a 'demoUser' to be used via VJDBC and deals with cockpit specific
 * properties used to authenticate.
 */
@Ignore
public abstract class AbstractVJDBCServicelayerTransactionalTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(AbstractVJDBCServicelayerTransactionalTest.class.getName());
	private static final String VJDBC_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";
	private static final String VJDBC_ID = "virtualjdbc.id";


	//should be compatible with data from /test/reports/testDataForReports.csv
	private final static String TEST_REPORT_USER = "demoUser";
	private final static String TEST_REPORT_PASSWD = "1234";

	//default credentials to be reverted after the test
	private String orignalReportUser;
	private String orignalReportPasswd;

	@After
	public void tearDown()
	{
		LOG.info("Reverting default credentials for VJDBC ... ");
		//
		Config.setParameter("cockpit.reports.vjdbc.username", orignalReportUser);
		Config.setParameter("cockpit.reports.vjdbc.password", orignalReportPasswd);
	}

	protected void assureIntegrationTestAllowed()
	{
		orignalReportUser = Config.getParameter("cockpit.reports.vjdbc.username");
		orignalReportPasswd = Config.getParameter("cockpit.reports.vjdbc.password");

		Config.setParameter("cockpit.reports.vjdbc.username", TEST_REPORT_USER);
		Config.setParameter("cockpit.reports.vjdbc.password", TEST_REPORT_PASSWD);

		LOG.info("Setting test credentials for VJDBC ... ");
		if (Registry.isStandaloneMode())
		{
			LOG.warn("Integration test will be ignored since it is in standalone mode");
		}
		Assume.assumeTrue(!Registry.isStandaloneMode());

		Connection con = null;
		final Properties credentialsProps = new Properties();
		try
		{

			credentialsProps.put("vjdbc.login.user", TEST_REPORT_USER);
			credentialsProps.put("vjdbc.login.password", TEST_REPORT_PASSWD);
			LOG.info("Assuring user account '" + credentialsProps.getProperty("vjdbc.login.user")
					+ "' created and acessible via VJDBC ... ");
			con = testVJDBCLogin(credentialsProps);
		}
		catch (final Exception e)
		{
			LOG.warn("Integration test can't be fired since available account '" + credentialsProps.getProperty("vjdbc.login.user")
					+ "' are not valid ," + e.getMessage());
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e.getMessage(), e);
			}
			Assume.assumeNoException(e);
		}
		finally
		{
			Utilities.tryToCloseJDBC(con, null, null);
		}

	}


	private Connection testVJDBCLogin(final Properties props) throws Exception //NOPMD
	{
		final String vjdbcId = Registry.getMasterTenant().getConfig().getParameter(VJDBC_ID);
		final int tomcatPort = Registry.getMasterTenant().getConfig().getInt("tomcat.http.port", 9001);
		Class.forName(VJDBC_DRIVER_CLASS).newInstance();

		final String url = "http://localhost:" + tomcatPort + "/virtualjdbc/service?tenant="
				+ Registry.getCurrentTenant().getTenantID();

		testHttpConnection(url);
		final Connection conn = DriverManager.getConnection("jdbc:hybris:flexiblesearch:" + url + "," + vjdbcId, props);
		Assert.assertEquals(Registry.getMasterTenant().getConfig().getParameter(SystemSpecificParams.DB_URL), conn.getMetaData()
				.getURL());
		LOG.info("Underlying data base url:: " + conn.getMetaData().getURL());
		return conn;
	}


	private void testHttpConnection(final String url) throws Exception
	{
		HttpURLConnection conn = null;
		ObjectOutputStream oos = null;

		try
		{
			final URL _url = new URL(url);
			conn = (HttpURLConnection) _url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setAllowUserInteraction(false);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-type", "binary/x-java-serialized");
			conn.setRequestProperty("vjdbc-method", "connect");

			oos = new ObjectOutputStream(conn.getOutputStream());
			oos.writeUTF("Some blah ....");
			oos.flush();
			conn.connect();

		}
		finally
		{
			if (oos != null)
			{
				try
				{
					oos.close();
				}
				catch (final Exception e)
				{
					LOG.error(e.getMessage());
				}

				if (conn != null)
				{
					conn.disconnect();
				}
			}
		}
	}
}
