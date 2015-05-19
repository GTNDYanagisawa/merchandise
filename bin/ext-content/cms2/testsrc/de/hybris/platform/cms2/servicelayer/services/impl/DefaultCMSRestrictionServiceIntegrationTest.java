/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.cms2.servicelayer.services.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSRestrictionServiceIntegrationTest extends ServicelayerTest
{

	@Resource
	private ModelService modelService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private CMSRestrictionService cmsRestrictionService;
	private CMSCategoryRestrictionModel restriction;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		final Collection<CategoryModel> categories = new ArrayList<CategoryModel>();
		categories.add(getCategoryByCode("testCategory0"));
		categories.add(getCategoryByCode("testCategory1"));
		categories.add(getCategoryByCode("testCategory2"));

		restriction = modelService.create(CMSCategoryRestrictionModel.class);
		restriction.setCategories(categories);
		restriction.setName("FooBar");
		restriction.setUid("FooBar");
		modelService.save(restriction);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSRestrictionService#getCategoryCodesForRestriction(de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel)}
	 * .
	 */
	@Test
	public void testGetCategoryCodesForRestriction()
	{
		final Collection<String> categoryCodes = cmsRestrictionService.getCategoryCodesForRestriction(restriction);
		assertThat(categoryCodes).hasSize(3);
		assertThat(categoryCodes).contains("testCategory0", "testCategory1", "testCategory2");
	}

	private CategoryModel getCategoryByCode(final String code)
	{
		final CategoryModel example = new CategoryModel();
		example.setCode(code);

		return flexibleSearchService.getModelByExample(example);
	}

}
