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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test to check when an entry can be modified
 */
@UnitTest
public class BundleOrderEntryModifiableCheckerTest
{
	private final BundleOrderEntryModifiableChecker checker = new BundleOrderEntryModifiableChecker();

	@Test
	public void testShouldMakeBunldeEntriesNotUpdatable()
	{

		final AbstractOrderEntryModel entryToUpdate = new CartEntryModel();
		entryToUpdate.setBundleNo(Integer.valueOf(1));
		Assert.assertFalse(checker.canModify(entryToUpdate));

	}

	@Test
	public void standAloneEntriesCanBeUpdated()
	{
		final AbstractOrderEntryModel entryToUpdate = new CartEntryModel();
		entryToUpdate.setBundleNo(Integer.valueOf(0));
		Assert.assertTrue(checker.canModify(entryToUpdate));

	}

	@Test
	public void testNullBundleNo()
	{
		final AbstractOrderEntryModel entryToUpdate = new CartEntryModel();
		entryToUpdate.setBundleNo(null);
		Assert.assertTrue(checker.canModify(entryToUpdate));
	}

}
