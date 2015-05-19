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
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.util.TaxValue;

import java.util.Arrays;
import java.util.Collection;

import org.cxml.ItemDetail;
import org.cxml.ItemID;
import org.cxml.ItemOut;
import org.cxml.Money;
import org.cxml.Tax;
import org.cxml.UnitPrice;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderEntryOverridingPopulatorTest
{

	private static final String PRICE_VALUE = "10.00";
	private final static String ITEM_QTY = "1";
	private final static String SUPPLIER_PART_ID = "123456";

	@InjectMocks
	private final DefaultOrderEntryOverridingPopulator defaultOrderEntryPopulator = new DefaultOrderEntryOverridingPopulator();

	private final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();

	@Mock
	private ProductService productService;
	@Mock
	private ItemOut itemOut;
	@Mock
	private ItemID itemId;
	@Mock
	private ItemDetail itemDetail;
	@Mock
	private UnitPrice unitPrice;
	@Mock
	private Money unitPriceMoney;

	@Mock
	private Populator<Tax, Collection<TaxValue>> taxValuePopulator;

	@Before
	public void setUp()
	{
		when(itemOut.getQuantity()).thenReturn(ITEM_QTY);
		when(itemOut.getItemID()).thenReturn(itemId);
		when(itemId.getSupplierPartID()).thenReturn(SUPPLIER_PART_ID);
		when(itemOut.getItemDetailOrBlanketItemDetail()).thenReturn(Arrays.<Object> asList(itemDetail));
		when(itemDetail.getUnitPrice()).thenReturn(unitPrice);
		when(unitPrice.getMoney()).thenReturn(unitPriceMoney);
		when(unitPriceMoney.getvalue()).thenReturn(PRICE_VALUE);

		when(productService.getProductForCode(SUPPLIER_PART_ID)).thenReturn(new ProductModel());
	}

	@Test
	public void testPopulate()
	{
		defaultOrderEntryPopulator.populate(itemOut, orderEntry);
		assertEquals(Double.valueOf(PRICE_VALUE), orderEntry.getBasePrice());
	}

}
