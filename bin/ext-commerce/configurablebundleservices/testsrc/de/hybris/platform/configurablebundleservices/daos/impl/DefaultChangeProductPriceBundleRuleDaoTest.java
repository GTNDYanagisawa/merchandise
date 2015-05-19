/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.configurablebundleservices.daos.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * JUnit test suite for {@link DefaultChangeProductPriceBundleRuleDao}
 */
@UnitTest
public class DefaultChangeProductPriceBundleRuleDaoTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultChangeProductPriceBundleRuleDao priceBundleRuleDao;

	@Before
	public void setUp() throws Exception
	{
		priceBundleRuleDao = new DefaultChangeProductPriceBundleRuleDao();
	}

	@Test
	public void testFindPriceBundleRulesByTargetProductAndTemplateWhenNoProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("targetProduct can not be null");

		priceBundleRuleDao.findBundleRulesByTargetProductAndTemplate(null, new BundleTemplateModel());
	}

	@Test
	public void testFindPriceBundleRulesByTargetProductAndTemplateWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		priceBundleRuleDao.findBundleRulesByTargetProductAndTemplate(new ProductModel(), null);
	}

	@Test
	public void testFindPriceBundleRulesByTargetProductWhenNoProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("product can not be null");

		priceBundleRuleDao.findBundleRulesByTargetProduct(null);
	}

	@Test
	public void testFindPriceBundleRulesByTargetProductAndCurrencyWhenNoProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("product can not be null");

		priceBundleRuleDao.findBundleRulesByTargetProductAndCurrency(null, new CurrencyModel());
	}

	@Test
	public void testFindPriceBundleRulesByTargetProductAndCurrencyWhenNoCurrency()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("currency can not be null");

		priceBundleRuleDao.findBundleRulesByTargetProductAndCurrency(new ProductModel(), null);
	}

	@Test
	public void testFindPriceBundleRulesByTargetProductAndTemplateAndCurrencyWhenNoProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("targetProduct can not be null");

		priceBundleRuleDao.findBundleRulesByTargetProductAndTemplateAndCurrency(null, new BundleTemplateModel(),
				new CurrencyModel());
	}

	@Test
	public void testFindPriceBundleRulesByTargetProductAndTemplateAndCurrencyWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		priceBundleRuleDao.findBundleRulesByTargetProductAndTemplateAndCurrency(new ProductModel(), null, new CurrencyModel());
	}

	@Test
	public void testFindPriceBundleRulesByTargetProductAndTemplateAndCurrencyWhenNoCurrency()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("currency can not be null");

		priceBundleRuleDao
				.findBundleRulesByTargetProductAndTemplateAndCurrency(new ProductModel(), new BundleTemplateModel(), null);
	}

}
