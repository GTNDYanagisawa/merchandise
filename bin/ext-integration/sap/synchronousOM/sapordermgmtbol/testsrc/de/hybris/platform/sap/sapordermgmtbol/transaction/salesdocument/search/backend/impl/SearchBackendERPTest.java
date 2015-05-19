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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.backend.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.core.module.impl.ModuleConfigurationAccessImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl.SearchResultImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl.SearchResultListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResultList;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class SearchBackendERPTest extends SapordermanagmentBolSpringJunitTest
{

	SearchBackendERP classUnderTest = new SearchBackendERP();




	@Before
	public void init() throws BackendException
	{
		classUnderTest.setGenericFactory(genericFactory);
	}



	public void testConnection()
	{
		final JCoConnection connection = classUnderTest.getDefaultJCoConnection();
		assertNotNull(connection);
	}


	@Test
	public void testMaxHits()
	{
		final int maxHits = 100;
		classUnderTest.setMaxHits(maxHits);
		assertEquals(maxHits, classUnderTest.getMaxHits());
	}

	@Test
	public void testDateRange()
	{
		final int dateRange = 30;
		classUnderTest.setDateRangeInDays(dateRange);
		assertEquals(dateRange, classUnderTest.getDateRangeInDays());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDateLow()
	{
		final Date date = new Date(114, 11, 15);
		System.out.println("Today's date: " + date);
		classUnderTest.setToday(date);
		final int dateRange = 30;
		classUnderTest.setDateRangeInDays(dateRange);
		final String dateAsString = classUnderTest.getDateLowAsString();
		assertNotNull(dateAsString);
		System.out.println("Date: " + dateAsString);
		assertEquals("2014", dateAsString.substring(0, 4));
		assertEquals("11", dateAsString.substring(4, 6));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDateLowFeb()
	{
		final Date date = new Date(114, 2, 1);
		System.out.println("Today's date: " + date);
		classUnderTest.setToday(date);
		final int dateRange = 1;
		classUnderTest.setDateRangeInDays(dateRange);
		final String dateAsString = classUnderTest.getDateLowAsString();
		assertNotNull(dateAsString);
		System.out.println("Date: " + dateAsString);
		assertEquals("2014", dateAsString.substring(0, 4));
		assertEquals("02", dateAsString.substring(4, 6));
		assertEquals("28", dateAsString.substring(6, 8));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDateHigh()
	{
		final Date date = new Date(114, 2, 1);
		System.out.println("Today's date: " + date);
		classUnderTest.setToday(date);
		final String dateAsString = classUnderTest.getDateTodayAsString();
		System.out.println("Today: " + dateAsString);
		assertEquals("2014", dateAsString.substring(0, 4));
		assertEquals("03", dateAsString.substring(4, 6));
		assertEquals("01", dateAsString.substring(6, 8));
	}

	@Test
	public void testCreateSearchResult()
	{
		assertNotNull(classUnderTest.createSearchResult());
	}

	@Test
	public void testCreateSearchResultList()
	{
		assertNotNull(classUnderTest.createSearchResultList());
	}

	@Test
	public void testConfigurationAccess()
	{
		final ModuleConfigurationAccess configurationAccess = new ModuleConfigurationAccessImpl();
		classUnderTest.setModuleConfigurationAccess(configurationAccess);
	}

	@Test
	public void testNoWarningMessage()
	{
		SearchResultList results = null;

		results = createResultList100();

		final int maxHits = 200;
		final boolean isWarning = classUnderTest.isWarningStatus(results, maxHits);
		assertFalse(isWarning);
	}

	@Test
	public void testNoErrorMessage()
	{
		SearchResultList results = null;

		results = createResultList100();

		final int maxHits = 200;
		final boolean isError = classUnderTest.isErrorStatus(results, maxHits);
		assertFalse(isError);
	}

	@Test
	public void testWarningMessage()
	{
		SearchResultList results = null;

		results = createResultList100();

		final int maxHits = 105;
		final boolean isWarning = classUnderTest.isWarningStatus(results, maxHits);
		assertTrue(isWarning);
	}

	@Test
	public void testPerformLogging()
	{
		SearchResultList results = null;

		results = createResultList100();

		final int maxHits = 105;
		classUnderTest.performLogging(results, maxHits);

	}

	private SearchResultList createResultList100()
	{
		SearchResultList results;
		results = new SearchResultListImpl();
		final SearchResult result = new SearchResultImpl();
		for (int i = 0; i < 100; i++)
		{
			results.add(result);
		}
		return results;
	}

}
