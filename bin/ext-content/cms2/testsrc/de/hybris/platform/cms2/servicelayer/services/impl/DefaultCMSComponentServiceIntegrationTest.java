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
package de.hybris.platform.cms2.servicelayer.services.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultCMSComponentServiceIntegrationTest extends ServicelayerTest // NOPMD Junit4
{
	@Resource
	private CMSComponentService cmsComponentService;

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService#isContainer(String)} .
	 */
	@Test
	public void shouldReturnFalseForAbstractCMSComponentIsContainer()
	{
		final boolean isContainer = cmsComponentService.isComponentContainer("AbstractCMSComponent");
		assertThat(isContainer).isFalse();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService#isContainer(String)} .
	 */
	@Test
	public void shouldReturnTrueForAbstractCMSComponentContainerIsContainer()
	{
		final boolean isContainer = cmsComponentService.isComponentContainer("AbstractCMSComponentContainer");
		assertThat(isContainer).isTrue();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService#isContainer(String)} .
	 */
	@Test
	public void shouldReturnTrueForABTestCMSComponentContainerIsContainer()
	{
		final boolean isContainer = cmsComponentService.isComponentContainer("ABTestCMSComponentContainer");
		assertThat(isContainer).isTrue();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService#isContainer(String)} .
	 */
	@Test
	public void shouldReturnFalseForSimpleCMSComponentIsContainer()
	{
		final boolean isContainer = cmsComponentService.isComponentContainer("SimpleCMSComponent");
		assertThat(isContainer).isFalse();
	}
}
