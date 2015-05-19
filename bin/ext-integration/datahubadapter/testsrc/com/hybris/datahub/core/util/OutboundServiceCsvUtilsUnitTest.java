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
package com.hybris.datahub.core.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.datahub.core.data.TestProductData;


@SuppressWarnings("javadoc")
@UnitTest
public class OutboundServiceCsvUtilsUnitTest
{
	private final OutboundServiceCsvUtils csvUtils = new OutboundServiceCsvUtils();

	private final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S";

	private TestProductData testProduct;
	private Map<String, Object> testProductMap;

	@Before
	public void setup()
	{
		testProduct = OutboundServiceDataGenerationTestUtils.createTestProductData();
		testProductMap = OutboundServiceDataGenerationTestUtils.createUniqueTestProductMap();

		csvUtils.setDatePattern(DEFAULT_DATE_FORMAT);
	}

	@Test
	public void testGetStringValueOfObject_ShouldFormatDates()
	{
		final Date date = new Date();
		final String formattedDate = csvUtils.toCsvValue(date);

		Assert.assertEquals(DateFormatUtils.formatUTC(date, DEFAULT_DATE_FORMAT), formattedDate);
	}

	@Test
	public void testGetStringValueOfObject_StringValueOfUtcDateCanBeConvertedBackToOriginalDate() throws ParseException
	{
		final Date originalDate = new Date();
		final String formattedDate = csvUtils.toCsvValue(originalDate);

		final SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		final Date convertedDate = formatter.parse(formattedDate);

		Assert.assertTrue(originalDate.getTime() == convertedDate.getTime());
		Assert.assertEquals(0, originalDate.compareTo(convertedDate));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetStringValueOfObject_WithInvalidDateFormat()
	{
		csvUtils.setDatePattern("INVALID_DATE_FORMAT");
		final Date date = new Date();
		csvUtils.toCsvValue(date);
	}

	@Test
	public void testConvertMapToCsv_ShouldIgnoreCollectionEntry()
	{
		final Map<String, Object> objMap = new HashMap<>();
		objMap.put("testProperty", "testValue");
		objMap.put("collectionProperty", Arrays.asList("value 1", "value 2"));

		final String[] csvArray = csvUtils.convertMapToCsv(objMap);

		Assert.assertEquals(2, csvArray.length);
		Assert.assertEquals("testProperty", csvArray[0]);
		Assert.assertEquals("testValue", csvArray[1]);
	}

	@Test
	public void testConvertMapToCsv()
	{
		final String[] objArray = csvUtils.convertMapToCsv(testProductMap);

		Assert.assertEquals(2, objArray.length);
		Assert.assertTrue(objArray[0].length() > 0);
		Assert.assertEquals("unit,style,SKU,integrationKey,baseName,isoCode,size", objArray[0]);
		Assert.assertTrue(objArray[1].length() > 0);
	}

	@Test
	public void testConvertMapToCsv_ShouldHandleNullValues()
	{
		testProductMap.put("propertyWithNullValue", null);
		final String[] objArray = csvUtils.convertMapToCsv(testProductMap);

		Assert.assertEquals(2, objArray.length);
		Assert.assertTrue(StringUtils.contains(objArray[0], "propertyWithNullValue"));
		Assert.assertTrue(StringUtils.contains(objArray[1], ",,"));
	}

	@SuppressWarnings("serial")
	@Test
	public void testConvertListToCsv_WithMultipleMaps_ShouldProperlyHandleNullValues()
	{
		final Map<String, Object> mapWithoutNullValue = new HashMap<String, Object>()
		{
			{
				put("propertyWhichSometimesContainsNull", "non-null-value");
				put("anotherProperty", "anotherValue1");
			}
		};
		final Map<String, Object> mapWithNullValue = new HashMap<String, Object>()
		{
			{
				put("propertyWhichSometimesContainsNull", null);
				put("anotherProperty", "anotherValue2");
			}
		};
		final List<Map<String, Object>> objList = Arrays.asList(mapWithNullValue, mapWithoutNullValue);
		final String[] csvArray = csvUtils.convertListToCsv(objList);

		Assert.assertEquals(3, csvArray.length);
		Assert.assertEquals("propertyWhichSometimesContainsNull,anotherProperty", csvArray[0]);
		Assert.assertEquals(",anotherValue2", csvArray[1]);
		Assert.assertEquals("non-null-value,anotherValue1", csvArray[2]);
	}

	@Test
	public void testConvertObjectToMap()
	{
		final Map<String, Object> objMap = csvUtils.convertObjectToMap(testProduct);

		Assert.assertEquals(7, objMap.size());
		Assert.assertEquals(testProduct.getBaseName(), objMap.get("baseName"));
	}

	@Test
	public void testConvertObjectToMap_ShouldIgnoreCollectionProperty()
	{
		final ObjectWithCollection obj = new ObjectWithCollection();
		obj.setTestList(Arrays.asList("value 1", "value 2"));

		final Map<String, Object> objMap = csvUtils.convertObjectToMap(obj);

		Assert.assertEquals(0, objMap.size());
	}

	@Test
	public void testConvertListToCsv()
	{
		final List<Map<String, Object>> objList = Arrays.asList(testProductMap);
		final String[] csvArray = csvUtils.convertListToCsv(objList);

		Assert.assertEquals(2, csvArray.length);
	}

	@Test
	public void testConvertObjectToEscapedCsv()
	{
		testProduct.setBaseName("test\"test,test\"\nLine2");
		final String[] csvArray = csvUtils.convertObjectToCsv(testProduct);

		assertEquals(2, csvArray.length);
		assertThat(csvArray[1], containsString("\"test\"\"test,test\"\"\nLine2\""));
	}

	@Test
	public void testConvertMapToEscapedCsv()
	{
		testProductMap.put("baseName", "test\"test,test\"\nLine2");
		final String[] csvArray = csvUtils.convertMapToCsv(testProductMap);

		assertEquals(2, csvArray.length);
		assertThat(csvArray[1], containsString("\"test\"\"test,test\"\"\nLine2\""));
	}

	@Test
	public void testConvertListToCsv_WithMultipleMaps()
	{
		final List<Map<String, Object>> objList = Arrays.asList(testProductMap,
				OutboundServiceDataGenerationTestUtils.createUniqueTestProductMap());
		final String[] csvArray = csvUtils.convertListToCsv(objList);

		Assert.assertEquals(3, csvArray.length);
	}

	@Test
	public void testTransmissionSafeMapWithObjects()
	{
		final Map<String, Object> original = new HashMap<>(1);
		original.put("attr1", "a string");
		original.put("attr2", new Integer(1));

		final Map<String, Object> safe = csvUtils.transmissionSafe(original);
		Assert.assertNotSame(original, safe);
		Assert.assertEquals("a string", safe.get("attr1"));
		Assert.assertEquals("1", safe.get("attr2"));
	}

	@Test
	public void testTransmissionSafeMapWithDate()
	{
		final Date date = new Date();
		final Map<String, Object> original = new HashMap<>(1);
		original.put("date", date);

		final Map<String, Object> safe = csvUtils.transmissionSafe(original);
		assertEquals(DateFormatUtils.formatUTC(date, DEFAULT_DATE_FORMAT), safe.get("date"));
	}

	@Test
	public void testTransmissionSafeMapWithNull()
	{
		final Map<String, Object> original = new HashMap<>(1);
		original.put("attr", null);

		final Map<String, Object> safe = csvUtils.transmissionSafe(original);
		Assert.assertTrue(safe.isEmpty());
	}

	private class ObjectWithCollection
	{
		private Collection<String> testList;

		public Collection<String> getTestList()
		{
			return testList;
		}

		public void setTestList(final Collection<String> testList)
		{
			this.testList = testList;
		}
	}
}
