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
package de.hybris.platform.jobs;

import de.hybris.platform.core.PK;
import de.hybris.platform.cronjob.model.CronJobModel;

import java.util.Iterator;


/**
 * Abstraction for the {@link Iterator} over {@link PK} with additional init/dispose logic.
 */
public interface DisposableRemovedItemPKProcessor<T extends CronJobModel> extends Iterator<PK>
{
	/**
	 * Method for initializing iterator with {@link CronJobModel} specific data.
	 */
	void init(T model);

	/**
	 * Method for disposing {@link CronJobModel} specific init data ( e.g. closing streams).
	 */
	void dispose();
}
