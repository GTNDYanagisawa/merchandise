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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.servicelayer.session.SessionService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigurationServiceImplTest
{

	private static final String DUMMY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"8\" VALUE_TXT=\"Value 8\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";


	/**
	 *
	 */
	private static final String CONFIG_ID_2 = "asdasdwer4543556zgfhvchtr";

	/**
	 *
	 */
	private static final String CONFIG_ID_1 = "asdsafsdgftert6er6erzz";

	private ProductConfigurationServiceImpl cut;

	@Mock
	private ConfigurationProvider configurationProviderMock;

	@Mock
	private ConfigModel modelMock;

	@Mock
	private ConfigurationProviderFactory configurationProviderFactoryMock;

	@Mock
	private SessionService sessionService;

	@Before
	public void setup()
	{
		cut = new ProductConfigurationServiceImpl();
		MockitoAnnotations.initMocks(this);
		cut.setConfigurationProviderFactory(configurationProviderFactoryMock);
		Mockito.when(configurationProviderFactoryMock.getProvider()).thenReturn(configurationProviderMock);
		cut.setSessionService(sessionService);
	}

	@Test
	public void testRetrieveConfiguration() throws Exception
	{
		final String configId = "ABC";
		Mockito.when(configurationProviderMock.retrieveConfigurationModel(configId)).thenReturn(modelMock);

		final ConfigModel retrievedModel = cut.retrieveConfigurationModel(configId);

		assertTrue("Not delegated", retrievedModel == modelMock);
	}


	@Test
	public void testRetrieveExternalConfiguration() throws Exception
	{
		final String configId = "ABC";

		Mockito.when(configurationProviderMock.retrieveExternalConfiguration(configId)).thenReturn(DUMMY_XML);

		final String xmlString = cut.retrieveExternalConfiguration(configId);

		assertTrue("Not delegated", xmlString == DUMMY_XML);
	}

	@Test
	public void testRetrieveConfigurationNull() throws Exception
	{
		final String configId = "ABC";
		Mockito.when(configurationProviderMock.retrieveConfigurationModel(configId)).thenReturn(null);

		final ConfigModel retrievedModel = cut.retrieveConfigurationModel(configId);

		assertNull("Not just delegated", retrievedModel);

	}

	@Test
	public void testGetLockNotNull()
	{
		Assert.assertNotNull("Lock objects may not be null", ProductConfigurationServiceImpl.getLock(CONFIG_ID_1));
	}

	@Test
	public void testGetLockDifferrentForDifferntConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_2);
		Assert.assertNotSame("Lock objects should not be same!", lock1, lock2);
	}

	@Test
	public void testGetLockSameforSameConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		Assert.assertSame("Lock objects should be same!", lock1, lock2);
	}

	@Test
	public void testGetLockMapShouldNotGrowEndless()
	{

		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final int maxLocks = ProductConfigurationServiceImpl.getMaxLocksPerMap() * 2;
		for (int ii = 0; ii <= maxLocks; ii++)
		{
			ProductConfigurationServiceImpl.getLock(String.valueOf(ii));
		}
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		Assert.assertNotSame("Lock objects should not be same!", lock1, lock2);
	}
}
