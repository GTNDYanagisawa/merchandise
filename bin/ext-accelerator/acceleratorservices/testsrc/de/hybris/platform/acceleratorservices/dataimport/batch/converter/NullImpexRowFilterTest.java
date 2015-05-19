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
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.impl.NullImpexRowFilter;

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;


/**
 * Test for {@link NullImpexRowFilterTest}
 */
@UnitTest
public class NullImpexRowFilterTest
{
	@Test
	public void testNullFilter()
	{
		final ImpexRowFilter filter = new NullImpexRowFilter();
		Assert.assertTrue(filter.filter(null));
		Assert.assertTrue(filter.filter(new HashMap<Integer, String>()));
	}

}
