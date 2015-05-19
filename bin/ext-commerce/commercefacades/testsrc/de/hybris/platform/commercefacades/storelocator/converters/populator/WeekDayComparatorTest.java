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
package de.hybris.platform.commercefacades.storelocator.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.WeekDay;
import de.hybris.platform.commercefacades.storelocator.converters.populator.WeekdayOpeningDayListPopulator.WeekDayComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.fest.assertions.Assertions;
import org.junit.Test;


/**
 * Unit test for the {@link WeekDayComparator}
 * 
 */
@UnitTest
public class WeekDayComparatorTest
{
	private WeekDayComparator comparator;

	@Test
	public void testCompareForSundayFirst()
	{

		comparator = new WeekDayComparator(WeekDay.SUNDAY);

		final TreeMap<WeekDay, String> sampleMap = new TreeMap<WeekDay, String>(comparator);
		sampleMap.put(WeekDay.MONDAY, "monday");
		sampleMap.put(WeekDay.TUESDAY, "tuesday");
		sampleMap.put(WeekDay.FRIDAY, "friday");

		sampleMap.put(WeekDay.SUNDAY, "sunday");
		sampleMap.put(WeekDay.SATURDAY, "saturday");

		assertValuesInOrder(new ArrayList<String>(sampleMap.values()), "sunday", "monday", "tuesday", "friday", "saturday");

	}

	@Test
	public void testCompareForMondayFirst()
	{

		comparator = new WeekDayComparator(WeekDay.MONDAY);

		final TreeMap<WeekDay, String> sampleMap = new TreeMap<WeekDay, String>(comparator);

		sampleMap.put(WeekDay.TUESDAY, "tuesday");
		sampleMap.put(WeekDay.FRIDAY, "friday");
		sampleMap.put(WeekDay.MONDAY, "monday");
		sampleMap.put(WeekDay.SUNDAY, "sunday");
		sampleMap.put(WeekDay.SATURDAY, "saturday");

		assertValuesInOrder(new ArrayList<String>(sampleMap.values()), "monday", "tuesday", "friday", "saturday", "sunday");

	}


	@Test
	public void testCompareForFridayFirstWithNulls()
	{

		comparator = new WeekDayComparator(WeekDay.FRIDAY);

		final TreeMap<WeekDay, String> sampleMap = new TreeMap<WeekDay, String>(comparator);
		sampleMap.put(WeekDay.MONDAY, "monday");
		sampleMap.put(WeekDay.TUESDAY, "tuesday");
		sampleMap.put(WeekDay.FRIDAY, "friday");
		sampleMap.put(null, "wednesday");
		sampleMap.put(WeekDay.SUNDAY, "sunday");
		sampleMap.put(WeekDay.SATURDAY, "saturday");

		assertValuesInOrder(new ArrayList<String>(sampleMap.values()), "wednesday", "friday", "saturday", "sunday", "monday",
				"tuesday");

	}

	protected void assertValuesInOrder(final List<String> value, final String... expactedOrder)
	{
		Assertions.assertThat(value).containsSequence(expactedOrder);
	}

}
