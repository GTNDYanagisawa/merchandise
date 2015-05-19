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
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationRejectedProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class SendRejectedEmailAutomatedWorkflowTemplateJobUnitTest
{
	private SendRejectedEmailAutomatedWorkflowTemplateJob sendRejectedEmailTemplateJob;
	private CustomerModel customerModel;
	private TitleModel titleModel;
	private B2BRegistrationModel registrationModel;
	private BusinessProcessService businessProcessService;
	private ModelService modelService;

	private static final String PROCESS_NAME = "registrationApprovedEmailProcess";
	private static final String NAME = "Richard Feynman";
	private static final String TITLE = "Mr";
	private static final String DEFAULT_EMAIL = "richard.feynman@test.ca";
	private static final String REJECTED_REASON = "Your registration is rejected";


	@Before
	public void setup()
	{
		sendRejectedEmailTemplateJob = new SendRejectedEmailAutomatedWorkflowTemplateJob();
		sendRejectedEmailTemplateJob.setProcessDefinitionName(PROCESS_NAME);

		modelService = mock(ModelService.class);
		sendRejectedEmailTemplateJob.setModelService(modelService);

		businessProcessService = mock(BusinessProcessService.class);
		sendRejectedEmailTemplateJob.setBusinessProcessService(businessProcessService);

		customerModel = createCustomerModel();
		registrationModel = createB2BRegistrationModel();

	}

	@Test
	public void testCreateProcessModel()
	{
		final B2BRegistrationRejectedProcessModel process = new B2BRegistrationRejectedProcessModel();

		when(businessProcessService.createProcess(Mockito.anyString(), Mockito.eq(PROCESS_NAME))).thenReturn(process);

		final B2BRegistrationRejectedProcessModel result = sendRejectedEmailTemplateJob.createProcessModel(customerModel,
				registrationModel);

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

		final B2BRegistrationModel b2bRegistration = new B2BRegistrationModel();

		b2bRegistration.setTitle(titleModel);

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

		b2bRegistration.setCompanyAddressCountry(country);
		b2bRegistration.setBaseStore(baseStore);
		b2bRegistration.setCmsSite(CMSsite);
		b2bRegistration.setLanguage(language);
		b2bRegistration.setCurrency(currency);
		b2bRegistration.setRejectReason(REJECTED_REASON);

		return b2bRegistration;
	}
}
