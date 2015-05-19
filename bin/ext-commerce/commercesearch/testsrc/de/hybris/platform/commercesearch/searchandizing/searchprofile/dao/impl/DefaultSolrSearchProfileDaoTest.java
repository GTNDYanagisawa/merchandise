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
package de.hybris.platform.commercesearch.searchandizing.searchprofile.dao.impl;

import static junit.framework.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercesearch.model.CategorySolrSearchProfileModel;
import de.hybris.platform.commercesearch.model.GlobalSolrSearchProfileModel;
import de.hybris.platform.commercesearch.searchandizing.searchprofile.dao.SearchProfileDao;
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
 * Tests the implementation of {@link DefaultSearchProfileDao}.
 */
@IntegrationTest
public class DefaultSolrSearchProfileDaoTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	@Resource
	private SearchProfileDao searchProfileDao;

	private SolrIndexedTypeModel indexedType01;
	private SolrIndexedTypeModel indexedType02;

	private final String DEFAULT_CATEGORY_CODE = "111";
	private final String INDEXED_TYPE_01 = "indexedType01";
	private final String INDEXED_TYPE_02 = "indexedType02";

	@Before
	public void setUp()
	{
		createSearchConfigs();
	}

	@Test
	public void testFindGlobalSolrSearchProfiles()
	{
		List<GlobalSolrSearchProfileModel> globalSolrSearchProfiles = searchProfileDao.findGlobalSolrSearchProfiles(indexedType01);
		assertEquals(0, globalSolrSearchProfiles.size());
		final GlobalSolrSearchProfileModel globalProfile = modelService.create(GlobalSolrSearchProfileModel.class);
		globalProfile.setIndexedType(indexedType01);
		globalProfile.setCode(DEFAULT_CATEGORY_CODE + indexedType01.getPk());
		modelService.save(globalProfile);

		globalSolrSearchProfiles = searchProfileDao.findGlobalSolrSearchProfiles(indexedType01);

		assertEquals(1, globalSolrSearchProfiles.size());
	}

	@Test
	public void testFindCategorySolrSearchProfiles()
	{
		List<CategorySolrSearchProfileModel> categorySolrSearchProfile = searchProfileDao.findCategorySolrSearchProfiles(
				indexedType01, DEFAULT_CATEGORY_CODE);
		assertEquals(0, categorySolrSearchProfile.size());
		final CategorySolrSearchProfileModel categoryProfile = modelService.create(CategorySolrSearchProfileModel.class);
		categoryProfile.setCategoryCode(DEFAULT_CATEGORY_CODE);
		categoryProfile.setIndexedType(indexedType01);
		categoryProfile.setCode(DEFAULT_CATEGORY_CODE + indexedType01.getPk());
		modelService.save(categoryProfile);

		categorySolrSearchProfile = searchProfileDao.findCategorySolrSearchProfiles(indexedType01, DEFAULT_CATEGORY_CODE);

		assertEquals(1, categorySolrSearchProfile.size());
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
