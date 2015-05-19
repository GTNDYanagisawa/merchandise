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
package de.hybris.platform.print.facades.impl;

import static junit.framework.Assert.fail;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.comments.model.CommentGroupModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.ReplyModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.print.collaboration.facades.PrintCollaborationFacade;
import de.hybris.platform.print.collaboration.filter.FilterCondition;
import de.hybris.platform.print.collaboration.filter.impl.AbstractFilterCondition;
import de.hybris.platform.print.collaboration.services.PrintCommentService.CommentType;
import de.hybris.platform.print.collaboration.services.PrintJobHistoryEntryDTO;
import de.hybris.platform.print.collaboration.sort.SortCondition;
import de.hybris.platform.print.model.PrintJobHistoryEntryModel;
import de.hybris.platform.print.services.impl.AbstractPrintJobServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.testframework.Transactional;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionCommentModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;


/**
 * tests for the PrintCollaborationFacade
 */
@Transactional
@IntegrationTest
public class DefaultPrintCollaborationFacadeTest extends AbstractPrintJobServicelayerTest
{
	/**
	 * service to test
	 */
	private PrintCollaborationFacade printCollaborationFacade;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private WorkflowProcessingService workflowProcessingService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		//get required beans and services
		modelService = getModelService();
		printCollaborationFacade = getPrintCollaborationFacade();
	}

	/**
	 * Filter comment list by TYPE COMMENT
	 */
	@Test
	public void testFilterByTypeComment()
	{
		final FilterCondition<CommentType> filterCondition = new AbstractFilterCondition<CommentType>()
		{
			@Override
			public String getFilterBy()
			{
				return "TYPE";
			}
		};
		filterCondition.setValues(Collections.singleton(CommentType.COMMENT));

		final Collection<CommentModel> filteredCommentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> singletonList(filterCondition), Collections.<SortCondition> emptyList(),
				getCommentModelList());

		assertThat(filteredCommentList).isNotEmpty();
		assertThat(filteredCommentList).containsOnly(getCommentModel());

	}

	/**
	 * Filter comment list by TYPE JOB
	 */
	@Test
	public void testFilterByTypeJob()
	{
		final FilterCondition<CommentType> filterCondition = new AbstractFilterCondition<CommentType>()
		{
			@Override
			public String getFilterBy()
			{
				return "TYPE";
			}
		};
		filterCondition.setValues(Collections.singleton(CommentType.JOB));

		final Collection<CommentModel> filteredCommentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> singletonList(filterCondition), Collections.<SortCondition> emptyList(),
				getCommentModelList());

		assertThat(filteredCommentList).isNotEmpty();
		assertThat(filteredCommentList).containsOnly(getCommentModelWithJob());
	}

	/**
	 * Filter comment list by TYPE PAGE_COMMENT
	 */
	@Test
	public void testFilterByTypePageComment()
	{
		final FilterCondition<CommentType> filterCondition = new AbstractFilterCondition<CommentType>()
		{
			@Override
			public String getFilterBy()
			{
				return "TYPE";
			}
		};
		filterCondition.setValues(Collections.singleton(CommentType.PAGE_COMMENT));

		final Collection<CommentModel> filteredCommentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> singletonList(filterCondition), Collections.<SortCondition> emptyList(),
				getCommentModelList());

		assertThat(filteredCommentList).isNotEmpty();
		assertThat(filteredCommentList).containsOnly(getCommentModelWithPage());

	}

	/**
	 * Filter comment list by TYPE PAGE_JOB
	 */
	@Test
	public void testFilterByTypePageJob()
	{
		final FilterCondition<CommentType> filterCondition = new AbstractFilterCondition<CommentType>()
		{
			@Override
			public String getFilterBy()
			{
				return "TYPE";
			}
		};
		filterCondition.setValues(Collections.singleton(CommentType.PAGE_JOB));

		final Collection<CommentModel> filteredCommentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> singletonList(filterCondition), Collections.<SortCondition> emptyList(),
				getCommentModelList());

		assertThat(filteredCommentList).isNotEmpty();
		assertThat(filteredCommentList).containsOnly(getCommentModelWithPageJob());

	}

	/**
	 * Filter comment list by type COMMENTGROUP
	 */
	@Test
	public void testFilterByCommentGroup()
	{
		final FilterCondition<CommentGroupModel> filterCondition = new AbstractFilterCondition<CommentGroupModel>()
		{
			@Override
			public String getFilterBy()
			{
				return "COMMENTGROUP";
			}
		};
		filterCondition.setValues(Collections.singleton(getCommentGroup()));

		getCommentModel().setCommentGroup(getCommentGroup());

		final Collection<CommentModel> filteredCommentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> singletonList(filterCondition), Collections.<SortCondition> emptyList(),
				getCommentModelList());

		assertThat(filteredCommentList).isNotEmpty();
		assertThat(filteredCommentList).containsOnly(getCommentModel());

	}


	/**
	 * Filter comment list by STATUS
	 */
	@Test
	public void testFilterByStatusToDo()
	{
		final FilterCondition<String> filterCondition = new AbstractFilterCondition<String>()
		{
			@Override
			public String getFilterBy()
			{
				return "STATUS";
			}
		};
		filterCondition.setValues(Collections.singleton("ToDo"));

		final Collection<CommentModel> filteredCommentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> singletonList(filterCondition), Collections.<SortCondition> emptyList(),
				getCommentModelList());

		assertThat(filteredCommentList).isNotEmpty();
		assertThat(filteredCommentList).hasSize(4); // TPS-261 comments should always be visible
		assertThat(filteredCommentList).contains(getCommentModelWithPageJob());
		assertThat(filteredCommentList).contains(getCommentModelWithJob());
	}

	/**
	 * Filter comment list by STATUS
	 */
	@Test
	public void testFilterByStatusQuestion()
	{
		final FilterCondition<String> filterCondition = new AbstractFilterCondition<String>()
		{
			@Override
			public String getFilterBy()
			{
				return "STATUS";
			}
		};
		filterCondition.setValues(Collections.singleton("Request"));

		final WorkflowActionModel currentAction = printCollaborationFacade.getCurrentAction(getCommentModelWithPageJob());

		final WorkflowActionTemplateModel workflowActionTemplateModelExample = new WorkflowActionTemplateModel();
		workflowActionTemplateModelExample.setCode("Request");

		final WorkflowActionTemplateModel workflowActionTemplate = getFlexibleSearchService().getModelByExample(
				workflowActionTemplateModelExample);

		assertThat(workflowActionTemplate).isNotNull();

		currentAction.setTemplate(workflowActionTemplate);

		final Collection<CommentModel> filteredCommentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> singletonList(filterCondition), Collections.<SortCondition> emptyList(),
				getCommentModelList());

		assertThat(filteredCommentList).isNotEmpty();
		assertThat(filteredCommentList).containsOnly(getCommentModelWithPageJob(), getCommentModel(), getCommentModelWithPage()); // TPS-261 comments should always be visible
	}

	/**
	 * Filter comment list by STATUS
	 */
	@Test
	public void testFilterByStatusOkay()
	{
		final FilterCondition<String> filterCondition = new AbstractFilterCondition<String>()
		{
			@Override
			public String getFilterBy()
			{
				return "STATUS";
			}
		};
		filterCondition.setValues(Collections.singleton("Okay"));

		final WorkflowActionModel currentAction = printCollaborationFacade.getCurrentAction(getCommentModelWithPageJob());

		final WorkflowActionTemplateModel workflowActionTemplateModelExample = new WorkflowActionTemplateModel();
		workflowActionTemplateModelExample.setCode("Okay");

		final WorkflowActionTemplateModel workflowActionTemplate = getFlexibleSearchService().getModelByExample(
				workflowActionTemplateModelExample);

		assertThat(workflowActionTemplate).isNotNull();

		currentAction.setTemplate(workflowActionTemplate);

		final Collection<CommentModel> filteredCommentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> singletonList(filterCondition), Collections.<SortCondition> emptyList(),
				getCommentModelList());

		assertThat(filteredCommentList).isNotEmpty();
		assertThat(filteredCommentList).containsOnly(getCommentModelWithPageJob(), getCommentModel(), getCommentModelWithPage()); // TPS-261 comments should always be visible

	}


	/**
	 * Sort comment list by CREATIONTIME
	 */
	@Test
	public void testSortByCreationTimeASC()
	{
		final SortCondition sortCondition = new SortCondition()
		{

			@Override
			public String getSortBy()
			{
				return "CREATIONTIME";
			}

			@Override
			public SortDirection getSortDirection()
			{
				return SortDirection.ASC;
			}
		};


		final Collection<CommentModel> sortedCommentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> emptyList(), Collections.<SortCondition> singletonList(sortCondition),
				getCommentModelList());

		assertThat(sortedCommentList).isNotEmpty();
		assertThat(sortedCommentList).hasSize(getCommentModelList().size());
		assertThat(Iterables.get(sortedCommentList, 0)).isEqualTo(getCommentModel());
	}

	/**
	 * Sort comment list by CREATIONTIME
	 */
	@Test
	public void testSortByCreationTimeDESC()
	{
		final SortCondition sortCondition = new SortCondition()
		{

			@Override
			public String getSortBy()
			{
				return "CREATIONTIME";
			}

			@Override
			public SortDirection getSortDirection()
			{
				return SortDirection.DESC;
			}
		};


		final Collection<CommentModel> sortedCommentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> emptyList(), Collections.<SortCondition> singletonList(sortCondition),
				getCommentModelList());

		assertThat(sortedCommentList).isNotEmpty();
		assertThat(sortedCommentList).hasSize(getCommentModelList().size());
		assertThat(Iterables.get(sortedCommentList, 0)).isEqualTo(getCommentModelWithPageJob());
	}

	/**
	 * Sort comment list by ONPAGE
	 */
	@Test
	public void testSortByOnPage()
	{
		final SortCondition sortCondition = new SortCondition()
		{

			@Override
			public String getSortBy()
			{
				return "ONPAGE";
			}

			@Override
			public SortDirection getSortDirection()
			{
				return SortDirection.DESC;
			}
		};


		final Collection<CommentModel> sortedCommentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> emptyList(), Collections.<SortCondition> singletonList(sortCondition),
				getCommentModelList());

		assertThat(sortedCommentList).isNotEmpty();
		assertThat(sortedCommentList).hasSize(getCommentModelList().size());
		assertThat(Iterables.get(sortedCommentList, 0)).isEqualTo(getCommentModelWithPage());
		assertThat(Iterables.get(sortedCommentList, 1)).isEqualTo(getCommentModelWithPageJob());
	}

	/**
	 * Sort and filter without SortCondition
	 */
	@Test
	public void testFilterAndSortCommentsWoFilter()
	{
		final Collection<CommentModel> commentList = printCollaborationFacade.filterAndSortComments(
				Collections.<FilterCondition> emptyList(), Collections.<SortCondition> emptyList(), getCommentModelList());

		assertThat(commentList).isNotEmpty();
		assertThat(commentList).hasSize(getCommentModelList().size());
	}

	/**
	 * Did all actions get the assigned user
	 */
	@Test
	public void testAssignJob()
	{
		printCollaborationFacade.assignJob(getCommentModel(), getUserModel());

		//check all actions for assigned prinicpal
		final WorkflowModel workflow = getCommentModel().getWorkflow();

		if (workflow != null && !CollectionUtils.isEmpty(workflow.getActions()))
		{
			for (final WorkflowActionModel action : workflow.getActions())
			{
				if (action.getPrincipalAssigned() != null)
				{
					assertThat(action.getPrincipalAssigned()).isEqualTo(getUserModel());
				}
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testCreateJobWithNullComment()
	{
		printCollaborationFacade.createJob(null, getDummyAttachment(), getUserModel());
	}

	/**
	 * Create a comment and check if a comment group has been assigned
	 */
	@Test
	public void testCreateComment()
	{
		final CommentModel comment = printCollaborationFacade.createComment(getDummyAttachment(), getUserModel(), "demo comment");
		comment.setCommentType(getCommentType());
		comment.setComponent(getComponent());

		modelService.save(comment);

		assertThat(comment).isNotNull();
		assertThat(comment.getCommentGroup()).isNotNull();
	}

	/**
	 * Was the prinicple assigned to the workflow? Were the attatchments assigned to the workflow actions? Was the prinicple
	 * assigned to the workflow actions? Has the workflow been created?
	 **/
	@Test
	public void testCreateJob()
	{
		//call function to test with created model / dummy attatchement / assignee
		printCollaborationFacade.createJob(getCommentModel(), getDummyAttachment(), getUserModel());

		//get the created workflow from comment 
		final WorkflowModel createdWorkflowModel = getCommentModel().getWorkflow();

		assertThat(createdWorkflowModel).isNotNull();

		//has the proper user been assigned?
		if (!createdWorkflowModel.getPrincipalAssigned().equals(getUserModel()))
		{
			fail("principal was not assigned to Workflow!");
		}

		assertThat(createdWorkflowModel.getActions()).isNotEmpty();

		//every action should have the assigned user and the assigned attatchement
		for (final WorkflowActionModel action : createdWorkflowModel.getActions())
		{
			final List<ItemModel> attatchmentItemList = action.getAttachmentItems();

			for (final ItemModel itemModel : attatchmentItemList)
			{
				assertThat(itemModel).isEqualTo(getDummyAttachment());
			}

			assertThat(action.getPrincipalAssigned()).isNotNull();

			assertThat(action.getPrincipalAssigned().equals(getUserModel()));
		}

	}

	/**
	 * Should fail on groups which have no read permission on WorkflowAction types.
	 */
	@Test(expected = ModelSavingException.class)
	public void testCreateJobWithUnassignableGroup()
	{
		final UserGroupModel example = new UserGroupModel();
		example.setUid("employeegroup");
		final UserGroupModel group = getOrCreateItem(example);
		//call function to test with created model / dummy attatchement / assignee
		printCollaborationFacade.createJob(getCommentModel(), getDummyAttachment(), group);
	}

	@Test
	public void testCreateReply()
	{
		//call function to test with no decision
		printCollaborationFacade.createReply(getCommentModel(), userService.getCurrentUser(), "a simple reply", null);

		//get the created reply
		final List<ReplyModel> replies = getCommentModel().getReplies();

		assertThat(replies).isNotEmpty();

		final ReplyModel reply = Iterables.getFirst(replies, null);

		//does the reply contain the correct text?
		if (!"a simple reply".equals(reply.getText()))
		{
			fail("reply has wrong text");
		}

		//reply should be deletable...
		assertThat(printCollaborationFacade.isDeleteAllowed(reply)).isTrue();
	}

	@Test
	public void testCreateReplyWithStateChange()
	{
		//create a job for this test case
		printCollaborationFacade.createJob(getCommentModel(), getDummyAttachment(), getUserModel());

		//get the beginning action and its comments
		final WorkflowActionModel startAction = printCollaborationFacade.getCurrentAction(getCommentModel());
		assertEquals("First action is not a ToDo.", "To be done", startAction.getName(Locale.forLanguageTag("en")));

		//ensure state can be changed
		userService.setCurrentUser((UserModel) startAction.getPrincipalAssigned());

		//switch to the first decision we will find
		final Collection<WorkflowDecisionModel> decisions = startAction.getDecisions();

		final WorkflowDecisionModel decision = decisions.iterator().next();
		workflowProcessingService.decideAction(startAction, decision);

		//call function to test with the selected decision
		printCollaborationFacade.createReply(getCommentModel(), userService.getCurrentUser(), "reply with state change", decision);

		//get the created reply
		final List<ReplyModel> replies = getCommentModel().getReplies();

		assertThat(replies).isNotEmpty();

		final ReplyModel reply = Iterables.getFirst(replies, null);

		//does the reply contain the correct text?
		if (!"reply with state change".equals(reply.getText()))
		{
			fail("reply has wrong text");
		}

		//reply should not be deletable...
		assertThat(printCollaborationFacade.isDeleteAllowed(reply)).isFalse();
	}

	@Test
	public void testDeleteComment()
	{
		final CommentGroupModel commentGroup = getCommentGroup();
		getModelService().save(commentGroup);

		final CommentModel job = getCommentModelWithJob();
		final CommentModel comment = getCommentModel();

		job.setCommentGroup(commentGroup);
		getModelService().save(job);

		comment.setCommentGroup(commentGroup);
		getModelService().save(comment);

		try
		{
			printCollaborationFacade.deleteComment(comment);
		}
		catch (final BusinessException e)
		{
			fail("Could not delete Comment " + e.getMessage());
		}

		assertThat(!getModelService().isRemoved(commentGroup));



		try
		{
			printCollaborationFacade.deleteComment(job);
		}
		catch (final BusinessException e)
		{
			fail("Could not delete Job " + e.getMessage());
		}

		assertThat(getModelService().isRemoved(commentGroup));
	}

	/**
	 * Comment may not be deleted, if it was received by DTP at least once
	 */
	@Test(expected = BusinessException.class)
	public void testDeleteCommentReceivedByDtp() throws BusinessException
	{

		//set up comment
		final CommentModel comment = new CommentModel();
		comment.setCode("My Test Comment");
		comment.setReceivedByDtp(Boolean.TRUE);

		//try to delete comment
		printCollaborationFacade.deleteComment(comment);

	}

	/**
	 * Test, if a comment may be deleted
	 */
	@Test
	public void testIsDeleteCommentAllowed()
	{

		//set up comment
		final CommentModel comment = new CommentModel();
		comment.setCode("My Test Comment");

		//try to delete comment which was already received by DTP
		assertThat(printCollaborationFacade.isDeleteAllowed(comment)).isTrue();

		comment.setReceivedByDtp(Boolean.FALSE);
		//try to delete comment which was already received by DTP
		assertThat(printCollaborationFacade.isDeleteAllowed(comment)).isTrue();

		comment.setReceivedByDtp(Boolean.TRUE);
		//try to delete comment which was already received by DTP
		assertThat(printCollaborationFacade.isDeleteAllowed(comment)).isFalse();
	}

	/**
	 * Do we have a current action assigned to the created workflow. Has the action been assigned to testUser. Is the action type
	 * todo / start
	 **/
	@Test
	public void testGetCurrentAction()
	{
		//get the created workflow from comment
		final WorkflowModel createdWorkflowModel = getCommentModelWithJob().getWorkflow();
		modelService.save(createdWorkflowModel);

		//create action
		final WorkflowActionModel workflowActionModel = modelService.create(WorkflowActionModel.class);
		workflowActionModel.setCode("workflowAction");
		workflowActionModel.setPrincipalAssigned(getUserModel());
		workflowActionModel.setComments(Collections.singletonList(getCommentModelWithJob()));


		final WorkflowActionTemplateModel workflowActionTemplateModel = modelService.create(WorkflowActionTemplateModel.class);
		workflowActionTemplateModel.setCode("workflowActionTemplateModel");
		workflowActionTemplateModel.setWorkflow(getCommentModelWithJob().getWorkflow().getJob());
		workflowActionTemplateModel.setPrincipalAssigned(getUserModel());
		workflowActionModel.setTemplate(workflowActionTemplateModel);
		workflowActionModel.setWorkflow(createdWorkflowModel);
		modelService.save(workflowActionModel);

		assertThat(createdWorkflowModel).isNotNull();

		//get active action
		final WorkflowActionModel createdWorkflowActionModel = printCollaborationFacade.getCurrentAction(getCommentModelWithJob());

		assertThat(createdWorkflowActionModel).isNotNull();
		assertThat(createdWorkflowActionModel.getPrincipalAssigned().getUid()).isEqualTo("layouter");
		assertThat(createdWorkflowActionModel.getActionType()).isEqualTo(WorkflowActionType.START);
	}


	@Test(expected = NullPointerException.class)
	public void testGetJobHistoryWithNull()
	{
		printCollaborationFacade.getJobHistory(null);
	}

	/**
	 * Do we get all WorkflowActionCommentModels which were created?
	 **/
	@Test
	public void testGetJobHistory()
	{
		//get the created workflow from comment
		final WorkflowModel createdWorkflowModel = getCommentModelWithJob().getWorkflow();

		//create action
		final WorkflowActionModel workflowActionModel = modelService.create(WorkflowActionModel.class);
		workflowActionModel.setCode("workflowAction");
		workflowActionModel.setComments(Collections.singletonList(getCommentModelWithJob()));


		final WorkflowActionTemplateModel workflowActionTemplateModel = modelService.create(WorkflowActionTemplateModel.class);
		workflowActionTemplateModel.setCode("workflowActionTemplateModel");
		workflowActionTemplateModel.setWorkflow(getCommentModelWithJob().getWorkflow().getJob());
		workflowActionTemplateModel.setPrincipalAssigned(getUserModel());
		modelService.save(workflowActionTemplateModel);

		workflowActionModel.setTemplate(workflowActionTemplateModel);
		workflowActionModel.setWorkflow(createdWorkflowModel);
		workflowActionModel.setPrincipalAssigned(getUserModel());
		modelService.save(workflowActionModel);

		//create action
		final WorkflowActionModel toAction = modelService.create(WorkflowActionModel.class);
		toAction.setCode("Question");
		toAction.setComments(Collections.singletonList(getCommentModelWithJob()));
		toAction.setTemplate(workflowActionTemplateModel);
		toAction.setWorkflow(createdWorkflowModel);
		toAction.setPrincipalAssigned(getUserModel());
		modelService.save(workflowActionModel);

		//Create WorkflowActionComment
		final PrintJobHistoryEntryModel printJobHistoryEntryModel = modelService.create(PrintJobHistoryEntryModel.class);
		printJobHistoryEntryModel.setComment("text.automatedcomments.statuschange");
		printJobHistoryEntryModel.setToActions(Collections.singletonList(toAction));
		printJobHistoryEntryModel.setCausedBy(getUserModel());
		printJobHistoryEntryModel.setUser(null);
		printJobHistoryEntryModel.setWorkflowAction(workflowActionModel);
		modelService.save(printJobHistoryEntryModel);

		workflowActionModel.setWorkflowActionComments(Collections
				.<WorkflowActionCommentModel> singletonList(printJobHistoryEntryModel));
		modelService.save(workflowActionModel);

		assertThat(createdWorkflowModel).isNotNull();

		createdWorkflowModel.setActions(Collections.singletonList(workflowActionModel));
		modelService.save(createdWorkflowModel);

		final List<PrintJobHistoryEntryDTO> workflowActionCommentModelList = printCollaborationFacade
				.getJobHistory(getCommentModelWithJob());

		assertThat(workflowActionCommentModelList).isNotNull();
		assertThat(workflowActionCommentModelList).isNotEmpty();
	}

	/**
	 * We should get a empty list if no workflow has been set
	 **/
	@Test
	public void testGetJobHistoryWithoutWorkflow()
	{
		final List<PrintJobHistoryEntryDTO> workflowActionCommentModelList = printCollaborationFacade
				.getJobHistory(getCommentModel());

		assertThat(workflowActionCommentModelList).isNotNull();
		assertThat(workflowActionCommentModelList).isEmpty();
	}

}
