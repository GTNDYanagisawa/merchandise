/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.importcockpit.daos.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.importcockpit.model.ImportCockpitCronJobModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultImportCockpitCronJobLogDaoTest
{

	private DefaultImportCockpitCronJobLogDao icDao;
	@Mock
	private ImportCockpitCronJobModel job;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private SearchResult searchResult;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		icDao = new DefaultImportCockpitCronJobLogDao();
		icDao.setFlexibleSearchService(flexibleSearchService);
	}

	@Test
	public void testFindRecentLogsByCronJob()
	{
		when(flexibleSearchService.search(Matchers.<FlexibleSearchQuery> any())).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(null);
		when(job.getStartTime()).thenReturn(null);
		when(job.getCreationtime()).thenReturn(null);
		icDao.findRecentLogsByCronJob(job);
		verify(flexibleSearchService).search(Matchers.<FlexibleSearchQuery> any());
		verify(job).getCreationtime();
		verify(job).getStartTime();
	}
}
