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
package com.hybris.dataonboarding.rest.client;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.hybris.dataonboarding.api.ImportRunFacade;
import com.hybris.dataonboarding.api.ImportRun;

/**
 * Unit test for making sure the mocks are available.
 */
@IntegrationTest
public class DataonboardingMockTest extends ServicelayerBaseTest
{

	@Resource
	private ImportRunFacade importRunFacade;

	@Test
	@Ignore
	public void testGetImportRunMock()
	{
		Collection<ImportRun> importRuns = importRunFacade.getImportRuns();
		Assert.assertTrue("no import runs", importRuns.isEmpty());
	}
}
