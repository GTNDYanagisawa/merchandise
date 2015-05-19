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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.regioncache.CacheValueLoadException;
import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.sap.core.bol.cache.CacheAccess;
import de.hybris.platform.sap.core.bol.cache.GenericCacheKey;
import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;

import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test Class for {@link CacheAccessImpl}.
 */
@UnitTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{ "/test/SAPCacheTest-context.xml" })
public class CacheAccessTest
{

	/**
	 * Test cache access.
	 */
	@Resource(name = "testCacheRegion")
	private CacheAccess cacheAccess;

	/**
	 * Get keys with simple key test.
	 */
	@Test
	public void testGetKeysWithSimpleKey()
	{
		try
		{
			cacheAccess.clearCache();
			cacheAccess.put("simpleKey", "valueForSimpleKey");
			final Set<Object> keys = cacheAccess.getKeys();
			assertNotNull(keys);
			assertTrue(keys.size() == 1);
			final CacheKey theKey = (CacheKey) keys.iterator().next();
			assertTrue(theKey instanceof GenericCacheKey);
			assertEquals("SAPObjects", theKey.getTypeCode());
		}
		catch (final SAPHybrisCacheException e)
		{
			assertThat(e.getCause().getClass()).isEqualTo(SAPHybrisCacheException.class);
		}
	}

	/**
	 * Get keys with generic cache key test.
	 */
	@Test
	public void testGetKeysWithGenericCacheKey()
	{
		try
		{
			cacheAccess.clearCache();
			final GenericCacheKey genericCacheKey = new GenericCacheKey("key", "anotherTypeCode");
			cacheAccess.put(genericCacheKey, "valueForSimpleKey");
			final Set<Object> keys = cacheAccess.getKeys();
			assertNotNull(keys);
			assertTrue(keys.size() == 1);
			final CacheKey theKey = (CacheKey) keys.iterator().next();
			assertTrue(theKey instanceof GenericCacheKey);
			assertEquals("anotherTypeCode", theKey.getTypeCode());
		}
		catch (final SAPHybrisCacheException e)
		{
			assertThat(e.getCause().getClass()).isEqualTo(SAPHybrisCacheException.class);
		}
	}

	/**
	 * Put if absent test.
	 */
	@Test
	public void testPutIfAbsent()
	{
		cacheAccess.clearCache();
		final GenericCacheKey genericCacheKey = new GenericCacheKey("key", "anotherTypeCode");
		try
		{
			cacheAccess.putIfAbsent(genericCacheKey, "valueForPut");
			assertNotNull(cacheAccess.get(genericCacheKey));
			cacheAccess.putIfAbsent(genericCacheKey, "newValueForPut");
			assertFalse("newValueForPut".equals(cacheAccess.get(genericCacheKey)));
			assertEquals("valueForPut", cacheAccess.get(genericCacheKey));
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
		cacheAccess.clearCache();
		final GenericCacheKey genericCacheKey = new GenericCacheKey("key", "typeCode");
		final MockCacheValueLoaderImpl<String> loader = new MockCacheValueLoaderImpl<String>();
		loader.setValue("loaderValue");

		Object theValue = cacheAccess.getWithLoader(genericCacheKey, loader);
		assertNotNull(cacheAccess.get(genericCacheKey));
		assertEquals("loaderValue", theValue);
		loader.setValue("newLoaderValue");
		theValue = cacheAccess.getWithLoader(genericCacheKey, loader);
		assertFalse("newLoaderValue".equals(theValue));
	}

	/**
	 * Put to cache test.
	 */
	@Test
	public void testPut()
	{
		cacheAccess.clearCache();
		final GenericCacheKey genericCacheKey = new GenericCacheKey("key", "anotherTypeCode");
		try
		{
			cacheAccess.put(genericCacheKey, "valueForPut");
			assertNotNull(cacheAccess.get(genericCacheKey));
			cacheAccess.put(genericCacheKey, "newValueForPut");
			assertEquals("newValueForPut", cacheAccess.get(genericCacheKey));
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
		cacheAccess.clearCache();
		final GenericCacheKey genericCacheKey = new GenericCacheKey("notExistKey", "nullValue");
		try
		{
			cacheAccess.remove(genericCacheKey);
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
		cacheAccess.clearCache();
		final GenericCacheKey genericCacheKey = new GenericCacheKey("key", "typeCode");
		try
		{
			cacheAccess.put(genericCacheKey, "value");
			assertNotNull(cacheAccess.get(genericCacheKey));
			cacheAccess.remove(genericCacheKey);
			assertNull(cacheAccess.get(genericCacheKey));
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
		cacheAccess.clearCache();
		final GenericCacheKey genericCacheKey1 = new GenericCacheKey("key1", "typeCode");
		final GenericCacheKey genericCacheKey2 = new GenericCacheKey("key2", "typeCode");
		final GenericCacheKey genericCacheKey3 = new GenericCacheKey("key3", "typeCode");
		try
		{
			cacheAccess.put(genericCacheKey1, "value1");
			cacheAccess.put(genericCacheKey2, "value2");
			cacheAccess.put(genericCacheKey3, "value3");

			assertEquals(3, cacheAccess.getNumObjects());
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
