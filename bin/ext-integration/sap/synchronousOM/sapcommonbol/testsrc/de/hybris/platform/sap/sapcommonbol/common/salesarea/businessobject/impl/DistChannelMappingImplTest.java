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
package de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class DistChannelMappingImplTest
{
	DistChannelMappingImpl classUnderTest = new DistChannelMappingImpl();
	private final String allowedPricingLevel = "A";
	private final String distChainCategory = "B";
	private final String distChannelForConditions = "C";
	private final String distChannelForCustomerMatirial = "D";
	private final String distChannelForSalesDocTypes = "E";
	private final String referencePlant = "1000";

	@Test
	public void testEqualsInitialObjects()
	{
		final DistChannelMappingImpl compare = new DistChannelMappingImpl();
		final boolean equals = classUnderTest.equals(compare);
		assertTrue(equals);
	}

	@Test
	public void testPricingLevel()
	{
		final DistChannelMappingImpl compare = new DistChannelMappingImpl();
		classUnderTest.setAllowedPricingLevel(allowedPricingLevel);
		assertEquals(allowedPricingLevel, classUnderTest.getAllowedPricingLevel());
		assertFalse(classUnderTest.equals(compare));
		compare.setAllowedPricingLevel(allowedPricingLevel);
		assertTrue(classUnderTest.equals(compare));
		assertEquals(classUnderTest.hashCode(), compare.hashCode());
	}

	@Test
	public void testDistChainCategory()
	{
		final DistChannelMappingImpl compare = new DistChannelMappingImpl();
		classUnderTest.setDistChainCategory(distChainCategory);
		assertEquals(distChainCategory, classUnderTest.getDistChainCategory());
		assertFalse(classUnderTest.equals(compare));
		compare.setDistChainCategory(distChainCategory);
		assertTrue(classUnderTest.equals(compare));
		assertEquals(classUnderTest.hashCode(), compare.hashCode());
	}

	@Test
	public void testDistChannelForConditions()
	{
		final DistChannelMappingImpl compare = new DistChannelMappingImpl();
		classUnderTest.setDistChannelForConditions(distChannelForConditions);
		assertEquals(distChannelForConditions, classUnderTest.getDistChannelForConditions());
		assertFalse(classUnderTest.equals(compare));
		compare.setDistChannelForConditions(distChannelForConditions);
		assertTrue(classUnderTest.equals(compare));
		assertEquals(classUnderTest.hashCode(), compare.hashCode());
	}

	@Test
	public void testDistChannelCustMat()
	{
		final DistChannelMappingImpl compare = new DistChannelMappingImpl();
		classUnderTest.setDistChannelForCustomerMatirial(distChannelForCustomerMatirial);
		assertEquals(distChannelForCustomerMatirial, classUnderTest.getDistChannelForCustomerMatirial());
		assertFalse(classUnderTest.equals(compare));
		compare.setDistChannelForCustomerMatirial(distChannelForCustomerMatirial);
		assertTrue(classUnderTest.equals(compare));
		assertEquals(classUnderTest.hashCode(), compare.hashCode());
	}


	@Test
	public void testDistChannelSalesDocTypes()
	{
		final DistChannelMappingImpl compare = new DistChannelMappingImpl();
		classUnderTest.setDistChannelForSalesDocTypes(distChannelForSalesDocTypes);
		assertEquals(distChannelForSalesDocTypes, classUnderTest.getDistChannelForSalesDocTypes());
		assertFalse(classUnderTest.equals(compare));
		compare.setDistChannelForSalesDocTypes(distChannelForSalesDocTypes);
		assertTrue(classUnderTest.equals(compare));
		assertEquals(classUnderTest.hashCode(), compare.hashCode());
	}

	@Test
	public void testRefPlant()
	{
		final DistChannelMappingImpl compare = new DistChannelMappingImpl();
		classUnderTest.setReferencePlant(referencePlant);
		assertEquals(referencePlant, classUnderTest.getReferencePlant());
		assertFalse(classUnderTest.equals(compare));
		compare.setReferencePlant(referencePlant);
		assertTrue(classUnderTest.equals(compare));
		assertEquals(classUnderTest.hashCode(), compare.hashCode());
	}
}
