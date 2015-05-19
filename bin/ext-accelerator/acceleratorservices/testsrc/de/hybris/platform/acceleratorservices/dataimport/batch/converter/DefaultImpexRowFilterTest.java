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
package de.hybris.platform.acceleratorservices.dataimport.batch.converter;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.impl.DefaultImpexRowFilter;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Test for {@link DefaultImpexRowFilterTest}
 */
@UnitTest
@Ignore
public class DefaultImpexRowFilterTest
{

	private static final String TEST_VALUE = "test";
	private DefaultImpexRowFilter filter;
	private Map<Integer, String> row;



	@Before
	public void setUp()
	{
		filter = new DefaultImpexRowFilter();
		row = new HashMap<Integer, String>();
	}

	@Test
	public void testExpression()
	{
		filter.setExpression("row[1]");
		Assert.assertFalse(filter.filter(row));
		row.put(Integer.valueOf(0), TEST_VALUE);
		Assert.assertFalse(filter.filter(row));
		row.put(Integer.valueOf(1), null);
		Assert.assertFalse(filter.filter(row));
		row.put(Integer.valueOf(1), "");
		Assert.assertFalse(filter.filter(row));
		row.put(Integer.valueOf(1), TEST_VALUE);
		Assert.assertTrue(filter.filter(row));
	}

	@Test
	public void testComplexExpression()
	{
		filter.setExpression("row[1] && row[0] == '" + TEST_VALUE + "'");
		Assert.assertFalse(filter.filter(row));
		row.put(Integer.valueOf(0), TEST_VALUE);
		Assert.assertFalse(filter.filter(row));
		row.put(Integer.valueOf(1), TEST_VALUE);
		Assert.assertTrue(filter.filter(row));
	}
}
