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
package de.hybris.platform.accountsummaryaddon.document.service.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mockito;

import de.hybris.platform.accountsummaryaddon.document.AccountSummaryDocumentQuery;
import de.hybris.platform.accountsummaryaddon.document.B2BDocumentQueryBuilder;
import de.hybris.platform.accountsummaryaddon.document.dao.B2BDocumentDao;
import de.hybris.platform.accountsummaryaddon.document.dao.PagedB2BDocumentDao;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;

public class DefaultB2BDocumentServiceMockTest extends HybrisMokitoTest
{

	@Test
	public void shouldReturnPagedDocumentResult()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.AMOUNT, true).build();
		final B2BDocumentModel document = mock(B2BDocumentModel.class);
		final PagedB2BDocumentDao pagedB2BDocumentDao = mock(PagedB2BDocumentDao.class);
		final SearchPageData<B2BDocumentModel> result = new SearchPageData<B2BDocumentModel>();
		result.setResults(Arrays.asList(document));

		when(pagedB2BDocumentDao.findDocuments(Mockito.any(AccountSummaryDocumentQuery.class))).thenReturn(result);
		when(document.getDocumentNumber()).thenReturn("PUR-001");

		final DefaultB2BDocumentService defaultB2BDocumentService = new DefaultB2BDocumentService();
		defaultB2BDocumentService.setPagedB2BDocumentDao(pagedB2BDocumentDao);

		final SearchPageData<B2BDocumentModel> finalResult = defaultB2BDocumentService.findDocuments(query);

		TestCase.assertEquals(1, finalResult.getResults().size());
		TestCase.assertEquals("PUR-001", finalResult.getResults().get(0).getDocumentNumber());

		verify(pagedB2BDocumentDao, Mockito.times(1)).findDocuments(query);
	}

	@Test
	public void shouldResultOpenDocuments()
	{
		final B2BDocumentModel document = mock(B2BDocumentModel.class);
		final SearchResult<B2BDocumentModel> value = new SearchResultImpl<>(Arrays.asList(document), 1, 1, 1);

		final B2BUnitModel unit = mock(B2BUnitModel.class);
		final B2BDocumentDao b2bDocumentDao = mock(B2BDocumentDao.class);

		when(b2bDocumentDao.getOpenDocuments(unit)).thenReturn(value);

		final DefaultB2BDocumentService defaultB2BDocumentService = new DefaultB2BDocumentService();
		defaultB2BDocumentService.setB2bDocumentDao(b2bDocumentDao);

		final SearchResult<B2BDocumentModel> result = defaultB2BDocumentService.getOpenDocuments(unit);

		TestCase.assertEquals(1, result.getTotalCount());

		verify(b2bDocumentDao, Mockito.times(1)).getOpenDocuments(unit);
	}
}
