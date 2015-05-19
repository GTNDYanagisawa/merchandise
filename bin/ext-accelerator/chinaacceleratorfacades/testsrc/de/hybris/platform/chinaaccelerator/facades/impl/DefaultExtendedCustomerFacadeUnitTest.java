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
package de.hybris.platform.chinaaccelerator.facades.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.platform.chinaaccelerator.facades.customer.impl.DefaultExtendedCustomerFacade;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacadeTest;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCustomerNameStrategy;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.PasswordEncoderService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


/**
 * DefaultExtendedCustomerFacadeUnitTest
 */
public class DefaultExtendedCustomerFacadeUnitTest extends DefaultCustomerFacadeTest
{
	private static final String TEST_DUMMY = "dummy";
	private static final String TEST_PASSWORD = "password";
	private static final String TEST_PASS_ENCODING = "complicated";
	private static final String TEST_USER_UID = "userId";
	private static final String TEST_MOBILE_NUMBER = "mobileNumber";

	@Mock
	private UserService userService;
	@Mock
	private UserModel user;
	@Mock
	private CustomerModel customer;
	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private ModelService mockModelService;
	@Mock
	private AbstractPopulatingConverter<UserModel, CustomerData> customerConverter;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private PasswordEncoderService passwordEncoderService;

	private DefaultExtendedCustomerFacade extendedCustomerFacade;

	private CustomerNameStrategy customerNameStrategy;

	/**
	 * Test method for
	 * {@link de.hybris.platform.chinaaccelerator.facades.customer.impl.DefaultExtendedCustomerFacade#register(de.hybris.platform.commercefacades.user.data.RegisterData)}
	 * .
	 */

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		extendedCustomerFacade = new DefaultExtendedCustomerFacade();
		extendedCustomerFacade.setUserService(userService);
		extendedCustomerFacade.setModelService(mockModelService);
		extendedCustomerFacade.setCustomerAccountService(customerAccountService);
		extendedCustomerFacade.setCustomerConverter(customerConverter);
		extendedCustomerFacade.setCommonI18NService(commonI18NService);
		extendedCustomerFacade.setPasswordEncoderService(passwordEncoderService);

		customerNameStrategy = new DefaultCustomerNameStrategy();
		extendedCustomerFacade.setCustomerNameStrategy(customerNameStrategy);

		given(customer.getUid()).willReturn(TEST_USER_UID);
		given(customer.getMobileNumber()).willReturn(TEST_MOBILE_NUMBER);
	}

	@Override
	@Test
	public void testRegister() throws DuplicateUidException
	{
		final RegisterData data = new RegisterData();
		data.setFirstName(TEST_DUMMY);
		data.setLastName(TEST_DUMMY);
		data.setLogin(TEST_DUMMY);
		data.setPassword(TEST_DUMMY);
		data.setTitleCode(TEST_DUMMY);
		data.setMobileNumber(TEST_DUMMY);
		final CustomerModel model = new CustomerModel();
		given(mockModelService.create(CustomerModel.class)).willReturn(model);
		extendedCustomerFacade.register(data);
		verify(customerAccountService).register(model, TEST_DUMMY);
	}

	@Test
	public void testupdateMobileNumber() throws PasswordMismatchException, DuplicateUidException
	{

		final String encodedPassword = "encodedOne";
		final CustomerData customerData = new CustomerData();
		customerData.setTitleCode(TEST_DUMMY);
		customerData.setFirstName(TEST_DUMMY);
		customerData.setLastName(TEST_DUMMY);
		customerData.setUid(TEST_USER_UID);
		customerData.setMobileNumber(TEST_DUMMY);

		given(userService.getUserForUID(customerData.getUid().toLowerCase(), CustomerModel.class)).willReturn(customer);
		given(passwordEncoderService.encode(customer, TEST_PASSWORD, TEST_PASS_ENCODING)).willReturn(encodedPassword);
		given(customer.getEncodedPassword()).willReturn(encodedPassword);
		given(customer.getPasswordEncoding()).willReturn(TEST_PASS_ENCODING);


		extendedCustomerFacade.updateMobileNumber(customerData, TEST_PASSWORD);

		verify(customer).setMobileNumber(TEST_DUMMY);
		verify(mockModelService).save(customer);
	}

	@Test(expected = PasswordMismatchException.class)
	public void testupdateMobileNumberPasswordMismatchException() throws PasswordMismatchException, DuplicateUidException
	{

		final String encodedPassword = "encodedOne";
		final String differentPassword = "differentOne";
		final CustomerData customerData = new CustomerData();
		customerData.setTitleCode(TEST_DUMMY);
		customerData.setFirstName(TEST_DUMMY);
		customerData.setLastName(TEST_DUMMY);
		customerData.setUid(TEST_USER_UID);
		customerData.setMobileNumber(TEST_DUMMY);

		given(userService.getUserForUID(customerData.getUid().toLowerCase(), CustomerModel.class)).willReturn(customer);
		given(passwordEncoderService.encode(customer, TEST_PASSWORD, TEST_PASS_ENCODING)).willReturn(encodedPassword);
		given(customer.getEncodedPassword()).willReturn(differentPassword);
		given(customer.getPasswordEncoding()).willReturn(TEST_PASS_ENCODING);

		extendedCustomerFacade.updateMobileNumber(customerData, TEST_PASSWORD);
	}

}
