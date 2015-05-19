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
package de.hybris.platform.voucher.jalo;

import static de.hybris.platform.testframework.Assert.assertCollection;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.order.CartEntry;
import de.hybris.platform.jalo.order.Order;
import de.hybris.platform.jalo.order.OrderEntry;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.jalo.order.price.ProductPriceInformations;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.voucher.constants.VoucherConstants;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for extension Voucher
 */
public class VoucherTest extends HybrisJUnit4TransactionalTest
{
	private VoucherManager manager;
	private PriceFactory sessionPriceFactoryBefore;

	@Before
	public void setUp() throws Exception
	{
		manager = (VoucherManager) jaloSession.getExtensionManager().getExtension(VoucherConstants.EXTENSIONNAME);
		replacePriceFactory();
	}

	protected void replacePriceFactory()
	{
		sessionPriceFactoryBefore = jaloSession.getPriceFactory();
		jaloSession.setPriceFactory(new MyPriceFactory());
	}

	@After
	public void tearDown()
	{
		restorePriceFactory();
	}

	protected void restorePriceFactory()
	{
		jaloSession.setPriceFactory(sessionPriceFactoryBefore);
	}

	@Test
	public void testVoucherTransfer() throws NoSuchAlgorithmException, JaloInvalidParameterException, ConsistencyCheckException,
			JaloPriceFactoryException
	{
		try
		{
			SerialVoucher sv = null;
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put(Voucher.CODE, "xyz");
			params.put(Voucher.CURRENCY, null);
			params.put(Voucher.VALUE, new Double(20.0));
			Assert.assertNotNull(sv = manager.createSerialVoucher(params));

			final String code1 = sv.generateVoucherCode();

			Cart cart = null;
			assertNotNull(cart = OrderManager.getInstance().createCart(jaloSession.getUser(),
					jaloSession.getSessionContext().getCurrency(), new Date(), false));
			Cart cart2 = null;
			assertNotNull(cart2 = OrderManager.getInstance().createCart(jaloSession.getUser(),
					jaloSession.getSessionContext().getCurrency(), new Date(), false));

			Product p = null;
			assertNotNull(p = ProductManager.getInstance().createProduct("foo"));

			Unit u = null;
			assertNotNull(u = ProductManager.getInstance().createUnit("foo", "bar"));

			final CartEntry ce = (CartEntry) cart.addNewEntry(p, 10, u);

			cart.recalculate();

			assertEquals(15.0, ce.getTotalPrice().doubleValue(), 0.000001);
			assertEquals(15.0, cart.getTotal(), 0.000001);

			final CartEntry ce2 = (CartEntry) cart2.addNewEntry(p, 100, u);

			cart2.recalculate();

			assertEquals(150.0, ce2.getTotalPrice().doubleValue(), 0.000001);
			assertEquals(150.0, cart2.getTotal(), 0.000001);

			de.hybris.platform.testframework.Assert.assertCollection(Collections.EMPTY_SET, manager.getAppliedVouchers(cart));
			assertCollection(Collections.EMPTY_LIST, manager.getAppliedVoucherCodes(cart));

			assertTrue(manager.redeemVoucher(code1, cart));

			assertEquals(12.0, cart.getTotal(), 0.000001);

			assertEquals(Collections.singleton(sv), manager.getAppliedVouchers(cart));

			// try to redeem again -> error
			assertFalse(manager.redeemVoucher(code1, cart));


			// try to redeem for second cart -> ok
			assertTrue(manager.redeemVoucher(code1, cart2));

			assertEquals(120.0, cart2.getTotal(), 0.000001);
			assertCollection(Collections.singleton(sv), manager.getAppliedVouchers(cart2));
			assertCollection(Collections.singletonList(code1), manager.getAppliedVoucherCodes(cart2));

			Order o = null;

			assertNotNull(o = OrderManager.getInstance().createOrder(cart));
			manager.afterOrderCreation(o, cart);

			final Collection<String> applied = new ArrayList<String>();
			for (final String c : manager.getAppliedVoucherCodes(o))
			{
				applied.add(c);
				// try to redeem again for order -> error since it has been already redeemed 
				assertNull(manager.redeemVoucher(c, o));
			}

			assertEquals(12.0, o.getTotal(), 0.000001);
			assertCollection(Collections.singleton(sv), manager.getAppliedVouchers(o));
			assertCollection(Collections.singletonList(code1), applied);

			Order o2 = null;
			Assert.assertNotNull(o2 = OrderManager.getInstance().createOrder("testord", jaloSession.getUser(),
					jaloSession.getSessionContext().getCurrency(), new Date(), false));

			final OrderEntry oe = (OrderEntry) o2.addNewEntry(p, 20, u);

			o2.recalculate();

			assertEquals(30.0, oe.getTotalPrice().doubleValue(), 0.000001);
			assertEquals(30.0, o2.getTotal(), 0.000001);

			final VoucherInvalidation inv = manager.redeemVoucher(code1, o2);
			if (inv != null)
			{
				Assert.assertNotNull(inv);
			}
			assertNull(inv);

			Order o3 = null;
			// clone cart2 -> voucher should be removed since it has already been consumed by o
			Assert.assertNotNull(o3 = OrderManager.getInstance().createOrder(cart2));
			manager.afterOrderCreation(o3, cart2);

			assertCollection(Collections.EMPTY_SET, manager.getAppliedVouchers(o3));
			assertCollection(Collections.EMPTY_LIST, manager.getAppliedVoucherCodes(o3));

			// total must be full sum now -> cart2 had 120.0 , order must have 150.0 !!! 
			assertEquals(150.0, o3.getTotal(), 0.000001);

			Cart cart3 = null;
			Assert.assertNotNull(cart3 = OrderManager.getInstance().createCart(jaloSession.getUser(),
					jaloSession.getSessionContext().getCurrency(), new Date(), false));

			final CartEntry ce3 = (CartEntry) cart3.addNewEntry(p, 1000, u);

			cart3.recalculate();

			assertEquals(1500.0, ce3.getTotalPrice().doubleValue(), 0.000001);
			assertEquals(1500.0, cart3.getTotal(), 0.000001);

			// now try to redeem voucher in new cart -> error since voucher code has been consumed by o
			assertFalse(manager.redeemVoucher(code1, cart3));

			assertCollection(Collections.EMPTY_SET, manager.getAppliedVouchers(cart3));
			assertCollection(Collections.EMPTY_LIST, manager.getAppliedVoucherCodes(cart3));

			Customer another = null;
			Assert.assertNotNull(another = UserManager.getInstance().createCustomer("another"));

			Order o4 = null;
			Assert.assertNotNull(o4 = OrderManager.getInstance().createOrder(another, jaloSession.getSessionContext().getCurrency(),
					new Date(), false));
			o4.addNewEntry(p, 1, u);
			o4.recalculate();
			assertEquals(1.5, o4.getTotal(), 0.000001);

			// try to redeem on order with different user
			assertNull(manager.redeemVoucher(code1, o4));

			assertEquals(1.5, o4.getTotal(), 0.000001);
			assertCollection(Collections.EMPTY_SET, manager.getAppliedVouchers(o4));
			assertCollection(Collections.EMPTY_LIST, manager.getAppliedVoucherCodes(o4));

			Cart cart4 = null;
			Assert.assertNotNull(cart4 = OrderManager.getInstance().createCart(another,
					jaloSession.getSessionContext().getCurrency(), new Date(), false));

			cart4.addNewEntry(p, 1, u);
			cart4.recalculate();
			assertEquals(1.5, cart4.getTotal(), 0.000001);

			// try to redeem on order with different user
			assertFalse(manager.redeemVoucher(code1, cart4));
			assertEquals(1.5, cart4.getTotal(), 0.000001);
			assertCollection(Collections.EMPTY_SET, manager.getAppliedVouchers(cart4));
			assertCollection(Collections.EMPTY_LIST, manager.getAppliedVoucherCodes(cart4));
		}
		finally
		{
			jaloSession.getSessionContext().setPriceFactory(null);
		}
	}

	private static class MyPriceFactory implements PriceFactory
	{

		public ProductPriceInformations getAllPriceInformations(final SessionContext ctx, final Product p, final Date date,
				final boolean net) throws JaloPriceFactoryException
		{
			return null;
		}

		public PriceValue getBasePrice(final AbstractOrderEntry entry) throws JaloPriceFactoryException
		{
			return new PriceValue(entry.getOrder().getCurrency().getIsoCode(), 1.5, entry.getOrder().isNet().booleanValue());
		}

		public List getDiscountValues(final AbstractOrderEntry entry) throws JaloPriceFactoryException
		{
			return Collections.EMPTY_LIST;
		}

		public List getDiscountValues(final AbstractOrder order) throws JaloPriceFactoryException
		{
			return Collections.EMPTY_LIST;
		}

		public List getProductDiscountInformations(final SessionContext ctx, final Product p, final Date date, final boolean net)
				throws JaloPriceFactoryException
		{
			return Collections.EMPTY_LIST;
		}

		public List getProductPriceInformations(final SessionContext ctx, final Product p, final Date date, final boolean net)
				throws JaloPriceFactoryException
		{
			return Collections.EMPTY_LIST;
		}

		public List getProductTaxInformations(final SessionContext ctx, final Product p, final Date date)
				throws JaloPriceFactoryException
		{
			return Collections.EMPTY_LIST;
		}

		public Collection getTaxValues(final AbstractOrderEntry entry) throws JaloPriceFactoryException
		{
			return Collections.EMPTY_LIST;
		}

		public boolean isNetUser(final User user)
		{
			return false;
		}
	}
}
