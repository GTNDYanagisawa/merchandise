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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;

import com.hybris.datahub.core.data.TestProductData;


/**
 * Utility class for creating beans and maps that can be used for testing the DataHubOutboundService
 */
public class OutboundServiceDataGenerationTestUtils
{
	private static final String BASE_NAME = "baseName";
	private static final String INTEGRATION_KEY = "integrationKey";
	private static final String ISO_CODE = "isoCode";
	private static final String SIZE = "size";
	private static final String SKU = "SKU";
	private static final String STYLE = "style";
	private static final String UNIT = "unit";

	private static final String BASE_NAME_VALUE = "test base name";
	private static final String INTEGRATION_KEY_VALUE = "123";
	private static final String ISO_CODE_VALUE = "en";
	private static final String SIZE_VALUE = "test size";
	private static final String SKU_VALUE = "test sku";
	private static final String STYLE_VALUE = "test style";
	private static final String UNIT_VALUE = "test unit";

	public static Map<String, Object> createUniqueTestProductMap()
	{
		final String uniqueString = RandomStringUtils.randomAlphabetic(3);

		final Map<String, Object> testProductMap;

		testProductMap = new HashMap<>();
		testProductMap.put(BASE_NAME, BASE_NAME_VALUE + "-" + uniqueString);
		testProductMap.put(INTEGRATION_KEY, INTEGRATION_KEY_VALUE + "-" + uniqueString);
		testProductMap.put(ISO_CODE, ISO_CODE_VALUE + "-" + uniqueString);
		testProductMap.put(SIZE, SIZE_VALUE + "-" + uniqueString);
		testProductMap.put(SKU, SKU_VALUE + "-" + uniqueString);
		testProductMap.put(STYLE, STYLE_VALUE + "-" + uniqueString);
		testProductMap.put(UNIT, UNIT_VALUE + "-" + uniqueString);

		return testProductMap;
	}

	public static Map<String, String> createUniquePrimaryKeyMap()
	{
		final String uniqueString = RandomStringUtils.randomAlphabetic(3);

		final Map<String, String> primaryKeyMap;

		primaryKeyMap = new HashMap<>();
		primaryKeyMap.put(INTEGRATION_KEY, INTEGRATION_KEY_VALUE + "-" + uniqueString);
		primaryKeyMap.put(SKU, SKU_VALUE + "-" + uniqueString);

		return primaryKeyMap;
	}

	public static TestProductData createTestProductData()
	{
		final TestProductData testProduct = new TestProductData();
		testProduct.setBaseName(BASE_NAME_VALUE);
		testProduct.setIntegrationKey(INTEGRATION_KEY_VALUE);
		testProduct.setIsoCode(ISO_CODE_VALUE);
		testProduct.setSize(SIZE_VALUE);
		testProduct.setSKU(SKU_VALUE);
		testProduct.setStyle(STYLE_VALUE);
		testProduct.setUnit(UNIT_VALUE);

		return testProduct;
	}
}
