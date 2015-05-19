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
package de.hybris.platform.servicelayer.cronjob.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cluster.legacy.LegacyBroadcastHandler;
import de.hybris.platform.core.PK;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobLogModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.cronjob.CronJobDao;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.JobDao;
import de.hybris.platform.servicelayer.cronjob.JobLogDao;
import de.hybris.platform.servicelayer.cronjob.RunCronJobMessageCreatorAndSender;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.tenant.TenantService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Platform aware test for {@link DefaultCronJobService}.
 */
@UnitTest
public class DefaultCronJobServiceTest
{
	private CronJobService service;

	@Mock
	private ModelService modelServiceMock;

	@Mock
	private LegacyBroadcastHandler broadcasterMock;

	@Mock
	private ClusterService clusterService;

	@Mock
	private CronJobDao cronJobMockDao;

	@Mock
	private JobDao jobMockDao;

	@Mock
	private JobLogDao jobLogDao;

	@Mock
	private Converter<List<JobLogModel>, String> jobLogConverter;

	@Before
	public void setUp()
	{

		MockitoAnnotations.initMocks(this);
		service = new DefaultCronJobService()
		{
			@Override
			protected ModelService getModelService()
			{

				return modelServiceMock;
			}

		};

		((DefaultCronJobService) service).setClusterService(clusterService);
		((DefaultCronJobService) service).setCronJobDao(cronJobMockDao);
		((DefaultCronJobService) service).setJobDao(jobMockDao);
		((DefaultCronJobService) service).setJobLogDao(jobLogDao);
		((DefaultCronJobService) service).setJobLogConverter(jobLogConverter);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetCronJobNullResult()
	{
		when(cronJobMockDao.findCronJobs("someCode")).thenReturn(Collections.EMPTY_LIST);

		service.getCronJob("someCode");
	}

	@Test
	public void testGetCronJobNoneResult()
	{
		final List<CronJobModel> allCJobs = Arrays.asList();

		when(cronJobMockDao.findCronJobs("someCode")).thenReturn(allCJobs);

		try
		{
			service.getCronJob("someCode");
			Assert.fail("If dao returns empty list service should thrown an exception ");
		}
		catch (final UnknownIdentifierException e)
		{
			//ok
		}
	}

	@Test
	public void testGetCronJobOneResult()
	{
		final CronJobModel one = new CronJobModel();
		one.setCode("one");

		final List<CronJobModel> allCJobs = Arrays.asList(one);

		when(cronJobMockDao.findCronJobs("someCode")).thenReturn(allCJobs);

		Assert.assertEquals("CronJobs list returned from service.getCronJob  should return first element of the  dao.findCronJob.",
				service.getCronJob("someCode"), one);

		verify(cronJobMockDao, times(1)).findCronJobs("someCode");
	}

	@Test
	public void testGetCronJobMoreResults()
	{

		final CronJobModel one = new CronJobModel();
		one.setCode("one");

		final CronJobModel two = new CronJobModel();
		one.setCode("two");

		final List<CronJobModel> allCJobs = Arrays.asList(one, two);

		when(cronJobMockDao.findCronJobs("someCode")).thenReturn(allCJobs);

		try
		{
			service.getCronJob("someCode");
			Assert.fail("Should not return more than one cronjob");
		}
		catch (final AmbiguousIdentifierException e)
		{
			//ok
		}
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetJobNullResult()
	{

		when(jobMockDao.findJobs("someCode")).thenReturn(Collections.EMPTY_LIST);

		service.getJob("someCode");
	}

	@Test
	public void testGetJobNoneResult()
	{
		final List<JobModel> allJobs = Arrays.asList();

		when(jobMockDao.findJobs("someCode")).thenReturn(allJobs);

		try
		{
			service.getJob("someCode");
			Assert.fail("If dao returns empty list service should thrown an exception ");
		}
		catch (final UnknownIdentifierException e)
		{
			//ok
		}
	}

	@Test
	public void testGetJobOneResult()
	{
		final JobModel one = new JobModel();
		one.setCode("one");

		final List<JobModel> allJobs = Arrays.asList(one);

		when(jobMockDao.findJobs("someCode")).thenReturn(allJobs);

		Assert.assertEquals("CronJobs list returned from service.getCronJob  should return first element of the  dao.findJob",
				service.getJob("someCode"), one);

		verify(jobMockDao, times(1)).findJobs("someCode");
	}

	@Test
	public void testGetJobMoreResults()
	{
		final JobModel one = new JobModel();
		one.setCode("one");
		final JobModel two = new JobModel();
		one.setCode("two");

		final List<JobModel> allCJobs = Arrays.asList(one, two);

		when(jobMockDao.findJobs("someCode")).thenReturn(allCJobs);
		try
		{
			service.getJob("someCode");
			Assert.fail("Should not return more than one job");
		}
		catch (final AmbiguousIdentifierException e)
		{
			//ok
		}
	}


	@Test(expected = SystemException.class)
	public void testGetRunningCronJobsNullResult()
	{

		when(cronJobMockDao.findRunningCronJobs()).thenReturn(null);

		service.getRunningOrRestartedCronJobs();
	}

	@Test
	public void testRuninigCronJobsNoneResult()
	{
		final List<CronJobModel> allCronJobs = Arrays.asList();

		when(cronJobMockDao.findRunningCronJobs()).thenReturn(allCronJobs);

		Assert.assertEquals("Result from dao should be the same as from service", allCronJobs,
				service.getRunningOrRestartedCronJobs());

		verify(cronJobMockDao, times(1)).findRunningCronJobs();
	}

	@Test
	public void testRuninigCronJobsOneResult()
	{
		final CronJobModel one = new CronJobModel();
		one.setCode("one");

		final List<CronJobModel> allCronJobs = Arrays.asList(one);

		final CronJobDao mockDao = mock(CronJobDao.class);
		((DefaultCronJobService) service).setCronJobDao(mockDao);
		when(mockDao.findRunningCronJobs()).thenReturn(allCronJobs);

		Assert.assertEquals("Result from dao should be the same as from service", allCronJobs,
				service.getRunningOrRestartedCronJobs());

		verify(mockDao, times(1)).findRunningCronJobs();
	}

	@Test
	public void testRuninigCronJobsMoreResults()
	{
		final CronJobModel one = new CronJobModel();
		one.setCode("one");
		final CronJobModel two = new CronJobModel();
		one.setCode("two");

		final List<CronJobModel> allCronJobs = Arrays.asList(one, two);

		when(cronJobMockDao.findRunningCronJobs()).thenReturn(allCronJobs);

		Assert.assertEquals("Result from dao should be the same as from service", allCronJobs,
				service.getRunningOrRestartedCronJobs());

		verify(cronJobMockDao, times(1)).findRunningCronJobs();
	}


	/**
	 * Just checks if there is a call for a {@link DefaultCronJobService#performCronJob(CronJobModel, boolean)} only.
	 * Assuming the previous logic for it was working.
	 */
	@Test
	public void testPerformCronJobOntheSameNode()
	{
		final Integer currentClusterId = Integer.valueOf(10212);

		final CronJobModel one = new CronJobModel();
		one.setCode("one");
		one.setNodeID(currentClusterId);

		when(Integer.valueOf(clusterService.getClusterId())).thenReturn(currentClusterId);
		when(modelServiceMock.getSource(Mockito.anyObject())).thenThrow(new IllegalStateException());

		try
		{
			service.performCronJob(one); //same node
			Assert.fail("Should not pass perform if getSource throws an exception");
		}
		catch (final IllegalStateException e)
		{
			//OK
		}
		verify(modelServiceMock, times(1)).getSource(one);
		//don't provide further verify jalo zone !!
	}

	@Test(expected = IllegalStateException.class)
	public void testPerformCronJobOntheOtherNodeInvalid()
	{
		final Integer currentClusterId = Integer.valueOf(10212);

		final CronJobModel one = new CronJobModel();
		one.setCode("one");
		one.setNodeID(Integer.valueOf(currentClusterId.intValue() + 100));//other node

		when(Integer.valueOf(clusterService.getClusterId())).thenReturn(currentClusterId);
		when(Boolean.valueOf(clusterService.isClusteringEnabled())).thenReturn(Boolean.FALSE);

		service.performCronJob(one);

	}


	@Test(expected = IllegalArgumentException.class)
	public void testPerformCronJobWithoutJob()
	{
		final Integer currentClusterId = Integer.valueOf(10212);

		final CronJobModel one = new CronJobModel();
		one.setCode("one");

		when(Integer.valueOf(clusterService.getClusterId())).thenReturn(currentClusterId);
		when(Boolean.valueOf(clusterService.isClusteringEnabled())).thenReturn(Boolean.FALSE);

		service.performCronJob(one);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testPerformCronJobWithJobWithoutNodeId()
	{
		final Integer currentClusterId = Integer.valueOf(10212);

		final JobModel two = new JobModel();
		two.setCode("two");

		final CronJobModel one = new CronJobModel();
		one.setCode("one");

		when(Integer.valueOf(clusterService.getClusterId())).thenReturn(currentClusterId);
		when(Boolean.valueOf(clusterService.isClusteringEnabled())).thenReturn(Boolean.FALSE);

		service.performCronJob(one);

	}

	@Test
	public void testPerformCronJobOntheOtherNode()
	{
		final PK somePk = PK.createFixedUUIDPK(102, System.currentTimeMillis());

		final Integer currentClusterId = Integer.valueOf(10212);

		final RunCronJobMessageCreatorAndSender builderAndSender = Mockito.spy(new DefaultRunCronJobMessageCreatorAndSender());

		final TenantService tenantService = mock(TenantService.class);

		((DefaultRunCronJobMessageCreatorAndSender) builderAndSender).setLegacyBroadcastHandler(broadcasterMock);
		((DefaultRunCronJobMessageCreatorAndSender) builderAndSender).setClusterService(clusterService);
		((DefaultRunCronJobMessageCreatorAndSender) builderAndSender).setTenantService(tenantService);

		final JobModel two = Mockito.spy(new JobModel());

		final CronJobModel one = Mockito.spy(new CronJobModel());
		one.setJob(two);

		when(one.getPk()).thenReturn(somePk);//some PK
		when(one.getNodeID()).thenReturn(Integer.valueOf(currentClusterId.intValue() + 100));//other node

		((DefaultCronJobService) service).setRunCronJobMessageBuilder(builderAndSender);
		((DefaultCronJobService) service).setTenantService(tenantService);


		when(tenantService.getCurrentTenantId()).thenReturn("giTenant");
		when(Integer.valueOf(clusterService.getClusterId())).thenReturn(currentClusterId);
		when(Boolean.valueOf(clusterService.isClusteringEnabled())).thenReturn(Boolean.TRUE);

		service.performCronJob(one);

		verify(builderAndSender, Mockito.times(1)).createAndSendMessage(currentClusterId.intValue() + 100, somePk);
	}


	@Test
	public void testIsCronJobAbortable()
	{
		final CronJobModel one = new CronJobModel();
		one.setCode("one");

		when(Boolean.valueOf(cronJobMockDao.isAbortable(one))).thenReturn(Boolean.FALSE);

		Assert.assertEquals("Result from dao should be the same as from service", false, service.isAbortable(one));

		verify(cronJobMockDao, times(1)).isAbortable(one);
	}

	@Test
	public void testIsCronJobPerformable()
	{
		final CronJobModel one = new CronJobModel();
		one.setCode("one");

		when(Boolean.valueOf(cronJobMockDao.isPerformable(one))).thenReturn(Boolean.FALSE);

		Assert.assertEquals("Result from dao should be the same as from service", false, service.isPerformable(one));

		verify(cronJobMockDao, times(1)).isPerformable(one);
	}


	@Test
	public void testRequestAbortAvoided()
	{
		final CronJobModel cronJobModel = mock(CronJobModel.class);

		checkSaveNotCalled(cronJobModel, CronJobStatus.ABORTED);
		checkSaveNotCalled(cronJobModel, CronJobStatus.FINISHED);
		checkSaveNotCalled(cronJobModel, CronJobStatus.PAUSED);
		checkSaveNotCalled(cronJobModel, CronJobStatus.UNKNOWN);
	}


	@Test
	public void testRequestAbortNotAvoided()
	{
		final CronJobModel cronJobModel = mock(CronJobModel.class);

		when(Boolean.valueOf(cronJobMockDao.isAbortable(cronJobModel))).thenReturn(Boolean.TRUE);

		checkSaveCalled(cronJobModel, CronJobStatus.RUNNING);
		checkSaveCalled(cronJobModel, CronJobStatus.RUNNINGRESTART);
	}

	@Test(expected = IllegalStateException.class)
	public void testRequestAbortRunninigAbortable()
	{
		final CronJobModel cronJobModel = mock(CronJobModel.class);

		checkSaveCalled(cronJobModel, CronJobStatus.RUNNING);
	}

	@Test(expected = IllegalStateException.class)
	public void testRequestAbortRestartedAbortable()
	{
		final CronJobModel cronJobModel = mock(CronJobModel.class);

		checkSaveCalled(cronJobModel, CronJobStatus.RUNNINGRESTART);
	}


	@Test
	public void testIsFinished()
	{
		final CronJobModel one = mock(CronJobModel.class);

		for (final CronJobResult result : CronJobResult.values())
		{
			for (final CronJobStatus status : CronJobStatus.values())
			{

				when(one.getStatus()).thenReturn(status);
				when(one.getResult()).thenReturn(result);

				if (status == CronJobStatus.FINISHED || status == CronJobStatus.ABORTED)
				{
					Assert.assertTrue("Should only return finished as true  when status is FINISHED or ABORTED  (but was " + status
							+ " " + result + ")", service.isFinished(one));
				}
				else
				{
					Assert.assertFalse("Should only return finished as false   when status is  not FINISHED or not ABORTED (but was "
							+ status + " " + result + ")", service.isFinished(one));
				}
				Mockito.reset(one);
			}
		}

	}




	@Test
	public void testIsPaused()
	{
		final CronJobModel one = mock(CronJobModel.class);

		for (final CronJobResult result : CronJobResult.values())
		{
			for (final CronJobStatus status : CronJobStatus.values())
			{

				when(one.getStatus()).thenReturn(status);
				when(one.getResult()).thenReturn(result);

				if (status == CronJobStatus.PAUSED)
				{
					Assert.assertTrue("Should only return pasued as true  when status is FINISHED, ABORTED  (but was " + status + " "
							+ result + ")", service.isPaused(one));
				}
				else
				{
					Assert.assertFalse("Should only return paused as false  when status is not FINISHED or not ABORTED (but was "
							+ status + " " + result + ")", service.isPaused(one));
				}
				Mockito.reset(one);
			}
		}
	}


	@Test
	public void testGetLogTextDefault()
	{
		final CronJobModel one = mock(CronJobModel.class);

		final List<JobLogModel> oneResult = mock(List.class);

		when(jobLogDao.findJobLogs(one, 500, true)).thenReturn(oneResult);

		service.getLogsAsText(one);

		verify(jobLogConverter, Mockito.times(1)).convert(oneResult);

	}

	@Test
	public void testGetLogTextNotDefault()
	{
		final CronJobModel one = mock(CronJobModel.class);

		final List<JobLogModel> oneResult = mock(List.class);

		when(jobLogDao.findJobLogs(one, 213, true)).thenReturn(oneResult);

		service.getLogsAsText(one, 213);

		verify(jobLogConverter, Mockito.times(1)).convert(oneResult);

	}

	@Test(expected = SystemException.class)
	public void testGetLogTextInvalid()
	{
		final CronJobModel one = mock(CronJobModel.class);

		final List<JobLogModel> oneResult = mock(List.class);

		when(jobLogDao.findJobLogs(one, -213, true)).thenReturn(oneResult);

		service.getLogsAsText(one, -213);

		//		verify(jobLogConverter, Mockito.times(1)).convert(oneResult);

	}


	@Test
	public void testIsRunning()
	{
		final CronJobModel one = mock(CronJobModel.class);

		for (final CronJobResult result : CronJobResult.values())
		{
			for (final CronJobStatus status : CronJobStatus.values())
			{

				when(one.getStatus()).thenReturn(status);
				when(one.getResult()).thenReturn(result);

				if (status == CronJobStatus.RUNNING || status == CronJobStatus.RUNNINGRESTART)
				{
					Assert.assertTrue("Should only return runining as true  when status is RUNNINIG, RUNNINGRESTART  (but was "
							+ status + " " + result + ")", service.isRunning(one));
				}
				else
				{
					Assert.assertFalse(
							"Should only return running as false  when status is not RUNNINIG or not RUNNINGRESTART (but was " + status
									+ " " + result + ")", service.isRunning(one));
				}
				Mockito.reset(one);
			}
		}
	}

	@Test
	public void testIsSuccessful()
	{
		final CronJobModel one = mock(CronJobModel.class);

		for (final CronJobResult result : CronJobResult.values())
		{
			for (final CronJobStatus status : CronJobStatus.values())
			{

				when(one.getStatus()).thenReturn(status);
				when(one.getResult()).thenReturn(result);

				if (status == CronJobStatus.FINISHED || status == CronJobStatus.ABORTED)
				{
					if (result == CronJobResult.SUCCESS)
					{
						Assert.assertTrue("Should only return successfull as true  when result is SUCCESS  (but was " + status + " "
								+ result + ")", service.isSuccessful(one));
					}
					else
					{
						try
						{
							Assert.assertFalse("Should only return successfull as true  when result is SUCCESS  (but was " + status
									+ " " + result + ")", service.isSuccessful(one));

						}
						catch (final IllegalStateException ile)
						{
							//Ok here
						}

					}

				}
				else
				{
					try
					{
						service.isSuccessful(one);
						Assert.fail("Should only return successfull as true  when result is SUCCESS  (but was " + status + " " + result
								+ ")");

					}
					catch (final IllegalStateException ile)
					{
						//Ok here
					}

				}
				Mockito.reset(one);
			}
		}
	}

	@Test
	public void testIsError()
	{
		final CronJobModel one = mock(CronJobModel.class);

		for (final CronJobResult result : CronJobResult.values())
		{
			for (final CronJobStatus status : CronJobStatus.values())
			{

				when(one.getStatus()).thenReturn(status);
				when(one.getResult()).thenReturn(result);

				if (status == CronJobStatus.FINISHED || status == CronJobStatus.ABORTED)
				{
					if (result == CronJobResult.ERROR || result == CronJobResult.FAILURE)
					{
						Assert.assertTrue("Should only return error  as true  when result is ERROR or FAILURE   (but was " + status
								+ " " + result + ")", service.isError(one));
					}
					else
					{
						try
						{
							Assert.assertFalse("Should only return error  as true  when result is ERROR or FAILURE   (but was " + status
									+ " " + result + ")", service.isError(one));

						}
						catch (final IllegalStateException ile)
						{
							//Ok here
						}

					}
				}
				else
				{
					try
					{
						service.isError(one);
						Assert.fail("Should only return error as true  when result is ERROR or FAILURE  (but was " + status + " "
								+ result + ")");

					}
					catch (final IllegalStateException ile)
					{
						//Ok here
					}

				}
				Mockito.reset(one);
			}
		}
	}


	private void checkSaveNotCalled(final CronJobModel cronJobModel, final CronJobStatus status)
	{
		when(cronJobModel.getStatus()).thenReturn(status);

		service.requestAbortCronJob(cronJobModel);

		verify(cronJobModel, Mockito.never()).setRequestAbort(Boolean.TRUE);
		verify(modelServiceMock, Mockito.never()).save(cronJobModel);

		Mockito.reset(cronJobModel);
	}

	private void checkSaveCalled(final CronJobModel cronJobModel, final CronJobStatus status)
	{
		when(cronJobModel.getStatus()).thenReturn(status);

		service.requestAbortCronJob(cronJobModel);

		verify(cronJobModel, Mockito.times(1)).setRequestAbort(Boolean.TRUE);
		verify(modelServiceMock, Mockito.atLeastOnce()).save(cronJobModel);

		Mockito.reset(cronJobModel);
	}

}
