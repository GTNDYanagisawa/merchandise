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
package de.hybris.platform.solrfacetsearch.config.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.solrfacetsearch.config.xml.Config;
import de.hybris.platform.solrfacetsearch.config.xml.PropertyType;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;


/**
 * 
 */
public class FacetSearchXMLParserTest extends ServicelayerTransactionalTest
{
	@Resource
	FacetSearchXMLConfigParser xmlConfigParser;

	/**
	 * This test examines proper operation of FacetSearchXMLConfigParser. Test Configuration contains the following data
	 * 
	 * &lt;?xml version="1.0" encoding="UTF-8"?&gt; &lt;config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 * xsi:noNamespaceSchemaLocation="../config/SolrFacetConfig.xsd"&gt; &lt;solrconfig&gt;
	 * &lt;mode&gt;standalone&lt;/mode&gt; &lt;endpointURLs&gt; &lt;endpointURL
	 * timeout="100"&gt;http://localhost/test&lt;/endpointURL&gt; &lt;/endpointURLs&gt; &lt;/solrconfig&gt;
	 * &lt;indexconfig&gt; &lt;exportpath&gt;testexportpath&lt;/exportpath&gt; &lt;batchSize&gt;100&lt;/batchSize&gt;
	 * &lt;/indexconfig&gt; &lt;itemTypes&gt; &lt;itemType code="Product" isVariant="false"
	 * identityProvider="testProvider"&gt; &lt;properties&gt; &lt;property isFacet="true" name="prop1" type="boolean"
	 * /&gt; &lt;property isFacet="true" name="prop2" type="string" /&gt; &lt;property isFacet="false" name="prop3"
	 * type="double" useRangeName="doubleRange" /&gt; &lt;/properties&gt; &lt;flexibleSearchUpdateQueries&gt;
	 * &lt;fullIndexQuery injectCurrentDate="false" injectCurrentTime="false" injectLastIndexTime="false"
	 * parameterProvider="test1" user="anonymous"&gt;fullIndexQuery&lt;/fullIndexQuery&gt; &lt;updateIndexQuery
	 * injectCurrentDate="false" injectCurrentTime="false" injectLastIndexTime="true" parameterProvider="test2"
	 * user="anonymous"&gt;updateIndexQuery&lt;/updateIndexQuery&gt; &lt;deleteFromIndexQuery injectCurrentDate="true"
	 * injectCurrentTime="true" injectLastIndexTime="true" parameterProvider="test3"
	 * user="anonymous"&gt;deleteFromIndexQuery&lt;/deleteFromIndexQuery&gt; &lt;/flexibleSearchUpdateQueries&gt;
	 * &lt;/itemType&gt; &lt;/itemTypes&gt; &lt;/config&gt;
	 */

	@Test
	public void testFacetSearchXMLConfigParser()
	{
		final Config config = xmlConfigParser.parseConfig("/test/TestFacetConfig.xml");
		Assert.assertNotNull(config);


		//examine solrConfig element
		assertEquals("embedded", config.getSolrconfig().getMode().value());
		assertEquals("http://localhost/test", config.getSolrconfig().getClusterconfig().getEndpointURLs().getEndpointURL().get(0)
				.getValue());
		assertTrue(config.getSolrconfig().getClusterconfig().getEndpointURLs().getEndpointURL().get(0).isMaster());

		//examine index config
		assertEquals("testexportpath", config.getIndexconfig().getExportpath());
		assertEquals(100, config.getIndexconfig().getBatchSize().intValue());

		//examine itemTypes
		assertEquals(1, config.getItemTypes().getItemType().size());
		assertEquals("Product", config.getItemTypes().getItemType().get(0).getCode());
		assertFalse(config.getItemTypes().getItemType().get(0).isVariant());
		//assertEquals("testProvider", config.getItemTypes().getItemType().get(0).get);
		assertEquals(3, config.getItemTypes().getItemType().get(0).getProperties().getProperty().size());

		//examine item's properties
		assertEquals("code", config.getItemTypes().getItemType().get(0).getProperties().getProperty().get(0).getName());
		assertSame(PropertyType.STRING, config.getItemTypes().getItemType().get(0).getProperties().getProperty().get(0).getType());
		assertTrue(config.getItemTypes().getItemType().get(0).getProperties().getProperty().get(0).isFacet());

		assertEquals("description", config.getItemTypes().getItemType().get(0).getProperties().getProperty().get(1).getName());
		assertEquals("string", config.getItemTypes().getItemType().get(0).getProperties().getProperty().get(1).getType().value());
		assertTrue(config.getItemTypes().getItemType().get(0).getProperties().getProperty().get(1).isFacet());

		assertEquals("name", config.getItemTypes().getItemType().get(0).getProperties().getProperty().get(2).getName());
		assertEquals(PropertyType.STRING, config.getItemTypes().getItemType().get(0).getProperties().getProperty().get(2).getType());
		assertFalse(config.getItemTypes().getItemType().get(0).getProperties().getProperty().get(2).isFacet());
		assertEquals("defaultSet", config.getItemTypes().getItemType().get(0).getProperties().getProperty().get(2).getRangeSet());

		//examine item's flexible querries
		assertEquals("fullIndexQuery", config.getItemTypes().getItemType().get(0).getFlexibleSearchUpdateQueries()
				.getFullIndexQuery().getValue());
		assertEquals("simpleParameterProvider", config.getItemTypes().getItemType().get(0).getFlexibleSearchUpdateQueries()
				.getFullIndexQuery().getParameterProvider());

		assertEquals("updateIndexQuery", config.getItemTypes().getItemType().get(0).getFlexibleSearchUpdateQueries()
				.getUpdateIndexQuery().getValue());

		assertEquals("deleteFromIndexQuery", config.getItemTypes().getItemType().get(0).getFlexibleSearchUpdateQueries()
				.getDeleteFromIndexQuery().getValue());

	}
}
