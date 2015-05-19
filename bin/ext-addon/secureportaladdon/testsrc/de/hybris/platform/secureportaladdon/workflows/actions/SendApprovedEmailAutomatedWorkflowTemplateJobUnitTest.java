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
package de.hybris.platform.secureportaladdon.workflows.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationApprovedProcessModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class SendApprovedEmailAutomatedWorkflowTemplateJobUnitTest
{
	private SendApprovedEmailAutomatedWorkflowTemplateJob sendApprovedEmailTemplateJob;
	private SendEmailAutomatedWorkflowTemplateJob sendEmailTemplateJob;
	private B2BRegistrationApprovedProcessModel approvedProcessModel;
	private CustomerModel customerModel;
	private TitleModel titleModel;
	private B2BRegistrationModel registrationModel;
	private SecureTokenService secureTokenService;
	private BusinessProcessService businessProcessService;
	private ModelService modelService;

	private static final String PROCESS_NAME = "registrationApprovedEmailProcess";
	private static final String NAME = "Richard Feynman";
	private static final String TITLE = "Mr";
	private static final String DEFAULT_EMAIL = "richard.feynman@Ttest.ca";

	@Before
	public void setup()
	{
		sendApprovedEmailTemplateJob = new SendApprovedEmailAutomatedWorkflowTemplateJob();
		sendApprovedEmailTemplateJob.setProcessDefinitionName(PROCESS_NAME);

		approvedProcessModel = new B2BRegistrationApprovedProcessModel();
		sendEmailTemplateJob = new SendEmailAutomatedWorkflowTemplateJob();

		modelService = mock(ModelService.class);
		sendApprovedEmailTemplateJob.setModelService(modelService);

		businessProcessService = mock(BusinessProcessService.class);
		sendApprovedEmailTemplateJob.setBusinessProcessService(businessProcessService);

		secureTokenService = mock(SecureTokenService.class);
		sendApprovedEmailTemplateJob.setSecureTokenService(secureTokenService);

		customerModel = createCustomerModel();
		registrationModel = createB2BRegistrationModel();

	}

	@Test
	public void testCreateProcessModel()
	{
		final B2BRegistrationApprovedProcessModel process = new B2BRegistrationApprovedProcessModel();

		when(businessProcessService.createProcess(Mockito.anyString(), Mockito.eq(PROCESS_NAME))).thenReturn(process);

		when(secureTokenService.encryptData(Mockito.any(SecureToken.class))).thenReturn("MYENCRYPTION");

		final B2BRegistrationApprovedProcessModel result = sendApprovedEmailTemplateJob.createProcessModel(customerModel,
				registrationModel);

		Mockito.verify(modelService).save(customerModel);

		assertEquals(customerModel.getToken(), "MYENCRYPTION");

		assertEquals("the B2BRegistrationApprovedProcessModel is expected", process, result);
	}

	private CustomerModel createCustomerModel()
	{

		final CustomerModel customer = new CustomerModel();

		customer.setName(NAME);
		customer.setUid(DEFAULT_EMAIL);

		final TitleModel titleModel = new TitleModel();
		titleModel.setCode(TITLE);
		customer.setTitle(titleModel);

		return customer;

	}

	private B2BRegistrationModel createB2BRegistrationModel()
	{

		final B2BRegistrationModel b2BRegistration = new B2BRegistrationModel();

		b2BRegistration.setTitle(titleModel);

		final CountryModel country = new CountryModel();
		country.setIsocode("US");
		country.setActive(Boolean.TRUE);

		final BaseStoreModel baseStore = new BaseStoreModel();
		final Locale locale = Locale.getDefault();
		baseStore.setName("cms site", locale);

		final CMSSiteModel CMSsite = new CMSSiteModel();

		final LanguageModel language = new LanguageModel();
		language.setIsocode("US");

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("US");

		b2BRegistration.setCompanyAddressCountry(country);
		b2BRegistration.setBaseStore(baseStore);
		b2BRegistration.setCmsSite(CMSsite);
		b2BRegistration.setLanguage(language);
		b2BRegistration.setCurrency(currency);

		return b2BRegistration;

	}

}
