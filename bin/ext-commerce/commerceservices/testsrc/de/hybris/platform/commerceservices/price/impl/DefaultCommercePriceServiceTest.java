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
package de.hybris.platform.commerceservices.price.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultCommercePriceService}
 */
@UnitTest
public class DefaultCommercePriceServiceTest
{
	private static final String TEST_CURRENCY = "EUR";
	private static final Boolean TEST_NET = Boolean.FALSE;

	private DefaultCommercePriceService csPriceService;
	@Mock
	private PriceService priceService;
	@Mock
	private ProductModel product;
	@Mock
	private VariantProductModel var1;
	@Mock
	private VariantProductModel var2;
	@Mock
	private PriceInformation priceInfo;
	@Mock
	private PriceInformation varPriceInfo;
	@Mock
	private PriceValue priceVal;
	@Mock
	private PriceValue varPriceVal;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		csPriceService = new DefaultCommercePriceService();
		csPriceService.setPriceService(priceService);
		given(priceInfo.getPriceValue()).willReturn(priceVal);
		given(varPriceInfo.getPriceValue()).willReturn(varPriceVal);
		given(priceVal.getCurrencyIso()).willReturn(TEST_CURRENCY);
		given(Boolean.valueOf(priceVal.isNet())).willReturn(TEST_NET);
		given(varPriceVal.getCurrencyIso()).willReturn(TEST_CURRENCY);
		given(Boolean.valueOf(varPriceVal.isNet())).willReturn(TEST_NET);
	}

	@Test
	public void testFromPriceVariants()
	{
		final List<PriceInformation> listPriceInfo = new ArrayList<PriceInformation>();
		final List<PriceInformation> varListPriceInfo = new ArrayList<PriceInformation>();
		final List<VariantProductModel> list = new LinkedList<VariantProductModel>();
		list.add(var1);
		list.add(var2);
		given(priceService.getPriceInformationsForProduct(var1)).willReturn(listPriceInfo);
		given(priceService.getPriceInformationsForProduct(var2)).willReturn(varListPriceInfo);
		given(product.getVariants()).willReturn(list);
		listPriceInfo.add(priceInfo);
		varListPriceInfo.add(varPriceInfo);
		given(Double.valueOf(priceVal.getValue())).willReturn(new Double(1d));
		given(Double.valueOf(varPriceVal.getValue())).willReturn(new Double(2d));
		Assert.assertEquals(priceInfo, csPriceService.getFromPriceForProduct(product));
		given(Double.valueOf(priceVal.getValue())).willReturn(new Double(2d));
		given(Double.valueOf(varPriceVal.getValue())).willReturn(new Double(1d));
		Assert.assertEquals(varPriceInfo, csPriceService.getFromPriceForProduct(product));
		given(Double.valueOf(priceVal.getValue())).willReturn(new Double(1d));
		Assert.assertEquals(priceInfo, csPriceService.getFromPriceForProduct(product));
	}

	@Test
	public void testFromPriceBase()
	{
		final List<PriceInformation> listPriceInfo = new ArrayList<PriceInformation>();
		given(product.getVariants()).willReturn(Collections.EMPTY_LIST);
		given(priceService.getPriceInformationsForProduct(product)).willReturn(listPriceInfo);
		listPriceInfo.add(priceInfo);
		Assert.assertEquals(priceInfo, csPriceService.getFromPriceForProduct(product));
	}

	@Test
	public void testWebPrice()
	{
		final List<PriceInformation> listPriceInfo = new ArrayList<PriceInformation>();
		given(priceService.getPriceInformationsForProduct(product)).willReturn(listPriceInfo);
		listPriceInfo.add(priceInfo);

		Assert.assertEquals(priceInfo, csPriceService.getWebPriceForProduct(product));
	}
}
