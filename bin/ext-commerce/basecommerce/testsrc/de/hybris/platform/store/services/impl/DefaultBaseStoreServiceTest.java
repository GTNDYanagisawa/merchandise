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
package de.hybris.platform.store.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.strategies.BaseStoreSelectorStrategy;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.daos.BaseStoreDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultBaseStoreServiceTest
{
	private static final String BASE_STORE_UID = "foo";

	private final DefaultBaseStoreService baseStoreService = new DefaultBaseStoreService();
	@Mock
	private BaseStoreDao baseStoreDaoMock;
	@Mock
	private BaseStoreSelectorStrategy baseStoreSelectorStrategy;
	@Mock
	private BaseStoreModel baseStoreMock1;
	@Mock
	private BaseStoreModel baseStoreMock2;



	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		baseStoreService.setBaseStoreDao(baseStoreDaoMock);
		baseStoreService.setBaseStoreSelectorStrategy(baseStoreSelectorStrategy);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.store.services.impl.DefaultBaseStoreService#getBaseStoreForUid(java.lang.String)}.
	 */
	@Test
	public void shouldThrowUnknownIdentifierExceptionWhenStoreHasNotBeenFound()
	{
		// given
		given(baseStoreDaoMock.findBaseStoresByUid(BASE_STORE_UID)).willReturn(Collections.EMPTY_LIST);

		try
		{
			// when
			baseStoreService.getBaseStoreForUid(BASE_STORE_UID);
			fail("Expected UnknownIdentifierException");
		}
		catch (final UnknownIdentifierException e)
		{
			// then
			assertThat(e.getMessage()).contains("Base store with uid '" + BASE_STORE_UID + "' not found!");
		}
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.store.services.impl.DefaultBaseStoreService#getBaseStoreForUid(java.lang.String)}.
	 */
	@Test
	public void shouldThrowAmbiguousIdentifierExceptionWhenHasBeenFoundMoreThanOneStoreForUid()
	{
		// given
		final List<BaseStoreModel> stores = new ArrayList<BaseStoreModel>();
		stores.add(baseStoreMock1);
		stores.add(baseStoreMock2);
		given(baseStoreDaoMock.findBaseStoresByUid(BASE_STORE_UID)).willReturn(stores);

		try
		{
			// when
			baseStoreService.getBaseStoreForUid(BASE_STORE_UID);
			fail("Expected UnknownIdentifierException");
		}
		catch (final AmbiguousIdentifierException e)
		{
			// then
			assertThat(e.getMessage()).contains(
					"Base store uid '" + BASE_STORE_UID + "' is not unique, " + stores.size() + " base stores found!");
		}
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.store.services.impl.DefaultBaseStoreService#getBaseStoreForUid(java.lang.String)}.
	 */
	@Test
	public void shouldReturnFoundBaseStoreModelForUid()
	{
		// given
		final List<BaseStoreModel> stores = new ArrayList<BaseStoreModel>();
		stores.add(baseStoreMock1);
		given(baseStoreDaoMock.findBaseStoresByUid(BASE_STORE_UID)).willReturn(stores);
		given(baseStoreMock1.getUid()).willReturn(BASE_STORE_UID);

		// when
		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid(BASE_STORE_UID);

		// then
		assertThat(baseStore).isNotNull();
		assertThat(baseStore).isEqualTo(stores.get(0));
		assertThat(baseStore.getUid()).isEqualTo(BASE_STORE_UID);
	}

	/**
	 * Test method for {@link de.hybris.platform.store.services.impl.DefaultBaseStoreService#getAllBaseStores()}.
	 */
	@Test
	public void shouldReturnAllBaseStores()
	{
		// given
		final List<BaseStoreModel> stores = new ArrayList<BaseStoreModel>();
		stores.add(baseStoreMock1);
		stores.add(baseStoreMock2);
		given(baseStoreDaoMock.findAllBaseStores()).willReturn(stores);

		// when
		final List<BaseStoreModel> allBaseStores = baseStoreService.getAllBaseStores();

		// then
		verify(baseStoreDaoMock, times(1)).findAllBaseStores();
		assertThat(allBaseStores).isNotEmpty();
		assertThat(allBaseStores).hasSize(2);
	}


	@Test
	public void shouldReturnBaseStoresFromStrategy()
	{
		// given		
		given(baseStoreSelectorStrategy.getCurrentBaseStore()).willReturn(baseStoreMock1);

		// when
		final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

		// then
		verify(baseStoreSelectorStrategy, times(1)).getCurrentBaseStore();
		assertThat(baseStore).isEqualTo(baseStoreMock1);
	}

	@Test
	public void shouldThrowAnExceptionFromStrategy()
	{
		// given		
		given(baseStoreSelectorStrategy.getCurrentBaseStore()).willThrow(new IllegalArgumentException());

		// when
		try
		{
			baseStoreService.getCurrentBaseStore();
			Assert.fail("Should throw an exception from strategy ");
		}
		catch (final IllegalArgumentException ile)
		{
			//ok here
		}
	}

}
