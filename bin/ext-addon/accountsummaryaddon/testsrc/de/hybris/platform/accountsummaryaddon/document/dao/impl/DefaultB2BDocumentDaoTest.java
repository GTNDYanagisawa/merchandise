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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.accountsummaryaddon.B2BIntegrationTest;
import de.hybris.platform.accountsummaryaddon.document.dao.B2BDocumentDao;
import de.hybris.platform.accountsummaryaddon.enums.DocumentStatus;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;
import de.hybris.platform.accountsummaryaddon.model.DocumentMediaModel;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

@IntegrationTest
public class DefaultB2BDocumentDaoTest extends B2BIntegrationTest
{

	@Resource
	private B2BDocumentDao b2bDocumentDao;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/accountsummaryaddon/test/testOrganizations.csv", "utf-8");
		importCsv("/accountsummaryaddon/test/testB2bdocument.csv", "utf-8");
	}

	private Set<String> expectedResults;
	private Set<String> actualResults;

	private Set<String> getDocumentMediaCodes(final SearchResult<DocumentMediaModel> documentMediaList)
	{
		final Set<String> result = new HashSet<String>();

		for (final DocumentMediaModel model : documentMediaList.getResult())
		{
			result.add(model.getCode());
		}

		return result;
	}

	@Test
	public void shouldReturnEmptyResultUnitNotExist()
	{
		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid("NOT_EXIST");

		final SearchResult<B2BDocumentModel> result = b2bDocumentDao.getOpenDocuments(unit);

		TestCase.assertEquals(0, result.getTotalCount());
	}

	@Test
	public void shouldReturnEmptyResultNoDocumentOpen()
	{
		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid("Rustic Services");

		final SearchResult<B2BDocumentModel> result = b2bDocumentDao.getOpenDocuments(unit);

		TestCase.assertEquals(0, result.getTotalCount());
	}

	@Test
	public void shouldReturnTwoDocuments()
	{
		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid("Custom Retail");

		final SearchResult<B2BDocumentModel> result = b2bDocumentDao.getOpenDocuments(unit);

		TestCase.assertEquals(2, result.getTotalCount());
		String[] documents = new String[] {
				result.getResult().get(0).getDocumentNumber(), result.getResult().get(1).getDocumentNumber()};
		Arrays.sort(documents);
		TestCase.assertEquals("DBN-002", documents[0]);
		TestCase.assertEquals("PUR-002", documents[1]);
	}

	@Test
	public void shouldReturnManyDocuments()
	{
		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid("Pronto Services");

		final SearchResult<B2BDocumentModel> result = b2bDocumentDao.getOpenDocuments(unit);

		TestCase.assertEquals(2, result.getTotalCount());
	}

	@Test
	public void shouldReturnOldDocumentsByNumberOfDay()
	{
		final SearchResult<DocumentMediaModel> result1 = b2bDocumentDao.findOldDocumentMedia(36500, null, null);

		TestCase.assertEquals(result1.getCount(), 0);

		final SearchResult<DocumentMediaModel> result2 = b2bDocumentDao.findOldDocumentMedia(-1, null, null);

		expectedResults = new HashSet<String>();
		actualResults = getDocumentMediaCodes(result2);

		expectedResults.add("documentMedia1");
		expectedResults.add("documentMedia2");
		expectedResults.add("documentMedia3");



		TestCase.assertTrue(expectedResults.equals(actualResults));

	}

	@Test
	public void shouldReturnOldDocumentsByNumberOfDayAndType()
	{
		final B2BDocumentTypeModel documentType1 = new B2BDocumentTypeModel();
		documentType1.setCode("Invoice");
		final List<B2BDocumentTypeModel> documentTypes = Arrays.asList(flexibleSearchService.getModelByExample(documentType1));

		final SearchResult<DocumentMediaModel> result = b2bDocumentDao.findOldDocumentMedia(-1, documentTypes, null);

		expectedResults = new HashSet<String>();
		actualResults = getDocumentMediaCodes(result);

		expectedResults.add("documentMedia2");

		TestCase.assertTrue(actualResults.equals(expectedResults));
	}

	@Test
	public void shouldReturnOldDocumentsByNumberOfDayAndStatus()
	{
		final List<DocumentStatus> documentStatuses = Arrays.asList(DocumentStatus.CLOSED);

		final SearchResult<DocumentMediaModel> result = b2bDocumentDao.findOldDocumentMedia(-1, null, documentStatuses);

		expectedResults = new HashSet<String>();
		actualResults = getDocumentMediaCodes(result);

		expectedResults.add("documentMedia3");

		TestCase.assertTrue(actualResults.equals(expectedResults));
	}

	@Test
	public void shouldReturnOldDocumentsByNumberOfDayAndTypeAndStatus()
	{
		final List<DocumentStatus> documentStatuses = Arrays.asList(DocumentStatus.OPEN);

		final B2BDocumentTypeModel documentType1 = new B2BDocumentTypeModel();
		documentType1.setCode("Purchase Order");
		final List<B2BDocumentTypeModel> documentTypes = Arrays.asList(flexibleSearchService.getModelByExample(documentType1));

		final SearchResult<DocumentMediaModel> result = b2bDocumentDao.findOldDocumentMedia(-1, documentTypes, documentStatuses);

		expectedResults = new HashSet<String>();
		actualResults = getDocumentMediaCodes(result);

		expectedResults.add("documentMedia1");

		TestCase.assertTrue(actualResults.equals(expectedResults));
	}
}
