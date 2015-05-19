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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.GetAllStrategy;

import junit.framework.TestCase;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class StrategyFactoryERP604Test extends TestCase
{

	private final StrategyFactoryERP604 classUnderTest = new StrategyFactoryERP604();

	@Test
	public void testCreateGetAllStrategy()
	{
		final GetAllStrategy factory = classUnderTest.createGetAllStrategy();

		assertNotNull(factory);
		assertEquals(GetAllStrategyERP604.class.getName(), factory.getClass().getName());
	}

}
