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
package de.hybris.platform.solrfacetsearch.valueprovider.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.solrfacetsearch.loader.impl.DefaultModelLoader;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.solr.common.SolrDocument;
import org.junit.Before;
import org.junit.Test;


/**
 * @author juergen
 * 
 */
public class DefaultModelLoaderTest extends ServicelayerTransactionalTest
{

	@Resource
	private DefaultModelLoader defaultModelLoader;
	@Resource
	private ProductService productService;
	@Resource
	private CatalogService catalogService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();
	}


	@Test
	public void testLoadProducts() throws Exception
	{
		final String[] codes =
		{ "HW2310-1003", "HW2310-1004", "HW2310-1005" };
		final CatalogVersionModel catalogVersion = catalogService.getCatalogVersion("hwcatalog", "Online");
		final Collection<SolrDocument> docs = new ArrayList<SolrDocument>();
		for (final String code : codes)
		{
			final SolrDocument doc = new SolrDocument();
			doc.addField("id", "hwcatalog/Online/" + code);
			final ProductModel product = productService.getProduct(catalogVersion, code);
			doc.addField("pk", Long.valueOf(product.getPk().getLongValue()));
			docs.add(doc);
		}
		Collection<Object> products = defaultModelLoader.loadModels(docs);
		assertNotNull("Loaded products must not be null", products);
		// Wrap it in a modifable collection
		products = new ArrayList<Object>(products);
		assertEquals("Size of products collection", 3, products.size());
		for (final String id : codes)
		{
			assertContainsProductWithCode(products, id);
		}
		assertTrue("No products must be left", products.isEmpty());
	}

	private void assertContainsProductWithCode(final Collection<Object> products, final String code)
	{
		boolean found = false;
		for (final Object o : products)
		{
			final ProductModel product = (ProductModel) o;
			if (product.getCode().equals(code))
			{
				products.remove(product);
				found = true;
				break;
			}
		}
		assertTrue("No product found with code " + code, found);
	}

}
