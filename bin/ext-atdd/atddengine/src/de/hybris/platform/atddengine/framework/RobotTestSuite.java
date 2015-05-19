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
package de.hybris.platform.atddengine.framework;

import java.util.List;


public interface RobotTestSuite
{
	void close();

	String getName();

	RobotTest getRobotTest(final String testName);

	List<RobotTest> getRobotTests();

	boolean isClosed();

	boolean isStarted();

	RobotTestResult run(final RobotTest robotTest);

	void start();
}
