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
package com.hybris.instore.widgets.pageablelist;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;


@UnitTest
public class PageableListTest
{

	private Pageable<String> pageable;

	@Test
	public void testCase1()
	{
		pageable = new PageableList<String>(createListOfStrings(12), 5);
		Assert.assertEquals(5, pageable.getPageSize());
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertFalse(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getCurrentPage(), 0, 5, 5);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertFalse(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getNextPage(), 1, 5, 5);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertTrue(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getNextPage(), 2, 5, 2);
		Assert.assertFalse(pageable.hasNextPage());
		Assert.assertTrue(pageable.hasPreviousPage());

		assertThat(pageable.getNextPage()).isEmpty();

		assertPageCorrect(pageable.getPreviousPage(), 1, 5, 5);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertTrue(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getPreviousPage(), 0, 5, 5);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertFalse(pageable.hasPreviousPage());

	}

	@Test
	public void testCase2()
	{
		pageable = new PageableList<String>(createListOfStrings(12), 4);
		Assert.assertEquals(4, pageable.getPageSize());
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertFalse(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getCurrentPage(), 0, 4, 4);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertFalse(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getNextPage(), 1, 4, 4);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertTrue(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getNextPage(), 2, 4, 4);
		Assert.assertFalse(pageable.hasNextPage());
		Assert.assertTrue(pageable.hasPreviousPage());

		assertThat(pageable.getNextPage()).isEmpty();

		assertPageCorrect(pageable.getPreviousPage(), 1, 4, 4);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertTrue(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getPreviousPage(), 0, 4, 4);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertFalse(pageable.hasPreviousPage());

	}

	@Test
	public void testCase3()
	{
		pageable = new PageableList<String>(createListOfStrings(24), 24);
		Assert.assertEquals(24, pageable.getPageSize());
		Assert.assertFalse(pageable.hasNextPage());
		Assert.assertFalse(pageable.hasPreviousPage());
		assertPageCorrect(pageable.getCurrentPage(), 0, 24, 24);

	}

	@Test
	public void testCase5()
	{
		pageable = new PageableList<String>(createListOfStrings(5), 1);
		Assert.assertEquals(1, pageable.getPageSize());

		assertPageCorrect(pageable.getCurrentPage(), 0, 1, 1);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertFalse(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getNextPage(), 1, 1, 1);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertTrue(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getNextPage(), 2, 1, 1);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertTrue(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getNextPage(), 3, 1, 1);
		Assert.assertTrue(pageable.hasNextPage());
		Assert.assertTrue(pageable.hasPreviousPage());

		assertPageCorrect(pageable.getNextPage(), 4, 1, 1);
		Assert.assertFalse(pageable.hasNextPage());
		Assert.assertTrue(pageable.hasPreviousPage());


		assertThat(pageable.getNextPage()).isEmpty();

	}

	@Test(expected = IllegalArgumentException.class)
	public void testNull()
	{
		pageable = new PageableList<String>(null, 100);
	}

	@Test
	public void testEmpty()
	{
		pageable = new PageableList<String>(Collections.<String> emptyList(), 100);
		Assert.assertFalse(pageable.hasNextPage());
		Assert.assertFalse(pageable.hasPreviousPage());

		assertThat(pageable.getCurrentPage()).isEmpty();
		assertThat(pageable.getNextPage()).isEmpty();

	}


	private void assertPageCorrect(final List<String> currentPage, final int pageNo, final int pageSize, final int size)
	{
		de.hybris.platform.testframework.Assert.assertCollection(createTemplateList(pageNo, pageSize, size), currentPage);
	}

	private Collection createTemplateList(final int pageNo, final int pageSize, final int size)
	{
		final List<String> template = new ArrayList<String>(size);
		for (int i = 0; i < size; i++)
		{
			template.add(Integer.toString(pageNo * pageSize + i));
		}
		return template;
	}


	private List<String> createListOfStrings(final int size)
	{
		final List<String> result = new ArrayList<String>(size);
		for (int i = 0; i < size; i++)
		{
			result.add(Integer.toString(i));
		}
		return result;
	}
}
