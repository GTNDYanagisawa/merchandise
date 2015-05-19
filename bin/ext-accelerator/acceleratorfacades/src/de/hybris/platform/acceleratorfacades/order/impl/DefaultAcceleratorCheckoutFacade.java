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
package de.hybris.platform.acceleratorfacades.order.impl;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.order.AcceleratorCheckoutService;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 */
public class DefaultAcceleratorCheckoutFacade extends DefaultCheckoutFacade implements AcceleratorCheckoutFacade
{
	private UiExperienceService uiExperienceService;
	private AcceleratorCheckoutService acceleratorCheckoutService;
	private PointOfServiceService pointOfServiceService;
	private Converter<PointOfServiceDistanceData, PointOfServiceData> pointOfServiceDistanceConverter;
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;

	@Override
	protected OrderModel placeOrder(final CartModel cartModel) throws InvalidCartException
	{
		final UiExperienceLevel uiExperienceLevel = getUiExperienceService().getUiExperienceLevel();
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		if (UiExperienceLevel.MOBILE.equals(uiExperienceLevel))
		{
			// Set application to WebMobile
			parameter.setSalesApplication(SalesApplication.WEBMOBILE);
			return getCommerceCheckoutService().placeOrder(parameter).getOrder();
		}
		// Default to WEB
		parameter.setSalesApplication(SalesApplication.WEB);
		return getCommerceCheckoutService().placeOrder(parameter).getOrder();
	}

	@Override
	public List<PointOfServiceData> getConsolidatedPickupOptions()
	{
		return Converters.convertAll(getAcceleratorCheckoutService().getConsolidatedPickupOptions(getCart()),
				getPointOfServiceDistanceConverter());
	}

	@Override
	public List<CartModificationData> consolidateCheckoutCart(final String pickupPointOfServiceName)
			throws CommerceCartModificationException
	{
		validateParameterNotNull(pickupPointOfServiceName, "pickupPointOfServiceName cannot be null");

		return Converters.convertAll(
				getAcceleratorCheckoutService().consolidateCheckoutCart(getCart(),
						getPointOfServiceService().getPointOfServiceForName(pickupPointOfServiceName)), getCartModificationConverter());
	}

	@Override
	public boolean isExpressCheckoutAllowedForCart()
	{
		return isExpressCheckoutEnabledForStore() ? isExpressCheckoutAllowedForShippingCart()
				|| isExpressCheckoutAllowedPickupOnlyCart() : false;
	}

	@Override
	public boolean isExpressCheckoutEnabledForStore()
	{
		if (getBaseStoreService().getCurrentBaseStore() != null)
		{
			return BooleanUtils.isTrue(getBaseStoreService().getCurrentBaseStore().getExpressCheckoutEnabled());
		}
		return false;
	}


	@Override
	public String getCheckoutFlowGroupForCheckout()
	{
		if (getBaseStoreService().getCurrentBaseStore() != null)
		{
			return getBaseStoreService().getCurrentBaseStore().getCheckoutFlowGroup();
		}
		return null;
	}

	@Override
	public ExpressCheckoutResult performExpressCheckout()
	{
		if (isExpressCheckoutEnabledForStore())
		{
			if (hasShippingItems())
			{
				if (!setDefaultDeliveryAddressForCheckout())
				{
					return ExpressCheckoutResult.ERROR_DELIVERY_ADDRESS;
				}

				if (!setCheapestDeliveryModeForCheckout())
				{
					return ExpressCheckoutResult.ERROR_CHEAPEST_DELIVERY_MODE;
				}
			}
			//Cart has Pickup Items ONLY
			else if (!setDeliveryModeIfAvailable())
			{
				return ExpressCheckoutResult.ERROR_DELIVERY_MODE;
			}

			if (!setDefaultPaymentInfoForCheckout())
			{
				return ExpressCheckoutResult.ERROR_PAYMENT_INFO;
			}

			return ExpressCheckoutResult.SUCCESS;
		}

		return ExpressCheckoutResult.ERROR_NOT_AVAILABLE;
	}

	@Override
	public boolean hasValidCart()
	{
		final CartData cartData = getCheckoutCart();
		final boolean validCart = cartData.getEntries() != null && !cartData.getEntries().isEmpty();
		return validCart;
	}

	@Override
	public boolean hasNoDeliveryAddress()
	{
		final CartData cartData = getCheckoutCart();
		return hasShippingItems() && (cartData == null || cartData.getDeliveryAddress() == null);
	}


	@Override
	public boolean hasNoDeliveryMode()
	{
		final CartData cartData = getCheckoutCart();
		return hasShippingItems() && (cartData == null || cartData.getDeliveryMode() == null);
	}


	@Override
	public boolean hasNoPaymentInfo()
	{
		final CartData cartData = getCheckoutCart();
		return (cartData == null || cartData.getPaymentInfo() == null);
	}

	protected boolean isExpressCheckoutAllowedForShippingCart()
	{
		final CustomerModel customerModel = (CustomerModel) getUserService().getCurrentUser();
		return hasShippingItems() && customerModel.getDefaultShipmentAddress() != null
				&& customerModel.getDefaultPaymentInfo() != null && customerModel.getDefaultPaymentInfo().isSaved();
	}

	protected boolean isExpressCheckoutAllowedPickupOnlyCart()
	{
		final CustomerModel customerModel = (CustomerModel) getUserService().getCurrentUser();
		return !hasShippingItems() && customerModel.getDefaultPaymentInfo() != null && customerModel.getDefaultPaymentInfo().isSaved();
	}

	@Override
	public boolean isTaxEstimationEnabledForCart()
	{
		final BaseStoreModel store = getBaseStoreService().getCurrentBaseStore();
		return store != null && Boolean.TRUE.equals(store.getTaxEstimationEnabled()) && store.isNet();
	}

	protected UiExperienceService getUiExperienceService()
	{
		return uiExperienceService;
	}

	@Required
	public void setUiExperienceService(final UiExperienceService uiExperienceService)
	{
		this.uiExperienceService = uiExperienceService;
	}

	protected AcceleratorCheckoutService getAcceleratorCheckoutService()
	{
		return acceleratorCheckoutService;
	}

	@Required
	public void setAcceleratorCheckoutService(final AcceleratorCheckoutService acceleratorCheckoutService)
	{
		this.acceleratorCheckoutService = acceleratorCheckoutService;
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

	protected Converter<PointOfServiceDistanceData, PointOfServiceData> getPointOfServiceDistanceConverter()
	{
		return pointOfServiceDistanceConverter;
	}

	@Required
	public void setPointOfServiceDistanceConverter(
			final Converter<PointOfServiceDistanceData, PointOfServiceData> pointOfServiceDistanceConverter)
	{
		this.pointOfServiceDistanceConverter = pointOfServiceDistanceConverter;
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

}
