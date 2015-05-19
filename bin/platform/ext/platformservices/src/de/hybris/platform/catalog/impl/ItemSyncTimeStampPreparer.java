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
package de.hybris.platform.catalog.impl;

import de.hybris.platform.catalog.model.ItemSyncTimestampModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;


/**
 * PrepareInterceptor for the {@link ItemSyncTimestampModel}. Sets the source and target catalog versions.
 */
public class ItemSyncTimeStampPreparer implements PrepareInterceptor
{
	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof ItemSyncTimestampModel)
		{
			final ItemSyncTimestampModel modelImpl = (ItemSyncTimestampModel) model;
			if (modelImpl.getSyncJob() == null)
			{
				if (modelImpl.getSourceVersion() != null && modelImpl.getTargetVersion() != null)
				{
					final SyncItemJobModel trivialSynItemModel = new SyncItemJobModel();
					trivialSynItemModel.setSourceVersion(modelImpl.getSourceVersion());
					trivialSynItemModel.setTargetVersion(modelImpl.getTargetVersion());
					modelImpl.setSyncJob(trivialSynItemModel);
				}
			}
			// b) job -> set src and tgt version automatically
			else
			{
				final SyncItemJobModel syncJobMode = modelImpl.getSyncJob();
				if (modelImpl.getSourceVersion() == null)
				{
					modelImpl.setSourceVersion(syncJobMode.getSourceVersion());
				}
				if (modelImpl.getTargetVersion() == null)
				{
					modelImpl.setTargetVersion(syncJobMode.getTargetVersion());
				}
			}
		}
	}
}
