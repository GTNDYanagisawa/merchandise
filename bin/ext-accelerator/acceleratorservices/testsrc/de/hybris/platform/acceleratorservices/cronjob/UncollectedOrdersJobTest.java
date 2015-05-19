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
package de.hybris.platform.acceleratorservices.cronjob;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.UncollectedOrdersCronJobModel;
import de.hybris.platform.acceleratorservices.order.dao.AcceleratorConsignmentDao;
import de.hybris.platform.acceleratorservices.order.strategies.UncollectedConsignmentsStrategy;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 */
@UnitTest
public class UncollectedOrdersJobTest
{
	private static final Logger LOG = Logger.getLogger(UncollectedOrdersJobTest.class);//NOPMD

	private static final String BASESITE = "testBasesite";

	private UncollectedOrdersJob uncollectedOrdersJob;


	@Mock
	private UncollectedConsignmentsStrategy customerServiceUncollectedConsignmentStrategy;
	@Mock
	private UncollectedConsignmentsStrategy reminderUncollectedConsignmentStrategy;
	@Mock
	private AcceleratorConsignmentDao acceleratorConsignmentDao;


	@Mock
	private BaseSiteModel testBaseSite;
	@Mock
	private UncollectedOrdersCronJobModel uncollectedOrdersCronJobModel;
	@Mock
	private ConsignmentModel freshConsignment;
	@Mock
	private ConsignmentModel remindConsignment;
	@Mock
	private ConsignmentModel cancelledConsignment;

	private List<ConsignmentModel> consignments;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		uncollectedOrdersJob = new UncollectedOrdersJob();
		uncollectedOrdersJob.setAcceleratorConsignmentDao(acceleratorConsignmentDao);
		uncollectedOrdersJob.setCustomerServiceUncollectedConsignmentStrategy(customerServiceUncollectedConsignmentStrategy);
		uncollectedOrdersJob.setReminderUncollectedConsignmentStrategy(reminderUncollectedConsignmentStrategy);
		uncollectedOrdersJob.setUncollectedConsignmentStatuses(new HashSet<String>());
		uncollectedOrdersJob.getUncollectedConsignmentStatuses().add(ConsignmentStatus.READY_FOR_PICKUP.getCode());

		BDDMockito.given(testBaseSite.getUid()).willReturn(BASESITE);
		BDDMockito.given(uncollectedOrdersCronJobModel.getSites()).willReturn(Collections.singletonList(testBaseSite));

		consignments = new ArrayList<>();
	}

	@Test
	public void testNoConsignments() throws Exception
	{
		BDDMockito.given(
				acceleratorConsignmentDao.findConsignmentsForStatus(Collections.singletonList(ConsignmentStatus.READY_FOR_PICKUP),
						Collections.singletonList(testBaseSite))).willReturn(Collections.EMPTY_LIST);

		final PerformResult result = uncollectedOrdersJob.perform(uncollectedOrdersCronJobModel);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		Mockito.verify(customerServiceUncollectedConsignmentStrategy, Mockito.times(0)).processConsignment(
				Mockito.any(ConsignmentModel.class));
		Mockito.verify(reminderUncollectedConsignmentStrategy, Mockito.times(0)).processConsignment(
				Mockito.any(ConsignmentModel.class));
	}

	@Test
	public void testConsigmentsNoTimeThreshold()
	{
		consignments.add(freshConsignment);

		BDDMockito.given(
				acceleratorConsignmentDao.findConsignmentsForStatus(Collections.singletonList(ConsignmentStatus.READY_FOR_PICKUP),
						Collections.singletonList(testBaseSite))).willReturn(consignments);
		BDDMockito.given(Boolean.valueOf(reminderUncollectedConsignmentStrategy.processConsignment(remindConsignment))).willReturn(
				Boolean.FALSE);
		BDDMockito.given(Boolean.valueOf(customerServiceUncollectedConsignmentStrategy.processConsignment(remindConsignment)))
				.willReturn(Boolean.FALSE);

		final PerformResult result = uncollectedOrdersJob.perform(uncollectedOrdersCronJobModel);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		Mockito.verify(customerServiceUncollectedConsignmentStrategy, Mockito.times(1)).processConsignment(freshConsignment);
		Mockito.verify(reminderUncollectedConsignmentStrategy, Mockito.times(1)).processConsignment(freshConsignment);
	}


	@Test
	public void testConsigmentsReminderThreshold()
	{
		consignments.add(remindConsignment);

		BDDMockito.given(
				acceleratorConsignmentDao.findConsignmentsForStatus(Collections.singletonList(ConsignmentStatus.READY_FOR_PICKUP),
						Collections.singletonList(testBaseSite))).willReturn(consignments);
		BDDMockito.given(Boolean.valueOf(reminderUncollectedConsignmentStrategy.processConsignment(remindConsignment))).willReturn(
				Boolean.TRUE);

		final PerformResult result = uncollectedOrdersJob.perform(uncollectedOrdersCronJobModel);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		Mockito.verify(customerServiceUncollectedConsignmentStrategy, Mockito.times(0)).processConsignment(
				Mockito.any(ConsignmentModel.class));
		Mockito.verify(reminderUncollectedConsignmentStrategy, Mockito.times(1)).processConsignment(remindConsignment);
	}

	@Test
	public void testConsigmentsCancelThreshold()
	{
		consignments.add(cancelledConsignment);

		BDDMockito.given(
				acceleratorConsignmentDao.findConsignmentsForStatus(Collections.singletonList(ConsignmentStatus.READY_FOR_PICKUP),
						Collections.singletonList(testBaseSite))).willReturn(consignments);
		BDDMockito.given(Boolean.valueOf(reminderUncollectedConsignmentStrategy.processConsignment(remindConsignment))).willReturn(
				Boolean.FALSE);

		final PerformResult result = uncollectedOrdersJob.perform(uncollectedOrdersCronJobModel);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		Mockito.verify(customerServiceUncollectedConsignmentStrategy, Mockito.times(1)).processConsignment(cancelledConsignment);
		Mockito.verify(reminderUncollectedConsignmentStrategy, Mockito.times(1)).processConsignment(cancelledConsignment);
	}


	@Test
	public void testConsigmentsMixed()
	{
		consignments.add(freshConsignment);
		consignments.add(remindConsignment);
		consignments.add(cancelledConsignment);

		BDDMockito.given(
				acceleratorConsignmentDao.findConsignmentsForStatus(Collections.singletonList(ConsignmentStatus.READY_FOR_PICKUP),
						Collections.singletonList(testBaseSite))).willReturn(consignments);
		BDDMockito.given(Boolean.valueOf(reminderUncollectedConsignmentStrategy.processConsignment(freshConsignment))).willReturn(
				Boolean.FALSE);
		BDDMockito.given(Boolean.valueOf(reminderUncollectedConsignmentStrategy.processConsignment(remindConsignment))).willReturn(
				Boolean.TRUE);
		BDDMockito.given(Boolean.valueOf(reminderUncollectedConsignmentStrategy.processConsignment(cancelledConsignment)))
				.willReturn(Boolean.FALSE);
		BDDMockito.given(Boolean.valueOf(customerServiceUncollectedConsignmentStrategy.processConsignment(freshConsignment)))
				.willReturn(Boolean.FALSE);
		BDDMockito.given(Boolean.valueOf(customerServiceUncollectedConsignmentStrategy.processConsignment(remindConsignment)))
				.willReturn(Boolean.FALSE);
		BDDMockito.given(Boolean.valueOf(customerServiceUncollectedConsignmentStrategy.processConsignment(cancelledConsignment)))
				.willReturn(Boolean.TRUE);

		final PerformResult result = uncollectedOrdersJob.perform(uncollectedOrdersCronJobModel);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		Mockito.verify(customerServiceUncollectedConsignmentStrategy, Mockito.times(1)).processConsignment(freshConsignment);
		Mockito.verify(reminderUncollectedConsignmentStrategy, Mockito.times(1)).processConsignment(freshConsignment);
		Mockito.verify(customerServiceUncollectedConsignmentStrategy, Mockito.times(0)).processConsignment(remindConsignment);
		Mockito.verify(reminderUncollectedConsignmentStrategy, Mockito.times(1)).processConsignment(remindConsignment);
		Mockito.verify(customerServiceUncollectedConsignmentStrategy, Mockito.times(1)).processConsignment(cancelledConsignment);
		Mockito.verify(reminderUncollectedConsignmentStrategy, Mockito.times(1)).processConsignment(cancelledConsignment);
	}
}
