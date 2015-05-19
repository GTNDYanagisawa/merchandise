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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;

import com.hybris.datahub.core.rest.client.ImpexDataImportClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultFragmentReaderUnitTest
{
	private DefaultFragmentReader reader;

	@Before
	public void setUp()
	{
		reader = new DefaultFragmentReader();
	}

	@Test(expected = ImpExException.class)
	public void testThrowsExceptionWhenIOErrorOccurs() throws ImpExException, IOException
	{
		reader.readScriptFragments(crashingStream());
	}

	private InputStream crashingStream() throws IOException
	{
		final InputStream in = Mockito.mock(InputStream.class);
		Mockito.doThrow(new IOException()).when(in).read();
		return in;
	}

	@Test
	public void testSplitsImpexScriptIntoLogicalBlocks() throws ImpExException
	{
		final List<ImpExFragment> blocks = reader.readScriptFragments(scriptStream());

		Assert.assertEquals(2, blocks.size());
	}

	@Test
	public void testPreservesTheOrderOfTheFragmentsAsInTheOriginalScript() throws ImpExException
	{
		final ImpExFragment[] blocks = reader.readScriptFragments(scriptStream()).toArray(new ImpExFragment[3]);

		Assert.assertTrue(blocks[0] instanceof ConstantTextFragment);
		Assert.assertTrue(blocks[1] instanceof DataHubDataFragment);
	}

	@Test
	public void testInjectsIntegrationLayerFacadeIntoTheDataFragment() throws ImpExException
	{
		final DataHubFacade facade = Mockito.mock(DataHubFacade.class);
		reader.setDataHubFacade(facade);

		final DataHubDataFragment dataFrag = extractIntegrationLayerDataFragment(scriptStream());

		Assert.assertSame(facade, dataFrag.getDataHubFacade());
	}

	@Test
	public void testInjectsDefaultFacadeIntoTheDataFragmentWhenNoFacadeWasSet() throws ImpExException
	{
		final DataHubDataFragment dataFrag = extractIntegrationLayerDataFragment(scriptStream());

		Assert.assertNotNull(dataFrag.getDataHubFacade());
		Assert.assertTrue(dataFrag.getDataHubFacade() instanceof ImpexDataImportClient);
	}

	private DataHubDataFragment extractIntegrationLayerDataFragment(final InputStream scriptInput) throws ImpExException
	{
		final List<ImpExFragment> blocks = reader.readScriptFragments(scriptInput);
		final DataHubDataFragment dataFrag = (DataHubDataFragment) blocks.get(1);
		return dataFrag;
	}

	private InputStream scriptStream()
	{
		final StringBuilder script = new StringBuilder()
				.append(
						"$baseProduct=baseProduct(code, catalogVersion(catalog(id[default='apparelProductCatalog']),version[default='Staged']))\n")
				.append(
						"$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])[unique=true,default=apparelProductCatalog:Staged]\n")
				.append("\n")
				.append(
						"INSERT_UPDATE Product;code[unique=true]; name[lang=en]; Unit(code); $catalogVersion[unique=true,allowNull=true];description[lang=en];approvalStatus(code);ean;manufacturerName\n")
				.append(
						"#$URL: https://integration.layer.host/rest/123/Product/en?fields=code,name,unit,catalogVersion,description,approvalStatus\n")
				.append("#$HEADER: x-TenantId=master\n").append("#$HEADER: someOtherHeader=boo");
		return new ByteArrayInputStream(script.toString().getBytes());
	}
}
