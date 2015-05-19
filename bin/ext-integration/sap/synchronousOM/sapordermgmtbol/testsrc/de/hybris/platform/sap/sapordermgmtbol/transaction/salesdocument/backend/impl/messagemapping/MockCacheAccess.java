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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.core.bol.cache.impl.CacheAccessImpl;


@SuppressWarnings("javadoc")
public class MockCacheAccess extends CacheAccessImpl
{
	public MockCacheAccess()
	{
		super("dummuCaAcheAcces", 10000);
	}

	Object obj;

	@Override
	public Object get(final Object key)
	{
		return obj;
	}

	@Override
	public void put(final Object key, final Object object) throws SAPHybrisCacheException
	{
		obj = object;
	}
}
