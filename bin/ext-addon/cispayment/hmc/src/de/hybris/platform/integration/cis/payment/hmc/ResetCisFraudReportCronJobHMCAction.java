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
package de.hybris.platform.integration.cis.payment.hmc;


import de.hybris.platform.hmc.util.action.ActionEvent;
import de.hybris.platform.hmc.util.action.ActionResult;
import de.hybris.platform.hmc.util.action.ItemAction;
import de.hybris.platform.integration.cis.payment.jalo.CisFraudReportCronJob;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;


public class ResetCisFraudReportCronJobHMCAction extends ItemAction
{
	@Override
	public ActionResult perform(final ActionEvent actionEvent) throws JaloBusinessException
	{
		final Item item = getItem(actionEvent);
		if (item instanceof CisFraudReportCronJob)
		{
			final CisFraudReportCronJob fraudReportCronJob = (CisFraudReportCronJob) item;
			fraudReportCronJob.setLastFraudReportEndTime(null);

			return new ActionResult(ActionResult.OK, true, false);
		}
		return new ActionResult(ActionResult.FAILED, false, false);
	}
}
