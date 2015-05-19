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
package de.hybris.platform.sap.sapordermgmtbol.module.transaction.item.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.AlternativeProductImpl;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

/**
 * 
 */
@UnitTest
@SuppressWarnings("javadoc")
public class AlternativeProductImplTest extends
		SapordermanagmentBolSpringJunitTest {

	protected AlternativeProductImpl classUnderTest;

	@Override
	@Before
	public void setUp() {
		classUnderTest = (AlternativeProductImpl) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ALTERNATIVE_PRODUCT);
	}

	@Test
	public void testConstructor() {
		assertNotNull(classUnderTest);
	}

	@Test
	public void testAttributes() {
		final String attribute = "A";
		classUnderTest.setDescription(attribute);
		assertEquals(attribute, classUnderTest.getDescription());
		classUnderTest.setEnteredProductIdType(attribute);
		assertEquals(attribute, classUnderTest.getEnteredProductIdType());
		classUnderTest.setSubstitutionReasonId(attribute);
		assertFalse(classUnderTest.isSubstituteProduct());
		assertTrue(classUnderTest.isDeterminationProduct());
	}

	@Test
	public void testClone() {
		assertNotNull(classUnderTest.clone());
	}

}
