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
package de.hybris.platform.secureportaladdon.facades.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.secureportaladdon.data.B2BRegistrationData;
import de.hybris.platform.secureportaladdon.exceptions.CustomerAlreadyExistsException;
import de.hybris.platform.secureportaladdon.facades.B2BRegistrationWorkflowFacade;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.test.TransactionTest;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@IntegrationTest
public class DefaultB2BRegistrationFacadeUnitTest extends TransactionTest
{

	private CMSSiteService cmsSiteService;

	private CommonI18NService commonI18NService;

	private ModelService modelService;

	private BaseStoreService baseStoreService;

	private UserService userService;

	private B2BRegistrationWorkflowFacade b2bRegistrationWorkflowFacade;

	private WorkflowTemplateService workflowTemplateService;

	private DefaultB2BRegistrationFacade b2bRegistrationFacade;

	private TitleModel titleModel;

	@Before
	public void setUp()
	{

		b2bRegistrationFacade = new DefaultB2BRegistrationFacade();

		cmsSiteService = mock(CMSSiteService.class);
		b2bRegistrationFacade.setCmsSiteService(cmsSiteService);

		baseStoreService = mock(BaseStoreService.class);
		b2bRegistrationFacade.setBaseStoreService(baseStoreService);


		commonI18NService = mock(CommonI18NService.class);
		b2bRegistrationFacade.setCommonI18NService(commonI18NService);

		modelService = mock(ModelService.class);
		b2bRegistrationFacade.setModelService(modelService);


		userService = mock(UserService.class);
		b2bRegistrationFacade.setUserService(userService);

		b2bRegistrationWorkflowFacade = mock(B2BRegistrationWorkflowFacade.class);
		b2bRegistrationFacade.setB2bRegistrationWorkflowFacade(b2bRegistrationWorkflowFacade);


		workflowTemplateService = mock(WorkflowTemplateService.class);
		b2bRegistrationFacade.setWorkflowTemplateService(workflowTemplateService);
	}

	@Test(expected = CustomerAlreadyExistsException.class)
	public void registerTestExpectedException() throws CustomerAlreadyExistsException
	{

		final B2BRegistrationData data = createB2BRegistrationData();
		final B2BRegistrationModel registration = createB2BRegistrationModel();
		final CustomerModel customer = createCustomerModel();

		when(userService.isUserExisting(data.getEmail())).thenReturn(true);

		final WorkflowTemplateModel workflowTemplate = mock(WorkflowTemplateModel.class);
		when(workflowTemplateService.getWorkflowTemplateForCode(Mockito.anyString())).thenReturn(workflowTemplate);

		when(modelService.create(B2BRegistrationModel.class)).thenReturn(registration);
		when(modelService.create(CustomerModel.class)).thenReturn(customer);

		b2bRegistrationFacade.register(data);
	}


	@Test
	public void registerTest() throws CustomerAlreadyExistsException
	{

		final B2BRegistrationData data = createB2BRegistrationData();
		final B2BRegistrationModel registration = createB2BRegistrationModel();
		final CustomerModel customer = createCustomerModel();

		when(userService.isUserExisting(data.getEmail())).thenReturn(false);

		final WorkflowTemplateModel workflowTemplate = mock(WorkflowTemplateModel.class);
		when(workflowTemplateService.getWorkflowTemplateForCode(Mockito.anyString())).thenReturn(workflowTemplate);

		when(modelService.create(B2BRegistrationModel.class)).thenReturn(registration);
		when(modelService.create(CustomerModel.class)).thenReturn(customer);

		b2bRegistrationFacade.register(data);

		verify(modelService).save(registration);
		verify(b2bRegistrationWorkflowFacade).launchWorkflow(workflowTemplate, registration);

	}

	private B2BRegistrationData createB2BRegistrationData()
	{
		final B2BRegistrationData data = new B2BRegistrationData();

		data.setPosition("Programmer");
		data.setTitleCode("01");
		data.setCompanyAddressCity("Montreal");
		data.setCompanyName("companyName");
		data.setMessage("Test");
		data.setTelephoneExtension("1234");
		data.setCompanyAddressPostalCode("J7V0J6");
		data.setEmail("test@gmail.com");
		data.setCompanyAddressStreet("Main St.");
		data.setCompanyAddressStreetLine2("");
		data.setName("Test Name");
		data.setCompanyAddressRegion("New York");
		data.setCompanyAddressCountryIso("US");
		data.setTelephone("122-232-2222");

		return data;
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

	private CustomerModel createCustomerModel()
	{

		final CustomerModel customer = new CustomerModel();

		customer.setName("Test Name");
		customer.setUid("test@gmail.com");

		final TitleModel titleModel = new TitleModel();
		titleModel.setCode("01");
		customer.setTitle(titleModel);

		return customer;

	}

}
