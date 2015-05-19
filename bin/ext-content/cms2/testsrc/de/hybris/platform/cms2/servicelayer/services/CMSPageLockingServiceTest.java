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
package de.hybris.platform.cms2.servicelayer.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class CMSPageLockingServiceTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(CMSPageLockingServiceTest.class);

	private final static String USER_A = "userA";
	private final static String USER_B = "userB";
	private final static String PAGE_A = "productPageA";
	private final static String PAGE_B = "productPageB";
	private final static String PAGE_C = "productPageC";
	private final static String CATALOG_ID = "sampleCatalog";
	private final static String CATALOG_VERSION = "Staged";

	private UserModel userA;
	private UserModel userB;
	private AbstractPageModel pageA;
	private AbstractPageModel pageB;
	private AbstractPageModel pageC;

	@Resource
	private UserService userService;
	@Resource
	private CMSPageService cmsPageService;
	@Resource
	private CMSPageLockingService cmsPageLockingService;
	@Resource
	private CatalogService catalogService;



	@Before
	public void setUp() throws Exception
	{
		LOG.info("Creating cms2 test data ..");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/test/cms2TestData.csv", "windows-1252");
		LOG.info("Finished creating cms2 test data " + (System.currentTimeMillis() - startTime) + "ms");

		catalogService.setSessionCatalogVersion(CATALOG_ID, CATALOG_VERSION);

		userA = userService.getUser(USER_A);
		userB = userService.getUser(USER_B);

		pageA = cmsPageService.getPageById(PAGE_A);
		pageB = cmsPageService.getPageById(PAGE_B);
		pageC = cmsPageService.getPageById(PAGE_C);
	}


	@Test
	public void testCheckPageLock()
	{
		assertTrue("Page A is not locked by user A!", cmsPageLockingService.isPageLockedBy(pageA, userA));
		assertFalse("Page B is locked by user A!", cmsPageLockingService.isPageLockedBy(pageB, userA));
		assertTrue("Page C is not locked by user B!", cmsPageLockingService.isPageLockedBy(pageC, userB));

		assertFalse("Page A is locked for user A!", cmsPageLockingService.isPageLockedFor(pageA, userA));
		assertTrue("Page A is not locked for user B!", cmsPageLockingService.isPageLockedFor(pageA, userB));
	}


	@Test
	public void testPageUnlock()
	{
		// User A cannot unlock page C
		cmsPageLockingService.setPageLocked(pageC, userA, false);
		assertFalse("Page C was unlocked by user A!", cmsPageLockingService.isPageLockedBy(pageC, userA));
		assertTrue("Page C is not locked for user A!", cmsPageLockingService.isPageLockedFor(pageC, userA));

		// User A can unlock own page
		cmsPageLockingService.setPageLocked(pageA, userA, false);
		assertFalse("Page A is still locked by user A!", cmsPageLockingService.isPageLockedBy(pageA, userA));

		cmsPageLockingService.setPageLocked(pageA, userA, true);
		assertTrue("Page A was not locked by user A!", cmsPageLockingService.isPageLockedBy(pageA, userA));
		assertTrue("Page A is not locked for user B!", cmsPageLockingService.isPageLockedFor(pageA, userB));

		cmsPageLockingService.setPageLocked(pageA, userB, false);
		// User B can unlock any page
		assertFalse("Page A was not unlocked by user B!", cmsPageLockingService.isPageLockedBy(pageA, userA));
	}

}
