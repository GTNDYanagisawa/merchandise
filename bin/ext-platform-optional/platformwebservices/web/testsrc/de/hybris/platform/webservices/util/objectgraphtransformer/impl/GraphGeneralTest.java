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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Test;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.basic.NullPropertyFilter;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.TxCollectionDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.misc.TxCollectionModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph.TpMediaDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph.TpMediaModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph.TpProductDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph.TpProductModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph.TpUnitDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph.TpUnitModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph2.Tp2AnotherProductDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph2.Tp2ExtendedProductDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph2.Tp2ProductModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph2.Tp2SimpleProductDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph2.Tp2UnitDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.productgraph2.Tp2UnitModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuAddressDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuAddressModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserModel;


public class GraphGeneralTest
{

	/**
	 * Transformation includes only basic copy operations.
	 */
	@Test
	public void testCopyPlainProperties()
	{
		// create mappings
		final DefaultGraphTransformer graph = new DefaultGraphTransformer();

		graph.addNodeMapping(new DefaultNodeMapping(graph, Tp2SimpleProductDTO.class, Tp2ProductModel.class));

		// create source and empyt target
		final Tp2SimpleProductDTO dto = new Tp2SimpleProductDTO("code", "ean");
		final Tp2ProductModel model = new Tp2ProductModel();

		// transform
		graph.transform(dto, model);

		// assert
		// just plain members were copied; no deep structure
		assertEquals(model.getCode(), dto.getCode());
		assertEquals(model.getEan(), dto.getEan());
	}

	/**
	 * Transformation includes copy and conversion operations.
	 */
	@Test
	public void testConversion()
	{
		// register mappings
		final DefaultGraphTransformer graph = new DefaultGraphTransformer();
		graph.addNodeMapping(new DefaultNodeMapping(graph, Tp2AnotherProductDTO.class, Tp2ProductModel.class));
		graph.addNodeMapping(new DefaultNodeMapping(graph, Tp2UnitDTO.class, Tp2UnitModel.class));

		// create source dto's ...
		final Tp2UnitDTO unitDto = new Tp2UnitDTO("unitCode");
		final Tp2AnotherProductDTO productDto = new Tp2AnotherProductDTO("code", "ean", unitDto);

		// ... and an empty target model
		final Tp2ProductModel model = new Tp2ProductModel();

		// transform
		graph.transform(productDto, model);

		// assert
		// - basic members were copied
		// - unit member was converted
		assertEquals(productDto.getCode(), model.getCode());
		assertEquals(productDto.getEan(), model.getEan());
		assertNotNull(model.getUnit());
		assertEquals(unitDto.getCode(), model.getUnit().getCode());
	}

	/**
	 * Advanced conversion which tests especially for correct target types.
	 * <p>
	 * A source type always rules over method parameter type (which may be different according to inheritance) Concrete
	 * Test assures that 'owner' property which is defined as type ItemModel always gets injected as concrete subinstance
	 * of 'ItemModel'
	 */
	@Test
	public void testConversion2()
	{
		final TuUserDTO owner = new TuUserDTO();
		owner.setUid("uid");
		final TuAddressDTO adrDto = new TuAddressDTO();
		adrDto.setOwner(owner);

		final TuUserDTO userDto = new TuUserDTO();
		userDto.setMainAddress(adrDto);
		userDto.setUid("uid");

		final GraphTransformer tree = new DefaultGraphTransformer(TuUserDTO.class);

		final TuUserModel model = tree.transform(userDto);

		assertEquals("uid", model.getUid());
		assertEquals("uid", ((TuUserModel) model.getMainAddress().getOwner()).getUid());
	}


	/**
	 * Transformation includes copy and conversion operations whereas converted elements are elements of a collection.
	 */
	@Test
	public void transformWithCollectionConversion()
	{
		// register mappings
		final DefaultGraphTransformer graph = new DefaultGraphTransformer();
		graph.addNodeMapping(new DefaultNodeMapping(graph, TpProductDTO.class, TpProductModel.class));
		graph.addNodeMapping(new DefaultNodeMapping(graph, TpUnitDTO.class, TpUnitModel.class));
		graph.addNodeMapping(new DefaultNodeMapping(graph, TpMediaDTO.class, TpMediaModel.class));

		// create source dto's
		final List<TpMediaDTO> medias = this.createArrayList(TpMediaDTO.class, 5);
		final TpProductDTO dto = new TpProductDTO("code", "ean");
		dto.setThumbnails(medias);

		final TpProductModel model = new TpProductModel();

		// transform
		graph.transform(dto, model);

		// assert
		// - basic members were copied
		// - member media: target collection elements were converted
		// - member media: target collection type equals source collection type (ArrayList)
		assertEquals(model.getCode(), dto.getCode());
		assertEquals(model.getEan(), dto.getEan());
		assertNotNull(model.getThumbnails());
		assertEquals(medias.size(), model.getThumbnails().size());
		assertEquals(ArrayList.class, model.getThumbnails().getClass());

		int index = 0;
		for (final TpMediaModel mediaModel : model.getThumbnails())
		{
			assertEquals(mediaModel.getCode(), medias.get(index++).getCode());
		}
	}

	@Test
	public void convertCollection()
	{
		// register mappings
		final DefaultGraphTransformer graph = new DefaultGraphTransformer();
		graph.addNodeMapping(new DefaultNodeMapping(graph, TpMediaDTO.class, TpMediaModel.class));

		// create a list and make convert it into a non-instantiable type
		// so that conversion operation can't create a target list of same type
		List<TpMediaDTO> medias = this.createArrayList(TpMediaDTO.class, 5);
		medias = medias.subList(0, medias.size());

		// transform
		final Collection<TpMediaModel> result = graph.transform(medias);

		// assert
		//  - target collection type doesn't equals source collection type (not instantiable)
		Assert.assertFalse(medias.getClass().equals(result.getClass()));
		int index = 0;
		for (final TpMediaModel mediaModel : result)
		{
			assertEquals(mediaModel.getCode(), medias.get(index++).getCode());
		}
	}


	/**
	 * Collection elements are of different types and conversion is tested twice based on different "copy object"
	 * mappings.
	 * <p>
	 * First run copies only attributes which are given by source1 type. Second run maps both source types and therefore
	 * copies more members.
	 */
	@Test
	public void convertCollection2()
	{
		// create mapping...
		// .. .for one basic type 
		final DefaultGraphTransformer graph = new DefaultGraphTransformer();
		graph.addNodeMapping(new DefaultNodeMapping(graph, Tp2SimpleProductDTO.class, Tp2ProductModel.class));

		// create source...
		// .. with two different types but of same basic type (extended type contains member 'manufacturerName')
		final Tp2SimpleProductDTO productDto1 = new Tp2SimpleProductDTO("code1", "ean1");
		final Tp2ExtendedProductDTO productDto2 = new Tp2ExtendedProductDTO("code2", "ean2", "company2");

		// ... and put both into a non-instantiable collection
		final List list = Arrays.asList(productDto1, productDto2);

		// transform
		List<Tp2ProductModel> result = graph.transform(list);

		// assert (1)
		// - target collection contains all source elements as transformed ProductModel's 
		// - objectgraph's source mapping doesn't contain 'manufacturerName' so that member is ignored 
		//   although the target object defines that member
		assertEquals(2, result.size());
		Tp2ProductModel model1 = result.get(0);
		Tp2ProductModel model2 = result.get(1);
		assertEquals(null, model1.getManufacturerName());
		assertEquals(null, model2.getManufacturerName());

		// modify mapping; add mapping for extended type (with 'manufacturerName')
		final NodeMapping nodeMapping = new DefaultNodeMapping(graph, Tp2ExtendedProductDTO.class, Tp2ProductModel.class);
		graph.addNodeMapping(nodeMapping);

		// transform again
		result = graph.transform(list);
		model1 = result.get(0);
		model2 = result.get(1);

		// assert
		// - now 'manufacturerName' is taken care of
		assertEquals(null, model1.getManufacturerName());
		assertEquals("company2", model2.getManufacturerName());
	}

	/**
	 * Tests various conversion Patterns for collection types (not their elements)
	 */
	@Test
	public void convertCollections3()
	{
		TxCollectionDTO dto;
		TxCollectionModel model;
		final DefaultGraphTransformer tree = new DefaultGraphTransformer();
		tree.addNodes(TxCollectionDTO.class);

		dto = new TxCollectionDTO();
		dto.setUsers1(new ArrayList<String>(Arrays.asList("1", "2", "3")));
		dto.setUsers2(new ArrayList<String>(Arrays.asList("1", "2", "3")));
		dto.setUsers3(new HashSet<String>(Arrays.asList("1", "2", "3")));
		dto.setUsers4(new HashSet<String>(Arrays.asList("1", "2", "3")));
		dto.setUsers5(new HashSet<String>(Arrays.asList("1", "2", "3")));
		dto.setUsers6(Arrays.asList("1", "2", "3"));

		model = (TxCollectionModel) tree.transform(dto);

		// TEST 1
		// test general conversion capabilities

		// List->Set  		
		assertEquals(HashSet.class, model.getUsers1().getClass());
		assertTrue(model.getUsers1().containsAll(dto.getUsers1()));

		// ArrayList->LinkedList
		assertTrue(model.getUsers2().containsAll(dto.getUsers2()));

		// HashSet->ArrayList
		assertTrue(model.getUsers3().containsAll(dto.getUsers3()));

		// Collection->List
		assertEquals(ArrayList.class, model.getUsers4().getClass());
		assertTrue(model.getUsers4().containsAll(dto.getUsers4()));

		// Collection->Collection
		assertEquals(HashSet.class, model.getUsers5().getClass());
		assertTrue(model.getUsers5().containsAll(dto.getUsers5()));

		// Collection [no constructor]->Collection
		assertEquals(ArrayList.class, model.getUsers6().getClass());
		assertTrue(model.getUsers6().containsAll(dto.getUsers6()));

		// TEST 2
		// test for new target instances even when no conversion is needed
		dto = new TxCollectionDTO();
		final ArrayList<String> users5 = new ArrayList<String>(Arrays.asList("1", "2", "3"));
		final HashSet<String> users6 = new HashSet<String>(Arrays.asList("1", "2", "3"));
		dto.setUsers5(users5);
		dto.setUsers6(users6);
		dto.setUsers7(users5);

		model = (TxCollectionModel) tree.transform(dto);

		assertEquals(dto.getUsers5(), model.getUsers5());
		assertNotSame(dto.getUsers5(), model.getUsers5());
		assertEquals(dto.getUsers6(), model.getUsers6());
		assertNotSame(dto.getUsers6(), model.getUsers6());
		assertEquals(dto.getUsers7(), model.getUsers7());
		assertNotSame(dto.getUsers7(), model.getUsers7());
	}


	@Test
	public void testMerging()
	{
		// Test setup:
		// dto: user with a address, address has 'lastname' set
		final TuUserDTO user = new TuUserDTO("user");
		final TuAddressDTO adr = new TuAddressDTO(null, "lastname");
		user.setMainAddress(adr);

		//model: user with address, address has 'firstname' set
		final TuUserModel tUser = new TuUserModel("user");
		final TuAddressModel tAdr = new TuAddressModel("firstname");
		tUser.setMainAddress(tAdr);

		//graph: dto graph with enabled NullPropertyFilter
		// without 'null' filter each model property gets overwritten
		// note: NullFilter should be obsolete when ModifiedProperties are enabled
		final DefaultGraphTransformer graph = new DefaultGraphTransformer(TuUserDTO.class);
		final GraphContext ctx = graph.createGraphContext();
		ctx.getPropertyFilterList().add(new NullPropertyFilter());

		// transform
		// user gets transformed into existing model
		graph.transform(ctx, user, tUser);

		// assert 'firstname' and 'lastname' are present
		assertSame(tAdr, tUser.getMainAddress());
		assertEquals("firstname", tUser.getMainAddress().getFirstname());
		assertEquals("lastname", tUser.getMainAddress().getLastname());
	}


	private <T> ArrayList<T> createArrayList(final Class clazz, final int size)
	{
		final ArrayList<T> result = new ArrayList<T>();
		try
		{
			final Constructor<T> constructor = clazz.getConstructor(new Class[]
			{ String.class });

			for (int i = 0; i < size; i++)
			{
				final T element = constructor.newInstance(String.valueOf(i));
				result.add(element);
			}
		}
		catch (final Exception e)
		{
			Assert.fail(e.getMessage());
		}
		return result;
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
		final GraphGeneralTest test = new GraphGeneralTest();
		test.convertCollections3();
	}

}
