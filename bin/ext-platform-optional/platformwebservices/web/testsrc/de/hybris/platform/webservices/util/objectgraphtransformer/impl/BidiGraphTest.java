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

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuAddressDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuAddressModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuCountryDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuCountryModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserModel;


public class BidiGraphTest
{
	@Test
	public void testInitialization()
	{
		// initialize a bidirectional graph
		final BidiGraphTransformer graph = new BidiGraphTransformer(TuUserDTO.class);

		// ask graph for UserDTO NodeMapping
		final NodeMapping dtoNodeMapping = graph.getNodeMapping(TuUserDTO.class);
		Assert.assertNotNull(dtoNodeMapping);
		// assert source<->target mapping of that NodeMapping 
		Assert.assertEquals(TuUserDTO.class, dtoNodeMapping.getSourceConfig().getType());
		Assert.assertEquals(TuUserModel.class, dtoNodeMapping.getTargetConfig().getType());

		// ask graph for UserModel NodeMapping
		final NodeMapping modelNodeMapping = graph.getNodeMapping(TuUserModel.class);
		Assert.assertNotNull(modelNodeMapping);
		// assert source<->target mapping of that NodeMapping 
		Assert.assertEquals(TuUserModel.class, modelNodeMapping.getSourceConfig().getType());
		Assert.assertEquals(TuUserDTO.class, modelNodeMapping.getTargetConfig().getType());

		// Assert all NodeMappings are present
		final Set<Class<?>> mappedNodes = graph.getNodeMappingsMap().getStaticMap().keySet();
		final Collection<Class<?>> expectedDTOs = Arrays.asList(TuUserDTO.class, TuAddressDTO.class, TuCountryDTO.class);
		final Collection<Class<?>> expectedModels = Arrays.asList(TuUserModel.class, TuAddressModel.class, TuCountryModel.class);

		Assert.assertEquals(7, mappedNodes.size());
		Assert.assertTrue(mappedNodes.containsAll(expectedDTOs));
		Assert.assertTrue(mappedNodes.containsAll(expectedModels));
		Assert.assertTrue(mappedNodes.contains(Collection.class));

		// Assert GraphConfig for source side
		final Map dtoNodes = graph.getSourceConfig().getNodes();
		Assert.assertEquals(3, dtoNodes.size());
		Assert.assertTrue(dtoNodes.keySet().containsAll(expectedDTOs));

		// Assert GraphConfig for target side
		final Map modelNodes = graph.getTargetConfig().getNodes();
		Assert.assertEquals(3, modelNodes.size());
		Assert.assertTrue(modelNodes.keySet().containsAll(expectedModels));


	}

	//	@Test
	//	public void testBasic()
	//	{
	//		final BidiGraphTransformer2 graph = new BidiGraphTransformer2(TuUserDTO.class);
	//
	//		final TuUserDTO user = new TuUserDTO("user");
	//		final TuAddressDTO adr = new TuAddressDTO("first", "last");
	//		user.setMainAddress(adr);
	//
	//
	//		// source -> target directly via source graph
	//		final TuUserModel model = graph.getSourceGraph().transform(user);
	//		assertEquals("user", model.getUid());
	//		Assert.assertNotNull(model.getMainAddress());
	//		assertEquals("first", model.getMainAddress().getFirstname());
	//
	//		// target -> source directly via target graph
	//		final TuUserModel userModel = new TuUserModel("userModel");
	//		final TuAddressModel adrModel = new TuAddressModel("firstModel", "lastModel");
	//		userModel.setMainAddress(adrModel);
	//
	//		final TuUserDTO userDto = graph.getTargetGraph().transform(userModel);
	//		assertEquals("userModel", userDto.getUid());
	//		Assert.assertNotNull(userDto.getMainAddress());
	//		assertEquals("firstModel", userDto.getMainAddress().getFirstname());
	//	}

	@Test
	public void testComposite1()
	{
		final BidiGraphTransformer graph = new BidiGraphTransformer(TuUserDTO.class);

		final TuUserDTO user = new TuUserDTO("user");
		final TuAddressDTO adr = new TuAddressDTO("first", "last");
		user.setMainAddress(adr);


		// source -> target implicitly via composite graph 
		final TuUserModel model = graph.transform(user);
		assertEquals("user", model.getUid());
		Assert.assertNotNull(model.getMainAddress());
		assertEquals("first", model.getMainAddress().getFirstname());

		// target -> source implicitly via composite graph
		final TuUserModel userModel = new TuUserModel("userModel");
		final TuAddressModel adrModel = new TuAddressModel("firstModel", "lastModel");
		userModel.setMainAddress(adrModel);

		final TuUserDTO userDto = graph.transform(userModel);
		assertEquals("userModel", userDto.getUid());
		Assert.assertNotNull(userDto.getMainAddress());
		assertEquals("firstModel", userDto.getMainAddress().getFirstname());
	}

	@Test
	public void testComposite2()
	{
		final BidiGraphTransformer graph = new BidiGraphTransformer();
		graph.addNodes(TuUserDTO.class);


		final TuUserDTO user = new TuUserDTO("user");
		final TuAddressDTO adr = new TuAddressDTO("first", "last");
		user.setMainAddress(adr);


		// source -> target implicitly via composite graph 
		final TuUserModel model = graph.transform(user);
		assertEquals("user", model.getUid());
		Assert.assertNotNull(model.getMainAddress());
		assertEquals("first", model.getMainAddress().getFirstname());

		// target -> source implicitly via composite graph
		final TuUserModel userModel = new TuUserModel("userModel");
		final TuAddressModel adrModel = new TuAddressModel("firstModel", "lastModel");
		userModel.setMainAddress(adrModel);

		final TuUserDTO userDto = graph.transform(userModel);
		assertEquals("userModel", userDto.getUid());
		Assert.assertNotNull(userDto.getMainAddress());
		assertEquals("firstModel", userDto.getMainAddress().getFirstname());
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
		final BidiGraphTest test = new BidiGraphTest();
		test.testComposite2();
	}


}
