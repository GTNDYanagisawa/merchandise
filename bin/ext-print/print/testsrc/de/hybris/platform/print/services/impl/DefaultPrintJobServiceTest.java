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
package de.hybris.platform.print.services.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.print.collaboration.services.PrintJobService;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.tests.AbstractPrintServicelayerTest;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.ListUtils;
import org.junit.Test;


@IntegrationTest
public class DefaultPrintJobServiceTest extends AbstractPrintServicelayerTest
{

	/** The class to be tested gets injected here */
	@Resource
	private PrintJobService printJobService;

	@Resource
	private WorkflowService newestWorkflowService;

	@Resource
	private WorkflowActionService workflowActionService;

	@Resource
	private WorkflowProcessingService workflowProcessingService;

	/**
	 * creates a job, checks if it exists and if the workflow is set up correctly and started
	 */
	/**
	 * TODO: Should create a test creating a job without a subject and check the workflow is named appropriately
	 */
	@Test
	public void testCreateJob()
	{
		// admin user will be owner of Workflow template and standard assignee for WorkflowActions
		final UserModel admin = getUserService().getUserForUID("admin");
		// creator will be the user creating a job
		final UserModel creator = createUser("TestUser");
		// assignee will be the defined special assignee for a WorkflowAction
		final UserModel assignee = createUser("OtherTestUser");

		// create a comment
		final CommentModel comment = prepareComment("testComment1", "MyJob", 0, creator);

		// create a page and assign comment to page
		final PageModel page = preparePage();
		final List<CommentModel> comments = new ArrayList<CommentModel>();
		comments.add(comment);
		page.setComments(comments);
		// here the interceptor PageCommentsPrepareInterceptor is triggered when comment history of page is dirty
		getModelService().save(page);

		/**
		 * TODO: Why are you starting a workflow to check later that it is started? Starting is done in PrintCollaborationFacade, so
		 * it should be tested in DefaultPrintCollaborationFacadeTest.java
		 */
		createPrintJobWorkflowTemplate(admin, assignee);

		// execute job creation method from business logic
		final WorkflowModel workflow = printJobService.createJob(comment, page, creator);
		getModelService().save(workflow);

		comment.setWorkflow(workflow);
		getModelService().save(comment);

		workflowProcessingService.startWorkflow(workflow);

		// verify workflow assignee is creator of job
		assertEquals("job workflow has wrong PrincipalAssigned", comment.getWorkflow().getPrincipalAssigned(), creator);

		// verify Comment has appropriate workflow attached
		assertTrue("no appropriate workflow attached to the comment", comment.getWorkflow().getName().equals("MyJob"));

		// verify workflow has been started
		assertTrue("workflow not started yet!", newestWorkflowService.isRunning(comment.getWorkflow()));

		// verify WorkflowActions from type WorkflowActionType.START and WorkflowActionType.END have creator and page assigned
		final List<WorkflowActionModel> workflowStartEndActions = ListUtils.union(
				workflowActionService.getStartWorkflowActions(comment.getWorkflow()),
				workflowActionService.getEndWorkflowActions(comment.getWorkflow()));
		for (final WorkflowActionModel action : workflowStartEndActions)
		{
			assertEquals("wrong principal assigned for start-action or end-action", action.getPrincipalAssigned(), creator);
			assertTrue("wrong or no page attached to start-action or end-action", action.getAttachments().size() >= 1
					&& action.getAttachments().get(0).getItem().equals(page));
			// verify start actions are set to IN_PROGRESS
			if (action.getActionType() == WorkflowActionType.START)
			{
				assertTrue("WorkflowAction of type start not activated", workflowActionService.isActive(action));
			}
		}

		// verify WorkflowActions from type WorkflowActionType.NORMAL have correct assignee and attachement
		final List<WorkflowActionModel> workflowNormalActions = workflowActionService.getNormalWorkflowActions(comment
				.getWorkflow());
		for (final WorkflowActionModel action : workflowNormalActions)
		{
			assertEquals("wrong principal assigned for middle-action", action.getPrincipalAssigned(), assignee);
			assertTrue("wrong or no page attached to middle-action", action.getAttachments().size() >= 1
					&& action.getAttachments().get(0).getItem().equals(page));
		}
	}

	/**
	 * creates a workflow based on the @printJobWorkflowName template, starts it and sets job, checks if it exists and if the
	 * workflow is set up correctly and started
	 */
	@Test
	public void testGetCurrentActions() throws Exception
	{
		// admin user will be owner of Workflow template and standard assignee for WorkflowActions
		final UserModel admin = getUserService().getUserForUID("admin");
		getUserService().setCurrentUser(admin);

		final WorkflowTemplateModel workflowTemplate = createPrintJobWorkflowTemplate(admin, admin);

		final WorkflowModel workflow = newestWorkflowService.createWorkflow(getPrintJobWorkflowName(), workflowTemplate,
				Collections.EMPTY_LIST, admin);
		getModelService().refresh(workflow);

		// get current actions now uses job history entries
		final CommentModel comment = prepareComment("testComment1", "MyJob", 0, createUser("TestUser"));
		comment.setWorkflow(workflow);
		getModelService().save(comment);

		workflowProcessingService.startWorkflow(newestWorkflowService.getWorkflowForCode(workflow.getCode()));

		final WorkflowActionModel startAction = workflowActionService.getStartWorkflowActions(workflow).get(0);
		final WorkflowDecisionModel decision12 = (WorkflowDecisionModel) startAction.getDecisions().toArray()[0];

		workflowProcessingService.decideAction(startAction, decision12);

		// execute method to test from business logic
		final List<WorkflowActionModel> currentActions = printJobService.getCurrentActions(workflow);
		// verify getCurrentAction delivers WorkflowAction middle from our default template
		assertSame("current actions list does not have correct amount of actions", Integer.valueOf(currentActions.size()),
				Integer.valueOf(1));
		assertEquals("wrong action found", workflowActionService.getNormalWorkflowActions(workflow).get(0), currentActions.get(0));

	}

}
