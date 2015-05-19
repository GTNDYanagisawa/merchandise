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
package de.hybris.platform.ycommercewebservices.cache;

import de.hybris.platform.core.Registry;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;

import java.io.IOException;


/**
 * {@link org.springframework.beans.factory.FactoryBean} that exposes a tenant-aware EhCache
 * {@link net.sf.ehcache.CacheManager} instance (independent or shared), configured from a specified config location.
 */
public class TenantAwareEhCacheManagerFactoryBean extends EhCacheManagerFactoryBean
{
	private String cacheNamePrefix = "wsCache_";

	public void afterPropertiesSet() throws IOException
	{

		setCacheManagerName(cacheNamePrefix + Registry.getCurrentTenant().getTenantID());
		super.afterPropertiesSet();
	}

	public void setCacheNamePrefix(final String cacheNamePrefix)
	{
		this.cacheNamePrefix = cacheNamePrefix;
	}
}
