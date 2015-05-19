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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.integration.cis.tax.CisTaxDocOrder;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.cis.api.tax.model.CisTaxDoc;
import com.hybris.cis.api.tax.model.CisTaxLine;


/**
 *
 *
 */
@UnitTest
public class DefaultCisShippingIncludedStrategyTest
{
	private DefaultCisShippingIncludedStrategy defaultCisShippingIncludedStrategy;

	@Before
	public void setUp()
	{
		defaultCisShippingIncludedStrategy = new DefaultCisShippingIncludedStrategy();
	}

	@Test
	public void shouldNotIncludeShipping()
	{
		final CisTaxDocOrder taxDocOrder = mock(CisTaxDocOrder.class);

		final AbstractOrderModel abstractOrder = mock(AbstractOrderModel.class);
		final List<AbstractOrderEntryModel> orderEntries = mock(ArrayList.class);
		final CisTaxDoc taxDoc = mock(CisTaxDoc.class);
		final List<CisTaxLine> taxLines = mock(ArrayList.class);

		given(Integer.valueOf(taxLines.size())).willReturn(Integer.valueOf(2));
		given(taxDoc.getLineItems()).willReturn(taxLines);
		given(taxDocOrder.getTaxDoc()).willReturn(taxDoc);
		given(Integer.valueOf(orderEntries.size())).willReturn(Integer.valueOf(2));
		given(abstractOrder.getEntries()).willReturn(orderEntries);
		given(taxDocOrder.getAbstractOrder()).willReturn(abstractOrder);

		Assert.assertFalse(defaultCisShippingIncludedStrategy.isShippingIncluded(taxDocOrder));
	}

	@Test
	public void shouldIncludeShipping()
	{
		final CisTaxDocOrder taxDocOrder = mock(CisTaxDocOrder.class);

		final AbstractOrderModel abstractOrder = mock(AbstractOrderModel.class);
		final List<AbstractOrderEntryModel> orderEntries = mock(ArrayList.class);
		final CisTaxDoc taxDoc = mock(CisTaxDoc.class);
		final List<CisTaxLine> taxLines = mock(ArrayList.class);

		given(Integer.valueOf(taxLines.size())).willReturn(Integer.valueOf(3));
		given(taxDoc.getLineItems()).willReturn(taxLines);
		given(taxDocOrder.getTaxDoc()).willReturn(taxDoc);
		given(Integer.valueOf(orderEntries.size())).willReturn(Integer.valueOf(2));
		given(abstractOrder.getEntries()).willReturn(orderEntries);
		given(taxDocOrder.getAbstractOrder()).willReturn(abstractOrder);

		Assert.assertTrue(defaultCisShippingIncludedStrategy.isShippingIncluded(taxDocOrder));
	}
}
