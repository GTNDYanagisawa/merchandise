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
package de.hybris.platform.b2b.punchout.interceptor;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link PunchOutCredentialValidateInterceptor}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutCredentialValidateInterceptorTest
{
	@Mock
	private InterceptorContext ctx;
	@Mock
	private PunchOutCredentialService punchOutCredentialService;
	@Mock
	private L10NService l10NService;
	private PunchOutCredentialValidateInterceptor validator;

	private PunchOutCredentialModel existingModel;
	private PunchOutCredentialModel newModel;
	private static final String DOMAIN = "domain";
	private static final String IDENTITY = "id";

	@Before
	public void setup()
	{
		existingModel = new PunchOutCredentialModel();
		existingModel.setDomain(DOMAIN);
		existingModel.setIdentity(IDENTITY);

		newModel = new PunchOutCredentialModel();
		newModel.setDomain(DOMAIN);
		newModel.setIdentity(IDENTITY);


		validator = new PunchOutCredentialValidateInterceptor();
		validator.setPunchOutCredentialService(punchOutCredentialService);
		validator.setL10NService(l10NService);
	}

	@Test(expected = InterceptorException.class)
	public void testCreatingNewCredentials() throws InterceptorException
	{
		//the service will find the other credentials that the one being created
		Mockito.when(punchOutCredentialService.getPunchOutCredential(DOMAIN, IDENTITY)).thenReturn(existingModel);
		validator.onValidate(newModel, ctx);
	}


}
