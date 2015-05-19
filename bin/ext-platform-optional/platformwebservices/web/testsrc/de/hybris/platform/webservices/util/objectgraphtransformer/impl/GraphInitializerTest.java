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

import de.hybris.platform.webservices.util.objectgraphtransformer.NodeConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.InitGraph1Base;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.InitGraph1Sub;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.InitGraph2Base;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.InitGraph2Sub;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing graph initialization API oriented (very low level based)
 * <p/>
 * Tests single (internal) graph elements like {@link PropertyConfig}, {@link NodeMapping}
 */
public class GraphInitializerTest
{
	private static final Logger LOG = Logger.getLogger(GraphInitializerTest.class);


	private static final String BASECLASS1_NAME = InitGraph1Base.class.getName();
	private static final String SUBCLASS1_NAME = InitGraph1Sub.class.getName();

	/**
	 * number1: covariant return type (number-integer) <br/>
	 * number2: covariant return type; change from read-only to read/write (added setter)<br/>
	 * number3: change from read-only to read/write (added setter)<br/>
	 * number4: change from write-only to read/write (added getter)<br/>
	 * number5: implemented newly as read-only (added getter/setter)<br/>
	 */
	private enum PropertyConfigTestResult1
	{

		NUMBER1("public java.lang.Integer " + SUBCLASS1_NAME + ".getNumber1()", //
				"public void " + SUBCLASS1_NAME + ".setNumber1(java.lang.Number)"), //
		NUMBER2("public java.lang.Integer " + SUBCLASS1_NAME + ".getNumber2()", //
				"public void " + BASECLASS1_NAME + ".setNumber2(java.lang.Number)"), //
		NUMBER3("public java.lang.Number " + BASECLASS1_NAME + ".getNumber3()", //
				"public void " + SUBCLASS1_NAME + ".setNumber3(java.lang.Number)"), //
		NUMBER4("public java.lang.Number " + SUBCLASS1_NAME + ".getNumber4()", //
				"public void " + BASECLASS1_NAME + ".setNumber4(java.lang.Number)"), //
		NUMBER5("public java.lang.Integer " + SUBCLASS1_NAME + ".getNumber5()", //
				"public void " + BASECLASS1_NAME + ".setNumber5(java.lang.Number)"), //
		;
		@SuppressWarnings("unused")
		public final String readMethod;
		@SuppressWarnings("unused")
		public final String writeMethod;

		private PropertyConfigTestResult1(final String read, final String write)
		{
			this.readMethod = read;
			this.writeMethod = write;
		}
	}

	private static final String BASECLASS2_NAME = InitGraph2Base.class.getName();
	private static final String SUBCLASS2_NAME = InitGraph2Sub.class.getName();

	/**
	 * number1-5: same like {@link PropertyConfigTestResult1}<br/>
	 * number6: covariant return type (number-integer); change from read-only to read/write (increased setter visibility)<br/>
	 */
	private enum PropertyConfigTestResult2
	{
		NUMBER1("public java.lang.Integer " + SUBCLASS2_NAME + ".getNumber1()", //
				"public void " + SUBCLASS2_NAME + ".setNumber1(java.lang.Number)"), //
		NUMBER2("public java.lang.Integer " + SUBCLASS2_NAME + ".getNumber2()", //
				"public void " + BASECLASS2_NAME + ".setNumber2(java.lang.Number)"), //
		NUMBER3("public java.lang.Number " + BASECLASS2_NAME + ".getNumber3()", //
				"public void " + SUBCLASS2_NAME + ".setNumber3(java.lang.Number)"), //
		NUMBER4("public java.lang.Number " + SUBCLASS2_NAME + ".getNumber4()", //
				"public void " + BASECLASS2_NAME + ".setNumber4(java.lang.Number)"), //
		NUMBER5("public java.lang.Integer " + SUBCLASS2_NAME + ".getNumber5()", //
				"public void " + BASECLASS2_NAME + ".setNumber5(java.lang.Number)"), //
		NUMBER6("public java.lang.Integer " + SUBCLASS2_NAME + ".getNumber6()", //
				"public void " + SUBCLASS2_NAME + ".setNumber6(java.lang.Number)"), //
		;
		@SuppressWarnings("unused")
		public final String readMethod;
		@SuppressWarnings("unused")
		public final String writeMethod;

		private PropertyConfigTestResult2(final String read, final String write)
		{
			this.readMethod = read;
			this.writeMethod = write;
		}
	}


	/**
	 * Tests whether all properties are found correctly and an appropriate {@link PropertyConfig} was created.
	 * <p/>
	 * There were several issues in the past with correct property detection which leads into incorrect or missing read
	 * and/or write method detection. Reasons for that were:
	 * <ul>
	 * <li>improper handling of covariant return types</li>
	 * <li>improper handling of bridge/synthetic methods (compiler generated methods)</li>
	 * <li>improper handling when increasing property visibilities (protected->public)</li>
	 * <li>changing property visibilities even affects the lookup-strategy of other properties</li>
	 * </ul>
	 */
	@Test
	public void testPropertyConfigLookup1()
	{
		final NodeConfig nodeCfg = new DefaultNodeConfig(InitGraph1Sub.class);
		final Map<String, PropertyConfig> propCfgMap = nodeCfg.getProperties();
		propCfgMap.remove("class");

		for (final PropertyConfig pCfg : propCfgMap.values())
		{
			this.assertPropertyConfig(pCfg, PropertyConfigTestResult1.class);
		}
	}

	@Test
	public void testPropertyConfigLookup2()
	{
		final NodeConfig nodeCfg = new DefaultNodeConfig(InitGraph2Sub.class);
		final Map<String, PropertyConfig> propCfgMap = nodeCfg.getProperties();
		propCfgMap.remove("class");

		for (final PropertyConfig pCfg : propCfgMap.values())
		{
			this.assertPropertyConfig(pCfg, PropertyConfigTestResult2.class);
		}
	}


	/**
	 * Tests whether appropriate {@link PropertyMapping} instances are created based on previously found
	 * {@link PropertyConfig}
	 */
	@Test
	public void testPropertyConfigLookup3()
	{
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(InitGraph1Sub.class);
		final NodeMapping nodeMapping = graph.getNodeMapping(InitGraph1Sub.class);

		final Map<String, PropertyMapping> pMapping = new LinkedHashMap<String, PropertyMapping>(nodeMapping.getPropertyMappings());

		// five regular PropertyMapping instances 
		// 'class' mapping is not available (no write method)
		assertEquals(5, pMapping.size());

		for (final PropertyMapping pMap : pMapping.values())
		{
			this.assertPropertyConfig(pMap.getSourceConfig(), PropertyConfigTestResult1.class);
		}
	}

	private boolean assertStrict = true;

	private void assertPropertyConfig(final PropertyConfig pCfg, final Class enumType)
	{
		Enum _enum = null;
		String readMethod = null;
		String writeMethod = null;

		try
		{
			_enum = Enum.valueOf(enumType, pCfg.getName().toUpperCase());
			readMethod = (String) enumType.getField("readMethod").get(_enum);
			writeMethod = (String) enumType.getField("writeMethod").get(_enum);
		}
		catch (final Exception e)
		{
			Assert.fail(e.getMessage());
		}

		// ok, we have sth to check
		if (_enum != null)
		{
			LOG.debug("Asserting property: " + _enum.name());
			assertMethod(pCfg.getReadMethod(), readMethod, assertStrict);
			assertMethod(pCfg.getWriteMethod(), writeMethod, assertStrict);

		}
	}

	private boolean assertMethod(final Method actualMethod, final String expectedMethod, final boolean strict)
	{
		boolean result = true;
		final String _actual = actualMethod != null ? actualMethod.toString() : null;

		if (strict)
		{
			assertEquals(expectedMethod, _actual);
		}
		else
		{
			try
			{
				assertEquals(expectedMethod, _actual);
			}
			catch (final AssertionError e)
			{
				result = false;
			}
		}

		if (!result)
		{
			LOG.debug("FAILED");
			LOG.debug(" got:    " + actualMethod);
			LOG.debug(" expect: " + expectedMethod);
		}
		else
		{
			LOG.debug("PASSED");
		}
		return result;
	}


	public static void main(final String... argc)
	{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));
		Logger.getRootLogger().setLevel(Level.DEBUG);

		final GraphInitializerTest test = new GraphInitializerTest();
		// developer flag; asserts ALL without interrupting when first assertion fails
		test.assertStrict = false;
		test.testPropertyConfigLookup3();
	}

}
