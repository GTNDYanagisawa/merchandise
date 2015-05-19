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
package de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.payment.cybersource.constants.CyberSourceConstants;
import de.hybris.platform.acceleratorservices.payment.cybersource.constants.CyberSourceV7HopConstants;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.CustomerBillToData;
import de.hybris.platform.acceleratorservices.payment.data.CustomerShipToData;
import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.acceleratorservices.payment.data.OrderPageAppearanceData;
import de.hybris.platform.acceleratorservices.payment.data.OrderPageConfirmationData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.SignatureData;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionSignatureData;
import de.hybris.platform.acceleratorservices.payment.cybersource.enums.SubscriptionFrequencyEnum;
import de.hybris.platform.acceleratorservices.payment.cybersource.enums.TransactionTypeEnum;
import de.hybris.platform.acceleratorservices.payment.strategies.CreateSubscriptionRequestStrategy;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultCreateSubscriptionRequestStrategy implements CreateSubscriptionRequestStrategy
{

	private static final Logger LOG = Logger.getLogger(DefaultCreateSubscriptionRequestStrategy.class);

	private CartService cartService;
	private Converter<AddressModel, CustomerBillToData> customerBillToDataConverter;
	private Converter<CartModel, CustomerShipToData> customerShipToDataConverter;
	private CustomerEmailResolutionService customerEmailResolutionService;
	private Converter<CreditCardPaymentInfoModel, PaymentInfoData> paymentInfoDataConverter;
	private SiteConfigService siteConfigService;
	private String hostedOrderPageVersion;

	@SuppressWarnings("unused")
	public CreateSubscriptionRequest createSubscriptionRequest(final String siteName, final String requestUrl,
	                                                           final String responseUrl, final String merchantCallbackUrl, final CustomerModel customerModel,
	                                                           final CreditCardPaymentInfoModel cardInfo, final AddressModel paymentAddress) throws IllegalArgumentException
	{
		final CartModel cartModel = getCartService().getSessionCart();
		if (cartModel == null)
		{
			return null;
		}

		final CreateSubscriptionRequest request = new CreateSubscriptionRequest();
		//Common Data
		request.setRequestId(UUID.randomUUID().toString());
		request.setSiteName(siteName);
		request.setRequestUrl(requestUrl);

		//Version Specific Data using converters
		request.setCustomerBillToData(getCustomerBillToDataConverter().convert(paymentAddress));
		this.setEmailAddress(request.getCustomerBillToData(), customerModel);
		request.setCustomerShipToData(getCustomerShipToDataConverter().convert(cartModel));
		request.setPaymentInfoData(getPaymentInfoDataConverter().convert(cardInfo));

		//In-line Version Specific Data
		request.setOrderInfoData(getRequestOrderInfoData(TransactionTypeEnum.subscription));
		request.setSignatureData(getRequestSignatureData());
		request.setSubscriptionSignatureData(getRequestSubscriptionSignatureData(SubscriptionFrequencyEnum.ON_DEMAND));
		request.setOrderPageAppearanceData(getHostedOrderPageAppearanceConfiguration());
		request.setOrderPageConfirmationData(getOrderPageConfirmationData(responseUrl, merchantCallbackUrl));

		return request;
	}


	protected void setEmailAddress(final CustomerBillToData customerBillToData, final CustomerModel customer)
	{
		if (customerBillToData.getBillToEmail() == null || customerBillToData.getBillToEmail().isEmpty())
		{
			customerBillToData.setBillToEmail(getCustomerEmailResolutionService().getEmailForCustomer(customer));
		}

	}

	protected OrderInfoData getRequestOrderInfoData(final TransactionTypeEnum transactionType)
	{
		final OrderInfoData data = new OrderInfoData();

		data.setOrderPageIgnoreAVS(Boolean.TRUE);
		data.setOrderPageIgnoreCVN(Boolean.TRUE);
		data.setOrderPageTransactionType(transactionType.name());

		return data;
	}

	protected SignatureData getRequestSignatureData()
	{
		final SignatureData data = new SignatureData();

		final CartModel cartModel = getCartService().getSessionCart();
		if (cartModel == null)
		{
			return null;
		}

		if (StringUtils.isNotEmpty(getHostedOrderPageTestCurrency()))
		{
			data.setCurrency(getHostedOrderPageTestCurrency());
		}
		else
		{
			data.setCurrency(cartModel.getCurrency().getIsocode());

		}

		data.setAmount(getSetupFeeAmount());
		data.setMerchantID(getMerchantId());
		data.setOrderPageSerialNumber(getSerialNumber());
		data.setOrderPageVersion(getHostedOrderPageVersion());
		data.setSharedSecret(getSharedSecret());

		return data;
	}

	protected SubscriptionSignatureData getRequestSubscriptionSignatureData(final SubscriptionFrequencyEnum frequencyEnum)
	{
		final SubscriptionSignatureData data = new SubscriptionSignatureData();

		final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

		data.setRecurringSubscriptionInfoAmount(BigDecimal.valueOf(0));
		data.setRecurringSubscriptionInfoAutomaticRenew(Boolean.FALSE);
		data.setRecurringSubscriptionInfoFrequency(frequencyEnum.getStringValue());
		data.setRecurringSubscriptionInfoNumberOfPayments(Integer.valueOf(0));
		data.setRecurringSubscriptionInfoStartDate(formatter.format(new Date()));
		data.setSharedSecret(getSharedSecret());

		return data;
	}

	protected OrderPageAppearanceData getHostedOrderPageAppearanceConfiguration()
	{
		final OrderPageAppearanceData data = new OrderPageAppearanceData();

		data.setOrderPageBackgroundImageURL(getSiteConfigProperty(CyberSourceV7HopConstants.HopAppearanceProperties.BACKGROUND_URL));
		data.setOrderPageBackgroundImageURL(getSiteConfigProperty(CyberSourceV7HopConstants.HopAppearanceProperties.BACKGROUND_URL));
		data.setOrderPageBarColor(getSiteConfigProperty(CyberSourceV7HopConstants.HopAppearanceProperties.BAR_COLOR));
		data.setOrderPageBarTextColor(getSiteConfigProperty(CyberSourceV7HopConstants.HopAppearanceProperties.BAR_TEXT_COLOR));
		data.setOrderPageColorScheme(getSiteConfigProperty(CyberSourceV7HopConstants.HopAppearanceProperties.COLOR_SCHEME));
		data.setOrderPageMessageBoxBackgroundColor(getSiteConfigProperty(CyberSourceV7HopConstants.HopAppearanceProperties.MESSAGE_BOX_BACKGROUND_COLOR));
		data.setOrderPageRequiredFieldColor(getSiteConfigProperty(CyberSourceV7HopConstants.HopAppearanceProperties.REQUIRED_FIELD_COLOR));

		return data;
	}

	protected OrderPageConfirmationData getOrderPageConfirmationData(final String responseUrl, final String merchantCallbackUrl)
	{
		final OrderPageConfirmationData data = new OrderPageConfirmationData();
		data.setCancelResponseUrl(responseUrl);
		data.setDeclineResponseUrl(responseUrl);
		data.setReceiptResponseUrl(responseUrl);
		data.setMerchantUrlPostAddress(merchantCallbackUrl);
		return data;
	}


	protected String getHostedOrderPageVersion()
	{
		return hostedOrderPageVersion;
	}

	@Required
	public void setHostedOrderPageVersion(final String hostedOrderPageVersion)
	{
		this.hostedOrderPageVersion = hostedOrderPageVersion;
	}

	/**
	 * This method is used to get the ISO currency code configured for the CyberSource Test Hosted Order Page configured
	 * in the Business Centre. This may be different to the currency being used for the live account.
	 * 
	 * @return a three character representing the currency ISO code.
	 */
	protected String getHostedOrderPageTestCurrency()
	{
		return getSiteConfigProperty(CyberSourceConstants.HopProperties.HOP_TEST_CURRENCY);
	}

	protected String getSiteConfigProperty(final String key)
	{
		return getSiteConfigService().getString(key, "");
	}

	/**
	 * Gets the CyberSource setup fee, currently populated by a config value.
	 * 
	 * @return the CyberSource setup fee amount
	 */
	protected BigDecimal getSetupFeeAmount()
	{
		final String configSetupFee = getSiteConfigProperty(CyberSourceConstants.HopProperties.HOP_SETUP_FEE);
		if (configSetupFee != null && !configSetupFee.isEmpty())
		{
			try
			{
				final DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance();
				formatter.setParseBigDecimal(true);
				return (BigDecimal) formatter.parse(configSetupFee);
			}
			catch (final Exception e)
			{
				LOG.debug("Error converting to BigDecimal of String value: " + configSetupFee, e);
			}
		}
		return null;
	}

	/**
	 * Gets the CyberSource merchant ID.
	 * 
	 * @return the CyberSource merchant ID
	 */
	protected String getMerchantId()
	{
		return getSiteConfigProperty(CyberSourceConstants.HopProperties.MERCHANT_ID);
	}

	/**
	 * Gets the CyberSource merchant's serial number that is used to encrypt and validate connections.
	 * 
	 * @return the serial number downloaded from the CyberSource Business Centre.
	 */
	protected String getSerialNumber()
	{
		return getSiteConfigProperty(CyberSourceConstants.HopProperties.SERIAL_NUMBER);
	}

	/**
	 * Gets the CyberSource merchant's shared secret that is used to encrypt and validate connections.
	 * 
	 * @return the shared secret downloaded from the CyberSource Business Centre.
	 */
	protected String getSharedSecret()
	{
		return getSiteConfigProperty(CyberSourceConstants.HopProperties.SHARED_SECRET);
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

	protected Converter<AddressModel, CustomerBillToData> getCustomerBillToDataConverter()
	{
		return customerBillToDataConverter;
	}

	@Required
	public void setCustomerBillToDataConverter(final Converter<AddressModel, CustomerBillToData> customerBillToDataConverter)
	{
		this.customerBillToDataConverter = customerBillToDataConverter;
	}

	protected Converter<CartModel, CustomerShipToData> getCustomerShipToDataConverter()
	{
		return customerShipToDataConverter;
	}

	@Required
	public void setCustomerShipToDataConverter(final Converter<CartModel, CustomerShipToData> customerShipToDataConverter)
	{
		this.customerShipToDataConverter = customerShipToDataConverter;
	}

	protected CustomerEmailResolutionService getCustomerEmailResolutionService()
	{
		return customerEmailResolutionService;
	}

	@Required
	public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService)
	{
		this.customerEmailResolutionService = customerEmailResolutionService;
	}

	protected Converter<CreditCardPaymentInfoModel, PaymentInfoData> getPaymentInfoDataConverter()
	{
		return paymentInfoDataConverter;
	}

	@Required
	public void setPaymentInfoDataConverter(final Converter<CreditCardPaymentInfoModel, PaymentInfoData> paymentInfoDataConverter)
	{
		this.paymentInfoDataConverter = paymentInfoDataConverter;
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
}
