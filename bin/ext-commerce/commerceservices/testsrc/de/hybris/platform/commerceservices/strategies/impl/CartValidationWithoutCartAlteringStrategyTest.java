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
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CartValidationWithoutCartAlteringStrategyTest
{
	private static Long OUT_OF_STOCK_VALUE = Long.valueOf(0);
	private static Long LOW_STOCK_VALUE = Long.valueOf(10);
	private static Long PRODUCT_QUANTITY = Long.valueOf(20);

	private CartValidationWithoutCartAlteringStrategy strategy;
	@Mock
	private ProductService productService;
	@Mock
	private CommerceStockService commerceStockService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CartModel cart;
	@Mock
	private CartEntryModel cartEntry_0;
	@Mock
	private ProductModel product_0;
	@Mock
	private CartEntryModel cartEntry_1;
	@Mock
	private ProductModel product_1;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private PointOfServiceModel pointOfService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		strategy = new CartValidationWithoutCartAlteringStrategy();
		strategy.setProductService(productService);
		strategy.setCommerceStockService(commerceStockService);
		strategy.setBaseStoreService(baseStoreService);

		given(cartEntry_0.getProduct()).willReturn(product_0);
		given(product_0.getCode()).willReturn("0");
		given(cartEntry_1.getProduct()).willReturn(product_1);
		given(product_1.getCode()).willReturn("1");

		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
	}

	@Test
	public void testCartValidation()
	{
		final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();
		entryList.add(cartEntry_0);
		entryList.add(cartEntry_1);
		given(cart.getEntries()).willReturn(entryList);
		given(cartEntry_0.getQuantity()).willReturn(PRODUCT_QUANTITY);
		given(cartEntry_1.getQuantity()).willReturn(PRODUCT_QUANTITY);
		given(productService.getProductForCode(cartEntry_0.getProduct().getCode())).willReturn(product_0);
		given(productService.getProductForCode(cartEntry_1.getProduct().getCode())).willReturn(product_1);
		given(commerceStockService.getStockLevelForProductAndBaseStore(product_0, baseStore)).willReturn(PRODUCT_QUANTITY);
		given(cartEntry_1.getDeliveryPointOfService()).willReturn(pointOfService);
		given(commerceStockService.getStockLevelForProductAndPointOfService(product_1, pointOfService)).willReturn(
				OUT_OF_STOCK_VALUE);

		final List<CommerceCartModification> modificationList = strategy.validateCart(cart);

		Assert.assertEquals(2, modificationList.size());

		CommerceCartModification modification = modificationList.get(0);
		Assert.assertEquals(CommerceCartModificationStatus.SUCCESS, modification.getStatusCode());
		Assert.assertEquals(PRODUCT_QUANTITY.longValue(), modification.getQuantity());
		Assert.assertEquals(PRODUCT_QUANTITY.longValue(), modification.getQuantityAdded());
		Assert.assertEquals(cartEntry_0, modification.getEntry());

		modification = modificationList.get(1);
		Assert.assertEquals(CommerceCartModificationStatus.NO_STOCK, modification.getStatusCode());
		Assert.assertEquals(PRODUCT_QUANTITY.longValue(), modification.getQuantity());
		Assert.assertEquals(OUT_OF_STOCK_VALUE.longValue(), modification.getQuantityAdded());
		Assert.assertEquals(cartEntry_1, modification.getEntry());
	}

	@Test
	public void testProductUnavailable()
	{
		given(cartEntry_0.getQuantity()).willReturn(PRODUCT_QUANTITY);
		given(productService.getProductForCode(cartEntry_0.getProduct().getCode())).willThrow(
				new UnknownIdentifierException("Product is unavailable."));

		final CommerceCartModification modification = strategy.validateCartEntry(cartEntry_0);
		Assert.assertEquals(CommerceCartModificationStatus.UNAVAILABLE, modification.getStatusCode());
		Assert.assertEquals(PRODUCT_QUANTITY.longValue(), modification.getQuantity());
		Assert.assertEquals(OUT_OF_STOCK_VALUE.longValue(), modification.getQuantityAdded());
		Assert.assertEquals(cartEntry_0, modification.getEntry());
	}

	@Test
	public void testProductAvailable()
	{
		given(productService.getProductForCode(cartEntry_0.getProduct().getCode())).willReturn(product_0);
		given(commerceStockService.getStockLevelForProductAndBaseStore(product_0, baseStore)).willReturn(PRODUCT_QUANTITY);
		given(cartEntry_0.getQuantity()).willReturn(PRODUCT_QUANTITY);

		final CommerceCartModification modification = strategy.validateCartEntry(cartEntry_0);
		Assert.assertEquals(CommerceCartModificationStatus.SUCCESS, modification.getStatusCode());
		Assert.assertEquals(PRODUCT_QUANTITY.longValue(), modification.getQuantity());
		Assert.assertEquals(PRODUCT_QUANTITY.longValue(), modification.getQuantityAdded());
		Assert.assertEquals(cartEntry_0, modification.getEntry());
	}

	@Test
	public void testNoStock()
	{
		given(productService.getProductForCode(cartEntry_0.getProduct().getCode())).willReturn(product_0);
		given(commerceStockService.getStockLevelForProductAndBaseStore(product_0, baseStore)).willReturn(OUT_OF_STOCK_VALUE);
		given(cartEntry_0.getQuantity()).willReturn(PRODUCT_QUANTITY);

		final CommerceCartModification modification = strategy.validateCartEntry(cartEntry_0);
		Assert.assertEquals(CommerceCartModificationStatus.NO_STOCK, modification.getStatusCode());
		Assert.assertEquals(PRODUCT_QUANTITY.longValue(), modification.getQuantity());
		Assert.assertEquals(OUT_OF_STOCK_VALUE.longValue(), modification.getQuantityAdded());
		Assert.assertEquals(cartEntry_0, modification.getEntry());
	}

	@Test
	public void testLowStock()
	{
		given(productService.getProductForCode(cartEntry_0.getProduct().getCode())).willReturn(product_0);
		given(commerceStockService.getStockLevelForProductAndBaseStore(product_0, baseStore)).willReturn(LOW_STOCK_VALUE);
		given(cartEntry_0.getQuantity()).willReturn(PRODUCT_QUANTITY);

		final CommerceCartModification modification = strategy.validateCartEntry(cartEntry_0);
		Assert.assertEquals(CommerceCartModificationStatus.LOW_STOCK, modification.getStatusCode());
		Assert.assertEquals(PRODUCT_QUANTITY.longValue(), modification.getQuantity());
		Assert.assertEquals(LOW_STOCK_VALUE.longValue(), modification.getQuantityAdded());
		Assert.assertEquals(cartEntry_0, modification.getEntry());

	}

	@Test
	public void testNoStockForPOS()
	{
		given(productService.getProductForCode(cartEntry_0.getProduct().getCode())).willReturn(product_0);
		given(cartEntry_0.getDeliveryPointOfService()).willReturn(pointOfService);
		given(commerceStockService.getStockLevelForProductAndPointOfService(product_0, pointOfService)).willReturn(
				OUT_OF_STOCK_VALUE);
		given(cartEntry_0.getQuantity()).willReturn(PRODUCT_QUANTITY);

		final CommerceCartModification modification = strategy.validateCartEntry(cartEntry_0);
		Assert.assertEquals(CommerceCartModificationStatus.NO_STOCK, modification.getStatusCode());
		Assert.assertEquals(PRODUCT_QUANTITY.longValue(), modification.getQuantity());
		Assert.assertEquals(OUT_OF_STOCK_VALUE.longValue(), modification.getQuantityAdded());
		Assert.assertEquals(cartEntry_0, modification.getEntry());
	}

	@Test
	public void testLowStockForPOS()
	{
		given(productService.getProductForCode(cartEntry_0.getProduct().getCode())).willReturn(product_0);
		given(cartEntry_0.getDeliveryPointOfService()).willReturn(pointOfService);
		given(commerceStockService.getStockLevelForProductAndPointOfService(product_0, pointOfService)).willReturn(LOW_STOCK_VALUE);
		given(cartEntry_0.getQuantity()).willReturn(PRODUCT_QUANTITY);

		final CommerceCartModification modification = strategy.validateCartEntry(cartEntry_0);
		Assert.assertEquals(CommerceCartModificationStatus.LOW_STOCK, modification.getStatusCode());
		Assert.assertEquals(PRODUCT_QUANTITY.longValue(), modification.getQuantity());
		Assert.assertEquals(LOW_STOCK_VALUE.longValue(), modification.getQuantityAdded());
		Assert.assertEquals(cartEntry_0, modification.getEntry());
	}

}