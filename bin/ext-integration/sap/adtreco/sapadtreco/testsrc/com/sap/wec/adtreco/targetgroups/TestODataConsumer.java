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
package com.sap.wec.adtreco.targetgroups;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;

import junit.framework.TestCase;

//import org.odata4j.consumer.ODataConsumer;

import com.sap.wec.adtreco.be.ODataClientService;
import com.sap.wec.adtreco.be.impl.ADTInitiativesBeCEIImpl;
import com.sap.wec.adtreco.bo.impl.SAPInitiative;
import com.sap.wec.adtreco.bo.impl.SAPInitiativeReaderImpl;


/**
 *
 */
public class TestODataConsumer extends TestCase
{

	/**
	 * 
	 */
	private static final String SERVICE_URL = "https://ldciank.wdf.sap.corp:44300/sap/opu/odata/sap/CUAN_COMMON_SRV/";


	public void testConsumerCreation()
	{
		final ODataClientService testService = new ODataClientService();
		//final ODataConsumer consumer = testService.createConsumerInstance(SERVICE_URL);
		//assertNotNull(consumer);

	}


	//	public void testConsumerAccess()
	//	{
	//
	//		final ODataConsumer consumer = createODataConsumer();
	//		final OQueryRequest<OEntity> entities = consumer.getEntities("Initiatives");
	//		assertNotNull(entities);
	//	}

	public void testSearchAll()
	{
		final SAPInitiativeReaderImpl targetGroupReader = new SAPInitiativeReaderImpl();
		/*
		 * targetGroupReader.setConsumerBuilder(new ODataConsumerBuilder()); targetGroupReader.setConfiguration(new
		 * ADTInitiativesBeCEIImpl());
		 */

		List<SAPInitiative> targetGroups = null;
		try
		{
			targetGroups = targetGroupReader.getAllInitiatives();
		}
		catch (ODataException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(targetGroups);
		System.out.println(targetGroups.size());
		System.out.println(targetGroups);

	}


	public void testRead()
	{
		final SAPInitiativeReaderImpl targetGroupReader = new SAPInitiativeReaderImpl();
		/*
		 * targetGroupReader.setConsumerBuilder(new ODataConsumerBuilder()); targetGroupReader.setConfiguration(new
		 * ADTInitiativesBeCEIImpl());
		 */

		final SAPInitiative targetGroup = targetGroupReader.getInitiative("3043");
		assertNotNull(targetGroup);
		System.out.println(targetGroup);

	}


	public void testSearch()
	{
		final SAPInitiativeReaderImpl targetGroupReader = new SAPInitiativeReaderImpl();
		/*
		 * targetGroupReader.setConsumerBuilder(new ODataConsumerBuilder()); targetGroupReader.setConfiguration(new
		 * ADTInitiativesBeCEIImpl());
		 */

		List<SAPInitiative> targetGroups = null;
		try
		{
			targetGroups = targetGroupReader.searchInitiatives("abc");
		}
		catch (ODataException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(targetGroups);
		System.out.println(targetGroups);
		assertEquals(true, targetGroups.size() > 0);

	}

	public void testSearch2()
	{
		final SAPInitiativeReaderImpl targetGroupReader = new SAPInitiativeReaderImpl();
		/*
		 * targetGroupReader.setConsumerBuilder(new ODataConsumerBuilder()); targetGroupReader.setConfiguration(new
		 * ADTInitiativesBeCEIImpl());
		 */

		List<SAPInitiative> targetGroups = null;
		try
		{
			targetGroups = targetGroupReader.searchInitiatives("abc");
		}
		catch (ODataException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(targetGroups);
		System.out.println(targetGroups);
		assertEquals(true, targetGroups.size() > 0);

	}

	public void testBPSearch()
	{
		final SAPInitiativeReaderImpl targetGroupReader = new SAPInitiativeReaderImpl();
		/*
		 * targetGroupReader.setConsumerBuilder(new ODataConsumerBuilder()); targetGroupReader.setConfiguration(new
		 * ADTInitiativesBeCEIImpl());
		 */

		List<SAPInitiative> targetGroups = null;
		try
		{
			targetGroups = targetGroupReader.searchInitiativesForBP("wec_b2b");
		}
		catch (ODataException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(targetGroups);
		System.out.println(targetGroups.size() + " target groups found for wec_b2b");
		assertEquals(true, targetGroups.size() > 0);
		System.out.println(targetGroups);

	}


	/**
	 * 
	 * 
	 protected ODataConsumer createODataConsumer() { final ODataClientService testService = new ODataClientService();
	 * final ODataConsumer consumer = testService.createConsumerInstance(SERVICE_URL, "hybris_test", "welcome1");
	 * 
	 * assertNotNull(consumer); return consumer; }
	 **/


}
