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
import de.hybris.platform.cms2.model.CMSComponentTypeModel;
import de.hybris.platform.cms2.model.ComponentTypeGroupModel;
import de.hybris.platform.cms2.model.contents.ContentSlotNameModel;
import de.hybris.platform.cockpit.model.meta.BaseType;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.model.meta.ObjectType;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ObjectTemplates4ContentSlotFilterTest
{
	private ObjectTemplates4ContentSlotFilter filter;
	@Mock
	private TypeService typeService;

	private static final String VALID_COMPONENT_TYPE = "validTypeComponent";


	@Before
	public void setUp()
	{
		filter = new ObjectTemplates4ContentSlotFilter();
		MockitoAnnotations.initMocks(this);
		filter.setTypeService(typeService);
		when(typeService.getTypeForCode(null)).thenThrow(new IllegalArgumentException());
	}


	@Test(expected = IllegalArgumentException.class)
	public void testCheckValidNullType()
	{
		final ContentSlotNameModel contentSlot = mock(ContentSlotNameModel.class);
		when(contentSlot.getValidComponentTypes()).thenReturn(null);
		when(contentSlot.getCompTypeGroup()).thenReturn(null);
		Assert.assertTrue(filter.isValidType(null, contentSlot));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckValidTypeNullContext()
	{
		final ObjectTemplate type = mock(ObjectTemplate.class);
		when(type.getCode()).thenReturn(VALID_COMPONENT_TYPE);
		Assert.assertTrue(filter.isValidType(type, null));
	}


	@Test
	public void checkAllNonValid()
	{
		final BaseType objType = mock(BaseType.class);
		final ObjectTemplate template = mock(ObjectTemplate.class);
		when(template.getBaseType()).thenReturn(objType);
		final CMSComponentTypeModel type = mockTypeModel("anyType", objType);
		final Set<CMSComponentTypeModel> validCmsComponentTypes = new HashSet<CMSComponentTypeModel>();
		validCmsComponentTypes.add(type);
		final ContentSlotNameModel contentSlot = mock(ContentSlotNameModel.class);
		when(contentSlot.getValidComponentTypes()).thenReturn(null);
		when(contentSlot.getCompTypeGroup()).thenReturn(null);
		Assert.assertFalse(filter.isValidType(template, contentSlot));
		final Collection<ObjectTemplate> filteredObjectTypes = filter.filterObjectTypes(Arrays.asList(template), contentSlot);
		org.fest.assertions.Assertions.assertThat(filteredObjectTypes).isEmpty();
	}

	@Test
	public void testToConfigContentSlotForValidType()
	{
		final BaseType objType = mock(BaseType.class);
		final ObjectTemplate template = mock(ObjectTemplate.class);
		when(template.getBaseType()).thenReturn(objType);
		final CMSComponentTypeModel type = mockTypeModel("anyType", objType);
		final Set<CMSComponentTypeModel> validCmsComponentTypes = new HashSet<CMSComponentTypeModel>();
		validCmsComponentTypes.add(type);
		final ContentSlotNameModel contentSlot = mock(ContentSlotNameModel.class);
		when(contentSlot.getValidComponentTypes()).thenReturn(validCmsComponentTypes);
		when(contentSlot.getCompTypeGroup()).thenReturn(null);
		Assert.assertTrue(filter.isValidType(template, contentSlot));
		final Collection<ObjectTemplate> filteredObjectTypes = filter.filterObjectTypes(Arrays.asList(template), contentSlot);
		org.fest.assertions.Assertions.assertThat(filteredObjectTypes).containsOnly(template);
	}


	@Test
	public void testToConfigContentSlotForCompGroupType()
	{
		final BaseType objType = mock(BaseType.class);
		final ObjectTemplate template = mock(ObjectTemplate.class);
		when(template.getBaseType()).thenReturn(objType);
		final CMSComponentTypeModel type = mockTypeModel("anyType", objType);
		final Set<CMSComponentTypeModel> cmsComponentTypeForGroup = new HashSet<CMSComponentTypeModel>();
		cmsComponentTypeForGroup.add(type);
		final ComponentTypeGroupModel compTypeGrp = mock(ComponentTypeGroupModel.class);
		final ContentSlotNameModel contentSlot = mock(ContentSlotNameModel.class);
		when(contentSlot.getValidComponentTypes()).thenReturn(null);
		when(contentSlot.getCompTypeGroup()).thenReturn(compTypeGrp);
		when(compTypeGrp.getCmsComponentTypes()).thenReturn(cmsComponentTypeForGroup);
		Assert.assertTrue(filter.isValidType(template, contentSlot));
		final Collection<ObjectTemplate> filteredObjectTypes = filter.filterObjectTypes(Arrays.asList(template), contentSlot);
		org.fest.assertions.Assertions.assertThat(filteredObjectTypes).containsOnly(template);
	}

	@Test
	public void testToConfigContentSlotForCompGroupTypeAndValidType()
	{
		final BaseType objType = mock(BaseType.class);
		final ObjectTemplate template1 = mock(ObjectTemplate.class);
		when(template1.getBaseType()).thenReturn(objType);
		final CMSComponentTypeModel type1 = mockTypeModel("validComponentType", objType);
		final Collection<CMSComponentTypeModel> cmsComponentTypeForGroup = new ArrayList<CMSComponentTypeModel>();
		cmsComponentTypeForGroup.add(type1);
		final BaseType objType2 = mock(BaseType.class);
		final ObjectTemplate template2 = mock(ObjectTemplate.class);
		when(template2.getBaseType()).thenReturn(objType2);

		final CMSComponentTypeModel type2 = mockTypeModel("validComponentGroupType", objType2);
		final Set<CMSComponentTypeModel> validCmsComponentTypes = new HashSet<CMSComponentTypeModel>();
		validCmsComponentTypes.add(type2);
		final ComponentTypeGroupModel compTypeGrp = mock(ComponentTypeGroupModel.class);
		final ContentSlotNameModel contentSlot = mock(ContentSlotNameModel.class);
		when(contentSlot.getValidComponentTypes()).thenReturn(validCmsComponentTypes);
		when(contentSlot.getCompTypeGroup()).thenReturn(compTypeGrp);
		when(compTypeGrp.getCmsComponentTypes()).thenReturn(Collections.<CMSComponentTypeModel> emptySet());
		Assert.assertFalse(filter.isValidType(template1, contentSlot));
		Assert.assertTrue(filter.isValidType(template2, contentSlot));
		final Collection<ObjectTemplate> filteredObjectTypes = filter.filterObjectTypes(Arrays.asList(template1, template2),
				contentSlot);
		org.fest.assertions.Assertions.assertThat(filteredObjectTypes).containsOnly(template2);
	}

	@Test
	public void testFilterForInvalidTypesAndValidCompTypeGroup()
	{
		final BaseType objType1 = mock(BaseType.class);
		final ObjectTemplate template1 = mock(ObjectTemplate.class);
		when(template1.getBaseType()).thenReturn(objType1);
		final CMSComponentTypeModel type1 = mockTypeModel("validComponentType", objType1);
		final Set<CMSComponentTypeModel> cmsComponentTypeForGroup = new HashSet<CMSComponentTypeModel>();
		cmsComponentTypeForGroup.add(type1);
		final BaseType objType2 = mock(BaseType.class);
		final ObjectTemplate template2 = mock(ObjectTemplate.class);
		when(template2.getBaseType()).thenReturn(objType2);
		final CMSComponentTypeModel type2 = mockTypeModel("validComponentGroupType", objType2);
		final Set<CMSComponentTypeModel> validCmsComponentTypes = new HashSet<CMSComponentTypeModel>();
		validCmsComponentTypes.add(type2);
		final ComponentTypeGroupModel compTypeGrp = mock(ComponentTypeGroupModel.class);
		final ContentSlotNameModel contentSlot = mock(ContentSlotNameModel.class);
		when(contentSlot.getValidComponentTypes()).thenReturn(validCmsComponentTypes);
		when(contentSlot.getCompTypeGroup()).thenReturn(compTypeGrp);
		when(compTypeGrp.getCmsComponentTypes()).thenReturn(cmsComponentTypeForGroup);

		Assert.assertTrue(filter.isValidType(template1, contentSlot));
		Assert.assertTrue(filter.isValidType(template2, contentSlot));
		final Collection<ObjectTemplate> filteredObjectTypes = filter.filterObjectTypes(Arrays.asList(template1, template2),
				contentSlot);
		org.fest.assertions.Assertions.assertThat(filteredObjectTypes).containsOnly(template1, template2);
	}

	@Test
	public void testFilterForisEmptyValidTypesAndValidCompTypeGroup()
	{
		final BaseType objType1 = mock(BaseType.class);
		final ObjectTemplate template1 = mock(ObjectTemplate.class);
		when(template1.getBaseType()).thenReturn(objType1);
		final CMSComponentTypeModel type1 = mockTypeModel("validComponentType", objType1);
		final Collection<CMSComponentTypeModel> cmsComponentTypeForGroup = new ArrayList<CMSComponentTypeModel>();
		cmsComponentTypeForGroup.add(type1);
		final BaseType objType2 = mock(BaseType.class);
		final ObjectTemplate template2 = mock(ObjectTemplate.class);
		when(template2.getBaseType()).thenReturn(objType2);
		final CMSComponentTypeModel type2 = mockTypeModel("validComponentGroupType", objType2);
		final Set<CMSComponentTypeModel> validCmsComponentTypes = new HashSet<CMSComponentTypeModel>();
		validCmsComponentTypes.add(type2);
		final ComponentTypeGroupModel compTypeGrp = mock(ComponentTypeGroupModel.class);
		final ContentSlotNameModel contentSlot = mock(ContentSlotNameModel.class);
		when(contentSlot.getValidComponentTypes()).thenReturn(Collections.EMPTY_SET);
		when(contentSlot.getCompTypeGroup()).thenReturn(compTypeGrp);
		when(compTypeGrp.getCmsComponentTypes()).thenReturn(Collections.<CMSComponentTypeModel> emptySet());
		Assert.assertFalse(filter.isValidType(template1, contentSlot));
		Assert.assertFalse(filter.isValidType(template2, contentSlot));
		final Collection<ObjectTemplate> filteredObjectTypes = filter.filterObjectTypes(Arrays.asList(template1, template2),
				contentSlot);
		org.fest.assertions.Assertions.assertThat(filteredObjectTypes).isEmpty();
	}

	/**
	 * 
	 */
	private CMSComponentTypeModel mockTypeModel(final String code, final ObjectType type)
	{
		when(type.getCode()).thenReturn(code);
		final CMSComponentTypeModel typeModel = mock(CMSComponentTypeModel.class);
		when(typeModel.getCode()).thenReturn(code);
		when(typeService.getTypeForCode(code)).thenReturn(typeModel);
		return typeModel;
	}
}
