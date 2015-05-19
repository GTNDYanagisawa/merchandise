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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuAddressDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuAddressModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserModel;


public class CustomNodeConfigsTest
{
	@Test
	public void testGlobalPropertyConfiguration()
	{
		// setup graph values (address name)
		final TuUserDTO user = new TuUserDTO();
		user.setMainAddress(new TuAddressDTO("firstname", "lastname"));

		// configure graph transformer
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);

		// TEST 1: remove a NodeProperty

		// retrieve NodeConfig for address node from transformer
		final DefaultNodeMapping cfg = (DefaultNodeMapping) graph.getNodeMapping(TuAddressDTO.class);

		// assert nodes properties as they are configured from transformer by default
		final Map<String, PropertyMapping> map = cfg.getPropertyMappings();
		assertNotNull(map.get("firstname"));
		assertNotNull(map.get("lastname"));
		assertNotNull(map.get("owner"));

		// remove 'firstname' node property from processing list
		cfg.removePropertyMapping("firstname");

		// transform
		TuUserModel model = graph.transform(user);

		// assert 'firstname' was not processed
		assertNull(model.getMainAddress().getFirstname());
		assertEquals("lastname", model.getMainAddress().getLastname());

		// TEST 2: Add a NodeProperty which maps different read/write methods

		// now add a new NodeProperty which maps lastname to firstname
		DefaultPropertyMapping prop = new DefaultPropertyMapping(cfg, "lastname", "firstname");
		prop.initialize(DefaultPropertyMapping.COMPLIANCE_LEVEL_HIGH);
		cfg.putPropertyMapping(prop);

		// transform
		model = graph.transform(user);

		// assert result
		assertNotNull(model.getMainAddress().getFirstname());
		assertEquals("lastname", model.getMainAddress().getFirstname());
		assertEquals("lastname", model.getMainAddress().getLastname());

		// TEST 3: Add a NodeProperty which uses a custom PropertyTransformer

		// define the property transformer

		// reconfigure graph
		cfg.removePropertyMapping("propertyId");
		prop = new DefaultPropertyMapping(cfg, "firstname");
		prop.getTargetConfig().setWriteInterceptor(new ToUppercaseConverter());
		prop.initialize(DefaultPropertyMapping.COMPLIANCE_LEVEL_HIGH);
		cfg.putPropertyMapping(prop);

		// transform
		model = graph.transform(user);

		// assert result
		assertEquals("lastname", model.getMainAddress().getLastname());
		assertEquals("FIRSTNAME", model.getMainAddress().getFirstname());
	}



	/**
	 * Similar to first test but with additional transformation.
	 */
	@Test
	public void testDistanceBasedNodeConfiguration2()
	{
		// setup graph values (address name)
		final TuUserDTO user = new TuUserDTO();
		user.setMainAddress(new TuAddressDTO("firstname", "lastname"));

		// configure graph transformer
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);

		final NodeMapping cfg = graph.getNodeMapping(TuAddressDTO.class);
		final GraphContext ctx = new GraphContextImpl(graph);

		//NodeMapping expected = ctx.getCurrentNodeConfig().getNodeConfig(TuAddressDTO.class);
		final NodeMapping actual = ctx.getConfiguration().getNodeMapping(0, TuAddressDTO.class);
		assertEquals(cfg, actual);

		// create a new NodeConfig based on a original one
		final DefaultNodeMapping newCfg = new DefaultNodeMapping(graph, cfg);
		final DefaultPropertyMapping nodeProp = new DefaultPropertyMapping(newCfg, "firstname", "lastname", null,
				new ToUppercaseConverter());
		// property compilation will be done lazy
		newCfg.putPropertyMapping(nodeProp);

		ctx.getConfiguration().addNodeMapping(1, newCfg);

		// transform
		TuUserModel model = graph.transform(ctx, user);

		// assert result
		assertEquals("FIRSTNAME", model.getMainAddress().getLastname());


		// transform
		model = graph.transform(user);

		// assert result
		assertEquals("firstname", model.getMainAddress().getFirstname());
		assertEquals("lastname", model.getMainAddress().getLastname());
	}

	@Test
	public void testPropertyBasedNodeConfiguration()
	{
		// setup a graph (user, address)
		final TuUserDTO user = new TuUserDTO();
		user.setMainAddress(new TuAddressDTO("firstname", "lastname"));
		user.setSecondAddress(new TuAddressDTO("firstname2", "lastname2"));

		// create graph transformer
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);


		// get NodeConfig for ADDRESS node
		final NodeMapping cfg = graph.getNodeMapping(TuAddressDTO.class);

		// and create a new one based on ADDRESS but with only one property "firstname"
		final NodeMapping newCfg = new DefaultNodeMapping(graph, cfg);
		((AbstractNodeMapping) newCfg).putPropertyMapping(cfg.getPropertyMappings().get("firstname"));

		final PropertyMapping propertyMapping = graph.getNodeMapping(TuUserDTO.class).getPropertyMappings().get("secondAddress");
		((DefaultPropertyMapping) propertyMapping).setNewNodeMappings(Collections.singletonList(newCfg));

		final TuUserModel result = graph.transform(user);

		assertEquals("firstname", result.getMainAddress().getFirstname());
		assertEquals("lastname", result.getMainAddress().getLastname());

		assertEquals("firstname2", result.getSecondAddress().getFirstname());
		assertNull(result.getSecondAddress().getLastname());
	}

	@Test
	public void testDistanceAndPropertyAndCollection()
	{
		// setup a graph (user, address)
		final TuUserDTO user = new TuUserDTO();
		user.setMainAddress(new TuAddressDTO("firstname", "lastname"));

		final List<TuAddressDTO> addresses = new ArrayList<TuAddressDTO>();
		addresses.add(new TuAddressDTO("1firstname", "1lastname"));
		addresses.add(new TuAddressDTO("2firstname", "2lastname"));
		user.setAddresses(addresses);

		// create graph transformer
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);

		// get NodeConfig for ADDRESS node
		final NodeMapping cfg = graph.getNodeMapping(TuAddressDTO.class);
		// and create a new one based on ADDRESS but with only one property "firstname"
		final NodeMapping newCfg = new DefaultNodeMapping(graph, cfg);
		((AbstractNodeMapping) newCfg).putPropertyMapping(cfg.getPropertyMappings().get("firstname"));

		final PropertyMapping propertyMapping = graph.getNodeMapping(TuUserDTO.class).getPropertyMappings().get("secondAddress");
		((DefaultPropertyMapping) propertyMapping).setNewNodeMappings(Collections.singletonList(newCfg));

		final GraphContext ctx = graph.createGraphContext();

		ctx.getConfiguration().addNodeMapping(1, newCfg);

		final TuUserModel model = graph.transform(ctx, user);
		final List<TuAddressModel> _adr = new ArrayList<TuAddressModel>(model.getAddresses());

		assertEquals(model.getMainAddress().getFirstname(), "firstname");
		assertNull(model.getMainAddress().getLastname());
		assertEquals(_adr.get(0).getFirstname(), "1firstname");
		assertNull(_adr.get(0).getLastname());
		assertNull(_adr.get(1).getLastname());
	}

	@Test
	public void testDistanceAndPropertyAndCollection2()
	{
		// setup a graph (user, address)
		final TuAddressDTO adr = new TuAddressDTO("firstname", "lastname");

		final List<TuAddressDTO> addresses = new ArrayList<TuAddressDTO>();
		addresses.add(new TuAddressDTO("1firstname", "1lastname"));
		addresses.add(new TuAddressDTO("2firstname", "2lastname"));
		adr.setMoreAddresses(addresses);

		// create graph transformer
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);

		// get NodeConfig for ADDRESS node
		final NodeMapping cfg = graph.getNodeMapping(TuAddressDTO.class);
		// and create a new one based on ADDRESS but with only one property "firstname"
		final NodeMapping newCfg = new DefaultNodeMapping(graph, cfg);
		((AbstractNodeMapping) newCfg).putPropertyMapping(cfg.getPropertyMappings().get("firstname"));

		final GraphContext ctx = graph.createGraphContext();

		ctx.getConfiguration().addNodeMapping(1, newCfg);

		final TuAddressModel model = graph.transform(ctx, adr);
		final List<TuAddressModel> _adr = new ArrayList<TuAddressModel>(model.getMoreAddresses());

		assertEquals(model.getFirstname(), "firstname");
		assertEquals(model.getLastname(), "lastname");
		assertEquals(_adr.get(0).getFirstname(), "1firstname");
		assertNull(_adr.get(0).getLastname());
		assertNull(_adr.get(1).getLastname());
	}

	@Test
	public void testDistanceAndPropertyAndCollection3()
	{
		final List<TuAddressDTO> addresses = new ArrayList<TuAddressDTO>();
		addresses.add(new TuAddressDTO("first1", "last1"));
		addresses.add(new TuAddressDTO("first2", "last2"));
		addresses.add(new TuAddressDTO("first3", "last3"));


		// create graph transformer
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);

		// get NodeConfig for ADDRESS node
		final NodeMapping cfg = graph.getNodeMapping(TuAddressDTO.class);
		// and create a new one based on ADDRESS but with only one property "firstname"
		final NodeMapping newCfg = new DefaultNodeMapping(graph, cfg);
		((AbstractNodeMapping) newCfg).putPropertyMapping(cfg.getPropertyMappings().get("firstname"));

		final GraphContext ctx = graph.createGraphContext();

		ctx.getConfiguration().addNodeMapping(0, newCfg);

		final List<TuAddressModel> list = graph.transform(ctx, addresses);

		assertEquals(list.get(0).getFirstname(), "first1");
		assertNull(list.get(0).getLastname());
		assertEquals(list.get(1).getFirstname(), "first2");
		assertNull(list.get(1).getLastname());
		assertEquals(list.get(2).getFirstname(), "first3");
		assertNull(list.get(2).getLastname());
	}


	@Test
	public void testDistanceAndPropertyAndCollection4()
	{
		// user and addresses (collection) 
		final TuUserDTO user = new TuUserDTO("user");
		user.setAddresses(new ArrayList<TuAddressDTO>());
		user.getAddresses().add(new TuAddressDTO("first1", "last1"));

		// create graph and retrieve context
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);
		final GraphContextImpl ctx = (GraphContextImpl) graph.createGraphContext();

		// modify graph context
		final DefaultNodeMapping nodeCfg = new DefaultNodeMapping(graph, TuAddressDTO.class, TuAddressModel.class);
		nodeCfg.putPropertyMapping(new DefaultPropertyMapping(nodeCfg, "firstname"));
		ctx.getConfiguration().addNodeMapping(0, nodeCfg);

		// transform
		final TuUserModel model = graph.transform(ctx, user);

		// assert
		final List<TuAddressModel> list = (List) model.getAddresses();

		assertEquals("first1", list.get(0).getFirstname());
		assertEquals("last1", list.get(0).getLastname());
		//assertNull(list.get(0).getLastname());

	}





	public static void main(final String argc[])
	{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));
		Logger.getRootLogger().setLevel(Level.DEBUG);

		final CustomNodeConfigsTest test = new CustomNodeConfigsTest();
		test.testDistanceAndPropertyAndCollection2();
	}
}
