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
package de.hybris.platform.commerceservices.strategies.impl;


import static junit.framework.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.PickupInStoreMode;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * Test cases for {@link DefaultPickupStrategy}
 */
@UnitTest
public class DefaultPickupStrategyTest
{
	@Spy
	@InjectMocks
	private final DefaultPickupStrategy strategy = new DefaultPickupStrategy();

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private BaseStoreModel baseStoreModel;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		strategy.setBaseStoreService(baseStoreService);
		strategy.setDefaultPickupInStoreMode(PickupInStoreMode.DISABLED);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(baseStoreModel.getPickupInStoreMode()).willReturn(PickupInStoreMode.BUY_AND_COLLECT);
	}

	@Test
	public void testNoStoreAvailable()
	{
		doReturn(null).when(baseStoreService).getCurrentBaseStore();
		assertEquals("Expected to return the disabled mode", PickupInStoreMode.DISABLED, strategy.getPickupInStoreMode());
	}

	@Test
	public void testStoreAvailable()
	{
		assertEquals("Expected to be BUY_AND_COLLECT mode", PickupInStoreMode.BUY_AND_COLLECT, strategy.getPickupInStoreMode());
	}

}
