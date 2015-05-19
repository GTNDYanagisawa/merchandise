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
package de.hybris.platform.catalog.job.strategy;

import de.hybris.platform.servicelayer.cronjob.PerformResult;


/**
 * Strategy abstraction for removing a given instance <code>T</code> related data.
 * 
 * @since 4.3
 */
public interface RemoveStrategy<T>
{
	PerformResult remove(T type);
}
