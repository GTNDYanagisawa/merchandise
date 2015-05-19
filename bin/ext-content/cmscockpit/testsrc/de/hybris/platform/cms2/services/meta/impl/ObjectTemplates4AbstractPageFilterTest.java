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
package de.hybris.platform.cms2.services.meta.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.RestrictionTypeModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.impl.DefaultCMSAdminRestrictionService;
import de.hybris.platform.cockpit.model.meta.BaseType;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



@UnitTest
public class ObjectTemplates4AbstractPageFilterTest
{

	private ObjectTemplates4AbstractPageFilter filter;

	@Mock
	private TypeService typeService;

	private static final String VALID_RESTRICTION_TYPE = "CMSProductRestriction";
	private static final String INVALID_RESTRICTION_TYPE = "InvalidRestriction";
	private static final String VALID_COMPOSED_TYPE = "ProductType";


	@Before
	public void setup()
	{
		filter = new ObjectTemplates4AbstractPageFilter();
		MockitoAnnotations.initMocks(this);
		filter.setTypeService(typeService);
		when(typeService.getComposedTypeForCode(null)).thenThrow(new IllegalArgumentException());

	}


	@Test(expected = IllegalArgumentException.class)
	public void testCheckValidNullType()
	{
		final ProductPageModel productPage = mock(ProductPageModel.class);

		final RestrictionTypeModel validRestrictionTypeModel = mock(RestrictionTypeModel.class);
		when(typeService.getComposedTypeForCode(VALID_RESTRICTION_TYPE)).thenReturn(validRestrictionTypeModel);
		when(validRestrictionTypeModel.getCode()).thenReturn(VALID_RESTRICTION_TYPE);

		final CMSPageTypeModel productPageTypeModel = mock(CMSPageTypeModel.class);
		when(typeService.getComposedTypeForClass(productPage.getClass())).thenReturn(productPageTypeModel);
		when(productPageTypeModel.getRestrictionTypes()).thenReturn(Collections.singletonList(validRestrictionTypeModel));

		Assert.assertTrue(filter.isValidType(null, productPage));
	}


	@Test
	public void testValidRestrictionType()
	{
		final ObjectTemplate templateForValidRestriction = mock(ObjectTemplate.class);
		final ProductPageModel productPage = mock(ProductPageModel.class);

		final BaseType baseTypeForValidRestriction = mock(BaseType.class);
		when(templateForValidRestriction.getBaseType()).thenReturn(baseTypeForValidRestriction);
		when(baseTypeForValidRestriction.getCode()).thenReturn(VALID_RESTRICTION_TYPE);

		final RestrictionTypeModel validRestrictionTypeModel = mock(RestrictionTypeModel.class);
		when(typeService.getComposedTypeForCode(VALID_RESTRICTION_TYPE)).thenReturn(validRestrictionTypeModel);
		when(validRestrictionTypeModel.getCode()).thenReturn(VALID_RESTRICTION_TYPE);

		final DefaultCMSAdminRestrictionService restrictionService = mock(DefaultCMSAdminRestrictionService.class);
		when(restrictionService.getAllowedRestrictionTypesForPage(productPage)).thenReturn(
				Collections.singletonList(validRestrictionTypeModel));
		filter.setCmsAdminRestrictionService(restrictionService);

		Assert.assertTrue(filter.isValidType(templateForValidRestriction, productPage));
	}


	@Test
	public void testFilterForEmptyRestrictionType()
	{
		final ObjectTemplate templateForValidRestriction = mock(ObjectTemplate.class);
		final ProductPageModel productPage = mock(ProductPageModel.class);

		final BaseType baseTypeForValidRestriction = mock(BaseType.class);
		when(templateForValidRestriction.getBaseType()).thenReturn(baseTypeForValidRestriction);
		when(baseTypeForValidRestriction.getCode()).thenReturn(VALID_RESTRICTION_TYPE);

		final RestrictionTypeModel validRestrictionTypeModel = mock(RestrictionTypeModel.class);
		when(typeService.getComposedTypeForCode(VALID_RESTRICTION_TYPE)).thenReturn(validRestrictionTypeModel);
		when(validRestrictionTypeModel.getCode()).thenReturn(VALID_RESTRICTION_TYPE);

		final DefaultCMSAdminRestrictionService restrictionService = mock(DefaultCMSAdminRestrictionService.class);
		when(restrictionService.getAllowedRestrictionTypesForPage(productPage)).thenReturn(Collections.EMPTY_LIST);
		filter.setCmsAdminRestrictionService(restrictionService);

		Assert.assertFalse(filter.isValidType(templateForValidRestriction, productPage));

	}

	@Test
	public void testFilterForInvalidRestrictionType()
	{
		final ObjectTemplate templateForValidRestriction = mock(ObjectTemplate.class);
		final ProductPageModel productPage = mock(ProductPageModel.class);

		final BaseType baseTypeForValidRestriction = mock(BaseType.class);
		when(templateForValidRestriction.getBaseType()).thenReturn(baseTypeForValidRestriction);
		when(baseTypeForValidRestriction.getCode()).thenReturn(VALID_RESTRICTION_TYPE);

		final RestrictionTypeModel validRestrictionTypeModel = mock(RestrictionTypeModel.class);
		when(typeService.getComposedTypeForCode(VALID_RESTRICTION_TYPE)).thenReturn(validRestrictionTypeModel);
		when(validRestrictionTypeModel.getCode()).thenReturn(VALID_RESTRICTION_TYPE);

		final DefaultCMSAdminRestrictionService restrictionService = mock(DefaultCMSAdminRestrictionService.class);
		final RestrictionTypeModel invalidRestrictionTypeModel = mock(RestrictionTypeModel.class);
		when(invalidRestrictionTypeModel.getCode()).thenReturn(INVALID_RESTRICTION_TYPE);
		when(restrictionService.getAllowedRestrictionTypesForPage(productPage)).thenReturn(
				Collections.singletonList(invalidRestrictionTypeModel));
		filter.setCmsAdminRestrictionService(restrictionService);

		Assert.assertFalse(filter.isValidType(templateForValidRestriction, productPage));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testForInvalidTypes()
	{
		final ObjectTemplate templateForRestriction = mock(ObjectTemplate.class);
		final ProductPageModel productPage = mock(ProductPageModel.class);

		final BaseType baseTypeForInvalidType = mock(BaseType.class);
		when(templateForRestriction.getBaseType()).thenReturn(baseTypeForInvalidType);
		when(baseTypeForInvalidType.getCode()).thenReturn(VALID_COMPOSED_TYPE);

		final ComposedTypeModel productTypeModel = mock(ComposedTypeModel.class);
		when(typeService.getComposedTypeForCode(VALID_COMPOSED_TYPE)).thenReturn(productTypeModel);
		when(productTypeModel.getCode()).thenReturn(VALID_COMPOSED_TYPE);

		Assert.assertFalse(filter.isValidType(templateForRestriction, productPage));

	}
}
