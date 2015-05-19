/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.cockpit.services;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.jalo.CockpitTest;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.workflow.impl.WorkflowServiceImpl;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.security.AccessManager;
import de.hybris.platform.jalo.security.UserRight;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.jalo.WorkflowAction;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests default workflow service
 * 
 */
@IntegrationTest
public class WorkflowServiceImplTest extends CockpitTest
{
	/** service that is tested */
	private WorkflowServiceImpl workflowService;

	private ModelService modelService;

	private User user;

	private Date now;

	@Before
	public void before()
	{
		workflowService = (WorkflowServiceImpl) applicationContext.getBean("workflowService");
		modelService = (ModelService) applicationContext.getBean("modelService");
		user = createUser("supa");
		final long nowMillis = System.currentTimeMillis();
		now = new Date(nowMillis - nowMillis % 1000);
	}

	/**
	 * Test if correct start time of workflow is returned
	 * 
	 * start time written directly in workflow
	 */
	@Test
	public void testStartTime1()
	{
		final Date now = new Date();
		final WorkflowModel workflow = createMock(WorkflowModel.class);
		expect(workflow.getStartTime()).andReturn(now);
		replay(workflow);
		final Date then = workflowService.getStartTime(workflow);
		assertEquals("Start time is different!", now, then);
	}

	/**
	 * Test if correct start time of workflow is returned
	 * 
	 * start time written workflow start action
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testStartTime2() throws InterruptedException
	{
		/* prepare data */
		final WorkflowModel workflow = createPlannedWorkflow();
		/* do the test */
		final Date then = workflowService.getStartTime(workflow);
		assertEquals("Start time is different!", now, then);
	}

	/**
	 * Test if correct workflows are returned for given view options
	 * 
	 * one regular planned, one adhoc finished.
	 */
	@Test
	public void testViewOptions()
	{
		/* prepare data */
		createPlannedWorkflow();
		/* do the test */
		List<TypedObject> allWorkflows = workflowService.getAllWorkflows(1, 0, null, null, null, null);
		assertEquals("Only one workflow was expected!", 1, allWorkflows.size());
		allWorkflows = workflowService.getAllWorkflows(2, 0, null, null, null, null);
		assertEquals("No workflow was expected!", 0, allWorkflows.size());
		/* prepare data */
		createFinishedAdhocWorkflow();
		/* do the test */
		allWorkflows = workflowService.getAllWorkflows(1, 4, null, null, null, null);
		assertEquals("Two workflows were expected!", 2, allWorkflows.size());
		allWorkflows = workflowService.getAllWorkflows(2, 2, null, null, null, null);
		assertEquals("No workflows were expected!", 0, allWorkflows.size());
	}

	/**
	 * Creates adhoc workflow with status finished
	 */
	private WorkflowModel createFinishedAdhocWorkflow()
	{
		jaloSession.setUser(user);
		//create action template 
		final WorkflowActionTemplateModel workflowActTmpl = modelService.create(WorkflowActionTemplateModel.class);
		workflowActTmpl.setOwner((ItemModel) modelService.get(user));
		workflowActTmpl.setCode("another name");
		//create workflow template
		final WorkflowTemplateModel adhocWorkflowTemplate = modelService.create(WorkflowTemplateModel.class);
		adhocWorkflowTemplate.setName("adhoctemplate");
		adhocWorkflowTemplate.setCode("adhoctemplate");
		//create workflow
		final WorkflowModel workflow = modelService.create(WorkflowModel.class);
		workflow.setJob(adhocWorkflowTemplate);
		//create action
		final WorkflowActionModel workflowAction = modelService.create(WorkflowActionModel.class);
		workflowAction.setActionType(WorkflowActionType.START);
		workflowAction.setFirstActivated(now);/* <- set date */
		workflowAction.setWorkflow(workflow);
		workflowAction.setTemplate(workflowActTmpl);
		workflowAction.setStatus(WorkflowActionStatus.COMPLETED);
		final List<WorkflowActionModel> wfas = new ArrayList<WorkflowActionModel>();
		wfas.add(workflowAction);
		workflow.setActions(wfas);
		workflowActTmpl.setWorkflow(adhocWorkflowTemplate);
		modelService.saveAll();
		return workflow;
	}

	/**
	 * Creates workflow with status planned
	 */
	private WorkflowModel createPlannedWorkflow()
	{
		jaloSession.setUser(user);
		//create action template 
		final WorkflowActionTemplateModel workflowActTmpl = modelService.create(WorkflowActionTemplateModel.class);
		workflowActTmpl.setOwner((ItemModel) modelService.get(user));
		workflowActTmpl.setCode("stupid action tmpl");
		//create workflow template
		final WorkflowTemplateModel workflowTemplate = modelService.create(WorkflowTemplateModel.class);
		//create workflow
		final WorkflowModel workflow = modelService.create(WorkflowModel.class);
		workflow.setJob(workflowTemplate);
		//create action
		final WorkflowActionModel workflowAction = modelService.create(WorkflowActionModel.class);
		workflowAction.setActionType(WorkflowActionType.START);
		workflowAction.setFirstActivated(now);/* <- set date */
		workflowAction.setWorkflow(workflow);
		workflowAction.setTemplate(workflowActTmpl);
		workflowAction.setStatus(WorkflowActionStatus.PENDING);
		final List<WorkflowActionModel> wfas = new ArrayList<WorkflowActionModel>();
		wfas.add(workflowAction);
		workflow.setActions(wfas);
		workflowActTmpl.setWorkflow(workflowTemplate);
		modelService.saveAll();
		return workflow;
	}

	protected User createUser(final String userName)
	{
		User user = null;
		try
		{
			user = UserManager.getInstance().createCustomer(userName);
			final UserRight readRight = AccessManager.getInstance().getOrCreateUserRightByCode(AccessManager.READ);
			assertNotNull("UserRight should not be null", readRight);
			TypeManager.getInstance().getComposedType(WorkflowAction.class).addPositivePermission(user, readRight);
			assertNotNull("User should not be null", user);
		}
		catch (final ConsistencyCheckException e)
		{
			fail("Can not create user caused by: " + e.getMessage());
		}
		return user;
	}
}
