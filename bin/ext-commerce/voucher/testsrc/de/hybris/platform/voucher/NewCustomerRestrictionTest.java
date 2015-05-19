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
package de.hybris.platform.voucher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.voucher.model.NewCustomerRestrictionModel;
import de.hybris.platform.voucher.model.VoucherModel;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests the {@link NewCustomerRestrictionModel}: only new customer are allowed to redeem the voucher.
 * 
 */
public class NewCustomerRestrictionTest extends ServicelayerTransactionalTest
{

	@Resource
	private VoucherService voucherService;
	@Resource
	private VoucherModelService voucherModelService;
	@Resource
	private UserService userService;
	@Resource
	private CartService cartService;
	@Resource
	private OrderService orderService;
	@Resource
	private ProductService productService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ModelService modelService;

	private CatalogVersionModel catVersion;
	private CartModel cart;
	private OrderModel order;
	private ProductModel product;
	private UserModel userDemo;
	private VoucherModel voucher;

	private final String voucherCode = "vo1";

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createDefaultCatalog();
		importCsv("/test/newCustomerRestrictionTestData.csv", "windows-1252");

		prepareData();
	}

	private void prepareData()
	{
		catVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(catVersion);

		userDemo = userService.getUserForUID("demo");
		userService.setCurrentUser(userDemo);

		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("EUR"));
		product = productService.getProductForCode("testProduct1");

		voucher = voucherService.getVoucher(voucherCode);
	}

	/**
	 * Tests the voucher with new user restriction(VOU-171):
	 * <ul>
	 * <li>adds a product to cart, and redeems the voucher which is applicable,</li>
	 * <li>creates an order from this cart,</li>
	 * <li>creates a new cart, and adds 2 products to it,</li>
	 * <li>the voucher is not applicable any more for the current user.</li>
	 * </ul>
	 */
	@Test
	public void testNewCustomerRestrictionVoucher() throws JaloPriceFactoryException, InvalidCartException
	{
		cart = prepareCart(product, 1);

		boolean result = voucherModelService.isApplicable(voucher, cart);
		assertTrue("voucher should be applicable", result);
		voucherService.redeemVoucher(voucherCode, cart);

		order = orderService.createOrderFromCart(cart);
		modelService.save(order);

		cartService.removeSessionCart();
		cart = prepareCart(product, 2);

		result = voucherModelService.isApplicable(voucher, cart);
		assertFalse("voucher should not be applicable", result);
	}

	private CartModel prepareCart(final ProductModel product, final int amount)
	{
		final CartModel cart = cartService.getSessionCart();
		cartService.addNewEntry(cart, product, amount, product.getUnit());
		modelService.save(cart);
		return cart;
	}

}
