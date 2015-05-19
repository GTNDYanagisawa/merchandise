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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v1
import static org.junit.Assert.fail

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.TestNamePrinter
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.TestUtil
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.config.TestConfigFactory
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.docu.BaseWSTestWatcher

import org.junit.Rule

/**
 * Base class for all groovy webservice tests
 */
@ManualTest
class BaseWSTest {
	protected final TestUtil testUtil;
	protected final ConfigObject config;

	@Rule
	public TestNamePrinter tnp = new TestNamePrinter(System.out);

	protected BaseWSTest() {
		config = TestConfigFactory.createConfig("v1", "/groovytests-property-file.groovy");
		testUtil = new TestUtil(config);
	}

	/**
	 * Ancillary method to mark that some webservice resources is not tested
	 * @param resource
	 */
	protected void missingTest(String resource) {
		fail("Missing test for resource :" + resource);
	}

	@Rule
	public BaseWSTestWatcher testWatcher = new BaseWSTestWatcher();
}