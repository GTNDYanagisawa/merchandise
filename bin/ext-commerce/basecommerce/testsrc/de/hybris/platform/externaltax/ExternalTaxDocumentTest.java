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
package de.hybris.platform.externaltax;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.util.TaxValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;


@UnitTest
public class ExternalTaxDocumentTest
{
	@Test
	public void testEmptyDocument()
	{
		final ExternalTaxDocument document = new ExternalTaxDocument();

		assertEquals(Collections.EMPTY_MAP, document.getAllTaxes());
		assertEquals(Collections.EMPTY_LIST, document.getShippingCostTaxes());
		assertEquals(Collections.EMPTY_LIST, document.getTaxesForOrderEntry(0));
		assertEquals(Collections.EMPTY_LIST, document.getTaxesForOrderEntry(2));
		assertEquals(Collections.EMPTY_LIST, document.getTaxesForOrderEntry(3));
	}

	@Test
	public void testDocument()
	{
		final TaxValue tv_10_0, tv_10_1;
		final TaxValue tv_0_0, tv_0_1, tv_0_2;
		final TaxValue tv_s_0, tv_s_1;

		final ExternalTaxDocument document = new ExternalTaxDocument();
		document.setTaxesForOrderEntry(//
				10, //
				tv_10_0 = new TaxValue("STATE", 12, true, 1.23, "USD"), //
				tv_10_1 = new TaxValue("LOCAL", 4, true, 23.32, "USD"));
		document.setTaxesForOrderEntry(//
				0, //
				tv_0_0 = new TaxValue("STATE", 12, true, 33.56, "USD"), //
				tv_0_1 = new TaxValue("LOCAL", 4, true, 594.12, "USD"), //
				tv_0_2 = new TaxValue("WTF", 33, true, 999.99, "USD"));

		document.setShippingCostTaxes(//
				tv_s_0 = new TaxValue("SHIP_STATE", 12, true, 3.44, "USD"), //
				tv_s_1 = new TaxValue("SHIP_LOCAL", 4, true, 96.87, "USD"));

		assertEquals(Arrays.asList(tv_0_0, tv_0_1, tv_0_2), document.getTaxesForOrderEntry(0));
		assertEquals(Arrays.asList(tv_10_0, tv_10_1), document.getTaxesForOrderEntry(10));

		assertEquals(Arrays.asList(tv_s_0, tv_s_1), document.getShippingCostTaxes());

		assertEquals(Collections.EMPTY_LIST, document.getTaxesForOrderEntry(1));
		assertEquals(Collections.EMPTY_LIST, document.getTaxesForOrderEntry(2));

		final Map<Integer, List<TaxValue>> entryTaxes = new HashMap<Integer, List<TaxValue>>();
		entryTaxes.put(Integer.valueOf(0), Arrays.asList(tv_0_0, tv_0_1, tv_0_2));
		entryTaxes.put(Integer.valueOf(10), Arrays.asList(tv_10_0, tv_10_1));

		assertEquals(entryTaxes, document.getAllTaxes());
	}

}
