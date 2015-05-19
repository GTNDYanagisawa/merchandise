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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.virtualjdbc.db.ReadWriteVjdbcDataSourceImplFactory;
import de.hybris.platform.virtualjdbc.db.VjdbcDataSourceImplFactory;

import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * JUnit Tests for the Vjdbc extension it is somehow unwise to destroy jdbconnection pool. What about subsequent test
 * using connections from (ReadWrite)VjdbcDataSourceImplFactory ??
 */
@IntegrationTest
public class VjdbcTest extends AbstractVjdbcTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(VjdbcTest.class.getName());

	@Test
	public void testDataSourceFactoryInstance()
	{
		HybrisDataSource ds = null;
		ds = Registry.getCurrentTenant().getDataSource(VjdbcDataSourceImplFactory.class.getName());
		assertEquals("datasource is NOT read-only (and should be)", true, ds.isReadOnly());
		assertTrue(ds.getDataSourceFactory() instanceof VjdbcDataSourceImplFactory);

		ds = Registry.getCurrentTenant().getDataSource(ReadWriteVjdbcDataSourceImplFactory.class.getName());
		assertEquals("datasource is read-only (and should NOT be)", false, ds.isReadOnly());
		assertTrue(ds.getDataSourceFactory() instanceof VjdbcDataSourceImplFactory);

	}
}
