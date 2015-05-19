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

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Test;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;


public class VirtualPropertyTest
{
	public static class TestVirtualPropConverter implements PropertyInterceptor
	{
		@Override
		public Object intercept(final PropertyContext ctx, final Object source)
		{
			// source node has no such property; property value must always be null
			Assert.assertNull(source);

			// take the parent nodes hash-code as value
			final Object node = ctx.getParentContext().getSourceNodeValue();
			return String.valueOf(node.hashCode());
		}
	}

	@GraphNode(target = TestVirtualPropTarget.class)
	public static class TestVirtualPropSource
	{
		private String value1;

		public String getValue1()
		{
			return value1;
		}
	}

	@GraphNode(target = TestVirtualPropSource.class)
	public static class TestVirtualPropTarget
	{
		private String value1;
		private String value2;

		public String getValue1()
		{
			return value1;
		}

		public void setValue1(final String value1)
		{
			this.value1 = value1;
		}

		public String getValue2()
		{
			return this.value2;
		}

		@GraphProperty(virtual = true, interceptor = TestVirtualPropConverter.class)
		public void setValue2(final String value2)
		{
			this.value2 = value2;
		}
	}



	@Test
	public void testVirtualProperty()
	{
		//		final BidiGraphTransformer bgraph = new BidiGraphTransformer(TestVirtualPropSource.class);
		//		final GraphTransformer graph = bgraph.getTargetGraph();
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TestVirtualPropSource.class);

		final NodeMapping nodeMap = graph.getNodeMapping(TestVirtualPropSource.class);
		Assert.assertTrue(nodeMap.getPropertyMappings().containsKey("value1"));
		Assert.assertTrue(nodeMap.getPropertyMappings().containsKey("value2"));

		final TestVirtualPropSource source = new TestVirtualPropSource();
		source.value1 = "value1";

		final TestVirtualPropTarget result = graph.transform(source);
		Assert.assertEquals(source.getValue1(), result.getValue1());
		Assert.assertEquals(String.valueOf(source.hashCode()), result.getValue2());
	}

	public static void main(final String[] argc)
	{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));
		//		Logger log = Logger.getLogger(ObjTree.class);
		//		log.setLevel(Level.DEBUG);
		//
		//		log = Logger.getLogger(DefaultObjTreeNodeCopier.class);
		//		log.setLevel(Level.DEBUG);

		Logger.getRootLogger().setLevel(Level.DEBUG);
		final VirtualPropertyTest test = new VirtualPropertyTest();
		test.testVirtualProperty();
	}




}
