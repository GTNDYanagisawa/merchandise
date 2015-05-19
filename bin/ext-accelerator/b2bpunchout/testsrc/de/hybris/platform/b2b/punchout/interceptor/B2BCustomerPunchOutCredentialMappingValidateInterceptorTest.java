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
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.punchout.model.B2BCustomerPunchOutCredentialMappingModel;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link B2BCustomerPunchOutCredentialMappingValidateInterceptor}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class B2BCustomerPunchOutCredentialMappingValidateInterceptorTest
{

	@Mock
	private InterceptorContext ctx;
	@Mock
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> customerService;
	@Mock
	private PunchOutCredentialService punchOutCredentialService;
	@Mock
	private L10NService l10NService;
	private B2BCustomerPunchOutCredentialMappingValidateInterceptor validator;

	@Before
	public void setup()
	{
		validator = new B2BCustomerPunchOutCredentialMappingValidateInterceptor();
		validator.setCustomerService(customerService);
		validator.setPunchOutCredentialService(punchOutCredentialService);
		validator.setL10NService(l10NService);
	}

	@Test(expected = InterceptorException.class)
	public void testInexistentCustomer() throws InterceptorException
	{
		final B2BCustomerPunchOutCredentialMappingModel model = buildModel(null);
		Mockito.when(customerService.getUserForUID(Mockito.anyString())).thenReturn(null);
		validator.onValidate(model, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void testNoIdentities() throws InterceptorException
	{
		final Set<PunchOutCredentialModel> emptyIdentities = new HashSet<>();
		final B2BCustomerPunchOutCredentialMappingModel model = buildModel(emptyIdentities);
		model.setCredentials(new HashSet());
		Mockito.when(customerService.getUserForUID(Mockito.anyString())).thenReturn(new B2BCustomerModel());
		validator.onValidate(model, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void testCredentialAlreadyExistent() throws InterceptorException
	{
		final PunchOutCredentialModel credential = Mockito.spy(new PunchOutCredentialModel());
		final Set<PunchOutCredentialModel> identities = new HashSet<>();
		identities.add(credential);
		final B2BCustomerPunchOutCredentialMappingModel model = buildModel(identities);
		model.getB2bCustomer().setUid(Long.toString(System.nanoTime()));
		final B2BCustomerPunchOutCredentialMappingModel existentModel = buildModel(identities);
		existentModel.getB2bCustomer().setUid(Long.toString(System.nanoTime()));
		credential.setB2BCustomerPunchOutCredentialMapping(existentModel);
		Mockito.when(customerService.getUserForUID(Mockito.anyString())).thenReturn(new B2BCustomerModel());
		Mockito.when(punchOutCredentialService.getPunchOutCredential(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(credential);
		validator.onValidate(model, ctx);
	}

	private B2BCustomerPunchOutCredentialMappingModel buildModel(final Set<PunchOutCredentialModel> identities)
	{
		final B2BCustomerPunchOutCredentialMappingModel model = Mockito.spy(new B2BCustomerPunchOutCredentialMappingModel());
		final B2BCustomerModel customer = Mockito.spy(new B2BCustomerModel());
		model.setB2bCustomer(customer);
		model.setCredentials(identities);
		return model;
	}
}
