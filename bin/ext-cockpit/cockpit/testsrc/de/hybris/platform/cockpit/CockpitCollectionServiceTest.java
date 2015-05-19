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
package de.hybris.platform.cockpit;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.DemoTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cockpit.model.CockpitObjectAbstractCollectionModel;
import de.hybris.platform.cockpit.model.CockpitObjectCollectionModel;
import de.hybris.platform.cockpit.model.CockpitObjectSpecialCollectionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Demonstrates usage of the CockpitCollectionService
 */
@DemoTest
public class CockpitCollectionServiceTest extends ServicelayerTransactionalTest
{
	@Resource
	private CockpitCollectionService cockpitCollectionService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ProductService productService;

	@Resource
	UserService userService;

	/**
	 * prepares some test data, before execution of every test:<br/>
	 * - the test principalModel will be created and saved<br/>
	 */
	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/test/testCollections.csv", "windows-1252");
	}

	/**
	 * Demonstrates how to create a deep copy of a cockpit collection
	 */
	@Test
	public void testCloneCollection()
	{
		final UserModel user = userService.getUserForUID("ahertz");
		final UserModel userB = userService.getUserForUID("abrode");
		final List<CockpitObjectAbstractCollectionModel> collections = cockpitCollectionService.getCollectionsForUser(user);
		final CockpitObjectCollectionModel collection = (CockpitObjectCollectionModel) collections.get(0);

		final CockpitObjectAbstractCollectionModel clone = cockpitCollectionService.cloneCollection(collection, userB);

		assertThat(clone).isNotNull();
		assertThat(clone.getUser()).isEqualTo(userB);
		assertThat(clone.getQualifier()).isEqualTo(collection.getQualifier());
		assertThat(clone.getElements().size()).isEqualTo(Integer.valueOf(collection.getElements().size()));
	}

	/**
	 * Demonstrates how to check read access for a given cockpit collection.<br/>
	 * The impex file testCollections.csv contains the test data permissions.<br/>
	 */
	@Test
	public void testHasReadCollectionRight()
	{
		final UserModel otherUser = userService.getUserForUID("other");
		final UserModel abrode = userService.getUserForUID("abrode");
		final UserModel dejol = userService.getUserForUID("dejol");

		final List<CockpitObjectAbstractCollectionModel> collections = cockpitCollectionService.getCollectionsForUser(dejol);

		assertThat(collections).isNotNull();
		assertThat(collections.size()).isEqualTo(2);

		final CockpitObjectAbstractCollectionModel collection = collections.get(0);
		boolean hasRight = cockpitCollectionService.hasReadCollectionRight(otherUser, collection);
		assertThat(hasRight).isFalse();

		hasRight = cockpitCollectionService.hasReadCollectionRight(abrode, collection);
		assertThat(hasRight).isTrue();

		hasRight = cockpitCollectionService.hasReadCollectionRight(dejol, collection);
		assertThat(hasRight).isTrue();
	}

	/**
	 * Demonstrates how to check write access for a given cockpit collection.<br/>
	 * The impex file testCollections.csv contains the test data permissions.<br/>
	 */
	@Test
	public void testHasWriteCollectionRight()
	{
		final UserModel otherUser = userService.getUserForUID("other");
		final UserModel abrode = userService.getUserForUID("abrode");
		final UserModel dejol = userService.getUserForUID("dejol");

		final List<CockpitObjectAbstractCollectionModel> collections = cockpitCollectionService.getCollectionsForUser(dejol);

		assertThat(collections).isNotNull();
		assertThat(collections.size()).isEqualTo(2);

		CockpitObjectAbstractCollectionModel writableCollection = null;
		for (final CockpitObjectAbstractCollectionModel collection : collections)
		{
			if ("writable".equals(collection.getQualifier()))
			{
				writableCollection = collection;
				break;
			}
		}

		boolean hasRight = cockpitCollectionService.hasWriteCollectionRight(otherUser, writableCollection);
		assertThat(hasRight).isFalse();

		hasRight = cockpitCollectionService.hasWriteCollectionRight(abrode, writableCollection);
		assertThat(hasRight).isTrue();

		hasRight = cockpitCollectionService.hasWriteCollectionRight(dejol, writableCollection);
		assertThat(hasRight).isTrue();
	}

	/**
	 * Demonstrates various ways to retrieve cockpit collections:<br/>
	 * - Retrieves Cockpit collections for a given user<br/>
	 * - Retrieves Cockpit special collections for a given user<br/>
	 * - Retrieves Cockpit special collections for a given user and collection type<br/>
	 */
	@Test
	public void testGetCollections()
	{
		// Get collections for user
		UserModel user = userService.getUserForUID("ahertz");
		List<CockpitObjectAbstractCollectionModel> collections = cockpitCollectionService.getCollectionsForUser(user);
		assertThat(collections).isNotNull();
		assertThat(collections.size()).isEqualTo(3);
		user = userService.getUserForUID("abrode");
		collections = cockpitCollectionService.getCollectionsForUser(user);
		assertThat(collections).isNotNull();
		assertThat(collections.size()).isEqualTo(4);

		// Get special collections for user
		user = userService.getUserForUID("ahertz");
		List<CockpitObjectSpecialCollectionModel> collections2 = cockpitCollectionService.getSpecialCollectionsForUser(user);
		assertThat(collections2).isNotNull();
		assertThat(collections2.size()).isEqualTo(1);
		assertThat(collections2.get(0).getQualifier()).isEqualTo("testSpecialA");
		assertThat(collections2.get(0).getCollectionType().getCode()).isEqualTo("blacklist");
		assertThat(collections2.get(0).getElements().size()).isEqualTo(2);

		// Get special collections for given user and collection
		user = userService.getUserForUID("abrode");
		collections2 = cockpitCollectionService.getSpecialCollections(user, "quickcollection");
		assertThat(collections2).isNotNull();
		assertThat(collections2.size()).isEqualTo(1);
		assertThat(collections2.get(0).getQualifier()).isEqualTo("testSpecialB");
		assertThat(collections2.get(0).getCollectionType().getCode()).isEqualTo("quickcollection");
		assertThat(collections2.get(0).getElements().size()).isEqualTo(3);
	}

	/**
	 * Demonstrates how to get elements from a Cockpit special collection
	 */
	@Test
	public void getElements()
	{
		final UserModel user = userService.getUserForUID("abrode");
		final List<CockpitObjectSpecialCollectionModel> collections = cockpitCollectionService.getSpecialCollections(user,
				"quickcollection");
		CockpitObjectSpecialCollectionModel threeElementsCollection = null;
		for (final CockpitObjectSpecialCollectionModel collection : collections)
		{
			if ("testSpecialB".equals(collection.getQualifier()))
			{
				threeElementsCollection = collection;
				break;
			}
		}
		assertThat(threeElementsCollection).isNotNull();
		List<ItemModel> elements = cockpitCollectionService.getElements(threeElementsCollection, 0, 100);
		assertThat(elements.size()).isEqualTo(3);
		assertThat(elements.get(0) instanceof ProductModel).isTrue();

		// Verify the count correctly limits the number of returned elements
		elements = cockpitCollectionService.getElements(threeElementsCollection, 0, 2);
		assertThat(elements.size()).isEqualTo(2);
	}

	/**
	 * Demonstrates how to add another item to the Cockpit collection<br/>
	 * - Retrieve the "testA" collection<br/>
	 * - Add a new ProductModel "testProduct4" to this collection<br/>
	 * - Verify the ProductModel was successfully added<br/>
	 */
	@Test
	public void testAddToCollection()
	{
		final UserModel user = userService.getUserForUID("ahertz");
		List<CockpitObjectAbstractCollectionModel> collections = cockpitCollectionService.getCollectionsForUser(user);
		CockpitObjectAbstractCollectionModel collection = null;
		for (final CockpitObjectAbstractCollectionModel coll : collections)
		{
			if ("testA".equals(coll.getQualifier()))
			{
				collection = coll;
				break;
			}
		}
		assertThat(collection.getElements().size()).isEqualTo(2);

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final List<ItemModel> elements = new ArrayList<ItemModel>();
		elements.add(productService.getProductForCode(catalogVersion, "testProduct4"));
		final int numberOfActuallyAddedElements = cockpitCollectionService.addToCollection(collection, elements);

		// Verify the size of the collection has increased by one
		collections = cockpitCollectionService.getCollectionsForUser(user);
		CockpitObjectAbstractCollectionModel changedCollection = null;
		for (final CockpitObjectAbstractCollectionModel coll : collections)
		{
			if ("testA".equals(coll.getQualifier()))
			{
				changedCollection = coll;
				break;
			}
		}
		assertThat(changedCollection.getElements().size()).isEqualTo(3);
		assertThat(numberOfActuallyAddedElements).isEqualTo(1);
	}


	/**
	 * Demonstrates how to remove an element from the cockpit collection:<br/>
	 * - Retrieve the "testA" collection <br/>
	 * - Attempt to remove a non-existent element in the collection<br/>
	 * - Attempt to remove an existing element in the collection<br/>
	 */
	@Test
	public void testRemoveFromCollection()
	{
		final UserModel user = userService.getUserForUID("ahertz");
		List<CockpitObjectAbstractCollectionModel> collections = cockpitCollectionService.getCollectionsForUser(user);
		CockpitObjectAbstractCollectionModel collection = null;

		for (final CockpitObjectAbstractCollectionModel coll : collections)
		{
			if ("testA".equals(coll.getQualifier()))
			{
				collection = coll;
				break;
			}
		}
		assertThat(collection.getElements().size()).isEqualTo(2);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final List<ItemModel> elements = new ArrayList<ItemModel>();
		elements.add(productService.getProductForCode(catalogVersion, "testProduct4"));
		int numberOfActuallyRemovedElements = cockpitCollectionService.removeFromCollection(collection, elements);

		// Verify the collection was unchanged (the given element was not part of the collection)
		collections = cockpitCollectionService.getCollectionsForUser(user);
		CockpitObjectAbstractCollectionModel changedCollection = null;
		for (final CockpitObjectAbstractCollectionModel coll : collections)
		{
			if ("testA".equals(coll.getQualifier()))
			{
				changedCollection = coll;
				break;
			}
		}
		assertThat(changedCollection.getElements().size()).isEqualTo(2);
		assertThat(numberOfActuallyRemovedElements).isEqualTo(0);

		// Now, try to remove an element that actually exists in the collection
		elements.clear();
		elements.add(productService.getProductForCode(catalogVersion, "testProduct0"));
		numberOfActuallyRemovedElements = cockpitCollectionService.removeFromCollection(collection, elements);
		collections = cockpitCollectionService.getCollectionsForUser(user);
		changedCollection = null;
		for (final CockpitObjectAbstractCollectionModel coll : collections)
		{
			if ("testA".equals(coll.getQualifier()))
			{
				changedCollection = coll;
				break;
			}
		}
		assertThat(changedCollection.getElements().size()).isEqualTo(1);
		assertThat(numberOfActuallyRemovedElements).isEqualTo(1);
	}

}
