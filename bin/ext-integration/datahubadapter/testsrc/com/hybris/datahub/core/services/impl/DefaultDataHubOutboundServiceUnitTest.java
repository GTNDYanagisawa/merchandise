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
 */
package com.hybris.datahub.core.services.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.hybris.datahub.core.data.TestProductData;
import com.hybris.datahub.core.dto.ResultData;
import com.hybris.datahub.core.rest.client.DefaultDataHubOutboundClient;
import com.hybris.datahub.core.util.OutboundServiceCsvUtils;
import com.hybris.datahub.core.util.OutboundServiceDataGenerationTestUtils;


@SuppressWarnings("javadoc")
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDataHubOutboundServiceUnitTest
{
	private final DefaultDataHubOutboundService outboundService = new DefaultDataHubOutboundService();

	private static final Map<String, Object> SAFE_MAP = ImmutableMap.of("safe", (Object) Boolean.TRUE);
	private static final String DEFAULT_FEED = "DEFAULT_FEED";
	private static final String DEFAULT_POOL = "GLOBAL";
	private static final String FEED_NAME = "SOME_FEED";
	private static final String POOL_NAME = "SOME_POOL";
	private static final String CANONICAL_TYPE = "TestCanonicalProduct";
	private static final String RAW_TYPE = "TestRawProduct";

	private TestProductData testProduct;
	private Map<String, Object> testProductMap;
	private Map<String, String> canonicalKeyMap;

	@Mock
	private DefaultDataHubOutboundClient dataHubOutboundClient;

	@Mock
	private OutboundServiceCsvUtils csvUtils;

	@Before
	public void setup() throws Exception
	{
		testProduct = OutboundServiceDataGenerationTestUtils.createTestProductData();
		testProductMap = OutboundServiceDataGenerationTestUtils.createUniqueTestProductMap();
		canonicalKeyMap = OutboundServiceDataGenerationTestUtils.createUniquePrimaryKeyMap();

		outboundService.setDataHubOutboundClient(dataHubOutboundClient);
		outboundService.setCsvUtils(csvUtils);

		setupOutboundService();
	}

	private void setupOutboundService() throws Exception
	{
		Mockito.doReturn(new ResultData()).when(dataHubOutboundClient).deleteItem(POOL_NAME, CANONICAL_TYPE, canonicalKeyMap);
		Mockito.doReturn(new ResultData()).when(dataHubOutboundClient).deleteItem(DEFAULT_POOL, CANONICAL_TYPE, canonicalKeyMap);
		Mockito.doReturn(new ResultData()).when(dataHubOutboundClient).deleteByFeed(DEFAULT_FEED, RAW_TYPE);

		Mockito.doReturn(SAFE_MAP).when(csvUtils).transmissionSafe(testProductMap);
		Mockito.doReturn(new ResultData()).when(dataHubOutboundClient).deleteByFeed(DEFAULT_FEED, RAW_TYPE, SAFE_MAP);
	}

	@Test
	public void testSendToDataHub_WithFeedNameAndObject() throws Exception
	{
		outboundService.sendToDataHub(FEED_NAME, CANONICAL_TYPE, testProduct);

		Mockito.verify(csvUtils).convertObjectToCsv(testProduct);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(FEED_NAME),
				Mockito.eq(CANONICAL_TYPE));
	}

	@Test
	public void testSendToDataHub_WithObject() throws Exception
	{
		outboundService.sendToDataHub(CANONICAL_TYPE, testProduct);

		Mockito.verify(csvUtils).convertObjectToCsv(testProduct);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(DEFAULT_FEED),
				Mockito.eq(CANONICAL_TYPE));
	}

	@Test
	public void testSendToDataHub_WithFeedNameAndMap() throws Exception
	{
		outboundService.sendToDataHub(FEED_NAME, CANONICAL_TYPE, testProductMap);

		Mockito.verify(csvUtils).convertMapToCsv(testProductMap);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(FEED_NAME),
				Mockito.eq(CANONICAL_TYPE));
	}

	@Test
	public void testSendToDataHub_WithMap() throws Exception
	{
		outboundService.sendToDataHub(CANONICAL_TYPE, testProductMap);

		Mockito.verify(csvUtils).convertMapToCsv(testProductMap);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(DEFAULT_FEED),
				Mockito.eq(CANONICAL_TYPE));
	}

	@Test
	public void testSendToDataHub_WithFeedNameAndListOfMaps() throws Exception
	{
		final List<Map<String, Object>> testList = Arrays.asList(testProductMap);
		outboundService.sendToDataHub(FEED_NAME, CANONICAL_TYPE, testList);

		Mockito.verify(csvUtils).convertListToCsv(testList);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(FEED_NAME),
				Mockito.eq(CANONICAL_TYPE));
	}

	@Test
	public void testSendToDataHub_WithListOfMaps() throws Exception
	{
		final List<Map<String, Object>> testList = Arrays.asList(testProductMap);
		outboundService.sendToDataHub(CANONICAL_TYPE, testList);

		Mockito.verify(csvUtils).convertListToCsv(testList);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(DEFAULT_FEED),
				Mockito.eq(CANONICAL_TYPE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendToDataHub_WithNullFeedNameAndObject() throws Exception
	{
		outboundService.sendToDataHub(null, CANONICAL_TYPE, testProduct);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendToDataHub_WithNullFeedNameAndMap() throws Exception
	{
		outboundService.sendToDataHub(null, CANONICAL_TYPE, testProductMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendToDataHub_WithNullRawTypeAndObject() throws Exception
	{
		outboundService.sendToDataHub(POOL_NAME, null, testProduct);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendToDataHub_WithNullRawTypeAndMap() throws Exception
	{
		outboundService.sendToDataHub(POOL_NAME, null, testProductMap);
	}

	@Test
	public void testDeleteItem() throws Exception
	{
		final ResultData result = outboundService.deleteItem(POOL_NAME, CANONICAL_TYPE, canonicalKeyMap);
		assertThat(result, is(notNullValue()));
		verify(dataHubOutboundClient).deleteItem(POOL_NAME, CANONICAL_TYPE, canonicalKeyMap);
	}

	@Test
	public void testDeleteItemWithDefaultPool() throws Exception
	{
		final ResultData result = outboundService.deleteItem(CANONICAL_TYPE, canonicalKeyMap);
		assertThat(result, is(notNullValue()));
		verify(dataHubOutboundClient).deleteItem(DEFAULT_POOL, CANONICAL_TYPE, canonicalKeyMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithNullTypeCode() throws Exception
	{
		outboundService.deleteItem(POOL_NAME, null, canonicalKeyMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithBlankTypeCode() throws Exception
	{
		outboundService.deleteItem(POOL_NAME, " ", canonicalKeyMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithNullFeedName() throws Exception
	{
		outboundService.deleteItem(null, CANONICAL_TYPE, canonicalKeyMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithBlankFeedName() throws Exception
	{
		outboundService.deleteItem(" ", CANONICAL_TYPE, canonicalKeyMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithNullPrimaryKeysMap() throws Exception
	{
		outboundService.deleteItem(POOL_NAME, CANONICAL_TYPE, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithEmptyPrimaryKeysMap() throws Exception
	{
		outboundService.deleteItem(POOL_NAME, CANONICAL_TYPE, Maps.<String, String> newHashMap());
	}

	@Test
	public void testDeleteByFeed() throws Exception
	{
		final ResultData result = outboundService.deleteByFeed(DEFAULT_FEED, RAW_TYPE);
		assertThat(result, is(notNullValue()));
		verify(dataHubOutboundClient).deleteByFeed(DEFAULT_FEED, RAW_TYPE);
	}

	@Test
	public void testDeleteByFeed_WithDefaultFeed() throws Exception
	{
		final ResultData result = outboundService.deleteByFeed(RAW_TYPE);
		assertThat(result, is(notNullValue()));
		verify(dataHubOutboundClient).deleteByFeed(DEFAULT_FEED, RAW_TYPE);
	}

	@Test
	public void testDeleteByFeed_WithKeyFields() throws Exception
	{
		final ResultData result = outboundService.deleteByFeed(DEFAULT_FEED, RAW_TYPE, testProductMap);
		assertNotNull(result);
	}

	@Test
	public void testDeleteByFeedWithKeysUsesTransmissionSafeValues() throws Exception
	{
		outboundService.deleteByFeed(DEFAULT_FEED, RAW_TYPE, testProductMap);
		verifySafeKeyMapTransmitted();
	}

	@Test
	public void testDeleteByFeed_WithKeyFieldsAndDefaultFeed() throws Exception
	{
		final ResultData result = outboundService.deleteByFeed(RAW_TYPE, testProductMap);
		assertNotNull(result);
	}

	@Test
	public void testDeleteByDefaultFeedWithKeysUsesTransmissionSafeValues() throws Exception
	{
		outboundService.deleteByFeed(RAW_TYPE, testProductMap);
		verifySafeKeyMapTransmitted();
	}

	private void verifySafeKeyMapTransmitted() throws Exception
	{
		final ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
		verify(dataHubOutboundClient).deleteByFeed(anyString(), eq(RAW_TYPE), mapCaptor.capture());
		assertSame(SAFE_MAP, mapCaptor.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemsWithNullFeedName() throws Exception
	{
		outboundService.deleteByFeed(null, RAW_TYPE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemsWithNullRawType() throws Exception
	{
		outboundService.deleteByFeed(DEFAULT_FEED, (String) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemFromFeedWithNullFeedName() throws Exception
	{
		outboundService.deleteByFeed(null, RAW_TYPE, testProductMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteFromDataFeedWithNullRawType() throws Exception
	{
		outboundService.deleteByFeed(DEFAULT_FEED, null, testProductMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteFromDataFeedWithNullPrimaryKeysMap() throws Exception
	{
		outboundService.deleteByFeed(DEFAULT_FEED, RAW_TYPE, (Map<String, Object>) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteFromDataFeedWithEmptyPrimaryKeysMap() throws Exception
	{
		outboundService.deleteByFeed(DEFAULT_FEED, RAW_TYPE, Maps.<String, Object> newHashMap());
	}
}
