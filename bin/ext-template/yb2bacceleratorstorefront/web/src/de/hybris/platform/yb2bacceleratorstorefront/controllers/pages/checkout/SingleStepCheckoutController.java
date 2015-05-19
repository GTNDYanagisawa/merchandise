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
package de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.checkout;

import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.order.B2BCartFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BDaysOfWeekData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.yb2bacceleratorstorefront.annotations.RequireHardLogIn;
import de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import de.hybris.platform.yb2bacceleratorstorefront.constants.WebConstants;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.ControllerConstants;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.util.GlobalMessages;
import de.hybris.platform.yb2bacceleratorstorefront.forms.AddressForm;
import de.hybris.platform.yb2bacceleratorstorefront.forms.PaymentDetailsForm;
import de.hybris.platform.yb2bacceleratorstorefront.forms.PlaceOrderForm;
import de.hybris.platform.yb2bacceleratorstorefront.forms.validation.PaymentDetailsValidator;
import de.hybris.platform.yb2bacceleratorstorefront.security.B2BUserGroupProvider;
import de.hybris.platform.yb2bacceleratorstorefront.util.XSSFilterUtil;
import de.hybris.platform.yb2bacceleratorstorefront.variants.VariantSortStrategy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * SingleStepCheckoutController
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/checkout/single")
public class SingleStepCheckoutController extends AbstractCheckoutController
{
	@SuppressWarnings("unused")
	protected static final Logger LOG = Logger.getLogger(SingleStepCheckoutController.class);

	private static final String SINGLE_STEP_CHECKOUT_SUMMARY_CMS_PAGE = "singleStepCheckoutSummaryPage";

	@Resource(name = "paymentDetailsValidator")
	private PaymentDetailsValidator paymentDetailsValidator;

	@Resource(name = "b2bProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "b2bUserGroupProvider")
	private B2BUserGroupProvider b2bUserGroupProvider;

	@Resource(name = "b2bContentPageBreadcrumbBuilder")
	private ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder;

	@ModelAttribute("titles")
	public Collection<TitleData> getTitles()
	{
		return getUserFacade().getTitles();
	}

	@ModelAttribute("countries")
	public Collection<CountryData> getCountries()
	{
		return getCheckoutFlowFacade().getDeliveryCountries();
	}

	@ModelAttribute("billingCountries")
	public Collection<CountryData> getBillingCountries()
	{
		return getCheckoutFlowFacade().getBillingCountries();
	}

	@ModelAttribute("costCenters")
	public List<? extends B2BCostCenterData> getVisibleActiveCostCenters()
	{
		final List<? extends B2BCostCenterData> costCenterData = getCheckoutFlowFacade().getActiveVisibleCostCenters();
		return costCenterData == null ? Collections.<B2BCostCenterData> emptyList() : costCenterData;
	}

	@ModelAttribute("paymentTypes")
	public Collection<B2BPaymentTypeData> getAllB2BPaymentTypes()
	{
		return getCheckoutFlowFacade().getPaymentTypesForCheckoutSummary();
	}

	@ModelAttribute("daysOfWeek")
	public Collection<B2BDaysOfWeekData> getAllDaysOfWeek()
	{
		return getCheckoutFlowFacade().getDaysOfWeekForReplenishmentCheckoutSummary();
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

	@ModelAttribute("cardTypes")
	public Collection<CardTypeData> getCardTypes()
	{
		return getCheckoutFlowFacade().getSupportedCardTypes();
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

	@InitBinder
	protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder)
	{
		final DateFormat dateFormat = new SimpleDateFormat(getMessageSource().getMessage("text.store.dateformat", null,
				getI18nService().getCurrentLocale()));
		final CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
		binder.registerCustomEditor(Date.class, editor);
	}

	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
	public String checkoutSummary()
	{
		if (hasItemsInCart())
		{
			return REDIRECT_PREFIX + "/checkout/single/summary";
		}
		return REDIRECT_PREFIX + "/cart";
	}

	@RequestMapping(value = "/summary", method = {RequestMethod.GET, RequestMethod.POST})
	@RequireHardLogIn
	public String checkoutSummary(final Model model) throws CMSItemNotFoundException
	{

		if (!b2bUserGroupProvider.isCurrentUserAuthorizedToCheckOut())
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.invalid.accountType");
			return FORWARD_PREFIX + "/cart";
		}

		if (!hasItemsInCart())
		{
			// no items in the cart
			return FORWARD_PREFIX + "/cart";
		}

		getCheckoutFlowFacade().setDeliveryAddressIfAvailable();
		getCheckoutFlowFacade().setDeliveryModeIfAvailable();
		getCheckoutFlowFacade().setPaymentInfoIfAvailable();
		getCheckoutFlowFacade().setDefaultPaymentTypeForCheckout();

		final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();
		if (cartData.getEntries() != null && !cartData.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				final String productCode = entry.getProduct().getCode();
				final ProductData product = productFacade.getProductForCodeAndOptions(productCode,
						Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));
				entry.setProduct(product);
			}
		}

		// Try to set default delivery address and delivery mode
		if (cartData.getPaymentType() == null)
		{
			getCheckoutFlowFacade().setPaymentTypeSelectedForCheckout(CheckoutPaymentType.ACCOUNT.getCode());
		}

		model.addAttribute("cartData", cartData);
		model.addAttribute("allItems", cartData.getEntries());
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		model.addAttribute("deliveryMode", cartData.getDeliveryMode());
		model.addAttribute("paymentInfo", cartData.getPaymentInfo());
		model.addAttribute("costCenter", cartData.getCostCenter());
		model.addAttribute("quoteText", new B2BCommentData());
		// TODO:Make configuration hmc driven than hardcoding in controllers
		model.addAttribute("nDays", getNumberRange(1, 30));
		model.addAttribute("nthDayOfMonth", getNumberRange(1, 31));
		model.addAttribute("nthWeek", getNumberRange(1, 12));

		model.addAttribute(new AddressForm());
		model.addAttribute(new PaymentDetailsForm());
		if (!model.containsAttribute("placeOrderForm"))
		{
			final PlaceOrderForm placeOrderForm = new PlaceOrderForm();
			// TODO: Make setting of default recurrence enum value hmc driven rather hard coding in controller
			placeOrderForm.setReplenishmentRecurrence(B2BReplenishmentRecurrenceEnum.MONTHLY);
			placeOrderForm.setnDays("14");
			final List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
			daysOfWeek.add(DayOfWeek.MONDAY);
			placeOrderForm.setnDaysOfWeek(daysOfWeek);
			model.addAttribute("placeOrderForm", placeOrderForm);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SINGLE_STEP_CHECKOUT_SUMMARY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SINGLE_STEP_CHECKOUT_SUMMARY_CMS_PAGE));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.SingleStepCheckout.CheckoutSummaryPage;
	}

	@ResponseBody
	@RequestMapping(value = "/summary/getCheckoutCart.json", method = {RequestMethod.GET, RequestMethod.POST} )
	@RequireHardLogIn
	public CartData getCheckoutCart()
	{
		final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();

		return cartData;
	}

	@ResponseBody
	@RequestMapping(value = "/summary/getCostCenters.json", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public List<? extends B2BCostCenterData> getCostCenters()
	{
		return getVisibleActiveCostCenters();
	}

	@ResponseBody
	@RequestMapping(value = "/summary/getDeliveryAddresses.json", method = {RequestMethod.GET, RequestMethod.POST})
	@RequireHardLogIn
	public List<? extends AddressData> getDeliveryAddresses()
	{
		final List<? extends AddressData> deliveryAddresses = getCheckoutFlowFacade().getSupportedDeliveryAddresses(true);
		if (deliveryAddresses == null)
		{
			return Collections.<AddressData> emptyList();
		}
		for (final AddressData address : deliveryAddresses)
		{
			if (getUserFacade().isDefaultAddress(address.getId()))
			{
				address.setDefaultAddress(true);
				break;
			}
		}
		return deliveryAddresses;
	}

	@ResponseBody
	@RequestMapping(value = "/summary/setDefaultAddress.json", method = {RequestMethod.GET, RequestMethod.POST})
	@RequireHardLogIn
	public List<? extends AddressData> setDefaultAddress(@RequestParam(value = "addressId") final String addressId)
	{
		getUserFacade().setDefaultAddress(getUserFacade().getAddressForCode(addressId));
		return getDeliveryAddresses();
	}

	@ResponseBody
	@RequestMapping(value = "/summary/setDeliveryAddress.json", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public CartData setDeliveryAddress(@RequestParam(value = "addressId") final String addressId)
	{
		AddressData addressData = null;

		final List<? extends AddressData> deliveryAddresses = getCheckoutFlowFacade().getSupportedDeliveryAddresses(false);
		for (final AddressData deliveryAddress : deliveryAddresses)
		{
			if (deliveryAddress.getId().equals(addressId))
			{
				addressData = deliveryAddress;
				break;
			}
		}

		if (addressData != null && getCheckoutFlowFacade().setDeliveryAddress(addressData))
		{
			final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();
			return cartData;
		}

		return null;
	}

	@ResponseBody
	@RequestMapping(value = "/summary/getDeliveryModes.json", method = {RequestMethod.GET, RequestMethod.POST})
	@RequireHardLogIn
	public List<? extends DeliveryModeData> getDeliveryModes()
	{
		final List<? extends DeliveryModeData> deliveryModes = getCheckoutFlowFacade().getSupportedDeliveryModes();
		return deliveryModes == null ? Collections.<ZoneDeliveryModeData> emptyList() : deliveryModes;
	}

	@ResponseBody
	@RequestMapping(value = "/summary/setDeliveryMode.json", method = {RequestMethod.GET, RequestMethod.POST})
	@RequireHardLogIn
	public CartData setDeliveryMode(@RequestParam(value = "modeCode") final String modeCode)
	{
		if (getCheckoutFlowFacade().setDeliveryMode(modeCode))
		{
			final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();
			return cartData;
		}
		else
		{
			return null;
		}
	}

	@RequestMapping(value = "/summary/getDeliveryAddressForm.json", method = {RequestMethod.GET, RequestMethod.POST})
	@RequireHardLogIn
	public String getDeliveryAddressForm(final Model model, @RequestParam(value = "addressId") final String addressId,
			@RequestParam(value = "createUpdateStatus") final String createUpdateStatus)
	{
		AddressData addressData = null;
		if (addressId != null && !addressId.isEmpty())
		{
			addressData = getCheckoutFlowFacade().getDeliveryAddressForCode(addressId);
		}

		final AddressForm addressForm = new AddressForm();

		final boolean hasAddressData = addressData != null;
		if (hasAddressData)
		{
			addressForm.setAddressId(addressData.getId());
			addressForm.setTitleCode(addressData.getTitleCode());
			addressForm.setFirstName(addressData.getFirstName());
			addressForm.setLastName(addressData.getLastName());
			addressForm.setLine1(addressData.getLine1());
			addressForm.setLine2(addressData.getLine2());
			addressForm.setTownCity(addressData.getTown());
			addressForm.setPostcode(addressData.getPostalCode());
			addressForm.setCountryIso(addressData.getCountry().getIsocode());
			addressForm.setShippingAddress(Boolean.valueOf(addressData.isShippingAddress()));
			addressForm.setBillingAddress(Boolean.valueOf(addressData.isBillingAddress()));
		}

		model.addAttribute("edit", Boolean.valueOf(hasAddressData));
		model.addAttribute("noAddresses", Boolean.valueOf(getUserFacade().isAddressBookEmpty()));

		model.addAttribute(addressForm);
		model.addAttribute("createUpdateStatus", createUpdateStatus);

		// Work out if the address form should be displayed based on the payment type
		final B2BPaymentTypeData paymentType = getCheckoutFlowFacade().getCheckoutCart().getPaymentType();
		final boolean payOnAccount = paymentType != null && CheckoutPaymentType.ACCOUNT.getCode().equals(paymentType.getCode());
		model.addAttribute("showAddressForm", Boolean.valueOf(!payOnAccount));

		return ControllerConstants.Views.Fragments.SingleStepCheckout.DeliveryAddressFormPopup;
	}

	@RequestMapping(value = "/summary/createUpdateDeliveryAddress.json", method = RequestMethod.POST)
	@RequireHardLogIn
	public String createUpdateDeliveryAddress(final Model model, @Valid final AddressForm form, final BindingResult bindingResult)
	{
		if (bindingResult.hasErrors())
		{
			model.addAttribute("edit", Boolean.valueOf(StringUtils.isNotBlank(form.getAddressId())));
			// Work out if the address form should be displayed based on the payment type
			final B2BPaymentTypeData paymentType = getCheckoutFlowFacade().getCheckoutCart().getPaymentType();
			final boolean payOnAccount = paymentType != null && CheckoutPaymentType.ACCOUNT.getCode().equals(paymentType.getCode());
			model.addAttribute("showAddressForm", Boolean.valueOf(!payOnAccount));

			return ControllerConstants.Views.Fragments.SingleStepCheckout.DeliveryAddressFormPopup;
		}

		// create delivery address and set it on cart
		final AddressData addressData = new AddressData();
		addressData.setId(form.getAddressId());
		addressData.setTitleCode(form.getTitleCode());
		addressData.setFirstName(form.getFirstName());
		addressData.setLastName(form.getLastName());
		addressData.setLine1(form.getLine1());
		addressData.setLine2(form.getLine2());
		addressData.setTown(form.getTownCity());
		addressData.setPostalCode(form.getPostcode());
		addressData.setCountry(getI18NFacade().getCountryForIsocode(form.getCountryIso()));
		addressData.setShippingAddress(Boolean.TRUE.equals(form.getShippingAddress())
				|| Boolean.TRUE.equals(form.getSaveInAddressBook()));

		addressData.setVisibleInAddressBook(Boolean.TRUE.equals(form.getSaveInAddressBook())
				|| StringUtils.isNotBlank(form.getAddressId()));
		addressData.setDefaultAddress(Boolean.TRUE.equals(form.getDefaultAddress()));

		if (StringUtils.isBlank(form.getAddressId()))
		{
			getUserFacade().addAddress(addressData);
		}
		else
		{
			getUserFacade().editAddress(addressData);
		}

		getCheckoutFlowFacade().setDeliveryAddress(addressData);

		if (getCheckoutFlowFacade().getCheckoutCart().getDeliveryMode() == null)
		{
			getCheckoutFlowFacade().setDeliveryModeIfAvailable();
		}

		model.addAttribute("createUpdateStatus", "Success");
		model.addAttribute("addressId", addressData.getId());

		return REDIRECT_PREFIX + "/checkout/single/summary/getDeliveryAddressForm.json?addressId=" + addressData.getId()
				+ "&createUpdateStatus=Success";
	}

	@ResponseBody
	@RequestMapping(value = "/summary/setCostCenter.json", method = RequestMethod.POST)
	@RequireHardLogIn
	public CartData setCostCenter(@RequestParam(value = "costCenterId") final String costCenterId)
	{
		// remove the delivery address;
		getCheckoutFlowFacade().removeDeliveryAddress();
		getCheckoutFlowFacade().removeDeliveryMode();
		final CartData cartData = getCheckoutFlowFacade().setCostCenterForCart(costCenterId,
				this.getCheckoutFlowFacade().getCheckoutCart().getCode());

		return cartData;
	}

	@ResponseBody
	@RequestMapping(value = "/summary/updateCostCenter.json", method = RequestMethod.POST)
	@RequireHardLogIn
	public CartData updateCostCenterForCart(@RequestParam(value = "costCenterId") final String costCenterId)
	{
		final CartData cartData = getCheckoutFlowFacade().setCostCenterForCart(costCenterId,
				getCheckoutFlowFacade().getCheckoutCart().getCode());

		return cartData;
	}

	@ResponseBody
	@RequestMapping(value = "/summary/getSavedCards.json", method = {RequestMethod.GET, RequestMethod.POST})
	@RequireHardLogIn
	public List<CCPaymentInfoData> getSavedCards()
	{
		final List<CCPaymentInfoData> paymentInfos = getUserFacade().getCCPaymentInfos(true);
		return paymentInfos == null ? Collections.<CCPaymentInfoData> emptyList() : paymentInfos;
	}

	@ResponseBody
	@RequestMapping(value = "/summary/setPaymentDetails.json", method = RequestMethod.POST)
	@RequireHardLogIn
	public CartData setPaymentDetails(@RequestParam(value = "paymentId") final String paymentId)
	{
		if (StringUtils.isNotBlank(paymentId) && getCheckoutFlowFacade().setPaymentDetails(paymentId))
		{
			final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();
			return cartData;
		}

		return null;
	}

	@ResponseBody
	@RequestMapping(value = "/summary/setPaymentType.json", method = RequestMethod.POST)
	@RequireHardLogIn
	public CartData setPaymentType(@RequestParam(value = "paymentType") final String paymentType)
	{
		getCheckoutFlowFacade().setPaymentTypeSelectedForCheckout(paymentType);
		getCheckoutFlowFacade().removeDeliveryAddress();
		getCheckoutFlowFacade().removeDeliveryMode();
		getCheckoutFlowFacade().setCostCenterForCart("", getCheckoutFlowFacade().getCheckoutCart().getCode());

		final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();

		return cartData;
	}

	@ResponseBody
	@RequestMapping(value = "/summary/setPurchaseOrderNumber.json", method = RequestMethod.POST)
	@RequireHardLogIn
	public CartData setPurchaseOrderNumber(@RequestParam(value = "purchaseOrderNumber") final String purchaseOrderNumber)
	{
		getCheckoutFlowFacade().setPurchaseOrderNumber(purchaseOrderNumber);
		final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();

		return cartData;
	}

	@RequestMapping(value = "/summary/getPaymentDetailsForm.json", method = {RequestMethod.GET, RequestMethod.POST})
	@RequireHardLogIn
	public String getPaymentDetailsForm(final Model model, @RequestParam(value = "paymentId") final String paymentId,
			@RequestParam(value = "createUpdateStatus") final String createUpdateStatus)
	{
		CCPaymentInfoData paymentInfoData = null;
		if (StringUtils.isNotBlank(paymentId))
		{
			paymentInfoData = getUserFacade().getCCPaymentInfoForCode(paymentId);
		}

		final PaymentDetailsForm paymentDetailsForm = new PaymentDetailsForm();

		if (paymentInfoData != null)
		{
			paymentDetailsForm.setPaymentId(paymentInfoData.getId());
			paymentDetailsForm.setCardTypeCode(paymentInfoData.getCardType());
			paymentDetailsForm.setNameOnCard(paymentInfoData.getAccountHolderName());
			paymentDetailsForm.setCardNumber(paymentInfoData.getCardNumber());
			paymentDetailsForm.setStartMonth(paymentInfoData.getStartMonth());
			paymentDetailsForm.setStartYear(paymentInfoData.getStartYear());
			paymentDetailsForm.setExpiryMonth(paymentInfoData.getExpiryMonth());
			paymentDetailsForm.setExpiryYear(paymentInfoData.getExpiryYear());
			paymentDetailsForm.setSaveInAccount(Boolean.valueOf(paymentInfoData.isSaved()));
			paymentDetailsForm.setIssueNumber(paymentInfoData.getIssueNumber());

			final AddressForm addressForm = new AddressForm();
			final AddressData addressData = paymentInfoData.getBillingAddress();
			if (addressData != null)
			{
				addressForm.setAddressId(addressData.getId());
				addressForm.setTitleCode(addressData.getTitleCode());
				addressForm.setFirstName(addressData.getFirstName());
				addressForm.setLastName(addressData.getLastName());
				addressForm.setLine1(addressData.getLine1());
				addressForm.setLine2(addressData.getLine2());
				addressForm.setTownCity(addressData.getTown());
				addressForm.setPostcode(addressData.getPostalCode());
				addressForm.setCountryIso(addressData.getCountry().getIsocode());
				addressForm.setShippingAddress(Boolean.valueOf(addressData.isShippingAddress()));
				addressForm.setBillingAddress(Boolean.valueOf(addressData.isBillingAddress()));
			}

			paymentDetailsForm.setBillingAddress(addressForm);
		}

		model.addAttribute("edit", Boolean.valueOf(paymentInfoData != null));
		model.addAttribute("paymentInfoData", getUserFacade().getCCPaymentInfos(true));
		model.addAttribute(paymentDetailsForm);
		model.addAttribute("createUpdateStatus", createUpdateStatus);
		return ControllerConstants.Views.Fragments.SingleStepCheckout.PaymentDetailsFormPopup;
	}

	@RequestMapping(value = "/summary/createUpdatePaymentDetails.json", method = RequestMethod.POST)
	@RequireHardLogIn
	public String createUpdatePaymentDetails(final Model model, @Valid final PaymentDetailsForm form,
			final BindingResult bindingResult)
	{
		paymentDetailsValidator.validate(form, bindingResult);

		final boolean editMode = StringUtils.isNotBlank(form.getPaymentId());

		if (bindingResult.hasErrors())
		{
			model.addAttribute("edit", Boolean.valueOf(editMode));

			return ControllerConstants.Views.Fragments.SingleStepCheckout.PaymentDetailsFormPopup;
		}

		final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
		paymentInfoData.setId(form.getPaymentId());
		paymentInfoData.setCardType(form.getCardTypeCode());
		paymentInfoData.setAccountHolderName(form.getNameOnCard());
		paymentInfoData.setCardNumber(form.getCardNumber());
		paymentInfoData.setStartMonth(form.getStartMonth());
		paymentInfoData.setStartYear(form.getStartYear());
		paymentInfoData.setExpiryMonth(form.getExpiryMonth());
		paymentInfoData.setExpiryYear(form.getExpiryYear());
		paymentInfoData.setSaved(Boolean.TRUE.equals(form.getSaveInAccount()));
		paymentInfoData.setIssueNumber(form.getIssueNumber());

		final AddressData addressData;
		if (!editMode && Boolean.FALSE.equals(form.getNewBillingAddress()))
		{
			addressData = getCheckoutCart().getDeliveryAddress();
			if (addressData == null)
			{
				GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.createSubscription.billingAddress.noneSelected");

				model.addAttribute("edit", Boolean.valueOf(editMode));
				return ControllerConstants.Views.Fragments.SingleStepCheckout.PaymentDetailsFormPopup;
			}

			addressData.setBillingAddress(true); // mark this as billing address
		}
		else
		{
			final AddressForm addressForm = form.getBillingAddress();

			addressData = new AddressData();
			if (addressForm != null)
			{
				addressData.setId(addressForm.getAddressId());
				addressData.setTitleCode(addressForm.getTitleCode());
				addressData.setFirstName(addressForm.getFirstName());
				addressData.setLastName(addressForm.getLastName());
				addressData.setLine1(addressForm.getLine1());
				addressData.setLine2(addressForm.getLine2());
				addressData.setTown(addressForm.getTownCity());
				addressData.setPostalCode(addressForm.getPostcode());
				addressData.setCountry(getI18NFacade().getCountryForIsocode(addressForm.getCountryIso()));
				addressData.setShippingAddress(Boolean.TRUE.equals(addressForm.getShippingAddress()));
				addressData.setBillingAddress(Boolean.TRUE.equals(addressForm.getBillingAddress()));
			}
		}

		paymentInfoData.setBillingAddress(addressData);

		final CCPaymentInfoData newPaymentSubscription = getCheckoutFlowFacade().createPaymentSubscription(paymentInfoData);
		if (newPaymentSubscription != null && StringUtils.isNotBlank(newPaymentSubscription.getSubscriptionId()))
		{
			if (Boolean.TRUE.equals(form.getSaveInAccount()) && getUserFacade().getCCPaymentInfos(true).size() <= 1)
			{
				getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
			}
			getCheckoutFlowFacade().setPaymentDetails(newPaymentSubscription.getId());
		}
		else
		{
			GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.createSubscription.failed");

			model.addAttribute("edit", Boolean.valueOf(editMode));
			return ControllerConstants.Views.Fragments.SingleStepCheckout.PaymentDetailsFormPopup;
		}

		model.addAttribute("createUpdateStatus", "Success");
		model.addAttribute("paymentId", newPaymentSubscription.getId());

		return REDIRECT_PREFIX + "/checkout/single/summary/getPaymentDetailsForm.json?paymentId=" + paymentInfoData.getId()
				+ "&createUpdateStatus=Success";
	}

	@RequestMapping(value = "/termsAndConditions")
	@RequireHardLogIn
	public String getTermsAndConditions(final Model model) throws CMSItemNotFoundException
	{
		final ContentPageModel pageForRequest = getCmsPageService().getPageForLabel("/termsAndConditions");
		storeCmsPageInModel(model, pageForRequest);
		setUpMetaDataForContentPage(model, pageForRequest);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, contentPageBreadcrumbBuilder.getBreadcrumbs(pageForRequest));
		return ControllerConstants.Views.Fragments.Checkout.TermsAndConditionsPopup;
	}

	@RequestMapping(value = "/placeOrder")
	@RequireHardLogIn
	public String placeOrder(final Model model, @Valid final PlaceOrderForm placeOrderForm, final BindingResult bindingResult)
			throws CMSItemNotFoundException, InvalidCartException, ParseException
	{
		// validate the cart
		final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();
		final boolean isAccountPaymentType = CheckoutPaymentType.ACCOUNT.getCode().equals(cartData.getPaymentType().getCode());
		final String securityCode = placeOrderForm.getSecurityCode();
		final boolean termsChecked = placeOrderForm.isTermsCheck();

		if (!termsChecked)
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.terms.not.accepted");
		}

		if (validateOrderform(placeOrderForm, model, cartData))
		{
			placeOrderForm.setTermsCheck(false);
			model.addAttribute(placeOrderForm);
			return checkoutSummary(model);
		}

		if (!isAccountPaymentType && !getCheckoutFlowFacade().authorizePayment(securityCode))
		{
			return checkoutSummary(model);
		}

		// validate quote negotiation
		if (placeOrderForm.isNegotiateQuote())
		{
			if (StringUtils.isBlank(placeOrderForm.getQuoteRequestDescription()))
			{
				GlobalMessages.addErrorMessage(model, "checkout.error.noQuoteDescription");
				return checkoutSummary(model);
			}
			else
			{
				getCheckoutFlowFacade().setQuoteRequestDescription(XSSFilterUtil.filter(placeOrderForm.getQuoteRequestDescription()));
			}
		}

		if (!termsChecked)
		{
			return checkoutSummary(model);
		}

		// validate replenishment
		if (placeOrderForm.isReplenishmentOrder())
		{
			if (placeOrderForm.getReplenishmentStartDate() == null)
			{
				bindingResult.addError(new FieldError(placeOrderForm.getClass().getSimpleName(), "replenishmentStartDate", ""));
				GlobalMessages.addErrorMessage(model, "checkout.error.replenishment.noStartDate");
				return checkoutSummary(model);
			}
			if (B2BReplenishmentRecurrenceEnum.WEEKLY.equals(placeOrderForm.getReplenishmentRecurrence()))
			{
				if (CollectionUtils.isEmpty(placeOrderForm.getnDaysOfWeek()))
				{
					GlobalMessages.addErrorMessage(model, "checkout.error.replenishment.no.Frequency");
					return checkoutSummary(model);
				}
			}
			final TriggerData triggerData = new TriggerData();
			populateTriggerDataFromPlaceOrderForm(placeOrderForm, triggerData);
			final ScheduledCartData scheduledCartData = getCheckoutFlowFacade().scheduleOrder(triggerData);
			return REDIRECT_PREFIX + "/checkout/replenishmentConfirmation/" + scheduledCartData.getJobCode();
		}

		final OrderData orderData;
		try
		{
			orderData = getCheckoutFlowFacade().placeOrder();
		}
		catch (final Exception e)
		{
			GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
			placeOrderForm.setNegotiateQuote(true);
			model.addAttribute(placeOrderForm);
			return checkoutSummary(model);
		}

		if (placeOrderForm.isNegotiateQuote())
		{
			return REDIRECT_PREFIX + "/checkout/quoteOrderConfirmation/" + orderData.getCode();
		}
		else
		{
			return REDIRECT_PREFIX + "/checkout/orderConfirmation/" + orderData.getCode();

		}
	}

	protected boolean validateOrderform(final PlaceOrderForm placeOrderForm, final Model model, final CartData cartData)
	{
		final boolean accountPaymentType = CheckoutPaymentType.ACCOUNT.getCode().equals(cartData.getPaymentType().getCode());
		final String securityCode = placeOrderForm.getSecurityCode();
		boolean invalid = false;

		if (cartData.getDeliveryAddress() == null)
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryAddress.notSelected");
			invalid = true;
		}

		if (cartData.getDeliveryMode() == null)
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryMethod.notSelected");
			invalid = true;
		}

		if (!accountPaymentType && cartData.getPaymentInfo() == null)
		{
			GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.notSelected");
			invalid = true;
		}
		else if (!accountPaymentType && StringUtils.isBlank(securityCode))
		{
			GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
			invalid = true;
		}

		return invalid;
	}

	@RequestMapping(value = "/summary/reorder", method =
	{ RequestMethod.PUT, RequestMethod.POST })
	@RequireHardLogIn
	public String reorder(@RequestParam(value = "orderCode") final String orderCode, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, InvalidCartException, ParseException, CommerceCartModificationException
	{
		// create a cart from the order and set it as session cart.
		getCheckoutFlowFacade().createCartFromOrder(orderCode);
		// validate for stock and availability
		final List<? extends CommerceCartModification> cartModifications = getCheckoutFlowFacade().validateSessionCart();
		for (final CommerceCartModification cartModification : cartModifications)
		{
			if (CommerceCartModificationStatus.NO_STOCK.equals(cartModification.getStatusCode()))
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"basket.page.message.update.reducedNumberOfItemsAdded.noStock", new Object[]
						{ cartModification.getEntry().getProduct().getName() });
				break;
			}
			else if (cartModification.getQuantity() != cartModification.getQuantityAdded())
			{
				// item has been modified to match available stock levels
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"basket.information.quantity.adjusted");
				break;
			}
			// TODO: handle more specific messaging, i.e. out of stock, product not available
		}
		return REDIRECT_PREFIX + "/checkout/single/summary";//checkoutSummary(model);
	}

	/**
	 * Need to move out of controller utility method for Replenishment
	 * 
	 */
	protected List<String> getNumberRange(final int startNumber, final int endNumber)
	{
		final List<String> numbers = new ArrayList<String>();
		for (int number = startNumber; number <= endNumber; number++)
		{
			numbers.add(String.valueOf(number));
		}
		return numbers;
	}

	/**
	 * Util method to copy values from place order form to TriggerData for replenishment
	 * 
	 * @param placeOrderForm
	 * @param triggerData
	 * @throws ParseException
	 */
	protected void populateTriggerDataFromPlaceOrderForm(final PlaceOrderForm placeOrderForm, final TriggerData triggerData)
			throws ParseException
	{
		final Date replenishmentStartDate = placeOrderForm.getReplenishmentStartDate();
		final Calendar calendar = Calendar.getInstance(getI18nService().getCurrentTimeZone(), getI18nService().getCurrentLocale());
		triggerData.setActivationTime((replenishmentStartDate.before(calendar.getTime()) ? calendar.getTime()
				: replenishmentStartDate));

		final B2BReplenishmentRecurrenceEnum recurrenceValue = placeOrderForm.getReplenishmentRecurrence();

		if (B2BReplenishmentRecurrenceEnum.DAILY.equals(recurrenceValue))
		{
			triggerData.setDay(Integer.valueOf(placeOrderForm.getnDays()));
			triggerData.setRelative(Boolean.TRUE);
		}
		else if (B2BReplenishmentRecurrenceEnum.WEEKLY.equals(recurrenceValue))
		{
			triggerData.setDaysOfWeek(placeOrderForm.getnDaysOfWeek());
			triggerData.setWeekInterval(Integer.valueOf(placeOrderForm.getnWeeks()));
			triggerData.setHour(Integer.valueOf(0));
			triggerData.setMinute(Integer.valueOf(0));
		}
		else if (B2BReplenishmentRecurrenceEnum.MONTHLY.equals(recurrenceValue))
		{
			triggerData.setDay(Integer.valueOf(placeOrderForm.getNthDayOfMonth()));
			triggerData.setRelative(Boolean.FALSE);
		}
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
}
