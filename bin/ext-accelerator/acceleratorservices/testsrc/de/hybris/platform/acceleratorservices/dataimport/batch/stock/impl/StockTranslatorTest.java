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
package de.hybris.platform.acceleratorservices.dataimport.batch.stock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.stock.StockImportAdapter;
import de.hybris.platform.acceleratorservices.dataimport.batch.stock.StockTranslator;
import de.hybris.platform.jalo.Item;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test for {@link StockTranslator}
 */
@UnitTest
public class StockTranslatorTest
{
	private static final String TEST_STOCK = "1234";

	private StockTranslator translator;

	@Mock
	private StockImportAdapter stockImportAdapter;

	@Mock
	private Item item;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		translator = new StockTranslator();
		translator.setStockImportAdapter(stockImportAdapter);
	}

	@Test
	public void test()
	{
		translator.performImport(TEST_STOCK, item);
		BDDMockito.verify(stockImportAdapter, BDDMockito.times(1)).performImport(TEST_STOCK, item);
	}
}
