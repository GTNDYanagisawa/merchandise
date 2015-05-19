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
package de.hybris.platform.secureportaladdon.email.process.actions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class RegistrationCleanupActionTest
{
	private RegistrationCleanupAction registrationCleanupAction;


	@Before
	public void setUp()
	{
		registrationCleanupAction = new RegistrationCleanupAction();
	}


	@Test(expected = IllegalArgumentException.class)
	public void callExecuteActionBusinessProcessModelNotTypeOfB2BRegistrationProcessModel()
	{
		final ConsignmentProcessModel wrongBusinessModel = Mockito.mock(ConsignmentProcessModel.class);

		registrationCleanupAction.executeAction(wrongBusinessModel);

	}


	@Test
	public void callExecuteActionNoDeleteCustomer()
	{
		registrationCleanupAction.setDeleteCustomer(false);

		final B2BRegistrationProcessModel businessProcessModel = new B2BRegistrationProcessModel();
		final B2BRegistrationModel b2bRegistrationModel = Mockito.mock(B2BRegistrationModel.class);

		businessProcessModel.setRegistration(b2bRegistrationModel);

		final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
		businessProcessModel.setCustomer(customerModel);

		final ModelService modelService = Mockito.mock(ModelService.class);

		registrationCleanupAction.setModelService(modelService);

		registrationCleanupAction.executeAction(businessProcessModel);

		//Mockito.verify(modelService).remove(businessProcessModel.getRegistration());
		Mockito.verify(modelService, Mockito.never()).remove(businessProcessModel.getCustomer());

	}


	@Test
	public void callExecuteActionDeleteCustomer()
	{
		registrationCleanupAction.setDeleteCustomer(true);

		final B2BRegistrationProcessModel businessProcessModel = new B2BRegistrationProcessModel();
		final B2BRegistrationModel b2bRegistrationModel = Mockito.mock(B2BRegistrationModel.class);

		businessProcessModel.setRegistration(b2bRegistrationModel);

		final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
		businessProcessModel.setCustomer(customerModel);

		final ModelService modelService = Mockito.mock(ModelService.class);

		registrationCleanupAction.setModelService(modelService);

		registrationCleanupAction.executeAction(businessProcessModel);

		//Mockito.verify(modelService).remove(businessProcessModel.getRegistration());
		Mockito.verify(modelService).remove(businessProcessModel.getCustomer());

	}


}
