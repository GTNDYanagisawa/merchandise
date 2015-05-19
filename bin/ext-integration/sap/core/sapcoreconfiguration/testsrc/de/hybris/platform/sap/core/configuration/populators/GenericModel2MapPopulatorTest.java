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
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for GenericModel2DtoPopulator.
 */
@IntegrationTest
public class GenericModel2MapPopulatorTest extends ServicelayerTransactionalTest
{

	private final GenericModel2MapPopulator classUnderTest = new GenericModel2MapPopulator();

	private final SAPConfigurationModel sapConfigurationModel = new SAPConfigurationModel();
	private final SAPRFCDestinationModel sapRFCDestinationModel = new SAPRFCDestinationModel();
	private final Map<String, Object> propertyMap = new HashMap<String, Object>();

	/**
	 * Variable for the model service.
	 */
	@Resource
	private ModelService modelService;

	/**
	 * Test setup.
	 */
	@Before
	public void setUp()
	{
		sapRFCDestinationModel.setRfcDestinationName("myRFCDestination");
		sapRFCDestinationModel.setTargetHost("myTargetHost");
		sapRFCDestinationModel.setSid("ABC");
		sapRFCDestinationModel.setInstance("20");
		sapRFCDestinationModel.setClient("000");
		sapRFCDestinationModel.setUserid("myUser");
		sapRFCDestinationModel.setPassword("myPassword");
		modelService.save(sapRFCDestinationModel);
		sapConfigurationModel.setCore_name("myCoreName");
		sapConfigurationModel.setSAPRFCDestination(sapRFCDestinationModel);
		modelService.save(sapConfigurationModel);
	}

	@SuppressWarnings("javadoc")
	@After
	public void tearDown()
	{
		modelService.remove(sapConfigurationModel);
		modelService.remove(sapRFCDestinationModel);
	}

	/**
	 * Tests if all properties with same names are copied.
	 */
	@Test
	public void testPopulateBySamePropertyNames()
	{
		final PK pk = sapConfigurationModel.getPk();
		classUnderTest.populate(sapConfigurationModel, propertyMap);
		assertEquals("myCoreName", propertyMap.get("core_name"));
		assertEquals(pk, propertyMap.get("pk"));
	}

	/**
	 * Tests if related PK property is copied.
	 */
	@Test
	public void testPopulateByRelatedItemTypePK()
	{
		final PK pk = sapConfigurationModel.getSAPRFCDestination().getPk();
		assertNotNull(pk);
		classUnderTest.populate(sapConfigurationModel, propertyMap);
		assertEquals(pk, propertyMap.get("SAPRFCDestinationPK"));
	}

}
