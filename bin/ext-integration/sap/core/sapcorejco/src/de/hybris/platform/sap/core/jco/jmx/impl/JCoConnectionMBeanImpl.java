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

package de.hybris.platform.sap.core.jco.jmx.impl;

import de.hybris.platform.jmx.mbeans.impl.AbstractJMXMBean;
import de.hybris.platform.sap.core.jco.jmx.JCoConnectionMBean;
import de.hybris.platform.sap.core.jco.monitor.provider.JCoConnectionMonitorLocalProvider;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;


/**
 * JCoConnection monitoring JMX MBean.
 */
@ManagedResource(description = "Overview of the SAP JCo Connections of the current node.")
public class JCoConnectionMBeanImpl extends AbstractJMXMBean implements JCoConnectionMBean
{

	private JCoConnectionMonitorLocalProvider localMonitorProvider;

	/**
	 * Injection setter for {@link JCoConnectionMonitorLocalProvider}.
	 * 
	 * @param localMonitorProvider
	 *           the localMonitorProvider to set
	 */
	@Required
	public void setLocalMonitorProvider(final JCoConnectionMonitorLocalProvider localMonitorProvider)
	{
		this.localMonitorProvider = localMonitorProvider;
	}

	@Override
	@ManagedAttribute(description = "Shows the total number of JCo connections of the current node.")
	public Integer getTotalCount()
	{
		return new TenantAwareExecutor<Integer>()
		{
			@Override
			protected Integer doExecute()
			{
				return localMonitorProvider.getTotalCount();
			}
		}.getResult();
	}

	@Override
	@ManagedAttribute(description = "Shows the number of JCo connections with a long lifetime of the current node.")
	public Integer getLongRunnerCount()
	{
		return new TenantAwareExecutor<Integer>()
		{
			@Override
			protected Integer doExecute()
			{
				return localMonitorProvider.getLongRunnerCount();
			}
		}.getResult();
	}

	@Override
	@ManagedAttribute(description = "Shows the number of RFC destinations whose pool size is reached of the current node.")
	public Integer getPoolLimitReachedCount()
	{
		return new TenantAwareExecutor<Integer>()
		{
			@Override
			protected Integer doExecute()
			{
				return localMonitorProvider.getPoolLimitReachedCount();
			}
		}.getResult();
	}

	@Override
	@ManagedOperation(description = "Returns all current JCo connections of the current node as XML string.")
	public String createSnapshotXML()
	{
		return new TenantAwareExecutor<String>()
		{
			@Override
			protected String doExecute()
			{
				return localMonitorProvider.createSnapshotXML();
			}
		}.getResult();
	}

	@Override
	@ManagedOperation(description = "Create JCo connections snapshot file of the current node.")
	public String createSnapshotFile()
	{
		{
			return new TenantAwareExecutor<String>()
			{
				@Override
				protected String doExecute()
				{
					return localMonitorProvider.createSnapshotFile();
				}
			}.getResult();
		}
	}

}
