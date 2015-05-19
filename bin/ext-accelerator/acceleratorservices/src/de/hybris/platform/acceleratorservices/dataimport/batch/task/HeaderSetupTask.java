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

import java.io.File;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Initially setup the batch header. The header is used throughout the pipeline as a reference and for cleanup.
 */
public class HeaderSetupTask
{
	protected String storeBaseDirectory;
	protected String catalog;
	protected boolean net;

	/**
	 * Initially creates the header.
	 * 
	 * @param file
	 * @return the header
	 */
	public BatchHeader execute(final File file)
	{
		Assert.notNull(file);
		final BatchHeader result = new BatchHeader();
		result.setFile(file);
		result.setStoreBaseDirectory(storeBaseDirectory);
		result.setCatalog(catalog);
		result.setNet(net);
		return result;
	}

	public String getStoreBaseDirectory()
	{
		return storeBaseDirectory;
	}

	@Required
	public void setStoreBaseDirectory(final String storeBaseDirectory)
	{
		this.storeBaseDirectory = storeBaseDirectory;
	}

	public String getCatalog()
	{
		return catalog;
	}

	@Required
	public void setCatalog(final String catalog)
	{
		this.catalog = catalog;
	}

	public boolean isNet()
	{
		return net;
	}

	@Required
	public void setNet(final boolean net)
	{
		this.net = net;
	}
}
