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
package de.hybris.platform.servicelayer.cluster;

import de.hybris.platform.servicelayer.internal.service.AbstractService;

import org.springframework.beans.factory.annotation.Required;



public class MockClusterService extends AbstractService implements ClusterService
{
	private int clusterId;
	private long clusterIslandId;

	@Override
	public int getClusterId()
	{
		return this.clusterId;
	}

	@Required
	public void setClusterId(final int clusterId)
	{
		this.clusterId = clusterId;
	}

	@Override
	public long getClusterIslandId()
	{
		return clusterIslandId;
	}

	@Required
	public void setClusterIslandId(final long clusterIslandId)
	{
		this.clusterIslandId = clusterIslandId;
	}

	@Override
	public boolean isClusteringEnabled()
	{
		return false;
	}

}
