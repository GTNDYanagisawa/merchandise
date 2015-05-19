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
package de.hybris.platform.returns;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReplacementReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReplacementEntryModel;
import de.hybris.platform.returns.model.ReplacementOrderModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * "Returns Order" test cases
 * 
 */
public class ReturnsServiceTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(ReturnsServiceTest.class.getName());

	@Resource
	private ReturnService returnService;
	@Resource
	private CartService cartService;
	@Resource
	private OrderService orderService;
	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;

	private OrderModel order;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createOrder();
	}

	public static void createCoreData() throws Exception
	{
		LOG.info("Creating essential data for core ...");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		importCsv("/servicelayer/test/testBasics.csv", "windows-1252");
		LOG.info("Finished creating essential data for core in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public static void createDefaultCatalog() throws Exception
	{
		LOG.info("Creating test catalog ...");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();

		importCsv("/servicelayer/test/testCatalog.csv", "windows-1252");

		// checking imported stuff
		final CatalogVersion version = CatalogManager.getInstance().getCatalog("testCatalog").getCatalogVersion("Online");
		Assert.assertNotNull(version);
		JaloSession.getCurrentSession().getSessionContext().setAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS,
				Collections.singletonList(version));
		//setting catalog to session and admin user
		final Category category = CategoryManager.getInstance().getCategoriesByCode("testCategory0").iterator().next();
		Assert.assertNotNull(category);
		final Product product = (Product) ProductManager.getInstance().getProductsByCode("testProduct0").iterator().next();
		Assert.assertNotNull(product);
		Assert.assertEquals(category, CategoryManager.getInstance().getCategoriesByProduct(product).iterator().next());

		LOG.info("Finished creating test catalog in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public void createOrder() throws InvalidCartException
	{
		LOG.info("Creating order ...");

		final ProductModel product0 = productService.getProduct("testProduct0");
		final ProductModel product1 = productService.getProduct("testProduct1");
		final ProductModel product2 = productService.getProduct("testProduct2");
		final CartModel cart = cartService.getSessionCart();
		final UserModel user = userService.getCurrentUser();
		cartService.addToCart(cart, product0, 2, null);
		cartService.addToCart(cart, product1, 2, null);
		cartService.addToCart(cart, product2, 2, null);

		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setOwner(user);
		deliveryAddress.setFirstname("Albert");
		deliveryAddress.setLastname("Einstein");
		deliveryAddress.setTown("Munich");

		final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
		paymentInfo.setOwner(cart);
		paymentInfo.setBank("MyBank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Ich");

		order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
	}

	/**
	 * Checks ...
	 * <ul>
	 * <li>ReturnsRequest creation
	 * <li>ReturnsRequest search
	 * <li>order reference
	 * </ul>
	 */
	@Test
	public void testCreateReturnRequest()
	{
		// testing setup data
		assertNotNull("Missing order instance ", order);
		assertEquals("There should be no existing ReturnRequest instance for the assigned order", 0, returnService
				.getReturnRequests(order.getCode()).size());

		// 'ReturnRequest': create, search, order reference check
		returnService.createReturnRequest(order); // no.1

		// testing 'search by order'
		final List<ReturnRequestModel> requests = returnService.getReturnRequests(order.getCode());
		assertEquals("Search should returns one ReturnRequest instance", 1, returnService.getReturnRequests(order.getCode()).size());

		returnService.createReturnRequest(order); // no.2
		assertEquals("Search should returns two ReturnRequest instance", 2, returnService.getReturnRequests(order.getCode()).size());

		// checking Order reference
		for (final ReturnRequestModel request : requests)
		{
			assertEquals("Wrong order assigned", order.getCode(), request.getOrder().getCode());
		}
	}

	/**
	 * Checks ...
	 * <ul>
	 * <li>RMA generation
	 * <li>Search for RMA based on the ReturnRequest
	 * </ul>
	 */
	@Test
	public void testRMAgeneration()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		assertNull("RMA should be <null> ", request.getRMA());
		request.setRMA(returnService.createRMA(request));
		assertNotNull("RMA shouldn't be <null> ", request.getRMA());
		assertEquals("Searching for RMA with the help of ReturnsService failed", request.getRMA(), returnService.getRMA(request));
	}

	/**
	 * Checks ...
	 * <ul>
	 * <li>replacement order creation
	 * <li>replacement entry creation
	 * <li>action
	 * <li>replacement reason
	 * <li>return reason
	 * <li>product reference
	 * <li>quantity
	 * <li>searching replacement order by RMA
	 * </ul>
	 */
	@Test
	public void testReplacementOrderCreation()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		request.setRMA(returnService.createRMA(request));

		final AbstractOrderEntryModel originalEntry = order.getEntries().iterator().next();
		final ReplacementOrderModel replacementOrder = returnService.createReplacementOrder(request);

		assertNotNull("ReplacementOrder instance shouldn't be <null> ", replacementOrder);
		assertNotNull("Returned ReplacementOrder instance shouldn't be <null> ", request.getReplacementOrder());

		final ReplacementEntryModel replacementEntry = returnService.createReplacement(request, originalEntry, "no.1", Long
				.valueOf(3), ReturnAction.IMMEDIATE, ReplacementReason.LATEDELIVERY);
		assertNotNull("ReplacementEntry shouldn't be <null> ", replacementEntry);
		assertEquals("Wrong reason assigned", replacementEntry.getReason(), ReplacementReason.LATEDELIVERY);
		assertEquals("Wrong reason assigned", replacementEntry.getAction(), ReturnAction.IMMEDIATE);
		assertEquals("Wrong product reference", replacementEntry.getOrderEntry().getProduct(), originalEntry.getProduct());
		assertEquals("Wrong expected quantity", replacementEntry.getExpectedQuantity(), Long.valueOf(3));

		returnService.addReplacementOrderEntries(replacementOrder, Arrays.asList(replacementEntry));

		assertEquals("There should be already an order entry assigned", 1, request.getReplacementOrder().getEntries().size());
		assertEquals("Wrong product reference", request.getReplacementOrder().getEntries().iterator().next().getProduct(),
				replacementEntry.getOrderEntry().getProduct());
	}

	/**
	 * Checks ...
	 * <ul>
	 * <li>refund creation
	 * <li>action
	 * <li>refund reason
	 * <li>product reference
	 * <li>quantity
	 * </ul>
	 */
	@Test
	public void testRefundEntryCreation()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		request.setRMA(returnService.createRMA(request));

		final AbstractOrderEntryModel originalEntry = order.getEntries().iterator().next();

		final RefundEntryModel refundEntry = returnService.createRefund(request, originalEntry, "no.1", Long.valueOf(3),
				ReturnAction.IMMEDIATE, RefundReason.LATEDELIVERY);
		assertNotNull("ReplacementEntry shouldn't be <null> ", refundEntry);
		assertEquals("Wrong reason assigned", refundEntry.getReason(), RefundReason.LATEDELIVERY);
		assertEquals("Wrong reason assigned", refundEntry.getAction(), ReturnAction.IMMEDIATE);
		assertEquals("Wrong product reference", refundEntry.getOrderEntry().getProduct(), originalEntry.getProduct());
		assertEquals("Wrong expected quantity", refundEntry.getExpectedQuantity(), Long.valueOf(3));

	}

	/**
	 * Checks ...
	 * <ul>
	 * <li>Search for 'returns entry' by 'product'
	 * <li>Search for 'returns entry' by 'order entry'
	 * </ul>
	 */
	@Test
	public void testReturnsEntryCreation()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		request.setRMA(returnService.createRMA(request));

		final AbstractOrderEntryModel originalEntry = order.getEntries().iterator().next();
		final ReplacementEntryModel replacementEntry = returnService.createReplacement(request, originalEntry, "no.1", Long
				.valueOf(3), ReturnAction.IMMEDIATE, ReplacementReason.LATEDELIVERY);

		final List<ReturnEntryModel> returnsEntry1 = returnService.getReturnEntries(originalEntry.getProduct());
		assertEquals("Search by product returns wrong 'returns' entry", returnsEntry1.iterator().next(), replacementEntry);
		final List<ReturnEntryModel> returnsEntry2 = returnService.getReturnEntry(originalEntry);
		assertEquals("Search by order entry returns wrong 'returns' entry", returnsEntry2.iterator().next(), replacementEntry);
	}

	@Test
	public void isReturnableTest()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		request.setRMA(returnService.createRMA(request));
		final AbstractOrderEntryModel originalEntry = order.getEntries().iterator().next();
		returnService.createRefund(request, originalEntry, "no.3", Long.valueOf(1), ReturnAction.IMMEDIATE,
				RefundReason.LATEDELIVERY);

		assertFalse(returnService.isReturnable(order, originalEntry, 1)); // missing consignment
		assertFalse(returnService.isReturnable(order, originalEntry, 2)); // wrong quantity
	}

	@Test
	public void getAllReturnableEntries()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		request.setRMA(returnService.createRMA(request));
		final Map<AbstractOrderEntryModel, Long> returnables = returnService.getAllReturnableEntries(order);
		for (final Map.Entry entry : returnables.entrySet())
		{
			assertEquals("Unexpected 'returnable'!", Long.valueOf(2), entry.getValue());
		}
	}
}
