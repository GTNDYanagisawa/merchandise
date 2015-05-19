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

import de.hybris.platform.acceleratorservices.dataexport.generic.config.ConfigurableSessionFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.integration.Message;
import org.springframework.integration.MessageDeliveryException;
import org.springframework.integration.MessagingException;
import org.springframework.integration.file.DefaultFileNameGenerator;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * A basic version of springs default ftp FileTransferringMessageHandler.
 */
public class DefaultUploadTransportService extends AbstractMessageHandler
{
	private ConfigurableSessionFactory sessionFactory;
	private FileNameGenerator fileNameGenerator = new DefaultFileNameGenerator();
	private String remoteDirectory = ".";
	private String remoteFileSeparator = "/";
	private String temporaryFileSuffix = ".writing";
	private boolean autoCreateDirectory = true;


	/**
	 * create a ftp session with information for the connection from the message's header then send the file
	 * 
	 * @param message
	 * @throws Exception
	 */
	@Override
	protected void handleMessageInternal(final Message<?> message) throws Exception
	{
		final Object payload = message.getPayload();
		if (payload instanceof File)
		{
			final File file = (File) payload;
			if (file.exists())
			{
				final Session session = sessionFactory.getSession(message);
				try
				{
					final String fileName = fileNameGenerator.generateFileName(message);
					sendFileToRemoteDirectory(file, remoteDirectory, fileName, session);
				}
				catch (final FileNotFoundException e)
				{
					throw new MessageDeliveryException(message, "File [" + file
							+ "] not found in local working directory; it was moved or deleted unexpectedly.", e);
				}
				catch (final IOException e)
				{
					throw new MessageDeliveryException(message, "Failed to transfer file [" + file
							+ "] from local working directory to remote FTP directory.", e);
				}
				catch (final Exception e)
				{
					throw new MessageDeliveryException(message, "Error handling message for file [" + file + "]", e);
				}
				finally
				{
					if (!(message.getPayload() instanceof File && file.exists()))
					{
						// we created the File, so we need to delete it
						try
						{
							file.delete();
						}
						catch (final Throwable th)
						{
							// ignore
						}
					}
					if (session != null)
					{
						session.close();
					}
				}
			}
		}
	}

	protected void sendFileToRemoteDirectory(final File file, String remoteDirectory, final String fileName, final Session session)
			throws FileNotFoundException, IOException
	{

		final FileInputStream fileInputStream = new FileInputStream(file);
		if (!StringUtils.hasText(remoteDirectory))
		{
			remoteDirectory = "";
		}
		else if (!remoteDirectory.endsWith(remoteFileSeparator))
		{
			remoteDirectory += remoteFileSeparator;
		}
		final String remoteFilePath = remoteDirectory + fileName;
		// write remote file first with .writing extension
		final String tempFilePath = remoteFilePath + temporaryFileSuffix;

		if (autoCreateDirectory)
		{
			ensureDirectoryExists(session, remoteDirectory, remoteDirectory);
		}
		logger.info("sending file: " + file.getName() + " to remote directory: " + remoteDirectory);
		session.write(fileInputStream, tempFilePath);
		fileInputStream.close();
		// then rename it to its final name
		session.rename(tempFilePath, remoteFilePath);
	}

	protected void ensureDirectoryExists(final Session session, String remoteDirectory, final String originalRemoteDirectory)
	{
		try
		{
			session.list(remoteDirectory);
		}
		catch (final IOException e)
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("Directory '" + remoteDirectory + "' does not exist. Will attempt to auto-create it");
			}
			final int nextSeparatorIndex = remoteDirectory.lastIndexOf(this.remoteFileSeparator);
			if (nextSeparatorIndex <= 0)
			{
				throw new MessagingException("Failed to auto-create directory '" + originalRemoteDirectory + "'");
			}
			else
			{
				remoteDirectory = remoteDirectory.substring(0, nextSeparatorIndex);
				this.ensureDirectoryExists(session, remoteDirectory, originalRemoteDirectory);
			}
		}
		final String missingDirectoryPath = originalRemoteDirectory.substring(remoteDirectory.length());
		final String[] directories = StringUtils.tokenizeToStringArray(missingDirectoryPath, this.remoteFileSeparator);
		String directory = remoteDirectory + this.remoteFileSeparator;
		for (final String directorySegment : directories)
		{
			directory += directorySegment + this.remoteFileSeparator;
			if (logger.isDebugEnabled())
			{
				logger.debug("Creating '" + directory + "'");
			}
			try
			{
				session.mkdir(directory);
			}
			catch (final Exception e)
			{
				throw new MessagingException("Failed to auto-create directory '" + directory + "'");
			}
		}
	}


	protected String getRemoteDirectory()
	{
		return remoteDirectory;
	}

	public void setRemoteDirectory(final String remoteDirectory)
	{
		this.remoteDirectory = remoteDirectory;
	}

	protected FileNameGenerator getFileNameGenerator()
	{
		return fileNameGenerator;
	}

	public void setFileNameGenerator(final FileNameGenerator fileNameGenerator)
	{
		this.fileNameGenerator = (fileNameGenerator != null) ? fileNameGenerator : new DefaultFileNameGenerator();
	}

	protected String getRemoteFileSeparator()
	{
		return remoteFileSeparator;
	}

	public void setRemoteFileSeparator(final String remoteFileSeparator)
	{
		Assert.hasText(remoteFileSeparator, "'remoteFileSeparator' must not be empty");
		this.remoteFileSeparator = remoteFileSeparator;
	}

	protected boolean isAutoCreateDirectory()
	{
		return autoCreateDirectory;
	}

	public void setAutoCreateDirectory(final boolean autoCreateDirectory)
	{
		this.autoCreateDirectory = autoCreateDirectory;
	}

	protected ConfigurableSessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public void setSessionFactory(final ConfigurableSessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	protected String getTemporaryFileSuffix()
	{
		return temporaryFileSuffix;
	}

	public void setTemporaryFileSuffix(final String temporaryFileSuffix)
	{
		this.temporaryFileSuffix = temporaryFileSuffix;
	}
}
