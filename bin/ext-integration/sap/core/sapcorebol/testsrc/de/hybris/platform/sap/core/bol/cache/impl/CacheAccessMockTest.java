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
package de.hybris.platform.sap.core.bol.cache.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.regioncache.CacheValueLoadException;
import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.sap.core.bol.cache.CacheAccess;
import de.hybris.platform.sap.core.bol.cache.GenericCacheKey;
import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test Class for {@link CacheAccessMockImpl}.
 */
@UnitTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{ "/test/SAPCacheTest-context.xml" })
public class CacheAccessMockTest
{

	/**
	 * Test cache access.
	 */
	@Resource(name = "SAPMockCacheReqion")
	private CacheAccess cacheMockAccess;

	/**
	 * Cache get/put test.
	 */
	@Test
	public void testCachePutGet()
	{

		final GenericCacheKey cacheKey = new GenericCacheKey(new String[]
		{ "key1" }, "typeCode");
		final Object value1 = "Value1";
		assertNotNull(cacheMockAccess);
		try
		{
			cacheMockAccess.put(cacheKey, value1);

			assertEquals(value1.toString(), cacheMockAccess.get(cacheKey));

		}
		catch (final SAPHybrisCacheException e)
		{
			e.printStackTrace(); // NOPMD
		}
	}

	/**
	 * Put if absent test.
	 */
	@Test
	public void testPutIfAbsent()
	{
		cacheMockAccess.clearCache();
		final GenericCacheKey genericCacheKey = new GenericCacheKey("key", "anotherTypeCode");
		try
		{
			cacheMockAccess.putIfAbsent(genericCacheKey, "valueForPut");
			assertNotNull(cacheMockAccess.get(genericCacheKey));
			cacheMockAccess.putIfAbsent(genericCacheKey, "newValueForPut");
			assertFalse("newValueForPut".equals(cacheMockAccess.get(genericCacheKey)));
			assertEquals("valueForPut", cacheMockAccess.get(genericCacheKey));
		}
		catch (final SAPHybrisCacheException e)
		{
			assertThat(e.getCause().getClass()).isEqualTo(SAPHybrisCacheException.class);
		}
	}

	/**
	 * Get with loader test.
	 */
	@Test
	public void testGetWithLoader()
	{
		cacheMockAccess.clearCache();
		final GenericCacheKey genericCacheKey = new GenericCacheKey("key", "typeCode");
		final MockCacheValueLoaderImpl<String> loader = new MockCacheValueLoaderImpl<String>();
		loader.setValue("loaderValue");

		Object theValue = cacheMockAccess.getWithLoader(genericCacheKey, loader);
		assertNotNull(cacheMockAccess.get(genericCacheKey));
		assertEquals("loaderValue", theValue);
		loader.setValue("newLoaderValue");
		theValue = cacheMockAccess.getWithLoader(genericCacheKey, loader);
		assertFalse("newLoaderValue".equals(theValue));
	}

	/**
	 * Put to cache test.
	 */
	@Test
	public void testPut()
	{
		cacheMockAccess.clearCache();
		final GenericCacheKey genericCacheKey = new GenericCacheKey("key", "anotherTypeCode");
		try
		{
			cacheMockAccess.put(genericCacheKey, "valueForPut");
			assertNotNull(cacheMockAccess.get(genericCacheKey));
			cacheMockAccess.put(genericCacheKey, "newValueForPut");
			assertEquals("newValueForPut", cacheMockAccess.get(genericCacheKey));
		}
		catch (final SAPHybrisCacheException e)
		{
			assertThat(e.getCause().getClass()).isEqualTo(SAPHybrisCacheException.class);
		}
	}

	/**
	 * Remove with not existing key test.
	 */
	@Test
	public void testRemoveWithNotExistingKey()
	{
		cacheMockAccess.clearCache();
		final GenericCacheKey genericCacheKey = new GenericCacheKey("notExistKey", "nullValue");
		try
		{
			cacheMockAccess.remove(genericCacheKey);
		}
		catch (final SAPHybrisCacheException e)
		{
			assertThat(e.getCause().getClass()).isEqualTo(SAPHybrisCacheException.class);
		}
	}

	/**
	 * Remove test.
	 */
	@Test
	public void testRemove()
	{
		cacheMockAccess.clearCache();
		final GenericCacheKey genericCacheKey = new GenericCacheKey("key", "typeCode");
		try
		{
			cacheMockAccess.put(genericCacheKey, "value");
			assertNotNull(cacheMockAccess.get(genericCacheKey));
			cacheMockAccess.remove(genericCacheKey);
			assertNull(cacheMockAccess.get(genericCacheKey));
		}
		catch (final SAPHybrisCacheException e)
		{
			assertThat(e.getCause().getClass()).isEqualTo(SAPHybrisCacheException.class);
		}

	}

	/**
	 * Get number of objects test.
	 */
	@Test
	public void testGetNumObjects()
	{
		cacheMockAccess.clearCache();
		final GenericCacheKey genericCacheKey1 = new GenericCacheKey("key1", "typeCode");
		final GenericCacheKey genericCacheKey2 = new GenericCacheKey("key2", "typeCode");
		final GenericCacheKey genericCacheKey3 = new GenericCacheKey("key3", "typeCode");
		try
		{
			cacheMockAccess.put(genericCacheKey1, "value1");
			cacheMockAccess.put(genericCacheKey2, "value2");
			cacheMockAccess.put(genericCacheKey3, "value3");

			assertEquals(3, cacheMockAccess.getNumObjects());
		}
		catch (final SAPHybrisCacheException e)
		{
			assertThat(e.getCause().getClass()).isEqualTo(SAPHybrisCacheException.class);
		}
	}

	/**
	 * Mock implementation for cache value loader.
	 */
	private class MockCacheValueLoaderImpl<V> implements CacheValueLoader<V>
	{

		private V obj;

		/**
		 * Standard constructor.
		 */
		public MockCacheValueLoaderImpl()
		{
		}

		@Override
		public V load(final CacheKey arg0) throws CacheValueLoadException
		{
			return this.obj;
		}

		/**
		 * Set value to be loaded from {@link CacheValueLoader}.
		 * 
		 * @param obj
		 *           value to be loaded.
		 */
		@SuppressWarnings("unchecked")
		public void setValue(final Object obj)
		{
			this.obj = (V) obj;
		}

	}
}
