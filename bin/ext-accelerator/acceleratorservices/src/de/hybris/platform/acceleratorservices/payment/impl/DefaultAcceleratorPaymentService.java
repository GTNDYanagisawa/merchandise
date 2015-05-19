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
package de.hybris.platform.acceleratorservices.payment.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.*;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.model.payment.CCPaySubValidationModel;
import de.hybris.platform.acceleratorservices.payment.PaymentService;
import de.hybris.platform.acceleratorservices.payment.dao.CreditCardPaymentSubscriptionDao;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.acceleratorservices.payment.enums.DecisionsEnum;
import de.hybris.platform.acceleratorservices.payment.strategies.ClientReferenceLookupStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.CreateSubscriptionRequestStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.CreateSubscriptionResultValidationStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.CreditCardPaymentInfoCreateStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.FraudCallbackStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.PaymentFormActionUrlStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.PaymentResponseInterpretationStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.PaymentTransactionStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.SignatureValidationStrategy;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


public class DefaultAcceleratorPaymentService implements PaymentService
{
	private static final Logger LOG = Logger.getLogger(DefaultAcceleratorPaymentService.class);

	private CartService cartService;
	private ModelService modelService;
	private UserService userService;
	private CreditCardPaymentSubscriptionDao creditCardPaymentSubscriptionDao;

	private SiteConfigService siteConfigService;

	private PaymentResponseInterpretationStrategy paymentResponseInterpretation;
	private PaymentResponseInterpretationStrategy hopPaymentResponseInterpretation;
	private PaymentFormActionUrlStrategy paymentFormActionUrlStrategy;

	private Converter<CreateSubscriptionRequest, PaymentData> paymentDataConverter;
	private CommonI18NService commonI18NService;
	private CreateSubscriptionRequestStrategy createSubscriptionRequestStrategy;
	private CreateSubscriptionResultValidationStrategy createSubscriptionResultValidationStrategy;
	private SignatureValidationStrategy signatureValidationStrategy;
	private CreditCardPaymentInfoCreateStrategy creditCardPaymentInfoCreateStrategy;
	private PaymentTransactionStrategy paymentTransactionStrategy;
	private FraudCallbackStrategy fraudCallbackStrategy;
	private ClientReferenceLookupStrategy clientReferenceLookupStrategy;

	@Override
	public PaymentData beginHopCreatePaymentSubscription(final String siteName, final String responseUrl,
			final String merchantCallbackUrl, final CustomerModel customer, final CreditCardPaymentInfoModel cardInfo,
			final AddressModel paymentAddress) throws IllegalArgumentException
	{
		final String requestUrl = getPaymentFormActionUrlStrategy().getHopRequestUrl();
		Assert.notNull(requestUrl, "The HopRequestUrl cannot be null");

		final CreateSubscriptionRequest request = getCreateSubscriptionRequestStrategy().createSubscriptionRequest(siteName,
				requestUrl, responseUrl, merchantCallbackUrl, customer, cardInfo, paymentAddress);

		PaymentData data = getPaymentDataConverter().convert(request);
		if (data == null)
		{
			data = new PaymentData();
			data.setParameters(new HashMap<String, String>());
		}

		return data;
	}

	@Override
	public PaymentData beginSopCreatePaymentSubscription(final String siteName, final String responseUrl,
			final String merchantCallbackUrl, final CustomerModel customer, final CreditCardPaymentInfoModel cardInfo,
			final AddressModel paymentAddress) throws IllegalArgumentException
	{
		final String requestUrl = getPaymentFormActionUrlStrategy().getSopRequestUrl(
				getClientReferenceLookupStrategy().lookupClientReferenceId());

		Assert.notNull(requestUrl, "The SopRequestUrl cannot be null");

		final CreateSubscriptionRequest request = getCreateSubscriptionRequestStrategy().createSubscriptionRequest(siteName,
				requestUrl, responseUrl, merchantCallbackUrl, customer, cardInfo, paymentAddress);

		PaymentData data = getPaymentDataConverter().convert(request);
		if (data == null)
		{
			data = new PaymentData();
			data.setParameters(new HashMap<String, String>());
		}

		return data;
	}

	@Override
	public PaymentSubscriptionResultItem completeHopCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters) throws IllegalArgumentException
	{
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();
		final CreateSubscriptionResult response = getHopPaymentResponseInterpretation().interpretResponse(parameters, null, errors);

		validateParameterNotNull(response, "CreateSubscriptionResult cannot be null");
		Assert.notNull(response.getDecision(), "Decision cannot be null");
		Assert.notNull(response.getAuthReplyData(), "AuthReplyData cannot be null");
		Assert.notNull(response.getCustomerInfoData(), "CustomerInfoData cannot be null");
		Assert.notNull(response.getOrderInfoData(), "OrderInfoData cannot be null");
		Assert.notNull(response.getPaymentInfoData(), "PaymentInfoData cannot be null");
		Assert.notNull(response.getSignatureData(), "SignatureData cannot be null");
		Assert.notNull(response.getSubscriptionInfoData(), "SubscriptionInfoData cannot be null");
		Assert.notNull(response.getSubscriptionSignatureData(), "SubscriptionSignatureData cannot be null");

		final PaymentSubscriptionResultItem paymentSubscriptionResult = new PaymentSubscriptionResultItem();
		paymentSubscriptionResult.setSuccess(DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()));
		paymentSubscriptionResult.setDecision(String.valueOf(response.getDecision()));
		paymentSubscriptionResult.setResultCode(String.valueOf(response.getReasonCode()));

		if (DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()))
		{
			// Validate signature
			if (getSignatureValidationStrategy().validateSignature(response.getSubscriptionInfoData()))
			{
				getPaymentTransactionStrategy().savePaymentTransactionEntry(customerModel, response.getRequestId(),
						response.getOrderInfoData());
				final CreditCardPaymentInfoModel cardPaymentInfoModel = getCreditCardPaymentInfoCreateStrategy().saveSubscription(
						customerModel, response.getCustomerInfoData(), response.getSubscriptionInfoData(),
						response.getPaymentInfoData(), saveInAccount);
				paymentSubscriptionResult.setStoredCard(cardPaymentInfoModel);

				// Check if the subscription has already been validated
				final CCPaySubValidationModel subscriptionValidation = getCreditCardPaymentSubscriptionDao()
						.findSubscriptionValidationBySubscription(cardPaymentInfoModel.getSubscriptionId());
				if (subscriptionValidation != null)
				{
					cardPaymentInfoModel.setSubscriptionValidated(true);
					getModelService().save(cardPaymentInfoModel);
					getModelService().remove(subscriptionValidation);
				}
			}
			else
			{
				LOG.error("Cannot create subscription. Subscription signature does not match.");
			}
		}
		else
		{
			LOG.error("Cannot create subscription. Decision: " + response.getDecision() + " - Reason Code: "
					+ response.getReasonCode());
			paymentSubscriptionResult.setErrors(errors);
		}
		return paymentSubscriptionResult;
	}

	@Override
	public PaymentSubscriptionResultItem completeSopCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters) throws IllegalArgumentException
	{
		final PaymentSubscriptionResultItem paymentSubscriptionResult = new PaymentSubscriptionResultItem();
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();
		paymentSubscriptionResult.setErrors(errors);

		final CreateSubscriptionResult response = getPaymentResponseInterpretation().interpretResponse(parameters,
				getClientReferenceLookupStrategy().lookupClientReferenceId(), errors);

		validateParameterNotNull(response, "CreateSubscriptionResult cannot be null");
		validateParameterNotNull(response.getDecision(), "Decision cannot be null");

		if (!getCreateSubscriptionResultValidationStrategy().validateCreateSubscriptionResult(errors, response).isEmpty())
		{
			return paymentSubscriptionResult;
		}

		paymentSubscriptionResult.setSuccess(DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()));
		paymentSubscriptionResult.setDecision(String.valueOf(response.getDecision()));
		paymentSubscriptionResult.setResultCode(String.valueOf(response.getReasonCode()));

		if (DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()))
		{
			// in case of ACCEPT we should have all these fields filled out
			Assert.notNull(response.getAuthReplyData(), "AuthReplyData cannot be null");
			Assert.notNull(response.getCustomerInfoData(), "CustomerInfoData cannot be null");
			Assert.notNull(response.getOrderInfoData(), "OrderInfoData cannot be null");
			Assert.notNull(response.getPaymentInfoData(), "PaymentInfoData cannot be null");
			Assert.notNull(response.getSignatureData(), "SignatureData cannot be null");
			Assert.notNull(response.getSubscriptionInfoData(), "SubscriptionInfoData cannot be null");
			Assert.notNull(response.getSubscriptionSignatureData(), "SubscriptionSignatureData cannot be null");

			// Validate signature
			if (getSignatureValidationStrategy().validateSignature(response.getSubscriptionInfoData()))
			{
				getPaymentTransactionStrategy().savePaymentTransactionEntry(customerModel, response.getRequestId(),
						response.getOrderInfoData());
				final CreditCardPaymentInfoModel cardPaymentInfoModel = getCreditCardPaymentInfoCreateStrategy().saveSubscription(
						customerModel, response.getCustomerInfoData(), response.getSubscriptionInfoData(),
						response.getPaymentInfoData(), saveInAccount);
				paymentSubscriptionResult.setStoredCard(cardPaymentInfoModel);

				// Check if the subscription has already been validated
				final CCPaySubValidationModel subscriptionValidation = getCreditCardPaymentSubscriptionDao()
						.findSubscriptionValidationBySubscription(cardPaymentInfoModel.getSubscriptionId());
				if (subscriptionValidation != null)
				{
					cardPaymentInfoModel.setSubscriptionValidated(true);
					getModelService().save(cardPaymentInfoModel);
					getModelService().remove(subscriptionValidation);
				}
			}
			else
			{
				LOG.error("Cannot create subscription. Subscription signature does not match.");
			}
		}
		else
		{
			LOG.error("Cannot create subscription. Decision: " + response.getDecision() + " - Reason Code: "
					+ response.getReasonCode());
		}
		return paymentSubscriptionResult;
	}

	@Override
	public void handleCreateSubscriptionCallback(final Map<String, String> parameters)
	{
		parameters.put("VerifyTransactionSignature()", "true");
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();
		final CreateSubscriptionResult response = getPaymentResponseInterpretation().interpretResponse(parameters,
				getClientReferenceLookupStrategy().lookupClientReferenceId(), errors);

		if (errors.isEmpty())
		{
			validateParameterNotNull(response, "CreateSubscriptionResult cannot be null");
			Assert.notNull(response.getDecision(), "Decision cannot be null");
			Assert.notNull(response.getAuthReplyData(), "AuthReplyData cannot be null");
			Assert.notNull(response.getCustomerInfoData(), "CustomerInfoData cannot be null");
			Assert.notNull(response.getOrderInfoData(), "OrderInfoData cannot be null");
			Assert.notNull(response.getPaymentInfoData(), "PaymentInfoData cannot be null");
			Assert.notNull(response.getSignatureData(), "SignatureData cannot be null");
			Assert.notNull(response.getSubscriptionInfoData(), "SubscriptionInfoData cannot be null");
			Assert.notNull(response.getSubscriptionSignatureData(), "SubscriptionSignatureData cannot be null");

			if (DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()))
			{
				// Validate signature
				if (getSignatureValidationStrategy().validateSignature(response.getSubscriptionInfoData()))
				{
					// Look to see if there is already a CreditCardPaymentInfoModel with that SubscriptionID
					final String subscriptionID = response.getSubscriptionInfoData().getSubscriptionID();
					final CreditCardPaymentInfoModel paymentInfo = getCreditCardPaymentSubscriptionDao()
							.findCreditCartPaymentBySubscription(subscriptionID);
					if (paymentInfo != null)
					{
						// Mark the CreditCardPaymentInfoModel as validated
						paymentInfo.setSubscriptionValidated(true);
						getModelService().save(paymentInfo);
					}
					else
					{
						// No CreditCardPaymentInfoModel exists yet, create a CCPaySubValidationModel to store the validated subscription ID
						final CCPaySubValidationModel subscriptionInfo = getModelService().create(CCPaySubValidationModel.class);
						subscriptionInfo.setSubscriptionId(subscriptionID);
						getModelService().save(subscriptionInfo);
					}
				}
				else
				{
					LOG.error(String
							.format(
									"Cannot create subscription. Subscription signature does not match SubscriptionSignedValue=%s SubscriptionIDPublicSignature=%s",
									response.getSubscriptionInfoData().getSubscriptionSignedValue(), response.getSubscriptionInfoData()
											.getSubscriptionIDPublicSignature()));
				}
			}
		}
	}

	@Override
	public void handleFraudUpdateCallback(final Map<String, String> parameters)
	{
		getFraudCallbackStrategy().handleFraudCallback(parameters);
	}

	/**
	 * This method add new PaymentTransactionEntry of type REVIEW_DECISION to the order. It also send event to allow
	 * submitorder proccess to end waitForReviewDecision action.
	 * 
	 * @param reviewDecisionEntry
	 *           - payment transaction entry of REVIEW_DECISION type
	 * @param guid
	 *           - {@link de.hybris.platform.core.model.order.AbstractOrderModel#GUID} to which PaymentTransaction will
	 *           be added
	 */
	@Override
	public void setPaymentTransactionReviewResult(final PaymentTransactionEntryModel reviewDecisionEntry, final String guid)
	{
		getPaymentTransactionStrategy().setPaymentTransactionReviewResult(reviewDecisionEntry, guid);
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
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

	public CreditCardPaymentSubscriptionDao getCreditCardPaymentSubscriptionDao()
	{
		return creditCardPaymentSubscriptionDao;
	}

	@Required
	public void setCreditCardPaymentSubscriptionDao(final CreditCardPaymentSubscriptionDao creditCardPaymentSubscriptionDao)
	{
		this.creditCardPaymentSubscriptionDao = creditCardPaymentSubscriptionDao;
	}

	protected PaymentFormActionUrlStrategy getPaymentFormActionUrlStrategy()
	{
		return paymentFormActionUrlStrategy;
	}

	@Required
	public void setPaymentFormActionUrlStrategy(final PaymentFormActionUrlStrategy paymentFormActionUrlStrategy)
	{
		this.paymentFormActionUrlStrategy = paymentFormActionUrlStrategy;
	}

	protected PaymentResponseInterpretationStrategy getPaymentResponseInterpretation()
	{
		return paymentResponseInterpretation;
	}

	@Required
	public void setPaymentResponseInterpretation(final PaymentResponseInterpretationStrategy paymentResponseInterpretation)
	{
		this.paymentResponseInterpretation = paymentResponseInterpretation;
	}

	protected PaymentResponseInterpretationStrategy getHopPaymentResponseInterpretation()
	{
		return hopPaymentResponseInterpretation;
	}

	@Required
	public void setHopPaymentResponseInterpretation(final PaymentResponseInterpretationStrategy paymentResponseInterpretation)
	{
		this.hopPaymentResponseInterpretation = paymentResponseInterpretation;
	}

	protected Converter<CreateSubscriptionRequest, PaymentData> getPaymentDataConverter()
	{
		return paymentDataConverter;
	}

	@Required
	public void setPaymentDataConverter(final Converter<CreateSubscriptionRequest, PaymentData> PaymentDataConverter)
	{
		this.paymentDataConverter = PaymentDataConverter;
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

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected CreateSubscriptionRequestStrategy getCreateSubscriptionRequestStrategy()
	{
		return createSubscriptionRequestStrategy;
	}

	@Required
	public void setCreateSubscriptionRequestStrategy(final CreateSubscriptionRequestStrategy createSubscriptionRequestStrategy)
	{
		this.createSubscriptionRequestStrategy = createSubscriptionRequestStrategy;
	}

	protected CreateSubscriptionResultValidationStrategy getCreateSubscriptionResultValidationStrategy()
	{
		return createSubscriptionResultValidationStrategy;
	}

	@Required
	public void setCreateSubscriptionResultValidationStrategy(
			final CreateSubscriptionResultValidationStrategy createSubscriptionResultValidationStrategy)
	{
		this.createSubscriptionResultValidationStrategy = createSubscriptionResultValidationStrategy;
	}

	protected SignatureValidationStrategy getSignatureValidationStrategy()
	{
		return signatureValidationStrategy;
	}

	@Required
	public void setSignatureValidationStrategy(final SignatureValidationStrategy signatureValidationStrategy)
	{
		this.signatureValidationStrategy = signatureValidationStrategy;
	}

	protected CreditCardPaymentInfoCreateStrategy getCreditCardPaymentInfoCreateStrategy()
	{
		return creditCardPaymentInfoCreateStrategy;
	}

	@Required
	public void setCreditCardPaymentInfoCreateStrategy(
			final CreditCardPaymentInfoCreateStrategy creditCardPaymentInfoCreateStrategy)
	{
		this.creditCardPaymentInfoCreateStrategy = creditCardPaymentInfoCreateStrategy;
	}

	protected PaymentTransactionStrategy getPaymentTransactionStrategy()
	{
		return paymentTransactionStrategy;
	}

	@Required
	public void setPaymentTransactionStrategy(final PaymentTransactionStrategy paymentTransactionStrategy)
	{
		this.paymentTransactionStrategy = paymentTransactionStrategy;
	}

	protected FraudCallbackStrategy getFraudCallbackStrategy()
	{
		return fraudCallbackStrategy;
	}

	@Required
	public void setFraudCallbackStrategy(final FraudCallbackStrategy fraudCallbackStrategy)
	{
		this.fraudCallbackStrategy = fraudCallbackStrategy;
	}

	protected ClientReferenceLookupStrategy getClientReferenceLookupStrategy()
	{
		return clientReferenceLookupStrategy;
	}

	@Required
	public void setClientReferenceLookupStrategy(final ClientReferenceLookupStrategy clientReferenceLookupStrategy)
	{
		this.clientReferenceLookupStrategy = clientReferenceLookupStrategy;
	}
}
