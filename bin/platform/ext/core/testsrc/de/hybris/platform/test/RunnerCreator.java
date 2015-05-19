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
package de.hybris.platform.test;

/**
 * Factory to create specific logic for each worker.
 */
public interface RunnerCreator<T extends Runnable>
{
	/**
	 * Creates a new runnable object for the given runner thread number.
	 */
	T newRunner(int threadNumber);
}