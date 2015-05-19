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
package de.hybris.platform.yb2bacceleratorcore.setup;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.commerceservices.setup.SetupSyncJobService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.yb2bacceleratorcore.model.MultipleCatalogsSyncCronJobModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class MultipleCatalogsSyncJobPerformableTest
{
	private static final String PRODUCT_CATALOG = "pc1";
	private static final String CONTENT_CATALOG = "cc1";

	private AbstractJobPerformable<MultipleCatalogsSyncCronJobModel> performable;

	@Mock
	private SetupSyncJobService setup;

	@Mock
	private MultipleCatalogsSyncCronJobModel cronJob;

	@Mock
	private CatalogModel contentCatalog;
	@Mock
	private CatalogModel productCatalog;

	private final List<CatalogModel> contentCatalogs = new ArrayList<CatalogModel>();

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		contentCatalogs.add(contentCatalog);
		performable = new MultipleCatalogsSyncJobPerformable()
		{
			@Override
			protected SetupSyncJobService getSetupSyncJobService()
			{
				return setup;
			}
		};

	}

	@Test(expected = NullPointerException.class)
	public void testNullCronJob()
	{

		performable.perform(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoContentCatalog()
	{
		BDDMockito.given(cronJob.getContentCatalogs()).willReturn(null);

		performable.perform(cronJob);

		Mockito.verifyZeroInteractions(setup);
	}


	@Test(expected = NullPointerException.class)
	public void testNoProductCatalog()
	{
		BDDMockito.given(cronJob.getContentCatalogs()).willReturn(contentCatalogs);
		BDDMockito.given(cronJob.getProductCatalog()).willReturn(null);

		performable.perform(cronJob);

		Mockito.verifyZeroInteractions(setup);

	}

	@Test
	public void testProductAndContentCatalogAllReturningSuccess()
	{
		BDDMockito.given(contentCatalog.getId()).willReturn(CONTENT_CATALOG);
		BDDMockito.given(productCatalog.getId()).willReturn(PRODUCT_CATALOG);

		BDDMockito.given(cronJob.getContentCatalogs()).willReturn(contentCatalogs);
		BDDMockito.given(cronJob.getProductCatalog()).willReturn(productCatalog);

		BDDMockito.given(setup.executeCatalogSyncJob(Mockito.anyString())).willReturn(
				new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED));

		final PerformResult result = performable.perform(cronJob);

		Mockito.verify(setup).executeCatalogSyncJob(CONTENT_CATALOG);
		Mockito.verify(setup).executeCatalogSyncJob(PRODUCT_CATALOG);

		Assert.assertEquals(result.getResult(), CronJobResult.SUCCESS);
		Assert.assertEquals(result.getStatus(), CronJobStatus.FINISHED);
	}


	@Test(expected = IllegalStateException.class)
	public void testProductAndContentCatalogAssignFails()
	{
		BDDMockito.given(contentCatalog.getId()).willReturn(CONTENT_CATALOG);
		BDDMockito.given(productCatalog.getId()).willReturn(PRODUCT_CATALOG);

		BDDMockito.given(cronJob.getContentCatalogs()).willReturn(contentCatalogs);
		BDDMockito.given(cronJob.getProductCatalog()).willReturn(productCatalog);


		Mockito.doThrow(new IllegalStateException("expected")).when(setup)
				.assignDependentSyncJobs(PRODUCT_CATALOG, Collections.singleton(CONTENT_CATALOG));

		performable.perform(cronJob);
	}


	@Test
	public void testProductAndContentCatalogFirstExecutionFails()
	{
		BDDMockito.given(contentCatalog.getId()).willReturn(CONTENT_CATALOG);
		BDDMockito.given(productCatalog.getId()).willReturn(PRODUCT_CATALOG);

		BDDMockito.given(cronJob.getContentCatalogs()).willReturn(contentCatalogs);
		BDDMockito.given(cronJob.getProductCatalog()).willReturn(productCatalog);

		BDDMockito.given(setup.executeCatalogSyncJob(CONTENT_CATALOG)).willReturn(
				new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED));
		BDDMockito.given(setup.executeCatalogSyncJob(PRODUCT_CATALOG)).willReturn(
				new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED));

		final PerformResult result = performable.perform(cronJob);


		Mockito.verify(setup).executeCatalogSyncJob(CONTENT_CATALOG);
		Mockito.verify(setup).executeCatalogSyncJob(PRODUCT_CATALOG);

		Assert.assertEquals(result.getResult(), CronJobResult.FAILURE);
		Assert.assertEquals(result.getStatus(), CronJobStatus.ABORTED);
	}

	@Test
	public void testProductAndContentCatalogLastExecutionFails()
	{
		BDDMockito.given(contentCatalog.getId()).willReturn(CONTENT_CATALOG);
		BDDMockito.given(productCatalog.getId()).willReturn(PRODUCT_CATALOG);

		BDDMockito.given(cronJob.getContentCatalogs()).willReturn(contentCatalogs);
		BDDMockito.given(cronJob.getProductCatalog()).willReturn(productCatalog);

		BDDMockito.given(setup.executeCatalogSyncJob(CONTENT_CATALOG)).willReturn(
				new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED));


		BDDMockito.given(setup.executeCatalogSyncJob(PRODUCT_CATALOG)).willReturn(
				new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED));

		final PerformResult result = performable.perform(cronJob);

		Mockito.verify(setup).executeCatalogSyncJob(CONTENT_CATALOG);
		Mockito.verify(setup).executeCatalogSyncJob(PRODUCT_CATALOG);

		Assert.assertEquals(result.getResult(), CronJobResult.FAILURE);
		Assert.assertEquals(result.getStatus(), CronJobStatus.ABORTED);
	}
}
