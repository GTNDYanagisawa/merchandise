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
package de.hybris.platform.secureportaladdon.controllers;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import de.hybris.platform.secureportaladdon.facades.B2BRegistrationFacade;
import de.hybris.platform.secureportaladdon.forms.RegistrationForm;
import com.hybris.ps.addon.testsupport.AbstractWebTest;
import com.hybris.ps.addon.testsupport.WebAppConfiguration;


@IntegrationTest
@Transactional
@WebAppConfiguration(webroot = "module:yb2bacceleratorstorefront/web/webroot", locations =
{ "module:yb2bacceleratorstorefront/**/web-application-config.xml",
		"module:captchaaddon/resources/captchaaddon/web/spring/captchaaddon-web-spring.xml",
		"module:addoncommon/resources/addoncommon/web/spring/addoncommon-b2b-web-spring.xml",
		"module:addoncommon/resources/addoncommon/web/spring/addoncommon-web-spring.xml",
		"module:secureportaladdon/resources/secureportaladdon/web/spring/secureportaladdon-web-spring.xml" })
public class DefaultB2BRegistrationControllerIntegrationTest extends AbstractWebTest
{
	public static final String ROOT = "/";

	@Resource
	private CheckoutFacade checkoutFacade;

	@Resource
	private ModelService modelService;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	private B2BRegistrationFacade b2bRegistrationFacade;


	@Before
	public void setUp() throws Exception
	{

		Registry.activateMasterTenant();
		setDefaultSite("Powertools Site");

	}

	private ModelService getModelService()
	{
		return modelService;
	}

	@Test
	public void testShowRegistrationPageNotEnableRegistration() throws Exception
	{

		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
		currentSite.setEnableRegistration(false);
		LOG.info("currentSite=" + currentSite.getName());
		getModelService().save(currentSite);

		final MvcResult result = getMockMvc().perform(get(MOCK_SERVLET_PATH + "/register")).andReturn();
		final String redirectedUrl = result.getResponse().getRedirectedUrl();
		Assert.assertTrue(StringUtils.endsWith(redirectedUrl, ROOT));

	}


	@Test
	public void testShowRegistrationPageEnableRegistration() throws Exception
	{

		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
		currentSite.setEnableRegistration(true);
		LOG.info("currentSite=" + currentSite.getName());
		getModelService().save(currentSite);

		getMockMvc().perform(get(MOCK_SERVLET_PATH + "/register")).andExpect(status().isOk())
				.andExpect(forwardedUrl("/WEB-INF/views/addons//secureportaladdon/desktop//pages/accountRegistration.jsp"))
				.andExpect(view().name("addon:/secureportaladdon/pages/accountRegistration"));

	}

	@Test
	public void testSubmitRegistrationSuccessful() throws Exception
	{

		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
		currentSite.setEnableRegistration(true);
		LOG.info("currentSite=" + currentSite.getName());
		getModelService().save(currentSite);

		getMockMvc()
				.perform(
						post(MOCK_SERVLET_PATH + "/register").contentType(MediaType.APPLICATION_FORM_URLENCODED).body(
								IntegrationTestUtil.convertObjectToFormUrlEncodedBytes(getTestRegistrationForm())))
				.andExpect(status().isOk())
				.andExpect(forwardedUrl("/WEB-INF/views/addons//secureportaladdon/desktop//pages/accountRegistration.jsp"))
				.andExpect(view().name("addon:/secureportaladdon/pages/accountRegistration"));

	}

	@Test
	public void testSubmitRegistrationWithValidation() throws Exception
	{

		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
		currentSite.setEnableRegistration(true);
		LOG.info("currentSite=" + currentSite.getName());
		getModelService().save(currentSite);

		//this.mockMvc.perform(post(MOCK_SERVLET_PATH + "/register")).

		getMockMvc()
				.perform(
						post(MOCK_SERVLET_PATH + "/register").contentType(MediaType.APPLICATION_FORM_URLENCODED).body(
								IntegrationTestUtil.convertObjectToFormUrlEncodedBytes(getMissingMandatoryRegistrationForm())))
				.andExpect(forwardedUrl("/WEB-INF/views/addons//secureportaladdon/desktop//pages/accountRegistration.jsp"))
				.andExpect(view().name("addon:/secureportaladdon/pages/accountRegistration"))
				.andExpect(model().attributeHasErrors("registrationForm"))
				.andExpect(model().attributeHasFieldErrors("registrationForm", "email"));

	}

	private RegistrationForm getTestRegistrationForm()
	{
		final RegistrationForm testRegistrationForm = new RegistrationForm();
		testRegistrationForm.setCompanyAddressCity("Montreal");
		testRegistrationForm.setCompanyAddressCountryIso("CA");
		testRegistrationForm.setCompanyAddressStreet("999 Main St");
		testRegistrationForm.setCompanyName("Hybris");
		testRegistrationForm.setEmail("lucy.test@hybris.com");
		testRegistrationForm.setName("Lucy Test");
		testRegistrationForm.setPosition("Developer");
		testRegistrationForm.setTelephone("5148866666");
		testRegistrationForm.setTitleCode("Ms");

		return testRegistrationForm;
	}

	private RegistrationForm getMissingMandatoryRegistrationForm()
	{
		final RegistrationForm testRegistrationForm = new RegistrationForm();
		testRegistrationForm.setCompanyAddressCity("Montreal");
		testRegistrationForm.setCompanyAddressCountryIso("CA");
		testRegistrationForm.setCompanyAddressStreet("999 Main St");
		testRegistrationForm.setCompanyName("Hybris");
		testRegistrationForm.setEmail("");
		testRegistrationForm.setName("");
		testRegistrationForm.setPosition("Developer");
		testRegistrationForm.setTelephone("5148866666");
		testRegistrationForm.setTitleCode("Ms");

		return testRegistrationForm;
	}

}
