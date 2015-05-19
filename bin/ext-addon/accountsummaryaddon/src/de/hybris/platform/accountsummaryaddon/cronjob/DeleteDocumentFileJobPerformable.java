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
package de.hybris.platform.accountsummaryaddon.cronjob;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import de.hybris.platform.accountsummaryaddon.document.service.B2BDocumentService;
import de.hybris.platform.accountsummaryaddon.model.cronjob.DeleteDocumentFileCronJobModel;


public class DeleteDocumentFileJobPerformable extends AbstractJobPerformable<DeleteDocumentFileCronJobModel>
{

	@Resource
	private B2BDocumentService b2bDocumentService;

	private static final Logger LOG = Logger.getLogger(DeleteDocumentFileJobPerformable.class.getName());

	@Override
	public PerformResult perform(final DeleteDocumentFileCronJobModel arg0)
	{

		 // if arg0.getNumberOfDay() set to null by mistake, make sure not to delete any media files. so set the number of
		 // day to 1000 years(365*1000)

		int numberOfDay = 365 * 1000;

		if (arg0.getNumberOfDay() == null)
		{
			LOG.error("[perform] number of days = null => set to 365*1000 in order to not delete any media files.");
		}
		else
		{
			numberOfDay = arg0.getNumberOfDay();
		}


		b2bDocumentService.deleteB2BDocumentFiles(numberOfDay, arg0.getDocumentTypeList(), arg0.getDocumentStatusList());

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

	}

}
