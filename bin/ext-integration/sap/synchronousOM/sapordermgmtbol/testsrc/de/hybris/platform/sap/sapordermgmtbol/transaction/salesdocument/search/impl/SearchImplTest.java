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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.backend.interf.SearchBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResultList;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class SearchImplTest
{

	SearchImpl classUnderTest = new SearchImpl();
	SearchFilter searchFilter = new SearchFilterImpl();
	private String firstValidCode;

	@Before
	public void init() throws BackendException
	{
		final SearchBackend searchBackend = EasyMock.createMock(SearchBackend.class);
		final SearchResultList searchResult = new SearchResultListImpl();
		final SearchResult result = new SearchResultImpl();
		searchResult.add(result);
		EasyMock.expect(searchBackend.getSearchResult(searchFilter)).andReturn(searchResult);
		EasyMock.replay(searchBackend);
		classUnderTest.setBackendObject(searchBackend);
	}

	@Test
	public void testSearchThenClean()
	{
		final PageableData pageableData = new PageableData();
		final List<SearchResult> searchResult = classUnderTest.getSearchResult(searchFilter, pageableData);
		assertNotNull(searchResult);
		assertFalse(classUnderTest.isDirty());
	}

	@Test
	public void testSearchSubsequentSearches()
	{
		final PageableData pageableData = new PageableData();
		final List<SearchResult> searchResult = classUnderTest.getSearchResult(searchFilter, pageableData);
		assertNotNull(searchResult);
		//the test: only one call of backend, see init method
		classUnderTest.getSearchResult(searchFilter, pageableData);
	}

	@Test
	public void testDirtyInitial()
	{
		assertTrue(classUnderTest.isDirty());
	}

	@Test
	public void testSetDirty()
	{
		classUnderTest.setDirty(false);
		assertFalse(classUnderTest.isDirty());
	}

	@Test
	public void testTotalNumberOfResultInitial()
	{
		final int size = classUnderTest.getSearchResultsTotalNumber();
		assertTrue(size == 0);
	}

	@Test
	public void testTotalNumberOfResult()
	{
		final PageableData pageableData = new PageableData();
		classUnderTest.getSearchResult(searchFilter, pageableData);
		assertTrue(classUnderTest.getSearchResultsTotalNumber() > 0);
	}

	@Test
	public void testGetValidSortOptions()
	{
		final List<SortData> validSortOptions = classUnderTest.getValidSortOptions();
		assertNotNull(validSortOptions);
		assertEquals(2, validSortOptions.size());
	}

	@Test
	public void testCheckedSortOptionsNothingSelected()
	{
		final PageableData pageableData = new PageableData();
		final List<SortData> setCheckedSortOption = classUnderTest.getCheckedSortOption(pageableData);
		assertNotNull(setCheckedSortOption);
		assertEquals(2, setCheckedSortOption.size());
		for (final SortData sortData : setCheckedSortOption)
		{
			assertFalse(sortData.isSelected());
		}

	}

	@Test
	public void testCheckedSortOptionsOneSelected()
	{
		final PageableData pageableData = new PageableData();
		firstValidCode = classUnderTest.getValidSortOptions().get(0).getCode();
		pageableData.setSort(firstValidCode);
		final List<SortData> setCheckedSortOption = classUnderTest.getCheckedSortOption(pageableData);
		assertNotNull(setCheckedSortOption);
		assertEquals(2, setCheckedSortOption.size());
		checkOneSelected(setCheckedSortOption);
	}

	@Test
	public void testCheckedSortOptionsEmptyWontOverride()
	{
		testCheckedSortOptionsOneSelected();
		//As no new sort attribute is passed, we still expect the existing one to be 
		//persistent
		final PageableData pageableData = new PageableData();
		final List<SortData> setCheckedSortOption = classUnderTest.getCheckedSortOption(pageableData);
		assertNotNull(setCheckedSortOption);
		assertEquals(2, setCheckedSortOption.size());
		checkOneSelected(setCheckedSortOption);

		//even if we didn't provide pageable data's sort option, it will 
		//be restored from session
		assertEquals(firstValidCode, pageableData.getSort());
	}


	void checkOneSelected(final List<SortData> setCheckedSortOption)
	{
		boolean oneIsSelected = false;
		for (final SortData sortData : setCheckedSortOption)
		{
			oneIsSelected = oneIsSelected || sortData.isSelected();
		}
		assertTrue(oneIsSelected);
	}

	@Test
	public void testSetSortOptions()
	{
		final List<SortData> sortData = new ArrayList<>();
		classUnderTest.setSortOptions(sortData);
		assertEquals(sortData, classUnderTest.getSortOptions());
	}


}
