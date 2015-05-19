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

import com.sap.custdev.projects.fbs.slc.dataloader.settings.IClientSetting;
import com.sap.custdev.projects.fbs.slc.dataloader.settings.IDataloaderSource;
import com.sap.custdev.projects.fbs.slc.dataloader.settings.IEccSetting;


public class DataloaderSource implements IDataloaderSource
{
	private final boolean loadBalanced;
	private final String sid;
	private final String messageServer;
	private String group;
	private final String rfcDestination;
	private final ClientSettings clientSetting;

	public DataloaderSource(final String instanceno, final String host, final String user, final String password,
			final String client, final String rfcDestination)
	{
		this.sid = instanceno;
		this.messageServer = host;
		this.clientSetting = new ClientSettings(client, user, password);
		this.rfcDestination = rfcDestination;
		loadBalanced = false;
	}

	public DataloaderSource(final String sid, final String messageServer, final String group, final String user,
			final String password, final String client, final String rfcDestination)
	{
		this.sid = sid;
		this.messageServer = messageServer;
		this.group = group;
		this.clientSetting = new ClientSettings(client, user, password);
		this.rfcDestination = rfcDestination;
		loadBalanced = true;
	}

	@Override
	public IClientSetting getClientSetting()
	{
		return clientSetting;
	}

	@Override
	public IEccSetting getEccSetting()
	{
		final IEccSetting eccSetting = new IEccSetting()
		{

			@Override
			public boolean isLoadBalanced()
			{
				return loadBalanced;
			}

			@Override
			public String getSid()
			{
				return sid;
			}

			@Override
			public String getMessageServer()
			{
				return messageServer;
			}

			@Override
			public String getGroup()
			{
				return group;
			}
		};

		return eccSetting;
	}

	@Override
	public String getRfcDestination()
	{
		return rfcDestination;
	}

}
