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
package de.hybris.platform.mobileservices.text;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.deeplink.model.rules.DeeplinkUrlModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.util.Config;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class UseCaseMTBS02to08ItemLinksTest extends StatusRecordTestBase
{
	private static final Logger LOG = Logger.getLogger(UseCaseMTBS02to08ItemLinksTest.class.getName());

	private String wapPushEnabledBefore;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createHardwareCatalog();

		createConfiguration1();

		// make sure links are allowed globally
		wapPushEnabledBefore = Config.getParameter("mobile.wappush.enabled");
		Config.setParameter("mobile.wappush.enabled", Boolean.TRUE.toString());
	}

	@After
	public void tearDown()
	{
		Config.setParameter("mobile.wappush.enabled", wapPushEnabledBefore);
	}

	private void createConfiguration1() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata05.csv", "UTF-8");
		try
		{
			final DeeplinkUrlModel test = modelService.create(DeeplinkUrlModel.class);
			test.setBaseUrl("http://localhost:9001/mobile");
			test.setCode(Config.getString("mobile.deeplinkurl.defaultCampaignCode", "mobile"));
			test.setName(Config.getString("mobile.deeplinkurl.defaultCampaignCode", "mobile"));
			modelService.save(test);
		}
		catch (final ModelSavingException e)
		{
			// Catch already exists exception
		}
	}

	@Test
	public void testCatalogLink() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "Hi Ho");
		assertNotNull("Message could not be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);
		assertTrue("test catalog failed", message.getIsLink().booleanValue());
		assertTrue("link is non empty", StringUtils.isNotEmpty(message.getOutgoingText()));
	}
}
