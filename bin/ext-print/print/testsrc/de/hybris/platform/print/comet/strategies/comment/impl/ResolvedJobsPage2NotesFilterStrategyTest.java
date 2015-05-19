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
package de.hybris.platform.print.comet.strategies.comment.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.print.comet.converter.impl.CommentInfo;
import de.hybris.platform.print.comet.validation.impl.ResolvedJobsPage2NotesFilterStrategy;
import de.hybris.platform.print.services.impl.AbstractPrintJobServicelayerTest;
import de.hybris.platform.workflow.WorkflowProcessingService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


@IntegrationTest
public class ResolvedJobsPage2NotesFilterStrategyTest extends AbstractPrintJobServicelayerTest
{
	private final ResolvedJobsPage2NotesFilterStrategy resolvedJobsPage2NotesFilterStrategy = (ResolvedJobsPage2NotesFilterStrategy) Registry
			.getApplicationContext().getBean("resolvedJobsPage2NotesFilterStrategy");

	private final WorkflowProcessingService workflowProcessingService = (WorkflowProcessingService) Registry
			.getApplicationContext().getBean("workflowProcessingService");

	@Test
	public void testFilterEndedWorfklows()
	{
		final CommentInfo comment = new CommentInfo();
		comment.setComment(prepareComment("comment", "Hy", 0, getUserModel()));

		final CommentInfo running = new CommentInfo();
		running.setComment(prepareComment("running", "Hy", 0, getUserModel()));
		getPrintCollaborationFacade().createJob(running.getComment(), getDummyAttachment(), getUserModel());

		final CommentInfo ended = new CommentInfo();
		ended.setComment(prepareComment("ended", "Hy", 0, getUserModel()));
		getPrintCollaborationFacade().createJob(ended.getComment(), getDummyAttachment(), getUserModel());

		//fake ending a workflow by simply ending it
		//strategy checks only for completed, finished and terminated status, not the end action itself
		workflowProcessingService.endWorkflow(ended.getComment().getWorkflow());

		final CommentInfo terminated = new CommentInfo();
		terminated.setComment(prepareComment("terminated", "Hy", 0, getUserModel()));
		getPrintCollaborationFacade().createJob(terminated.getComment(), getDummyAttachment(), getUserModel());

		//terminate the workflow
		workflowProcessingService.terminateWorkflow(terminated.getComment().getWorkflow());

		final List<CommentInfo> commentInfos = new ArrayList<CommentInfo>();
		commentInfos.add(comment);
		commentInfos.add(running);
		commentInfos.add(ended);
		commentInfos.add(terminated);

		final List<CommentInfo> filteredCommentInfos = resolvedJobsPage2NotesFilterStrategy.doFilter(commentInfos);

		assertThat(filteredCommentInfos).isNotEmpty();
		assertThat(filteredCommentInfos).containsOnly(comment, running);
	}
}
