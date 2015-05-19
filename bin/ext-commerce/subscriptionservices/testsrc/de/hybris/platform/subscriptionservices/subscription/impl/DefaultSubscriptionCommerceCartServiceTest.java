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
package de.hybris.platform.subscriptionservices.subscription.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.CommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceRemoveEntriesStrategy;
import de.hybris.platform.commerceservices.order.CommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.order.impl.OrderEntryModifiableChecker;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * JUnit test suite for {@link DefaultSubscriptionCommerceCartService}
 */
@UnitTest
public class DefaultSubscriptionCommerceCartServiceTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	public static final int APPEND_AS_LAST = -1;

	private DefaultSubscriptionCommerceCartService commerceCartService;
	private CartModel masterCartModel;
	private CartModel childCartModelMonthly;
	private CartEntryModel cartEntryModel;
	private ProductModel productModel;
	private SubscriptionProductModel subscriptionProductModel;
	private UnitModel unitModel;
	private BillingTimeModel billingFrequencyModelPayNow;
	private BillingFrequencyModel billingFrequencyModelMonthly;
	private WarehouseModel warehouseModel;

	@Mock
	private CartService cartService;
	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	@Mock
	private CalculationService calculationService;
	@Mock
	private SubscriptionCommercePriceService subscriptionCommercePriceService;
	@Mock
	private ModelService modelService;
	@Mock
	private WarehouseService warehouseService;
	@Mock
	private BillingTimeService billingTimeService;
	@Mock
	private StockService stockService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CommerceStockService commerceStockService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private BillingPlanModel billingPlan;
	@Mock
	private SubscriptionCommerceCartStrategy subscriptionCommerceCartStrategy;
	@Mock
	private CommerceUpdateCartEntryStrategy commerceUpdateCartEntryStrategy;
	@Mock
	private CommerceAddToCartStrategy commerceAddToCartStrategy;
	@Mock
	private CommerceRemoveEntriesStrategy commerceRemoveEntriesStrategy;

	@Spy
	private final ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker = new OrderEntryModifiableChecker();


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		commerceCartService = new DefaultSubscriptionCommerceCartService();
		commerceCartService.setCommerceCartCalculationStrategy(commerceCartCalculationStrategy);
		commerceCartService.setCommercePriceService(subscriptionCommercePriceService);
		commerceCartService.setModelService(modelService);
		commerceCartService.setCommerceStockService(commerceStockService);
		commerceCartService.setBillingTimeService(billingTimeService);
		commerceCartService.setBaseStoreService(baseStoreService);
		commerceCartService.setBaseSiteService(baseSiteService);
		commerceCartService.setSubscriptionCommerceCartStrategy(subscriptionCommerceCartStrategy);
		commerceCartService.setCommerceUpdateCartEntryStrategy(commerceUpdateCartEntryStrategy);
		commerceCartService.setCommerceAddToCartStrategy(commerceAddToCartStrategy);
		commerceCartService.setCommerceRemoveEntriesStrategy(commerceRemoveEntriesStrategy);
		commerceCartService.setSubscriptionProductStockQuantity(1000);
		productModel = mock(ProductModel.class);
		unitModel = mock(UnitModel.class);
		subscriptionProductModel = mock(SubscriptionProductModel.class);
		warehouseModel = mock(WarehouseModel.class);
		cartEntryModel = mock(CartEntryModel.class);

		billingFrequencyModelPayNow = mock(BillingTimeModel.class);
		given(billingFrequencyModelPayNow.getCode()).willReturn("paynow");
		billingFrequencyModelMonthly = mock(BillingFrequencyModel.class);
		given(billingFrequencyModelMonthly.getCode()).willReturn("monthly");
		given(billingFrequencyModelMonthly.getCartAware()).willReturn(Boolean.TRUE);
		final SubscriptionTermModel subscriptionTerm = mock(SubscriptionTermModel.class);
		billingPlan = mock(BillingPlanModel.class);
		given(subscriptionTerm.getBillingPlan()).willReturn(billingPlan);
		given(subscriptionProductModel.getSubscriptionTerm()).willReturn(subscriptionTerm);
		masterCartModel = mock(CartModel.class);
		given(masterCartModel.getBillingTime()).willReturn(billingFrequencyModelPayNow);

		childCartModelMonthly = mock(CartModel.class);
		given(childCartModelMonthly.getBillingTime()).willReturn(billingFrequencyModelMonthly);
		given(childCartModelMonthly.getParent()).willReturn(masterCartModel);

		final List<AbstractOrderModel> childCarts = new ArrayList<AbstractOrderModel>();
		childCarts.add(childCartModelMonthly);
		given(masterCartModel.getChildren()).willReturn(childCarts);

		given(billingTimeService.getBillingTimeForCode(null)).willReturn(billingFrequencyModelPayNow);

		final BaseSiteModel baseSite = mock(BaseSiteModel.class);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);

		Mockito.doReturn(Boolean.TRUE).when(entryOrderChecker).canModify(Mockito.any(AbstractOrderEntryModel.class));
	}


	@Test
	public void testAddToCart() throws CommerceCartModificationException, CalculationException
	{
		final StockLevelModel stockLevelModel = mock(StockLevelModel.class);
		long actualAdded = 0;

		given(Integer.valueOf(stockLevelModel.getAvailable())).willReturn(Integer.valueOf(10));
		given(subscriptionProductModel.getMaxOrderQuantity()).willReturn(null);
		given(productModel.getMaxOrderQuantity()).willReturn(null);
		given(Boolean.valueOf(calculationService.requiresCalculation(masterCartModel))).willReturn(Boolean.TRUE);

		// add to master cart (paynow, product)
		given(cartService.addNewEntry(masterCartModel, productModel, 1, unitModel, APPEND_AS_LAST, true))
				.willReturn(cartEntryModel);

		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantityAdded(1);
		mod.setEntry(cartEntryModel);
		Mockito.doReturn(mod).when(commerceAddToCartStrategy).addToCart(Mockito.any(CommerceCartParameter.class));

		actualAdded = commerceCartService.addToCart(masterCartModel, productModel, 1, unitModel, false, "<no xml>")
				.getQuantityAdded();
		Assert.assertEquals(1, actualAdded);

		// add to master cart (paynow, subscription product)
		given(cartService.addNewEntry(masterCartModel, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false)).willReturn(
				cartEntryModel);
		given(cartEntryModel.getProduct()).willReturn(subscriptionProductModel);
		mod.setEntry(cartEntryModel);
		actualAdded = commerceCartService.addToCart(masterCartModel, subscriptionProductModel, 1, unitModel, false, "<no xml>")
				.getQuantityAdded();
		Assert.assertEquals(1, actualAdded);

		// add to existing child cart (monthly, subscription product)
		given(Boolean.valueOf(calculationService.requiresCalculation(childCartModelMonthly))).willReturn(Boolean.TRUE);
		given(subscriptionProductModel.getSubscriptionTerm().getBillingPlan().getBillingFrequency()).willReturn(
				billingFrequencyModelMonthly);
		given(cartService.addNewEntry(childCartModelMonthly, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false))
				.willReturn(cartEntryModel);
		mod.setEntry(cartEntryModel);
		actualAdded = commerceCartService.addToCart(masterCartModel, subscriptionProductModel, 1, unitModel, false, "<no xml>")
				.getQuantityAdded();
		Assert.assertEquals(1, actualAdded);

		// add to new child cart (yearly, subscription product)
		final CartModel childCartModelYearly;
		childCartModelYearly = mock(CartModel.class);
		given(Boolean.valueOf(calculationService.requiresCalculation(childCartModelYearly))).willReturn(Boolean.TRUE);
		given(modelService.create(CartModel.class)).willReturn(childCartModelYearly);
		final BillingFrequencyModel billingFrequencyModelYearly = mock(BillingFrequencyModel.class);
		given(billingFrequencyModelYearly.getCode()).willReturn("yearly");
		given(billingFrequencyModelYearly.getCartAware()).willReturn(Boolean.TRUE);
		given(billingPlan.getBillingFrequency()).willReturn(billingFrequencyModelYearly);
		given(cartService.addNewEntry(childCartModelYearly, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false))
				.willReturn(cartEntryModel);
		mod.setEntry(cartEntryModel);

		actualAdded = commerceCartService.addToCart(masterCartModel, subscriptionProductModel, 1, unitModel, false, "<no xml>")
				.getQuantityAdded();

		Assert.assertEquals(1, actualAdded);
		verify(commerceAddToCartStrategy, times(6)).addToCart(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testAddToCartWhenCartIsNull() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Cart model cannot be null");

		commerceCartService.addToCart(null, productModel, 1, unitModel, false, "<no xml>");
	}

	@Test
	public void testAddToCartWhenProductIsNull() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Product model cannot be null");

		commerceCartService.addToCart(masterCartModel, null, 1, unitModel, false, "<no xml>");
	}

	@Test
	public void testAddToCartIsNoMasterCart() throws CommerceCartModificationException, CalculationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("is not a master cart");

		commerceCartService.addToCart(childCartModelMonthly, productModel, 1, unitModel, false, "<no xml>");
	}

	@Test
	public void testAddToCartMasterHasWrongBillingFrequency() throws CommerceCartModificationException, CalculationException
	{
		given(masterCartModel.getBillingTime()).willReturn(billingFrequencyModelMonthly);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("does not equal the required master cart billing frequency");

		commerceCartService.addToCart(masterCartModel, productModel, 1, unitModel, false, "<no xml>");
	}

	@Test
	public void testAddToCartSubscriptionProductWithWrongQuantity() throws CommerceCartModificationException, CalculationException
	{
		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("given quantityToAdd (2) exceeds the max");

		commerceCartService.addToCart(masterCartModel, subscriptionProductModel, 2, unitModel, false, "<no xml>")
				.getQuantityAdded();
	}

	@Test
	public void testAddToCartProductNoStock() throws CommerceCartModificationException, CalculationException
	{
		long actualAdded = 0;
		final StockLevelModel stockLevelModel = mock(StockLevelModel.class);

		given(warehouseService.getDefWarehouse()).willReturn(Collections.singletonList(warehouseModel));
		given(masterCartModel.getBillingTime()).willReturn(billingFrequencyModelPayNow);

		given(Integer.valueOf(stockLevelModel.getAvailable())).willReturn(Integer.valueOf(0));
		given(stockService.getAllStockLevels(productModel)).willReturn(Collections.singletonList(stockLevelModel));

		given(cartService.addNewEntry(masterCartModel, productModel, 1, unitModel, APPEND_AS_LAST, false)).willReturn(
				cartEntryModel);

		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantityAdded(0);
		mod.setEntry(cartEntryModel);
		Mockito.doReturn(mod).when(commerceAddToCartStrategy).addToCart(Mockito.any(CommerceCartParameter.class));

		actualAdded = commerceCartService.addToCart(masterCartModel, productModel, 1, unitModel, false, "<no xml>")
				.getQuantityAdded();
		Assert.assertEquals(0, actualAdded);
	}

	@Test
	public void testAddToCartSubscriptionProductNoStock() throws CommerceCartModificationException, CalculationException
	{
		long actualAdded = 0;
		final StockLevelModel stockLevelModel = mock(StockLevelModel.class);

		given(warehouseService.getDefWarehouse()).willReturn(Collections.singletonList(warehouseModel));
		given(masterCartModel.getBillingTime()).willReturn(billingFrequencyModelPayNow);
		given(productModel.getMaxOrderQuantity()).willReturn(null);
		given(subscriptionProductModel.getMaxOrderQuantity()).willReturn(null);
		//given(subscriptionProductModel.getBillingFrequency()).willReturn(billingFrequencyModelPayNow);

		// No stocklevels are maintained for subscription products
		given(Integer.valueOf(stockLevelModel.getAvailable())).willReturn(Integer.valueOf(0));
		given(stockService.getAllStockLevels(productModel)).willReturn(Collections.singletonList(stockLevelModel));

		given(cartService.addNewEntry(masterCartModel, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false)).willReturn(
				cartEntryModel);

		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantityAdded(1);
		mod.setEntry(cartEntryModel);
		Mockito.doReturn(mod).when(commerceAddToCartStrategy).addToCart(Mockito.any(CommerceCartParameter.class));

		actualAdded = commerceCartService.addToCart(masterCartModel, subscriptionProductModel, 1, unitModel, false, "<no xml>")
				.getQuantityAdded();
		Assert.assertEquals(1, actualAdded);
	}



	@Test
	public void testRemoveAllEntriesWhenCartIsNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Cart model cannot be null");

		commerceCartService.removeAllEntries((CartModel) null);
	}

	@Test
	public void testRemoveAllEntriesMasterCart() throws CommerceCartModificationException, CalculationException
	{

		final AbstractOrderEntryModel entryModel = mock(AbstractOrderEntryModel.class);
		final List<AbstractOrderEntryModel> entries = Collections.singletonList(entryModel);
		final List<AbstractOrderModel> childCarts = Collections.singletonList((AbstractOrderModel) childCartModelMonthly);

		given(masterCartModel.getEntries()).willReturn(entries);
		given(masterCartModel.getChildren()).willReturn(childCarts);
		given(childCartModelMonthly.getEntries()).willReturn(entries);

		commerceCartService.removeAllEntries(masterCartModel);

		verify(modelService, times(1)).removeAll(entries);
		verify(modelService, times(1)).remove(childCartModelMonthly);
	}

	@Test
	public void testRemoveAllEntriesChildCart() throws CommerceCartModificationException, CalculationException
	{
		final AbstractOrderEntryModel entryModel = mock(AbstractOrderEntryModel.class);
		final List<AbstractOrderEntryModel> entries = Collections.singletonList(entryModel);

		given(childCartModelMonthly.getEntries()).willReturn(entries);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Provided cart 'null' is not a master cart");

		commerceCartService.removeAllEntries(childCartModelMonthly);
	}

	@Test
	public void testUpdateQuantityForCartEntryWhenCartIsNull() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Cart model cannot be null");

		commerceCartService.updateQuantityForCartEntry(null, 1, 1);
	}

	@Test
	public void testUpdateQuantityForCartEntryWhenCartEntryNotFound() throws CommerceCartModificationException
	{
		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("Unknown entry number 1");

		commerceCartService.updateQuantityForCartEntry(masterCartModel, 1, 1);
	}

	@Test
	public void testUpdateQuantityForCartEntryWhenCartIsNotMasterCart() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("is not a master cart");

		commerceCartService.updateQuantityForCartEntry(childCartModelMonthly, 1, 1);
	}

	@Test
	public void testUpdateQuantityForCartEntry() throws CommerceCartModificationException, CalculationException
	{
		final StockLevelModel stockLevelModel = mock(StockLevelModel.class);
		final CartEntryModel entryToUpdate = mock(CartEntryModel.class);

		given(warehouseService.getDefWarehouse()).willReturn(Collections.singletonList(warehouseModel));
		given(entryToUpdate.getEntryNumber()).willReturn(Integer.valueOf(1));
		given(masterCartModel.getEntries()).willReturn(Collections.singletonList((AbstractOrderEntryModel) entryToUpdate));
		given(masterCartModel.getBillingTime()).willReturn(billingFrequencyModelPayNow);

		given(entryToUpdate.getProduct()).willReturn(productModel);
		given(Integer.valueOf(stockLevelModel.getAvailable())).willReturn(Integer.valueOf(10));
		given(stockService.getAllStockLevels(productModel)).willReturn(Collections.singletonList(stockLevelModel));
		given(productModel.getMaxOrderQuantity()).willReturn(null);
		given(Boolean.valueOf(calculationService.requiresCalculation(masterCartModel))).willReturn(Boolean.TRUE);
		Mockito.doReturn(entryToUpdate).when(subscriptionCommerceCartStrategy).getEntryForNumber(masterCartModel, 1);
		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantity(0);
		mod.setEntry(cartEntryModel);
		Mockito.doReturn(mod).when(commerceUpdateCartEntryStrategy)
				.updateQuantityForCartEntry(Mockito.any(CommerceCartParameter.class));

		final CommerceCartModification result1 = commerceCartService.updateQuantityForCartEntry(masterCartModel, 1, 0);
		Assert.assertEquals(0, result1.getQuantity());

		mod.setQuantity(5);

		final CommerceCartModification result2 = commerceCartService.updateQuantityForCartEntry(masterCartModel, 1, 5);
		Assert.assertNotNull(result2.getEntry());
		Assert.assertEquals(5, result2.getQuantity());

		verify(commerceUpdateCartEntryStrategy, times(2)).updateQuantityForCartEntry(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testUpdateQuantityForCartEntrySubscriptionProduct() throws CommerceCartModificationException, CalculationException
	{
		final CartEntryModel entryToUpdate = mock(CartEntryModel.class);

		given(warehouseService.getDefWarehouse()).willReturn(Collections.singletonList(warehouseModel));
		given(entryToUpdate.getEntryNumber()).willReturn(Integer.valueOf(1));
		given(masterCartModel.getEntries()).willReturn(Collections.singletonList((AbstractOrderEntryModel) entryToUpdate));
		given(masterCartModel.getBillingTime()).willReturn(billingFrequencyModelPayNow);
		given(Boolean.valueOf(calculationService.requiresCalculation(masterCartModel))).willReturn(Boolean.TRUE);
		given(entryToUpdate.getProduct()).willReturn(subscriptionProductModel);

		Mockito.doReturn(new CartEntryModel()).when(subscriptionCommerceCartStrategy).getEntryForNumber(masterCartModel, 1);
		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantity(0);
		Mockito.doReturn(mod).when(commerceUpdateCartEntryStrategy)
				.updateQuantityForCartEntry(Mockito.any(CommerceCartParameter.class));

		final CommerceCartModification result1 = commerceCartService.updateQuantityForCartEntry(masterCartModel, 1, 0);
		Assert.assertEquals(0, result1.getQuantity());

		verify(commerceUpdateCartEntryStrategy, times(1)).updateQuantityForCartEntry(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testUpdateQuantityForCartEntrySubscriptionProductWrongQuantity() throws CommerceCartModificationException,
			CalculationException
	{
		final StockLevelModel stockLevelModel = mock(StockLevelModel.class);
		final CartEntryModel entryToUpdate = mock(CartEntryModel.class);

		given(warehouseService.getDefWarehouse()).willReturn(Collections.singletonList(warehouseModel));
		given(entryToUpdate.getEntryNumber()).willReturn(Integer.valueOf(1));
		given(masterCartModel.getEntries()).willReturn(Collections.singletonList((AbstractOrderEntryModel) entryToUpdate));

		given(entryToUpdate.getProduct()).willReturn(subscriptionProductModel);
		given(Integer.valueOf(stockLevelModel.getAvailable())).willReturn(Integer.valueOf(10));
		given(stockService.getAllStockLevels(productModel)).willReturn(Collections.singletonList(stockLevelModel));

		Mockito.doReturn(entryToUpdate).when(subscriptionCommerceCartStrategy).getEntryForNumber(masterCartModel, 1);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("must have a new quantity of 0 or 1");

		commerceCartService.updateQuantityForCartEntry(masterCartModel, 1, 2);

	}

	@Test
	public void testRecalculateCartEntryWhenCartIsNull() throws CalculationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Cart model cannot be null");

		commerceCartService.recalculateCart((CartModel) null);
	}

	@Test
	public void testRecalculateCartEntry() throws CalculationException
	{
		commerceCartService.recalculateCart(masterCartModel);
		verify(commerceCartCalculationStrategy, times(2)).recalculateCart(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testRecalculateCartEntryWhenCartIsNotMasterCart() throws CalculationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("is not a master cart");

		commerceCartService.recalculateCart(childCartModelMonthly);
	}

}