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
package com.hybris.cis.api.test.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Assert;

import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisAddressType;
import com.hybris.cis.api.model.CisLineItem;
import com.hybris.cis.api.model.CisOrder;


/**
 * @author justin.robbins
 * 
 */
public class TestUtils
{

	private TestUtils()
	{
	}

	public static void assertEquals(final BigDecimal expected, final BigDecimal actual, final boolean precisionMatters)
	{
		if (precisionMatters)
		{
			Assert.assertEquals(expected, actual);
		}
		else
		{
			Assert.assertEquals(0, expected.compareTo(actual));
		}
	}

	public static CisOrder createSampleOrder()
	{
		final CisOrder usOrder = new CisOrder();
		final CisAddress shipToAddress = newAddress("1700 Broadway", "New York", "NY", "10019", "US");
		shipToAddress.setType(CisAddressType.SHIP_TO);

		final CisAddress originAddress = newAddress("1295 Charleston Rd", "Mountain View", "CA", "94043-1307", "US");
		originAddress.setType(CisAddressType.SHIP_FROM);

		final CisAddress acceptanceAddress = newAddress("2-24 29th St", "Fair Lawn", "NJ", "07410-3948", "US");
		acceptanceAddress.setType(CisAddressType.ADMIN_ORIGIN);

		final CisAddress billToAddress = newAddress("1700 Broadway", "New York", "NY", "10019", "US");
		billToAddress.setFirstName("TestFirstName");
		billToAddress.setLastName("TestLastName");
		billToAddress.setEmail("test@123.com");
		billToAddress.setType(CisAddressType.BILL_TO);

		final List<CisLineItem> lineItems = new ArrayList<CisLineItem>();
		final CisLineItem lineItem = new CisLineItem();
		lineItem.setId(new Integer(12));
		lineItem.setItemCode("100");
		lineItem.setQuantity(new Integer(1));
		lineItem.setUnitPrice(BigDecimal.TEN);
		lineItem.setProductDescription("Test item");
		lineItems.add(lineItem);

		final CisLineItem lineItem2 = new CisLineItem();
		lineItem2.setId(new Integer(34));
		lineItem2.setItemCode("200");
		lineItem2.setQuantity(new Integer(2));
		lineItem2.setUnitPrice(BigDecimal.TEN);
		lineItem2.setProductDescription("Test item");
		lineItems.add(lineItem2);

		usOrder.setId("UT" + new Date().getTime() + new Random(new Date().getTime()).nextInt(100));
		usOrder.getAddresses().add(shipToAddress);
		usOrder.getAddresses().add(originAddress);
		usOrder.getAddresses().add(acceptanceAddress);
		usOrder.getAddresses().add(billToAddress);
		usOrder.setLineItems(lineItems);

		usOrder.setCurrency("USD");
		return usOrder;

	}

	private static CisAddress newAddress(final String addressLine1, final String city, final String state, final String zip,
			final String country)
	{
		final CisAddress address = new CisAddress();
		address.setAddressLine1(addressLine1);
		address.setAddressLine2("line2");
		address.setCity(city);
		address.setState(state);
		address.setZipCode(zip);
		address.setCountry(country);
		return address;
	}
}
