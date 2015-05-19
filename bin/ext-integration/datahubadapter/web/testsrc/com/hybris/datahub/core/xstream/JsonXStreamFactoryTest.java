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
package com.hybris.datahub.core.xstream;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.thoughtworks.xstream.XStream;


/**
 *
 */
@UnitTest
public class JsonXStreamFactoryTest
{
	@Spy
	private final JsonXStreamFactory factory = new JsonXStreamFactory();

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testMapperOverridenJson() throws Exception
	{

		final Object streamObject = factory.getObject();

		Assert.assertTrue(streamObject instanceof XStream);

		final XStream stream = (XStream) streamObject;


		Assert.assertEquals("testData", stream.getMapper().aliasForSystemAttribute("testData"));
		Assert.assertEquals(null, stream.getMapper().aliasForSystemAttribute("class"));
	}
}
