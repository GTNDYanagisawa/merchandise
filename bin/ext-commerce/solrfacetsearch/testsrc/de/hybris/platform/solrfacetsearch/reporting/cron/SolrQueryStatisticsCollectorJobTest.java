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
package de.hybris.platform.solrfacetsearch.reporting.cron;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.reporting.SolrQueryAggregatedStatsModel;
import de.hybris.platform.solrfacetsearch.reporting.data.AggregatedSearchQueryInfo;
import de.hybris.platform.solrfacetsearch.reporting.impl.Log4jSolrQueryStatisticsAggregator;
import de.hybris.platform.solrfacetsearch.reporting.impl.Log4jSolrReportingService;
import de.hybris.platform.testframework.TestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test for SolrQueryStatisticsCollectorJob
 */
@IntegrationTest
public class SolrQueryStatisticsCollectorJobTest extends AbstractSolrTest
{
	@Resource
	SolrQueryStatisticsCollectorJob solrQueryStatisticsCollectorJob;

	@Mock
	Log4jSolrQueryStatisticsAggregator mockSolrQueryStatisticsAggregator;

	@Resource
	Log4jSolrReportingService solrReportingService;

	@Resource
	FlexibleSearchService flexibleSearchService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		super.setUp();
	}

	@After
	public void afterTest()
	{
		solrQueryStatisticsCollectorJob.setEnableCollectingStatistics(true);
	}

	@Test
	public void testIfAggregatedStatisticsAreSavedToDb()
	{
		//given
		final Date date1 = new DateTime().withYear(2011).withMonthOfYear(10).withDayOfMonth(15).withHourOfDay(12)
				.withMinuteOfHour(20).toDate();
		final Date date2 = new DateTime().withYear(2011).withMonthOfYear(10).withDayOfMonth(15).withHourOfDay(12)
				.withMinuteOfHour(45).toDate();
		final Date date3 = new DateTime().withYear(2011).withMonthOfYear(11).withDayOfMonth(19).withHourOfDay(12)
				.withMinuteOfHour(45).toDate();
		final List<AggregatedSearchQueryInfo> aggrResults = new ArrayList<AggregatedSearchQueryInfo>();
		aggrResults.add(new AggregatedSearchQueryInfo(SOLR_CONFIG_NAME, "monitor", "en", 5, date1));
		aggrResults.add(new AggregatedSearchQueryInfo(SOLR_CONFIG_NAME, "monitor lcd", "en", 3, date2));
		aggrResults.add(new AggregatedSearchQueryInfo(SOLR_CONFIG_NAME, "canon", "en", 2, date3));

		Mockito.when(mockSolrQueryStatisticsAggregator.aggregate()).thenReturn(aggrResults);

		solrQueryStatisticsCollectorJob.setSolrQueryStatisticsAggregator(mockSolrQueryStatisticsAggregator);

		//when
		final PerformResult perform = solrQueryStatisticsCollectorJob.perform(null);

		//then
		org.fest.assertions.Assertions.assertThat(perform.getResult()).isEqualTo(CronJobResult.SUCCESS);
		org.fest.assertions.Assertions.assertThat(perform.getStatus()).isEqualTo(CronJobStatus.FINISHED);

		final SearchResult<SolrQueryAggregatedStatsModel> result = flexibleSearchService
				.search("SELECT {PK} FROM {SolrQueryAggregatedStats}");
		org.fest.assertions.Assertions.assertThat(result.getResult()).hasSize(3);
	}

	@Test
	public void testDisableCollectingStatistics()
	{
		//given
		solrQueryStatisticsCollectorJob.setEnableCollectingStatistics(false);

		//when
		final PerformResult perform = solrQueryStatisticsCollectorJob.perform(null);

		//then
		org.fest.assertions.Assertions.assertThat(perform.getResult()).isEqualTo(CronJobResult.SUCCESS);
		org.fest.assertions.Assertions.assertThat(perform.getStatus()).isEqualTo(CronJobStatus.FINISHED);

		final SearchResult<SolrQueryAggregatedStatsModel> result = flexibleSearchService
				.search("SELECT {PK} FROM {SolrQueryAggregatedStats}");
		org.fest.assertions.Assertions.assertThat(result.getResult()).isEmpty();
	}

	@Test
	public void testIfErrorDuringPerformance()
	{
		//given
		Mockito.when(mockSolrQueryStatisticsAggregator.aggregate()).thenThrow(new RuntimeException());
		solrQueryStatisticsCollectorJob.setSolrQueryStatisticsAggregator(mockSolrQueryStatisticsAggregator);

		TestUtils.disableFileAnalyzer("test for an error during job execution");
		//when
		final PerformResult perform = solrQueryStatisticsCollectorJob.perform(null);

		TestUtils.enableFileAnalyzer();

		//then
		org.fest.assertions.Assertions.assertThat(perform.getResult()).isEqualTo(CronJobResult.ERROR);
		org.fest.assertions.Assertions.assertThat(perform.getStatus()).isEqualTo(CronJobStatus.FINISHED);

		final SearchResult<SolrQueryAggregatedStatsModel> result = flexibleSearchService
				.search("SELECT {PK} FROM {SolrQueryAggregatedStats}");
		org.fest.assertions.Assertions.assertThat(result.getResult()).isEmpty();
	}
}
