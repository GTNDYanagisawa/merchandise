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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrIntegrationTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;


public class DefaultIndexerServiceEmbededIntegrationTest extends AbstractSolrIntegrationTest
{



	static final String PRODUCT_CONFIG_NAME = "productConfig";

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DefaultIndexerServiceEmbededIntegrationTest.class.getName());


	@Override
	protected void setUpBasic() throws Exception
	{
		createCoreData();
		createDefaultUsers();
	}

	@Override
	protected void setUpProductData() throws Exception
	{
		createHardwareCatalog();
		hwOnline = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
	}

	@Override
	protected List<CatalogVersionModel> getCatalogVersionsForSolrFacetSearchConfig()
	{
		return Collections.singletonList(hwOnline);
	}


	@Override
	protected String getSolrConfigName()
	{
		return PRODUCT_CONFIG_NAME;
	}


	@Override
	protected List<SolrIndexerQueryModel> setUpIndexerQueries()
	{
		final SolrIndexerQueryModel fullQueryModel = modelService.create(SolrIndexerQueryModel.class);
		fullQueryModel.setType(IndexerOperationValues.FULL);
		fullQueryModel.setQuery("select {pk} from {Product}");
		fullQueryModel.setIdentifier("fullQuery");

		final SolrIndexerQueryModel updateQueryModel = modelService.create(SolrIndexerQueryModel.class);
		updateQueryModel.setType(IndexerOperationValues.UPDATE);
		updateQueryModel.setQuery("SELECT {PK} FROM {Product} WHERE {modifiedtime} >= ?lastIndexTime");
		updateQueryModel.setIdentifier("updateQuery");
		updateQueryModel.setInjectLastIndexTime(true);

		final SolrIndexerQueryModel deleteQueryModel = modelService.create(SolrIndexerQueryModel.class);
		deleteQueryModel.setType(IndexerOperationValues.DELETE);
		deleteQueryModel.setQuery("SELECT {PK} FROM {Product}");
		deleteQueryModel.setIdentifier("deleteQuery");

		return Arrays.asList(fullQueryModel, updateQueryModel, deleteQueryModel);
	}


	@Override
	protected List<SolrIndexedPropertyModel> setUpIndexProperties()
	{
		final SolrIndexedPropertyModel codeProperty = modelService.create(SolrIndexedPropertyModel.class);
		codeProperty.setName("code");
		codeProperty.setType(SolrPropertiesTypes.STRING);
		codeProperty.setFacet(true);

		final SolrIndexedPropertyModel nameProperty = modelService.create(SolrIndexedPropertyModel.class);
		nameProperty.setName("name");
		nameProperty.setType(SolrPropertiesTypes.STRING);
		nameProperty.setFacet(true);
		return Arrays.asList(nameProperty, codeProperty);
	}


	@Test
	public void testExportAllItemTypes() throws FacetConfigServiceException, IndexerException
	{
		indexerService.performFullIndex(facetSearchConfig);
	}

	@Test
	public void testExportAllItemTypesIncremental() throws FacetConfigServiceException, IndexerException
	{
		indexerService.updateIndex(facetSearchConfig);
	}


	@Test
	public void testRemoveAllIndexedDocuments() throws IndexerException, FacetConfigServiceException
	{
		indexerService.deleteFromIndex(facetSearchConfig);
	}
}
