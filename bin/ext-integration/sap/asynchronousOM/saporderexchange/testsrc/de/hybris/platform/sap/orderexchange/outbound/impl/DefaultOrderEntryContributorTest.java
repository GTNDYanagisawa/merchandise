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
package de.hybris.platform.sap.orderexchange.outbound.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class DefaultOrderEntryContributorTest
{
	private static final String LANG_DE = "DE";
	private static final String LANG_EN = "EN";

	private static final long QUANTITY_1000_LONG = 1000;
	private static final int ENTRY_NUMBER_100_INT = 100;
	private static final String PRODUCT_NAME1_DE = "productName1_DE";
	private static final String UNIT_CODE1 = "unitCode1";
	private static final String PRODUCT_CODE1 = "productCode1";

	private static final long QUANTITY_2000_LONG = 1000;
	private static final int ENTRY_NUMBER_200_INT = 100;
	private static final String PRODUCT_NAME2_EN = "productName2_EN";
	private static final String UNIT_CODE2 = "unitCode2";
	private static final String PRODUCT_CODE2 = "productCode2";

	private static final String CODE = "Code";

	private DefaultOrderEntryContributor cut;

	@Before
	public void setUp()
	{
		cut = new DefaultOrderEntryContributor();
	}

	@Test
	public void testGetColumns()
	{
		final Set<String> columns = cut.getColumns();

		assertTrue(columns.contains(OrderCsvColumns.ORDER_ID));
		assertTrue(columns.contains(OrderEntryCsvColumns.ENTRY_NUMBER));
		assertTrue(columns.contains(OrderEntryCsvColumns.QUANTITY));
		assertTrue(columns.contains(OrderEntryCsvColumns.REJECTION_REASON));
		assertTrue(columns.contains(OrderEntryCsvColumns.NAMED_DELIVERY_DATE));
		assertTrue(columns.contains(OrderEntryCsvColumns.ENTRY_UNIT_CODE));
		assertTrue(columns.contains(OrderEntryCsvColumns.PRODUCT_CODE));
		assertTrue(columns.contains(OrderEntryCsvColumns.PRODUCT_NAME));
	}

	@Test
	public void testCreateRow()
	{
		final OrderModel order = new OrderModel();
		ProductModel product = new ProductModel();
		UnitModel unit = new UnitModel();
		final Locale loc = new Locale(LANG_DE);
		final LanguageModel language_de = new LanguageModel();
		final LanguageModel language_en = new LanguageModel();
		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		final List<LanguageModel> fallbackLanguages = new ArrayList<LanguageModel>();
		OrderEntryModel entry = new OrderEntryModel();

		language_de.setIsocode(LANG_DE);
		language_en.setIsocode(LANG_EN);
		fallbackLanguages.add(language_en);
		language_de.setFallbackLanguages(fallbackLanguages);
		order.setCode(CODE);
		order.setLanguage(language_de);

		//		Item 100
		unit.setCode(UNIT_CODE1);

		product.setCode(PRODUCT_CODE1);
		product.setName(PRODUCT_NAME1_DE, loc);

		entry.setEntryNumber(ENTRY_NUMBER_100_INT);
		entry.setQuantity(Long.valueOf(QUANTITY_1000_LONG));
		entry.setProduct(product);
		entry.setUnit(unit);
		entries.add(entry);

		//		Item 200
		unit = new UnitModel();
		product = new ProductModel();
		entry = new OrderEntryModel();

		unit.setCode(UNIT_CODE2);

		product.setCode(PRODUCT_CODE2);
		product.setName(PRODUCT_NAME2_EN, loc);

		entry.setEntryNumber(ENTRY_NUMBER_200_INT);
		entry.setQuantity(Long.valueOf(QUANTITY_2000_LONG));
		entry.setProduct(product);
		entry.setUnit(unit);
		entries.add(entry);


		order.setEntries(entries);

		final List<Map<String, Object>> rows = cut.createRows(order);

		final Map<String, Object> row = rows.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(ENTRY_NUMBER_100_INT, row.get(OrderEntryCsvColumns.ENTRY_NUMBER));
		assertEquals(QUANTITY_1000_LONG, row.get(OrderEntryCsvColumns.QUANTITY));
		assertEquals(UNIT_CODE1, row.get(OrderEntryCsvColumns.ENTRY_UNIT_CODE));
		assertEquals(PRODUCT_CODE1, row.get(OrderEntryCsvColumns.PRODUCT_CODE));
		assertEquals(PRODUCT_NAME1_DE, row.get(OrderEntryCsvColumns.PRODUCT_NAME));

		final Map<String, Object> row1 = rows.get(1);
		assertEquals(CODE, row1.get(OrderCsvColumns.ORDER_ID));
		assertEquals(ENTRY_NUMBER_200_INT, row1.get(OrderEntryCsvColumns.ENTRY_NUMBER));
		assertEquals(QUANTITY_2000_LONG, row1.get(OrderEntryCsvColumns.QUANTITY));
		assertEquals(UNIT_CODE2, row1.get(OrderEntryCsvColumns.ENTRY_UNIT_CODE));
		assertEquals(PRODUCT_CODE2, row1.get(OrderEntryCsvColumns.PRODUCT_CODE));
		assertEquals(PRODUCT_NAME2_EN, row1.get(OrderEntryCsvColumns.PRODUCT_NAME));
	}

	@Test
	public void testCreateRowWithNullUnit()
	{
		final OrderModel order = new OrderModel();
		final ProductModel product = new ProductModel();
		final LanguageModel language_de = new LanguageModel();
		final OrderEntryModel entry = new OrderEntryModel();

		language_de.setIsocode(LANG_DE);
		order.setCode(CODE);
		order.setLanguage(language_de);

		product.setCode(PRODUCT_CODE1);
		product.setName(PRODUCT_NAME1_DE, new Locale(LANG_DE));

		entry.setEntryNumber(ENTRY_NUMBER_100_INT);
		entry.setQuantity(Long.valueOf(QUANTITY_1000_LONG));
		entry.setProduct(product);

		order.setEntries(Collections.<AbstractOrderEntryModel> singletonList(entry));

		final List<Map<String, Object>> rows = cut.createRows(order);

		final Map<String, Object> row = rows.get(0);
		assertNull(row.get(OrderEntryCsvColumns.ENTRY_UNIT_CODE));

	}
}
