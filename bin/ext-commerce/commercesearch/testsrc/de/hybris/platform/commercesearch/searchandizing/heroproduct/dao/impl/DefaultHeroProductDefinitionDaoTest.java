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
package de.hybris.platform.commercesearch.searchandizing.heroproduct.dao.impl;

import static junit.framework.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercesearch.model.SolrHeroProductDefinitionModel;
import de.hybris.platform.commercesearch.searchandizing.heroproduct.dao.HeroProductDefinitionDao;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests the implementation of {@link de.hybris.platform.commercesearch.searchandizing.heroproduct.dao.HeroProductDefinitionDao}.
 */
@IntegrationTest
public class DefaultHeroProductDefinitionDaoTest extends ServicelayerTransactionalTest
{

	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	@Resource
	private HeroProductDefinitionDao heroProductDefinitionDao;

	private CategoryModel category01;
	private CategoryModel category02;

	private CatalogModel catalog01;
	private CatalogModel catalog02;

	private CatalogVersionModel catVer01;
	private CatalogVersionModel catVer02;

	private SolrIndexedTypeModel indexedType01;
	private SolrIndexedTypeModel indexedType02;

	private final String CAT_ID_01 = "cat01";
	private final String CAT_ID_02 = "cat02";
	private final String CAT_VERSION_01 = "catVersion01";
	private final String CAT_VERSION_02 = "catVersion02";
	private final String CATEGORY_01 = "category01";
	private final String CATEGORY_02 = "category02";
	private final String INDEXED_TYPE_01 = "indexedType01";
	private final String INDEXED_TYPE_02 = "indexedType02";

	@Before
	public void setUp()
	{
		createCatalogs();
		createCatalogVersions();
		createCategories();
		createSearchConfigs();
	}

	@Test
	public void testFindSolrHeroProductDefinitionsByCategory()
	{
		List<SolrHeroProductDefinitionModel> solrHeroProductDefinitions = heroProductDefinitionDao
				.findSolrHeroProductDefinitionsByCategory(category01);
		assertEquals(0, solrHeroProductDefinitions.size());
		final SolrHeroProductDefinitionModel heroProduct = modelService.create(SolrHeroProductDefinitionModel.class);
		heroProduct.setCategory(category01);
		heroProduct.setCatalogVersion(catVer01);
		heroProduct.setIndexedType(indexedType01);
		heroProduct.setCode(CATEGORY_01 + indexedType01.getPk());
		modelService.save(heroProduct);
		solrHeroProductDefinitions = heroProductDefinitionDao.findSolrHeroProductDefinitionsByCategory(category01);
		assertEquals(1, solrHeroProductDefinitions.size());
	}

	@Test
	public void testFindSolrHeroProductDefinitionsByCategoryAndSearchConfig()
	{
		List<SolrHeroProductDefinitionModel> solrHeroProductDefinitions = heroProductDefinitionDao
				.findSolrHeroProductDefinitionsByCategory(category01);
		assertEquals(0, solrHeroProductDefinitions.size());
		final SolrHeroProductDefinitionModel heroProduct = modelService.create(SolrHeroProductDefinitionModel.class);
		heroProduct.setCategory(category01);
		heroProduct.setCatalogVersion(catVer01);
		heroProduct.setIndexedType(indexedType01);
		heroProduct.setCode(CATEGORY_01 + indexedType01.getPk());
		modelService.save(heroProduct);
		solrHeroProductDefinitions = heroProductDefinitionDao.findSolrHeroProductDefinitionsByCategory(category01,
				indexedType01);
		assertEquals(1, solrHeroProductDefinitions.size());
	}

	private void createCatalogs()
	{
		catalog01 = modelService.create(CatalogModel.class);
		catalog01.setId(CAT_ID_01);
		catalog02 = modelService.create(CatalogModel.class);
		catalog02.setId(CAT_ID_02);
		modelService.saveAll();
	}

	private void createCatalogVersions()
	{
		catVer01 = modelService.create(CatalogVersionModel.class);
		catVer01.setCatalog(catalog01);
		catVer01.setVersion(CAT_VERSION_01);
		catVer02 = modelService.create(CatalogVersionModel.class);
		catVer02.setCatalog(catalog02);
		catVer02.setVersion(CAT_VERSION_02);
		modelService.saveAll();
	}

	private void createCategories()
	{
		category01 = modelService.create(CategoryModel.class);
		category01.setCode(CATEGORY_01);
		category01.setCatalogVersion(catVer01);
		category02 = modelService.create(CategoryModel.class);
		category02.setCode(CATEGORY_02);
		category02.setCatalogVersion(catVer02);
		modelService.saveAll();
	}

	private void createSearchConfigs()
	{
		indexedType01 = modelService.create(SolrIndexedTypeModel.class);
		indexedType01.setIdentifier(INDEXED_TYPE_01);
		indexedType01.setType(typeService.getComposedTypeForClass(ProductModel.class));
		indexedType02 = modelService.create(SolrIndexedTypeModel.class);
		indexedType02.setIdentifier(INDEXED_TYPE_02);
		indexedType02.setType(typeService.getComposedTypeForClass(ProductModel.class));
		modelService.saveAll();
	}

}
