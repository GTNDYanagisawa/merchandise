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
package de.hybris.platform.webservices.resources.commands;


import org.apache.log4j.Logger;

import de.hybris.platform.cronjob.dto.CronJobDTO;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.webservices.AbstractCommand;
import de.hybris.platform.webservices.BadRequestException;
import de.hybris.platform.webservices.WsUtilService;


/**
 * the Command for creating new cronjob based on current cronjob
 */
public class CreateCronJobCommand extends AbstractCommand<CronJobModel>
{
	private static final Logger LOG = Logger.getLogger(CreateCronJobCommand.class);

	@Override
	public CronJobModel execute(final CronJobModel resourceModel, final Object requestDTO)
	{
		// expect at least 'code' as parameter from passed request dto
		final String code = ((CronJobDTO) requestDTO).getCode();
		if (code == null || code.equals(""))
		{
			// exceptions can be thrown freely, they are getting handled by the framework
			throw new BadRequestException("CreateCronJobCommand on (" + resourceModel.getCode()
					+ ") failed. Set the code for a new cronjob.");
		}

		// create a new cronjob
		// take existing cronjob of this resource as template, replace property values which are given with request DTO
		final WsUtilService wsService = getServiceLocator().getWsUtilService();
		final CronJobModel cronJob = wsService.createCronJobBasedOnCurrent(resourceModel, code);
		if (cronJob == null)
		{
			throw new BadRequestException("CreateCronJobCommand on (" + resourceModel.getCode()
					+ ") failed. A cronjob with requested code already exists.");
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("CreateCronJobCommand on (" + resourceModel.getCode() + ") is completed.");
			}
		}

		return cronJob;
	}

}
