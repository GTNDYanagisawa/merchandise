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
package de.hybris.platform.sap.productconfig.hmc.dataloader.configuration;

import com.sap.custdev.projects.fbs.slc.dataloader.settings.IDataloaderConfiguration;
import com.sap.custdev.projects.fbs.slc.dataloader.settings.IDataloaderSource;
import com.sap.custdev.projects.fbs.slc.dataloader.settings.IDataloaderTarget;
import com.sap.custdev.projects.fbs.slc.dataloader.settings.IInitialDownloadConfiguration;


public class DataloaderConfiguration implements IDataloaderConfiguration
{

	private IInitialDownloadConfiguration initialDownloadConfiguration;
	private IDataloaderSource source;
	private IDataloaderTarget target;

	public void setInitialDownloadConfiguration(final IInitialDownloadConfiguration initialDownloadConfiguration)
	{
		this.initialDownloadConfiguration = initialDownloadConfiguration;
	}

	@Override
	public IInitialDownloadConfiguration getInitialDownloadConfiguration()
	{
		return initialDownloadConfiguration;
	}

	public void setSource(final IDataloaderSource source)
	{
		this.source = source;
	}

	@Override
	public IDataloaderSource getSource()
	{
		return source;
	}

	public void setTarget(final IDataloaderTarget target)
	{
		this.target = target;
	}

	@Override
	public IDataloaderTarget getTarget()
	{
		return target;
	}
}
