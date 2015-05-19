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
package de.hybris.platform.secureportaladdon.workflows;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationProcessModel;
import de.hybris.platform.secureportaladdon.workflows.actions.SendEmailAutomatedWorkflowTemplateJob;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class SendEmailAutomatedWorkflowTemplateJobTest
{

	private SendEmailAutomatedWorkflowTemplateJob sendEmailWorkflowJob;

	private WorkflowAttachmentService workflowAttachmentService;
	private WorkflowActionModel workflowActionModel;

	private CustomerModel customerModel;
	private B2BRegistrationModel b2bRegistrationModel;

	private BusinessProcessService businessProcessService;

	private B2BRegistrationProcessModel b2bRegistrationProcessModel;

	private ModelService modelService;

	private UserService userService;





	@Before
	public void setUp()
	{
		sendEmailWorkflowJob = new SendEmailAutomatedWorkflowTemplateJob();
		workflowActionModel = Mockito.mock(WorkflowActionModel.class);

		workflowAttachmentService = Mockito.mock(WorkflowAttachmentService.class);
		sendEmailWorkflowJob.setWorkflowAttachmentService(workflowAttachmentService);

		customerModel = Mockito.mock(CustomerModel.class);
		b2bRegistrationModel = Mockito.mock(B2BRegistrationModel.class);

		businessProcessService = Mockito.mock(BusinessProcessService.class);
		sendEmailWorkflowJob.setBusinessProcessService(businessProcessService);

		b2bRegistrationProcessModel = Mockito.mock(B2BRegistrationProcessModel.class);


		modelService = Mockito.mock(ModelService.class);
		sendEmailWorkflowJob.setModelService(modelService);

		userService = Mockito.mock(UserService.class);
		sendEmailWorkflowJob.setUserService(userService);


	}


	@Test(expected = IllegalArgumentException.class)
	public void callPerformWithNoB2BRegistrationModel()
	{
		sendEmailWorkflowJob.perform(workflowActionModel);
	}


	@Test(expected = IllegalArgumentException.class)
	public void callPerformWithNoCustomerModel()
	{

		final List<ItemModel> b2bRegistrationModelList = new ArrayList<>();
		b2bRegistrationModelList.add(b2bRegistrationModel);

		Mockito.when(
				workflowAttachmentService.getAttachmentsForAction(workflowActionModel,
						"de.hybris.platform.secureportaladdon.model.B2BRegistrationModel")).thenReturn(b2bRegistrationModelList);

		Mockito.when(userService.getUserForUID(Mockito.anyString(), Mockito.eq(CustomerModel.class))).thenReturn(null);

		sendEmailWorkflowJob.perform(workflowActionModel);

	}

	@Test
	public void callPerformWithValidB2BRegistrationModel()
	{

		final List<ItemModel> b2bRegistrationModelList = new ArrayList<>();
		b2bRegistrationModelList.add(b2bRegistrationModel);

		Mockito.when(
				workflowAttachmentService.getAttachmentsForAction(workflowActionModel,
						"de.hybris.platform.secureportaladdon.model.B2BRegistrationModel")).thenReturn(b2bRegistrationModelList);

		Mockito.when(userService.getUserForUID(Mockito.anyString(), Mockito.eq(CustomerModel.class))).thenReturn(customerModel);

		Mockito.when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString())).thenReturn(
				b2bRegistrationProcessModel);

		sendEmailWorkflowJob.perform(workflowActionModel);

		Mockito.verify(businessProcessService).startProcess(b2bRegistrationProcessModel);
	}

}
