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
package de.hybris.platform.commerceservices.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultCartValidationStrategyTest
{
	private DefaultCartValidationStrategy strategy;
	@Mock
	private ModelService modelService;
	@Mock
	private CartService cartService;
	@Mock
	private ProductService productService;
	@Mock
	private CommerceStockService commerceStockService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CartModel cart;
	@Mock
	private CartEntryModel cartEntry;
	@Mock
	private ProductModel product;
	@Mock
	private BaseStoreModel store;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		strategy = new DefaultCartValidationStrategy();
		strategy.setModelService(modelService);
		strategy.setCartService(cartService);
		strategy.setProductService(productService);
		strategy.setCommerceStockService(commerceStockService);
		strategy.setBaseStoreService(baseStoreService);

		given(cartEntry.getProduct()).willReturn(product);
		given(product.getCode()).willReturn("1");

		given(baseStoreService.getCurrentBaseStore()).willReturn(store);
		given(cartService.getEntriesForProduct(cart, product)).willReturn(Collections.<CartEntryModel> emptyList());
	}

	@Test
	public void testUnavailableStock()
	{
		given(productService.getProductForCode(cartEntry.getProduct().getCode())).willThrow(
				new UnknownIdentifierException("Product is unavailable."));

		final CommerceCartModification modification = strategy.validateCartEntry(cart, cartEntry);
		Assert.assertEquals(CommerceCartModificationStatus.UNAVAILABLE, modification.getStatusCode());
	}

	@Test
	public void testNoStock()
	{
		given(productService.getProductForCode(cartEntry.getProduct().getCode())).willReturn(product);
		given(commerceStockService.getStockLevelForProductAndBaseStore(product, store)).willReturn(Long.valueOf(0));
		given(cartEntry.getQuantity()).willReturn(Long.valueOf(20));

		final CommerceCartModification modification = strategy.validateCartEntry(cart, cartEntry);
		Assert.assertEquals(CommerceCartModificationStatus.NO_STOCK, modification.getStatusCode());
	}

	@Test
	public void testLowStock()
	{
		given(productService.getProductForCode(cartEntry.getProduct().getCode())).willReturn(product);
		given(commerceStockService.getStockLevelForProductAndBaseStore(product, store)).willReturn(Long.valueOf(10));
		given(cartEntry.getQuantity()).willReturn(Long.valueOf(20));

		final CommerceCartModification modification = strategy.validateCartEntry(cart, cartEntry);
		Assert.assertEquals(CommerceCartModificationStatus.LOW_STOCK, modification.getStatusCode());
	}
}