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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.security.Principal;
import de.hybris.platform.util.Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;


/**
 *	
 */
@IntegrationTest(standalone = false)
public class VjdbcFlexScopedTest extends AbstractVjdbcFlexTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(VjdbcFlexScopedTest.class);

	private final static int EXPECTED_PRODUCT_SCOPED_COUNT = 1;
	private final static String RO_PRODUCT = "someRooProduct";
	private final static String RW_PRODUCT = "someKangaProduct";
	private final static String RW_CAT_VERSION = "kangaVersion";
	private final static String RO_CAT_VERSION = "rooVersion";

	@Override
	protected void setUpProducts()
	{
		try
		{
			createReadOnlyData();
			createReadWriteData();
		}
		catch (final ConsistencyCheckException cce)
		{
			throw new IllegalStateException(cce);
		}
	}

	/**
	 * create category for r/o group
	 */
	private void createReadOnlyData() throws ConsistencyCheckException
	{
		final Language lang1 = C2LManager.getInstance().createLanguage("someLang");
		final Catalog catalog1 = CatalogManager.getInstance().createCatalog("RoooCatalog");
		final CatalogVersion version1 = CatalogManager.getInstance().createCatalogVersion(catalog1, RO_CAT_VERSION, lang1);

		version1.setActive(true);

		final Category category1 = CategoryManager.getInstance().createCategory("rooCategory");

		CatalogManager.getInstance().setCatalogVersion(category1, version1);
		version1.setRootCategories(Collections.singletonList(category1));
		version1.setReadPrincipals(Arrays.asList(new Principal[]
		{ getUserGroup(false) }));
		version1.setWritePrincipals(Arrays.asList(new Principal[]
		{ getUserGroup(false) }));

		final Product product = ProductManager.getInstance().createProduct(RO_PRODUCT);
		category1.addProduct(product);

		CatalogManager.getInstance().setCatalogVersion(product, version1);
	}

	/**
	 * create category for r/w group
	 */
	private void createReadWriteData() throws ConsistencyCheckException
	{
		final Language lang1 = C2LManager.getInstance().createLanguage("someOtherLang");
		final Catalog catalog1 = CatalogManager.getInstance().createCatalog("KangaCatalog");
		final CatalogVersion version1 = CatalogManager.getInstance().createCatalogVersion(catalog1, RW_CAT_VERSION, lang1);

		version1.setActive(true);

		final Category category1 = CategoryManager.getInstance().createCategory("kangaCategory");

		CatalogManager.getInstance().setCatalogVersion(category1, version1);
		version1.setRootCategories(Collections.singletonList(category1));
		version1.setReadPrincipals(Arrays.asList(new Principal[]
		{ getUserGroup(true) }));
		version1.setWritePrincipals(Arrays.asList(new Principal[]
		{ getUserGroup(true) }));

		final Product product = ProductManager.getInstance().createProduct(RW_PRODUCT);
		category1.addProduct(product);

		CatalogManager.getInstance().setCatalogVersion(product, version1);
	}


	private String buildQuery(final String templateQuery, final boolean adminMode)
	{
		final Collection<CatalogVersion> ccv = CatalogManager.getInstance().getAllReadableCatalogVersions(
				jaloSession.getSessionContext(), getUser(adminMode));
		final StringBuffer sbuff = new StringBuffer(100);
		for (final CatalogVersion cv : ccv)
		{
			sbuff.append(cv.getPK().getLongValueAsString() + " ,");
		}
		return String.format(templateQuery, sbuff.substring(0, sbuff.lastIndexOf(",") - 1));
	}

	/**
	 * @param prepared
	 *           - mode true - rw else -ro
	 */
	private void executePreparedInReadWriteMode(final Connection vjdbcConnection, final boolean readWriteMode) throws SQLException
	{
		final String realQuery = buildQuery(
				"SELECT {p.code}, {cv.version} FROM {Product as p}, {CatalogVersion as cv} WHERE {p.catalogVersion} = {cv.PK} AND {p.catalogVersion} IN (%s)",
				readWriteMode);

		ResultSet res = null;
		PreparedStatement pstmt = null;
		try
		{
			LOG.info("Executing query:: " + realQuery);
			pstmt = vjdbcConnection.prepareStatement(realQuery);
			res = pstmt.executeQuery();
			int idx = 0;
			if (res != null)
			{
				while (res.next())
				{
					++idx;
					if (LOG.isDebugEnabled())
					{
						LOG.debug("result[" + (idx) + "]:" + res.getString(1));
					}
					Assert.assertTrue(res.getString(1).equals(readWriteMode ? RW_PRODUCT : RO_PRODUCT));
					Assert.assertTrue(res.getString(2).equals(readWriteMode ? RW_CAT_VERSION : RO_CAT_VERSION));
				}
			}
			Assert.assertEquals("Should get " + EXPECTED_PRODUCT_SCOPED_COUNT + " instead of " + idx, EXPECTED_PRODUCT_SCOPED_COUNT,
					idx);
		}
		finally
		{
			Utilities.tryToCloseJDBC(vjdbcConnection, pstmt, res);
		}
	}


	@Test
	public void checkProductCountForReadOnly() throws Exception
	{
		executePreparedInReadWriteMode(getHttpFlexConnection(getUserPrincipals(false)), false);//read mode
	}

	@Test
	public void checkProductCountForReadWrite() throws Exception
	{
		executePreparedInReadWriteMode(getHttpFlexConnection(getUserPrincipals(true)), true);//read-write mode
	}

}
