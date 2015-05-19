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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Test;

import de.hybris.platform.webservices.util.objectgraphtransformer.nodefactory.Tf2Source1;
import de.hybris.platform.webservices.util.objectgraphtransformer.nodefactory.Tf2Target1;
import de.hybris.platform.webservices.util.objectgraphtransformer.nodefactory.Tf2Target1Factory;
import de.hybris.platform.webservices.util.objectgraphtransformer.nodefactory.TfSource1;
import de.hybris.platform.webservices.util.objectgraphtransformer.nodefactory.TfSource2;
import de.hybris.platform.webservices.util.objectgraphtransformer.nodefactory.TfSource3;
import de.hybris.platform.webservices.util.objectgraphtransformer.nodefactory.TfTarget1;
import de.hybris.platform.webservices.util.objectgraphtransformer.nodefactory.TfTarget2Factory;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuAddressDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserModelFactory;


public class GraphNodeFactoryTest
{

	@Test
	public void testFinder()
	{
		// configure graph
		final DefaultGraphTransformer graph = new DefaultGraphTransformer();
		graph.addNodes(TfSource1.class);

		// testcase: dto2 has a finder whose find result depends on dto2.value
		// 0: finds nothing; >0: returns always same static instance
		final TfSource2 dto2 = new TfSource2(-5);
		final TfSource3 dto3 = new TfSource3();

		final TfSource1 dto = new TfSource1();
		dto.setValue(10);
		dto.setDto2(dto2);
		dto.setDto3(dto3);

		TfTarget1 result = (TfTarget1) graph.transform(dto);

		// DTO2 is taken from finder (finder returned null->copy) 
		Assert.assertNotSame(TfTarget2Factory.STATIC_TARGETDTO2, result.getDto2());
		assertEquals(dto2.getValue(), result.getDto2().getValue());

		// DTO3 is copied
		assertSame(dto3, result.getDto3());

		// change a threshold so that finder returns something
		dto2.setValue(10);
		result = (TfTarget1) graph.transform(dto);
		// DTO2 is taken from finder (finder returned an instance)
		assertSame(TfTarget2Factory.STATIC_TARGETDTO2, result.getDto2());
		assertSame(dto3, result.getDto3());
	}

	@Test
	public void testFinder2()
	{
		final DefaultGraphTransformer graph = new DefaultGraphTransformer();
		graph.addNodes(Tf2Source1.class);

		((DefaultNodeMapping) graph.getNodeMapping(Tf2Source1.class)).setNodeFactory(new Tf2Target1Factory());


		final List<Tf2Source1> source = new ArrayList<Tf2Source1>();
		source.add(new Tf2Source1("1"));
		source.add(new Tf2Source1("5"));
		source.add(new Tf2Source1("1"));
		source.add(new Tf2Source1("101"));
		source.add(new Tf2Source1("101"));

		List<Tf2Target1> result = graph.transform(source);

		// general asserts (should be clear)
		assertEquals(5, result.size());
		assertEquals("1", result.get(0).getId());
		assertEquals("5", result.get(1).getId());
		assertEquals("1", result.get(2).getId());
		assertEquals("101", result.get(3).getId());
		assertEquals("101", result.get(4).getId());

		// identity for 1th and 3rd element as well as 4th and 5th  
		assertSame(result.get(0), result.get(2));
		assertSame(result.get(3), result.get(4));

		// new test without finder
		((DefaultNodeConfig) graph.getNodeMapping(Tf2Source1.class).getSourceConfig()).setUidPropertyNames(null);
		((DefaultNodeMapping) graph.getNodeMapping(Tf2Source1.class)).setNodeFactory(null);

		result = graph.transform(source);

		// no identity for 1th and 3rd element as well as 4th and 5th  
		assertNotSame(result.get(0), result.get(2));
		assertNotSame(result.get(3), result.get(4));
	}

	@Test
	public void testFinder3()
	{
		// setup: user1 with one address whose owner is user1
		final TuUserDTO owner = new TuUserDTO();
		owner.setUid("uid");

		final TuAddressDTO adr = new TuAddressDTO();
		adr.setFirstname("firstname");
		adr.setLastname("lastname");
		adr.setOwner(owner);

		final TuUserDTO user = new TuUserDTO();
		user.setUid("uid");

		user.setMainAddress(adr);

		final DefaultGraphTransformer tree = new DefaultGraphTransformer();
		tree.addNodes(TuUserDTO.class);

		((DefaultNodeConfig) tree.getNodeMapping(TuUserDTO.class).getSourceConfig()).setUidPropertyNames("uid");
		((DefaultNodeMapping) tree.getNodeMapping(TuUserDTO.class)).setNodeFactory(new TuUserModelFactory());

		final TuUserModel result = tree.transform(user);

		// assert identity of result (user1) and address owner of user1 (user1) 
		assertSame(result, result.getMainAddress().getOwner());
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
		final GraphNodeFactoryTest test = new GraphNodeFactoryTest();
		test.testFinder2();
	}



}
