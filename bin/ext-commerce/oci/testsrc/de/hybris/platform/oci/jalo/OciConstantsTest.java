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
package de.hybris.platform.oci.jalo;

import static junit.framework.Assert.assertEquals;

import de.hybris.platform.oci.constants.OciConstants;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.junit.Test;


/**
 * testing all public methods of oci class OciConstants
 */
public class OciConstantsTest extends HybrisJUnit4TransactionalTest
{
	//---------------- test methoden ---------------------------------
	@Test
	public void testOciConstants() throws Exception
	{
		assertEquals(OciConstants.BACKGROUND_SEARCH, "BACKGROUND_SEARCH");
		assertEquals(OciConstants.DEFAULT_OCI_BUTTON_VALUE, "SAP OCI Buyer");
		assertEquals(OciConstants.DETAIL, "DETAIL");
		assertEquals(OciConstants.FUNCTION, "FUNCTION");
		assertEquals(OciConstants.IS_OCI_LOGIN, "IS_OCI_LOGIN");
		assertEquals(OciConstants.OCI_VERSION, "OCI_VERSION");
		assertEquals(OciConstants.OUTBOUND_SECTION_DATA, "OUTBOUND_SECTION_DATA");
		assertEquals(OciConstants.PRODUCTID, "PRODUCTID");
		assertEquals(OciConstants.QUANTITY, "QUANTITY");
		assertEquals(OciConstants.SEARCHSTRING, "SEARCHSTRING");
		assertEquals(OciConstants.SOURCING, "SOURCING");
		assertEquals(OciConstants.VALIDATE, "VALIDATE");
		assertEquals(OciConstants.VENDOR, "VENDOR");
	}

}
