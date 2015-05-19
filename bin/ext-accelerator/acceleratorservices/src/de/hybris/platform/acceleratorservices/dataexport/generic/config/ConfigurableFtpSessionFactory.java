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
package de.hybris.platform.acceleratorservices.dataexport.generic.config;

import de.hybris.platform.acceleratorservices.model.export.ExportDataCronJobModel;

import org.apache.commons.net.ftp.FTPFile;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;


/**
 * Customized FTP Session factory that configures the FTP session directly (not via spring xml unlike Default) before
 * creating the session.
 */
public class ConfigurableFtpSessionFactory extends DefaultFtpSessionFactory implements ConfigurableSessionFactory<FTPFile>
{
	@Override
	public Session<FTPFile> getSession(final Message<?> message)
	{
		final MessageHeaders headers = message.getHeaders();

		if (headers.containsKey("cronjob"))
		{
			final Object object = headers.get("cronjob");
			if (object instanceof ExportDataCronJobModel)
			{
				final ExportDataCronJobModel cronJobModel = (ExportDataCronJobModel) object;

				//set the session factory settings here
				this.setHost(cronJobModel.getThirdPartyHost());
				this.setUsername(cronJobModel.getThirdPartyUsername());
				this.setPassword(cronJobModel.getThirdPartyPassword());
			}
		}

		return getSession();
	}
}
