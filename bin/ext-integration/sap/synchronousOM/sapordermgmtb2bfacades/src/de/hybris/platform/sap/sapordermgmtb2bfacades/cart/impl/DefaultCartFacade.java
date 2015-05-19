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
package de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl;

import static de.hybris.platform.util.localization.Localization.getLocalizedString;

import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.exception.DomainException;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.sap.sapordermgmtservices.prodconf.ProductConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Default cart facade, serving the cart in the context of SAP synchronous order management. The cart does not touch the
 * hybris persistence, but is stored in the back end session after the first addToCart interaction.<br>
 * Pricing, availability etc. are all fetched from the SAP back end representation of the order.
 */
public class DefaultCartFacade implements CartFacade, de.hybris.platform.commercefacades.order.CartFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultCartFacade.class);
	private CartService cartService;
	private ProductImageHelper productImageHelper;
	private UserService userService;
	private CartRestorationFacade cartRestorationFacade;
	private de.hybris.platform.commercefacades.order.CartFacade standardFacade;
	private BackendAvailabilityService backendAvailabilityService;
	private ProductConfigurationService productConfigurationService;
	private ConfigurationProviderFactory configurationProviderFactory;

	private static final String CART_MODIFICATION_ERROR = "basket.error.occurred";



	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#getSessionCart()
	 */
	@Override
	public CartData getSessionCart()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSessionCart");
		}
		if (isBackendDown())
		{
			final CartData cartWhenSessionDown = getStandardFacade().getSessionCart();
			cartWhenSessionDown.setBackendDown(true);
			cartWhenSessionDown.getTotalPrice().setFormattedValue("");
			return cartWhenSessionDown;
		}
		else
		{
			if (!isUserLoggedOn())
			{
				return new CartData();
			}
			final CartData sessionCart = getCartService().getSessionCart();
			productImageHelper.enrichWithProductImages(sessionCart);
			return sessionCart;
		}
	}


	private boolean isUserLoggedOn()
	{
		final UserModel userModel = userService.getCurrentUser();
		return !userService.isAnonymousUser(userModel);
	}

	/**
	 * Returns the user model corresponding to the logged in user
	 *
	 * @return userModel
	 */
	protected UserModel getCurrentUser()
	{
		final UserModel userModel = userService.getCurrentUser();
		return userModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#getSessionCartWithEntryOrdering(boolean)
	 */
	@Override
	public CartData getSessionCartWithEntryOrdering(final boolean recentlyAddedFirst)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSessionCartWithEntryOrdering called with: " + recentlyAddedFirst);
		}
		if (isBackendDown())
		{
			final CartData cartWhenSessionDown = getStandardFacade().getSessionCartWithEntryOrdering(recentlyAddedFirst);
			cartWhenSessionDown.setBackendDown(true);
			cartWhenSessionDown.getTotalPrice().setFormattedValue("");
			return cartWhenSessionDown;
		}
		else
		{
			if (!isUserLoggedOn())
			{
				return new CartData();
			}
			final CartData sessionCart = getCartService().getSessionCart(recentlyAddedFirst);
			productImageHelper.enrichWithProductImages(sessionCart);
			return sessionCart;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#hasSessionCart()
	 */
	@Override
	public boolean hasSessionCart()
	{
		if (isBackendDown())
		{
			return standardFacade.hasSessionCart();
		}
		return getCartService().hasSessionCart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#getMiniCart()
	 */
	@Override
	public CartData getMiniCart()
	{
		return getSessionCart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#addToCart(java.lang.String, long)
	 */
	@Override
	public CartModificationData addToCart(final String code, final long quantity) throws CommerceCartModificationException
	{

		if (isBackendDown())
		{
			final CartModificationData cartModificationBackendDown = getStandardFacade().addToCart(code, quantity);
			final OrderEntryData entry = cartModificationBackendDown.getEntry();
			entry.setBackendDown(true);
			return cartModificationBackendDown;
		}
		else
		{
			final CartModificationData cartModification = getCartService().addToCart(code, quantity);
			productImageHelper.enrichWithProductImages(cartModification.getEntry());

			if (this.cartRestorationFacade != null)
			{
				this.cartRestorationFacade.setSavedCart(getCartService().getSessionCart());
			}

			return cartModification;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#addToCart(java.lang.String, long, java.lang.String)
	 */
	@Override
	public CartModificationData addToCart(final String code, final long quantity, final String storeId)
			throws CommerceCartModificationException
	{
		LOG.info("addToCart called with store ID, ignoring: " + storeId);
		return this.addToCart(code, quantity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#validateCartData()
	 */
	@Override
	public List<CartModificationData> validateCartData() throws CommerceCartModificationException
	{
		return getCartService().validateCartData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#updateCartEntry(long, long)
	 */
	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final long quantity)
			throws CommerceCartModificationException
	{
		if (isBackendDown())
		{
			final String itemKey = getItemKey(entryNumber);
			checkForConfigurationRelease(quantity, itemKey);
			return getStandardFacade().updateCartEntry(entryNumber, quantity);
		}
		else
		{
			return getCartService().updateCartEntry(entryNumber, quantity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#updateCartEntry(long, java.lang.String)
	 */
	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final String storeId)
			throws CommerceCartModificationException
	{
		throw new ApplicationBaseRuntimeException("Not supported: updateCartEntry(final long entryNumber, final String storeId)");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#restoreSavedCart(java.lang.String)
	 */
	@Override
	public CartRestorationData restoreSavedCart(final String code) throws CommerceCartRestorationException
	{
		if (isBackendDown())
		{
			return standardFacade.restoreSavedCart(code);
		}
		else
		{
			if (this.cartRestorationFacade != null)
			{
				final CartRestorationData hybrisCart = this.cartRestorationFacade.restoreSavedCart(code, this.getCurrentUser());

				return hybrisCart;

			}
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#getDeliveryCountries()
	 */
	@Override
	public List<CountryData> getDeliveryCountries()
	{
		//No delivery countries available, only choosing from existing addresses supported
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#estimateExternalTaxes(java.lang.String, java.lang.String)
	 */
	@Override
	public CartData estimateExternalTaxes(final String deliveryZipCode, final String countryIsoCode)
	{
		//We cannot support this, as the delivery costs are based on the ship-to party address in the ERP case
		throw new ApplicationBaseRuntimeException("Not supported: estimateExternalTaxes");

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#removeStaleCarts()
	 */
	@Override
	public void removeStaleCarts()
	{
		//No stale carts in this scenario

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#restoreAnonymousCartAndTakeOwnership(java.lang.String)
	 */
	@Override
	public CartRestorationData restoreAnonymousCartAndTakeOwnership(final String guid) throws CommerceCartRestorationException
	{
		//No anonymous carts in our scenario
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#removeSessionCart()
	 */
	@Override
	public void removeSessionCart()
	{
		if (this.cartRestorationFacade != null)
		{
			this.cartRestorationFacade.removeSavedCart();
		}

		getCartService().removeSessionCart();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#getCartsForCurrentUser()
	 */
	@Override
	public List<CartData> getCartsForCurrentUser()
	{
		return Arrays.asList(getSessionCart());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#mergeCarts(de.hybris.platform.core.model.order.CartModel,
	 * de.hybris.platform.core.model.order.CartModel, java.util.List)
	 */
	@Override
	public void mergeCarts(final CartModel fromCart, final CartModel toCart, final List<CommerceCartModification> modifications)
			throws CommerceCartMergingException
	{
		throw new ApplicationBaseRuntimeException("mergeCarts not supported");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#restoreAnonymousCartAndMerge(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public CartRestorationData restoreAnonymousCartAndMerge(final String fromAnonumousCartGuid, final String toUserCartGuid)
			throws CommerceCartMergingException, CommerceCartRestorationException
	{
		throw new ApplicationBaseRuntimeException("restoreAnonymousCartAndMerge not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#restoreCartAndMerge(java.lang.String, java.lang.String)
	 */
	@Override
	public CartRestorationData restoreCartAndMerge(final String fromUserCartGuid, final String toUserCartGuid)
			throws CommerceCartRestorationException, CommerceCartMergingException
	{
		throw new ApplicationBaseRuntimeException("restoreCartAndMerge not supported");
	}

	@Override
	public CartModificationData addOrderEntry(final OrderEntryData cartEntry)
	{


		CartModificationData cartModification = null;
		try
		{
			cartModification = addToCart(cartEntry.getProduct().getCode(), cartEntry.getQuantity().longValue());
		}
		catch (final CommerceCartModificationException e)
		{
			throw new DomainException(getLocalizedString(CART_MODIFICATION_ERROR), e);
		}

		return cartModification;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade#updateOrderEntry(de.hybris.platform.commercefacades
	 * .order.data.OrderEntryData)
	 */
	@Override
	public CartModificationData updateOrderEntry(final OrderEntryData cartEntry) throws EntityValidationException
	{
		final long entryNumber = cartEntry.getEntryNumber().longValue();
		final long quantity = cartEntry.getQuantity().longValue();
		if (isBackendDown())
		{
			try
			{
				final String itemKey = getItemKey(entryNumber);
				checkForConfigurationRelease(quantity, itemKey);
				return getStandardFacade().updateCartEntry(entryNumber, quantity);
			}
			catch (final CommerceCartModificationException e)
			{
				throw new EntityValidationException("UpdateOrderEntry failed", e);
			}
		}
		else
		{

			final CartModificationData cartModificationData = getCartService().updateCartEntry(entryNumber, quantity);
			if (getCartRestorationFacade() != null)
			{
				try
				{
					getCartRestorationFacade().setSavedCart(getSessionCart());
				}
				catch (final CommerceCartModificationException e)
				{
					throw new EntityValidationException("UpdateOrderEntry failed", e);
				}
			}
			return cartModificationData;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade#addOrderEntryList(java.util.List)
	 */
	@Override
	public List<CartModificationData> addOrderEntryList(final List<OrderEntryData> cartEntries)
	{
		throw new ApplicationBaseRuntimeException("addOrderEntryList not supported");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade#updateOrderEntryList(java.util.List)
	 */
	@Override
	public List<CartModificationData> updateOrderEntryList(final List<OrderEntryData> cartEntries)
	{
		throw new ApplicationBaseRuntimeException("updateOrderEntryList not supported");
	}


	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}


	public CartService getCartService()
	{
		return cartService;
	}


	public ProductImageHelper getProductImageHelper()
	{
		return productImageHelper;
	}

	public void setProductImageHelper(final ProductImageHelper productImageHelper)
	{
		this.productImageHelper = productImageHelper;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the cartRestorationFacade
	 */
	public CartRestorationFacade getCartRestorationFacade()
	{
		return cartRestorationFacade;
	}


	/**
	 * @param cartRestorationFacade
	 *           the cartRestorationFacade to set
	 */
	public void setCartRestorationFacade(final CartRestorationFacade cartRestorationFacade)
	{
		this.cartRestorationFacade = cartRestorationFacade;
	}


	/**
	 * Sets hybris standard facade
	 *
	 * @param standardFacade
	 */
	public void setStandardFacade(final de.hybris.platform.commercefacades.order.CartFacade standardFacade)
	{
		this.standardFacade = standardFacade;

	}


	/**
	 * @return Hybris standard facade (which is used is the backend is not available)
	 */
	protected de.hybris.platform.commercefacades.order.CartFacade getStandardFacade()
	{
		return standardFacade;
	}


	/**
	 * @return Is Backend down?
	 */
	public boolean isBackendDown()
	{
		return backendAvailabilityService.isBackendDown();
	}


	/**
	 * @return the backendAvailabilityService
	 */
	protected BackendAvailabilityService getBackendAvailabilityService()
	{
		return backendAvailabilityService;
	}


	/**
	 * @param backendAvailabilityService
	 *           the backendAvailabilityService to set
	 */
	public void setBackendAvailabilityService(final BackendAvailabilityService backendAvailabilityService)
	{
		this.backendAvailabilityService = backendAvailabilityService;
	}

	@Override
	public boolean hasEntries()
	{
		if (isBackendDown())
		{
			return standardFacade.hasEntries();
		}
		else
		{
			boolean hasEntries = false;
			final CartData sessionCart = getCartService().getSessionCart();
			if (sessionCart != null && sessionCart.getEntries() != null)
			{
				hasEntries = sessionCart.getEntries().size() > 0;
			}
			return hasEntries;
		}
	}


	/**
	 * @return the productConfigurationService
	 */
	public ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}


	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}




	void checkForConfigurationRelease(final long quantity, final String itemKey)
	{
		if (quantity == 0)
		{
			final String configId = getProductConfigurationService().getGetConfigId(itemKey);
			if (configId != null)
			{
				getConfigurationProvider().releaseSession(configId);
			}
		}
	}



	ConfigurationProvider getConfigurationProvider()
	{
		return getConfigurationProviderFactory().getProvider();
	}


	String getItemKey(final long entryNumber)
	{
		final List<OrderEntryData> entries = getSessionCart().getEntries();
		for (final OrderEntryData entry : entries)
		{
			if (entry.getEntryNumber().longValue() == entryNumber)
			{
				return entry.getHandle();
			}
		}
		return null;
	}


	/**
	 * @return the configurationProviderFactory
	 */
	public ConfigurationProviderFactory getConfigurationProviderFactory()
	{
		return configurationProviderFactory;
	}


	/**
	 * @param configurationProviderFactory
	 *           the configurationProviderFactory to set
	 */
	public void setConfigurationProviderFactory(final ConfigurationProviderFactory configurationProviderFactory)
	{
		this.configurationProviderFactory = configurationProviderFactory;
	}

}
