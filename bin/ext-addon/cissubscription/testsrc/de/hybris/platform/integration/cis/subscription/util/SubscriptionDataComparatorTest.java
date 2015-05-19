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
package de.hybris.platform.integration.cis.subscription.util;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * Test class for {@link SubscriptionDataComparator}.
 */
@UnitTest
public class SubscriptionDataComparatorTest
{

	private List<SubscriptionData> subscriptions;

	@Before
	public void createSubscriptionsForSorting()
	{
		subscriptions = new ArrayList<>(5);
		final Calendar calendar = Calendar.getInstance();

		final SubscriptionData sub0 = new SubscriptionData();
		sub0.setSubscriptionStatus("ACTIVE");
		sub0.setId("0");
		sub0.setStartDate(calendar.getTime());

		final SubscriptionData sub1 = new SubscriptionData();
		sub1.setSubscriptionStatus("ACTIVE");
		sub1.setId("1");
		calendar.add(Calendar.DATE, -2);
		sub1.setStartDate(calendar.getTime());

		final SubscriptionData sub2 = new SubscriptionData();
		sub2.setSubscriptionStatus("PAUSED");
		sub2.setId("2");

		final SubscriptionData sub3 = new SubscriptionData();
		sub3.setSubscriptionStatus("CANCELLED");
		sub3.setId("3");

		final SubscriptionData sub4 = new SubscriptionData();
		sub4.setSubscriptionStatus("EXPIRED");
		sub4.setId("4");

		subscriptions.add(sub4);
		subscriptions.add(sub3);
		subscriptions.add(sub2);
		subscriptions.add(sub1);
		subscriptions.add(sub0);
	}

	@Test
	public void testSubscriptionDataComparatorInList()
	{
		Collections.sort(subscriptions, new SubscriptionDataComparator());
		for (int i = 0; i < subscriptions.size(); ++i)
		{
			final SubscriptionData sub = subscriptions.get(i);
			assertEquals("Incorrect order: Subscription with id [" + sub.getId() + "] placed on " + i, String.valueOf(i),
					sub.getId());
		}
	}

	@Test
	public void testSubscriptionDataComparatorDirectCompare()
	{
		final SubscriptionData sub4 = subscriptions.get(0);
		final SubscriptionData sub3 = subscriptions.get(1);
		final SubscriptionData sub2 = subscriptions.get(2);
		final SubscriptionData sub1 = subscriptions.get(3);
		final SubscriptionData sub0 = subscriptions.get(4);
		final SubscriptionDataComparator comparator = new SubscriptionDataComparator();

		assertEquals("Should be: ACTIVE -> PAUSED < 0", -1, comparator.compare(sub0, sub2));
		assertEquals("Should be: ACTIVE -> CANCELLED < 0", -1, comparator.compare(sub0, sub3));
		assertEquals("Should be: ACTIVE -> EXPIRED < 0", -1, comparator.compare(sub0, sub4));
		assertEquals("Should be: PAUSED -> CANCELLED < 0", -1, comparator.compare(sub2, sub3));
		assertEquals("Should be: PAUSED -> EXPIRED < 0", -1, comparator.compare(sub2, sub4));
		assertEquals("Should be: CANCELLED -> EXPIRED < 0", -1, comparator.compare(sub3, sub4));
		assertEquals("Should be: EXPIRED -> ACTIVE > 0", 1, comparator.compare(sub4, sub0));

		assertEquals("Should be: ACTIVE -> ACTIVE == 0", 0, comparator.compare(sub0, sub0));
		assertEquals("Should be: PAUSED -> PAUSED == 0", 0, comparator.compare(sub2, sub2));
		assertEquals("Should be: CANCELLED -> CANCELLED == 0", 0, comparator.compare(sub3, sub3));
		assertEquals("Should be: EXPIRED -> EXPIRED == 0", 0, comparator.compare(sub4, sub4));

		final SubscriptionData sub5 = new SubscriptionData();
		sub5.setSubscriptionStatus("UNKNOWN STATUS");
		assertEquals("Should be: * -> UNKNOWN STATUS == 0", 0, comparator.compare(sub4, sub5));
		assertEquals("Should be: UNKNOWN STATUS -> * == 0", 0, comparator.compare(sub5, sub1));

		sub5.setSubscriptionStatus(null);
		assertEquals("Should be: * -> null Status == 0", 0, comparator.compare(sub4, sub5));
		assertEquals("Should be: null STATUS -> * == 0", 0, comparator.compare(sub5, sub1));

		sub5.setStartDate(null);
		sub5.setSubscriptionStatus("ACTIVE");
		assertEquals(0, comparator.compare(sub5, sub1)); // no exceptions on null date

		assertEquals("Should be: ACTIVE -> ACTIVE with earlier start date < 0", -1, comparator.compare(sub0, sub1));
	}
}