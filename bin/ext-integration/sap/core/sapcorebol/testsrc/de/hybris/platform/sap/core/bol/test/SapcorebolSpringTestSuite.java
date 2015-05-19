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
package de.hybris.platform.sap.core.bol.test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBaseBEDeterminationTest;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBaseTest;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectHelperTest;
import de.hybris.platform.sap.core.bol.cache.GenericCacheKeyTest;
import de.hybris.platform.sap.core.bol.cache.impl.CacheAccessMockTest;
import de.hybris.platform.sap.core.bol.cache.impl.CacheAccessTest;
import de.hybris.platform.sap.core.bol.logging.LoggingTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Spring testsuite for extension sapcorebol.
 */
@UnitTest
@RunWith(Suite.class)
@SuiteClasses(
{ LoggingTest.class, CacheAccessMockTest.class, CacheAccessTest.class, GenericCacheKeyTest.class, BusinessObjectBaseTest.class,
		BusinessObjectBaseBEDeterminationTest.class, BusinessObjectHelperTest.class })
public class SapcorebolSpringTestSuite
{
	//
}
