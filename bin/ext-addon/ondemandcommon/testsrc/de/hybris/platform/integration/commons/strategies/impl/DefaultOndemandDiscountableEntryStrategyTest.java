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
 */
package de.hybris.platform.integration.commons.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.integration.commons.constants.OndemandcommonConstants;
import de.hybris.platform.integration.commons.strategies.OndemandDiscountableEntryStrategy;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;


@IntegrationTest
public class DefaultOndemandDiscountableEntryStrategyTest extends ServicelayerTest
{
	private OndemandDiscountableEntryStrategy ondemandDiscountableEntryStrategy;

	private List<AbstractOrderEntryModel> abstractOrderEntries;

	@Before
	public void inittest()
	{
		Config.setParameter(OndemandcommonConstants.NON_DISCOUNTABLE_PRODUCTS, "12345, 123457");
		ondemandDiscountableEntryStrategy = new DefaultOndemandDiscountableEntryStrategy();
		stubEntries();
	}

	public void stubEntries()
	{
		abstractOrderEntries = new ArrayList<AbstractOrderEntryModel>();
		final AbstractOrderEntryModel lineOne = Mockito.mock(AbstractOrderEntryModel.class);
		final ProductModel productOne = Mockito.mock(ProductModel.class);
		given(productOne.getCode()).willReturn("12345");
		given(lineOne.getProduct()).willReturn(productOne);

		final AbstractOrderEntryModel lineTwo = Mockito.mock(AbstractOrderEntryModel.class);
		final ProductModel productTwo = Mockito.mock(ProductModel.class);
		given(productTwo.getCode()).willReturn("123456");
		given(lineTwo.getProduct()).willReturn(productTwo);

		final AbstractOrderEntryModel lineThree = Mockito.mock(AbstractOrderEntryModel.class);
		final ProductModel productThree = Mockito.mock(ProductModel.class);
		given(productThree.getCode()).willReturn("1234567");
		given(lineThree.getProduct()).willReturn(productThree);

		abstractOrderEntries.add(lineOne);
		abstractOrderEntries.add(lineTwo);
		abstractOrderEntries.add(lineThree);
	}

	@Test
	public void shouldReturnDiscountableEntries()
	{
		final List<AbstractOrderEntryModel> discountableEntries = ondemandDiscountableEntryStrategy
				.getDiscountableEntries(abstractOrderEntries);

		Assert.notEmpty(discountableEntries);
		Assert.isTrue(discountableEntries.size() == 2);
		Assert.isTrue(discountableEntries.get(0).equals(abstractOrderEntries.get(1)));
	}

	@Test
	public void shouldReturnNonDiscountableEntries()
	{
		final List<AbstractOrderEntryModel> discountableEntries = ondemandDiscountableEntryStrategy
				.getNonDiscountableEntries(abstractOrderEntries);

		Assert.notEmpty(discountableEntries);
		Assert.isTrue(discountableEntries.size() == 1);
		Assert.isTrue(discountableEntries.get(0).equals(abstractOrderEntries.get(0)));
	}
}
