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
package de.hybris.platform.acceleratorservices.dataexport.generic.impl;

import de.hybris.platform.acceleratorservices.dataexport.generic.UploadDataProcessorService;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobDao;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.message.ErrorMessage;
import org.springframework.integration.message.GenericMessage;


/**
 * Default implementation of {@link UploadDataProcessorService}.
 */
public class DefaultUploadDataProcessorService implements UploadDataProcessorService
{
	private static final Logger LOG = Logger.getLogger(DefaultUploadDataProcessorService.class);
	private CronJobDao cronJobDao;

	protected CronJobDao getCronJobDao()
	{
		return cronJobDao;
	}

	@Required
	public void setCronJobDao(final CronJobDao cronJobDao)
	{
		this.cronJobDao = cronJobDao;
	}

	@Override
	public Message<List<File>> findFiles(final Message<?> message, @Header(value = "filenameRegex") final String filenameRegex,
			@Header(value = "directory") final String directory)
	{
		final File directoryPath = new File(directory);
		if (directoryPath.exists())
		{
			final File[] files = directoryPath.listFiles(new PatternFilenameFilter(filenameRegex));
			final List<File> detectedFiles = Arrays.asList(files);
			LOG.info("Found " + detectedFiles.size() + " Files in directory");
			return new GenericMessage<List<File>>(detectedFiles);
		}
		LOG.info("No files found");
		return new GenericMessage<List<File>>(new ArrayList<File>());
	}

	@Override
	public CronJobModel getUploadCronJob(final Message<File> message)
	{
		final File payload = message.getPayload();
		final String filename = payload.getName();
		final String[] filenameParts = filename.split("-");
		if (filenameParts.length > 1)
		{
			final String cronJobCode = filenameParts[0];
			final List<CronJobModel> cronJobModels = cronJobDao.findCronJobs(cronJobCode);
			if (!cronJobModels.isEmpty())
			{
				return cronJobModels.get(0);
			}
			LOG.warn("Did not find config for file: " + filename);
		}
		return null;
	}

	@Override
	public File handlerError(final ErrorMessage message)
	{
		LOG.info("Error with file: " + message.getPayload().getMessage());
		final Throwable payload = message.getPayload();
		if (payload instanceof MessagingException)
		{
			final MessagingException messagingException = (MessagingException) payload;
			final Message<?> failedMessage = messagingException.getFailedMessage();
			final Object failedMessagePayload = failedMessage.getPayload();
			if (failedMessagePayload instanceof File)
			{
				LOG.info("Moving " + ((File) failedMessagePayload).getName() + " to error folder");
				return (File) failedMessagePayload;
			}
		}
		return null;
	}

	public static class PatternFilenameFilter implements FilenameFilter
	{
		private final Pattern pattern;

		public PatternFilenameFilter(final String patternStr)
		{
			pattern = Pattern.compile(patternStr);
		}

		protected Pattern getPattern()
		{
			return pattern;
		}

		@Override
		public boolean accept(final File dir, final String fileName)
		{
			return getPattern().matcher(fileName).matches();
		}
	}
}
