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
package de.hybris.platform.solrfacetsearch.search;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.enums.KeywordRedirectMatchType;
import de.hybris.platform.solrfacetsearch.handler.KeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.handler.impl.DefaultContainsKeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.handler.impl.DefaultEndsWithKeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.handler.impl.DefaultExactKeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.handler.impl.DefaultRegexKeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.handler.impl.DefaultStartsWithKeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.model.redirect.SolrAbstractKeywordRedirectModel;
import de.hybris.platform.solrfacetsearch.model.redirect.SolrFacetSearchKeywordRedirectModel;
import de.hybris.platform.solrfacetsearch.search.impl.DefaultSolrKeywordRedirectService;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class SolrKeywordRedirectTest
{
	@Mock
	private SolrSearchResult searchResult;
	@Mock
	private SearchQuery searchQuery;
	@Mock
	private FacetSearchConfig facetSearchConfig;
	@Mock
	private SolrFacetSearchKeywordDao solrFacetSearchKeywordDao;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private KeywordRedirectSorter keywordRedirectSorter;
	@InjectMocks
	private final DefaultSolrKeywordRedirectService solrKeywordRedirectService = new DefaultSolrKeywordRedirectService();

	private final LanguageModel lang = new LanguageModel();


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		searchResult = new SolrSearchResult(0, null, null, null, null, 0, 0, null, searchQuery, null);
		lang.setIsocode("de");

		when(commonI18NService.getCurrentLanguage()).thenReturn(lang);
		when(searchQuery.getFacetSearchConfig()).thenReturn(facetSearchConfig);

		final HashMap<KeywordRedirectMatchType, KeywordRedirectHandler> map = new HashMap<KeywordRedirectMatchType, KeywordRedirectHandler>();
		map.put(KeywordRedirectMatchType.CONTAINS, new DefaultContainsKeywordRedirectHandler());
		map.put(KeywordRedirectMatchType.ENDS_WITH, new DefaultEndsWithKeywordRedirectHandler());
		map.put(KeywordRedirectMatchType.EXACT, new DefaultExactKeywordRedirectHandler());
		map.put(KeywordRedirectMatchType.REGEX, new DefaultRegexKeywordRedirectHandler());
		map.put(KeywordRedirectMatchType.STARTS_WITH, new DefaultStartsWithKeywordRedirectHandler());

		solrKeywordRedirectService.setRedirectHandlers(map);
		when(keywordRedirectSorter.sort(Mockito.<List<SolrFacetSearchKeywordRedirectModel>> any())).thenAnswer(new Answer()
		{
			@Override
			public List<KeywordRedirectValue> answer(final InvocationOnMock invocation)
			{
				final Object[] args = invocation.getArguments();
				return (List<KeywordRedirectValue>) args[0];
			}
		});
	}

	@Test
	public void testProcess()
	{
		solrKeywordRedirectService.attachKeywordRedirect(searchResult);
		assertThat(searchResult.getKeywordRedirects()).isEmpty();

		defaultTestSingleMatch(KeywordRedirectMatchType.EXACT, "fullMatch", "fullMatch");
		defaultTestSingleMatch(KeywordRedirectMatchType.STARTS_WITH, "startsWith", "startsWithWithSomethingAfter");
		defaultTestSingleMatch(KeywordRedirectMatchType.ENDS_WITH, "startsWith", "endsWithstartsWith");
		defaultTestSingleMatch(KeywordRedirectMatchType.CONTAINS, "contains", "start contains end");
		defaultTestSingleMatch(KeywordRedirectMatchType.REGEX, "a[bc]{2,3}.+e", "abcc-e");
	}

	private void defaultTestSingleMatch(final KeywordRedirectMatchType matchMode, final String matchWord,
			final String fullQueryText)
	{
		searchResult.setKeywordRedirects(Collections.<KeywordRedirectValue> emptyList());
		final List<SolrFacetSearchKeywordRedirectModel> matches = new ArrayList<SolrFacetSearchKeywordRedirectModel>();
		final SolrFacetSearchKeywordRedirectModel redirect = new SolrFacetSearchKeywordRedirectModel();
		final SolrAbstractKeywordRedirectModel redirectValue = prepareRedirect(redirect, matchWord, matchMode);
		final SolrFacetSearchKeywordRedirectModel redirectNoMatch = new SolrFacetSearchKeywordRedirectModel();
		prepareRedirect(redirectNoMatch, "noMatchMatch", matchMode);
		matches.add(redirect);
		matches.add(redirectNoMatch);
		when(solrFacetSearchKeywordDao.findKeywords(searchQuery.getFacetSearchConfig().getName(), lang.getIsocode())).thenReturn(
				matches);
		//when(solrKeywordRedirectService.findKeywordRedirects(searchQuery)).thenReturn(matches);
		when(searchQuery.getUserQuery()).thenReturn(fullQueryText);
		solrKeywordRedirectService.attachKeywordRedirect(searchResult);
		assertThat(searchResult.getKeywordRedirects().size()).isEqualTo(1);
		for (final KeywordRedirectValue value : searchResult.getKeywordRedirects())
		{
			assertThat(value.getRedirect() == redirectValue);
		}
	}

	private SolrAbstractKeywordRedirectModel prepareRedirect(final SolrFacetSearchKeywordRedirectModel redirect,
			final String keyword, final KeywordRedirectMatchType match)
	{
		final SolrAbstractKeywordRedirectModel redirectValue = mock(SolrAbstractKeywordRedirectModel.class);
		redirect.setMatchType(match);
		redirect.setKeyword(keyword.trim());
		redirect.setIgnoreCase(Boolean.TRUE);
		redirect.setRedirect(redirectValue);
		return redirectValue;
	}

}
