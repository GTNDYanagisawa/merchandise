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
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.accountsummaryaddon.B2BIntegrationTest;
import de.hybris.platform.accountsummaryaddon.document.AccountSummaryDocumentQuery;
import de.hybris.platform.accountsummaryaddon.document.AmountRange;
import de.hybris.platform.accountsummaryaddon.document.B2BDocumentQueryBuilder;
import de.hybris.platform.accountsummaryaddon.document.DateRange;
import de.hybris.platform.accountsummaryaddon.document.dao.PagedB2BDocumentDao;
import de.hybris.platform.accountsummaryaddon.enums.DocumentStatus;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;

@IntegrationTest
public class DefaultPagedB2BDocumentDaoTest extends B2BIntegrationTest
{
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Resource
	private PagedB2BDocumentDao pagedB2BDocumentDao;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/accountsummaryaddon/test/testOrganizations.csv", "utf-8");
		importCsv("/accountsummaryaddon/test/testB2bdocument.csv", "utf-8");
	}

	@Test
	public void shouldReturnAllDocumentsAscSort()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.STATUS, true).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(10, result.getResults().size());

		for (final B2BDocumentModel document : result.getResults())
		{
			TestCase.assertEquals(DocumentStatus.OPEN, document.getStatus());
		}
	}

	@Test
	public void shouldReturnAllDocumentsDescSort()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.STATUS, false).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(10, result.getResults().size());

		TestCase.assertEquals(DocumentStatus.CLOSED, result.getResults().get(0).getStatus());
		TestCase.assertEquals(DocumentStatus.CLOSED, result.getResults().get(1).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(2).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(3).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(4).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(5).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(6).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(7).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(8).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(9).getStatus());
	}


	@Test
	public void shouldReturnOnlyDocumentsWherePurchaseOrder()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.STATUS, true).addCriteria(
				B2BDocumentModel.DOCUMENTTYPE, "Purchase Order").build();


		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(1, result.getResults().size());

		TestCase.assertEquals("Purchase Order", result.getResults().get(0).getDocumentType().getCode());
	}

	@Test
	public void shouldReturnOnlyDocumentsLikeNote()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.STATUS, true)
				.addCriteria(B2BDocumentModel.DOCUMENTTYPE, "Note").addCriteria(B2BDocumentModel.DOCUMENTNUMBER, "DBN").build();


		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(2, result.getResults().size());

		TestCase.assertEquals("Debit Note", result.getResults().get(0).getDocumentType().getCode());
		TestCase.assertEquals("Debit Note", result.getResults().get(1).getDocumentType().getCode());
	}

	@Test
	public void shouldReturnOnlyFirstPageSortByDueDate()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 2, B2BDocumentModel.DUEDATE, true).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(2, result.getResults().size());
		TestCase.assertEquals("CRN-005", result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals("CRN-006", result.getResults().get(1).getDocumentNumber());
		
		Date date0 = result.getResults().get(0).getDueDate();
		Date date1 = result.getResults().get(1).getDueDate();
		TestCase.assertEquals("2013-07-07", sdf.format(date0));
		TestCase.assertEquals("2013-07-08", sdf.format(date1));
	}

	@Test
	public void shouldReturnSecondPageSortByAmount()
	{
		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(1, 2, B2BDocumentModel.AMOUNT, true).build();

		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(2, result.getResults().size());

		TestCase.assertEquals("75.31", result.getResults().get(0).getAmount().toString());
		TestCase.assertEquals("85.20", result.getResults().get(1).getAmount().toString());
	}


	@Test
	public void shouldReturnDocumentAssociateToUser()
	{
		login("mark.rivers@rustic-hw.com");

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(3, result.getResults().size());
		TestCase.assertEquals("DBN-001", result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals("DBN-002", result.getResults().get(1).getDocumentNumber());
		TestCase.assertEquals("PUR-002", result.getResults().get(2).getDocumentNumber());
	}

	@Test
	public void shouldReturnOpenDocuments()
	{
		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria("status", DocumentStatus.OPEN).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(10, result.getResults().size());
		for (final B2BDocumentModel document : result.getResults())
		{
			TestCase.assertEquals(DocumentStatus.OPEN, document.getStatus());
		}
	}


	@Test
	public void shouldReturnProntoServicesDocuments()
	{
		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.UNIT, "Pronto Services")
				.build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(2, result.getResults().size());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(0).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(1).getStatus());
	}

	@Test
	public void shouldReturnEmptyResultForServicesWestDocuments()
	{
		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.UNIT, "Services West")
				.build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(0, result.getResults().size());
	}

	@Test
	public void shouldReturnDocumentsBetweenAmountRange()
	{
		final AmountRange amountRange = new AmountRange(new BigDecimal("75.30"), new BigDecimal("76.31"));

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.AMOUNT, amountRange).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(1, result.getResults().size());
		TestCase.assertEquals("PUR-002", result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals("75.31", result.getResults().get(0).getAmount().toPlainString());
	}

	@Test
	public void shouldReturnDocumentsBetweenOpenamountRange()
	{
		final AmountRange amountRange = new AmountRange(new BigDecimal("26.54"), new BigDecimal("26.54"));

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.OPENAMOUNT, amountRange)
				.build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(1, result.getResults().size());
		TestCase.assertEquals("DBN-002", result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals("26.54", result.getResults().get(0).getOpenAmount().toPlainString());
	}

	@Test
	public void shouldReturnDocumentsBetweenDateRange() throws ParseException
	{
		final Date minDate = DateUtils.parseDate("2013-08-10", new String[]
		{ "yyyy-MM-dd" });
		final Date maxDate = DateUtils.parseDate("2013-08-11", new String[]
		{ "yyyy-MM-dd" });

		final DateRange dateRange = new DateRange(minDate, maxDate);

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.DATE, dateRange).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(2, result.getResults().size());
		
		Date date0 = result.getResults().get(0).getDate();
		Date date1 = result.getResults().get(1).getDate();
		date0.after(minDate);
		TestCase.assertEquals("2013-08-11", sdf.format(date0));
		TestCase.assertEquals("2013-08-11", sdf.format(date1));
	}

	@Test
	public void shouldReturnDocumentsBetweenDueDateRange() throws ParseException
	{
		final Date minDate = DateUtils.parseDate("2013-08-16", new String[]
		{ "yyyy-MM-dd" });
		final Date maxDate = DateUtils.parseDate("2013-08-17", new String[]
		{ "yyyy-MM-dd" });

		final DateRange dateRange = new DateRange(minDate, maxDate);

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.DUEDATE, dateRange).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(1, result.getResults().size());
		
		Date date0 = result.getResults().get(0).getDueDate();
		TestCase.assertEquals("2013-08-16", sdf.format(date0));
	}

	// No result
	@Test
	public void shouldNotReturnResult()
	{
		login("mark.rivers@rustic-hw.com");

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.DOCUMENTTYPE, "UNKNOW").build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(0, result.getResults().size());
	}

	// unknow criteria
	@Test
	public void shouldGetErrorCriteriaNotFound()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria("unknowcriteria", "any").build();

		try
		{
			pagedB2BDocumentDao.findDocuments(query);
			TestCase.fail();
		}
		catch (final FlexibleSearchException e)
		{
			//Success
			TestCase.assertTrue(StringUtils.startsWith(e.getMessage(), "cannot search unknown field"));
		}
	}

	// invalidate page
	@Test
	public void shouldGetErrorInvalidPage()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(-1, 10, B2BDocumentModel.OPENAMOUNT, true).build();
		try
		{
			pagedB2BDocumentDao.findDocuments(query);
			TestCase.fail();
		}
		catch (final IllegalArgumentException e)
		{
			//Success
			TestCase.assertEquals("pageableData current page must be zero or greater", e.getMessage());
		}
	}

	//
	@Test
	public void shouldGetErrorInvalidPageSize()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, -1, B2BDocumentModel.OPENAMOUNT, true).build();
		try
		{
			pagedB2BDocumentDao.findDocuments(query);
			TestCase.fail();
		}
		catch (final IllegalArgumentException e)
		{
			//Success
			TestCase.assertEquals("pageableData page size must be greater than zero", e.getMessage());
		}
	}
}
