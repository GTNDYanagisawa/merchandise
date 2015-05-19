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
package de.hybris.platform.accountsummaryaddon.controllers.pages;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;


/**
 * Controller sample unit test
 * 
 */
@IntegrationTest
@WebAppConfiguration(webroot = "module:yb2bacceleratorstorefront/web/webroot", locations =
{ "module:yb2bacceleratorstorefront/**/web-application-config.xml",
		"module:addoncommon/resources/addoncommon/web/spring/addoncommon-b2b-web-spring.xml",
		"module:addoncommon/resources/addoncommon/web/spring/addoncommon-web-spring.xml",
		"module:accountsummary/resources/accountsummary/web/spring/accountsummary-web-spring.xml" })
public class AccountStatusControllerTest extends AbstractWebTest
{

	@Before
	public void setUp() throws Exception
	{
		final Tenant tenant = Registry.activateMasterTenant();
		setDefaultSite("Powertools Site");
		TestCase.assertNotNull(tenant.getJaloConnection().createAnonymousCustomerSession());
	}

	@Test
	public void shouldResultAccountStatusTemplate() throws Exception
	{
		getMockMvc().perform(
				get(MOCK_SERVLET_PATH + "/my-company/organization-management/accountstatus/details").param("unit", "Pronto").accept(
						MediaType.APPLICATION_JSON)).andExpect(view().name("addon:/accountsummaryaddon/pages/accountStatusTemplate"));
	}
}
