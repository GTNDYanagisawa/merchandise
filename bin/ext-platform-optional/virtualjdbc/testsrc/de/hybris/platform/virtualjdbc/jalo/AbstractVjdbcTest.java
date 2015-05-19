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
package de.hybris.platform.virtualjdbc.jalo;

import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.util.Utilities;
import de.hybris.platform.virtualjdbc.constants.VjdbcConstants;

import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;

import de.simplicit.vjdbc.VJdbcProperties;

import junit.framework.Assert;


@Ignore
public abstract class AbstractVjdbcTest extends HybrisJUnit4Test
{
	protected static final Logger LOG = Logger.getLogger(AbstractVjdbcTest.class.getName());

	protected final static String PRODUCT_PREFIX = "vjdbcSimpleTestProduct";

	protected final static String QUERY_FIND_PRODUCTS = " SELECT item_t0.Code FROM junit_products item_t0 ";

	protected final static String CONDITION = " WHERE  item_t0.Code like '" + PRODUCT_PREFIX + "%' ";//"SELECT item_t0.Code FROM junit_products item_t0 WHERE ( item_t0.Code like 'VJDBC%') ";

	protected final static int PRODUCT_COUNT = 5;

	protected User testRWUser;
	protected User testROUser;

	protected UserGroup testRWGroup;
	protected UserGroup testROGroup;

	protected final List<Product> productList = new ArrayList<Product>(PRODUCT_COUNT);

	@Before
	public void setUpTestData() throws ConsistencyCheckException
	{
		jaloSession.setUser(UserManager.getInstance().getAdminEmployee());

		checkIntegrationTest();
		try
		{
			testRWGroup = UserManager.getInstance().getUserGroupByGroupID(
					Registry.getMasterTenant().getConfig().getString(VjdbcConstants.DB.VJDBC_RW_USER_GROUP, null));
		}
		catch (final JaloItemNotFoundException e)
		{
			testRWGroup = UserManager.getInstance().createUserGroup(
					Registry.getMasterTenant().getConfig().getString(VjdbcConstants.DB.VJDBC_RW_USER_GROUP, null));
		}
		try
		{
			testROGroup = UserManager.getInstance().getUserGroupByGroupID(
					Registry.getMasterTenant().getConfig().getString(VjdbcConstants.DB.VJDBC_RO_USER_GROUP, null));
		}
		catch (final JaloItemNotFoundException e)
		{
			testROGroup = UserManager.getInstance().createUserGroup(
					Registry.getMasterTenant().getConfig().getString(VjdbcConstants.DB.VJDBC_RO_USER_GROUP, null));
		}
		LOG.info("Created VJDBC groups ...");

		final Properties credentialsProps = new Properties();
		credentialsProps.put(VJdbcProperties.LOGIN_USER, "testRWUser");
		credentialsProps.put(VJdbcProperties.LOGIN_PASSWORD, "1234");

		try
		{
			testRWUser = UserManager.getInstance().getUserByLogin(credentialsProps.getProperty(VJdbcProperties.LOGIN_USER));
		}
		catch (final JaloItemNotFoundException e)
		{
			testRWUser = UserManager.getInstance().createUser(credentialsProps.getProperty(VJdbcProperties.LOGIN_USER));
		}
		testRWUser.setPassword(credentialsProps.getProperty(VJdbcProperties.LOGIN_PASSWORD));
		testRWGroup.addMember(testRWUser);
		assureUserCreatedCorrectlyRemotePlatform(credentialsProps);

		//overwrite props
		credentialsProps.put(VJdbcProperties.LOGIN_USER, "testROUser");
		credentialsProps.put(VJdbcProperties.LOGIN_PASSWORD, "1234");

		try
		{
			testROUser = UserManager.getInstance().getUserByLogin(credentialsProps.getProperty(VJdbcProperties.LOGIN_USER));
		}
		catch (final JaloItemNotFoundException e)
		{
			testROUser = UserManager.getInstance().createUser(credentialsProps.getProperty(VJdbcProperties.LOGIN_USER));
		}
		testROUser.setPassword(credentialsProps.getProperty(VJdbcProperties.LOGIN_PASSWORD));
		testROGroup.addMember(testROUser);
		assureUserCreatedCorrectlyRemotePlatform(credentialsProps);

		LOG.info("Created VJDBC users ...");
		setUpProducts();
		LOG.info("Created VJDBC test data ...");

	}

	protected void checkIntegrationTest()
	{
		if (Registry.isStandaloneMode())
		{
			LOG.warn("Integration test will be ignored since it is in standalone mode");
		}
		Assume.assumeTrue(!Registry.isStandaloneMode());
		try
		{
			LOG.info("Checking if integration test can be fired ....");
			final int tomcatPort = Registry.getMasterTenant().getConfig().getInt("tomcat.http.port", 9001);
			Class.forName(VjdbcConstants.DB.VJDBC_DRIVER_CLASS).newInstance();
			final String url = "http://localhost:" + tomcatPort + "/virtualjdbc/service?tenant="
					+ Registry.getCurrentTenant().getTenantID();

			testConnection(url);
			LOG.info("Integration test could  be fired host " + url + " available.");
		}
		catch (final Exception e)
		{
			LOG.warn("Integration test can't be fired ," + e.getMessage());
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e.getMessage(), e);
			}
			Assume.assumeNoException(e);
		}

	}

	protected boolean assureUserCreatedCorrectlyRemotePlatform(final Properties userCreatedCredentials)
	{
		Connection con = null;
		try
		{
			LOG.info("Assuring user account created and acessible via VJDBC ... ");
			con = getHttpFlexConnection(userCreatedCredentials);
			return true;
		}
		catch (final SQLException e)
		{
			LOG.error(e);
			Assert.fail(e.getMessage());
		}
		catch (final Exception e)
		{
			LOG.warn("Integration test can't be fired since created accounts are not valid ," + e.getMessage());
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
		return false;
	}

	@After
	public void tearDown()
	{
		productList.clear();
	}

	protected User getUser(final boolean readWrite)
	{
		if (readWrite)
		{
			return testRWUser;
		}
		else
		{
			return testROUser;
		}
	}

	protected UserGroup getUserGroup(final boolean readWrite)
	{
		if (readWrite)
		{
			return testRWGroup;
		}
		else
		{
			return testROGroup;
		}
	}

	protected Properties getUserPrincipals()
	{
		return getUserPrincipals(false);
	}

	protected Properties getUserPrincipals(final boolean readWrite)
	{
		final Properties props = new Properties();
		if (readWrite)
		{
			props.put(VJdbcProperties.LOGIN_USER, testRWUser.getUID());
			props.put(VJdbcProperties.LOGIN_PASSWORD, testRWUser.getPassword());
		}
		else
		{
			props.put(VJdbcProperties.LOGIN_USER, testROUser.getUID());
			props.put(VJdbcProperties.LOGIN_PASSWORD, testROUser.getPassword());
		}
		return props;
	}

	private void assignCatalogVersions()
	{
		final Catalog def = CatalogManager.getInstance().getDefaultCatalog();
		//final Item def = getDefaultCatalog(getCatalogManagerInstance());
		Item catalogVersion = null;
		if (def != null)
		{
			catalogVersion = def.getActiveCatalogVersion();//getActiveCatalogVersion(def);// def.getActiveCatalogVersion();
		}

		jaloSession.setAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS,

		Collections.singletonList(catalogVersion == null ? CatalogConstants.NO_VERSIONS_AVAILABLE_DUMMY : catalogVersion));
	}

	protected void setUpProducts()
	{
		assignCatalogVersions();
		for (int i = 0; i < PRODUCT_COUNT; i++)
		{
			productList.add(ProductManager.getInstance().createProduct(PRODUCT_PREFIX + i));
		}

		LOG.info("Flexible search result ::"
				+ (FlexibleSearch.getInstance().search(jaloSession.getSessionContext(),
						"select * from {Product} where {code} like '" + PRODUCT_PREFIX + "%'", Collections.EMPTY_MAP, Item.class)
						.getCount()));
	}

	private void testConnection(final String url) throws Exception
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

	protected Connection getHttpSqlConnection(final Properties props) throws Exception //NOPMD
	{
		final String vjdbcId = Registry.getMasterTenant().getConfig().getParameter(VjdbcConstants.DB.VJDBC_ID);
		final int tomcatPort = Registry.getMasterTenant().getConfig().getInt("tomcat.http.port", 9001);
		Class.forName(VjdbcConstants.DB.VJDBC_DRIVER_CLASS).newInstance();
		final String url = "http://localhost:" + tomcatPort + "/virtualjdbc/service?tenant="
				+ Registry.getCurrentTenant().getTenantID();

		testConnection(url);
		final Connection vjdbcCon = DriverManager.getConnection("jdbc:hybris:sql:" + url + "," + vjdbcId, props);
		verifyUnderlyingConnection(vjdbcCon);
		return vjdbcCon;
	}


	protected Connection getHttpFlexConnection(final Properties props) throws Exception //NOPMD
	{
		final String vjdbcId = Registry.getMasterTenant().getConfig().getParameter(VjdbcConstants.DB.VJDBC_ID);
		final int tomcatPort = Registry.getMasterTenant().getConfig().getInt("tomcat.http.port", 9001);
		Class.forName(VjdbcConstants.DB.VJDBC_DRIVER_CLASS).newInstance();

		final String url = "http://localhost:" + tomcatPort + "/virtualjdbc/service?tenant="
				+ Registry.getCurrentTenant().getTenantID();

		testConnection(url);
		final Connection vjdbcCon = DriverManager.getConnection("jdbc:hybris:flexiblesearch:" + url + "," + vjdbcId, props);
		verifyUnderlyingConnection(vjdbcCon);
		return vjdbcCon;
	}

	/**
	 * 
	 */
	protected void verifyUnderlyingConnection(final Connection vjdbcCon) throws SQLException
	{
		Assert.assertFalse("Connection should be opened", vjdbcCon.isClosed());
		//Assert.assertTrue(vjdbcCon.getMetaData().getURL()
		//		.startsWith(Registry.getMasterTenant().getConfig().getParameter(SystemSpecificParams.DB_URL)));
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Underlying data base url:: " + vjdbcCon.getMetaData().getURL());
		}
	}
}
