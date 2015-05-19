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
package de.hybris.platform.sap.sapcommonbol.suites;

import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.impl.AddressImplTest;
import de.hybris.platform.sap.sapcommonbol.common.backendobject.impl.ConverterBackendERPCRMTest;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.impl.DistChannelMappingImplTest;
import de.hybris.platform.sap.sapcommonbol.transaction.util.ConversionHelperTest;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionToolsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * Unit test suite for sapcommonbol. <br>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
{ ConversionHelperTest.class, //
		ConverterBackendERPCRMTest.class, //
		ConversionToolsTest.class, //
		AddressImplTest.class, //
		DistChannelMappingImplTest.class, //

})
public class UnitTestSuite
{
	/* Empty block */
}
