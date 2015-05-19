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
package de.hybris.platform.servicelayer.tenant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.servicelayer.MockTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@UnitTest
@ContextConfiguration(locations =
{ "classpath:/servicelayer/test/servicelayer-mock-base-test.xml" })
public class MockTenantTest extends MockTest
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(MockTenantTest.class);


	@Resource(name = "tenantService")
	private TenantService tenantService;

	@Resource(name = "tenantFactory")
	private Tenant tenant;


	@Test
	public void testEventRegistration()
	{
		assertEquals(tenant.getTenantID(), tenantService.getCurrentTenantId());
	}

	@Test
	public void testCurrentTenant() throws Exception
	{
		assertEquals("junit", tenant.getTenantID());
		assertEquals("junit", tenantService.getCurrentTenantId());

		tenantService = (TenantService) applicationContext.getBean("tenantService");
		assertEquals("junit", tenantService.getCurrentTenantId());
	}

	@Test
	public void testOtherServicesDiffer() throws Exception
	{
		final ModelService one = (ModelService) applicationContext.getBean("modelService");
		final ModelService one2 = (ModelService) applicationContext.getBean("modelService");
		assertTrue(one == one2);
	}

}
