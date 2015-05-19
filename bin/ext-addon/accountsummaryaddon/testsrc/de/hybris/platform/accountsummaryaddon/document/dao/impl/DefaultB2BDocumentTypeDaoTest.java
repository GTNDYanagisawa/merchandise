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
package de.hybris.platform.accountsummaryaddon.document.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.accountsummaryaddon.B2BIntegrationTest;
import de.hybris.platform.accountsummaryaddon.document.dao.B2BDocumentTypeDao;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;

@IntegrationTest
public class DefaultB2BDocumentTypeDaoTest extends B2BIntegrationTest
{

	@Resource
	private B2BDocumentTypeDao b2bDocumentTypeDao;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/accountsummaryaddon/test/testB2BDocumentType.csv", "utf-8");
	}

	@Test
	public void shouldReturnAllDocumentType()
	{
		final SearchResult<B2BDocumentTypeModel> result = b2bDocumentTypeDao.getAllDocumentTypes();

		TestCase.assertEquals(5, result.getTotalCount());

		final Set<String> resultSet = new HashSet();
		for (final B2BDocumentTypeModel each : result.getResult())
		{
			resultSet.add(each.getCode());
		}

		TestCase.assertTrue(resultSet.contains("Purchase Order"));
		TestCase.assertTrue(resultSet.contains("Invoice"));
		TestCase.assertTrue(resultSet.contains("Credit Note"));
		TestCase.assertTrue(resultSet.contains("Debit Note"));
		TestCase.assertTrue(resultSet.contains("Statement"));
	}
}
