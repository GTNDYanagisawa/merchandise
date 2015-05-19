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
package de.hybris.platform.webservices.util.objectgraphtransformer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.GraphPropSource;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.GraphPropTarget;


public class PropertyConverterTest
{

	@Test
	public void testReadConverter()
	{
		// we have to switch off logging in order to avoid continuous
		// build errors
		final Logger logger = Logger.getLogger(PropertyProcessorImpl.class);
		final Level level = logger.getLevel();
		try
		{
			logger.setLevel(Level.OFF);
			// SETUP: a graph with property mappings (GraphPropSource->GraphPropTarget):
			// value1: Integer -> IntToStringConv    | String (valid)
			// value2: String  -> StringToNumberConv | Integer (valid; typecheck=false)
			// value3: Number                        | Integer (valid; typecheck=false)
			// value4: Double                        | Integer (invalid; typecheck=false)
			final DefaultGraphTransformer graph = new DefaultGraphTransformer(GraphPropSource.class);

			// assert for correct PropertyMappings
			final Map<String, PropertyMapping> cfgMap = graph.getNodeMapping(GraphPropSource.class).getPropertyMappings();
			assertTrue(cfgMap.containsKey("value1"));
			assertTrue(cfgMap.containsKey("value2"));
			assertTrue(cfgMap.containsKey("value3"));
			assertFalse(cfgMap.containsKey("value4"));

			assertEquals(3, cfgMap.size());

			// TEST1
			// test read-converter
			// set "10" for each value
			GraphPropSource source = new GraphPropSource();
			source.setValue1(Integer.valueOf(10));
			source.setValue2("10");
			source.setValue3(Integer.valueOf(10));
			source.setValue4(Double.valueOf(10));

			// transform
			GraphPropTarget target = graph.transform(source);
			// assert: all values except value4 are be processed successfully
			assertEquals("10", target.getValue1());
			assertEquals(Integer.valueOf(10), target.getValue2());
			assertEquals(Integer.valueOf(10), target.getValue3());
			assertNull(target.getValue4());

			// TEST2
			// test invalid runtime-type handling (provoke a runtime-exception-valid mapping but invalid runtime-type)
			// transform should display a console exception but should not interrupt processing
			source = new GraphPropSource();
			source.setValue3(Double.valueOf(10));

			// transform
			target = graph.transform(source);
			assertNull(target.getValue4());
		}
		finally
		{
			logger.setLevel(level);
		}
	}

	@Test
	public void testWriteConverter()
	{
		// SETUP: a graph with property mappings (GraphPropTarget->GraphPropSource):
		// value1: String  | StringToIntConv:Integer   -> Integer (valid)
		// value2: Integer | NumberToStringConv:String -> String  (valid)
		// value3: Integer |                           -> Number  (valid)
		// value4: Integer |                           -> Double  (invalid)
		//final GraphTransformer graph = (new BidiGraphTransformer(GraphPropSource.class)).getTargetGraph();
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(GraphPropTarget.class);

		// assert for correct PropertyMappings
		final Map<String, PropertyMapping> cfgMap = graph.getNodeMapping(GraphPropTarget.class).getPropertyMappings();
		assertTrue(cfgMap.containsKey("value1"));
		assertTrue(cfgMap.containsKey("value2"));
		assertTrue(cfgMap.containsKey("value3"));
		assertFalse(cfgMap.containsKey("value4"));


		// TEST1
		// test write-converter
		// set "10" for each value
		final GraphPropTarget target = new GraphPropTarget();
		target.setValue1("10");
		target.setValue2(Integer.valueOf(10));
		target.setValue3(Integer.valueOf(10));
		target.setValue4(Integer.valueOf(10));


		// transform
		final GraphPropSource source = graph.transform(target);
		// assert: all values except value4 are be processed successfully
		assertEquals(Integer.valueOf(10), source.getValue1());
		assertEquals("10", source.getValue2());
		assertEquals(Integer.valueOf(10), source.getValue3());
		assertNull(source.getValue4());
	}

	// TODO: test a combination of source-target -> read-write converter

	public static void main(final String[] argc)
	{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));
		Logger.getRootLogger().setLevel(Level.DEBUG);

		final PropertyConverterTest test = new PropertyConverterTest();
		test.testWriteConverter();

	}




}
