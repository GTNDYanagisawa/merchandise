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
package de.hybris.platform.acceleratorservices.dataexport.generic;

import de.hybris.platform.cronjob.model.CronJobModel;

import java.io.File;
import java.util.List;

import org.springframework.integration.Message;
import org.springframework.integration.message.ErrorMessage;


/**
 * Service that deals with the finding and transfer of the generated files.
 */
public interface UploadDataProcessorService
{
	/**
	 * Find files matching the provided regex in the specified directory
	 * 
	 * @param message
	 *           the message
	 * @param filenameRegex
	 *           filename regular expression
	 * @param directory
	 *           the directory path to search within
	 * @return a message that contains a list of found files
	 */
	Message<List<File>> findFiles(Message<?> message, String filenameRegex, String directory);

	/**
	 * get the cronjob configuration that generates this file
	 * 
	 * @param message
	 *           message that contains the file
	 * @return the cronjob configuration
	 */
	CronJobModel getUploadCronJob(Message<File> message);

	/**
	 * handle any exceptions that occur during transport.
	 * 
	 * @param message
	 * @return the file that had the issue
	 */
	File handlerError(ErrorMessage message);
}
