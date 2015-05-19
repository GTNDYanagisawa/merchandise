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
package de.hybris.platform.webservices.objectgraphtransformer;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.dto.order.CartEntryDTO;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.webservices.AbstractCollectionResource;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.BidiGraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.DefaultPropertyMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;


// special modification/ addons of GraphTransformer for webservice extension
public class YGraphTransformerTest
{
	@Test
	public void testStringToEnumConverter()
	{
		final StringToHybrisEnumValueConverter conv = new StringToHybrisEnumValueConverter();

		// assert old fashioned enumerations (class constants) 
		HybrisEnumValue enumValue = conv.getEnumValue(OrderStatus.class, "CREATED");
		assertEquals(OrderStatus.CREATED, enumValue);

		// assert standard enumerations (enum)
		// enum-code is not equal to enum-name
		enumValue = conv.getEnumValue(ArticleApprovalStatus.class, "CHECK");
		assertEquals(ArticleApprovalStatus.CHECK, enumValue);
	}


	@GraphNode(target = TestYColNodeProc.class)
	public static class TestYColNodeProc
	{
		private List<String> value1 = new ArrayList<String>();

		public List<String> getValue1()
		{
			return value1;
		}

		public void setValue1(final List<String> value1)
		{
			this.value1 = value1;
		}
	}

	public static class TestYNodeProcModel
	{
		//
	}

	public static class TestYColNodeProcResource extends AbstractCollectionResource<Collection<TestYNodeProcModel>>
	{
		public TestYColNodeProcResource()
		{
			super("TestYNodeProc");
		}
	}

	/**
	 * Sophisticated property lookup test.
	 * <p/>
	 * {@link CartEntryModel} uses at least two techniques for property 'order' which is problematic for various
	 * introspector mechanism. Problematic because of inheritance issues between AbstractOrderModel and CartModel which
	 * extends AbstractOrderModel.
	 * <p/>
	 * Used inheritance techniques are described for property 'order' and are:<br/>
	 * - Visibility get increased (from protected in AbstractOrderModel to public in OrderModel) <br/>
	 * - Covariant return type (from AbstractOrderEntryModel in AbstractOrderModel to CartEntryModel in OrderModel)
	 */
	@Test
	public void testCartEntryModelPropertyLookup()
	{
		BidiGraphTransformer graph;

		graph = new BidiGraphTransformer(CartEntryDTO.class);

		final DefaultPropertyMapping cfg = (DefaultPropertyMapping) graph.getNodeMapping(CartEntryDTO.class).getPropertyMappings()
				.get("order");

		final Method read = cfg.getTargetConfig().getReadMethod();
		final Method write = cfg.getTargetConfig().getWriteMethod();

		final String expectedReadMethod = "public de.hybris.platform.core.model.order.CartModel de.hybris.platform.core.model.order.CartEntryModel.getOrder()";
		final String expectedWriteMethod = "public void de.hybris.platform.core.model.order.CartEntryModel.setOrder(de.hybris.platform.core.model.order.AbstractOrderModel)";
		assertEquals(expectedReadMethod, read != null ? read.toString() : null);
		assertEquals(expectedWriteMethod, write != null ? write.toString() : null);
	}


	public static void main(final String[] argc)
	{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));
		Logger.getRootLogger().setLevel(Level.DEBUG);

		final YGraphTransformerTest test = new YGraphTransformerTest();
		test.testCartEntryModelPropertyLookup();
	}
}
