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
package de.hybris.platform.commercefacades.user;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commercefacades.user.impl.DefaultUserFacade;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.CollectionUtils;


/**
 * Integration test suite for {@link DefaultUserFacade}
 */
@IntegrationTest
public class DefaultUserFacadeIntegrationTest extends ServicelayerTest
{
	private static final String TEST_TITLE = "dr";
	private static final Logger LOG = Logger.getLogger(DefaultUserFacadeIntegrationTest.class);

	private static final String LANG_EN = "en";
	private static final String LANG_DE = "de";
	private static final String TEST_USER_UID = "dejol";


	@Resource
	private UserFacade userFacade;

	@Resource
	private UserService userService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private SessionService sessionService;

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
	public void testGetTitles()
	{
		final List<TitleData> titles = userFacade.getTitles();
		Assert.assertFalse(CollectionUtils.isEmpty(titles));

		boolean englishDrFound = false;
		boolean germanDrFound = false;
		for (final TitleData title : titles)
		{
			if (TEST_TITLE.equals(title.getCode()))
			{
				if ("dr.".equals(title.getName()))
				{
					englishDrFound = true;
				}
				if ("Dr.".equals(title.getName()))
				{
					germanDrFound = true;
				}
			}
		}
		Assert.assertTrue(englishDrFound);
		Assert.assertFalse(germanDrFound);
	}

	@Test
	public void testGetTitlesDe()
	{
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage(LANG_DE));
		final List<TitleData> titles = userFacade.getTitles();
		Assert.assertFalse(CollectionUtils.isEmpty(titles));

		boolean englishDrFound = false;
		boolean germanDrFound = false;
		for (final TitleData title : titles)
		{
			if (TEST_TITLE.equals(title.getCode()))
			{
				if ("dr.".equals(title.getName()))
				{
					englishDrFound = true;
				}
				if ("Dr.".equals(title.getName()))
				{
					germanDrFound = true;
				}
			}
		}
		Assert.assertFalse(englishDrFound);
		Assert.assertTrue(germanDrFound);
	}


}
