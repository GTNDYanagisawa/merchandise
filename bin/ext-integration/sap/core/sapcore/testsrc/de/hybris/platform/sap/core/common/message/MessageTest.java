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
package de.hybris.platform.sap.core.common.message;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.module.impl.ModuleResourceAccessImpl;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * Test for Message.
 */
@UnitTest
@ContextConfiguration(locations =
{ "MessageTest-spring.xml" })
public class MessageTest extends SapcoreSpringJUnitTest
{

	@Resource(name = "sapCoreModuleResourceAccess")
	private ModuleResourceAccessImpl moduleResourceAccess;

	/**
	 * Test getDescription w/o parameters.
	 */
	@Test
	public void testGetMessageText()
	{
		final Message classUnderTest = new Message(Message.INFO, "mymodule.bo.test.get.string.with.resourcekey");
		classUnderTest.setModuleResourceAccess(moduleResourceAccess);
		assertEquals("My Module Resource Test", classUnderTest.getMessageText());
	}

	/**
	 * Test getDescription w/o parameters.
	 */
	@Test
	public void testGetMessageTextWithParameters()
	{
		final Message classUnderTest = new Message(Message.INFO, "mymodule.bo.test.get.string.with.resourcekey.and.arguments",
				new String[]
				{ "arg1", "arg2" }, "myProperty");
		classUnderTest.setModuleResourceAccess(moduleResourceAccess);
		assertEquals("My Module Resource Test - arguments a1:arg1, a2: arg2", classUnderTest.getMessageText());
	}

	/**
	 * Test the equals method.
	 */
	@Test
	public void testEquals()
	{
		final Message msg = new Message(Message.INFO);
		Assert.assertTrue(msg.equals(msg));
		Assert.assertFalse(msg.equals(null));//NOPMD
		Assert.assertFalse(msg.equals(new Object()));

		final Message msg2 = new Message(Message.INFO);
		Assert.assertTrue(msg.equals(msg2));

		msg2.setFieldId("id");
		Assert.assertFalse(msg.equals(msg2));
		msg.setFieldId("id");
		Assert.assertTrue(msg.equals(msg2));

		msg2.setRefTechKey(new TechKey("1337"));
		Assert.assertFalse(msg.equals(msg2));
		msg.setRefTechKey(new TechKey("1337"));
		Assert.assertTrue(msg.equals(msg2));

		msg2.setResourceKey("key");
		Assert.assertFalse(msg.equals(msg2));
		msg.setResourceKey("key");
		Assert.assertTrue(msg.equals(msg2));

		msg.setResourceArgs(new String[]
		{ "a", "b" });
		Assert.assertFalse(msg.equals(msg2));
		msg2.setResourceArgs(new String[]
		{ "a" });
		Assert.assertFalse(msg.equals(msg2));
		msg2.setResourceArgs(new String[]
		{ "a", "b" });
		Assert.assertTrue(msg.equals(msg2));


		final Message msg3 = new Message(Message.INFO, "key3");
		Assert.assertFalse(msg.equals(msg3));

		final Message msg4 = new Message(Message.DEBUG, "key");
		Assert.assertFalse(msg.equals(msg4));
	}

	/**
	 * Test the toString method.
	 */
	@Test
	public void testToString()
	{
		final String description = "description", property = "property", fieldId = "fieldid", resourceKey = "resourcekey";
		final String[] resourceArgs = new String[]
		{ "a", "b" };

		final Message msg = new Message(Message.INFO);
		msg.setDescription(description);
		msg.setProperty(property);
		msg.setFieldId(fieldId);
		msg.setResourceKey(resourceKey);
		msg.setResourceArgs(resourceArgs);

		final String expected = "Message Type: " + Message.INFO + ", Description: " + description + ", Property: " + property
				+ ", FieldId: " + fieldId + ", ResourceKey: " + resourceKey + ", Args: a b ";

		Assert.assertEquals(expected, msg.toString());
	}

}
