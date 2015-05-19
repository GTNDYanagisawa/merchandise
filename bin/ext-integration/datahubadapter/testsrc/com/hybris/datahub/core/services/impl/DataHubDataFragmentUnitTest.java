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
package com.hybris.datahub.core.services.impl;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


@UnitTest
@SuppressWarnings("javadoc")
public class DataHubDataFragmentUnitTest extends AbstractScriptFragmentTest<DataHubDataFragment>
{
	private static final String VALID_URL = "https://somehost/rest/123/Catalog";
	private static final String INVALID_URL = "https://somehost/rest";
	private static final String DATA = "1;Spring Catalog;";

	private DataHubFacade dataHub;

	@Before
	public void setUp()
	{
		dataHub = setUpDataHub();
		fragment = new DataHubDataFragment(dataHub);
	}

	@SuppressWarnings("unchecked")
	private DataHubFacade setUpDataHub()
	{
		final InputStream input = new ByteArrayInputStream(DATA.getBytes());
		final DataHubFacade facade = Mockito.mock(DataHubFacade.class);
		Mockito.doReturn(input).when(facade).readData(eq(VALID_URL), anyMap());
		return facade;
	}

	@Test
	public void testTheFacadeImplementationCanBeReadBack()
	{
		Assert.assertSame(dataHub, fragment.getDataHubFacade());
	}

	@Test
	public void testScriptFragmentIsEmptyBeforeAnyLineWasAdded() throws IOException
	{
		Assert.assertEquals("", fragment.getContent());
	}

	@Test
	public void testCommentCannotBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("# das ist ein kommentar");
	}

	@Test
	public void testMacroCannotBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])");
	}

	@Test
	public void testEmptyLineCanBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("");
	}

	@Test
	public void testNullCannotBeAdded() throws IOException
	{
		final boolean wasAdded = fragment.addLine(null);

		assertLineWasNotAdded(wasAdded, null);
	}

	@Test
	public void testSomeTextCannotBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("[unique=true,default=apparelProductCatalog:Staged]");
	}

	@Test
	public void testINSERT_UPDATEHeaderCannotBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("INSERT_UPDATE Category;code[unique=true];$catalogVersion");
	}

	@Test
	public void testINSERTHeaderCannotBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("INSERT Category;code[unique=true];$catalogVersion");
	}

	@Test
	public void testUPDATEHeaderCannotBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("UPDATE Category;code[unique=true];$catalogVersion");
	}

	@Test
	public void testUrlIsEmptyBeforeTheUrlCommentWasAdded()
	{
		Assert.assertEquals("", fragment.getUrl());
	}

	@Test
	public void testURLCommentCanBeAdded()
	{
		final boolean wasAdded = fragment.addLine("#$URL: https://somehost/rest/123/Catalog?fields=parent,catalog,version");

		Assert.assertTrue(wasAdded);
		Assert.assertEquals("https://somehost/rest/123/Catalog?fields=parent,catalog,version", fragment.getUrl());
	}

	@Test
	public void testHeadersAreEmptyBeforeAnyHeaderIsAdded()
	{
		final Map<String, String> headers = fragment.getHeaders();

		Assert.assertNotNull(headers);
		Assert.assertTrue(headers.isEmpty());
	}

	@Test
	public void testHEADERCommentCanBeAdded() throws IOException
	{
		final boolean wasAdded = fragment.addLine("#$HEADER: x-TenantId=master");

		Assert.assertTrue(wasAdded);
		Assert.assertEquals("master", fragment.getHeader("x-TenantId"));
	}

	@Test
	public void testIgnoresUnparsibleHeader()
	{
		final boolean added = fragment.addLine("#$HEADER: x-Tenant: master");

		Assert.assertFalse(added);
		Assert.assertTrue(fragment.getHeaders().isEmpty());
	}

	@Test
	public void testContentIsDataFromTheIntegrationLayerInsteadOfAddedLines() throws IOException
	{
		fragment.addLine("#$URL: " + VALID_URL);
		fragment.addLine("#$HEADER: x-TenantId=master");

		final String content = fragment.getContent();

		Assert.assertEquals(DATA, content);
	}

	@SuppressWarnings(
			{"rawtypes", "unchecked"})
	@Test
	public void testPassesUrlAndHeadersToTheIntegrationLayer() throws IOException
	{
		final ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<Map> headers = ArgumentCaptor.forClass(Map.class);
		fragment.addLine("#$URL: " + VALID_URL);
		fragment.addLine("#$HEADER: x-TenantId=master");

		fragment.getContent();

		Mockito.verify(dataHub).readData(url.capture(), headers.capture());
		Assert.assertEquals(VALID_URL, url.getValue());
		Assert.assertEquals("master", headers.getValue().get("x-TenantId"));
	}


	@Test
	public void testDataFromTheIntegrationLayerCanBeRetrievedAsStream() throws IOException
	{
		fragment.addLine("#$URL: " + VALID_URL);
		fragment.addLine("#$HEADER: x-TenantId=master");

		final String content = readContentFromTheInputStream();

		Assert.assertEquals(DATA, content);
	}

	@Test(expected = IOException.class)
	public void testOtherExceptionsAreConvertedToIOExceptionWhenInputStreamIsRetreived() throws IOException
	{
		throwExceptionOnReadingFromRemoteResource(new IllegalStateException(), INVALID_URL);

		fragment.addLine("#$URL: " + INVALID_URL);
		fragment.addLine("#$HEADER: x-TenantId=master");

		readContentFromTheInputStream();
	}

	@SuppressWarnings("unchecked")
	private void throwExceptionOnReadingFromRemoteResource(final IllegalStateException ex, final String url)
	{
		Mockito.doThrow(ex).when(dataHub).readData(eq(url), anyMap());
	}
}
