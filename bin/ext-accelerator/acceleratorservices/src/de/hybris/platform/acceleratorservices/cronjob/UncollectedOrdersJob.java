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

import de.hybris.platform.acceleratorservices.model.UncollectedOrdersCronJobModel;
import de.hybris.platform.acceleratorservices.order.dao.AcceleratorConsignmentDao;
import de.hybris.platform.acceleratorservices.order.strategies.UncollectedConsignmentsStrategy;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Job reviews existing open orders and looks for uncollected pick up consignments. Then sends reminder email or moves
 * entry to customers service if time threshold has been crossed.
 */
public class UncollectedOrdersJob extends AbstractJobPerformable<UncollectedOrdersCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(UncollectedOrdersJob.class);//NOPMD

	private UncollectedConsignmentsStrategy customerServiceUncollectedConsignmentStrategy;
	private UncollectedConsignmentsStrategy reminderUncollectedConsignmentStrategy;
	private AcceleratorConsignmentDao acceleratorConsignmentDao;
	private Set<String> uncollectedConsignmentStatuses;

	@Override
	public PerformResult perform(final UncollectedOrdersCronJobModel cronJob)
	{
		final List<ConsignmentModel> readyForPickupConsignments = getAcceleratorConsignmentDao().findConsignmentsForStatus(
				Converters.convertAll(getUncollectedConsignmentStatuses(), getStatusConverter()), cronJob.getSites());

		for (final ConsignmentModel consignmentModel : readyForPickupConsignments)
		{
			if (!getReminderUncollectedConsignmentStrategy().processConsignment(consignmentModel))
			{
				getCustomerServiceUncollectedConsignmentStrategy().processConsignment(consignmentModel);
			}
		}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}


	protected Converter<String, ConsignmentStatus> getStatusConverter()
	{
		return new Converter<String, ConsignmentStatus>()
		{
			@Override
			public ConsignmentStatus convert(final String status) throws ConversionException
			{
				return ConsignmentStatus.valueOf(status);
			}

			@Override
			public ConsignmentStatus convert(final String status, final ConsignmentStatus prototype) throws ConversionException
			{
				return ConsignmentStatus.valueOf(status);
			}
		};
	}


	protected AcceleratorConsignmentDao getAcceleratorConsignmentDao()
	{
		return acceleratorConsignmentDao;
	}

	@Required
	public void setAcceleratorConsignmentDao(final AcceleratorConsignmentDao acceleratorConsignmentDao)
	{
		this.acceleratorConsignmentDao = acceleratorConsignmentDao;
	}

	protected Set<String> getUncollectedConsignmentStatuses()
	{
		return uncollectedConsignmentStatuses;
	}

	@Required
	public void setUncollectedConsignmentStatuses(final Set<String> uncollectedConsignmentStatuses)
	{
		this.uncollectedConsignmentStatuses = uncollectedConsignmentStatuses;
	}

	protected UncollectedConsignmentsStrategy getReminderUncollectedConsignmentStrategy()
	{
		return reminderUncollectedConsignmentStrategy;
	}

	@Required
	public void setReminderUncollectedConsignmentStrategy(
			final UncollectedConsignmentsStrategy reminderUncollectedConsignmentStrategy)
	{
		this.reminderUncollectedConsignmentStrategy = reminderUncollectedConsignmentStrategy;
	}

	protected UncollectedConsignmentsStrategy getCustomerServiceUncollectedConsignmentStrategy()
	{
		return customerServiceUncollectedConsignmentStrategy;
	}

	@Required
	public void setCustomerServiceUncollectedConsignmentStrategy(
			final UncollectedConsignmentsStrategy customerServiceUncollectedConsignmentStrategy)
	{
		this.customerServiceUncollectedConsignmentStrategy = customerServiceUncollectedConsignmentStrategy;
	}
}