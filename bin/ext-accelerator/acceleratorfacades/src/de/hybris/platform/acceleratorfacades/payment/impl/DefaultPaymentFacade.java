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
package de.hybris.platform.acceleratorfacades.payment.impl;

import de.hybris.platform.acceleratorfacades.payment.PaymentFacade;
import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.payment.PaymentService;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class DefaultPaymentFacade implements PaymentFacade
{
	private BaseSiteService baseSiteService;
	private SiteConfigService siteConfigService;
	private Converter<PaymentSubscriptionResultItem, PaymentSubscriptionResultData> paymentSubscriptionResultDataConverter;
	private PaymentService paymentService;
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
	private UserService userService;

	protected CheckoutCustomerStrategy getCheckoutCustomerStrategy()
	{
		return checkoutCustomerStrategy;
	}

	@Required
	public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy)
	{
		this.checkoutCustomerStrategy = checkoutCustomerStrategy;
	}

	private CheckoutCustomerStrategy checkoutCustomerStrategy;

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	@Required
	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
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

	protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	@Required
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}

	protected Converter<PaymentSubscriptionResultItem, PaymentSubscriptionResultData> getPaymentSubscriptionResultDataConverter()
	{
		return paymentSubscriptionResultDataConverter;
	}

	@Required
	public void setPaymentSubscriptionResultDataConverter(
			final Converter<PaymentSubscriptionResultItem, PaymentSubscriptionResultData> paymentSubscriptionResultDataConverter)
	{
		this.paymentSubscriptionResultDataConverter = paymentSubscriptionResultDataConverter;
	}

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@Required
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	@Override
	public PaymentData beginHopCreateSubscription(final String responseUrl, final String merchantCallbackUrl)
	{
		final String fullResponseUrl = getFullResponseUrl(responseUrl, true);
		final String fullMerchantCallbackUrl = getFullResponseUrl(merchantCallbackUrl, true);
		final String siteName = getCurrentSiteName();

		final CustomerModel customerModel = getCurrentUserForCheckout();
		final AddressModel paymentAddress = getDefaultPaymentAddress(customerModel);

		return getPaymentService().beginHopCreatePaymentSubscription(siteName, fullResponseUrl, fullMerchantCallbackUrl, customerModel, null, paymentAddress);
	}

	@Override
	public PaymentData beginSopCreateSubscription(final String responseUrl, final String merchantCallbackUrl)
	{
		final String fullResponseUrl = getFullResponseUrl(responseUrl, true);
		final String fullMerchantCallbackUrl = getFullResponseUrl(merchantCallbackUrl, true);
		final String siteName = getCurrentSiteName();

		final CustomerModel customerModel = getCurrentUserForCheckout();
		final AddressModel paymentAddress = getDefaultPaymentAddress(customerModel);

		return getPaymentService().beginSopCreatePaymentSubscription(siteName, fullResponseUrl, fullMerchantCallbackUrl, customerModel, null, paymentAddress);
	}

	@Override
	public PaymentSubscriptionResultData completeHopCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount)
	{
		final CustomerModel customerModel = getCurrentUserForCheckout();
		final PaymentSubscriptionResultItem paymentSubscriptionResultItem = getPaymentService().completeHopCreatePaymentSubscription(
				customerModel, saveInAccount, parameters);

		if (paymentSubscriptionResultItem != null)
		{
			return getPaymentSubscriptionResultDataConverter().convert(paymentSubscriptionResultItem);
		}

		return null;
	}

	@Override
	public PaymentSubscriptionResultData completeSopCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount)
	{
		final CustomerModel customerModel = getCurrentUserForCheckout();
		final PaymentSubscriptionResultItem paymentSubscriptionResultItem = getPaymentService().completeSopCreatePaymentSubscription(
				customerModel, saveInAccount, parameters);

		if (paymentSubscriptionResultItem != null)
		{
			return getPaymentSubscriptionResultDataConverter().convert(paymentSubscriptionResultItem);
		}

		return null;
	}

	protected AddressModel getDefaultPaymentAddress(final CustomerModel customerModel)
	{
		return customerModel.getDefaultPaymentAddress();
	}

	/**
	 * @return the name of the current base site.
	 */
	protected String getCurrentSiteName()
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

		return currentBaseSite == null ? "" : currentBaseSite.getUid();
	}

	/**
	 * Resolves a given URL to a full URL including server and port, etc.
	 *
	 * @param responseUrl - the URL to resolve
	 * @param isSecure    - flag to indicate whether the final URL should use a secure connection or not.
	 * @return a full URL including HTTP protocol, server, port, path etc.
	 */
	protected String getFullResponseUrl(final String responseUrl, final boolean isSecure)
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

		final String fullResponseUrl = getSiteBaseUrlResolutionService().getWebsiteUrlForSite(currentBaseSite, isSecure,
				responseUrl);

		return fullResponseUrl == null ? "" : fullResponseUrl;
	}

	protected CustomerModel getCurrentUserForCheckout()
	{
		return getCheckoutCustomerStrategy().getCurrentUserForCheckout();
	}
}
