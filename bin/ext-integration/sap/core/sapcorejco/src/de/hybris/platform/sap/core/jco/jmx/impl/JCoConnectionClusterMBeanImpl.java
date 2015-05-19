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
import de.hybris.platform.sap.core.jco.jmx.JCoConnectionClusterMBean;
import de.hybris.platform.sap.core.jco.monitor.event.JCoConnectionsSnapshotClusterHandler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;


/**
 * JCoConnection cluster monitoring JMX MBean.
 */
@ManagedResource(description = "Overview of the SAP JCo Connections of all nodes of the cluster.")
public class JCoConnectionClusterMBeanImpl extends AbstractJMXMBean implements JCoConnectionClusterMBean
{

	private JCoConnectionsSnapshotClusterHandler snapshotClusterHandler;

	/**
	 * Injection setter for {@link JCoConnectionsSnapshotClusterHandler}.
	 * 
	 * @param snapshotClusterHandler
	 *           the snapshotClusterHandler to set
	 */
	@Required
	public void setSnapshotClusterHandler(final JCoConnectionsSnapshotClusterHandler snapshotClusterHandler)
	{
		this.snapshotClusterHandler = snapshotClusterHandler;
	}

	@Override
	@ManagedAttribute(description = "Shows the total number of JCo connections of all nodes of the cluster.")
	public Integer getTotalCount()
	{
		return new TenantAwareExecutor<Integer>()
		{
			@Override
			protected Integer doExecute()
			{
				return snapshotClusterHandler.getTotalCount();
			}
		}.getResult();
	}

	@Override
	@ManagedAttribute(description = "Shows the number of JCo connections with a long lifetime of all nodes of the cluster.")
	public Integer getLongRunnerCount()
	{
		return new TenantAwareExecutor<Integer>()
		{
			@Override
			protected Integer doExecute()
			{
				return snapshotClusterHandler.getLongRunnerCount();
			}
		}.getResult();
	}

	@Override
	@ManagedAttribute(description = "Shows the number of RFC destinations whose pool size is reached of all nodes of the cluster.")
	public Integer getPoolLimitReachedCount()
	{
		return new TenantAwareExecutor<Integer>()
		{
			@Override
			protected Integer doExecute()
			{
				return snapshotClusterHandler.getPoolLimitReachedCount();
			}
		}.getResult();
	}

	@Override
	@ManagedAttribute(description = "Shows the total number of nodes of the cluster.")
	public Integer getNodesCount()
	{
		return new TenantAwareExecutor<Integer>()
		{
			@Override
			protected Integer doExecute()
			{
				return snapshotClusterHandler.getNodesCount();
			}
		}.getResult();
	}

	@Override
	@ManagedAttribute(description = "Shows the total number of nodes which returned no result of the cluster .")
	public Integer getNodesWithoutResultCount()
	{
		return new TenantAwareExecutor<Integer>()
		{
			@Override
			protected Integer doExecute()
			{
				return snapshotClusterHandler.getNodesWithoutResultCount();
			}
		}.getResult();
	}

	@Override
	@ManagedAttribute(description = "Shows the time stamp of the JCo connections cluster attributes cache.")
	public Date getCacheTimestamp()
	{
		return new TenantAwareExecutor<Date>()
		{
			@Override
			protected Date doExecute()
			{
				return snapshotClusterHandler.getCacheTimestamp();
			}
		}.getResult();
	}

	@Override
	@ManagedAttribute(description = "Shows the time stamp the JCo connections cluster attribute caches is valid to.")
	public Date getCacheExpirationTimestamp()
	{
		return new TenantAwareExecutor<Date>()
		{
			@Override
			protected Date doExecute()
			{
				return snapshotClusterHandler.getCacheExpirationTimestamp();
			}
		}.getResult();
	}

	@Override
	@ManagedOperation(description = "Resets the JCo connections cluster attributes cache.")
	public String resetCache()
	{
		return new TenantAwareExecutor<String>()
		{
			@Override
			protected String doExecute()
			{
				snapshotClusterHandler.resetCache();
				return "JCo connections cluster snapshot cache has been reset";
			}
		}.getResult();
	}

	@Override
	@ManagedOperation(description = "Returns all current JCo connections of all nodes of the cluster as XML string.")
	public String createSnapshotXML()
	{
		return new TenantAwareExecutor<String>()
		{
			@Override
			protected String doExecute()
			{
				return snapshotClusterHandler.createSnapshotXML();
			}
		}.getResult();
	}

	@Override
	@ManagedOperation(description = "Create JCo connections cluster snapshot file of all nodes of the cluster.")
	public String createSnapshotFile()
	{
		return new TenantAwareExecutor<String>()
		{
			@Override
			protected String doExecute()
			{
				return snapshotClusterHandler.createSnapshotFile();
			}
		}.getResult();
	}

}
