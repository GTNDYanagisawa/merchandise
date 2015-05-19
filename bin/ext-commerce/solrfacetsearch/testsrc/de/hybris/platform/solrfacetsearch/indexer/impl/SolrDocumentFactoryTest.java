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
package de.hybris.platform.solrfacetsearch.indexer.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperties;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.impl.MockFacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.indexer.spi.SolrDocumentFactory;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

import java.util.Collection;

import javax.annotation.Resource;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;


/**
 * @author juergen
 * 
 */
public class SolrDocumentFactoryTest extends ServicelayerTest
{

	private FacetSearchConfigService facetSearchConfigService;

	@Resource
	private ProductService productService;
	@Resource
	private SolrDocumentFactory solrDocumentFactory;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();
		facetSearchConfigService = new MockFacetSearchConfigService();
		final AutowireCapableBeanFactory beanFactory = getApplicationContext().getAutowireCapableBeanFactory();
		beanFactory.autowireBeanProperties(facetSearchConfigService, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
	}


	@Test
	public void testCreateSolrInputDocument() throws Exception
	{
		final FacetSearchConfig config = facetSearchConfigService.getConfiguration("productSearch");
		assertNotNull("Facet Search Config must not be null", config);
		final ProductModel product = productService.getProduct("HW2310-1004");
		assertNotNull("Product must not be null", product);
		final IndexConfig indexConfig = config.getIndexConfig();
		assertNotNull("Index config must not be null", indexConfig);
		final Collection<IndexedType> indexedTypes = indexConfig.getIndexedTypes().values();
		assertNotNull("Collection of indexed types must not be null", indexedTypes);
		assertEquals("Size of collection of indexed types", 1, indexedTypes.size());
		final IndexedType indexedType = indexedTypes.iterator().next();
		final SolrInputDocument inputDocument = solrDocumentFactory.createInputDocument(product, config.getIndexConfig(),
				indexedType);
		assertNotNull("id must not be null", inputDocument.getField("id"));
		inputDocument.removeField("id");
		assertNotNull("pk must not be null", inputDocument.getField("pk"));
		inputDocument.removeField("pk");
		assertNotNull("catalogId must not be null", inputDocument.getField("catalogId"));
		assertEquals("Catalog ID", product.getCatalogVersion().getCatalog().getId(), inputDocument.getField("catalogId").getValue());
		inputDocument.removeField("catalogId");
		assertNotNull("catalogVersion must not be null", inputDocument.getField("catalogVersion"));
		assertEquals("Catalog Version", product.getCatalogVersion().getVersion(), inputDocument.getField("catalogVersion")
				.getValue());
		inputDocument.removeField("catalogVersion");
		final Collection<IndexedProperty> indexedProperties = indexedType.getIndexedProperties().values();
		for (final IndexedProperty indexedProperty : indexedProperties)
		{
			final Collection<FieldValue> fieldValues = IndexedProperties.getFieldValueProvider(indexedProperty).getFieldValues(
					indexConfig, indexedProperty, product);
			assertContainsValues(inputDocument, fieldValues);
		}
		assertTrue("Input document has too many fields", inputDocument.getFieldNames().isEmpty());
	}

	/**
	 * @param inputDocument
	 * @param fieldValues
	 */
	private void assertContainsValues(final SolrInputDocument inputDocument, final Collection<FieldValue> fieldValues)
	{
		for (final FieldValue fieldValue : fieldValues)
		{
			final String fieldName = fieldValue.getFieldName();
			final Object value = inputDocument.getFieldValue(fieldName);
			assertEquals("Field value for " + fieldName, fieldValue.getValue(), value);
			inputDocument.removeField(fieldName);
		}
	}

}
