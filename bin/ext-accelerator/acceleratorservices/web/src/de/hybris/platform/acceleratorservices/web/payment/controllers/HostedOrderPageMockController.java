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
package de.hybris.platform.acceleratorservices.web.payment.controllers;

import de.hybris.platform.acceleratorservices.payment.utils.AcceleratorDigestUtils;
import de.hybris.platform.acceleratorservices.web.payment.forms.AddressForm;
import de.hybris.platform.acceleratorservices.web.payment.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorservices.web.payment.validation.PaymentDetailsValidator;
import de.hybris.platform.commerceservices.util.AbstractComparator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * 
 */
@Controller
@RequestMapping("/hop-mock")
public class HostedOrderPageMockController
{
	protected static final String HOP_PAYMENT_FORM_PAGE = "hopMock/paymentForm";
	protected static final String HOP_REDIRECT_POST_PAGE = "hopMock/redirectPost";

	protected static final char SEPARATOR_CHAR = '\u00AC'; // Mathematical not sign
	protected static final String SEPARATOR_STR = Character.toString(SEPARATOR_CHAR);

	private static final String SHARED_SECRET = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKB" + "gQCdz1fPUnZj0focVszsOmGj3MwBXtF18LZH"
			+ "kTK4CV88hQq2vUWKMYe2UVLIEvVWghLOCy/t" + "tsGD5NS0k8dllHJvRu2ElQNJZGb4tG/7hxux"
			+ "NxH9YaZqv5z0hSiUbTgQfFDuO7NqzmwMiW3z" + "q7y6UiiSBkNfV2WCmnKg9vJk/Jp+1wIDAQAB";

	@Resource(name = "paymentDetailsValidator")
	private PaymentDetailsValidator paymentDetailsValidator;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	@Resource(name = "supportedCardTypes")
	private Map<String, String> supportedCardTypes;

	@Resource(name = "defaultPaymentDetailsForm")
	private PaymentDetailsForm defaultPaymentDetailsForm;

	@Resource(name = "acceleratorDigestUtils")
	private AcceleratorDigestUtils digestUtils;

	public PaymentDetailsValidator getPaymentDetailsValidator()
	{
		return paymentDetailsValidator;
	}

	public MessageSource getMessageSource()
	{
		return messageSource;
	}

	public PaymentDetailsForm getDefaultPaymentDetailsForm()
	{
		return defaultPaymentDetailsForm;
	}

	@ModelAttribute("cardTypes")
	public Map<String, String> getSupportedCardTypes()
	{
		return supportedCardTypes;
	}

	@ModelAttribute("currentLanguageIso")
	public String getCurrentLanguageIso()
	{
		final Locale currentLocale = Locale.getDefault();
		return currentLocale.getCountry().toLowerCase(Locale.getDefault());
	}

	@ModelAttribute("months")
	public List<SelectOption> getMonths()
	{
		final List<SelectOption> months = new ArrayList<SelectOption>();

		months.add(new SelectOption("1", "01"));
		months.add(new SelectOption("2", "02"));
		months.add(new SelectOption("3", "03"));
		months.add(new SelectOption("4", "04"));
		months.add(new SelectOption("5", "05"));
		months.add(new SelectOption("6", "06"));
		months.add(new SelectOption("7", "07"));
		months.add(new SelectOption("8", "08"));
		months.add(new SelectOption("9", "09"));
		months.add(new SelectOption("10", "10"));
		months.add(new SelectOption("11", "11"));
		months.add(new SelectOption("12", "12"));

		return months;
	}

	@ModelAttribute("startYears")
	public List<SelectOption> getStartYears()
	{
		final List<SelectOption> startYears = new ArrayList<SelectOption>();
		final Calendar calender = new GregorianCalendar();

		for (int i = calender.get(Calendar.YEAR); i > (calender.get(Calendar.YEAR) - 6); i--)
		{
			startYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
		}

		return startYears;
	}

	@ModelAttribute("expiryYears")
	public List<SelectOption> getExpiryYears()
	{
		final List<SelectOption> expiryYears = new ArrayList<SelectOption>();
		final Calendar calender = new GregorianCalendar();

		for (int i = calender.get(Calendar.YEAR); i < (calender.get(Calendar.YEAR) + 11); i++)
		{
			expiryYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
		}

		return expiryYears;
	}

	@ModelAttribute("billingCountries")
	public List<SelectOption> getBillingCountries()
	{
		final List<SelectOption> countries = new ArrayList<SelectOption>();
		final Locale[] locales = Locale.getAvailableLocales();
		final Map<String, String> countryMap = new HashMap<String, String>();

		for (final Locale locale : locales)
		{
			final String code = locale.getCountry();
			final String name = locale.getDisplayCountry();

			// putting in a map to remove duplicates
			if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(name))
			{
				countryMap.put(code, name);
			}
		}

		for (final String key : countryMap.keySet())
		{
			countries.add(new SelectOption(key, countryMap.get(key)));
		}

		Collections.sort(countries, CountryComparator.INSTANCE);

		return countries;
	}

	@ModelAttribute("mockErrorResponses")
	public List<SelectOption> getMockErrorResponses()
	{
		final List<SelectOption> errorResponses = new ArrayList<SelectOption>();
		errorResponses.add(new SelectOption("150", getMessage("mock.response.error.generalSystemFailure")));
		errorResponses.add(new SelectOption("151", getMessage("mock.response.error.serverTimeOut")));
		errorResponses.add(new SelectOption("152", getMessage("mock.response.error.serviceTimeout")));

		return errorResponses;
	}

	@RequestMapping(method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String getHopPaymentForm(final HttpServletRequest request, final Model model)
	{
		//Dummy Credit Card Information
		final PaymentDetailsForm paymentDetailsForm = new PaymentDetailsForm();

		// Copy over the default values
		BeanUtils.copyProperties(getDefaultPaymentDetailsForm(), paymentDetailsForm);

		// Override DefaultPaymentDetailsForm values with existing customer payment info data if available.
		if (StringUtils.isNotBlank(getParameter("card_accountNumber", request)))
		{
			paymentDetailsForm.setCardTypeCode(getParameter("card_cardType", request));

			//Force entry of card number as this is dependent on the card type.
			paymentDetailsForm.setCardNumber("");

			final String verificationNumber = getParameter("card_cvNumber", request);
			if (StringUtils.isNotBlank(verificationNumber))
			{
				paymentDetailsForm.setVerificationNumber(verificationNumber);
			}

			final String issueNumber = getParameter("card_issueNumber", request);
			if (StringUtils.isNotBlank(issueNumber))
			{
				paymentDetailsForm.setIssueNumber(issueNumber);
			}

			final String startMonth = getParameter("card_startMonth", request);
			if (StringUtils.isNotBlank(startMonth))
			{
				paymentDetailsForm.setStartMonth(startMonth);
			}

			final String startYear = getParameter("card_startYear", request);
			if (StringUtils.isNotBlank(startYear))
			{
				paymentDetailsForm.setStartYear(startYear);
			}

			paymentDetailsForm.setExpiryMonth(getParameter("card_expirationMonth", request));
			paymentDetailsForm.setExpiryYear(getParameter("card_expirationYear", request));
		}

		// Store the original parameters sent by the caller
		paymentDetailsForm.setOriginalParameters(serializeRequestParameters(request));

		//Actual Customer Billing Address
		final AddressForm addressForm = new AddressForm();
		if (request != null)
		{
			addressForm.setFirstName(getParameter("billTo_firstName", request));
			addressForm.setLastName(getParameter("billTo_lastName", request));
			addressForm.setLine1(getParameter("billTo_street1", request));
			addressForm.setLine2(getParameter("billTo_street2", request));
			addressForm.setTownCity(getParameter("billTo_city", request));
			addressForm.setState(getParameter("billTo_state", request));
			addressForm.setPostcode(getParameter("billTo_postalCode", request));
			addressForm.setCountryIso(getParameter("billTo_country", request));
			addressForm.setPhoneNumber(getParameter("billTo_phoneNumber", request));
			addressForm.setEmailAddress(getParameter("billTo_email", request));
		}

		paymentDetailsForm.setBillingAddress(addressForm);
		model.addAttribute("paymentDetailsForm", paymentDetailsForm);

		return HOP_PAYMENT_FORM_PAGE;
	}

	@RequestMapping(value = "/process", method = RequestMethod.POST)
	public String doValidateAndPost(@Valid final PaymentDetailsForm form, final BindingResult bindingResult,
			final HttpServletRequest request, final Model model)
	{
		getPaymentDetailsValidator().validate(form, bindingResult);

		if (bindingResult.hasErrors())
		{
			return HOP_PAYMENT_FORM_PAGE;
		}

		final Map<String, String> params = deserializeParameterMap(form.getOriginalParameters());

		final AddressForm billingAddress = form.getBillingAddress();
		if (params != null)
		{
			final String reason = form.getMockReasonCode();
			processTransactionDecision(request, reason, params);

			//Card Details
			final String endPortion = form.getCardNumber().trim().substring(form.getCardNumber().length() - 4);
			final String maskedCardNumber = "************" + endPortion;

			params.put("card_accountNumber", maskedCardNumber);
			params.put("card_cardType", form.getCardTypeCode());
			params.put("card_expirationMonth", form.getExpiryMonth());
			params.put("card_expirationYear", form.getExpiryYear());
			params.put("card_startMonth", form.getStartMonth());
			params.put("card_startYear", form.getStartYear());

			final String subscriptionId = UUID.randomUUID().toString();
			params.put("paySubscriptionCreateReply_subscriptionID", subscriptionId);
			params.put("paySubscriptionCreateReply_subscriptionIDPublicSignature", getMockedPublicDigest(subscriptionId));

			if (billingAddress != null)
			{
				params.put("billTo_firstName", billingAddress.getFirstName());
				params.put("billTo_lastName", billingAddress.getLastName());
				params.put("billTo_street1", billingAddress.getLine1());
				params.put("billTo_street2", billingAddress.getLine2());
				params.put("billTo_city", billingAddress.getTownCity());
				params.put("billTo_state", billingAddress.getState());
				params.put("billTo_postalCode", billingAddress.getPostcode());
				params.put("billTo_country", billingAddress.getCountryIso());
				params.put("billTo_phoneNumber", billingAddress.getPhoneNumber());
				params.put("billTo_email", billingAddress.getEmailAddress());
			}
			model.addAttribute("postUrl", params.get("orderPage_receiptResponseURL"));

			Boolean showDebugPage = form.getShowDebugPage();
			if (showDebugPage == null)
			{
				showDebugPage = Boolean.FALSE;
			}
			model.addAttribute("showDebugPage", showDebugPage);
			model.addAttribute("postParams", params);

			return HOP_REDIRECT_POST_PAGE;
		}

		return HOP_PAYMENT_FORM_PAGE;
	}

	protected void processTransactionDecision(final HttpServletRequest request, final String reasonCode,
			final Map<String, String> params)
	{
		if (params == null || request == null)
		{
			return;
		}

		String decision = "ACCEPT";
		if (request.getParameter("button.fail") != null)
		{
			decision = "ERROR";
		}

		String modReasonCode = reasonCode;
		if ("ACCEPT".equalsIgnoreCase(decision) && StringUtils.isBlank(reasonCode))
		{
			modReasonCode = "100";
		}
		else if (StringUtils.isBlank(reasonCode))
		{
			//General error
			modReasonCode = "150";
		}

		//Default decision to ACCEPT 100
		params.put("decision", decision);
		params.put("reasonCode", modReasonCode);
		params.put("decision_publicSignature", getMockedPublicDigest(decision + modReasonCode));
	}

	protected String getMockedPublicDigest(final String customValues)
	{
		String result;
		try
		{
			result = digestUtils.getPublicDigest(customValues, SHARED_SECRET);
		}
		catch (final Exception e)
		{
			result = "BzW+Xn0ZgZHeQRcFB6ri";
		}

		return result;
	}

	protected String getParameter(final String parameterName, final HttpServletRequest request)
	{
		final String result = request.getParameter(parameterName);
		if ("null".equalsIgnoreCase(result))
		{
			return "";
		}
		return StringUtils.isNotBlank(result) ? result : "";
	}

	protected String serializeRequestParameters(final HttpServletRequest request)
	{
		final StringBuilder result = new StringBuilder();

		final Enumeration myEnum = request.getParameterNames();
		while (myEnum.hasMoreElements())
		{
			final String paramName = (String) myEnum.nextElement();
			result.append(paramName).append(SEPARATOR_STR).append(request.getParameter(paramName));
			if (myEnum.hasMoreElements())
			{
				result.append(SEPARATOR_STR);
			}
		}

		return result.toString();
	}

	protected Map<String, String> deserializeParameterMap(final String paramString)
	{
		final Map<String, String> results = new HashMap<>();
		if (StringUtils.isNotBlank(paramString))
		{
			final String[] params = paramString.split(SEPARATOR_STR);
			for (int i = 0; i < params.length; i++)
			{
				results.put(params[i], (i + 1 >= params.length ? "" : params[++i]));
			}
		}
		return results;
	}





	protected String getMessage(final String code)
	{
		return getMessageSource().getMessage(code, null, Locale.getDefault());
	}

	/**
	 * Data class used to hold a drop down select option value. Holds the code identifier as well as the display name.
	 */
	public static class SelectOption
	{
		private final String code;
		private final String name;

		public SelectOption(final String code, final String name)
		{
			this.code = code;
			this.name = name;
		}

		public String getCode()
		{
			return code;
		}

		public String getName()
		{
			return name;
		}
	}

	/**
	 * Comparator class used to sort countries.
	 */
	public static class CountryComparator extends AbstractComparator<SelectOption>
	{
		public static final CountryComparator INSTANCE = new CountryComparator();

		@Override
		protected int compareInstances(final SelectOption option1, final SelectOption option2)
		{
			int result = compareValues(option1.getName(), option2.getName(), false);
			if (EQUAL == result)
			{
				result = compareValues(option1.getCode(), option2.getCode(), false);
			}
			return result;
		}
	}
}
