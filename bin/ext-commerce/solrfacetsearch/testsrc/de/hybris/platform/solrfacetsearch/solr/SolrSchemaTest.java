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
package de.hybris.platform.solrfacetsearch.solr;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrValueRangeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrValueRangeSetModel;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrServer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit Tests for the Solrfacetsearch extension
 */
public class SolrSchemaTest extends AbstractSolrTest
{


	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(SolrSchemaTest.class.getName());


	private static final String TESTCATALOG1 = "testCatalog1";
	private static final String WINTER = "Winter";

	private CatalogVersionModel catalogVersion;
	private SolrValueRangeSetModel defaultSet;


	@Override
	protected void setUpBasic() throws Exception
	{
		createCoreData();
	}

	@Override
	protected void setUpProductData() throws ImpExException
	{
		importCsv("/test/testFacetSearchConfig.csv", "windows-1252");
		catalogVersion = catalogVersionService.getCatalogVersion(TESTCATALOG1, WINTER);
	}

	@Override
	protected List<CatalogVersionModel> getCatalogVersionsForSolrFacetSearchConfig()
	{
		return Collections.singletonList(catalogVersion);
	}

	@Override
	protected List<SolrIndexedPropertyModel> setUpIndexProperties()
	{
		final SolrIndexedPropertyModel codeProperty = modelService.create(SolrIndexedPropertyModel.class);
		codeProperty.setName("code");
		codeProperty.setType(SolrPropertiesTypes.STRING);
		codeProperty.setFacet(true);

		final SolrIndexedPropertyModel descriptionProperty = modelService.create(SolrIndexedPropertyModel.class);
		descriptionProperty.setName("description");
		descriptionProperty.setType(SolrPropertiesTypes.STRING);
		descriptionProperty.setFacet(true);

		final SolrIndexedPropertyModel nameProperty = modelService.create(SolrIndexedPropertyModel.class);
		nameProperty.setName("name");
		nameProperty.setType(SolrPropertiesTypes.STRING);
		nameProperty.setFacet(false);
		nameProperty.setRangeSet(defaultSet);
		// YTODO Auto-generated method stub
		return Arrays.asList(codeProperty, descriptionProperty, nameProperty);
	}

	@Override
	protected List<SolrValueRangeSetModel> setUpValueRanges()
	{
		defaultSet = modelService.create(SolrValueRangeSetModel.class);
		defaultSet.setName("defaultSet");
		defaultSet.setType("double");

		final SolrValueRangeModel rangeModel = modelService.create(SolrValueRangeModel.class);
		rangeModel.setName("doubleRange");
		rangeModel.setFrom("1");
		rangeModel.setTo("999");
		rangeModel.setSolrValueRangeSet(defaultSet);

		defaultSet.setSolrValueRanges(Arrays.asList(rangeModel));
		return Collections.singletonList(defaultSet);
	}


	/**
	 * @return the tennantId
	 */
	public String getTennantId()
	{
		return jaloSession.getTenant().getTenantID();
	}

	private SolrServer getSolrServer() throws SolrServiceException, FacetConfigServiceException
	{
		return getSolrService().getSolrServerMaster(solrConfig, indexedType);
	}



	@Test
	public void testDynamicInt() throws Exception
	{
		final String dynamicField = "dynamic_int";
		final String dynamicFieldMultiValued = "dynamic_int_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, Integer.valueOf(1));
			document.addField(dynamicFieldMultiValued, Integer.valueOf(3));
			document.addField(dynamicFieldMultiValued, Integer.valueOf(4));
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), Integer.valueOf(1));

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains(Integer.valueOf(3)));
			Assert.assertTrue(dynamicFieldValues.contains(Integer.valueOf(4)));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}

	@Test
	public void testDynamicString() throws Exception
	{
		final String dynamicField = "dynamic_string";
		final String dynamicFieldMultiValued = "dynamic_string_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, "1");
			document.addField(dynamicFieldMultiValued, "3");
			document.addField(dynamicFieldMultiValued, "4");
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), "1");

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains("3"));
			Assert.assertTrue(dynamicFieldValues.contains("4"));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}

	@Test
	public void testDynamicLong() throws Exception
	{
		final String dynamicField = "dynamic_long";
		final String dynamicFieldMultiValued = "dynamic_long_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, Long.valueOf(1));
			document.addField(dynamicFieldMultiValued, Long.valueOf(Long.MIN_VALUE));
			document.addField(dynamicFieldMultiValued, Long.valueOf(Long.MAX_VALUE));
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), Long.valueOf(1));

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains(Long.valueOf(Long.MIN_VALUE)));
			Assert.assertTrue(dynamicFieldValues.contains(Long.valueOf(Long.MAX_VALUE)));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}

	@Test
	public void testDynamicText() throws Exception
	{
		final String dynamicField = "dynamic_text";
		final String dynamicFieldMultiValued = "dynamic_text_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, "Hello World");
			document.addField(dynamicFieldMultiValued, "Hello World 1");
			document.addField(dynamicFieldMultiValued, "Hello World 2");
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), "Hello World");

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains("Hello World 1"));
			Assert.assertTrue(dynamicFieldValues.contains("Hello World 2"));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}

	@Test
	public void testDynamicBoolean() throws Exception
	{
		final String dynamicField = "dynamic_boolean";
		final String dynamicFieldMultiValued = "dynamic_boolean_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, Boolean.TRUE);
			document.addField(dynamicFieldMultiValued, Boolean.FALSE);
			document.addField(dynamicFieldMultiValued, Boolean.TRUE);
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), Boolean.TRUE);

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains(Boolean.TRUE));
			Assert.assertTrue(dynamicFieldValues.contains(Boolean.FALSE));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}

	@Test
	public void testDynamicFloat() throws Exception
	{
		final String dynamicField = "dynamic_float";
		final String dynamicFieldMultiValued = "dynamic_float_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, Float.valueOf(1.0f));
			document.addField(dynamicFieldMultiValued, Float.valueOf(2.0f));
			document.addField(dynamicFieldMultiValued, Float.valueOf(3.0f));
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), Float.valueOf(1.0f));

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains(Float.valueOf(2.0f)));
			Assert.assertTrue(dynamicFieldValues.contains(Float.valueOf(3.0f)));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}

	@Test
	public void testDynamicDouble() throws Exception
	{
		final String dynamicField = "dynamic_double";
		final String dynamicFieldMultiValued = "dynamic_double_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, Double.valueOf(1.0f));
			document.addField(dynamicFieldMultiValued, Double.valueOf(2.0f));
			document.addField(dynamicFieldMultiValued, Double.valueOf(3.0f));
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), Double.valueOf(1.0f));

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains(Double.valueOf(2.0f)));
			Assert.assertTrue(dynamicFieldValues.contains(Double.valueOf(3.0f)));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}

	@Test
	public void testDynamicDate() throws Exception
	{
		final String dynamicField = "dynamic_date";
		final String dynamicFieldMultiValued = "dynamic_date_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final Calendar date1 = Calendar.getInstance();
			final Calendar date2 = Calendar.getInstance();
			final Calendar date3 = Calendar.getInstance();
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, date1.getTime());
			document.addField(dynamicFieldMultiValued, date2.getTime());
			document.addField(dynamicFieldMultiValued, date3.getTime());
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), date1.getTime());

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains(date2.getTime()));
			Assert.assertTrue(dynamicFieldValues.contains(date3.getTime()));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}


	@Test
	public void testDynamicTrieInt() throws Exception
	{
		final String dynamicField = "dynamic_tint";
		final String dynamicFieldMultiValued = "dynamic_tint_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, Integer.valueOf(1));
			document.addField(dynamicFieldMultiValued, Integer.valueOf(3));
			document.addField(dynamicFieldMultiValued, Integer.valueOf(4));
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), Integer.valueOf(1));

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains(Integer.valueOf(3)));
			Assert.assertTrue(dynamicFieldValues.contains(Integer.valueOf(4)));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}



	@Test
	public void testDynamicTrieLong() throws Exception
	{
		final String dynamicField = "dynamic_tlong";
		final String dynamicFieldMultiValued = "dynamic_tlong_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, Long.valueOf(1));
			document.addField(dynamicFieldMultiValued, Long.valueOf(Long.MIN_VALUE));
			document.addField(dynamicFieldMultiValued, Long.valueOf(Long.MAX_VALUE));
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), Long.valueOf(1));

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains(Long.valueOf(Long.MIN_VALUE)));
			Assert.assertTrue(dynamicFieldValues.contains(Long.valueOf(Long.MAX_VALUE)));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}


	@Test
	public void testDynamicTrieFloat() throws Exception
	{
		final String dynamicField = "dynamic_tfloat";
		final String dynamicFieldMultiValued = "dynamic_tfloat_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, Float.valueOf(1.0f));
			document.addField(dynamicFieldMultiValued, Float.valueOf(2.0f));
			document.addField(dynamicFieldMultiValued, Float.valueOf(3.0f));
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), Float.valueOf(1.0f));

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains(Float.valueOf(2.0f)));
			Assert.assertTrue(dynamicFieldValues.contains(Float.valueOf(3.0f)));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}

	@Test
	public void testDynamicTrieDouble() throws Exception
	{
		final String dynamicField = "dynamic_tdouble";
		final String dynamicFieldMultiValued = "dynamic_tdouble_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, Double.valueOf(1.0f));
			document.addField(dynamicFieldMultiValued, Double.valueOf(2.0f));
			document.addField(dynamicFieldMultiValued, Double.valueOf(3.0f));
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), Double.valueOf(1.0f));

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains(Double.valueOf(2.0f)));
			Assert.assertTrue(dynamicFieldValues.contains(Double.valueOf(3.0f)));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}

	@Test
	public void testDynamicTrieDate() throws Exception
	{
		final String dynamicField = "dynamic_tdate";
		final String dynamicFieldMultiValued = "dynamic_tdate_mv";
		final SolrServer solrServer = getSolrServer();
		final String id = UUID.randomUUID().toString();
		try
		{
			final Calendar date1 = Calendar.getInstance();
			final Calendar date2 = Calendar.getInstance();
			final Calendar date3 = Calendar.getInstance();
			final SolrInputDocument document = new SolrInputDocument();
			document.addField("id", id);
			document.addField(dynamicField, date1.getTime());
			document.addField(dynamicFieldMultiValued, date2.getTime());
			document.addField(dynamicFieldMultiValued, date3.getTime());
			solrServer.add(document);
			solrServer.commit();

			// Perform the query, getting the response, and validating the results
			final QueryResponse response = solrServer.query(new SolrQuery("id:" + id));
			Assert.assertNotNull(response);
			final SolrDocumentList documents = response.getResults();
			Assert.assertNotNull(documents);
			Assert.assertEquals(1, documents.size());
			final SolrDocument resultDocument = documents.iterator().next();
			Assert.assertNotNull(resultDocument);

			// Verify the identifier field
			Assert.assertEquals(resultDocument.getFieldValue("id"), id);

			// Verify the non-multivalued field
			Assert.assertEquals(resultDocument.getFieldValue(dynamicField), date1.getTime());

			// Verify the multivalued field
			final Collection<Object> dynamicFieldValues = resultDocument.getFieldValues(dynamicFieldMultiValued);
			Assert.assertNotNull(dynamicFieldValues);
			Assert.assertEquals(2, dynamicFieldValues.size());
			Assert.assertTrue(dynamicFieldValues.contains(date2.getTime()));
			Assert.assertTrue(dynamicFieldValues.contains(date3.getTime()));
		}
		finally
		{
			if (solrServer != null)
			{
				solrServer.deleteById(id);
				solrServer.commit();
			}
		}
	}
}
