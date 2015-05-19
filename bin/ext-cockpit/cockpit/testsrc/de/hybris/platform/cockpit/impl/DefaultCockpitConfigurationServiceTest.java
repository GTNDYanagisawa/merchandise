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
package de.hybris.platform.cockpit.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.daos.CockpitConfigurationDao;
import de.hybris.platform.cockpit.model.CockpitUIComponentConfigurationModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test class that tests {@link DefaultCockpitConfigurationService}
 */
@UnitTest
public class DefaultCockpitConfigurationServiceTest
{
	private DefaultCockpitConfigurationService cockpitConfigurationService;
	private List<CockpitUIComponentConfigurationModel> componentConfigurationModels;
	private CockpitUIComponentConfigurationModel model1;
	private CockpitUIComponentConfigurationModel model2;

	@Mock
	private CockpitConfigurationDao cockpitConfigurationDao;



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		cockpitConfigurationService = new DefaultCockpitConfigurationService();
		cockpitConfigurationService.setCockpitConfigurationDao(cockpitConfigurationDao);

		componentConfigurationModels = new ArrayList<CockpitUIComponentConfigurationModel>(2);
		model1 = mock(CockpitUIComponentConfigurationModel.class);
		when(model1.getCode()).thenReturn("model1Code");
		model2 = mock(CockpitUIComponentConfigurationModel.class);
		componentConfigurationModels.add(0, model1);
		componentConfigurationModels.add(1, model2);
	}



	@Test
	public void testGetComponentConfigurationsForPrincipal()
	{
		final PrincipalModel principalModel = mock(PrincipalModel.class);

		when(cockpitConfigurationDao.findComponentConfigurationsByPrincipal(principalModel)).thenReturn(
				componentConfigurationModels);

		final List<CockpitUIComponentConfigurationModel> resultModels = cockpitConfigurationService
				.getComponentConfigurationsForPrincipal(principalModel);

		Assert.assertEquals("Got different number of results", 2, resultModels.size());
	}


	@Test
	public void testGetDedicatedComponentConfigurationsForPrincipal()
	{
		final PrincipalModel principalModel = mock(PrincipalModel.class);

		when(cockpitConfigurationDao.findDedicatedComponentConfigurationsByPrincipal(principalModel)).thenReturn(
				componentConfigurationModels);

		final List<CockpitUIComponentConfigurationModel> resultModels = cockpitConfigurationService
				.getDedicatedComponentConfigurationsForPrincipal(principalModel);

		Assert.assertEquals("Got different number of results", 2, resultModels.size());
	}


	@Test
	public void testGetComponentConfiguration()
	{
		final PrincipalModel principalModel = mock(PrincipalModel.class);
		final String objectTemplateCode = "mockTemplateCode";
		final String code = "mockCode";

		when(cockpitConfigurationDao.findComponentConfigurations(principalModel, objectTemplateCode, code)).thenReturn(
				componentConfigurationModels);

		try
		{
			cockpitConfigurationService.getComponentConfiguration(principalModel, objectTemplateCode, code);
			Assert.fail("Should throw exception!");
		}
		catch (final AmbiguousIdentifierException aie)
		{
			// OK
		}

		componentConfigurationModels.remove(1);

		final CockpitUIComponentConfigurationModel resultModel = cockpitConfigurationService.getComponentConfiguration(
				principalModel, objectTemplateCode, code);

		Assert.assertNotNull(resultModel);
		Assert.assertEquals("Wrong configuration returned", "model1Code", resultModel.getCode());
	}


}
