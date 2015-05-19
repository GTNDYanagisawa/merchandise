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
package de.hybris.platform.acceleratorservices.dataimport.batch.task.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.FileOrderComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test for {@link FileOrderComparator}
 */
@UnitTest
public class FileOrderComparatorTest
{
	private static final Integer PRIORITY_1 = Integer.valueOf(1);
	private static final Integer PRIORITY_2 = Integer.valueOf(2);
	private static final String PREFIX_1 = "abc";
	private static final String PREFIX_2 = "def";
	private static final String DUMMY = "dummy";

	@Mock
	private File file1;
	@Mock
	private File file2;
	private FileOrderComparator comparator;
	private List<File> list;
	private Map<String, Integer> prefixMap;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		comparator = new FileOrderComparator();
		prefixMap = new HashMap<String, Integer>();
		prefixMap.put(PREFIX_1, PRIORITY_1);
		prefixMap.put(PREFIX_2, PRIORITY_2);
		comparator.setPrefixPriority(prefixMap);
		list = new ArrayList<File>();
		list.add(file1);
		list.add(file2);
		given(file1.getName()).willReturn(PREFIX_1 + DUMMY);
		given(file2.getName()).willReturn(PREFIX_2 + DUMMY);
	}

	@Test
	public void testPriority()
	{
		Collections.sort(list, comparator);
		Assert.assertEquals(file2, list.get(0));
		prefixMap.put(PREFIX_1, PRIORITY_2);
		prefixMap.put(PREFIX_2, PRIORITY_1);
		Collections.sort(list, comparator);
		Assert.assertEquals(file1, list.get(0));
	}

	@Test
	public void testDefaultPriority()
	{
		prefixMap.remove(PREFIX_1);
		Collections.sort(list, comparator);
		Assert.assertEquals(file2, list.get(0));
		prefixMap.remove(PREFIX_2);
		prefixMap.put(PREFIX_1, PRIORITY_1);
		Collections.sort(list, comparator);
		Assert.assertEquals(file1, list.get(0));
	}

	@Test
	public void testModificationDate()
	{
		prefixMap.clear();
		given(Long.valueOf(file1.lastModified())).willReturn(Long.valueOf(1l));
		given(Long.valueOf(file2.lastModified())).willReturn(Long.valueOf(2l));
		Collections.sort(list, comparator);
		Assert.assertEquals(file1, list.get(0));
		given(Long.valueOf(file1.lastModified())).willReturn(Long.valueOf(3l));
		Collections.sort(list, comparator);
		Assert.assertEquals(file2, list.get(0));
	}

}
