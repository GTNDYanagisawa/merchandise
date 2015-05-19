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
package de.hybris.platform.sap.core.configuration.test;

import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * Base test class for sapcoreconfiguration junit test in the hybris environment without starting the server.
 */
// Standard spring xml cannot be added since it uses objects which are only available at server runtime
//  "classpath:sapcoreconfiguration-spring.xml" and "classpath:sapcoreconfiguration-datahub-spring.xml" explicitly omitted due to server dependencies 
public abstract class SapcoreconfigurationSpringJUnitTest extends SapcoreSpringJUnitTest // NOPMD
{

	// 

}
