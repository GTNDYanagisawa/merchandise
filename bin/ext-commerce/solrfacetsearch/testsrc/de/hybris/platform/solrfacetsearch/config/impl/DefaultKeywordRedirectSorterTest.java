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
package de.hybris.platform.solrfacetsearch.config.impl;

import de.hybris.platform.solrfacetsearch.enums.KeywordRedirectMatchType;
import de.hybris.platform.solrfacetsearch.model.redirect.SolrAbstractKeywordRedirectModel;
import de.hybris.platform.solrfacetsearch.model.redirect.SolrFacetSearchKeywordRedirectModel;
import de.hybris.platform.solrfacetsearch.model.redirect.SolrURIRedirectModel;
import de.hybris.platform.solrfacetsearch.search.impl.DefaultKeywordRedirectSorter;
import de.hybris.platform.solrfacetsearch.search.impl.KeywordLengthComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;


public class DefaultKeywordRedirectSorterTest
{

	DefaultKeywordRedirectSorter defaultKeywordRedirectSorter = new DefaultKeywordRedirectSorter();

	@Before
	public void setUp() throws Exception
	{
		final List<KeywordRedirectMatchType> sortOrder = new ArrayList<KeywordRedirectMatchType>();
		sortOrder.add(KeywordRedirectMatchType.EXACT);
		sortOrder.add(KeywordRedirectMatchType.REGEX);
		sortOrder.add(KeywordRedirectMatchType.STARTS_WITH);
		sortOrder.add(KeywordRedirectMatchType.ENDS_WITH);
		//sortOrder.add(KeywordRedirectMatchType.CONTAINS); to test if non complete list is also good handled
		defaultKeywordRedirectSorter.setSortOrder(sortOrder);
	}

	/**
	 * Test method for {@link DefaultKeywordRedirectSorter#sort(List)}.
	 */
	@Test
	public void testSort()
	{
		final SolrURIRedirectModel redir = new SolrURIRedirectModel();

		final List<SolrFacetSearchKeywordRedirectModel> toSort = new ArrayList<SolrFacetSearchKeywordRedirectModel>();
		final SolrFacetSearchKeywordRedirectModel val1 = createKeywordRedirect("Ala", KeywordRedirectMatchType.CONTAINS, redir);
		final SolrFacetSearchKeywordRedirectModel val2 = createKeywordRedirect("Ala", KeywordRedirectMatchType.ENDS_WITH, redir);
		final SolrFacetSearchKeywordRedirectModel val3 = createKeywordRedirect("Ala", KeywordRedirectMatchType.STARTS_WITH, redir);
		final SolrFacetSearchKeywordRedirectModel val4 = createKeywordRedirect("Ala", KeywordRedirectMatchType.REGEX, redir);
		final SolrFacetSearchKeywordRedirectModel val5 = createKeywordRedirect("Ala", KeywordRedirectMatchType.EXACT, redir);

		toSort.add(val1);
		toSort.add(val2);
		toSort.add(val3);
		toSort.add(val4);
		toSort.add(val5);

		Assertions.assertThat(toSort).containsSequence(val1, val2, val3, val4, val5);

		final List<SolrFacetSearchKeywordRedirectModel> result = defaultKeywordRedirectSorter.sort(toSort);

		Assertions.assertThat(result).containsSequence(val5, val4, val3, val2, val1);
	}

	SolrFacetSearchKeywordRedirectModel createKeywordRedirect(final String keyword, final KeywordRedirectMatchType matchType,
			final SolrAbstractKeywordRedirectModel redirect)
	{
		final SolrFacetSearchKeywordRedirectModel result = new SolrFacetSearchKeywordRedirectModel();
		result.setKeyword(keyword.trim());
		result.setMatchType(matchType);
		result.setRedirect(redirect);
		return result;
	}

	@Test
	public void testSortByKeywordLength()
	{
		final SolrURIRedirectModel redir = new SolrURIRedirectModel();

		final List<Comparator<SolrFacetSearchKeywordRedirectModel>> comparatorList = new ArrayList<Comparator<SolrFacetSearchKeywordRedirectModel>>();
		comparatorList.add(new KeywordLengthComparator());
		defaultKeywordRedirectSorter.setComparatorList(comparatorList);

		final List<SolrFacetSearchKeywordRedirectModel> toSort = new ArrayList<SolrFacetSearchKeywordRedirectModel>();
		final SolrFacetSearchKeywordRedirectModel val1 = createKeywordRedirect("jean", KeywordRedirectMatchType.CONTAINS, redir);
		final SolrFacetSearchKeywordRedirectModel val2 = createKeywordRedirect("jeans", KeywordRedirectMatchType.CONTAINS, redir);
		final SolrFacetSearchKeywordRedirectModel val3 = createKeywordRedirect("je", KeywordRedirectMatchType.EXACT, redir);
		final SolrFacetSearchKeywordRedirectModel val4 = createKeywordRedirect("jea", KeywordRedirectMatchType.EXACT, redir);

		toSort.add(val1);
		toSort.add(val2);
		toSort.add(val3);
		toSort.add(val4);

		Assertions.assertThat(toSort).containsSequence(val1, val2, val3, val4);

		final List<SolrFacetSearchKeywordRedirectModel> result = defaultKeywordRedirectSorter.sort(toSort);

		Assertions.assertThat(result).containsSequence(val4, val3, val2, val1);
	}

}
