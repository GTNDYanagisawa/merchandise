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
package de.hybris.platform.cockpit.reports.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.enums.RefreshTimeOption;
import de.hybris.platform.cockpit.reports.JasperReportCacheService;
import de.hybris.platform.cockpit.reports.model.JasperWidgetPreferencesModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for DefaultJasperReportsRefreshService
 */
@UnitTest
public class DefaultJasperReportsRefreshServiceTest
{
	DefaultJasperReportsRefreshService jasperReportsRefreshService = new DefaultJasperReportsRefreshService();

	@Mock
	private JasperReportCacheService jasperReportsCacheService;

	@Mock
	private JasperWidgetPreferencesModel widget;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		jasperReportsRefreshService.setJasperReportsCacheService(jasperReportsCacheService);
	}

	@Test
	public void testRefreshingWithSuccess() throws InterruptedException
	{
		//given
		jasperReportsRefreshService.startRefreshing(widget, RefreshTimeOption.FIVESEC);
		Thread.sleep(6000);

		//when
		final boolean anyRefreshed = jasperReportsRefreshService.onRefresh();

		//then
		assertThat(anyRefreshed).isTrue();
		verify(jasperReportsCacheService).update(widget);
	}

	@Test
	public void testRefreshingWithFailure() throws InterruptedException
	{
		//given
		jasperReportsRefreshService.startRefreshing(widget, RefreshTimeOption.HALFMIN);

		//when
		final boolean anyRefreshed = jasperReportsRefreshService.onRefresh();

		//then
		assertThat(anyRefreshed).isFalse();
	}

	@Test
	public void testRefreshingWithFailureBecauseNeverSet() throws InterruptedException
	{
		//given
		jasperReportsRefreshService.startRefreshing(widget, RefreshTimeOption.NEVER);

		//when
		final boolean anyRefreshed = jasperReportsRefreshService.onRefresh();

		//then
		assertThat(anyRefreshed).isFalse();
	}
}
