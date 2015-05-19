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
package de.hybris.platform.cybersource.commands;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.payment.commands.factory.CommandFactory;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.dto.CardInfo;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;


/**
 *
 */
@ManualTest
public class IsApplicableCommandTest //extends HybrisJUnit4Test
{
	private ApplicationContext applicationContext;

	@Before
	public void setUp() throws Exception
	{
		Registry.activateMasterTenant();
		applicationContext = Registry.getGlobalApplicationContext();
	}


	@Test
	public void testIsApplicableCommandTest() throws InterruptedException
	{
		final CommandFactoryRegistry cfp = (CommandFactoryRegistry) applicationContext.getBean("commandFactoryRegistry");
		Assert.assertNotNull(cfp);
		final CommandFactory commandFactory = cfp.getFactory((CardInfo) null, false);
		Assert.assertEquals("de.hybris.platform.payment.commands.factory.impl.DefaultCommandFactoryImpl", commandFactory.getClass()
				.getName());
	}
}
