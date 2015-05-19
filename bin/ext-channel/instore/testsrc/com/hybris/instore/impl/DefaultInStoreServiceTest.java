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
package com.hybris.instore.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.user.StoreEmployeeGroupModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.instore.strategies.InStoreSiteSelectorStrategy;


@UnitTest
public class DefaultInStoreServiceTest
{
	private static final String DEFAULT_POS = "defPOS";
	private static final String DEFAULT_SITE = "defSite";
	private static final String TEST_STORE = "testStore";

	@Mock
	private UserService userService;

	private DefaultInStoreService defaultInStoreService;

	private StoreEmployeeGroupModel storeEmployeeGroupModel;
	private EmployeeModel storeEmployeeModel;
	private PointOfServiceModel defPOS;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		defaultInStoreService = new DefaultInStoreService();
		defaultInStoreService.setUserService(userService);

		final BaseStoreModel testBaseStore = new BaseStoreModel();
		testBaseStore.setUid(TEST_STORE);

		final List<BaseSiteModel> sites = new ArrayList<BaseSiteModel>();
		testBaseStore.setCmsSites(sites);
		final BaseSiteModel defSite = new BaseSiteModel();
		defSite.setUid(DEFAULT_SITE);
		sites.add(defSite);
		sites.add(new BaseSiteModel());

		defPOS = new PointOfServiceModel();
		defPOS.setName(DEFAULT_POS);
		defPOS.setBaseStore(testBaseStore);

		storeEmployeeGroupModel = new StoreEmployeeGroupModel();
		storeEmployeeGroupModel.setUid("testGroup");
		storeEmployeeGroupModel.setStore(defPOS);

		storeEmployeeModel = new EmployeeModel();
		storeEmployeeModel.setUid("testInStoreEmployee");
		storeEmployeeModel.setGroups(Collections.<PrincipalGroupModel> singleton(storeEmployeeGroupModel));



		final List<PointOfServiceModel> pos = new ArrayList<PointOfServiceModel>();
		pos.add(defPOS);
		pos.add(new PointOfServiceModel());
		testBaseStore.setPointsOfService(pos);
	}


	@Test
	public void testInStoreEmployee()
	{
		// Test correct failing
		Mockito.when(userService.getCurrentUser()).thenReturn(new CustomerModel());

		boolean exceptionThrown = false;
		try
		{
			defaultInStoreService.getCurrentEmployee();
		}
		catch (final IllegalStateException e)
		{
			// mocked session user is a customer, so we should reach this catch block
			exceptionThrown = true;
		}
		Assert.assertTrue("Expected exception has not been thrown.", exceptionThrown);


		// Test correct result
		Mockito.when(userService.getCurrentUser()).thenReturn(storeEmployeeModel);
		final EmployeeModel currentInStoreEmployee = defaultInStoreService.getCurrentEmployee();
		Assert.assertNotNull("Store Employee was null", currentInStoreEmployee);
		Assert.assertEquals("Store Employee was not the expected one.", "testInStoreEmployee", currentInStoreEmployee.getUid());
	}


	@Test
	public void testGetBaseStore()
	{
		Mockito.when(userService.getCurrentUser()).thenReturn(storeEmployeeModel);
		Assert.assertEquals(TEST_STORE, defaultInStoreService.getBaseStoreForCurrentUser().getUid());

		// test correct exception throwing
		defPOS.setBaseStore(null);
		Assert.assertTrue("Expected exception has not been thrown.", callGetBaseStoreExpectingException());

	}

	@Test
	public void testGetBaseSite()
	{
		Mockito.when(userService.getCurrentUser()).thenReturn(storeEmployeeModel);
		Assert.assertEquals(DEFAULT_SITE, defaultInStoreService.getDefaultSiteForCurrentUser().getUid());

		// test correct exception throwing
		defPOS.setBaseStore(null);
		//exception should be thrown if no store is set
		Assert.assertTrue("Expected exception has not been thrown.", callGetSiteExpectingException(false));
		Assert.assertTrue("Expected exception has not been thrown.", callGetSiteExpectingException(true));

		// exception should be thrown if store has no linked BaseSite
		defPOS.setBaseStore(new BaseStoreModel());
		Assert.assertTrue("Expected exception has not been thrown.", callGetSiteExpectingException(false));

		// exception should be thrown if user is not an Employee
		Mockito.when(userService.getCurrentUser()).thenReturn(new CustomerModel());
		Assert.assertTrue("Expected exception has not been thrown.", callGetSiteExpectingException(false));
		Assert.assertTrue("Expected exception has not been thrown.", callGetSiteExpectingException(true));
	}


	@Test
	public void testGetDefaultSiteStrategyDelegation()
	{
		Mockito.when(userService.getCurrentUser()).thenReturn(storeEmployeeModel);
		Assert.assertEquals(DEFAULT_SITE, defaultInStoreService.getDefaultSiteForCurrentUser().getUid());

		final InStoreSiteSelectorStrategy selectorStrategy = Mockito.mock(InStoreSiteSelectorStrategy.class);
		defaultInStoreService.setSiteSelectorStrategy(selectorStrategy);

		// invoke method that should use strategy
		defaultInStoreService.getDefaultSiteForCurrentUser();

		// test if strategy method has been called 
		Mockito.verify(selectorStrategy).getDefaultSite(defaultInStoreService.getSitesForCurrentUser());
	}

	@Test
	public void testGetPointOfService()
	{
		Mockito.when(userService.getCurrentUser()).thenReturn(storeEmployeeModel);
		Assert.assertEquals(DEFAULT_POS, defaultInStoreService.getDefaultPointOfServiceForCurrentUser().getName());

		// test correct exception throwing
		storeEmployeeGroupModel.setStore(null);
		// exception should be thrown if no store is set for the employee group
		Assert.assertTrue("Expected exception has not been thrown.", callGetPOSExpectingException(false));
		Assert.assertTrue("Expected exception has not been thrown.", callGetPOSExpectingException(true));

		Mockito.when(userService.getCurrentUser()).thenReturn(new CustomerModel());
		// exception should be thrown if user is not an Employee
		Assert.assertTrue("Expected exception has not been thrown.", callGetPOSExpectingException(false));
		Assert.assertTrue("Expected exception has not been thrown.", callGetPOSExpectingException(true));
	}


	private boolean callGetBaseStoreExpectingException()
	{
		try
		{
			defaultInStoreService.getBaseStoreForCurrentUser();
		}
		catch (final IllegalStateException e)
		{
			return true;
		}
		return false;
	}

	private boolean callGetSiteExpectingException(final boolean all)
	{
		try
		{
			if (all)
			{
				defaultInStoreService.getSitesForCurrentUser();
			}
			else
			{
				defaultInStoreService.getDefaultSiteForCurrentUser();
			}
		}
		catch (final IllegalStateException e)
		{
			return true;
		}
		return false;
	}

	private boolean callGetPOSExpectingException(final boolean all)
	{
		try
		{
			if (all)
			{
				defaultInStoreService.getPointsOfServiceForCurrentUser();
			}
			else
			{
				defaultInStoreService.getDefaultPointOfServiceForCurrentUser();
			}
		}
		catch (final IllegalStateException e)
		{
			return true;
		}
		return false;
	}

	@Test
	public void testSites()
	{
		Mockito.when(userService.getCurrentUser()).thenReturn(storeEmployeeModel);

		final BaseSiteModel defaultSiteForCurrentUser = defaultInStoreService.getDefaultSiteForCurrentUser();
		Assert.assertEquals(DEFAULT_SITE, defaultSiteForCurrentUser.getUid());

		final List<BaseSiteModel> sitesForCurrentUser = defaultInStoreService.getSitesForCurrentUser();
		Assert.assertEquals(2, sitesForCurrentUser.size());

		// test correct exception throwing
		defPOS.setBaseStore(null);
	}

	@Test
	public void testPointsOfService()
	{
		Mockito.when(userService.getCurrentUser()).thenReturn(storeEmployeeModel);

		final PointOfServiceModel defaultPointOfServiceForCurrentUser = defaultInStoreService
				.getDefaultPointOfServiceForCurrentUser();
		Assert.assertEquals(DEFAULT_POS, defaultPointOfServiceForCurrentUser.getName());

		final StoreEmployeeGroupModel newEmployeeGroup = new StoreEmployeeGroupModel();
		newEmployeeGroup.setStore(new PointOfServiceModel());
		final Set<PrincipalGroupModel> newGroups = new HashSet<PrincipalGroupModel>(storeEmployeeModel.getGroups());
		newGroups.add(newEmployeeGroup);
		storeEmployeeModel.setGroups(newGroups);

		final List<PointOfServiceModel> pointsOfServiceForCurrentUser = defaultInStoreService.getPointsOfServiceForCurrentUser();
		Assert.assertEquals(2, pointsOfServiceForCurrentUser.size());
	}

}
