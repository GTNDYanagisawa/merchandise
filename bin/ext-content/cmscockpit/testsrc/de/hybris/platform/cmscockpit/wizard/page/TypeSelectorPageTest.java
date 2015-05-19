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
package de.hybris.platform.cmscockpit.wizard.page;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.ContentSlotNameModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cmscockpit.session.impl.CmsPageBrowserModel;
import de.hybris.platform.cmscockpit.wizard.CmsWizard;
import de.hybris.platform.cockpit.model.meta.BaseType;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.media.impl.AbstractObjectTypeFilter;
import de.hybris.platform.cockpit.services.meta.ObjectTypeFilter;
import de.hybris.platform.testframework.TestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class TypeSelectorPageTest
{
	private static final String CONTENT_SLOT_POSITION = "1";
	private TypeSelectorPage typeSelectorPage;
	private List<ObjectTemplate> types;

	@Mock
	private ObjectTemplate objectTemplate1;
	@Mock
	private ObjectTemplate objectTemplate2;
	@Mock
	private ObjectTemplate objectTemplate3;
	@Mock
	private BaseType baseType1;
	@Mock
	private BaseType baseType2;
	@Mock
	private BaseType baseType3;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		final CmsWizard cmsWizard = mock(CmsWizard.class);
		final CmsPageBrowserModel cmsPageBrowserModel = mock(CmsPageBrowserModel.class);
		final TypedObject pageObject = mock(TypedObject.class);
		final AbstractPageModel pageModel = mock(AbstractPageModel.class);
		final PageTemplateModel masterTemplate = mock(PageTemplateModel.class);
		final ContentSlotNameModel contentSlotName = mock(ContentSlotNameModel.class);

		when(contentSlotName.getName()).thenReturn(CONTENT_SLOT_POSITION);
		when(masterTemplate.getAvailableContentSlots()).thenReturn(Collections.singletonList(contentSlotName));
		when(pageModel.getMasterTemplate()).thenReturn(masterTemplate);
		when(pageObject.getObject()).thenReturn(pageModel);
		when(cmsPageBrowserModel.getCurrentPageObject()).thenReturn(pageObject);
		when(cmsWizard.getBrowserModel()).thenReturn(cmsPageBrowserModel);

		typeSelectorPage = new TypeSelectorPage("", cmsWizard);
		typeSelectorPage.setPosition(CONTENT_SLOT_POSITION);
		types = new ArrayList<ObjectTemplate>();
		types.add(objectTemplate1);
		types.add(objectTemplate2);
		types.add(objectTemplate3);
		BDDMockito.given(objectTemplate1.getBaseType()).willReturn(baseType1);
		BDDMockito.given(objectTemplate2.getBaseType()).willReturn(baseType2);
		BDDMockito.given(objectTemplate3.getBaseType()).willReturn(baseType3);

		BDDMockito.given(baseType1.getCode()).willReturn("type1");
		BDDMockito.given(baseType2.getCode()).willReturn("type2");
		BDDMockito.given(baseType3.getCode()).willReturn("type3");
	}

	@Test
	public void testFilterTypesNullFilters()
	{
		final List<ObjectTemplate> filteredTypes = typeSelectorPage.filterTypes(types);
		Assertions.assertThat(filteredTypes).containsExactly(objectTemplate1, objectTemplate2, objectTemplate3);
	}

	@Test
	public void testFilterTypesEmptyContentSlotFilter()
	{
		final List<ObjectTemplate> filteredTypes = typeSelectorPage.filterTypes(types);
		Assertions.assertThat(filteredTypes).containsExactly(objectTemplate1, objectTemplate2, objectTemplate3);
	}

	@Test
	public void testFilterTypesCorruptedFilter()
	{
		TestUtils.disableFileAnalyzer("Testing exception case");
		final ObjectTypeFilter corruptedFilter = new AbstractObjectTypeFilter<ObjectTemplate, ContentSlotNameModel>()
		{

			@Override
			public boolean isValidType(final ObjectTemplate type, final ContentSlotNameModel target)
			{
				throw new NullPointerException();
			}
		};

		typeSelectorPage.setContentSlotObjectTypesFilter(corruptedFilter);
		final List<ObjectTemplate> filteredTypes = typeSelectorPage.filterTypes(types);
		Assertions.assertThat(filteredTypes).containsExactly(objectTemplate1, objectTemplate2, objectTemplate3);
		TestUtils.enableFileAnalyzer();
	}

	@Test
	public void testFilterTypesWorkingFilter1()
	{
		final ObjectTypeFilter type2Filter = new AbstractObjectTypeFilter<ObjectTemplate, ContentSlotNameModel>()
		{

			@Override
			public boolean isValidType(final ObjectTemplate type, final ContentSlotNameModel target)
			{
				return type.getBaseType().getCode().equals("type2");
			}

		};
		typeSelectorPage.setContentSlotObjectTypesFilter(type2Filter);
		final List<ObjectTemplate> filteredTypes = typeSelectorPage.filterTypes(types);
		Assertions.assertThat(filteredTypes).containsOnly(objectTemplate2);
	}

	@Test
	public void testFilterTypesWorkingFilter2()
	{
		final ObjectTypeFilter type2Filter = new AbstractObjectTypeFilter<ObjectTemplate, ContentSlotNameModel>()
		{

			@Override
			public boolean isValidType(final ObjectTemplate type, final ContentSlotNameModel target)
			{
				return !type.getBaseType().getCode().equals("type2");
			}

		};
		typeSelectorPage.setContentSlotObjectTypesFilter(type2Filter);
		final List<ObjectTemplate> filteredTypes = typeSelectorPage.filterTypes(types);
		Assertions.assertThat(filteredTypes).containsExactly(objectTemplate1, objectTemplate3);
	}

	@Test
	public void testFilterAllTypes()
	{
		final ObjectTypeFilter filterAllFilter = new AbstractObjectTypeFilter<ObjectTemplate, ContentSlotNameModel>()
		{

			@Override
			public boolean isValidType(final ObjectTemplate type, final ContentSlotNameModel target)
			{
				return false;
			}

		};
		typeSelectorPage.setContentSlotObjectTypesFilter(filterAllFilter);
		final List<ObjectTemplate> filteredTypes = typeSelectorPage.filterTypes(types);
		Assertions.assertThat(filteredTypes).isEmpty();
	}

}
