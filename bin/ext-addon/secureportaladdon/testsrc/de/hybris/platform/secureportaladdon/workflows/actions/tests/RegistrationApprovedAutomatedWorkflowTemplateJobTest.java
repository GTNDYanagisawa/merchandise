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
package de.hybris.platform.secureportaladdon.workflows.actions.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.secureportaladdon.workflows.actions.RegistrationApprovedAutomatedWorkflowTemplateJob;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@UnitTest
public class RegistrationApprovedAutomatedWorkflowTemplateJobTest
{

	private RegistrationApprovedAutomatedWorkflowTemplateJob registrationApprovedAutomatedWorkflowTemplateJob;

	private WorkflowActionModel workflowActionModel;
	private WorkflowDecisionModel workflowDecisionModel;
	private CustomerModel customer;
	private B2BCustomerModel b2BCustomer;
	private B2BRegistrationModel b2BRegistrationModel;
	private TitleModel titleModel;
	private B2BUnitModel b2bUnitModel;

	private ModelService modelService;
	private WorkflowAttachmentService workflowAttachmentService;
	private UserService userService;

	private List<ItemModel> attachments;
	private List<CustomerModel> customers;

	private static final String NAME = "Richard Feynman";
	private static final String TITLE = "Mr";
	private static final String DEFAULT_EMAIL = "richard.feynman@Ttest.ca";
	private static final String DECISION_NAME = "Default decision name";
	private static final String DEFAULT_B2BUNIT_NAME = "Pronto";


	@Before
	public void setUp()
	{
		registrationApprovedAutomatedWorkflowTemplateJob = new RegistrationApprovedAutomatedWorkflowTemplateJob();

		registrationApprovedAutomatedWorkflowTemplateJob.setModelService(modelService = mock(ModelService.class));

		registrationApprovedAutomatedWorkflowTemplateJob
				.setWorkflowAttachmentService(workflowAttachmentService = mock(WorkflowAttachmentService.class));

		registrationApprovedAutomatedWorkflowTemplateJob.setUserService(userService = mock(UserService.class));

		customer = createCustomerModel();
		b2BCustomer = createB2BCustomerModel(customer);
		b2BRegistrationModel = createB2BRegistrationModel();
		b2bUnitModel = createB2BUnit();

		customers = Arrays.asList(customer);

		workflowActionModel = new WorkflowActionModel();

		workflowDecisionModel = new WorkflowDecisionModel();
		workflowDecisionModel.setName(DECISION_NAME, Locale.getDefault());

		workflowActionModel.setDecisions(Arrays.asList(workflowDecisionModel));
	}


	@Test
	public void testPerform()
	{

		attachments = Arrays.asList(customer, b2BRegistrationModel);

		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation)
			{
				customers = new LinkedList<>();

				return null;
			}
		}).when(modelService).remove(customer);


		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation)
			{
				customers = new ArrayList<CustomerModel>();
				customers.add(b2BCustomer);

				return null;
			}
		}).when(modelService).save(b2BCustomer);



		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation)
			{
				attachments = new LinkedList<ItemModel>();
				attachments = Arrays.asList(createB2BCustomerModel(customers.get(0)), b2BRegistrationModel);

				return null;
			}
		}).when(workflowAttachmentService).addItems(any(WorkflowModel.class), any(attachments.getClass()));


		workflowAttachmentService.addItems(workflowActionModel.getWorkflow(), attachments);

		when(userService.getUserForUID(b2BRegistrationModel.getEmail(), CustomerModel.class)).thenReturn(customer);

		final List<ItemModel> customerAsList = new LinkedList<ItemModel>();
		customerAsList.add(customer);
		when(workflowAttachmentService.getAttachmentsForAction(workflowActionModel, CustomerModel.class.getName())).thenReturn(
				customerAsList);

		final List<ItemModel> b2BRegistrationModelAsList = new LinkedList<ItemModel>();
		b2BRegistrationModelAsList.add(b2BRegistrationModel);
		when(workflowAttachmentService.getAttachmentsForAction(workflowActionModel, B2BRegistrationModel.class.getName()))
				.thenReturn(b2BRegistrationModelAsList);

		when(modelService.create(B2BCustomerModel.class)).thenReturn(b2BCustomer);

		final WorkflowModel workflowModel = new WorkflowModel();
		workflowModel.setActions(Arrays.asList(workflowActionModel));

		workflowActionModel.setWorkflow(workflowModel);

		final WorkflowDecisionModel decision = registrationApprovedAutomatedWorkflowTemplateJob.perform(workflowActionModel);

		assertEquals("The right decision shoule be returned", decision, workflowDecisionModel);

		assertTrue("B2BCustomer should have been created", attachments.get(0) instanceof B2BCustomerModel);

		assertEquals("B2BCustomer should have been assigned a B2BUnit", ((B2BCustomerModel) attachments.get(0)).getDefaultB2BUnit()
				.getName(), b2bUnitModel.getName());

		assertTrue("b2BRegistrationModel should still be in workflow attachment",
				attachments.get(1).getClass().getName().endsWith("B2BRegistrationModel"));
	}



	/******
	 * */
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


	/**********
	 * 
	 * */
	private B2BCustomerModel createB2BCustomerModel(final CustomerModel customer)
	{
		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();

		b2bCustomer.setEmail(customer.getUid());
		b2bCustomer.setName(customer.getName());
		b2bCustomer.setTitle(customer.getTitle());
		b2bCustomer.setUid(customer.getUid());

		b2bCustomer.setDefaultB2BUnit(createB2BUnit());

		return b2bCustomer;
	}


	/*****
	 * 
	 * */
	private B2BRegistrationModel createB2BRegistrationModel()
	{

		final B2BRegistrationModel b2BRegistration = new B2BRegistrationModel();

		b2BRegistration.setTitle(titleModel);

		final CountryModel country = new CountryModel();
		country.setIsocode("US");
		country.setActive(Boolean.TRUE);

		b2BRegistration.setCompanyAddressCountry(country);

		return b2BRegistration;

	}


	/*****
	 * 
	 * 
	 */
	private B2BUnitModel createB2BUnit()
	{
		final B2BUnitModel unit = new B2BUnitModel();
		unit.setName(DEFAULT_B2BUNIT_NAME);

		return unit;
	}


}
