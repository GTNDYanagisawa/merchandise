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
package com.sap.hybris.sapcustomerb2c;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sap.hybris.sapcustomerb2c.inbound.CustomerImportServiceTest;
import com.sap.hybris.sapcustomerb2c.outbound.CustomerExportServiceTest;
import com.sap.hybris.sapcustomerb2c.outbound.CustomerPublishActionTest;
import com.sap.hybris.sapcustomerb2c.outbound.CustomerRegistrationEventListenerTest;
import com.sap.hybris.sapcustomerb2c.outbound.DefaultAddressInterceptorTest;
import com.sap.hybris.sapcustomerb2c.outbound.DefaultCustomerInterceptorTest;


/**
 * Test suite for all JUnit tests
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
{ CustomerExportServiceTest.class, CustomerPublishActionTest.class,
		CustomerRegistrationEventListenerTest.class, CustomerImportServiceTest.class,
		DefaultCustomerInterceptorTest.class, DefaultAddressInterceptorTest.class })
public class CustomerTestSuite
{
	// empty block should be documented
}
