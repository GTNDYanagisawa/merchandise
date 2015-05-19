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
package de.hybris.platform.commerceservices.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.*;
import de.hybris.platform.commerceservices.order.dao.CartEntryDao;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderParameter;
import de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService;
import de.hybris.platform.commerceservices.stock.strategies.CommerceAvailabilityCalculationStrategy;
import de.hybris.platform.commerceservices.stock.strategies.impl.DefaultCommerceAvailabilityCalculationStrategy;
import de.hybris.platform.commerceservices.stock.strategies.impl.DefaultWarehouseSelectionStrategy;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.variants.model.VariantTypeModel;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


/**
 * JUnit test suite for {@link DefaultCommerceCartService}
 */
@UnitTest
public class DefaultCommerceCartServiceTest
{
	private static final int STORE_ONE_STOCK_QUANTITY = 123;
	private static final int STORE_TWO_STOCK_QUANTITY = 321;
	private static final int NUMBER_OF_ITEMS_TO_CART = 5;

	@InjectMocks
	private final DefaultCommerceCartService commerceCartService = new DefaultCommerceCartService();
	@InjectMocks
	@Spy
	private final AbstractCommerceAddToCartStrategy addToCartStrategy = new DefaultCommerceAddToCartStrategy();
	@InjectMocks
	@Spy
	private final DefaultCommerceUpdateCartEntryStrategy updateCartEntryStrategy = new DefaultCommerceUpdateCartEntryStrategy();
	@InjectMocks
	@Spy
	private final DefaultCommerceCartRestorationStrategy cartRestorationStrategy = new DefaultCommerceCartRestorationStrategy();
	@InjectMocks
	@Spy
	private final DefaultCommerceCartSplitStrategy cartSplitStrategy = new DefaultCommerceCartSplitStrategy();
	@InjectMocks
	@Spy
	private final DefaultCommerceRemoveEntriesStrategy commerceRemoveEntriesStrategy = new DefaultCommerceRemoveEntriesStrategy();
	// The mocks below are injected into defaultCommerceStockService
	@InjectMocks
	@Spy
	private final DefaultCommerceStockService defaultCommerceStockService = new DefaultCommerceStockService();
	@SuppressWarnings("unused")
	@Spy
	private final CommerceAvailabilityCalculationStrategy commerceAvailabilityCalculationStrategy = new DefaultCommerceAvailabilityCalculationStrategy(); //NOPMD
	@SuppressWarnings("unused")
	@Spy
	private final OrderEntryModifiableChecker orderEntryModifiableChecker = new OrderEntryModifiableChecker();
	private ProductModel productModel;
	private BaseStoreModel baseStoreModel;
	private BaseSiteModel baseSiteModel;
	private PromotionGroupModel promotionGroupModel;
	@Mock
	private CartModel oldCartForDelivery, oldCartForWeb, newCart, cartModel;
	@Mock
	private UnitModel unitModel;
	@Mock
	private AbstractOrderEntryModel orderEntryModelForDelivery, orderEntryModelForWeb, orderEntryModelForWeb2;
	@Mock
	private PointOfServiceModel storeOne, storeTwo;
	@Mock
	private WarehouseModel warehouseOne, warehouseTwo;
	@Mock
	private StockLevelModel stockLevelModelOne, stockLevelModelTwo;
	@Mock
	private ProductService productService;
	@Mock
	private CartService cartService;
	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy; //NOPMD
	@Mock
	private ModelService modelService; //NOPMD
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private TimeService timeService;
	@Mock
	private CartEntryDao cartEntryDao;
	@Mock
	private CartFactory cartFactory;
	@Mock
	private StockService stockService;
	@Mock
	private GuidKeyGenerator guidKeyGenerator;
	@Mock
	private SessionService sessionService;
	@Mock
	private DefaultWarehouseSelectionStrategy warehouseSelectionStrategy; //NOPMD
	@Mock
	private CommerceCartEstimateTaxesStrategy commerceCartEstimateTaxesStrategy;
	@Mock
	private CommerceCartHashCalculationStrategy commerceCartHashCalculationStrategy;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		productModel = mock(ProductModel.class);
		baseStoreModel = mock(BaseStoreModel.class);
		promotionGroupModel = mock(PromotionGroupModel.class);
		baseSiteModel = mock(BaseSiteModel.class);

		given(timeService.getCurrentTime()).willReturn(new Date());

		stubOrder();
		stubStores();
		stubWarehouseStockLevels();
		stubCartModel();
		cartRestorationStrategy.setCartValidityPeriod(0);
		cartRestorationStrategy.setGuidKeyGenerator(guidKeyGenerator);
		commerceCartService.setCommerceAddToCartStrategy(addToCartStrategy);
		commerceCartService.setCommerceUpdateCartEntryStrategy(updateCartEntryStrategy);
		commerceCartService.setCommerceCartRestorationStrategy(cartRestorationStrategy);
		commerceCartService.setCommerceCartSplitStrategy(cartSplitStrategy);
		commerceCartService.setCommerceRemoveEntriesStrategy(commerceRemoveEntriesStrategy);
		given(guidKeyGenerator.generate()).willReturn(String.valueOf(System.currentTimeMillis()));

		addToCartStrategy.setConfigurationService(configurationService);

	}

	private void stubOrder()
	{
		given(orderEntryModelForDelivery.getProduct()).willReturn(productModel);
		given(orderEntryModelForDelivery.getDeliveryPointOfService()).willReturn(storeOne);
		given(orderEntryModelForDelivery.getQuantity()).willReturn(Long.valueOf(STORE_ONE_STOCK_QUANTITY));
		given(orderEntryModelForDelivery.getUnit()).willReturn(unitModel);

		given(orderEntryModelForWeb.getProduct()).willReturn(productModel);
		given(orderEntryModelForWeb.getQuantity()).willReturn(Long.valueOf(STORE_ONE_STOCK_QUANTITY));
		given(orderEntryModelForWeb.getUnit()).willReturn(unitModel);

		given(orderEntryModelForWeb2.getProduct()).willReturn(productModel);
		given(orderEntryModelForWeb2.getQuantity()).willReturn(Long.valueOf(STORE_ONE_STOCK_QUANTITY));
		given(orderEntryModelForWeb2.getUnit()).willReturn(unitModel);
	}

	private void stubStores()
	{
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSiteModel);
		given(storeOne.getBaseStore()).willReturn(baseStoreModel);
		given(storeTwo.getBaseStore()).willReturn(baseStoreModel);

		given(baseSiteModel.getDefaultPromotionGroup()).willReturn(promotionGroupModel);
		given(baseStoreModel.getWarehouses()).willReturn(Collections.singletonList(warehouseOne));
	}

	private void stubWarehouseStockLevels()
	{
		given(storeOne.getWarehouses()).willReturn(Collections.singletonList(warehouseOne));
		given(storeTwo.getWarehouses()).willReturn(Collections.singletonList(warehouseTwo));
		given(stockService.getStockLevels(productModel, storeOne.getWarehouses())).willReturn(
				Collections.singletonList(stockLevelModelOne));
		given(stockService.getStockLevels(productModel, storeTwo.getWarehouses())).willReturn(
				Collections.singletonList(stockLevelModelTwo));
	}

	private void stubCartModel()
	{
		given(oldCartForDelivery.getSite()).willReturn(baseSiteModel);
		given(oldCartForDelivery.getEntries()).willReturn(Collections.singletonList(orderEntryModelForDelivery));
		given(oldCartForWeb.getSite()).willReturn(baseSiteModel);
		given(oldCartForWeb.getEntries()).willReturn(Collections.singletonList(orderEntryModelForWeb));
		given(newCart.getSite()).willReturn(baseSiteModel);
		given(cartFactory.createCart()).willReturn(newCart);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddToCartWhenCartIsNull() throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(null);
		parameter.setProduct(productModel);
		parameter.setQuantity(1);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);

		commerceCartService.addToCart(parameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddToCartWhenProductIsNull() throws CommerceCartModificationException
	{

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(null);
		parameter.setQuantity(1);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);
		commerceCartService.addToCart(parameter);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testAddToCartWhenProductIsBase() throws CommerceCartModificationException
	{
		given(productModel.getVariantType()).willReturn(new VariantTypeModel());

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setQuantity(1);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);

		commerceCartService.addToCart(parameter);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testAddToCartWhenQuantityIsZero() throws CommerceCartModificationException
	{
		given(productModel.getVariantType()).willReturn(null);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setQuantity(0);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);
		commerceCartService.addToCart(parameter);
	}

	@SuppressWarnings("deprecation")
	@Test(expected = CommerceCartModificationException.class)
	public void testAddToCartWhenNoUnitFound() throws CommerceCartModificationException
	{
		given(productService.getOrderableUnit(productModel)).willThrow(new ModelNotFoundException("no unit"));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setQuantity(1);
		parameter.setUnit(null);
		parameter.setCreateNewEntry(false);
		commerceCartService.addToCart(parameter);
		Assert.fail("InvalidCartException should be thrown");
	}

	@Test
	public void testAddToCartLowerStock() throws CommerceCartModificationException, CalculationException
	{
		given(defaultCommerceStockService.getStockLevelForProductAndBaseStore(productModel, baseStoreModel)).willReturn(
				Long.valueOf(10));
		given(productModel.getMaxOrderQuantity()).willReturn(null);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setQuantity(20);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);


		final long actualAdded = commerceCartService.addToCart(parameter).getQuantityAdded();
		Assert.assertEquals(10, actualAdded);
	}

	@Test
	public void testAddToCart() throws CommerceCartModificationException, CalculationException
	{
		given(defaultCommerceStockService.getStockLevelForProductAndBaseStore(productModel, baseStoreModel)).willReturn(
				Long.valueOf(10));
		given(productModel.getMaxOrderQuantity()).willReturn(null);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setQuantity(1);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);

		final long actualAdded = commerceCartService.addToCart(parameter).getQuantityAdded();
		Assert.assertEquals(1, actualAdded);
	}


	@Test
	public void testAddToCartWithMethodHooksEnabled() throws CommerceCartModificationException, CalculationException
	{
		given(defaultCommerceStockService.getStockLevelForProductAndBaseStore(productModel, baseStoreModel)).willReturn(
				Long.valueOf(10));
		given(productModel.getMaxOrderQuantity()).willReturn(null);

		final CommerceAddToCartMethodHook commerceAddToCartMethodHook = Mockito.mock(CommerceAddToCartMethodHook.class);
		addToCartStrategy.setCommerceAddToCartMethodHooks(Collections
				.<CommerceAddToCartMethodHook> singletonList(commerceAddToCartMethodHook));


		given(configurationService.getConfiguration()).willReturn(configuration);
		given(Boolean.valueOf(configuration.getBoolean("commerceservices.commerceaddtocartmethodhook.enabled"))).willReturn(
				Boolean.TRUE);


		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setQuantity(1);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);

		final long actualAdded = commerceCartService.addToCart(parameter).getQuantityAdded();
		Assert.assertEquals(1, actualAdded);
		verify(commerceAddToCartMethodHook, times(1)).beforeAddToCart(parameter);
		verify(commerceAddToCartMethodHook, times(1)).afterAddToCart(any(CommerceCartParameter.class),
				any(CommerceCartModification.class));

	}

	@Test
	public void testAddToCartWithMethodHooksDisabled() throws CommerceCartModificationException, CalculationException
	{
		given(defaultCommerceStockService.getStockLevelForProductAndBaseStore(productModel, baseStoreModel)).willReturn(
				Long.valueOf(10));
		given(productModel.getMaxOrderQuantity()).willReturn(null);

		final CommerceAddToCartMethodHook commerceAddToCartMethodHook = Mockito.mock(CommerceAddToCartMethodHook.class);
		addToCartStrategy.setCommerceAddToCartMethodHooks(Collections
				.<CommerceAddToCartMethodHook> singletonList(commerceAddToCartMethodHook));


		given(configurationService.getConfiguration()).willReturn(configuration);
		given(Boolean.valueOf(configuration.getBoolean("commerceservices.commerceaddtocartmethodhook.enabled"))).willReturn(
				Boolean.FALSE);


		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(false);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setQuantity(1);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);

		final long actualAdded = commerceCartService.addToCart(parameter).getQuantityAdded();
		Assert.assertEquals(1, actualAdded);
		verify(commerceAddToCartMethodHook, never()).beforeAddToCart(parameter);
		verify(commerceAddToCartMethodHook, never()).afterAddToCart(any(CommerceCartParameter.class),
				any(CommerceCartModification.class));

	}

	@Test
	public void testAddToCartWithMethodHooksWithPropertyDisabled() throws CommerceCartModificationException, CalculationException
	{
		given(defaultCommerceStockService.getStockLevelForProductAndBaseStore(productModel, baseStoreModel)).willReturn(
				Long.valueOf(10));
		given(productModel.getMaxOrderQuantity()).willReturn(null);

		final CommerceAddToCartMethodHook commerceAddToCartMethodHook = Mockito.mock(CommerceAddToCartMethodHook.class);
		addToCartStrategy.setCommerceAddToCartMethodHooks(Collections
				.<CommerceAddToCartMethodHook> singletonList(commerceAddToCartMethodHook));


		given(configurationService.getConfiguration()).willReturn(configuration);
		given(Boolean.valueOf(configuration.getBoolean("commerceservices.commerceaddtocartmethodhook.enabled"))).willReturn(
				Boolean.FALSE);


		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setQuantity(1);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);

		final long actualAdded = commerceCartService.addToCart(parameter).getQuantityAdded();
		Assert.assertEquals(1, actualAdded);
		verify(commerceAddToCartMethodHook, never()).beforeAddToCart(parameter);
		verify(commerceAddToCartMethodHook, never()).afterAddToCart(any(CommerceCartParameter.class),
				any(CommerceCartModification.class));

	}

	@Test
	public void testAddToCartWhenNoCartWasPassed() throws CommerceCartModificationException, CalculationException
	{
		given(cartService.getSessionCart()).willReturn(cartModel);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setQuantity(1);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);
		commerceCartService.addToCart(parameter);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateCartWhenCartIsNull()
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(null);
		commerceCartService.calculateCart(parameter);
	}

	@Test
	public void testCalculateCart() throws CalculationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		commerceCartService.calculateCart(parameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRecalculateCartWhenCartIsNull() throws CalculationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(null);
		commerceCartService.recalculateCart(parameter);
	}

	@Test
	public void testRecalculateCart() throws CalculationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		commerceCartService.recalculateCart(parameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveAllEntriesWhenCartIsNull()
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(null);
		commerceCartService.removeAllEntries(parameter);
	}

	@Test
	public void testRemoveAllEntries()
	{
		final AbstractOrderEntryModel entryModel = mock(AbstractOrderEntryModel.class);
		final List<AbstractOrderEntryModel> entries = Collections.singletonList(entryModel);

		given(cartModel.getEntries()).willReturn(entries);
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		commerceCartService.removeAllEntries(parameter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateQuantityWhenCartIsNull() throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(null);
		parameter.setEntryNumber(1);
		parameter.setQuantity(1);
		commerceCartService.updateQuantityForCartEntry(parameter);
	}

	@Test
	public void testGetEntryForNumberEmptyEntries()
	{
		given(cartModel.getEntries()).willReturn(null);
		final AbstractOrderEntryModel entry = addToCartStrategy.getEntryForNumber(cartModel, 2);
		Assert.assertNull(entry);
	}

	@Test
	public void testCheckCartLevelWithEntries()
	{
		final CartEntryModel entryModel = mock(CartEntryModel.class);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartService.getEntriesForProduct(cartModel, productModel)).willReturn(Collections.singletonList(entryModel));
		given(entryModel.getQuantity()).willReturn(Long.valueOf(12));
		final long level = addToCartStrategy.checkCartLevel(productModel, cartModel, null);
		Assert.assertEquals(12, level);
	}

	@Test
	public void testCheckStockLevelEmptyWarehouse()
	{
		given(Boolean.valueOf(defaultCommerceStockService.isStockSystemEnabled(Mockito.any(BaseStoreModel.class)))).willReturn(
				Boolean.FALSE);
		final long stock = addToCartStrategy.getAvailableStockLevel(productModel, null);
		Assert.assertEquals(9999, stock);
	}

	@Test
	public void testCanModifyCartEntry() throws CommerceCartModificationException
	{
		final int FIRST = 1;

		final AbstractOrderEntryModel entryOne = Mockito.mock(AbstractOrderEntryModel.class);

		final List<AbstractOrderEntryModel> entries = Arrays.asList(entryOne);

		given(cartModel.getEntries()).willReturn(entries);
		given(entryOne.getEntryNumber()).willReturn(Integer.valueOf(FIRST));
		given(entryOne.getProduct()).willReturn(productModel);

		Mockito.doReturn(Boolean.TRUE).when(orderEntryModifiableChecker).canModify(Mockito.any(AbstractOrderEntryModel.class));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setEntryNumber(FIRST);
		parameter.setQuantity(100);
		Assert.assertNotNull(commerceCartService.updateQuantityForCartEntry(parameter));
	}

	@Test
	public void testCanNotModifyCartEntry()
	{
		final int FIRST = 1;
		final AbstractOrderEntryModel entryOne = Mockito.mock(AbstractOrderEntryModel.class);

		final List<AbstractOrderEntryModel> entries = Arrays.asList(entryOne);

		given(cartModel.getEntries()).willReturn(entries);
		given(entryOne.getEntryNumber()).willReturn(Integer.valueOf(FIRST));

		Mockito.doReturn(Boolean.FALSE).when(orderEntryModifiableChecker).canModify(Mockito.any(AbstractOrderEntryModel.class));

		try
		{
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			parameter.setEntryNumber(FIRST);
			parameter.setQuantity(100);
			commerceCartService.updateQuantityForCartEntry(parameter);
			Assert.fail(" update should fail if the entry is marked as a give away");
		}
		catch (final CommerceCartModificationException e)
		{
			Assert.assertEquals("Entry is not updatable", e.getMessage());
			//
		}
	}

	@Test
	public void testGetAvailableStockLevelFromPos()
	{
		// Store One is supplied by Warehouse One which has 123 of product X stored
		// Store Two is supplied by Warehouse Two which has 321 of product X stored
		given(Integer.valueOf(stockLevelModelOne.getAvailable())).willReturn(Integer.valueOf(STORE_ONE_STOCK_QUANTITY));
		given(Integer.valueOf(stockLevelModelTwo.getAvailable())).willReturn(Integer.valueOf(STORE_TWO_STOCK_QUANTITY));

		// Test to find the stock level of the same item of different quantities in different stores of the same store brand.
		assertEquals("Unexpected stock quantity for the point of service: storeOne", STORE_ONE_STOCK_QUANTITY,
				addToCartStrategy.getAvailableStockLevel(productModel, storeOne));
		assertEquals("Unexpected stock quantity for the point of service: storeTwo", STORE_TWO_STOCK_QUANTITY,
				addToCartStrategy.getAvailableStockLevel(productModel, storeTwo));
	}

	@Test
	public void testCartHasBeenRecentlyUpdated() throws CommerceCartRestorationException
	{
		// Ensure that cartModel has been recently updated
		given(oldCartForDelivery.getModifiedtime()).willReturn(new Date(1L));
		given(timeService.getCurrentTime()).willReturn(new Date(0L));

		assertFalse("Old cart should not equal the new cart", oldCartForDelivery.equals(newCart));
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(oldCartForDelivery);
		assertEquals("No modifications should be present", 0, commerceCartService.restoreCart(parameter).getModifications().size());
	}

	@Test
	public void testCartHasNotBeenRecentlyUpdated() throws CommerceCartRestorationException
	{
		// Ensure that cartModel has not been recently updated
		given(oldCartForDelivery.getModifiedtime()).willReturn(new Date(0L));
		given(timeService.getCurrentTime()).willReturn(new Date(1L));

		// Store has product in stock
		given(Integer.valueOf(stockLevelModelOne.getAvailable())).willReturn(Integer.valueOf(STORE_ONE_STOCK_QUANTITY));

		assertFalse("Old cart should not equal the new cart", oldCartForDelivery.equals(newCart));
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(oldCartForDelivery);
		assertEquals("One modification should be present", 1, commerceCartService.restoreCart(parameter).getModifications().size());
	}

	@Test
	public void testAddToCartStockFromPos() throws CommerceCartModificationException
	{
		// Add # of product X on cart
		// User returns, # <= stock available from pos
		// Expect all items in cart to be restored
		given(productModel.getMaxOrderQuantity()).willReturn(null);
		given(Integer.valueOf(stockLevelModelOne.getAvailable())).willReturn(Integer.valueOf(NUMBER_OF_ITEMS_TO_CART));
		given(Integer.valueOf(stockLevelModelTwo.getAvailable())).willReturn(Integer.valueOf(NUMBER_OF_ITEMS_TO_CART));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setPointOfService(storeOne);
		parameter.setQuantity(NUMBER_OF_ITEMS_TO_CART);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);

		final CommerceCartModification modification = commerceCartService.addToCart(parameter);

		assertEquals("SUCCESS status expected", CommerceCartModificationStatus.SUCCESS, modification.getStatusCode());
	}

	@Test
	public void testAddToCartForPosPartialStockFromPos() throws CommerceCartModificationException
	{
		// Add # of product X on cart
		// User returns, 0 < stock available from pos < #
		// Expect stock available from pos to be restored
		// Inform user that only some of product X was restored
		given(productModel.getMaxOrderQuantity()).willReturn(null);
		given(Integer.valueOf(stockLevelModelOne.getAvailable())).willReturn(Integer.valueOf(NUMBER_OF_ITEMS_TO_CART - 2));
		given(Integer.valueOf(stockLevelModelTwo.getAvailable())).willReturn(Integer.valueOf(NUMBER_OF_ITEMS_TO_CART));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setPointOfService(storeOne);
		parameter.setQuantity(NUMBER_OF_ITEMS_TO_CART);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);
		final CommerceCartModification modification = commerceCartService.addToCart(parameter);

		assertEquals("LOW_STOCK status expected", CommerceCartModificationStatus.LOW_STOCK, modification.getStatusCode());
		assertEquals("Unexpected amount added", modification.getQuantityAdded(), NUMBER_OF_ITEMS_TO_CART - 2);
	}

	@Test
	public void testAddToCartForPosNoStockFromPos() throws CommerceCartModificationException
	{
		// TODO: Need to pass this test ~JW 10/18/2012
		// Add # of product X on cart
		// User returns, 0 == stock available from pos, # <= stock available from web
		// Inform user that product X is no longer available from pos 

		// Out of stock for 1st pos, in stock for 2nd pos
		given(productModel.getMaxOrderQuantity()).willReturn(null);
		given(Integer.valueOf(stockLevelModelOne.getAvailable())).willReturn(Integer.valueOf(0));
		given(Integer.valueOf(stockLevelModelTwo.getAvailable())).willReturn(Integer.valueOf(NUMBER_OF_ITEMS_TO_CART));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setPointOfService(storeOne);
		parameter.setQuantity(NUMBER_OF_ITEMS_TO_CART);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);

		final CommerceCartModification modification = commerceCartService.addToCart(parameter);

		assertEquals("NO_STOCK status expected", CommerceCartModificationStatus.NO_STOCK, modification.getStatusCode());
		assertEquals("Unexpected amount added", modification.getQuantityAdded(), 0);
	}

	@Test
	public void testAddToCartForPosProductNoLongerOnShelf() throws CommerceCartModificationException
	{
		// Add # of product X on cart
		// User returns, product X is no longer on shelf
		// Expect no items to be restored
		// Do not display any message to the user

		// Mock a product that is not in the system
		given(productService.getProductForCode(productModel.getCode())).willThrow(
				new UnknownIdentifierException("Product not found"));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setPointOfService(storeOne);
		parameter.setQuantity(NUMBER_OF_ITEMS_TO_CART);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);

		final CommerceCartModification modification = commerceCartService.addToCart(parameter);
		assertEquals("UNAVAILABLE status expected", CommerceCartModificationStatus.UNAVAILABLE, modification.getStatusCode());
	}

	@Test
	public void testUpdateQuantityForPickupCartEntry() throws CommerceCartModificationException
	{
		//Pass the order entry number and quantity
		//Check it has pointOfService information. If "yes" then update the quantity for that point of service stock level
		final CartModel cartModel = mock(CartModel.class);
		final CartModel cartModelWithoutPosEntries = mock(CartModel.class);
		final ProductModel productModel1 = mock(ProductModel.class);

		final CartEntryModel orderEntryModel1 = mock(CartEntryModel.class);
		final PointOfServiceModel store1 = mock(PointOfServiceModel.class);

		given(productModel1.getMaxOrderQuantity()).willReturn(null);

		given(orderEntryModel1.getDeliveryPointOfService()).willReturn(store1);
		given(orderEntryModel1.getEntryNumber()).willReturn(Integer.valueOf(1));
		given(orderEntryModel1.getProduct()).willReturn(productModel1);

		final CartEntryModel orderEntryModel2 = mock(CartEntryModel.class);
		given(orderEntryModel2.getEntryNumber()).willReturn(Integer.valueOf(2));
		given(orderEntryModel2.getProduct()).willReturn(productModel1);

		given(cartModel.getEntries()).willReturn(Collections.singletonList((AbstractOrderEntryModel) orderEntryModel1));
		given(cartModelWithoutPosEntries.getEntries()).willReturn(
				Collections.singletonList((AbstractOrderEntryModel) orderEntryModel2));

		given(orderEntryModel1.getGiveAway()).willReturn(Boolean.FALSE);
		given(orderEntryModel2.getGiveAway()).willReturn(Boolean.FALSE);

		given(defaultCommerceStockService.getStockLevelForProductAndPointOfService(productModel1, store1)).willReturn(
				Long.valueOf(10));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setEntryNumber(1);
		parameter.setQuantity(1000);
		CommerceCartModification commerceCartModification = commerceCartService.updateQuantityForCartEntry(parameter);
		assertEquals(10, commerceCartModification.getQuantityAdded());

		parameter.setQuantity(5);
		commerceCartModification = commerceCartService.updateQuantityForCartEntry(parameter);
		assertEquals(5, commerceCartModification.getQuantityAdded());
	}


	@Test
	public void testUpdatePointOfServiceForCartEntry() throws CommerceCartModificationException
	{
		final CartModel cartModel = mock(CartModel.class);
		final ProductModel productModel1 = mock(ProductModel.class);

		final CartEntryModel cartEntryModel1 = mock(CartEntryModel.class);

		final PointOfServiceModel pointOfServiceModel1 = mock(PointOfServiceModel.class);
		given(pointOfServiceModel1.getName()).willReturn("testStore1-PointOfService");

		given(productModel1.getMaxOrderQuantity()).willReturn(null);

		given(cartEntryModel1.getDeliveryPointOfService()).willReturn(pointOfServiceModel1);
		given(cartEntryModel1.getEntryNumber()).willReturn(Integer.valueOf(0));
		given(cartEntryModel1.getQuantity()).willReturn(Long.valueOf(2));
		given(cartEntryModel1.getProduct()).willReturn(productModel1);

		final CartEntryModel cartEntryModel2 = mock(CartEntryModel.class);

		final PointOfServiceModel pointOfServiceModel2 = mock(PointOfServiceModel.class);
		given(pointOfServiceModel2.getName()).willReturn("testStore2-PointOfService");

		given(cartEntryModel2.getDeliveryPointOfService()).willReturn(pointOfServiceModel2);
		given(cartEntryModel2.getEntryNumber()).willReturn(Integer.valueOf(1));
		given(cartEntryModel2.getQuantity()).willReturn(Long.valueOf(3));
		given(cartEntryModel2.getProduct()).willReturn(productModel1);

		given(cartEntryModel1.getGiveAway()).willReturn(Boolean.FALSE);
		given(cartEntryModel2.getGiveAway()).willReturn(Boolean.FALSE);

		final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();
		entryList.add(cartEntryModel1);
		entryList.add(cartEntryModel2);

		given(defaultCommerceStockService.getStockLevelForProductAndPointOfService(productModel1, pointOfServiceModel1))
				.willReturn(Long.valueOf(5));
		given(defaultCommerceStockService.getStockLevelForProductAndPointOfService(productModel1, pointOfServiceModel2))
				.willReturn(Long.valueOf(5));

		given(cartModel.getEntries()).willReturn(entryList);

		given(cartEntryDao.findEntriesByProductAndPointOfService(cartModel, productModel1, pointOfServiceModel1)).willReturn(
				Collections.singletonList(cartEntryModel1));
		given(cartEntryDao.findEntriesByProductAndPointOfService(cartModel, productModel1, pointOfServiceModel2)).willReturn(
				Collections.singletonList(cartEntryModel2));

		given(addToCartStrategy.getEntryForProductAndPointOfService(cartModel, productModel1, pointOfServiceModel1)).willReturn(
				Integer.valueOf(0));
		given(addToCartStrategy.getEntryForProductAndPointOfService(cartModel, productModel1, pointOfServiceModel2)).willReturn(
				Integer.valueOf(1));

		//Update the POS for the second entry with POS1 so that it gets merged with existing entry
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setEntryNumber(1L);
		parameter.setPointOfService(pointOfServiceModel1);
		final CommerceCartModification modification = commerceCartService.updatePointOfServiceForCartEntry(parameter);

		//Check if POS is updated
		assertEquals(pointOfServiceModel1.getName(), modification.getEntry().getDeliveryPointOfService().getName());
		//Check whether it's the first entry which got updated
		assertEquals(0, modification.getEntry().getEntryNumber().intValue());
		//Check the quantity that was added. The POS 2's quantity is 3 and this should be added
		assertEquals(3, modification.getQuantityAdded());
		//Check the total quantity of the first entry and check if its sum of both entries.
		assertEquals(5, modification.getQuantity());
	}


	@Test
	public void testUpdateToShippingModeForCartEntry() throws CommerceCartModificationException
	{
		final CartModel cartModel = new CartModel();
		final MockProductModel productModel1 = new MockProductModel(12345l);

		final CartEntryModel cartEntryModel1 = new CartEntryModel();
		final PointOfServiceModel pointOfServiceModel1 = new PointOfServiceModel();
		pointOfServiceModel1.setName("testStore1-PointOfService");

		cartEntryModel1.setDeliveryPointOfService(pointOfServiceModel1);
		cartEntryModel1.setEntryNumber(Integer.valueOf(0));
		cartEntryModel1.setQuantity(Long.valueOf(6));
		cartEntryModel1.setProduct(productModel1);

		final CartEntryModel cartEntryModel2 = new CartEntryModel();

		cartEntryModel2.setDeliveryPointOfService(null);
		cartEntryModel2.setEntryNumber(Integer.valueOf(1));
		cartEntryModel2.setQuantity(Long.valueOf(9));
		cartEntryModel2.setProduct(productModel1);

		final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();
		entryList.add(cartEntryModel1);
		entryList.add(cartEntryModel2);
		cartModel.setEntries(entryList);

		given(defaultCommerceStockService.getStockLevelForProductAndBaseStore(productModel1, baseStoreModel)).willReturn(
				Long.valueOf(90));

		assertEquals("testStore1-PointOfService", cartEntryModel1.getDeliveryPointOfService().getName());

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setEntryNumber(0L);
		final CommerceCartModification modification = commerceCartService.updateToShippingModeForCartEntry(parameter);
		//Scenario -1  Merging logic if a shipping entry is already present with same sku and product
		assertEquals(15, modification.getQuantity());
		assertEquals(null, modification.getEntry().getDeliveryPointOfService());
		//Scenario - 2  If the cart entry is alone and its a pick up store to shipping change
		entryList.remove(cartEntryModel2);
		cartEntryModel1.setQuantity(Long.valueOf(95));
		final CommerceCartModification modification2 = commerceCartService.updateToShippingModeForCartEntry(parameter);

		assertEquals(90, modification2.getQuantity());


	}

	@Test
	public void shouldAddToCartAdjustedQuantityWhenMaxOrderQuantityForProductExceeded() throws Exception
	{
		given(defaultCommerceStockService.getStockLevelForProductAndBaseStore(productModel, baseStoreModel)).willReturn(
				Long.valueOf(10));
		given(productModel.getMaxOrderQuantity()).willReturn(Integer.valueOf(5));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(productModel);
		parameter.setQuantity(8);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(false);

		final CommerceCartModification cartModificationResult = commerceCartService.addToCart(parameter);

		assertThat(cartModificationResult.getQuantityAdded()).isEqualTo(5);
		assertThat(cartModificationResult.getStatusCode()).isEqualTo(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
	}


	@Test
	public void shouldEstimateTaxesWithoutCache()
	{
		given(commerceCartEstimateTaxesStrategy.estimateTaxes(cartModel, "11211", "US")).willReturn(BigDecimal.TEN);
		given(commerceCartHashCalculationStrategy.buildHashForAbstractOrder(cartModel, Arrays.asList("11211", "US"))).willReturn(
				"hash");
		given(sessionService.getAttribute(DefaultCommerceCartService.ESTIMATED_TAXES)).willReturn(null);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setDeliveryZipCode("11211");
		parameter.setDeliveryCountryIso("US");

		final BigDecimal estimatedTaxes = commerceCartService.estimateTaxes(parameter).getTax();
		Assert.assertThat(estimatedTaxes, CoreMatchers.equalTo(BigDecimal.TEN));
	}

	@Test
	public void shouldEstimateTaxesWithCache()
	{
		given(commerceCartEstimateTaxesStrategy.estimateTaxes(cartModel, "11211", "US")).willReturn(BigDecimal.TEN);
		final CommerceOrderParameter orderParameter = new CommerceOrderParameter();
		orderParameter.setOrder(cartModel);
		orderParameter.setAdditionalValues(Arrays.asList("11211", "US"));

		given(commerceCartHashCalculationStrategy.buildHashForAbstractOrder(any(CommerceOrderParameter.class))).willReturn("hash");
		final DefaultCommerceCartService.HashAndTaxEstimate hashAndTaxEstimate = new DefaultCommerceCartService.HashAndTaxEstimate(
				"hash", BigDecimal.ONE);
		given(sessionService.getAttribute(DefaultCommerceCartService.ESTIMATED_TAXES)).willReturn(hashAndTaxEstimate);
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setDeliveryZipCode("11211");
		parameter.setDeliveryCountryIso("US");
		final BigDecimal estimatedTaxes = commerceCartService.estimateTaxes(parameter).getTax();
		Assert.assertThat(estimatedTaxes, CoreMatchers.equalTo(BigDecimal.ONE));
	}

	@Test
	public void shouldEstimateTaxesWithInvalidCache()
	{
		given(commerceCartEstimateTaxesStrategy.estimateTaxes(cartModel, "11211", "US")).willReturn(BigDecimal.TEN);
		given(commerceCartHashCalculationStrategy.buildHashForAbstractOrder(any(CommerceOrderParameter.class))).willReturn(
				"invalidhash");
		final DefaultCommerceCartService.HashAndTaxEstimate hashAndTaxEstimate = new DefaultCommerceCartService.HashAndTaxEstimate(
				"hash", BigDecimal.ONE);
		given(sessionService.getAttribute(DefaultCommerceCartService.ESTIMATED_TAXES)).willReturn(hashAndTaxEstimate);
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setDeliveryZipCode("11211");
		parameter.setDeliveryCountryIso("US");
		final BigDecimal estimatedTaxes = commerceCartService.estimateTaxes(parameter).getTax();
		Assert.assertThat(estimatedTaxes, CoreMatchers.equalTo(BigDecimal.TEN));
	}


	@Test
	public void shouldRestoreCartForDelivery() throws CommerceCartRestorationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(oldCartForDelivery);
		given(timeService.getCurrentTime()).willReturn(new Date());
		given(oldCartForDelivery.getModifiedtime()).willReturn(DateUtils.addDays(new Date(), -1));

		final CommerceCartRestoration commerceCartRestoration = commerceCartService.restoreCart(parameter);
		final List<CommerceCartModification> modifications = commerceCartRestoration.getModifications();
		Assert.assertThat(Integer.valueOf(modifications.size()), CoreMatchers.equalTo(Integer.valueOf(1)));
		assertEquals(oldCartForDelivery.getEntries().get(0).getProduct(), modifications.get(0).getEntry().getProduct());

	}

	@Test
	public void shouldRestoreCartForWeb() throws CommerceCartRestorationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(oldCartForWeb);
		given(timeService.getCurrentTime()).willReturn(new Date());
		given(oldCartForWeb.getModifiedtime()).willReturn(DateUtils.addDays(new Date(), -1));

		final CommerceCartRestoration commerceCartRestoration = commerceCartService.restoreCart(parameter);
		final List<CommerceCartModification> modifications = commerceCartRestoration.getModifications();
		Assert.assertThat(Integer.valueOf(modifications.size()), CoreMatchers.equalTo(Integer.valueOf(1)));
		assertEquals(oldCartForWeb.getEntries().get(0).getProduct(), modifications.get(0).getEntry().getProduct());
	}

	@Test
	public void shouldSplitCartWith1Entry() throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(oldCartForDelivery);
		parameter.setEntryNumber(0);

		final long split = commerceCartService.split(parameter);
		assertEquals(0, split);

	}


	protected static class MockProductModel extends ProductModel
	{
		private final long id;

		public MockProductModel(final long id)
		{
			this.id = id;
		}

		@Override
		public de.hybris.platform.core.PK getPk()
		{
			return de.hybris.platform.core.PK.fromLong(id);
		}
	}
}
