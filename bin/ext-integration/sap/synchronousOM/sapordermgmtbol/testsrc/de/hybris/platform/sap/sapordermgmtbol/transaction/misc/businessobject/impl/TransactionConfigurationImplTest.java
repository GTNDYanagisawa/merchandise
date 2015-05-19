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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class TransactionConfigurationImplTest extends SapordermanagmentBolSpringJunitTest
{
	TransactionConfigurationImpl classUnderTest = null;

	@Before
	public void init()
	{
		classUnderTest = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_TRANSACTION_CONFIGURATION);
		LocaleUtil.setLocale(Locale.US);
	}

	@Test
	public void testInstantiation()
	{
		assertNotNull(classUnderTest);
	}

	@Test
	public void testMergeIdenticalProducts()
	{
		assertTrue(classUnderTest.isMergeIdenticalProducts());
	}


	@Test
	public void testHeaderTextID() throws BackendException
	{
		final String id = "A";
		classUnderTest.setHeaderTextID(id);
		assertEquals(id, classUnderTest.getHeaderTextID());
	}

	@Test
	public void testItemTextID() throws BackendException
	{
		final String id = "A";
		classUnderTest.setItemTextID(id);
		assertEquals(id, classUnderTest.getItemTextID());
	}

	@Test
	public void testGetLanguageISO()
	{
		{
			assertEquals("en", classUnderTest.getLanguageIso());
		}
	}
}
