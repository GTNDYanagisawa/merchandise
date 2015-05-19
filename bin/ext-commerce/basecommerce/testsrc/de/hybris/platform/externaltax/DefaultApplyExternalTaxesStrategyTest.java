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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultApplyExternalTaxesStrategyTest extends ServicelayerBaseTest
{
	@Resource(mappedName = "defaultApplyExternalTaxesStrategy")
	ApplyExternalTaxesStrategy applyExternalTaxesStrategy;

	@Resource
	ModelService modelService;

	@Resource
	CommonI18NService commonI18NService;

	@Resource
	UserService userService;

	CurrencyModel eur, usd;
	ProductModel product;
	UnitModel unit;

	@Before
	public void setUp()
	{
		try
		{
			eur = commonI18NService.getCurrency("EUR");
		}
		catch (final UnknownIdentifierException e)
		{
			eur = new CurrencyModel();
			eur.setIsocode("EUR");
			eur.setDigits(Integer.valueOf(2));
			modelService.save(eur);
		}
		try
		{
			usd = commonI18NService.getCurrency("USD");
		}
		catch (final UnknownIdentifierException e)
		{
			usd = new CurrencyModel();
			usd.setIsocode("USD");
			usd.setDigits(Integer.valueOf(2));
			modelService.save(usd);
		}

		final CatalogModel cat = new CatalogModel();
		cat.setId("cat");
		final CatalogVersionModel ver = new CatalogVersionModel();
		ver.setCatalog(cat);
		ver.setVersion("online");
		product = new ProductModel();
		product.setCatalogVersion(ver);
		product.setCode("product");
		unit = new UnitModel();
		unit.setCode("unit");
		unit.setUnitType("unittype");
		unit.setConversion(Double.valueOf(1.0));

		modelService.saveAll(cat, ver, unit, product);
	}

	@Test
	public void testEmptyDoc()
	{
		CartModel cart = prepareCart(3, usd);
		// set some dummy data to check whether it's cleared correctly
		cart.setTotalTax(Double.valueOf(123.45));
		cart.getEntries().get(1).setTaxValues(Arrays.asList(new TaxValue("DUMMY", 12, true, 99.99, "USD")));
		modelService.save(cart);

		final ExternalTaxDocument document = new ExternalTaxDocument();

		applyExternalTaxesStrategy.applyExternalTaxes(cart, document);
		modelService.save(cart);
		final PK cartPK = cart.getPk();
		modelService.detach(cart);
		cart = modelService.get(cartPK);

		assertEquals(Double.valueOf(0.0), cart.getTotalTax());
		assertEquals(Collections.EMPTY_LIST, cart.getTotalTaxValues());
		for (final AbstractOrderEntryModel e : cart.getEntries())
		{
			assertEquals(Collections.EMPTY_LIST, e.getTaxValues());
		}
	}

	@Test
	public void testLegalDoc()
	{
		CartModel cart = prepareCart(2, usd);
		modelService.save(cart);

		final ExternalTaxDocument document = new ExternalTaxDocument();
		final TaxValue tv_0_0 = new TaxValue("STA", 10, true, 9.99, "usd");
		final TaxValue tv_0_1 = new TaxValue("GOV", 20, true, 19.99, "usd");
		final TaxValue tv_0_2 = new TaxValue("LOC", 30, true, 29.99, "usd");
		document.setTaxesForOrderEntry(0, tv_0_0, tv_0_1, tv_0_2);

		final TaxValue tv_1_0 = new TaxValue("STA", 3, true, 12.34, "usd");
		final TaxValue tv_1_1 = new TaxValue("GOV", 4, true, 45.98, "usd");
		document.setTaxesForOrderEntry(1, tv_1_0, tv_1_1);

		final TaxValue tv_s_0 = new TaxValue("STA", 12, true, 0.99, "usd");
		final TaxValue tv_s_1 = new TaxValue("GOV", 34, true, 0.01, "usd");
		document.setShippingCostTaxes(tv_s_0, tv_s_1);

		applyExternalTaxesStrategy.applyExternalTaxes(cart, document);
		modelService.save(cart);
		final PK cartPK = cart.getPk();
		modelService.detach(cart);
		cart = modelService.get(cartPK);

		assertEquals(Double.valueOf(119.29), cart.getTotalTax());
		assertEquals(Arrays.asList(tv_s_0, tv_s_1), cart.getTotalTaxValues());
		assertEquals(Arrays.asList(tv_0_0, tv_0_1, tv_0_2), cart.getEntries().get(0).getTaxValues());
		assertEquals(Arrays.asList(tv_1_0, tv_1_1), cart.getEntries().get(1).getTaxValues());
	}


	CartModel prepareCart(final int entriesCount, final CurrencyModel curr)
	{
		final CartModel cart = new CartModel();
		cart.setCurrency(curr);
		cart.setDate(new Date());
		cart.setUser(userService.getCurrentUser());
		cart.setNet(Boolean.TRUE);

		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		for (int i = 0; i < entriesCount; i++)
		{
			final CartEntryModel entry = new CartEntryModel();
			entry.setProduct(product);
			entry.setQuantity(Long.valueOf(1));
			entry.setUnit(unit);
			entry.setOrder(cart);
			entry.setEntryNumber(Integer.valueOf(i));
			entries.add(entry);
		}
		cart.setEntries(entries);

		return cart;
	}

}
