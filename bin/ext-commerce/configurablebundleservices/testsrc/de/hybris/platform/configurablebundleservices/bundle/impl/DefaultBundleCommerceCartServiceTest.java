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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.CommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.order.impl.OrderEntryModifiableChecker;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.daos.OrderEntryDao;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * JUnit test suite for {@link DefaultBundleCommerceCartService}
 */
@UnitTest
public class DefaultBundleCommerceCartServiceTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final int APPEND_AS_LAST = -1;
	private static final String SESSION_ATTRIBUTE_CALCULATE_CART = "CALCULATE_CART";

	private DefaultBundleCommerceCartService commerceCartService;
	private CartModel masterCartModel;
	private CartModel childCartModelMonthly;
	private CartEntryModel cartEntryModel;
	private ProductModel productModel;
	private SubscriptionProductModel subscriptionProductModel;
	private UnitModel unitModel;
	private BillingTimeModel billingTimeModelPayNow;
	private BillingFrequencyModel billingFrequencyModelMonthly;

	private BundleTemplateModel bundleTemplateModel;

	@Mock
	private ProductService productService;
	@Mock
	private CartService cartService;
	@Mock
	private SubscriptionCommercePriceService subscriptionCommercePriceService;
	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	@Mock
	private ModelService modelService;
	@Mock
	private WarehouseService warehouseService;
	@Mock
	private TimeService timeService;
	@Mock
	private BillingTimeService billingTimeService;
	@Mock
	private StockService stockService;
	@Mock
	private SessionService sessionService;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private OrderEntryDao cartEntryDao;
	@Mock
	private BundleRuleService bundleRuleService;
	@Mock
	private BundleOrderEntryRemoveableChecker orderEntryRemoveableChecker;
	@Mock
	private L10NService l10NService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CommerceStockService commerceStockService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private SubscriptionCommerceCartStrategy subscriptionCommerceCartStrategy;
	@Mock
	private CommerceAddToCartStrategy commerceAddToCartStrategy;
	@Mock
	private CommerceUpdateCartEntryStrategy commerceUpdateCartEntryStrategy;

	@Spy
	private final ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker = new OrderEntryModifiableChecker();
	@Mock
	private final ModifiableChecker<AbstractOrderEntryModel> orderEntryModifiableChecker = new BundleOrderEntryModifiableChecker();
	@Mock
	private final AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker = new DefaultCartBundleComponentEditableChecker();

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		commerceCartService = new DefaultBundleCommerceCartService();
		commerceCartService.setCommerceCartCalculationStrategy(commerceCartCalculationStrategy);
		commerceCartService.setCommercePriceService(subscriptionCommercePriceService);
		commerceCartService.setSessionService(sessionService);
		commerceCartService.setModelService(modelService);
		commerceCartService.setBundleCartEntryDao(cartEntryDao);
		commerceCartService.setBillingTimeService(billingTimeService);
		commerceCartService.setBundleTemplateService(bundleTemplateService);
		commerceCartService.setSubscriptionCommerceCartStrategy(subscriptionCommerceCartStrategy);
		commerceCartService.setOrderEntryModifiableChecker(orderEntryModifiableChecker);
		commerceCartService.setSubscriptionProductStockQuantity(1000);
		commerceCartService.setRemovableChecker(orderEntryRemoveableChecker);
		commerceCartService.setBundleComponentEditableChecker(bundleComponentEditableChecker);
		commerceCartService.setBundleRuleService(bundleRuleService);
		commerceCartService.setL10NService(l10NService);
		commerceCartService.setBaseStoreService(baseStoreService);
		commerceCartService.setBaseSiteService(baseSiteService);
		commerceCartService.setCommerceStockService(commerceStockService);
		commerceCartService.setCommerceAddToCartStrategy(commerceAddToCartStrategy);
		commerceCartService.setCommerceUpdateCartEntryStrategy(commerceUpdateCartEntryStrategy);
		productModel = mock(ProductModel.class);
		unitModel = mock(UnitModel.class);
		subscriptionProductModel = mock(SubscriptionProductModel.class);
		cartEntryModel = mock(CartEntryModel.class);

		billingTimeModelPayNow = mock(BillingTimeModel.class);
		given(billingTimeModelPayNow.getCode()).willReturn("paynow");
		given(billingTimeModelPayNow.getCartAware()).willReturn(Boolean.TRUE);
		billingFrequencyModelMonthly = mock(BillingFrequencyModel.class);
		given(billingFrequencyModelMonthly.getCode()).willReturn("monthly");
		given(billingFrequencyModelMonthly.getCartAware()).willReturn(Boolean.TRUE);
		masterCartModel = mock(CartModel.class);
		given(masterCartModel.getBillingTime()).willReturn(billingTimeModelPayNow);

		childCartModelMonthly = mock(CartModel.class);
		given(childCartModelMonthly.getBillingTime()).willReturn(billingFrequencyModelMonthly);
		given(childCartModelMonthly.getParent()).willReturn(masterCartModel);

		final List<AbstractOrderModel> childCarts = new ArrayList<AbstractOrderModel>();
		childCarts.add(childCartModelMonthly);
		given(masterCartModel.getChildren()).willReturn(childCarts);

		given(billingTimeService.getBillingTimeForCode(null)).willReturn(billingTimeModelPayNow);

		given(subscriptionProductModel.getSoldIndividually()).willReturn(Boolean.TRUE);
		given(productModel.getSoldIndividually()).willReturn(Boolean.TRUE);

		final BaseSiteModel baseSite = mock(BaseSiteModel.class);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);

		Mockito.when(sessionService.executeInLocalView(Mockito.any(SessionExecutionBody.class))).thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				final SessionExecutionBody args = (SessionExecutionBody) invocation.getArguments()[0];
				return args.execute();
			}
		});

		doReturn(Boolean.TRUE).when(entryOrderChecker).canModify(Mockito.any(AbstractOrderEntryModel.class));

		bundleTemplateModel = mock(BundleTemplateModel.class);
	}

	@Test
	public void testAddToCartNoBundleParamsOldMethod() throws CommerceCartModificationException, CalculationException
	{
		final StockLevelModel stockLevelModel = mock(StockLevelModel.class);
		long actualAdded = 0;
		final SubscriptionPricePlanModel subscriptionPricePlan = mock(SubscriptionPricePlanModel.class);
		final RecurringChargeEntryModel recurringChargeEntry = mock(RecurringChargeEntryModel.class);
		final BillingPlanModel billingPlan = mock(BillingPlanModel.class);
		final SubscriptionTermModel subscriptionTerm = mock(SubscriptionTermModel.class);

		given(Integer.valueOf(stockLevelModel.getAvailable())).willReturn(Integer.valueOf(10));
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCartModel, null, 0))).willReturn(Boolean.TRUE);

		// add to master cart (paynow, product)
		given(cartService.addNewEntry(masterCartModel, productModel, 1, unitModel, APPEND_AS_LAST, false)).willReturn(
				cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		given(productModel.getMaxOrderQuantity()).willReturn(null);

		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantityAdded(1);
		mod.setEntry(cartEntryModel);
		Mockito.doReturn(mod).when(commerceAddToCartStrategy).addToCart(Mockito.any(CommerceCartParameter.class));

		actualAdded = commerceCartService.addToCart(masterCartModel, productModel, 1, unitModel, false).getQuantityAdded();
		assertEquals(1, actualAdded);

		// add to master cart (paynow, subscription product)
		given(cartService.addNewEntry(masterCartModel, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false)).willReturn(
				cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		given(cartEntryModel.getProduct()).willReturn(subscriptionProductModel);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		given(subscriptionProductModel.getMaxOrderQuantity()).willReturn(null);
		actualAdded = commerceCartService.addToCart(masterCartModel, subscriptionProductModel, 1, unitModel, false)
				.getQuantityAdded();
		assertEquals(1, actualAdded);

		// add to existing child cart (monthly, subscription product)
		given(subscriptionProductModel.getSubscriptionTerm()).willReturn(subscriptionTerm);
		given(subscriptionTerm.getBillingPlan()).willReturn(billingPlan);
		given(subscriptionProductModel.getSubscriptionTerm().getBillingPlan().getBillingFrequency()).willReturn(
				billingFrequencyModelMonthly);
		given(cartService.addNewEntry(childCartModelMonthly, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false))
				.willReturn(cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(childCartModelMonthly);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		given(subscriptionCommercePriceService.getSubscriptionPricePlanForEntry(cartEntryModel)).willReturn(subscriptionPricePlan);
		given(subscriptionPricePlan.getRecurringChargeEntries()).willReturn(Collections.singleton(recurringChargeEntry));
		given(recurringChargeEntry.getBillingTime()).willReturn(billingFrequencyModelMonthly);
		actualAdded = commerceCartService.addToCart(masterCartModel, subscriptionProductModel, 1, unitModel, false)
				.getQuantityAdded();
		assertEquals(1, actualAdded);

		// add to new child cart (yearly, subscription product)
		final CartModel childCartModelYearly;
		childCartModelYearly = mock(CartModel.class);
		given(modelService.create(CartModel.class)).willReturn(childCartModelYearly);
		BillingTimeModel billingFrequencyModelYearly;
		billingFrequencyModelYearly = mock(BillingTimeModel.class);
		given(billingFrequencyModelYearly.getCode()).willReturn("yearly");
		given(billingFrequencyModelYearly.getCartAware()).willReturn(Boolean.TRUE);
		given(cartService.addNewEntry(childCartModelYearly, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false))
				.willReturn(cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(childCartModelYearly);
		final List<AbstractOrderModel> childCarts = new ArrayList<AbstractOrderModel>();
		childCarts.add(childCartModelMonthly);
		childCarts.add(childCartModelYearly);
		given(masterCartModel.getChildren()).willReturn(childCarts);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		given(recurringChargeEntry.getBillingTime()).willReturn(billingFrequencyModelYearly);
		actualAdded = commerceCartService.addToCart(masterCartModel, subscriptionProductModel, 1, unitModel, false)
				.getQuantityAdded();
		assertEquals(1, actualAdded);

		verify(commerceAddToCartStrategy, times(6)).addToCart(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testAddToCartNoBundleParamsNewMethod() throws CommerceCartModificationException, CalculationException
	{
		final StockLevelModel stockLevelModel = mock(StockLevelModel.class);
		long actualAdded = 0;
		final SubscriptionPricePlanModel subscriptionPricePlan = mock(SubscriptionPricePlanModel.class);
		final RecurringChargeEntryModel recurringChargeEntry = mock(RecurringChargeEntryModel.class);
		final BillingPlanModel billingPlan = mock(BillingPlanModel.class);
		final SubscriptionTermModel subscriptionTerm = mock(SubscriptionTermModel.class);

		given(Integer.valueOf(stockLevelModel.getAvailable())).willReturn(Integer.valueOf(10));
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCartModel, null, 0))).willReturn(Boolean.TRUE);

		// add to master cart (paynow, product)
		given(cartService.addNewEntry(masterCartModel, productModel, 1, unitModel, APPEND_AS_LAST, false)).willReturn(
				cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		given(productModel.getMaxOrderQuantity()).willReturn(null);

		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantityAdded(1);
		mod.setEntry(cartEntryModel);
		Mockito.doReturn(mod).when(commerceAddToCartStrategy).addToCart(Mockito.any(CommerceCartParameter.class));

		List<CommerceCartModification> modifications = commerceCartService.addToCart(masterCartModel, productModel, 1, unitModel,
				false, 0, null, false, "<no xml>");
		assertEquals(1, modifications.size());
		actualAdded = modifications.iterator().next().getQuantityAdded();
		assertEquals(1, actualAdded);

		// add to master cart (paynow, subscription product)
		given(cartService.addNewEntry(masterCartModel, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false)).willReturn(
				cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		given(cartEntryModel.getProduct()).willReturn(subscriptionProductModel);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		given(subscriptionProductModel.getMaxOrderQuantity()).willReturn(null);
		modifications.clear();
		modifications = commerceCartService.addToCart(masterCartModel, subscriptionProductModel, 1, unitModel, false, 0, null,
				false, "<no xml>");
		assertEquals(1, modifications.size());
		actualAdded = modifications.iterator().next().getQuantityAdded();
		assertEquals(1, actualAdded);

		// add to existing child cart (monthly, subscription product)
		given(subscriptionProductModel.getSubscriptionTerm()).willReturn(subscriptionTerm);
		given(subscriptionTerm.getBillingPlan()).willReturn(billingPlan);
		given(subscriptionProductModel.getSubscriptionTerm().getBillingPlan().getBillingFrequency()).willReturn(
				billingFrequencyModelMonthly);
		given(cartService.addNewEntry(childCartModelMonthly, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false))
				.willReturn(cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(childCartModelMonthly);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		given(subscriptionCommercePriceService.getSubscriptionPricePlanForEntry(cartEntryModel)).willReturn(subscriptionPricePlan);
		given(subscriptionPricePlan.getRecurringChargeEntries()).willReturn(Collections.singleton(recurringChargeEntry));
		given(recurringChargeEntry.getBillingTime()).willReturn(billingFrequencyModelMonthly);
		modifications.clear();
		modifications = commerceCartService.addToCart(masterCartModel, subscriptionProductModel, 1, unitModel, false, 0, null,
				false, "<no xml>");
		assertEquals(1, modifications.size());
		actualAdded = modifications.iterator().next().getQuantityAdded();
		assertEquals(1, actualAdded);

		// add to new child cart (yearly, subscription product)
		final CartModel childCartModelYearly;
		childCartModelYearly = mock(CartModel.class);
		given(modelService.create(CartModel.class)).willReturn(childCartModelYearly);
		BillingTimeModel billingFrequencyModelYearly;
		billingFrequencyModelYearly = mock(BillingTimeModel.class);
		given(billingFrequencyModelYearly.getCode()).willReturn("yearly");
		given(billingFrequencyModelYearly.getCartAware()).willReturn(Boolean.TRUE);
		given(cartService.addNewEntry(childCartModelYearly, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false))
				.willReturn(cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(childCartModelYearly);
		final List<AbstractOrderModel> childCarts = new ArrayList<AbstractOrderModel>();
		childCarts.add(childCartModelMonthly);
		childCarts.add(childCartModelYearly);
		given(masterCartModel.getChildren()).willReturn(childCarts);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		given(recurringChargeEntry.getBillingTime()).willReturn(billingFrequencyModelYearly);
		modifications.clear();
		modifications = commerceCartService.addToCart(masterCartModel, subscriptionProductModel, 1, unitModel, false, 0, null,
				false, "<no xml>");
		assertEquals(1, modifications.size());
		actualAdded = modifications.iterator().next().getQuantityAdded();
		assertEquals(1, actualAdded);

		verify(commerceAddToCartStrategy, times(6)).addToCart(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testAddToCartNewBundle() throws CommerceCartModificationException, CalculationException
	{
		final StockLevelModel stockLevelModel = mock(StockLevelModel.class);
		final CartEntryModel cartEntryModel = mock(CartEntryModel.class);
		long actualAdded = 0;

		given(Integer.valueOf(stockLevelModel.getAvailable())).willReturn(Integer.valueOf(10));
		given(cartService.addNewEntry(masterCartModel, productModel, 1, unitModel, APPEND_AS_LAST, false)).willReturn(
				cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		//YTODO given(cartEntryModel.getMasterAbstractOrder()).willReturn(masterCartModel);
		final List<AbstractOrderEntryModel> allEntries = new ArrayList<AbstractOrderEntryModel>();
		allEntries.add(cartEntryModel);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(productModel);
		given(bundleTemplateModel.getProducts()).willReturn(products);
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateModel, -1))).willReturn(
				Boolean.TRUE);
		given(productModel.getMaxOrderQuantity()).willReturn(null);

		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantityAdded(1);
		mod.setEntry(cartEntryModel);
		Mockito.doReturn(mod).when(commerceAddToCartStrategy).addToCart(Mockito.any(CommerceCartParameter.class));

		// add to master cart (paynow, product): create new bundle
		final List<CommerceCartModification> modifications = commerceCartService.addToCart(masterCartModel, productModel, 1,
				unitModel, false, -1, bundleTemplateModel, false, "<no xml>");
		assertEquals(1, modifications.size());
		actualAdded = modifications.iterator().next().getQuantityAdded();
		assertEquals(1, actualAdded);
		verify(commerceAddToCartStrategy, times(1)).addToCart(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testAddToCartExistingBundle() throws CommerceCartModificationException, CalculationException
	{
		final CartEntryModel cartEntryModel = mock(CartEntryModel.class);
		long actualAdded = 0;

		given(cartService.addNewEntry(masterCartModel, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false)).willReturn(
				cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(subscriptionProductModel);
		given(bundleTemplateModel.getProducts()).willReturn(products);
		final List<CartEntryModel> allCartEntries = new ArrayList<CartEntryModel>();
		allCartEntries.add(cartEntryModel);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNo(masterCartModel, 1)).willReturn(allCartEntries);
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateModel, 1))).willReturn(
				Boolean.TRUE);
		given(subscriptionProductModel.getMaxOrderQuantity()).willReturn(null);
		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantityAdded(1);
		mod.setEntry(cartEntryModel);
		Mockito.doReturn(mod).when(commerceAddToCartStrategy).addToCart(Mockito.any(CommerceCartParameter.class));

		// add to master cart (paynow, subscription product): add to existing bundle 1
		final List<CommerceCartModification> modifications = commerceCartService.addToCart(masterCartModel,
				subscriptionProductModel, 1, unitModel, false, 1, bundleTemplateModel, false, "<no xml>");
		assertEquals(1, modifications.size());
		actualAdded = modifications.iterator().next().getQuantityAdded();
		assertEquals(1, actualAdded);

		verify(commerceAddToCartStrategy, times(1)).addToCart(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testAddToCartWhenCartIsNull() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterCartModel can not be null");

		commerceCartService.addToCart(null, productModel, 1, unitModel, false);
	}

	@Test
	public void testAddToCartWhenProductIsNull() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("productModel can not be null");

		commerceCartService.addToCart(masterCartModel, null, 1, unitModel, false);
	}

	@Test
	public void testAddToCartNoBundleTemplate() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		commerceCartService.addToCart(masterCartModel, productModel, 1, unitModel, false, 1, null, false, "<no xml>");
	}

	@Test
	public void testAddToCartNoBundleNumber() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleNo and bundleTemplate must be provided");

		commerceCartService
				.addToCart(masterCartModel, productModel, 1, unitModel, false, 0, bundleTemplateModel, false, "<no xml>");
	}

	@Test
	public void testAddToCartWhenInvalidBundleNumber() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The bundleNo must not be lower then '-1'");

		commerceCartService.addToCart(masterCartModel, productModel, 1, unitModel, false, -2, bundleTemplateModel, false,
				"<no xml>");
	}

	@Test
	public void testAddToCartNoEntryForBundleNumber() throws CommerceCartModificationException
	{
		final CartEntryModel cartEntryModel1 = mock(CartEntryModel.class);
		final List<CartEntryModel> allEntries = new ArrayList<CartEntryModel>();
		given(cartEntryDao.findEntriesByMasterCartAndBundleNo(masterCartModel, 1)).willReturn(allEntries);
		given(cartEntryModel1.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(cartEntryModel1.getBundleNo()).willReturn(Integer.valueOf(2));

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("No entry for bundleNo=1");

		commerceCartService
				.addToCart(masterCartModel, productModel, 1, unitModel, false, 1, bundleTemplateModel, false, "<no xml>");
	}

	@Test
	public void testAddToCartWrongBundleNoForTemplate() throws CommerceCartModificationException
	{
		final BundleTemplateModel rootBundleTemplateModel = mock(BundleTemplateModel.class);
		final BundleTemplateModel bundleTemplateModel2 = mock(BundleTemplateModel.class);
		final BundleTemplateModel rootBundleTemplateModel2 = mock(BundleTemplateModel.class);
		final CartEntryModel cartEntryModel1 = mock(CartEntryModel.class);
		final List<CartEntryModel> allEntries = new ArrayList<CartEntryModel>();
		allEntries.add(cartEntryModel1);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNo(masterCartModel, 4)).willReturn(allEntries);
		given(cartEntryModel1.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(rootBundleTemplateModel);
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel2)).willReturn(rootBundleTemplateModel2);
		given(cartEntryModel1.getBundleNo()).willReturn(Integer.valueOf(4));

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("cannot be added to bundle 4");

		commerceCartService.addToCart(masterCartModel, productModel, 1, unitModel, false, 4, bundleTemplateModel2, false,
				"<no xml>");
	}

	@Test
	public void testAddToCartWrongBundleQuantity() throws CommerceCartModificationException
	{
		final CartEntryModel cartEntryModel = mock(CartEntryModel.class);

		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		//YTODO given(cartEntryModel.getMasterAbstractOrder()).willReturn(masterCartModel);
		final List<AbstractOrderEntryModel> allEntries = new ArrayList<AbstractOrderEntryModel>();
		allEntries.add(cartEntryModel);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(productModel);
		given(bundleTemplateModel.getProducts()).willReturn(products);
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("quantityToAdd (2) exceeds the max. allowed quantity");

		commerceCartService.addToCart(masterCartModel, productModel, 2, unitModel, false, -1, bundleTemplateModel, false,
				"<no xml>");

	}

	@Test
	public void testAddToCartWhenProductNotInTemplate() throws CommerceCartModificationException
	{
		final List<ProductModel> products = new ArrayList<ProductModel>();
		given(bundleTemplateModel.getProducts()).willReturn(products);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("not in the products list of component (bundle template)");

		commerceCartService.addToCart(masterCartModel, productModel, 1, unitModel, false, -1, bundleTemplateModel, false,
				"<no xml>");
	}

	@Test
	public void testAddToCartNotSoldIndividuallyInBundle() throws CommerceCartModificationException
	{
		final CartEntryModel cartEntryModel = mock(CartEntryModel.class);

		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);
		given(productModel.getSoldIndividually()).willReturn(Boolean.FALSE);
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(productModel);
		given(bundleTemplateModel.getProducts()).willReturn(products);
		final List<CartEntryModel> allCartEntries = new ArrayList<CartEntryModel>();
		allCartEntries.add(cartEntryModel);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNo(masterCartModel, 1)).willReturn(allCartEntries);
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateModel, 1))).willReturn(
				Boolean.TRUE);
		given(productModel.getMaxOrderQuantity()).willReturn(null);
		given(commerceAddToCartStrategy.addToCart(null)).willReturn(null);
		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantityAdded(1);
		doReturn(mod).when(commerceAddToCartStrategy).addToCart(Mockito.any(CommerceCartParameter.class));

		final List<CommerceCartModification> modifications = commerceCartService.addToCart(masterCartModel, productModel, 1,
				unitModel, false, 1, bundleTemplateModel, false, "<no xml>");
		assertEquals(1, modifications.size());
		final long actualAdded = modifications.iterator().next().getQuantityAdded();
		assertEquals(1, actualAdded);
	}

	@Test
	public void testAddToCartNotSoldIndividuallyNoBundle() throws CommerceCartModificationException
	{
		final CartEntryModel cartEntryModel = mock(CartEntryModel.class);

		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		final List<AbstractOrderEntryModel> allEntries = new ArrayList<AbstractOrderEntryModel>();
		allEntries.add(cartEntryModel);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);
		given(productModel.getSoldIndividually()).willReturn(Boolean.FALSE);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("must not be sold individually");

		commerceCartService.addToCart(masterCartModel, productModel, 1, unitModel, false, 0, null, false, "<no xml>");
	}

	@Test
	public void testAddToCartWhenAutoPick() throws CommerceCartModificationException
	{
		given(bundleTemplateModel.getBundleSelectionCriteria()).willReturn(new AutoPickBundleSelectionCriteriaModel());

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("is not in the products list of component (bundle template)");

		commerceCartService.addToCart(masterCartModel, productModel, 1, unitModel, false, -1, bundleTemplateModel, false,
				"<no xml>");
	}

	@Test
	public void testAddToCart2ProductsWhenNoBundleNumber() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleNo '0' is not allowed");

		commerceCartService.addToCart(masterCartModel, unitModel, 0, productModel, bundleTemplateModel, subscriptionProductModel,
				bundleTemplateModel, "<no xml>", "<no xml>");
	}

	@Test
	public void testAddToCart2ProductsWhenNoCart() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterCartModel can not be null");

		commerceCartService.addToCart(null, unitModel, -1, productModel, bundleTemplateModel, subscriptionProductModel,
				bundleTemplateModel, "<no xml>", "<no xml>");
	}

	@Test
	public void testAddToCart2ProductsWhenNoMasterCart() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("not a master cart");

		commerceCartService.addToCart(childCartModelMonthly, unitModel, -1, productModel, bundleTemplateModel,
				subscriptionProductModel, bundleTemplateModel, "<no xml>", "<no xml>");
	}

	@Test
	public void testAddToCart2ProductsWhenNoProduct1() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("productModel1 can not be null");

		commerceCartService.addToCart(masterCartModel, unitModel, -1, null, bundleTemplateModel, subscriptionProductModel,
				bundleTemplateModel, "<no xml>", "<no xml>");
	}

	@Test
	public void testAddToCart2ProductsWhenNoProduct2() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("productModel2 can not be null");

		commerceCartService.addToCart(masterCartModel, unitModel, -1, productModel, bundleTemplateModel, null, bundleTemplateModel,
				"<no xml>", "<no xml>");
	}

	@Test
	public void testAddToCart2ProductsWhenNoTemplate1() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplateModel1 can not be null");

		commerceCartService.addToCart(masterCartModel, unitModel, -1, productModel, null, subscriptionProductModel,
				bundleTemplateModel, "<no xml>", "<no xml>");
	}

	@Test
	public void testAddToCart2ProductsWhenNoTemplate2() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplateModel2 can not be null");

		commerceCartService.addToCart(masterCartModel, unitModel, -1, productModel, bundleTemplateModel, subscriptionProductModel,
				null, "<no xml>", "<no xml>");
	}

	@Test
	public void testAddToCart2ProductsWhenAutoPick1() throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplateModel2 = mock(BundleTemplateModel.class);
		given(bundleTemplateModel.getBundleSelectionCriteria()).willReturn(new AutoPickBundleSelectionCriteriaModel());
		given(bundleTemplateModel2.getBundleSelectionCriteria()).willReturn(new PickExactlyNBundleSelectionCriteriaModel());

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("is not in the products list of component (bundle template)");

		commerceCartService.addToCart(masterCartModel, unitModel, -1, productModel, bundleTemplateModel, subscriptionProductModel,
				bundleTemplateModel2, "<no xml>", "<no xml>");
	}

	@Test
	public void testAddToCart2ProductsWhenAutoPick2() throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplateModel2 = mock(BundleTemplateModel.class);
		given(bundleTemplateModel.getBundleSelectionCriteria()).willReturn(new PickExactlyNBundleSelectionCriteriaModel());
		given(bundleTemplateModel2.getBundleSelectionCriteria()).willReturn(new AutoPickBundleSelectionCriteriaModel());

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("is not in the products list of component (bundle template)");

		commerceCartService.addToCart(masterCartModel, unitModel, -1, productModel, bundleTemplateModel, subscriptionProductModel,
				bundleTemplateModel2, "<no xml>", "<no xml>");
	}

	@Test
	public void testRemoveAllEntriesWhenCartIsNull() throws CommerceCartModificationException, CalculationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterCartModel can not be null");

		commerceCartService.removeAllEntries(null, 1);
	}

	@Test
	public void testRemoveAllEntriesBundle() throws CommerceCartModificationException, CalculationException
	{
		final CartEntryModel cartEntryModel = mock(CartEntryModel.class);

		given(cartService.addNewEntry(masterCartModel, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false)).willReturn(
				cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(cartEntryModel.getEntryNumber()).willReturn(Integer.valueOf(0));
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);
		final List<CartEntryModel> allCartEntries = new ArrayList<CartEntryModel>();
		allCartEntries.add(cartEntryModel);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNo(masterCartModel, 1)).willReturn(allCartEntries);

		// remove existing bundle 1
		commerceCartService.removeAllEntries(masterCartModel, 1);
		verify(commerceCartCalculationStrategy, times(2)).calculateCart(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testRemoveAllEntriesFromUnknownBundle() throws CommerceCartModificationException, CalculationException
	{
		final CartEntryModel cartEntryModel = mock(CartEntryModel.class);

		given(cartService.addNewEntry(masterCartModel, subscriptionProductModel, 1, unitModel, APPEND_AS_LAST, false)).willReturn(
				cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		final List<AbstractOrderEntryModel> allEntries = new ArrayList<AbstractOrderEntryModel>();
		allEntries.add(cartEntryModel);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(cartEntryModel.getEntryNumber()).willReturn(Integer.valueOf(0));
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("BundleNo 99 does not exist");

		// remove an unknown bundle 
		commerceCartService.removeAllEntries(masterCartModel, 99);
	}

	@Test
	public void testUpdateQuantityForCartEntryWhenCartIsNull() throws CommerceCartModificationException, CalculationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterCartModel can not be null");

		commerceCartService.updateQuantityForCartEntry(null, 0, 0);
	}

	@Test
	public void testUpdateQuantityForCartEntryWhenNoMasterCartIsGiven() throws CommerceCartModificationException,
			CalculationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("not a master cart");

		commerceCartService.updateQuantityForCartEntry(childCartModelMonthly, 0, 0);
	}

	@Test
	public void testUpdateQuantityForCartEntryWrongQuantity() throws CommerceCartModificationException, CalculationException
	{
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		final List<AbstractOrderEntryModel> allEntries = new ArrayList<AbstractOrderEntryModel>();
		allEntries.add(cartEntryModel);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartEntryModel.getProduct()).willReturn(productModel);
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(cartEntryModel.getEntryNumber()).willReturn(Integer.valueOf(0));
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);
		Mockito.doReturn(cartEntryModel).when(subscriptionCommerceCartStrategy).getEntryForNumber(masterCartModel, 0);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("must have a new quantity of 0");

		commerceCartService.updateQuantityForCartEntry(masterCartModel, 0, 2);
	}

	@Test
	public void testUpdateQuantityForCartEntryStandalone() throws CommerceCartModificationException, CalculationException
	{
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		final List<AbstractOrderEntryModel> allEntries = new ArrayList<AbstractOrderEntryModel>();
		allEntries.add(cartEntryModel);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(0));
		given(cartEntryModel.getProduct()).willReturn(productModel);
		given(cartEntryModel.getBundleTemplate()).willReturn(null);
		given(cartEntryModel.getEntryNumber()).willReturn(Integer.valueOf(0));
		given(productModel.getMaxOrderQuantity()).willReturn(null);
		Mockito.doReturn(cartEntryModel).when(subscriptionCommerceCartStrategy).getEntryForNumber(masterCartModel, 0);
		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantityAdded(2);
		mod.setEntry(cartEntryModel);
		Mockito.doReturn(mod).when(commerceUpdateCartEntryStrategy)
				.updateQuantityForCartEntry(Mockito.any(CommerceCartParameter.class));

		final long actualAdded = commerceCartService.updateQuantityForCartEntry(masterCartModel, 0, 2).getQuantityAdded();

		assertEquals(2, actualAdded);
		verify(commerceUpdateCartEntryStrategy, times(1)).updateQuantityForCartEntry(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testUpdateQuantityForCartEntryWhenAutoPick() throws CommerceCartModificationException
	{
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		final List<AbstractOrderEntryModel> allEntries = new ArrayList<AbstractOrderEntryModel>();
		allEntries.add(cartEntryModel);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartEntryModel.getProduct()).willReturn(productModel);
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(cartEntryModel.getEntryNumber()).willReturn(Integer.valueOf(0));
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);
		given(bundleTemplateModel.getBundleSelectionCriteria()).willReturn(new AutoPickBundleSelectionCriteriaModel());

		thrown.expect(CommerceCartModificationException.class);
		commerceCartService.updateQuantityForCartEntry(masterCartModel, 0, 0);
	}

	@Test
	public void testUpdateQuantityForCartEntryBundle() throws CommerceCartModificationException, CalculationException
	{
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		final List<AbstractOrderEntryModel> allEntries = new ArrayList<AbstractOrderEntryModel>();
		allEntries.add(cartEntryModel);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartEntryModel.getProduct()).willReturn(productModel);
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(cartEntryModel.getEntryNumber()).willReturn(Integer.valueOf(0));
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		when(Boolean.valueOf(orderEntryRemoveableChecker.canRemove(cartEntryModel))).thenReturn(Boolean.TRUE);
		Mockito.doReturn(cartEntryModel).when(subscriptionCommerceCartStrategy).getEntryForNumber(masterCartModel, 0);
		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantityAdded(0);
		Mockito.doReturn(mod).when(commerceUpdateCartEntryStrategy)
				.updateQuantityForCartEntry(Mockito.any(CommerceCartParameter.class));

		final long actualAdded = commerceCartService.updateQuantityForCartEntry(masterCartModel, 0, 0).getQuantityAdded();

		assertEquals(0, actualAdded);
		verify(commerceUpdateCartEntryStrategy, times(1)).updateQuantityForCartEntry(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testSetCartEntriesInSameBundleToNotCalculatedMasterCartEntry()
	{
		final CartEntryModel masterCartEntry = new CartEntryModel();
		final CartEntryModel masterCartEntry2 = new CartEntryModel();
		masterCartEntry.setOrder(masterCartModel);
		masterCartEntry2.setOrder(masterCartModel);
		masterCartEntry.setBundleNo(Integer.valueOf(1));
		masterCartEntry2.setBundleNo(Integer.valueOf(1));
		masterCartEntry.setCalculated(Boolean.TRUE);
		masterCartEntry2.setCalculated(Boolean.TRUE);
		masterCartEntry.setChildEntries(Collections.EMPTY_LIST);
		masterCartEntry2.setChildEntries(Collections.EMPTY_LIST);

		final List<CartEntryModel> allMasterCartEntries = new ArrayList<CartEntryModel>();
		allMasterCartEntries.add(masterCartEntry);
		allMasterCartEntries.add(masterCartEntry2);
		final List<AbstractOrderEntryModel> allMasterOrderEntries = new ArrayList<AbstractOrderEntryModel>();
		allMasterOrderEntries.add(masterCartEntry);
		allMasterOrderEntries.add(masterCartEntry2);
		given(masterCartModel.getEntries()).willReturn(allMasterOrderEntries);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNo(masterCartModel, 1)).willReturn(allMasterCartEntries);

		commerceCartService.setCartEntriesInSameBundleToNotCalculated(masterCartEntry);

		assertEquals(Boolean.TRUE, masterCartEntry.getCalculated());
		assertEquals(Boolean.FALSE, masterCartEntry2.getCalculated());
	}

	@Test
	public void testSetCartEntriesInSameBundleToNotCalculatedChildCartEntry()
	{
		final CartEntryModel masterCartEntry = new CartEntryModel();
		final CartEntryModel masterCartEntry2 = new CartEntryModel();
		final CartEntryModel cartEntry = new CartEntryModel();
		final CartEntryModel cartEntry2 = new CartEntryModel();

		masterCartEntry.setOrder(masterCartModel);
		masterCartEntry2.setOrder(masterCartModel);
		masterCartEntry.setCalculated(Boolean.TRUE);
		masterCartEntry2.setCalculated(Boolean.TRUE);
		masterCartEntry.setBundleNo(Integer.valueOf(1));
		masterCartEntry2.setBundleNo(Integer.valueOf(1));
		masterCartEntry.setChildEntries(Collections.singletonList((AbstractOrderEntryModel) cartEntry));
		masterCartEntry2.setChildEntries(Collections.singletonList((AbstractOrderEntryModel) cartEntry2));

		cartEntry.setOrder(childCartModelMonthly);
		cartEntry.setMasterEntry(masterCartEntry);
		cartEntry.setCalculated(Boolean.TRUE);

		cartEntry2.setOrder(childCartModelMonthly);
		cartEntry2.setMasterEntry(masterCartEntry2);
		cartEntry2.setCalculated(Boolean.TRUE);

		final List<AbstractOrderEntryModel> allMasterEntries = new ArrayList<AbstractOrderEntryModel>();
		allMasterEntries.add(masterCartEntry);
		allMasterEntries.add(masterCartEntry2);

		given(cartEntryDao.findEntriesByMasterCartAndBundleNo(masterCartModel, 1)).willReturn(allMasterEntries);
		given(masterCartModel.getEntries()).willReturn(allMasterEntries);

		commerceCartService.setCartEntriesInSameBundleToNotCalculated(cartEntry);

		assertEquals(Boolean.TRUE, cartEntry.getCalculated());
		assertEquals(Boolean.FALSE, cartEntry2.getCalculated());
		assertEquals(Boolean.FALSE, masterCartEntry.getCalculated());
		assertEquals(Boolean.FALSE, masterCartEntry2.getCalculated());
	}

	@Test
	public void testCalculateBundleCartNoMasterCart()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("cartModel can not be null");

		commerceCartService.calculateCart((CartModel) null);
	}

	@Test
	public void testAddToBundle() throws CommerceCartModificationException
	{

		final CartEntryModel masterCartEntry = new CartEntryModel();
		final CartEntryModel cartEntry = new CartEntryModel();
		final CartEntryModel cartEntry2 = new CartEntryModel();
		final CartEntryModel newCartEntry = new CartEntryModel();

		masterCartEntry.setOrder(masterCartModel);
		//YTODO masterCartEntry.setMasterAbstractOrder(masterCartModel);
		masterCartEntry.setCalculated(Boolean.TRUE);
		masterCartEntry.setBundleNo(Integer.valueOf(1));

		cartEntry.setOrder(childCartModelMonthly);
		//YTODO cartEntry.setMasterAbstractOrder(masterCartModel);
		cartEntry.setBundleNo(Integer.valueOf(1));
		cartEntry.setCalculated(Boolean.TRUE);

		cartEntry2.setOrder(childCartModelMonthly);
		//YTODO cartEntry2.setMasterAbstractOrder(masterCartModel);
		cartEntry2.setBundleNo(Integer.valueOf(1));
		cartEntry2.setCalculated(Boolean.TRUE);

		final List<AbstractOrderEntryModel> allEntries = new ArrayList<AbstractOrderEntryModel>();
		allEntries.add(cartEntry);
		allEntries.add(cartEntry2);
		allEntries.add(masterCartEntry);
		given(masterCartModel.getEntries()).willReturn(allEntries);

		newCartEntry.setOrder(masterCartModel);
		//YTODO newCartEntry.setMasterAbstractOrder(masterCartModel);

		commerceCartService.addToBundle(newCartEntry, bundleTemplateModel, -1);
		assertEquals(2, newCartEntry.getBundleNo().intValue());
	}

	@Test
	public void testCheckSelectionCriteriaNotExceededWhenNoCart() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterCart can not be null");

		commerceCartService.checkIsSelectionCriteriaNotExceeded(null, new BundleTemplateModel(), 1);
	}

	@Test
	public void testCheckSelectionCriteriaNotExceededWhenNoTemplate() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		commerceCartService.checkIsSelectionCriteriaNotExceeded(new CartModel(), null, 1);
	}

	@Test
	public void testCheckSelectionCriteriaNotExceededWhenExactlyNExceeded() throws CommerceCartModificationException
	{
		final List<CartEntryModel> allEntries = new ArrayList<CartEntryModel>();
		PickExactlyNBundleSelectionCriteriaModel pickExactlyNBundleSelectionCriteria;
		pickExactlyNBundleSelectionCriteria = mock(PickExactlyNBundleSelectionCriteriaModel.class);
		given(pickExactlyNBundleSelectionCriteria.getN()).willReturn(Integer.valueOf(1));
		given(bundleTemplateModel.getBundleSelectionCriteria()).willReturn(pickExactlyNBundleSelectionCriteria);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, 1, bundleTemplateModel)).willReturn(
				allEntries);

		commerceCartService.checkIsSelectionCriteriaNotExceeded(masterCartModel, bundleTemplateModel, 1);

		// add a cart entry so that the max. quantity will be exceeded
		allEntries.add(cartEntryModel);
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(1));

		thrown.expect(CommerceCartModificationException.class);

		commerceCartService.checkIsSelectionCriteriaNotExceeded(masterCartModel, bundleTemplateModel, 1);
	}

	@Test
	public void testCheckSelectionCriteriaNotExceededWhenNToMExceeded() throws CommerceCartModificationException
	{
		final List<CartEntryModel> allEntries = new ArrayList<CartEntryModel>();
		PickNToMBundleSelectionCriteriaModel pickNToMBundleSelectionCriteria;
		pickNToMBundleSelectionCriteria = mock(PickNToMBundleSelectionCriteriaModel.class);
		given(pickNToMBundleSelectionCriteria.getN()).willReturn(Integer.valueOf(1));
		given(pickNToMBundleSelectionCriteria.getM()).willReturn(Integer.valueOf(2));
		given(bundleTemplateModel.getBundleSelectionCriteria()).willReturn(pickNToMBundleSelectionCriteria);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, 1, bundleTemplateModel)).willReturn(
				allEntries);

		commerceCartService.checkIsSelectionCriteriaNotExceeded(masterCartModel, bundleTemplateModel, 1);

		// add a cart entry, the max. quantity will not yet be exceeded
		allEntries.add(cartEntryModel);
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(1));

		commerceCartService.checkIsSelectionCriteriaNotExceeded(masterCartModel, bundleTemplateModel, 1);

		// add another cart entry so that the max. quantity will be exceeded
		final CartEntryModel cartEntryModel2 = mock(CartEntryModel.class);
		allEntries.add(cartEntryModel2);
		given(cartEntryModel2.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(cartEntryModel2.getBundleNo()).willReturn(Integer.valueOf(1));

		thrown.expect(CommerceCartModificationException.class);

		commerceCartService.checkIsSelectionCriteriaNotExceeded(masterCartModel, bundleTemplateModel, 1);
	}

	@Test
	public void testCheckIsEntryRemovableWhenNoCartEntry() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("cartEntry can not be null");

		commerceCartService.checkIsEntryRemovable(null);
	}

	@Test
	public void testCheckSelectionCriteriaNotUnderThresholdWhenCriteriaNotMet() throws CommerceCartModificationException
	{
		given(cartEntryModel.getProduct()).willReturn(productModel);
		final BundleTemplateModel bundleTemplate = mock(BundleTemplateModel.class);
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplate);
		given(Boolean.valueOf(orderEntryRemoveableChecker.canRemove(cartEntryModel))).willReturn(Boolean.valueOf(false));

		thrown.expect(CommerceCartModificationException.class);

		commerceCartService.checkSelectionCriteriaNotUnderThreshold(cartEntryModel);
	}

	@Test
	public void testCheckIsComponentEditableWhenCartIsNull() throws CommerceCartModificationException, CalculationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterCart can not be null");

		commerceCartService.checkIsComponentEditable(null, new BundleTemplateModel(), 0);
	}

	@Test
	public void testCheckCanBeAddedToComponentWhenCartIsNull() throws CommerceCartModificationException, CalculationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterCart can not be null");

		commerceCartService.checkCanBeAddedToComponent(null, new BundleTemplateModel(), 0);
	}

	@Test
	public void testCheckIsComponentDependencyMetAfterRemoval() throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplateDevice = mock(BundleTemplateModel.class);
		final BundleTemplateModel bundleTemplatePlan = mock(BundleTemplateModel.class);
		final BundleTemplateModel bundleTemplateFee = mock(BundleTemplateModel.class);
		final BundleTemplateModel bundleTemplateAddOn = mock(BundleTemplateModel.class);

		// no other components in bundle
		commerceCartService.checkIsComponentDependencyMetAfterRemoval(masterCartModel, bundleTemplatePlan, 1);

		// no other component is dependent on bundleTemplateDevice
		final Collection<BundleTemplateModel> requiredTemplates = new HashSet<BundleTemplateModel>();
		requiredTemplates.add(bundleTemplatePlan);
		given(bundleTemplateAddOn.getRequiredBundleTemplates()).willReturn(requiredTemplates);
		final List<BundleTemplateModel> allBundleTemplates = new ArrayList<BundleTemplateModel>();
		allBundleTemplates.add(bundleTemplateDevice);
		allBundleTemplates.add(bundleTemplateFee);
		allBundleTemplates.add(bundleTemplatePlan);
		allBundleTemplates.add(bundleTemplateAddOn);
		given(bundleTemplateService.getTemplatesForMasterOrderAndBundleNo(masterCartModel, 1)).willReturn(allBundleTemplates);
		commerceCartService.checkIsComponentDependencyMetAfterRemoval(masterCartModel, bundleTemplateDevice, 1);

		// there is a dependency on bundleTemplatePlan but an entry could be removed from bundleTemplatePlan without invalidating it
		final List<CartEntryModel> planEntries = new ArrayList<CartEntryModel>();
		planEntries.add(cartEntryModel);
		planEntries.add(new CartEntryModel());
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, 1, bundleTemplatePlan)).willReturn(
				planEntries);
		given(Boolean.valueOf(orderEntryRemoveableChecker.canRemove(cartEntryModel))).willReturn(Boolean.valueOf(true));
		commerceCartService.checkIsComponentDependencyMetAfterRemoval(masterCartModel, bundleTemplatePlan, 1);

		// there is a dependency on bundleTemplatePlan and an entry cannot be removed from bundleTemplatePlan without invalidating it
		given(Boolean.valueOf(orderEntryRemoveableChecker.canRemove(cartEntryModel))).willReturn(Boolean.valueOf(false));
		given(cartEntryModel.getProduct()).willReturn(productModel);
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplatePlan);
		try
		{
			doReturn("cannot be removed").when(l10NService).getLocalizedString(Mockito.anyString(), Mockito.any(Object[].class));

			commerceCartService.checkIsComponentDependencyMetAfterRemoval(masterCartModel, bundleTemplatePlan, 1);
			fail();
		}
		catch (final CommerceCartModificationException e)
		{
			assertTrue(e.getMessage().indexOf("cannot be removed") > -1);
		}

		// there is a dependency on bundleTemplatePlan and only 1 entry left for bundleTemplatePlan
		planEntries.clear();
		planEntries.add(new CartEntryModel());

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("Cannot remove last product");

		commerceCartService.checkIsComponentDependencyMetAfterRemoval(masterCartModel, bundleTemplatePlan, 1);

	}

	@Test
	public void testIsNotValidComponent() throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();

		doThrow(new CommerceCartModificationException("illegal component")).when(bundleComponentEditableChecker)
				.checkIsComponentDependencyMet(masterCartModel, bundleTemplate, 1);
		Assert.assertFalse(commerceCartService.checkIsComponentSelectionCriteriaMet(masterCartModel, bundleTemplate, 1));
	}

	@Test
	public void testIsValidComponent() throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();

		given(Boolean.valueOf(bundleComponentEditableChecker.isComponentSelectionCriteriaMet(masterCartModel, bundleTemplate, 1)))
				.willReturn(Boolean.TRUE);
		Assert.assertTrue(commerceCartService.checkIsComponentSelectionCriteriaMet(masterCartModel, bundleTemplate, 1));
		verify(bundleComponentEditableChecker).isComponentSelectionCriteriaMet(masterCartModel, bundleTemplate, 1);
	}

	@Test
	public void testCheckIsComponentSelectionCriteriaMetWhenCartIsNull() throws CommerceCartModificationException,
			CalculationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterCart can not be null");

		commerceCartService.checkIsComponentSelectionCriteriaMet(null, new BundleTemplateModel(), 0);
	}

	@Test
	public void testCheckIsProductAlreadyInComponent() throws CommerceCartModificationException
	{
		final CartModel masterCart = mock(CartModel.class);
		final BundleTemplateModel bundleTemplate = mock(BundleTemplateModel.class);
		final ProductModel product = mock(ProductModel.class);

		final List<CartEntryModel> cartEntries = new ArrayList<CartEntryModel>();

		// no bundle
		commerceCartService.checkIsProductAlreadyInComponent(masterCart, DefaultBundleCommerceCartService.NO_BUNDLE,
				bundleTemplate, product);

		// bundle, but no entries for component
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCart, 1, bundleTemplate)).willReturn(cartEntries);
		commerceCartService.checkIsProductAlreadyInComponent(masterCart, 1, bundleTemplate, product);

		// bundle, entries for given template, but not for given product
		final CartEntryModel cartEntry1 = mock(CartEntryModel.class);
		given(cartEntry1.getProduct()).willReturn(new ProductModel());
		cartEntries.add(cartEntry1);
		commerceCartService.checkIsProductAlreadyInComponent(masterCart, 1, bundleTemplate, product);

		// bundle, entries for given template and given product exists for given template
		final CartEntryModel cartEntry2 = mock(CartEntryModel.class);
		given(cartEntry2.getProduct()).willReturn(product);
		cartEntries.add(cartEntry2);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("already in the cart for component (bundle template)");

		commerceCartService.checkIsProductAlreadyInComponent(masterCart, 1, bundleTemplate, product);
	}

	@Test
	public void testGetFirstInvalidComponentInCartWhenNoCart()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterCart can not be null");

		commerceCartService.getFirstInvalidComponentInCart(null);
	}

	@Test
	public void testGetFirstInvalidComponentInCart()
	{
		final CartModel masterCart = mock(CartModel.class);

		// cart is empty
		final List<AbstractOrderEntryModel> cartEntries = new ArrayList<AbstractOrderEntryModel>();
		given(masterCart.getEntries()).willReturn(cartEntries);
		BundleTemplateModel invalidComponent = commerceCartService.getFirstInvalidComponentInCart(masterCart);
		assertNull(invalidComponent);

		// standalone device in cart
		final AbstractOrderEntryModel cartStandaloneEntryDevice = mock(CartEntryModel.class);
		cartEntries.add(cartStandaloneEntryDevice);
		invalidComponent = commerceCartService.getFirstInvalidComponentInCart(masterCart);
		assertNull(invalidComponent);

		// create a package/cart with device component 
		final BundleTemplateModel parentComponent = mock(BundleTemplateModel.class);
		final BundleTemplateModel deviceComponent = mock(BundleTemplateModel.class);
		given(deviceComponent.getParentTemplate()).willReturn(parentComponent);
		given(bundleTemplateService.getRootBundleTemplate(deviceComponent)).willReturn(parentComponent);
		final List<BundleTemplateModel> childTemplates = new ArrayList<BundleTemplateModel>();
		childTemplates.add(deviceComponent);
		given(parentComponent.getChildTemplates()).willReturn(childTemplates);

		// device component in cart; ComponentDependency: component relevant, selection criteria fulfilled
		final AbstractOrderEntryModel cartBundleEntryDevice = mock(CartEntryModel.class);
		given(cartBundleEntryDevice.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartBundleEntryDevice.getBundleTemplate()).willReturn(deviceComponent);
		cartEntries.add(cartBundleEntryDevice);
		given(Boolean.valueOf(bundleComponentEditableChecker.isComponentDependencyMet(masterCart, deviceComponent, 1))).willReturn(
				Boolean.TRUE);
		given(Boolean.valueOf(bundleComponentEditableChecker.isComponentSelectionCriteriaMet(masterCart, deviceComponent, 1)))
				.willReturn(Boolean.TRUE);
		invalidComponent = commerceCartService.getFirstInvalidComponentInCart(masterCart);
		assertNull(invalidComponent);

		// device component in cart; ComponentDependency: component relevant, selection criteria not fulfilled
		given(Boolean.valueOf(bundleComponentEditableChecker.isComponentSelectionCriteriaMet(masterCart, deviceComponent, 1)))
				.willReturn(Boolean.FALSE);
		invalidComponent = commerceCartService.getFirstInvalidComponentInCart(masterCart);
		assertEquals(deviceComponent, invalidComponent);

		// device component in cart; ComponentDependency: component not relevant, selection criteria not fulfilled
		given(Boolean.valueOf(bundleComponentEditableChecker.isComponentDependencyMet(masterCart, deviceComponent, 1))).willReturn(
				Boolean.FALSE);
		invalidComponent = commerceCartService.getFirstInvalidComponentInCart(masterCart);
		assertNull(invalidComponent);

		// device component in cart; ComponentDependency: component not relevant, selection criteria fulfilled
		given(Boolean.valueOf(bundleComponentEditableChecker.isComponentSelectionCriteriaMet(masterCart, deviceComponent, 1)))
				.willReturn(Boolean.TRUE);
		invalidComponent = commerceCartService.getFirstInvalidComponentInCart(masterCart);
		assertNull(invalidComponent);

		// add a plan component to package/cart 
		final BundleTemplateModel planComponent = mock(BundleTemplateModel.class);
		given(planComponent.getParentTemplate()).willReturn(parentComponent);
		given(bundleTemplateService.getRootBundleTemplate(planComponent)).willReturn(parentComponent);
		childTemplates.add(planComponent);

		// device, plan component in cart; ComponentDependency: plan component relevant, plan selection criteria not fulfilled
		final AbstractOrderEntryModel cartBundleEntryPlan = mock(CartEntryModel.class);
		given(cartBundleEntryPlan.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartBundleEntryPlan.getBundleTemplate()).willReturn(planComponent);
		cartEntries.add(cartBundleEntryPlan);
		given(Boolean.valueOf(bundleComponentEditableChecker.isComponentDependencyMet(masterCart, planComponent, 1))).willReturn(
				Boolean.TRUE);
		given(Boolean.valueOf(bundleComponentEditableChecker.isComponentSelectionCriteriaMet(masterCart, planComponent, 1)))
				.willReturn(Boolean.FALSE);
		invalidComponent = commerceCartService.getFirstInvalidComponentInCart(masterCart);
		assertEquals(planComponent, invalidComponent);

		// add a tv component to package/cart 
		final BundleTemplateModel tvComponent = mock(BundleTemplateModel.class);
		given(tvComponent.getParentTemplate()).willReturn(parentComponent);
		given(bundleTemplateService.getRootBundleTemplate(tvComponent)).willReturn(parentComponent);
		childTemplates.add(tvComponent);

		// device, plan, tv component in cart; ComponentDependency: tv component relevant, tv selection criteria not fulfilled
		// plan component is returned as invalid as it comes before the invalid tv component
		final AbstractOrderEntryModel cartBundleEntryTv = mock(CartEntryModel.class);
		given(cartBundleEntryTv.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartBundleEntryTv.getBundleTemplate()).willReturn(planComponent);
		cartEntries.add(cartBundleEntryTv);
		given(Boolean.valueOf(bundleComponentEditableChecker.isComponentDependencyMet(masterCart, tvComponent, 1))).willReturn(
				Boolean.TRUE);
		given(Boolean.valueOf(bundleComponentEditableChecker.isComponentSelectionCriteriaMet(masterCart, tvComponent, 1)))
				.willReturn(Boolean.FALSE);
		invalidComponent = commerceCartService.getFirstInvalidComponentInCart(masterCart);
		assertEquals(planComponent, invalidComponent);
	}

}