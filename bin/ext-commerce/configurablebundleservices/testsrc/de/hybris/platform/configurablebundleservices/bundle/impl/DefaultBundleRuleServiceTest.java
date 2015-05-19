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
package de.hybris.platform.configurablebundleservices.bundle.impl;

import static junit.framework.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.daos.impl.DefaultChangeProductPriceBundleRuleDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * JUnit test suite for {@link FindBundlePricingWithCurrentPriceFactoryStrategy} and {@link DefaultBundleRuleService}.
 * The good cases are tested in the integration tests as it makes no sense to mock a complex bundle template/rule system
 * especially if the logic for evaluating the rules is implemented in a flexible search query.
 */
@UnitTest
public class DefaultBundleRuleServiceTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private BundleRuleService bundleRuleService;

	private DefaultChangeProductPriceBundleRuleDao changeProductPriceBundleRuleDao;

	@Before
	public void setUp() throws Exception
	{
		final DefaultBundleRuleService defaultBundleRuleService = new DefaultBundleRuleService();
		changeProductPriceBundleRuleDao = mock(DefaultChangeProductPriceBundleRuleDao.class);
		defaultBundleRuleService.setChangeProductPriceBundleRuleDao(changeProductPriceBundleRuleDao);
		setBundleRuleService(defaultBundleRuleService);
	}

	@Test
	public void testGetChangePriceBundleRuleForOrderEntryWhenNoCartEntry()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("entry can not be null");

		getBundleRuleService().getChangePriceBundleRuleForOrderEntry(null);
	}

	@Test
	public void testGetChangePriceBundleRuleForOrderEntryWhenNoBundleTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("entry model does not have a bundle template");

		getBundleRuleService().getChangePriceBundleRuleForOrderEntry(new CartEntryModel());
	}

	@Test
	public void testGetChangePriceBundleRuleWhenNoBundleTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("targetComponent can not be null");

		getBundleRuleService().getChangePriceBundleRule(null, new ProductModel(), new ProductModel(), new CurrencyModel());
	}

	@Test
	public void testGetChangePriceBundleRuleWhenNoTargetProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("targetProduct can not be null");

		getBundleRuleService().getChangePriceBundleRule(new BundleTemplateModel(), null, new ProductModel(), new CurrencyModel());
	}

	@Test
	public void testGetChangePriceBundleRuleWhenNoConditionalProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("conditionalProduct can not be null");

		getBundleRuleService().getChangePriceBundleRule(new BundleTemplateModel(), new ProductModel(), null, new CurrencyModel());
	}

	@Test
	public void testGetChangePriceBundleRuleWhenNoCurrency()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("currency can not be null");

		getBundleRuleService().getChangePriceBundleRule(new BundleTemplateModel(), new ProductModel(), new ProductModel(), null);
	}

	@Test
	public void testGetDisableRuleForBundleProductWhenNoCart()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterAbstractOrder can not be null");

		getBundleRuleService().getDisableRuleForBundleProduct(null, new ProductModel(), new BundleTemplateModel(), 1, false);
	}

	@Test
	public void testGetDisableRuleForBundleProductWhenNoProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("product can not be null");

		getBundleRuleService().getDisableRuleForBundleProduct(new CartModel(), null, new BundleTemplateModel(), 1, false);
	}

	@Test
	public void testGetDisableRuleForBundleProductWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		getBundleRuleService().getDisableRuleForBundleProduct(new CartModel(), new ProductModel(), null, 1, false);
	}

	@Test
	public void testGetChangePriceBundleRule2WhenNoBundleTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		getBundleRuleService().getChangePriceBundleRule(new CartModel(), null, new ProductModel(), 1);
	}

	@Test
	public void testGetChangePriceBundleRule2WhenNoTargetProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("targetProduct can not be null");

		getBundleRuleService().getChangePriceBundleRule(new CartModel(), new BundleTemplateModel(), null, 1);
	}

	@Test
	public void testGetChangePriceBundleRule2WhenNoCart()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterAbstractOrder can not be null");

		getBundleRuleService().getChangePriceBundleRule(null, new BundleTemplateModel(), new ProductModel(), 1);
	}

	@Test
	public void testGetChangePriceBundleRuleWithLowestPriceWhenNoProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("targetProduct can not be null");

		getBundleRuleService().getChangePriceBundleRuleWithLowestPrice(null, new CurrencyModel());
	}

	@Test
	public void testGetChangePriceBundleRuleWithLowestPriceWhenNoCurrency()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("currency can not be null");

		getBundleRuleService().getChangePriceBundleRuleWithLowestPrice(new ProductModel(), null);
	}

	@Test
	public void testGetChangePriceBundleRuleWithLowestPrice()
	{
		final CurrencyModel currency = mock(CurrencyModel.class);
		final ProductModel product = mock(ProductModel.class);

		final BundleTemplateModel bundleTemplate1 = mock(BundleTemplateModel.class);

		final List<ChangeProductPriceBundleRuleModel> priceRules = new ArrayList<ChangeProductPriceBundleRuleModel>();
		given(changeProductPriceBundleRuleDao.findBundleRulesByTargetProductAndCurrency(product, currency)).willReturn(priceRules);

		// create 1st price rule which is ok
		final ChangeProductPriceBundleRuleModel priceRule1_300USD = mock(ChangeProductPriceBundleRuleModel.class);
		given(priceRule1_300USD.getBundleTemplate()).willReturn(bundleTemplate1);
		given(priceRule1_300USD.getPrice()).willReturn(BigDecimal.valueOf(300));
		given(priceRule1_300USD.getCurrency()).willReturn(currency);
		priceRules.add(priceRule1_300USD);
		ChangeProductPriceBundleRuleModel foundPriceRule = getBundleRuleService().getChangePriceBundleRuleWithLowestPrice(product,
				currency);
		assertEquals(BigDecimal.valueOf(300), foundPriceRule.getPrice());

		// create 2nd rule with lower price but it is orphaned (not assigned to a template)
		final ChangeProductPriceBundleRuleModel priceRule2_200USD = mock(ChangeProductPriceBundleRuleModel.class);
		given(priceRule2_200USD.getPrice()).willReturn(BigDecimal.valueOf(200));
		given(priceRule2_200USD.getCurrency()).willReturn(currency);
		priceRules.add(priceRule2_200USD);
		foundPriceRule = getBundleRuleService().getChangePriceBundleRuleWithLowestPrice(product, currency);
		assertEquals(BigDecimal.valueOf(300), foundPriceRule.getPrice());

		// 2nd price is now assigned to a template
		final BundleTemplateModel bundleTemplate2 = mock(BundleTemplateModel.class);
		given(priceRule2_200USD.getBundleTemplate()).willReturn(bundleTemplate2);
		foundPriceRule = getBundleRuleService().getChangePriceBundleRuleWithLowestPrice(product, currency);
		assertEquals(BigDecimal.valueOf(200), foundPriceRule.getPrice());

		// create 3rd rule with lowest price ever which is ok
		final ChangeProductPriceBundleRuleModel priceRule3_100USD = mock(ChangeProductPriceBundleRuleModel.class);
		given(priceRule3_100USD.getBundleTemplate()).willReturn(bundleTemplate1);
		given(priceRule3_100USD.getPrice()).willReturn(BigDecimal.valueOf(100));
		given(priceRule3_100USD.getCurrency()).willReturn(currency);
		priceRules.add(priceRule3_100USD);
		foundPriceRule = getBundleRuleService().getChangePriceBundleRuleWithLowestPrice(product, currency);
		assertEquals(BigDecimal.valueOf(100), foundPriceRule.getPrice());
	}

	protected BundleRuleService getBundleRuleService()
	{
		return bundleRuleService;
	}

	public void setBundleRuleService(final BundleRuleService bundleRuleService)
	{
		this.bundleRuleService = bundleRuleService;
	}

}
