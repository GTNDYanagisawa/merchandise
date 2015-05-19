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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.configurer.impl.ConfigurerEntitiesListImplTest;
import de.hybris.platform.sap.core.common.message.MessageListTest;
import de.hybris.platform.sap.core.common.message.MessageTest;
import de.hybris.platform.sap.core.common.util.DefaultGenericFactoryTest;
import de.hybris.platform.sap.core.common.util.LogDebugTest;
import de.hybris.platform.sap.core.common.util.conv.GuidConversionUtilTest;
import de.hybris.platform.sap.core.configuration.global.impl.DefaultSAPGlobalConfigurationServiceTest;
import de.hybris.platform.sap.core.configuration.http.impl.DefaultHTTPDestinationServiceTest;
import de.hybris.platform.sap.core.configuration.impl.DefaultConfigurationPropertyAccessTest;
import de.hybris.platform.sap.core.configuration.impl.DefaultGlobalConfigurationManagerTest;
import de.hybris.platform.sap.core.configuration.rfc.impl.DefaultRFCDestinationServiceTest;
import de.hybris.platform.sap.core.module.impl.ModuleConfigurationAccessTest;
import de.hybris.platform.sap.core.module.impl.ModuleResourceAccessTest;
import de.hybris.platform.sap.core.requestsequencer.RequestSequencerFilterTest;
import de.hybris.platform.sap.core.requestsequencer.configurer.impl.UrlPatternImplTest;
import de.hybris.platform.sap.core.spring.GenericPopulatorConfigurerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Spring testsuite for extension sapcore.
 */
@UnitTest
@RunWith(Suite.class)
@SuiteClasses(
{ ModuleConfigurationAccessTest.class, ModuleResourceAccessTest.class, MessageTest.class, DefaultRFCDestinationServiceTest.class,
		DefaultHTTPDestinationServiceTest.class, DefaultGenericFactoryTest.class, DefaultGlobalConfigurationManagerTest.class,
		DefaultSAPGlobalConfigurationServiceTest.class, DefaultConfigurationPropertyAccessTest.class, UrlPatternImplTest.class,
		ConfigurerEntitiesListImplTest.class, RequestSequencerFilterTest.class, LogDebugTest.class,
		GenericPopulatorConfigurerTest.class, GuidConversionUtilTest.class, MessageListTest.class })
public class SapcoreSpringTestSuite
{

}
