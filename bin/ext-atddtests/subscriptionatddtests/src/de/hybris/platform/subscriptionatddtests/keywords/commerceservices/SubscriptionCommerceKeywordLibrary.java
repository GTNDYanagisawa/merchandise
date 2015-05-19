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
package de.hybris.platform.subscriptionatddtests.keywords.commerceservices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.atddengine.keywords.AbstractKeywordLibrary;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.entitlementfacades.data.EntitlementData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.subscriptionfacades.converters.SubscriptionXStreamAliasConverter;
import de.hybris.platform.subscriptionfacades.order.SubscriptionCartFacade;
import de.hybris.platform.util.DiscountValue;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Robotframework library for commerce cart related keywords
 */
public class SubscriptionCommerceKeywordLibrary extends AbstractKeywordLibrary
{
	private static final Logger LOG = Logger.getLogger(SubscriptionCommerceKeywordLibrary.class);

	private static final double DELTA = 0.01;

	@Autowired
	private CartService cartService;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private SubscriptionXStreamAliasConverter xStreamAliasConverter;

	@Autowired
	private CheckoutFacade checkoutFacade;

	@Autowired
	private ProductFacade productFacade;

	@Autowired
	private StoreSessionFacade storeSessionFacade;

	@Autowired
	private UserFacade userFacade;

	@Autowired
	private SubscriptionCartFacade cartFacade;

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify number of child carts</i>
	 * <p>
	 * 
	 * @param expectedCartCount
	 *           the expected number of child carts
	 */
	public void verifyNumberOfChildCarts(final int expectedCartCount)
	{
		assertEquals("Number of child carts does not match the expected value", expectedCartCount, cartService.getSessionCart()
				.getChildren().size());
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify cart total for billing event</i>
	 * <p>
	 * 
	 * @param billingEventCode
	 *           the billing event to verify the order total for
	 * @param expectedTotal
	 *           the expected order total for the billing event
	 */
	public void verifyCartTotalForBillingEvent(final String billingEventCode, final double expectedTotal)
	{
		final AbstractOrderModel orderToCheck = getOrderForBillingEvent(billingEventCode);
		double orderTotal;

		try
		{
			if (orderToCheck == null)
			{
				orderTotal = 0;
			}
			else
			{
				orderTotal = orderToCheck.getTotalPrice().doubleValue();
			}
			assertEquals("The order total for billing event [" + billingEventCode + "] does not match the expected value",
					expectedTotal, orderTotal, DELTA);
		}
		catch (final Exception e)
		{
			LOG.error("", e);
		}

	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify timeunits for entitlements of product</i>
	 * <p>
	 * 
	 * @param productCode
	 */
	public void verifyTimeunitsForEntitlementsOfProduct(final String productCode)
	{
		final ProductData product = productFacade.getProductForCodeAndOptions(productCode,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));

		final List<EntitlementData> entitlements = product.getEntitlements();

		assertNotNull(entitlements);

		for (final EntitlementData entitlement : entitlements)
		{
			assertNotNull(entitlement.getTimeUnit());
			assertNotNull(entitlement.getTimeUnitStart());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>get discount price</i>
	 * <p>
	 * 
	 * @param billingEventCode
	 *           the billing event to verify the discount price for
	 * @param expectedDiscountPrice
	 *           the expected discount price for the billing event
	 */
	public void getDiscountPrice(final String billingEventCode, final double expectedDiscountPrice)
	{
		final AbstractOrderModel orderToCheck = getOrderForBillingEvent(billingEventCode);

		assertNotNull("No order found for the given billing event [" + billingEventCode + "]", orderToCheck);
		double discounts = 0;
		final List<AbstractOrderEntryModel> entries = orderToCheck.getEntries();
		if (entries != null)
		{
			for (final AbstractOrderEntryModel entry : entries)
			{
				final List<DiscountValue> discountValues = entry.getDiscountValues();
				if (discountValues != null)
				{
					for (final DiscountValue dValue : discountValues)
					{
						discounts += dValue.getAppliedValue();
					}
				}
			}
		}

		assertEquals("The discount for billing event [" + billingEventCode + "] does not match the expected value",
				expectedDiscountPrice, discounts, DELTA);

	}

	/**
	 * Switches the currency of the session to the given value. This logic is copied from
	 * StoreSessionController.selectCurrency
	 * 
	 * @param currency
	 *           the currency to switch to
	 */
	public void setCartCurrencyTo(final String currency)
	{
		storeSessionFacade.setCurrentCurrency(currency);
		userFacade.syncSessionCurrency();
		cartFacade.refreshProductXMLs();
	}

	/**
	 * Prepares the session currency for a test case.
	 * 
	 * @param currency
	 *           the currency to switch to
	 */
	public void prepareCurrency(final String currency)
	{
		final String backup = storeSessionFacade.getCurrentCurrency().getIsocode();
		sessionService.setAttribute("atdd.currency.backup", backup);

		setCartCurrencyTo(currency);
	}

	/**
	 * Resets the currency after a test case
	 */
	public void resetCurrency()
	{
		final String backup = sessionService.getAttribute("atdd.currency.backup");
		if (StringUtils.isNotEmpty(backup))
		{
			setCartCurrencyTo(backup);
		}
	}

	/**
	 * Returns the order (cart) to check for the given billing event
	 * 
	 * @param billingEventCode
	 *           the code of the billing event to find the order for
	 * @return the order to check
	 */
	private AbstractOrderModel getOrderForBillingEvent(final String billingEventCode)
	{
		final CartModel masterCartModel = cartService.getSessionCart();

		AbstractOrderModel orderToCheck = null;

		if (masterCartModel.getBillingTime().getCode().equals(billingEventCode))
		{
			orderToCheck = masterCartModel;
		}
		else
		{
			for (final AbstractOrderModel orderModel : masterCartModel.getChildren())
			{
				if (orderModel.getBillingTime().getCode().equals(billingEventCode))
				{
					orderToCheck = orderModel;
				}
			}
		}
		return orderToCheck;
	}

}
