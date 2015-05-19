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
package de.hybris.platform.sap.sapordermgmtb2bfacades.checkout.impl;

import de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BDaysOfWeekData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.sap.sapordermgmtservices.checkout.CheckoutService;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Default implementation of {@link B2BCheckoutFacade}
 */
public class DefaultB2BCheckoutFacade implements B2BCheckoutFacade
{

	private static final String MSG_NOT_SUPPORTED = "Not supported in the context of SAP order management";

	private static final Logger LOG = Logger.getLogger(DefaultB2BCheckoutFacade.class);

	private CartService cartService;
	private CheckoutService checkoutService;
	private SapPartnerService sapPartnerService;
	private B2BCheckoutFacade b2bCheckoutFacade;
	private ProductImageHelper productImageHelper;
	private Converter<AddressModel, AddressData> addressConverter;
	private BackendAvailabilityService backendAvailabilityService;


	/**
	 * @return Is Backend down?
	 */
	public boolean isBackendDown()
	{
		return backendAvailabilityService.isBackendDown();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#hasCheckoutCart()
	 */
	@Override
	public boolean hasCheckoutCart()
	{
		if (isBackendDown())
		{
			return false;
		}
		return cartService.hasSessionCart();
	}

	/**
	 * @return the sapPartnerService
	 */
	public SapPartnerService getSapPartnerService()
	{
		return sapPartnerService;
	}

	/**
	 * @param sapPartnerService
	 *           the sapPartnerService to set
	 */
	public void setSapPartnerService(final SapPartnerService sapPartnerService)
	{
		this.sapPartnerService = sapPartnerService;
	}

	/**
	 * @return the addressConverter
	 */
	public Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	/**
	 * @param addressConverter
	 *           the addressConverter to set
	 */
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	/**
	 * @return the backendAvailabilityService
	 */
	public BackendAvailabilityService getBackendAvailabilityService()
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

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#getCheckoutCart()
	 */
	@Override
	public CartData getCheckoutCart()
	{
		if (isBackendDown())
		{
			return new CartData();
		}

		final CartData checkoutCart = cartService.getSessionCart();
		productImageHelper.enrichWithProductImages(checkoutCart);
		return checkoutCart;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#getSupportedDeliveryAddresses(boolean)
	 */
	@Override
	public List<? extends AddressData> getSupportedDeliveryAddresses(final boolean visibleAddressesOnly)
	{
		if (isBackendDown())
		{
			return null;
		}

		final Collection<AddressModel> addressesForOwner = sapPartnerService.getAllowedDeliveryAddresses();
		final List<AddressData> result = new ArrayList<>();
		for (final AddressModel model : addressesForOwner)
		{
			result.add(addressConverter.convert(model));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#getDeliveryAddressForCode(java.lang.String)
	 */
	@Override
	public AddressData getDeliveryAddressForCode(final String code)
	{
		return b2bCheckoutFacade.getDeliveryAddressForCode(code);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commercefacades.order.CheckoutFacade#setDeliveryAddress(de.hybris.platform.commercefacades.
	 * user.data.AddressData)
	 */
	@Override
	public boolean setDeliveryAddress(final AddressData usedAddress)
	{
		if (isBackendDown())
		{
			return false;
		}

		final Collection<AddressModel> allowedDeliveryAddresses = sapPartnerService.getAllowedDeliveryAddresses();
		AddressModel deliveryAddress = null;

		for (final AddressModel address : allowedDeliveryAddresses)
		{
			if (usedAddress.getId().equals(address.getPk().toString()))
			{
				deliveryAddress = address;
				break;
			}
		}

		if (deliveryAddress != null)
		{
			return checkoutService.setDeliveryAddress(deliveryAddress.getSapCustomerID());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#removeDeliveryAddress()
	 */
	@Override
	public boolean removeDeliveryAddress()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#getSupportedDeliveryModes()
	 */
	@Override
	public List<? extends DeliveryModeData> getSupportedDeliveryModes()
	{
		if (isBackendDown())
		{
			return null;
		}

		return checkoutService.getSupportedDeliveryModes();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#setDeliveryAddressIfAvailable()
	 */
	@Override
	public boolean setDeliveryAddressIfAvailable()
	{
		// nothing needed here as the delivery address will be either determined from the SD partners
		// attached to the order, or the user will explicitly select a different one
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#setDeliveryModeIfAvailable()
	 */
	@Override
	public boolean setDeliveryModeIfAvailable()
	{
		// nothing to do, as the delivery mode will be either determined from SD settings,
		// or the user will explicitly select one
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#setPaymentInfoIfAvailable()
	 */
	@Override
	public boolean setPaymentInfoIfAvailable()
	{
		handleNotSupportedLogging("setPaymentInfoIfAvailable");
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#setDeliveryMode(java.lang.String)
	 */
	@Override
	public boolean setDeliveryMode(final String deliveryModeCode)
	{
		if (isBackendDown())
		{
			return false;
		}

		return checkoutService.setDeliveryMode(deliveryModeCode);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#removeDeliveryMode()
	 */
	@Override
	public boolean removeDeliveryMode()
	{
		if (isBackendDown())
		{
			return false;
		}

		return checkoutService.setDeliveryMode("");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#getDeliveryCountries()
	 */
	@Override
	public List<CountryData> getDeliveryCountries()
	{
		//We don't return any countries. Specifying a manual address currently not supported
		handleNotSupportedLogging("getDeliveryCountries");
		return Collections.EMPTY_LIST;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#getBillingCountries()
	 */
	@Override
	public List<CountryData> getBillingCountries()
	{
		//We don't return any countries. Specifying a manual address currently not supported
		handleNotSupportedLogging("getBillingCountries");
		return Collections.EMPTY_LIST;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#getCountryForIsocode(java.lang.String)
	 */

	@Override
	public CountryData getCountryForIsocode(final String countryIso)
	{
		return b2bCheckoutFacade.getCountryForIsocode(countryIso);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#setPaymentDetails(java.lang.String)
	 */
	@Override
	public boolean setPaymentDetails(final String paymentInfoId)
	{
		handleNotSupportedException();
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#getSupportedCardTypes()
	 */
	@Override
	public List<CardTypeData> getSupportedCardTypes()
	{
		//credit card payment currently not supported
		handleNotSupportedLogging("getSupportedCardTypes");
		return Collections.EMPTY_LIST;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commercefacades.order.CheckoutFacade#createPaymentSubscription(de.hybris.platform.commercefacades
	 * .order.data.CCPaymentInfoData)
	 */
	@Override
	public CCPaymentInfoData createPaymentSubscription(final CCPaymentInfoData paymentInfoData)
	{
		handleNotSupportedException();
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#authorizePayment(java.lang.String)
	 */
	@Override
	public boolean authorizePayment(final String securityCode)
	{
		handleNotSupportedException();
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#placeOrder()
	 */
	@Override
	public OrderData placeOrder() throws InvalidCartException
	{
		if (isBackendDown())
		{
			//We should never reach this method as checkout is forbidden
			throw new ApplicationBaseRuntimeException("Place order not allowed if backend is down");
		}

		return checkoutService.placeOrder();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#containsTaxValues()
	 */
	@Override
	public boolean containsTaxValues()
	{
		//Prices from ERP already contain taxes
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#getAddressDataForId(java.lang.String, boolean)
	 */
	@Override
	public AddressData getAddressDataForId(final String addressId, final boolean visibleAddressesOnly)
	{
		return b2bCheckoutFacade.getAddressDataForId(addressId, visibleAddressesOnly);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#prepareCartForCheckout()
	 */
	@Override
	public void prepareCartForCheckout()
	{
		//no specific preparation needed in our scenario

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#setDefaultPaymentInfoForCheckout()
	 */
	@Override
	public boolean setDefaultPaymentInfoForCheckout()
	{
		handleNotSupportedException();
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#setDefaultDeliveryAddressForCheckout()
	 */
	@Override
	public boolean setDefaultDeliveryAddressForCheckout()
	{
		// nothing needed as this will be determined from the backend
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#setCheapestDeliveryModeForCheckout()
	 */
	@Override
	public boolean setCheapestDeliveryModeForCheckout()
	{
		handleNotSupportedException();
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#hasShippingItems()
	 */
	@Override
	public boolean hasShippingItems()
	{
		handleNotSupportedException();
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.CheckoutFacade#hasPickUpItems()
	 */
	@Override
	public boolean hasPickUpItems()
	{
		handleNotSupportedException();
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#getVisibleCostCenters()
	 */
	@Override
	public List<? extends B2BCostCenterData> getVisibleCostCenters()
	{
		//No (buyer) cost centers available in SAP synch order management
		handleNotSupportedLogging("getVisibleCostCenters");
		return Collections.EMPTY_LIST;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#getActiveVisibleCostCenters()
	 */
	@Override
	public List<B2BCostCenterData> getActiveVisibleCostCenters()
	{
		//No (buyer) cost centers available in SAP synch order management
		handleNotSupportedLogging("getActiveVisibleCostCenters");
		return Collections.EMPTY_LIST;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#setCostCenterForCart(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public <T extends AbstractOrderData> T setCostCenterForCart(final String costCenterCode, final String orderCode)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#getPaymentTypesForCheckoutSummary()
	 */
	@Override
	public List<B2BPaymentTypeData> getPaymentTypesForCheckoutSummary()
	{
		// Only Account Payment is available
		final B2BPaymentTypeData paymentType = new B2BPaymentTypeData();
		paymentType.setCode(CheckoutPaymentType.ACCOUNT.getCode().toLowerCase());
		paymentType.setDisplayName(CheckoutPaymentType.ACCOUNT.getCode().toLowerCase());

		final List<B2BPaymentTypeData> paymentTypes = new ArrayList();
		paymentTypes.add(paymentType);

		return paymentTypes;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#setPaymentTypeSelectedForCheckout(java.lang.String
	 * )
	 */
	@Override
	public boolean setPaymentTypeSelectedForCheckout(final String paymentType)
	{
		handleNotSupportedLogging("setPaymentTypeSelectedForCheckout");
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#setPurchaseOrderNumber(java.lang.String)
	 */
	@Override
	public boolean setPurchaseOrderNumber(final String purchaseOrderNumber)
	{
		if (isBackendDown())
		{
			return false;
		}

		return checkoutService.setPurchaseOrderNumber(purchaseOrderNumber);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#setQuoteRequestDescription(java.lang.String)
	 */
	@Override
	public boolean setQuoteRequestDescription(final String quoteRequestDescription)
	{
		handleNotSupportedException();
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#getDaysOfWeekForReplenishmentCheckoutSummary()
	 */
	@Override
	public List<B2BDaysOfWeekData> getDaysOfWeekForReplenishmentCheckoutSummary()
	{
		handleNotSupportedLogging("getDaysOfWeekForReplenishmentCheckoutSummary");
		return Collections.EMPTY_LIST;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#scheduleOrder(de.hybris.platform.
	 * b2bacceleratorfacades.order.data.TriggerData)
	 */
	@Override
	public ScheduledCartData scheduleOrder(final TriggerData trigger)
	{
		handleNotSupportedException();
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#createCartFromOrder(java.lang.String)
	 */
	@Override
	public void createCartFromOrder(final String orderCode)
	{
		handleNotSupportedException();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#validateSessionCart()
	 */
	@Override
	public List<? extends CommerceCartModification> validateSessionCart() throws CommerceCartModificationException
	{

		//No validation available in checkout, we do a validation only before entering checkout
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade#setDefaultPaymentTypeForCheckout()
	 */
	@Override
	public void setDefaultPaymentTypeForCheckout()
	{
		//Nothing needed as currently we only have one payment type invoice payment

	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected void handleNotSupportedException()
	{
		throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
	}

	protected void handleNotSupportedLogging(final String call)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(MSG_NOT_SUPPORTED + ", ignoring: " + call);
		}
	}

	/**
	 * @return the b2bCheckoutFacade
	 */
	public B2BCheckoutFacade getB2bCheckoutFacade()
	{
		return b2bCheckoutFacade;
	}

	/**
	 * @param b2bCheckoutFacade
	 *           the b2bCheckoutFacade to set
	 */
	public void setB2bCheckoutFacade(final B2BCheckoutFacade b2bCheckoutFacade)
	{
		this.b2bCheckoutFacade = b2bCheckoutFacade;
	}

	/**
	 * @return the checkoutService
	 */
	public CheckoutService getCheckoutService()
	{
		return checkoutService;
	}

	/**
	 * @param checkoutService
	 *           the checkoutService to set
	 */
	public void setCheckoutService(final CheckoutService checkoutService)
	{
		this.checkoutService = checkoutService;
	}

	/**
	 * @return the productImageHelper
	 */
	public ProductImageHelper getProductImageHelper()
	{
		return productImageHelper;
	}

	/**
	 * @param productImageHelper
	 *           the productImageHelper to set
	 */
	public void setProductImageHelper(final ProductImageHelper productImageHelper)
	{
		this.productImageHelper = productImageHelper;
	}

}
