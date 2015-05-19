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
package de.hybris.platform.sap.core.jco.test;

import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;

import org.springframework.test.context.ContextConfiguration;


/**
 * Base test class for JCO unit tests.
 */
@ContextConfiguration(locations =
{ "classpath:sapcorejco-spring.xml", "classpath:sapcorejco-monitor-spring.xml", "classpath:sapcorejco-test-spring.xml",
		"classpath:global-sapcorejco-spring.xml" })
public class SapcoreJCoJUnitTest extends SapcoreSpringJUnitTest
{

}
