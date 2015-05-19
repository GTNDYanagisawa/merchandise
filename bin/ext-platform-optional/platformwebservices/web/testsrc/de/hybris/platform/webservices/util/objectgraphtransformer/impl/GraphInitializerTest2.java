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

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuAddressDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuCountryDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuCountryModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuMediaDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUnitDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserDTO;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;


/**
 * Tests graph initialization use-case oriented.
 * 
 */
public class GraphInitializerTest2
{
	@GraphNode(target = CollectionTypeDTO.class)
	@SuppressWarnings("unused")
	private static class CollectionTypeDTO
	{
		// collected some basic DTOs which are not having any child nodes
		private List<TuCountryDTO> prop1;
		private List prop2;
		private Collection<TuMediaDTO> prop3;
		private Collection<Object> prop4;
		private Collection<? extends TuUnitDTO> prop5;

		public List<TuCountryDTO> getProp1()
		{
			return prop1;
		}

		public void setProp1(final List<TuCountryDTO> prop1)
		{
			this.prop1 = prop1;
		}

		public List getProp2()
		{
			return prop2;
		}

		public void setProp2(final List prop2)
		{
			this.prop2 = prop2;
		}

		public Collection<TuMediaDTO> getProp3()
		{
			return prop3;
		}

		public void setProp3(final Collection<TuMediaDTO> prop3)
		{
			this.prop3 = prop3;
		}

		public Collection<Object> getProp4()
		{
			return prop4;
		}

		public void setProp4(final Collection<Object> prop4)
		{
			this.prop4 = prop4;
		}

		public Collection<? extends TuUnitDTO> getProp5()
		{
			return prop5;
		}

		public void setProp5(final Collection<? extends TuUnitDTO> prop5)
		{
			this.prop5 = prop5;
		}
	}

	@Test
	public void testManualConfiguration()
	{
		final DefaultGraphTransformer graph = new DefaultGraphTransformer();

		final NodeMapping cfg = new DefaultNodeMapping(graph, TuUserDTO.class, TuUserDTO.class);
		graph.addNodeMappings(Collections.singletonList(cfg));

		final NodeMapping cfg2 = graph.getNodeMapping(TuUserDTO.class);

		assertEquals(TuUserDTO.class, cfg2.getSourceConfig().getType());
		assertEquals(TuUserDTO.class, cfg2.getTargetConfig().getType());
		Assert.assertNull(cfg2.getNodeFactory());
		Assert.assertNotNull(cfg.getProcessor());
	}

	@Test
	public void testManualReconfiguration()
	{
		// create a graph
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);

		final DefaultNodeMapping node = (DefaultNodeMapping) graph.getNodeMapping(TuAddressDTO.class);
		DefaultPropertyMapping prop = (DefaultPropertyMapping) node.getPropertyMappings().get("country");

		// node must be initialized
		Assert.assertTrue(node.isInitialized());
		Assert.assertTrue(prop.isInitialized());

		// modify one of this nodes child properties  
		prop = new DefaultPropertyMapping(node, "country");
		node.putPropertyMapping(prop);

		// that property was not initialized yet
		Assert.assertFalse(node.isInitialized());
		Assert.assertFalse(prop.isInitialized());
		Assert.assertNull(prop.getSourceConfig().getReadType());
		Assert.assertNull(prop.getTargetConfig().getWriteType());

		// just a transformation which should trigger any necessary initializations
		final TuUserDTO user = new TuUserDTO();
		user.setMainAddress(new TuAddressDTO());
		graph.transform(user);

		// Assert that PropertyMapping was lazily initialized
		Assert.assertTrue(node.isInitialized());
		Assert.assertTrue(prop.isInitialized());
		Assert.assertEquals(TuCountryDTO.class, prop.getSourceConfig().getReadType());
		Assert.assertEquals(TuCountryModel.class, prop.getTargetConfig().getWriteType());

	}

	@Test
	public void testCollectionTypeDetection()
	{
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(CollectionTypeDTO.class);

		Assert.assertNotNull(graph.getNodeMapping(TuCountryDTO.class));
		Assert.assertNotNull(graph.getNodeMapping(TuMediaDTO.class));
		Assert.assertNotNull(graph.getNodeMapping(TuUnitDTO.class));

	}


	public static void main(final String... argc)
	{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));
		Logger.getRootLogger().setLevel(Level.DEBUG);

		final GraphInitializerTest2 test = new GraphInitializerTest2();
		test.testManualReconfiguration();
	}



}
