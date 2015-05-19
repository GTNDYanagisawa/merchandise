/*
 *
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package de.hybris.platform.licence.sap;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Properties;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultKeySystemTest
{
	private static final String SYSTEM_ID = "CPS";
	private static final String HW_ID = "Y4989890650";

	private DefaultKeySystem defaultKeySystem;

	@Before
	public void setUp() throws Exception
	{
		defaultKeySystem = new DefaultKeySystem();
	}

	@Test
	public void testInit() throws Exception
	{
		assertThat(defaultKeySystem.init()).isTrue();
	}

	@Test
	public void testGetSystemId() throws Exception
	{
		assertThat(defaultKeySystem.getSystemId()).hasSize(3).isEqualTo(SYSTEM_ID);
	}

	@Test
	public void testGetSwProducts() throws Exception
	{
		assertThat(defaultKeySystem.getSwProducts()).containsOnly("CPS_HDB", "CPS_ROW", "CPS_ORA", "CPS_MSS", "CPS_MYS");
	}

	@Test
	public void shouldContainProductNamesWithConfiguredDbPostfixes() throws Exception
	{
		// given
		final TestKeySystemWithOverridenDbCodes keySystem = new TestKeySystemWithOverridenDbCodes();

		// when
		final Vector swProducts = keySystem.getSwProducts();

		// then
		assertThat(swProducts)
				.containsOnly("CPS_SAPTEST", "CPS_MYSQLTEST", "CPS_ORACLETEST", "CPS_SQLSERVERTEST", "CPS_HSQLDBTEST");
	}

	@Test
	public void testGetBasisRelease() throws Exception
	{
		assertThat(defaultKeySystem.getBasisRelease()).isEqualTo("hybris");
	}

	@Test
	public void testGetHwId() throws Exception
	{
		assertThat(defaultKeySystem.getHwId()).hasSize(11).isEqualTo(HW_ID);
	}

	@Test
	public void testGetCmdPrefix() throws Exception
	{
		final String cmdPrefix = defaultKeySystem.getCmdPrefix();
		if (System.getProperty("os.name").equalsIgnoreCase("windows"))
		{
			assertThat(cmdPrefix).isEqualTo("license.bat");
		}
		else
		{
			assertThat(cmdPrefix).isEqualTo("license.sh");
		}
	}

	private static class TestKeySystemWithOverridenDbCodes extends DefaultKeySystem
	{
		@Override
		protected Properties loadPlatformProperties()
		{
			final Properties properties = new Properties();
			properties.put("license.db.code.sap", "SAPTEST");
			properties.put("license.db.code.hsqldb", "HSQLDBTEST");
			properties.put("license.db.code.mysql", "MYSQLTEST");
			properties.put("license.db.code.oracle", "ORACLETEST");
			properties.put("license.db.code.sqlserver", "SQLSERVERTEST");

			return properties;
		}
	}

}
