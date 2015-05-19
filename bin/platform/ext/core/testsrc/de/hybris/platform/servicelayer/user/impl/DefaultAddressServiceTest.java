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
package de.hybris.platform.servicelayer.user.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.daos.AddressDao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Unit tests for {@link DefaultAddressService}.
 */
@UnitTest
public class DefaultAddressServiceTest
{
	private DefaultAddressService addressService;

	private AddressDao addressDao;

	@Before
	public void setUp()
	{
		addressDao = Mockito.mock(AddressDao.class);

		final ModelService modelService = Mockito.mock(ModelService.class);
		Mockito.when(modelService.create(AddressModel.class)).thenAnswer(new Answer<AddressModel>()
		{
			@Override
			public AddressModel answer(final InvocationOnMock invocation) throws Throwable
			{
				return new AddressModel();
			}
		});
		Mockito.when(modelService.clone(Mockito.any(AddressModel.class))).thenAnswer(new Answer<AddressModel>()
		{
			@Override
			public AddressModel answer(final InvocationOnMock invocation) throws Throwable
			{
				final AddressModel oldAddress = (AddressModel) invocation.getArguments()[0];
				final ByteArrayOutputStream outp = new ByteArrayOutputStream();
				final ObjectOutputStream bOutp = new ObjectOutputStream(outp);
				bOutp.writeObject(oldAddress);
				final ByteArrayInputStream inp = new ByteArrayInputStream(outp.toByteArray());
				final ObjectInputStream bInp = new ObjectInputStream(inp);
				return (AddressModel) bInp.readObject();
			}
		});

		addressService = new DefaultAddressService();
		addressService.setModelService(modelService);
		addressService.setAddressDao(addressDao);
	}

	@Test
	public void testCreateAddressForUser()
	{
		final UserModel user = new UserModel();

		final AddressModel address = addressService.createAddressForUser(user);

		assertNotNull("Address is null.", address);
		assertEquals("Owner differs.", user, address.getOwner());
	}

	@Test
	public void testCreateAddressForOwner()
	{
		final UserGroupModel group = new UserGroupModel();
		group.setUid("testGroup");
		group.setName("Testgroup");

		final AddressModel address = addressService.createAddressForOwner(group);

		assertNotNull("Address is null.", address);
		assertEquals("Owner differs.", group, address.getOwner());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateAddressForOwnerWithNull()
	{
		addressService.createAddressForOwner(null);
	}

	@Test
	public void testGetAddressesForOwner()
	{
		final AddressModel adr1 = new AddressModel();
		final AddressModel adr2 = new AddressModel();

		final UserGroupModel group = new UserGroupModel();

		Mockito.when(addressDao.findAddressesForOwner(group)).thenReturn(Arrays.asList(adr1, adr2));

		final Collection<AddressModel> result = addressService.getAddressesForOwner(group);

		assertNotNull("Address collection is null.", result);
		assertEquals("Address collection size differs.", 2, result.size());
		assertEquals("Address collection content differs.", new HashSet<AddressModel>(Arrays.asList(adr1, adr2)),
				new HashSet<AddressModel>(result));
	}

	@Test
	public void testGetNonAddressesForOwner()
	{
		final UserGroupModel group = new UserGroupModel();

		Mockito.when(addressDao.findAddressesForOwner(group)).thenReturn(Collections.EMPTY_LIST);

		final Collection<AddressModel> result = addressService.getAddressesForOwner(group);

		assertNotNull("Address collection is null.", result);
		assertEquals("Address collection size differs.", 0, result.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetAddressesForOwnerWithNull()
	{
		addressService.getAddressesForOwner(null);
	}

	@Test
	public void testCloneAddress()
	{
		final UserGroupModel group = new UserGroupModel();
		group.setUid("testGroup");
		group.setName("Testgroup");

		final AddressModel original = new AddressModel();
		original.setFirstname("Test");
		original.setLastname("Tester");
		original.setOwner(group);

		final AddressModel clone = addressService.cloneAddress(original);

		assertNotNull("Cloned address is null.", clone);
		assertFalse("Cloned address is equal to original.", original.equals(clone));
		assertEquals("Firstname differs.", original.getFirstname(), clone.getFirstname());
		assertEquals("Lastname differs.", original.getLastname(), clone.getLastname());
		assertEquals("Owner differs.", original.getOwner(), clone.getOwner());
		assertEquals("Original differs.", original, clone.getOriginal());
		assertEquals("Duplicate is not set.", Boolean.TRUE, clone.getDuplicate());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCloneAddressWithNull()
	{
		addressService.cloneAddress(null);
	}

	@Test
	public void testCloneAddressForOwner()
	{
		final UserGroupModel group = new UserGroupModel();
		group.setUid("testGroup");
		group.setName("Testgroup");

		final AddressModel original = new AddressModel();
		original.setFirstname("Test");
		original.setLastname("Tester");
		original.setOwner(group);

		final UserModel user = new UserModel();
		user.setUid("testUser");
		user.setName("Testuser");

		final AddressModel clone = addressService.cloneAddressForOwner(original, user);

		assertNotNull("Cloned address is null.", clone);
		assertFalse("Cloned address is equal to original.", original.equals(clone));
		assertEquals("Firstname differs.", original.getFirstname(), clone.getFirstname());
		assertEquals("Lastname differs.", original.getLastname(), clone.getLastname());
		assertEquals("Owner differs.", user, clone.getOwner());
		assertEquals("Original differs.", original, clone.getOriginal());
		assertEquals("Duplicate is not set.", Boolean.TRUE, clone.getDuplicate());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCloneAddressForOwnerWithAddressNull()
	{
		final UserModel user = new UserModel();
		user.setUid("testUser");
		user.setName("Testuser");

		addressService.cloneAddressForOwner(null, user);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCloneAddressForOwnerWithOwnerNull()
	{
		final UserGroupModel group = new UserGroupModel();
		group.setUid("testGroup");
		group.setName("Testgroup");

		final AddressModel original = new AddressModel();
		original.setFirstname("Test");
		original.setLastname("Tester");
		original.setOwner(group);

		addressService.cloneAddressForOwner(original, null);
	}
}
