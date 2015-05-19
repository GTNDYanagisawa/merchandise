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
package de.hybris.platform.sap.core.configuration.dao.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.core.configuration.dao.GenericConfigurationDao;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for GenericModel2DtoPopulator.
 */
@IntegrationTest
public class DefaultGenericConfigurationDaoTest extends ServicelayerTransactionalTest
{

	private static final String CODE_SAP_CONFIGURATION = "SAPConfiguration";
	private static final String CODE_SAP_HTTP_DESTINATION = "SAPHTTPDestination";

	@Resource(name = "sapCoreGenericConfigurationDao")
	private GenericConfigurationDao classUnderTest;

	private final SAPConfigurationModel sapConfigurationModel1 = new SAPConfigurationModel();
	private final SAPConfigurationModel sapConfigurationModel2 = new SAPConfigurationModel();
	private final SAPHTTPDestinationModel sapHTTPDestinationModel1 = new SAPHTTPDestinationModel();
	private final SAPHTTPDestinationModel sapHTTPDestinationModel2 = new SAPHTTPDestinationModel();

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
		sapHTTPDestinationModel1.setHttpDestinationName("myHTTPDestination1");
		sapHTTPDestinationModel1.setTargetURL("http://a.b.com");
		modelService.save(sapHTTPDestinationModel1);
		sapHTTPDestinationModel2.setHttpDestinationName("myHTTPDestination2");
		sapHTTPDestinationModel2.setTargetURL("http://a.b.com");
		modelService.save(sapHTTPDestinationModel2);
		sapConfigurationModel1.setCore_name("myCoreName1");
		modelService.save(sapConfigurationModel1);
		sapConfigurationModel2.setCore_name("myCoreName2");
		modelService.save(sapConfigurationModel2);
	}

	@SuppressWarnings("javadoc")
	@After
	public void tearDown()
	{
		modelService.remove(sapConfigurationModel1);
		modelService.remove(sapConfigurationModel2);
		modelService.remove(sapHTTPDestinationModel1);
		modelService.remove(sapHTTPDestinationModel2);
	}

	/**
	 * Tests read all SAPConfiguration models.
	 */
	@Test
	public void testReadAllSAPConfigurationModels()
	{
		final List<ItemModel> sapConfigurationModels = classUnderTest.getAllModelsForCode(CODE_SAP_CONFIGURATION);
		assertEquals(2, sapConfigurationModels.size());
	}

	/**
	 * Tests read all SAPConfiguration models.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testReadAllDestinationModels()
	{
		final List<ItemModel> sapGlobalConfigurationModels = classUnderTest.getAllModelsForCode(CODE_SAP_HTTP_DESTINATION);
		assertEquals(2, sapGlobalConfigurationModels.size());
	}

}
