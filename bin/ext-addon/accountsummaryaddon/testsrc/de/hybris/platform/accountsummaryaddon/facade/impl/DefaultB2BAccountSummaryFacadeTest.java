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
package de.hybris.platform.accountsummaryaddon.facade.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.accountsummaryaddon.B2BIntegrationTest;
import de.hybris.platform.accountsummaryaddon.constants.AccountsummaryaddonConstants;
import de.hybris.platform.accountsummaryaddon.document.data.B2BAmountBalanceData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.accountsummaryaddon.facade.B2BAccountSummaryFacade;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.query.QueryParameters;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.Config;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BAccountSummaryFacadeTest extends B2BIntegrationTest
{

	@Resource
	private B2BAccountSummaryFacade b2bAccountSummaryFacade;

	@Resource
	private CommonI18NService commonI18NService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/accountsummaryaddon/test/testOrganizations.csv", "utf-8");
		importCsv("/accountsummaryaddon/test/testB2bdocument.csv", "utf-8");

		final CurrencyModel currency = commonI18NService.getCurrency("USD");
		commonI18NService.setCurrentCurrency(currency);
	}

	@Test
	public void shouldGetValidResult()
	{
		final Map<String, String> params = QueryParameters.with("currentPage", "0").and("pageSize", "10")
				.and("unit", "Custom Retail").and("status", AccountsummaryaddonConstants.SEARCH_STATUS_OPEN)
				.and("searchBy", B2BDocumentModel.DOCUMENTNUMBER).and("searchValue", "PU").buildMap();

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.findDocuments(params);

		TestCase.assertEquals(1, result.getResults().size());
		TestCase.assertEquals("PUR-002", result.getResults().get(0).getDocumentNumber());
	}

	//should get amount balance for 2 documents and 1 range (no past due)
	@Test
	@Ignore
	public void shouldGetAmountBalanceFor2DocumentsAnd1RangeNoPastDue()
	{
		setDateRanges("1", "", "", "", "", "", "", "");

		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid("Custom Retail");
		final B2BAmountBalanceData results = b2bAccountSummaryFacade.getAmountBalance(unit);

		TestCase.assertEquals(1, results.getDueBalance().size());

		TestCase.assertEquals("$ 0,00", results.getDueBalance().values().toArray()[0]);

		TestCase.assertEquals("$ 102,08", results.getOpenBalance());
		TestCase.assertEquals("$ 0,00", results.getPastDueBalance());
		TestCase.assertEquals("$ 102,08", results.getCurrentBalance());
	}

	//should get amount balance for 2 documents and 1 range (only one past due)
	@Test
	@Ignore
	public void shouldGetAmountBalanceFor2DocumentsAnd1RangeOnePastDue()
	{
		setDateRanges("1", "", "", "", "", "", "", "");

		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid("Pronto Goods");
		final B2BAmountBalanceData results = b2bAccountSummaryFacade.getAmountBalance(unit);

		TestCase.assertEquals(1, results.getDueBalance().size());

		TestCase.assertEquals("$ 25,54", results.getDueBalance().values().toArray()[0]);

		TestCase.assertEquals("$ 41,08", results.getOpenBalance());
		TestCase.assertEquals("$ 25,54", results.getPastDueBalance());
		TestCase.assertEquals("$ 15,54", results.getCurrentBalance());
	}

	//should get amount balance for 2 documents and 3 range (only one past due)
	@Test
	@Ignore
	public void shouldGetAmountBalanceFor2DocumentsAnd3RangeOnePastDue()
	{
		setDateRanges("1", "10", "11", "30", "31", "", "", "");

		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid("Pronto Goods");
		final B2BAmountBalanceData results = b2bAccountSummaryFacade.getAmountBalance(unit);

		TestCase.assertEquals(3, results.getDueBalance().size());

		TestCase.assertEquals("$ 0,00", results.getDueBalance().values().toArray()[0]);
		TestCase.assertEquals("$ 25,54", results.getDueBalance().values().toArray()[1]);
		TestCase.assertEquals("$ 0,00", results.getDueBalance().values().toArray()[2]);

		TestCase.assertEquals("$ 41,08", results.getOpenBalance());
		TestCase.assertEquals("$ 25,54", results.getPastDueBalance());
		TestCase.assertEquals("$ 15,54", results.getCurrentBalance());
	}

	//should get amount balance for 3 documents and 3 range (2 past due)
	@Test
	@Ignore
	public void shouldGetAmountBalanceFor3DocumentsAnd3Range2PastDue()
	{
		setDateRanges("1", "5", "6", "30", "31", "", "", "");

		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid("Pronto");
		final B2BAmountBalanceData results = b2bAccountSummaryFacade.getAmountBalance(unit);

		TestCase.assertEquals(3, results.getDueBalance().size());

		TestCase.assertEquals("$ 21,51", results.getDueBalance().values().toArray()[0]);
		TestCase.assertEquals("$ 25,54", results.getDueBalance().values().toArray()[1]);
		TestCase.assertEquals("$ 0,00", results.getDueBalance().values().toArray()[2]);

		TestCase.assertEquals("$ 62,59", results.getOpenBalance());
		TestCase.assertEquals("$ 47,05", results.getPastDueBalance());
		TestCase.assertEquals("$ 15,54", results.getCurrentBalance());
	}


	//should get amount balance for 3 documents and 3 range (2 past due and 1 not includeInOpenBalance)
	@Test
	@Ignore
	public void shouldGetAmountBalanceFor3DocumentsAnd3Range2PastDueButOneNotIncludeInOpenBalance()
	{
		setDateRanges("1", "5", "6", "30", "31", "", "", "");

		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid("Services East");
		final B2BAmountBalanceData results = b2bAccountSummaryFacade.getAmountBalance(unit);

		TestCase.assertEquals(3, results.getDueBalance().size());

		TestCase.assertEquals("$ 25,54", results.getDueBalance().values().toArray()[0]);
		TestCase.assertEquals("$ 0,00", results.getDueBalance().values().toArray()[1]);
		TestCase.assertEquals("$ 12,54", results.getDueBalance().values().toArray()[2]);

		TestCase.assertEquals("$ 38,08", results.getOpenBalance());
		TestCase.assertEquals("$ 38,08", results.getPastDueBalance());
		TestCase.assertEquals("$ 0,00", results.getCurrentBalance());
	}

	/**
	 * test how the attribute "displayInAllList" of document type impacts the search result If the document type of one
	 * document is set to displayInAllList=false when search all open documents, this document should not be in search
	 * result
	 **/
	@Test
	public void shouldNotIncludeStatementDocument()
	{

		final Map<String, String> params = QueryParameters.with("currentPage", "0").and("pageSize", "10")
				.and("unit", "Custom Retail").and("sort", B2BDocumentModel.STATUS).and("searchRangeMax", "")
				.and("searchRangeMin", "").and("status", "status_all").and("searchBy", B2BDocumentModel.DOCUMENTNUMBER)
				.and("searchValue", "").buildMap();

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.findDocuments(params);

		int count = 0;
		for (final B2BDocumentData element : result.getResults())
		{
			if (!element.getDocumentType().getDisplayInAllList().booleanValue())
			{
				count++;
			}
		}

		TestCase.assertEquals(0, count);
		TestCase.assertEquals(0, result.getPagination().getCurrentPage());
		TestCase.assertEquals(1, result.getPagination().getNumberOfPages());
		TestCase.assertEquals(3, result.getPagination().getTotalNumberOfResults());
	}

	@Test
	public void shouldIncludeStatementDocumentWhenSearchByDocumentNumber()
	{

		final Map<String, String> params = QueryParameters.with("currentPage", "0").and("pageSize", "10")
				.and("unit", "Pronto Goods").and("status", AccountsummaryaddonConstants.SEARCH_STATUS_OPEN)
				.and("searchBy", B2BDocumentModel.DOCUMENTNUMBER).and("searchValue", "STA-001").buildMap();

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.findDocuments(params);

		TestCase.assertEquals(1, result.getResults().size());
	}

	/**
	 * test how the attribute "displayInAllList" of document type impacts the search result If the document type of one
	 * document is set to displayInAllList=false when search by document number or type, this document should be in
	 * search result
	 **/
	@Test
	public void shouldIncludeStatementDocumentWhenSearchByDocumentType()
	{

		final Map<String, String> params = QueryParameters.with("currentPage", "0").and("pageSize", "10")
				.and("unit", "Pronto Goods").and("searchBy", B2BDocumentModel.DOCUMENTTYPE).and("searchValue", "Statement")
				.buildMap();

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.findDocuments(params);

		TestCase.assertEquals(1, result.getResults().size());
	}

	public void setDateRanges(final String minRange1, final String maxRange1, final String minRange2, final String maxRange2,
			final String minRange3, final String maxRange3, final String minRange4, final String maxRange4)
	{
		Calendar baseDate = new GregorianCalendar(2013, Calendar.AUGUST, 13, 12, 0);
		int days = Days.daysBetween(new DateTime(baseDate.getTime()), new DateTime()).getDays();

		Config.setParameter("accountsummaryaddon.daterange.1.start", minRange1);
		Config.setParameter("accountsummaryaddon.daterange.1.end", getRange(maxRange1, days));

		Config.setParameter("accountsummaryaddon.daterange.2.start", getRange(minRange2, days));
		Config.setParameter("accountsummaryaddon.daterange.2.end", getRange(maxRange2, days));

		Config.setParameter("accountsummaryaddon.daterange.3.start", getRange(minRange3, days));
		Config.setParameter("accountsummaryaddon.daterange.3.end", getRange(maxRange3, days));

		Config.setParameter("accountsummaryaddon.daterange.4.start", getRange(minRange4, days));
		Config.setParameter("accountsummaryaddon.daterange..end", getRange(maxRange4, days));
	}

	public String getRange(String range, int shift)
	{
		if (StringUtils.isEmpty(range))
		{
			return "";
		}
		else
		{
			int total = shift + Integer.parseInt(range);
			return Integer.toString(total);
		}
	}
}
