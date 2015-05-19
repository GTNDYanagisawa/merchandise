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
package de.hybris.platform.commerceservices.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * @author mariusz.donigiewicz
 * 
 */
@UnitTest
public class OrderEntryModifiableCheckerTest
{
	private final OrderEntryModifiableChecker checker = new OrderEntryModifiableChecker();

	@Mock
	public AbstractOrderEntryModel model;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGiveAwayFalseBasePriceNull()
	{

		BDDMockito.given(model.getBasePrice()).willReturn(null);
		BDDMockito.given(model.getGiveAway()).willReturn(Boolean.FALSE);

		Assert.assertTrue(checker.canModify(model));
	}

	@Test
	public void testGiveAwayFalseBasePriceZero()
	{

		BDDMockito.given(model.getBasePrice()).willReturn(Double.valueOf(0.0));
		BDDMockito.given(model.getGiveAway()).willReturn(Boolean.FALSE);

		Assert.assertTrue(checker.canModify(model));
	}

	@Test
	public void testGiveAwayFalseBasePriceNotNull()
	{

		BDDMockito.given(model.getBasePrice()).willReturn(Double.valueOf(10.0));
		BDDMockito.given(model.getGiveAway()).willReturn(Boolean.FALSE);

		Assert.assertTrue(checker.canModify(model));
	}


	@Test
	public void testGiveAwayTrueBasePriceNull()
	{

		BDDMockito.given(model.getBasePrice()).willReturn(null);
		BDDMockito.given(model.getGiveAway()).willReturn(Boolean.TRUE);

		Assert.assertFalse(checker.canModify(model));
	}

	@Test
	public void testGiveAwayTrueBasePriceZero()
	{

		BDDMockito.given(model.getBasePrice()).willReturn(Double.valueOf(0.0));
		BDDMockito.given(model.getGiveAway()).willReturn(Boolean.TRUE);

		Assert.assertFalse(checker.canModify(model));
	}

	@Test
	public void testGiveAwayTrueBasePriceNotNull()
	{

		BDDMockito.given(model.getBasePrice()).willReturn(Double.valueOf(10.0));
		BDDMockito.given(model.getGiveAway()).willReturn(Boolean.TRUE);

		Assert.assertFalse(checker.canModify(model));
	}


	@Test
	public void testGiveAwayNullBasePriceNull()
	{

		BDDMockito.given(model.getBasePrice()).willReturn(null);
		BDDMockito.given(model.getGiveAway()).willReturn(null);

		Assert.assertTrue(checker.canModify(model));
	}

	@Test
	public void testGiveAwayNullBasePriceZero()
	{

		BDDMockito.given(model.getBasePrice()).willReturn(Double.valueOf(0.0));
		BDDMockito.given(model.getGiveAway()).willReturn(null);

		Assert.assertTrue(checker.canModify(model));
	}

	@Test
	public void testGiveAwayNullBasePriceNotNull()
	{

		BDDMockito.given(model.getBasePrice()).willReturn(Double.valueOf(10.0));
		BDDMockito.given(model.getGiveAway()).willReturn(null);

		Assert.assertTrue(checker.canModify(model));
	}

}
