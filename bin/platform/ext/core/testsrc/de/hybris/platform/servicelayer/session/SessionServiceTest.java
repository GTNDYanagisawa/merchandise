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
package de.hybris.platform.servicelayer.session;

import static de.hybris.platform.test.SessionCloneTestUtils.assertClonedContextAttributesEqual;
import static de.hybris.platform.test.SessionCloneTestUtils.cloneViaSerialization;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.session.SessionService.SessionAttributeLoader;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class SessionServiceTest extends ServicelayerTransactionalBaseTest
{
	private static final String TEST_USER_KEY = "testUser";

	@Resource
	private SessionService sessionService;
	@Resource
	private UserService userService;

	@Test
	public void testSetAndGetAttribute() throws Exception
	{
		final UserModel userSet = userService.getUserForUID("admin");

		sessionService.setAttribute(TEST_USER_KEY, userSet);
		final String newDescription = "New description";
		userSet.setDescription(newDescription);
		final UserModel userGotten = sessionService.getAttribute(TEST_USER_KEY);
		assertNotNull("User", userGotten);
		assertSame("User", userSet, userGotten);
		assertEquals("User description", newDescription, userGotten.getDescription());
	}

	@Test
	public void testGetOrLoadAttribute() throws Exception
	{
		final UserModel userSet = userService.getUserForUID("admin");
		assertNull("User is null", sessionService.getAttribute(TEST_USER_KEY));
		final UserModel userGotten = sessionService.getOrLoadAttribute(TEST_USER_KEY, new SessionAttributeLoader<UserModel>()
		{
			@Override
			public UserModel load()
			{
				return userSet;
			}
		});
		assertNotNull("User is null", userGotten);
		assertSame("User", userSet, userGotten);
	}

	@Test
	public void testNewModel() throws Exception
	{
		final UserModel userSet = new UserModel();
		sessionService.setAttribute(TEST_USER_KEY, userSet);
		final UserModel userGot = sessionService.getAttribute(TEST_USER_KEY);
		assertEquals(userSet, userGot);
	}

	@Test
	public void testGetAllAttributes() throws Exception
	{
		final Map slAttributes = sessionService.getAllAttributes();
		final int slAttributesSize = slAttributes.size();
		final Map jaloAttributes = jaloSession.getAttributes();
		final int jaloAttributesSize = jaloAttributes.size();

		assertEquals("All attributes size", jaloAttributesSize, slAttributesSize);

		for (final Object key : jaloAttributes.keySet())
		{
			assertTrue("Session contains: " + key, slAttributes.containsKey(key));
		}
	}

	@Test
	public void testRemoveAttribute()
	{
		final UserModel userSet = new UserModel();
		sessionService.setAttribute(TEST_USER_KEY, userSet);
		assertNotNull(sessionService.getAttribute(TEST_USER_KEY));
		sessionService.removeAttribute(TEST_USER_KEY);
		assertNull(sessionService.getAttribute(TEST_USER_KEY));
	}

	@Test
	public void testSerializability() throws Exception
	{
		final Session original = sessionService.createNewSession();
		final Session serialized = cloneViaSerialization(original);

		assertClonedContextAttributesEqual(original.getAllAttributes(), serialized.getAllAttributes());
	}

}
