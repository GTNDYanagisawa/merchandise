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
import de.hybris.platform.util.CSVConstants;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class ConstantTextFragmentUnitTest extends AbstractScriptFragmentTest<ConstantTextFragment>
{
	@Before
	public void setUp()
	{
		fragment = new ConstantTextFragment();
	}

	@Test
	public void testScriptFragmentIsEmptyBeforeAnyLineWasAdded()
	{
		Assert.assertEquals("", fragment.getContent());
	}

	@Test
	public void testCommentCanBeAdded() throws IOException
	{
		final String aComment = "# das ist ein kommentar";

		final boolean wasAdded = fragment.addLine(aComment);

		assertLineIsInTheFragment(wasAdded, aComment);
	}

	@Test
	public void testMacroCanBeAdded() throws IOException
	{
		final String aMacro = "$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])";

		final boolean wasAdded = fragment.addLine(aMacro);

		assertLineIsInTheFragment(wasAdded, aMacro);
	}

	@Test
	public void testEmptyLineCanBeAdded() throws IOException
	{
		final boolean wasAdded = fragment.addLine("");

		assertLineIsInTheFragment(wasAdded, "");
	}

	@Test
	public void testNullCannotBeAdded() throws IOException
	{
		final boolean wasAdded = fragment.addLine(null);

		assertLineWasNotAdded(wasAdded, null);
	}

	@Test
	public void testSomeTextCanBeAdded() throws IOException
	{
		final String someText = "[unique=true,default=apparelProductCatalog:Staged]";

		final boolean wasAdded = fragment.addLine(someText);

		assertLineIsInTheFragment(wasAdded, someText);
	}

	@Test
	public void testHeaderCanBeAdded() throws IOException
	{
		testLineThatShouldBeAdded("INSERT_UPDATE Category;code[unique=true];$catalogVersion");
	}

	@Test
	public void testURLCommentCannotBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("#$URL: https://somehost/rest/123/Catalog?fields=parent,catalog,version");
	}

	@Test
	public void testHEADERCommentCannotBeAdded() throws IOException
	{
		testLineThatShouldNotBeAdded("#$HEADER: x-TenantId=master");
	}

	@Test
	public void testAddedLinesPreserveTheOrderInTheFragment() throws IOException
	{
		final String aComment = "# This is test IMPEX script";
		final String aMacro = "$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])";
		final String someText = "[unique=true,default=apparelProductCatalog:Staged]";

		fragment.addLine(aComment);
		fragment.addLine(aMacro);
		fragment.addLine(someText);

		assertOrderOfTheLines(aComment, aMacro, someText);
	}

	private void assertOrderOfTheLines(final String... lines) throws IOException
	{
		int prevLinePos = -1;
		for (int i = 0; i < lines.length; i++)
		{
			assertLineIsInTheFragment(true, lines[i]);

			final int currLinePos = fragment.getContent().indexOf(lines[i]);
			if (prevLinePos >= currLinePos)
			{
				Assert.fail("\"" + lines[i - 1] + "\" is unexpectedly found after \"" + lines[i] + "\"");
			}
			prevLinePos = currLinePos;
		}
	}

	@Test
	public void testContentCanBeReadFromTheInputStream() throws IOException
	{
		final String addedContent = "This text should be read back from the InputStream";
		fragment.addLine(addedContent);

		final String readContent = readContentFromTheInputStream().trim();

		Assert.assertEquals(addedContent, readContent);
	}

	private void testLineThatShouldBeAdded(final String line) throws IOException
	{
		final boolean wasAdded = fragment.addLine(line);

		assertLineIsInTheFragment(wasAdded, line);
	}

	private void assertLineIsInTheFragment(final boolean wasAdded, final String exp) throws IOException
	{
		final String line = exp + CSVConstants.HYBRIS_LINE_SEPARATOR;

		Assert.assertTrue("Must return 'true' for \"" + exp + "\"", wasAdded);
		Assert.assertTrue("\"" + line + "\" not found inside the block content", fragment.getContent().contains(line));
	}
}
