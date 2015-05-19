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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.testframework.Assert;

import java.math.BigDecimal;

import org.junit.Test;


@UnitTest
public class PriceModelImplTest
{
	private final PriceModel model = new PriceModelImpl();

	@Test
	public void testPriceTest()
	{
		final String currency = "USD";
		final BigDecimal priceValue = BigDecimal.ONE;

		model.setCurrency(currency);
		model.setPriceValue(priceValue);

		assertEquals(currency, model.getCurrency());
		assertEquals(priceValue, model.getPriceValue());
	}

	@Test
	public void testClone()
	{
		final PriceModel clone = model.clone();

		final String currency = "USD";
		final BigDecimal priceValue = BigDecimal.ONE;

		model.setCurrency(currency);
		model.setPriceValue(priceValue);

		Assert.assertNotEquals(model.getCurrency(), clone.getCurrency());
		Assert.assertNotEquals(model.getPriceValue(), clone.getPriceValue());
	}


	@Test
	public void testCloneMustBeEquals() throws Exception
	{
		final String currency = "USD";
		final BigDecimal priceValue = BigDecimal.ONE;

		model.setCurrency(currency);
		model.setPriceValue(priceValue);

		final PriceModel clone = model.clone();

		assertEquals("Clone must be equal", model, clone);

	}

	@Test
	public void testCloneMustHaveSomeHashCode() throws Exception
	{
		final String currency = "USD";
		final BigDecimal priceValue = BigDecimal.ONE;

		model.setCurrency(currency);
		model.setPriceValue(priceValue);

		final PriceModel clone = model.clone();

		assertEquals("Clone must be equal", model.hashCode(), clone.hashCode());

	}

}
