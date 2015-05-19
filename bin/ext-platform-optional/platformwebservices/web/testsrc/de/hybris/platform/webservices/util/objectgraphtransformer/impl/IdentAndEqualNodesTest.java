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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Test;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.CycleOneDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.CycleTwoDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuAddressDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserModelFactory;


/**
 * Anything which deals with identity and equality.
 * <p/>
 * Identic nodes must be recognized to prevent cyclic processing.
 * <p/>
 * Equal nodes are recognized through a 'nodeId' and are helpful to create identic nodes for the target graph.
 * 
 */
public class IdentAndEqualNodesTest
{

	@Test
	public void testCyclicReferences()
	{
		// Test setup:
		// a basic cycle; two nodes referencing each other
		// target graph nodes are of same type like source nodes
		final CycleOneDTO sourceDto1 = new CycleOneDTO(1);
		final CycleTwoDTO sourceDto2 = new CycleTwoDTO(2);
		sourceDto1.setCycleTwoDTO(sourceDto2);

		try
		{
			// setup graph
			final DefaultGraphTransformer graph = new DefaultGraphTransformer();
			graph.addNodes(CycleOneDTO.class);

			// transform
			final CycleOneDTO targetDto1 = (CycleOneDTO) graph.transform(sourceDto1);

			// assert
			final CycleTwoDTO targetDto2 = targetDto1.getCycleTwoDTO();
			Assert.assertNotNull(targetDto2);
			assertEquals(2, targetDto2.getValue());
			// source and target are equal, but not identic
			assertNotSame(sourceDto2, targetDto2);
		}
		catch (final StackOverflowError e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCyclicReferences2()
	{
		// Test setup:
		// user1  has address has user1 (user1 are same)
		// target graph nodes are of other types
		final TuUserDTO user = new TuUserDTO("uid");
		final TuAddressDTO adr = new TuAddressDTO("firstname");
		adr.setOwner(user);

		final TuAddressDTO adr2 = new TuAddressDTO("firstname");
		adr2.setOwner(user);

		user.setMainAddress(adr);
		user.setSecondAddress(adr2);

		// create graph
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);
		final GraphContext ctx = graph.createGraphContext();

		// transform
		final TuUserModel result = (TuUserModel) graph.transform(ctx, user);

		// Assert identity of result and address owner
		assertSame(result, result.getMainAddress().getOwner());
		assertSame(result, result.getSecondAddress().getOwner());

		// assert that max processed distance is 2
		assertEquals(2, ctx.getMaxDistance());

		//general
		assertEquals("uid", result.getUid());
		assertEquals("firstname", result.getMainAddress().getFirstname());
	}


	@Test
	public void testCyclicReferences3()
	{
		// Test setup:
		// user has two identic members (mainAddress, secondAddress) which both referencing user again
		final TuUserDTO user = new TuUserDTO("uid");
		final TuAddressDTO adr = new TuAddressDTO("firstname");
		adr.setOwner(user);
		user.setMainAddress(adr);
		user.setSecondAddress(adr);

		// create graph
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);
		TuUserModel result = null;

		// transform 1
		result = (TuUserModel) graph.transform(user);

		// Assert identity of addresses
		assertSame(result.getMainAddress(), result.getSecondAddress());

		// transform 2
		result = new TuUserModel("uid");
		result = graph.transform(user, result);

		// Assert identity of addresses
		assertSame(result.getMainAddress(), result.getSecondAddress());
		assertSame(result, result.getMainAddress().getOwner());
	}



	@Test
	public void testCyclicsAndNodeUid()
	{
		// Test setup:
		// dto: user has two equal members (mainAddress, secondAddress) which both referencing user again
		// members are equal (produce same UID) but not identic 
		final TuUserDTO user1 = new TuUserDTO("user1");
		final TuUserDTO user2 = new TuUserDTO("user1");
		final TuAddressDTO adr = new TuAddressDTO("firstname", null, user2);
		user1.setMainAddress(adr);
		user1.setSecondAddress(adr);

		// graph: 
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);
		// change some configuration settings via API
		((DefaultNodeConfig) graph.getNodeMapping(TuUserDTO.class).getSourceConfig()).setUidPropertyNames("uid");
		((DefaultNodeMapping) graph.getNodeMapping(TuUserDTO.class)).setNodeFactory(new TuUserModelFactory());

		// transform 1: no merging
		TuUserModel result = graph.transform(user1);

		// Assert identities
		assertSame(result, result.getMainAddress().getOwner());
		assertSame(result, result.getSecondAddress().getOwner());
		assertSame(result.getMainAddress(), result.getSecondAddress());

		// transform 2: merging into passed model
		result = new TuUserModel("user1");
		result = graph.transform(user1, result);

		// Assert identity of addresses
		assertSame(result, result.getMainAddress().getOwner());
		assertSame(result, result.getSecondAddress().getOwner());
		assertSame(result.getMainAddress(), result.getSecondAddress());
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
		final IdentAndEqualNodesTest test = new IdentAndEqualNodesTest();
		test.testCyclicsAndNodeUid();
	}



}
