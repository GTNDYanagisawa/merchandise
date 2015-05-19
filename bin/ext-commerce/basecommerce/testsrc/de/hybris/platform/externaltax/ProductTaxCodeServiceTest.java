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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.externaltax.ProductTaxCodeModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Black box test for {@link ProductTaxCodeService}.
 */
@IntegrationTest
public class ProductTaxCodeServiceTest extends ServicelayerBaseTest
{
	@Resource
	ProductTaxCodeService productTaxCodeService;

	@Resource
	ModelService modelService;

	static String[][] TEST_DATA =
	{
	{ "P-01", "C-A", "TC-01" },
	{ "P-01", "C-B", "TC-02" },
	{ "P-01", "C-C", "TC-03" },
	{ "P-02", "C-A", "TC-01" },
	{ "P-02", "C-C", "TC-04" },
	{ "P-03", "C-A", "TC-05" },
	{ "P-03", "C-B", "TC-02" } };

	@Before
	public void setUp()
	{
		for (final String[] record : TEST_DATA)
		{
			final ProductTaxCodeModel taxCode = modelService.create(ProductTaxCodeModel.class);
			taxCode.setProductCode(record[0]);
			taxCode.setTaxArea(record[1]);
			taxCode.setTaxCode(record[2]);
		}
		modelService.saveAll();
	}

	@Test
	public void testDirectLookups()
	{
		assertNull(productTaxCodeService.lookupTaxCode("foo", "bar"));
		assertNull(productTaxCodeService.lookupTaxCode("P-01", "foo"));
		assertNull(productTaxCodeService.lookupTaxCode("foo", "C-A"));

		// test existing values direct lookup
		for (final String[] record : TEST_DATA)
		{
			final String productCode = record[0];
			final String countryCode = record[1];
			final String taxCode = record[2];
			assertEquals(taxCode, productTaxCodeService.lookupTaxCode(productCode, countryCode));
		}

		// test existing values bulk lookup
		for (final Map.Entry<String, Map<String, String>> e : createBulkLookupMap().entrySet())
		{
			final String countryCode = e.getKey();
			final Collection<String> productCodes = e.getValue().keySet();
			final Map<String, String> expectedResult = e.getValue();

			assertEquals(expectedResult, productTaxCodeService.lookupTaxCodes(productCodes, countryCode));
		}

		// test no-result bulk lookup
		assertEquals(Collections.EMPTY_MAP,
				productTaxCodeService.lookupTaxCodes(Arrays.asList(TEST_DATA[0][0], TEST_DATA[1][0], TEST_DATA[2][0]), "foo"));

		// test empty bulk lookup
		assertEquals(Collections.EMPTY_MAP, productTaxCodeService.lookupTaxCodes(Collections.EMPTY_LIST, "foo"));
	}

	@Test
	public void testModelLookup()
	{
		assertNull(productTaxCodeService.getTaxCodeForProductAndArea("foo", "bar"));
		assertNull(productTaxCodeService.getTaxCodeForProductAndArea("P-01", "foo"));
		assertNull(productTaxCodeService.getTaxCodeForProductAndArea("foo", "C-A"));

		// test existing values direct lookup
		for (final String[] record : TEST_DATA)
		{
			final String productCode = record[0];
			final String countryCode = record[1];
			final String taxCode = record[2];

			final ProductTaxCodeModel model = productTaxCodeService.getTaxCodeForProductAndArea(productCode, countryCode);
			assertNotNull(model);
			assertEquals(productCode, model.getProductCode());
			assertEquals(countryCode, model.getTaxArea());
			assertEquals(taxCode, model.getTaxCode());
		}

		assertEquals(Collections.EMPTY_LIST, productTaxCodeService.getTaxCodesForProduct("foo"));

		final Map<String, String> dataForProduct = new HashMap<String, String>();
		dataForProduct.put(TEST_DATA[0][1], TEST_DATA[0][2]);
		dataForProduct.put(TEST_DATA[1][1], TEST_DATA[1][2]);
		dataForProduct.put(TEST_DATA[2][1], TEST_DATA[2][2]);
		assertEquals(dataForProduct, getCountryCodeMap(productTaxCodeService.getTaxCodesForProduct(TEST_DATA[0][0])));
	}

	Map<String, String> getCountryCodeMap(final Collection<ProductTaxCodeModel> taxCodes)
	{
		final Map<String, String> data = new HashMap<String, String>();
		for (final ProductTaxCodeModel m : taxCodes)
		{
			data.put(m.getTaxArea(), m.getTaxCode());
		}
		return data;
	}

	private Map<String, Map<String, String>> createBulkLookupMap()
	{
		final Map<String, Map<String, String>> bulkLookupData = new HashMap<String, Map<String, String>>();
		for (final String[] record : TEST_DATA)
		{
			final String productCode = record[0];
			final String countryCode = record[1];
			final String taxCode = record[2];

			Map<String, String> countryMap = bulkLookupData.get(countryCode);
			if (countryMap == null)
			{
				countryMap = new HashMap<String, String>();
				bulkLookupData.put(countryCode, countryMap);
			}
			countryMap.put(productCode, taxCode);
		}
		return bulkLookupData;
	}

}
