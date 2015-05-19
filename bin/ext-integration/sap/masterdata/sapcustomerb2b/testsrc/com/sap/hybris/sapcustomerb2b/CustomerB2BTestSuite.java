package com.sap.hybris.sapcustomerb2b;

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


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sap.hybris.sapcustomerb2b.inbound.B2BUnitAddressDeletionNotificationTranslatorTest;
import com.sap.hybris.sapcustomerb2b.inbound.B2BUnitAddressDeletionServiceTest;
import com.sap.hybris.sapcustomerb2b.inbound.DefaultSAPCustomerAddressConsistencyInterceptorTest;
import com.sap.hybris.sapcustomerb2b.outbound.B2BCustomerExportServiceTest;
import com.sap.hybris.sapcustomerb2b.outbound.DefaultB2BCustomerAddressInterceptorTest;
import com.sap.hybris.sapcustomerb2b.outbound.DefaultB2BCustomerInterceptorTest;


/**
 * Only an empty class with annotations which run the JUnits all together
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
{ B2BUnitAddressDeletionNotificationTranslatorTest.class, B2BUnitAddressDeletionServiceTest.class,
		DefaultSAPCustomerAddressConsistencyInterceptorTest.class, B2BCustomerExportServiceTest.class,
		DefaultB2BCustomerAddressInterceptorTest.class, DefaultB2BCustomerInterceptorTest.class })
public class CustomerB2BTestSuite
{

}
