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
package de.hybris.platform.commercefacades.customer;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.PasswordEncoderService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Integration test suite for {@link DefaultCustomerFacade}
 */
@IntegrationTest
public class DefaultCustomerFacadeIntegrationTest extends ServicelayerTest
{
	private static final String TEST_TITLE = "dr";
	private static final String TEST_LAST = "last";
	private static final String TEST_FIRST = "first";
	private static final Logger LOG = Logger.getLogger(DefaultCustomerFacadeIntegrationTest.class);

	private static final String LANG_EN = "en";
	private static final String TEST_USER_UID = "dejol";
	private static final String NEW_PASSWORD = "newPassword";
	private static final String NEW_PASSWORD_2 = "newPassword2";
	private static final String OLD_PASSWORD = "1234";
	private static final String MD5_ENCODING = "md5";


	@Resource(name = "defaultCustomerFacade", type = de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade.class)
	private CustomerFacade customerFacade;

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
	private DefaultCustomerAccountService customerAccountService;

	@Before
	public void setUp() throws Exception
	{
		// Create data for tests
		LOG.info("Creating data for user facade..");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		importCsv("/commercefacades/test/testUserFacade.csv", "utf-8");
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage(LANG_EN));
		setCurrentUser(TEST_USER_UID);
		LOG.info("Finished data for product facade " + (System.currentTimeMillis() - startTime) + "ms");
		customerAccountService.setEventService(Mockito.mock(EventService.class));
	}

	@After
	public void restore()
	{
		customerAccountService.setEventService(eventService);
	}

	protected void setCurrentUser(final String userUid)
	{
		setCurrentUser(userService.getUserForUID(userUid));
	}

	protected void setCurrentUser(final UserModel user)
	{
		sessionService.setAttribute(UserConstants.USER_SESSION_ATTR_KEY, user);
	}


	@Test
	public void testGetCurrentUser()
	{
		final CustomerData customer = customerFacade.getCurrentCustomer();
		Assert.assertEquals(TEST_USER_UID, customer.getUid());
		Assert.assertEquals("Max de", customer.getFirstName());
		Assert.assertEquals("Jol", customer.getLastName());
		Assert.assertEquals("en", customer.getLanguage().getIsocode());
		Assert.assertEquals("EUR", customer.getCurrency().getIsocode());
		Assert.assertEquals("dr", customer.getTitleCode());
		Assert.assertEquals("Max de Jol", customer.getName());
	}



	@Test
	public void testChangePassword()
	{
		customerFacade.changePassword(OLD_PASSWORD, NEW_PASSWORD);

		final UserModel userModel = userService.getUserForUID(TEST_USER_UID);
		final String expectedEncodedPassword = passwordEncoderService.encode(userModel, NEW_PASSWORD,
				userModel.getPasswordEncoding());
		final String encodedPassword = userModel.getEncodedPassword();
		Assert.assertEquals(expectedEncodedPassword, encodedPassword);

	}

	@Test
	public void testUpdatePassword() throws TokenInvalidatedException
	{
		final CustomerModel customerModel = userService.getUserForUID(TEST_USER_UID, CustomerModel.class);
		Assert.assertNull(customerModel.getToken());
		customerFacade.forgottenPassword("DeJol");
		modelService.refresh(customerModel);
		final String token = customerModel.getToken();
		Assert.assertNotNull(token);
		customerFacade.updatePassword(token, NEW_PASSWORD);
		modelService.refresh(customerModel);
		Assert.assertNull(customerModel.getToken());
		final String expected = passwordEncoderService.encode(customerModel, NEW_PASSWORD, "md5");
		Assert.assertEquals(expected, customerModel.getEncodedPassword());
		try
		{
			customerFacade.updatePassword(token, NEW_PASSWORD_2);
			Assert.fail("TokenInvalidatedException expected");
		}
		catch (final TokenInvalidatedException e)
		{
			modelService.refresh(customerModel);
			Assert.assertEquals(expected, customerModel.getEncodedPassword());
		}
	}

	@Test(expected = DuplicateUidException.class)
	public void testRegister() throws DuplicateUidException
	{
		final String uid = "test@test.de";
		final RegisterData data = new RegisterData();
		data.setFirstName(TEST_FIRST);
		data.setLastName(TEST_LAST);
		data.setLogin(uid);
		data.setPassword(NEW_PASSWORD);
		data.setTitleCode(TEST_TITLE);
		customerFacade.register(data);
		final CustomerModel customerModel = userService.getUserForUID(uid, CustomerModel.class);
		Assert.assertNotNull(customerModel);
		Assert.assertEquals(TEST_FIRST + " " + TEST_LAST, customerModel.getName());
		Assert.assertEquals(uid, customerModel.getUid());
		Assert.assertEquals(passwordEncoderService.encode(customerModel, NEW_PASSWORD, customerModel.getPasswordEncoding()),
				customerModel.getEncodedPassword());
		Assert.assertEquals(TEST_TITLE, customerModel.getTitle().getCode());
		Assert.assertTrue(StringUtils.isNotBlank(customerModel.getCustomerID()));

		customerFacade.register(data);
	}

	@Test
	public void testRegisterAndChangePassword() throws DuplicateUidException
	{
		final String uid = "test1@test.de";
		final RegisterData data = new RegisterData();
		data.setFirstName(TEST_FIRST);
		data.setLastName(TEST_LAST);
		data.setLogin(uid);
		data.setPassword(OLD_PASSWORD);
		data.setTitleCode(TEST_TITLE);
		customerFacade.register(data);

		// Validate that the customer has been created correctly
		final CustomerModel userModel = userService.getUserForUID(uid, CustomerModel.class);
		Assert.assertNotNull(userModel);
		Assert.assertEquals(TEST_FIRST + " " + TEST_LAST, userModel.getName());
		Assert.assertEquals(uid, userModel.getUid());
		Assert.assertEquals(MD5_ENCODING, userModel.getPasswordEncoding());
		Assert.assertEquals(passwordEncoderService.encode(userModel, OLD_PASSWORD, userModel.getPasswordEncoding()),
				userModel.getEncodedPassword());
		Assert.assertEquals(TEST_TITLE, userModel.getTitle().getCode());

		// Change the password from OLD_PASSWORD to NEW_PASSWORD
		setCurrentUser(uid);
		customerFacade.changePassword(OLD_PASSWORD, NEW_PASSWORD);

		// Reload the user and check the password
		final UserModel userModel2 = userService.getUserForUID(uid);
		modelService.refresh(userModel2);
		Assert.assertEquals(uid, userModel2.getUid());
		Assert.assertEquals(MD5_ENCODING, userModel2.getPasswordEncoding());
		Assert.assertEquals(passwordEncoderService.encode(userModel2, NEW_PASSWORD, userModel2.getPasswordEncoding()),
				userModel2.getEncodedPassword());
	}

	@Test
	public void testForgottenPassword() throws IOException
	{
		final CustomerModel customer = modelService.create(CustomerModel.class);
		customer
				.setUid("Testtesttesttesttesttesttest.testtesttesttest.testtesttesttest.testtest.test.test.test.test.test.ttesttesttesttesttesttesttestset@example.com");
		customer.setName("name");
		customer.setEncodedPassword("testtesttest");
		modelService.save(customer);

		customerAccountService.forgottenPassword(customer);
	}
}
