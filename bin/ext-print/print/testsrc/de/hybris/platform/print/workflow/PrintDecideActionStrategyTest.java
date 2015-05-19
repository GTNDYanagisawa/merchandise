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
package de.hybris.platform.print.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.print.collaboration.facades.PrintCollaborationFacade;
import de.hybris.platform.print.collaboration.services.PrintJobHistoryEntryDTO;
import de.hybris.platform.print.collaboration.services.impl.DefaultPrintJobHistoryEntryDTO;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.model.PrintJobHistoryEntryModel;
import de.hybris.platform.print.tests.AbstractPrintWorkflowTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.model.WorkflowActionCommentModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.util.Assert;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;


@IntegrationTest
public class PrintDecideActionStrategyTest extends AbstractPrintWorkflowTest
{

	/** The class to be tested gets injected here */
	@Resource
	private PrintCollaborationFacade printCollaborationFacade;

	@Resource
	private WorkflowProcessingService workflowProcessingService;

	@Resource
	private UserService userService;

	/**
	 * Make a switch from ToDo to Question and check if history entry has been created correctly
	 */
	@Test
	public void testStatusChange()
	{
		// create a page
		final PageModel page = preparePage();
		getModelService().save(page);

		userService.setCurrentUser(getPrintmanager());

		// create a comment
		createComment(page, "job", "job description", getPrintmanager());
		final CommentModel comment = isCommentExists("job");

		// convert to job
		printCollaborationFacade.createJob(comment, page, getPrintmanager());
		assertNotNull("Comment has not been converted to a job!", comment.getWorkflow());

		// get the beginning action and its comments
		final WorkflowActionModel startAction = printCollaborationFacade.getCurrentAction(comment);
		assertEquals("First action is not a ToDo.", "To be done", startAction.getName(Locale.forLanguageTag("en")));
		final Collection<WorkflowActionCommentModel> startComments = startAction.getWorkflowActionComments();

		// Before switching the action we need to log in as the correct user
		userService.setCurrentUser(getLayouter());

		// switch to the first decision we will find
		final Collection<WorkflowDecisionModel> decisions = startAction.getDecisions();

		final WorkflowDecisionModel decision = decisions.iterator().next();
		workflowProcessingService.decideAction(startAction, decision);

		Assert.notEmpty(decision.getToActions(), "No to-actions for current decision?");

		final Collection<WorkflowActionCommentModel> endComments = startAction.getWorkflowActionComments();
		assertTrue("Number of comments after changing status has not increased.", startComments.size() < endComments.size());

		// we only test comments of type history entry
		Collections2.filter(endComments, new Predicate<WorkflowActionCommentModel>()
		{
			@Override
			public boolean apply(@Nullable final WorkflowActionCommentModel comment)
			{
				return comment instanceof PrintJobHistoryEntryModel;
			}
		});

		// fetch last comment and check if it's correct
		final WorkflowActionCommentModel[] actionComments = endComments.toArray(new WorkflowActionCommentModel[endComments.size()]);
		final WorkflowActionCommentModel lastComment = actionComments[actionComments.length - 1];

		assertSame("Last comment is not a print job history entry.", PrintJobHistoryEntryModel.class, lastComment.getClass());

		final PrintJobHistoryEntryDTO dto = new DefaultPrintJobHistoryEntryDTO((PrintJobHistoryEntryModel) lastComment);

		assertEquals("Contents of saved and retrieved comment differs.",
				"Status changed from *To be done* to *Question* by *layouter*", dto.getText());
	}

}
