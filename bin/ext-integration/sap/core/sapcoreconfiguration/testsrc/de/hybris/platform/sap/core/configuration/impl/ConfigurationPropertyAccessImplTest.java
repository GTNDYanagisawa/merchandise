/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 * 
 */
package de.hybris.platform.sap.core.configuration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.core.configuration.test.SapcoreconfigurationSpringJUnitTest;
import de.hybris.platform.store.BaseStoreModel;


/**
 * Unit tests for the test of the ConfigurationPropertyAccessImpl.
 */
@UnitTest
public class ConfigurationPropertyAccessImplTest extends SapcoreconfigurationSpringJUnitTest
{

	/**
	 * Class under test.
	 */
	private ConfigurationPropertyAccessImpl classUnderTest;

	/**
	 * Test creation time.
	 */
	private final Date creationTime = new Date();

	@Override
	@Before
	public void setUp()
	{
		createTestData();

	}

	/**
	 * Tests if all properties are available.
	 */
	@Test
	public void testProperties()
	{
		final Map<String, Object> allProperties = classUnderTest.getAllProperties();
		assertNotNull(allProperties);
		assertTrue(allProperties.size() >= 2);
		assertEquals("testCoreName", classUnderTest.getProperty("core_name"));
		assertEquals("testCoreName", allProperties.get("core_name"));
		assertEquals(creationTime, classUnderTest.getProperty("creationtime"));
		assertEquals(creationTime, allProperties.get("creationtime"));
	}

	/**
	 * Tests if all property accesses are available with the correct properties.
	 */
	@Test
	public void testPropertyAccesses()
	{
		final Map<String, ConfigurationPropertyAccess> allPropertyAccesses = classUnderTest.getAllPropertyAccesses();
		assertNotNull(allPropertyAccesses);
		assertTrue(allPropertyAccesses.size() >= 1);
		assertEquals("testRfcDestinationName",
				classUnderTest.getPropertyAccess("SAPRFCDestination").getProperty("rfcDestinationName"));
		assertEquals("testRfcDestinationName", allPropertyAccesses.get("SAPRFCDestination").getProperty("rfcDestinationName"));
	}

	/**
	 * Tests if all property access collections are available with the correct properties.
	 */
	@Test
	public void testPropertyAccessCollections()
	{
		final Map<String, Collection<ConfigurationPropertyAccess>> allPropertyAccessCollections = classUnderTest
				.getAllPropertyAccessCollections();
		assertNotNull(allPropertyAccessCollections);
		assertTrue(allPropertyAccessCollections.size() >= 1);
		assertEquals(2, classUnderTest.getPropertyAccessCollection("baseStores").size());
		final Iterator<ConfigurationPropertyAccess> iterator1 = classUnderTest.getPropertyAccessCollection("baseStores").iterator();
		assertEquals("testUid1", iterator1.next().getProperty("uid"));
		assertEquals("testUid2", iterator1.next().getProperty("uid"));
	}

	/**
	 * Create test SAPConfigurationModel with 1:1 related SAPRFCDestinationModel and 1:n related BaseStoreModel.
	 */
	private void createTestData()
	{
		// Create SAP Configuration
		final SAPConfigurationModel testSAPConfigurationModel = new SAPConfigurationModel();
		testSAPConfigurationModel.setCore_name("testCoreName");
		testSAPConfigurationModel.setCreationtime(creationTime);
		// Create RFC Destination and add it to SAP Configuration
		final SAPRFCDestinationModel sapRFCDestinationModel = new SAPRFCDestinationModel();
		sapRFCDestinationModel.setRfcDestinationName("testRfcDestinationName");
		sapRFCDestinationModel.setSid("testSid");
		testSAPConfigurationModel.setSAPRFCDestination(sapRFCDestinationModel);
		// Create 2 Base Stores and add it to SAP Configuration
		final List<BaseStoreModel> baseStores = new ArrayList<BaseStoreModel>();
		final BaseStoreModel baseStore1 = new BaseStoreModel();
		baseStore1.setUid("testUid1");
		baseStores.add(baseStore1);
		final BaseStoreModel baseStore2 = new BaseStoreModel();
		baseStore2.setUid("testUid2");
		baseStores.add(baseStore2);
		testSAPConfigurationModel.setBaseStores(baseStores);

		// Create class under test
		classUnderTest = new ConfigurationPropertyAccessImpl(testSAPConfigurationModel);
	}

}
