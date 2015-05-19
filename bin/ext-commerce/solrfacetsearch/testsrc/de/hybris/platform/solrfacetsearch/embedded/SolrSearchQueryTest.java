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
import static org.junit.Assert.assertTrue;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SolrQueryConverter;
import de.hybris.platform.solrfacetsearch.search.SolrQueryPostProcessor;
import de.hybris.platform.solrfacetsearch.search.impl.DefaultSolrQueryConverter;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;
import de.hybris.platform.testframework.Transactional;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.fest.util.Collections;
import org.junit.Test;


@Transactional
public class SolrSearchQueryTest extends AbstractSolrTest
{

	private static final Logger LOG = Logger.getLogger(SolrSearchQueryTest.class);

	@Resource
	private FieldNameProvider solrFieldNameProvider;
	@Resource(name = "solrQueryConverter")
	private SolrQueryConverter converter;

	private SolrServer server;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		server = getSolrService().getSolrServer(facetSearchConfig.getSolrConfig(), indexedType);
		dropIndex();
	}

	@Override
	protected String getSolrConfigName()
	{
		return "SearchQueryTest";
	}



	@Test
	public void testSearchQuery() throws Exception
	{

		//create a file reader to get the string in the xml file
		final String xmlFile = readXmlFile("/test/TestFacetSearchQuery.xml");
		final DirectXmlRequest xmlRequest = new DirectXmlRequest("/update", xmlFile);
		server.request(xmlRequest);
		server.commit();

		//test number of all imported products
		final SearchQuery query = new SearchQuery(facetSearchConfig, indexedType);


		String language = "de";
		query.setLanguage(language);
		final String currency = "eur";
		query.setCurrency(currency);

		assertEquals(10, server.query(converter.convertSolrQuery(query)).getResults().getNumFound());

		//test without field
		query.search("maxtor");
		LOG.debug(server.query(converter.convertSolrQuery(query)));
		assertEquals(2, server.query(converter.convertSolrQuery(query)).getResults().getNumFound());
		query.clearAllFields();

		//test single field
		final String FIELD_NAME = "name";
		final String FIELD_CATEGORY = "category";
		query.addFacetValue(FIELD_NAME, "Dell");
		assertEquals(1, server.query(converter.convertSolrQuery(query)).getResults().getNumFound());
		query.clearAllFields();
		query.addFacetValue(FIELD_NAME, "maxtor");

		assertEquals(2, server.query(converter.convertSolrQuery(query)).getResults().getNumFound());
		query.clearAllFields();
		query.addFacetValue(FIELD_NAME, "notFound");

		assertEquals(0, server.query(converter.convertSolrQuery(query)).getResults().getNumFound());

		//test multifields
		language = "en";
		query.setLanguage(language);
		query.clearAllFields();
		query.addFacetValue(FIELD_CATEGORY, "camera");
		assertEquals(3, server.query(converter.convertSolrQuery(query)).getResults().getNumFound());
		query.addFacetValue(FIELD_NAME, "sony");
		assertEquals(2, server.query(converter.convertSolrQuery(query)).getResults().getNumFound());

		//test remove field with value
		query.removeFacetValue(FIELD_NAME, "sony");
		assertEquals(3, server.query(converter.convertSolrQuery(query)).getResults().getNumFound());
		//test remove field
		query.removeField("category");
		assertEquals(10, server.query(converter.convertSolrQuery(query)).getResults().getNumFound());
	}

	@Test
	public void testSearchQueryWithPreProcessors() throws Exception
	{

		//create a file reader to get the string in the xml file
		final String xmlFile = readXmlFile("/test/TestFacetSearchQuery.xml");
		final DirectXmlRequest xmlRequest = new DirectXmlRequest("/update", xmlFile);
		server.request(xmlRequest);
		server.commit();

		//test number of all imported products
		final FacetSearchConfig config = facetSearchConfigService.getConfiguration(localConfig.getName());
		final SearchQuery query = new SearchQuery(config, indexedType);

		final String language = "de";
		query.setLanguage(language);
		final String currency = "eur";
		query.setCurrency(currency);

		final DefaultSolrQueryConverter converter = new DefaultSolrQueryConverter();
		converter.setSolrFieldNameProvider(solrFieldNameProvider);
		converter.setQueryPostProcessors(Collections.<SolrQueryPostProcessor> list(new SolrQueryPostProcessor()
		{

			@Override
			public SolrQuery process(final SolrQuery query, final SearchQuery solrSearchQuery)
			{
				query.setSortField("id", ORDER.asc);
				query.setStart(Integer.valueOf(0));
				query.setRows(Integer.valueOf(10));
				return query;
			}
		}));
		Object prev = null;
		SolrDocumentList results = server.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("Result size limited in PostProcessor", 10, results.size());
		for (final SolrDocument doc : results)
		{
			final Object fieldValue = doc.getFieldValue("id");
			if (prev != null)
			{
				assertTrue(prev instanceof Comparable);
				assertTrue(((Comparable) prev).compareTo(fieldValue) < 0);
			}
			prev = fieldValue;
		}
		converter.setQueryPostProcessors(Collections.<SolrQueryPostProcessor> list(new SolrQueryPostProcessor()
		{

			@Override
			public SolrQuery process(final SolrQuery query, final SearchQuery solrSearchQuery)
			{
				query.setSortField("id", ORDER.desc);
				query.setStart(Integer.valueOf(2));
				query.setRows(Integer.valueOf(10));
				return query;
			}
		}));
		results = server.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("Result size limited in PostProcessor", 8, results.size());
		for (final SolrDocument doc : results)
		{
			final Object fieldValue = doc.getFieldValue("id");
			if (prev != null)
			{
				assertTrue(prev instanceof Comparable);
				assertTrue(((Comparable) prev).compareTo(fieldValue) > 0);
			}
			prev = fieldValue;
		}
		converter.setQueryPostProcessors(Collections.<SolrQueryPostProcessor> list(new SolrQueryPostProcessor()
		{
			@Override
			public SolrQuery process(final SolrQuery query, final SearchQuery solrSearchQuery)
			{
				query.setSortField("id", ORDER.asc);
				return query;
			}
		}, new SolrQueryPostProcessor()
		{

			@Override
			public SolrQuery process(final SolrQuery query, final SearchQuery solrSearchQuery)
			{
				query.setStart(Integer.valueOf(3));
				return query;
			}
		}, new SolrQueryPostProcessor()
		{

			@Override
			public SolrQuery process(final SolrQuery query, final SearchQuery solrSearchQuery)
			{
				query.setRows(Integer.valueOf(6));
				return query;
			}
		}));
		results = server.query(converter.convertSolrQuery(query)).getResults();
		assertEquals("Result size limited in PostProcessor", 6, results.size());
		for (final SolrDocument doc : results)
		{
			final Object fieldValue = doc.getFieldValue("id");
			if (prev != null)
			{
				assertTrue(prev instanceof Comparable);
				assertTrue(((Comparable) prev).compareTo(fieldValue) <= 0);
			}
			prev = fieldValue;
		}
	}

	@Override
	protected List<SolrIndexedPropertyModel> setUpIndexProperties()
	{
		final SolrIndexedPropertyModel codeProperty = modelService.create(SolrIndexedPropertyModel.class);
		codeProperty.setName("code");
		codeProperty.setType(SolrPropertiesTypes.STRING);
		final SolrIndexedPropertyModel descriptionProperty = modelService.create(SolrIndexedPropertyModel.class);
		descriptionProperty.setName("description");
		descriptionProperty.setType(SolrPropertiesTypes.TEXT);
		descriptionProperty.setLocalized(true);
		final SolrIndexedPropertyModel nameProperty = modelService.create(SolrIndexedPropertyModel.class);
		nameProperty.setName("name");
		nameProperty.setType(SolrPropertiesTypes.TEXT);
		nameProperty.setLocalized(true);

		final SolrIndexedPropertyModel categoryProperty = modelService.create(SolrIndexedPropertyModel.class);
		categoryProperty.setName("category");
		categoryProperty.setType(SolrPropertiesTypes.STRING);
		categoryProperty.setLocalized(false);

		return Arrays.asList(codeProperty, descriptionProperty, nameProperty, categoryProperty);
	}
}
