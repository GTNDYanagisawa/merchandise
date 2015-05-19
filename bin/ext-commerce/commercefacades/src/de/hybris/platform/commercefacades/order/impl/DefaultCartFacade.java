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
package de.hybris.platform.commercefacades.order.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.*;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Implementation for {@link CartFacade}. Delivers main functionality for cart.
 */
public class DefaultCartFacade implements CartFacade
{
	private CartService cartService;
	private ProductService productService;
	private CommerceCartService commerceCartService;
	private Converter<CartModel, CartData> miniCartConverter;
	private Converter<CartModel, CartData> cartConverter;
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	private Converter<CommerceCartRestoration, CartRestorationData> cartRestorationConverter;
	private BaseSiteService baseSiteService;
	private UserService userService;
	private PointOfServiceService pointOfServiceService;
	private DeliveryService deliveryService;
	private Converter<CountryModel, CountryData> countryConverter;
	private PriceDataFactory priceDataFactory;


	@Override
	public CartData getSessionCart()
	{
		final CartData cartData;
		if (hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();
			cartData = getCartConverter().convert(cart);
		}
		else
		{
			cartData = createEmptyCart();
		}
		return cartData;
	}

	@Override
	public CartData getMiniCart()
	{
		final CartData cartData;
		if (hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();
			cartData = getMiniCartConverter().convert(cart);
		}
		else
		{
			cartData = createEmptyCart();
		}
		return cartData;
	}

	protected CartData createEmptyCart()
	{
		return getMiniCartConverter().convert(null);
	}

	@Override
	public boolean hasSessionCart()
	{
		return getCartService().hasSessionCart();
	}

	@Override
	public boolean hasEntries()
	{
		return hasSessionCart() && !CollectionUtils.isEmpty(getCartService().getSessionCart().getEntries());
	}

	@Override
	public CartModificationData addToCart(final String code, final long quantity) throws CommerceCartModificationException
	{
		final ProductModel product = getProductService().getProductForCode(code);
		final CartModel cartModel = getCartService().getSessionCart();
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setProduct(product);
		parameter.setQuantity(quantity);
		parameter.setUnit(product.getUnit());
		parameter.setCreateNewEntry(false);

		final CommerceCartModification modification = getCommerceCartService().addToCart(parameter);

		return getCartModificationConverter().convert(modification);
	}

	@Override
	public CartModificationData addToCart(final String code, final long quantity, final String storeId)
			throws CommerceCartModificationException
	{
		if (storeId == null)
		{
			return addToCart(code, quantity);
		}
		else
		{
			final ProductModel product = getProductService().getProductForCode(code);
			final CartModel cartModel = getCartService().getSessionCart();
			final PointOfServiceModel pointOfServiceModel = getPointOfServiceService().getPointOfServiceForName(storeId);
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			parameter.setProduct(product);
			parameter.setPointOfService(pointOfServiceModel);
			parameter.setQuantity(quantity);
			parameter.setUnit(product.getUnit());
			parameter.setCreateNewEntry(false);

			final CommerceCartModification modification = getCommerceCartService().addToCart(parameter);

			return getCartModificationConverter().convert(modification);
		}
	}

	@Override
	public List<CartModificationData> validateCartData() throws CommerceCartModificationException
	{
		if (hasSessionCart())
		{
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(getCartService().getSessionCart());
			return Converters.convertAll(getCommerceCartService().validateCart(parameter), getCartModificationConverter());
		}
		else
		{
			return Collections.emptyList();
		}
	}

	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final long quantity)
			throws CommerceCartModificationException
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setEntryNumber(entryNumber);
		parameter.setQuantity(quantity);

		final CommerceCartModification modification = getCommerceCartService().updateQuantityForCartEntry(parameter);

		return getCartModificationConverter().convert(modification);
	}

	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final String storeId)
			throws CommerceCartModificationException
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final PointOfServiceModel pointOfServiceModel = StringUtils.isEmpty(storeId) ? null : getPointOfServiceService()
				.getPointOfServiceForName(storeId);
		if (pointOfServiceModel == null)
		{
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			parameter.setEntryNumber(entryNumber);
			return getCartModificationConverter().convert(getCommerceCartService().updateToShippingModeForCartEntry(parameter));
		}
		else
		{

			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			parameter.setEntryNumber(entryNumber);
			parameter.setPointOfService(pointOfServiceModel);

			return getCartModificationConverter().convert(getCommerceCartService().updatePointOfServiceForCartEntry(parameter));
		}
	}

	@Override
	public CartRestorationData restoreSavedCart(final String guid) throws CommerceCartRestorationException
	{
		if (!hasEntries())
		{
			getCartService().setSessionCart(null);
		}

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		final CartModel cartForGuidAndSiteAndUser = getCommerceCartService().getCartForGuidAndSiteAndUser(guid,
				getBaseSiteService().getCurrentBaseSite(), getUserService().getCurrentUser());
		parameter.setCart(cartForGuidAndSiteAndUser);

		return getCartRestorationConverter().convert(getCommerceCartService().restoreCart(parameter));
	}

	@Override
	public CartRestorationData restoreAnonymousCartAndTakeOwnership(final String guid) throws CommerceCartRestorationException
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		final CartModel cart = getCommerceCartService().getCartForGuidAndSiteAndUser(guid, currentBaseSite,
				getUserService().getAnonymousUser());
		if (cart == null)
		{
			throw new CommerceCartRestorationException(String.format("Cart not found for guid %s", guid));
		}
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cart);

		final CommerceCartRestoration commerceCartRestoration = getCommerceCartService().restoreCart(parameter);
		getCartService().changeCurrentCartUser(getUserService().getCurrentUser());
		return getCartRestorationConverter().convert(commerceCartRestoration);
	}

	@Override
	public CartRestorationData restoreAnonymousCartAndMerge(final String fromAnonymousCartGuid, final String toUserCartGuid)
			throws CommerceCartRestorationException, CommerceCartMergingException
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		final CartModel fromCart = getCommerceCartService().getCartForGuidAndSiteAndUser(fromAnonymousCartGuid, currentBaseSite,
				getUserService().getAnonymousUser());

		final CartModel toCart = getCommerceCartService().getCartForGuidAndSiteAndUser(toUserCartGuid, currentBaseSite,
				getUserService().getCurrentUser());

		if (toCart == null)
		{
			throw new CommerceCartRestorationException("Cart cannot be null");
		}

		if (fromCart == null)
		{
			return restoreSavedCart(toUserCartGuid);
		}

		/*
		 * if(fromCart != null && toCart == null) { return restoreAnonymousCartAndTakeOwnership(fromAnonymousCartGuid); }
		 * 
		 * if(fromCart == null && toCart == null) { return null; }
		 */

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(toCart);

		final CommerceCartRestoration restoration = getCommerceCartService().restoreCart(parameter);
		parameter.setCart(getCartService().getSessionCart());

		commerceCartService.mergeCarts(fromCart, parameter.getCart(), restoration.getModifications());

		final CommerceCartRestoration commerceCartRestoration = getCommerceCartService().restoreCart(parameter);

		commerceCartRestoration.setModifications(restoration.getModifications());

		getCartService().changeCurrentCartUser(getUserService().getCurrentUser());
		return getCartRestorationConverter().convert(commerceCartRestoration);
	}

	@Override
	public CartRestorationData restoreCartAndMerge(final String fromUserCartGuid, final String toUserCartGuid)
			throws CommerceCartRestorationException, CommerceCartMergingException
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		final CartModel fromCart = getCommerceCartService().getCartForGuidAndSiteAndUser(fromUserCartGuid, currentBaseSite,
				getUserService().getCurrentUser());

		final CartModel toCart = getCommerceCartService().getCartForGuidAndSiteAndUser(toUserCartGuid, currentBaseSite,
				getUserService().getCurrentUser());

		if (fromCart == null && toCart != null)
		{
			return restoreSavedCart(toUserCartGuid);
		}

		if (fromCart != null && toCart == null)
		{
			return restoreSavedCart(fromUserCartGuid);
		}

		if (fromCart == null && toCart == null)
		{
			return null;
		}

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(toCart);

		final CommerceCartRestoration restoration = getCommerceCartService().restoreCart(parameter);
		parameter.setCart(getCartService().getSessionCart());

		commerceCartService.mergeCarts(fromCart, parameter.getCart(), restoration.getModifications());

		final CommerceCartRestoration commerceCartRestoration = getCommerceCartService().restoreCart(parameter);

		commerceCartRestoration.setModifications(restoration.getModifications());

		getCartService().changeCurrentCartUser(getUserService().getCurrentUser());
		return getCartRestorationConverter().convert(commerceCartRestoration);
	}

	@Override
	public void removeStaleCarts()
	{
		final UserModel currentUser = getUserService().getCurrentUser();

		// DO NOT CLEAN ANONYMOUS USER CARTS
		if (getUserService().isAnonymousUser(currentUser))
		{
			return;
		}

		getCommerceCartService().removeStaleCarts(getCartService().getSessionCart(), getBaseSiteService().getCurrentBaseSite(),
				currentUser);
	}

	@Override
	public CartData estimateExternalTaxes(final String deliveryZipCode, final String countryIsoCode)
	{

		final CartModel currentCart = getCartService().getSessionCart();
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(currentCart);
		parameter.setDeliveryZipCode(deliveryZipCode);
		parameter.setDeliveryCountryIso(countryIsoCode);

		final BigDecimal taxTotal = commerceCartService.estimateTaxes(parameter).getTax();

		final CartData sessionCart = getSessionCart();
		final PriceData taxData = priceDataFactory.create(PriceDataType.BUY, taxTotal, currentCart.getCurrency());
		final PriceData totalPriceData = priceDataFactory.create(PriceDataType.BUY,
				taxTotal.add(sessionCart.getTotalPrice().getValue()), currentCart.getCurrency());

		sessionCart.setTotalTax(taxData);
		sessionCart.setTotalPrice(totalPriceData);
		sessionCart.setNet(false);

		return sessionCart;
	}

	@Override
	public CartData getSessionCartWithEntryOrdering(final boolean recentlyAddedFirst)
	{
		if (hasSessionCart())
		{
			final CartData data = getSessionCart();

			if (recentlyAddedFirst)
			{
				final List<OrderEntryData> listEntries = data.getEntries();
				final List<OrderEntryData> recentlyAddedListEntries = new ArrayList<OrderEntryData>();

				for (int index = listEntries.size(); index > 0; index--)
				{
					recentlyAddedListEntries.add(listEntries.get(index - 1));
				}

				data.setEntries(Collections.unmodifiableList(recentlyAddedListEntries));
			}

			return data;
		}
		return createEmptyCart();
	}

	@Override
	public List<CountryData> getDeliveryCountries()
	{
		final List<CountryData> countries = Converters.convertAll(getDeliveryService().getDeliveryCountriesForOrder(null),
				getCountryConverter());
		Collections.sort(countries, DefaultCheckoutFacade.CountryComparator.INSTANCE);
		return countries;
	}

	@Override
	public void removeSessionCart()
	{
		cartService.removeSessionCart();
	}

	@Override
	public List<CartData> getCartsForCurrentUser()
	{
		return Converters.convertAll(
				commerceCartService.getCartsForSiteAndUser(baseSiteService.getCurrentBaseSite(), userService.getCurrentUser()),
				getCartConverter());
	}

	@Override
	public void mergeCarts(final CartModel fromCart, final CartModel toCart, final List<CommerceCartModification> modifications)
			throws CommerceCartMergingException
	{
		commerceCartService.mergeCarts(fromCart, toCart, modifications);
	}


	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected Converter<CartModel, CartData> getMiniCartConverter()
	{
		return miniCartConverter;
	}

	@Required
	public void setMiniCartConverter(final Converter<CartModel, CartData> miniCartConverter)
	{
		this.miniCartConverter = miniCartConverter;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected Converter<CartModel, CartData> getCartConverter()
	{
		return cartConverter;
	}

	@Required
	public void setCartConverter(final Converter<CartModel, CartData> cartConverter)
	{
		this.cartConverter = cartConverter;
	}

	protected Converter<CommerceCartModification, CartModificationData> getCartModificationConverter()
	{
		return cartModificationConverter;
	}

	@Required
	public void setCartModificationConverter(
			final Converter<CommerceCartModification, CartModificationData> cartModificationConverter)
	{
		this.cartModificationConverter = cartModificationConverter;
	}

	protected Converter<CommerceCartRestoration, CartRestorationData> getCartRestorationConverter()
	{
		return cartRestorationConverter;
	}

	@Required
	public void setCartRestorationConverter(final Converter<CommerceCartRestoration, CartRestorationData> cartRestorationConverter)
	{
		this.cartRestorationConverter = cartRestorationConverter;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	@Required
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}

	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	public DeliveryService getDeliveryService()
	{
		return deliveryService;
	}

	public void setDeliveryService(final DeliveryService deliveryService)
	{
		this.deliveryService = deliveryService;
	}

	public Converter<CountryModel, CountryData> getCountryConverter()
	{
		return countryConverter;
	}

	public void setCountryConverter(final Converter<CountryModel, CountryData> countryConverter)
	{
		this.countryConverter = countryConverter;
	}

}
