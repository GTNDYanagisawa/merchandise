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
package de.hybris.platform.cms2.servicelayer.services.admin.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.RestrictionTypeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.Collections;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultCMSAdminRestrictionServiceTest
{

	private static final String CMS_Page_Type = "CategoryPage";
	@Mock
	private TypeService typeService;
	@Mock
	private CMSPageTypeModel cmsPageType;

	DefaultCMSAdminRestrictionService restrictionService;



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		restrictionService = new DefaultCMSAdminRestrictionService();
		given(cmsPageType.getCode()).willReturn(CMS_Page_Type);
		given(typeService.getComposedTypeForClass(CategoryPageModel.class)).willReturn(cmsPageType);
		restrictionService.setTypeService(typeService);

	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.admin.impl.DefaultCMSAdminRestrictionService#getAllowedRestrictionTypesForPage(de.hybris.platform.cms2.model.pages.AbstractPageModel)}
	 * .
	 */
	@Test
	public void shouldGetAllowedRestrictionForPage()
	{
		final CategoryPageModel categoryPage = new CategoryPageModel();
		final RestrictionTypeModel restrictionTypeModel = new RestrictionTypeModel();
		given(cmsPageType.getRestrictionTypes()).willReturn(Collections.singletonList(restrictionTypeModel));
		final Collection<RestrictionTypeModel> result = restrictionService.getAllowedRestrictionTypesForPage(categoryPage);

		Assertions.assertThat(result).containsOnly(restrictionTypeModel);
		Assertions.assertThat(result).isNotEmpty();
		Assertions.assertThat(result).isNotNull();
	}


	@Test(expected = IllegalArgumentException.class)
	public void testForNullPage()
	{
		restrictionService.getAllowedRestrictionTypesForPage((AbstractPageModel) null);
	}

}
