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
package de.hybris.platform.chinaaccelerator.facades.customer.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chinaaccelerator.facades.customer.ExtendedCustomerFacade;
import de.hybris.platform.chinaaccelerator.services.customer.impl.ExtendedCustomerAccountService;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacadeIntegrationTest;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.PasswordEncoderService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * Integration test suite for {@link DefaultExtendedCustomerFacade}
 *
 */
@IntegrationTest
public class DefaultExtendedCustomerFacadeIntegrationTest extends DefaultCustomerFacadeIntegrationTest
{
	private static final String TEST_TITLE = "dr";
	private static final String TEST_LAST = "last";
	private static final String TEST_FIRST = "first";
	private static final String TEST_MOBILE = "mobileNumber";
	private static final String NEW_UID = "newUser@test.com";
	private static final String NEW_MOBILE = "newMobileNumber";
	private static final Logger LOG = Logger.getLogger(DefaultExtendedCustomerFacadeIntegrationTest.class);


	private static final String NEW_PASSWORD = "newPassword";
	private static final String ORIGINAL_UID = "Original@Dot.com";


	@Resource(name = "customerFacade")
	protected ExtendedCustomerFacade customerFacade;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private SessionService sessionService;

	@Resource
	private PasswordEncoderService passwordEncoderService;

	@Resource
	private EventService eventService;

	@Resource
	private ExtendedCustomerAccountService customerAccountService;

	@Test(expected = DuplicateUidException.class)
	public void testRegisterWithDuplicatedMobile() throws DuplicateUidException
	{

		final CustomerModel customerModel = userService.getUserForUID(ORIGINAL_UID.toLowerCase(), CustomerModel.class);

		final CustomerData customerData = new CustomerData();
		customerData.setUid(customerModel.getUid());
		customerData.setMobileNumber(TEST_MOBILE);
		customerFacade.updateMobileNumber(customerData, NEW_PASSWORD);

		final RegisterData duplicatedMobileData = new RegisterData();
		duplicatedMobileData.setFirstName(TEST_FIRST);
		duplicatedMobileData.setLastName(TEST_LAST);
		duplicatedMobileData.setLogin(NEW_UID);
		duplicatedMobileData.setPassword(NEW_PASSWORD);
		duplicatedMobileData.setTitleCode(TEST_TITLE);
		duplicatedMobileData.setMobileNumber(TEST_MOBILE);

		customerFacade.register(duplicatedMobileData);
	}

	@Test(expected = DuplicateUidException.class)
	public void testUpdateMobileNumber() throws DuplicateUidException
	{

		final RegisterData newRegisterData = new RegisterData();
		newRegisterData.setFirstName(TEST_FIRST);
		newRegisterData.setLastName(TEST_LAST);
		newRegisterData.setLogin(NEW_UID);
		newRegisterData.setPassword(NEW_PASSWORD);
		newRegisterData.setTitleCode(TEST_TITLE);
		newRegisterData.setMobileNumber(NEW_MOBILE);
		customerFacade.register(newRegisterData);


		userService.setCurrentUser(userService.getUserForUID(ORIGINAL_UID.toLowerCase(), CustomerModel.class));
		final CustomerData customerData = new CustomerData();
		customerData.setUid(ORIGINAL_UID);
		customerData.setMobileNumber(NEW_MOBILE);
		customerFacade.updateMobileNumber(customerData, NEW_PASSWORD);
	}

}
