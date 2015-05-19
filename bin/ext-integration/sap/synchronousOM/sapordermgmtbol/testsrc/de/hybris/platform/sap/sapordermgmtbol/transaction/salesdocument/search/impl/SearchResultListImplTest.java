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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.junit.Test;



@UnitTest
@SuppressWarnings("javadoc")
public class SearchResultListImplTest
{
	SearchResultListImpl classUnderTest = new SearchResultListImpl();

	@Test
	public void testAdd()
	{
		addNewItem("A");
		final int size = classUnderTest.size();
		assertEquals(size, 1);
	}

	private void addNewItem(final String key)
	{
		final SearchResult result = new SearchResultImpl();
		result.setKey(new TechKey(key));
		result.setCreationDate(new Date());
		classUnderTest.add(result);
	}

	private void addNewItem(final String key, final Date date)
	{
		final SearchResult result = new SearchResultImpl();
		result.setKey(new TechKey(key));
		result.setCreationDate(date);
		classUnderTest.add(result);
	}

	@Test
	public void testClear()
	{
		addNewItem("A");
		classUnderTest.clear();
		final int size = classUnderTest.size();
		assertEquals(size, 0);
	}

	@Test
	public void testSetPageableData()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		classUnderTest.setPageableData(pageableData);
		assertEquals(pageableData, classUnderTest.getPageableData());
	}

	@Test
	public void testPaging()
	{
		add5Items();
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(3);
		classUnderTest.setPageableData(pageableData);
		final List<SearchResult> searchResult = classUnderTest.getSearchResult();
		assertEquals(searchResult.size(), 3);
		checkFirstItemOnPage(searchResult, "E");
	}

	private void checkFirstItemOnPage(final List<SearchResult> searchResult, final String key)
	{
		final SearchResult firstItemOnPage = searchResult.get(0);
		assertEquals(key, firstItemOnPage.getKey().getIdAsString());
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testPagingWoPageableData()
	{
		add5Items();
		classUnderTest.getSearchResult();
	}

	@Test
	public void testPagingSizeBiggerThanResult()
	{
		add5Items();
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(6);
		classUnderTest.setPageableData(pageableData);
		final List<SearchResult> searchResult = classUnderTest.getSearchResult();
		assertEquals(searchResult.size(), 5);
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testPagingSizePageSizeNoMatch()
	{
		add5Items();
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(2);
		pageableData.setPageSize(3);
		classUnderTest.setPageableData(pageableData);
		classUnderTest.getSearchResult();
	}

	@Test
	public void testPagingSubsequentPage()
	{
		add5Items();
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(1);
		pageableData.setPageSize(3);
		classUnderTest.setPageableData(pageableData);
		final List<SearchResult> searchResult = classUnderTest.getSearchResult();
		assertEquals(searchResult.size(), 2);
		checkFirstItemOnPage(searchResult, "B");

	}

	@Test
	public void testSorting()
	{
		try
		{
			add5ItemsWithCreatingDate();
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
		}

		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(1);
		pageableData.setPageSize(3);
		classUnderTest.setPageableData(pageableData);
		List<SearchResult> searchResult = classUnderTest.getSearchResult();
		assertEquals(searchResult.size(), 2);
		checkFirstItemOnPage(searchResult, "B");

		pageableData.setCurrentPage(0);
		pageableData.setPageSize(3);
		classUnderTest.setPageableData(pageableData);
		searchResult = classUnderTest.getSearchResult();
		assertEquals(searchResult.size(), 3);
		checkFirstItemOnPage(searchResult, "E");

		pageableData.setCurrentPage(2);
		pageableData.setPageSize(2);
		classUnderTest.setPageableData(pageableData);
		searchResult = classUnderTest.getSearchResult();
		assertEquals(searchResult.size(), 1);
		checkFirstItemOnPage(searchResult, "A");
	}

	/**
	 * 
	 */
	private void add5Items()
	{
		addNewItem("A");
		addNewItem("B");
		addNewItem("C");
		addNewItem("D");
		addNewItem("E");
	}

	private void add5ItemsWithCreatingDate() throws ParseException
	{
		final DateFormat format = DateFormat.getDateInstance();
		addNewItem("A", format.parse("01.01.2014"));
		addNewItem("B", format.parse("02.01.2014"));
		addNewItem("C", format.parse("03.01.2014"));
		addNewItem("D", format.parse("04.01.2014"));
		addNewItem("E", format.parse("05.01.2014"));
	}
}
