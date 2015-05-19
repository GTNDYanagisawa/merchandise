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
package de.hybris.platform.acceleratorservices.dataimport.batch.task;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.HeaderTask;


/**
 * Cleanup for the impex import process. Deletes the transformed file and moves the processed file to the archive
 * directory.
 */
public class CleanupTask implements HeaderTask
{
	private CleanupHelper cleanupHelper;

	@Override
	public BatchHeader execute(final BatchHeader header)
	{
		cleanupHelper.cleanup(header, false);
		return null;
	}

	/**
	 * @param cleanupHelper
	 *           the cleanupHelper to set
	 */
	public void setCleanupHelper(final CleanupHelper cleanupHelper)
	{
		this.cleanupHelper = cleanupHelper;
	}

	/**
	 * @return the cleanupHelper
	 */
	protected CleanupHelper getCleanupHelper()
	{
		return cleanupHelper;
	}
}
