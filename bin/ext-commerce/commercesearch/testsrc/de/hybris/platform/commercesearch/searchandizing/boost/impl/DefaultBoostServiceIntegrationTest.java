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
package de.hybris.platform.commercesearch.searchandizing.boost.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercesearch.model.AbstractSolrSearchProfileModel;
import de.hybris.platform.commercesearch.model.CategorySolrSearchProfileModel;
import de.hybris.platform.commercesearch.model.GlobalSolrSearchProfileModel;
import de.hybris.platform.commercesearch.searchandizing.boost.BoostService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.DefaultFlexibleSearchService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.init.SolrfacetsearchDataSetup;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.util.CSVConstants;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@IntegrationTest
public class DefaultBoostServiceIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String CATALOG = "electronicsProductCatalog";
	private static final String CATALOG_VERSION = "Online";
	private static final String MAIN_CATEGORY = "main_category";
	private static final String MAIN_CATEGORY_PROFILE = "main_category-profile";
	private static final String GLOBAL_PROFILE = "global-profile";
	private static final String SOLR_INDEXED_TYPE = "electronicsProductType";

	@Resource
	private ImportService importService;
	@Resource
	private ModelService modelService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private FacetSearchConfigService facetSearchConfigService;
	@Resource
	private DefaultFlexibleSearchService flexibleSearchService;

	@Resource
	private BoostService boostService;

	CatalogVersionModel catalogVersion;
	CategoryModel mainCategory;
	SolrIndexedTypeModel solrIndexedType;
	FacetSearchConfig facetSearchConfig;
	IndexedType indexedType;

	@Before
	public void setUp() throws Exception
	{
		importService.importData(new StreamBasedImpExResource(SolrfacetsearchDataSetup.class.
				getResourceAsStream("/test/impex/catalog.impex"), CSVConstants.HYBRIS_ENCODING,
				Character.valueOf(';')));
		importService.importData(new StreamBasedImpExResource(SolrfacetsearchDataSetup.class.
				getResourceAsStream("/test/impex/solr.impex"), CSVConstants.HYBRIS_ENCODING, Character.valueOf(';')));

		catalogVersion = catalogVersionService.getCatalogVersion(CATALOG, CATALOG_VERSION);
		facetSearchConfig = facetSearchConfigService.getConfiguration(catalogVersion);
		indexedType = getIndexedType(facetSearchConfig);
		solrIndexedType = getSolrIndexedType();
		createCategories();
	}

	@Test
	public void returnsNullAsProfileForCategory() throws Throwable
	{
		// given
		// no search profile created for the main category

		// when
		final AbstractSolrSearchProfileModel solrSearchProfile
				= boostService.getProfileForCategoryAndIndexedType(indexedType, mainCategory, facetSearchConfig);
		// then
		assertNull(solrSearchProfile);
	}

	@Test
	public void returnsCorrectProfileForCategory() throws Throwable
	{
		// given
		final CategorySolrSearchProfileModel mainCategoryProfile = modelService.create(CategorySolrSearchProfileModel.class);
		mainCategoryProfile.setCategoryCode(MAIN_CATEGORY);
		mainCategoryProfile.setCode(MAIN_CATEGORY_PROFILE);
		mainCategoryProfile.setIndexedType(solrIndexedType);
		modelService.saveAll();

		// when
		final AbstractSolrSearchProfileModel solrSearchProfile
				= boostService.getProfileForCategoryAndIndexedType(indexedType, mainCategory, facetSearchConfig);
		
		// then
		assertEquals(mainCategoryProfile, solrSearchProfile);
	}

	@Test
	public void returnsGlobalProfileIfNoCategoryProvided() throws Throwable
	{
		// given
		final GlobalSolrSearchProfileModel globalProfile = modelService.create(GlobalSolrSearchProfileModel.class);
		globalProfile.setCode(GLOBAL_PROFILE);
		globalProfile.setIndexedType(solrIndexedType);
		modelService.saveAll();

		// when
		final AbstractSolrSearchProfileModel solrSearchProfile
				= boostService.getProfileForCategoryAndIndexedType(indexedType, null, facetSearchConfig);

		// then
		assertEquals(globalProfile, solrSearchProfile);
	}

	private void createCategories()
	{
		mainCategory = modelService.create(CategoryModel.class);
		mainCategory.setCode(MAIN_CATEGORY);
		mainCategory.setCatalogVersion(catalogVersion);

		modelService.saveAll();
	}

	private IndexedType getIndexedType(final FacetSearchConfig config)
	{
		final IndexConfig indexConfig = config.getIndexConfig();

		// Strategy for working out which of the available indexed types to use
		final Collection<IndexedType> indexedTypes = indexConfig.getIndexedTypes().values();
		if (CollectionUtils.isNotEmpty(indexedTypes))
		{
			// When there are multiple - select the first
			return indexedTypes.iterator().next();
		}

		// No indexed types
		return null;
	}

	private SolrIndexedTypeModel getSolrIndexedType()
	{
		final StringBuilder query = new StringBuilder("SELECT {sitm." + SolrIndexedTypeModel.PK + "} ");
		query.append("FROM {" + SolrIndexedTypeModel._TYPECODE + " AS sitm} ");
		query.append("WHERE {sitm." + SolrIndexedTypeModel.IDENTIFIER + "} = ?" + SolrIndexedTypeModel.IDENTIFIER);

		final Map<String, Object> params = (Map) Collections.singletonMap(SolrIndexedTypeModel.IDENTIFIER, SOLR_INDEXED_TYPE);

		final SearchResult<SolrIndexedTypeModel> searchRes = flexibleSearchService.search(query.toString(), params);
		return searchRes.getResult().get(0);
	}

}
