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
package de.hybris.platform.sap.core.configuration.datahub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.sap.core.configuration.populators.GenericModel2MapPopulator;
import de.hybris.platform.sap.core.configuration.test.SapcoreconfigurationSpringJUnitTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests the {@link ConfigurationSaveListener}.
 */
@UnitTest
public class DataHubTransferHandlerTest extends SapcoreconfigurationSpringJUnitTest
{
	//	@Resource(name = "sapCoreDataHubTransferConfigurationManager")
	private final DataHubTransferConfigurationManager dataHubTransferConfigurationManager = new DataHubTransferConfigurationManager();

	private final DataHubTransferHandler classUnderTest = new DataHubTransferHandler();
	private final SAPConfigurationModel sapConfigurationModel = new SAPConfigurationModel();
	private final SAPGlobalConfigurationModel sapGlobalConfigurationModel = new SAPGlobalConfigurationModel();

	@Override
	@SuppressWarnings(
	{ "javadoc", "unchecked", "rawtypes" })
	@Before
	public void setUp()
	{
		sapConfigurationModel.setCore_name("core_name");
		sapGlobalConfigurationModel.setCore_name("global_core_name");

		final AbstractPopulatingConverter converter = new PopulatingConverterMock();
		final ArrayList<GenericModel2MapPopulator> populatorList = new ArrayList<GenericModel2MapPopulator>();
		populatorList.add(new GenericModel2MapPopulator());
		converter.setPopulators(populatorList);

		DataHubTransferConfiguration transferConfig = new DataHubTransferConfiguration();
		transferConfig.setItemCode(SapcoreconfigurationConstants.ITEM_CODE_SAP_CONFIGURATION);
		transferConfig.setDataHubManager(dataHubTransferConfigurationManager);
		transferConfig.setConverter(converter);
		transferConfig.addToDataHubConfigurations();

		transferConfig = new DataHubTransferConfiguration();
		transferConfig.setItemCode(SapcoreconfigurationConstants.ITEM_CODE_SAP_GLOBAL_CONFIGURATION);
		transferConfig.setDataHubManager(dataHubTransferConfigurationManager);
		transferConfig.setConverter(converter);
		transferConfig.addToDataHubConfigurations();
	}

	/**
	 * test method createConfigurationMap.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateConfigurationMap()
	{
		AbstractPopulatingConverter<ItemModel, Map<String, Object>> converter = dataHubTransferConfigurationManager
				.getDataHubTransferConfigurations(SapcoreconfigurationConstants.ITEM_CODE_SAP_CONFIGURATION).get(0).getConverter();
		Map<String, Object> propertyMap = classUnderTest.createConfigurationMap(sapConfigurationModel, converter);
		assertNotNull(propertyMap);
		assertEquals("core_name", propertyMap.get("core_name"));

		converter = dataHubTransferConfigurationManager
				.getDataHubTransferConfigurations(SapcoreconfigurationConstants.ITEM_CODE_SAP_GLOBAL_CONFIGURATION).get(0)
				.getConverter();
		propertyMap = classUnderTest.createConfigurationMap(sapGlobalConfigurationModel, converter);
		assertNotNull(propertyMap);
		assertEquals("global_core_name", propertyMap.get("core_name"));
	}

	/**
	 * Create a Converter for testing.
	 */
	@SuppressWarnings("rawtypes")
	private class PopulatingConverterMock extends AbstractPopulatingConverter
	{

		@Override
		protected Object createTarget()
		{
			return new HashMap<String, Object>();
		}

	}

}
