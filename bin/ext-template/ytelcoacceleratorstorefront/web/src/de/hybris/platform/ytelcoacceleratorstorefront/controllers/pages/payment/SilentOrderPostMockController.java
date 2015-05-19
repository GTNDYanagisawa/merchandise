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
package de.hybris.platform.ytelcoacceleratorstorefront.controllers.pages.payment;

import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.subscriptionfacades.SubscriptionFacade;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPaymentData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;
import de.hybris.platform.ytelcoacceleratorstorefront.controllers.AbstractController;
import de.hybris.platform.ytelcoacceleratorstorefront.controllers.util.GlobalMessages;
import de.hybris.platform.ytelcoacceleratorstorefront.forms.SopPaymentDetailsForm;
import de.hybris.platform.ytelcoacceleratorstorefront.forms.validation.SbgSopPaymentDetailsValidator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("sbg-sop-mock")
public class SilentOrderPostMockController extends AbstractController
{
	private static final Logger LOG = Logger.getLogger(SilentOrderPostMockController.class);

	private static final String REDIRECT_URL_SUMMARY = REDIRECT_PREFIX + "/checkout/multi/summary";
	private static final String REDIRECT_URL_ADD_PAYMENT_METHOD = REDIRECT_PREFIX
			+ "/paymentDetails/add-payment-method?targetArea=";
	private static final String REDIRECT_URL_EDIT_PAYMENT_DETAILS = REDIRECT_PREFIX
			+ "/paymentDetails/edit-payment-details?targetArea=accountArea&paymentInfoId=";
	private static final String REDIRECT_URL_PAYMENT_INFO = REDIRECT_PREFIX + "/my-account/payment-details";

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource(name = "subscriptionFacade")
	private SubscriptionFacade subscriptionFacade;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "sbgSopPaymentDetailsValidator")
	private SbgSopPaymentDetailsValidator sopPaymentDetailsValidator;

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	protected CheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}

	protected SubscriptionFacade getSubscriptionFacade()
	{
		return subscriptionFacade;
	}

	protected UserFacade getUserFacade()
	{
		return userFacade;
	}

	protected SbgSopPaymentDetailsValidator getSopPaymentDetailsValidator()
	{
		return sopPaymentDetailsValidator;
	}

	@RequestMapping(value = "/handle-form-post", method = RequestMethod.POST)
	public String handleFormPost(@Valid final SopPaymentDetailsForm form, final BindingResult bindingResult,
			@RequestParam("targetArea") final String targetArea, @RequestParam("editMode") final Boolean editMode,
			@RequestParam("paymentInfoId") final String paymentInfoId, final RedirectAttributes redirectAttributes)
	{
		getSopPaymentDetailsValidator().validate(form, bindingResult);
		if (bindingResult.hasErrors())
		{
			redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
					Collections.singletonList("checkout.error.paymentmethod.formentry.invalid"));
			redirectAttributes
					.addFlashAttribute("org.springframework.validation.BindingResult.sopPaymentDetailsForm", bindingResult);
			redirectAttributes.addFlashAttribute("sopPaymentDetailsForm", bindingResult.getTarget());

			return Boolean.TRUE.equals(editMode) ? REDIRECT_URL_EDIT_PAYMENT_DETAILS + paymentInfoId
					: REDIRECT_URL_ADD_PAYMENT_METHOD + targetArea;

		}
		else
		{
			final String authorizationRequestId = (String) getSessionService().getAttribute("authorizationRequestId");
			final String authorizationRequestToken = (String) getSessionService().getAttribute("authorizationRequestToken");

			try
			{
				if (BooleanUtils.isTrue(editMode))
				{
					final CCPaymentInfoData ccPaymentInfoData = setupCCPaymentInfoData(form, paymentInfoId);
					if (null != ccPaymentInfoData)
					{
						final CCPaymentInfoData result = getSubscriptionFacade().changePaymentMethod(ccPaymentInfoData, null, true,
								null);

						// enrich result data with form data, which is not provided from the facade call
						result.setId(paymentInfoId);
						result.getBillingAddress().setTitleCode(ccPaymentInfoData.getBillingAddress().getTitleCode());
						result.setStartMonth(ccPaymentInfoData.getStartMonth());
						result.setStartYear(ccPaymentInfoData.getStartYear());
						result.setIssueNumber(ccPaymentInfoData.getIssueNumber());

						getUserFacade().updateCCPaymentInfo(result);

						if (form.getMakeAsDefault().booleanValue())
						{
							getUserFacade().setDefaultPaymentInfo(result);
						}

						redirectAttributes.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
								Collections.singletonList("text.account.paymentDetails.editSuccessful"));
					}
					else
					{
						redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
								Collections.singletonList("text.account.paymentDetails.nonExisting.error"));
					}
				}
				else
				{
					final SubscriptionPaymentData result = getSubscriptionFacade().finalizeTransaction(authorizationRequestId,
							authorizationRequestToken, createPaymentDetailsMap(form, targetArea));

					final CCPaymentInfoData newPaymentSubscription = getSubscriptionFacade().createPaymentSubscription(
							result.getParameters());

					// enrich result data with form data, which is not provided from the facade call
					newPaymentSubscription.setStartMonth(form.getStartMonth());
					newPaymentSubscription.setStartYear(form.getStartYear());
					newPaymentSubscription.setIssueNumber(form.getIssueNumber());
					newPaymentSubscription.setSaved(true);

					getUserFacade().updateCCPaymentInfo(newPaymentSubscription);

					if (form.getMakeAsDefault().booleanValue())
					{
						getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
					}

					getCheckoutFacade().setPaymentDetails(newPaymentSubscription.getId());

					redirectAttributes.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
							Collections.singletonList("text.account.paymentDetails.addSuccessful"));
				}
			}
			catch (final SubscriptionFacadeException e)
			{
				LOG.error("Creating a new payment method failed", e);
				redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
						Collections.singletonList("checkout.multi.paymentMethod.addPaymentDetails.incomplete"));
				return REDIRECT_URL_ADD_PAYMENT_METHOD + targetArea;
			}
		}

		if (StringUtils.equals(targetArea, "multiCheckoutArea"))
		{
			return REDIRECT_URL_SUMMARY;
		}

		return REDIRECT_URL_PAYMENT_INFO;
	}

	private Map<String, String> createPaymentDetailsMap(final SopPaymentDetailsForm form, final String targetArea)
	{
		final Map<String, String> map = new HashMap<>();

		// Mask the card number
		String maskedCardNumber = "************";
		if (form.getCardNumber().length() >= 4)
		{
			final String endPortion = form.getCardNumber().trim().substring(form.getCardNumber().length() - 4);
			maskedCardNumber = maskedCardNumber + endPortion;
		}

		map.put("cardNumber", maskedCardNumber);
		map.put("cardType", form.getCardTypeCode());
		map.put("expiryMonth", form.getExpiryMonth());
		map.put("expiryYear", form.getExpiryYear());
		map.put("issueNumber", form.getIssueNumber());
		map.put("nameOnCard", form.getNameOnCard());
		map.put("startMonth", form.getStartMonth());
		map.put("startYear", form.getStartYear());

		if (form.getBillingAddress() != null && Boolean.TRUE.equals(form.getNewBillingAddress()))
		{
			map.put("billingAddress_countryIso", form.getBillingAddress().getCountryIso());
			map.put("billingAddress_titleCode", form.getBillingAddress().getTitleCode());
			map.put("billingAddress_firstName", form.getBillingAddress().getFirstName());
			map.put("billingAddress_lastName", form.getBillingAddress().getLastName());
			map.put("billingAddress_line1", form.getBillingAddress().getLine1());
			map.put("billingAddress_line2", form.getBillingAddress().getLine2());
			map.put("billingAddress_postcode", form.getBillingAddress().getPostcode());
			map.put("billingAddress_townCity", form.getBillingAddress().getTownCity());
		}
		else if (getCheckoutFacade().hasCheckoutCart() && StringUtils.equals(targetArea, "multiCheckoutArea"))
		{
			final AddressData deliveryAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
			map.put("billingAddress_countryIso", deliveryAddress.getCountry().getIsocode());
			map.put("billingAddress_firstName", deliveryAddress.getFirstName());
			map.put("billingAddress_titleCode", deliveryAddress.getTitleCode());
			map.put("billingAddress_lastName", deliveryAddress.getLastName());
			map.put("billingAddress_line1", deliveryAddress.getLine1());
			map.put("billingAddress_line2", deliveryAddress.getLine2());
			map.put("billingAddress_postcode", deliveryAddress.getPostalCode());
			map.put("billingAddress_townCity", deliveryAddress.getTown());
		}

		return map;
	}

	private CCPaymentInfoData setupCCPaymentInfoData(final SopPaymentDetailsForm form, final String paymentInfoId)
	{
		final CCPaymentInfoData ccPaymentInfoData = getUserFacade().getCCPaymentInfoForCode(paymentInfoId);

		if (null != form && null != ccPaymentInfoData)
		{
			ccPaymentInfoData.setCardType(form.getCardTypeCode());
			ccPaymentInfoData.setCardNumber(form.getCardNumber());
			ccPaymentInfoData.setAccountHolderName(form.getNameOnCard());
			ccPaymentInfoData.setStartMonth(form.getStartMonth());
			ccPaymentInfoData.setStartYear(form.getStartYear());
			ccPaymentInfoData.setExpiryMonth(form.getExpiryMonth());
			ccPaymentInfoData.setExpiryYear(form.getExpiryYear());
			ccPaymentInfoData.setIssueNumber(form.getIssueNumber());

			final AddressData addressData = new AddressData();
			addressData.setId(form.getBillingAddress().getAddressId());
			addressData.setFirstName(form.getBillingAddress().getFirstName());
			addressData.setLastName(form.getBillingAddress().getLastName());
			addressData.setLine1(form.getBillingAddress().getLine1());
			addressData.setLine2(form.getBillingAddress().getLine2());
			addressData.setTown(form.getBillingAddress().getTownCity());
			addressData.setPostalCode(form.getBillingAddress().getPostcode());
			addressData.setTitleCode(form.getBillingAddress().getTitleCode());

			if (StringUtils.isNotEmpty(form.getBillingAddress().getCountryIso()))
			{
				final CountryData countryData = new CountryData();
				countryData.setIsocode(form.getBillingAddress().getCountryIso());
				addressData.setCountry(countryData);
			}

			ccPaymentInfoData.setBillingAddress(addressData);
		}

		return ccPaymentInfoData;
	}
}
