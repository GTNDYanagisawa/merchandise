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

import de.hybris.platform.oci.jalo.utils.Utils;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.junit.Test;


/**
 * testing all public methods of oci class Utils
 * 
 * 
 */
public class UtilsTest extends HybrisJUnit4TransactionalTest
{

	//---------- test methoden --------------------------------------
	@Test
	public void testisEmpty() throws Exception
	{
		assertEquals(Utils.isEmpty(" "), true);
		assertEquals(Utils.isEmpty(null), true);
		assertEquals(Utils.isEmpty("moo"), false);
	}

	@Test
	public void testdoubleTostring() throws Exception
	{
		assertEquals(Utils.doubleToString(0), "0.000");
		assertEquals(Utils.doubleToString(0.0001), "0.000");
		assertEquals(Utils.doubleToString(0.0009), "0.001");
		assertEquals(Utils.doubleToString(10.0), "10.000");
		assertEquals(Utils.doubleToString(12345678901.234), "12345678901.234");
		assertEquals(Utils.doubleToString(1234567890123.4567890), "1234567890123.457"); //keine l�ngenbegrenzung nach links	
	}

	//TODO: implement this test
	/*
	 * public void testgenerateXMLDataForSAPProduct() throws Exception { Product p =
	 * JaloSession.getCurrentSession().getProductManager().createProduct("P001"); registerForRemoval(p); Unit testunit =
	 * JaloSession.getCurrentSession().getProductManager().createUnit("testtype","testcode");
	 * registerForRemoval(testunit);
	 * 
	 * p.setName("testproductname"); p.setDescription("m��"); p.setUnit(testunit);
	 * 
	 * SAPProduct sapp = new DefaultSAPProduct(p);
	 * 
	 * HashMap map1 = new HashMap(); map1.put("OCI_VERSION", "2.0"); OutboundSection obs1 = new OutboundSection(map1,
	 * "HOOK_URL"); assertEquals(Utils.generateXMLData(sapp,obs1), "");
	 * 
	 * 
	 * 
	 * }
	 */
}
