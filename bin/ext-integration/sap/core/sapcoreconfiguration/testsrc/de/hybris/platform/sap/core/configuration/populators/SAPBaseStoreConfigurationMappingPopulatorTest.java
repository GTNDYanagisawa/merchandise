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
package de.hybris.platform.sap.core.configuration.populators;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 * Test for GenericModel2DtoPopulator.
 */
@UnitTest
public class SAPBaseStoreConfigurationMappingPopulatorTest
{

	private final SAPBaseStoreConfigurationMappingPopulator classUnderTest = new SAPBaseStoreConfigurationMappingPopulator();

	private final SAPConfigurationModel sapConfigurationModel = new SAPConfigurationModel();
	private final BaseStoreModel baseStoreModel = new BaseStoreModel();
	private final Map<String, Object> propertyMap = new HashMap<String, Object>();

	/**
	 * Test setup.
	 */
	@Before
	public void setUp()
	{
		sapConfigurationModel.setCore_name("myBaseStoreConfigurationName");
		baseStoreModel.setUid("myBaseStoreUid");
		baseStoreModel.setSAPConfiguration(sapConfigurationModel);
	}

	/**
	 * Tests if all properties with same names are copied.
	 */
	@Test
	public void testPopulateMapping()
	{
		classUnderTest.populate(baseStoreModel, propertyMap);
		assertEquals("myBaseStoreUid", propertyMap.get("baseStoreUid"));
		assertEquals("myBaseStoreConfigurationName", propertyMap.get("baseStoreConfigurationName"));
	}

}
