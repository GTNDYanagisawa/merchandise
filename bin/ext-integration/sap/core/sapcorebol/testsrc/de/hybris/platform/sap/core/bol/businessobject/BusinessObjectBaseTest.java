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
package de.hybris.platform.sap.core.bol.businessobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;


/**
 * BusinessObjectBase test.
 */
@UnitTest
public class BusinessObjectBaseTest
{

	private final BusinessObjectBase classUnderTest = new BusinessObjectBase();

	/**
	 * Test extension data.
	 */
	@Test
	public void testExtensionData()
	{

		final HashMap<String, Object> exampleData = new HashMap<String, Object>();
		exampleData.put("extensionKey1", "value1");
		exampleData.put("extensionKey2", "value2");
		exampleData.put("extensionKey3", "value3");

		for (final String key : exampleData.keySet())
		{
			classUnderTest.addExtensionData(key, exampleData.get(key));
		}

		final Object extensionData = classUnderTest.getExtensionData("extensionKey1");
		assertEquals("value1", extensionData);

		final Map<String, Object> extensionMap = classUnderTest.getExtensionMap();
		assertNotNull(extensionMap);
		assertEquals(3, extensionMap.size());

		final Set<Entry<String, Object>> extensionDataValues = classUnderTest.getExtensionDataValues();
		assertEquals(3, extensionDataValues.size());

		classUnderTest.removeExtensionData("extensionKey1");
		assertNull(classUnderTest.getExtensionData("extensionKey1"));

		classUnderTest.removeExtensionDataValues();
		assertNull(classUnderTest.getExtensionData("extensionKey2"));

		classUnderTest.setExtensionMap(exampleData);
		assertNotNull(classUnderTest.getExtensionData("extensionKey1"));

		classUnderTest.removeExtensionDataValues();

	}

	/**
	 * Tests simple add and remove methods with the help of the BusinessObjectBase.
	 */
	@Test
	public void testAddRemoveMessage()
	{
		final String key = "testMessageResourceKey";
		final String text = "This is the text of the test message";

		final Message msg = new Message(Message.INFO, key);
		msg.setDescription(text);
		classUnderTest.addMessage(msg);

		final MessageList list = classUnderTest.getMessageList();
		Assert.assertTrue("Message should be contained after adding it!", list.contains(key));
		Assert.assertTrue("Message should be contained after adding it!", list.contains(msg));
		Assert.assertEquals("Message should be contained after adding it!", 1, list.size());

		final Message fromList = list.get(0);
		Assert.assertEquals(msg, fromList);
		Assert.assertEquals(msg.getDescription(), fromList.getDescription());

		classUnderTest.clearMessages(key);
		Assert.assertTrue("Message shouldn't be contained after removing it!", classUnderTest.getMessageList().isEmpty());
	}
}
