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
 */

/**
 * 
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.docu;



/**
 * Interface for strategies which save summary of test to file
 */
public interface SaveWSOutputStrategy
{
	public static final String WS_OUTPUT_DIR = "resources/WS_OUTPUT";

	void saveFailedTest(SummaryOfRunningTest summary, Throwable t);

	void saveSucceededTest(SummaryOfRunningTest summary);
}
