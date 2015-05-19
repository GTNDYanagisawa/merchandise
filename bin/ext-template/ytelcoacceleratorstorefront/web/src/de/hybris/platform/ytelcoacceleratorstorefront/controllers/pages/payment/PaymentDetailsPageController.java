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

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.subscriptionfacades.SubscriptionFacade;
import de.hybris.platform.subscriptionfacades.billing.CreditCardFacade;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPaymentData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;
import de.hybris.platform.util.Config;
import de.hybris.platform.ytelcoacceleratorstorefront.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.ytelcoacceleratorstorefront.constants.WebConstants;
import de.hybris.platform.ytelcoacceleratorstorefront.controllers.ControllerConstants;
import de.hybris.platform.ytelcoacceleratorstorefront.controllers.pages.AbstractPageController;
import de.hybris.platform.ytelcoacceleratorstorefront.controllers.pages.checkout.MultiStepCheckoutController;
import de.hybris.platform.ytelcoacceleratorstorefront.controllers.pages.checkout.MultiStepCheckoutController.CheckoutSteps;
import de.hybris.platform.ytelcoacceleratorstorefront.controllers.pages.checkout.MultiStepCheckoutController.SelectOption;
import de.hybris.platform.ytelcoacceleratorstorefront.controllers.util.CartHelper;
import de.hybris.platform.ytelcoacceleratorstorefront.controllers.util.GlobalMessages;
import de.hybris.platform.ytelcoacceleratorstorefront.forms.AddressForm;
import de.hybris.platform.ytelcoacceleratorstorefront.forms.PaymentDetailsForm;
import de.hybris.platform.ytelcoacceleratorstorefront.forms.PaymentSubscriptionsForm;
import de.hybris.platform.ytelcoacceleratorstorefront.forms.SopPaymentDetailsForm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;


@Controller
@RequestMapping("paymentDetails")
public class PaymentDetailsPageController extends AbstractPageController
{
	private static final Logger LOG = Logger.getLogger(PaymentDetailsPageController.class);

	private static final String SILENT_ORDER_POST_PAGE = "/pages/checkout/multi/sbgSilentOrderPostPage";
	private static final String PAYMENT_DETAILS_CMS_PAGE = "payment-details";
	private static final String MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL = "multiStepCheckoutSummary";
	private static final String REDIRECT_URL_ADD_PAYMENT_METHOD = REDIRECT_PREFIX
			+ "/paymentDetails/add-payment-method?targetArea=";
	private static final String REDIRECT_URL_SUMMARY = REDIRECT_PREFIX + "/checkout/multi/summary";

	private static final String ACCOUNT_AREA = "accountArea";
	private static final String MULTI_CHECKOUT_AREA = "multiCheckoutArea";

	private static final String REDIRECT_URL_CART = REDIRECT_PREFIX + "/cart";
	private static final String REDIRECT_TO_PAYMENT_INFO_PAGE = REDIRECT_PREFIX + "/my-account/payment-details";
	private static final String REDIRECT_TO_MANAGE_PAYMENT_METHOD = REDIRECT_PREFIX + "/my-account/manage-payment-method";

	@Resource(name = "subscriptionFacade")
	private SubscriptionFacade subscriptionFacade;

	@Resource(name = "userFacade")
	protected UserFacade userFacade;

	@Resource(name = "acceleratorCheckoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "siteBaseUrlResolutionService")
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	@Resource(name = "creditCardFacade")
	private CreditCardFacade creditCardFacade;

	@ModelAttribute("titles")
	public Collection<TitleData> getTitles()
	{
		return userFacade.getTitles();
	}

	@ModelAttribute("countries")
	public Collection<CountryData> getCountries()
	{
		return getCheckoutFacade().getDeliveryCountries();
	}

	@ModelAttribute("billingCountries")
	public Collection<CountryData> getBillingCountries()
	{
		return getCheckoutFacade().getBillingCountries();
	}

	@ModelAttribute("cardTypes")
	public Collection<CardTypeData> getCardTypes()
	{
		final Collection<CardTypeData> creditCards = getCheckoutFacade().getSupportedCardTypes();
		getCreditCardFacade().mappingStrategy(creditCards);

		return creditCards;
	}

	@ModelAttribute("months")
	public List<SelectOption> getMonths()
	{
		final List<SelectOption> months = new ArrayList<SelectOption>();

		months.add(new SelectOption("01", "01"));
		months.add(new SelectOption("02", "02"));
		months.add(new SelectOption("03", "03"));
		months.add(new SelectOption("04", "04"));
		months.add(new SelectOption("05", "05"));
		months.add(new SelectOption("06", "06"));
		months.add(new SelectOption("07", "07"));
		months.add(new SelectOption("08", "08"));
		months.add(new SelectOption("09", "09"));
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

	@ModelAttribute("checkoutSteps")
	public List<CheckoutSteps> addCheckoutStepsToModel(final HttpServletRequest request)
	{
		final String baseUrl = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString())
				.replacePath(request.getContextPath()).build().toUriString();

		return MultiStepCheckoutController.createCheckoutSteps(baseUrl);
	}

	@RequestMapping(value = "/add-payment-method", method = RequestMethod.GET)
	public String doAddPaymentMethod(final HttpServletRequest request, final Model model,
			@RequestParam("targetArea") final String targetArea) throws CMSItemNotFoundException
	{
		if (StringUtils.equals(targetArea, MULTI_CHECKOUT_AREA) && !hasItemsInCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}

		final String clientIp = getClientIpAddr(request);
		setupAddPaymentPage(model);

		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute("metaRobots", "no-index,no-follow");
		model.addAttribute("isFirstPaymentMethod", Boolean.valueOf(userFacade.getCCPaymentInfos(true).size() == 0));

		// Build up the SOP form data and render page containing form
		if (Config.getBoolean("accelerator.storefront.checkout.multistep.sop", false))
		{
            final SopPaymentDetailsForm sopPaymentDetailsForm = getSopPaymentDetailsForm(model);

			if (ACCOUNT_AREA.equals(targetArea))
			{
				model.addAttribute("newBillingAddress", "true");
			}
            else
            {
                model.addAttribute("deliveryAddress", checkoutFacade.getCheckoutCart().getDeliveryAddress());
                sopPaymentDetailsForm.setNewBillingAddress(Boolean.FALSE);
            }

			try
			{
				final CartData cartData = getCheckoutFacade().getCheckoutCart();
				cartData.setEntries(CartHelper.removeEmptyEntries(cartData.getEntries()));
				model.addAttribute("cartData", cartData);
				setupSilentOrderPostPage(sopPaymentDetailsForm, model, clientIp, targetArea, null);
				return SILENT_ORDER_POST_PAGE;
			}
			catch (final Exception e)
			{
				LOG.error("Failed to setup payment details form", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
				model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);

				if (StringUtils.equals(targetArea, ACCOUNT_AREA))
				{
					return ControllerConstants.Views.Pages.Account.AccountPaymentInfoPage;
				}

				return ControllerConstants.Views.Pages.MultiStepCheckout.ChooseDeliveryMethodPage;
			}
		}
		else
		{
			// If not using HOP we need to build up the payment details form
			final PaymentDetailsForm paymentDetailsForm = new PaymentDetailsForm();
			final AddressForm addressForm = new AddressForm();
			paymentDetailsForm.setBillingAddress(addressForm);
			model.addAttribute(paymentDetailsForm);

			if (StringUtils.equals(targetArea, ACCOUNT_AREA))
			{
				return ControllerConstants.Views.Pages.Account.AccountPaymentInfoPage;
			}

			return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
		}
	}

	@RequestMapping(value = "/edit-payment-details", method = RequestMethod.GET)
	public String doEditPaymentDetails(final HttpServletRequest request, final Model model,
			@RequestParam("targetArea") final String targetArea, @RequestParam("paymentInfoId") final String paymentInfoId)
			throws CMSItemNotFoundException
	{
		if (StringUtils.isBlank(paymentInfoId))
		{
			LOG.warn("Payment method id may not be blank.");
			return ControllerConstants.Views.Pages.Account.AccountPaymentInfoPage;
		}

		final String clientIp = getClientIpAddr(request);
		setupAddPaymentPage(model);

		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute("metaRobots", "no-index,no-follow");
		model.addAttribute("isEditMode", "true");
		model.addAttribute("isFirstPaymentMethod", Boolean.valueOf(userFacade.getCCPaymentInfos(true).size() == 1));

		final CCPaymentInfoData ccPaymentInfoData = userFacade.getCCPaymentInfoForCode(paymentInfoId);

		// Build up the SOP form data and render page containing form
		if (Config.getBoolean("accelerator.storefront.checkout.multistep.sop", false))
		{
			if (null == ccPaymentInfoData)
			{
				GlobalMessages.addErrorMessage(model, "text.account.paymentDetails.nonExisting.error");
				return ControllerConstants.Views.Pages.Account.AccountPaymentInfoPage;
			}

			final SopPaymentDetailsForm sopPaymentDetailsForm = getSopPaymentDetailsForm(model);

			try
			{
				setupSilentOrderPostPage(sopPaymentDetailsForm, model, clientIp, targetArea, ccPaymentInfoData);

				return SILENT_ORDER_POST_PAGE;
			}
			catch (final Exception e)
			{
				LOG.error("Failed to setup payment details form", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
				model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);

				if (StringUtils.equals(targetArea, ACCOUNT_AREA))
				{
					return ControllerConstants.Views.Pages.Account.AccountPaymentInfoPage;
				}

				return ControllerConstants.Views.Pages.MultiStepCheckout.ChooseDeliveryMethodPage;
			}
		}
		else
		{
			// If not using HOP we need to build up the payment details form
			final PaymentDetailsForm paymentDetailsForm = new PaymentDetailsForm();
			final AddressForm addressForm = new AddressForm();
			paymentDetailsForm.setBillingAddress(addressForm);
			model.addAttribute(paymentDetailsForm);

			if (StringUtils.equals(targetArea, ACCOUNT_AREA))
			{
				return ControllerConstants.Views.Pages.Account.AccountPaymentInfoPage;
			}

			return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
		}
	}

	private SopPaymentDetailsForm getSopPaymentDetailsForm(final Model model)
	{
		SopPaymentDetailsForm sopPaymentDetailsForm = new SopPaymentDetailsForm();
		if (model.containsAttribute("sopPaymentDetailsForm"))
		{
			sopPaymentDetailsForm = (SopPaymentDetailsForm) model.asMap().get("sopPaymentDetailsForm");
		}

		return sopPaymentDetailsForm;
	}

	@RequestMapping(value = "/remove-payment-method", method = RequestMethod.GET)
	public String removePaymentMethod(@RequestParam(value = "paymentInfoId") final String paymentMethodId,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		final CCPaymentInfoData ccPaymentInfoData = userFacade.getCCPaymentInfoForCode(paymentMethodId);

		try
		{
			if (null == ccPaymentInfoData)
			{
				redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
						Collections.singletonList("text.account.paymentDetails.nonExisting.error"));
			}
			else
			{
				subscriptionFacade.changePaymentMethod(ccPaymentInfoData, "disable", true, new HashMap<String, String>());
				userFacade.unlinkCCPaymentInfo(paymentMethodId);
				redirectAttributes.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
						Collections.singletonList("text.account.profile.paymentCard.removed"));
			}
		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.error(String.format("Removing payment method with id %s failed", paymentMethodId), e);

			redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
					Collections.singletonList("text.account.paymentDetails.remove.error"));
		}

		return REDIRECT_TO_PAYMENT_INFO_PAGE;
	}

	@RequestMapping(value = "/change-payment-method-subscription", method = RequestMethod.POST)
	public String changePaymentMethodForSubscriptions(@RequestParam(value = "paymentInfoId") final String oldPaymentMethodId,
			final PaymentSubscriptionsForm paymentSubscriptionsForm, final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		if (paymentSubscriptionsForm != null)
		{
			final CCPaymentInfoData ccToDelete = userFacade.getCCPaymentInfoForCode(oldPaymentMethodId);
			model.addAttribute("paymentInfo", ccToDelete);

			final CCPaymentInfoData ccPaymentInfoData = userFacade.getCCPaymentInfoForCode(paymentSubscriptionsForm
					.getNewPaymentMethodId());

			if (null == ccPaymentInfoData)
			{
				redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
						Collections.singletonList("text.account.paymentDetails.nonExisting.error"));

				return REDIRECT_TO_PAYMENT_INFO_PAGE;
			}

			if (paymentSubscriptionsForm.getSubscriptionsToChange() != null)
			{
				try
				{
					for (final String subscriptionId : paymentSubscriptionsForm.getSubscriptionsToChange())
					{
						subscriptionFacade.replacePaymentMethod(subscriptionId, ccPaymentInfoData.getSubscriptionId(), null);
					}

					redirectAttributes.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
							Collections.singletonList("text.account.paymentDetails.associatedSubscriptions.changeSuccessful"));

				}
				catch (final SubscriptionFacadeException e)
				{
					redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
							Collections.singletonList("text.account.paymentDetails.manageSubscriptions.unable"));
				}
			}
		}

		return REDIRECT_TO_MANAGE_PAYMENT_METHOD + "?paymentInfoId=" + oldPaymentMethodId;
	}

	protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("metaRobots", "no-index,no-follow");
		model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(hasNoPaymentInfo()));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, getAccountBreadcrumbBuilder()
				.getBreadcrumbs("text.account.paymentDetails"));
		final ContentPageModel contentPage = getContentPageForLabelOrId(getLabelOrId(model));
		storeCmsPageInModel(model, contentPage);
		setUpMetaDataForContentPage(model, contentPage);
	}

	private String getLabelOrId(final Model model)
	{
		if (StringUtils.equals(ACCOUNT_AREA, (String) model.asMap().get("targetArea")))
		{
			return PAYMENT_DETAILS_CMS_PAGE;
		}
		else if (StringUtils.equals(MULTI_CHECKOUT_AREA, (String) model.asMap().get("targetArea")))
		{
			return MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL;
		}

		return StringUtils.EMPTY;
	}

	protected void setupSilentOrderPostPage(final SopPaymentDetailsForm sopPaymentDetailsForm, final Model model,
			final String clientIpAddress, final String targetArea, final CCPaymentInfoData ccPaymentInfoData)
	{
		model.addAttribute("targetArea", targetArea);

		try
		{
			final String postUrl = getSubscriptionFacade().hpfUrl();
			final SubscriptionPaymentData initResult = getSubscriptionFacade().initializeTransaction(clientIpAddress,
					getSopResponseUrl(true, targetArea), getSopResponseUrl(true, targetArea), new HashMap<String, String>());
			final String sessionToken = initResult.getParameters().get("sessionTransactionToken");

			Assert.notNull(sessionToken, "Session token may not be null");
			Assert.notNull(postUrl, "Post URL may not be null");

			getSessionService().setAttribute("authorizationRequestId", clientIpAddress);
			getSessionService().setAttribute("authorizationRequestToken", sessionToken);

			model.addAttribute("postUrl", postUrl);
			model.addAttribute("sessionToken", sessionToken);
		}
		catch (final SubscriptionFacadeException e)
		{
			model.addAttribute("postUrl", null);
			model.addAttribute("sessionToken", null);
			LOG.warn("Failed to initialize session for silent order post page", e);
			GlobalMessages.addErrorMessage(model, "checkout.multi.sop.globalError");
		}
		catch (final IllegalArgumentException e)
		{
			model.addAttribute("postUrl", null);
			model.addAttribute("sessionToken", null);
			LOG.warn("Failed to set up silent order post page", e);
			GlobalMessages.addErrorMessage(model, "checkout.multi.sop.globalError");
		}

		if (!model.containsAttribute("accErrorMsgs"))
		{
			setupSopPaymentDetailsForm(sopPaymentDetailsForm, ccPaymentInfoData, targetArea);
		}

		model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);
		model.addAttribute("paymentInfo", ccPaymentInfoData);
	}

	private void setupSopPaymentDetailsForm(final SopPaymentDetailsForm sopPaymentDetailsForm,
			final CCPaymentInfoData ccPaymentInfoData, final String targetArea)
	{
		if (null != ccPaymentInfoData)
		{
			sopPaymentDetailsForm.setCardNumber(ccPaymentInfoData.getCardNumber());
			sopPaymentDetailsForm.setCardTypeCode(ccPaymentInfoData.getCardType());
			sopPaymentDetailsForm.setExpiryMonth(ccPaymentInfoData.getExpiryMonth());
			sopPaymentDetailsForm.setExpiryYear(ccPaymentInfoData.getExpiryYear());
			sopPaymentDetailsForm.setIssueNumber(ccPaymentInfoData.getIssueNumber());
			sopPaymentDetailsForm.setNameOnCard(ccPaymentInfoData.getAccountHolderName());
			sopPaymentDetailsForm.setStartMonth(ccPaymentInfoData.getStartMonth());
			sopPaymentDetailsForm.setStartYear(ccPaymentInfoData.getStartYear());

            setupBillingAddress(sopPaymentDetailsForm, ccPaymentInfoData.getBillingAddress());
		}
	}

	private void setupBillingAddress(final SopPaymentDetailsForm sopPaymentDetailsForm, final AddressData address)
	{
		sopPaymentDetailsForm.setNewBillingAddress(Boolean.TRUE);

		if (null != address)
		{
			final AddressForm addressForm = new AddressForm();
			addressForm.setTitleCode(address.getTitleCode());
			addressForm.setFirstName(address.getFirstName());
			addressForm.setLastName(address.getLastName());
			addressForm.setLine1(address.getLine1());
			addressForm.setLine2(address.getLine2());
			addressForm.setTownCity(address.getTown());
			addressForm.setPostcode(address.getPostalCode());

			if (null != address.getCountry())
			{
				addressForm.setCountryIso(address.getCountry().getIsocode());
			}

			sopPaymentDetailsForm.setBillingAddress(addressForm);
		}
	}

	protected String getClientIpAddr(final HttpServletRequest request)
	{
		String clientIp = request.getHeader("X-Forwarded-For");
		if (StringUtils.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp))
		{
			clientIp = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp))
		{
			clientIp = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp))
		{
			clientIp = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StringUtils.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp))
		{
			clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StringUtils.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp))
		{
			clientIp = request.getRemoteAddr();
		}
		return clientIp;
	}

	protected String getSopResponseUrl(final boolean secure, final String targetArea)
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

		final String sopResponseUrl = getSiteBaseUrlResolutionService().getWebsiteUrlForSite(currentBaseSite, secure,
				"/paymentDetails/sop-response?targetArea=" + targetArea);

		return sopResponseUrl == null ? "NOTSET" : sopResponseUrl;
	}

	@RequestMapping(value = "/sop-response", method = RequestMethod.GET)
	public String doHandleSopResponse(final RedirectAttributes redirectModel, @RequestParam("targetArea") final String targetArea)
			throws CMSItemNotFoundException
	{
		final String authorizationRequestId = (String) getSessionService().getAttribute("authorizationRequestId");
		final String authorizationRequestToken = (String) getSessionService().getAttribute("authorizationRequestToken");

		try
		{
			final SubscriptionPaymentData result = getSubscriptionFacade().finalizeTransaction(authorizationRequestId,
					authorizationRequestToken, new HashMap<String, String>());
			final CCPaymentInfoData newPaymentSubscription = getSubscriptionFacade().createPaymentSubscription(
					result.getParameters());
			getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
			getCheckoutFacade().setPaymentDetails(newPaymentSubscription.getId());
		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.error("Creating a new payment method failed", e);
			redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
					Collections.singletonList("checkout.multi.paymentMethod.addPaymentDetails.incomplete"));

			return REDIRECT_URL_ADD_PAYMENT_METHOD + targetArea;
		}

		if (StringUtils.equals(targetArea, ACCOUNT_AREA))
		{
			return ControllerConstants.Views.Pages.Account.AccountPaymentInfoPage;
		}

		return REDIRECT_URL_SUMMARY;
	}

	protected boolean hasNoPaymentInfo()
	{
		getCheckoutFacade().setPaymentInfoIfAvailable();
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		return (cartData == null || cartData.getPaymentInfo() == null);
	}

	/**
	 * Checks if there are any items in the cart.
	 * 
	 * @return returns true if items found in cart.
	 */
	protected boolean hasItemsInCart()
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();

		return (cartData.getEntries() != null && !cartData.getEntries().isEmpty());
	}

	protected SubscriptionFacade getSubscriptionFacade()
	{
		return subscriptionFacade;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	protected SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	protected UserFacade getUserFacade()
	{
		return userFacade;
	}

	protected CheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}

	protected ResourceBreadcrumbBuilder getAccountBreadcrumbBuilder()
	{
		return accountBreadcrumbBuilder;
	}

	public CreditCardFacade getCreditCardFacade()
	{
		return creditCardFacade;
	}
}
