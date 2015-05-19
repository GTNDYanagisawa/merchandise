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
package de.hybris.platform.sap.core.configuration.rfc.test;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sap.core.configuration.enums.SncQoP;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Test for RFC destination service.
 */
@IntegrationTest
public class RFCDestinationServiceTest extends ServicelayerTransactionalTest
{

	private SAPRFCDestinationModel rfcDestinationModel = null;

	/**
	 * Variable for the model service.
	 */
	@Resource
	private ModelService modelService;


	@SuppressWarnings("javadoc")
	@Before
	public void setUp()
	{
		rfcDestinationModel = new SAPRFCDestinationModel();

		rfcDestinationModel.setPassword("password");
		rfcDestinationModel.setRfcDestinationName("rfcName");
		rfcDestinationModel.setSid("7");
		rfcDestinationModel.setUserid("1337");
		rfcDestinationModel.setClient("42");
		rfcDestinationModel.setTargetHost("host");
		rfcDestinationModel.setInstance("instance");
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testRFCDestinationIncompleteSncData()
	{
		rfcDestinationModel.setSncMode(true);
		boolean modelSaved = true;
		try
		{
			modelService.save(rfcDestinationModel);
		}
		catch (final ModelSavingException e)
		{
			assertThat(e.getCause().getClass()).isEqualTo(InterceptorException.class);
			modelSaved = false;
		}

		if (modelSaved)
		{
			modelService.remove(rfcDestinationModel);
		}

	}

	@SuppressWarnings("javadoc")
	@Test
	public void testRFCDestinationCompleteSncData()
	{
		rfcDestinationModel.setSncMode(true);
		rfcDestinationModel.setSncPartnerName("SNCPartnerName");
		rfcDestinationModel.setSncQoP(SncQoP.INTEGRITY_PROTECTION);

		modelService.save(rfcDestinationModel);

		modelService.remove(rfcDestinationModel);
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testRFCDestinationSncModeFalse()
	{
		modelService.save(rfcDestinationModel);

		modelService.remove(rfcDestinationModel);
	}
}
