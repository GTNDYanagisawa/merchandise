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
import static org.junit.Assert.assertSame;

import de.hybris.platform.catalog.dto.ProductFeatureDTO;
import de.hybris.platform.catalog.dto.classification.ClassificationAttributeUnitDTO;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.InDto1;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.InDto2;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Ignore;


/**
 * Failing tests or prototype testing.
 */
@Ignore("PLA-11441")
public class GraphSandboxTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(GraphSandboxTest.class);


	public void testCyclicCollectionReferences()
	{
		final List list = new ArrayList();
		list.add(new TuUserDTO("firstname1"));
		list.add("placeholder");
		list.add(new TuUserDTO("firstname3"));
		list.add(list);

		final GraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);

		final List result = graph.transform(list);
		assertSame(result, result.get(3));

	}


	public void testBidiGraphWithCollectionRoot()
	{
		final GraphTransformer graph = new BidiGraphTransformer(TuUserDTO.class);

		List<TuUserDTO> dtoList = new ArrayList<TuUserDTO>();
		dtoList.add(new TuUserDTO("user"));

		List<TuUserModel> modelList = graph.transform(dtoList);
		assertEquals(TuUserModel.class, modelList.get(0).getClass());

		modelList = new ArrayList<TuUserModel>();
		modelList.add(new TuUserModel("user"));
		dtoList = graph.transform(modelList);
		assertEquals(TuUserDTO.class, dtoList.get(0).getClass());
	}


	public void testInheritedNodes()
	{
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(InDto2.class);

		// is behavior: only the last node of inheritance tree is accepted
		// wanted: every node is accepted
		final NodeMapping nodeCfg1 = graph.getNodeMapping(InDto1.class);
		final NodeMapping nodeCfg2 = graph.getNodeMapping(InDto2.class);

		assertEquals(InDto1.class, nodeCfg1.getSourceConfig().getType().getClass());
		assertEquals(InDto2.class, nodeCfg2.getSourceConfig().getType().getClass());
	}

	public void testManualChanges()
	{
		//		final BidiGraphTransformer graph = new BidiGraphTransformer(ProductFeatureDTO.class);
		//		graph.addNodeType(ClassificationAttributeUnitDTO.class);
		//		graph.compile();

		// this one behaves different (correct behavior) to the code snippet above (not correct behavior)
		final BidiGraphTransformer graph = new BidiGraphTransformer();
		graph.addNodes(ProductFeatureDTO.class);
		graph.addNodes(ClassificationAttributeUnitDTO.class);
		graph.initialize();

		LOG.debug(graph.getNodeMapping(ProductFeatureModel.class).getPropertyMappings());
		LOG.debug(graph.getNodeMapping(ProductFeatureDTO.class).getPropertyMappings());
	}



	public static void main(final String[] argc)
	{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));

		Logger.getRootLogger().setLevel(Level.DEBUG);
		final GraphSandboxTest test = new GraphSandboxTest();
		test.testManualChanges();
	}
}
