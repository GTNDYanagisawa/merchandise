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
package de.hybris.platform.commerceservicesatddtests.keywords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import de.hybris.platform.atddengine.keywords.AbstractKeywordLibrary;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


public class CommerceServicesKeywordLibrary extends AbstractKeywordLibrary
{
	private static final Logger LOG = Logger.getLogger(CommerceServicesKeywordLibrary.class);
	private static final double DELTA = 0.01;
	private static final String ANONYMOUS_UID = "anonymous";

	@Autowired
	private CartService cartService;

	@Autowired
	private CartFacade cartFacade;

	@Autowired
	private UserService userService;

	@Autowired
	private BaseSiteService baseSiteService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private I18NService i18nService;

	@Autowired
	private SessionService sessionService;

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>add product to cart once</i>
	 * <p>
	 * 
	 * @param productCode
	 *           the code of the product to add
	 */
	public void addProductToCartOnce(final String productCode)
	{
		addProductToCart(productCode, 1);
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>add product to cart</i>
	 * <p>
	 * 
	 * @param productCode
	 *           the code of the product to add
	 * 
	 * @param quantity
	 *           the number of units to add
	 */
	public void addProductToCart(final String productCode, final long quantity)
	{
		try
		{
			assertEquals(quantity, cartFacade.addToCart(productCode, quantity).getQuantity());
		}
		catch (final Exception e)
		{
			// catch any exceptions that would get swallowed by the robot framework and log them
			LOG.error("An exception occured while adding a product to cart", e);
			fail(e.getMessage());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>delete cart entry</i>
	 * <p>
	 * 
	 * @param entryNumber
	 *           the entry number to delete
	 */
	public void deleteCartEntry(final long entryNumber)
	{
		try
		{
			assertEquals(0, cartFacade.updateCartEntry(entryNumber, 0).getQuantity());
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.error("An exception occured while deleting a cart entry", e);
			fail(e.getMessage());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>remove product from cart</i>
	 * <p>
	 * 
	 * @param productCode
	 *           the code of the product to remove
	 */
	public void removeProductFromCart(final String productCode)
	{
		for (final OrderEntryData entry : cartFacade.getSessionCart().getEntries())
		{
			if (productCode.equals(entry.getProduct().getCode()))
			{
				deleteCartEntry(entry.getEntryNumber().longValue());
				break;
			}
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify cart total</i>
	 * <p>
	 * 
	 * @param expectedTotal
	 *           the expected order total for the billing event
	 */
	public void verifyCartTotal(final double expectedTotal)
	{
		final CartModel sessionCart = cartService.getSessionCart();

		assertNotNull("The session cart is null", sessionCart);

		try
		{
			final double orderTotal = sessionCart.getTotalPrice().doubleValue();
			assertEquals("The order total for does not match the expected value", expectedTotal, orderTotal, DELTA);
		}
		catch (final Exception e)
		{
			LOG.error("An exception occured while calculating the order total", e);
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>set current base site</i>
	 * <p>
	 * 
	 * @param baseSiteUid
	 *           the unique base site ID
	 */
	public void setCurrentBaseSite(final String baseSiteUid)
	{
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(baseSiteUid);
		baseSiteService.setCurrentBaseSite(baseSite, true);
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>prepare session</i>
	 * <p>
	 * 
	 * @param baseSiteUid
	 *           the unique base site ID
	 */
	public void prepareSession(final String baseSiteUid)
	{
		setCurrentBaseSite(baseSiteUid);
		setCartUser(ANONYMOUS_UID);

		final Locale locale = i18nService.getCurrentLocale();
		sessionService.setAttribute("ATDD-Locale", locale);
		i18nService.setCurrentLocale(Locale.US);
	}

	/**
	 * Java implementation of the robot keyword <br/>
	 * <p>
	 * <i>reset system attributes</i>
	 * </p>
	 */
	public void resetSystemAttributes()
	{
		Locale locale = sessionService.getAttribute("ATDD-locale");

		if (locale == null)
		{
			locale = Locale.US;
		}

		i18nService.setCurrentLocale(locale);
		sessionService.removeAttribute("ATDD-Locale");
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>login</i>
	 * <p>
	 * 
	 * @param userUID
	 *           the unique user ID
	 */
	public void login(final String userUID)
	{
		setCartUser(userUID);
	}

	/**
	 * Sets the cart user and the session currency
	 * 
	 * @param userUID
	 *           the unique user ID
	 */
	private void setCartUser(final String userUID)
	{
		final UserModel user = userService.getUserForUID(userUID);

		CurrencyModel currency = user.getSessionCurrency();
		if (currency == null)
		{
			BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();
			if (baseSite == null)
			{
				baseSite = baseSiteService.getAllBaseSites().iterator().next();
				assertNotNull("No base site was found. Please review your sample data!", baseSite);
				setCurrentBaseSite(baseSite.getUid());
			}
			final BaseStoreModel baseStore = baseSite.getStores().iterator().next();
			assertNotNull("No base store was found. Please review your sample data!", baseStore);
			currency = baseStore.getCurrencies().iterator().next();
		}
		assertNotNull("No currency was found. Please review your sample data!", currency);

		LOG.info(String.format("Setting cart user [%s] and currency [%s]", user.getUid(), currency.getIsocode()));

		cartService.changeCurrentCartUser(user);
		cartService.changeSessionCartCurrency(currency);

		// avoids that the cart is calculated for the wrong user
		modelService.refresh(cartService.getSessionCart());

		// adding currency to session manually is only a workaround
		JaloSession.getCurrentSession().getSessionContext().setCurrency((Currency) modelService.toPersistenceLayer(currency));
		JaloSession.getCurrentSession().getSessionContext().setUser((User) modelService.getSource(user));

	}
}
