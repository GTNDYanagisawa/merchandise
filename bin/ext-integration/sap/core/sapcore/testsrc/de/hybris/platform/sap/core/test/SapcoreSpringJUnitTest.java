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
package de.hybris.platform.sap.core.test;

import de.hybris.platform.sap.core.common.util.DefaultGenericFactory;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.runtime.SAPHybrisSessionProvider;

import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;


/**
 * Base test class for sapcore junit test in the hybris environment without starting the server.
 */
@ContextConfiguration(locations =
{ "classpath:sapcore-spring.xml", "classpath:sapcore-configuration-spring.xml", "classpath:sapcore-test-spring.xml" })
public abstract class SapcoreSpringJUnitTest extends SapcoretestSpringJUnitTest
{

	/**
	 * SAP Hybris session provider.
	 */
	@Resource(name = "sapCoreHybrisSessionProvider")
	protected SAPHybrisSessionProvider sapHybrisSessionProvider; //NOPMD

	/**
	 * Generic Factory.
	 */
	@Resource(name = "sapCoreGenericFactory")
	protected DefaultGenericFactory genericFactory; //NOPMD

	/**
	 * Getter for generic factory.
	 * 
	 * @return the genericFactory
	 */
	protected DefaultGenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	@Override
	public void setUp()
	{
		super.setUp();
		// Create SAP Hybris Session if it does not exist yet
		sapHybrisSessionProvider.getSAPHybrisSession();
		LocaleUtil.setLocale(Locale.ENGLISH);
	}

	@Override
	public void tearDown()
	{
		super.tearDown();
		// Destroy SAP Hybris Session
		sapHybrisSessionProvider.destroySAPHybrisSession();
		LocaleUtil.setLocale(null);
	}

}
