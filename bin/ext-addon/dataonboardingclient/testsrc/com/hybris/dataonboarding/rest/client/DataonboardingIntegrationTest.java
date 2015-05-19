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

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hybris.dataonboarding.api.ImportRunFacade;
import com.hybris.dataonboarding.api.ImportRun;


/**
 * Manual test for calling the DOB service via REST client embedded in the platform.
 */
@ManualTest
public class DataonboardingIntegrationTest extends ServicelayerBaseTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DataonboardingIntegrationTest.class.getName());

	@Resource
	private ImportRunFacade importRunFacade;

	@Test
	public void testGetImportRun()
	{
		Assert.assertTrue("no import runs", importRunFacade.getImportRuns().isEmpty());
	}
}
