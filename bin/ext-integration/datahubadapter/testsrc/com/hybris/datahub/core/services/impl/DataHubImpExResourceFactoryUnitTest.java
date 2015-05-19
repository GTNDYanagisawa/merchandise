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

import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("javadoc")
@UnitTest
public class DataHubImpExResourceFactoryUnitTest
{
	private DataHubImpExResourceFactory factory;

	@Before
	public void setUp()
	{
		factory = new DataHubImpExResourceFactory();
	}

	@Test
	public void testDefaultReaderIsSetWhenNoReaderWasExplicitlyAssigned()
	{
		final FragmentReader reader = factory.getFragmentReader();

		Assert.assertNotNull(reader);
		Assert.assertTrue(reader instanceof DefaultFragmentReader);
	}

	@Test
	public void testFragmentReaderCanBeSpecifiedInsteadOfUsingTheDefaultOne()
	{
		final FragmentReader specific = dummyFragmentReader();
		factory.setFragmentReader(specific);

		final FragmentReader reader = factory.getFragmentReader();

		Assert.assertSame(specific, reader);
	}

	private FragmentReader dummyFragmentReader()
	{
		final List<ImpExFragment> noFragments = Collections.emptyList();
		return simulateFragmentReader(noFragments);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThrowsExcpetionWhenNullIsPassedForTheReaderToUse()
	{
		factory.setFragmentReader(null);
	}

	@Test
	public void testExtractsFragmentsUsingDefaultReader() throws ImpExException
	{
		final List<ImpExFragment> blocks = factory.extractFragments(scriptStream());

		Assert.assertFalse(blocks.isEmpty());
	}

	@Test
	public void testUsesExplicitlyAssignedReader() throws ImpExException
	{
		factory.setFragmentReader(dummyFragmentReader());

		final List<ImpExFragment> blocks = factory.extractFragments(scriptStream());

		Assert.assertTrue(blocks.isEmpty());
	}

	private InputStream scriptStream()
	{
		final String script = "# this is a very short sample ImpEx script";
		return new ByteArrayInputStream(script.getBytes());
	}

	@Test
	public void testCreatesAnInputStreamThatReadsAllFragmentsSequentially() throws IOException, ImpExException
	{
		final InputStream dummyStream = new ByteArrayInputStream(new byte[0]);
		final String[] fragments =
				{"Fragment 1", "Fragment 2"};
		factory.setFragmentReader(multiFragmentReaderSimulator(fragments));

		try (final InputStream in = factory.createScriptStream(dummyStream))
		{
			final String script = IOUtils.toString(in);

			Assert.assertEquals(fragments[0] + fragments[1], script);
		}
	}

	private FragmentReader multiFragmentReaderSimulator(final String... contents) throws IOException
	{
		final List<ImpExFragment> fragments = new ArrayList<>(contents.length);
		for (final String content : contents)
		{
			fragments.add(simulateFragment(content));
		}
		return simulateFragmentReader(fragments);
	}

	private ImpExFragment simulateFragment(final String content) throws IOException
	{
		final ImpExFragment frag = Mockito.mock(ImpExFragment.class);
		Mockito.doReturn(content).when(frag).getContent();
		Mockito.doReturn(new ByteArrayInputStream(content.getBytes())).when(frag).getContentAsInputStream();
		return frag;
	}

	private ImpExFragment crashingFragment() throws IOException
	{
		final ImpExFragment frag = Mockito.mock(ImpExFragment.class);
		Mockito.doThrow(new IOException()).when(frag).getContent();
		Mockito.doThrow(new IOException()).when(frag).getContentAsInputStream();
		return frag;
	}

	private FragmentReader simulateFragmentReader(final List<ImpExFragment> fragments)
	{
		final FragmentReader reader = Mockito.mock(FragmentReader.class);
		try
		{
			Mockito.doReturn(fragments).when(reader).readScriptFragments(any(InputStream.class));
		}
		catch (final ImpExException e)
		{
			e.printStackTrace();
		}
		return reader;
	}

	private FragmentReader simulateFragmentReader(final ImpExFragment... fragments)
	{
		return simulateFragmentReader(Arrays.asList(fragments));
	}

	@Test(expected = ImpExException.class)
	public void testThrowsExceptionWhenFailedToReadOriginalStream() throws ImpExException, IOException
	{
		factory.createResource(crashingStream());
	}

	private InputStream crashingStream() throws IOException
	{
		final InputStream in = Mockito.mock(InputStream.class);
		Mockito.doThrow(new IOException()).when(in).read();
		return in;
	}

	@Test(expected = ImpExException.class)
	public void testThrowsExceptionWhenFailedToReadFragmentContent() throws ImpExException, IOException
	{
		final FragmentReader reader = simulateFragmentReader(crashingFragment());
		factory.setFragmentReader(reader);

		factory.createResource(scriptStream());
	}
}
