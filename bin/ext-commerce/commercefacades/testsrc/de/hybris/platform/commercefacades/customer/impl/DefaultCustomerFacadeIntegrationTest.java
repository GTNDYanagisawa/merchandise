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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.commerceservices.event.ForgottenPwdEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Integration test suite for {@link DefaultCustomerFacade}
 */
@IntegrationTest
public class DefaultCustomerFacadeIntegrationTest extends ServicelayerTest
{
	private static final String TEST_TITLE = "dr";
	private static final String TEST_LAST = "last";
	private static final String TEST_FIRST = "first";

	private static final String ORIGINAL_UID = "Original@Dot.com";
	private static final String LOWER_ORIGINAL_UID = ORIGINAL_UID.toLowerCase();

	private static final String VARIOUS_CASE_ORIGINAL_UID = "oRiginAl@doT.Com";

	private static final Logger LOG = Logger.getLogger(DefaultCustomerFacadeIntegrationTest.class);


	private static final String NEW_PASSWORD = "newPassword";

	private RegisterData registerData;

	@Resource
	private CustomerFacade customerFacade;


	@Resource
	private UserService userService;

	@Resource
	private ModelService modelService;

	@Resource
	private SessionService sessionService;

	@Mock
	private EventService mockEventService;

	@Resource
	private EventService eventService;

	@Resource
	private DefaultCustomerAccountService customerAccountService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		customerAccountService.setEventService(mockEventService);
		try
		{
			final TitleModel drTitle = modelService.create(TitleModel.class);
			drTitle.setName(TEST_TITLE);
			drTitle.setCode(TEST_TITLE);
			modelService.save(drTitle);
		}
		catch (final ModelSavingException mse)
		{
			LOG.warn(" can not save title ");
			if (LOG.isDebugEnabled())
			{
				LOG.debug(mse);
			}

		}

		registerData = registerOriginalStartUser();
		setCurrentUser(ORIGINAL_UID.toLowerCase());

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
	public void testRegisterCaseInsensitive() throws DuplicateUidException
	{

		final CustomerModel customerModel = userService.getUserForUID(ORIGINAL_UID.toLowerCase(), CustomerModel.class);
		Assert.assertNotNull(customerModel);
		Assert.assertEquals(ORIGINAL_UID, customerModel.getOriginalUid());
		Assert.assertEquals(LOWER_ORIGINAL_UID, customerModel.getUid());

		registerData.setLogin(VARIOUS_CASE_ORIGINAL_UID);
		try
		{
			customerFacade.register(registerData);
			Assert.fail("Should not be able to add the same used uid case-insensitive");
		}
		catch (final DuplicateUidException dup)
		{
			//ok
		}

		registerData.setLogin(LOWER_ORIGINAL_UID);
		try
		{
			customerFacade.register(registerData);
			Assert.fail("Should not be able to add the same used uid case-insensitive");
		}
		catch (final DuplicateUidException dup)
		{
			//ok
		}

	}



	@Test
	public void testForgottenPasswordCaseSensitive() throws DuplicateUidException
	{
		Mockito.reset(mockEventService);

		customerFacade.forgottenPassword(ORIGINAL_UID);
		Mockito.verify(mockEventService, Mockito.times(1)).publishEvent(Mockito.any(ForgottenPwdEvent.class));

		Mockito.reset(mockEventService);

		customerFacade.forgottenPassword(VARIOUS_CASE_ORIGINAL_UID);
		Mockito.verify(mockEventService, Mockito.times(1)).publishEvent(Mockito.any(ForgottenPwdEvent.class));

		Mockito.reset(mockEventService);
		try
		{
			customerFacade.forgottenPassword("someDummy@Dot.com");
			Assert.fail("Forgot password should respect case sensitiveness");
		}
		catch (final UnknownIdentifierException uie)
		{
			//ok
		}
		Mockito.verifyNoMoreInteractions(mockEventService);

	}

	@Test
	public void testUpdateEmailCaseInsensitive() throws TokenInvalidatedException, PasswordMismatchException,
			DuplicateUidException
	{
		// Set the current user to ORIGINAL_UID
		userService.setCurrentUser(userService.getUserForUID(ORIGINAL_UID.toLowerCase(), CustomerModel.class));

		// Chane the UID to 'NewMail@Dot.com'
		customerFacade.changeUid("NewMail@Dot.com", NEW_PASSWORD);

		final CustomerModel customerModel = userService.getUserForUID("NewMail@Dot.com".toLowerCase(), CustomerModel.class);
		Assert.assertNotNull(customerModel);
		Assert.assertEquals("NewMail@Dot.com", customerModel.getOriginalUid());
		Assert.assertEquals("NewMail@Dot.com".toLowerCase(), customerModel.getUid());
	}


	@Test
	public void testUpdateEmailByTheCase() throws TokenInvalidatedException, PasswordMismatchException, DuplicateUidException
	{
		// Set the current user to ORIGINAL_UID
		userService.setCurrentUser(userService.getUserForUID(ORIGINAL_UID.toLowerCase(), CustomerModel.class));

		// Change the UID to 'NewMail@Dot.com'
		customerFacade.changeUid(VARIOUS_CASE_ORIGINAL_UID, NEW_PASSWORD);

		final CustomerModel customerModel = userService.getUserForUID(VARIOUS_CASE_ORIGINAL_UID.toLowerCase(), CustomerModel.class);
		Assert.assertNotNull(customerModel);
		Assert.assertEquals(VARIOUS_CASE_ORIGINAL_UID, customerModel.getOriginalUid());
		Assert.assertEquals(VARIOUS_CASE_ORIGINAL_UID.toLowerCase(), customerModel.getUid());
	}

	@Test(expected = PasswordMismatchException.class)
	public void testUpdateUidInvalidPassword() throws TokenInvalidatedException, PasswordMismatchException, DuplicateUidException
	{

		// Set the current user to ORIGINAL_UID
		userService.setCurrentUser(userService.getUserForUID(ORIGINAL_UID.toLowerCase(), CustomerModel.class));

		// Try to change the UID,but with the wrong password
		customerFacade.changeUid("NewMail@Dot.com", "wrong-password");
	}

	@Test
	public void testUpdateFullProfileCaseInsensitive() throws DuplicateUidException
	{

		final CustomerData sessionCustomer = customerFacade.getCurrentCustomer();

		sessionCustomer.setDisplayUid(VARIOUS_CASE_ORIGINAL_UID);

		customerFacade.updateFullProfile(sessionCustomer);
		CustomerModel customerModel = userService.getUserForUID(LOWER_ORIGINAL_UID, CustomerModel.class);

		Assert.assertEquals(VARIOUS_CASE_ORIGINAL_UID, customerModel.getOriginalUid());
		Assert.assertEquals(VARIOUS_CASE_ORIGINAL_UID.toLowerCase(), customerModel.getUid());


		sessionCustomer.setUid("DifferentUid@Dot.com");//completely different uid(and originaluid)

		customerFacade.updateFullProfile(sessionCustomer);
		try
		{
			userService.getUserForUID(LOWER_ORIGINAL_UID, CustomerModel.class);
			Assert.fail("uid was changed should not be able to find customer with old one ");
		}
		catch (final UnknownIdentifierException uke)
		{
			// ok here
		}

		customerModel = userService.getUserForUID("DifferentUid@Dot.com".toLowerCase(), CustomerModel.class);

		Assert.assertEquals("DifferentUid@Dot.com", customerModel.getOriginalUid());
		Assert.assertEquals("DifferentUid@Dot.com".toLowerCase(), customerModel.getUid());
	}

	@Test
	public void testUpdateFullProfileOriginalUidIsOverwrittenByUid() throws DuplicateUidException
	{
		final CustomerData sessionCustomer = customerFacade.getCurrentCustomer();
		sessionCustomer.setUid("DifferentUid@Dot.com");
		customerFacade.updateFullProfile(sessionCustomer);

		final CustomerModel customerModel = userService.getUserForUID("DifferentUid@Dot.com".toLowerCase(), CustomerModel.class);
		Assert.assertEquals("DifferentUid@Dot.com", customerModel.getOriginalUid());
		Assert.assertEquals("DifferentUid@Dot.com".toLowerCase(), customerModel.getUid());
	}

	@Test
	public void testUpdateProfileCaseInsensitive() throws DuplicateUidException
	{

		final CustomerModel customerModel = userService.getUserForUID(LOWER_ORIGINAL_UID, CustomerModel.class);
		Assert.assertNotNull(customerModel);
		Assert.assertEquals(ORIGINAL_UID, customerModel.getOriginalUid());
		Assert.assertEquals(LOWER_ORIGINAL_UID, customerModel.getUid());

		userService.setCurrentUser(customerModel);

		final CustomerData sessionCustomer = customerFacade.getCurrentCustomer();

		CustomerModel foundCustomerModel = userService.getUserForUID(LOWER_ORIGINAL_UID, CustomerModel.class);
		Assert.assertNotNull(foundCustomerModel);
		Assert.assertEquals(ORIGINAL_UID, foundCustomerModel.getOriginalUid());
		Assert.assertEquals(LOWER_ORIGINAL_UID, foundCustomerModel.getUid());

		sessionCustomer.setUid(VARIOUS_CASE_ORIGINAL_UID);
		sessionCustomer.setDisplayUid(VARIOUS_CASE_ORIGINAL_UID);
		customerFacade.updateProfile(sessionCustomer);

		foundCustomerModel = userService.getUserForUID(LOWER_ORIGINAL_UID, CustomerModel.class);
		Assert.assertNotNull(foundCustomerModel);
		Assert.assertEquals(VARIOUS_CASE_ORIGINAL_UID, foundCustomerModel.getOriginalUid());
		Assert.assertEquals(LOWER_ORIGINAL_UID, foundCustomerModel.getUid());

		sessionCustomer.setDisplayUid("Completely@Different.Com");
		customerFacade.updateProfile(sessionCustomer);

		try
		{
			userService.getUserForUID("Completely@Different.Com".toLowerCase(), CustomerModel.class);
			Assert.fail("should not be able to find a user, the displayUid has been overwritten by uid");
		}
		catch (final UnknownIdentifierException uke)
		{
			// fine here
		}

		sessionCustomer.setUid("Completely@Different.Com");
		customerFacade.updateProfile(sessionCustomer);

		foundCustomerModel = userService.getUserForUID("Completely@Different.Com".toLowerCase(), CustomerModel.class);
		Assert.assertNotNull(foundCustomerModel);
		Assert.assertEquals("Completely@Different.Com", foundCustomerModel.getOriginalUid());
		Assert.assertEquals("Completely@Different.Com".toLowerCase(), foundCustomerModel.getUid());


		// does  nothing with uid search 
	}


	private RegisterData registerOriginalStartUser() throws DuplicateUidException
	{
		final RegisterData registerData = new RegisterData();
		registerData.setFirstName(TEST_FIRST);
		registerData.setLastName(TEST_LAST);
		registerData.setLogin(ORIGINAL_UID);
		registerData.setPassword(NEW_PASSWORD);
		registerData.setTitleCode(TEST_TITLE);

		customerFacade.register(registerData);

		return registerData;
	}
}
