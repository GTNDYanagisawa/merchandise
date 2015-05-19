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
package de.hybris.platform.solrfacetsearch.embedded;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrServerConfigModel;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;


/**
 * JUnit Tests for the Solrfacetsearch extension
 */
public class SolrFacetSearchEmbeddedTest extends AbstractSolrTest
{
	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(SolrFacetSearchEmbeddedTest.class.getName());


	@Resource
	ModelService modelService;
	@Resource
	CatalogService catalogService;

	@Resource
	ProductService productService;
	@Resource
	I18NService i18nService;

	protected CatalogVersionModel winter;

	protected SolrServer server;

	@Override
	@Before
	public void setUp() throws Exception
	{
		Assume.assumeTrue(getSolrServerModeConfiguration() == EMBEDDED_MODE);
		super.setUp();
		server = getSolrService().getSolrServer(facetSearchConfig.getSolrConfig(), indexedType);
		// Delete any documents that may be there from a previous run
		dropIndex();
	}

	@Override
	protected SolrServerConfigModel setUpSolrServerConfig()
	{
		return setUpEmbeddedSolrServerConfig();
	}

	@Override
	protected void setUpBasic() throws Exception
	{
		createCoreData();
		createDefaultUsers();
	}

	@Override
	protected void setUpProductData() throws Exception
	{
		//test catalog
		importCsv("/test/testFacetSearchConfig.csv", "windows-1252");
		winter = catalogVersionService.getCatalogVersion(TESTCATALOG1, WINTER);
	}


	@Override
	protected List<CatalogVersionModel> getCatalogVersionsForSolrFacetSearchConfig()
	{
		return Collections.singletonList(winter);
	}

	/**
	 *
	 */
	@Test
	public void testEmbeddedInstance() throws Exception
	{

		try
		{
			// Get a server instance from the core container that matches the tenant id.
			for (int i = 0; i < 10; i++)
			{
				final SolrInputDocument document = new SolrInputDocument();
				document.addField("id", "hello world test " + i, 1.0f);
				final String value = "hello world ";
				document.addField("shortDescription_text_de", value + i, 1.0f);
				document.addField("shortDescription_text_en", value + i, 1.0f);
				document.addField("shortDescription_text_fr", value + i, 1.0f);
				document.addField("longDescription_text_de", value + i, 1.0f);
				document.addField("longDescription_text_en", value + i, 1.0f);
				document.addField("longDescription_text_fr", value + i, 1.0f);
				server.add(document);
			}
			server.commit();
			assertEquals(10, server.query(new SolrQuery("*:*")).getResults().size());
		}
		catch (final Exception e)
		{
			server.rollback();
		}
	}


	@Override
	protected String readXmlFile(final String pathName)
	{
		final InputStream is = SolrFacetSearchEmbeddedTest.class.getResourceAsStream(pathName);
		if (is == null)
		{
			LOG.error("file [" + pathName + "] cannot be found.");
		}
		else
		{
			try
			{
				final BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String s = "";
				final StringBuffer sb = new StringBuffer(s);
				while ((s = br.readLine()) != null)
				{
					sb.append(s);
				}
				return sb.toString();
			}
			catch (final IOException ioe)
			{
				LOG.error(ioe.getMessage());
			}
		}
		return null;
	}



}
