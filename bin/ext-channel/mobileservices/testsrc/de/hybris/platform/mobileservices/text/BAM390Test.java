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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;
import de.hybris.platform.mobileservices.text.testimpl.BAM390DetectorTask;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class BAM390Test extends StatusRecordTestBase
{
	@Resource
	BAM390DetectorTask dataModelConsitencyChecker;

	@Before
	public void createConfigurationBam390() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata02.csv", "UTF-8");
	}

	@Test
	public void testBam390() throws Exception
	{
		final MessageTestingUtilities messageTestingUtilities = new MessageTestingUtilities(modelService,
				dataModelConsitencyChecker);

		/* get a message model created */
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "Hello Ho");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);

		/* bam390 test it */
		final int maxWaitTimeForTask = 30;
		final long startTime = System.currentTimeMillis();
		final long endTime = startTime + (60 * 1000); // run at most 60 seconds

		for (int i = 0; i < 200 && System.currentTimeMillis() < endTime; i++)
		{
			assertFalse("BAM-390 reproduced - see logs",
					messageTestingUtilities.isModelServiceBogusStressTest(message, maxWaitTimeForTask));
		}
	}
}
