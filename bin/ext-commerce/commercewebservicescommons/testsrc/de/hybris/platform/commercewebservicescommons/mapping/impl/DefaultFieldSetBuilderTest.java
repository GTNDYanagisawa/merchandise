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
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercewebservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultFieldSetBuilderTest
{
	DefaultFieldSetBuilder fieldSetBuilder;
	DefaultFieldSetLevelHelper fieldSetLevelHelper;

	@Before
	public void setUp()
	{
		final Map<String, String> addressLevelMap = new HashMap<String, String>();
		addressLevelMap.put(FieldSetLevelHelper.BASIC_LEVEL, "firstName,lastName,town");
		addressLevelMap.put("EXTENDED", "firstName,lastName,line1,line2,town,country");

		final Map<String, String> countryLevelMap = new HashMap<String, String>();
		countryLevelMap.put(FieldSetLevelHelper.BASIC_LEVEL, "name");
		countryLevelMap.put("EXTENDED", "name,isocode");

		final Map<String, String> regionLevelMap = new HashMap<String, String>();
		regionLevelMap.put(FieldSetLevelHelper.BASIC_LEVEL, "name");
		regionLevelMap.put("EXTENDED", "name,isocode");

		final Map<Class, Map<String, String>> levelMap = new HashMap<Class, Map<String, String>>();
		levelMap.put(AddressDataTest.class, addressLevelMap);
		levelMap.put(CountryDataTest.class, countryLevelMap);
		levelMap.put(RegionDataTest.class, regionLevelMap);

		fieldSetLevelHelper = new DefaultFieldSetLevelHelper();
		fieldSetLevelHelper.setLevelMap(levelMap);

		fieldSetBuilder = new DefaultFieldSetBuilder();
		fieldSetBuilder.setDefaultRecurrencyLevel(3);
		fieldSetBuilder.setFieldSetLevelHelper(fieldSetLevelHelper);
	}

	@Test
	public void testBasic()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(AddressDataTest.class, "address", "BASIC");
		//System.out.println(set);
		Assert.assertTrue(set.contains("address.firstName"));
		Assert.assertTrue(set.contains("address.lastName"));
		Assert.assertTrue(set.contains("address.town"));
		Assert.assertEquals(3, set.size());
	}

	@Test
	public void testExtended()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(AddressDataTest.class, "address", "EXTENDED");
		//System.out.println(set);
		Assert.assertTrue(set.contains("address.firstName"));
		Assert.assertTrue(set.contains("address.lastName"));
		Assert.assertTrue(set.contains("address.town"));
		Assert.assertTrue(set.contains("address.line1"));
		Assert.assertTrue(set.contains("address.line2"));
		Assert.assertTrue(set.contains("address.country"));
		Assert.assertTrue(set.contains("address.country.name"));
		Assert.assertEquals(7, set.size());
	}

	@Test
	public void testFull()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(AddressDataTest.class, "address", "FULL");
		//System.out.println(set);
		Assert.assertTrue(set.contains("address.firstName"));
		Assert.assertTrue(set.contains("address.lastName"));
		Assert.assertTrue(set.contains("address.town"));
		Assert.assertTrue(set.contains("address.line1"));
		Assert.assertTrue(set.contains("address.line2"));
		Assert.assertTrue(set.contains("address.country"));
		Assert.assertTrue(set.contains("address.country.name"));
		Assert.assertTrue(set.contains("address.billingAddress"));
		Assert.assertTrue(set.contains("address.phone"));
		Assert.assertTrue(set.contains("address.region"));
		Assert.assertTrue(set.contains("address.region.name"));
		Assert.assertTrue(set.contains("address.visibleInAddressBook"));
		Assert.assertTrue(set.contains("address.formattedAddress"));
		Assert.assertTrue(set.contains("address.title"));
		Assert.assertTrue(set.contains("address.companyName"));
		Assert.assertTrue(set.contains("address.email"));
		Assert.assertTrue(set.contains("address.defaultAddress"));
		Assert.assertTrue(set.contains("address.titleCode"));
		Assert.assertTrue(set.contains("address.id"));
		Assert.assertTrue(set.contains("address.postalCode"));
		Assert.assertTrue(set.contains("address.shippingAddress"));
		Assert.assertEquals(21, set.size());
	}

	@Test
	public void testFieldConfiguration()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(AddressDataTest.class, "address", "country(BASIC,name)");
		//System.out.println(set);
		Assert.assertTrue(set.contains("address.country"));
		Assert.assertTrue(set.contains("address.country.name"));
		Assert.assertEquals(2, set.size());
	}

	@Test
	public void testMixedConfiguration()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(AddressDataTest.class, "address", "BASIC,line1,country(BASIC,name)");
		//System.out.println(set);
		Assert.assertTrue(set.contains("address.firstName"));
		Assert.assertTrue(set.contains("address.lastName"));
		Assert.assertTrue(set.contains("address.town"));
		Assert.assertTrue(set.contains("address.line1"));
		Assert.assertTrue(set.contains("address.country"));
		Assert.assertTrue(set.contains("address.country.name"));
		Assert.assertEquals(6, set.size());
	}

	@Test
	public void testMixedConfigurationWithSpace()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(AddressDataTest.class, "address",
				" BASIC  , country ( BASIC , name ) , line1");
		//System.out.println(set);
		Assert.assertTrue(set.contains("address.firstName"));
		Assert.assertTrue(set.contains("address.lastName"));
		Assert.assertTrue(set.contains("address.town"));
		Assert.assertTrue(set.contains("address.line1"));
		Assert.assertTrue(set.contains("address.country"));
		Assert.assertTrue(set.contains("address.country.name"));
		Assert.assertEquals(6, set.size());
	}

	@Test
	public void testThreeLevelConfiguration()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test",
				"name,address(country(name,isocode),firstName)");
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.name"));
		Assert.assertTrue(set.contains("test.address"));
		Assert.assertTrue(set.contains("test.address.firstName"));
		Assert.assertTrue(set.contains("test.address.country"));
		Assert.assertTrue(set.contains("test.address.country.name"));
		Assert.assertTrue(set.contains("test.address.country.isocode"));
		Assert.assertEquals(6, set.size());
	}

	@Test
	public void testEmptyConf()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(AddressDataTest.class, "address", "town()");
		Assert.assertTrue(set.contains("address.town"));
		Assert.assertEquals(1, set.size());
	}

	@Test
	public void testMissingBracket()
	{
		String errorMessage = null;
		try
		{
			fieldSetBuilder.createFieldSet(AddressDataTest.class, "address", "BASIC,country(BASIC,isocode()");
		}
		catch (final ConversionException e)
		{
			errorMessage = e.getMessage();
		}

		Assert.assertEquals("Incorrect configuration : Missing ')'", errorMessage);
	}

	@Test
	public void testIncorrectFieldName()
	{
		String errorMessage = null;
		try
		{
			fieldSetBuilder.createFieldSet(AddressDataTest.class, "address", "country$");
		}
		catch (final ConversionException e)
		{
			errorMessage = e.getMessage();
		}

		Assert.assertEquals("Incorrect field:'country$'", errorMessage);
	}

	@Test
	public void testMissingFieldName()
	{
		String errorMessage = null;
		try
		{
			fieldSetBuilder.createFieldSet(AddressDataTest.class, "address", "country   , ,code");
		}
		catch (final ConversionException e)
		{
			errorMessage = e.getMessage();
		}

		Assert.assertEquals("Incorrect field: field name is empty string", errorMessage);
	}

	@Test
	public void testIncorrectConfiguration()
	{
		String errorMessage = null;
		try
		{
			fieldSetBuilder.createFieldSet(AddressDataTest.class, "address", "country , ");
		}
		catch (final ConversionException e)
		{
			errorMessage = e.getMessage();
		}

		Assert.assertEquals("Incorrect configuration", errorMessage);
	}

	@Test
	public void testSimpleClassWithConf()
	{
		String errorMessage = null;
		try
		{
			fieldSetBuilder.createFieldSet(AddressDataTest.class, "address", "town(BASIC)");
		}
		catch (final ConversionException e)
		{
			errorMessage = e.getMessage();
		}
		Assert.assertEquals("Incorrect configuration : field 'address.town' don't need configuration", errorMessage);
	}

	@Test
	public void testEnumField()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "enumType");
		Assert.assertTrue(set.contains("test.enumType"));
		Assert.assertEquals(1, set.size());
	}

	@Test
	public void testHybrisEnumField()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "hybrisEnumType");
		Assert.assertTrue(set.contains("test.hybrisEnumType"));
		Assert.assertEquals(1, set.size());
	}

	@Test
	public void testWrapperField()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "wrapperType");
		Assert.assertTrue(set.contains("test.wrapperType"));
		Assert.assertEquals(1, set.size());
	}

	@Test
	public void testNumberField()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "numberType");
		Assert.assertTrue(set.contains("test.numberType"));
		Assert.assertEquals(1, set.size());
	}

	@Test
	public void testDateField()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "dateType");
		Assert.assertTrue(set.contains("test.dateType"));
		Assert.assertEquals(1, set.size());
	}

	@Test
	public void testIfStaticFieldIsNotAdded()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(StaticDataTest.class, "test", "BASIC");
		Assert.assertFalse(set.contains("test.STATIC_FIELD"));
	}

	@Test
	public void testArrayField()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "arrayType");
		Assert.assertTrue(set.contains("test.arrayType"));
		Assert.assertTrue(set.contains("test.arrayType.name"));
		Assert.assertEquals(2, set.size());
	}

	@Test
	public void testArrayFieldWithConf()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "arrayType(isocode)");
		Assert.assertTrue(set.contains("test.arrayType"));
		Assert.assertTrue(set.contains("test.arrayType.isocode"));
		Assert.assertEquals(2, set.size());
	}

	@Test
	public void testListField()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "name,countryList(name)");
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.name"));
		Assert.assertTrue(set.contains("test.countryList"));
		Assert.assertTrue(set.contains("test.countryList.name"));
		Assert.assertEquals(3, set.size());
	}

	@Test
	public void testMapField()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "simpleMap");
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.simpleMap"));
		Assert.assertTrue(set.contains("test.simpleMap.key"));
		Assert.assertTrue(set.contains("test.simpleMap.value"));
		Assert.assertEquals(3, set.size());
	}

	@Test
	public void testMapFieldWithEmptyConf()
	{
		Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "simpleMap() , name");
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.name"));
		Assert.assertTrue(set.contains("test.simpleMap"));
		Assert.assertTrue(set.contains("test.simpleMap.key"));
		Assert.assertTrue(set.contains("test.simpleMap.value"));
		Assert.assertEquals(4, set.size());

		set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "simpleMap(()()) , name");
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.name"));
		Assert.assertTrue(set.contains("test.simpleMap"));
		Assert.assertTrue(set.contains("test.simpleMap.key"));
		Assert.assertTrue(set.contains("test.simpleMap.value"));
		Assert.assertEquals(4, set.size());
	}

	@Test
	public void testMapFieldConfForValue()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "countryMap(()(isocode))");
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.countryMap"));
		Assert.assertTrue(set.contains("test.countryMap.key"));
		Assert.assertTrue(set.contains("test.countryMap.value"));
		Assert.assertTrue(set.contains("test.countryMap.value.isocode"));
		Assert.assertEquals(4, set.size());
	}


	@Test
	public void testMapFieldConfForKey()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "countryMap((isocode))");
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.countryMap"));
		Assert.assertTrue(set.contains("test.countryMap.key"));
		Assert.assertTrue(set.contains("test.countryMap.key.isocode"));
		Assert.assertTrue(set.contains("test.countryMap.value"));
		Assert.assertTrue(set.contains("test.countryMap.value.name"));
		Assert.assertEquals(5, set.size());
	}

	@Test
	public void testMapFieldWithConf()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "countryMap((BASIC)(BASIC,isocode))");
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.countryMap"));
		Assert.assertTrue(set.contains("test.countryMap.key"));
		Assert.assertTrue(set.contains("test.countryMap.key.name"));
		Assert.assertTrue(set.contains("test.countryMap.value"));
		Assert.assertTrue(set.contains("test.countryMap.value.name"));
		Assert.assertTrue(set.contains("test.countryMap.value.isocode"));
		Assert.assertEquals(6, set.size());
	}

	@Test
	public void testNestedMap()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "nestedGenericCollections");
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.nestedGenericCollections"));
		Assert.assertTrue(set.contains("test.nestedGenericCollections.key"));
		Assert.assertTrue(set.contains("test.nestedGenericCollections.value"));
		Assert.assertTrue(set.contains("test.nestedGenericCollections.value.key"));
		Assert.assertTrue(set.contains("test.nestedGenericCollections.value.value"));
		Assert.assertTrue(set.contains("test.nestedGenericCollections.value.value.name"));
		Assert.assertEquals(6, set.size());
	}

	@Test
	public void testNestedMapWithConf()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test",
				"nestedGenericCollections( () ( () (name,isocode)) )");
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.nestedGenericCollections"));
		Assert.assertTrue(set.contains("test.nestedGenericCollections.key"));
		Assert.assertTrue(set.contains("test.nestedGenericCollections.value"));
		Assert.assertTrue(set.contains("test.nestedGenericCollections.value.key"));
		Assert.assertTrue(set.contains("test.nestedGenericCollections.value.value"));
		Assert.assertTrue(set.contains("test.nestedGenericCollections.value.value.name"));
		Assert.assertTrue(set.contains("test.nestedGenericCollections.value.value.isocode"));
		Assert.assertEquals(7, set.size());
	}

	@Test
	public void testMapWithGenerics()
	{
		final Map<String, Class> typeVariableMap = new HashMap();
		typeVariableMap.put("STATE", CountryDataTest.class);
		typeVariableMap.put("RESULT", RegionDataTest.class);
		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setTypeVariableMap(typeVariableMap);

		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test",
				"mapWithGenerics(()(state,result(name,countryIso)))", context);

		//System.out.println(set);
		Assert.assertTrue(set.contains("test.mapWithGenerics"));
		Assert.assertTrue(set.contains("test.mapWithGenerics.key"));
		Assert.assertTrue(set.contains("test.mapWithGenerics.value"));
		Assert.assertTrue(set.contains("test.mapWithGenerics.value.state"));
		Assert.assertTrue(set.contains("test.mapWithGenerics.value.state.name"));
		Assert.assertTrue(set.contains("test.mapWithGenerics.value.result"));
		Assert.assertTrue(set.contains("test.mapWithGenerics.value.result.name"));
		Assert.assertTrue(set.contains("test.mapWithGenerics.value.result.countryIso"));
		Assert.assertEquals(8, set.size());
	}

	@Test
	public void testIncorrectMapConf()
	{
		String errorMessage = null;
		try
		{
			fieldSetBuilder.createFieldSet(DataTest.class, "test", "countryMap(isocode)");
		}
		catch (final ConversionException e)
		{
			errorMessage = e.getMessage();
		}

		Assert.assertEquals("Incorrect map configuration : 'isocode'", errorMessage);
	}

	@Test
	public void testGenericType()
	{
		final Map<String, Class> typeVariableMap = new HashMap();
		typeVariableMap.put("STATE", CountryDataTest.class);
		typeVariableMap.put("RESULT", RegionDataTest.class);
		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setTypeVariableMap(typeVariableMap);

		final Set<String> set = fieldSetBuilder.createFieldSet(GenericsDataTest.class, "test",
				"state(isocode),result(name,isocode),parentState", context);
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.state"));
		Assert.assertTrue(set.contains("test.state.isocode"));
		Assert.assertTrue(set.contains("test.parentState"));
		Assert.assertTrue(set.contains("test.parentState.name"));
		Assert.assertTrue(set.contains("test.result"));
		Assert.assertTrue(set.contains("test.result.isocode"));
		Assert.assertTrue(set.contains("test.result.name"));
		Assert.assertEquals(7, set.size());
	}

	@Test
	public void testNestedGenericType()
	{
		final Map<String, Class> typeVariableMap = new HashMap();
		typeVariableMap.put("STATE", String.class);
		typeVariableMap.put("RESULT", RecurrencyData.class);
		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setTypeVariableMap(typeVariableMap);

		final Set<String> set = fieldSetBuilder.createFieldSet(GenericsDataTest.class, "test", "result(BASIC)", context);
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.result"));
		Assert.assertTrue(set.contains("test.result.name"));
		Assert.assertTrue(set.contains("test.result.parent"));
		Assert.assertTrue(set.contains("test.result.parent.name"));
		Assert.assertTrue(set.contains("test.result.parent.parent"));
		Assert.assertTrue(set.contains("test.result.parent.parent.name"));
		Assert.assertTrue(set.contains("test.result.parent.parent.parent"));
		Assert.assertTrue(set.contains("test.result.parent.parent.parent.name"));
		Assert.assertTrue(set.contains("test.result.parent.parent.parent.parent"));
		Assert.assertTrue(set.contains("test.result.parent.parent.parent.parent.name"));
		Assert.assertTrue(set.contains("test.result.parent.parent.parent.parent.parent"));
		Assert.assertEquals(11, set.size());
	}

	@Test
	public void testTypeWithRecurrency()
	{
		final Map<String, String> variantLevelMap = new HashMap<String, String>();
		variantLevelMap.put(FieldSetLevelHelper.BASIC_LEVEL, "parent");
		fieldSetLevelHelper.getLevelMap().put(RecurrencyData.class, variantLevelMap);

		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "fieldWithRecurrency1,fieldWithRecurrency2");
		//System.out.println(set);

		Assert.assertTrue(set.contains("test.fieldWithRecurrency1"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency1.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency1.parent.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency1.parent.parent.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency1.parent.parent.parent.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2.parent.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2.parent.parent.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2.parent.parent.parent.parent"));
		Assert.assertEquals(10, set.size());

		fieldSetLevelHelper.getLevelMap().remove(RecurrencyData.class);
	}

	@Test
	public void testRecurrencyLevel()
	{
		final Map<String, String> variantLevelMap = new HashMap<String, String>();
		variantLevelMap.put(FieldSetLevelHelper.BASIC_LEVEL, "parent");
		fieldSetLevelHelper.getLevelMap().put(RecurrencyData.class, variantLevelMap);

		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setRecurrencyLevel(1);
		Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "fieldWithRecurrency1,fieldWithRecurrency2",
				context);
		//System.out.println(set);

		Assert.assertTrue(set.contains("test.fieldWithRecurrency1"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency1.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency1.parent.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2.parent.parent"));
		Assert.assertEquals(6, set.size());

		context.setRecurrencyLevel(2);
		set = fieldSetBuilder.createFieldSet(DataTest.class, "test", "fieldWithRecurrency1,fieldWithRecurrency2", context);
		//System.out.println(set);

		Assert.assertTrue(set.contains("test.fieldWithRecurrency1"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency1.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency1.parent.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency1.parent.parent.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2.parent.parent"));
		Assert.assertTrue(set.contains("test.fieldWithRecurrency2.parent.parent.parent"));
		Assert.assertEquals(8, set.size());

		fieldSetLevelHelper.getLevelMap().remove(RecurrencyData.class);
	}

	@Test
	public void testWildcardType()
	{
		final Set<String> set = fieldSetBuilder.createFieldSet(DataTest.class, "test",
				"wildcardList1(name),wildcardList2(name,isocode)");
		//System.out.println(set);
		Assert.assertTrue(set.contains("test.wildcardList1"));
		Assert.assertTrue(set.contains("test.wildcardList1.name"));
		Assert.assertTrue(set.contains("test.wildcardList2"));
		Assert.assertTrue(set.contains("test.wildcardList2.name"));
		Assert.assertTrue(set.contains("test.wildcardList2.isocode"));
		Assert.assertEquals(5, set.size());
	}

	@Test
	public void testMaxFieldSetSizeExceeded()
	{
		String errorMessage = "";
		final int defaultMaxFieldSetSize = fieldSetBuilder.getDefaultMaxFieldSetSize();
		fieldSetBuilder.setDefaultMaxFieldSetSize(4);
		try
		{
			fieldSetBuilder.createFieldSet(DataTest.class, "test", "BASIC");
		}
		catch (final ConversionException e)
		{
			errorMessage = e.getMessage();
		}

		fieldSetBuilder.setDefaultMaxFieldSetSize(defaultMaxFieldSetSize);
		Assert.assertEquals(
				"Max field set size exceeded. Reason of that can be : too generic configuration, lack of properly defined BASIC field set level for data class, reccurency in data structure",
				errorMessage);
	}


	@SuppressWarnings("unused")
	private class DataTest
	{
		String name;
		int value;
		Boolean wrapperType;
		BigDecimal numberType;
		Date dateType;
		EnumType enumType;
		StockLevelStatus hybrisEnumType;
		AddressDataTest address;
		List<CountryDataTest> countryList;
		Map<String, String> simpleMap;
		Map<CountryDataTest, RegionDataTest> countryMap;
		CountryDataTest[][] arrayType;
		RecurrencyData fieldWithRecurrency1;
		RecurrencyData fieldWithRecurrency2;
		Map<String, GenericsDataTest<CountryDataTest, RegionDataTest>> mapWithGenerics;
		List<? extends List<CountryDataTest>> wildcardList1;
		List<? super CountryDataTest> wildcardList2;
		Map<String, Map<String, List<? extends CountryDataTest>>> nestedGenericCollections;
	}

	@SuppressWarnings("unused")
	private static class StaticDataTest
	{
		static String STATIC_FIELD = "static";
		String name;
		int value;
	}

	@SuppressWarnings("unused")
	private class ParentGenericsDataTest<STATE, RESULT>
	{
		STATE parentState;
		RESULT result;
	}

	@SuppressWarnings("unused")
	private class GenericsDataTest<STATE, RESULT> extends ParentGenericsDataTest<STATE, RESULT>
	{
		STATE state;
	}

	@SuppressWarnings("unused")
	private class AddressDataTest
	{
		private RegionDataTest region;
		private String lastName;
		private String titleCode;
		private String phone;
		private String companyName;
		private CountryDataTest country;
		private String id;
		private String title;
		private String postalCode;
		private String email;
		private boolean defaultAddress;
		private boolean visibleInAddressBook;
		private String formattedAddress;
		private boolean billingAddress;
		private String town;
		private String firstName;
		private String line1;
		private boolean shippingAddress;
		private String line2;
	}

	@SuppressWarnings("unused")
	private class RegionDataTest
	{
		private String name;
		private String isocode;
		private String countryIso;
		private String isocodeShort;
	}

	@SuppressWarnings("unused")
	private class CountryDataTest
	{
		private String name;
		private String isocode;
	}

	@SuppressWarnings("unused")
	private class RecurrencyData
	{
		String name;
		RecurrencyData parent;
	}

	private enum EnumType
	{
		ONE, TWO
	}
}
