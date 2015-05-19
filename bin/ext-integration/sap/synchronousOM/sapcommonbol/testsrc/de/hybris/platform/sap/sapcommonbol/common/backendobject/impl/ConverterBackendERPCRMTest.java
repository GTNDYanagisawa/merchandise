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
package de.hybris.platform.sap.sapcommonbol.common.backendobject.impl;

import de.hybris.platform.sap.sapcommonbol.common.backendobject.impl.ConverterBackendERPCRM.CurrencyLocalization;
import de.hybris.platform.sap.sapcommonbol.common.backendobject.impl.ConverterBackendERPCRM.UomLocalization;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class ConverterBackendERPCRMTest extends TestCase
{

	private static final String UNIT_PC = "PC";
	private static final String DESCRIPTION_USD = "US Dollar";
	private static final String CURRENCY_USD = "USD";

	public void testCurrencyLocalization()
	{
		final Map<String, Integer> numberOfDecimalsForCurrencies = new HashMap<String, Integer>();
		numberOfDecimalsForCurrencies.put(CURRENCY_USD, Integer.valueOf(2));
		final Map<String, String> currencyDescriptions = new HashMap<String, String>();
		currencyDescriptions.put(CURRENCY_USD, DESCRIPTION_USD);
		final ConverterBackendERPCRM.CurrencyLocalization currLocalization = new CurrencyLocalization(
				numberOfDecimalsForCurrencies, currencyDescriptions);
		assertEquals(new Integer(2), currLocalization.getNumberOfDecimals(CURRENCY_USD));
		assertEquals(DESCRIPTION_USD, currLocalization.getDescription(CURRENCY_USD));
	}

	public void testUOMLocalization()
	{
		final Map<String, Integer> numberOfDecimalsForUOMs = new HashMap<String, Integer>();
		numberOfDecimalsForUOMs.put(UNIT_PC, Integer.valueOf(3));
		final Map<String, String> internalToExternalUom = new HashMap<String, String>();
		final Map<String, String> externalToInternalUom = new HashMap<String, String>();
		final Map<String, String> internalToExternalDescriptionsUom = new HashMap<String, String>();
		final ConverterBackendERPCRM.UomLocalization uomLocalization = new UomLocalization(numberOfDecimalsForUOMs,
				internalToExternalUom, externalToInternalUom, internalToExternalDescriptionsUom);
		assertEquals(new Integer(3), uomLocalization.getNumberOfDecimals(UNIT_PC));
	}
}
