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
package de.hybris.platform.commercefacades.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.PasswordEncoderService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation for the {@link CustomerFacade}.
 */
public class DefaultCustomerFacade implements CustomerFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultCustomerFacade.class);

	private UserService userService;
	private CustomerAccountService customerAccountService;
	private CommonI18NService commonI18NService;
	private ModelService modelService;
	private StoreSessionFacade storeSessionFacade;
	private CommerceCartService commerceCartService;
	private CartService cartService;
	private SessionService sessionService;
	private UserFacade userFacade;
	private OrderFacade orderFacade;

	private Populator<AddressData, AddressModel> addressReversePopulator;
	private Populator<CustomerData, UserModel> customerReversePopulator;
	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	private Converter<UserModel, CustomerData> customerConverter;
	private Converter<TitleModel, TitleData> titleConverter;

	private CustomerNameStrategy customerNameStrategy;
	private PasswordEncoderService passwordEncoderService;

	@Override
	public CustomerData getCurrentCustomer()
	{
		return getCustomerConverter().convert(getUserService().getCurrentUser());
	}

	@Override
	public void changePassword(final String oldPassword, final String newPassword) throws PasswordMismatchException
	{
		final UserModel currentUser = getUserService().getCurrentUser();
		try
		{
			getCustomerAccountService().changePassword(currentUser, oldPassword, newPassword);
		}
		catch (final de.hybris.platform.commerceservices.customer.PasswordMismatchException e)
		{
			throw new PasswordMismatchException(e);
		}
	}

	@Override
	public void register(final RegisterData registerData) throws DuplicateUidException
	{
		validateParameterNotNullStandardMessage("registerData", registerData);
		Assert.hasText(registerData.getFirstName(), "The field [FirstName] cannot be empty");
		Assert.hasText(registerData.getLastName(), "The field [LastName] cannot be empty");
		Assert.hasText(registerData.getLogin(), "The field [Login] cannot be empty");

		final CustomerModel newCustomer = getModelService().create(CustomerModel.class);
		newCustomer.setName(getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName()));

		if (StringUtils.isNotBlank(registerData.getFirstName()) && StringUtils.isNotBlank(registerData.getLastName()))
		{
			newCustomer.setName(getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName()));
		}
		final TitleModel title = getUserService().getTitleForCode(registerData.getTitleCode());
		newCustomer.setTitle(title);
		setUidForRegister(registerData, newCustomer);
		newCustomer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		newCustomer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
		getCustomerAccountService().register(newCustomer, registerData.getPassword());
	}

	@Override
	public void createGuestUserForAnonymousCheckout(final String email, final String name) throws DuplicateUidException
	{
		validateParameterNotNullStandardMessage("email", email);
		final CustomerModel guestCustomer = getModelService().create(CustomerModel.class);
		final String guid = generateGUID();

		//takes care of localizing the name based on the site language
		guestCustomer.setUid(guid + "|" + email);
		guestCustomer.setName(name);
		guestCustomer.setType(CustomerType.valueOf(CustomerType.GUEST.getCode()));
		guestCustomer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		guestCustomer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());

		getCustomerAccountService().registerGuestForAnonymousCheckout(guestCustomer, guid);
		updateCartWithGuestForAnonymousCheckout(getCustomerConverter().convert(guestCustomer));
	}

	/**
	 * Generates a customer ID during registration
	 */
	@Override
	public String generateGUID()
	{
		return UUID.randomUUID().toString();
	}

	@Override
	public void changeGuestToCustomer(final String pwd, final String orderGUID) throws DuplicateUidException
	{
		getCustomerAccountService().convertGuestToCustomer(pwd, orderGUID);
	}

	/**
	 * Initializes a customer with given registerData
	 */
	protected void setUidForRegister(final RegisterData registerData, final CustomerModel customer)
	{
		customer.setUid(registerData.getLogin().toLowerCase());
		customer.setOriginalUid(registerData.getLogin());
	}

	@Override
	public void updateProfile(final CustomerData customerData) throws DuplicateUidException
	{
		validateDataBeforeUpdate(customerData);

		final String name = getCustomerNameStrategy().getName(customerData.getFirstName(), customerData.getLastName());
		final CustomerModel customer = getCurrentSessionCustomer();
		customer.setOriginalUid(customerData.getDisplayUid());
		getCustomerAccountService().updateProfile(customer, customerData.getTitleCode(), name, customerData.getUid());
	}

	protected void validateDataBeforeUpdate(final CustomerData customerData)
	{
		validateParameterNotNullStandardMessage("customerData", customerData);
		Assert.hasText(customerData.getTitleCode(), "The field [TitleCode] cannot be empty");
		Assert.hasText(customerData.getFirstName(), "The field [FirstName] cannot be empty");
		Assert.hasText(customerData.getLastName(), "The field [LastName] cannot be empty");
		Assert.hasText(customerData.getUid(), "The field [Uid] cannot be empty");
	}

	@Override
	public void updateFullProfile(final CustomerData customerData) throws DuplicateUidException
	{
		validateParameterNotNullStandardMessage("customerData", customerData);
		Assert.hasText(customerData.getTitleCode(), "The field [TitleCode] cannot be empty");
		Assert.hasText(customerData.getFirstName(), "The field [FirstName] cannot be empty");
		Assert.hasText(customerData.getLastName(), "The field [LastName] cannot be empty");
		Assert.hasText(customerData.getUid(), "The field [Uid] cannot be empty");

		final CustomerModel customer = getCurrentSessionCustomer();
		getCustomerReversePopulator().populate(customerData, customer);

		if (customer.getDefaultPaymentAddress() != null)
		{
			getModelService().save(customer.getDefaultPaymentAddress());
		}

		if (customer.getDefaultShipmentAddress() != null)
		{
			getModelService().save(customer.getDefaultShipmentAddress());
		}

		getModelService().save(customer);
	}

	@Override
	public void updatePassword(final String token, final String newPassword) throws TokenInvalidatedException
	{
		getCustomerAccountService().updatePassword(token, newPassword);
	}

	@Override
	public void forgottenPassword(final String uid)
	{
		Assert.hasText(uid, "The field [uid] cannot be empty");
		final CustomerModel customerModel = getUserService().getUserForUID(uid.toLowerCase(), CustomerModel.class);
		getCustomerAccountService().forgottenPassword(customerModel);
	}

	@Override
	public void loginSuccess()
	{
		final CustomerData userData = getCurrentCustomer();

		// First thing to do is to try to change the user on the session cart
		if (getCartService().hasSessionCart())
		{
			getCartService().changeCurrentCartUser(getUserService().getCurrentUser());
		}

		// Update the session currency (which might change the cart currency)
		if (!updateSessionCurrency(userData.getCurrency(), getStoreSessionFacade().getDefaultCurrency()))
		{
			// Update the user
			getUserFacade().syncSessionCurrency();
		}

		// Update the user
		getUserFacade().syncSessionLanguage();

		// Calculate the cart after setting everything up
		if (getCartService().hasSessionCart())
		{
			final CartModel sessionCart = getCartService().getSessionCart();
			try
			{
				final CommerceCartParameter parameter = new CommerceCartParameter();
				parameter.setEnableHooks(true);
				parameter.setCart(sessionCart);
				getCommerceCartService().recalculateCart(parameter);
			}
			catch (final CalculationException ex)
			{
				LOG.error("Failed to recalculate order [" + sessionCart.getCode() + "]", ex);
			}
		}
	}

	@Override
	public void changeUid(final String newUid, final String currentPassword) throws DuplicateUidException,
			PasswordMismatchException
	{
		try
		{
			getCustomerAccountService().changeUid(newUid, currentPassword);
		}
		catch (final de.hybris.platform.commerceservices.customer.PasswordMismatchException pse)
		{
			throw new PasswordMismatchException(pse);
		}

	}


	@Override
	public void updateCartWithGuestForAnonymousCheckout(final CustomerData guestCustomerData)
	{
		// First thing to do is to try to change the user on the session cart
		if (getCartService().hasSessionCart())
		{
			getCartService().changeCurrentCartUser(getUserService().getUserForUID(guestCustomerData.getUid()));
		}

		// Update the session currency (which might change the cart currency)
		if (!updateSessionCurrency(guestCustomerData.getCurrency(), getStoreSessionFacade().getDefaultCurrency()))
		{
			// Update the user
			getUserFacade().syncSessionCurrency();
		}

		if (!updateSessionLanguage(guestCustomerData.getLanguage(), getStoreSessionFacade().getDefaultLanguage()))
		{
			// Update the user
			getUserFacade().syncSessionLanguage();
		}

		// Calculate the cart after setting everything up
		if (getCartService().hasSessionCart())
		{
			final CartModel sessionCart = getCartService().getSessionCart();
			try
			{
				final CommerceCartParameter parameter = new CommerceCartParameter();
				parameter.setEnableHooks(true);
				parameter.setCart(sessionCart);
				getCommerceCartService().recalculateCart(parameter);
			}
			catch (final CalculationException ex)
			{
				LOG.error("Failed to recalculate order [" + sessionCart.getCode() + "]", ex);
			}
		}
	}

	@Override
	public void rememberMeLoginSuccessWithUrlEncoding(final boolean languageEncoding, final boolean currencyEncoding)
	{
		final CustomerData userData = getCurrentCustomer();

		// First thing to do is to try to change the user on the session cart
		if (getCartService().hasSessionCart())
		{
			getCartService().changeCurrentCartUser(getUserService().getCurrentUser());
		}

		// Update the session currency (which might change the cart currency)
		if (!currencyEncoding && !updateSessionCurrency(userData.getCurrency(), getStoreSessionFacade().getDefaultCurrency()))
		{
			// Update the user
			getUserFacade().syncSessionCurrency();
		}

		if (!languageEncoding && !updateSessionLanguage(userData.getLanguage(), getStoreSessionFacade().getDefaultLanguage()))
		{
			// Update the user
			getUserFacade().syncSessionLanguage();
		}

		// Calculate the cart after setting everything up
		if (getCartService().hasSessionCart())
		{
			final CartModel sessionCart = getCartService().getSessionCart();
			try
			{
				final CommerceCartParameter parameter = new CommerceCartParameter();
				parameter.setEnableHooks(true);
				parameter.setCart(sessionCart);
				getCommerceCartService().recalculateCart(parameter);
			}
			catch (final CalculationException ex)
			{
				LOG.error("Failed to recalculate order [" + sessionCart.getCode() + "]", ex);
			}
		}
	}


	protected boolean updateSessionCurrency(final CurrencyData preferredCurrency, final CurrencyData defaultCurrency)
	{
		if (preferredCurrency != null)
		{
			final String currencyIsoCode = preferredCurrency.getIsocode();

			// Get the available currencies and check if the currency iso code is supported
			final Collection<CurrencyData> currencies = getStoreSessionFacade().getAllCurrencies();
			for (final CurrencyData currency : currencies)
			{
				if (StringUtils.equals(currency.getIsocode(), currencyIsoCode))
				{
					// Set the current currency
					getStoreSessionFacade().setCurrentCurrency(currencyIsoCode);
					return true;
				}
			}
		}

		// Fallback to the default
		getStoreSessionFacade().setCurrentCurrency(defaultCurrency.getIsocode());
		return false;
	}

	protected boolean updateSessionLanguage(final LanguageData preferredLanguage, final LanguageData defaultLanguage)
	{
		if (preferredLanguage != null)
		{
			final String languageIsoCode = preferredLanguage.getIsocode();

			// Get the available languages and check if the language iso code is supported
			final Collection<LanguageData> languages = getStoreSessionFacade().getAllLanguages();
			for (final LanguageData language : languages)
			{
				if (StringUtils.equals(language.getIsocode(), languageIsoCode))
				{
					// Set the current language
					getStoreSessionFacade().setCurrentLanguage(languageIsoCode);
					return true;
				}
			}
		}

		// Fallback to the default
		getStoreSessionFacade().setCurrentLanguage(defaultLanguage.getIsocode());
		return false;
	}

	protected CustomerModel getCurrentSessionCustomer()
	{
		return (CustomerModel) getUserService().getCurrentUser();
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

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected StoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	@Required
	public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
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

	protected Populator<AddressData, AddressModel> getAddressReversePopulator()
	{
		return addressReversePopulator;
	}

	@Required
	public void setAddressReversePopulator(final Populator<AddressData, AddressModel> addressReversePopulator)
	{
		this.addressReversePopulator = addressReversePopulator;
	}

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> getCreditCardPaymentInfoConverter()
	{
		return creditCardPaymentInfoConverter;
	}

	@Required
	public void setCreditCardPaymentInfoConverter(
			final Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter)
	{
		this.creditCardPaymentInfoConverter = creditCardPaymentInfoConverter;
	}

	protected Converter<UserModel, CustomerData> getCustomerConverter()
	{
		return customerConverter;
	}

	@Required
	public void setCustomerConverter(final Converter<UserModel, CustomerData> customerConverter)
	{
		this.customerConverter = customerConverter;
	}

	protected Converter<TitleModel, TitleData> getTitleConverter()
	{
		return titleConverter;
	}

	@Required
	public void setTitleConverter(final Converter<TitleModel, TitleData> titleConverter)
	{
		this.titleConverter = titleConverter;
	}

	protected CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	@Required
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

	protected UserFacade getUserFacade()
	{
		return userFacade;
	}

	@Required
	public void setUserFacade(final UserFacade userFacade)
	{
		this.userFacade = userFacade;
	}

	protected PasswordEncoderService getPasswordEncoderService()
	{
		return passwordEncoderService;
	}

	@Required
	public void setPasswordEncoderService(final PasswordEncoderService passwordEncoderService)
	{
		this.passwordEncoderService = passwordEncoderService;
	}

	protected Populator<CustomerData, UserModel> getCustomerReversePopulator()
	{
		return customerReversePopulator;
	}

	@Required
	public void setCustomerReversePopulator(final Populator<CustomerData, UserModel> customerReversePopulator)
	{
		this.customerReversePopulator = customerReversePopulator;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected OrderFacade getOrderFacade()
	{
		return orderFacade;
	}

	@Required
	public void setOrderFacade(final OrderFacade orderFacade)
	{
		this.orderFacade = orderFacade;
	}
}
