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
package de.hybris.platform.sap.core.jco;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Server testsuite for extension sapcorejco.
 */
@UnitTest
@RunWith(Suite.class)
@SuiteClasses(
{SapcorejcoSNCTest.class})
public class SapcorejcoServerTestSuite {

}



