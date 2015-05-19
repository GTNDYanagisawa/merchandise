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
 */
package de.hybris.platform.commercewebservicescommons.mapping.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercewebservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.commercewebservicescommons.mapping.config.FieldSetLevelMapping;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;


@UnitTest
public class DefaultFieldSetLevelHelperTest
{
	private final String EXTENDED_LEVEL = "EXTENDED";
	private final String ADDRESS_BASIC_LEVEL = "firstName,lastName,town";
	private final String ADDRESS_EXTENDED_LEVEL = "firstName,lastName,line1,line2,town,country";
	private final String COUNTRY_BASIC_LEVEL = "name";
	private final String COUNTRY_EXTENDED_LEVEL = "name,isocode";
	private final String TEST_BASIC_LEVEL = "value1,value2";
	private final String TEST_DEFAULT_LEVEL = "value1,value2";
	private final String TEST_FULL_LEVEL = "value1,value2,parentValue1,parentValue2,parentValue3";

	@Mock
	private ApplicationContext ctx;
	private DefaultFieldSetLevelHelper fieldSetLevelHelper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final Map<String, String> addressLevelMap = new HashMap<String, String>();
		addressLevelMap.put(FieldSetLevelHelper.BASIC_LEVEL, ADDRESS_BASIC_LEVEL);
		addressLevelMap.put(EXTENDED_LEVEL, ADDRESS_EXTENDED_LEVEL);
		final FieldSetLevelMapping addressMapping = new FieldSetLevelMapping();
		addressMapping.setDtoClass(AddressData.class);
		addressMapping.setLevelMapping(addressLevelMap);

		final Map<String, String> countryLevelMap = new HashMap<String, String>();
		countryLevelMap.put(FieldSetLevelHelper.BASIC_LEVEL, COUNTRY_BASIC_LEVEL);
		countryLevelMap.put(EXTENDED_LEVEL, COUNTRY_EXTENDED_LEVEL);
		final FieldSetLevelMapping countryMapping = new FieldSetLevelMapping();
		countryMapping.setDtoClass(CountryData.class);
		countryMapping.setLevelMapping(countryLevelMap);

		final Map<String, FieldSetLevelMapping> mapping = new HashMap<String, FieldSetLevelMapping>();
		mapping.put("addressMapping", addressMapping);
		mapping.put("countryMapping", countryMapping);


		Mockito.when(ctx.getBeansOfType(FieldSetLevelMapping.class)).thenReturn(mapping);

		fieldSetLevelHelper = new DefaultFieldSetLevelHelper();
		fieldSetLevelHelper.setApplicationContext(ctx);
	}

	@Test
	public void testLevelMap()
	{
		Assert.assertNotNull(fieldSetLevelHelper.getLevelMap());
		Assert.assertTrue(fieldSetLevelHelper.getLevelMap().containsKey(AddressData.class));
		Assert.assertTrue(fieldSetLevelHelper.getLevelMap().containsKey(CountryData.class));
		Assert.assertEquals(2, fieldSetLevelHelper.getLevelMap().size());

		final Map<String, String> addressLevelMap = fieldSetLevelHelper.getLevelMap().get(AddressData.class);
		Assert.assertNotNull(addressLevelMap);
		Assert.assertEquals(ADDRESS_BASIC_LEVEL, addressLevelMap.get(FieldSetLevelHelper.BASIC_LEVEL));
		Assert.assertEquals(ADDRESS_EXTENDED_LEVEL, addressLevelMap.get(EXTENDED_LEVEL));
		Assert.assertEquals(2, addressLevelMap.size());

		final Map<String, String> countryLevelMap = fieldSetLevelHelper.getLevelMap().get(CountryData.class);
		Assert.assertNotNull(addressLevelMap);
		Assert.assertEquals(COUNTRY_BASIC_LEVEL, countryLevelMap.get(FieldSetLevelHelper.BASIC_LEVEL));
		Assert.assertEquals(COUNTRY_EXTENDED_LEVEL, countryLevelMap.get(EXTENDED_LEVEL));
		Assert.assertEquals(2, countryLevelMap.size());
	}

	@Test
	public void testIsLevelName()
	{
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.BASIC_LEVEL, AddressData.class));
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.DEFAULT_LEVEL, AddressData.class));
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(EXTENDED_LEVEL, AddressData.class));
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.FULL_LEVEL, AddressData.class));

		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.BASIC_LEVEL, TestDTO.class));
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.DEFAULT_LEVEL, TestDTO.class));
		Assert.assertFalse(fieldSetLevelHelper.isLevelName(EXTENDED_LEVEL, TestDTO.class));
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.FULL_LEVEL, TestDTO.class));
	}

	@Test
	public void testGetLevelDefinitionForClass()
	{
		String levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(AddressData.class, FieldSetLevelHelper.BASIC_LEVEL);
		Assert.assertEquals(ADDRESS_BASIC_LEVEL, levelDefinition);
		levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(AddressData.class, EXTENDED_LEVEL);
		Assert.assertEquals(ADDRESS_EXTENDED_LEVEL, levelDefinition);
	}

	@Test
	public void testCreateBasicLevel()
	{
		String levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.BASIC_LEVEL);
		Assert.assertNull(levelDefinition);

		levelDefinition = fieldSetLevelHelper.createBasicLevelDefinition(TestDTO.class);
		Assert.assertEquals(TEST_BASIC_LEVEL, levelDefinition);

		levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.BASIC_LEVEL);
		Assert.assertEquals(TEST_BASIC_LEVEL, levelDefinition);
	}

	@Test
	public void testCreateDefaultLevel()
	{
		String levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
		Assert.assertNull(levelDefinition);

		levelDefinition = fieldSetLevelHelper.createDefaultLevelDefinition(TestDTO.class);
		Assert.assertEquals(TEST_DEFAULT_LEVEL, levelDefinition);

		levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
		Assert.assertEquals(TEST_DEFAULT_LEVEL, levelDefinition);
	}

	@Test
	public void testCreateFullLevel()
	{
		String levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.FULL_LEVEL);
		Assert.assertNull(levelDefinition);

		levelDefinition = fieldSetLevelHelper.createFullLevelDefinition(TestDTO.class);
		Assert.assertEquals(TEST_FULL_LEVEL, levelDefinition);

		levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.FULL_LEVEL);
		Assert.assertEquals(TEST_FULL_LEVEL, levelDefinition);
	}

	@SuppressWarnings("unused")
	private static class TestParentDTO
	{
		String parentValue1;
		int parentValue2;
		Map<String, String> parentValue3;
	}

	@SuppressWarnings("unused")
	private static class TestDTO extends TestParentDTO
	{
		static String STATIC_FIELD = "static";
		final Boolean wrapperType = Boolean.FALSE;
		String value1;
		String value2;
	}
}
