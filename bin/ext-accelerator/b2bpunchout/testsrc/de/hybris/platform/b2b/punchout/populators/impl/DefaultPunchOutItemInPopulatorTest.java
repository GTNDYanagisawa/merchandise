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
package de.hybris.platform.b2b.punchout.populators.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.enums.PunchOutClassificationDomain;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.cxml.ItemIn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutItemInPopulatorTest
{

	private static final String UNIT_OF_MEASURE = "UnitOfMeasure";
	private static final Long ORDER_ENTRY_QUANTITY = Long.valueOf(666L);
	private static final String LANG_ISO_CODE = "en";
	private static final String PRODUCT_CODE = "1234";
	private static final String CURRENCY_ISO_CODE = "USD";
	private static final String UNIT_TYPE = "mm";
	private static final String DESCRIPTION = "description";
	private static final Double ORDER_ENTRY_PRICE = new Double(123.12);

	@InjectMocks
	private final DefaultPunchOutItemInPopulator orderEntryPopulator = new DefaultPunchOutItemInPopulator();
	private final AbstractOrderEntryModel orderEntryModel = new OrderEntryModel();
	private final ItemIn itemIn = new ItemIn();
	private final LanguageModel lang = new LanguageModel();
	private final CurrencyModel currency = new CurrencyModel();
	@Mock
	private CommonI18NService commonI18NService;

	@Before
	public void setUp()
	{
		lang.setIsocode(LANG_ISO_CODE);
		currency.setIsocode(CURRENCY_ISO_CODE);
		when(commonI18NService.getCurrentLanguage()).thenReturn(lang);
		when(commonI18NService.getCurrentCurrency()).thenReturn(currency);

		final ProductModel product = mock(ProductModel.class);
		final UnitModel unitModel = new UnitModel();
		unitModel.setUnitType(UNIT_TYPE);


		when(product.getCode()).thenReturn(PRODUCT_CODE);
		when(product.getUnit()).thenReturn(unitModel);
		when(product.getDescription()).thenReturn(DESCRIPTION);
		when(product.getUnitOfMeasure()).thenReturn(UNIT_OF_MEASURE);
		orderEntryModel.setProduct(product);
		orderEntryModel.setQuantity(ORDER_ENTRY_QUANTITY);
		orderEntryModel.setBasePrice(ORDER_ENTRY_PRICE);

	}

	@Test
	public void normalPopulation()
	{

		orderEntryPopulator.populate(orderEntryModel, itemIn);
		assertEquals(String.valueOf(orderEntryModel.getQuantity()), itemIn.getQuantity());

		assertEquals(orderEntryModel.getProduct().getCode(), itemIn.getItemID().getSupplierPartID());
		assertEquals(CURRENCY_ISO_CODE, itemIn.getItemDetail().getUnitPrice().getMoney().getCurrency());
		assertEquals(String.valueOf(ORDER_ENTRY_PRICE), itemIn.getItemDetail().getUnitPrice().getMoney().getvalue());
		assertEquals(String.valueOf(orderEntryModel.getBasePrice()), itemIn.getItemDetail().getUnitPrice().getMoney().getvalue());


		assertEquals(orderEntryModel.getProduct().getDescription(), itemIn.getItemDetail().getDescription().get(0).getvalue());
		assertEquals(LANG_ISO_CODE, itemIn.getItemDetail().getDescription().get(0).getXmlLang());
		assertEquals(orderEntryModel.getProduct().getUnitOfMeasure(), itemIn.getItemDetail().getUnitOfMeasure());

		assertEquals(PunchOutClassificationDomain.UNSPSC.getCode(), itemIn.getItemDetail().getClassification().get(0).getDomain());
		assertEquals(orderEntryModel.getProduct().getUnspcs(), itemIn.getItemDetail().getClassification().get(0).getvalue());

	}


}
