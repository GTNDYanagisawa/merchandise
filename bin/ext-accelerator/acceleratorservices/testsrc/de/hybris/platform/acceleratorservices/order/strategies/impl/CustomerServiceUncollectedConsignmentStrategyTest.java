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
package de.hybris.platform.acceleratorservices.order.strategies.impl;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 */
public class CustomerServiceUncollectedConsignmentStrategyTest
{
	private static final Logger LOG = Logger.getLogger(CustomerServiceUncollectedConsignmentStrategyTest.class);//NOPMD


	private ReminderUncollectedConsignmentStrategy customerServiceUncollectedConsignmentStrategy;
	private final Integer timeThreshold = Integer.valueOf(50);
	private Date referenceDate;

	@Mock
	private BusinessProcessService businessProcessService;
	@Mock
	private ModelService modelService;
	@Mock
	private ConsignmentModel consignmentModel;
	@Mock
	private BusinessProcessModel businessProcessModel;
	@Mock
	private ConsignmentProcessModel consignmentProcessModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		customerServiceUncollectedConsignmentStrategy = new ReminderUncollectedConsignmentStrategy();
		customerServiceUncollectedConsignmentStrategy.setBusinessProcessService(businessProcessService);
		customerServiceUncollectedConsignmentStrategy.setModelService(modelService);
		customerServiceUncollectedConsignmentStrategy.setTimeThreshold(timeThreshold);

		referenceDate = new Date();
	}


	@Test
	public void testProcessConsignmentNOK()
	{
		BDDMockito.given(consignmentModel.getShippingDate()).willReturn(
				DateUtils.addHours(referenceDate, 0 - timeThreshold.intValue() + 1));

		final boolean result = customerServiceUncollectedConsignmentStrategy.processConsignment(consignmentModel);

		Assert.assertFalse(result);
	}


	@Test
	public void testProcessConsignmentOKBusinessProcessExists()
	{
		BDDMockito.given(consignmentModel.getShippingDate()).willReturn(
				DateUtils.addHours(referenceDate, 0 - timeThreshold.intValue() - 1));
		BDDMockito.given(businessProcessService.getProcess(Mockito.anyString())).willReturn(businessProcessModel);

		final boolean result = customerServiceUncollectedConsignmentStrategy.processConsignment(consignmentModel);

		Assert.assertFalse(result);
	}

	@Test
	public void testProcessConsignmentOKNoBusinessProcessExists()
	{
		BDDMockito.given(consignmentModel.getShippingDate()).willReturn(
				DateUtils.addHours(referenceDate, 0 - timeThreshold.intValue() - 1));
		BDDMockito.given(businessProcessService.getProcess(Mockito.anyString())).willReturn(null);
		BDDMockito.given(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString())).willReturn(
				consignmentProcessModel);

		customerServiceUncollectedConsignmentStrategy.processConsignment(consignmentModel);

		Assert.assertTrue(true);
		Mockito.verify(consignmentProcessModel).setConsignment(consignmentModel);
		Mockito.verify(modelService).save(consignmentProcessModel);
		Mockito.verify(businessProcessService).startProcess(consignmentProcessModel);
	}
}
