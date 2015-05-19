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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.easymock.EasyMock;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.enums.BackendType;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.core.configuration.test.SapcoreconfigurationSpringJUnitTest;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;


/**
 * Unit tests for the test of the SAPConfigurationServiceImpl.
 */
@UnitTest
public class SAPConfigurationServiceImplTest extends SapcoreconfigurationSpringJUnitTest
{
	private final Date creationTime = new Date();
	private final SAPConfigurationServiceImpl classUnderTest = new SAPConfigurationServiceImpl();
	private BaseStoreService baseStoreServiceMock = null;
	private BaseStoreService baseStoreServiceWithoutBaseStoreMock = null;
	private BaseStoreService baseStoreServiceWithourSAPConfigurationMock = null;
	private BaseStoreService baseStoreServiceWithoutRFCMock = null;

	@Override
	public void setUp()
	{
		super.setUp();


		final BaseStoreModel currentBaseStore = createBaseStoreComplete();
		baseStoreServiceMock = EasyMock.createMock(BaseStoreService.class);
		EasyMock.expect(baseStoreServiceMock.getCurrentBaseStore()).andReturn(currentBaseStore).anyTimes();
		EasyMock.replay(baseStoreServiceMock);

		baseStoreServiceWithoutBaseStoreMock = EasyMock.createMock(BaseStoreService.class);
		EasyMock.expect(baseStoreServiceWithoutBaseStoreMock.getCurrentBaseStore()).andReturn(null).anyTimes();
		EasyMock.replay(baseStoreServiceWithoutBaseStoreMock);

		final BaseStoreModel currentBaseStoreWithoutSAPConfiguration = createBaseStoreWithoutSAPConfiguration();
		baseStoreServiceWithourSAPConfigurationMock = EasyMock.createMock(BaseStoreService.class);
		EasyMock.expect(baseStoreServiceWithourSAPConfigurationMock.getCurrentBaseStore())
				.andReturn(currentBaseStoreWithoutSAPConfiguration).anyTimes();
		EasyMock.replay(baseStoreServiceWithourSAPConfigurationMock);

		final BaseStoreModel currentBaseStoreWithoutRFC = createBaseStoreWithoutRFCDestination();
		baseStoreServiceWithoutRFCMock = EasyMock.createMock(BaseStoreService.class);
		EasyMock.expect(baseStoreServiceWithoutRFCMock.getCurrentBaseStore()).andReturn(currentBaseStoreWithoutRFC).anyTimes();
		EasyMock.replay(baseStoreServiceWithoutRFCMock);

	}

	/**
	 * Test get all properties.
	 * 
	 * @throws Exception
	 *            Exception
	 * 
	 */
	@Test
	public void testGetAllProperties() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		final Map<String, Object> allProperties = classUnderTest.getAllProperties();
		assertNotNull(allProperties);
	}

	/**
	 * Test get all properties failure.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test(expected = ConfigurationRuntimeException.class)
	public void testGetAllPropertiesFailure() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceWithourSAPConfigurationMock);
		classUnderTest.getAllProperties();
	}

	/**
	 * Test is SAP Configuration active.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testIsSAPConfigurationActive() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		assertTrue(classUnderTest.isSAPConfigurationActive());
	}

	/**
	 * Test is base store active.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testIsBaseStoreActive() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		assertTrue(classUnderTest.isBaseStoreActive());
	}

	/**
	 * Test get base store services.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testGetBaseStoreService() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		assertEquals(baseStoreServiceMock, classUnderTest.getBaseStoreService());
	}

	/**
	 * Test get SAP configuration name.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testGetSAPConfigurationName() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		assertEquals("testCoreName", classUnderTest.getSAPConfigurationName());
	}

	/**
	 * Test get property access.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testGetPropertyAccess() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		assertNotNull(classUnderTest.getPropertyAccess("SAPRFCDestination"));
	}

	/**
	 * Test get all property access.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testGetAllPropertyAccesses() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		assertFalse(classUnderTest.getAllPropertyAccesses().isEmpty());
	}

	/**
	 * Test get property access collection.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testGetPropertyAccessCollection() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		assertNotNull(classUnderTest.getPropertyAccessCollection("baseStores"));
	}

	/**
	 * Test get all property access collections.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testGetAllPropertyAccessCollections() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		assertFalse(classUnderTest.getAllPropertyAccessCollections().isEmpty());
	}

	/**
	 * Test get backend type.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testGetBackendType() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		assertEquals(BackendType.ERP.toString(), classUnderTest.getBackendType());
	}

	/**
	 * Test get backend type failure.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test(expected = ConfigurationRuntimeException.class)
	public void testGetBackendTypeFailure() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceWithoutRFCMock);
		classUnderTest.getBackendType();
	}

	/**
	 * Test get base store property.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testGetBaseStoreProperty() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		assertEquals(creationTime, classUnderTest.getBaseStoreProperty("creationtime"));
	}

	/**
	 * Test get base store property failure.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test(expected = ConfigurationRuntimeException.class)
	public void testGetBaseStorePropertyFailure() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceWithoutBaseStoreMock);
		classUnderTest.getBaseStoreProperty("creationtime");
	}

	/**
	 * Test to String.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testToString() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		assertTrue(classUnderTest.toString().contains("testCoreName"));
	}

	/**
	 * Test to String no SAP base store found.
	 * 
	 * @throws Exception
	 *            Exception
	 */
	@Test
	public void testToStringNoSAPBaseStoreFound() throws Exception
	{
		classUnderTest.setBaseStoreService(baseStoreServiceWithourSAPConfigurationMock);
		assertTrue(classUnderTest.toString().contains("- No SAP Base Store Configuration found!"));
	}


	/**
	 * Create complete base store.
	 * 
	 * @return BaseStoreModel Base Store Model
	 */
	private BaseStoreModel createBaseStoreComplete()
	{
		// Create SAP Configuration
		final SAPConfigurationModel testSAPConfigurationModel = createSAPConfigurationTestData();
		// Create RFC Destination and add it to SAP Configuration
		final SAPRFCDestinationModel sapRFCDestinationModel = new SAPRFCDestinationModel();
		sapRFCDestinationModel.setRfcDestinationName("testRfcDestinationName");
		sapRFCDestinationModel.setBackendType(BackendType.ERP);
		sapRFCDestinationModel.setSid("testSid");
		testSAPConfigurationModel.setSAPRFCDestination(sapRFCDestinationModel);

		final BaseStoreModel baseStoreModel = createBaseStoreTestData(testSAPConfigurationModel);
		return baseStoreModel;
	}

	/**
	 * Create base store without SAP configuration.
	 * 
	 * @return BaseStoreModel Base Store Model without SAP configuration
	 */
	private BaseStoreModel createBaseStoreWithoutSAPConfiguration()
	{
		return createBaseStoreTestData(null);
	}

	/**
	 * Create base store without RFC destination.
	 * 
	 * @return BaseStoreModel Base Store Model without RFC destination
	 */
	private BaseStoreModel createBaseStoreWithoutRFCDestination()
	{
		// Create SAP Configuration
		final SAPConfigurationModel testSAPConfigurationModel = createSAPConfigurationTestData();
		final BaseStoreModel baseStoreModel = createBaseStoreTestData(testSAPConfigurationModel);
		return baseStoreModel;
	}

	/**
	 * Create base store containing test data.
	 * 
	 * @param testSAPConfigurationModel
	 *           SAP configuration model
	 * @return BaseStoreModel Base Store Model containing test data
	 */
	private BaseStoreModel createBaseStoreTestData(final SAPConfigurationModel testSAPConfigurationModel)
	{
		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		baseStoreModel.setCreationtime(creationTime);
		baseStoreModel.setSAPConfiguration(testSAPConfigurationModel);
		return baseStoreModel;
	}

	/**
	 * Create SAP configuration model with test data.
	 * 
	 * @return SAPConfigurationModel SAP configuration model with test data
	 */
	private SAPConfigurationModel createSAPConfigurationTestData()
	{
		// Create SAP Configuration
		final SAPConfigurationModel testSAPConfigurationModel = new SAPConfigurationModel();
		testSAPConfigurationModel.setCore_name("testCoreName");
		testSAPConfigurationModel.setCreationtime(creationTime);
		return testSAPConfigurationModel;
	}


}
