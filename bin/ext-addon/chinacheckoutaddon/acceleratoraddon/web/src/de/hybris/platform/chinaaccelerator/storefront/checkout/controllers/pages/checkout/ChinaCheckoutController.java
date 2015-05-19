/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.chinaaccelerator.storefront.checkout.controllers.pages.checkout;


import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PlaceOrderForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.AddressValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.verification.AddressVerificationResultHandler;
import de.hybris.platform.chinaaccelerator.facades.checkout.ChinaCheckoutFacade;
import de.hybris.platform.chinaaccelerator.facades.data.CityData;
import de.hybris.platform.chinaaccelerator.facades.data.DistrictData;
import de.hybris.platform.chinaaccelerator.facades.data.PaymentModeData;
import de.hybris.platform.chinaaccelerator.facades.location.CityFacade;
import de.hybris.platform.chinaaccelerator.facades.location.DistrictFacade;
import de.hybris.platform.chinaaccelerator.services.enums.DeliveryTimeslot;
import de.hybris.platform.chinaaccelerator.services.enums.InvoiceCategory;
import de.hybris.platform.chinaaccelerator.services.model.invoice.InvoiceModel;
import de.hybris.platform.chinaaccelerator.storefront.checkout.controllers.ControllerConstants;
import de.hybris.platform.chinaaccelerator.storefront.checkout.forms.ChinaAddressForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.InvoiceData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.ZoneDeliveryModeService;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;
//import de.hybris.platform.chinaacceleratorservices.core.enums.DeliveryTimeslot;
//import de.hybris.platform.chinaacceleratorservices.core.enums.InvoiceCategory;





/**
 * ChinaCheckoutController
 */
@Controller
@RequestMapping(value = "/checkout/multi")
public class ChinaCheckoutController extends MultiStepCheckoutController
{
	private static final Logger LOG = Logger.getLogger(ChinaCheckoutController.class);

	private static final String MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL = "multiStepCheckoutSummary";

	private static final String REDIRECT_URL_CHINA_STEP_2 = REDIRECT_PREFIX + "/checkout/multi/step2";
	private static final String FORWARD_URL_CHINA_STEP_timeout = FORWARD_PREFIX + "/checkout/multi/timeout";
	private static final String REDIRECT_URL_CHINA_STEP_timeout = REDIRECT_PREFIX + "/login/checkout";
	private static final String REDIRECT_URL_CHOOSE_DELIVERY_METHOD = REDIRECT_PREFIX + "/checkout/multi/choose-delivery-method";
	private static final String REDIRECT_URL_CART = REDIRECT_PREFIX + "/cart";
	private static final String ADDRESS_CODE_PATH_VARIABLE_PATTERN = "{addressCode:.*}";
	private static final String ADD_EDIT_ADDRESS_CMS_PAGE = "add-edit-address";


	@Resource(name = "multiStepCheckoutBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@Resource(name = "addressValidator")
	private AddressValidator addressValidator;

	@Resource(name = "addressVerificationResultHandler")
	private AddressVerificationResultHandler addressVerificationResultHandler;

	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade checkoutFacade;

	@Resource(name = "chinaCheckoutFacade")
	private ChinaCheckoutFacade chinaCheckoutFacade;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userFacade")
	protected UserFacade userFacade;

	@Resource(name = "customerFacade")
	protected CustomerFacade customerFacade;

	@Resource(name = "cityFacade")
	private CityFacade cityFacade;

	@Resource(name = "districtFacade")
	private DistrictFacade districtFacade;

	@Resource(name = "zoneDeliveryModeService")
	private ZoneDeliveryModeService zoneDeliveryModeService;

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CityFacade getCityFacade()
	{
		return this.cityFacade;
	}

	protected DistrictFacade getDistrictFacade()
	{
		return this.districtFacade;
	}

	protected ZoneDeliveryModeService getZoneDeliveryModeService()
	{
		return zoneDeliveryModeService;
	}


	@Override
	@ModelAttribute("checkoutSteps")
	public List<CheckoutSteps> addCheckoutStepsToModel(final HttpServletRequest request)
	{
		final List<CheckoutSteps> checkoutSteps = new ArrayList<CheckoutSteps>();
		checkoutSteps.add(new CheckoutSteps("step2", "/checkout/multi/step2", Integer.valueOf(1)));
		checkoutSteps.add(new CheckoutSteps("deliveryAddress", "/checkout/multi/add-delivery-address", Integer.valueOf(2)));
		checkoutSteps.add(new CheckoutSteps("deliveryMethod", "/checkout/multi/choose-delivery-method", Integer.valueOf(3)));
		checkoutSteps.add(new CheckoutSteps("paymentMethod", "/checkout/multi/add-payment-method", Integer.valueOf(4)));
		checkoutSteps.add(new CheckoutSteps("confirmOrder", "/checkout/multi/summary", Integer.valueOf(5)));

		return checkoutSteps;
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String gotoFirstStep()
	{
		if (getCheckoutFlowFacade().hasValidCart())
		{
			//return (getCheckoutFacade().hasShippingItems()) ? REDIRECT_URL_CHINA_STEP_2 : REDIRECT_URL_CHOOSE_DELIVERY_LOCATION; // BOPiS
			return REDIRECT_URL_CHINA_STEP_2;
		}
		LOG.info("Missing, empty or unsupported cart");
		return REDIRECT_URL_CART;
	}

	@RequestMapping(value = "/step2", method = RequestMethod.GET)
	@RequireHardLogIn
	public String step2(final Model model) throws CMSItemNotFoundException, JSONException
	{
		if (!getCheckoutFlowFacade().hasValidCart())
		{
			LOG.info("Missing, empty or unsupported cart");
			return REDIRECT_URL_CART;
		}

		if (!getCheckoutFacade().setDeliveryModeIfAvailable())
		{
			LOG.info("No supported deliveryMode is availabe");
			return REDIRECT_URL_CART;
		}

		// BOPiS
		//		if (!getCheckoutFacade().hasShippingItems())
		//		{
		//			return REDIRECT_URL_CHOOSE_DELIVERY_LOCATION;
		//		}

		getCheckoutFacade().setDeliveryAddressIfAvailable();

		model.addAttribute("addressDataFromAddressBook", userFacade.getAddressBook()); // CNACC


		//		final DeliveryModeData deliveryModeData = this.getCheckoutFacade().getCheckoutCart().getDeliveryMode();

		//		if (deliveryModeData != null)
		//		{
		//			final String deliveryModeCode = deliveryModeData.getCode() != null ? deliveryModeData.getCode() : "empty code";
		//			final String deliveryModeName = deliveryModeData.getName() != null ? deliveryModeData.getName() : "empty name";
		//			LOG.debug("DeliveryMode CODE=" + deliveryModeCode + ", deliveryMode NAME=" + deliveryModeName);
		//		}

		boolean isFirst = true;
		final List<PaymentModeData> paymentModes = new ArrayList<PaymentModeData>();
		for (final DeliveryModeData data : this.getCheckoutFacade().getSupportedDeliveryModes())
		{
			final DeliveryModeModel deliveryModeModel = this.getZoneDeliveryModeService().getDeliveryModeForCode(data.getCode());

			for (final PaymentModeModel paymentModeModel : deliveryModeModel.getSupportedPaymentModes())
			{
				if (isFirst)
				{
					this.chinaCheckoutFacade.setPaymentMode(paymentModeModel);
					this.checkoutFacade.setPaymentDetails(paymentModeModel.getCode());
					isFirst = false;
				}
				final PaymentModeData dto = new PaymentModeData();
				dto.setCode(paymentModeModel.getCode());
				dto.setName(paymentModeModel.getName());
				paymentModes.add(dto);
			}
			break;
		}
		model.addAttribute("paymentModes", paymentModes);

		final CartModel cartModel = chinaCheckoutFacade.getCart();
		final CartData cartData = chinaCheckoutFacade.convertCart(cartModel);

		final JSONObject jsonobject = new JSONObject();
		if (cartData.getInvoice() != null)
		{
			jsonobject.put("requireInvoice", "1");
			final InvoiceData invoiceData = cartData.getInvoice();
			jsonobject.put("invoicedCategory", invoiceData.getInvoicedCategory());
			jsonobject.put("invoicedTitle", invoiceData.getInvoicedTitle());
			jsonobject.put("invoiceName", invoiceData.getInvoicedName());
		}
		else
		{
			jsonobject.put("requireInvoice", "0");
		}
		model.addAttribute("cartData", cartData);
		model.addAttribute("invoiceInfo", jsonobject.toString());
		model.addAttribute("deliveryAddresses", getDeliveryAddresses(cartData.getDeliveryAddress()));
		model.addAttribute("noAddress", Boolean.valueOf(getCheckoutFlowFacade().hasNoDeliveryAddress()));
		model.addAttribute("addressForm", new ChinaAddressForm());
		model.addAttribute("showSaveToAddressBook", Boolean.TRUE);
		this.prepareDataForPage(model);


		//Setup Delivery Time Slot
		model.addAttribute("deliveryTimeSlots", DeliveryTimeslot.values());
		this.chinaCheckoutFacade.setDeliveryTimeslot(DeliveryTimeslot.values()[0]);

		//Initialize place order form
		model.addAttribute(new PlaceOrderForm());


		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryAddress.breadcrumb"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return ControllerConstants.Views.Pages.MultiStepCheckout.Step2Page;
	}

	protected boolean isDefaultAddress(final String addressId)
	{
		final AddressData defaultAddress = userFacade.getDefaultAddress();
		return (defaultAddress != null && defaultAddress.getId() != null && defaultAddress.getId().equals(addressId));
	}


	@RequestMapping(value = "/set-default-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String setDefaultAddress(@PathVariable("addressCode") final String addressCode, final RedirectAttributes redirectModel)
	{
		final AddressData addressData = new AddressData();
		addressData.setDefaultAddress(true);
		addressData.setVisibleInAddressBook(true);
		addressData.setId(addressCode);
		userFacade.setDefaultAddress(addressData);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
				"account.confirmation.default.address.changed");
		return REDIRECT_URL_CHINA_STEP_2;
	}

	protected ChinaAddressForm getPreparedAddressForm()
	{
		final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
		final ChinaAddressForm addressForm = new ChinaAddressForm();
		addressForm.setFirstName(currentCustomerData.getFirstName());
		addressForm.setLastName(currentCustomerData.getLastName());
		addressForm.setTitleCode(currentCustomerData.getTitleCode());
		return addressForm;
	}

	@RequestMapping(value = "/editAddress", method = RequestMethod.GET)
	public String editAddress(final Model model,
			@RequestParam(value = "provinceIsoCode", required = false) final String provinceIsoCode,
			@RequestParam(value = "cityCode", required = false) final String cityCode,
			@RequestParam(value = "addressPersonName", required = false) final String personName,
			@RequestParam(value = "addressLine1", required = false) final String street,
			@RequestParam(value = "addressPostalCode", required = false) final String postalCode,
			@RequestParam(value = "addressLandlinePhonePart1", required = false) final String addressLandlinePhonePart1,
			@RequestParam(value = "addressLandlinePhonePart2", required = false) final String addressLandlinePhonePart2,
			@RequestParam(value = "addressLandlinePhonePart3", required = false) final String addressLandlinePhonePart3,
			@RequestParam(value = "addressCellPhone", required = false) final String addressCellPhone,
			@RequestParam(value = "addressId", required = false) final String addressId,
			@RequestParam(value = "cmd", required = false) final String cmd) throws CMSItemNotFoundException
	{

		final JaloSession session = JaloSession.getCurrentSession();
		if (session == null)
		{
			return FORWARD_URL_CHINA_STEP_timeout;
		}
		final String xx = session.getUser().getUid();
		if ("anonymous".equals(xx))
		{
			return FORWARD_URL_CHINA_STEP_timeout;
		}

		model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso("CN"));

		// get cities if provinceIsoCode is set 
		if (provinceIsoCode != null)
		{
			final List<CityData> cityDTOs = getCityFacade().getCitiesByRegionCode(provinceIsoCode);
			model.addAttribute("cities", cityDTOs);
		}
		if (cityCode != null)
		{
			final List<DistrictData> cityDistricts = this.getDistrictFacade().getDistrictsByCityCode(cityCode);
			model.addAttribute("cityDistricts", cityDistricts);
		}

		//
		final ChinaAddressForm addressForm;
		if (cmd != null && cmd.equals("initialaddressform"))
		{
			addressForm = getPreparedAddressForm();
		}
		else
		{
			addressForm = new ChinaAddressForm();
		}


		addressForm.setCountryIso("CN");

		// set incoming data if any
		if (addressId != null)
		{
			addressForm.setAddressId(addressId);
		}

		if (personName != null)
		{
			addressForm.setFirstName(personName);
		}
		if (street != null)
		{
			addressForm.setLine1(street);
		}
		if (postalCode != null)
		{
			addressForm.setPostcode(postalCode);
		}


		// set incoming values if any for having selectors preselected 
		if (provinceIsoCode != null)
		{
			addressForm.setRegionIso(provinceIsoCode);
		}
		if (cityCode != null)
		{
			addressForm.setCityCode(cityCode);
		}

		// 
		if (addressLandlinePhonePart1 != null && !StringUtils.isEmpty(addressLandlinePhonePart1))
		{
			addressForm.setLandlinePhonePart1(addressLandlinePhonePart1);
		}
		if (addressLandlinePhonePart2 != null && !StringUtils.isEmpty(addressLandlinePhonePart2))
		{
			addressForm.setLandlinePhonePart2(addressLandlinePhonePart2);
		}
		if (addressLandlinePhonePart3 != null && !StringUtils.isEmpty(addressLandlinePhonePart3))
		{
			addressForm.setLandlinePhonePart3(addressLandlinePhonePart3);
		}
		if (addressCellPhone != null && !StringUtils.isEmpty(addressCellPhone))
		{
			addressForm.setCellPhone(addressCellPhone);
		}
		if (addressId != null && !StringUtils.isEmpty(addressId))
		{
			addressForm.setAddressId(addressId);
		}

		//


		model.addAttribute("addressForm", addressForm);
		model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));
		model.addAttribute("isDefaultAddress", Boolean.FALSE);
		storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));

		model.addAttribute("metaRobots", "no-index,no-follow");

		// depending on the cmd value, a different addressform JSP fragment is returned
		// as this method is used for AddingNew and ModifyingExisting addresses
		final String fragmentName;
		if (cmd != null && cmd.equals("MODIFYADDRESS"))
		{
			fragmentName = "fragments/address/modifyChinaAddressForm";
		}
		else
		{
			fragmentName = "fragments/address/editChinaAddressForm";
		}

		return fragmentName;
	}

	/*
	 * @RequestMapping(value = "/edit-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	 * 
	 * @RequireHardLogIn public String editAddress(@PathVariable("addressCode") final String addressCode, final Model
	 * model) throws CMSItemNotFoundException {
	 */
	@RequestMapping(value = "/edit-address", method = RequestMethod.GET)
	public String editAddress(@RequestParam(value = "addressCode", required = true) final String addressCode,
			@RequestParam(value = "cmd", required = false) final String cmd, final Model model) throws CMSItemNotFoundException
	{

		final JaloSession session = JaloSession.getCurrentSession();
		if (session == null)
		{
			return FORWARD_URL_CHINA_STEP_timeout;
		}
		final String xx = session.getUser().getUid();
		if ("anonymous".equals(xx))
		{
			return FORWARD_URL_CHINA_STEP_timeout;
		}
		final ChinaAddressForm addressForm = new ChinaAddressForm();
		model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
		model.addAttribute("titleData", userFacade.getTitles());
		model.addAttribute("addressForm", addressForm);
		model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));

		for (final AddressData addressData : userFacade.getAddressBook())
		{
			if (addressData.getId() != null && addressData.getId().equals(addressCode))
			{
				model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressData.getCountry().getIsocode()));
				model.addAttribute("country", addressData.getCountry().getIsocode());
				model.addAttribute("addressData", addressData);
				addressForm.setAddressId(addressData.getId());
				addressForm.setTitleCode(addressData.getTitleCode());
				addressForm.setFirstName(addressData.getFirstName());
				addressForm.setLastName(addressData.getLastName());
				addressForm.setLine1(addressData.getLine1());
				addressForm.setLine2(addressData.getLine2());
				addressForm.setTownCity(addressData.getTown());
				addressForm.setPostcode(addressData.getPostalCode());
				addressForm.setCountryIso(addressData.getCountry().getIsocode());
				if (addressData.getRegion() != null && !StringUtils.isEmpty(addressData.getRegion().getIsocode()))
				{
					addressForm.setRegionIso(addressData.getRegion().getIsocode());
				}

				// if region is set, provide city list
				if (addressData.getRegion() != null && !StringUtils.isEmpty(addressData.getRegion().getIsocode()))
				{
					final List<CityData> cityDTOs = getCityFacade().getCitiesByRegionCode(addressData.getRegion().getIsocode());
					model.addAttribute("cities", cityDTOs);
				}

				// if city is set, provide district list
				if (addressData.getCity() != null && !StringUtils.isEmpty(addressData.getCity()))
				{
					final String cityCode = retrieveCityCode(addressData.getId());

					final List<DistrictData> cityDistricts = this.getDistrictFacade().getDistrictsByCityCode(cityCode);
					model.addAttribute("cityDistricts", cityDistricts);
					addressForm.setCityCode(cityCode);
				}

				// set districtcode if available
				if (addressData.getCityDistrict() != null && !StringUtils.isEmpty(addressData.getCityDistrict()))
				{
					final String cityDistrictCode = retrieveCityDistrictCode(addressData.getId());
					addressForm.setCityDistrictCode(cityDistrictCode);
				}

				// phone // complete phonenumber
				if (addressData.getPhone() != null && !StringUtils.isEmpty(addressData.getPhone()))
				{
					addressForm.setPhone(addressData.getPhone());
				}
				// store phone
				if (addressData.getLandlinePhonePart1() != null && !StringUtils.isEmpty(addressData.getLandlinePhonePart1()))
				{
					addressForm.setLandlinePhonePart1(addressData.getLandlinePhonePart1());
				}
				if (addressData.getLandlinePhonePart2() != null && !StringUtils.isEmpty(addressData.getLandlinePhonePart2()))
				{
					addressForm.setLandlinePhonePart2(addressData.getLandlinePhonePart2());
				}
				if (addressData.getLandlinePhonePart3() != null && !StringUtils.isEmpty(addressData.getLandlinePhonePart3()))
				{
					addressForm.setLandlinePhonePart3(addressData.getLandlinePhonePart3());
				}

				if (addressData.getCellphone() != null && !StringUtils.isEmpty(addressData.getCellphone()))
				{
					addressForm.setCellPhone(addressData.getCellphone());
				}


				if (isDefaultAddress(addressData.getId()))
				{
					addressForm.setDefaultAddress(Boolean.TRUE);
					model.addAttribute("isDefaultAddress", Boolean.TRUE);
				}
				else
				{
					addressForm.setDefaultAddress(Boolean.FALSE);
					model.addAttribute("isDefaultAddress", Boolean.FALSE);
				}
				break;
			}
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE)); // TODO: ?
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));

		//		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		//		breadcrumbs.add(new Breadcrumb("/my-account/address-book", getMessageSource().getMessage("text.account.addressBook", null,
		//				getI18nService().getCurrentLocale()), null));
		//		breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.addressBook.addEditAddress", null,
		//				getI18nService().getCurrentLocale()), null));
		//		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("metaRobots", "no-index,no-follow");
		model.addAttribute("edit", Boolean.TRUE);
		return "fragments/address/modifyChinaAddressForm";
	}

	@RequestMapping(value = "/edit-address", method = RequestMethod.POST)
	public String editAddress(final ChinaAddressForm addressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{

		/**
		 * getAddressValidator().validate(addressForm, bindingResult); if (bindingResult.hasErrors()) {
		 * GlobalMessages.addErrorMessage(model, "form.global.error"); storeCmsPageInModel(model,
		 * getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE)); setUpMetaDataForContentPage(model,
		 * getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE)); setUpAddressFormAfterError(addressForm, model); //
		 * TODO: needed here? //return ControllerConstants.Views.Pages.Account.AccountEditAddressPage; return
		 * REDIRECT_URL_CHINA_STEP_2; // ? }
		 */
		final JaloSession session = JaloSession.getCurrentSession();
		if (session == null)
		{
			return REDIRECT_URL_CHINA_STEP_timeout;
		}
		final String xx = session.getUser().getUid();
		if ("anonymous".equals(xx))
		{
			return REDIRECT_URL_CHINA_STEP_timeout;
		}


		model.addAttribute("metaRobots", "no-index,no-follow");

		final AddressData newAddress = new AddressData();
		newAddress.setId(addressForm.getAddressId());
		newAddress.setTitleCode(addressForm.getTitleCode());
		newAddress.setFirstName(addressForm.getFirstName());
		newAddress.setLine1(addressForm.getLine1());
		newAddress.setPostalCode(addressForm.getPostcode());
		newAddress.setShippingAddress(true);
		newAddress.setVisibleInAddressBook(true);

		addressForm.setCountryIso("CN");

		newAddress.setCountry(getI18NFacade().getCountryForIsocode(addressForm.getCountryIso()));

		if (addressForm.getRegionIso() != null && !StringUtils.isEmpty(addressForm.getRegionIso()))
		{
			newAddress.setRegion(getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso()));
		}

		// store city and district if any
		if (addressForm.getCityCode() != null && !StringUtils.isEmpty(addressForm.getCityCode()))
		{
			final CityData cityData = this.getCityFacade().getCityForCode(addressForm.getCityCode());
			newAddress.setCityData(cityData);
		}
		if (addressForm.getCityDistrictCode() != null && !StringUtils.isEmpty(addressForm.getCityDistrictCode()))
		{
			final DistrictData districtData = this.getDistrictFacade().getDistrictByCode(addressForm.getCityDistrictCode());
			newAddress.setCityDistrictData(districtData);
		}

		// store phone
		if (addressForm.getLandlinePhonePart1() != null && !StringUtils.isEmpty(addressForm.getLandlinePhonePart1()))
		{
			newAddress.setLandlinePhonePart1(addressForm.getLandlinePhonePart1());
		}
		if (addressForm.getLandlinePhonePart2() != null && !StringUtils.isEmpty(addressForm.getLandlinePhonePart2()))
		{
			newAddress.setLandlinePhonePart2(addressForm.getLandlinePhonePart2());
		}
		if (addressForm.getLandlinePhonePart3() != null && !StringUtils.isEmpty(addressForm.getLandlinePhonePart3()))
		{
			newAddress.setLandlinePhonePart3(addressForm.getLandlinePhonePart3());
		}

		if (addressForm.getCellPhone() != null && !StringUtils.isEmpty(addressForm.getCellPhone()))
		{
			newAddress.setCellphone(addressForm.getCellPhone());
		}


		if (Boolean.TRUE.equals(addressForm.getDefaultAddress()) || userFacade.getAddressBook().size() <= 1)
		{
			newAddress.setDefaultAddress(true);
			newAddress.setVisibleInAddressBook(true);
		}

		/*
		 * needed here? final AddressVerificationResult<AddressVerificationDecision> verificationResult =
		 * getAddressVerificationFacade() .verifyAddressData(newAddress); final boolean addressRequiresReview =
		 * getAddressVerificationResultHandler().handleResult(verificationResult, newAddress, model, redirectModel,
		 * bindingResult, getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
		 * "checkout.multi.address.updated");
		 * 
		 * if (addressRequiresReview) { model.addAttribute("regions",
		 * getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso())); model.addAttribute("country",
		 * addressForm.getCountryIso()); model.addAttribute("edit", Boolean.TRUE); storeCmsPageInModel(model,
		 * getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE)); setUpMetaDataForContentPage(model,
		 * getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE)); return
		 * ControllerConstants.Views.Pages.Account.AccountEditAddressPage; }
		 */
		userFacade.editAddress(newAddress);
		return REDIRECT_URL_CHINA_STEP_2;
	}

	@RequestMapping(value = "/add-address", method = RequestMethod.POST)
	public String addAddress(final ChinaAddressForm addressForm, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{


		/*
		 * getAddressValidator().validate(addressForm, bindingResult); if (bindingResult.hasErrors()) {
		 * GlobalMessages.addErrorMessage(model, "form.global.error"); storeCmsPageInModel(model,
		 * getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE)); setUpMetaDataForContentPage(model,
		 * getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE)); setUpAddressFormAfterError(addressForm, model); return
		 * ControllerConstants.Views.Pages.Account.AccountEditAddressPage; }
		 */

		final JaloSession session = JaloSession.getCurrentSession();
		if (session == null)
		{
			return FORWARD_URL_CHINA_STEP_timeout;
		}
		final String xx = session.getUser().getUid();
		if ("anonymous".equals(xx))
		{
			return FORWARD_URL_CHINA_STEP_timeout;
		}

		final AddressData newAddress = new AddressData();
		newAddress.setFirstName(addressForm.getFirstName());
		newAddress.setLine1(addressForm.getLine1());
		newAddress.setPostalCode(addressForm.getPostcode());
		newAddress.setShippingAddress(true); // what is it doing?
		newAddress.setVisibleInAddressBook(true);

		addressForm.setCountryIso("CN");

		newAddress.setCountry(getI18NFacade().getCountryForIsocode(addressForm.getCountryIso()));

		if (addressForm.getRegionIso() != null && !StringUtils.isEmpty(addressForm.getRegionIso()))
		{
			newAddress.setRegion(getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso()));
		}

		// store city and district if any
		if (addressForm.getCityCode() != null && !StringUtils.isEmpty(addressForm.getCityCode()))
		{
			final CityData cityData = this.getCityFacade().getCityForCode(addressForm.getCityCode());
			newAddress.setCityData(cityData);
		}
		if (addressForm.getCityDistrictCode() != null && !StringUtils.isEmpty(addressForm.getCityDistrictCode()))
		{
			final DistrictData districtData = this.getDistrictFacade().getDistrictByCode(addressForm.getCityDistrictCode());
			newAddress.setCityDistrictData(districtData);
		}
		//

		// store phone
		if (addressForm.getLandlinePhonePart1() != null && !StringUtils.isEmpty(addressForm.getLandlinePhonePart1()))
		{
			newAddress.setLandlinePhonePart1(addressForm.getLandlinePhonePart1());
		}
		if (addressForm.getLandlinePhonePart2() != null && !StringUtils.isEmpty(addressForm.getLandlinePhonePart2()))
		{
			newAddress.setLandlinePhonePart2(addressForm.getLandlinePhonePart2());
		}
		if (addressForm.getLandlinePhonePart3() != null && !StringUtils.isEmpty(addressForm.getLandlinePhonePart3()))
		{
			newAddress.setLandlinePhonePart3(addressForm.getLandlinePhonePart3());
		}

		if (addressForm.getCellPhone() != null && !StringUtils.isEmpty(addressForm.getCellPhone()))
		{
			newAddress.setCellphone(addressForm.getCellPhone());
		}


		if (userFacade.isAddressBookEmpty())
		{
			newAddress.setDefaultAddress(true);
			newAddress.setVisibleInAddressBook(true);
		}
		else
		{
			newAddress.setDefaultAddress(addressForm.getDefaultAddress() != null && addressForm.getDefaultAddress().booleanValue());
		}


		// CNACC needed?
		/*
		 * final AddressVerificationResult<AddressVerificationDecision> verificationResult =
		 * getAddressVerificationFacade() .verifyAddressData(newAddress); final boolean addressRequiresReview =
		 * getAddressVerificationResultHandler().handleResult(verificationResult, newAddress, model, redirectModel,
		 * bindingResult, getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
		 * "checkout.multi.address.added");
		 * 
		 * if (addressRequiresReview) { model.addAttribute("regions",
		 * getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso())); model.addAttribute("country",
		 * addressForm.getCountryIso()); storeCmsPageInModel(model,
		 * getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE)); setUpMetaDataForContentPage(model,
		 * getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE)); //return
		 * ControllerConstants.Views.Pages.Account.AccountEditAddressPage; return REDIRECT_URL_CHINA_STEP_2; }
		 */
		userFacade.addAddress(newAddress);
		return REDIRECT_URL_CHINA_STEP_2;
	}

	@RequestMapping(value = "/timeout", method = RequestMethod.POST)
	public @ResponseBody
	String checkoutlogin2(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws CMSItemNotFoundException, JSONException
	{
		return "timeout_1";
	}

	@RequestMapping(value = "/timeout", method = RequestMethod.GET)
	public @ResponseBody
	String checkoutlogin1(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws CMSItemNotFoundException, JSONException
	{
		return "timeout_1";
	}

	@RequestMapping(value = "/saveInvoice", method = RequestMethod.GET)
	public @ResponseBody
	String saveInvoiceINfo(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws CMSItemNotFoundException, JSONException
	{
		final String invoiceTitle = request.getParameter("_invoiceTitle");
		final String invoicMsg = request.getParameter("invoicMsg");
		final String invoiceCategory = request.getParameter("_invoiceCategory");


		final CartModel cartModel = chinaCheckoutFacade.getCart();
		final CartData cartData = chinaCheckoutFacade.convertCart(cartModel);
		InvoiceData invoiceData = cartData.getInvoice() == null ? new InvoiceData() : cartData.getInvoice();
		final InvoiceModel invoiceModel = cartModel.getInvoice() == null ? new InvoiceModel() : cartModel.getInvoice();
		if (invoiceCategory != null && invoiceCategory.trim().length() > 0)
		{
			invoiceModel.setCategory(InvoiceCategory.valueOf(invoiceCategory));
		}
		if (invoiceTitle != null && invoiceTitle.trim().length() > 0)
		{
			invoiceModel.setTitle(de.hybris.platform.chinaaccelerator.services.enums.InvoiceTitle.valueOf(invoiceTitle));
		}
		invoiceModel.setInvoicedName(invoicMsg);

		chinaCheckoutFacade.saveInvoice(invoiceModel);
		cartModel.setInvoice(invoiceModel);

		chinaCheckoutFacade.mergeCart(cartModel);
		invoiceData = chinaCheckoutFacade.getInvoiceData(invoiceModel);
		cartData.setInvoice(invoiceData);

		final JSONObject jsonobject = new JSONObject();
		if (invoiceData != null)
		{
			jsonobject.put("requireInvoice", "1");
			jsonobject.put("invoicedCategory", invoiceData.getInvoicedCategory());
			jsonobject.put("invoicedTitle", invoiceData.getInvoicedTitle());
			jsonobject.put("invoiceName", invoiceData.getInvoicedName());
		}
		else
		{
			jsonobject.put("requireInvoice", "0");
		}
		return jsonobject.toString();
	}

	@RequestMapping(value = "/cancelInvoice", method = RequestMethod.GET)
	public @ResponseBody
	String cancelInvoice(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws CMSItemNotFoundException
	{

		final CartModel cartModel = chinaCheckoutFacade.getCart();
		final CartData cartData = chinaCheckoutFacade.convertCart(cartModel);
		final InvoiceModel invoiceModel = cartModel.getInvoice();
		if (invoiceModel != null)
		{
			chinaCheckoutFacade.removeInvoice(invoiceModel);
			cartModel.setInvoice(null);
			chinaCheckoutFacade.mergeCart(cartModel);

		}
		cartData.setInvoice(null);
		return "";

	}

	@RequestMapping(value = "/getCurrentInvoice", method = RequestMethod.GET)
	public @ResponseBody
	String getCurrentInvoice(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws CMSItemNotFoundException, JSONException
	{
		final JSONObject jsonobject = new JSONObject();
		final CartModel cartModel = chinaCheckoutFacade.getCart();
		final CartData cartData = chinaCheckoutFacade.convertCart(cartModel);
		if (cartData.getInvoice() != null)
		{
			jsonobject.put("requireInvoice", "1");
			final InvoiceData invoiceData = cartData.getInvoice();
			jsonobject.put("invoicedCategory", invoiceData.getInvoicedCategory());
			jsonobject.put("invoicedTitle", invoiceData.getInvoicedTitle());
			jsonobject.put("invoiceName", invoiceData.getInvoicedName());
		}
		else
		{
			jsonobject.put("requireInvoice", "0");
		}
		return jsonobject.toString();
	}





	@RequestMapping(value = "/select-delivery-address-ajax", method = RequestMethod.GET)
	@RequireHardLogIn
	public @ResponseBody
	String doAjaxSelectDeliveryAddress(@RequestParam(value = "addressCode", required = true) final String selectedAddressCode)
	{
		final String result = doSelectDeliveryAddress(selectedAddressCode);
		if (REDIRECT_URL_CHOOSE_DELIVERY_METHOD.endsWith(result))
		{
			return "OK";
		}
		else
		{
			return "FALSE";
		}
	}

	@RequestMapping(value = "/select-payment-mode-ajax", method = RequestMethod.GET)
	@RequireHardLogIn
	public @ResponseBody
	String doAjaxSelectPaymentMode(@RequestParam(value = "paymentMode", required = true) final String selectedPaymentCode)
	{
		final PaymentModeModel paymentMode = getPaymentModeModel(selectedPaymentCode);
		this.chinaCheckoutFacade.setPaymentMode(paymentMode);
		this.checkoutFacade.setPaymentDetails(paymentMode.getCode());
		return "OK";
	}


	@RequestMapping(value = "/select-delivery-timeslot-ajax", method = RequestMethod.GET)
	@RequireHardLogIn
	public @ResponseBody
	String doAjaxSelectDeliveryTimeSlot(@RequestParam(value = "timeslot", required = true) final String selectedTimeSlotCode)
	{
		final DeliveryTimeslot deliveryTimeslot = DeliveryTimeslot.valueOf(selectedTimeSlotCode);
		this.chinaCheckoutFacade.setDeliveryTimeslot(deliveryTimeslot);
		return "OK";
	}

	@RequestMapping(value = "/placeOrderOneStep")
	@RequireHardLogIn
	public String placeOrderOneStep(@ModelAttribute("placeOrderForm") final PlaceOrderForm placeOrderForm, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException,
			InvalidCartException, CommerceCartModificationException
	{
		if (validateChinaOrderForm(placeOrderForm, redirectModel))
		{
			return REDIRECT_URL_CHINA_STEP_2;
		}

		//Validate the cart
		if (validateCart(redirectModel))
		{
			// Invalid cart. Bounce back to the cart page.
			return REDIRECT_URL_CHINA_STEP_2;
		}

		// authorize, if failure occurs don't allow to place the order
		boolean isPaymentUthorized = false;
		try
		{
			isPaymentUthorized = getCheckoutFacade().authorizePayment(placeOrderForm.getSecurityCode());
		}
		catch (final AdapterException ae)
		{
			// handle a case where a wrong paymentProvider configurations on the store see getCommerceCheckoutService().getPaymentProvider()
			LOG.error(ae.getMessage(), ae);
		}
		if (!isPaymentUthorized)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"checkout.error.authorization.failed");
			return REDIRECT_URL_CHINA_STEP_2;
		}

		final OrderData orderData;
		try
		{
			orderData = getCheckoutFacade().placeOrder();
		}
		catch (final Exception e)
		{
			LOG.error("Failed to place Order", e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "checkout.placeOrder.failed");
			return REDIRECT_URL_CHINA_STEP_2;
		}

		return redirectToOrderConfirmationPage(orderData);
	}

	@RequestMapping(value = "/cartdata", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getCartdata(final Model model, final ChinaAddressForm addressForm, final RedirectAttributes redirectModel)
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);
		return "fragments/cart/chinaCartSettleForm";
	}

	/**
	 * @param selectedPaymentCode
	 * @return PaymentModeModel
	 */
	private PaymentModeModel getPaymentModeModel(final String selectedPaymentCode)
	{
		if (!StringUtils.isEmpty(selectedPaymentCode))
		{
			for (final DeliveryModeData deliveryModeData : this.getCheckoutFacade().getSupportedDeliveryModes())
			{
				final DeliveryModeModel deliveryModeModel = this.getZoneDeliveryModeService().getDeliveryModeForCode(
						deliveryModeData.getCode());

				for (final PaymentModeModel paymentModeModel : deliveryModeModel.getSupportedPaymentModes())
				{
					if (paymentModeModel.getCode().equals(selectedPaymentCode))
					{
						return paymentModeModel;
					}
				}

			}
		}

		return null;
	}

	/**
	 * @param placeOrderForm
	 * @param model
	 * @return
	 */
	private boolean validateChinaOrderForm(final PlaceOrderForm placeOrderForm, final RedirectAttributes rediectmodel)
	{
		boolean invalid = false;

		if (getCheckoutFlowFacade().hasNoDeliveryAddress())
		{
			GlobalMessages.addFlashMessage(rediectmodel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"checkout.deliveryAddress.notSelected");
			invalid = true;
		}

		final CartData cartData = getCheckoutFacade().getCheckoutCart();

		/*
		 * if (!getCheckoutFacade().containsTaxValues()) { LOG.error(String .format(
		 * "Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't continue"
		 * , cartData.getCode())); GlobalMessages.addFlashMessage(rediectmodel, GlobalMessages.ERROR_MESSAGES_HOLDER,
		 * "checkout.error.tax.missing"); invalid = true; }
		 */

		if (Boolean.FALSE.equals(cartData.isCalculated()))
		{
			LOG.error(String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
			GlobalMessages.addFlashMessage(rediectmodel, GlobalMessages.ERROR_MESSAGES_HOLDER, "checkout.error.cart.notcalculated");
			invalid = true;
		}

		return invalid;
	}

	protected String retrieveCityCode(final String addressModelPk)
	{
		if (addressModelPk != null)
		{
			final AddressModel obj = this.modelService.get(PK.parse(addressModelPk));
			if (obj != null && obj.getCity() != null)
			{
				return obj.getCity().getCode();
			}
		}
		LOG.info("Could not retrieve city code. AddressModel PK=" + addressModelPk);
		return "";
	}

	protected String retrieveCityDistrictCode(final String addressModelPk)
	{
		if (addressModelPk != null)
		{
			final AddressModel obj = this.modelService.get(PK.parse(addressModelPk));
			if (obj != null && obj.getCityDistrict() != null)
			{
				return obj.getCityDistrict().getCode();
			}
		}
		LOG.info("Could not retrieve citydistrict code. AddressModel PK=" + addressModelPk);
		return "";
	}


}
