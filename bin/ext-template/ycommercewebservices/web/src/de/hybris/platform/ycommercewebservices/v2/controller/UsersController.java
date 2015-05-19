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
 */
package de.hybris.platform.ycommercewebservices.v2.controller;

import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderHistoriesData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservicescommons.cache.CacheControl;
import de.hybris.platform.commercewebservicescommons.cache.CacheControlDirective;
import de.hybris.platform.commercewebservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressValidationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserSignUpWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK.PKException;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ycommercewebservices.constants.YcommercewebservicesConstants;
import de.hybris.platform.ycommercewebservices.populator.HttpRequestCustomerDataPopulator;
import de.hybris.platform.ycommercewebservices.populator.options.PaymentInfoOption;
import de.hybris.platform.ycommercewebservices.user.data.AddressDataList;
import de.hybris.platform.ycommercewebservices.validation.data.AddressValidationData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Main Controller for Users
 * 
 * @pathparam userId User identifier or one of the literals below :
 *            <ul>
 *            <li>'current' for currently authenticated user</li>
 *            <li>'anonymous' for anonymous user</li>
 *            </ul>
 * @pathparam addressId Address identifier
 * @pathparam paymentDetailsId - Payment details identifier
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users")
@CacheControl(directive = CacheControlDirective.PRIVATE)
public class UsersController extends BaseCommerceController
{
	private static final Logger LOG = Logger.getLogger(UsersController.class);
	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;
	@Resource(name = "userFacade")
	private UserFacade userFacade;
	@Resource(name = "userService")
	private UserService userService;
    @Resource(name = "modelService")
    private ModelService modelService;
	@Resource(name = "customerGroupFacade")
	private CustomerGroupFacade customerGroupFacade;
	@Resource(name = "addressVerificationFacade")
	private AddressVerificationFacade addressVerificationFacade;
	@Resource(name = "httpRequestCustomerDataPopulator")
	private HttpRequestCustomerDataPopulator httpRequestCustomerDataPopulator;
	@Resource(name = "httpRequestAddressDataPopulator")
	private Populator<HttpServletRequest, AddressData> httpRequestAddressDataPopulator;
	@Resource(name = "addressValidator")
	private Validator addressValidator;
	@Resource(name = "httpRequestPaymentInfoPopulator")
	private ConfigurablePopulator<HttpServletRequest, CCPaymentInfoData, PaymentInfoOption> httpRequestPaymentInfoPopulator;
	@Resource(name = "addressDataErrorsPopulator")
	private Populator<AddressVerificationResult<AddressVerificationDecision>, Errors> addressDataErrorsPopulator;
	@Resource(name = "validationErrorConverter")
	private Converter<Object, List<ErrorWsDTO>> validationErrorConverter;
	@Resource(name = "ccPaymentInfoValidator")
	private Validator ccPaymentInfoValidator;
	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;
	@Resource(name = "putUserDTOValidator")
	private Validator putUserDTOValidator;
	@Resource(name = "userSignUpDTOValidator")
	private Validator userSignUpDTOValidator;

	/**
	 * Register a customer.<br>
	 * It accepts two sets of parameters :
	 * <ul>
	 * <li>First for registering a customer. In this case required parameters are : login, password, firstName, lastName,
	 * titleCode</li>
	 * <li>Second for converting a guest to a customer. In this case required parameters are : guid, password</li>
	 * <ul>
	 * 
	 * @formparam login Customer's login. Customer login is case insensitive.
	 * @formparam password Customer's password.
	 * @formparam firstName Customer's first name.
	 * @formparam lastName Customer's last name.
	 * @formparam titleCode Customer's title code. For a list of codes, see /{baseSiteId}/titles resource
	 * @formparam guid Guest order's guid.
	 * @throws de.hybris.platform.commerceservices.customer.DuplicateUidException
	 *            in case the requested login already exists
	 * @throws de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException
	 *            in case given parameters are invalid
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void registerUser(@RequestParam(required = false) final String login, @RequestParam final String password,
			@RequestParam(required = false) final String titleCode, @RequestParam(required = false) final String firstName,
			@RequestParam(required = false) final String lastName, @RequestParam(required = false) final String guid)
			throws DuplicateUidException, RequestParameterException
	{
		if (guid != null)
		{
			convertToCustomer(login, password, titleCode, firstName, lastName, guid);
		}
		else
		{
			registerNewUser(login, password, titleCode, firstName, lastName);
		}
	}

	/**
	 * Register a customer.
	 * 
	 * @param user
	 *           User's object
	 * @bodyparams uid,password,titleCode,firstName,lastName
	 * @throws de.hybris.platform.commerceservices.customer.DuplicateUidException
	 *            in case the requested login already exists
	 * @throws UnknownIdentifierException
	 *            if the title code is invalid
	 * @throws WebserviceValidationException
	 *            if any filed is invalid
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(value = HttpStatus.CREATED)
	public void registerUser(@RequestBody final UserSignUpWsDTO user) throws DuplicateUidException, UnknownIdentifierException,
			IllegalArgumentException, WebserviceValidationException
	{
		validate(user, "user", userSignUpDTOValidator);
		final RegisterData registration = dataMapper.map(user, RegisterData.class, "login,password,titleCode,firstName,lastName");
		customerFacade.register(registration);
	}

	private void registerNewUser(final String login, final String password, final String titleCode, final String firstName,
			final String lastName) throws RequestParameterException, DuplicateUidException
	{
		validateParametersForNewUser(login, titleCode, firstName, lastName);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("registerUser: login=" + login);
		}

		if (!EmailValidator.getInstance().isValid(login))
		{
			throw new RequestParameterException("Login [" + login + "] is not a valid e-mail address!",
					RequestParameterException.INVALID, "login");
		}

		final RegisterData registration = new RegisterData();
		registration.setFirstName(firstName);
		registration.setLastName(lastName);
		registration.setLogin(login);
		registration.setPassword(password);
		registration.setTitleCode(titleCode);
		customerFacade.register(registration);
	}

	private void validateParametersForNewUser(final String login, final String titleCode, final String firstName,
			final String lastName) throws RequestParameterException
	{
		if (login == null)
		{
			throw new RequestParameterException("Required parameter is missing!", RequestParameterException.MISSING, "login");
		}

		if (titleCode == null)
		{
			throw new RequestParameterException("Required parameter is missing!", RequestParameterException.MISSING, "titleCode");
		}

		if (firstName == null)
		{
			throw new RequestParameterException("Required parameter is missing!", RequestParameterException.MISSING, "firstName");
		}

		if (lastName == null)
		{
			throw new RequestParameterException("Required parameter is missing!", RequestParameterException.MISSING, "lastName");
		}
	}

	private void convertToCustomer(final String login, final String password, final String titleCode, final String firstName,
			final String lastName, final String guid) throws RequestParameterException, DuplicateUidException
	{
		validateParametersForConvertingUser(login, titleCode, firstName, lastName);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("registerUser: guid=" + guid);
		}

		try
		{
			customerFacade.changeGuestToCustomer(password, guid);
		}
		catch (final UnknownIdentifierException ex)
		{
			throw new RequestParameterException("Order with guid " + guid + " not found in current BaseStore",
					RequestParameterException.UNKNOWN_IDENTIFIER, "guid", ex);
		}
		catch (final IllegalArgumentException ex)
		{
			// Occurs when order does not belong to guest user. 
			// For security reasons it's better to treat it as "unknown identifier" error
			throw new RequestParameterException("Order with guid " + guid + " not found in current BaseStore",
					RequestParameterException.UNKNOWN_IDENTIFIER, "guid", ex);
		}
	}

	private void validateParametersForConvertingUser(final String login, final String titleCode, final String firstName,
			final String lastName) throws RequestParameterException
	{
		if (login != null)
		{
			throw new RequestParameterException("Unknown parameter!", RequestParameterException.UNKNOWN_IDENTIFIER, "login");
		}

		if (titleCode != null)
		{
			throw new RequestParameterException("Unknown parameter!", RequestParameterException.UNKNOWN_IDENTIFIER, "titleCode");
		}

		if (firstName != null)
		{
			throw new RequestParameterException("Unknown parameter!", RequestParameterException.UNKNOWN_IDENTIFIER, "firstName");
		}

		if (lastName != null)
		{
			throw new RequestParameterException("Unknown parameter!", RequestParameterException.UNKNOWN_IDENTIFIER, "lastName");
		}
	}

	/**
	 * Returns customer profile.
	 * 
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * 
	 * @return Customer profile.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public UserWsDTO getUser(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		final UserWsDTO dto = dataMapper.map(customerData, UserWsDTO.class, fields);
		return dto;
	}

	/**
	 * Update customer's profile. Attributes not given in request will be reset (set as null or default).
	 * 
	 * @formparam firstName Customer's first name.
	 * @formparam lastName Customer's last name.
	 * @formparam titleCode Customer's title code. For a list of codes, see /{baseSiteId}/titles resource
	 * @formparam language Customer's language.
	 * @formparam currency Customer's currency.
	 * @throws DuplicateUidException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void putUser(@RequestParam final String firstName, @RequestParam final String lastName,
			@RequestParam(required = true) final String titleCode, final HttpServletRequest request) throws DuplicateUidException
	{
		final CustomerData customer = customerFacade.getCurrentCustomer();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("putCustomer: userId=" + customer.getUid());
		}
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setTitleCode(titleCode);
		customer.setLanguage(null);
		customer.setCurrency(null);
		httpRequestCustomerDataPopulator.populate(request, customer);

		customerFacade.updateFullProfile(customer);
	}

	/**
	 * Update customer's profile. Attributes not given in request body will be reset (set as null or default).
	 * 
	 * @param user
	 *           User's object
	 * @bodyparams firstName,lastName,titleCode,currency(isocode),language(isocode)
	 * @throws DuplicateUidException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void putUser(@RequestBody final UserWsDTO user) throws DuplicateUidException
	{
		validate(user, "user", putUserDTOValidator);

		final CustomerData customer = customerFacade.getCurrentCustomer();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("putCustomer: userId=" + customer.getUid());
		}

		dataMapper.map(user, customer, "firstName,lastName,titleCode,currency(isocode),language(isocode)", true);
		customerFacade.updateFullProfile(customer);
	}

	/**
	 * Update customer's profile. Only attributes given in the request will be changed.
	 * 
	 * @formparam firstName Customer's first name.
	 * @formparam lastName Customer's last name.
	 * @formparam titleCode Customer's title code. For a list of codes, see /{baseSiteId}/titles resource
	 * @formparam language Customer's language.
	 * @formparam currency Customer's currency.
	 * @throws DuplicateUidException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.OK)
	public void updateUser(final HttpServletRequest request) throws DuplicateUidException
	{
		final CustomerData customer = customerFacade.getCurrentCustomer();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateUser: userId=" + customer.getUid());
		}
		httpRequestCustomerDataPopulator.populate(request, customer);
		customerFacade.updateFullProfile(customer);
	}

	/**
	 * Update customer's profile. Only attributes given in the request body will be changed.
	 * 
	 * @param user
	 *           User's object
	 * @bodyparams firstName,lastName,titleCode,currency(isocode),language(isocode)
	 * @throws DuplicateUidException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void updateUser(@RequestBody final UserWsDTO user) throws DuplicateUidException
	{
		final CustomerData customer = customerFacade.getCurrentCustomer();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateUser: userId=" + customer.getUid());
		}

		dataMapper.map(user, customer, "firstName,lastName,titleCode,currency(isocode),language(isocode)", false);
		customerFacade.updateFullProfile(customer);
	}


    @Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
    public void deactivateUser()
    {
        final UserModel userModel = userService.getCurrentUser();
        if (!userModel.isLoginDisabled())
        {
            userModel.setLoginDisabled(true);
            modelService.save(userModel);
        }
        if (LOG.isDebugEnabled())
        {
            LOG.debug("deactivateUser: userId=" + userModel.getUid());
        }
    }

	/**
	 * Change customer's login.
	 * 
	 * @formparam newLogin Customer's new login. Customer login is case insensitive.
	 * @formparam password Customer's current password.
	 * @throws DuplicateUidException
	 * @throws PasswordMismatchException
	 * @throws RequestParameterException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/login", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void changeLogin(@RequestParam final String newLogin, @RequestParam final String password)
			throws DuplicateUidException, PasswordMismatchException, RequestParameterException
	{
		if (!EmailValidator.getInstance().isValid(newLogin))
		{
			throw new RequestParameterException("Login [" + newLogin + "] is not a valid e-mail address!",
					RequestParameterException.INVALID, "newLogin");
		}
		customerFacade.changeUid(newLogin, password);
	}

	/**
	 * Change Customer's password.
	 * 
	 * @formparam new New password
	 * @formparam old Old password. Required only for ROLE_CUSTOMERGROUP
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/password", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public void changePassword(@PathVariable final String userId, @RequestParam(required = false) final String old,
			@RequestParam(value = "new") final String newPassword)
	{
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (containsRole(auth, "ROLE_TRUSTED_CLIENT") || containsRole(auth, "ROLE_CUSTOMERMANAGERGROUP"))
		{
			userService.setPassword(userId, newPassword);
		}
		else
		{
			if (StringUtils.isEmpty(old))
			{
				throw new RequestParameterException("Request parameter 'old' is missing.", RequestParameterException.MISSING, "old");
			}
			customerFacade.changePassword(old, newPassword);
		}
	}

	private boolean containsRole(final Authentication auth, final String role)
	{
		for (final GrantedAuthority ga : auth.getAuthorities())
		{
			if (ga.getAuthority().equals(role))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Get customer's addresses.
	 * 
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return List of customer's addresses
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses", method = RequestMethod.GET)
	@ResponseBody
	public AddressListWsDTO getAddresses(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<AddressData> addressList = userFacade.getAddressBook();
		final AddressDataList addressDataList = new AddressDataList();
		addressDataList.setAddresses(addressList);
		final AddressListWsDTO dto = dataMapper.map(addressDataList, AddressListWsDTO.class, fields);
		return dto;
	}

	/**
	 * Create new address.
	 * 
	 * @formparam firstName Customer's first name. This parameter is required.
	 * @formparam lastName Customer's last name. This parameter is required.
	 * @formparam titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam country.isocode Country isocode. This parameter is required and have influence on how rest of
	 *            parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)
	 * @formparam line1 First part of address. If this parameter is required depends on country (usually it is required).
	 * @formparam line2 Second part of address. If this parameter is required depends on country (usually it is not
	 *            required)
	 * @formparam town Town name. If this parameter is required depends on country (usually it is required)
	 * @formparam postalCode Postal code. If this parameter is required depends on country (usually it is required)
	 * @formparam region.isocode Isocode for region. If this parameter is required depends on country.
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return Created address
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public AddressWsDTO createAddress(final HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws WebserviceValidationException
	{
		final AddressData addressData = super.createAddressInternal(request);
		final AddressWsDTO dto = dataMapper.map(addressData, AddressWsDTO.class, fields);
		return dto;
	}

	/**
	 * Create new address.
	 * 
	 * @param address
	 *           Address object
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @bodyparams 
	 *             firstName,lastName,titleCode,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress
	 * @return Created address
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public AddressWsDTO createAddress(@RequestBody final AddressWsDTO address,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws WebserviceValidationException
	{
		validate(address, "address", addressDTOValidator);
		final AddressData addressData = dataMapper.map(address, AddressData.class,
				"firstName,lastName,titleCode,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress");
		addressData.setShippingAddress(true);
		addressData.setVisibleInAddressBook(true);

		userFacade.addAddress(addressData);
		if (addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}

		final AddressWsDTO dto = dataMapper.map(addressData, AddressWsDTO.class, fields);
		return dto;
	}

	/**
	 * Returns detail information about address with given id.
	 * 
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return Address data
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.GET)
	@ResponseBody
	public AddressWsDTO getAddress(@PathVariable final String addressId,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws WebserviceValidationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getAddress: id=" + addressId);
		}
		final AddressData addressData = userFacade.getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException(
					"Address with given id: '" + addressId + "' doesn't exist or belong to another user",
					RequestParameterException.INVALID, "addressId");
		}

		final AddressWsDTO dto = dataMapper.map(addressData, AddressWsDTO.class, fields);
		return dto;
	}

	/**
	 * Update address. Attributes not given in request will be reset (set as null or default).
	 * 
	 * @formparam firstName Customer's first name. This parameter is required.
	 * @formparam lastName Customer's last name. This parameter is required.
	 * @formparam titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam country .isocode Country isocode. This parameter is required and have influence on how rest of
	 *            parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)
	 * @formparam line1 First part of address. If this parameter is required depends on country (usually it is required).
	 * @formparam line2 Second part of address. If this parameter is required depends on country (usually it is not
	 *            required)
	 * @formparam town Town name. If this parameter is required depends on country (usually it is required)
	 * @formparam postalCode Postal code. If this parameter is required depends on country (usually it is required)
	 *            restparam region .isocode Isocode for region. If this parameter is required depends on country.
	 * @formparam defaultAddress Parameter specifies if address should be default for customer
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void putAddress(@PathVariable final String addressId, final HttpServletRequest request)
			throws WebserviceValidationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("editAddress: id=" + addressId);
		}
		final AddressData addressData = userFacade.getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException(
					"Address with given id: '" + addressId + "' doesn't exist or belong to another user",
					RequestParameterException.INVALID, "addressId");
		}
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFirstName(null);
		addressData.setLastName(null);
		addressData.setCountry(null);
		addressData.setLine1(null);
		addressData.setLine2(null);
		addressData.setPostalCode(null);
		addressData.setRegion(null);
		addressData.setTitle(null);
		addressData.setTown(null);
		addressData.setDefaultAddress(false);
		addressData.setFormattedAddress(null);

		httpRequestAddressDataPopulator.populate(request, addressData);

		final Errors errors = new BeanPropertyBindingResult(addressData, "addressData");
		addressValidator.validate(addressData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
		userFacade.editAddress(addressData);

		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}
	}

	/**
	 * Update address. Attributes not given in request body will be reset (set as null or default).
	 * 
	 * @param address
	 *           Address object
	 * @bodyparams 
	 *             firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void putAddress(@PathVariable final String addressId, @RequestBody final AddressWsDTO address)
			throws WebserviceValidationException
	{
		validate(address, "address", addressDTOValidator);
		final AddressData addressData = userFacade.getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException(
					"Address with given id: '" + addressId + "' doesn't exist or belong to another user",
					RequestParameterException.INVALID, "addressId");
		}
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFormattedAddress(null);
		dataMapper.map(address, addressData,
				"firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress", true);

		userFacade.editAddress(addressData);

		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}
	}

	/**
	 * Update address. Only attributes given in request will be changed.
	 * 
	 * @formparam firstName Customer's first name. This parameter is required.
	 * @formparam lastName Customer's last name. This parameter is required.
	 * @formparam titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam country.isocode Country isocode. This parameter is required and have influence on how rest of
	 *            parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)
	 * @formparam line1 First part of address. If this parameter is required depends on country (usually it is required).
	 * @formparam line2 Second part of address. If this parameter is required depends on country (usually it is not
	 *            required)
	 * @formparam town Town name. If this parameter is required depends on country (usually it is required)
	 * @formparam postalCode Postal code. If this parameter is required depends on country (usually it is required)
	 * @formparam region.isocode ISO code for region. If this parameter is required depends on country.
	 * @formparam defaultAddress Parameter specifies if address should be default for customer
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.OK)
	public void patchAddress(@PathVariable final String addressId, final HttpServletRequest request)
			throws WebserviceValidationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("editAddress: id=" + addressId);
		}
		final AddressData addressData = userFacade.getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException(
					"Address with given id: '" + addressId + "' doesn't exist or belong to another user",
					RequestParameterException.INVALID, "addressId");
		}
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFormattedAddress(null);
		final Errors errors = new BeanPropertyBindingResult(addressData, "addressData");

		httpRequestAddressDataPopulator.populate(request, addressData);
		addressValidator.validate(addressData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		if (addressData.getId().equals(userFacade.getDefaultAddress().getId()))
		{
			addressData.setDefaultAddress(true);
			addressData.setVisibleInAddressBook(true);
		}
		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}
		userFacade.editAddress(addressData);
	}

	/**
	 * Update address. Only attributes given in request body will be changed.
	 * 
	 * @param address
	 *           address object
	 * @bodyparams 
	 *             firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void patchAddress(@PathVariable final String addressId, @RequestBody final AddressWsDTO address)
			throws WebserviceValidationException
	{
		final AddressData addressData = userFacade.getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException(
					"Address with given id: '" + addressId + "' doesn't exist or belong to another user",
					RequestParameterException.INVALID, "addressId");
		}
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFormattedAddress(null);

		dataMapper.map(address, addressData,
				"firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress", false);
		validate(addressData, "address", addressValidator);

		if (addressData.getId().equals(userFacade.getDefaultAddress().getId()))
		{
			addressData.setDefaultAddress(true);
			addressData.setVisibleInAddressBook(true);
		}
		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}
		userFacade.editAddress(addressData);
	}

	/**
	 * Removes address from customer
	 * 
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void deleteAddress(@PathVariable final String addressId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("deleteAddress: id=" + addressId);
		}
		final AddressData address = userFacade.getAddressForCode(addressId);
		if (address == null)
		{
			throw new RequestParameterException(
					"Address with given id: '" + addressId + "' doesn't exist or belong to another user",
					RequestParameterException.INVALID, "addressId");
		}
		userFacade.removeAddress(address);
	}

	/**
	 * Verifies address
	 * 
	 * @formparam country.isocode Country isocode. This parameter is required and have influence on how rest of
	 *            parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)
	 * @formparam line1 First part of address. If this parameter is required depends on country (usually it is required).
	 * @formparam line2 Second part of address. If this parameter is required depends on country (usually it is not
	 *            required)
	 * @formparam town Town name. If this parameter is required depends on country (usually it is required)
	 * @formparam postalCode Postal code. If this parameter is required depends on country (usually it is required)
	 * @formparam region.isocode Isocode for region. If this parameter is required depends on country.
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return verification results. If address is incorrect there are information about errors and suggested addresses
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/verification", method = RequestMethod.POST)
	@ResponseBody
	public AddressValidationWsDTO verifyAddress(final HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final AddressData addressData = new AddressData();
		final Errors errors = new BeanPropertyBindingResult(addressData, "addressData");
		AddressValidationData validationData = new AddressValidationData();

		httpRequestAddressDataPopulator.populate(request, addressData);
		if (isAddressValid(addressData, errors, validationData))
		{
			validationData = verifyAddresByService(addressData, errors, validationData);
		}
		final AddressValidationWsDTO dto = dataMapper.map(validationData, AddressValidationWsDTO.class, fields);
		return dto;
	}

	/**
	 * Verifies address
	 * 
	 * @param address
	 *           address object
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @bodyparams firstName,lastName,titleCode,line1,line2,town,postalCode,country(isocode),region(isocode)
	 * @return verification results. If address is incorrect there are information about errors and suggested addresses
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/verification", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public AddressValidationWsDTO verifyAddress(@RequestBody final AddressWsDTO address,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		// validation is a bit different here
		final AddressData addressData = dataMapper.map(address, AddressData.class,
				"firstName,lastName,titleCode,line1,line2,town,postalCode,country(isocode),region(isocode)");
		final Errors errors = new BeanPropertyBindingResult(addressData, "addressData");
		AddressValidationData validationData = new AddressValidationData();

		if (isAddressValid(addressData, errors, validationData))
		{
			validationData = verifyAddresByService(addressData, errors, validationData);
		}
		final AddressValidationWsDTO dto = dataMapper.map(validationData, AddressValidationWsDTO.class, fields);
		return dto;
	}

	/**
	 * Checks if address is valid by a validators
	 * 
	 * @formparam addressData
	 * @formparam errors
	 * @formparam validationData
	 * @return true - adress is valid , false - address is invalid
	 */
	private boolean isAddressValid(final AddressData addressData, final Errors errors, final AddressValidationData validationData)
	{
		addressValidator.validate(addressData, errors);

		if (errors.hasErrors())
		{
			validationData.setDecision(AddressVerificationDecision.REJECT.toString());
			validationData.setErrors(createResponseErrors(errors));
			return false;
		}
		return true;
	}

	/**
	 * Verifies address by commerce service
	 * 
	 * @formparam addressData
	 * @formparam errors
	 * @formparam validationData
	 * @return object with verification errors and suggested addresses list
	 */
	private AddressValidationData verifyAddresByService(final AddressData addressData, final Errors errors,
			final AddressValidationData validationData)
	{
		final AddressVerificationResult<AddressVerificationDecision> verificationDecision = addressVerificationFacade
				.verifyAddressData(addressData);
		if (verificationDecision.getErrors() != null && !verificationDecision.getErrors().isEmpty())
		{
			populateErrors(errors, verificationDecision);
			validationData.setErrors(createResponseErrors(errors));
		}

		validationData.setDecision(verificationDecision.getDecision().toString());

		if (verificationDecision.getSuggestedAddresses() != null && verificationDecision.getSuggestedAddresses().size() > 0)
		{
			final AddressDataList addressDataList = new AddressDataList();
			addressDataList.setAddresses(verificationDecision.getSuggestedAddresses());
			validationData.setSuggestedAddressesList(addressDataList);
		}

		return validationData;
	}

	private ErrorListWsDTO createResponseErrors(final Errors errors)
	{
		final List<ErrorWsDTO> webserviceErrorDto = new ArrayList<>();
		validationErrorConverter.convert(errors, webserviceErrorDto);
		final ErrorListWsDTO webserviceErrorList = new ErrorListWsDTO();
		webserviceErrorList.setErrors(webserviceErrorDto);
		return webserviceErrorList;
	}

	/**
	 * Populates Errors object
	 * 
	 * @param errors
	 * @param addressVerificationResult
	 */
	private void populateErrors(final Errors errors,
			final AddressVerificationResult<AddressVerificationDecision> addressVerificationResult)
	{
		addressDataErrorsPopulator.populate(addressVerificationResult, errors);
	}

	/**
	 * Return customer's credit card payment details list.
	 * 
	 * @queryparam saved Type of payment details
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return Customer's payment details list
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails", method = RequestMethod.GET)
	@ResponseBody
	public PaymentDetailsListWsDTO getPaymentInfos(@RequestParam(required = false, defaultValue = "false") final boolean saved,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getPaymentInfos");
		}

		final List<CCPaymentInfoData> paymentInfoDataList = userFacade.getCCPaymentInfos(saved);
		final PaymentDetailsListWsDTO dto = new PaymentDetailsListWsDTO();
		dto.setPayments(dataMapper.mapAsList(paymentInfoDataList, PaymentDetailsWsDTO.class, fields));
		return dto;
	}

	/**
	 * Return customer's credit card payment details for given id.
	 * 
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return Customer's credit card payment details
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.GET)
	@ResponseBody
	public PaymentDetailsWsDTO getPaymentDetails(@PathVariable final String paymentDetailsId,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final PaymentDetailsWsDTO dto = dataMapper.map(getPaymentInfo(paymentDetailsId), PaymentDetailsWsDTO.class, fields);
		return dto;
	}

	public CCPaymentInfoData getPaymentInfo(final String paymentDetailsId)
	{
		LOG.debug("getPaymentInfo : id = " + paymentDetailsId);
		try
		{
			final CCPaymentInfoData paymentInfoData = userFacade.getCCPaymentInfoForCode(paymentDetailsId);
			if (paymentInfoData == null)
			{
				throw new RequestParameterException("Payment details [" + paymentDetailsId + "] not found.",
						RequestParameterException.UNKNOWN_IDENTIFIER, "paymentDetailsId");
			}
			return paymentInfoData;
		}
		catch (final PKException e)
		{
			throw new RequestParameterException("Payment details [" + paymentDetailsId + "] not found.",
					RequestParameterException.UNKNOWN_IDENTIFIER, "paymentDetailsId", e);
		}
	}

	/**
	 * Removes customer's credit card payment details by it's id.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void deletePaymentInfo(@PathVariable final String paymentDetailsId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("deletePaymentInfo: id = " + paymentDetailsId);
		}
		getPaymentInfo(paymentDetailsId);
		userFacade.removeCCPaymentInfo(paymentDetailsId);
	}

	/**
	 * Updates existing customer's credit card payment details by it's ID. Only attributes given in request will be
	 * changed.
	 * 
	 * @formparam accountHolderName Name on card. This parameter is required.
	 * @formparam cardNumber Card number. This parameter is required.
	 * @formparam cardType Card type. This parameter is required. Call GET /{baseSiteId}/cardtypes beforehand to see what
	 *            card types are supported
	 * @formparam expiryMonth Month of expiry date. This parameter is required.
	 * @formparam expiryYear Year of expiry date. This parameter is required.
	 * @formparam issueNumber
	 * @formparam startMonth
	 * @formparam startYear
	 * @formparam subscriptionId
	 * @formparam saved Parameter defines if the payment details should be saved for the customer and than could be
	 *            reused for future orders.
	 * @formparam defaultPaymentInfo Parameter defines if the payment details should be used as default for customer.
	 * @formparam billingAddress.firstName Customer's first name. This parameter is required.
	 * @formparam billingAddress.lastName Customer's last name. This parameter is required.
	 * @formparam billingAddress.titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam billingAddress.country.isocode Country isocode. This parameter is required and have influence on how
	 *            rest of address parameters are validated (e.g. if parameters are required :
	 *            line1,line2,town,postalCode,region.isocode)
	 * @formparam billingAddress.line1 First part of address. If this parameter is required depends on country (usually
	 *            it is required).
	 * @formparam billingAddress.line2 Second part of address. If this parameter is required depends on country (usually
	 *            it is not required)
	 * @formparam billingAddress.town Town name. If this parameter is required depends on country (usually it is
	 *            required)
	 * @formparam billingAddress.postalCode Postal code. If this parameter is required depends on country (usually it is
	 *            required)
	 * @formparam billingAddress.region.isocode Isocode for region. If this parameter is required depends on country.
	 * @throws RequestParameterException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.OK)
	public void updatePaymentInfo(@PathVariable final String paymentDetailsId, final HttpServletRequest request)
			throws RequestParameterException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updatePaymentInfo: id = " + paymentDetailsId);
		}

		final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentDetailsId);

		final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();
		final Collection<PaymentInfoOption> options = new ArrayList<PaymentInfoOption>();
		options.add(PaymentInfoOption.BASIC);
		options.add(PaymentInfoOption.BILLING_ADDRESS);

		httpRequestPaymentInfoPopulator.populate(request, paymentInfoData, options);
		validate(paymentInfoData, "paymentDetails", ccPaymentInfoValidator);

		userFacade.updateCCPaymentInfo(paymentInfoData);
		if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo && paymentInfoData.isDefaultPaymentInfo())
		{
			userFacade.setDefaultPaymentInfo(paymentInfoData);
		}
	}

	/**
	 * Updates existing customer's credit card payment details by it's ID. Only attributes given in request body will be
	 * changed.
	 * 
	 * @param paymentDetails
	 *           payment details object
	 * @bodyparams 
	 *             accountHolderName,cardNumber,cardType,issueNumber,startMonth,expiryMonth,startYear,expiryYear,subscriptionId
	 *             ,defaultPaymentInfo,saved,billingAddress(firstName,lastName,titleCode,line1,line2,town,postalCode,
	 *             region(isocode),country(isocode),defaultAddress)
	 * @throws RequestParameterException
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void updatePaymentInfo(@PathVariable final String paymentDetailsId,
			@RequestBody final PaymentDetailsWsDTO paymentDetails) throws RequestParameterException
	{
		final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentDetailsId);
		final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();

		dataMapper
				.map(paymentDetails,
						paymentInfoData,
						"accountHolderName,cardNumber,cardType,issueNumber,startMonth,expiryMonth,startYear,expiryYear,subscriptionId,defaultPaymentInfo,saved,billingAddress(firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress)",
						false);
		validate(paymentInfoData, "paymentDetails", ccPaymentInfoValidator);

		userFacade.updateCCPaymentInfo(paymentInfoData);
		if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo && paymentInfoData.isDefaultPaymentInfo())
		{
			userFacade.setDefaultPaymentInfo(paymentInfoData);
		}

	}

	/**
	 * Updates existing customer's credit card payment info by payment info ID. Attributes not given in request will be
	 * reset (set as null or default).
	 * 
	 * @formparam accountHolderName Name on card. This parameter is required.
	 * @formparam cardNumber Card number. This parameter is required.
	 * @formparam cardType Card type. This parameter is required. Call GET /{baseSiteId}/cardtypes beforehand to see what
	 *            card types are supported
	 * @formparam expiryMonth Month of expiry date. This parameter is required.
	 * @formparam expiryYear Year of expiry date. This parameter is required.
	 * @formparam issueNumber
	 * @formparam startMonth
	 * @formparam startYear
	 * @formparam subscriptionId
	 * @formparam saved Parameter defines if the payment details should be saved for the customer and than could be
	 *            reused for future orders.
	 * @formparam defaultPaymentInfo Parameter defines if the payment details should be used as default for customer.
	 * @formparam billingAddress.firstName Customer's first name. This parameter is required.
	 * @formparam billingAddress.lastName Customer's last name. This parameter is required.
	 * @formparam billingAddress.titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam billingAddress.country.isocode Country isocode. This parameter is required and have influence on how
	 *            rest of address parameters are validated (e.g. if parameters are required :
	 *            line1,line2,town,postalCode,region.isocode)
	 * @formparam billingAddress.line1 First part of address. If this parameter is required depends on country (usually
	 *            it is required).
	 * @formparam billingAddress.line2 Second part of address. If this parameter is required depends on country (usually
	 *            it is not required)
	 * @formparam billingAddress.town Town name. If this parameter is required depends on country (usually it is
	 *            required)
	 * @formparam billingAddress.postalCode Postal code. If this parameter is required depends on country (usually it is
	 *            required)
	 * @formparam billingAddress.region.isocode Isocode for region. If this parameter is required depends on country.
	 * @throws RequestParameterException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void putPaymentInfo(@PathVariable final String paymentDetailsId, final HttpServletRequest request)
			throws RequestParameterException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updatePaymentInfo: id = " + paymentDetailsId);
		}

		final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentDetailsId);

		final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();
		paymentInfoData.setAccountHolderName(null);
		paymentInfoData.setCardNumber(null);
		paymentInfoData.setCardType(null);
		paymentInfoData.setExpiryMonth(null);
		paymentInfoData.setExpiryYear(null);
		paymentInfoData.setDefaultPaymentInfo(false);
		paymentInfoData.setSaved(false);

		paymentInfoData.setIssueNumber(null);
		paymentInfoData.setStartMonth(null);
		paymentInfoData.setStartYear(null);
		paymentInfoData.setSubscriptionId(null);

		final AddressData address = paymentInfoData.getBillingAddress();
		address.setFirstName(null);
		address.setLastName(null);
		address.setCountry(null);
		address.setLine1(null);
		address.setLine2(null);
		address.setPostalCode(null);
		address.setRegion(null);
		address.setTitle(null);
		address.setTown(null);

		final Collection<PaymentInfoOption> options = new ArrayList<PaymentInfoOption>();
		options.add(PaymentInfoOption.BASIC);
		options.add(PaymentInfoOption.BILLING_ADDRESS);

		httpRequestPaymentInfoPopulator.populate(request, paymentInfoData, options);
		validate(paymentInfoData, "paymentDetails", ccPaymentInfoValidator);

		userFacade.updateCCPaymentInfo(paymentInfoData);
		if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo && paymentInfoData.isDefaultPaymentInfo())
		{
			userFacade.setDefaultPaymentInfo(paymentInfoData);
		}
	}

	/**
	 * Updates existing customer's credit card payment info by payment info ID. Attributes not given in request will be
	 * reset (set as null or default).
	 * 
	 * @param paymentDetails
	 *           payment details object
	 * @bodyparams 
	 *             accountHolderName,cardNumber,cardType,issueNumber,startMonth,expiryMonth,startYear,expiryYear,subscriptionId
	 *             ,defaultPaymentInfo,saved,billingAddress(firstName,lastName,titleCode,line1,line2,town,postalCode,
	 *             region(isocode),country(isocode),defaultAddress)
	 * @throws RequestParameterException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void putPaymentInfo(@PathVariable final String paymentDetailsId, @RequestBody final PaymentDetailsWsDTO paymentDetails)
			throws RequestParameterException
	{
		final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentDetailsId);
		final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();

		validate(paymentDetails, "paymentDetails", paymentDetailsDTOValidator);
		dataMapper
				.map(paymentDetails,
						paymentInfoData,
						"accountHolderName,cardNumber,cardType,issueNumber,startMonth,expiryMonth,startYear,expiryYear,subscriptionId,defaultPaymentInfo,saved,billingAddress(firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress)",
						true);

		userFacade.updateCCPaymentInfo(paymentInfoData);
		if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo && paymentInfoData.isDefaultPaymentInfo())
		{
			userFacade.setDefaultPaymentInfo(paymentInfoData);
		}
	}

	/**
	 * Returns all customer groups of a customer.
	 * 
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return All customer groups of a customer.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/customergroups", method = RequestMethod.GET)
	@ResponseBody
	public UserGroupListWsDTO getAllCustomerGroupsForCustomer(@PathVariable final String userId,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<UserGroupData> userGroupDataList = customerGroupFacade.getCustomerGroupsForUser(userId);
		final UserGroupListWsDTO dto = new UserGroupListWsDTO();
		dto.setUserGroups(dataMapper.mapAsList(userGroupDataList, UserGroupWsDTO.class, fields));
		return dto;
	}

	protected Set<OrderStatus> extractOrderStatuses(final String statuses)
	{
		final String statusesStrings[] = statuses.split(YcommercewebservicesConstants.OPTIONS_SEPARATOR);

		final Set<OrderStatus> statusesEnum = new HashSet<>();
		for (final String status : statusesStrings)
		{
			statusesEnum.add(OrderStatus.valueOf(status));
		}
		return statusesEnum;
	}

	protected OrderHistoriesData createOrderHistoriesData(final SearchPageData<OrderHistoryData> result)
	{
		final OrderHistoriesData orderHistoriesData = new OrderHistoriesData();

		orderHistoriesData.setOrders(result.getResults());
		orderHistoriesData.setSorts(result.getSorts());
		orderHistoriesData.setPagination(result.getPagination());

		return orderHistoriesData;
	}
}
