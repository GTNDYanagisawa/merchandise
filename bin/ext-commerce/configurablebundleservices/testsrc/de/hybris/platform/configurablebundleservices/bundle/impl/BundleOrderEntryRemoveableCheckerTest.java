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
package de.hybris.platform.configurablebundleservices.bundle.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.daos.OrderEntryDao;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartService;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test to see when an order entry can be deleted
 */
@UnitTest
public class BundleOrderEntryRemoveableCheckerTest
{
	private final BundleOrderEntryRemoveableChecker checker = new BundleOrderEntryRemoveableChecker();

	@Mock
	private OrderEntryDao cartEntryDao;

	@Mock
	private SubscriptionCommerceCartService subscriptionCommerceCartService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		checker.setCartEntryDao(cartEntryDao);
		checker.setSubscriptionCommerceCartService(subscriptionCommerceCartService);
	}

	@Test
	public void shouldRemoveNonBundleEntry()
	{
		final CartEntryModel cartEntry = new CartEntryModel();
		cartEntry.setBundleNo(Integer.valueOf(0));
		Assert.assertTrue(checker.canRemove(cartEntry));
	}

	@Test
	public void autoPickProductShouldNotBeDeleted()
	{
		final BundleSelectionCriteriaModel autopick = new AutoPickBundleSelectionCriteriaModel();
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		bundleTemplate.setBundleSelectionCriteria(autopick);
		final CartEntryModel cartEntry = new CartEntryModel();
		cartEntry.setBundleTemplate(bundleTemplate);
		cartEntry.setBundleNo(Integer.valueOf(4));
		Assert.assertFalse(checker.canRemove(cartEntry));
	}

	@Test
	public void shouldNotDeleteItemsWhichDontSatisfyMinCondition()
	{
		final BundleSelectionCriteriaModel pickExactly6 = new PickExactlyNBundleSelectionCriteriaModel();
		((PickExactlyNBundleSelectionCriteriaModel) pickExactly6).setN(Integer.valueOf(6));
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		bundleTemplate.setBundleSelectionCriteria(pickExactly6);
		final CartEntryModel cartEntry = new CartEntryModel();
		cartEntry.setBundleTemplate(bundleTemplate);
		final Integer bundleNo = Integer.valueOf(4);
		final CartModel masterCart = new CartModel();
		//YTODO cartEntry.setMasterAbstractOrder(masterCart);
		cartEntry.setBundleNo(bundleNo);
		final List<CartEntryModel> bundleEntries = mock(List.class);
		when(Integer.valueOf(bundleEntries.size())).thenReturn(Integer.valueOf(5));
		when(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCart, bundleNo.intValue(), bundleTemplate))
				.thenReturn(bundleEntries);

		Assert.assertFalse(checker.canRemove(cartEntry));

	}

	@Test
	public void shouldDeleteItemsWhichSatisfyMinCondition()
	{
		final BundleSelectionCriteriaModel pick6to8 = new PickNToMBundleSelectionCriteriaModel();
		((PickNToMBundleSelectionCriteriaModel) pick6to8).setN(Integer.valueOf(6));
		((PickNToMBundleSelectionCriteriaModel) pick6to8).setM(Integer.valueOf(8));
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		bundleTemplate.setBundleSelectionCriteria(pick6to8);
		final CartEntryModel cartEntry = new CartEntryModel();
		cartEntry.setBundleTemplate(bundleTemplate);
		final Integer bundleNo = Integer.valueOf(4);
		final CartModel masterCart = new CartModel();
		//YTODO cartEntry.setMasterAbstractOrder(masterCart);
		cartEntry.setBundleNo(bundleNo);
		final List<CartEntryModel> bundleEntries = mock(List.class);
		when(Integer.valueOf(bundleEntries.size())).thenReturn(Integer.valueOf(7));
		when(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCart, bundleNo.intValue(), bundleTemplate))
				.thenReturn(bundleEntries);
		when(subscriptionCommerceCartService.getMasterCartForCartEntry(cartEntry)).thenReturn(masterCart);

		Assert.assertTrue(checker.canRemove(cartEntry));

	}

	@Test
	public void shouldDeleteItemWithNullSelectionCriteria()
	{
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		bundleTemplate.setBundleSelectionCriteria(null);
		final CartEntryModel cartEntry = new CartEntryModel();
		cartEntry.setBundleTemplate(bundleTemplate);
		final Integer bundleNo = Integer.valueOf(4);
		//YTODO final CartModel masterCart = new CartModel();
		//YTODO cartEntry.setMasterAbstractOrder(masterCart);
		cartEntry.setBundleNo(bundleNo);
		Assert.assertTrue(checker.canRemove(cartEntry));
	}

}
