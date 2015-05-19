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
package de.hybris.platform.print.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.print.collaboration.filter.FilterCondition;
import de.hybris.platform.print.collaboration.filter.impl.AbstractFilterCondition;
import de.hybris.platform.print.collaboration.services.PrintCommentService.CommentType;
import de.hybris.platform.print.collaboration.sort.SortCondition;
import de.hybris.platform.print.collaboration.sort.impl.SortByCreationTimeStrategy;
import de.hybris.platform.print.collaboration.sort.impl.SortByOnPageStrategy;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class PrintWorkflowIntegrationTest extends AbstractPrintWorkflowTest
{
	@Resource
	private WorkflowProcessingService workflowProcessingService;

	@Test
	public void testFilterAndSortFunctionality()
	{
		// log in as print manager
		final UserModel printmanager = getPrintmanager();
		getUserService().setCurrentUser(printmanager);

		// create a page
		final PageModel page = preparePage();
		getModelService().save(page);

		// create a job, assign metadata
		createComment(page, "job", "job description", printmanager);
		final CommentModel job = isCommentExists("job");
		createCommentMetadata(page, job);
		getModelService().refresh(job);

		getPrintCollaborationFacade().createJob(job, page, printmanager);
		assertNotNull("Comment has not been converted to a job!", job.getWorkflow());

		// create comment, do not assign metadata
		createComment(page, "page comment", "page comment description", printmanager);
		final CommentModel pageComment = isCommentExists("page comment");
		getModelService().refresh(pageComment);

		// create job, do not assign metadata
		createComment(page, "page job", "page job description", printmanager);
		final CommentModel pageJob = isCommentExists("page job");
		getModelService().refresh(pageJob);

		getPrintCollaborationFacade().createJob(pageJob, page, printmanager);
		assertNotNull("Comment has not been converted to a job!", pageJob.getWorkflow());

		// create comment, assign metadata
		createComment(page, "comment", "comment description", printmanager);
		final CommentModel comment = isCommentExists("comment");
		createCommentMetadata(page, comment);
		getModelService().refresh(comment);

		// check if sorting is ok
		List<CommentModel> comments;
		comments = page.getComments();

		List<CommentModel> filteredComments;
		filteredComments = new ArrayList<CommentModel>(getPrintCollaborationFacade().filterAndSortComments(Collections.EMPTY_LIST,
				createSortConditions(), comments));

		// should be in this order: page job, page comment, comment, job
		assertEquals("page should have 4 comments", Integer.valueOf(4), Integer.valueOf(filteredComments.size()));
		assertEquals("1. in list should be the page job", pageJob, filteredComments.get(0));
		assertEquals("2. in list should be the page comment", pageComment, filteredComments.get(1));
		assertEquals("3. in list should be the comment", comment, filteredComments.get(2));
		assertEquals("4. in list should be the job", job, filteredComments.get(3));

		// filter for only "To Do"
		final List<FilterCondition> filterConditions = new ArrayList<FilterCondition>();
		final FilterCondition<String> filterByTodo = createFilterCondition("STATUS");
		filterByTodo.setValues(Collections.singleton("ToDo"));
		filterConditions.add(filterByTodo);

		filteredComments = new ArrayList<CommentModel>(getPrintCollaborationFacade().filterAndSortComments(filterConditions,
				createSortConditions(), comments));

		// should have 4
		assertEquals("filter should have 4 comments", Integer.valueOf(4), Integer.valueOf(filteredComments.size()));

		// filter for only job & "To Do"
		final FilterCondition<CommentType> filterByJob = createFilterCondition("TYPE");
		filterByJob.setValues(Collections.singleton(CommentType.JOB));
		filterConditions.add(filterByJob);

		filteredComments = new ArrayList<CommentModel>(getPrintCollaborationFacade().filterAndSortComments(filterConditions,
				createSortConditions(), comments));

		// should have 1
		assertEquals("filter should have 1 comment", Integer.valueOf(1), Integer.valueOf(filteredComments.size()));

		// log in as layouter
		final UserModel layouter = getLayouter();
		getUserService().setCurrentUser(layouter);

		// set status of job to "Question"
		final WorkflowActionModel startAction = getPrintCollaborationFacade().getCurrentAction(job);
		assertEquals("First action is not a ToDo.", "To be done", startAction.getName(Locale.forLanguageTag("en")));

		final Collection<WorkflowDecisionModel> decisions = startAction.getDecisions();

		final WorkflowDecisionModel decision = decisions.iterator().next();
		workflowProcessingService.decideAction(startAction, decision);

		final WorkflowActionModel currentAction = getPrintCollaborationFacade().getCurrentAction(job);
		assertEquals("Switched action is not a Question.", "Question", currentAction.getName(Locale.forLanguageTag("en")));

		// filter only "To Do"
		filterConditions.remove(filterByJob);

		filteredComments = new ArrayList<CommentModel>(getPrintCollaborationFacade().filterAndSortComments(filterConditions,
				createSortConditions(), comments));

		// should have 1
		assertEquals("filter should have 3 comments", Integer.valueOf(3), Integer.valueOf(filteredComments.size())); // TPS-261 comments should always be visible
	}

	private <T> FilterCondition<T> createFilterCondition(final String key)
	{
		final AbstractFilterCondition<T> condition = new AbstractFilterCondition<T>()
		{
			@Override
			public String getFilterBy()
			{
				return key;
			}
		};
		return condition;
	}

	private List<SortCondition> createSortConditions()
	{
		final List<SortCondition> conditions = new ArrayList<SortCondition>();
		conditions.add(new SortCondition()
		{
			@Override
			public String getSortBy()
			{
				return SortByOnPageStrategy.TYPE;
			}

			@Override
			public SortDirection getSortDirection()
			{
				return SortDirection.DESC;
			}
		});
		conditions.add(new SortCondition()
		{
			@Override
			public String getSortBy()
			{
				return SortByCreationTimeStrategy.TYPE;
			}

			@Override
			public SortDirection getSortDirection()
			{
				return SortDirection.DESC;
			}
		});
		return conditions;
	}
}
