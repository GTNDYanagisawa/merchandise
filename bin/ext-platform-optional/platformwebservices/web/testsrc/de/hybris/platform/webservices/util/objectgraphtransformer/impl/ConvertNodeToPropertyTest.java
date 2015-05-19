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

import junit.framework.Assert;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyFilter;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;
import de.hybris.platform.webservices.util.objectgraphtransformer.basic.BasicNodeFilter;


public class ConvertNodeToPropertyTest
{
	@GraphNode(target = MyDto.class)
	public static class MyModel
	{
		private String name = null;
		private MyChildModel child1 = null;
		private MyChildModel child2 = null;

		public String getName()
		{
			return name;
		}

		public void setName(final String name)
		{
			this.name = name;
		}

		public MyChildModel getChild1()
		{
			return child1;
		}

		public void setChild1(final MyChildModel child1)
		{
			this.child1 = child1;
		}

		public MyChildModel getChild2()
		{
			return child2;
		}

		public void setChild2(final MyChildModel child2)
		{
			this.child2 = child2;
		}

	}

	@GraphNode(target = MyChildDto.class)
	public static class MyChildModel
	{
		private String id = null;
		private String code = null;

		public String getCode()
		{
			return code;
		}

		public void setCode(final String code)
		{
			this.code = code;
		}

		public String getId()
		{
			return id;
		}

		public void setId(final String id)
		{
			this.id = id;
		}
	}

	@GraphNode(target = MyModel.class, addNodes = MyChildModel.class)
	public static class MyDto
	{
		private String name = null;
		private MyChildDto child1 = null;
		private String child2 = null;


		public String getName()
		{
			return name;
		}

		public void setName(final String name)
		{
			this.name = name;
		}

		public MyChildDto getChild1()
		{
			return this.child1;
		}

		public void setChild1(final MyChildDto child1)
		{
			this.child1 = child1;
		}

		public String getChild2()
		{
			return child2;
		}

		@GraphProperty(interceptor = MyChildDtoToStringInterceptor.class)
		public void setChild2(final String child2)
		{
			this.child2 = child2;
		}


	}

	@GraphNode(target = MyChildModel.class)
	public static class MyChildDto
	{
		private String id = null;
		private String code = null;

		public String getId()
		{
			return id;
		}

		public void setId(final String id)
		{
			this.id = id;
		}

		public String getCode()
		{
			return code;
		}

		public void setCode(final String code)
		{
			this.code = code;
		}
	}


	public static class MyChildDtoToStringInterceptor implements PropertyInterceptor<MyChildDto, String>
	{
		@Override
		public String intercept(final PropertyContext propertyCtx, final MyChildDto propertyValue)
		{
			return propertyValue.getId() + ":" + propertyValue.getCode();
		}
	}



	public void testNodeToPropertyConversion(final GraphTransformer graph)
	{
		// fill the graph (source view)
		final MyChildModel childModel = new MyChildModel();
		childModel.setCode("code");
		childModel.setId("id");
		final MyModel model = new MyModel();
		model.setName("name");
		model.setChild1(childModel);
		model.setChild2(childModel);

		// test 1
		// nothing special, default converter behavior
		// which is: processing all nodes
		final GraphContext ctx = graph.createGraphContext();
		MyDto target = graph.transform(ctx, model);
		Assert.assertEquals("name", target.getName());
		Assert.assertNotNull(target.getChild1());
		Assert.assertEquals("id:code", target.getChild2());
		Assert.assertEquals(1, ctx.getMaxDistance());

		// test 2
		// add a BasicNodeFilter which stops processing at depth 1
		// expected behavior: 
		// - child1 property gets filtered because it copies/transforms a source into a target node 
		// - child2 property gets processed although it's source is node (but target is a plain String property)
		final GraphContext ctx2 = graph.createGraphContext();
		final PropertyFilter propFilter = new BasicNodeFilter(0);
		ctx2.getNodeFilterList().add(propFilter);

		target = graph.transform(ctx2, model);
		Assert.assertEquals("name", target.getName());
		Assert.assertNull(target.getChild1());
		Assert.assertEquals("id:code", target.getChild2());
		// XXX: probably this should become more consistent and return '0' in future
		Assert.assertEquals(1, ctx2.getMaxDistance());

	}

	@Test
	public void testNodeToPropertyConversion()
	{
		final GraphTransformer modelToDtoGraph = new BidiGraphTransformer(MyModel.class);
		this.testNodeToPropertyConversion(modelToDtoGraph);

		final GraphTransformer dtoToModelGraph = new BidiGraphTransformer(MyDto.class);
		this.testNodeToPropertyConversion(dtoToModelGraph);
	}




	public static void main(final String argc[])
	{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));
		Logger.getRootLogger().setLevel(Level.DEBUG);

		final ConvertNodeToPropertyTest test = new ConvertNodeToPropertyTest();
		test.testNodeToPropertyConversion();

	}

}
